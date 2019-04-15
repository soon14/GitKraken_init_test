package cn.rf.hz.web.cloudpark.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.cloudpark.service.ParkingInformation;

import cn.rf.hz.web.cloudpark.daoxx.Tc_parkInformationMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_parkingareaMapper;
import cn.rf.hz.web.cloudpark.moder.Tc_parkingarea;
import cn.rf.hz.web.utils.DataConstants;
import cn.rf.hz.web.utils.JedisPoolUtils;
import cn.rf.hz.web.utils.StringUtil;

@Service("parkingInformation")
public class ParkingInformationImp implements ParkingInformation {
	private final static Logger LOG = Logger.getLogger(ParkingInformationImp.class);
	@Autowired
	private Tc_parkInformationMapper informationMapper;
	@Autowired
	private Tc_parkingareaMapper parkingareaMapper;

	@Override
	public JSONObject queryParkingInformation(String requestBody) {

		LOG.info("=====getCarInfoForCharge:==请求requestBody:" + requestBody);
		JSONObject data = JSON.parseObject(requestBody);
		String ParkingLotNo = data.getString("parkinglotno");
		long startTime1 = System.currentTimeMillis();
		List<Object> list = informationMapper.queryparkingarea(ParkingLotNo);
		int count = 0; // 车位总数
		int stopedCount = 0; // 已停车位数
		int prepCount = 0; // 剩余车位数
		ArrayList tmpList = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			LOG.info("=====getCarInfoForCharge:" + i);
			// System.out.println(list.get(i));
			JSONObject area = new JSONObject((Map) list.get(i));
			count = count + area.getIntValue("countcw");
			LOG.info("=====getCarInfoForCharge:count" + count);
			stopedCount = stopedCount + area.getIntValue("stopedcw");
			LOG.info("=====getCarInfoForCharge:stopedCount" + stopedCount);
			prepCount = prepCount + area.getIntValue("prepcw");
			LOG.info("=====getCarInfoForCharge:prepCount" + prepCount);
			LOG.info("=====getCarInfoForCharge:11111");
			List<Object> listchannels = informationMapper.queryChannelByParkingLotNoAndAreaID(area);
			area.remove("ParkingLotNo");
			LOG.info("=====getCarInfoForCharge:22222");
			JSONArray array = new JSONArray();
			if (listchannels != null && listchannels.size() > 0) {
				for (Object channel : listchannels) {
					JSONObject model = JSONObject.parseObject(JSONObject.toJSONString(channel));
					LOG.info("=====getCarInfoForCharge:333," + JSONObject.toJSONString(channel));
					JSONObject object = new JSONObject();
					object.put("channelid", model.getString("ChannelID"));
					if (model.containsKey("NextAreaID")) {
						if (model.getString("NextAreaID") == null || model.getString("NextAreaID").isEmpty()
								|| "0".equals(model.getString("NextAreaID")))
							object.put("nextareaid", "");
						else {
							object.put("nextareaid", model.getString("NextAreaID"));
						}
					} else {
						object.put("nextareaid", "");
					}
					array.add(object);
				}
			}
			/*
			 * for( int j=0;j<listchannels.size();j++){ channel =new
			 * JSONObject((Map)listchannels.get(i)); }
			 */
			area.put("channles", array);
			tmpList.add(area);
		}
		LOG.info("=====getCarInfoForCharge:4444444");
		JSONArray areas = new JSONArray(tmpList);
		LOG.info("=====getCarInfoForCharge:55555");
		JSONObject result = new JSONObject();
		result.put("carparkid", ParkingLotNo);
		result.put("areas", areas);
		result.put("count", count);
		result.put("stopedcount", stopedCount);
		result.put("prepcount", prepCount);
		LOG.info(result);
		return result;
	}

	@Override
	public Integer countChannelAll() {
		long startTime1 = System.currentTimeMillis();
		Integer countChannel = informationMapper.countChannelAll();
		LOG.info(countChannel);
		return countChannel;
	}

	@Override
	public Integer countAll() {
		long startTime1 = System.currentTimeMillis();
		Integer count = informationMapper.countAll();
		LOG.info(count);
		return count;
	}

	@Override
	public String queryParkins(int id, String parkname, int offset, int limit) {
		LOG.info("------queryParkins-----id=" + id + ",parkname=" + parkname + ",offset=" + offset + ",limit=" + limit);
		long startTime1 = System.currentTimeMillis();
		Map map = new HashMap<String, Integer>();
		map.put("offset", offset);
		map.put("limit", limit);
		if (id > 0) {
			map.put("id", id);
		}
		if (!StringUtil.isEmpty(parkname)) {
			map.put("parkname", parkname);
		}
		List<Object> listchannels = informationMapper.queryparkings(map);
		JSONArray channels = new JSONArray(listchannels);
		LOG.info(channels);
		return channels.toJSONString();
	}

	@Override
	public String queryParkingChannels(int id, String parkname, int offset, int limit) {
		LOG.info("-----queryParkingChannels------id=" + id + ",parkname=" + parkname + ",offset=" + offset + ",limit="
				+ limit);
		long startTime1 = System.currentTimeMillis();
		LOG.info("offset=" + offset + ",limit=" + limit);
		Map map = new HashMap<String, Integer>();
		map.put("offset", offset);
		map.put("limit", limit);
		if (id > 0) {
			map.put("id", id);
		}
		if (!StringUtil.isEmpty(parkname)) {
			map.put("parkname", parkname);
		}
		List<Object> listchannels = informationMapper.queryparkingchannels(map);
		JSONArray channels = new JSONArray(listchannels);
		LOG.info(channels.toJSONString());
		return channels.toJSONString();
	}

	@Override
	public String queryParkingChannelsById(int id) {
		LOG.info("-----queryParkingChannels------id=" + id);
		long startTime1 = System.currentTimeMillis();
		LOG.info("id=" + id);
		Map map = new HashMap<String, Integer>();
		if (id > 0) {
			map.put("id", id);
		}
		List<Object> listchannels = informationMapper.queryparkingchannelById(map);
		JSONArray channels = new JSONArray(listchannels);
		LOG.info(channels.toJSONString());
		return channels.toJSONString();
	}

	public Tc_parkingarea getAreaInfo(String areaid) {
		LOG.info("=============getArea areaid==========" + areaid);
		Tc_parkingarea area = null;
		String cacheKey = DataConstants.CLOUDPARK_AREA_CACHE_ + areaid;
		if (JedisPoolUtils.exists(cacheKey)) {
			String areaInfo = JedisPoolUtils.get(cacheKey);
			if (null != areaInfo && !areaInfo.isEmpty()) {
				LOG.info("=============parentAreaId from cache:" + areaInfo);
				area = JSONObject.parseObject(areaInfo, Tc_parkingarea.class);
			}
		} else {
			Tc_parkingarea tc_area = parkingareaMapper.selectByPrimaryKey(Integer.valueOf(areaid));
			if (tc_area != null) {
				/*
				 * parentAreaId = area.getAreaType();//父级别的区域 if (parentAreaId
				 * != null && parentAreaId.length() > 0) {
				 * JedisPoolUtils.setex(cacheKey, 3600 * 24 * 30, //父
				 * 区域一般不变话，过期时间设置为一个月 parentAreaId); }
				 */
				// BeanUtils.copyProperties(tc_area, area);
				JedisPoolUtils.setex(cacheKey, 3600 * 24 * 30, // 父
																// 区域一般不变话，过期时间设置为一个月
						JSONObject.toJSONString(tc_area));
				LOG.info("=============parentAreaId from db:" + JSONObject.toJSONString(tc_area));
			}
		}
		LOG.info("=============parentAreaId==========" + JSONObject.toJSONString(area));
		return area;
	}
}
