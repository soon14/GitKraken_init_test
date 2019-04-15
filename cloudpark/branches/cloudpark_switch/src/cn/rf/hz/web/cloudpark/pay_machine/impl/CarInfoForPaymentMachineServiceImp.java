package cn.rf.hz.web.cloudpark.pay_machine.impl;

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
import com.reformer.cloudpark.service.ParkingInformation;
import com.reformer.datatunnel.client.DataTunnelPublishClient;

import cn.rf.hz.web.cloudpark.daoxx.Tc_parkingareaMapper;
import cn.rf.hz.web.cloudpark.impl.BoxInfoServiceImp;
import cn.rf.hz.web.cloudpark.impl.PublicParkingService;
import cn.rf.hz.web.cloudpark.moder.Tc_chargerecordinfo;
import cn.rf.hz.web.cloudpark.moder.Tc_parkingarea;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm_in;
import cn.rf.hz.web.cloudpark.pay_machine.service.CarInfoForPaymentMachineService;
import cn.rf.hz.web.sharding.dao.Tc_chargerecordinfoMapper;
import cn.rf.hz.web.sharding.dao.Tc_usercrdtmMapper;
import cn.rf.hz.web.sharding.dao.Tc_usercrdtm_inMapper;
import cn.rf.hz.web.utils.BigDataAnalyze;
import cn.rf.hz.web.utils.DataConstants;
import cn.rf.hz.web.utils.DateUtil;
import cn.rf.hz.web.utils.JedisPoolUtils;
import cn.rf.hz.web.utils.ParkingLotConfigUtil;
import cn.rf.hz.web.utils.StringUtil;
import cn.rf.hz.web.utils.httputils.HttpClientUtil;

@Service("carInfoForPaymentMachineService")
public class CarInfoForPaymentMachineServiceImp implements CarInfoForPaymentMachineService {

	private final static Logger LOG = Logger.getLogger(CarInfoForPaymentMachineServiceImp.class);

	@Autowired
	Tc_usercrdtm_inMapper usercrdtmInMapper;

	@Autowired
	Tc_usercrdtmMapper usercrdtmMapper;

	@Autowired
	PublicParkingService publicParkingService;

	@Autowired
	Tc_parkingareaMapper areaMapper;

	@Autowired(required = false)
	PublicParkingService publicparkingservice;

	@Autowired
	Tc_chargerecordinfoMapper chargerecordinfoMapper;

	@Autowired
	ParkingInformation parkingInformation;

	@Autowired
	private DataTunnelPublishClient dataTunnelPublishClient;

	/**
	 * @Title: getCarInfoListbyCarcode
	 * @Description: 根据车牌模糊匹配场内车辆信息集合
	 * @param: parkinglotNo
	 *             车场ID
	 * @param: carcode
	 *             车牌号
	 * @return: int
	 */
	public List<Tc_usercrdtm_in> getCarInfoListbyCarcode(String parkinglotNo, String carcode) {
		Map<String, Object> params = new HashMap<>();
		int partitionID = publicParkingService.getPartitionidin(parkinglotNo);
		params.put("partitionID", partitionID);
		params.put("parkNo", parkinglotNo);
		params.put("carCode", carcode);
		List<Tc_usercrdtm_in> usercrdtmIn_list = usercrdtmInMapper.queryUserCrdtmInListByWhere(params);
		return usercrdtmIn_list;
	}

	/**
	 * @Title: getCarInfoCountbyCarcode
	 * @Description: 根据车牌模糊匹配场内相似车辆数量
	 * @param: parkinglotNo
	 *             车场ID
	 * @param: carcode
	 *             车牌号
	 * @return: int
	 */
	public int getCarInfoCountbyCarcode(String parkinglotNo, String carcode) {
		Map<String, Object> params = new HashMap<>();
		int partitionID = publicParkingService.getPartitionidin(parkinglotNo);
		params.put("partitionID", partitionID);
		params.put("parkNo", parkinglotNo);
		params.put("carCode", carcode);
		// 根据条件返回查询总数
		int count = 0;
		count = usercrdtmInMapper.queryUserCrdtmInListCountByWhere(params);
		return count;
	}

	/**
	 * @Title: getCarInfoCountbyCarcode
	 * @Description: 根据入场时间范围模糊匹配场内相似信息集合
	 * @param: parkinglotNo
	 *             车场ID
	 * @param: startDate
	 *             开始时间
	 * @param: endDate
	 *             结束时间
	 * @return: int
	 */
	public List<Tc_usercrdtm_in> getCarInfoListbyTime(String parkinglotNo, String startDate, String endDate) {
		Map<String, Object> params = new HashMap<>();
		int partitionID = publicParkingService.getPartitionidin(parkinglotNo);
		params.put("partitionID", partitionID);
		params.put("parkNo", parkinglotNo);
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		List<Tc_usercrdtm_in> usercrdtmIn_list = usercrdtmInMapper.queryUserCrdtmInListByWhere(params);
		return usercrdtmIn_list;
	}

	/**
	 * @Title: getCarInfoCountbyCarcode
	 * @Description: 根据入场时间范围模糊匹配场内相似车辆数量
	 * @param: parkinglotNo
	 *             车场ID
	 * @param: startDate
	 *             开始时间
	 * @param: endDate
	 *             结束时间
	 * @return: int
	 */
	public int getCarInfoCountbyTime(String parkinglotNo, String startDate, String endDate) {
		Map<String, Object> params = new HashMap<>();
		int partitionID = publicParkingService.getPartitionidin(parkinglotNo);
		params.put("partitionID", partitionID);
		params.put("parkNo", parkinglotNo);
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		// 根据条件返回查询总数
		int count = 0;
		count = usercrdtmInMapper.queryUserCrdtmInListCountByWhere(params);
		return count;
	}

	@Override
	public List<Tc_chargerecordinfo> getChargerecordListbyCarcode(String parkinglotNo, String carcode) {

		return null;
	}

	@Override
	public JSONObject uploadHeartBeatRecord(JSONObject jsonHeart) {
		JSONObject resultJSON = new JSONObject();
		try {
			String power_status = jsonHeart.getString("power_status"); // 电源状态
			String door_status = jsonHeart.getString("door_status"); // 开门状态
			String notes_charge_status = jsonHeart.getString("notes_charge_status");// 纸币收钱机状态
			String notes_change_status = jsonHeart.getString("notes_change_status");// 纸币找零机状态
			String coins_charge_status = jsonHeart.getString("coins_charge_status");// 硬币收钱机状态
			String coins_change_status = jsonHeart.getString("coins_change_status");// 硬币找零机状态
			String printer_status = jsonHeart.getString("printer_status");// 打印机状态
			String scancode_status = jsonHeart.getString("scancode_status");// 扫描条码状态
			String readcard_status = jsonHeart.getString("readcard_status");// 读卡器状态
			String pos_status = jsonHeart.getString("pos_status");// pos机状态
			String park_code = jsonHeart.getString("park_code");// 车场编号
			int area_id = jsonHeart.getIntValue("area_id");// 区域编号
			int channel_id = jsonHeart.getIntValue("channel_id");// 通道编号
			int station_id = jsonHeart.getIntValue("station_id");// 缴费机编号
			String upload_time = jsonHeart.getString("upload_time");// 上传时间

			// 保存方法
			// TODO

			resultJSON.put("sign", "");
			resultJSON.put("send_date", DateUtil.getNowPlusTime());
			resultJSON.put("res_code", 0);
			resultJSON.put("res_msg", "success");
		} catch (Exception e) {
			e.printStackTrace();
			resultJSON.put("sign", "");
			resultJSON.put("res_code", -1);
			resultJSON.put("res_msg", "uploadHeartBeatRecord is failed");
		}
		return resultJSON;
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

	@Override
	public JSONObject getCarChargeInfo(String parkinglotNo, String carCode, String inTime) {
		JSONObject resultJSON = new JSONObject();
		try {
			String key = BigDataAnalyze.geListKeyByDataType(parkinglotNo, DataConstants.CLOUDPARK_INOUT, "k");
			String strCarInInfo = JedisPoolUtils.hget(key, carCode);
			String params = "";
			String areaId = "";
			String chargetime = DateUtil.getNowDateTime();
			String productId = "";
			if (strCarInInfo != null && !strCarInInfo.isEmpty()) {
				JSONArray carArray = JSONArray.parseArray(strCarInInfo);
				params = "{\"parkId\": \"" + Integer.valueOf(parkinglotNo) + "\",\"carProp\": {\"start\": \""
						+ DateUtil.getLongToDateStr(carArray.getJSONObject(0).getLong("intime")) + "\",\"end\": \""
						+ chargetime + "\",\"carId\": \"" + carArray.getJSONObject(0).getString("carcode")
						+ "\",\"ruleId\": \"" + carArray.getJSONObject(0).getString("chargeruleid")
						+ "\",\"areaId\": \"" + carArray.getJSONObject(0).getString("areaId")
						+ "\",\"turnOverList\": [],\"paidValue\": 0}}";
				areaId = carArray.getJSONObject(0).getString("areaId");
				productId = carArray.getJSONObject(0).getString("chargeruleid");
			} else {
				Map<String, Object> mapper = new HashMap<String, Object>();
				mapper.put("PartitionID", getPartitionidin(parkinglotNo));
				mapper.put("ParkingLotNo", parkinglotNo);
				mapper.put("CarCode", carCode);
				List<Object> usercrdtminlist = usercrdtmInMapper.queryByCondition(mapper);
				if (usercrdtminlist != null && usercrdtminlist.size() > 0) {
					Tc_usercrdtm_in usercrdtmin = (Tc_usercrdtm_in) usercrdtminlist.get(0);
					params = "{\"parkId\": " + Integer.valueOf(parkinglotNo) + ",\"carProp\": {\"start\": \""
							+ DateUtil.getPlusTime(usercrdtmin.getCrdtm()) + "\",\"end\": \"" + chargetime
							+ "\",\"carId\": \"" + usercrdtmin.getCarcode() + "\",\"ruleId\": \""
							+ usercrdtmin.getChargeruleid() + "\",\"areaId\": \"" + usercrdtmin.getAreaId()
							+ "\",\"turnOverList\": [],\"paidValue\": 0}}";
					areaId = usercrdtmin.getAreaId();
					productId = String.valueOf(usercrdtmin.getChargeruleid());
				}

			}
			JSONObject Cost = HttpClientUtil.doPost(ParkingLotConfigUtil.GetParkingLotConfig("cloudchargeurl"), params);
			LOG.info("====================getCarChargeInfo,cloudchargeurl,return:" + Cost.toJSONString()
					+ "================");
			if (Cost.getString("returnCode").equals("0000")) {
				resultJSON.put("car_code", carCode);
				resultJSON.put("area_id", areaId);
				Tc_parkingarea areaInfo = areaMapper.selectByPrimaryKey(Integer.valueOf(areaId));
				if (areaInfo != null && !areaInfo.getAreaname().isEmpty()) {
					resultJSON.put("area_name", areaInfo.getAreaname());
				} else {
					resultJSON.put("area_name", "");
				}
				resultJSON.put("in_time", inTime);
				resultJSON.put("charge_time", chargetime);
				resultJSON.put("product_id", productId);
				resultJSON.put("charge_money", Cost.getJSONObject("result").getInteger("sumCost"));
				resultJSON.put("reduction_amount", 0);
				resultJSON.put("paid_amount", 0);
				resultJSON.put("barcode_available_count", "0");
				resultJSON.put("export_code", "0");
				resultJSON.put("last_out_time", Cost.getJSONObject("result").getString("lastOutTime"));
				resultJSON.put("is_upload_zero_chargeinfo", "0");
				resultJSON.put("surplus_out_time", "0");
				resultJSON.put("stop_time",
						(DateUtil.StringfomateDate(chargetime).getTime() - DateUtil.StringfomateDate(inTime).getTime())
								/ 1000);
				resultJSON.put("res_code", "0");
				resultJSON.put("res_msg", "getCarChargeInfo success");
			} else {
				resultJSON.put("res_code", "1");
				resultJSON.put("res_msg", "getCarChargeInfo failed");
			}

		} catch (Exception e) {
			e.printStackTrace();
			resultJSON.put("sign", "");
			resultJSON.put("res_code", -1);
			resultJSON.put("res_msg", "uploadHeartBeatRecord error");
		}
		return resultJSON;
	}

	@Override
	public JSONObject uploadChargeRecordInfo(JSONObject chargeInfo) {
		JSONObject resultJSON = new JSONObject();
		String parkinglotNo = chargeInfo.getString("parkno");
		try {
			ArrayList<Tc_chargerecordinfo> chargerecordinfolist = setChargerecordinfo(chargeInfo);

			chargerecordinfoMapper.batchInsertChargerecordinfo(chargerecordinfolist);

			for (int i = 0; i < chargerecordinfolist.size(); i++) {
				if (chargerecordinfolist.get(i).getAmounttype() == null
						|| chargerecordinfolist.get(i).getAmounttype() == 0) {
					// 停车收费
					sendKafkaMesUploadPayRecord(parkinglotNo, chargerecordinfolist);
				} else if (chargerecordinfolist.get(i).getAmounttype() > 0) {
					// 其他收费
					sendKafkaMesUploadPayRecord(parkinglotNo, chargerecordinfolist,
							DataConstants.CLOUDPARK_OTHERCHARGE);
				}
			}
			
			resultJSON.put("park_code", parkinglotNo);
			resultJSON.put("res_code", 0);
			resultJSON.put("res_msg", "success");
		} catch (Exception e) {
			e.printStackTrace();
			resultJSON.put("park_code", parkinglotNo);
			resultJSON.put("res_code", 1);
			resultJSON.put("res_msg", "failed");
		}
		return resultJSON;
	}

	public ArrayList<Tc_chargerecordinfo> setChargerecordinfo(JSONObject data) {
		ArrayList<Tc_chargerecordinfo> chargerecordinfolist = new ArrayList<Tc_chargerecordinfo>();
		try {
			String ParkingLotNo = data.getString("park_code");
			JSONArray dataArrays = data.getJSONArray("data");
			for (int k = 0; k < dataArrays.size(); k++) {
				JSONObject object = dataArrays.getJSONObject(k);
				Tc_chargerecordinfo chargerecordinfo = new Tc_chargerecordinfo();
				chargerecordinfo.setParkinglotno(ParkingLotNo);
				if (object.containsKey("car_code")) {
					chargerecordinfo.setCarcode(object.getString("car_code"));
				}
				if (object.containsKey("in_time")) {
					chargerecordinfo.setIntime(DateUtil.StringfomateDate(object.getString("in_time")));
				}
				if (object.containsKey("out_time")) {
					chargerecordinfo.setOuttime(DateUtil.StringfomateDate(object.getString("out_time")));
				}
				if (object.containsKey("charge_date")) {
					chargerecordinfo.setChargedate(DateUtil.StringfomateDate(object.getString("charge_date")));
				}
				if (object.containsKey("stop_time")) {
					chargerecordinfo.setStoptime(object.getString("stop_time"));
				}
				if (object.containsKey("receivabl_amount")) {
					// 应收金额
					chargerecordinfo.setChargemoney((float) (Math.round(object.getFloat("receivabl_amount"))));
				}
				if (object.containsKey("reduction_amount")) {
					// 减免金额
					chargerecordinfo.setReductionamount((float) (Math.round(object.getFloat("reduction_amount"))));
				}
				if (object.containsKey("paid_amount")) {
					// 实收金额
					chargerecordinfo.setRealchargeamount((float) (Math.round(object.getFloat("paid_amount"))));
				}

				if (object.containsKey("charge_typeid")) {
					chargerecordinfo.setChargetype(object.getIntValue("charge_typeid"));
				}

				if (object.containsKey("deviceid")) {
					// deviceid = object.getInteger("deviceId");
					chargerecordinfo.setDeviceid(object.getInteger("deviceid"));
				}

				if (object.containsKey("channelid")) {
					chargerecordinfo.setChannelid(object.getInteger("channelid"));
				}

				/*
				 * if (object.containsKey("chargeRuleId")) {
				 * chargerecordinfo.setChargeruleid(object.getInteger(
				 * "chargeRuleId")); }
				 */
				if (object.containsKey("charge_ruleid")) {
					// 收费规则Id
					// chargeRuleId
					chargerecordinfo.setChargeruleid(object.getIntValue("charge_ruleid"));

				}

				if (object.containsKey("operatorid")) {
					// 收费员ID
					chargerecordinfo.setChargeuserid(String.valueOf(object.getIntValue("operatorid")));
				}
				if (object.containsKey("charge_source")) {
					/*
					 * 0 现金 1 电子账户 2 支付宝支付 3 微信支付 4 平台代扣 5 喵街 6 闪付 7 支付宝刷卡
					 */
					chargerecordinfo.setChargesource(String.valueOf(object.getIntValue("charge_source")));
				}
				if (object.containsKey("lastouttime")) {
					chargerecordinfo.setLastouttime(DateUtil.StringfomateDate(object.getString("lastouttime")));
				}
				chargerecordinfo.setPartitionid(getPartitionid(ParkingLotNo));
				chargerecordinfo.setRecordid(publicparkingservice.getChargesequenceId());
				chargerecordinfo.setParkinglotno(ParkingLotNo);
				if (object.containsKey("car_code") && object.containsKey("in_time")) {
					// 获取车辆进场记录ID zhh修改
					try {
						JSONObject mapparam = new JSONObject();
						mapparam.put("ParkingLotNo", ParkingLotNo);
						mapparam.put("CarCode", object.getString("car_code"));
						mapparam.put("Crdtm", object.getString("in_time"));
						mapparam.put("PartitionID", getPartitionid(ParkingLotNo));
						LOG.info("=====获取进场ID:" + mapparam + "======");
						List<Object> list = usercrdtmMapper.queryByCondition(mapparam);
						if (list != null && list.size() > 0) {
							Tc_usercrdtm usercrdtm = (Tc_usercrdtm) list.get(0);
							if (usercrdtm != null && usercrdtm.getRecordid() > 0) {
								chargerecordinfo.setInrecordid(Integer.valueOf(usercrdtm.getRecordid().toString()));
								if (chargerecordinfo.getEmplyname() == null
										|| chargerecordinfo.getEmplyname().isEmpty()) {
									chargerecordinfo.setEmplyname(usercrdtm.getUsername());
								}
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				if (object.containsKey("amount_type")) {
					/*
					 * 0 停车收费 1充值收费 2 卡片成本 3 消费金额 4车位收费
					 */
					// 收费类型
					chargerecordinfo.setAmounttype(object.getIntValue("amount_type"));

				}

				Boolean isallfree = false;
				if (object.containsKey("reduction_name")) {
					// 减免名称
					// ReductionsName
					String reductionsname = object.getString("reduction_name");
					if (reductionsname.indexOf("全免") > -1) {
						chargerecordinfo.setReductionsname("全免");
						isallfree = true;
					} else {
						chargerecordinfo.setReductionsname(object.getString("reduction_name"));
					}
				}
				if (object.containsKey("reduction_type")) {
					String reductiontype = object.getString("reduction_type");
					if (isallfree) {
						String[] types = reductiontype.split(",");
						chargerecordinfo.setReductiontype(types[types.length - 1]);
					} else {
						chargerecordinfo.setReductiontype(object.getString("reduction_type"));
					}
				}

				if (object.containsKey("change_amount")) {
					chargerecordinfo.setChargeamount(object.getFloat("change_amount "));
				}

				if (object.containsKey("notchange_amount")) {
					chargerecordinfo.setChargeamount(object.getFloat("notchange_amount"));
				}

				if (object.containsKey("order_number")) {
					chargerecordinfo.setPayrecordid(object.getInteger("order_number"));
				}

				if (object.containsKey("remarks1")) {
					chargerecordinfo.setRemarks1(object.getString("remarks1"));
				}

				if (object.containsKey("remarks2")) {
					chargerecordinfo.setRemarks2(object.getString("remarks2"));
				}
				// 增加区域信息字段
				if (object.containsKey("areaid")) {
					chargerecordinfo.setAreaid(object.getInteger("areaid"));
				}
				chargerecordinfolist.add(chargerecordinfo);
			}
		} catch (Exception e) {
			LOG.info("====================Exception================" + e.getMessage());
			e.printStackTrace();
		}
		return chargerecordinfolist;
	}

	public void sendKafkaMesUploadPayRecord(String ParkingLotNo, ArrayList<Tc_chargerecordinfo> chargerecordinfolist) {
		try {
			JSONArray jsonArray = new JSONArray();
			LOG.info("=============chargerecordinfolist.size" + chargerecordinfolist.size() + "=============");
			long l = 0;
			for (int p = 0; p < chargerecordinfolist.size(); p++) {
				Tc_chargerecordinfo chargerecordinfo = chargerecordinfolist.get(p);
				JSONObject charge = JSONObject.parseObject(JSONObject.toJSONString(chargerecordinfo));
				if (chargerecordinfo.getAreaid() != null && chargerecordinfo.getAreaid() > 0) {
					Tc_parkingarea area = parkingInformation.getAreaInfo(String.valueOf(chargerecordinfo.getAreaid()));
					if (null != area && null != area.getAreaname())
						charge.put("areaName", area.getAreaname());
				}
				jsonArray.add(charge);
			}
			LOG.info("=============jsonArraycharge" + jsonArray + "=============");
			BigDataAnalyze.sendMess(jsonArray, dataTunnelPublishClient, l, "in_p", ParkingLotNo,
					DataConstants.CLOUDPARK_CHARGE);

		} finally {
		}
	}

	public void sendKafkaMesUploadPayRecord(String ParkingLotNo, ArrayList<Tc_chargerecordinfo> chargerecordinfolist,
			String topic) {
		try {
			JSONArray jsonArray = new JSONArray();
			LOG.info("=============chargerecordinfolist.size" + chargerecordinfolist.size() + "=============");
			long l = 0;
			for (int p = 0; p < chargerecordinfolist.size(); p++) {
				Tc_chargerecordinfo chargerecordinfo = chargerecordinfolist.get(p);
				JSONObject charge = JSONObject.parseObject(JSONObject.toJSONString(chargerecordinfo));
				if (chargerecordinfo.getAreaid() != null && chargerecordinfo.getAreaid() > 0) {
					Tc_parkingarea area = parkingInformation.getAreaInfo(String.valueOf(chargerecordinfo.getAreaid()));
					if (null != area && null != area.getAreaname())
						charge.put("areaName", area.getAreaname());
				}
				jsonArray.add(charge);
			}
			LOG.info("=============jsonArraycharge" + jsonArray + "=============");
			BigDataAnalyze.sendMess(jsonArray, dataTunnelPublishClient, l, "in_p", ParkingLotNo, topic);

		} finally {
		}
	}
}
