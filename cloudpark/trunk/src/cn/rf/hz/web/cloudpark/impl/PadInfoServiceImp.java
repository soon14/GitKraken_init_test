package cn.rf.hz.web.cloudpark.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.cloudpark.daoxx.Pb_parkingparmMapper;
import cn.rf.hz.web.cloudpark.daoxx.Pb_stationMapper;
import cn.rf.hz.web.cloudpark.moder.Pb_parkingparm;
import cn.rf.hz.web.cloudpark.moder.Pb_station;
import cn.rf.hz.web.cloudpark.daoxx.Tc_channelMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_parkInformationMapper;
import cn.rf.hz.web.cloudpark.moder.Tc_channel;
import cn.rf.hz.web.cloudpark.service.PadInfoService;
import cn.rf.hz.web.cloudpark.service.Tc_channelService;
import cn.rf.hz.web.utils.DateUtil;
import cn.rf.hz.web.utils.ParkingLotConfigUtil;

@Service("padinfo")
public class PadInfoServiceImp implements PadInfoService {
	private final static Logger LOG = Logger.getLogger(PadInfoServiceImp.class);
	@Autowired
	private Pb_stationMapper stationMapper;
	@Autowired
	private Tc_channelMapper channelMapper;
	@Autowired
	private Pb_parkingparmMapper parkingparmMapper;

	@Autowired
	private Tc_parkInformationMapper parkinginfoMapper;

	@Override
	public JSONObject getPadInfo(String requestBody) {
		// System.out.println("=================="+ParkingLotNo);
		JSONObject result = new JSONObject();
		JSONObject model = new JSONObject();
		result.put("sign", "");

		if (requestBody == null || requestBody.isEmpty()) {
			result.put("data", new JSONArray());
			result.put("resCode", 100);
			result.put("resMsg", "The method parameter is null");
			LOG.error("The method parameter is null:" + requestBody);
			return result;
		}
		try {
			JSONObject data = JSON.parseObject(requestBody);// 暂时未加密
			String serialnumber = data.getString("serialNumber");

			JSONObject ossconfig = new JSONObject();
			ossconfig.put("ossEndpoint", "http://oss-cn-hangzhou.aliyuncs.com");
			ossconfig.put("ossBucketName", "test-fq");
			ossconfig.put("ossAccessKeyId", "u1KDPtqLl3JkCjno");
			ossconfig.put("ossAccessKeySecret", "bmNmpLbb4JfUymoc6ZlAokqTSAvmSI");
			ossconfig.put("parkingEntranceDir", "temporary-fq/parking_entrance-fq/");
			ossconfig.put("parkingDepartureDir", "temporary-fq/parking_departure-fq/");
			model.put("ossConfig", ossconfig);

			Pb_station station = stationMapper.selectBySerialNumber(serialnumber);
			if (station != null && station.getRecordid() > 0) {
				model.put("parkNo", station.getParkinglotno());
				model.put("parkingLotName", station.getStationname());
				model.put("channelId", station.getChannelid());
				Tc_channel channel = channelMapper.selectByPrimaryKey(station.getChannelid());
				if (channel != null && channel.getChannelid() > 0) {
					model.put("chargeRuleId", channel.getChargeruleid());
					model.put("areaId", channel.getParkingareaid());
				}
				model.put("remarks1", "");
				model.put("remarks2", "");

				String isuploadimage = ParkingLotConfigUtil.GetParkingLotConfig("isuploadimage");
				LOG.info(isuploadimage);
				Pb_parkingparm parkingparm = new Pb_parkingparm();
				parkingparm = parkingparmMapper.selectByPrimaryKey(station.getParkinglotno(),
						Integer.valueOf(isuploadimage));
				if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
					model.put("isUploadImage", parkingparm.getParmvalue());
				}
				LOG.info("getPadInfo,isUploadImage:" + parkingparm.getParmvalue());

				String isallowtemporarycarin = ParkingLotConfigUtil.GetParkingLotConfig("isallowtemporarycarin");
				LOG.info(isallowtemporarycarin);
				parkingparm = new Pb_parkingparm();
				parkingparm = parkingparmMapper.selectByPrimaryKey(station.getParkinglotno(),
						Integer.valueOf(isallowtemporarycarin));
				if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
					model.put("isAllowTemporaryCarIn", parkingparm.getParmvalue());
				}
				LOG.info("getPadInfo,isAllowTemporaryCarIn:" + parkingparm.getParmvalue());

				String isallowcarinwhennospaces = ParkingLotConfigUtil.GetParkingLotConfig("isallowcarinwhennospaces");
				LOG.info(isallowcarinwhennospaces);
				parkingparm = new Pb_parkingparm();
				parkingparm = parkingparmMapper.selectByPrimaryKey(station.getParkinglotno(),
						Integer.valueOf(isallowcarinwhennospaces));
				if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
					model.put("isAllowCarInWhenNoSpaces", parkingparm.getParmvalue());
				}
				LOG.info("getPadInfo,isAllowCarInWhenNoSpaces:" + parkingparm.getParmvalue());

				String isinparkinglot = ParkingLotConfigUtil.GetParkingLotConfig("isinparkinglot");
				LOG.info(isinparkinglot);

				parkingparm = new Pb_parkingparm();
				parkingparm = parkingparmMapper.selectByPrimaryKey(station.getParkinglotno(),
						Integer.valueOf(isinparkinglot));
				if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
					model.put("isInParkingLot", parkingparm.getParmvalue());
				}
				LOG.info("getPadInfo,isInParkingLot:" + parkingparm.getParmvalue());

				String iscloudcharge = ParkingLotConfigUtil.GetParkingLotConfig("iscloudcharge");
				LOG.info(iscloudcharge);
				parkingparm = new Pb_parkingparm();
				parkingparm = parkingparmMapper.selectByPrimaryKey(station.getParkinglotno(),
						Integer.valueOf(iscloudcharge));
				if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
					model.put("isCloudCharge", parkingparm.getParmvalue());
				}
				LOG.info("getPadInfo,isCloudCharge:" + parkingparm.getParmvalue());

				String cloudchargeurl = ParkingLotConfigUtil.GetParkingLotConfig("cloudchargeurl");
				LOG.info("getPadInfo,cloudchargeurl:" + cloudchargeurl);

				model.put("cloudChargeUrl", cloudchargeurl);

				String ismultiplechargerule = ParkingLotConfigUtil.GetParkingLotConfig("ismultiplechargerule");
				LOG.info(ismultiplechargerule);
				parkingparm = new Pb_parkingparm();
				parkingparm = parkingparmMapper.selectByPrimaryKey(station.getParkinglotno(),
						Integer.valueOf(ismultiplechargerule));
				if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
					model.put("isMultipleChargeRule", parkingparm.getParmvalue());
				}

				LOG.info("getPadInfo,isMultipleChargeRule:" + parkingparm.getParmvalue());
				String isreductionsuperposition = ParkingLotConfigUtil.GetParkingLotConfig("isreductionsuperposition");
				parkingparm = new Pb_parkingparm();
				parkingparm = parkingparmMapper.selectByPrimaryKey(station.getParkinglotno(),
						Integer.valueOf(isreductionsuperposition));
				if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
					model.put("isReductionSuperposition", parkingparm.getParmvalue());
				}

				result.put("data", model);
				result.put("resCode", 0);
				result.put("resMsg", "Succeed to obtain Pad information");
				LOG.info("getPadInfo:Succeed to obtain pad information:" + requestBody);
			} else {
				result.put("data", new JSONArray());
				result.put("resCode", 1);
				result.put("resMsg", "failed to obtain pad information");
				LOG.error("getPadInfo:failed to obtain pad information:" + requestBody);
			}
		} catch (Exception e) {
			result.put("data", new JSONArray());
			result.put("resCode", 2);
			result.put("resMsg", "An exception occurs for pad information");
			LOG.error("getPadInfo:An exception occurs for pad information:" + e.toString());
		}
		return result;
	}

	@Override
	public JSONObject getNewPadInfo(String requestBody) {
		LOG.info("getNewPadInfogetNewPadInfogetNewPadInfogetNewPadInfo");
		// System.out.println("=================="+ParkingLotNo);
		System.out.println("===========getNewPadInfo,requestBody:" + requestBody + "==================");
		System.out.println("===========getNewPadInfo,begin:" + DateUtil.getNowTimeString() + "==================");
		JSONObject result = new JSONObject();
		JSONObject model = new JSONObject();
		result.put("sign", "");

		if (requestBody == null || requestBody.isEmpty()) {
			result.put("data", new JSONArray());
			result.put("resCode", 100);
			result.put("resMsg", "The method parameter is null");
			LOG.error("The method parameter is null:" + requestBody);
			return result;
		}
		try {
			JSONObject data = JSON.parseObject(requestBody);// 暂时未加密
			String serialnumber = data.getString("serialNumber");
			if (serialnumber == null || serialnumber.isEmpty()) {
				result.put("data", new JSONArray());
				result.put("resCode", 100);
				result.put("resMsg", "The serialNumber is null");
				LOG.error("The serialNumber is null:" + requestBody);
				return result;
			}

			JSONObject ossconfig = new JSONObject();
			ossconfig.put("ossEndpoint", "http://oss-cn-hangzhou.aliyuncs.com");
			ossconfig.put("ossBucketName", "test-fq");
			ossconfig.put("ossAccessKeyId", "u1KDPtqLl3JkCjno");
			ossconfig.put("ossAccessKeySecret", "bmNmpLbb4JfUymoc6ZlAokqTSAvmSI");
			ossconfig.put("parkingEntranceDir", "temporary-fq/parking_entrance-fq/");
			ossconfig.put("parkingDepartureDir", "temporary-fq/parking_departure-fq/");
			model.put("ossConfig", ossconfig);
			LOG.info("getPadInfo:" + requestBody);
			Pb_station station = stationMapper.selectBySerialNumber(serialnumber);
			if (station != null && station.getRecordid() > 0) {

				LOG.info("getPadInfo,Station:" + station.getRecordid());
				model.put("deviceId", station.getRecordid());
				model.put("parkNo", station.getParkinglotno());
				String parkinglotname = parkinginfoMapper.queryparkingNameById(station.getParkinglotno());
				model.put("parkingLotName", parkinglotname);
				List<Tc_channel> channellist = channelMapper.selectByMStationno(station.getRecordid());
				JSONArray array = new JSONArray();
				if (channellist != null && channellist.size() > 0) {
					for (int i = 0; i < channellist.size(); i++) {
						LOG.info("getPadInfo,channel:" + channellist.get(i).getChannelid());
						JSONObject object = new JSONObject();
						object.put("channelId", channellist.get(i).getChannelid());
						object.put("channelName", channellist.get(i).getChannelname());
						object.put("chargeRuleId", channellist.get(i).getChargeruleid());
						object.put("areaId", channellist.get(i).getParkingareaid());
						object.put("inOrOut", channellist.get(i).getInorout());
						array.add(object);
					}
				} else {
					result.put("data", model);
					result.put("resCode", 1);
					result.put("resMsg", "failed to obtain pad information,the channellist is null");
					LOG.error("getNewPadInfo:failed to obtain pad information,the channellist is null:" + requestBody);
					System.out.println(
							"===========getNewPadInfo,end:" + DateUtil.getNowTimeString() + "==================");
					return result;
				}
				LOG.info("1");
				model.put("data", array);
				model.put("remarks1", "");
				model.put("remarks2", "");
				LOG.info("2");

				String isuploadimage = ParkingLotConfigUtil.GetParkingLotConfig("isuploadimage");
				LOG.info(isuploadimage);
				Pb_parkingparm parkingparm = new Pb_parkingparm();
				LOG.info("2.1");
				/*
				 * parkingparm =
				 * parkingparmMapper.selectByPrimaryKey(station.getParkinglotno(
				 * ), Integer.valueOf(isuploadimage)); LOG.info("2.2"); if
				 * (parkingparm != null &&
				 * !parkingparm.getParmvalue().isEmpty()) { LOG.info("2.3");
				 * model.put("isUploadImage", parkingparm.getParmvalue());
				 * LOG.info("2.4"); } LOG.info("2.5");
				 */
				// LOG.info("getPadInfo,isUploadImage:" +
				// parkingparm.getParmvalue());

				String isallowtemporarycarin = ParkingLotConfigUtil.GetParkingLotConfig("isallowtemporarycarin");
				LOG.info("3");
				LOG.info(isallowtemporarycarin);
				parkingparm = new Pb_parkingparm();
				LOG.info("3.1");
				parkingparm = parkingparmMapper.selectByPrimaryKey(station.getParkinglotno(),
						Integer.valueOf(isallowtemporarycarin));
				LOG.info("3.2");
				if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
					LOG.info("3.3");
					model.put("isAllowTemporaryCarIn", parkingparm.getParmvalue());
					LOG.info("3.4");
				} else {
					result.put("data", model);
					result.put("resCode", 1);
					result.put("resMsg", "failed to obtain pad information,the isallowtemporarycarin is null");
					LOG.error("getNewPadInfo:failed to obtain pad information,the isallowtemporarycarin is null:"
							+ requestBody);
					System.out.println(
							"===========getNewPadInfo,end:" + DateUtil.getNowTimeString() + "==================");
					return result;
				}
				// LOG.info("getPadInfo,isAllowTemporaryCarIn:" +
				// parkingparm.getParmvalue());

				String isallowcarinwhennospaces = ParkingLotConfigUtil.GetParkingLotConfig("isallowcarinwhennospaces");
				LOG.info("4");
				LOG.info(isallowcarinwhennospaces);
				parkingparm = new Pb_parkingparm();
				LOG.info("4.1");
				parkingparm = parkingparmMapper.selectByPrimaryKey(station.getParkinglotno(),
						Integer.valueOf(isallowcarinwhennospaces));
				LOG.info("4.2");
				if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
					LOG.info("4.3");
					model.put("isAllowCarInWhenNoSpaces", parkingparm.getParmvalue());
					LOG.info("4.4");
				}
				// LOG.info("getPadInfo,isAllowCarInWhenNoSpaces:" +
				// parkingparm.getParmvalue());

				String isinparkinglot = ParkingLotConfigUtil.GetParkingLotConfig("isinparkinglot");
				LOG.info("5");
				LOG.info(isinparkinglot);

				parkingparm = new Pb_parkingparm();
				LOG.info("5.1");
				parkingparm = parkingparmMapper.selectByPrimaryKey(station.getParkinglotno(),
						Integer.valueOf(isinparkinglot));
				LOG.info("5.2");
				if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
					LOG.info("5.3");
					model.put("isInParkingLot", parkingparm.getParmvalue());
					LOG.info("5.4");
				} else {
					result.put("data", model);
					result.put("resCode", 1);
					result.put("resMsg", "failed to obtain pad information,the isinparkinglot is null");
					LOG.error(
							"getNewPadInfo:failed to obtain pad information,the isinparkinglot is null:" + requestBody);
					System.out.println(
							"===========getNewPadInfo,end:" + DateUtil.getNowTimeString() + "==================");
					return result;
				}
				// LOG.info("getPadInfo,isInParkingLot:" +
				// parkingparm.getParmvalue());

				String iscloudcharge = ParkingLotConfigUtil.GetParkingLotConfig("iscloudcharge");
				LOG.info("5");
				LOG.info(iscloudcharge);
				LOG.info("5.1");
				parkingparm = new Pb_parkingparm();
				LOG.info("5.2");
				parkingparm = parkingparmMapper.selectByPrimaryKey(station.getParkinglotno(),
						Integer.valueOf(iscloudcharge));
				LOG.info("5.3");
				if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
					LOG.info("5.4");
					model.put("isCloudCharge", parkingparm.getParmvalue());
					LOG.info("5.5");
				} else {
					result.put("data", model);
					result.put("resCode", 1);
					result.put("resMsg", "failed to obtain pad information,the iscloudcharge is null");
					LOG.error(
							"getNewPadInfo:failed to obtain pad information,the iscloudcharge is null:" + requestBody);
					System.out.println(
							"===========getNewPadInfo,end:" + DateUtil.getNowTimeString() + "==================");
					return result;
				}
				// LOG.info("getPadInfo,isCloudCharge:" +
				// parkingparm.getParmvalue());

				String cloudchargeurl = ParkingLotConfigUtil.GetParkingLotConfig("cloudchargeurl");
				// LOG.info("getPadInfo,cloudchargeurl:" + cloudchargeurl);

				model.put("cloudChargeUrl", cloudchargeurl);

				String ismultiplechargerule = ParkingLotConfigUtil.GetParkingLotConfig("ismultiplechargerule");
				LOG.info("6");
				LOG.info(ismultiplechargerule);
				LOG.info("6.1");
				parkingparm = new Pb_parkingparm();
				LOG.info("6.2");
				parkingparm = parkingparmMapper.selectByPrimaryKey(station.getParkinglotno(),
						Integer.valueOf(ismultiplechargerule));
				if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
					model.put("isMultipleChargeRule", parkingparm.getParmvalue());
				} else {
					result.put("data", model);
					result.put("resCode", 1);
					result.put("resMsg", "failed to obtain pad information,the ismultiplechargerule is null");
					LOG.error("getNewPadInfo:failed to obtain pad information,the ismultiplechargerule is null:"
							+ requestBody);
					System.out.println(
							"===========getNewPadInfo,end:" + DateUtil.getNowTimeString() + "==================");
					return result;
				}

				// LOG.info("getPadInfo,isMultipleChargeRule:" +
				// parkingparm.getParmvalue());
				String isreductionsuperposition = ParkingLotConfigUtil.GetParkingLotConfig("isreductionsuperposition");
				LOG.info("7");
				parkingparm = new Pb_parkingparm();
				LOG.info("7.1");
				parkingparm = parkingparmMapper.selectByPrimaryKey(station.getParkinglotno(),
						Integer.valueOf(isreductionsuperposition));
				if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
					model.put("isReductionSuperposition", parkingparm.getParmvalue());
				} else {
					result.put("data", model);
					result.put("resCode", 1);
					result.put("resMsg", "failed to obtain pad information,the isreductionsuperposition is null");
					LOG.error("getNewPadInfo:failed to obtain pad information,the isreductionsuperposition is null:"
							+ requestBody);
					System.out.println(
							"===========getNewPadInfo,end:" + DateUtil.getNowTimeString() + "==================");
					return result;
				}
				LOG.info("8");
				String defaultchargeruleid = ParkingLotConfigUtil.GetParkingLotConfig("defaultchargeruleid");
				parkingparm = parkingparmMapper.selectByPrimaryKey(station.getParkinglotno(),
						Integer.valueOf(defaultchargeruleid));
				if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
					model.put("defaultChargeRuleId", Integer.valueOf(parkingparm.getParmvalue()));
				} else {
					result.put("data", model);
					result.put("resCode", 1);
					result.put("resMsg", "failed to obtain pad information,the defaultchargeruleid is null");
					LOG.error("getNewPadInfo:failed to obtain pad information,the defaultchargeruleid is null:"
							+ requestBody);
					System.out.println(
							"===========getNewPadInfo,end:" + DateUtil.getNowTimeString() + "==================");
					return result;
				}

				parkingparm = new Pb_parkingparm();
				String isallowfreelongtermcarautoout = ParkingLotConfigUtil
						.GetParkingLotConfig("isallowfreelongtermcarautoout");
				parkingparm = parkingparmMapper.selectByPrimaryKey(station.getParkinglotno(),
						Integer.valueOf(isallowfreelongtermcarautoout));
				if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
					model.put("isAllowFreeLongtermCarAutoOut", Integer.parseInt(parkingparm.getParmvalue()));
				}
				
				parkingparm = new Pb_parkingparm();
				String isallowfreetemporarycarautoout = ParkingLotConfigUtil
						.GetParkingLotConfig("isallowfreetemporarycarautoout");
				parkingparm = parkingparmMapper.selectByPrimaryKey(station.getParkinglotno(),
						Integer.valueOf(isallowfreetemporarycarautoout));
				if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
					model.put("isAllowFreeTemporaryCarAutoOut", Integer.parseInt(parkingparm.getParmvalue()));
				}
				
				String isuploadvoicewhenopengate = ParkingLotConfigUtil.GetParkingLotConfig("isuploadvoicewhenopengate");
				parkingparm = new Pb_parkingparm();
				parkingparm = parkingparmMapper.selectByPrimaryKey(station.getParkinglotno(),
						Integer.valueOf(isuploadvoicewhenopengate));
				if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
					model.put("isUploadVoiceWhenOpengate", parkingparm.getParmvalue());
				}
				LOG.info("getPadInfo,isUploadVoiceWhenOpengate:" + parkingparm.getParmvalue());

				
				LOG.info("getNewPadInfo,data:" + model);
				result.put("data", model);
				result.put("resCode", 0);
				result.put("resMsg", "Succeed to obtain Pad information");
				LOG.info("getNewPadInfo:Succeed to obtain pad information:" + requestBody);
				System.out
						.println("===========getNewPadInfo,end:" + DateUtil.getNowTimeString() + "==================");
				return result;
			} else {
				result.put("data", new JSONArray());
				result.put("resCode", 1);
				result.put("resMsg", "failed to obtain pad information");
				LOG.error("getNewPadInfo:failed to obtain pad information:" + requestBody);
				System.out
						.println("===========getNewPadInfo,end:" + DateUtil.getNowTimeString() + "==================");
				return result;
			}
		} catch (Exception e) {
			result.put("data", new JSONArray());
			result.put("resCode", 2);
			result.put("resMsg", "An exception occurs for pad information");
			LOG.error("getNewPadInfo:An exception occurs for pad information:" + e.toString());
			System.out.println("===========getNewPadInfo,end:" + DateUtil.getNowTimeString() + "==================");
			return result;
		}

	}
}
