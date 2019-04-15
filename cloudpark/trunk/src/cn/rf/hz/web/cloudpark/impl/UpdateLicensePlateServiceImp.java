package cn.rf.hz.web.cloudpark.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.cloudpark.model.Tc_userinfo;
import com.reformer.cloudpark.service.ParkOutInService;
import com.reformer.datatunnel.client.DataTunnelPublishClient;

import cn.rf.hz.web.cloudpark.daoxx.Tc_channelMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_cwnuminfoMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_usercrdtmin_anomalyMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_userinfoMapper;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm_in;
import cn.rf.hz.web.cloudpark.service.UpdateLicensePlateService;
//import cn.rf.hz.web.cloudpark.moder.Tc_userinfo;
import cn.rf.hz.web.sharding.dao.Tc_usercrdtmMapper;
import cn.rf.hz.web.sharding.dao.Tc_usercrdtm_inMapper;
import cn.rf.hz.web.utils.BigDataAnalyze;
import cn.rf.hz.web.utils.DataConstants;
import cn.rf.hz.web.utils.DateUtil;
import cn.rf.hz.web.utils.StringUtil;

@Service("updateLicensePlateService")
public class UpdateLicensePlateServiceImp implements UpdateLicensePlateService {

	private final static Logger LOG = Logger.getLogger(UpdateLicensePlateServiceImp.class);

	@Autowired
	private Tc_usercrdtmMapper usercrdtmMapper;
	@Autowired
	private Tc_userinfoMapper userinfoMapper;
	@Autowired
	private Tc_channelMapper channelMapper;
	@Autowired
	private Tc_usercrdtm_inMapper usercrdtm_inMapper;
	@Autowired
	private Tc_cwnuminfoMapper cwnuminfoMapper;
	@Autowired
	Tc_usercrdtmin_anomalyMapper usercrdtmin_anomalyMapper;
	@Autowired
	private DataTunnelPublishClient dataTunnelPublishClient;

	@Autowired
	private ParkOutInService parkOutInService;

	@Autowired
	private PublicParkingService publicParkingService;

	/**
	 * 上传纠正记录
	 */
	@Override
	public JSONObject uploadCorrectRecord(String requestBody) {
		// 返回结果
		JSONObject resultJSON = new JSONObject();
		JSONObject jsocorrect = new JSONObject();
		JSONObject data = JSON.parseObject(requestBody);
		// 停车场编号
		String parkNo = data.getString("parkNo");
		JSONArray dataArrays = data.getJSONArray("data");
		ArrayList<Tc_usercrdtm> usercrdtmList = new ArrayList<Tc_usercrdtm>();
		ArrayList<Tc_usercrdtm_in> usercrdtmInList = new ArrayList<Tc_usercrdtm_in>();
		if (dataArrays != null) {
			for (int i = 0; i < dataArrays.size(); i++) {
				JSONObject object = dataArrays.getJSONObject(i);
				LOG.info("data:" + object.toJSONString());
				// 纠正前车牌
				String oldLicensePlateNumber = object.getString("oldLicensePlateNumber");
				// 纠正后车牌
				String licensePlateNumber = object.getString("licensePlateNumber");
				// 进场时间

				jsocorrect.put("oldLicensePlateNumber", oldLicensePlateNumber);
				jsocorrect.put("licensePlateNumber", licensePlateNumber);

				Date inTime = object.getDate("inTime");

				jsocorrect.put("inTime", inTime);

				// 通道Id
				String channelId = object.getString("channelId");
				// 进出场分区值
				int partitionID = getPartitionid(parkNo);
				// 进场分区值
				int partitionIDin = getPartitionidin(parkNo);
				// 获取进出场记录
				Tc_usercrdtm usercrdtm = GetUsercrdtm(parkNo, oldLicensePlateNumber, licensePlateNumber, inTime,
						channelId, partitionID);

				jsocorrect.put("record_id", usercrdtm.getRecordid());
				if (usercrdtm != null) {
					usercrdtmList.add(usercrdtm);
					// 修改进场记录
					Tc_usercrdtm_in usercrdtmIn = GetUsercrdtmIn(parkNo, licensePlateNumber, oldLicensePlateNumber,
							inTime, partitionIDin, usercrdtm.getUsername(), usercrdtm.getChargeruleid(),
							usercrdtm.getLastoutTime(), usercrdtm.getCarintype());
					if (usercrdtmIn != null) {
						usercrdtmInList.add(usercrdtmIn);
					}
				}
			}
		}
		if (usercrdtmList.isEmpty()) {
			// 返回错误提示
			resultJSON.put("parkNo", parkNo);
			resultJSON.put("sign", "");
			resultJSON.put("resCode", 2);
			resultJSON.put("resMsg", "处理上传修改车牌记录失败,usercrdtmList is null");
		} else {
			try {
				// 更新进出场记录
				usercrdtmMapper.batchUpdateUsercrdtm(usercrdtmList);
				// 更新进场记录
				if (!usercrdtmInList.isEmpty()) {
					usercrdtm_inMapper.batchUpdateUsercrdtm_in(usercrdtmInList);

					JSONArray jsonArray = new JSONArray();
					for (int p = 0; p < usercrdtmInList.size(); p++) {
						Tc_usercrdtm_in usercrdtmIn = (Tc_usercrdtm_in) usercrdtmInList.get(p);
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("carcode", usercrdtmIn.getCarcode2());
						jsonObject.put("carstyleid", usercrdtmIn.getCarstyleid());
						jsonObject.put("channelid", usercrdtmIn.getChannelid());
						jsonObject.put("chargeruleid", usercrdtmIn.getChargeruleid());
						jsonObject.put("outtime", usercrdtmIn.getCrdtm().getTime() + 100); // 被修改车牌的出场时间设置为入场时间，表示没有停留，但是考虑到客户端相等时不处理的，所以加个100ms
						jsonObject.put("parkinglotno", usercrdtmIn.getParkinglotno());
						jsonObject.put("imagepath", usercrdtmIn.getImagepath());
						jsonObject.put("partitionid", usercrdtmIn.getPartitionid());
						jsonObject.put("recordid", usercrdtmIn.getRecordid());
						jsonObject.put("username", usercrdtmIn.getUsername());
						jsonObject.put("carintype", usercrdtmIn.getCarintype());
						jsonObject.put("areaId", usercrdtmIn.getAreaId());
						jsonObject.put("isupdate", 1);
						jsonArray.add(jsonObject);
					}
					LOG.info("==========================纠正jsonArray===" + jsonArray.toJSONString());
					// long l = JedisPoolUtils.hincrBy("cloudPark_inout_flow",
					// parkNo, usercrdtmInList.size());
					long l = 0;// 增量包的序号从消费端生成，默认给个0
					BigDataAnalyze.sendMess(jsonArray, dataTunnelPublishClient, l, "out_p", parkNo,
							DataConstants.CLOUDPARK_INOUT);

					// 发消息
					// sendKafkaMes(parkNo, usercrdtmInList);
					JSONArray jsonArray2 = new JSONArray();
					for (int p = 0; p < usercrdtmInList.size(); p++) {
						Tc_usercrdtm_in usercrdtmIn = (Tc_usercrdtm_in) usercrdtmInList.get(p);
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("carcode", usercrdtmIn.getCarcode());
						jsonObject.put("carcode2", usercrdtmIn.getCarcode2());
						jsonObject.put("carstyleid", usercrdtmIn.getCarstyleid());
						jsonObject.put("channelid", usercrdtmIn.getChannelid());
						jsonObject.put("chargeruleid", usercrdtmIn.getChargeruleid());
						jsonObject.put("intime", usercrdtmIn.getCrdtm().getTime());
						jsonObject.put("lastouttime", usercrdtmIn.getLastouttime().getTime());
						jsonObject.put("parkinglotno", usercrdtmIn.getParkinglotno());
						jsonObject.put("imagepath", usercrdtmIn.getImagepath());
						jsonObject.put("partitionid", usercrdtmIn.getPartitionid());
						jsonObject.put("recordid", usercrdtmIn.getRecordid());
						jsonObject.put("username", usercrdtmIn.getUsername());
						jsonObject.put("carintype", usercrdtmIn.getCarintype());
						jsonObject.put("areaId", usercrdtmIn.getAreaId());
						jsonObject.put("isupdate", 1);
						jsonArray2.add(jsonObject);
					}
					LOG.info("==========================纠正jsonArray===" + jsonArray.toJSONString());
					// long l = JedisPoolUtils.hincrBy("cloudPark_inout_flow",
					// parkNo, usercrdtmInList.size());
					BigDataAnalyze.sendMess(jsonArray2, dataTunnelPublishClient, 0, "in_p", parkNo,
							DataConstants.CLOUDPARK_INOUT);
				}

				JSONArray jsonArray3 = new JSONArray();
				jsonArray3.add(jsocorrect);
				LOG.info("==========================纠正jsonArray3===" + jsonArray3.toJSONString());
				BigDataAnalyze.sendMess(jsonArray3, dataTunnelPublishClient, 0, "in_p", parkNo,
						DataConstants.CLOUDPARK_CORRECT);
				resultJSON.put("parkNo", parkNo);
				resultJSON.put("sign", "");
				resultJSON.put("resCode", 0);
				resultJSON.put("resMsg", "处理上传修改车牌记录成功");
			} catch (Exception ex) {
				resultJSON.put("parkNo", parkNo);
				resultJSON.put("sign", "");
				resultJSON.put("resCode", 2);
				resultJSON.put("resMsg", "处理上传修改车牌记录失败,message is error");
				ex.printStackTrace();
			}
		}
		JSONArray updateplatenoarry = new JSONArray();
		updateplatenoarry.add(jsocorrect);
		BigDataAnalyze.send_updateplateno(updateplatenoarry, dataTunnelPublishClient, parkNo);
		return resultJSON;
	}

	/**
	 * 上传无牌车纠正记录
	 */
	@Override
	public JSONObject uploadCorrectUnlicensedRecord(String requestBody) {
		// 返回结果
		JSONObject resultJSON = new JSONObject();
		LOG.info("请求Json:" + requestBody);
		JSONObject data = JSON.parseObject(requestBody);
		int count = data.getIntValue("count");
		// 停车场编号
		String parkNo = data.getString("parkNo");
		JSONArray dataArrays = data.getJSONArray("data");
		JSONArray dataArrays2 = new JSONArray();
		// 获取无牌车数据
		for (int i = 0; i < dataArrays.size(); i++) {
			JSONObject object = dataArrays.getJSONObject(i);
			String remarks1 = object.getString("remarks1");
			if ("".equals(remarks1) || remarks1 == null) {
				continue;
			}
			object.put("licensePlateNumber", remarks1);
			// 进场时间
			Date inTime = object.getDate("inTime");
			// 通道Id
			String channelId = object.getString("channelId");
			int channelIdInt = Integer.parseInt(channelId);
			// 图片路径
			// String imagePath = getImagePath(parkNo, channelIdInt, inTime, 0);
			// object.put("imagePath", imagePath);
			object.put("serialNumber", "");
			dataArrays2.add(object);
		}
		if (dataArrays2.isEmpty()) {
			// 返回错误提示
			resultJSON.put("parkNo", parkNo);
			resultJSON.put("sign", "");
			resultJSON.put("resCode", 2);
			resultJSON.put("resMsg", "处理上传修改车牌记录失败");
		} else {
			try {
				// 上传无牌车纠正数据
				JSONObject newdata = new JSONObject();
				newdata.put("count", count);
				newdata.put("parkNo", parkNo);
				newdata.put("data", dataArrays2);
				String newdataStr = newdata.toJSONString();

				System.out.println(newdataStr);

				LOG.info("上传无牌车纠正数据Json:" + newdataStr);
				parkOutInService.saveParkIn(newdataStr);
				// 返回成功信息
				resultJSON.put("parkNo", parkNo);
				resultJSON.put("sign", "");
				resultJSON.put("resCode", 0);
				resultJSON.put("resMsg", "处理上传修改车牌记录成功");

			} catch (Exception e) {
				LOG.info(e.getMessage());
				// 返回错误提示
				resultJSON.put("parkNo", parkNo);
				resultJSON.put("sign", "");
				resultJSON.put("resCode", 2);
				resultJSON.put("resMsg", "处理上传修改车牌记录失败");
			}
		}

		return resultJSON;
	}
	// public JSONObject uploadCorrectUnlicensedRecord(String requestBody) {
	// // TODO Auto-generated method stub
	//
	// // 返回结果
	// JSONObject resultJSON = new JSONObject();
	// // TODO Auto-generated method stub
	// // System.out.println(requestBody);
	// JSONObject data = JSON.parseObject(requestBody);
	// // System.out.println(data);
	// // String sign = data.getString("sign");
	// // 停车场编号
	// String parkNo = data.getString("parkNo");
	// JSONArray dataArrays = data.getJSONArray("data");
	//
	// // 已存在的进场记录集合
	// ArrayList<Tc_usercrdtm_in> oldUserCrdtmInList = new
	// ArrayList<Tc_usercrdtm_in>();
	// // 异常表记录集合
	// ArrayList<Tc_usercrdtmin_anomaly> in_anomalyList = new
	// ArrayList<Tc_usercrdtmin_anomaly>();
	// // 进出场记录集合
	// ArrayList<Tc_usercrdtm> userCrdtmList = new ArrayList<Tc_usercrdtm>();
	// // 进场记录集合
	// ArrayList<Tc_usercrdtm_in> userCrdtmInList = new
	// ArrayList<Tc_usercrdtm_in>();
	// // 停车位信息集合
	// List<Tc_cwnuminfo> cwnuminfoList = new ArrayList<Tc_cwnuminfo>();
	// if (dataArrays != null) {
	// for (int i = 0; i < dataArrays.size(); i++) {
	// JSONObject object = dataArrays.getJSONObject(i);
	// // 备注(纠正后车牌)
	// String remarks1 = object.getString("remarks1");
	// if ("".equals(remarks1) || remarks1 == null) {
	// continue;
	// }
	// // 进场时间
	// Date inTime = object.getDate("inTime");
	// // 通道Id
	// String channelId = object.getString("channelId");
	// int channelIdInt = Integer.parseInt(channelId);
	// // 图片路径
	// String imagePath = getImagePath(parkNo, channelIdInt, inTime, 0);
	// // 进出场分区值
	// int partitionID = getPartitionid(parkNo);
	// // 进场分区值
	// int partitionIDin = getPartitionidin(parkNo);
	//
	// // 查询是否已存在进场记录,如果存在则添加进场异常记录
	// Tc_usercrdtm_in oldUserCrdtmIn =
	// GetUsercrdtmInForUnlicensedRecord(partitionIDin, parkNo, remarks1);
	// if (oldUserCrdtmIn != null && oldUserCrdtmIn.getRecordid() > 0) {
	// oldUserCrdtmInList.add(oldUserCrdtmIn);
	// Tc_usercrdtmin_anomaly in_anomaly = new Tc_usercrdtmin_anomaly();
	// in_anomaly.setCarcode(oldUserCrdtmIn.getCarcode());
	// in_anomaly.setCarcolor(oldUserCrdtmIn.getCarcolor());
	// in_anomaly.setCarlabel(oldUserCrdtmIn.getCarlabel());
	// in_anomaly.setCarstyleid(oldUserCrdtmIn.getCarstyleid());
	// in_anomaly.setChannelid(oldUserCrdtmIn.getChannelid());
	// in_anomaly.setChargeruleid(oldUserCrdtmIn.getChargeruleid());
	// in_anomaly.setCrdtm(oldUserCrdtmIn.getCrdtm());
	// in_anomaly.setImagepath(oldUserCrdtmIn.getImagepath());
	// in_anomaly.setParkinglotno(oldUserCrdtmIn.getParkinglotno());
	// in_anomalyList.add(in_anomaly);
	// }
	//
	// int areaId = 0;
	// // 添加进出场记录
	// Tc_usercrdtm usercrdtm = new Tc_usercrdtm();
	// Tc_channel channelInfo = channelMapper.selectByPrimaryKey(channelIdInt);
	// if (channelInfo != null && channelInfo.getChargeruleid() > 0) {
	// usercrdtm.setChargeruleid(channelInfo.getChargeruleid());
	// areaId = channelInfo.getParkingareaid();
	// } else {
	// usercrdtm.setChargeruleid(0);
	// }
	// usercrdtm.setChannelid(channelIdInt);
	// usercrdtm.setCarcode(remarks1);
	// usercrdtm.setCarstyleid(0);
	// usercrdtm.setInorout(0);
	// usercrdtm.setCrdtm(inTime);
	// usercrdtm.setImagepath(imagePath);
	// usercrdtm.setRecordid(null);
	// usercrdtm.setIsupload(false);
	// usercrdtm.setCarcode2("");
	// usercrdtm.setParkinglotno(parkNo);
	// usercrdtm.setPartitionid(partitionID);
	// userCrdtmList.add(usercrdtm);
	//
	// // 添加进场记录
	// Tc_usercrdtm_in usercrdtmIn = new Tc_usercrdtm_in();
	// usercrdtmIn.setChargeruleid(usercrdtm.getChargeruleid());
	// usercrdtmIn.setChannelid(channelIdInt);
	// usercrdtmIn.setCarcode(remarks1);
	// usercrdtmIn.setCarstyleid(0);
	// usercrdtmIn.setCrdtm(inTime);
	// usercrdtmIn.setImagepath(imagePath);
	// usercrdtmIn.setNote(remarks1);
	// usercrdtmIn.setCarcode2("");
	// usercrdtmIn.setParkinglotno(parkNo);
	// usercrdtmIn.setPartitionid(partitionIDin);
	// userCrdtmInList.add(usercrdtmIn);
	//
	// // 停车位表更新信息
	// Tc_cwnuminfo cwnuminfo =
	// cwnuminfoMapper.queryOnebyParkinglotnoAndAreaid(parkNo, areaId);
	// if (cwnuminfo != null && cwnuminfo.getRecordid() > 0) {
	// int countcw = cwnuminfo.getCountcw();
	// int stopedcw = cwnuminfo.getStopedcw();
	// int prepcw = cwnuminfo.getPrepcw();
	// if (stopedcw < countcw) {
	// stopedcw += 1;
	// cwnuminfo.setStopedcw(stopedcw);
	// }
	// if (prepcw > 0) {
	// prepcw -= 1;
	// cwnuminfo.setPrepcw(prepcw);
	// }
	// cwnuminfoList.add(cwnuminfo);
	// }
	// }
	// }
	// if (userCrdtmList.isEmpty()) {
	// // 返回错误提示
	// resultJSON.put("parkNo", parkNo);
	// resultJSON.put("sign", "");
	// resultJSON.put("resCode", 2);
	// resultJSON.put("resMsg", "处理上传修改车牌记录失败");
	// } else {
	// try {
	// // 批量插入异常记录
	// if (!in_anomalyList.isEmpty())
	// usercrdtmin_anomalyMapper.batchInsertusercrdtmin_anomaly(in_anomalyList);
	//
	// // 批量删除异常记录对应的进场记录
	// if (!oldUserCrdtmInList.isEmpty())
	// usercrdtm_inMapper.batchDelUsercrdtm_inByRecordId(oldUserCrdtmInList);
	//
	// // 批量插入进出场记录
	// usercrdtmMapper.batchInsertUsercrdtm(userCrdtmList);
	//
	// // 批量插入进场记录
	// usercrdtm_inMapper.batchInsertUsercrdtm_in(userCrdtmInList);
	//
	// // 批量更新停车位记录
	// if (!cwnuminfoList.isEmpty())
	// cwnuminfoMapper.batchUpdateCwnuminfo(cwnuminfoList);
	//
	// // 发消息
	// sendKafkaMes(parkNo, userCrdtmInList);
	//
	// resultJSON.put("parkNo", parkNo);
	// resultJSON.put("sign", "");
	// resultJSON.put("resCode", 0);
	// resultJSON.put("resMsg", "处理上传修改车牌记录成功");
	// } catch (Exception ex) {
	// resultJSON.put("parkNo", parkNo);
	// resultJSON.put("sign", "");
	// resultJSON.put("resCode", 2);
	// resultJSON.put("resMsg", "处理上传修改车牌记录失败");
	// ex.printStackTrace();
	// }
	// }
	// return resultJSON;
	// }

	/**
	 * 获取进出场记录 for uploadCorrectUnlicensedRecord
	 */
	private Tc_usercrdtm_in GetUsercrdtmInForUnlicensedRecord(int partitionID, String parkNo, String remarks1) {
		JSONObject mapparam = new JSONObject();
		mapparam.put("partitionID", partitionID);
		mapparam.put("ParkingLotNo", parkNo);
		mapparam.put("CarCode", remarks1);
		LOG.info(mapparam);
		// 进场记录
		Tc_usercrdtm_in userCrdtmIn = usercrdtm_inMapper.queryOneByCarCodeOrderByCrdtm(mapparam);
		return userCrdtmIn;
	}

	/**
	 * 获取进出场记录 for uploadCorrectRecord
	 */
	private Tc_usercrdtm GetUsercrdtm(String parkNo, String oldLicensePlateNumber, String licensePlateNumber,
			Date inTime, String channelId, int partitionID) {
		JSONObject mapparam = new JSONObject();
		mapparam.put("ParkingLotNo", parkNo);
		mapparam.put("CarCode", oldLicensePlateNumber);
		mapparam.put("Crdtm", inTime);
		mapparam.put("ChannelID", channelId);
		mapparam.put("PartitionID", partitionID);
		LOG.info(mapparam);
		// 查询结果
		List<Object> list = usercrdtmMapper.queryByCondition(mapparam);
		if (list != null && list.size() > 0) {
			Tc_usercrdtm usercrdtm = (Tc_usercrdtm) list.get(0);
			if (usercrdtm != null && usercrdtm.getRecordid() > 0) {
				usercrdtm.setOldcarcode(usercrdtm.getCarcode());
				usercrdtm.setCarcode(licensePlateNumber);
				// 根据parkNo和licensePlateNumber,查询tc_userinfo
				Tc_userinfo userInfo = publicParkingService.getuserinfo(licensePlateNumber, parkNo,
						usercrdtm.getCrdtm(), channelId);
				usercrdtm.setUsername(userInfo != null ? userInfo.getUsername() : "临时车");
				// 获取收费规则Id
				int channelIdInt = Integer.parseInt(channelId);
				int ruleId = publicParkingService.getUserRuleId(userInfo, channelIdInt);
				usercrdtm.setChargeruleid(ruleId);
				usercrdtm.setCarintype(userInfo != null ? 1 : 0);
				// 获取最后免费出场时间
				Date lastOutTime = publicParkingService.getLastOutTime(usercrdtm);
				// 如果入场时间==最晚离场时间，判断当前车辆是否是长期车，如果是长期车，则最晚离场时间=有效期结束时间
				if (inTime.getTime() == lastOutTime.getTime()) {
					if (userInfo != null) {
						lastOutTime = userInfo.getEnddt();
					}
				}
				usercrdtm.setLastoutTime(lastOutTime);
				// 返回usercrdtm
				return usercrdtm;
			}
		}
		return null;
	}

	/**
	 * 获取场内记录 for uploadCorrectRecord
	 */
	private Tc_usercrdtm_in GetUsercrdtmIn(String parkNo, String licensePlateNumber, String oldLicensePlateNumber,
			Date inTime, int partitionID, String userName, int chargeRuleId, Date outTime, int carInType) {
		// 修改进场表的1条记录
		JSONObject mapparam2 = new JSONObject();
		mapparam2.put("PartitionID", partitionID);
		mapparam2.put("ParkingLotNo", parkNo);
		mapparam2.put("CarCode", oldLicensePlateNumber);
		mapparam2.put("Crdtm", inTime);
		List<Object> usercrdtm_inlist = usercrdtm_inMapper.queryByCondition(mapparam2);
		if (usercrdtm_inlist != null && usercrdtm_inlist.size() > 0) {
			Tc_usercrdtm_in usercrdtm_in = (Tc_usercrdtm_in) usercrdtm_inlist.get(0);
			usercrdtm_in.setCarcode2(usercrdtm_in.getCarcode());
			usercrdtm_in.setCarcode(licensePlateNumber);
			usercrdtm_in.setUsername(userName);
			usercrdtm_in.setLastouttime(outTime);
			usercrdtm_in.setChargeruleid(chargeRuleId);
			usercrdtm_in.setCarintype(carInType);
			return usercrdtm_in;
		} else {
			return null;
		}
	}

	/**
	 * 获取进出场表分区值
	 */
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

	/**
	 * 获取进场（场内）表分区值
	 */
	public int getPartitionidin(String ParkingLotNo) {
		int hasresult = StringUtil.getAsciiCode(ParkingLotNo) % 16;
		return hasresult;
	}

	public void sendKafkaMes(String ParkingLotNo, ArrayList<Tc_usercrdtm_in> usercrdtm_in) {
		JSONArray jsonArray = new JSONArray();
		LOG.info("=============usercrdtm_in.szie" + usercrdtm_in.size() + "=============");
		// long l = JedisPoolUtils.hincrBy("cloudPark_inout_flow", ParkingLotNo,
		// usercrdtm_in.size());
		long l = 0;// 增量包的序号从消费端生成，默认给个0
		LOG.info("=============l" + l + "=============");
		for (int p = 0; p < usercrdtm_in.size(); p++) {
			jsonArray.add(usercrdtm_in.get(p));
		}
		LOG.info("=============jsonArrayin" + jsonArray + "=============");
		BigDataAnalyze.sendMess(jsonArray, dataTunnelPublishClient, l, "in_p", ParkingLotNo,
				DataConstants.CLOUDPARK_INOUT);
		if (l % 10 == 0 && l != 0) {
			JSONObject mapparam = new JSONObject();
			mapparam.put("ParkingLotNo", ParkingLotNo);
			JSONArray array = queryParkState(mapparam);

			BigDataAnalyze.sendMess(jsonArray, dataTunnelPublishClient, l, "in_k", ParkingLotNo,
					DataConstants.CLOUDPARK_INOUT);
		}
	}

	/**
	 * 获取场内的信息的数据
	 */
	public JSONArray queryParkState(JSONObject mapparam) {
		LOG.info(" mapparam============" + mapparam + "==============");
		List<Object> list = this.usercrdtm_inMapper.queryTc_usercrdtm_in(mapparam);
		JSONArray array = new JSONArray(list);
		LOG.info("arrayarrayarray==========" + array + "================");
		return array;
	}

}
