package cn.rf.hz.web.service.gd;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import cn.rf.hz.web.action.gd.GdEntranceRecognizeAction;
import cn.rf.hz.web.bean.gd.ParkingEntranceRecord;
import cn.rf.hz.web.mapper.gd.GdEntranceRecognizeMapper;
import cn.rf.hz.web.utils.BigDataAnalyze;
import cn.rf.hz.web.utils.ConfigUtil;
import cn.rf.hz.web.utils.DateUtil;
import cn.rf.hz.web.utils.JedisPoolUtils;
import cn.rf.hz.web.utils.RedisVideoStopCarInfo;
import cn.rf.hz.web.utils.RequestUtil;
import cn.rf.hz.web.utils.URLUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.datatunnel.client.DataTunnelPublishClient;
import com.reformer.datatunnel.client.dto.MessageDto;

@Service("gdEntranceRecognizeService")
public class GdEntranceRecognizeService
{
	private final static Logger LOG = Logger.getLogger(GdEntranceRecognizeAction.class);

	@Autowired
	private GdEntranceRecognizeMapper mapper;

	@Autowired
	private CarParksService carParksService;

	@Autowired
	private DataTunnelPublishClient dataTunnelPublishClient;

	@Autowired
	private DataErrorService dataErrorService;

	public GdEntranceRecognizeMapper getMapper()
	{
		return mapper;
	}

	public void setMapper(GdEntranceRecognizeMapper mapper)
	{
		this.mapper = mapper;
	}

	public ParkingEntranceRecord getEntranceRecognizePlateNumber(Integer carParkId, String licensePlateNumber)
	{
		return getMapper().getEntranceRecognizePlateNumber(carParkId, licensePlateNumber);
	}

	/**
	 * 新平台 入场
	 * 
	 * @param map
	 * @return
	 */
	public String saveGdEntranceRecognize(String sourceStr)
	{
		LOG.info("4.1进场数据信息(新系统):" + sourceStr);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		JSONObject entranceMap = JSON.parseObject(sourceStr);
		Integer count = (Integer) entranceMap.get("count");
		Integer carParkId = (Integer) entranceMap.get("parkId");
		Integer totalParkingNumer = (Integer) entranceMap.get("totalParkingNumer");
		Integer remainParkingNumber = (Integer) entranceMap.get("remainParkingNumber");
		totalParkingNumer = totalParkingNumer == null ? -1 : totalParkingNumer;
		remainParkingNumber = remainParkingNumber == null ? -1 : remainParkingNumber;

		// 返回集合
		JSONObject resultGather = new JSONObject();
		resultGather.put("count", count);
		resultGather.put("carParkId", carParkId);
		resultGather.put("errorcode", "");

		// 源数据
		JSONArray videoDatas = entranceMap.getJSONArray("data");

		try
		{
			for (int i = 0; i < count; i++)
			{
				JSONObject videoData = videoDatas.getJSONObject(i);
				Integer oldRecordId = videoData.getInteger("record_id");

				String licensePlateNumber = videoData.getString("licensePlateNumber");
				String inTime =  videoData.getString("inTime");

				videoData.put("isWithhold", false);
				inTime = inTime.replace("/", "-").replace("\\", "-").replace("--", "-");
				//车牌、日期校验
				if ((!"".equals(licensePlateNumber) && null != licensePlateNumber && !"无车牌".equals(licensePlateNumber))
						&& DateUtil.isValidDate(inTime))
				{
					
					videoData.put("carParkId", carParkId);
					videoData.put("oldRecordId", oldRecordId);
					videoData.put("inTime", df.parse(inTime));
					videoData.put("recognitionNumber", licensePlateNumber);
					videoData.put("mark", 0);
					videoData.put("discountRate", 0);
					videoData.put("discountTime", 0);
					videoData.put("discountMoney", 0);
					videoData.put("isRecognition", 0);
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
					sqlArray.add(videoData);
			}
			if (sqlArray.size() != 0 )
				getMapper().saveGdEntranceRecognize1(sqlArray);

		} catch (Exception e)
		{
			
			
			// 单条入库
			int rowcount = 0;
			for (int i = 0; i < count; i++)
			{
				JSONObject videoData = videoDatas.getJSONObject(i);

				//不重复，并且格式不正确
				if (videoData.getInteger("result").equals(0))
				{
					try{
						
						rowcount = getMapper().saveGdEntranceRecognize(videoData);
					}catch(Exception sqlE)
					{
						if (sqlE.getClass() != DuplicateKeyException.class)
						{
							videoData.put("errorcode", sqlE.getMessage());
							LOG.error("4.1进场数据异常(新系统):" + sqlE.getMessage());
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
		
		// 更新停车位
		if (totalParkingNumer > -1 || remainParkingNumber > -1)
			carParksService.updateCarParkTotalNumAndEmptyNum(carParkId, totalParkingNumer, remainParkingNumber);
		
		
		//redis缓存，以及张泉金大数据
		for (int i = 0; i < count; i++)
		{
			JSONObject videoData = videoDatas.getJSONObject(i);
			
			//正常格式
			if (videoData.getInteger("result").equals(0))
			{
				//存入redis以及把数据推送给张泉金
				RedisVideoStopCarInfo.entrance(videoData,dataTunnelPublishClient);
			}
			
			videoData.remove("mark");
			videoData.remove("recognitionNumber");
			videoData.remove("discountRate");
			videoData.remove("discountTime");
			videoData.remove("discountMoney");
			videoData.remove("remarks1");
			videoData.remove("parkingNo");
			videoData.remove("status");
			videoData.remove("oldRecordId");
			videoData.remove("channelId");
			videoData.remove("imgName");
			videoData.remove("channelName");
			videoData.remove("inTime");
			videoData.remove("repetition");
			videoData.remove("isRecognition");
			
		}
		resultGather.put("data", videoDatas);
		return resultGather.toJSONString();
	}

	/**
	 * 入场（老系统）
	 * 
	 * @param sourceData
	 * @return
	 */
	public String saveOldGdEntranceRecognize(String sourceData)
	{
		LOG.info("4.1上传进场数据信息（老系统）:" + sourceData);
		int rowcount = 0;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject map = JSON.parseObject(sourceData);
		Integer carParkId = map.getInteger("carParkId");
		Integer oldRecordId = map.getInteger("oldRecordId");

		String inTime = map.getString("inTimeStr");
		if (!"".equals(inTime) && null != inTime)
			inTime = inTime.replace("/", "-").replace("\\", "-").replace("--", "-");

		map.put("mark", 0);
		map.put("discountRate", 0);
		map.put("discountTime", 0);
		map.put("discountMoney", 0);
		map.put("isRecognition", 0);
		String licensePlateNumber = (String) map.get("licensePlateNumber");

		String result = "{\"errorcode\":1}";
		try
		{
			try
			{
				if (!"".equals(licensePlateNumber) && null != licensePlateNumber)
				{
					map.put("licensePlateNumber", licensePlateNumber);
					map.put("recognitionNumber", licensePlateNumber);
				} else
				{
					map.put("licensePlateNumber", "无车牌");
					map.put("recognitionNumber", "无车牌");
				}
				if (!"".equals(inTime) && null != inTime)
					map.put("inTime", df.parse(inTime));
				else
					map.put("inTime", new Date());

				map.put("addTime", new Date());

				// 入库
				rowcount = this.getMapper().saveGdEntranceRecognize(map);

				if (rowcount > 0)
					result = "{\"errorcode\":0}";
				else
					result = "{\"errorcode\":1}";

			} catch (Exception e)
			{
			}

		} catch (Exception e)
		{
			LOG.error("4.1上传进场数据异常(老系统):" + e.getMessage());
			result = "{\"errorcode\":1}";
		}

		//存入redis以及把数据推送给张泉金
		//RedisVideoStopCarInfo.oldEntrance(sourceData, dataTunnelPublishClient);


		LOG.info("4.1上传进场数据结果(老系统):" + result);
		return result;

	}

	public int updateParkingEntranceRecordImagePath(Map<String, Object> map)
	{
		return getMapper().updateParkingEntranceRecordImagePath(map);
	}

	public Map<String, Object> findByWSEntranceInfo(Map<String, Object> map)
	{
		return getMapper().findByWSEntranceInfo(map);
	}

	public int updateBySelective(Map<String, Object> map)
	{
		return getMapper().updateBySelective(map);
	}

	public Map<String, Object> findGdEntranceRecognize(Map<String, Object> map)
	{
		return getMapper().findGdEntranceRecognize(map);
	}

	public String updateMark(String sourceData)
	{

		LOG.info("更新车位离场信息:" + sourceData);
		String result = "{\"errorcode\":1}";
		try
		{
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int rowcount = 0;
			JSONObject map = JSON.parseObject(sourceData);
			String outTime = map.getString("outTime");

			if ("".equals(outTime) || null == outTime)
				outTime = map.getString("inTimeStr");

			outTime = outTime.replace("/", "-").replace("\\", "-").replace("--", "-");

			map.put("outTime", df.parse(outTime));
			rowcount = this.getMapper().updateMark1(map);
			if (rowcount > 0)
				result = "{\"errorcode\":0}";
			else
				result = "{\"errorcode\":1}";

		} catch (Exception e)
		{
			result = "{\"errorcode\":1}";
			LOG.error("更新车位离场异常:" + result);
		}

		LOG.info("更新车位离场结果 :" + result);

		return result;
	}

	public void saveGdEntranceRecognize1(JSONArray ja)
	{
		getMapper().saveGdEntranceRecognize1(ja);

	}

	public int findEntranceNumber(Map<String, Object> map)
	{
		return getMapper().findEntranceNumber(map);
	}

	public Map<String, Object> findEntranceInfo(Integer carParkId, Integer oldRecordId)
	{
		Map<String, Integer> map = new HashMap<String, Integer>();

		map.put("carParkId", carParkId);
		map.put("oldRecordId", oldRecordId);

		return getMapper().findEntranceInfo(map);
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
		String key = "oldEntrance" + carParkId + oldRecordId;
		Long num = JedisPoolUtils.incr(key);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// key 保留3600秒（1小时）,
		if (num == 0)
		{
			JedisPoolUtils.setex(key, 3600, df.format(new Date()));
		} else if (num >= 9)
		{
			// redis的Key 一个小时候自动失效
			return true;
		} else
			return false;

		return false;

	}

	public Map<String, Object> yunUpdate(JSONObject jsonData)
	{
		return getMapper().yunUpdate(jsonData);
	}
}
