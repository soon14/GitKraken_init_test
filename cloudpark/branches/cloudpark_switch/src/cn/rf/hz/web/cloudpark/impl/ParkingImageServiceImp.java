package cn.rf.hz.web.cloudpark.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import com.aliyun.oss.common.utils.IOUtils;

import cn.rf.hz.web.cloudpark.daoxx.Tc_channelMapper;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm_in;
import cn.rf.hz.web.cloudpark.service.ParkingImageService;
import cn.rf.hz.web.sharding.dao.Tc_usercrdtm_inMapper;
import cn.rf.hz.web.utils.DateUtil;
import cn.rf.hz.web.utils.OSSConfigure;
import cn.rf.hz.web.utils.OSSUtil;
import cn.rf.hz.web.utils.StringUtil;
import cn.rf.hz.web.utils.json.JSONUtils;

@Service("ParkingImage")
public class ParkingImageServiceImp implements ParkingImageService {
	private final static Logger LOG = Logger.getLogger(ParkingImageServiceImp.class);
	@Autowired
	private Tc_usercrdtm_inMapper usercrdtminMapper;
	@Autowired
	private Tc_channelMapper channelMapper;

	@Override
	public JSONObject getSmallImageInfo(String requestBody) {
		JSONObject result = new JSONObject();
		result.put("sign", "");
		LOG.info("=============getSmallImageInfo,requestBody:" + requestBody + "=============");
		if (requestBody == null || requestBody.isEmpty()) {
			result.put("data", new JSONArray());
			result.put("resCode", 100);
			result.put("resMsg", "The method parameter is null");
			LOG.error("getSmallImageInfo:The method parameter is null," + requestBody);
			return result;
		}
		JSONObject data = JSON.parseObject(requestBody);// 暂时未加密
		String parkNo = data.getString("parkNo");
		String licensePlateNumber = data.getString("licensePlateNumber");
		if (parkNo == null || parkNo.isEmpty()) {
			result.put("data", new JSONArray());
			result.put("resCode", 100);
			result.put("resMsg", "The parkNo is null");
			LOG.error("getSmallImageInfo:The parkNo is null," + requestBody);
			return result;
		}
		if (licensePlateNumber == null || licensePlateNumber.isEmpty()) {
			result.put("data", new JSONArray());
			result.put("resCode", 100);
			result.put("resMsg", "The licensePlateNumber is null");
			LOG.error("getSmallImageInfo:The licensePlateNumber is null," + requestBody);
			return result;
		}

		result.put("sign", "");
		result.put("parkNo", parkNo);
		Integer Partitionid = getPartitionidin(parkNo);
		Map<String, Object> param = new HashMap<String, Object>();
		try {
			List<Tc_usercrdtm_in> usercrdtmin = new ArrayList<Tc_usercrdtm_in>();
			if (!licensePlateNumber.equals("无牌车")) {
				if (licensePlateNumber != null && licensePlateNumber.length() > 0) {
					if (licensePlateNumber.length() <= 6) {
						StringBuilder sbcarcode = new StringBuilder();
						for (int i = 0; i < licensePlateNumber.length(); i++) {
							sbcarcode.append(String.valueOf(licensePlateNumber.charAt(i)));
							if (i != licensePlateNumber.length() - 1) {
								sbcarcode.append("%");
							}
						}
						// usercrdtmin =
						// usercrdtminMapper.selectByParknoAndCarCode(parkNo,
						// sbcarcode.toString());
						param.put("parkinglotno", parkNo);
						param.put("carcode", "%" + sbcarcode + "%");
						param.put("partitionid", Partitionid);
						usercrdtmin = usercrdtminMapper.selectByParknoAndCarCode(param);
					} else {
						// String reg = "[\u4e00-\u9fa5]";
						// Pattern pat = Pattern.compile(reg);
						// Matcher mat = pat.matcher(licensePlateNumber);
						// String newcarcode = mat.replaceAll("");

						// 车牌大于6时 截取车牌后6位进行like查询
						String newcarcode = licensePlateNumber.substring(licensePlateNumber.length() - 6,
								licensePlateNumber.length());
						// usercrdtmin =
						// usercrdtminMapper.selectByParknoAndCarCode(parkNo,
						// newcarcode);
						param = new HashMap<String, Object>();
						param.put("parkinglotno", parkNo);
						param.put("carcode", "%" + newcarcode + "%");
						param.put("partitionid", Partitionid);
						usercrdtmin = usercrdtminMapper.selectByParknoAndCarCode(param);
						if (usercrdtmin != null && usercrdtmin.size() > 0) {

						} else {
							param = new HashMap<String, Object>();
							param.put("parkinglotno", parkNo);
							param.put("partitionid", Partitionid);
							// 后6位截取进行5位车牌模糊查询
							String carcode1 = "_" + newcarcode.substring(1, 6);
							param.put("carcode1", "%" + carcode1 + "%");
							String carcode2 = newcarcode.substring(0, 1) + "_" + newcarcode.substring(2, 6);
							param.put("carcode2", "%" + carcode2 + "%");
							String carcode3 = newcarcode.substring(0, 2) + "_" + newcarcode.substring(3, 6);
							param.put("carcode3", "%" + carcode3 + "%");
							String carcode4 = newcarcode.substring(0, 3) + "_" + newcarcode.substring(4, 6);
							param.put("carcode4", "%" + carcode4 + "%");
							String carcode5 = newcarcode.substring(0, 4) + "_" + newcarcode.substring(5, 6);
							param.put("carcode5", "%" + carcode5 + "%");
							String carcode6 = newcarcode.substring(0, 5) + "_";
							param.put("carcode6", "%" + carcode6 + "%");

							usercrdtmin = usercrdtminMapper.selectByParknoAndCarCode1(param);
							if (usercrdtmin != null && usercrdtmin.size() > 0) {
							} else {
								carcode1 = "";
								carcode2 = "";
								carcode3 = "";
								carcode4 = "";
								carcode5 = "";
								carcode6 = "";
								param = new HashMap<String, Object>();
								param.put("parkinglotno", parkNo);
								param.put("partitionid", Partitionid);
								// 截取后5位车牌进行4位车牌模糊匹配
								newcarcode = licensePlateNumber.substring(licensePlateNumber.length() - 5,
										licensePlateNumber.length());
								param.put("carcode1", "%" + newcarcode + "%");
								carcode1 = "_" + newcarcode.substring(1, 5);
								param.put("carcode2", "%" + carcode1 + "%");
								carcode2 = newcarcode.substring(0, 1) + "_" + newcarcode.substring(2, 5);
								param.put("carcode3", "%" + carcode2 + "%");
								carcode3 = newcarcode.substring(0, 2) + "_" + newcarcode.substring(3, 5);
								param.put("carcode4", "%" + carcode3 + "%");
								carcode4 = newcarcode.substring(0, 3) + "_" + newcarcode.substring(4, 5);
								param.put("carcode5", "%" + carcode4 + "%");
								carcode5 = newcarcode.substring(0, 4) + "_";
								param.put("carcode6", "%" + carcode5 + "%");

								// usercrdtmin =
								// usercrdtminMapper.selectByParknoAndCarCode1(parkNo,
								// carcode1, carcode2,carcode3, carcode4,
								// carcode5,
								// newcarcode);
								usercrdtmin = usercrdtminMapper.selectByParknoAndCarCode1(param);
								if (usercrdtmin != null && usercrdtmin.size() > 0) {
								} else {
									carcode1 = "";
									carcode2 = "";
									carcode3 = "";
									carcode4 = "";
									carcode5 = "";
									carcode6 = "";
									String carcode7 = "";
									String carcode8 = "";
									String carcode9 = "";
									String carcode10 = "";

									param = new HashMap<String, Object>();
									param.put("parkinglotno", parkNo);
									param.put("partitionid", Partitionid);

									// 截取后5位车牌进行3位车牌模糊匹配
									carcode1 = "__" + newcarcode.substring(2, 5);
									param.put("carcode1", "%" + carcode1 + "%");
									carcode2 = "_" + newcarcode.substring(0, 1) + "_" + newcarcode.substring(3, 5);
									param.put("carcode2", "%" + carcode2 + "%");
									carcode3 = "_" + newcarcode.substring(0, 2) + "_" + newcarcode.substring(4, 5);
									param.put("carcode3", "%" + carcode3 + "%");
									carcode4 = "_" + newcarcode.substring(0, 3) + "_";
									param.put("carcode4", "%" + carcode4 + "%");
									carcode5 = newcarcode.substring(0, 1) + "__" + newcarcode.substring(3, 5);
									param.put("carcode5", "%" + carcode5 + "%");
									carcode6 = newcarcode.substring(0, 1) + "_" + newcarcode.substring(2, 3) + "_"
											+ newcarcode.substring(4, 5);
									param.put("carcode6", "%" + carcode6 + "%");
									carcode7 = newcarcode.substring(0, 1) + "_" + newcarcode.substring(2, 4) + "_";
									param.put("carcode7", "%" + carcode7 + "%");
									carcode8 = newcarcode.substring(0, 2) + "__" + newcarcode.substring(4, 5);
									param.put("carcode8", "%" + carcode8 + "%");
									carcode9 = newcarcode.substring(0, 2) + "_" + newcarcode.substring(2, 3) + "_";
									param.put("carcode9", "%" + carcode9 + "%");
									carcode10 = newcarcode.substring(0, 3) + "__";
									param.put("carcode10", "%" + carcode10 + "%");
									// usercrdtmin =
									// usercrdtminMapper.selectByParknoAndCarCode2(parkNo,
									// carcode1, carcode2,carcode3, carcode4,
									// carcode5, carcode6, carcode7, carcode8,
									// carcode9,carcode10);
									usercrdtmin = usercrdtminMapper.selectByParknoAndCarCode2(param);
								}
							}
						}
					}
				} else {
					// 车牌为空时，默认返回最近4条进场记录
					param = new HashMap<String, Object>();
					param.put("parkinglotno", parkNo);
					param.put("partitionid", Partitionid);
					param.put("offset", 4);
					usercrdtmin = usercrdtminMapper.selectByParknoAndSize(param);
				}
			} else {
				param = new HashMap<String, Object>();
				param.put("parkinglotno", parkNo);
				param.put("partitionid", Partitionid);
				param.put("carcode", "'%无牌车%'");
				usercrdtmin = usercrdtminMapper.selectCarInNoLincenPlateNumber(param);

				LOG.info("=============getSmallImageInfo," + usercrdtmin.size() + "=============");
				LOG.error("=============getSmallImageInfo," + usercrdtmin.size() + "=============");
			}
			LOG.info("=============getSmallImageInfo,匹配到的在场车辆集合:" + JSONUtils.toJSONString(usercrdtmin)
					+ "=============");

			if (usercrdtmin != null && usercrdtmin.size() > 0) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (Tc_usercrdtm_in s : usercrdtmin) {
					map.put(s.getCarcode(), s);
				}
				List<Tc_usercrdtm_in> usercrdtmin1 = new ArrayList<Tc_usercrdtm_in>();
				for (String key : map.keySet()) {
					Tc_usercrdtm_in m = (Tc_usercrdtm_in) map.get(key);
					if (m != null && m.getRecordid() > 0) {
						usercrdtmin1.add(m);
					}
				}
				LOG.info("==========getSmallImageInfo:11111111================");
				usercrdtmin = usercrdtmin1;
				JSONArray list = new JSONArray();
				for (int i = 0; i < usercrdtmin.size(); i++) {
					JSONObject model = new JSONObject();
					// 图片有效时长1800秒即半小时
					String newImagePath = "";
					if (usercrdtmin.get(i).getCarcode().contains("无牌车")) {
						newImagePath = OSSUtil.getUrlSigned(usercrdtmin.get(i).getImagepath() + "-2.jpg", 1800);
					} else {
						newImagePath = OSSUtil.getUrlSigned(usercrdtmin.get(i).getImagepath() + "-1.jpg", 1800);
					}
					model.put("imagePath", newImagePath.replace("http://cloudpark.oss-cn-hangzhou.aliyuncs.com",
							"http://cloud.parking24.cn:9090").replace("http://cloudpark.vpc100-oss-cn-hangzhou.aliyuncs.com",
									"http://cloud.parking24.cn:9090"));
					model.put("licensePlateNumber", usercrdtmin.get(i).getCarcode());
					model.put("channelId", usercrdtmin.get(i).getChannelid());
					model.put("inTime",
							(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(usercrdtmin.get(i).getCrdtm()));
					list.add(model);
				}
				LOG.info("==========getSmallImageInfo:11111111================");
				result.put("data", list);
				result.put("resCode", 0);
				result.put("resMsg", "Succeed to obtain InCarImage information");
				LOG.info("==========getSmallImageInfo:Succeed to obtain InCarImage information," + requestBody
						+ "================");
			} else {
				result.put("data", new JSONArray());
				result.put("resCode", 0);
				result.put("resMsg", "Succeed to obtain InCarImage information,But the information is null");
				LOG.error(
						"===========getSmallImageInfo:Failed to obtain InCarImage information,the information is null,"
								+ requestBody + "=============");
			}
		} catch (Exception e) {
			result.put("data", new JSONArray());
			result.put("resCode", 2);
			result.put("resMsg", "An exception occurs for InCarImage information");
			LOG.error("==============getSmallImageInfo:An exception occurs for InCarImage information," + e.toString()
					+ "===================");
		}
		return result;
	}

	@Override
	public JSONObject getAliyunImageUrl(String requestBody) {
		JSONObject result = new JSONObject();
		LOG.info("=============getAliyunImageUrl,requestBody:" + requestBody + "=============");
		JSONObject data = JSON.parseObject(requestBody);// 暂时未加密
		String inPath = data.getString("inPath");
		String outPath = data.getString("outPath");
		if (inPath != null && !inPath.isEmpty()) {
			try {

				String ImageUrl = OSSUtil.getUrlSigned(inPath + "-2.jpg", 1800);
				if (ImageUrl != null && !ImageUrl.isEmpty()) {
					result.put("inUrl", ImageUrl.replace("http://cloudpark.oss-cn-hangzhou.aliyuncs.com",
							"http://cloud.parking24.cn:9090").replace("http://cloudpark.vpc100-oss-cn-hangzhou.aliyuncs.com",
									"http://cloud.parking24.cn:9090"));
					LOG.info("=============getAliyunImageUrl,inUrl:" + ImageUrl + "=============");
				} else {
					result.put("inUrl", "");
				}
			} catch (Exception ex) {
				result.put("inUrl", "");
				LOG.error("=============getAliyunImageUrl,inUrl:" + ex.toString() + "=============");
			}
		} else {
			result.put("inUrl", "");
		}
		if (outPath != null && !outPath.isEmpty()) {
			try {

				String ImageUrl = OSSUtil.getUrlSigned(outPath + "-2.jpg", 1800);
				if (ImageUrl != null && !ImageUrl.isEmpty()) {
					result.put("outUrl", ImageUrl.replace("http://cloudpark.oss-cn-hangzhou.aliyuncs.com",
							"http://cloud.parking24.cn:9090").replace("http://cloudpark.vpc100-oss-cn-hangzhou.aliyuncs.com",
									"http://cloud.parking24.cn:9090"));
					LOG.info("=============getAliyunImageUrl,outUrl:" + ImageUrl + "=============");
				} else {
					result.put("outUrl", "");
				}
			} catch (Exception ex) {
				result.put("outUrl", "");
				LOG.error("=============getAliyunImageUrl,outUrl:" + ex.toString() + "=============");
			}
		} else {
			result.put("outUrl", "");
		}
		LOG.info("=============getAliyunImageUrl,result:" + result.toJSONString() + "=============");
		return result;
	}

	@Override
	public JSONObject uploadImageToAliyun(String requestBody) {
		JSONObject result = new JSONObject();
		try {
			LOG.info("=============uploadImageToAliyun,requestBody:" + requestBody.substring(0, 100) + "=============");
			JSONObject data = JSON.parseObject(requestBody);// 暂时未加密
			String ParkingLotNo = data.getString("parkNo");
			JSONArray dataArrays = data.getJSONArray("data");
			for (int i = 0; i < dataArrays.size(); i++) {
				JSONObject object = dataArrays.getJSONObject(i);
				// String licensePlateNumber =
				// String.valueOf(object.getString("licensePlateNumber"));
				String eventTime = DateUtil.StringLongFomateDate(object.getString("eventTime"));
				String channelId = String.valueOf(object.getString("channelId"));
				String bigImage = String.valueOf(object.getString("bigImage"));
				String smallImage = String.valueOf(object.getString("smallImage"));
				String InOrOut = String.valueOf(object.getString("InOrOut"));
				String userid = object.containsKey("userid") ? object.getString("userid") : "";
				String inoutid = object.containsKey("inOutId") ? object.getString("inOutId") : "";
				// String remarks1 =
				// String.valueOf(object.getString("remarks1"));
				String imageName = "";
				if (inoutid != null && !inoutid.isEmpty()) {
					imageName = inoutid;
				} else if (userid != null && !userid.isEmpty()) {
					imageName = userid;
				} else {
					imageName = ParkingLotNo + "_" + channelId + "_" + eventTime;
				}
				String imagePath = "";
				// 进场
				if (InOrOut.equals("0")) {
					imagePath = OSSConfigure.getInstance().getParkingEntranceDir().replace("//", "/");
					LOG.info("=============uploadImageToAliyun,imagePath0:" + imagePath + "=============");
				} else {
					imagePath = OSSConfigure.getInstance().getParkingDepartureDir().replace("//", "/");
					LOG.info("=============uploadImageToAliyun,imagePath1:" + imagePath + "=============");
				}

				imageName = imagePath + ParkingLotNo + "/" + DateUtil.formatYearMonth(new Date()) + "/" + imageName;
				LOG.info("=============uploadImageToAliyun,imageName1:" + imageName + "=============");
				// 上传小图
				if (smallImage != null && !smallImage.isEmpty()) {
					InputStream smallStream = StringUtil.convertStringToStream(smallImage);
					int m = OSSUtil.putFileSinged(smallStream, imageName + "-1.jpg");
					LOG.info("=============uploadImageToAliyun,uploadsmallresultm:" + m + "=============");
				}
				// 上传大图
				if (bigImage != null && !bigImage.isEmpty()) {
					InputStream bigStream = StringUtil.convertStringToStream(bigImage);
					int n = OSSUtil.putFileSinged(bigStream, imageName + "-2.jpg");
					LOG.info("=============uploadImageToAliyun,uploadsmallresultn:" + n + "=============");
				}

				result.put("parkNo", ParkingLotNo);
				result.put("sign", "");
				result.put("resCode", 0);
				result.put("resMsg", "success");
			}
		} catch (Exception e) {
			result.put("parkNo", "");
			result.put("sign", "");
			result.put("resCode", 1);
			result.put("resMsg", "fail");
		}
		return result;
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

	public int getPartitionidin(String ParkingLotNo) {
		int hasresult = StringUtil.getAsciiCode(ParkingLotNo) % 16;
		return hasresult;
	}

	@Override
	public JSONObject batchGetSmallImagePath(String requestBody) {
		JSONObject result = new JSONObject();
		LOG.info("=============batchGetSmallImagePath,requestBody:" + requestBody + "=============");
		if (requestBody == null || requestBody.isEmpty()) {
			result.put("data", new JSONArray());
			result.put("resCode", 100);
			result.put("resMsg", "The method parameter is null");
			LOG.error("batchGetSmallImagePath:The method parameter is null," + requestBody);
			return result;
		}
		JSONObject data = JSON.parseObject(requestBody);// 暂时未加密
		String imagePaths = data.getString("imagePath");
		String type = data.getString("type");
		if (type == null || type.isEmpty()) {
			type = "1";// 默认小图
		}
		if (imagePaths == null || imagePaths.isEmpty()) {
			result.put("data", new JSONArray());
			result.put("resCode", 100);
			result.put("resMsg", "The imagePath is null");
			LOG.error("batchGetSmallImagePath:The imagePath is null," + requestBody);
			return result;
		}
		String iamgeUrls = "";
		try {
			String[] arraypath = imagePaths.split(",");
			for (int i = 0; i < arraypath.length; i++) {
				String ImageUrl = OSSUtil.getUrlSigned(arraypath[i] + "-" + type + ".jpg", 1800);
				if (ImageUrl != null && !ImageUrl.isEmpty()) {
					iamgeUrls += ImageUrl.replace("http://cloudpark.oss-cn-hangzhou.aliyuncs.com",
							"http://cloud.parking24.cn:9090").replace("http://cloudpark.vpc100-oss-cn-hangzhou.aliyuncs.com",
									"http://cloud.parking24.cn:9090");
					LOG.info("=============batchGetSmallImagePath,ImageUrl:" + ImageUrl + "=============");
				}
				if (i != arraypath.length - 1) {
					iamgeUrls += ",";
				}
			}

		} catch (Exception ex) {
			result.put("inUrl", "");
			LOG.error("=============batchGetSmallImagePath,ImageUrl:" + ex.toString() + "=============");
		}
		result.put("ImageUrl", iamgeUrls);
		LOG.info("=============batchGetSmallImagePath,result:" + result.toJSONString() + "=============");
		return result;
	}
	
	@Override
	public JSONObject uploadVoiceToAliyun(String requestBody) {
		JSONObject result = new JSONObject();
		try {
			LOG.info("=============uploadVoiceToAliyun,requestBody:" + requestBody.substring(0, 100) + "=============");
			JSONObject data = JSON.parseObject(requestBody);// 暂时未加密
			String ParkingLotNo = data.getString("parkNo");
			JSONArray dataArrays = data.getJSONArray("data");
			for (int i = 0; i < dataArrays.size(); i++) {
				JSONObject object = dataArrays.getJSONObject(i);
				// String licensePlateNumber =
				// String.valueOf(object.getString("licensePlateNumber"));
				String eventTime = DateUtil.StringLongFomateDate(object.getString("eventTime"));
				String channelId = String.valueOf(object.getString("channelId"));
				String voice = String.valueOf(object.getString("voice"));
				String voiceName = "";
				voiceName = ParkingLotNo + "_" + channelId + "_" + eventTime + "_" + i;
				String voicePath = "";

				voicePath = OSSConfigure.getInstance().getParkingOpengateVoiceDir().replace("//", "/");
				LOG.info("=============uploadVoiceToAliyun,voicePath:" + voicePath + "=============");

				voiceName = voicePath + ParkingLotNo + "/" + DateUtil.formatYearMonth(new Date()) + "/" + voiceName;
				LOG.info("=============uploadVoiceToAliyun,voiceName:" + voiceName + "=============");
				// 上传语音
				if (voice != null && !voice.isEmpty()) {
					InputStream voiceStream = StringUtil.convertStringToStream(voice);
					int m = OSSUtil.putFileSinged(voiceStream, voiceName + ".mp3");
					LOG.info("=============uploadVoiceToAliyun,uploadvoiceresultm:" + m + "=============");
				}
				result.put("parkNo", ParkingLotNo);
				result.put("sign", "");
				result.put("resCode", 0);
				result.put("resMsg", "success");
			}
		} catch (Exception e) {
			result.put("parkNo", "");
			result.put("sign", "");
			result.put("resCode", 1);
			result.put("resMsg", "fail");
			e.printStackTrace();
		}
		return result;
	}
}
