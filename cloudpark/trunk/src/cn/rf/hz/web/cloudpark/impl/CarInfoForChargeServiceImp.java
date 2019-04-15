package cn.rf.hz.web.cloudpark.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.cloudpark.model.Tc_userinfo;

import cn.rf.hz.web.cloudpark.daoxx.Tc_parkInformationMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_userinfoMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_whiteuserinfoMapper;
import cn.rf.hz.web.cloudpark.moder.Tc_chargerecordinfo;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm_in;
import cn.rf.hz.web.cloudpark.moder.Tc_whiteuserinfo;
import cn.rf.hz.web.cloudpark.service.CarInfoForChargeService;
import cn.rf.hz.web.sharding.dao.Tc_chargerecordinfoMapper;
import cn.rf.hz.web.sharding.dao.Tc_usercrdtmMapper;
import cn.rf.hz.web.sharding.dao.Tc_usercrdtm_inMapper;
import cn.rf.hz.web.utils.DataConstants;
import cn.rf.hz.web.utils.DateUtil;
import cn.rf.hz.web.utils.JedisPoolUtils;
import cn.rf.hz.web.utils.StringUtil;

@Service("carInfoForChargeService")
public class CarInfoForChargeServiceImp implements CarInfoForChargeService {
	private final static Logger LOG = Logger.getLogger(CarInfoForChargeServiceImp.class);
	@Autowired
	private Tc_usercrdtm_inMapper usercrdtm_inMapper;
	@Autowired
	private Tc_usercrdtmMapper usercrdtmMapper;
	@Autowired
	private Tc_chargerecordinfoMapper chargerecordinfoMapper;

	@Autowired
	private Tc_parkInformationMapper parkInformationMapper;

	@Autowired
	private Tc_userinfoMapper userinfoMapper;

	@Autowired
	Tc_whiteuserinfoMapper whiteuserinfoMapper;

	@Autowired
	private PublicParkingService publicParkingService;

	@Autowired
	private DateSharingServiceImp dateSharingService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rf.hz.web.cloudpark.impl.CarInfoForChargeService#getCarInfoForCharge(
	 * java.lang.String)
	 */
	@Override
	public JSONObject getCarInfoForCharge(String requestBody) {
		JSONObject resultJSON = new JSONObject();
		LOG.info("=====getCarInfoForCharge:==请求requestBody:" + requestBody);
		JSONObject data = JSON.parseObject(requestBody);
		String parkNo = data.getString("parkId");
		String carCode = data.getString("carId");
		String enterTime = data.getString("enterTime");
		// 获取入场信息
		Tc_usercrdtm_in usercrdtm_in = getUserCrdtmIn(parkNo, carCode, enterTime);
		if (usercrdtm_in != null) {
			try {
				int carInType = usercrdtm_in.getCarintype();
				resultJSON = getCarInfo(parkNo, carCode, enterTime, usercrdtm_in.getChannelid(), carInType);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resultJSON.put("parkingRecordId", usercrdtm_in.getRecordid());
			// 获取收费记录信息
			Date inTime = DateUtil.StrToDate(enterTime);
			JSONArray jsonArray = null;
			try {
				jsonArray = getChargeHistory(parkNo, carCode, inTime);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resultJSON.put("data", jsonArray);
			resultJSON.put("resCode", 0);
			resultJSON.put("resMsg", "Success to get usercrdtm_in and charge");

		} else {
			resultJSON.put("resCode", 2);
			resultJSON.put("resMsg", "failed to get usercrdtm_in");
		}
		LOG.info("CarInfoForChargeService.getCarInfoForCharge.resp:" + carCode + "_" + parkNo + ":"
				+ resultJSON.toJSONString());
		return resultJSON;
	}

	/**
	 * @Title: getCarInfoForApp @Description:给行呗提供车辆信息 (行呗1.3版本用) @param
	 *         requerstBody @return JSONObject @throws
	 */
	public JSONObject getCarInfoForApp(String requestBody) {
		JSONObject resultJSON = new JSONObject();
		LOG.info("=====getCarInfoForApp:==请求requestBody:" + requestBody);
		JSONObject data = JSON.parseObject(requestBody);
		String carCode = data.getString("licensePlateNumber");
		String historyTable = JedisPoolUtils.hget("history_in", carCode);
		if (historyTable != null && !"".equals(historyTable)) {
			String[] arr = historyTable.split(",");
			if (arr.length >= 2) {
				String parkNo = arr[0];
				String inTime = arr[1];
				long inTimeValue = Long.parseLong(inTime);
				String inTimeStr = DateUtil.getLongToDateStr(inTimeValue);
				Tc_usercrdtm_in usercrdtm_in = getUserCrdtmIn(parkNo, carCode, inTimeStr);
				if (usercrdtm_in != null) {
					resultJSON.put("type", 0);
					resultJSON.put("recordId", usercrdtm_in.getRecordid());
					resultJSON.put("oldRecordId", usercrdtm_in.getRecordid());
					resultJSON.put("carParkId", parkNo);
					String parkName = parkInformationMapper.queryparkingNameById(parkNo);
					resultJSON.put("carParkName", parkName);
					resultJSON.put("licensePlateNumber", carCode);
					resultJSON.put("acquiringTime", inTimeStr);
					resultJSON.put("playingTime", "");
					resultJSON.put("areFare", 0);
					// 获取行呗App预约信息
					String appObjectStr = JedisPoolUtils.hget(DataConstants.SHARE_INOUT, carCode);
					LOG.info("=========行呗App预约信息======:" + appObjectStr);
					if (appObjectStr != null && !"".equals(appObjectStr)) {
						JSONObject appObject = JSONObject.parseObject(appObjectStr);
						String resDate = appObject.getString("ordertime");
						resultJSON.put("resDate", resDate);
					}
					// 获取图片
					String imagePath = "";
					if (usercrdtm_in.getImagepath() != null && usercrdtm_in.getImagepath().isEmpty()) {
						imagePath = usercrdtm_in.getImagepath();
					}
					resultJSON.put("picUrl", imagePath);
					resultJSON.put("latitude", "");
					resultJSON.put("longitude", "");
					resultJSON.put("province", "");
					resultJSON.put("mycity", "");
					resultJSON.put("dist", "");
					resultJSON.put("orderMode", 0);
					resultJSON.put("reservationNumber", "");
					resultJSON.put("resCode", 0);
					resultJSON.put("resMsg", "Success to get carinfo");
				} else {
					resultJSON.put("resCode", 2);
					resultJSON.put("resMsg", carCode + "failed to get carinfo, usercrdtm_in is null");
				}
			} else {
				resultJSON.put("resCode", 4);
				resultJSON.put("resMsg", carCode + "failed to get carinfo, historyTable arr.length<2");
			}
		} else {
			resultJSON.put("resCode", 3);
			resultJSON.put("resMsg", carCode + "failed to get carinfo, historyTable is null");
		}

		return resultJSON;
	}

	/**
	 * @Title: getHistoryInCarInfoForApp @Description:给行呗提供历史入场的车辆信息
	 *         (行呗1.4版本用) @param requerstBody @return JSONObject @throws
	 */
	public JSONObject getHistoryInCarInfoForApp(String requestBody) {
		JSONObject resultJSON = new JSONObject();
		LOG.info("=====getHistoryInCarInfoForApp:==请求requestBody:" + requestBody);
		JSONObject data = JSON.parseObject(requestBody);
		String carCode = data.getString("licensePlateNumber");
		// 获取该车辆最新的入场记录
		JSONObject historyInJSON = getHistoryIn(carCode);
		int msgcode = historyInJSON.getIntValue("msgcode");
		if (msgcode == 0) {
			String parkNo = historyInJSON.getString("parkNo");
			int int_Parkinglotno = Integer.parseInt(parkNo);
			// 8000-9000的车场属于评估车场,不同步数据到行呗
			if (int_Parkinglotno >= 8000 && int_Parkinglotno <= 9000) {
				resultJSON.put("resCode", 2);
				resultJSON.put("resMsg", carCode + " failed to get usercrdtm_in,data from assessment parkinglotno is"
						+ int_Parkinglotno);
				return resultJSON;
			}
			long inTimeValue = historyInJSON.getLongValue("inTime");
			String inTimeStr = DateUtil.getLongToDateStr(inTimeValue);
			// 获取数据库入场记录
			Tc_usercrdtm usercrdtm = getUserCrdtm(parkNo, carCode, inTimeStr, 0);
			if (usercrdtm != null) {
				// 判断当前车辆是否已离场
				JSONObject historyOutJSON = getHistoryOut(carCode);
				int isout = 0; // 0表示未离场，1表示已离场
				int ourMsgcode = historyOutJSON.getIntValue("msgcode");
				if (ourMsgcode == 0) {
					long outTimeValue = historyOutJSON.getLongValue("outTime");
					if (outTimeValue > inTimeValue) {
						isout = 1;
					}
				}
				// 如果场内车辆停留时间超过3天，通知行呗已出场
				if (isout == 0) {
					long inTime = usercrdtm.getCrdtm().getTime();
					long currentTime = new Date().getTime();
					long differenceTime = currentTime - inTime;
					if (differenceTime >= (3 * 24 * 3600 * 1000)) {
						isout = 1;
					}
				}

				resultJSON.put("isout", isout);
				resultJSON.put("type", 0);
				resultJSON.put("recordId", usercrdtm.getRecordid());
				resultJSON.put("oldRecordId", usercrdtm.getRecordid());
				resultJSON.put("carParkId", parkNo);
				String parkName = parkInformationMapper.queryparkingNameById(parkNo);
				resultJSON.put("carParkName", parkName);
				resultJSON.put("licensePlateNumber", carCode);
				resultJSON.put("acquiringTime", inTimeStr);
				resultJSON.put("playingTime", "");
				resultJSON.put("areFare", 0);
				// 获取行呗App预约信息
				String appObjectStr = JedisPoolUtils.hget(DataConstants.SHARE_INOUT, carCode);
				LOG.info("getHistoryInCarInfoForApp.appObjectStr:" + appObjectStr);
				if (appObjectStr != null && !"".equals(appObjectStr)) {
					JSONObject appObject = JSONObject.parseObject(appObjectStr);
					String resDate = appObject.getString("ordertime");
					resultJSON.put("resDate", resDate);
				}
				// 获取图片
				String imagePath = "";
				if (usercrdtm.getImagepath() != null && usercrdtm.getImagepath().isEmpty()) {
					imagePath = usercrdtm.getImagepath();
				}
				resultJSON.put("picUrl", imagePath);
				resultJSON.put("latitude", "");
				resultJSON.put("longitude", "");
				resultJSON.put("province", "");
				resultJSON.put("mycity", "");
				resultJSON.put("dist", "");
				resultJSON.put("orderMode", 0);
				resultJSON.put("reservationNumber", "");

				resultJSON.put("resCode", 0);
				resultJSON.put("resMsg", "Success to get carinfo");
			} else {
				resultJSON.put("resCode", 1);
				resultJSON.put("resMsg", carCode + " failed to get carinfo,getusercrdtm is null");
			}

		} else {
			resultJSON.put("resCode", 2);
			resultJSON.put("resMsg", carCode + " failed to get carinfo, historyInTable is null");
		}

		return resultJSON;
	}

	/**
	 * @Title: getHistoryOutCarInfoForApp
	 * @Description:给行呗提供历史出场的车辆信息(行呗1.4版本用)
	 * @param requerstBody
	 * @return JSONObject
	 */
	public JSONObject getHistoryOutCarInfoForApp(String requestBody) {
		JSONObject resultJSON = new JSONObject();
		LOG.info("=====getHistoryOutCarInfoForApp:==请求requestBody:" + requestBody);
		JSONObject data = JSON.parseObject(requestBody);
		String carCode = data.getString("licensePlateNumber");
		// 获取当前车辆最新离场记录
		JSONObject historyOutJSON = getHistoryOut(carCode);
		int msgcode = historyOutJSON.getIntValue("msgcode");
		if (msgcode == 0) {
			long outTimeValue = historyOutJSON.getLongValue("outTime");
			String outTimeStr = DateUtil.getLongToDateStr(outTimeValue);
			String parkNo = historyOutJSON.getString("parkNo");
			resultJSON.put("carParkId", parkNo);
			resultJSON.put("licensePlateNumber", carCode);
			resultJSON.put("outTime", outTimeStr);

			resultJSON.put("resCode", 0);
			resultJSON.put("resMsg", "Success to get carinfo");
		} else {
			resultJSON.put("resCode", 2);
			resultJSON.put("resMsg", carCode + " failed to get outcarinfo, historyOutTable is null");
		}

		return resultJSON;
	}

	/**
	 * @Title: getUserCrdtmIn @Description: 车辆入场记录 @param parkNo 车场ID @param
	 *         carCode 车牌号 @param carCode 车牌号 @return inTime 入场时间 @return
	 *         Tc_usercrdtm_in @throws
	 */
	private Tc_usercrdtm_in getUserCrdtmIn(String parkNo, String carCode, String inTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		Tc_usercrdtm_in usercrdtm_in = new Tc_usercrdtm_in();
		map.put("PartitionID", getPartitionidin(parkNo));
		map.put("ParkingLotNo", parkNo);
		map.put("CarCode", carCode);
		map.put("Crdtm", inTime);
		List<Object> objectList = usercrdtm_inMapper.queryByCondition(map);
		if (objectList != null && !objectList.isEmpty()) {
			usercrdtm_in = (Tc_usercrdtm_in) objectList.get(0);
		} else {
			return null;
		}
		return usercrdtm_in;
	}

	/**
	 * @Title: getUserCrdtm
	 * @Description: 车辆出入记录
	 * @param parkNo
	 *            车场ID
	 * @param carCode
	 *            车牌号
	 * @param inoutTime
	 *            入场或出场时间
	 * @param inorout
	 *            入场或出场标记
	 * @return Tc_usercrdtm
	 */
	private Tc_usercrdtm getUserCrdtm(String parkNo, String carCode, String inoutTime, int inorout) {
		Map<String, Object> map = new HashMap<String, Object>();
		Tc_usercrdtm usercrdtm = new Tc_usercrdtm();
		Date inoutTimeDate = DateUtil.StrToDate(inoutTime);
		map.put("PartitionID", getPartitionid(parkNo, inoutTimeDate));
		map.put("ParkingLotNo", parkNo);
		map.put("CarCode", carCode);
		map.put("Crdtm", inoutTime);
		map.put("InOrOut", inorout);
		List<Object> objectList = usercrdtmMapper.queryUserCrdtmForApp(map);
		if (objectList != null && !objectList.isEmpty()) {
			usercrdtm = (Tc_usercrdtm) objectList.get(0);
		} else {
			return null;
		}
		return usercrdtm;
	}

	private JSONObject getHistoryIn(String carcode) {
		JSONObject resultJSON = new JSONObject();
		resultJSON.put("msgcode", 1);
		String historyInTable = JedisPoolUtils.hget("history_in", carcode);
		if (historyInTable != null && !"".equals(historyInTable)) {
			String[] arr = historyInTable.split(",");
			if (arr.length >= 2) {
				String parkNo = arr[0];
				String inTime = arr[1];
				resultJSON.put("msgcode", 0);
				resultJSON.put("parkNo", parkNo);
				resultJSON.put("inTime", inTime);
			}
		}
		return resultJSON;
	}

	private JSONObject getHistoryOut(String carcode) {
		JSONObject resultJSON = new JSONObject();
		resultJSON.put("msgcode", 1);
		String historyOutTable = JedisPoolUtils.hget("history_out", carcode);
		if (historyOutTable != null && !"".equals(historyOutTable)) {
			String[] arr = historyOutTable.split(",");
			if (arr.length >= 2) {
				String parkNo = arr[0];
				String outTime = arr[1];
				resultJSON.put("msgcode", 0);
				resultJSON.put("parkNo", parkNo);
				resultJSON.put("outTime", outTime);
			}
		}
		return resultJSON;
	}

	/**
	 * @Title: getCarInfo @Description: 获取车辆信息 @param parkNo 车场ID @param carCode
	 *         车牌号 @param inTimeStr 入场时间 @param channelId 通道编号 @return
	 *         JSONObject @throws
	 */
	private JSONObject getCarInfo(String parkNo, String carCode, String inTimeStr, int channelId, int carInType)
			throws Exception {
		// 根据parkNo和licensePlateNumber,查询tc_userinfo
		Date inTime = DateUtil.StrToDate(inTimeStr);
		Date outTime = new Date();
		// 根据parkNo和licensePlateNumber,查询tc_userinfo
		Tc_userinfo userInfo = null;
		// 判断是否按照长期车规则计算费用
		int binaryCarInType = BinaryCarInType.getBinaryCarInType(carInType, BinaryCarInType.IsLongBit);
		if (binaryCarInType == 1) {
			String longUserStr = JedisPoolUtils.hget(DataConstants.CLOUDPARK_USERGROUP, carCode);
			LOG.info("从redis中取长期用户信息:" + longUserStr);
			// 获取长期车数据
			if (longUserStr != null && !longUserStr.isEmpty()) {
				JSONArray userArray = JSONArray.parseArray(longUserStr);
				for (int i = 0; i < userArray.size(); i++) {
					JSONObject userObject = userArray.getJSONObject(i);
					userInfo = new Tc_userinfo();
					userInfo.setBgndt(userObject.getDate("bgndt"));
					userInfo.setEnddt(userObject.getDate("enddt"));
					userInfo.setChargeruleid(userObject.getIntValue("chargeruleid"));
				}
			} else {
				// 查看是不是白名单
				Tc_whiteuserinfo whiteuserInfo = whiteuserinfoMapper.selectByParkNoAndCarCode(parkNo, carCode);
				if (whiteuserInfo != null) {
					userInfo = new Tc_userinfo();
					userInfo.setBgndt(whiteuserInfo.getStarttime());
					userInfo.setEnddt(whiteuserInfo.getEndtine());
					userInfo.setChargeruleid(whiteuserInfo.getUsecount());
				} else {
					// 查看是不是长期用户
					userInfo = userinfoMapper.selectByCarCodeAndParkingLotNo(carCode, parkNo);
				}
			}
			// 如果出入场时间与长期用户有效期没有交集，则按照临停车算
			if (userInfo != null) {
				long inTimeLong = inTime.getTime();
				long outTimeLong = outTime.getTime();
				long bgndtlong = userInfo.getBgndt().getTime();
				long enddtlong = userInfo.getEnddt().getTime();
				if (inTimeLong > enddtlong || outTimeLong < bgndtlong) {
					userInfo = null;
				}
			}
		}
		// 获取收费规则Id
		int ruleId = publicParkingService.getUserRuleId(userInfo, channelId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("start", inTimeStr);
		Date currentDate = new Date();
		String currentDateStr = DateUtil.getPlusTime(currentDate);
		jsonObject.put("end", currentDateStr);
		jsonObject.put("carId", carCode);
		jsonObject.put("turnOverList", new JSONArray());
		// jsonObject.put("tags","");
		jsonObject.put("ruleId", ruleId);
		jsonObject.put("mstart", userInfo == null ? "" : DateUtil.getPlusTime(userInfo.getBgndt()));
		jsonObject.put("mend", userInfo == null ? "" : DateUtil.getPlusTime(userInfo.getEnddt()));
		return jsonObject;
	}

	/**
	 * @throws Exception
	 * @Title: getChargeHistory @Description: 获取入场车辆已缴纳的费用记录 @param parkNo
	 *         车场ID @param carCode 车牌号 @param inTime 入场时间 @return
	 *         JSONObject @throws
	 */
	private JSONArray getChargeHistory(String parkNo, String carCode, Date inTime) throws Exception {
		// 根据入场时间和当前系统时间进行时间分组计算分区并获取缴费记录集合
		String inTimeStr = DateUtil.getFomartDate(inTime, "yyyy-MM-dd HH:mm:ss");
		String outTimeStr = DateUtil.getFomartDate(new Date(), "yyyy-MM-dd HH:mm:ss");
		List<Integer> partitionidlist = dateSharingService.getPartitionids(parkNo, inTimeStr, outTimeStr);
		List<Object> chargelist = new ArrayList<>();
		for (Integer pitem : partitionidlist) {
			Map<String, Object> chargeMap = new HashMap<String, Object>();
			chargeMap.put("PartitionID", pitem);
			chargeMap.put("ParkingLotNo", parkNo);
			chargeMap.put("CarCode", carCode);
			chargeMap.put("InTime", inTime);
			chargeMap.put("OutTime", null);
			// 获取当前分区下的缴费记录集合
			List<Object> itemchargelist = chargerecordinfoMapper.queryByCondition(chargeMap);
			chargelist.addAll(itemchargelist);
		}
		// 生成缴费记录json数组
		JSONArray jsonArray = new JSONArray();
		if (chargelist != null && !chargelist.isEmpty()) {
			for (int i = 0; i < chargelist.size(); i++) {
				JSONObject jsonObject = new JSONObject();
				Tc_chargerecordinfo info = (Tc_chargerecordinfo) chargelist.get(i);
				if (info != null) {
					// 得到收费jsonArray
					jsonObject.put("parkId", parkNo);
					jsonObject.put("carId", carCode);
					jsonObject.put("enterTime", DateUtil.getPlusTime(info.getIntime()));
					jsonObject.put("lastOutTime", DateUtil.getPlusTime(info.getLastouttime()));
					jsonObject.put("payTime", DateUtil.getPlusTime(info.getChargedate()));
					float real = info.getRealchargeamount();
					int realInt = (int) real;
					jsonObject.put("realCost", String.valueOf(realInt));
					float reduction = info.getReductionamount();
					int reductionInt = (int) reduction;
					jsonObject.put("discountCost", String.valueOf(reductionInt));
					jsonArray.add(jsonObject);
				}
			}
		}
		return jsonArray;
	}

	/**
	 * 获取进出场表分区值
	 */
	private Integer getPartitionid(String ParkingLotNo, Date date) {
		int hasresult = StringUtil.getAsciiCode(ParkingLotNo) % 16;
		// 分区字段开始计算
		String partitionIdTemp = "";
		if (hasresult < 10) {
			partitionIdTemp = DateUtil.getFomartDate(date, "yyyyMM") + "0" + hasresult;
		} else {
			partitionIdTemp = DateUtil.getFomartDate(date, "yyyyMM") + hasresult;
		}
		Integer partitionID = Integer.parseInt(partitionIdTemp);
		// 分区字段计算结束
		return partitionID;
	}

	/**
	 * 获取进场（场内）表分区值
	 */
	private int getPartitionidin(String ParkingLotNo) {
		int hasresult = StringUtil.getAsciiCode(ParkingLotNo) % 16;
		return hasresult;
	}

}
