package cn.rf.hz.web.cloudpark.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.BlowJobs.BJNetComm;
//import com.BlowJobs.BJNetComm;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.cloudpark.daoxx.devicebaseinfoMapper;
import cn.rf.hz.web.cloudpark.moder.Activationcode;
import cn.rf.hz.web.cloudpark.moder.Pb_parkingbox;
import cn.rf.hz.web.cloudpark.moder.Pb_station;
import cn.rf.hz.web.cloudpark.moder.Tc_channel;
import cn.rf.hz.web.cloudpark.moder.Tc_ledinfo;
import cn.rf.hz.web.cloudpark.moder.Tc_parkingarea;
import cn.rf.hz.web.cloudpark.moder.Tc_recognitioncamera;
import cn.rf.hz.web.cloudpark.moder.devicebaseinfo;
import cn.rf.hz.web.cloudpark.service.PadInfoService;
import cn.rf.hz.web.utils.DateUtil;
import cn.rf.hz.web.utils.ParkingLotConfigUtil;
import cn.rf.hz.web.utils.StringUtil;
import cn.rf.hz.web.cloudpark.daoxx.ActivationcodeMapper;
import cn.rf.hz.web.cloudpark.daoxx.Pb_parkingboxMapper;
import cn.rf.hz.web.cloudpark.daoxx.Pb_stationMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_channelMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_ledinfoMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_parkInformationMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_parkingareaMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_recognitioncameraMapper;

@Controller
@RequestMapping("/factorySetting")
public class FactorySettingController {

	@Autowired(required = false)
	private devicebaseinfoMapper devicebaseMapper;

	@Autowired(required = false)
	private Tc_parkInformationMapper parkinginfoMapper;

	@Autowired(required = false)
	private Pb_parkingboxMapper parkingboxMapper;

	@Autowired(required = false)
	private Tc_channelMapper channelMapper;

	@Autowired(required = false)
	private Pb_stationMapper stationMapper;

	@Autowired(required = false)
	private Tc_recognitioncameraMapper cameraMapper;

	@Autowired(required = false)
	private Tc_ledinfoMapper ledMapper;

	@Autowired(required = false)
	private Tc_parkingareaMapper areaMapper;

	@Autowired(required = false)
	private ActivationcodeMapper codeMapper;

	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getPadSerialNumber", method = RequestMethod.POST)
	public void getPadSerialNumber(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		System.out.println("===========getPadSerialNumber,begin:" + DateUtil.getNowTimeString() + "==================");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		JSONObject result = new JSONObject();
		int rescode = 1;
		String newSerialNumber = "";
		System.out.println("===========getPadSerialNumber,requestBody:" + requestBody + "==================");
		if (requestBody == null || requestBody.isEmpty()) {
			rescode = 100;
		} else {
			JSONObject data = JSON.parseObject(requestBody);// 暂时未加密
			String code = data.getString("code");
			System.out.println("===========getPadSerialNumber,code:" + code + "==================");
			Activationcode model = codeMapper.selectByCode(code);
			int a = 0;
			if (model != null && model.getRecordid() > 0) {
				if (model.getIsused() == 1) {
					rescode = 3;
				} else {
					model.setIsused(1);
					a = codeMapper.updateByPrimaryKey(model);
				}

			} else {
				rescode = 2;
			}
			System.out.println(
					"===========getPadSerialNumber,update activationcode status success:" + a + "==================");
			if (a > 0) {
				/*
				 * String serialNumber =
				 * devicebaseMapper.selectMaxSerialNumber("PAD0"); if
				 * (serialNumber == null || "".equals(serialNumber)) {
				 * serialNumber = "0"; }
				 */
				String parkinglotno = model.getParkinglotno();
				if (parkinglotno == null || parkinglotno.isEmpty()) {
					parkinglotno = "0";
				}
				String serialNumber = "";
				System.out.println("===========getPadSerialNumber,getmaxtestparkinglotno11111==================");
				String minParkingLotNo = ParkingLotConfigUtil.GetParkingLotConfig("minofficalparkinglotno");
				System.out.println("===========getPadSerialNumber,getmaxtestparkinglotno22222==================");
				if (minParkingLotNo == null || minParkingLotNo.isEmpty()) {
					minParkingLotNo = "1000";
				}
				String minPadSerialNumber = ParkingLotConfigUtil.GetParkingLotConfig("minofficalpadserialnumber");
				if (minPadSerialNumber == null || minPadSerialNumber.isEmpty()) {
					minPadSerialNumber = "10000";
				}
				System.out.println(
						"===========getPadSerialNumber,maxParkingLotNo:" + minParkingLotNo + "==================");
				// 正式停车场
				if (Integer.valueOf(parkinglotno) >= Integer.valueOf(minParkingLotNo)) {
					System.out.println("===========getPadSerialNumber,3333333==================");
					serialNumber = stationMapper.selectMaxSerialNumber();
					if (serialNumber != null && !serialNumber.isEmpty()) {
						if (serialNumber.length() == 32) {

							serialNumber = serialNumber.substring(8);// 截取从第八位开始到最后的字符串
							// int i = Integer.parseInt(serialNumber, 16);
							if (Integer.valueOf(serialNumber, 16) <= Integer.parseInt(minPadSerialNumber)) {
								serialNumber = minPadSerialNumber;// 16进制数字
								System.out.println("===========getPadSerialNumber:type1===================");
							} else {
								serialNumber = String
										.valueOf(Integer.toHexString(Integer.valueOf(serialNumber, 16) + 1));
								System.out.println("===========getPadSerialNumber:type2===================");
							}
						} else {
							rescode = 2;// 序列号无效
							serialNumber = "";
							System.out.println("===========getPadSerialNumber:type3===================");
						}
					} else {
						serialNumber = minPadSerialNumber;// 16进制数字
						System.out.println("===========getPadSerialNumber:type4===================");
					}
				} else {
					System.out.println("===========getPadSerialNumber,444444444==================");
					serialNumber = stationMapper.selectMaxSerialNumberByParkingLotNo(1000);// 测试停车场
					if (serialNumber != null && !serialNumber.isEmpty()) {
						if (serialNumber.length() == 32) {
							serialNumber = serialNumber.substring(8);// 截取从第八位开始到最后的字符串
							// int i = Integer.parseInt(serialNumber, 16);
							if (Integer.valueOf(serialNumber, 16) < Integer.parseInt(minPadSerialNumber) - 1) {
								serialNumber = String
										.valueOf(Integer.toHexString(Integer.valueOf(serialNumber, 16) + 1));
								System.out.println("===========getPadSerialNumber:type5===================");
							} else {
								serialNumber = "";
								System.out.println("===========getPadSerialNumber:type6===================");
							}
						} else {
							serialNumber = "";
							System.out.println("===========getPadSerialNumber:type7===================");
						}
					} else {
						serialNumber = "1";
						System.out.println("===========getPadSerialNumber:type8===================");
					}
				}
				System.out.println("===========getPadSerialNumber,serialNumber:" + serialNumber + "==================");
				if (serialNumber != null && !serialNumber.isEmpty()) {
					int i = Integer.parseInt(serialNumber, 16);// 16进制转10进制
					newSerialNumber = StringUtil.fillZero(Integer.toHexString(i).toUpperCase(), 24);// 10进制转16进制
																									// 左侧自动填充0
					newSerialNumber = StringUtil.StringToHexString("PAD0").toUpperCase() + newSerialNumber;
					Pb_station station = stationMapper.selectByPrimaryKey(model.getStationid());
					if (station != null) {
						if (station.getSerialnumber() == null || station.getSerialnumber().isEmpty()) {
							station.setSerialnumber(newSerialNumber);
							int b = stationMapper.updateByPrimaryKey(station);
							if (b > 0) {
								rescode = 0;
							} else {
								rescode = 1;
							}
						} else {
							newSerialNumber = station.getSerialnumber();
							rescode = 0;
						}
					} else {
						rescode = 0;
					}
				} else {
					rescode = 2;
				}
			}

			if (rescode == 100) {
				result.put("resCode", 100);
				result.put("resMsg", "The method parameter is null");
			} else if (rescode == 5) {
				result.put("resCode", 5);
				result.put("resMsg", "the station is null");
			} else if (rescode == 4) {
				result.put("resCode", 4);
				result.put("resMsg", "update code status failed");
			} else if (rescode == 3) {
				result.put("resCode", 3);
				result.put("resMsg", "the code is used");
			} else if (rescode == 2) {
				result.put("resCode", 2);
				result.put("resMsg", "the code is invalid");
			} else if (rescode == 1) {
				result.put("resCode", 1);
				result.put("resMsg", "Get SerialNumber Failed");
			} else if (rescode == 0) {
				byte[] bytenumber = BJNetComm.genSN32(newSerialNumber);
				newSerialNumber = StringUtil.ByteToHexString(bytenumber);
				System.out.println("===========getPadSerialNumber,newSerialNumber:" + newSerialNumber + "=====");
				result.put("SerialNumber", newSerialNumber.toUpperCase());
				result.put("resCode", 0);
				result.put("resMsg", "Get SerialNumber Success");
			}
			System.out.println("===========getPadSerialNumber,result:" + result.toJSONString() + "==================");
			if (result != null && !result.isEmpty()) {
				out.println(result.toJSONString());
			}
		}
		out.flush();
		out.close();
		System.out.println("===========getPadSerialNumber,end:" + DateUtil.getNowTimeString() + "==================");
	}

	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getParkingLotTree", method = RequestMethod.POST)
	public void getParkingLotTree(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		JSONObject result = new JSONObject();
		if (requestBody == null || requestBody.isEmpty()) {
			result.put("data", "");
			result.put("resCode", 100);
			result.put("resMsg", "The method parameter is null");
		}
		JSONObject data = JSON.parseObject(requestBody);// 暂时未加密
		String parkingLotNo = data.getString("parkingLotNo");
		System.out.println("===========getParkingLotTree,requestBody:" + requestBody + "==================");
		// 读取停车场信息
		JSONObject parlinglotobject = new JSONObject();
		String parkinglotname = parkinginfoMapper.queryparkingNameById(parkingLotNo);
		parlinglotobject.put("type", "parkinglot");
		parlinglotobject.put("id", parkingLotNo);
		parlinglotobject.put("name", parkinglotname);
		parlinglotobject.put("pId", "");
		parlinglotobject.put("serialNumber", "");
		parlinglotobject.put("isDevice", "0");

		// 读取停车场区域信息

		// JSONArray resultarray = new JSONArray();

		JSONArray areaarray = new JSONArray();
		List<Tc_parkingarea> arealist = areaMapper.queryparkingAreaByParkingLotNo(parkingLotNo);
		if (arealist != null && arealist.size() > 0) {
			for (int i = 0; i < arealist.size(); i++) {
				Tc_parkingarea areamodel = (Tc_parkingarea) arealist.get(i);
				if (areamodel != null && areamodel.getAreaid() > 0) {
					JSONObject areaobject = new JSONObject();
					areaobject.put("type", "area");
					areaobject.put("id", areamodel.getAreaid().toString());
					areaobject.put("name", areamodel.getAreaname());
					areaobject.put("pId", parkingLotNo);
					areaobject.put("serialNumber", "");
					areaobject.put("isDevice", "0");

					JSONArray boxarray = new JSONArray();
					List<Pb_parkingbox> boxlist = parkingboxMapper.selectByAreaID(areamodel.getAreaid());
					if (boxlist != null && boxlist.size() > 0) {
						for (int j = 0; j < boxlist.size(); j++) {
							Pb_parkingbox boxmodel = boxlist.get(j);
							if (boxmodel != null && boxmodel.getRecordid() > 0) {
								JSONObject boxobject = new JSONObject();
								boxobject.put("type", "box");
								boxobject.put("id", boxmodel.getRecordid().toString());
								boxobject.put("name", boxmodel.getStationname());
								boxobject.put("pId", areamodel.getAreaid().toString());
								boxobject.put("serialNumber", "");
								boxobject.put("isDevice", "1");

								JSONArray stationarray = new JSONArray();
								List<Object> stationidlist = channelMapper
										.selectStationIDByParkingChannelID(boxmodel.getRecordid());
								for (int m = 0; m < stationidlist.size(); m++) {
									String stationid = ((Tc_channel) stationidlist.get(m)).getMstationno().toString();
									if (stationid != null && !stationid.isEmpty()) {
										Pb_station stationmodel = stationMapper
												.selectByPrimaryKey(Integer.valueOf(stationid));
										if (stationmodel != null && stationmodel.getRecordid() > 0) {
											JSONObject stationobject = new JSONObject();
											stationobject.put("type", "pad");
											stationobject.put("id", stationmodel.getRecordid().toString());
											stationobject.put("name", stationmodel.getStationname());
											stationobject.put("pId", boxmodel.getRecordid().toString());
											stationobject.put("serialNumber", stationmodel.getSerialnumber());
											stationobject.put("isDevice", "1");

											JSONArray channelarray = new JSONArray();
											List<Tc_channel> channellist = channelMapper
													.selectByMStationno(stationmodel.getRecordid());
											if (channellist != null && channellist.size() > 0) {
												for (int n = 0; n < channellist.size(); n++) {
													Tc_channel channelmodel = channellist.get(n);
													if (channelmodel != null && channelmodel.getChannelid() > 0) {
														JSONObject channelobject = new JSONObject();
														channelobject.put("type", "channel");
														channelobject.put("id", channelmodel.getChannelid().toString());
														channelobject.put("name", channelmodel.getChannelname());
														channelobject.put("pId", stationmodel.getRecordid().toString());
														channelobject.put("serialNumber", "");
														channelobject.put("isDevice", "0");

														JSONArray cameraarray = new JSONArray();
														if (channelmodel.getCameraid() != null
																&& channelmodel.getCameraid() > 0) {
															Tc_recognitioncamera cameramodel = cameraMapper
																	.selectByPrimaryKey(channelmodel.getCameraid());
															if (cameramodel != null && cameramodel.getCameraid() > 0) {
																JSONObject cameraobject = new JSONObject();
																cameraobject.put("type", "cam");
																cameraobject.put("id",
																		cameramodel.getCameraid().toString());
																cameraobject.put("name", cameramodel.getCameraname());
																cameraobject.put("pId",
																		channelmodel.getChannelid().toString());
																cameraobject.put("serialNumber", "");
																cameraobject.put("isDevice", "1");
																cameraarray.add(cameraobject);
															}
														}

														if (channelmodel.getCameraid2() != null
																&& channelmodel.getCameraid2() > 0) {
															Tc_recognitioncamera cameramodel2 = cameraMapper
																	.selectByPrimaryKey(channelmodel.getCameraid2());
															if (cameramodel2 != null
																	&& cameramodel2.getCameraid() > 0) {
																JSONObject cameraobject = new JSONObject();
																cameraobject.put("type", "cam");
																cameraobject.put("id",
																		cameramodel2.getCameraid().toString());
																cameraobject.put("name", cameramodel2.getCameraname());
																cameraobject.put("pId",
																		channelmodel.getChannelid().toString());
																cameraobject.put("serialNumber", "");
																cameraobject.put("isDevice", "1");

																cameraarray.add(cameraobject);
															}
														}

														List<Tc_ledinfo> ledlist = ledMapper
																.selectByChannelID(channelmodel.getChannelid());
														if (ledlist != null && ledlist.size() > 0) {
															for (int a = 0; a < ledlist.size(); a++) {
																Tc_ledinfo ledmodel = ledlist.get(a);
																if (ledmodel != null && ledmodel.getRecordid() > 0) {
																	JSONObject ledobject = new JSONObject();
																	ledobject.put("type", "led");
																	ledobject.put("id",
																			ledmodel.getRecordid().toString());
																	ledobject.put("name", ledmodel.getLedname());
																	ledobject.put("pId",
																			channelmodel.getChannelid().toString());
																	ledobject.put("serialNumber", "");
																	ledobject.put("isDevice", "1");

																	cameraarray.add(ledobject);
																}
															}
														}
														if (cameraarray.size() > 0) {
															channelobject.put("data", cameraarray);
														}
														channelarray.add(channelobject);
													}
												}
											}
											if (channelarray.size() > 0) {
												stationobject.put("data", channelarray);
											}
											stationarray.add(stationobject);
										}
									}
								}
								if (stationarray.size() > 0) {
									boxobject.put("data", stationarray);
								}
								boxarray.add(boxobject);
								// resultarray.add(boxobject);
							}
						}
					}
					if (boxarray.size() > 0) {
						areaobject.put("data", boxarray);
					}
					areaarray.add(areaobject);
				}
			}
		}

		parlinglotobject.put("data", areaarray);

		result.put("parkNo", parkingLotNo);
		result.put("sign", "");
		result.put("resCode", 0);
		result.put("data", parlinglotobject);
		// result.put("data", resultarray);// 只传从Box开始往下的设备树
		result.put("resMsg", "Get ParkingLotTree success");
		System.out.println("===========getParkingLotTree,result:" + result.toJSONString() + "==================");
		if (result != null && !result.isEmpty()) {
			out.println(result.toJSONString());
		}
		out.flush();
		out.close();
	}

	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getDeviceSerialNumberTree", method = RequestMethod.POST)
	public void getDeviceSerialNumberTree(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		JSONObject result = new JSONObject();
		if (requestBody == null || requestBody.isEmpty()) {
			result.put("data", "");
			result.put("resCode", 100);
			result.put("resMsg", "The method parameter is null");
		}
		JSONObject data = JSON.parseObject(requestBody);// 暂时未加密
		String serialnumber = data.getString("serialNumber");

		JSONArray array = new JSONArray();
		Pb_parkingbox boxmodel = parkingboxMapper.selectBySerialNumber(serialnumber);
		if (boxmodel != null && boxmodel.getRecordid() > 0) {
			List<Object> stationidlist = channelMapper.selectStationIDByParkingChannelID(boxmodel.getRecordid());
			for (int m = 0; m < stationidlist.size(); m++) {
				String stationid = ((Tc_channel) stationidlist.get(m)).getMstationno().toString();
				if (stationid != null && !stationid.isEmpty()) {
					Pb_station stationmodel = stationMapper.selectByPrimaryKey(Integer.valueOf(stationid));
					if (stationmodel != null && stationmodel.getRecordid() > 0) {
						JSONObject stationobject = new JSONObject();
						stationobject.put("TYPE", "PAD");
						stationobject.put("SN", stationmodel.getSerialnumber());
						array.add(stationobject);
						List<Tc_channel> channellist = channelMapper.selectByMStationno(stationmodel.getRecordid());
						if (channellist != null && channellist.size() > 0) {
							for (int n = 0; n < channellist.size(); n++) {
								Tc_channel channelmodel = channellist.get(n);
								if (channelmodel != null && channelmodel.getChannelid() > 0) {
									if (channelmodel.getCameraid() != null && channelmodel.getCameraid() > 0) {
										Tc_recognitioncamera cameramodel = cameraMapper
												.selectByPrimaryKey(channelmodel.getCameraid());
										if (cameramodel != null && cameramodel.getCameraid() > 0) {
											JSONObject cameraobject = new JSONObject();
											cameraobject.put("TYPE", "CAMERA");
											if (cameramodel.getSerialnumber() == null) {
												cameraobject.put("SN", "");
											} else {
												cameraobject.put("SN", cameramodel.getSerialnumber());
											}
											array.add(cameraobject);
										}
									}
									if (channelmodel.getCameraid2() != null && channelmodel.getCameraid2() > 0) {
										Tc_recognitioncamera cameramodel2 = cameraMapper
												.selectByPrimaryKey(channelmodel.getCameraid2());
										if (cameramodel2 != null && cameramodel2.getCameraid() > 0) {
											JSONObject cameraobject = new JSONObject();
											cameraobject.put("TYPE", "CAMERA");
											if (cameramodel2.getSerialnumber() == null) {
												cameraobject.put("SN", "");
											} else {
												cameraobject.put("SN", cameramodel2.getSerialnumber());
											}
											array.add(cameraobject);
										}
									}
									List<Tc_ledinfo> ledlist = ledMapper.selectByChannelID(channelmodel.getChannelid());
									if (ledlist != null && ledlist.size() > 0) {
										for (int a = 0; a < ledlist.size(); a++) {
											Tc_ledinfo ledmodel = ledlist.get(a);
											if (ledmodel != null && ledmodel.getRecordid() > 0) {
												JSONObject ledobject = new JSONObject();
												ledobject.put("TYPE", "LED");
												ledobject.put("SN", ledmodel.getSerialnumber());
												array.add(ledobject);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		if (array.size() > 0) {
			result.put("sign", "");
			result.put("resCode", 0);
			result.put("resMsg", "Get Device SerialNumber Success");
			result.put("data", array);

		} else {
			result.put("sign", "");
			result.put("resCode", 1);
			result.put("resMsg", "The Device SerialNumber is null");
			result.put("data", array);
		}
		System.out
				.println("===========getDeviceSerialNumberTree,result:" + result.toJSONString() + "==================");
		if (result != null && !result.isEmpty()) {
			out.println(result.toJSONString());
		}
		out.flush();
		out.close();
	}

	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/uploadParkingLotTree", method = RequestMethod.POST)
	public void uploadParkingLotTree(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Content-type", "text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		JSONObject result = new JSONObject();
		if (requestBody == null || requestBody.isEmpty()) {
			result.put("data", "");
			result.put("resCode", 100);
			result.put("resMsg", "The method parameter is null");
		}
		try {
			JSONObject data = JSON.parseObject(requestBody);// 暂时未加密
			System.out.println("===========uploadParkingLotTree,requestBody:" + requestBody + "==================");
			JSONArray areaarray = data.getJSONArray("data");
			if (areaarray != null && areaarray.size() > 0) {
				for (int a = 0; a < areaarray.size(); a++) {
					System.out.println("===========areaarray:" + areaarray.size() + "," + a + "==================");
					JSONObject areaobject = areaarray.getJSONObject(a);
					JSONArray boxarray = areaobject.getJSONArray("data");
					if (boxarray != null && boxarray.size() > 0) {
						for (int b = 0; b < boxarray.size(); b++) {
							System.out
									.println("===========boxarray:" + boxarray.size() + "," + b + "==================");
							JSONObject boxobject = boxarray.getJSONObject(b);
							String boxid = boxobject.getString("id");
							String boxsn = boxobject.getString("serialNumber");
							if (boxsn != null && !boxsn.isEmpty()) {
								Pb_parkingbox parkingboxmodel = parkingboxMapper
										.selectByPrimaryKey(Integer.valueOf(boxid));
								if (parkingboxmodel != null && parkingboxmodel.getRecordid() > 0) {
									parkingboxmodel.setSerialnumber(boxsn);
									int i = parkingboxMapper.updateByPrimaryKey(parkingboxmodel);
									System.out.print("===========boxid,boxsn:" + boxid + "," + boxsn + "," + i
											+ "==================");
								}
							}
							JSONArray stationarray = boxobject.getJSONArray("data");
							if (stationarray != null && stationarray.size() > 0) {
								for (int c = 0; c < stationarray.size(); c++) {
									System.out.println("===========stationarray:" + stationarray.size() + "," + c
											+ "==================");
									JSONObject stationobject = stationarray.getJSONObject(c);
									JSONArray channelarray = stationobject.getJSONArray("data");
									if (channelarray != null && channelarray.size() > 0) {
										for (int d = 0; d < channelarray.size(); d++) {
											System.out.println("===========channelarray:" + channelarray.size() + ","
													+ d + "==================");
											JSONObject channelobject = channelarray.getJSONObject(d);
											JSONArray carmeraarray = channelobject.getJSONArray("data");
											if (carmeraarray != null && carmeraarray.size() > 0) {
												for (int e = 0; e < carmeraarray.size(); e++) {
													System.out.println("===========carmeraarray:" + carmeraarray.size()
															+ "," + e + "==================");
													JSONObject object = carmeraarray.getJSONObject(e);
													String cameraid = object.getString("id");
													String camerasn = object.getString("serialNumber");
													String type = object.getString("type");
													System.out.print("===========type:" + type + "==================");
													if (camerasn != null && !camerasn.isEmpty()) {
														if (type.equals("cam")) {
															Tc_recognitioncamera cameramodel = cameraMapper
																	.selectByPrimaryKey(Integer.valueOf(cameraid));
															if (cameramodel != null && cameramodel.getCameraid() > 0) {
																cameramodel.setSerialnumber(camerasn);
																int m = cameraMapper.updateByPrimaryKey(cameramodel);
																System.out.println("===========cvamereid,camerasn:"
																		+ cameraid + "," + camerasn + "," + m
																		+ "==================");
															}

														} else if (type.equals("led")) {
															Tc_ledinfo ledmodel = ledMapper
																	.selectByPrimaryKey(Integer.valueOf(cameraid));
															if (ledmodel != null && ledmodel.getRecordid() > 0) {
																ledmodel.setSerialnumber(camerasn);
																int m = ledMapper.updateByPrimaryKey(ledmodel);
																System.out.println("===========ledid,ledsn:" + cameraid
																		+ "," + camerasn + "," + m
																		+ "==================");
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			result.put("resCode", 0);
			result.put("data", "");
			result.put("resMsg", "upload ParkingLotTree success");
		} catch (Exception e) {
			result.put("resCode", 1);
			result.put("data", "");
			result.put("resMsg", "upload ParkingLotTree failed");
		}
		System.out.println("===========uploadParkingLotTree,result:" + result.toJSONString() + "==================");
		if (result != null && !result.isEmpty()) {
			out.println(result.toJSONString());
		}
		out.flush();
		out.close();
	}
}
