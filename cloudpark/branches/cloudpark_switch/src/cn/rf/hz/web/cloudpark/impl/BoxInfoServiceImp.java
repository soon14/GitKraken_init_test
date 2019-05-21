package cn.rf.hz.web.cloudpark.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.cloudpark.daoxx.Pb_ledshowMapper;
import cn.rf.hz.web.cloudpark.daoxx.Pb_parkingboxMapper;
import cn.rf.hz.web.cloudpark.daoxx.Pb_parkingparmMapper;
import cn.rf.hz.web.cloudpark.daoxx.Pb_specialcarcodeMapper;
import cn.rf.hz.web.cloudpark.daoxx.Pb_stationMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_channelMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_ledinfoMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_parkInformationMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_parkingareaMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_parkingboxmemoryMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_recognitioncameraMapper;
import cn.rf.hz.web.cloudpark.moder.Pb_ledshow;
import cn.rf.hz.web.cloudpark.moder.Pb_parkingbox;
import cn.rf.hz.web.cloudpark.moder.Pb_parkingparm;
import cn.rf.hz.web.cloudpark.moder.Pb_specialcarcode;
import cn.rf.hz.web.cloudpark.moder.Pb_station;
import cn.rf.hz.web.cloudpark.moder.Tc_channel;
import cn.rf.hz.web.cloudpark.moder.Tc_ledinfo;
import cn.rf.hz.web.cloudpark.moder.Tc_parkingarea;
import cn.rf.hz.web.cloudpark.moder.Tc_parkingboxmemory;
import cn.rf.hz.web.cloudpark.moder.Tc_recognitioncamera;
import cn.rf.hz.web.cloudpark.service.BoxInfoService;
import cn.rf.hz.web.utils.DateUtil;
import cn.rf.hz.web.utils.ParkingLotConfigUtil;

@Service("boxinfo")
public class BoxInfoServiceImp implements BoxInfoService {
	private final static Logger LOG = Logger.getLogger(BoxInfoServiceImp.class);
	@Autowired
	private Pb_parkingboxMapper parkingboxMapper;
	@Autowired
	private Tc_channelMapper channelMapper;
	@Autowired
	private Tc_recognitioncameraMapper cameraMapper;
	@Autowired
	private Tc_ledinfoMapper ledMapper;
	@Autowired
	private Pb_stationMapper stationMapper;
	@Autowired
	private Pb_parkingparmMapper parkingparmMapper;
	@Autowired
	private Pb_ledshowMapper ledshowMapper;
	@Autowired
	private Tc_parkInformationMapper parkinginfoMapper;

	@Autowired
	private Tc_parkingareaMapper areaMapper;

	@Autowired
	private Pb_specialcarcodeMapper specialMapper;

	@Autowired
	private Tc_parkingboxmemoryMapper memoryMapper;

	@Override
	public JSONObject getBoxInfo(String requestBody) {
		// System.out.println("=================="+ParkingLotNo);
		LOG.error("getBoxInfo:requestBody,000000000000000000000");
		JSONObject result = new JSONObject();
		LOG.error("getBoxInfo:requestBody," + requestBody);
		result.put("sign", "");
		if (requestBody == null || requestBody.isEmpty()) {
			result.put("data", new JSONArray());
			result.put("resCode", 100);
			result.put("resMsg", "The method parameter is null");
			LOG.error("getBoxInfo:The method parameter is null," + requestBody);
			return result;
		}
		LOG.error("getBoxInfo:requestBody,1111111111111111");
		try {
			LOG.error("getBoxInfo:requestBody,2222222222222222");
			JSONObject data = JSON.parseObject(requestBody);// 暂时未加密
			String serialnumber = data.getString("serialNumber");
			LOG.error("getBoxInfo:requestBody,33333333333333333333");
			if (serialnumber == null || serialnumber.isEmpty()) {
				result.put("data", new JSONArray());
				result.put("resCode", 100);
				result.put("resMsg", "The serialnumber is null");
				LOG.error("getBoxInfo:The serialnumber is null," + requestBody);
				return result;
			}
			LOG.info("getBoxInfo:serialNumber," + serialnumber);
			JSONArray array = new JSONArray();
			Pb_parkingbox parkingbox = parkingboxMapper.selectBySerialNumber(serialnumber);
			if (parkingbox != null && parkingbox.getRecordid() > 0) {
				LOG.info("=====================getBoxInfo:parkingbox," + parkingbox.getRecordid());
				String parktype = parkinginfoMapper.queryParkingTypeById(parkingbox.getParkinglotno());
				List<Object> stationidlist = channelMapper.selectStationIDByParkingChannelID(parkingbox.getRecordid());
				if (stationidlist != null && stationidlist.size() > 0)

					for (int i = 0; i < stationidlist.size(); i++) {

						String stationid = ((Tc_channel) stationidlist.get(i)).getMstationno().toString();
						if (stationid != null && !stationid.isEmpty()) {
							Pb_station stationmodel = stationMapper.selectByPrimaryKey(Integer.valueOf(stationid));
							List<Tc_channel> channellist = channelMapper.selectByMStationno(stationmodel.getRecordid());
							if (channellist != null && channellist.size() > 0) {
								for (int n = 0; n < channellist.size(); n++) {
									JSONObject object = new JSONObject();
									object.put("channelId", channellist.get(n).getChannelid());
									object.put("areaId", channellist.get(n).getParkingareaid());
									object.put("padIp",
											(stationmodel.getSerialnumber() == null
													|| stationmodel.getSerialnumber().isEmpty())
															? stationmodel.getStationip()
															: "BJAN." + stationmodel.getSerialnumber());
									object.put("padSn",
											(stationmodel.getSerialnumber() == null
													|| stationmodel.getSerialnumber().isEmpty()) ? ""
															: stationmodel.getSerialnumber());

									if (channellist.get(n).getCameraid() != null
											&& channellist.get(n).getCameraid() > 0) {
										Tc_recognitioncamera camera1 = cameraMapper
												.selectByPrimaryKey(channellist.get(n).getCameraid());
										if (camera1 != null && camera1.getCameraid() > 0) {
											object.put("cameraIp1",
													(camera1.getSerialnumber() == null
															|| camera1.getSerialnumber().isEmpty())
																	? camera1.getCameraip()
																	: "BJAN." + camera1.getSerialnumber());
											object.put("cameraSn1",
													(camera1.getSerialnumber() == null
															|| camera1.getSerialnumber().isEmpty()) ? ""
																	: camera1.getSerialnumber());
										} else {
											object.put("cameraIp1", "");
											object.put("cameraSn1", "");
										}
									} else {
										object.put("cameraIp1", "");
										object.put("cameraSn1", "");
									}
									if (channellist.get(n).getCameraid2() != null
											&& channellist.get(n).getCameraid2() > 0) {
										Tc_recognitioncamera camera2 = cameraMapper
												.selectByPrimaryKey(channellist.get(n).getCameraid2());
										if (camera2 != null && camera2.getCameraid() > 0) {
											object.put("cameraIp2",
													(camera2.getSerialnumber() == null
															|| camera2.getSerialnumber().isEmpty())
																	? camera2.getCameraip()
																	: "BJAN." + camera2.getSerialnumber());
											object.put("cameraSn2",
													(camera2.getSerialnumber() == null
															|| camera2.getSerialnumber().isEmpty()) ? ""
																	: camera2.getSerialnumber());
										} else {
											object.put("cameraIp2", "");
											object.put("cameraSn2", "");
										}
									} else {
										object.put("cameraIp2", "");
										object.put("cameraSn2", "");
									}
									List<Tc_ledinfo> ledlist = ledMapper
											.selectByChannelID(channellist.get(n).getChannelid());
									if (ledlist != null && ledlist.size() > 0) {
										Tc_ledinfo ledinfo = ledlist.get(0);
										if (ledinfo != null && ledinfo.getRecordid() > 0) {
											object.put("ledIp",
													(ledinfo.getSerialnumber() == null
															|| ledinfo.getSerialnumber().isEmpty()) ? ledinfo.getLedip()
																	: "BJAN." + ledinfo.getSerialnumber());
											object.put("ledSn",
													(ledinfo.getSerialnumber() == null
															|| ledinfo.getSerialnumber().isEmpty()) ? ""
																	: ledinfo.getSerialnumber());
										}

									}
									if (channellist.get(n).getIsuploadrecord()) {
										object.put("isUploadRecord", 1);
									} else {
										object.put("isUploadRecord", 0);
									}
									if (channellist.get(n).getIssendnotice()) {
										object.put("isSendNotice", 1);
									} else {
										object.put("isSendNotice", 0);
									}

									object.put("gateId", channellist.get(n).getGateid());
									object.put("inOutState", channellist.get(n).getInorout());
									if ("1".equals(parktype)) {
										object.put("insideOrOutside", channellist.get(n).getInsideoroutside());
										if (channellist.get(n).getInorout() == 0) {
											object.put("isAllIn", channellist.get(n).getIsallin());
										} else if (channellist.get(n).getInorout() == 1) {
											object.put("isCharge", channellist.get(n).getIscharge() == true ? 1 : 0);
											object.put("nextAreaId", channellist.get(n).getNextareaid());
										}
									}
									object.put("remarks1", "");

									String channelcontrol = channellist.get(n).getChannelcontrol();
									LOG.info("channelcontrol:======" + channelcontrol + "======");
									if (channelcontrol != null && !channelcontrol.isEmpty()) {
										channelcontrol = channelcontrol.replace("true", "1").replace("false", "0");
										LOG.info("channelcontrol:======" + channelcontrol + "======");
										JSONArray controlarray = JSONArray.parseArray(channelcontrol);
										for (int c = 0; c < controlarray.size(); c++) {
											JSONObject controlobject = controlarray.getJSONObject(c);
											if ("1".equals(controlobject.getString("isAllowTemporaryCarInOut"))) {
												LOG.info("channelcontrol:======" + channelcontrol + "======");
												controlobject.put("isAllowTemporaryCarInOut", 1);
											} else {
												controlobject.put("isAllowTemporaryCarInOut", 0);
											}
											LOG.info("controlobject.getString(isAllowLongtermCarInOut):======"
													+ controlobject.getString("isAllowLongtermCarInOut") + "======");
											if ("1".equals(controlobject.getString("isAllowLongtermCarInOut"))) {
												controlobject.put("isAllowLongtermCarInOut", 1);
											} else {
												controlobject.put("isAllowLongtermCarInOut", 0);
											}
											LOG.info("controlobject.getString(isAllowWhitelistCarInOut):======"
													+ controlobject.getString("isAllowWhitelistCarInOut") + "======");
											if ("1".equals(controlobject.getString("isAllowWhitelistCarInOut"))) {
												controlobject.put("isAllowWhitelistCarInOut", 1);
											} else {
												controlobject.put("isAllowWhitelistCarInOut", 0);
											}
										}
										object.put("channelcontrol", controlarray);
									} else {
										object.put("channelcontrol", new JSONArray());
									}

									array.add(object);
								}
							}
							String parkinglotno = "";
							if (parktype == null) {
								parktype = "0";
							}
							if ("0".equals(parktype)) {
								parkinglotno = parkingbox.getParkinglotno();
								result.put("isInParkingLot", 0);
							} else if ("1".equals(parktype)) {
								result.put("isInParkingLot", 1);
								Tc_parkingarea areamodel = areaMapper.selectByPrimaryKey(parkingbox.getParkingareaid());
								if (areamodel != null && areamodel.getParentid() != null
										&& areamodel.getParentid() > 0) {
									parkinglotno = parkingbox.getParkinglotno() + "_" + parkingbox.getParkingareaid();
								} else {
									parkinglotno = parkingbox.getParkinglotno();
								}
							}
							String isuploadimage = ParkingLotConfigUtil.GetParkingLotConfig("isuploadimage");
							Pb_parkingparm parkingparm = new Pb_parkingparm();
							parkingparm = parkingparmMapper.selectByPrimaryKey(parkinglotno,
									Integer.valueOf(isuploadimage));
							if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
								result.put("isUploadImage", Integer.parseInt(parkingparm.getParmvalue()));
							}
							parkingparm = new Pb_parkingparm();
							String isallowtemporarycarin = ParkingLotConfigUtil
									.GetParkingLotConfig("isallowtemporarycarin");
							parkingparm = parkingparmMapper.selectByPrimaryKey(parkinglotno,
									Integer.valueOf(isallowtemporarycarin));
							if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
								result.put("isAllowTemporaryCarIn", Integer.parseInt(parkingparm.getParmvalue()));
							}
							String isallowcarinwhennospaces = ParkingLotConfigUtil
									.GetParkingLotConfig("isallowcarinwhennospaces");
							parkingparm = new Pb_parkingparm();
							parkingparm = parkingparmMapper.selectByPrimaryKey(parkinglotno,
									Integer.valueOf(isallowcarinwhennospaces));
							if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
								result.put("isAllowCarInWhenNoSpaces", Integer.parseInt(parkingparm.getParmvalue()));
							}
							/*
							 * String isinparkinglot =
							 * ParkingLotConfigUtil.GetParkingLotConfig(
							 * "isinparkinglot"); parkingparm = new
							 * Pb_parkingparm(); parkingparm =
							 * parkingparmMapper.selectByPrimaryKey(
							 * parkinglotno, Integer.valueOf(isinparkinglot));
							 * if (parkingparm != null &&
							 * !parkingparm.getParmvalue().isEmpty()) {
							 * result.put("isInParkingLot",
							 * Integer.parseInt(parkingparm.getParmvalue())); }
							 */
							String iscloudcharge = ParkingLotConfigUtil.GetParkingLotConfig("iscloudcharge");
							parkingparm = new Pb_parkingparm();
							parkingparm = parkingparmMapper.selectByPrimaryKey(parkinglotno,
									Integer.valueOf(iscloudcharge));
							if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
								result.put("isCloudCharge", Integer.parseInt(parkingparm.getParmvalue()));
							}
							result.put("cloudChargeUrl", ParkingLotConfigUtil.GetParkingLotConfig("cloudchargeurl"));

							result.put("controllerReportUrl",
									ParkingLotConfigUtil.GetParkingLotConfig("controllerreporturl"));

							Pb_specialcarcode carcodemodel = specialMapper.selectByParkingLotNo(parkinglotno);
							if (carcodemodel != null && carcodemodel.getRecordid() > 0) {
								if (carcodemodel.getMatchfirstcarcode() != null
										&& !carcodemodel.getMatchfirstcarcode().isEmpty()) {
									result.put("matchcarcodethefirsttwo",
											carcodemodel.getMatchfirstcarcode().split(","));

								} else {
									result.put("matchcarcodethefirsttwo", new String[0]);
								}
								if (carcodemodel.getMatchallcarcode() != null
										&& !carcodemodel.getMatchallcarcode().isEmpty()) {
									result.put("matchcarcodeall", carcodemodel.getMatchallcarcode().split(","));
								} else {
									result.put("matchcarcodeall", new String[0]);
								}
							} else {
								result.put("matchcarcodethefirsttwo",
										ParkingLotConfigUtil.GetParkingLotConfig("specialcarcode1").split(","));
								result.put("matchcarcodeall",
										ParkingLotConfigUtil.GetParkingLotConfig("specialcarcode2").split(","));
							}
							String isuploadbigimage = ParkingLotConfigUtil.GetParkingLotConfig("isuploadbigimage");
							parkingparm = new Pb_parkingparm();
							parkingparm = parkingparmMapper.selectByPrimaryKey(parkinglotno,
									Integer.valueOf(isuploadbigimage));
							if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
								result.put("isUploadBigImage", Integer.parseInt(parkingparm.getParmvalue()));
							}
							String isuploadsmallimage = ParkingLotConfigUtil.GetParkingLotConfig("isuploadsmallimage");
							parkingparm = new Pb_parkingparm();
							parkingparm = parkingparmMapper.selectByPrimaryKey(parkinglotno,
									Integer.valueOf(isuploadsmallimage));
							if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
								result.put("isUploadSmallImage", Integer.parseInt(parkingparm.getParmvalue()));
							}
							parkingparm = new Pb_parkingparm();
							String cameratriggerinterval = ParkingLotConfigUtil
									.GetParkingLotConfig("cameratriggerinterval");
							parkingparm = parkingparmMapper.selectByPrimaryKey(parkinglotno,
									Integer.valueOf(cameratriggerinterval));
							if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
								result.put("cameraTriggerInterval", Integer.parseInt(parkingparm.getParmvalue()));
							}
							parkingparm = new Pb_parkingparm();
							String isallowunlicensedcarin = ParkingLotConfigUtil
									.GetParkingLotConfig("isallowunlicensedcarin");
							parkingparm = parkingparmMapper.selectByPrimaryKey(parkinglotno,
									Integer.valueOf(isallowunlicensedcarin));
							if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
								result.put("isallowunlicensedcarin", Integer.parseInt(parkingparm.getParmvalue()));
							}
							parkingparm = new Pb_parkingparm();
							String voicealert = ParkingLotConfigUtil.GetParkingLotConfig("voicealert");
							parkingparm = parkingparmMapper.selectByPrimaryKey(parkinglotno,
									Integer.valueOf(voicealert));
							if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
								result.put("voicealert", Integer.parseInt(parkingparm.getParmvalue()));
							}
							parkingparm = new Pb_parkingparm();
							String isallowfreelongtermcarautoout = ParkingLotConfigUtil
									.GetParkingLotConfig("isallowfreelongtermcarautoout");
							parkingparm = parkingparmMapper.selectByPrimaryKey(parkinglotno,
									Integer.valueOf(isallowfreelongtermcarautoout));
							if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
								result.put("isAllowFreeLongtermCarAutoOut",
										Integer.parseInt(parkingparm.getParmvalue()));
							}

							parkingparm = new Pb_parkingparm();
							String isallowfreetemporarycarautoout = ParkingLotConfigUtil
									.GetParkingLotConfig("isallowfreetemporarycarautoout");
							parkingparm = parkingparmMapper.selectByPrimaryKey(parkinglotno,
									Integer.valueOf(isallowfreetemporarycarautoout));
							if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
								result.put("isAllowFreeTemporaryCarAutoOut",
										Integer.parseInt(parkingparm.getParmvalue()));
							}

							parkingparm = new Pb_parkingparm();
							String isallowtemporarycarinwhennospaces = ParkingLotConfigUtil
									.GetParkingLotConfig("isallowtemporarycarinwhennospaces");
							parkingparm = parkingparmMapper.selectByPrimaryKey(parkinglotno,
									Integer.valueOf(isallowtemporarycarinwhennospaces));
							if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
								result.put("isAllowTemporaryCarInWhenNoSpaces",
										Integer.parseInt(parkingparm.getParmvalue()));
							}
							parkingparm = new Pb_parkingparm();
							String islongtermcarcharge = ParkingLotConfigUtil
									.GetParkingLotConfig("islongtermcarcharge");
							parkingparm = parkingparmMapper.selectByPrimaryKey(parkingbox.getParkinglotno(),
									Integer.valueOf(islongtermcarcharge));
							if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
								result.put("IsLongtermcarCharge", Integer.parseInt(parkingparm.getParmvalue()));
							}

							parkingparm = new Pb_parkingparm();
							String isinparkingswitch = ParkingLotConfigUtil.GetParkingLotConfig("isinparkingswitch");
							parkingparm = parkingparmMapper.selectByPrimaryKey(parkinglotno,
									Integer.valueOf(isinparkingswitch));
							if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
								result.put("isInparkingSwitch", Integer.parseInt(parkingparm.getParmvalue()));
							}

							parkingparm = new Pb_parkingparm();
							String isuploadunlicensedcarimage = ParkingLotConfigUtil
									.GetParkingLotConfig("isuploadunlicensedcarimage");
							parkingparm = parkingparmMapper.selectByPrimaryKey(parkinglotno,
									Integer.valueOf(isuploadunlicensedcarimage));
							if (parkingparm != null && !parkingparm.getParmvalue().isEmpty()) {
								result.put("isUploadUnLicensedCarImage", Integer.parseInt(parkingparm.getParmvalue()));
							}

							LOG.info("=============GetBoxINfo,array:" + array + "======================");
							result.put("data", array);
							result.put("parkNo", parkinglotno);
							result.put("resCode", 0);
							result.put("resMsg", "Succeed to obtain box information");
							LOG.info("getBoxInfo:Succeed to obtain box information," + result.toJSONString());
						} else {
							result.put("data", "");
							result.put("parkNo", parkingbox.getParkinglotno());
							result.put("resCode", 1);
							result.put("resMsg", "failed to obtain box information");
							LOG.error("getBoxInfo:failed to obtain box information," + requestBody);
						}

					}
				else {
					result.put("data", new JSONArray());
					result.put("parkNo", "");
					result.put("resCode", 1);
					result.put("resMsg", "failed to obtain box information");
					LOG.error("getBoxInfo:failed to obtain box information," + requestBody);
				}
			}
		} catch (Exception e) {
			result.put("data", new JSONArray());
			result.put("parkNo", "");
			result.put("resCode", 2);
			result.put("resMsg", "An exception occurs for box information");
			LOG.error("getBoxInfo:An exception occurs for box information," + e.toString());
		}
		return result;
	}

	@Override
	public JSONObject getLedShowInfo(String requestBody) {
		JSONObject result = new JSONObject();

		result.put("sign", "");
		if (requestBody == null || requestBody.isEmpty()) {
			result.put("data", new JSONArray());
			result.put("resCode", 100);
			result.put("resMsg", "The method parameter is null");
			LOG.error("getLedShowInfo:The method parameter is null," + requestBody);
			return result;
		}
		try {
			JSONObject data = JSON.parseObject(requestBody);// 暂时未加密
			String serialnumber = data.getString("serialNumber");
			LOG.info("getLedShowInfo:serialNumber," + serialnumber);
			JSONArray array = new JSONArray();
			Pb_parkingbox parkingbox = parkingboxMapper.selectBySerialNumber(serialnumber);
			if (parkingbox != null && parkingbox.getRecordid() > 0) {
				List<Object> stationidlist = channelMapper.selectStationIDByParkingChannelID(parkingbox.getRecordid());
				if (stationidlist != null && stationidlist.size() > 0)
					for (int i = 0; i < stationidlist.size(); i++) {
						String stationid = ((Tc_channel) stationidlist.get(i)).getMstationno().toString();
						if (stationid != null && !stationid.isEmpty()) {
							Pb_station stationmodel = stationMapper.selectByPrimaryKey(Integer.valueOf(stationid));
							LOG.info("=============" + stationmodel.getRecordid() + "======================");
							List<Tc_channel> channellist = channelMapper.selectByMStationno(stationmodel.getRecordid());
							if (channellist != null && channellist.size() > 0) {

								for (int n = 0; n < channellist.size(); n++) {

									List<Tc_ledinfo> ledlist = ledMapper
											.selectByChannelID(channellist.get(n).getChannelid());
									if (ledlist != null && ledlist.size() > 0) {
										Tc_ledinfo ledmodel = ledlist.get(0);
										if (ledmodel != null && ledmodel.getRecordid() > 0) {
											List<Pb_ledshow> ledshowlist = ledshowMapper
													.selectByPid(ledmodel.getRecordid());
											for (int j = 0; j < ledshowlist.size(); j++) {
												Pb_ledshow ledshowmodel = ledshowlist.get(j);
												if (ledshowmodel != null && ledshowmodel.getRecordid() > 0) {
													JSONObject object = new JSONObject();
													object.put("ip",
															(ledmodel.getSerialnumber() == null
																	|| ledmodel.getSerialnumber().isEmpty())
																			? ledmodel.getLedip()
																			: "BJAN." + ledmodel.getSerialnumber());
													//1余位显示屏 2收费显示屏 3信息显示屏 4多条屏
													object.put("ledtype", ledmodel.getLedtype());
													
													object.put("line", ledshowmodel.getLine());
													object.put("color", ledshowmodel.getColor());
													object.put("method", ledshowmodel.getMethod());
													object.put("scrollDelay", ledshowmodel.getScrolldelay());
													object.put("stayTime", ledshowmodel.getStaytime());
													object.put("ideltime", ledshowmodel.getIdeltime());
													object.put("swdelay", ledshowmodel.getSwdelay());
													object.put("toflash", ledshowmodel.getToflash() == true ? 1 : 0);
													object.put("programId", ledshowmodel.getProgramid());
													LOG.info("=============ledshowmodel.getLedtype(),"
															+ ledshowmodel.getLedtype() + "======================");
													if (ledshowmodel.getLedtype() == 0) {
														LOG.info("=============22222222======================");
														if (ledshowmodel.getContent().indexOf(",") > -1) {
															object.put("content",
																	ledshowmodel.getContent().split(",")[0]);
															if (!ledshowmodel.getContent().split(",")[1].equals("")) {
																object.put("areaid", Integer.valueOf(
																		ledshowmodel.getContent().split(",")[1]));
															}
															if (ledshowmodel.getContent().split(",")[2].equals("")) {
																object.put("istotalspace", 0);
															} else {
																object.put("istotalspace", Integer.valueOf(
																		ledshowmodel.getContent().split(",")[2]));
															}
														} else {
															object.put("content", ledshowmodel.getContent());
															object.put("istotalspace", 0);
														}
													} else {
														LOG.info("=============111111======================");
														object.put("content", ledshowmodel.getContent());
														// object.put("istotalspace",
														// 0);
													}
													array.add(object);
												}
											}
										}
									}
								}
							}
						}
					}

			}
			result.put("data", array);
			result.put("resCode", 0);
			result.put("resMsg", "Succeed to obtain Ledshow information");
		} catch (Exception e) {
			result.put("data", new JSONArray());
			result.put("parkNo", "");
			result.put("resCode", 2);
			result.put("resMsg", "An exception occurs for Ledshow information");
			LOG.error("getLedShowInfo:An exception occurs for Ledshow information," + e.toString());
		}

		return result;
	}

	@Override
	public JSONObject getParkingParamInfo(String requestBody) {
		JSONObject result = new JSONObject();

		result.put("sign", "");
		if (requestBody == null || requestBody.isEmpty()) {
			result.put("data", new JSONArray());
			result.put("resCode", 100);
			result.put("resMsg", "The method parameter is null");
			LOG.error("getParkingParamInfo: method parameter is null," + requestBody);
			return result;
		}
		try {
			JSONObject data = JSON.parseObject(requestBody);// 暂时未加密
			String parkinglotno = data.getString("parkinglotno");
			if (parkinglotno != null && !parkinglotno.isEmpty()) {
				LOG.info("getParkingParamInfo:parkinglotno," + parkinglotno);
				JSONArray array = new JSONArray();
				List<Pb_parkingparm> list = new ArrayList<Pb_parkingparm>();

				String parkingtype = parkinginfoMapper.queryParkingTypeById(parkinglotno);
				if (parkingtype == null || parkingtype.isEmpty()) {
					parkingtype = "0";
				}
				// if ("1".equals(parkingtype)) {
				// String areaid = "";
				// List<Tc_parkingarea> arealist =
				// areaMapper.queryparkingAreaByParkingLotNo(parkinglotno);
				// for (Tc_parkingarea areamodel : arealist) {
				// if (areamodel.getParentid() != null) {
				// areaid = String.valueOf(areamodel.getAreaid());
				// break;
				// }
				// }
				// parkinglotno = parkinglotno + "_" + areaid;
				// }

				list = parkingparmMapper.selectByParkingLotNo(parkinglotno);
				for (Pb_parkingparm model : list) {
					if (model.getParmid() == 14) {
						result.put("isallowtemporarycarin", model.getParmvalue());
					}
					if (model.getParmid() == 16) {
						result.put("isallowcarinwhennospaces", model.getParmvalue());
					}
					if (model.getParmid() == 27) {
						result.put("isallowunlicensedcarin", model.getParmvalue());
					}
					if (model.getParmid() == 46) {
						result.put("defaultchargeruleid", model.getParmvalue());
					}
					if (model.getParmid() == 48) {
						result.put("iscloudcharge", model.getParmvalue());
					}
					if (model.getParmid() == 49) {
						result.put("isreductionsuperposition", model.getParmvalue());
					}
					if (model.getParmid() == 50) {
						result.put("ismultiplechargerule", model.getParmvalue());
					}
					if (model.getParmid() == 51) {
						result.put("isuploadbigimage", model.getParmvalue());
					}
					if (model.getParmid() == 52) {
						result.put("isuploadsmallimage", model.getParmvalue());
					}
					if (model.getParmid() == 53) {
						result.put("cameratriggerinterval", model.getParmvalue());
					}
					if (model.getParmid() == 56) {
						result.put("xbproductid", model.getParmvalue());
					}
					if (model.getParmid() == 57) {
						result.put("voicealert", model.getParmvalue());
					}
					if (model.getParmid() == 58) {
						result.put("isallowfreelongtermcarautoout", model.getParmvalue());
					}
					if (model.getParmid() == 59) {
						result.put("isallowfreetemporarycarAutoout", model.getParmvalue());
					}
					if (model.getParmid() == 60) {
						result.put("isallowtemporarycarinwhennospaces", model.getParmvalue());
					}
					if (model.getParmid() == 61) {
						result.put("isinparkingswitch", model.getParmvalue());
					}
					if (model.getParmid() == 62) {
						result.put("islongtermcarcharge", model.getParmvalue());
					}
				}

				result.put("isinparkinglot", parkingtype);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public JSONObject uploadBoxMemoryUsage(String requestBody) {
		JSONObject result = new JSONObject();
		try {
			JSONObject data = JSON.parseObject(requestBody);// 暂时未加密
			String parkinglotno = data.getString("parkinglotno");
			String serialnumber = data.getString("serialnumber");
			String memorysize = data.getString("memorysize");
			String memoryusedsize = data.getString("memoryusedsize");
			String memorynotusedsize = data.getString("memorynotusedsize");
			String createtime = data.getString("createtime");
			float usage = Float.parseFloat(memoryusedsize) / Float.parseFloat(memorysize);
			LOG.info("uploadBoxMemoryUsage,usage:" + usage);
			DecimalFormat df = new DecimalFormat("0.00");// 格式化小数
			String s = df.format(usage);// 返回的是String类型

			LOG.info("uploadBoxMemoryUsage,s:" + s);
			Tc_parkingboxmemory model = new Tc_parkingboxmemory();
			model.setParkinglotno(parkinglotno);
			model.setSerialnumber(serialnumber);
			model.setMemorysize(Long.parseLong(memorysize));
			model.setMemoryusedsize(Long.parseLong(memoryusedsize));
			model.setMemorynotusedsize(Long.parseLong(memorynotusedsize));
			LOG.info("uploadBoxMemoryUsage,111111:");
			model.setMemoryusage(BigDecimal.valueOf(Double.parseDouble(s)));
			double number = 1;
			BigDecimal b1 = new BigDecimal(Double.toString(number));
			LOG.info("uploadBoxMemoryUsage,b1:" + b1);
			BigDecimal b2 = new BigDecimal(Double.parseDouble(s));
			LOG.info("uploadBoxMemoryUsage,b2:" + b2);
			float notusage = b1.subtract(b2).floatValue();
			LOG.info("uploadBoxMemoryUsage,notusage:" + notusage);

			model.setMemorynotusage(BigDecimal.valueOf(notusage));
			LOG.info("uploadBoxMemoryUsage,Memorynotusage:" + BigDecimal.valueOf(notusage));
			model.setCreatetime(DateUtil.StrToDate(createtime));
			LOG.info("uploadBoxMemoryUsage,Createtime:" + DateUtil.StrToDate(createtime));
			int i = memoryMapper.insert(model);
			if (i > 0) {
				result.put("resCode", "0");
				result.put("resMsg", "upload boxmemory success");
			} else {
				result.put("resCode", "1");
				result.put("resMsg", "upload boxmemory failed");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
