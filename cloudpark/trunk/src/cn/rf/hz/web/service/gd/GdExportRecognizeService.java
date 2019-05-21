package cn.rf.hz.web.service.gd;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import cn.rf.hz.web.action.gd.CarParkSearchAction;
import cn.rf.hz.web.action.gd.GdEntranceRecognizeAction;
import cn.rf.hz.web.bean.gd.ParkingExportRecord;
import cn.rf.hz.web.mapper.gd.GdExportRecognizeMapper;
import cn.rf.hz.web.utils.BigDataAnalyze;
import cn.rf.hz.web.utils.DateUtil;
import cn.rf.hz.web.utils.JedisPoolUtils;
import cn.rf.hz.web.utils.RedisVideoStopCarInfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.datatunnel.client.DataTunnelPublishClient;
import com.reformer.datatunnel.client.dto.MessageDto;

@Service("gdExportRecognizeService")
public class GdExportRecognizeService<T>
{
	private final static Logger LOG = Logger.getLogger(GdExportRecognizeService.class);

	@Autowired
	private GdExportRecognizeMapper<T> mapper;

	@Autowired(required = false)
	private GdEntranceRecognizeAction gdEntranceRecognizeAction;

	@Autowired(required = false)
	private CarParkSearchAction carParkSearchAction;

	@Autowired
	private GdEntranceRecognizeService gdEntranceRecognizeService;

	@Autowired
	private CarParksService carParksService;

	@Autowired
	private DataTunnelPublishClient dataTunnelPublishClient;
	
	@Autowired
	private DataErrorService dataErrorService;

	public GdExportRecognizeMapper<T> getMapper()
	{
		return mapper;
	}

	public void setMapper(GdExportRecognizeMapper<T> mapper)
	{
		this.mapper = mapper;
	}

	public ParkingExportRecord findById(Map<String, Object> map)
	{
		return getMapper().findById(map);
	}

	public int save(Map<String, Object> map)
	{
		return getMapper().save(map);
	}

	public int update(Map<String, Object> map)
	{
		return getMapper().update(map);
	}

	public void save1(JSONArray data)
	{
		getMapper().save1(data);

	}

	/**
	 * 
	 * 出场数据 老系统
	 * 
	 * @param sourceData
	 * @return
	 */
	public String saveOldGdExportRecognize(String sourceData)
	{

		LOG.debug("4.2上传出场数据信息(老系统):" + sourceData);
		JSONObject data = JSON.parseObject(sourceData);
		Integer carParkId = data.getInteger("carParkId");
		Integer oldRecordId = 0;
		Date outTimeStr = new Date();
		
//		Integer totalParkingNumer = (Integer) exportMap.get("totalParkingNumer");
//		Integer remainParkingNumber = (Integer) exportMap.get("remainParkingNumber");
//		totalParkingNumer = totalParkingNumer == null ? -1 : totalParkingNumer;
//		remainParkingNumber = remainParkingNumber == null ? -1 : remainParkingNumber;

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		oldRecordId = data.getInteger("oldRecordId");
		data.put("oldRecordId", oldRecordId);

		String result = "{\"errorcode\":1}";
		int rowcount = 0;
		try
		{
			String outTime = data.getString("inTimeStr");
			if (!"".equals(outTime) && null != outTime)
				outTime = outTime.replace("/", "-").replace("\\", "-").replace("--", "-");

			outTimeStr = df.parse(outTime);

			data.put("outTime", outTimeStr);
			rowcount = this.getMapper().save(data);

		} catch (Exception e1)
		{
			// 主键异常
			if (e1.getClass() == DuplicateKeyException.class)
			{
				rowcount = 2;
			} else
			{
				// 非主键异常,重复上传
				if (isNormalError(carParkId, oldRecordId))
					rowcount = 1;
				else
				{
					rowcount = 0;
					//保存异常数据
					dataErrorService.save("出场（老系统）",carParkId,oldRecordId,e1.getMessage(),sourceData);
				}
				
				LOG.error("4.2上传出场数据异常(老系统):" + e1);
			}
		}

		if (rowcount > 0)
		{
//			if (rowcount == 1)
//			{
				// 更新入场表，离场标志
				String updateMarkResult = gdEntranceRecognizeService.updateMark(sourceData);
				LOG.debug("4.2上传出场数据,更新离场Mark结果(老系统):" + updateMarkResult);
//			}
			result = "{\"errorcode\":0}";
		} else
			result = "{\"errorcode\":1}";

		// 更新停车位
//		if (totalParkingNumer > 0 || remainParkingNumber > 0)
//		{
//			String updateCarParkTotalNumAndEmptyNumResult = carParksService.updateCarParkTotalNumAndEmptyNum(carParkId, totalParkingNumer, remainParkingNumber);
//			LOG.info("4.2上传出场数据,更新停车位结果(老系统):" + updateCarParkTotalNumAndEmptyNumResult);
//		}

		// 存入redis以及把数据推送给张泉金
		RedisVideoStopCarInfo.export(data, dataTunnelPublishClient);
		
		
		LOG.debug("4.2上传出场数据结果(老系统):" + result);
		return result;
	}

	/**
	 * 
	 * 出场 新系统
	 * 
	 * @param sourceData
	 * @return
	 */
	public String saveGdExportRecognize(String sourceData)
	{
		LOG.info("4.2上传出场数据信息（新系统）:" + sourceData);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		JSONObject exportMap = JSON.parseObject(sourceData);
		Integer count = (Integer) exportMap.get("count");
		Integer carParkId = (Integer) exportMap.get("parkId");
		Integer totalParkingNumer = (Integer) exportMap.get("totalParkingNumer");
		Integer remainParkingNumber = (Integer) exportMap.get("remainParkingNumber");
		totalParkingNumer = totalParkingNumer == null ? -1 : totalParkingNumer;
		remainParkingNumber = remainParkingNumber == null ? -1 : remainParkingNumber;
		// 源数据
		JSONArray videoDatas = exportMap.getJSONArray("data");
		try{
			for (int i = 0; i < count; i++)
			{
				JSONObject videoData = videoDatas.getJSONObject(i);
				Integer oldRecordId = videoData.getInteger("record_id");
				String licensePlateNumber = videoData.getString("licensePlateNumber");
				String outTime =  videoData.getString("outTime");
				outTime = outTime.replace("/", "-").replace("\\", "-").replace("--", "-");
				
				//车牌、日期校验
				if ((!"".equals(licensePlateNumber) && null != licensePlateNumber && !"无车牌".equals(licensePlateNumber))
						&& DateUtil.isValidDate(outTime))
				{
					videoData.put("outTime", df.parse(outTime));
					videoData.put("carParkId", carParkId);
					videoData.put("oldRecordId", oldRecordId);
					videoData.put("result", 0);
				}else
					videoData.put("result", 1);
			}
			
			// 入库，去掉格式不正确的
			JSONArray sqlArray = new JSONArray();
			for (int i = 0; i < count; i++)
			{
				JSONObject videoData = videoDatas.getJSONObject(i);
				//正常格式
				if (videoData.getInteger("result").equals(0))
				{
					sqlArray.add(videoData);
				}
			}
			
			if (sqlArray.size() != 0 )
				getMapper().save1(sqlArray);
			
		}catch(Exception e){
			
			// 单条入库
			int rowcount = 0;
			for (int i = 0; i < count; i++)
			{
				JSONObject videoData = videoDatas.getJSONObject(i);

				//不重复，并且格式不正确
				if (videoData.getInteger("result").equals(0))
				{
					try{
						
						rowcount = this.getMapper().save(videoData);
					}catch(Exception sqlE)
					{
						if (sqlE.getClass() != DuplicateKeyException.class)
						{
							videoData.put("errorcode", sqlE.getMessage());
							LOG.error("4.2上传出场数据异常(新系统):" + sqlE.getMessage());
						}
						else 
							rowcount = 2;
					}
				}
				
				if (rowcount > 0)
					rowcount = 0;
				else
					rowcount = 1;
				
				videoData.put("result", rowcount);
			}
		}
		
		
		// 更新入场表，离场标志、代扣
		for (int i = 0; i < count; i++)
		{
			JSONObject videoData = videoDatas.getJSONObject(i);

			//不重复，并且格式不正确
			if (videoData.getInteger("result").equals(0))
			{
				gdEntranceRecognizeService.updateMark(JSON.toJSONString(videoData));
				
				// TOLL代扣
				if (videoData.getInteger("outType") == 1)
					JedisPoolUtils.hset("withhold", carParkId + "_" + videoData.getIntValue("oldRecordId"), videoData.toJSONString());
			}
			
			// 存入redis以及把数据推送给张泉金
			RedisVideoStopCarInfo.export(videoData, dataTunnelPublishClient);
						
			
			videoData.remove("amount");
			videoData.remove("parkId");
			videoData.remove("inRecordId");
			videoData.remove("outTime");
			videoData.remove("outType");
			videoData.remove("imgName");
			videoData.remove("channelName");
			videoData.remove("oldRecordId");
			videoData.remove("channelId");
			videoData.remove("remarks1");
			videoData.remove("parkingNo");
		}

		// 返回集合
		JSONObject resultGather = new JSONObject();
		resultGather.put("count", count);
		resultGather.put("carParkId", carParkId);
		resultGather.put("errorcode", "");

		resultGather.put("data", videoDatas);

		// 更新停车位
		if (totalParkingNumer > -1 || remainParkingNumber > -1)
			carParksService.updateCarParkTotalNumAndEmptyNum(carParkId, totalParkingNumer, remainParkingNumber);
		
		
		
		return resultGather.toJSONString();
	}

	/**
	 * 非正常错误 大于10次入库，非主键冲突异常将返回正常，减小数据库压力
	 * 
	 * @param carParkId
	 * @param oldRecordId
	 * @return
	 */
	public boolean isNormalError(int carParkId, int oldRecordId)
	{
		String key = "oldExport" + carParkId + oldRecordId;
		Long num = JedisPoolUtils.incr(key);
		
		//key 保留3600秒（1小时）, 
		if(num == 0 )
			JedisPoolUtils.setex(key, 3600, num+"");
		else if(num >= 9 )
		{
			//redis的Key 一个小时候自动失效
			return true;
		}
		else
			return false;
		
		return false;
		
	}

}
