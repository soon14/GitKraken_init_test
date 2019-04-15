package cn.rf.hz.web.cloudpark.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.cloudpark.service.ParkingInformation;

import cn.rf.hz.web.cloudpark.daoxx.Tc_parkingareaMapper;
import cn.rf.hz.web.cloudpark.moder.Tc_crosspoint;
import cn.rf.hz.web.cloudpark.moder.Tc_parkingarea;
import cn.rf.hz.web.cloudpark.service.Tc_crosspointService;
import cn.rf.hz.web.sharding.dao.Tc_crosspointMapper;
import cn.rf.hz.web.sharding.dao.Tc_usercrdtm_inMapper;
import cn.rf.hz.web.utils.BigDataAnalyze;
import cn.rf.hz.web.utils.DataConstants;
import cn.rf.hz.web.utils.JedisPoolUtils;

@Service("crosspointService")
public class Tc_crosspointServiceImp implements Tc_crosspointService {
	private static final Logger logger = LoggerFactory.getLogger(Tc_crosspointServiceImp.class);
	@Autowired
	private Tc_crosspointMapper crosspointMapper;
	@Autowired
	private Tc_parkingareaMapper parkingareaMapper;
	@Autowired
	private Tc_usercrdtm_inMapper usercrdtm_inMapper;
	@Autowired
	private PublicParkingService publicParkingService;

	@Autowired
	private ParkingInformation parkingInformation;

	private static long fsize = 5000;
	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rf.hz.web.cloudpark.impl.Tc_crosspointService#
	 * getCrosspointbyParkinglotsNo(java.lang.String)
	 */
	// [
	// {
	// \"carcode\": \"test\",
	// \"points\": [],
	// \"intime\": 1476531865000,
	// \"recordid\": 38913072,
	// \"parkinglotno\": \"2030\"
	// }
	// ]

	@Override
	public JSONArray getCrosspointbyParkinglotsNo(String parkinglotNo) {
		JSONObject jso = new JSONObject();
		jso.put("ParkingLotNo", parkinglotNo);
		JSONArray array_point = new JSONArray();
		// 出入场全量
		double count = usercrdtm_inMapper.selectcarinCount(parkinglotNo);
		if (count < fsize) {
			jso.put("limtcount", fsize);
			jso.put("fpage", 0);
			List<Object> list = this.usercrdtm_inMapper.queryTc_usercrdtm_in_limit(jso);
			// 生成过车点全量包
			JSONArray arraypoint = getCrosspoint(parkinglotNo, list);
			if (arraypoint != null && !arraypoint.isEmpty()) {
				array_point.addAll(getCrosspoint(parkinglotNo, list));
			}
		} else {
			double spiltcount = Math.ceil(count / fsize);
			jso.put("limtcount", fsize);
			long fpage = 0;
			for (int i = 0; i < spiltcount; i++) {
				fpage = ((i + 1) - 1) * fsize;
				jso.put("fpage", fpage);
				List<Object> list = this.usercrdtm_inMapper.queryTc_usercrdtm_in_limit(jso);
				// 生成过车点全量包
				JSONArray arraypoint = getCrosspoint(parkinglotNo, list);
				if (arraypoint != null && !arraypoint.isEmpty()) {
					array_point.addAll(arraypoint);
				}
			}
		}
		logger.info("Tc_crosspointServiceImp.array_point:" + array_point);
		return array_point;
	}

	public JSONArray getCrosspoint(String parkinglotNo, List<Object> list_in) {
		JSONArray array_point = new JSONArray();// 过车点外层数组
		for (Object obj_in : list_in) {
			String obj_inStr = JSONObject.toJSONString(obj_in);
			JSONObject json_in = JSONObject.parseObject(obj_inStr);
			String carcode = json_in.getString("carcode");
			int partitionID = publicParkingService.getPartitionidin(parkinglotNo);
			// 获取当前车辆过车点记录
			List<Tc_crosspoint> list_crosspoint = crosspointMapper.queryParkingStatebyCarCode(carcode, parkinglotNo,
					partitionID);
			// 过车点json对象
			JSONObject json_crosspoint = new JSONObject();
			// 如果过车点记录不为空，则生成该车辆的过车点全量包
			if (list_crosspoint != null && !list_crosspoint.isEmpty()) {
				json_crosspoint.put("carcode", carcode);
				json_crosspoint.put("intime", json_in.getLongValue("intime"));
				json_crosspoint.put("recordid", json_in.getLongValue("recordid"));
				json_crosspoint.put("parkinglotno", parkinglotNo);
				// 过车点数据数组
				JSONArray points = new JSONArray();
				for (Tc_crosspoint crosspoint : list_crosspoint) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("carcode", crosspoint.getCarcode());
					// 区域
					jsonObject.put("areaId", crosspoint.getAreaId());
					Tc_parkingarea areaInfo = parkingInformation.getAreaInfo(crosspoint.getAreaId());
					jsonObject.put("areaName", areaInfo.getAreaname());
					// 下一个区域
					if (crosspoint.getParentAreaId() != null && !crosspoint.getParentAreaId().isEmpty()) {
						Tc_parkingarea parentArea = parkingInformation.getAreaInfo(crosspoint.getParentAreaId());
						jsonObject.put("parentAreaId", crosspoint.getParentAreaId());
						jsonObject.put("parentAreaName", parentArea.getAreaname());
					}
					jsonObject.put("status", crosspoint.getStatus());
					jsonObject.put("inoutTime", crosspoint.getInoutTime());
					jsonObject.put("ruleId", crosspoint.getRuleId());
					jsonObject.put("mstart", crosspoint.getMstart());
					jsonObject.put("mend", crosspoint.getMend());
					jsonObject.put("channelId", crosspoint.getChannelId());
					jsonObject.put("lastouttime", crosspoint.getLastouttime());
					jsonObject.put("parkinglotno", crosspoint.getParkinglotno());
					jsonObject.put("mruleId", crosspoint.getMruleId());
					points.add(jsonObject);
				}
				json_crosspoint.put("points", points);
			}
			logger.info("Tc_crosspointServiceImp.json_crosspoint:" + json_crosspoint);
			if (json_crosspoint.size() > 0) {
				array_point.add(json_crosspoint);
			}

		}
		return array_point;
	}

	/**
	 * @Title: getCrosspoints @Description: 过车点信息 @param carcode 车牌号 @param
	 *         parkinglotNo 停车场 @param partitionID @return List
	 *         <Tc_crosspoint> @throws
	 */
	public JSONArray getCrosspointbyCarcode(String carcode, String parkinglotNo, int partitionID) {
		JSONArray array_crosspoint = new JSONArray();
		// 先取缓存中数据,缓存取不到取数据库
		String key = BigDataAnalyze.geListKeyByDataType(parkinglotNo, DataConstants.CLOUDPARK_SWITCH, "k");
		String crosspointStr = JedisPoolUtils.hget(key, carcode);
		if (crosspointStr != null) {
			JSONArray crosspointArray = JSONArray.parseArray(crosspointStr);
			for (int i = 0; i < crosspointArray.size(); i++) {
				JSONObject item = crosspointArray.getJSONObject(i);
				JSONArray points = item.getJSONArray("points");
				array_crosspoint.addAll(points);
			}
		} else {
			List<Tc_crosspoint> list_crosspoint = crosspointMapper.queryParkingStatebyCarCode(carcode, parkinglotNo,
					partitionID);
			for (Tc_crosspoint crosspoint : list_crosspoint) {
				String jsonObjectStr = JSONObject.toJSONString(crosspoint);
				JSONObject jsonObject = JSONObject.parseObject(jsonObjectStr);
				// 获取当前区域名称
				Tc_parkingarea areaInfo = parkingInformation.getAreaInfo(crosspoint.getAreaId());
				jsonObject.put("areaName", areaInfo.getAreaname());
				// 获取上一个区域名称
				if (crosspoint.getParentAreaId() != null && !crosspoint.getParentAreaId().isEmpty()) {
					Tc_parkingarea parentAreaInfo = parkingInformation.getAreaInfo(crosspoint.getParentAreaId());
					jsonObject.put("parentAreaName", parentAreaInfo.getAreaname());
				}
				array_crosspoint.add(jsonObject);
			}
		}
		return array_crosspoint;
	}

	public Tc_crosspoint getFirstCrosspointbyCarcode(String carcode, String parkinglotNo, int partitionID) {
		Tc_crosspoint crosspoint = null;
		// 先从缓存中获取
		String key = BigDataAnalyze.geListKeyByDataType(parkinglotNo, DataConstants.CLOUDPARK_SWITCH, "k");
		String crosspointStr = JedisPoolUtils.hget(key, carcode);
		if (crosspointStr != null) {
			JSONArray crosspointArray = JSONArray.parseArray(crosspointStr);
			logger.info("Tc_crosspointServiceImp.CLOUDPARK_SWITCH.crosspointArray:" + crosspointArray);
			JSONObject json_crosspointArray = crosspointArray.getJSONObject(0);
			JSONArray points = json_crosspointArray.getJSONArray("points");
			JSONObject json_crosspoint = points.getJSONObject(0);
			crosspoint = JSONObject.toJavaObject(json_crosspoint, Tc_crosspoint.class);
		} else {
			Map<String, Object> param = new HashMap<>();
			param.put("partitionID", partitionID);
			param.put("ParkingLotNo", parkinglotNo);
			param.put("CarCode", carcode);
			crosspoint = crosspointMapper.queryFirstCrosspointByCarCode(param);
		}
		return crosspoint;
	}
}
