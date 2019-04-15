package cn.rf.hz.web.cloudpark.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
import com.reformer.cloudpark.service.Tc_userinfoService;

import cn.rf.hz.web.cloudpark.daoxx.Pb_parkingparmMapper;
import cn.rf.hz.web.cloudpark.daoxx.Pb_productMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_chargeruleMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_parkInformationMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_parkingareaMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_usercarinfoMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_userinfoMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_whiteuserinfoMapper;
import cn.rf.hz.web.cloudpark.moder.Pb_parkingparm;
import cn.rf.hz.web.cloudpark.moder.Pb_product;
import cn.rf.hz.web.cloudpark.moder.Pb_productKey;
import cn.rf.hz.web.cloudpark.moder.Tc_chargerule;
import cn.rf.hz.web.cloudpark.moder.Tc_chargeruleKey;
import cn.rf.hz.web.cloudpark.moder.Tc_parkingarea;
import cn.rf.hz.web.cloudpark.moder.Tc_usercarinfo;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm_in;
import cn.rf.hz.web.cloudpark.moder.Tc_userinfocarin;
//import cn.rf.hz.web.cloudpark.moder.Tc_userinfo;
import cn.rf.hz.web.cloudpark.moder.Tc_whiteuserinfo;
import cn.rf.hz.web.cloudpark.service.CarInfoForPadService;
import cn.rf.hz.web.cloudpark.service.SendUserInfoMess;
import cn.rf.hz.web.sharding.dao.Tc_usercrdtm_inMapper;
import cn.rf.hz.web.utils.BigDataAnalyze;
import cn.rf.hz.web.utils.DataConstants;
import cn.rf.hz.web.utils.DateUtil;
import cn.rf.hz.web.utils.JedisPoolUtils;
import cn.rf.hz.web.utils.ParkingLotConfigUtil;
import cn.rf.hz.web.utils.StringUtil;
import cn.rf.hz.web.utils.httputils.HttpClientUtil;

@Service("userinfoService")
public class Tc_userinfoServiceImp implements Tc_userinfoService {

	private final static Logger LOG = Logger.getLogger(Tc_userinfoServiceImp.class);
	@Autowired
	Tc_userinfoMapper userinfomapper;

	@Autowired
	Tc_whiteuserinfoMapper whiteusermapper;

	@Autowired(required = false)
	public SendUserInfoMess senduserinfomess;

	@Autowired(required = false)
	public Pb_parkingparmMapper parkingparmMapper;

	@Autowired(required = false)
	public Pb_productMapper productMapper;

	@Autowired(required = false)
	public Tc_chargeruleMapper chargeruleMapper;
	@Autowired
	private Tc_parkInformationMapper parkinginfoMapper;
	@Autowired
	private Tc_parkingareaMapper parkingareaMapper;
	@Autowired
	private Tc_usercarinfoMapper usercarinfoMapper;

	@Autowired
	private Tc_usercrdtm_inMapper usercrdtm_inMapper;

	@Autowired
	private Tc_userinfoMapper userinfoMapper;

	@Autowired
	private CarInfoForPadService padservice;

	@Override
	public Tc_userinfo selectBylicensePlateNumber(String carCode, String parkinglotno, Date datetime) {
		// TODO Auto-generated method stub
		Tc_userinfo userinfo = userinfomapper.selectBylicensePlateNumber(carCode, parkinglotno, datetime);
		return userinfo;
	}

	@Override
	public JSONArray selectByparkingNo(String requestBody) {
		JSONObject result = new JSONObject();
		JSONArray list = new JSONArray();
		try {

			JSONObject data = JSON.parseObject(requestBody);// 暂时未加密
			String parkinglotno = data.getString("ParkingLotNo");
			String currentAreaId = null;
			if (parkinglotno.indexOf('_') > -1) {
				currentAreaId = parkinglotno.split("_")[1];
				parkinglotno = parkinglotno.split("_")[0];
			}
			// 判断是否为场中场
			boolean isParkInPark = parkinginfoMapper.queryParkingTypeById(parkinglotno).equals("1") ? true : false;
			Tc_parkingarea ext_Area = new Tc_parkingarea();
			// 获取场中场外场区域信息
			if (isParkInPark) {
				ext_Area = parkingareaMapper.queryExternalAreaByParkingLotNo(parkinglotno);
			}
			// 获取长期用信息
			List<Tc_userinfo> userinfo = userinfomapper.selectByParkinglotno(parkinglotno);
			for (int i = 0; i < userinfo.size(); i++) {
				Tc_userinfo item_userinfo = userinfo.get(i);
				// 获取长期用户下的车牌信息
				List<Tc_usercarinfo> carslist = usercarinfoMapper.selectByUserID(item_userinfo.getRecordid());
				// 获取用户不为空的车牌
				List<String> newcarcode = new ArrayList<String>();
				for (Tc_usercarinfo item : carslist) {
					if (item.getCarcode() != null && !item.getCarcode().trim().isEmpty()) {
						newcarcode.add(item.getCarcode());
					}
				}
				// 遍历存储用户不为空的车牌
				for (int n = 0; n < newcarcode.size(); n++) {
					JSONObject model = new JSONObject();
					model.put("parkinglot", userinfo.get(i).getParkinglot());
					model.put("carcode", newcarcode.get(n).toString());
					model.put("chargeruleid", userinfo.get(i).getChargeruleid());
					// model.put("userpropertiy",
					// userinfo.get(i).getUserpropertiy());
					// model.put("bgndt",
					// time.format(userinfo.get(i).getBgndt()));
					// model.put("enddt",
					// time.format(userinfo.get(i).getEnddt()));
					model.put("userpropertiy", userinfo.get(i).getUserpropertiy());
					model.put("bgndt", userinfo.get(i).getBgndt());
					model.put("enddt", userinfo.get(i).getEnddt());
					model.put("username", userinfo.get(i).getUsername());
					LOG.error("userid set " + userinfo.get(i).getRecordid());
					model.put("userid", userinfo.get(i).getRecordid());
					model.put("type", 2);
					model.put("isfixed", userinfo.get(i).getIsfixed());

					// zhh0216
					String act = "0";
					if (userinfo.get(i).getParkinglot() < newcarcode.size()) {
						act = "1";
					}
					model.put("act", act);

					Pb_productKey p = new Pb_productKey();
					p.setParkinglotno(parkinglotno);
					p.setProductid(userinfo.get(i).getChargeruleid());
					List<String> listchannels = getchannelids(p);
					List<String> listareas = getareas(p);
					model.put("channelids", listchannels.toArray());
					model.put("areaids", listareas.toArray());
					LOG.info("=======================长期用户区域数组:" + listareas + "===================");
					LOG.info("=======================长期用户当前区域:" + currentAreaId + "===================");
					LOG.info("=======================长期用户外场区域:" + ext_Area.getAreaid() + "===================");
					// 场中场情况判断
					if (isParkInPark) {
						if (currentAreaId != null) {
							// 1)如果车场编号是：车场ID_区域ID，则读取包括区域ID的长期用户
							if (listareas.contains(currentAreaId)) {
								list.add(model);
							}
						} else {
							// 2)如果车场编号是：车场ID,则只读取包括外场区域的长期用户
							if (listareas.contains(ext_Area.getAreaid().toString())) {
								list.add(model);
							}
						}
					} else {
						// 普通场全部读取
						list.add(model);
					}
				}
			}

			// result.put("data", list.toJSONString());

			List<Tc_whiteuserinfo> whiteuserlist = whiteusermapper.selectByParkingNo(parkinglotno);
			for (int i = 0; i < whiteuserlist.size(); i++) {
				JSONObject model = new JSONObject();
				// model.put("parkinglot", 0);
				model.put("carcode", whiteuserlist.get(i).getLicenseplatenumber());
				model.put("chargeruleid", whiteuserlist.get(i).getUsecount());
				// model.put("userpropertiy", -1);
				// model.put("bgndt",
				// time.format(whiteuserlist.get(i).getStarttime()));
				// model.put("enddt",
				// time.format(whiteuserlist.get(i).getEndtine()));
				model.put("bgndt", whiteuserlist.get(i).getStarttime());
				model.put("enddt", whiteuserlist.get(i).getEndtine());
				model.put("type", whiteuserlist.get(i).getWhitetype());
				Pb_productKey p1 = new Pb_productKey();
				p1.setParkinglotno(parkinglotno);
				p1.setProductid(whiteuserlist.get(i).getUsecount());
				List<String> listchannels1 = getchannelids(p1);
				model.put("channelids", listchannels1.toArray());
				List<String> listareas = getareas(p1);
				model.put("areaids", listareas.toArray());
				LOG.info("=======================白名单区域数组:" + listareas + "===================");
				LOG.info("=======================白名单当前区域:" + currentAreaId + "===================");
				LOG.info("=======================白名单外场区域:" + ext_Area.getAreaid() + "===================");
				// 场中场情况判断
				if (isParkInPark) {
					if (currentAreaId != null) {
						// 1)如果车场编号是：车场ID_区域ID，则读取包括区域ID的长期用户
						if (listareas.contains(currentAreaId)) {
							list.add(model);
						}
					} else {
						// 2)如果车场编号是：车场ID,则只读取包括外场区域的长期用户
						if (listareas.contains(ext_Area.getAreaid().toString())) {
							list.add(model);
						}
					}
				} else {
					// 普通场全部读取
					list.add(model);
				}
			}

			// result.put("parkingid", parkinglotno);
			// result.put("topic", "cloudpark_usergroup");
			// result.put("msgtype", "in_k");
			// result.put("data", list);

			/*
			 * result.put("parkinglotno", parkinglotno); result.put("type",
			 * "k"); result.put("data", list); result1.put("userinfo", result);
			 */

		} catch (Exception e) {
			LOG.error("getUserInfo:An exception occurs for user information:" + e.toString());
		}
		LOG.info("=======================黑白名单" + list.toJSONString() + "===================");
		return list;
	}

	public List<String> getchannelids(Pb_productKey key) {
		LOG.info("keykey" + key);
		Pb_product pr = productMapper.selectByPrimaryKey(key);
		LOG.info("prpr" + pr);
		List<String> listchannels = new ArrayList<String>();
		if (pr != null) {
			String s = pr.getChannelids();
			LOG.info("getChannelids" + s);
			if (s != null && !s.equals("")) {
				String[] channels = s.split(",");
				for (int y = 0; y < channels.length; y++) {
					listchannels.add(channels[y]);
				}

			}
		}

		LOG.info("listchannels" + listchannels);
		return listchannels;

	}

	public List<String> getareas(Pb_productKey key) {
		LOG.info("keykey" + key);
		Pb_product pr = productMapper.selectByPrimaryKey(key);
		LOG.info("prpr" + pr);
		List<String> areaschannels = new ArrayList<String>();
		if (pr != null) {
			String s = pr.getAreaIds();
			LOG.info("getAreaIds" + s);
			if (s != null && !s.equals("")) {
				String[] areaIds = s.split(",");
				for (int y = 0; y < areaIds.length; y++) {
					areaschannels.add(areaIds[y]);
				}
			}
		}
		LOG.info("listchannels" + areaschannels);
		return areaschannels;
	}

	@Override
	public JSONObject uploadUserInfoForXb(String requestBody) {
		JSONObject result = new JSONObject();
		List<Tc_userinfo> list = setTcuserinfo(requestBody);
		try {
			for (int i = 0; i < list.size(); i++) {
				Tc_userinfo usermodel = userinfomapper.selectByUserNoAndParkingLotNo(list.get(i).getCarcode(),
						list.get(i).getCarcode1(), list.get(i).getCarcode2(), list.get(i).getParkinglotno());
				if (usermodel != null && usermodel.getRecordid() > 0) {
					int j = 0;
					if (DateUtil.compareDate(usermodel.getEnddt(), DateUtil.getCurrentDate())) {
						// 有效结束时间小于当前时间 已经过期 先删除 后新增
						// j =
						// userinfomapper.deleteByPrimaryKey(usermodel.getRecordid());
						// LOG.info("====================uploadUserInfoForXb:time
						// is expire,delete," + j
						// + "================");
						//
						// j = userinfomapper.insert(list.get(i));
						// LOG.info("====================uploadUserInfoForXb:time
						// is expire,insert," + j
						// + "================");
						usermodel.setBgndt(list.get(i).getBgndt());
						usermodel.setEnddt(list.get(i).getEnddt());
						j = userinfomapper.insert(usermodel);
						LOG.info("====================uploadUserInfoForXb:time is expire,insert," + j
								+ "================");

					} else {
						// list.get(i).setRecordid(usermodel.getRecordid());
						// list.get(i).setBgndt(usermodel.getBgndt());
						if (DateUtil.compareDate(list.get(i).getBgndt(), usermodel.getBgndt())) {
							usermodel.setBgndt(list.get(i).getBgndt());
						}
						if (DateUtil.compareDate(usermodel.getEnddt(), list.get(i).getEnddt())) {
							usermodel.setEnddt(list.get(i).getEnddt());
						}
						j = userinfomapper.updateByPrimaryKey(usermodel);
						LOG.info("====================uploadUserInfoForXb:updateindex," + i + ",result:" + j
								+ "================");
					}
					if (j > 0) {
						/*
						 * // 上传删除增量与上传新增增量 JSONObject delobject =
						 * generateIncrement(usermodel, "1");// 1删除 LOG.info(
						 * "====================uploadUserInfoForXb:delobject,"
						 * + delobject.toString() + "================");
						 * JSONObject delresult =
						 * senduserinfomess.addUserInfomation2(delobject.
						 * toJSONString()); LOG.info(
						 * "====================uploadUserInfoForXb:delresult,"
						 * + delresult.toString() + "================");
						 */
						JSONObject addobject = generateIncrement(list.get(i), "0");// 0新增
						LOG.info("====================uploadUserInfoForXb:addobject," + addobject.toString()
								+ "================");
						JSONObject addresult = senduserinfomess.addUserInfomation2(addobject.toJSONString());
						LOG.info("====================uploadUserInfoForXb:addresult," + addresult.toString()
								+ "================");
					}
				} else {
					List<String> liststr = new ArrayList<String>();
					if (list.get(i).getCarcode() != null && !list.get(i).getCarcode().isEmpty()) {
						liststr.add(list.get(i).getCarcode());
					}
					if (list.get(i).getCarcode1() != null && !list.get(i).getCarcode1().isEmpty()) {
						liststr.add(list.get(i).getCarcode1());
					}
					if (list.get(i).getCarcode2() != null && !list.get(i).getCarcode2().isEmpty()) {
						liststr.add(list.get(i).getCarcode2());
					}
					for (String carcode : liststr) {
						Tc_userinfo model = userinfomapper.selectByCarCodeAndParkingLotNo(carcode,
								list.get(i).getParkinglotno());
						if (model != null && model.getCarcode().equals(carcode)) {
							if ((model.getCarcode1() == null || model.getCarcode1().isEmpty())
									&& (model.getCarcode2() == null || model.getCarcode2().isEmpty())) {
								// 删除
								int j = userinfomapper.deleteByPrimaryKey(model.getRecordid());
								LOG.info("====================uploadUserInfoForXb:Add:Delete,result:" + j
										+ "================");
							} else {
								// 更新
								model.setCarcode("");
								int j = userinfomapper.updateByPrimaryKey(model);
								LOG.info("====================uploadUserInfoForXb:Add:Update,result:" + j
										+ "================");
							}
						}
						if (model != null && model.getCarcode1().equals(carcode)) {
							if ((model.getCarcode() == null || model.getCarcode().isEmpty())
									&& (model.getCarcode2() == null || model.getCarcode2().isEmpty())) {
								// 删除
								int j = userinfomapper.deleteByPrimaryKey(model.getRecordid());
								model = null;
								LOG.info("====================uploadUserInfoForXb:Add:Delete,result:" + j
										+ "================");
							} else {
								// 更新
								model.setCarcode1("");
								int j = userinfomapper.updateByPrimaryKey(model);
								LOG.info("====================uploadUserInfoForXb:Add:Update,result:" + j
										+ "================");
							}
						}
						if (model != null && model.getCarcode2().equals(carcode)) {
							if ((model.getCarcode() == null || model.getCarcode().isEmpty())
									&& (model.getCarcode1() == null || model.getCarcode1().isEmpty())) {
								// 删除
								int j = userinfomapper.deleteByPrimaryKey(model.getRecordid());
								LOG.info("====================uploadUserInfoForXb:Add:Delete,result:" + j
										+ "================");
							} else {
								// 更新
								model.setCarcode2("");
								int j = userinfomapper.updateByPrimaryKey(model);
								LOG.info("====================uploadUserInfoForXb:Add:Update,result:" + j
										+ "================");
							}
						}
					}
					int j = userinfomapper.insert(list.get(i));
					/*
					 * Tc_userinfo userinfomodel =
					 * userinfomapper.selectByUserNoAndParkingLotNo(list.get(i).
					 * getCarcode(), list.get(i).getCarcode1(),
					 * list.get(i).getCarcode2(),
					 * list.get(i).getParkinglotno());
					 */
					LOG.info("=======uploadUserInfoForXb:insertindex," + list.get(i).getRecordid() + "========");
					list.get(i).setRecordid(list.get(i).getRecordid());
					LOG.info("====================uploadUserInfoForXb:insertindex," + i + ",result:" + j
							+ "================");
					if (j > 0) {
						generateIncrement(list.get(i), "0");// 0新增
					}
				}
			}
			result.put("resCode", 0);
			result.put("resMsg", "Succeed to Save UserInfo");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("resCode", "1");
			result.put("resMsg", "Failed to Save UserInfo");
			LOG.info("uploadUserInfoForXb,exception occurred:" + e.getMessage().toString() + "================");
		}
		return result;
	}

	public List<Tc_userinfo> setTcuserinfo(String requestBody) {
		List<Tc_userinfo> list = new ArrayList<Tc_userinfo>();
		JSONObject data = JSON.parseObject(requestBody);
		JSONArray dataArrays = data.getJSONArray("data");
		for (int k = 0; k < dataArrays.size(); k++) {
			JSONObject object = dataArrays.getJSONObject(k);
			Tc_userinfo userinfo = new Tc_userinfo();
			if (object.containsKey("userNo")) {
				userinfo.setUserno(object.getString("userNo"));
				LOG.info("====================setUserNo" + object.getString("userNo") + "================");
			}
			if (object.containsKey("userName")) {
				userinfo.setUsername(object.getString("userName"));
				LOG.info("====================setUserName" + object.getString("userName") + "================");
			} else {
				userinfo.setUsername("");
			}
			if (object.containsKey("parkingLot")) {
				userinfo.setParkinglot(
						(object.getString("parkingLot") == null || object.getString("parkingLot").isEmpty()) ? 1
								: object.getInteger("parkingLot"));
				LOG.info("====================setParkingLot" + object.getString("parkingLot") + "================");
			} else {
				userinfo.setParkinglot(1);
			}
			if (object.containsKey("carCode")) {
				userinfo.setCarcode(object.getString("carCode"));
				if (object.getString("carCode") != null && !object.getString("carCode").isEmpty()) {

				}
				LOG.info("====================setCarcode:" + object.getString("carCode") + "================");
			}
			if (object.containsKey("carCode1")) {
				userinfo.setCarcode1(object.getString("carCode1"));
				LOG.info("====================setCarcode1:" + object.getString("carCode1") + "================");
			} else {
				userinfo.setCarcode1("");
			}
			if (object.containsKey("carCode2")) {
				userinfo.setCarcode2(object.getString("carCode2"));
				LOG.info("====================setCarcode2:" + object.getString("carCode2") + "================");
			} else {
				userinfo.setCarcode2("");
			}
			if (object.containsKey("chargeRuleID")) {
				String xbproductid = ParkingLotConfigUtil.GetParkingLotConfig("xbproductid");
				Pb_parkingparm parkingparm = parkingparmMapper.selectByPrimaryKey(object.getString("parkingLotNo"),
						Integer.valueOf(xbproductid));
				if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
					userinfo.setChargeruleid(Integer.parseInt(parkingparm.getParmvalue()));
				} else {
					userinfo.setChargeruleid(0);
				}
				// userinfo.setChargeruleid(object.getInteger("chargeRuleID"));
				LOG.info(
						"====================setchargeRuleID" + object.getInteger("chargeRuleID") + "================");
			}
			if (object.containsKey("userPropertiy")) {
				userinfo.setUserpropertiy(object.getInteger("userPropertiy"));
				LOG.info("====================setuserPropertiy:" + object.getInteger("userPropertiy")
						+ "================");
			}
			if (object.containsKey("bgndt")) {
				userinfo.setBgndt(DateUtil.StrToDate(object.getString("bgndt")));
				LOG.info("====================Setbgndt:" + object.getDate("bgndt") + "================");
			}

			if (object.containsKey("enddt")) {
				String enddt = DateUtil.getFormattedDate(object.getString("enddt")) + " 23:59:59";
				userinfo.setEnddt(DateUtil.StrToDate(enddt));
				LOG.info("====================setenddt:" + DateUtil.StrToDate(enddt) + "================");
			}

			if (object.containsKey("carLabel")) {
				userinfo.setCarlabel(object.getString("carLabel"));
				LOG.info("====================setcarlabel:" + object.getString("carLabel") + "================");
			} else {
				userinfo.setCarlabel("");
			}

			if (object.containsKey("carColor")) {
				userinfo.setCarcolor(object.getString("carColor"));
				LOG.info("====================setcarcolor:" + object.getString("carColor") + "================");
			} else {
				userinfo.setCarcolor("");
			}
			if (object.containsKey("carStyleid")) {
				userinfo.setCarstyleid(object.getInteger("carStyleid"));
				LOG.info("====================setcarstyleid:" + object.getInteger("carStyleid") + "================");
			}

			if (object.containsKey("userTel")) {
				userinfo.setUsertel(object.getString("userTel"));
				LOG.info("====================setusertel:" + object.getString("userTel") + "================");
			}

			if (object.containsKey("userAddress")) {
				userinfo.setUseraddress(object.getString("userAddress"));
				LOG.info("====================setuserAddress:" + object.getString("userAddress") + "================");
			}

			if (object.containsKey("userMemo")) {
				userinfo.setUsermemo(object.getString("userMemo"));
				LOG.info("====================setuserMemo:" + object.getString("userMemo") + "================");
			}

			if (object.containsKey("createPeople")) {
				userinfo.setCreatepeople(object.getInteger("createPeople"));
				LOG.info("====================setcreatePeople:" + object.getInteger("createPeople")
						+ "================");
			}

			if (object.containsKey("createDate")) {
				userinfo.setCreatedate(object.getDate("createDate"));
				LOG.info("====================setcreateDate:" + object.getDate("createDate") + "================");
			} else {
				userinfo.setCreatedate(DateUtil.getCurrentDate());
			}

			if (object.containsKey("updatePeople")) {
				userinfo.setUpdatepeople(object.getInteger("updatePeople"));
				LOG.info("====================setupdatePeople:" + object.getInteger("updatePeople")
						+ "================");
			}

			if (object.containsKey("updateDate")) {
				userinfo.setUpdatedate(object.getDate("updateDate"));
				LOG.info("====================setupdateDate:" + object.getDate("updateDate") + "================");
			} else {
				userinfo.setUpdatedate(DateUtil.getCurrentDate());
			}
			if (object.containsKey("carCode3")) {
				userinfo.setCarcode3(object.getString("carCode3"));
				LOG.info("====================setcarCode3:" + object.getString("carCode3") + "================");
			} else {
				userinfo.setCarcode3("");
			}
			if (object.containsKey("carCode4")) {
				userinfo.setCarcode4(object.getString("carCode4"));
				LOG.info("====================setcarCode4:" + object.getString("carCode4") + "================");
			} else {
				userinfo.setCarcode4("");
			}
			if (object.containsKey("isUpload")) {
				userinfo.setIsupload(object.getBoolean("isUpload"));
				LOG.info("====================setisUpload:" + object.getBoolean("isUpload") + "================");
			}

			if (object.containsKey("isWhiteList")) {
				userinfo.setIswhitelist(object.getInteger("isWhiteList"));
				LOG.info("====================setisWhiteList:" + object.getInteger("isWhiteList") + "================");
			}

			if (object.containsKey("balance")) {
				userinfo.setBalance(object.getBigDecimal("balance"));
				LOG.info("====================setbalance:" + object.getBigDecimal("balance") + "================");
			}

			if (object.containsKey("parkingLotNo")) {
				userinfo.setParkinglotno(object.getString("parkingLotNo"));
				LOG.info(
						"====================setparkingLotNo:" + object.getString("parkingLotNo") + "================");
			}
			if (object.containsKey("isFixed")) {
				userinfo.setIsfixed(object.getInteger("isFixed"));
				LOG.info(
						"====================setparkingLotNo:" + object.getString("parkingLotNo") + "================");
			} else {
				userinfo.setIsfixed(0);
			}

			list.add(userinfo);
		}

		return list;
	}

	// 生成增量包
	public JSONObject generateIncrement(Tc_userinfo usermodel, String type) {
		JSONObject addresult = new JSONObject();

		String[] arrCarcode = { usermodel.getCarcode(), usermodel.getCarcode1(), usermodel.getCarcode2(),
				usermodel.getCarcode3(), usermodel.getCarcode4() };
		List<String> listCarcode = new ArrayList<String>();
		for (int m = 0; m < arrCarcode.length; m++) {
			if (!listCarcode.contains(arrCarcode[m]) && arrCarcode[m] != null && !arrCarcode[m].equals("")) {
				listCarcode.add(arrCarcode[m]);
			}
		}

		for (String carCode : listCarcode) {
			JSONArray array = new JSONArray();
			JSONObject object = new JSONObject();
			object.put("topic", "cloudpark_usergroup");
			if (type == "1") {
				object.put("msgtype", "out_p");
			} else if (type == "0") {
				object.put("msgtype", "in_p");
			}
			object.put("parkingid", usermodel.getParkinglotno());
			JSONObject carobject = new JSONObject();
			carobject.put("bgndt", usermodel.getBgndt());
			carobject.put("enddt", DateUtil.getDateFromString(DateUtil.formatDate(usermodel.getEnddt()) + " 23:59:59"));
			carobject.put("userpropertiy", usermodel.getUserpropertiy());
			String xbproductid = ParkingLotConfigUtil.GetParkingLotConfig("xbproductid");
			Pb_parkingparm parkingparm = parkingparmMapper.selectByPrimaryKey(usermodel.getParkinglotno(),
					Integer.valueOf(xbproductid));
			if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
				carobject.put("chargeruleid", Integer.parseInt(parkingparm.getParmvalue()));
			} else {
				carobject.put("chargeruleid", 0);
			}
			carobject.put("carcode", carCode);
			carobject.put("username", usermodel.getUsername());
			carobject.put("type", "2");
			carobject.put("userid", usermodel.getRecordid());
			carobject.put("isfixed", "0");
			carobject.put("carcodes", listCarcode.toArray());
			carobject.put("parkinglot", usermodel.getParkinglot());
			if (type == "1") {
				carobject.put("act", "0");// act 1新增 0删除
			} else if (type == "0") {
				carobject.put("act", "1");// act 1新增 0删除
			}
			array.add(carobject);
			object.put("data", array);
			LOG.info("====================uploadUserInfoForXb:object," + object.toString() + "================");
			addresult = senduserinfomess.addUserInfomation2(object.toJSONString());
			LOG.info("====================uploadUserInfoForXb:addobject," + addresult.toString() + "================");
		}

		return addresult;
	}

	@Override
	public JSONObject getProductInfo(String requestBody) {
		JSONObject result = new JSONObject();
		try {
			if (requestBody == null || requestBody.isEmpty()) {
				result.put("respone_code", "1");
				result.put("respone_msg", "get productinfo failed,the requestBody is null");
			} else {
				JSONObject object = JSON.parseObject(requestBody);
				String parkinglotno = object.getString("car_park_id");
				String productid = object.getString("charge_product_id");
				String request_datetime = object.getString("request_datetime");
				if (parkinglotno != null && !parkinglotno.isEmpty() && productid != null && !productid.isEmpty()) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("parkid", parkinglotno);
					map.put("productid", productid);
					Pb_product productmodel = productMapper.SelectProductByid(map);
					if (productmodel != null && productmodel.getProductid() > 0) {
						Tc_chargeruleKey ruleKey = new Tc_chargeruleKey();
						ruleKey.setChargeruleid(productmodel.getLongruleid());
						ruleKey.setParkinglotno(parkinglotno);
						Tc_chargerule rulemodel = chargeruleMapper.selectByPrimaryKey(ruleKey);
						if (rulemodel != null && rulemodel.getChargeruleid() > 0) {

							int days = 0;
							int money = 0;
							// 月租用户
							if (rulemodel.getChargeno() == 10003) {
								days = Integer.valueOf(rulemodel.getAllfilesname().split("\\|")[1].split(",")[1]);

								double f = Double.parseDouble(rulemodel.getAllfilesname().split("\\|")[1].split(",")[0])
										* 100;
								money = (int) f;
							} else if (rulemodel.getChargeno() == 10005)// 一次性缴费用户
							{
								days = Integer.valueOf(rulemodel.getAllfilesname().split("\\|")[1].split(",")[1]) * 365;

								double f = Double.parseDouble(rulemodel.getAllfilesname().split("\\|")[1].split(",")[0])
										* 100;
								money = (int) f;
							}
							result.put("respone_code", "0");
							result.put("respone_msg", "get productinfo succeed");
							result.put("car_park_id", parkinglotno);
							result.put("charge_product_id", productmodel.getProductid().toString());
							result.put("charge_product_name", productmodel.getProductname());
							result.put("rent_days", String.valueOf(days));
							result.put("rent_amount", String.valueOf(money));
							result.put("rent_charge_rule_id", String.valueOf(rulemodel.getChargeruleid()));
							result.put("rent_charge_rule_name", String.valueOf(rulemodel.getChargerulename()));
							result.put("request_datetime", request_datetime);
							result.put("respone_datetime", DateUtil.getCurrDateTime());
							LOG.info("getProductInfo,result:" + result.toJSONString());
						} else {
							result.put("respone_code", "1");
							result.put("respone_msg", "get productinfo failed,the chargerule is null");
						}
					} else {
						result.put("respone_code", "1");
						result.put("respone_msg", "get productinfo failed,the product is null");
					}

				} else {
					result.put("respone_code", "1");
					result.put("respone_msg", "get productinfo failed,the parameter is null");
				}
			}
		} catch (Exception e) {
			result.put("respone_code", "1");
			result.put("respone_msg", "get productinfo failed,exception occurred");
			LOG.info("getProductInfo,exception occurred:" + e.toString());
		}
		return result;
	}

	@Override
	public JSONObject syncUserInfoForApp(String requestBody) {
		JSONObject result = new JSONObject();
		Tc_userinfo model = setTcuserinfo1(requestBody);
		try {
			if (!model.getDatasource().isEmpty() && model.getDatasource() != null && !model.getSourceid().isEmpty()
					&& model.getSourceid() != null) {
				Tc_userinfo usermodel = userinfomapper.selectByDataSourceAndParkingLotNo(model.getParkinglotno(),
						model.getDatasource(), model.getSourceid());
				if (usermodel != null && usermodel.getRecordid() > 0) {
					if (DateUtil.compareDate(model.getBgndt(), usermodel.getBgndt())) {
						usermodel.setBgndt(model.getBgndt());
					}
					if (DateUtil.compareDate(usermodel.getEnddt(), model.getEnddt())) {
						usermodel.setEnddt(model.getEnddt());
					}
					userinfomapper.updateByPrimaryKey(usermodel);
				} else {
					userinfomapper.insert(model);
				}
			} else {
				Tc_userinfo usermodel = userinfomapper.selectByUserNoAndParkingLotNo(model.getCarcode(),
						model.getCarcode1(), model.getCarcode2(), model.getParkinglotno());
				if (usermodel != null && usermodel.getRecordid() > 0) {
					int j = 0;
					if (DateUtil.compareDate(usermodel.getEnddt(), DateUtil.getCurrentDate())) {
						usermodel.setBgndt(model.getBgndt());
						usermodel.setEnddt(model.getEnddt());
						j = userinfomapper.insert(usermodel);
						LOG.info("====================asycUserInfoForApp:time is expire,insert," + j
								+ "================");

					} else {
						if (DateUtil.compareDate(model.getBgndt(), usermodel.getBgndt())) {
							usermodel.setBgndt(model.getBgndt());
						}
						if (DateUtil.compareDate(usermodel.getEnddt(), model.getEnddt())) {
							usermodel.setEnddt(model.getEnddt());
						}
						j = userinfomapper.updateByPrimaryKey(usermodel);
						LOG.info("====================asycUserInfoForApp:result:" + j + "================");
					}
					if (j > 0) {
						JSONObject addobject = generateIncrement(model, "0");// 0新增
						LOG.info("====================asycUserInfoForApp:addobject," + addobject.toString()
								+ "================");
						JSONObject addresult = senduserinfomess.addUserInfomation2(addobject.toJSONString());
						LOG.info("====================asycUserInfoForApp:addresult," + addresult.toString()
								+ "================");
					}
				} else {
					List<String> liststr = new ArrayList<String>();
					if (model.getCarcode() != null && !model.getCarcode().isEmpty()) {
						liststr.add(model.getCarcode());
					}
					if (model.getCarcode1() != null && !model.getCarcode1().isEmpty()) {
						liststr.add(model.getCarcode1());
					}
					if (model.getCarcode2() != null && !model.getCarcode2().isEmpty()) {
						liststr.add(model.getCarcode2());
					}
					for (String carcode : liststr) {
						Tc_userinfo userinfomodel = userinfomapper.selectByCarCodeAndParkingLotNo(carcode,
								model.getParkinglotno());
						if (userinfomodel != null && userinfomodel.getCarcode().equals(carcode)) {
							if ((userinfomodel.getCarcode1() == null || userinfomodel.getCarcode1().isEmpty())
									&& (userinfomodel.getCarcode2() == null || userinfomodel.getCarcode2().isEmpty())) {
								// 删除
								int j = userinfomapper.deleteByPrimaryKey(model.getRecordid());
								LOG.info("====================asycUserInfoForApp:Add:Delete,result:" + j
										+ "================");
							} else {
								// 更新
								model.setCarcode("");
								int j = userinfomapper.updateByPrimaryKey(model);
								LOG.info("====================asycUserInfoForApp:Add:Update,result:" + j
										+ "================");
							}
						}
						if (userinfomodel != null && userinfomodel.getCarcode1().equals(carcode)) {
							if ((userinfomodel.getCarcode() == null || userinfomodel.getCarcode().isEmpty())
									&& (userinfomodel.getCarcode2() == null || userinfomodel.getCarcode2().isEmpty())) {
								// 删除
								int j = userinfomapper.deleteByPrimaryKey(userinfomodel.getRecordid());
								userinfomodel = null;
								LOG.info("====================asycUserInfoForApp:Add:Delete,result:" + j
										+ "================");
							} else {
								// 更新
								userinfomodel.setCarcode1("");
								int j = userinfomapper.updateByPrimaryKey(userinfomodel);
								LOG.info("====================asycUserInfoForApp:Add:Update,result:" + j
										+ "================");
							}
						}
						if (userinfomodel != null && userinfomodel.getCarcode2().equals(carcode)) {
							if ((userinfomodel.getCarcode() == null || userinfomodel.getCarcode().isEmpty())
									&& (userinfomodel.getCarcode1() == null || userinfomodel.getCarcode1().isEmpty())) {
								// 删除
								int j = userinfomapper.deleteByPrimaryKey(userinfomodel.getRecordid());
								LOG.info("====================asycUserInfoForApp:Add:Delete,result:" + j
										+ "================");
							} else {
								// 更新
								userinfomodel.setCarcode2("");
								int j = userinfomapper.updateByPrimaryKey(userinfomodel);
								LOG.info("====================asycUserInfoForApp:Add:Update,result:" + j
										+ "================");
							}
						}
					}
					int j = userinfomapper.insert(model);

					LOG.info("=======asycUserInfoForApp:insertindex," + model.getRecordid() + "========");
					model.setRecordid(model.getRecordid());
					LOG.info("====================asycUserInfoForApp:result:" + j + "================");
					if (j > 0) {
						generateIncrement(model, "0");// 0新增
					}
				}
				result.put("respone_code", "0");
				result.put("respone_msg", "Succeed to Asyc UserInfo");
				JSONObject object = JSON.parseObject(requestBody);
				result.put("car_park_id", object.getString("car_park_id"));
				result.put("user_id", object.getString("user_id"));
				result.put("user_name", object.getString("user_name"));
				result.put("license_plate_number", object.getString("license_plate_number"));
				result.put("request_datetime", object.getString("request_datetime"));
				result.put("respone_datetime", DateUtil.getCurrDateTime());
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("respone_code", "1");
			result.put("respone_msg", "Failed to Asyc UserInfo");
			LOG.info("asycUserInfoForApp,exception occurred:" + e.getMessage().toString() + "================");
		}
		return result;
	}

	public Tc_userinfo setTcuserinfo1(String requestBody) {
		JSONObject object = JSON.parseObject(requestBody);
		Tc_userinfo userinfo = new Tc_userinfo();
		if (object.containsKey("user_id")) {
			userinfo.setUserno(object.getString("user_id"));
			LOG.info("====================setuser_id" + object.getString("user_id") + "================");
		}
		if (object.containsKey("user_name")) {
			userinfo.setUsername(object.getString("user_name"));
			LOG.info("====================setuser_name" + object.getString("user_name") + "================");
		} else {
			userinfo.setUsername("");
		}
		if (object.containsKey("parking_space_number")) {
			userinfo.setParkinglot((object.getString("parking_space_number") == null
					|| object.getString("parking_space_number").isEmpty()) ? 1
							: object.getInteger("parking_space_number"));
			LOG.info("====================setparking_space_number" + object.getString("parking_space_number")
					+ "================");
		} else {
			userinfo.setParkinglot(1);
		}

		if (object.containsKey("license_plate_number")) {
			String[] strCarCodes = object.getString("license_plate_number").split(",");
			if (strCarCodes.length == 1) {
				userinfo.setCarcode(strCarCodes[0]);
				userinfo.setCarcode1("");
				userinfo.setCarcode2("");
				userinfo.setCarcode3("");
				userinfo.setCarcode4("");
			} else if (strCarCodes.length == 2) {
				userinfo.setCarcode(strCarCodes[0]);
				userinfo.setCarcode1(strCarCodes[1]);
				userinfo.setCarcode2("");
				userinfo.setCarcode3("");
				userinfo.setCarcode4("");
			} else if (strCarCodes.length == 3) {
				userinfo.setCarcode(strCarCodes[0]);
				userinfo.setCarcode1(strCarCodes[1]);
				userinfo.setCarcode2(strCarCodes[2]);
				userinfo.setCarcode3("");
				userinfo.setCarcode4("");
			} else if (strCarCodes.length == 4) {
				userinfo.setCarcode(strCarCodes[0]);
				userinfo.setCarcode1(strCarCodes[1]);
				userinfo.setCarcode2(strCarCodes[2]);
				userinfo.setCarcode3(strCarCodes[3]);
				userinfo.setCarcode4("");
			} else if (strCarCodes.length == 5) {
				userinfo.setCarcode(strCarCodes[0]);
				userinfo.setCarcode1(strCarCodes[1]);
				userinfo.setCarcode2(strCarCodes[2]);
				userinfo.setCarcode3(strCarCodes[3]);
				userinfo.setCarcode4(strCarCodes[4]);
			}
			LOG.info("====================setlicense_plate_number" + object.getString("license_plate_number")
					+ "================");
		}
		if (object.containsKey("charge_product_id")) {
			String ruleid = object.getString("charge_product_id");
			if (ruleid != null && !ruleid.isEmpty()) {
				userinfo.setChargeruleid(Integer.parseInt(ruleid));
			} else {
				String xbproductid = ParkingLotConfigUtil.GetParkingLotConfig("xbproductid");
				Pb_parkingparm parkingparm = parkingparmMapper.selectByPrimaryKey(object.getString("car_park_id"),
						Integer.valueOf(xbproductid));
				if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
					userinfo.setChargeruleid(Integer.parseInt(parkingparm.getParmvalue()));
				} else {
					userinfo.setChargeruleid(0);
				}
			}
			// userinfo.setChargeruleid(object.getInteger("chargeRuleID"));

		} else {
			String xbproductid = ParkingLotConfigUtil.GetParkingLotConfig("xbproductid");
			Pb_parkingparm parkingparm = parkingparmMapper.selectByPrimaryKey(object.getString("car_park_id"),
					Integer.valueOf(xbproductid));
			if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
				userinfo.setChargeruleid(Integer.parseInt(parkingparm.getParmvalue()));
			} else {
				userinfo.setChargeruleid(0);
			}
		}
		LOG.info("====================setcharge_product_id" + object.getInteger("charge_product_id")
				+ "================");
		if (object.containsKey("lease_start_datetime")) {
			String bgndt = DateUtil.getFormattedDate(object.getString("lease_start_datetime")) + " 00:00:00";
			userinfo.setBgndt(DateUtil.StrToDate(bgndt));
			LOG.info("====================Setlease_start_datetime:" + object.getDate("lease_start_datetime")
					+ "================");
		}

		if (object.containsKey("lease_end_datetime")) {
			String enddt = DateUtil.getFormattedDate(object.getString("lease_end_datetime")) + " 23:59:59";
			userinfo.setEnddt(DateUtil.StrToDate(enddt));
			LOG.info("====================setlease_end_datetime:" + DateUtil.StrToDate(enddt) + "================");
		}

		if (object.containsKey("createDate")) {
			userinfo.setCreatedate(object.getDate("createDate"));
			LOG.info("====================setcreateDate:" + object.getDate("createDate") + "================");
		} else {
			userinfo.setCreatedate(DateUtil.getCurrentDate());
		}

		if (object.containsKey("updateDate")) {
			userinfo.setUpdatedate(object.getDate("updateDate"));
			LOG.info("====================setupdateDate:" + object.getDate("updateDate") + "================");
		} else {
			userinfo.setUpdatedate(DateUtil.getCurrentDate());
		}

		if (object.containsKey("car_park_id")) {
			userinfo.setParkinglotno(object.getString("car_park_id"));
			LOG.info("====================setcar_park_id:" + object.getString("car_park_id") + "================");
		}
		if (object.containsKey("data_source")) {
			userinfo.setDatasource(object.getString("data_source"));
			LOG.info("====================setdata_source:" + object.getString("data_source") + "================");
		}
		if (object.containsKey("source_id")) {
			userinfo.setSourceid(object.getString("source_id"));
			LOG.info("====================setsource_id:" + object.getString("source_id") + "================");
		}

		userinfo.setIsfixed(0);
		userinfo.setUserpropertiy(0);
		userinfo.setCarstyleid(0);
		userinfo.setCreatepeople(-100);
		userinfo.setUpdatepeople(-100);
		return userinfo;
	}

	@Override
	public JSONObject getUserInfoCarIn(String requestBody) {
		JSONObject result = new JSONObject();
		try {
			JSONArray array = new JSONArray();
			JSONObject data = JSON.parseObject(requestBody);
			String ParkingLotNo = data.getString("ParkingLotNo");
			String UserInfoId = data.getString("UserInfoId");
			String begin = data.getString("begin");
			Integer page = 1;
			page = data.getInteger("page");
			Integer rows = data.getInteger("rows");
			Integer offset = (page - 1) * rows;
			Integer limit = page * rows;
			BigDecimal cost = new BigDecimal(0);
			BigDecimal paid = new BigDecimal(0);

			Tc_userinfo userinfo = userinfoMapper.selectByPrimaryKey(Integer.valueOf(UserInfoId));
			List<Tc_usercarinfo> usercarlist = usercarinfoMapper.selectByUserID(Integer.valueOf(UserInfoId));
			LOG.info("====================getUserInfoCarIn,usercarlist.size():" + usercarlist.size()
					+ "================");
			if (usercarlist != null && usercarlist.size() > 0) {
				String key = BigDataAnalyze.geListKeyByDataType(ParkingLotNo, DataConstants.CLOUDPARK_INOUT, "k");
				LOG.info("====================getUserInfoCarIn,key:" + key + "================");
				for (Tc_usercarinfo model : usercarlist) {
					String longUserStr = JedisPoolUtils.hget(key, model.getCarcode());
					LOG.info("====================getUserInfoCarIn,longUserStr:" + longUserStr + "================");
					if (longUserStr != null && !longUserStr.isEmpty()) {
						JSONArray userArray = JSONArray.parseArray(longUserStr);
						JSONObject object = new JSONObject();
						object.put("carcode", userArray.getJSONObject(0).getString("carcode"));
						object.put("intime", DateUtil.getLongToDateStr(userArray.getJSONObject(0).getLong("intime")));

						object.put("carintype", userArray.getJSONObject(0).getString("carintype"));
						object.put("channelid", userArray.getJSONObject(0).getString("channelid"));
						object.put("areaid", userArray.getJSONObject(0).getString("areaId"));
						object.put("chargeruleid", userArray.getJSONObject(0).getString("chargeruleid"));
						object.put("parkinglotno", ParkingLotNo);
						object.put("username", userArray.getJSONObject(0).getString("username"));
						object.put("partitionid", getPartitionid(ParkingLotNo));

						String start = DateUtil.getLongToDateStr(userArray.getJSONObject(0).getLong("intime"));
						String end = DateUtil.getCurrDate() + " 00:00:00";
						if (begin != null && !begin.isEmpty()) {
							end = begin + " 00:00:00";
						}
						String strNowDay = DateUtil.getCurrDate() + " 00:00:00";
						String strNowTime = DateUtil.getNowPlusTime();
						String strIntime = start;
						String strInDate = DateUtil.getFormattedDate(start) + " 00:00:00";// yyyy-MM-dd

						Date dtNowTime = DateUtil.getDateFromString(strNowTime);
						Date dtNowDay = DateUtil.getDateFromString(strNowDay);
						Date dtend = DateUtil.getDateFromString(end);
						Date dtIntime = DateUtil.getDateFromString(strIntime);
						Date dtInDate = DateUtil.getDateFromString(strInDate);

						Boolean isValid = true;
						if (DateUtil.compareDate(userinfo.getEnddt(), dtNowTime)) {// 有效期外续费
							LOG.info("====================getUserInfoCarIn,Money,111111111:" + strInDate + "," + end
									+ "================");
							if (!strInDate.equals(end))// 判断进场时间与续费开始时间是否相等
							{
								LOG.info("====================getUserInfoCarIn,Money,2222222222:================");
								if (DateUtil.compareDate(dtend, dtIntime))// 续费开始时间小于进场时间
								{
									LOG.info(
											"====================getUserInfoCarIn,Money,2222223333333:================");
									isValid = false;
								} else {
									if (!strNowDay.equals(end)) {
										LOG.info(
												"====================getUserInfoCarIn,Money,3333333333:================");
										if (DateUtil.compareDate(dtNowTime, dtend)) {
											isValid = false;
											LOG.info(
													"====================getUserInfoCarIn,Money,3333334444444:================");
										} else {
											LOG.info(
													"====================getUserInfoCarIn,Money,444444444444:================");
										}
									} else {
										LOG.info(
												"====================getUserInfoCarIn,Money,444444444455555:================");
									}
								}
							} else {
								LOG.info("====================getUserInfoCarIn,Money,5555555:================");
								end = strIntime;
								if (strNowDay.equals(end)) {
									end = strNowTime;
								}
							}
						} else {
							isValid = false;
						}

						LOG.info("====================getUserInfoCarIn,Money,end:" + end + "================");
						if (isValid) {
							JSONObject paidparams = new JSONObject();
							paidparams.put("parkNo", ParkingLotNo);
							paidparams.put("carCode", userArray.getJSONObject(0).getString("carcode"));
							paidparams.put("outTime", end);
							// paidparams.put("channelId", 0);
							LOG.info("====================getUserInfoCarIn,Money,Paid,params"
									+ paidparams.toJSONString() + "================");
							JSONObject paidobject = padservice.getCarInfoForPad(paidparams.toJSONString());
							LOG.info("====================getUserInfoCarIn,Money,Paid:" + paidobject
									+ "================");
							if (paidobject != null && paidobject.getString("resCode").equals("0")) {
								JSONArray paidarray = paidobject.getJSONArray("chargeHistory");
								if (paidarray != null && paidarray.size() > 0) {
									for (int i = 0; i < paidarray.size(); i++) {
										BigDecimal x2 = new BigDecimal(
												paidarray.getJSONObject(i).getFloat("realchargeamount"));
										paid = paid.add(x2);
									}
									paid = paid.divide(new BigDecimal(1), 2, BigDecimal.ROUND_HALF_UP);// 四舍五入保留两位小数
								}
							}

							JSONObject param = new JSONObject();
							param.put("parkId", ParkingLotNo);
							JSONObject carProp = new JSONObject();
							carProp.put("start", start);
							carProp.put("end", end);
							carProp.put("carId", userArray.getJSONObject(0).getString("carcode"));
							carProp.put("ruleId", userArray.getJSONObject(0).getString("chargeruleid"));
							carProp.put("areaId", userArray.getJSONObject(0).getString("areaId"));
							carProp.put("turnOverList", new JSONArray());
							carProp.put("paidValue", paid);
							carProp.put("mstart", DateUtil.getPlusTime(userinfo.getBgndt()));
							carProp.put("mend", DateUtil.getPlusTime(userinfo.getEnddt()));
							param.put("carProp", carProp);

							LOG.info("====================getUserInfoCarIn,Money,params:" + param + "================");
							JSONObject Cost = HttpClientUtil
									.doPost(ParkingLotConfigUtil.GetParkingLotConfig("cloudchargeurl"), param);
							LOG.info("====================getUserInfoCarIn,Money,Cost:" + Cost + "================");

							if (Cost.getString("returnCode").equals("0000")) {
								BigDecimal x1 = new BigDecimal(Cost.getJSONObject("result").getFloat("realCost") / 100);
								x1 = x1.divide(new BigDecimal(1), 2, BigDecimal.ROUND_HALF_UP);
								cost = cost.add(x1);
								object.put("money", x1);
							} else {
								object.put("money", 0);
							}
							object.put("outtime", end);
						} else {
							object.put("outtime", "-");
							object.put("money", 0);
						}
						array.add(object);
					} else {
						Map<String, Object> mapper = new HashMap<String, Object>();
						mapper.put("PartitionID", getPartitionidin(ParkingLotNo));
						mapper.put("ParkingLotNo", ParkingLotNo);
						mapper.put("CarCode", model.getCarcode());
						List<Object> usercrdtminlist = usercrdtm_inMapper.queryByCondition(mapper);
						LOG.info("====================getUserInfoCarIn1,usercrdtminlist:" + usercrdtminlist.size()
								+ "================");
						if (usercrdtminlist != null && usercrdtminlist.size() > 0) {
							Tc_usercrdtm_in usercrdtmin = (Tc_usercrdtm_in) usercrdtminlist.get(0);
							JSONObject object = new JSONObject();
							object.put("carcode", usercrdtmin.getCarcode());
							object.put("intime", DateUtil.getPlusTime(usercrdtmin.getCrdtm()));
							object.put("carintype", usercrdtmin.getCarintype());
							object.put("channelid", usercrdtmin.getChannelid());
							object.put("areaid", usercrdtmin.getAreaId());
							object.put("chargeruleid", usercrdtmin.getChargeruleid());
							object.put("parkinglotno", ParkingLotNo);
							object.put("username", usercrdtmin.getUsername());
							object.put("partitionid", getPartitionid(ParkingLotNo));

							String start = DateUtil.getPlusTime(usercrdtmin.getCrdtm());
							String end = DateUtil.getCurrDate() + " 00:00:00";
							if (begin != null && !begin.isEmpty()) {
								end = begin + " 00:00:00";
							}
							String strNowDay = DateUtil.getCurrDate() + " 00:00:00";
							String strNowTime = DateUtil.getNowPlusTime();
							String strIntime = start;
							String strInDate = DateUtil.getFormattedDate(strIntime) + " 00:00:00";// yyyy-MM-dd

							Date dtNowTime = DateUtil.getDateFromString(strNowTime);
							Date dtNowDay = DateUtil.getDateFromString(strNowDay);
							Date dtend = DateUtil.getDateFromString(end);
							Date dtIntime = DateUtil.getDateFromString(strIntime);
							Date dtInDate = DateUtil.getDateFromString(strInDate);

							Boolean isValid = true;
							if (DateUtil.compareDate(userinfo.getEnddt(), dtNowTime)) {// 有效期外续费
								if (!strInDate.equals(end))// 判断进场时间与续费开始时间是否相等
								{
									if (DateUtil.compareDate(dtend, dtIntime))// 续费开始时间小于进场时间
									{
										isValid = false;
									} else {
										if (!strNowDay.equals(end)) {
											if (DateUtil.compareDate(dtNowTime, dtend)) {
												isValid = false;
											} else {
											}
										} else {

										}
									}
								} else {
									end = strIntime;
									if (strNowDay.equals(end)) {
										end = strNowTime;
									}
								}
							} else {
								isValid = false;
							}
							LOG.info("====================getUserInfoCarIn1,Money,end:" + end + "================");

							if (isValid) {
								JSONObject paidparams = new JSONObject();
								paidparams.put("parkNo", ParkingLotNo);
								paidparams.put("carCode", usercrdtmin.getCarcode());
								paidparams.put("outTime", end);
								// paidparams.put("channelId", 0);
								LOG.info("====================getUserInfoCarIn1,Money,Paid,params"
										+ paidparams.toJSONString() + "================");
								JSONObject paidobject = padservice.getCarInfoForPad(paidparams.toJSONString());
								LOG.info("====================getUserInfoCarIn1,Money,Paid:" + paidobject
										+ "================");
								if (paidobject != null && paidobject.getString("resCode").equals("0")) {
									JSONArray paidarray = paidobject.getJSONArray("chargeHistory");
									if (paidarray != null && paidarray.size() > 0) {
										for (int i = 0; i < paidarray.size(); i++) {
											BigDecimal x2 = new BigDecimal(
													paidarray.getJSONObject(i).getFloat("realchargeamount"));
											paid = paid.add(x2);
										}
										paid = paid.divide(new BigDecimal(1), 2, BigDecimal.ROUND_HALF_UP);// 四舍五入保留两位小数
									}
								}

								JSONObject param = new JSONObject();
								param.put("parkId", ParkingLotNo);
								JSONObject carProp = new JSONObject();
								carProp.put("start", start);
								carProp.put("end", end);
								carProp.put("carId", usercrdtmin.getCarcode());
								carProp.put("ruleId", usercrdtmin.getChargeruleid());
								carProp.put("areaId", usercrdtmin.getAreaId());
								carProp.put("turnOverList", new JSONArray());
								carProp.put("paidValue", paid);
								carProp.put("mstart", DateUtil.getPlusTime(userinfo.getBgndt()));
								carProp.put("mend", DateUtil.getPlusTime(userinfo.getEnddt()));
								param.put("carProp", carProp);

								LOG.info("====================getUserInfoCarIn1,Money,params:" + param
										+ "================");
								JSONObject Cost = HttpClientUtil
										.doPost(ParkingLotConfigUtil.GetParkingLotConfig("cloudchargeurl"), param);
								LOG.info("====================getUserInfoCarIn1,Money,Cost:" + Cost
										+ "================");

								if (Cost.getString("returnCode").equals("0000")) {
									BigDecimal x1 = new BigDecimal(
											Cost.getJSONObject("result").getFloat("realCost") / 100);
									x1 = x1.divide(new BigDecimal(1), 2, BigDecimal.ROUND_HALF_UP);
									object.put("money", x1);
									cost = cost.add(x1);
								} else {
									object.put("money", 0);
								}
								object.put("outtime", end);
								// array.add(object);
							} else {
								object.put("outtime", "-");
								object.put("money", 0);
							}
							array.add(object);
							// Date dtnow = DateUtil.getCurrentDate();
							// Date dtend = DateUtil.getDateFromString(end);
							// Date dtin = usercrdtmin.getCrdtm();
							// if (DateUtil.compareDate(dtend, dtin)) {
							// // end = DateUtil.getNowDateTime();
							// end = DateUtil.getPlusTime(dtin);
							// // 续费开始时间小于进场时间 end=进场时间
							// } else if (DateUtil.compareDate(dtend, dtnow)) {
							//
							// } else {
							// end = start;
							// }
							//
							// LOG.info("====================getUserInfoCarIn,Money,end:"
							// + end + "================");
							// String params = "{\"parkId\": " +
							// Integer.valueOf(ParkingLotNo)
							// + ",\"carProp\": {\"start\": \"" + start +
							// "\",\"end\": \"" + end
							// + "\",\"carId\": \"" + usercrdtmin.getCarcode() +
							// "\",\"ruleId\": \""
							// + usercrdtmin.getChargeruleid() + "\",\"areaId\":
							// \"" + usercrdtmin.getAreaId()
							// + "\",\"turnOverList\": [],\"paidValue\":
							// 0,\"mstart\":\""
							// + DateUtil.getPlusTime(userinfo.getBgndt()) +
							// "\",\"mend\":\""
							// + DateUtil.getPlusTime(userinfo.getEnddt()) +
							// "\"}}";
							//
							// LOG.info(
							// "====================getUserInfoCarIn,Money,params:"
							// + params + "================");
							//
							// JSONObject Cost = HttpClientUtil
							// .doPost(ParkingLotConfigUtil.GetParkingLotConfig("cloudchargeurl"),
							// params);
							// LOG.info("====================getUserInfoCarIn,Money,Cost:"
							// + Cost.toJSONString()
							// + "================");
							//
							// JSONObject paidparams = new JSONObject();
							// paidparams.put("parkNo", ParkingLotNo);
							// paidparams.put("carCode",
							// usercrdtmin.getCarcode());
							// paidparams.put("outTime", end);
							// // paidparams.put("channelId", 0);
							// LOG.info("====================getUserInfoCarIn,Money,Paid,params"
							// + paidparams.toJSONString() +
							// "================");
							//
							// JSONObject paidobject =
							// padservice.getCarInfoForPad(paidparams.toJSONString());
							//
							// LOG.info("====================getUserInfoCarIn,Money,Paid:"
							// + paidobject.toJSONString()
							// + "================");
							//
							// if (Cost != null &&
							// Cost.getString("returnCode").equals("0000")
							// && paidobject.getString("resCode").equals("0")) {
							//
							// BigDecimal x1 = new
							// BigDecimal(Cost.getJSONObject("result").getFloat("realCost")
							// / 100);
							// cost = cost.add(x1);
							// JSONArray paidarray =
							// paidobject.getJSONArray("chargeHistory");
							// for (int i = 0; i < paidarray.size(); i++) {
							// BigDecimal x2 = new BigDecimal(
							// paidarray.getJSONObject(i).getFloat("realchargeamount")
							// / 100);
							// paid = paid.add(x2);
							// }
							// cost = cost.subtract(paid);
							// if (cost.compareTo(new BigDecimal(0)) <= 0) {
							// cost = new BigDecimal(0);
							// }
							// object.put("money",
							// x1.subtract(paid).compareTo(new BigDecimal(0)) <=
							// 0 ? 0
							// : x1.subtract(paid).setScale(2,
							// BigDecimal.ROUND_HALF_UP).doubleValue());
							// } else {
							// object.put("money", 0);
							// }
							// object.put("outtime", end);
							// array.add(object);
						}
					}
				}
			}

			if (limit > array.size()) {
				limit = array.size();
			}
			LOG.info("====================getUserInfoCarIn,Array:" + array.toJSONString() + "================");
			/*
			 * List<Tc_userinfocarin> list =
			 * JSONObject.parseArray(array.toJSONString(),
			 * Tc_userinfocarin.class); Collections.sort(list);
			 */
			List<Tc_userinfocarin> list = JSON.parseArray(array.toJSONString(), Tc_userinfocarin.class);
			Collections.sort(list);

			LOG.info("====================getUserInfoCarIn,Array:" + array + "================");
			result.put("total", list.size());
			result.put("rows", list.subList(offset, limit));
			result.put("cost", cost.setScale(2, BigDecimal.ROUND_HALF_UP));
			LOG.info("====================getUserInfoCarIn,Result:" + result.toJSONString() + "================");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public JSONObject getUserInfoByID(String requestBody) {
		JSONObject result = new JSONObject();
		try {
			if (requestBody == null || requestBody.isEmpty()) {
				result.put("respone_code", "1");
				result.put("respone_msg", "get userinfo failed,the requestBody is null");
			} else {
				JSONObject object = JSON.parseObject(requestBody);
				String userid = object.getString("userid");
				if (userid == null || userid.isEmpty()) {
					result.put("respone_code", "1");
					result.put("respone_msg", "get userinfo failed,the userid is null");
				} else {
					Tc_userinfo model = userinfomapper.selectByPrimaryKey(Integer.valueOf(userid));
					if (model != null && model.getRecordid() > 0) {
						result.put("data", model);
						result.put("respone_code", "0");
						result.put("respone_msg", "get userinfo succeed");
					} else {
						result.put("respone_code", "1");
						result.put("respone_msg", "get userinfo failed,the model is null");
					}
				}
			}
		} catch (Exception e) {
			result.put("respone_code", "1");
			result.put("respone_msg", "get userinfo failed,exception occurred");
			LOG.info("getUserInfoByID,exception occurred:" + e.toString());
		}
		return result;
	}

	public int getPartitionidin(String ParkingLotNo) {
		int hasresult = StringUtil.getAsciiCode(ParkingLotNo) % 16;
		return hasresult;
	}

	public Integer getPartitionid(String ParkingLotNo) {
		int hasresult = StringUtil.getAsciiCode(ParkingLotNo) % 16;
		LOG.info("=============hasresult" + hasresult + "=============");
		// 分区字段开始计算
		String partitionIdTemp = "";
		if (hasresult < 10) {
			partitionIdTemp = DateUtil.getFomartDate(new Date(), "yyyyMM") + "0" + hasresult;

		} else {
			partitionIdTemp = DateUtil.getFomartDate(new Date(), "yyyyMM") + hasresult;
		}
		Integer partitionID = Integer.parseInt(partitionIdTemp);
		// 分区字段计算结束
		return partitionID;
	}

}
