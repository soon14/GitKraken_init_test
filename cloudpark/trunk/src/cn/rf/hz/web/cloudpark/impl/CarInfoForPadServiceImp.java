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

import cn.rf.hz.web.cloudpark.daoxx.Pb_parkingparmMapper;
import cn.rf.hz.web.cloudpark.daoxx.Pb_productMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_userinfoMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_whiteuserinfoMapper;
import cn.rf.hz.web.cloudpark.moder.Pb_parkingparm;
import cn.rf.hz.web.cloudpark.moder.Pb_product;
import cn.rf.hz.web.cloudpark.moder.Tc_channel;
import cn.rf.hz.web.cloudpark.moder.Tc_chargerecordinfo;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm_in;
import cn.rf.hz.web.cloudpark.moder.Tc_whiteuserinfo;
import cn.rf.hz.web.cloudpark.service.CarInfoForPadService;
import cn.rf.hz.web.cloudpark.service.Tc_channelService;
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

	@Override
	/**
	 * 获取进场车辆相关信息到PAD
	 */
	public JSONObject getCarInfoForPad(String requestBody) {
		// 返回结果
		JSONObject resultJSON = new JSONObject();
		try {
			// LOG.info("===========================请求requestBody:"+requestBody);
			JSONObject data = JSON.parseObject(requestBody);
			String parkNo = data.getString("parkNo");
			String carCode = data.getString("carCode");
			Date outTime = data.getDate("outTime");
			String outChannelId = data.getString("channelId");
			// 获取最新的进场记录
			Tc_usercrdtm_in usercrdtmIn = getUserCrdtmIn(parkNo, carCode);
			// LOG.info("===========================请求Tc_usercrdtm_in:"+usercrdtmIn);
			// System.out.println(usercrdtmIn);
			int resCode = 0;
			if (usercrdtmIn != null) {
				// LOG.info("===========================111:");
				String lastOuttime = null;
				lastOuttime = DateUtil.getPlusTime(usercrdtmIn.getLastouttime());
				Date inTime = usercrdtmIn.getCrdtm();
				resultJSON.put("lastOuttime", lastOuttime); // 最晚离场时间
				// 当前入场时间的缴费记录条件
				JSONObject chargeHistory = getChargeHistory(parkNo, carCode, inTime);
				JSONArray chargeHistory_Array = chargeHistory.getJSONArray("jsonArray");
				resultJSON.put("chargeHistory", chargeHistory_Array); // 缴费记录
				resultJSON.put("outType", 0); // 出场类型（长期1,临停0）
				// 车辆信息
				float paidValue = chargeHistory.getFloat("paidValue");
				int channelId = usercrdtmIn.getChannelid();
				JSONObject carInfo = new JSONObject();
				// 获取车辆信息
				carInfo = getCarInfo(parkNo, carCode, inTime, channelId, paidValue, outTime,
						usercrdtmIn.getCarintype());
				String outDesc = carInfo.getString("outDesc");
				int channelCarType = carInfo.getIntValue("channelCarType"); // 车辆类型
																			// 用于通道管制
				// 行呗入场记录判断
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
				// 如果离场时间<=最晚离场时间,设置出场标签,已缴费或在免费停车时间内
				long max_lastoutTime = getMaxLastOutTime(usercrdtmIn.getLastouttime().getTime(), chargeHistory_Array);
				if (outTime.getTime() <= max_lastoutTime) {
					resultJSON.put("outType", 3);// 已缴费或在免费停车时间内
				}
				// 判断是否允许通过当前通道
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
				resultJSON.put("resCode", resCode);
				resultJSON.put("resMag", carCode + " success to get usercrdtm_in");
			} else {
				resCode = 2;
				resultJSON.put("resCode", resCode);
				resultJSON.put("resMag", carCode + " failed to get usercrdtm_in");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultJSON;
	}

	/**
	 * @Title: getUserCrdtmIn @Description: 车辆入场记录 @param parkNo 车场ID @param
	 * carCode 车牌号 @return @return Tc_usercrdtm_in @throws
	 */
	private Tc_usercrdtm_in getUserCrdtmIn(String parkNo, String carCode) {
		Tc_usercrdtm_in usercrdtmIn = null;
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("partitionID", getPartitionidin(parkNo));
			map.put("ParkingLotNo", parkNo);
			map.put("CarCode", carCode);
			usercrdtmIn = usercrdtm_inMapper.queryOneByCarCodeOrderByCrdtm(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return usercrdtmIn;
	}

	/**
	 * @Title: getChargeHistory @Description: 获取入场车辆已缴纳的费用记录 @param parkNo
	 * 车场ID @param carCode 车牌号 @param inTime 入场时间 @return JSONObject @throws
	 */
	private JSONObject getChargeHistory(String parkNo, String carCode, Date inTime) {
		JSONObject jsonResult = new JSONObject();
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
			chargeMap.put("AmountType", 0);
			chargeMap.put("InTime", inTime);
			chargeMap.put("OutTime", null);
			// 获取当前分区下的缴费记录集合
			List<Object> itemchargelist = chargerecordinfoMapper.queryByCondition(chargeMap);
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
					jsonObject.put("intime", info.getIntime().getTime());
					jsonObject.put("lastouttime", info.getLastouttime().getTime());
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
	 * @throws Exception
	 * 
	 * @Title: getCarInfo @Description: 获取车辆信息 @param parkNo 车场ID @param carCode
	 * 车牌号 @param inTime 入场时间 @param channelId 通道编号 @param paidValue
	 * 已缴费总金额 @return JSONObject @throws
	 */
	private JSONObject getCarInfo(String parkNo, String carCode, Date inTime, int channelId, float paidValue,
			Date outTime, int carintype) throws Exception {
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
			// 如果出入场时间与长期用户（白名单）有效期没有交集，则按照临停车算
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
		// 获取收费规则Id
		int ruleId = publicParkingService.getUserRuleId(userInfo, channelId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("carId", carCode);
		jsonObject.put("parkId", parkNo);
		jsonObject.put("mstart", userInfo == null ? "" : DateUtil.getPlusTime(userInfo.getBgndt()));
		jsonObject.put("freeParkTime", 0);
		jsonObject.put("start", DateUtil.getPlusTime(inTime));
		jsonObject.put("parkType", 0); // 0为普通车场,1为场中场
		jsonObject.put("ruleId", ruleId);
		jsonObject.put("mend", userInfo == null ? "" : DateUtil.getPlusTime(userInfo.getEnddt()));
		jsonObject.put("parkingType", userInfo == null ? 0 : 1);
		jsonObject.put("turnOverList", new JSONArray());
		jsonObject.put("mRuleId", ruleId);
		jsonObject.put("paidValue", paidValue);
		String currentDateStr = DateUtil.getPlusTime(outTime);
		// 计算长期用户剩余天数
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
	 * @Title: isAollowCrossChannel @Description: 判断是否允许车辆通过当前通道 @param
	 * channelCarType 当前车辆的车辆类型 @param ruleid 计费规则（计费包） @param outChannelid
	 * 出口通道id @param outTime 离场时间 @return boolean @throws
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
	 * 根据车场和计费包判断该车辆在当前出通道是否是长期车或者白名单 @param parkNo 车场ID @param ruleid
	 * 计费包id @param outChannelid 出通道 @param currentCarType 当前车辆类型 @return
	 * int @throws
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
	 * carin_lastoutTime 场内记录中最晚离场时间 @param chargeHistory 缴费历史记录 @return @return
	 * long @throws
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
