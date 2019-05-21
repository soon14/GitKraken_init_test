package cn.rf.hz.web.cloudpark.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.cloudpark.model.Tc_userinfo;
import com.reformer.cloudpark.service.ParkOutInService;
import com.reformer.cloudpark.service.ParkingInformation;
import com.reformer.cloudpark.service.Tc_userinfoService;
import com.reformer.datatunnel.client.DataTunnelPublishClient;
import com.reformer.sharding.sequence.Sequence;
import com.reformer.sharding.sequence.exception.SequenceException;

import cn.rf.hz.web.cloudpark.daoxx.Pb_stationMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_channelMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_charge_jmMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_charge_jmrecordMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_usercrdtmin_anomalyMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_usercrdtmout_anomalyMapper;
import cn.rf.hz.web.cloudpark.moder.Tc_channel;
import cn.rf.hz.web.cloudpark.moder.Tc_charge_jm;
import cn.rf.hz.web.cloudpark.moder.Tc_charge_jmrecord;
import cn.rf.hz.web.cloudpark.moder.Tc_chargerecordinfo;
import cn.rf.hz.web.cloudpark.moder.Tc_opengaterecord;
import cn.rf.hz.web.cloudpark.moder.Tc_parkingarea;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm_in;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtmin_anomaly;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtmout_anomaly;
import cn.rf.hz.web.cloudpark.service.Tc_channelService;
import cn.rf.hz.web.sharding.dao.Tc_chargerecordinfoMapper;
import cn.rf.hz.web.sharding.dao.Tc_usercrdtmMapper;
import cn.rf.hz.web.sharding.dao.Tc_usercrdtm_inMapper;
//import cn.rf.hz.web.cloudpark.moder.Tc_userinfo;
import cn.rf.hz.web.utils.BigDataAnalyze;
import cn.rf.hz.web.utils.DataConstants;
import cn.rf.hz.web.utils.DateUtil;
import cn.rf.hz.web.utils.StringUtil;
import cn.rf.hz.web.utils.ThreadLocalDateUtil;

@Service("parkOutInService")
public class ParkOutInServiceImp implements ParkOutInService {

	private static long flowInterval = 100000;

	@Autowired
	private ParkingInformation parkingInformation;

	/**
	 * 获取场内的信息的数据
	 */
	@Override
	public JSONArray queryParkState(JSONObject mapparam) {
		LOG.info(" mapparam============" + mapparam + "==============");
		List<Object> list = this.usercrdtm_inMapper.queryTc_usercrdtm_in(mapparam);
		JSONArray array = new JSONArray(list);
		LOG.info("arrayarrayarray==========" + array + "================");
		return array;
	}

	@Override
	public String parkingState(JSONObject mapparam) {
		LOG.info(" mapparam============" + mapparam + "==============");
		// List<Object> list =
		// this.usercrdtm_inMapper.queryTc_usercrdtm_in(mapparam);
		JSONArray array = queryParkState(mapparam);
		// 把messge 某些key 修正
		array = BigDataAnalyze.fixArray(array, "in");
		LOG.info("arrayarrayarray==========" + array + "================");
		return array.toJSONString();
	}

	private final static Logger LOG = Logger.getLogger(ParkOutInServiceImp.class);
	@Autowired
	Tc_chargerecordinfoMapper chargerecordinfoMapper;

	@Autowired
	private Tc_usercrdtmMapper usercrdtmMapper;

	@Autowired
	private Tc_usercrdtmout_anomalyMapper usercrdtmout_anomalyMapper;
	@Autowired
	private DataTunnelPublishClient dataTunnelPublishClient;

	@Autowired
	private Sequence sequencEntrance;

	@Autowired
	private Tc_usercrdtm_inMapper usercrdtm_inMapper;

	@Autowired(required = false)
	private Tc_userinfoService userinfoService;
	@Autowired(required = false)
	Tc_channelMapper channelmapper;

	@Autowired(required = false)
	private Tc_channelService channelService;

	@Autowired(required = false)
	Tc_usercrdtmin_anomalyMapper usercrdtmin_anomalyMapper;

	@Autowired(required = false)
	PublicParkingService publicparkingservice;

	@Autowired(required = false)
	private Pb_stationMapper stationMapper;

	@Autowired
	private Tc_charge_jmMapper jmMapper;

	@Autowired
	private Tc_charge_jmrecordMapper jmrecordMapper;

	/**
	 * 车辆入场信息上传
	 */
	@SuppressWarnings("finally")
	@Override
	public String saveParkIn(String requestBody) {
		// 入场数据集合
		// 入场list
		JSONObject reMsg = new JSONObject();
		int resCode = 0;

		try {

			JSONObject data = JSON.parseObject(requestBody);
			String ParkingLotNo = data.getString("parkNo");
			reMsg.put("parkNo", ParkingLotNo);
			reMsg.put("sign", "");
			reMsg.put("resCode", resCode);
			reMsg.put("resMsg", "success");

			ArrayList<Tc_usercrdtm> usercrdtmlist = setTc_usercrdtm(requestBody);
			LOG.info("====================" + usercrdtmlist + "================");
			// usercrdtmlist 每一辆车去场内表查询，返回一个集合，
			// 如果集合不为空，需要把集合里面的场内车移动到异常表去Tc_usercrdtmin_anomaly
			// 如果为空，则usercrdtmlist的信息再写一份到场内表里面去
			// 保存出入场表信息
			// 保存场内信息
			// 保存场内异常信息
			// 还要更新管理数据库的车场信息表的，已停车位和未停车位（未完成）
			// 还要判断2车位和3辆车
			// 还要新加一个字段算出每辆免费出场时间，入场时间加上每个车场的免费停车时间
			// 保存完成，发消息，发增量和全量消息
			// 异常list
			ArrayList<Tc_usercrdtmin_anomaly> anomalylist = setAnomalylist(usercrdtmlist);
			LOG.info("====================anomalylist" + anomalylist + "================");
			LOG.info("====================anomalylist.szie" + anomalylist.size() + "================");
			// this.usercrdtmMapper.batchInsertUsercrdtm(usercrdtmlist);
			if (anomalylist.size() > 0) {
				// 假设有异常就插入异常表
				this.usercrdtmin_anomalyMapper.batchInsertusercrdtmin_anomaly(anomalylist);
			}
			// 场内list
			ArrayList<Tc_usercrdtm_in> usercrdtmin = setUsercrdtmin(usercrdtmlist);

			this.usercrdtmMapper.batchInsertUsercrdtm(usercrdtmlist);
			this.usercrdtm_inMapper.batchInsertUsercrdtm_in(usercrdtmin);
			sendKafkaMes(ParkingLotNo, usercrdtmin, "in_p");

		} catch (Exception e) {
			reMsg.put("resCode", -1);
			reMsg.put("resMsg", "fail");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			return reMsg.toJSONString();
		}

	}

	/**
	 * 车辆出场信息上传
	 */
	@Override
	public String saveParkOut(String requestBody) {
		LOG.info("====================WELCOME================");
		LOG.info("====================requestBody" + requestBody + "================");
		JSONObject reMsg = new JSONObject();
		int resCode = 0;

		JSONObject data = JSON.parseObject(requestBody);
		String ParkingLotNo = data.getString("parkNo");
		reMsg.put("parkNo", ParkingLotNo);
		reMsg.put("sign", "");
		reMsg.put("resCode", resCode);
		reMsg.put("resMsg", "success");
		try {

			// 新增N条出场数据
			// REMOVE 场内数据
			// 还要更新通道所在区域的已停车数量和未停车数量
			LOG.info("=============开始读取参数=============");
			ArrayList<Tc_usercrdtm> usercrdtmlist = setTc_usercrdtm_out(requestBody);
			LOG.info("=============读取参数结束=============");
			ArrayList<Tc_usercrdtm_in> usercrdtminlist = setUsercrdtmin(usercrdtmlist);
			this.usercrdtmMapper.batchInsertUsercrdtm(usercrdtmlist);
			// this.usercrdtm_inMapper.batchDelUsercrdtm_in(usercrdtminlist);
			this.usercrdtm_inMapper.batchDelUsercrdtm_in_out(usercrdtminlist);
			LOG.info("=============setTc_usercrdtm_out返回值" + usercrdtmlist.toString() + "=============");
			// 获取车辆进场记录ID zhh修改
			for (int i = 0; i < usercrdtmlist.size(); i++) {
				LOG.info("=====开始更新收费记录表的出场ID" + i + "======");
				JSONObject mapparam = new JSONObject();
				mapparam.put("ParkingLotNo", usercrdtmlist.get(i).getParkinglotno());
				mapparam.put("CarCode", usercrdtmlist.get(i).getCarcode());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				// mapparam.put("OutTime", sdf.format(new
				// Date(Long.parseLong(usercrdtmlist.get(i).getCrdtm().toString()))));
				mapparam.put("OutTime", sdf.format(usercrdtmlist.get(i).getCrdtm()));
				LOG.info("=====XXXDDDXXXDDDXXDD" + usercrdtmlist.get(i).getCrdtm() + "======");
				mapparam.put("PartitionID", getPartitionid(usercrdtmlist.get(i).getParkinglotno()));
				LOG.info("=====获取收费记录表信息:" + mapparam + "======");
				List<Object> list = chargerecordinfoMapper.queryByCondition(mapparam);
				if (list != null && list.size() > 0) {
					Tc_chargerecordinfo chargerecord = (Tc_chargerecordinfo) list.get(0);
					if (chargerecord != null && chargerecord.getRecordid() > 0) {
						chargerecord.setOutrecordid((Integer.valueOf(usercrdtmlist.get(i).getRecordid().toString())));
						LOG.info("=====读取到的收费记录表的ID:" + chargerecord.getOutrecordid() + "======");
						chargerecordinfoMapper.updateByPrimaryKey(chargerecord);
						LOG.info("=====更新成功" + i + "======");
					}
				}
				LOG.info("=====结束更新收费记录表的出场ID" + i + "======");
			}

			// JSONObject dat2a = new JSONObject();
			// dat2a.put("usercrdtmlist", usercrdtmlist);
			// sendKafkaMesOut()
			LOG.info("=============kafuka begin=============");
			sendKafkaMesOut(ParkingLotNo, usercrdtminlist);
			sendKafkaMesusercrdtm(ParkingLotNo, usercrdtmlist, "out_p");
			LOG.info("=============kafuka end=============");
			// for (int l = 0; l < usercrdtmlist.size(); l++) {

			// }
			// LOG.info("====================dat2a" + dat2a +
			// "================");
		} catch (Exception ex) {
			LOG.error("====================Exception" + ex.getMessage() + "================");
			reMsg.put("sign", "");
			reMsg.put("resCode", -1);
			reMsg.put("resMsg", "fail");
		} finally {

		}
		return reMsg.toJSONString();
	}

	/**
	 * ParkingLotNo partitionID,查询停车记录
	 */
	@Override
	public void queryCarInOutRecord(String requestBody) {
		JSONObject data = JSON.parseObject(requestBody);
		String ParkingLotNo = data.getString("ParkingLotNo");
		String temp = "";
		int hasresult = StringUtil.getAsciiCode(ParkingLotNo) % 16;
		if (hasresult < 10) {
			temp = DateUtil.getFomartDate(new Date(), "yyyyMM") + "0" + hasresult;
		} else {
			temp = DateUtil.getFomartDate(new Date(), "yyyyMM") + hasresult;
		}
		int partitionID = Integer.parseInt(temp);
		JSONObject mapparam = new JSONObject();
		mapparam.put("ParkingLotNo", ParkingLotNo);
		mapparam.put("partitionID", partitionID);
		LOG.info(mapparam);
		List<Object> list = this.usercrdtmMapper.queryCarInOutRecord(mapparam);

		LOG.info("=============list.size" + list.size() + "====================");
	}

	public JSONArray queryChargerecordinfo(JSONObject mapparam) {
		LOG.info(" mapparam============" + mapparam + "==============");
		List<Object> list = this.chargerecordinfoMapper.queryChargerecordinfo(mapparam);
		JSONArray array = new JSONArray(list);
		LOG.info("queryChargerecordinfo===array=======" + array + "================");
		return array;
	}

	@Override
	public String queryChargerecordinfo4string(JSONObject mapparam) {
		// TODO Auto-generated method stub
		LOG.info(" mapparam============" + mapparam + "==============");
		List<Object> list = this.chargerecordinfoMapper.queryChargerecordinfo(mapparam);
		JSONArray array = new JSONArray(list);
		LOG.info("queryChargerecordinfo4string==========" + array + "================");
		return array.toJSONString();
	}

	private long getSequenceId() {
		long id;
		try {
			id = sequencEntrance.nextValue();
		} catch (SequenceException e) {
			throw new IllegalArgumentException("sequence netValue error");
		}
		return id;
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

	// 入场查询
	public ArrayList<Tc_usercrdtm> setTc_usercrdtm(String requestBody) {
		JSONObject data = JSON.parseObject(requestBody);

		// String sign = data.getString("sign");
		String ParkingLotNo = data.getString("parkNo");

		JSONArray dataArrays = data.getJSONArray("data");
		ArrayList<Tc_usercrdtm> arrayList = new ArrayList<Tc_usercrdtm>();
		for (int i = 0; i < dataArrays.size(); i++) {

			/**
			 * 调用外部接口获取最后免费立场时间 需要传的参数：1.车场id 2.规则应该是个数组 3.入场时间intime
			 * 4.目前还按照临时车15分钟，长期用户2小时算
			 */

			long addtime = 0;
			Tc_usercrdtm usercrdtm = new Tc_usercrdtm();
			String licensePlateNumber = "";
			String inTime = "";
			int channelId = -1;
			String serialNumber = "";
			String remarks1 = "";
			usercrdtm.setRecordid(publicparkingservice.getEntranceSequenceId());
			usercrdtm.setPartitionid(getPartitionid(ParkingLotNo));
			usercrdtm.setParkinglotno(ParkingLotNo);
			JSONObject object = dataArrays.getJSONObject(i);
			if (object.containsKey("licensePlateNumber")) {
				licensePlateNumber = object.getString("licensePlateNumber");
				usercrdtm.setCarcode(licensePlateNumber);
			}
			if (object.containsKey("inTime")) {
				inTime = object.getString("inTime");
				usercrdtm.setCrdtm(DateUtil.StringfomateDate(inTime));
			}
			if (object.containsKey("channelId")) {
				channelId = object.getIntValue("channelId");
				usercrdtm.setChannelid(channelId);
			}
			if (object.containsKey("serialNumber")) {
				// serialNumber = object.getString("serialNumber");

			}

			if (object.containsKey("remarks1")) {
				remarks1 = object.getString("remarks1");
			}
			usercrdtm.setCarcode2(remarks1);
			Tc_userinfo userinf = userinfoService.selectBylicensePlateNumber(licensePlateNumber, ParkingLotNo,
					usercrdtm.getCrdtm());

			if (userinf != null) {
				addtime = 2 * 60 * 60 * 1000;// 长期用户
				usercrdtm.setUsername(userinf.getUsername());
				usercrdtm.setChargeruleid(userinf.getChargeruleid());
			} else {
				usercrdtm.setUsername("临时车");
				LOG.info("=============channelId" + channelId + "=============");
				Tc_channel channel = channelmapper.selectByPrimaryKey(channelId);
				addtime = 15 * 60 * 1000;
				if (channel != null) {

					LOG.info("=============channelId" + channel.getChargeruleid() + "=============");
					usercrdtm.setChargeruleid(channel.getChargeruleid());
				} else {
					LOG.info("=============channelId");
					usercrdtm.setChargeruleid(0);
				}
			}
			usercrdtm.setInorout(0);// 进出状态(0进 1出)
			String userid = object.containsKey("userid") ? object.getString("userid") : "";
			String inoutid = object.containsKey("inOutId") ? object.getString("inOutId") : "";
			String path = getImagePath(ParkingLotNo, object.getIntValue("channelId"), usercrdtm.getCrdtm(), inoutid,
					userid, 0);
			usercrdtm.setImagepath(path);// 图片路径需要修改

			Date lastdate = publicparkingservice.getLastOutTime(usercrdtm);

			usercrdtm.setLastoutTime(lastdate);
			LOG.info("=============setLastoutTime" + usercrdtm.getLastoutTime() + "=================");
			usercrdtm.setCarstyleid(0);
			arrayList.add(usercrdtm);
		}
		return arrayList;
	}

	private String getImagePath(String ParkingLotNo, int channelId, Date intime, String userid, int flag) {
		// flag0进1出
		String path = "";
		ResourceBundle bundle = ResourceBundle.getBundle("ossConfig");
		if (flag == 0) {
			path = bundle.getString("parkingEntranceDir");
		} else {
			path = bundle.getString("parkingDepartureDir");
		}

		path = path + ParkingLotNo + "/" + new SimpleDateFormat("yyyyMM").format(intime) + "/";
		if (userid != null && !userid.isEmpty()) {
			path = path + userid;
		} else {
			path = path + ParkingLotNo + "_" + channelId + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(intime);
		}
		LOG.info("=============imagPath" + path + "=================================");
		return path;
	}

	/**
	 * 
	 * @param ParkingLotNo
	 *            停车场编号
	 * @param channelId
	 *            通道编号
	 * @param intime
	 *            进场时间
	 * @param inoutid
	 *            替代原来的userid
	 * @param userid
	 *            兼容原有版本userid
	 * @param flag
	 *            0进1出
	 * @return
	 */
	private String getImagePath(String ParkingLotNo, int channelId, Date intime, String inoutid, String userid,
			int flag) {
		// flag0进1出
		String path = "";
		ResourceBundle bundle = ResourceBundle.getBundle("ossConfig");
		if (flag == 0) {
			path = bundle.getString("parkingEntranceDir");
		} else {
			path = bundle.getString("parkingDepartureDir");
		}

		path = path + ParkingLotNo + "/" + new SimpleDateFormat("yyyyMM").format(intime) + "/";

		if (inoutid != null && !inoutid.isEmpty()) {
			path = path + inoutid;
		} else if (userid != null && !userid.isEmpty()) {
			path = path + userid;
		} else {
			path = path + ParkingLotNo + "_" + channelId + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(intime);
		}
		LOG.info("=============imagPath" + path + "=================================");
		return path;
	}

	// 出场查询
	public ArrayList<Tc_usercrdtm> setTc_usercrdtm_out(String requestBody) {
		JSONObject data = JSON.parseObject(requestBody);
		// String sign = data.getString("sign");
		String ParkingLotNo = data.getString("parkNo");
		JSONArray dataArrays = data.getJSONArray("data");
		ArrayList<Tc_usercrdtm> arrayList = new ArrayList<Tc_usercrdtm>();
		for (int i = 0; i < dataArrays.size(); i++) {
			Tc_usercrdtm usercrdtm = new Tc_usercrdtm();
			String licensePlateNumber = "";
			String outTime = "";
			int channelId = -1;
			String serialNumber = "";
			String remarks1 = "";
			usercrdtm.setRecordid(publicparkingservice.getEntranceSequenceId());
			usercrdtm.setPartitionid(getPartitionid(ParkingLotNo));
			usercrdtm.setParkinglotno(ParkingLotNo);
			JSONObject object = dataArrays.getJSONObject(i);
			if (object.containsKey("licensePlateNumber")) {
				licensePlateNumber = object.getString("licensePlateNumber");
				usercrdtm.setCarcode(licensePlateNumber);
			}
			if (object.containsKey("outTime")) {
				outTime = object.getString("outTime");
				// ThreadLocalDateFormatMap
				LOG.info("outTime=============outTime" + outTime + "=================================outTime");
				// usercrdtm.setCrdtm(DateUtil.StringfomateDate(outTime));
				try {
					usercrdtm.setCrdtm(ThreadLocalDateUtil.parse(outTime));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (object.containsKey("channelId")) {
				channelId = object.getIntValue("channelId");
				usercrdtm.setChannelid(channelId);
			}
			if (object.containsKey("serialNumber")) {
				serialNumber = object.getString("serialNumber");
			}
			if (object.containsKey("remarks1")) {
				remarks1 = object.getString("remarks1");
			}
			Tc_userinfo userinf = userinfoService.selectBylicensePlateNumber(licensePlateNumber, ParkingLotNo,
					usercrdtm.getCrdtm());

			if (userinf != null) {
				usercrdtm.setUsername(userinf.getUsername());
				usercrdtm.setChargeruleid(userinf.getChargeruleid());

			} else {
				usercrdtm.setUsername("临时车");
				LOG.info("=============channelId" + channelId + "=============");
				Tc_channel channel = channelmapper.selectByPrimaryKey(channelId);
				if (channel != null) {
					LOG.info("=============channelId" + channel.getChargeruleid() + "=============");
					usercrdtm.setChargeruleid(channel.getChargeruleid());
				} else {
					LOG.info("=============channelId");
					usercrdtm.setChargeruleid(0);
				}
			}
			usercrdtm.setInorout(1);// 进出状态(0进 1出)
			String userid = object.containsKey("userid") ? object.getString("userid") : "";
			String inoutid = object.containsKey("inOutId") ? object.getString("inOutId") : "";
			String imagePath = getImagePath(ParkingLotNo, channelId, usercrdtm.getCrdtm(), inoutid, userid, 1);
			usercrdtm.setImagepath(imagePath);// 图片路径需要修改
			// 没有值得参数
			usercrdtm.setCarstyleid(0);

			// 获取场内记录(将场内记录的用户名称和车辆标签更新到出场记录中)
			Map<String, Object> inParam = new HashMap<String, Object>();
			int partitionID = getPartitionidin(usercrdtm.getParkinglotno());
			inParam.put("partitionID", partitionID);
			inParam.put("ParkingLotNo", ParkingLotNo);
			inParam.put("CarCode", licensePlateNumber);
			Tc_usercrdtm_in usercrdtmIn = this.usercrdtm_inMapper.queryOneByCarCodeOrderByCrdtm(inParam);
			if (usercrdtmIn != null) {
				usercrdtm.setUsername(usercrdtmIn.getUsername());
				usercrdtm.setCarintype(usercrdtmIn.getCarintype());
			}
			// zhh0210add
			usercrdtm.setAreaId(object.containsKey("areaId") ? object.getString("areaId") : "");
			arrayList.add(usercrdtm);

		}

		return arrayList;
	}

	public boolean isExceptionParking(String requestBody) {
		JSONObject data = JSON.parseObject(requestBody);

		return false;
	}

	public ArrayList<Tc_usercrdtmin_anomaly> setAnomalylist(ArrayList<Tc_usercrdtm> usercrdtmlist) {
		ArrayList<Tc_usercrdtm_in> usercrdtm_inlist = new ArrayList<Tc_usercrdtm_in>();

		ArrayList<Tc_usercrdtmin_anomaly> usercrdtmin_anomalyList = new ArrayList<Tc_usercrdtmin_anomaly>();
		for (int i = 0; i < usercrdtmlist.size(); i++) {
			Tc_usercrdtm usercrdtm = usercrdtmlist.get(i);
			Tc_usercrdtm_in usercrdtm_in = usercrdtm_inMapper.queryParkingStatebyCarCode(usercrdtm.getCarcode(),
					usercrdtm.getParkinglotno(), getPartitionidin(usercrdtm.getParkinglotno()));
			if (usercrdtm_in != null) {
				usercrdtm_inlist.add(usercrdtm_in);
			}
		}
		if (usercrdtm_inlist.size() > 0) {
			usercrdtm_inMapper.batchDelUsercrdtm_in(usercrdtm_inlist);
		}
		for (int j = 0; j < usercrdtm_inlist.size(); j++) {
			Tc_usercrdtm_in usercrdtm_in = usercrdtm_inlist.get(j);
			Tc_usercrdtmin_anomaly usercrdtmin_anomaly = new Tc_usercrdtmin_anomaly();
			usercrdtmin_anomaly.setCarcode(usercrdtm_in.getCarcode());
			usercrdtmin_anomaly.setUsername(usercrdtm_in.getUsername());
			usercrdtmin_anomaly.setChargeruleid(usercrdtm_in.getChargeruleid());
			usercrdtmin_anomaly.setCrdtm(usercrdtm_in.getCrdtm());
			usercrdtmin_anomaly.setChannelid(usercrdtm_in.getChannelid());
			usercrdtmin_anomaly.setImagepath(usercrdtm_in.getImagepath());
			usercrdtmin_anomaly.setParkinglotno(usercrdtm_in.getParkinglotno());
			usercrdtmin_anomaly.setCarlabel("");
			usercrdtmin_anomaly.setCarcolor("");
			usercrdtmin_anomaly.setCarstyleid(0);
			usercrdtmin_anomalyList.add(usercrdtmin_anomaly);
		}
		return usercrdtmin_anomalyList;
	}

	public ArrayList<Tc_usercrdtm_in> setUsercrdtmin(ArrayList<Tc_usercrdtm> usercrdtmlist) {
		ArrayList<Tc_usercrdtm_in> usercrdtm_inlist = new ArrayList<Tc_usercrdtm_in>();

		for (int i = 0; i < usercrdtmlist.size(); i++) {
			// usercrdtm_in.setCarlabel(tm.setCarlabel(carlabel))
			// usercrdtm_in.setCarcolor(CarColor);
			// usercrdtm_in.setCarstyleid(CarStyleid);
			// usercrdtm_in.setCarcode2(CarCode2);
			// usercrdtm_in.setParkinglocation(Parkinglocation);
			// usercrdtm_in.setNote(Note);
			// usercrdtm_in.setRecognizecarcode(RecognizeCarCode);
			Tc_usercrdtm tm = usercrdtmlist.get(i);
			Tc_usercrdtm_in usercrdtm_in = new Tc_usercrdtm_in();
			usercrdtm_in.setRecordid(publicparkingservice.getParksequenceId());
			usercrdtm_in.setUsername(tm.getUsername());
			usercrdtm_in.setCarcode(tm.getCarcode());
			usercrdtm_in.setChargeruleid(tm.getChargeruleid());

			usercrdtm_in.setCrdtm(tm.getCrdtm());
			usercrdtm_in.setChannelid(tm.getChannelid());
			usercrdtm_in.setImagepath(tm.getImagepath());
			LOG.info("=============setLastoutTime" + tm.getLastoutTime());
			usercrdtm_in.setLastouttime(tm.getLastoutTime());
			usercrdtm_in.setParkinglotno(tm.getParkinglotno());
			usercrdtm_in.setPartitionid(getPartitionidin(tm.getParkinglotno()));
			usercrdtm_in.setCarstyleid(tm.getCarstyleid());
			usercrdtm_in.setCarcode2(tm.getCarcode2());
			usercrdtm_inlist.add(usercrdtm_in);
		}
		return usercrdtm_inlist;
	}

	public void sendKafkaMes(String ParkingLotNo, ArrayList<Tc_usercrdtm_in> usercrdtm_in, String msgtype) {
		try {

			// JedisPoolUtils.lock(ParkingLotNo, "in");

			JSONArray jsonArray = new JSONArray();
			LOG.info("=============usercrdtm_in.szie" + usercrdtm_in.size() + "=============");
			// long l = JedisPoolUtils.hincrBy("cloudPark_inout_flow",
			// ParkingLotNo, usercrdtm_in.size()); 增量包的序号从消费端生成，默认给个0
			long l = 0;
			// LOG.info("=============l" + l + "=============");
			for (int p = 0; p < usercrdtm_in.size(); p++) {
				jsonArray.add(usercrdtm_in.get(p));
			}
			LOG.info("=============jsonArrayin" + jsonArray + "=============");
			BigDataAnalyze.sendMess(jsonArray, dataTunnelPublishClient, l, msgtype, ParkingLotNo,
					DataConstants.CLOUDPARK_INOUT);
			/*
			 * if (l % flowInterval == 0 && l != 0) { l =
			 * JedisPoolUtils.hincrBy("cloudPark_inout_flow", ParkingLotNo, 1);
			 * LOG.info("============= ===========100的倍数，发送全量================="
			 * ); JSONObject mapparam = new JSONObject();
			 * mapparam.put("ParkingLotNo", ParkingLotNo); JSONArray array =
			 * queryParkState(mapparam); BigDataAnalyze.sendMess(array,
			 * dataTunnelPublishClient, l, "in_k", ParkingLotNo,
			 * DataConstants.CLOUDPARK_INOUT); }
			 */
		} finally {
			// JedisPoolUtils.unlock(ParkingLotNo, "in");
		}
	}

	public void sendKafkaMesusercrdtm(String ParkingLotNo, ArrayList<Tc_usercrdtm> usercrdtm_list, String msgtype) {
		try {

			// JedisPoolUtils.lock(ParkingLotNo, "in");

			JSONArray jsonArray = new JSONArray();
			LOG.info("=============usercrdtm_list.szie" + usercrdtm_list.size() + "=============");
			// long l = JedisPoolUtils.hincrBy("cloudPark_inout_flow",
			// ParkingLotNo, usercrdtm_in.size()); 增量包的序号从消费端生成，默认给个0
			long l = 0;
			// LOG.info("=============l" + l + "=============");
			for (int p = 0; p < usercrdtm_list.size(); p++) {
				jsonArray.add(usercrdtm_list.get(p));
			}
			LOG.info("=============jsonArrayin" + jsonArray + "=============");
			BigDataAnalyze.sendMess(jsonArray, dataTunnelPublishClient, l, msgtype, ParkingLotNo,
					DataConstants.CLOUDPARK_INOUT_RECORD);
		} finally {
			// JedisPoolUtils.unlock(ParkingLotNo, "in");
		}
	}

	public void sendKafkaAnoMes(String ParkingLotNo, ArrayList<Tc_usercrdtmin_anomaly> usercrdtmin_anomaly,
			String msgtype) {
		try {
			JSONArray jsonArray = new JSONArray();
			LOG.info("=============Tc_usercrdtmin_anomaly.szie" + usercrdtmin_anomaly.size() + "=============");
			long l = 0;
			for (int p = 0; p < usercrdtmin_anomaly.size(); p++) {
				jsonArray.add(usercrdtmin_anomaly.get(p));
			}
			LOG.info("=============jsonArrayin" + jsonArray + "=============");
			BigDataAnalyze.sendMess(jsonArray, dataTunnelPublishClient, l, msgtype, ParkingLotNo,
					DataConstants.CLOUDPARK_ANOIN);
		} finally {
		}
	}

	public void sendKafkaOpenGateMes(String ParkingLotNo, List<Tc_opengaterecord> opengaterecord, String msgtype) {
		try {
			JSONArray jsonArray = new JSONArray();
			LOG.info("=============opengaterecord.szie" + opengaterecord.size() + "=============");
			long l = 0;
			for (int p = 0; p < opengaterecord.size(); p++) {
				jsonArray.add(opengaterecord.get(p));
			}
			LOG.info("=============jsonArrayin" + jsonArray + "=============");
			BigDataAnalyze.sendMess(jsonArray, dataTunnelPublishClient, l, msgtype, ParkingLotNo,
					DataConstants.CLOUDPARK_OPENGATE);
		} finally {
		}

	}

	public void sendKafkaMesOut(String ParkingLotNo, ArrayList<Tc_usercrdtm_in> usercrdtm_in) {
		try {

			// JedisPoolUtils.lock(ParkingLotNo, "out");

			JSONArray jsonArray = new JSONArray();
			LOG.info("=============usercrdtm_in.szie" + usercrdtm_in.size() + "=============");
			// long l = JedisPoolUtils.hincrBy("cloudPark_inout_flow",
			// ParkingLotNo, usercrdtm_in.size());
			long l = 0;
			LOG.info("=============l" + l + "=============");
			for (int p = 0; p < usercrdtm_in.size(); p++) {
				jsonArray.add(usercrdtm_in.get(p));
			}
			LOG.info("=============jsonArrayout" + jsonArray + "=============");
			// BigDataAnalyze.parkOutDataMessage(jsonArray,
			// dataTunnelPublishClient,
			// l, "p", ParkingLotNo);
			BigDataAnalyze.sendMess(jsonArray, dataTunnelPublishClient, l, "out_p", ParkingLotNo,
					DataConstants.CLOUDPARK_INOUT);
		} finally {
			// JedisPoolUtils.unlock(ParkingLotNo, "out");
		}
	}

	public void sendKafkaMesUploadPayRecord(String ParkingLotNo, ArrayList<Tc_chargerecordinfo> chargerecordinfolist) {
		try {

			// JedisPoolUtils.lock(ParkingLotNo, "charge");

			JSONArray jsonArray = new JSONArray();
			LOG.info("=============chargerecordinfolist.szie" + chargerecordinfolist.size() + "=============");
			/*
			 * long l = JedisPoolUtils.hincrBy("cloudpark_charge_flow",
			 * ParkingLotNo, chargerecordinfolist.size()); 增量包的序号从消费端生成，默认给个0
			 */
			long l = 0;
			LOG.info("=============l" + l + "=============");
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
			/*
			 * if (l % flowInterval == 0 && l != 0) { l =
			 * JedisPoolUtils.hincrBy("cloudpark_charge_flow", ParkingLotNo,
			 * chargerecordinfolist.size()); LOG.info(
			 * "============= ===========10的倍数，发送全量=================");
			 * JSONObject mapparam = new JSONObject();
			 * mapparam.put("ParkingLotNo", ParkingLotNo); JSONArray array =
			 * queryChargerecordinfo(mapparam); BigDataAnalyze.sendMess(array,
			 * dataTunnelPublishClient, l, "in_k", ParkingLotNo,
			 * DataConstants.CLOUDPARK_CHARGE); //
			 * BigDataAnalyze.chargePayMessage(array, dataTunnelPublishClient,
			 * // l, "k", ParkingLotNo); }
			 */
		} finally {

			// JedisPoolUtils.unlock(ParkingLotNo, "charge");
		}
	}

	public void sendKafkaMesUploadPayRecord(String ParkingLotNo, ArrayList<Tc_chargerecordinfo> chargerecordinfolist,
			String topic) {
		try {

			// JedisPoolUtils.lock(ParkingLotNo, "charge");

			JSONArray jsonArray = new JSONArray();
			LOG.info("=============chargerecordinfolist.szie" + chargerecordinfolist.size() + "=============");
			/*
			 * long l = JedisPoolUtils.hincrBy("cloudpark_charge_flow",
			 * ParkingLotNo, chargerecordinfolist.size()); 增量包的序号从消费端生成，默认给个0
			 */
			long l = 0;
			LOG.info("=============l" + l + "=============");
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
			/*
			 * if (l % flowInterval == 0 && l != 0) { l =
			 * JedisPoolUtils.hincrBy("cloudpark_charge_flow", ParkingLotNo,
			 * chargerecordinfolist.size()); LOG.info(
			 * "============= ===========10的倍数，发送全量=================");
			 * JSONObject mapparam = new JSONObject();
			 * mapparam.put("ParkingLotNo", ParkingLotNo); JSONArray array =
			 * queryChargerecordinfo(mapparam); BigDataAnalyze.sendMess(array,
			 * dataTunnelPublishClient, l, "in_k", ParkingLotNo,
			 * DataConstants.CLOUDPARK_CHARGE); //
			 * BigDataAnalyze.chargePayMessage(array, dataTunnelPublishClient,
			 * // l, "k", ParkingLotNo); }
			 */
		} finally {

			// JedisPoolUtils.unlock(ParkingLotNo, "charge");
		}
	}

	/**
	 * @Title: sendcarTagAnomaly2KafKa @Description: 发送打标签异常数据到kafka @param
	 *         parkinglotNo 车场ID @param carTagAnomalys 标签异常记录数组 @return
	 *         void @throws
	 */
	public void sendcarTagAnomaly2KafKa(String parkinglotNo, JSONArray jsonArray) {
		// 判断是否长期车标签异常,如果异常则发消息到kafka
		long l = 0;
		BigDataAnalyze.sendMess(jsonArray, dataTunnelPublishClient, l, "in_p", parkinglotNo,
				DataConstants.CLOUDPLACE_ABNORMALTAG);
	}

	@Override
	public String uploadExecptionOut(String requestBody) {
		JSONObject reMsg = new JSONObject();
		int resCode = 0;
		try {
			JSONObject data = JSON.parseObject(requestBody);
			String ParkingLotNo = data.getString("parkNo");
			reMsg.put("parkNo", ParkingLotNo);
			reMsg.put("sign", "");
			reMsg.put("resCode", resCode);
			reMsg.put("resMsg", "success");

			ArrayList<Tc_usercrdtmout_anomaly> usercrdtmout_anomalylist = set_usercrdtmout_anomaly(requestBody);
			if (usercrdtmout_anomalylist.size() > 0) {
				usercrdtmout_anomalyMapper.batchInsertusercrdtmout_anomaly(usercrdtmout_anomalylist);
			}
		} catch (Exception e) {
			reMsg.put("resCode", -1);
			reMsg.put("resMsg", "fail");
			e.printStackTrace();
			return reMsg.toJSONString();
		} finally {
		}
		return reMsg.toJSONString();
	}

	@Override
	// 上传缴费信息接口
	public String uploadBoxPayRecord(String requestBody) {
		// TODO Auto-generated method stub
		LOG.info("====================WELCOME================");
		LOG.info("====================requestBody" + requestBody + "================");
		JSONObject reMsg = new JSONObject();
		int resCode = 0;

		JSONObject data = JSON.parseObject(requestBody);
		String ParkingLotNo = data.getString("parkNo");
		reMsg.put("parkNo", ParkingLotNo);
		reMsg.put("sign", "");
		reMsg.put("resCode", resCode);
		reMsg.put("resMsg", "success");

		try {

			ArrayList<Tc_chargerecordinfo> chargerecordinfolist = setChargerecordinfo(requestBody);
			JSONArray a = new JSONArray();
			a.add(chargerecordinfolist);
			LOG.info("====================chargerecordinfolist" + chargerecordinfolist.size() + "================");
			LOG.info("====================JSONArray" + a + "================");
			for(Tc_chargerecordinfo e:chargerecordinfolist){
				if(e.getAmounttype()==-1){
					if(e.getCarcode()!=null || "".equals(e.getCarcode())){
						e.setCarcode("结算无车牌");
					}
					
				}
			}
			chargerecordinfoMapper.batchInsertChargerecordinfo(chargerecordinfolist);

			for (int i = 0; i < chargerecordinfolist.size(); i++) {
				if (chargerecordinfolist.get(i).getAmounttype() == null
						|| chargerecordinfolist.get(i).getAmounttype() == 0) {
					// 停车收费
					sendKafkaMesUploadPayRecord(ParkingLotNo, chargerecordinfolist);
				} else if (chargerecordinfolist.get(i).getAmounttype() == -1
						|| chargerecordinfolist.get(i).getAmounttype() > 0) {
					// 其他收费
					sendKafkaMesUploadPayRecord(ParkingLotNo, chargerecordinfolist,
							DataConstants.CLOUDPARK_OTHERCHARGE);
				}
			}

		} catch (Exception ex) {
			reMsg.put("resCode", -1);
			reMsg.put("resMsg", "fail");
			LOG.info("====================Exception" + ex.getMessage() + "================");
			ex.printStackTrace();
			return reMsg.toJSONString();
		} finally {

		}
		return reMsg.toJSONString();
	}

	@Override
	public String DelBoxPayRecord(String requestBody) {
		JSONObject reMsg = new JSONObject();
		int resCode = 0;
		try {
			JSONObject data = JSON.parseObject(requestBody);
			String ParkingLotNo = data.getString("parkNo");
			reMsg.put("parkNo", ParkingLotNo);
			reMsg.put("sign", "");
			reMsg.put("resCode", resCode);
			reMsg.put("resMsg", "success");
			LOG.info("========================ParkingLotNo" + ParkingLotNo);
			LOG.info("========================licensePlateNumber" + data.getString("licensePlateNumber"));
			LOG.info("========================inTime" + data.getString("inTime"));
			Timestamp fintime = null;
			if ((data.getString("inTime")) != null) {
				fintime = Timestamp.valueOf(data.getString("inTime"));
			}

			int resultcount = this.usercrdtmout_anomalyMapper.deleteByLotNoAndCarcode(ParkingLotNo,
					data.getString("licensePlateNumber"), fintime);
			if (resultcount == 0) {

				LOG.info("========================resultcount" + resultcount);
				LOG.info("========================删除异常出场存在问题" + resultcount);
			} else {
				LOG.info("========================resultcount" + resultcount);
				LOG.info("========================删除异常出场成功" + resultcount);
			}
		} catch (Exception e) {
			reMsg.put("resCode", -1);
			reMsg.put("resMsg", "fail");
			e.printStackTrace();
			return reMsg.toJSONString();
		} finally {
		}
		return reMsg.toJSONString();
	}

	public ArrayList<Tc_usercrdtmout_anomaly> set_usercrdtmout_anomaly(String requestBody) {
		// LOG.info("Tc_usercrdtmout_anomaly requestBody==========" +
		// requestBody + "================");
		JSONObject data = JSON.parseObject(requestBody);
		String ParkingLotNo = data.getString("parkNo");
		JSONArray dataArrays = data.getJSONArray("data");
		ArrayList<Tc_usercrdtmout_anomaly> arrayList = new ArrayList<Tc_usercrdtmout_anomaly>();
		for (int i = 0; i < dataArrays.size(); i++) {
			JSONObject object = dataArrays.getJSONObject(i);
			Tc_usercrdtmout_anomaly out_anomaly = new Tc_usercrdtmout_anomaly();
			String carcode = object.getString("licensePlateNumber");
			int chargeruleid = 2;
			int carstyleid = 0;
			// String channelid = "";
			// String imagePath = "xxxx";
			// int operatorid = object.getIntValue("operatorID");
			String outcrdtm = object.getString("outTime");
			String intime = object.getString("inTime");

			out_anomaly.setCarcode(carcode);
			out_anomaly.setChargeruleid(chargeruleid);
			out_anomaly.setCarstyleid(carstyleid);
			out_anomaly.setOutcrdtm(DateUtil.StringfomateDate(outcrdtm));
			out_anomaly.setIntime(DateUtil.StringfomateDate(intime));
			out_anomaly.setChannelid(object.containsKey("channelID") ? object.getIntValue("channelID") : 0);

			// zhh1210edit
			/*
			 * Tc_userinfo userinf = publicparkingservice.getuserinfo(carcode,
			 * ParkingLotNo, DateUtil.StringfomateDate(intime),
			 * String.valueOf(object.containsKey("channelID") ?
			 * object.getIntValue("channelID") : 0));
			 * 
			 * int ruleid = publicparkingservice.getUserRuleId(userinf,
			 * out_anomaly.getChannelid()); LOG.info("ruleid==========" + ruleid
			 * + "================"); out_anomaly.setChargeruleid(ruleid);
			 */

			/*
			 * if (object.containsKey("channelID")) {
			 * out_anomaly.setChannelid(object.getIntValue("channelID")); } else
			 * { out_anomaly.setChannelid(0); }
			 */
			out_anomaly.setChargemoney(object.getFloat("amount"));
			out_anomaly.setParkinglotno(ParkingLotNo);
			out_anomaly.setOperatorid(object.getIntValue("operatorID"));

			// zhh1103add
			String userid = object.containsKey("userid") ? object.getString("userid") : "";
			String inoutid = object.containsKey("inOutId") ? object.getString("inOutId") : "";
			String imagepath = getImagePath(ParkingLotNo, object.getIntValue("channelID"),
					DateUtil.StringfomateDate(outcrdtm), inoutid, userid, 1);
			out_anomaly.setImagepath(imagepath);
			LOG.info("uploadExecptionOut,set_usercrdtmout_anomaly,setImagepath:" + imagepath);
			// out_anomaly.setIsupload(false);
			// DateUtil.StringfomateDate(inTime)
			// out_anomaly
			arrayList.add(out_anomaly);
		}
		return arrayList;
	}

	public ArrayList<Tc_chargerecordinfo> setChargerecordinfo(String requestBody) {

		ArrayList<Tc_chargerecordinfo> chargerecordinfolist = new ArrayList<Tc_chargerecordinfo>();
		try {
			JSONObject data = JSON.parseObject(requestBody);
			String ParkingLotNo = data.getString("parkNo");
			JSONArray dataArrays = data.getJSONArray("data");
			for (int k = 0; k < dataArrays.size(); k++) {
				JSONObject object = dataArrays.getJSONObject(k);
				Tc_chargerecordinfo chargerecordinfo = new Tc_chargerecordinfo();
				if (object.containsKey("licensePlateNumber")) {
					chargerecordinfo.setCarcode(object.getString("licensePlateNumber"));

				}
				if (object.containsKey("chargeDate")) {
					chargerecordinfo.setChargedate(DateUtil.StringfomateDate(object.getString("chargeDate")));
				}

				if (object.containsKey("outTime")) {
					chargerecordinfo.setOuttime(DateUtil.StringfomateDate(object.getString("outTime")));
				} else {
					// 如果不存在出场时间，则补充缴费时间为出场时间
					chargerecordinfo.setOuttime(chargerecordinfo.getChargedate());
					chargerecordinfo.setRemarks1("app缴费,补充缴费时间为出场时间");
				}

				if (object.containsKey("inTime")) {
					chargerecordinfo.setIntime(DateUtil.StringfomateDate(object.getString("inTime")));
				} else {
					// 如果不存在进场时间，则补充出场时间为进场时间
					chargerecordinfo.setIntime(chargerecordinfo.getOuttime());
					chargerecordinfo.setRemarks1("协商收费,补充出场时间为进场时间");
				}

				if (object.containsKey("stopTime")) {
					chargerecordinfo.setStoptime(object.getString("stopTime"));
				}
				if (object.containsKey("receivablAmount")) {
					// 应收金额
					chargerecordinfo.setChargemoney((float) (Math.round(object.getFloat("receivablAmount"))));
				}
				if (object.containsKey("reductionAmount")) {
					// 减免金额
					chargerecordinfo.setReductionamount((float) (Math.round(object.getFloat("reductionAmount"))));
				}
				if (object.containsKey("paidAmount")) {
					// 实收金额
					chargerecordinfo.setRealchargeamount((float) (Math.round(object.getFloat("paidAmount"))));
				}

				if (object.containsKey("chargeTypeID")) {
					/*
					 * 1 票箱收费 2 自助缴费机 3 手持机 4 行呗App 5 岗亭收费 6 中央收费
					 */
					chargerecordinfo.setChargetype(object.getIntValue("chargeTypeID"));
					if (object.getIntValue("chargeTypeID") == 5) {// 月租续费
						Tc_userinfo userinf = userinfoService.selectBylicensePlateNumber(
								object.getString("licensePlateNumber").split(",")[0], ParkingLotNo,
								DateUtil.StringfomateDate(object.getString("outTime")));
						if (userinf != null) {
							chargerecordinfo.setEmplyname(userinf.getUsername());
						}
					}
				}
				// int deviceid = 0;
				/*
				 * if (object.containsKey("serialNumber") &&
				 * !object.getString("serialNumber").equals("")) { LOG.info(
				 * "====================serialNumber" +
				 * object.getString("serialNumber") + "================");
				 * Pb_station station =
				 * stationMapper.selectBySerialNumber(object.getString(
				 * "serialNumber")); if (station != null) {
				 * chargerecordinfo.setDeviceid(station.getRecordid()); deviceid
				 * = station.getRecordid(); } if (station != null &&
				 * station.getRecordid() > 0) { List<Tc_channel> channellist =
				 * channelmapper.selectByMStationno(station.getRecordid()); if
				 * (channellist != null && channellist.size() > 0) { for (int i
				 * = 0; i < channellist.size(); i++) { if
				 * (channellist.get(i).getInorout() == 1) // 出场id {
				 * chargerecordinfo.setChannelid(channellist.get(i).getChannelid
				 * ()); chargerecordinfo.setChargeruleid(channellist.get(i).
				 * getChargeruleid()); } } } } }
				 */

				if (object.containsKey("deviceId")) {
					// deviceid = object.getInteger("deviceId");
					chargerecordinfo.setDeviceid(object.getInteger("deviceId"));
				}

				if (object.containsKey("channelId")) {
					chargerecordinfo.setChannelid(object.getInteger("channelId"));
				}

				/*
				 * if (object.containsKey("chargeRuleId")) {
				 * chargerecordinfo.setChargeruleid(object.getInteger(
				 * "chargeRuleId")); }
				 */
				if (object.containsKey("chargeRuleId")) {
					// 收费规则Id
					// chargeRuleId
					chargerecordinfo.setChargeruleid(object.getIntValue("chargeRuleId"));

				}

				if (object.containsKey("operatorID")) {
					// 收费员ID
					chargerecordinfo.setChargeuserid(String.valueOf(object.getIntValue("operatorID")));
				}
				if (object.containsKey("chargeSource")) {
					/*
					 * 0 现金 1 电子账户 2 支付宝支付 3 微信支付 4 平台代扣 5 喵街 6 闪付 7 支付宝刷卡
					 */
					chargerecordinfo.setChargesource(String.valueOf(object.getIntValue("chargeSource")));
				}
				if (object.containsKey("lastOutTime")) {
					chargerecordinfo.setLastouttime(DateUtil.StringfomateDate(object.getString("lastOutTime")));
				}
				chargerecordinfo.setPartitionid(getPartitionid(ParkingLotNo));
				chargerecordinfo.setRecordid(publicparkingservice.getChargesequenceId());
				chargerecordinfo.setParkinglotno(ParkingLotNo);
				if (object.containsKey("licensePlateNumber") && object.containsKey("inTime")) {
					// 获取车辆进场记录ID zhh修改
					try {
						JSONObject mapparam = new JSONObject();
						mapparam.put("ParkingLotNo", ParkingLotNo);
						mapparam.put("CarCode", object.getString("licensePlateNumber"));
						mapparam.put("Crdtm", object.getString("inTime"));
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

				if (object.containsKey("amountType")) {
					/*
					 * 0 停车收费 1充值收费 2 卡片成本 3 消费金额 4车位收费
					 */
					// 收费类型
					chargerecordinfo.setAmounttype(object.getIntValue("amountType"));

				}

				Boolean isallfree = false;
				if (object.containsKey("reductionsName")) {
					// 减免名称
					// ReductionsName
					String reductionsname = object.getString("reductionsName");
					if (reductionsname.indexOf("全免") > -1) {
						chargerecordinfo.setReductionsname("全免");
						isallfree = true;
					} else {
						chargerecordinfo.setReductionsname(object.getString("reductionsName"));
					}
				}
				if (object.containsKey("reductionType")) {
					String reductiontype = object.getString("reductionType");
					if (isallfree) {
						String[] types = reductiontype.split(",");
						chargerecordinfo.setReductiontype(types[types.length - 1]);
					} else {
						chargerecordinfo.setReductiontype(object.getString("reductionType"));
					}
					// saveJmrecord(ParkingLotNo, object, deviceid, username);
				}

				if (object.containsKey("remarks1")) {
					chargerecordinfo.setRemarks1(object.getString("remarks1"));
					// saveJmrecord(ParkingLotNo, object, deviceid, username);
				}

				if (object.containsKey("remarks2")) {
					chargerecordinfo.setRemarks2(object.getString("remarks2"));
					// saveJmrecord(ParkingLotNo, object, deviceid, username);
				}
				// 增加区域信息字段
				if (object.containsKey("areaId")) {
					chargerecordinfo.setAreaid(object.getInteger("areaId"));
				}
				chargerecordinfolist.add(chargerecordinfo);
			}
		} catch (Exception e) {
			LOG.info("====================Exception================" + e.getMessage());
			e.printStackTrace();
		}
		return chargerecordinfolist;
	}

	/**
	 * @param ParkingLotNo
	 * @param object
	 * @param deviceid
	 * @param username
	 */
	private void saveJmrecord(String ParkingLotNo, JSONObject object, int deviceid, String username) {
		if (object.containsKey("reductionType")) {
			// 减免类型
			// ReductionType
			String reductiontype = object.getString("reductionType");
			LOG.info("====================reductionType" + object.getString("reductionType") + "================");
			if (reductiontype != null && !reductiontype.isEmpty()) {
				String[] arrtype = reductiontype.split(",");
				for (int i = 0; i < arrtype.length; i++) {
					// System.out.println(num[i]);
					Tc_charge_jmrecord jmrecordmodel = new Tc_charge_jmrecord();
					jmrecordmodel.setSysno("");
					jmrecordmodel.setUsername(username);
					jmrecordmodel.setSerial("");
					jmrecordmodel.setCarcode(object.getString("licensePlateNumber"));
					jmrecordmodel.setIntime(DateUtil.StringfomateDate(object.getString("inTime")));
					LOG.info("====================jmrecordmodel,intime" + object.getString("inTime") + ","
							+ DateUtil.StringfomateDate(object.getString("inTime")) + "================");
					jmrecordmodel.setUsertypeid(object.getIntValue("chargeRuleId"));
					jmrecordmodel.setDevicesysid(deviceid);
					jmrecordmodel.setJmtypeid(Integer.valueOf(arrtype[i]));
					Tc_charge_jm jmmodel = jmMapper.selectByPrimaryKey(Integer.valueOf(arrtype[i]));
					if (jmmodel != null && jmmodel.getJmtypeid() > 0) {
						jmrecordmodel.setJmmoney(jmmodel.getJmmoney());
						jmrecordmodel.setJmtime(jmmodel.getJmtime());
						jmrecordmodel.setJmdiscount(jmmodel.getJmdiscount());
						jmrecordmodel.setJmnum(jmmodel.getJmcode());
					}
					jmrecordmodel.setJmdept(0);
					jmrecordmodel.setJmcodes("");
					jmrecordmodel.setOperatorid(object.getString("operatorID"));
					jmrecordmodel.setOperatordate(DateUtil.StringfomateDate(object.getString("chargeDate")));
					LOG.info("====================jmrecordmodel,chargeDate:" + object.getString("chargeDate") + ","
							+ DateUtil.StringfomateDate(object.getString("chargeDate")) + "================");
					jmrecordmodel.setParkinglotno(ParkingLotNo);

					int r = jmrecordMapper.insert(jmrecordmodel);
					LOG.info("====================AddJmRecord,ReturnValue:" + r + "================");
				}
			}
		}
	}

}
