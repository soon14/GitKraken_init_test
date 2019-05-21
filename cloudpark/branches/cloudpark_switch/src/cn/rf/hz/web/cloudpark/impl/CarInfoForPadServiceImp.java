package cn.rf.hz.web.cloudpark.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.cloudpark.model.Tc_userinfo;
import com.reformer.cloudpark.service.ParkingInformation;

import cn.rf.hz.web.cloudpark.daoxx.Pb_parkingparmMapper;
import cn.rf.hz.web.cloudpark.daoxx.Pb_productMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_userinfoMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_whiteuserinfoMapper;
import cn.rf.hz.web.cloudpark.moder.ParkingLotParameter;
import cn.rf.hz.web.cloudpark.moder.Pb_parkingparm;
import cn.rf.hz.web.cloudpark.moder.Pb_product;
import cn.rf.hz.web.cloudpark.moder.Tc_channel;
import cn.rf.hz.web.cloudpark.moder.Tc_chargerecordinfo;
import cn.rf.hz.web.cloudpark.moder.Tc_crosspoint;
import cn.rf.hz.web.cloudpark.moder.Tc_parkingarea;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm_in;
import cn.rf.hz.web.cloudpark.moder.Tc_whiteuserinfo;
import cn.rf.hz.web.cloudpark.service.BoxInfoService;
import cn.rf.hz.web.cloudpark.service.CarInfoForPadService;
import cn.rf.hz.web.cloudpark.service.Tc_channelService;
import cn.rf.hz.web.cloudpark.service.Tc_crosspointService;
import cn.rf.hz.web.sharding.dao.Tc_chargerecordinfoMapper;
import cn.rf.hz.web.sharding.dao.Tc_usercrdtm_inMapper;
import cn.rf.hz.web.utils.BigDataAnalyze;
import cn.rf.hz.web.utils.DataConstants;
import cn.rf.hz.web.utils.DateUtil;
import cn.rf.hz.web.utils.JedisPoolUtils;
import cn.rf.hz.web.utils.ParkingLotConfigUtil;
import cn.rf.hz.web.utils.StringUtil;

/**
 * 获取进场车辆相关信息到PAD
 */
@Service("carInfoForPadService")
public class CarInfoForPadServiceImp implements CarInfoForPadService {
	private final static Logger LOG = Logger.getLogger(CarInfoForPadServiceImp.class);

	private final static int TEMP_CAR = 0; // 临时车
	private final static int LONG_CAR = 1; // 长期车
	private final static int WHITE_CAR = 2; // 白名单
	private final static int XBYY_CAR = 3; // 行呗预约
	private final static int TYPE_WHITE = 0; // redis中usergroup，type白名单是0，黑名单是1，长期车是2
	private final static int TYPE_LONGCAR = 2;
	private final static int RES_SUCCESS = 0;
	private final static int RES_FAILED = 2;

	private final Semaphore charge_semp = new Semaphore(1000);
	@Autowired
	private Tc_usercrdtm_inMapper usercrdtm_inMapper;
	@Autowired
	private Tc_chargerecordinfoMapper chargerecordinfoMapper;
	@Autowired
	private Pb_productMapper productMapper;
	@Autowired
	private PublicParkingService publicParkingService;

	@Autowired
	private Tc_userinfoMapper userinfoMapper;

	@Autowired
	private Tc_whiteuserinfoMapper whiteuserinfoMapper;

	@Autowired
	private Tc_channelService channelService;

	@Autowired
	private Pb_parkingparmMapper parkingparmMapper;

	@Autowired
	private DateSharingServiceImp dateSharingService;

	@Autowired
	private BoxInfoService boxInfoService;

	@Autowired
	private Tc_crosspointService crosspointService;

	@Autowired
	private ParkingInformation parkingInformation;

	@Override
	/**
	 * 获取进场车辆相关信息到PAD
	 */
	public JSONObject getCarInfoForPad(String requestBody) {
		// 返回结果
		JSONObject resultJSON = new JSONObject();
		try {
			JSONObject data = JSON.parseObject(requestBody);
			String parkNo = data.getString("parkNo");
			String carCode = data.getString("carCode");
			Date outTime = data.getDate("outTime");
			String outChannelId = data.getString("channelId");
			// 判断是不是为场中场或者场内切换
			ParkingLotParameter pp = getParkingParamInfo(parkNo);
			boolean isInparkingOrSwitch = false;
			if (pp.isIsinparkingswitch() || pp.isIsinparkinglot()) {
				isInparkingOrSwitch = true;
			}
			// 1.获取最新的进场记录
			Tc_usercrdtm_in usercrdtmIn = getUserCrdtmIn(parkNo, carCode, isInparkingOrSwitch);
			if (usercrdtmIn != null) {
				String lastOuttime = null;
				lastOuttime = DateUtil.getPlusTime(usercrdtmIn.getLastouttime());
				Date inTime = usercrdtmIn.getCrdtm();
				resultJSON.put("lastOuttime", lastOuttime); // 最晚离场时间
				// 当前入场时间的缴费记录
				JSONObject json_chargeHistory = getChargeHistory(parkNo, carCode, inTime);
				JSONArray array_chargeHistory = json_chargeHistory.getJSONArray("jsonArray");
				resultJSON.put("chargeHistory", array_chargeHistory); // 缴费记录
				resultJSON.put("outType", 0); // 出场类型（长期1,临停0）
				// 车辆信息
				float paidValue = json_chargeHistory.getFloat("paidValue");
				int channelId = usercrdtmIn.getChannelid();
				JSONObject carInfo = new JSONObject();
				// 获取车辆信息
				carInfo = getCarInfo(usercrdtmIn, paidValue, outTime, isInparkingOrSwitch);
				// 出场描述
				String outDesc = carInfo.getString("outDesc");
				int channelCarType = carInfo.getIntValue("channelCarType"); // 车辆类型,用于通道管制
				// 2.行呗入场记录判断
				String appObjectStr = JedisPoolUtils.hget(DataConstants.SHARE_INOUT, carCode);
				if (appObjectStr != null && !"".equals(appObjectStr)) {
					// LOG.info("=========预约记录======:"+appObjectStr);
					JSONObject appObject = JSONObject.parseObject(appObjectStr);
					Date endtime = appObject.getDate("endTime"); // 行呗预约有效结束时间
					// LOG.info("=========预约记录时间======进场时间"+inTime+",预约时间"+endtime);
					if (inTime.getTime() <= endtime.getTime()) {
						outDesc = "行呗预约";
						resultJSON.put("outType", 1);// 出场类型（长期1,临停0）打长期标记，不计费
						channelCarType = XBYY_CAR;
					}
				}

				// 3.判断是否免费离场
				long outTimeLong = outTime.getTime();
				long carin_lastoutTime = usercrdtmIn.getLastouttime().getTime();
				JSONArray turnOverList = carInfo.getJSONArray("turnOverList");
				boolean freeOut = isfreeOut(outTimeLong, carin_lastoutTime, array_chargeHistory, turnOverList);
				if (freeOut) {
					resultJSON.put("outType", 3);// 已缴费或在免费停车时间内
				}

				// 4.判断通道管制和0元车自动放行
				if (outChannelId != null && !outChannelId.isEmpty()) {
					int ruleid = carInfo.getIntValue("mRuleId");
					JSONObject accJson = isAollowCrossChannel(channelCarType, parkNo, ruleid, outChannelId, outTime);
					boolean flag = accJson.getBooleanValue("flag");
					if (flag) {
						// 允许通过，则判断0元车是否自动放行
						flag = isAollowZeroCarPass(parkNo, channelCarType);
						if (!flag) {
							String cartypeName = channelCarType != TEMP_CAR ? "长期车" : "临时车";
							resultJSON.put("outType", -7);
							outDesc = cartypeName + "手动放行";
						}
					} else {
						// 不允许通过，则为通道管制
						resultJSON.put("outType", -7);
						String cartypeName = accJson.getString("cartypeName");
						outDesc = cartypeName + "通道管制";
					}
				}
				resultJSON.put("outDesc", outDesc);
				resultJSON.put("carInfo", carInfo);
				resultJSON.put("resCode", RES_SUCCESS);
				resultJSON.put("resMag", carCode + " success to get usercrdtm_in");
			} else {
				resultJSON.put("resCode", RES_FAILED);
				resultJSON.put("resMag", carCode + " failed to get usercrdtm_in");
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultJSON.put("resCode", RES_FAILED);
		}
		return resultJSON;
	}

	// @Override
	// public JSONObject getHistoryChargeForPad(String requestBody) {
	// JSONObject resultJSON = new JSONObject();
	// try {
	// charge_semp.acquire();
	// JSONObject data = JSON.parseObject(requestBody);
	// String carCode = data.getString("carcode");
	// Date inTime = data.getDate("intime");
	// String parkNo = data.getString("parkno");
	// if (null == parkNo || parkNo.isEmpty()) {
	// throw new Exception("CarInfoForPadServiceImp.parkNo is null or parkNo
	// isempty！ ");
	// }
	// if (inTime == null) {
	// throw new Exception("CarInfoForPadServiceImp.inTime is null or inTime
	// isempty！ ");
	// }
	// if (null == carCode || carCode.isEmpty()) {
	// throw new Exception("CarInfoForPadServiceImp.carCode is null or carCode
	// isempty！ ");
	// }
	// // 缴费历史记录
	// List<Object> chargelist = new ArrayList<>();
	// // 根据入场时间和当前系统时间进行时间分组计算分区并获取缴费记录集合
	// String inTimeStr = DateUtil.getFomartDate(inTime, "yyyy-MM-dd HH:mm:ss");
	// String outTimeStr = DateUtil.getFomartDate(new Date(), "yyyy-MM-dd
	// HH:mm:ss");
	// List<Integer> partitionidlist =
	// dateSharingService.getPartitionids(parkNo, inTimeStr, outTimeStr);
	// for (Integer pitem : partitionidlist) {
	// Map<String, Object> chargeMap = new HashMap<String, Object>();
	// chargeMap.put("PartitionID", pitem);
	// chargeMap.put("ParkingLotNo", parkNo);
	// chargeMap.put("CarCode", carCode);
	// chargeMap.put("InTime", inTime);
	// chargeMap.put("OutTime", null);
	// // 获取当前分区下的缴费记录集合
	// List<Object> itemchargelist =
	// chargerecordinfoMapper.queryChargeHistory(chargeMap);
	// // if (itemchargelist == null || itemchargelist.isEmpty()) {
	// // itemchargelist =
	// // chargerecordinfoMapper.queryChargeHistorybyStatus(chargeMap);
	// // }
	// chargelist.addAll(itemchargelist);
	// }
	// // 获取已交总金额
	// float paidvalue = 0;
	// if (!chargelist.isEmpty()) {
	// for (int i = 0; i < chargelist.size(); i++) {
	// Tc_chargerecordinfo info = (Tc_chargerecordinfo) chargelist.get(i);
	// // 获取收费金额和
	// paidvalue += info.getRealchargeamount() + info.getReductionamount();//
	// 已付金额=实收金额+减免金额
	// }
	// }
	// resultJSON.put("paidvalue", paidvalue);
	// resultJSON.put("carcode", carCode);
	// resultJSON.put("parkno", parkNo);
	// resultJSON.put("rescode", 0);
	// resultJSON.put("resmsg", parkNo + "-" + carCode + ":get chargeHistory
	// success ");
	//
	// charge_semp.release();
	// } catch (Exception e) {
	//
	// resultJSON.put("rescode", -1);
	// resultJSON.put("resmsg", "get chargeHistory failed ");
	// e.printStackTrace();
	// } finally {
	// charge_semp.release();
	// }
	//
	// return resultJSON;
	// }

	/**
	 * @Title: getCarInfo @Description: 获取车辆信息 @param parkNo 车场ID @param carCode
	 *         车牌号 @param inTime 入场时间 @param channelId 通道编号 @param paidValue
	 *         已缴费总金额 @return JSONObject @throws
	 */
	private JSONObject getCarInfo(Tc_usercrdtm_in usercrdtmIn, float paidValue, Date outTime,
			boolean isInparkingOrSwitch) throws Exception {
		String parkNo = usercrdtmIn.getParkinglotno();
		String carCode = usercrdtmIn.getCarcode();
		int carintype = usercrdtmIn.getCarintype();
		Date inTime = usercrdtmIn.getCrdtm();
		String areaId = usercrdtmIn.getAreaId();
		int ruleId = usercrdtmIn.getChargeruleid();
		// 获取长期用户信息
		JSONObject json_userInfo = getUserInfo(parkNo, carCode, carintype, inTime, outTime);
		int channelCarType = json_userInfo.getIntValue("channelCarType");
		Tc_userinfo userInfo = JSONObject.toJavaObject(json_userInfo.getJSONObject("userInfo"), Tc_userinfo.class);
		// 如果长期用户信息不为空,则按照长期车用户算费
		if (userInfo != null) {
			ruleId = userInfo.getChargeruleid();
		}
		// 构建carinfo信息
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("carId", carCode);
		jsonObject.put("parkId", parkNo);
		jsonObject.put("areaId", areaId);
		jsonObject.put("mstart", userInfo == null ? "" : DateUtil.getPlusTime(userInfo.getBgndt()));
		jsonObject.put("freeParkTime", 0);
		jsonObject.put("start", DateUtil.getPlusTime(inTime));
		jsonObject.put("parkType", 0); // 0为普通车场,1为场中场
		jsonObject.put("ruleId", ruleId);
		jsonObject.put("mend", userInfo == null ? "" : DateUtil.getPlusTime(userInfo.getEnddt()));
		jsonObject.put("parkingType", userInfo == null ? 0 : 1);
		// 如果是场内切换/场中场，则需要生成过车点信息
		if (isInparkingOrSwitch) {
			JSONArray array_crosspoint = crosspointService.getCrosspointbyCarcode(carCode, parkNo,
					getPartitionidin(parkNo));
			// 异常处理（场内记录与过车点相同）
			for (int i = 0; i < array_crosspoint.size(); i++) {
				JSONObject json_point = array_crosspoint.getJSONObject(i);
				int point_catintype = 0;
				if (json_point.getLongValue("mstart") > 0 && json_point.getLongValue("mend") > 0) {
					point_catintype = 1;
				}
				if (areaId.equals(json_point.getString("areaId"))
						&& inTime.getTime() == json_point.getLongValue("inoutTime")
						&& ruleId == json_point.getIntValue("ruleId") && carintype == point_catintype) {
					// 更新主记录的付款金额
					array_crosspoint.remove(i);
					break;
				}
			}
			LOG.info("getCarInfoForPad.getUserCrdtmIn.array_crosspoint:" + carCode + "_" + array_crosspoint);
			jsonObject.put("turnOverList", array_crosspoint);
		} else {
			jsonObject.put("turnOverList", new JSONArray());
		}
		jsonObject.put("mRuleId", ruleId);
		jsonObject.put("paidValue", paidValue);
		// 计算长期用户剩余天数
		String currentDateStr = DateUtil.getPlusTime(outTime);
		jsonObject.put("timeleft", -1);
		if (userInfo != null && channelCarType == LONG_CAR) {
			long outTimeLong = outTime.getTime();
			long endTimeLong = userInfo.getEnddt().getTime();
			long timeleft = (endTimeLong - outTimeLong) / 1000;
			String voicealert = ParkingLotConfigUtil.GetParkingLotConfig("voicealert");
			Pb_parkingparm parkingparm = parkingparmMapper.selectByPrimaryKey(parkNo, Integer.valueOf(voicealert));
			if (parkingparm != null) {
				long value = Long.valueOf(parkingparm.getParmvalue());
				if (timeleft <= (value * 24 * 60 * 60)) {
					jsonObject.put("timeleft", timeleft);
				}
			}
		}
		jsonObject.put("end", currentDateStr);
		long parkTime = getTime(outTime, inTime);
		jsonObject.put("parkTime", parkTime);
		switch (channelCarType) {
		case TEMP_CAR:
			jsonObject.put("outDesc", "临时车");
			break;
		case LONG_CAR:
			jsonObject.put("outDesc", "长期车");
			break;
		case WHITE_CAR:
			jsonObject.put("outDesc", "白名单");
			break;
		}
		jsonObject.put("channelCarType", channelCarType);
		jsonObject.put("carintype", carintype);
		return jsonObject;
	}

	/**
	 * @Title: getChargeHistory @Description: 获取入场车辆已缴纳的费用记录 @param parkNo
	 *         车场ID @param carCode 车牌号 @param inTime 入场时间 @return
	 *         JSONObject @throws
	 */
	private JSONObject getChargeHistory(String parkNo, String carCode, Date inTime) {
		JSONObject jsonResult = new JSONObject();
		List<Object> chargelist = new ArrayList<>();
		// 根据入场时间和当前系统时间进行时间分组计算分区并获取缴费记录集合
		String inTimeStr = DateUtil.getFomartDate(inTime, "yyyy-MM-dd HH:mm:ss");
		String outTimeStr = DateUtil.getFomartDate(new Date(), "yyyy-MM-dd HH:mm:ss");
		List<Integer> partitionidlist = dateSharingService.getPartitionids(parkNo, inTimeStr, outTimeStr);
		for (Integer pitem : partitionidlist) {
			Map<String, Object> chargeMap = new HashMap<String, Object>();
			chargeMap.put("PartitionID", pitem);
			chargeMap.put("ParkingLotNo", parkNo);
			chargeMap.put("CarCode", carCode);
			chargeMap.put("AmountType", 0);
			chargeMap.put("InTime", inTime);
			chargeMap.put("OutTime", null);
			// 获取当前分区下的缴费记录集合
			List<Object> itemchargelist = chargerecordinfoMapper.queryChargeHistory(chargeMap);
			if (itemchargelist == null || itemchargelist.isEmpty()) {
				itemchargelist = chargerecordinfoMapper.queryChargeHistorybyStatus(chargeMap);
			}
			chargelist.addAll(itemchargelist);
		}
		// 生成缴费记录json数组
		JSONArray jsonArray = new JSONArray();
		float paidvalue = 0;
		if (!chargelist.isEmpty()) {
			for (int i = 0; i < chargelist.size(); i++) {
				JSONObject jsonObject = new JSONObject();
				Tc_chargerecordinfo info = (Tc_chargerecordinfo) chargelist.get(i);
				if (info != null) {
					// 获取收费金额和
					paidvalue += info.getRealchargeamount() + info.getReductionamount();// 已付金额=实收金额+减免金额
					// 得到收费jsonArray
					jsonObject.put("parkinglotno", info.getParkinglotno());
					jsonObject.put("outtime", info.getOuttime() == null ? "" : info.getOuttime().getTime());
					jsonObject.put("partitionid", info.getPartitionid());
					float chargemoney = info.getChargemoney();
					int chargeMoneyInt = (int) chargemoney;
					jsonObject.put("chargemoney", chargeMoneyInt);
					jsonObject.put("carcode", info.getCarcode());
					jsonObject.put("recordid", info.getRecordid());
					if (info.getIntime() != null) {
						jsonObject.put("intime", info.getIntime().getTime());
					}
					if (info.getLastouttime() != null) {
						jsonObject.put("lastouttime", info.getLastouttime().getTime());
					}
					if (info.getAreaid() != null) {
						jsonObject.put("areaid", info.getAreaid());
						Tc_parkingarea areaInfo = parkingInformation.getAreaInfo(info.getAreaid().toString());
						jsonObject.put("areaName", areaInfo.getAreaname());
					}
					jsonObject.put("chargesource", info.getChargesource());
					float reductionAmount = info.getReductionamount();
					int reductionAmountInt = (int) reductionAmount;
					jsonObject.put("reductionamount", reductionAmountInt);
					jsonObject.put("stoptime", info.getStoptime());
					jsonObject.put("chargetype", info.getChargetype());
					jsonObject.put("chargeuserid", info.getChargeuserid());
					float realchargeAmount = info.getRealchargeamount();
					int realchargeAmountInt = (int) realchargeAmount;
					jsonObject.put("realchargeamount", realchargeAmountInt);
					jsonObject.put("chargedate", info.getChargedate().getTime());
					jsonArray.add(jsonObject);
				}
			}
		}
		jsonResult.put("jsonArray", jsonArray);
		float paidvalueFloat = paidvalue;
		int paidvalueInt = (int) paidvalueFloat;
		jsonResult.put("paidValue", paidvalueInt);
		return jsonResult;
	}

	/**
	 * @Title: getUserCrdtmIn @Description: 车辆入场记录 @param parkNo 车场ID @param
	 *         carCode 车牌号 @param isInparkingOrSwitch 是否场中场/场内切换 @return @return
	 *         Tc_usercrdtm_in @throws
	 */
	private Tc_usercrdtm_in getUserCrdtmIn(String parkNo, String carCode, boolean isInparkingOrSwitch) {
		Tc_usercrdtm_in usercrdtmIn = null;
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("partitionID", getPartitionidin(parkNo));
			map.put("ParkingLotNo", parkNo);
			map.put("CarCode", carCode);
			usercrdtmIn = usercrdtm_inMapper.queryOneByCarCodeOrderByCrdtm(map);
			// 判断主入场记录晚到，但过车点先到，则以时间最小的过车点作为主记录
			if (usercrdtmIn == null && isInparkingOrSwitch) {
				Tc_crosspoint crosspoint = crosspointService.getFirstCrosspointbyCarcode(carCode, parkNo,
						getPartitionidin(parkNo));
				if (crosspoint != null) {
					LOG.info("getCarInfoForPad.getUserCrdtmIn.crosspoint:" + carCode + "_" + crosspoint.toString());
					Tc_usercrdtm_in point2usercrdtmIn = new Tc_usercrdtm_in();
					point2usercrdtmIn.setUsername("");
					point2usercrdtmIn.setCarcode(carCode);
					point2usercrdtmIn.setChargeruleid(crosspoint.getRuleId() == null ? 0 : crosspoint.getRuleId());
					point2usercrdtmIn.setCrdtm(crosspoint.getInoutTime());
					point2usercrdtmIn.setLastouttime(crosspoint.getLastouttime());
					point2usercrdtmIn.setChannelid(crosspoint.getChannelId());
					point2usercrdtmIn.setParkinglotno(parkNo);
					point2usercrdtmIn.setAreaId(crosspoint.getAreaId());
					if (crosspoint.getMstart() != null && crosspoint.getMend() != null) {
						point2usercrdtmIn.setCarintype(LONG_CAR);
					} else {
						point2usercrdtmIn.setCarintype(TEMP_CAR);
					}
					usercrdtmIn = point2usercrdtmIn;
					LOG.info("getCarInfoForPad.getUserCrdtmIn.point2usercrdtmIn:" + carCode + "_"
							+ crosspoint.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return usercrdtmIn;
	}

	/**
	 * @Title: getUserInfo @Description: 获取当前车辆长期用户信息 @param parkNo 车场 @param
	 *         carCode 车牌号 @param carintype 车辆标记 @param inTime 进场时间 @param
	 *         outTime 出场时间 @return JSONObject @throws
	 */
	private JSONObject getUserInfo(String parkNo, String carCode, int carintype, Date inTime, Date outTime) {
		JSONObject json_UserInfo = new JSONObject();
		// 根据parkNo和licensePlateNumber,查询tc_userinfo
		Tc_userinfo userInfo = null;
		int channelCarType = TEMP_CAR;
		// 判断是否按照长期车规则计算费用
		int binaryCarInType = BinaryCarInType.getBinaryCarInType(carintype, BinaryCarInType.IsLongBit);
		if (binaryCarInType == LONG_CAR) {
			String key = BigDataAnalyze.geListKeyByDataType(parkNo, DataConstants.CLOUDPARK_USERGROUP, "k");
			String longUserStr = JedisPoolUtils.hget(key, carCode);
			// LOG.info("从redis中取长期用户信息:"+longUserStr);
			// 获取长期车数据
			if (longUserStr != null && !longUserStr.isEmpty()) {
				JSONArray userArray = JSONArray.parseArray(longUserStr);
				for (int i = 0; i < userArray.size(); i++) {
					JSONObject userObject = userArray.getJSONObject(i);
					userInfo = new Tc_userinfo();
					userInfo.setBgndt(userObject.getDate("bgndt"));
					userInfo.setEnddt(userObject.getDate("enddt"));
					userInfo.setChargeruleid(userObject.getIntValue("chargeruleid"));
					int userType = userObject.getIntValue("type");// redis中usergroup，type白名单是0，黑名单是1，长期车是2
					switch (userType) {
					case TYPE_WHITE:
						channelCarType = WHITE_CAR;
						break;
					case TYPE_LONGCAR:
						channelCarType = LONG_CAR;
						break;
					}
				}
			} else {
				// 查看是不是白名单
				Tc_whiteuserinfo whiteuserInfo = whiteuserinfoMapper.selectByParkNoAndCarCode(parkNo, carCode);
				if (whiteuserInfo != null) {
					userInfo = new Tc_userinfo();
					userInfo.setBgndt(whiteuserInfo.getStarttime());
					userInfo.setEnddt(whiteuserInfo.getEndtine());
					userInfo.setChargeruleid(whiteuserInfo.getUsecount());
					channelCarType = WHITE_CAR;
				} else {
					// 查看是不是长期用户
					userInfo = userinfoMapper.selectByCarCodeAndParkingLotNo(carCode, parkNo);
					channelCarType = LONG_CAR;
				}
			}
			// 如果出入场时间与长期用户（白名单）有效期没有交集，则不给长期用户信息,按照临停车算
			if (userInfo != null) {
				long inTimeLong = inTime.getTime();
				long outTimeLong = outTime.getTime();
				long bgndtlong = userInfo.getBgndt().getTime();
				long enddtlong = userInfo.getEnddt().getTime();
				LOG.info("inTimeLong" + inTimeLong + ",outTimeLong" + outTimeLong + ",bgndtlong" + bgndtlong
						+ ",enddtlong" + enddtlong);
				if (inTimeLong > enddtlong || outTimeLong < bgndtlong) {
					userInfo = null;
					channelCarType = TEMP_CAR;
				}
			}
		}
		json_UserInfo.put("userInfo", userInfo);
		json_UserInfo.put("channelCarType", channelCarType);
		return json_UserInfo;
	}

	/**
	 * @Title: isAollowCrossChannel @Description: 判断是否允许车辆通过当前通道 @param
	 *         channelCarType 当前车辆的车辆类型 @param ruleid 计费规则（计费包） @param
	 *         outChannelid 出口通道id @param outTime 离场时间 @return boolean @throws
	 */
	private JSONObject isAollowCrossChannel(int channelCarType, String parkNo, int ruleid, String outChannelid,
			Date outTime) {
		boolean flag = true;
		String cartypeName = "临时车";
		int channelId = Integer.valueOf(outChannelid);
		Tc_channel channelInfo = channelService.queryRuleByChannelid(channelId);
		String controlStr = channelInfo.getChannelcontrol();
		LOG.info("isAollowCrossChannel.channel:" + controlStr);
		if (!controlStr.isEmpty()) {
			JSONArray jsonArray = JSONArray.parseArray(controlStr);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject obj = jsonArray.getJSONObject(i);
				// 出场时间
				long outTimeLong = outTime.getTime();
				String outDateStr = DateUtil.formatDate(outTime);
				// 管制开始时间
				String beginTimeStr = outDateStr + " " + obj.getString("bgntime");
				LOG.info("isAollowCrossChannel.beginTimeStr:" + beginTimeStr);
				Date beginTime = DateUtil.StringfomateDate(beginTimeStr);
				long beginTimeLong = beginTime.getTime();
				// 管制结束时间
				String endTimeStr = outDateStr + " " + obj.getString("endtime");
				LOG.info("isAollowCrossChannel.endTimeStr:" + endTimeStr);
				Date endTime = DateUtil.StringfomateDate(endTimeStr);
				long endTimeLong = endTime.getTime();
				if (outTimeLong >= beginTimeLong && outTimeLong <= endTimeLong) {
					// 如果车辆类型是长期车或白名单，则判断在当前出通道上是否为长期车或白名单
					if (channelCarType == LONG_CAR || channelCarType == WHITE_CAR) {
						channelCarType = getChannelCarTypeByParkIdAndProductId(parkNo, ruleid, outChannelid,
								channelCarType);
					}
					// 根据车辆类型判断是否通道管制
					switch (channelCarType) {
					case TEMP_CAR:
						flag = obj.getBooleanValue("isAllowTemporaryCarInOut");
						break;
					case LONG_CAR:
						flag = obj.getBooleanValue("isAllowLongtermCarInOut");
						cartypeName = "长期车";
						break;
					case WHITE_CAR:
						flag = obj.getBooleanValue("isAllowWhitelistCarInOut");
						cartypeName = "白名单";
						break;
					case XBYY_CAR:
						// 行呗预约不需要通道管制
						flag = true;
						cartypeName = "长期车";
						break;
					}
					break;
				}
			}
		}
		JSONObject resultJSON = new JSONObject();
		resultJSON.put("flag", flag);
		resultJSON.put("cartypeName", cartypeName);
		return resultJSON;
	}

	/**
	 * @Title: isAollowZeroCarPass
	 * @Description: 判断是否允许0元车直接通过
	 * @param parkNo
	 *            车场ID
	 * @return boolean
	 */
	private boolean isAollowZeroCarPass(String parkNo, int channelCarType) {
		boolean flag = true; // 默认0元车自动放行
		String autoCarType = ""; // 自动放行车辆类型标识
		switch (channelCarType) {
		case TEMP_CAR:
			autoCarType = ParkingLotConfigUtil.GetParkingLotConfig("isallowfreetemporarycarautoout");
			break;
		case LONG_CAR:
			autoCarType = ParkingLotConfigUtil.GetParkingLotConfig("isallowfreelongtermcarautoout");
			break;
		case WHITE_CAR:
			autoCarType = ParkingLotConfigUtil.GetParkingLotConfig("isallowfreelongtermcarautoout");
			break;
		case XBYY_CAR:
			autoCarType = ParkingLotConfigUtil.GetParkingLotConfig("isallowfreelongtermcarautoout");
			break;
		}
		// 根据自动放行车辆类型标识，判断当前类型车是否0元自动放行
		if (!autoCarType.isEmpty()) {
			Pb_parkingparm parkingparm = parkingparmMapper.selectByPrimaryKey(parkNo, Integer.valueOf(autoCarType));
			int isAllowCarAutoOut = 0;
			if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
				isAllowCarAutoOut = Integer.parseInt(parkingparm.getParmvalue());
			}
			flag = isAllowCarAutoOut > 0 ? true : false;
		}

		return flag;
	}

	/**
	 * @Title: getChannelCarTypeByParkIdAndProductId @Description:
	 *         根据车场和计费包判断该车辆在当前出通道上是否是长期车或者白名单 @param parkNo 车场ID @param ruleid
	 *         计费包id @param outChannelid 出通道 @param currentCarType
	 *         当前车辆类型 @return int @throws
	 */
	private int getChannelCarTypeByParkIdAndProductId(String parkNo, int ruleid, String outChannelid,
			int currentCarType) {
		int carType = currentCarType;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("parkid", parkNo);
		map.put("productid", ruleid);
		Pb_product productmodel = productMapper.SelectProductByid(map);
		String channelids = productmodel.getChannelids();
		// 如果长期车或者白名单计费规则中channelids不为空，则判断channelids是否包含当前出场通道，channelids为空，车辆类型不变
		if (channelids != null && !channelids.isEmpty()) {
			// 长期车或者白名单计费规则中不包含当前出场通道，则视为临时车处理
			if (!channelids.contains(outChannelid)) {
				carType = TEMP_CAR;
			}
		}
		return carType;
	}

	/**
	 * 
	 * @Title: getMaxLastOutTime @Description: 获取最大的最晚离场时间 @param
	 *         carin_lastoutTime 场内记录中最晚离场时间 @param chargeHistory
	 *         缴费历史记录 @return @return long @throws
	 */
	private long getMaxLastOutTime(long carin_lastoutTime, JSONArray chargeHistory) {
		long lastoutTime = carin_lastoutTime;
		LOG.info("getCarInfoForPad_lastoutTime_-1:" + lastoutTime);
		for (int i = 0; i < chargeHistory.size(); i++) {
			JSONObject jsonObject = chargeHistory.getJSONObject(i);
			long history_lastoutTime = jsonObject.getLongValue("lastouttime");
			if (lastoutTime < history_lastoutTime) {
				lastoutTime = history_lastoutTime;
			}
			LOG.info("getCarInfoForPad_lastoutTime_" + i + ":" + lastoutTime);
		}
		return lastoutTime;
	}

	/**
	 * @Title: isfreeOut @Description: 判断是否免费离场 @param outTime 离场时间 @param
	 *         carin_lastoutTime 最晚离场时间 @param chargeHistory 历史缴费记录 @param
	 *         turnOverList 过车点（场内切换点）记录 @return boolean @throws
	 */
	private boolean isfreeOut(long outTime, long carin_lastoutTime, JSONArray chargeHistory, JSONArray turnOverList) {
		boolean freeOut = true;
		// 获取场内记录最晚离场时间
		long max_carinlastoutTime = getMaxLastOutTime(carin_lastoutTime, chargeHistory);
		// 1、普通场（没有场内切换）的情况
		if (turnOverList.isEmpty()) {
			// 出场时间<最晚离场时间，则免费离场true
			freeOut = outTime >= max_carinlastoutTime ? false : true;
		} else {
			// 2、场中场/场内切换，则比较出场时间和最晚离场时间
			// 1)对过车点进行时间从小到大排序
			List<Tc_crosspoint> list = JSONArray.parseArray(turnOverList.toJSONString(), Tc_crosspoint.class);
			Collections.sort(list, new Comparator<Tc_crosspoint>() {
				public int compare(Tc_crosspoint c1, Tc_crosspoint c2) {
					long value = c1.getInoutTime().getTime() - c2.getInoutTime().getTime();
					return (int) value;
				}
			});
			// 2)比较场内记录最晚离场时间和第一个过车点入场时间
			Tc_crosspoint first_crosspoint = (Tc_crosspoint) list.get(0);
			if (max_carinlastoutTime <= first_crosspoint.getInoutTime().getTime()) {
				return false;
			}
			// 3)比较相邻两点间的最晚离场时间和入场/出场时间，判断是否要缴费
			for (int i = 0; i < list.size() - 1; i++) {
				Tc_crosspoint i_crosspoint = (Tc_crosspoint) list.get(i);
				Tc_crosspoint j_crosspoint = (Tc_crosspoint) list.get(i + 1);
				if (i_crosspoint.getLastouttime().getTime() <= j_crosspoint.getInoutTime().getTime()) {
					freeOut = false;
					break;
				}
			}
			// 4)比较最后出场时间和最后一条过车点的最晚离场时间
			Tc_crosspoint point_last = list.get(list.size() - 1);
			freeOut = outTime >= point_last.getLastouttime().getTime() ? false : true;
		}
		return freeOut;
	}

	/**
	 * @Title: getParkingParamInfo @Description: 获取停车场参数配置信息 @param parkNo
	 *         停车场编号 @return ParkingLotParameter @throws
	 */
	public ParkingLotParameter getParkingParamInfo(String parkNo) {
		// 先从redis缓存中获取车场相关参数，如果获取不到，则调用接口获取并将结果写入缓存中
		LOG.info("CarInfoForPadServiceImp_.getParkingParamInfo_parkNo:" + parkNo);
		if (parkNo.indexOf('_') > -1) {
			parkNo = parkNo.split("_")[0];
		}
		ParkingLotParameter pp = null;
		String cacheKey = DataConstants.CLOUDPARK_PARKINGLOTPARAM_CACHE_ + parkNo;
		if (JedisPoolUtils.exists(cacheKey)) {
			String parkinglotParamStr = JedisPoolUtils.get(cacheKey);
			if (null != parkinglotParamStr && !parkinglotParamStr.isEmpty()) {
				LOG.info("CarInfoForPadServiceImp_" + parkNo + ".getParkingParamInfo.JedisPoolUtils.get:"
						+ parkinglotParamStr);
				pp = JSONObject.parseObject(parkinglotParamStr, ParkingLotParameter.class);
			}
		} else {
			JSONObject params = new JSONObject();
			params.put("parkinglotno", parkNo);
			String paramsString = params.toJSONString();
			JSONObject jsonObject = boxInfoService.getParkingParamInfo(paramsString);
			LOG.info("CarInfoForPadServiceImp_" + parkNo + ".getParkingParamInfo:" + jsonObject);
			if (jsonObject != null) {
				pp = JSONObject.toJavaObject(jsonObject, ParkingLotParameter.class);
				JedisPoolUtils.setex(cacheKey, 3600 * 24, JSONObject.toJSONString(pp));// 过期时间设置为一天
				LOG.info("CarInfoForPadServiceImp_" + parkNo + ".getParkingParamInfo.JedisPoolUtils.setex:"
						+ JSONObject.toJSONString(pp));
			}
		}
		return pp;
	}

	/**
	 * 两个时间之间的秒数
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	private long getTime(Date date1, Date date2) {
		// 转换为标准时间
		long mytime = (date1.getTime() - date2.getTime()) / 1000;
		return mytime;
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
