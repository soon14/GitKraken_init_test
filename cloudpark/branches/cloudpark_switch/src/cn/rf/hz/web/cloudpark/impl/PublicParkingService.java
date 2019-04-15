package cn.rf.hz.web.cloudpark.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.cloudpark.model.Tc_userinfo;
import com.reformer.sharding.sequence.Sequence;
import com.reformer.sharding.sequence.exception.SequenceException;

import cn.rf.hz.web.cloudpark.daoxx.Tc_channelMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_userinfoMapper;
import cn.rf.hz.web.cloudpark.moder.Tc_channel;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm_in;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtmin_anomaly;
import cn.rf.hz.web.sharding.dao.Tc_usercrdtm_abnormalMapper;
import cn.rf.hz.web.sharding.dao.Tc_usercrdtm_inMapper;
import cn.rf.hz.web.utils.DataConstants;
import cn.rf.hz.web.utils.DateUtil;
import cn.rf.hz.web.utils.JedisPoolUtils;
import cn.rf.hz.web.utils.RequestUtil;
import cn.rf.hz.web.utils.StringUtil;
import cn.rf.hz.web.utils.ThreadLocalDateUtil;
import cn.rf.hz.web.utils.httputils.HttpProtocolHandler;
import cn.rf.hz.web.utils.httputils.HttpRequest;
import cn.rf.hz.web.utils.httputils.HttpResponse;
import cn.rf.hz.web.utils.httputils.HttpResultType;

@Service("publicparkingservice")

public class PublicParkingService {
	private final static Logger log = Logger.getLogger(PublicParkingService.class);

	@Autowired
	private Sequence sequencEntrance;

	@Autowired
	private Sequence sequencePark;

	@Autowired
	private Sequence sequenceCharge;

	@Autowired(required = false)
	Tc_channelMapper channelmapper;

	@Autowired
	private Tc_usercrdtm_inMapper usercrdtm_inMapper;

	@Autowired
	private Tc_usercrdtm_abnormalMapper usercrdtm_abnormalMapper;

	@Autowired
	Tc_userinfoMapper userinfomapper;

	/**
	 * 获取用户名称
	 * 
	 * @param licensePlateNumber
	 * @param parkingLotNo
	 * @return
	 */

	public Tc_userinfo getuserinfo(String licensePlateNumber, String parkingLotNo, Date datetime, String channelId) {
		JSONObject CarProp = new JSONObject();
		CarProp.put("parkNo", parkingLotNo);
		CarProp.put("carCode", licensePlateNumber);
		CarProp.put("inoutTime", datetime);
		CarProp.put("inoutType", "0");
		CarProp.put("channelId", channelId);
		Integer ruleId = getRuleID(Integer.parseInt(channelId));
		CarProp.put("ruleId", ruleId);
		log.info("CarPropCarProp" + CarProp.toJSONString());
		String afterhalfUrl = "lots/getCarTypeInfo.shtml";
		ResourceBundle bundle = ResourceBundle.getBundle("ControlResource");
		String posturl = bundle.getString("longtermusersurl").trim() + afterhalfUrl;
		Tc_userinfo user = null;
		try {
			String resultjson = null;
			// 获取车辆标签数据（长期车标签或临停车标签）,最多重试3次,3次失败,则将异常数据丢入kafka
			for (int i = 0; i < 3; i++) {
				resultjson = sendpost(posturl, CarProp.toJSONString(), parkingLotNo);
				log.info("PublicParkingService.getuserinfo.resultjson." + licensePlateNumber + ":" + resultjson
						+ " ,count:" + i);
				// 没有获取到数据，就重试
				if (resultjson == null || resultjson.isEmpty()) {
					log.info("PublicParkingService.sendpost.exception." + licensePlateNumber
							+ ":resultjson is empty or time out,count:" + i);
					Thread.sleep(1000);
					// 如果i==2,依然重试失败,则将异常数据存入异常标签map中
					if (i == 2) {
						// 将打标签异常的数据发消息到KafKa
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("carcode", licensePlateNumber);
						jsonObject.put("channelid", channelId);
						jsonObject.put("inouttime", datetime);
						jsonObject.put("inouttype", "0");
						jsonObject.put("ruleid", ruleId);
						jsonObject.put("parkno", parkingLotNo);
						JSONArray jsonArray = new JSONArray();
						jsonArray.add(jsonObject);
						String key = "carTag_" + licensePlateNumber;
						JedisPoolUtils.set(key, jsonArray.toJSONString());

					}
				}
			}

			JSONObject result_ = JSON.parseObject(resultjson);
			if (result_.getString("isLongCar").equals("1")) {
				JSONObject object = result_.getJSONObject("userJSON");
				user = new Tc_userinfo();
				user.setBgndt(ThreadLocalDateUtil.parse(object.getString("bgndt")));
				user.setEnddt(ThreadLocalDateUtil.parse(object.getString("enddt")));
				user.setChargeruleid(Integer.parseInt(object.getString("ruleId")));
				user.setUsername(object.getString("username"));
				user.setUserpropertiy(Integer.parseInt(object.getString("userpropertiy")));
			}

		} catch (Exception e) {
			log.info("PublicParkingService.getuserinfo.exception." + licensePlateNumber + ":" + e.getMessage());
			user = null;
			e.printStackTrace();
		}
		return user;
	}

	public String sendpost(String url, String param, String parkid) {
		log.info("PublicParkingService.sendpost.url:" + url);
		HttpRequest clReq = new HttpRequest(HttpResultType.STRING);
		clReq.setUrl(url);
		Map<String, String> head = new HashMap<String, String>();
		head.put("Content-Type", "text/plain;charset=UTF-8");
		head.put("parkid", parkid);
		clReq.setHeadMap(head);
		clReq.setCharset("UTF-8");
		HttpResponse httpreponse = null;
		String result = "";
		try {
			httpreponse = HttpProtocolHandler.getInstance().execute(clReq, param);
			result = httpreponse.getStringResult();
			log.info("PublicParkingService.sendpost:" + result);
			/* JSONObject result_ = JSON.parseObject(resultjson); */
		} catch (Exception e) {
			result = "";
			log.info("PublicParkingService.sendpost.exception." + param + ":" + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取入场时间 小于 有效期结束时间 的用户信息
	 * 
	 * @param licensePlateNumber
	 * @param ParkingLotNo
	 * @param datetime
	 * @return
	 */

	public Tc_userinfo getuserinfoByIntime(String licensePlateNumber, String ParkingLotNo, Date datetime) {
		Tc_userinfo userinfo = userinfomapper.selectByParkCodeAndEnddt(licensePlateNumber, ParkingLotNo, datetime);
		return userinfo;
		// return userinf!=null?userinf.getUsername():"临时车";
	}

	/**
	 * 通过requestBody设置需要保存的Usercrdtm对象(数组)
	 * 
	 * @param requestBody
	 * @return
	 */
	public ArrayList<Tc_usercrdtm> setUsercrdtm(String requestBody) {
		log.info("============setUsercrdtm" + requestBody + "===========");
		JSONObject data = JSON.parseObject(requestBody);
		String ParkingLotNo = data.getString("parkNo");// 停车场编号
		JSONArray dataArrays = data.getJSONArray("data");
		ArrayList<Tc_usercrdtm> arrayList = new ArrayList<Tc_usercrdtm>();
		for (int i = 0; i < dataArrays.size(); i++) {
			Tc_usercrdtm usercrdtm = new Tc_usercrdtm();
			usercrdtm.setRecordid(getEntranceSequenceId());
			usercrdtm.setPartitionid(getPartitionid(ParkingLotNo));
			usercrdtm.setParkinglotno(ParkingLotNo);
			JSONObject object = dataArrays.getJSONObject(i);
			if (object.containsKey("licensePlateNumber")) {
				usercrdtm.setCarcode(object.getString("licensePlateNumber"));
			}
			if (object.containsKey("inTime")) {
				// usercrdtm.setCrdtm(DateUtil.StringfomateDate(object.getString("inTime")));
				try {
					usercrdtm.setCrdtm(ThreadLocalDateUtil.parse(object.getString("inTime")));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			usercrdtm.setChannelid(object.containsKey("channelId") ? object.getIntValue("channelId") : 0);
			usercrdtm.setCarcode2(object.containsKey("remarks1") ? object.getString("remarks1") : "");
			usercrdtm.setInorout(0);// 进出状态(0进 1出)
			usercrdtm.setCarstyleid(0);
			arrayList.add(usercrdtm);
			// String path =getImagePath(ParkingLotNo,
			// object.getIntValue("channelId"), usercrdtm.getCrdtm(), 0) ;
			String userid = object.containsKey("userid") ? object.getString("userid") : "";
			String inOutId = object.containsKey("inOutId") ? object.getString("inOutId") : "";
			usercrdtm.setImagepath(getImagePath(ParkingLotNo, object.getIntValue("channelId"), usercrdtm.getCrdtm(),
					inOutId, userid, 0));// 图片路径需要修改
			log.info("============Carcode" + usercrdtm.getCarcode() + "===========");
			log.info("============getParkinglotno" + usercrdtm.getParkinglotno() + "===========");
			Tc_userinfo userinf = getuserinfo(usercrdtm.getCarcode(), usercrdtm.getParkinglotno(), usercrdtm.getCrdtm(),
					String.valueOf(object.containsKey("channelId") ? object.getIntValue("channelId") : 0));
			usercrdtm.setCarintype(userinf != null ? 1 : 0);

			if (userinf != null) {
				if (isorder(object, usercrdtm)) {
					usercrdtm.setCarintype(3);
				} else {
					usercrdtm.setCarintype(1);
				}
			} else {
				if (isorder(object, usercrdtm)) {
					usercrdtm.setCarintype(2);
				} else {
					usercrdtm.setCarintype(0);
				}
			}
			// log.info("============userinf" + userinf+ "===========");
			usercrdtm.setUsername(userinf != null ? userinf.getUsername() : "临时车");

			/**
			 * 在有效期内的预约用户也打成长期车标记setCarintype=1
			 */

			log.info("============Username" + usercrdtm.getUsername() + "===========");
			usercrdtm.setChargeruleid(getUserRuleId(userinf, usercrdtm.getChannelid()));
			log.info("============Chargeruleid" + usercrdtm.getChargeruleid() + "===========");
			// usercrdtm.setLastoutTime(new Date());
			Date lastOutTime = getLastOutTime(usercrdtm);
			// 如果入场时间==最晚离场时间，判断当前车辆是否是长期车，如果是长期车，则最晚离场时间=有效期结束时间
			if (usercrdtm.getCrdtm().getTime() == lastOutTime.getTime()) {
				if (userinf != null) {
					lastOutTime = userinf.getEnddt();
				}
			}
			usercrdtm.setLastoutTime(lastOutTime);
			if (object.containsKey("areaId")) {
				usercrdtm.setAreaId(String.valueOf(object.getIntValue("areaId")));
			}

		}
		return arrayList;

	}

	public boolean isorder(JSONObject object, Tc_usercrdtm usercrdtm) {
		boolean flag = false;
		try {
			if (object.containsKey("licensePlateNumber")) {
				if (JedisPoolUtils.hexists(DataConstants.SHARE_INOUT, object.getString("licensePlateNumber"))) {
					String res = JedisPoolUtils.hget(DataConstants.SHARE_INOUT, object.getString("licensePlateNumber"));
					JSONObject jso_ = JSON.parseObject(res);
					long endtime_ = 0;
					try {
						endtime_ = ThreadLocalDateUtil.parse(jso_.getString("endTime")).getTime();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					log.info("=============Date intime" + usercrdtm.getCrdtm());
					log.info("=============intime_" + usercrdtm.getCrdtm().getTime());
					log.info("============endtime_" + endtime_);
					long intime_ = usercrdtm.getCrdtm().getTime();
					if (endtime_ > intime_) {
						// usercrdtm.setCarintype(1);
						flag = true;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 通过出入场信息到场内表查找场内异常记录
	 * 
	 * @param usercrdtm_list
	 * @return
	 */
	public ArrayList<Tc_usercrdtm_in> getProblemCarList(ArrayList<Tc_usercrdtm> usercrdtm_list) {
		ArrayList<Tc_usercrdtm_in> carsProblemList = new ArrayList<Tc_usercrdtm_in>();
		for (int i = 0; i < usercrdtm_list.size(); i++) {
			Tc_usercrdtm usercrdtm = usercrdtm_list.get(i);
			Tc_usercrdtm_in usercrdtm_in = usercrdtm_inMapper.queryParkingStatebyCarCode(usercrdtm.getCarcode(),
					usercrdtm.getParkinglotno(), getPartitionidin(usercrdtm.getParkinglotno()));
			if (usercrdtm_in != null) {
				carsProblemList.add(usercrdtm_in);
			}
		}
		return carsProblemList;
	}

	/**
	 * 通过出入场信息到场内表查找场内异常记录
	 * 
	 * @param usercrdtm_list
	 * @return
	 */
	public ArrayList<Tc_usercrdtm_in> queryLaterCode(ArrayList<Tc_usercrdtm> usercrdtm_list) {
		log.info("============usercrdtm_listsize()" + usercrdtm_list.size() + "===========");
		ArrayList<Tc_usercrdtm_in> carsProblemList = new ArrayList<Tc_usercrdtm_in>();
		for (int i = 0; i < usercrdtm_list.size(); i++) {
			Tc_usercrdtm usercrdtm = usercrdtm_list.get(i);
			Tc_usercrdtm_in usercrdtm_in = usercrdtm_inMapper.queryLaterCode(usercrdtm.getCarcode(),
					usercrdtm.getParkinglotno(), usercrdtm.getCrdtm());
			if (usercrdtm_in != null) {
				carsProblemList.add(usercrdtm_in);
			}
		}
		return carsProblemList;
	}

	/**
	 * 根据场内异常车辆异常信息
	 * 
	 * @param problemcars
	 * @return
	 */
	public ArrayList<Tc_usercrdtmin_anomaly> setUsercrdtmin_anomaly(ArrayList<Tc_usercrdtm_in> problemcars) {
		ArrayList<Tc_usercrdtmin_anomaly> usercrdtmin_anomalyList = new ArrayList<Tc_usercrdtmin_anomaly>();
		for (int j = 0; j < problemcars.size(); j++) {
			Tc_usercrdtm_in usercrdtm_in = problemcars.get(j);
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

	/**
	 * 通过过车记录对象，生成场内信息对象
	 * 
	 * @param usercrdtmlist
	 * @return
	 */
	public ArrayList<Tc_usercrdtm_in> setUsercrdtm_in(ArrayList<Tc_usercrdtm> usercrdtmlist) {
		ArrayList<Tc_usercrdtm_in> usercrdtm_inlist = new ArrayList<Tc_usercrdtm_in>();

		for (int i = 0; i < usercrdtmlist.size(); i++) {
			Tc_usercrdtm tm = usercrdtmlist.get(i);
			Tc_usercrdtm_in usercrdtm_in = new Tc_usercrdtm_in();
			usercrdtm_in.setRecordid(getParksequenceId());
			usercrdtm_in.setUsername(tm.getUsername());
			usercrdtm_in.setCarcode(tm.getCarcode());
			usercrdtm_in.setChargeruleid(tm.getChargeruleid());
			usercrdtm_in.setCrdtm(tm.getCrdtm());
			usercrdtm_in.setChannelid(tm.getChannelid());
			usercrdtm_in.setImagepath(tm.getImagepath());
			usercrdtm_in.setLastouttime(tm.getLastoutTime());
			usercrdtm_in.setParkinglotno(tm.getParkinglotno());
			usercrdtm_in.setPartitionid(getPartitionidin(tm.getParkinglotno()));
			usercrdtm_in.setCarstyleid(tm.getCarstyleid());
			usercrdtm_in.setCarcode2(tm.getCarcode2());
			usercrdtm_in.setCarintype(tm.getCarintype());
			usercrdtm_in.setAreaId(tm.getAreaId());
			usercrdtm_inlist.add(usercrdtm_in);
		}
		return usercrdtm_inlist;
	}

	/**
	 * 获取分库分表的主键key（出入场）
	 * 
	 * @return
	 */
	public long getEntranceSequenceId() {
		long id;
		try {
			id = sequencEntrance.nextValue();
		} catch (SequenceException e) {
			throw new IllegalArgumentException("sequence netValue error");
		}
		return id;
	}

	/**
	 * 获取分库分表的主键key（场内表）
	 * 
	 * @return
	 */
	public long getParksequenceId() {
		long id;
		try {
			id = sequencePark.nextValue();
		} catch (SequenceException e) {
			throw new IllegalArgumentException("sequence netValue error");
		}
		return id;
	}

	/**
	 * 获取分库分表的主键key（场内表）
	 * 
	 * @return
	 */
	public long getChargesequenceId() {
		long id;
		try {
			id = sequenceCharge.nextValue();
		} catch (SequenceException e) {
			throw new IllegalArgumentException("sequence netValue error");
		}
		return id;
	}

	/**
	 * 出入场表partitionid
	 * 
	 * @param ParkingLotNo
	 * @return
	 */
	public Integer getPartitionid(String ParkingLotNo) {
		int hasresult = StringUtil.getAsciiCode(ParkingLotNo) % 16;
		log.info("=============hasresult" + hasresult + "=============");
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
	 * 场内表partitionid
	 * 
	 * @param ParkingLotNo
	 * @return
	 */
	public int getPartitionidin(String ParkingLotNo) {
		int hasresult = StringUtil.getAsciiCode(ParkingLotNo) % 16;
		return hasresult;
	}

	/**
	 * 获取图片路径
	 * 
	 * @param ParkingLotNo
	 * @param channelId
	 * @param intime
	 * @param flag
	 * @return
	 */
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
		log.info("=============imagPath" + path + "=================================");
		return path;
	}

	/**
	 * 
	 * @param ParkingLotNo
	 *            停车场编号
	 * @param channelId
	 *            通道Id
	 * @param intime
	 *            进场时间
	 * @param inoutid
	 *            进出场ID(原来的userid)
	 * @param userid
	 *            (兼容原有版本的userid)
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
		log.info("=============imagPath" + path + "=================================");
		return path;
	}

	/**
	 * 获取规则id，用户对象为空，则取通道上的规则，不为空则取用户表上的规则id
	 * 
	 * @param userinfo
	 * @param channelid
	 * @return
	 */
	public Integer getUserRuleId(Tc_userinfo userinfo, Integer channelid) {
		Integer ruleId = 0;
		try {
			if (userinfo != null) {
				ruleId = userinfo.getChargeruleid();

			} else {
				ruleId = getRuleID(channelid);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ruleId;
	}

	/**
	 * @param channelid
	 * @return
	 */
	private Integer getRuleID(Integer channelid) {
		Integer ruleId;
		if (JedisPoolUtils.exists(DataConstants.CHANNEL_CACHE_ + channelid)) {

			String channeldate = JedisPoolUtils.get(DataConstants.CHANNEL_CACHE_ + channelid);
			JSONObject jso = JSON.parseObject(channeldate);
			log.info("=============channeldate" + channeldate);

			if (jso.containsKey("chargeruleid")) {
				log.info("=============redis缓存取ruleId");
				ruleId = jso.getIntValue("chargeruleid");
			} else {
				log.info("=============数据库中取");
				Tc_channel channel = channelmapper.selectByPrimaryKey(channelid);
				ruleId = channel.getChargeruleid();
				String fchannel = JSONObject.toJSONString(channel);
				JedisPoolUtils.setex(DataConstants.CHANNEL_CACHE_ + channelid, 3600 * 24, fchannel);
			}

		} else {
			log.info("=============数据库中取");
			Tc_channel channel = channelmapper.selectByPrimaryKey(channelid);
			ruleId = channel.getChargeruleid();
			String channeldate = JSONObject.toJSONString(channel);
			JedisPoolUtils.setex(DataConstants.CHANNEL_CACHE_ + channelid, 3600 * 24, channeldate);

		}
		return ruleId;
	}

	/**
	 * 计算最晚免费离场时间
	 * 
	 * @return
	 */
	public Date getLastOutTime(Tc_usercrdtm usercrdtm) {
		JSONObject requestjson = new JSONObject();
		requestjson.put("parkId", usercrdtm.getParkinglotno());
		Date returnDate = usercrdtm.getCrdtm();
		// Date date;
		try {
			JSONObject CarProp = new JSONObject();
			CarProp.put("start", DateUtil.getPlusTime2(usercrdtm.getCrdtm()));
			log.info("===getCrdtm" + usercrdtm.getCrdtm() + "====");
			log.info("===start" + DateUtil.getPlusTime2(usercrdtm.getCrdtm()) + "====");
			// CarProp.put("end",DateUtil.getNowDateTime());
			// CarProp.put("end",DateUtil.getPlusTime2(new
			// Date(usercrdtm.getCrdtm().getTime()+5000)));
			CarProp.put("end", DateUtil.getPlusTime2(usercrdtm.getCrdtm()));
			CarProp.put("carId", usercrdtm.getCarcode());
			CarProp.put("ruleId", usercrdtm.getChargeruleid());
			// CarProp.put("ruleId", 2);
			log.info("===end" + DateUtil.getPlusTime2(usercrdtm.getCrdtm()) + "====");

			Tc_userinfo userinfo = getuserinfo(usercrdtm.getCarcode(), usercrdtm.getParkinglotno(),
					usercrdtm.getCrdtm(), String.valueOf(usercrdtm.getChannelid()));
			// if (userinfo != null && userinfo.getUserpropertiy() == -1) {
			// returnDate = userinfo.getEnddt();
			// } else {
			if (userinfo != null) {
				// CarProp.put("mstart", userinfo.getBgndt());
				CarProp.put("mstart", DateUtil.getStrTime(userinfo.getBgndt()));
				CarProp.put("mend", DateUtil.getStrTime(userinfo.getEnddt()));
				// DateUtil.getLongToDateStr(userinfo.getEnddt())
			}
			requestjson.put("carProp", CarProp);
			log.info("===requestjson" + requestjson.toJSONString() + "====");
			String afterhalfUrl = "charge/cost/standCharge";

			ResourceBundle bundle = ResourceBundle.getBundle("ControlResource");

			HttpRequest clReq = new HttpRequest(HttpResultType.STRING);
			log.info("requesturl" + bundle.getString("cloudchargeUrl").trim() + afterhalfUrl);
			clReq.setUrl(bundle.getString("cloudchargeUrl").trim() + afterhalfUrl);

			Map<String, String> head = new HashMap<String, String>();
			head.put("Content-Type", "text/plain;charset=UTF-8");
			clReq.setHeadMap(head);
			clReq.setCharset("UTF-8");
			HttpResponse httpreponse = null;
			try {
				httpreponse = HttpProtocolHandler.getInstance().execute(clReq, requestjson.toJSONString());

				String resultjson = httpreponse.getStringResult();

				log.info("===requestjson" + resultjson + "====");
				JSONObject result_ = JSON.parseObject(resultjson);

				log.info("===result_" + result_ + "====");

				String resultvalue = result_.getJSONObject("result").getString("lastOutTime");

				log.info("======resultvalue" + resultvalue + "====");

				try {
					returnDate = ThreadLocalDateUtil.parse(resultvalue);
				} catch (ParseException e) {
					returnDate = usercrdtm.getCrdtm();
					log.info("======date error，计费返还的字符串有问题，字符串是====" + resultvalue);

					e.printStackTrace();
				}

				log.info("======resultvalue" + returnDate + "====");

			} catch (Exception e) {

				returnDate = usercrdtm.getCrdtm();
				e.printStackTrace();
			}
			// }

		} catch (Exception e1) {
			returnDate = usercrdtm.getCrdtm();
			e1.printStackTrace();
		}

		return returnDate;
	}

	/**
	 * 定时生成全包量 ，和缓存最晚离场时间和最晚出场时间比较，有问题的场内信息移动到Abnormal表
	 * 
	 * @param obj
	 */
	public void moveToAbnormal(JSONObject obj) {
		log.info("======准备移动到====Abnormal 表");
		usercrdtm_abnormalMapper.insertabnomal(obj);
		usercrdtm_inMapper.deletebyrecordid(obj.getLongValue("recordid"), obj.getString("parkinglotno"));
		log.info("======场内表数据清理完毕");
	}

	public void GetWithholdInfo(ArrayList<Tc_usercrdtm> usercrdtm_list) {
		log.info("======GetWithholdInfoGetWithholdInfo");
		Tc_usercrdtm usercrdtm = usercrdtm_list.get(0);
		log.info("======GetWithholdInfoGetWithholdInfo" + usercrdtm.getCarcode());
		log.info("======GetWithholdInfoGetWithholdInfo" + usercrdtm.getParkinglotno());
		log.info("======GetWithholdInfoGetWithholdInfo" + usercrdtm.getCrdtm());
		Tc_userinfo user = getuserinfo(usercrdtm.getCarcode(), usercrdtm.getParkinglotno(), usercrdtm.getCrdtm(),
				String.valueOf(usercrdtm.getChannelid()));
		log.info("======useruseruser" + user);
		if (user == null) {
			log.info("======useruseruser" + user);
			JSONObject CarProp = new JSONObject();
			CarProp.put("licensePlateNumber", usercrdtm.getCarcode());
			CarProp.put("carParkId", usercrdtm.getParkinglotno());
			// CarProp.put("carParkId", "0");
			log.info("======getCarcode" + usercrdtm.getCarcode());
			String result = "";
			if (JedisPoolUtils.hexists(DataConstants.SHARE_INOUT, usercrdtm.getCarcode())) {
				result = JedisPoolUtils.hget(DataConstants.SHARE_INOUT, usercrdtm.getCarcode());
			}

			log.info("======resultresult" + result);
			String intime = "";
			String reservation = "0";
			String reservationNumber = "0";
			String reserveTime = "";
			if (result != null && !result.equals("")) {
				JSONObject jso = JSON.parseObject(result);
				reserveTime = jso.getString("ordertime");
				reservationNumber = jso.getString("index");
				reservation = "1";
			}

			try {
				intime = ThreadLocalDateUtil.formatDate(usercrdtm.getCrdtm());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			CarProp.put("inTime", intime);
			CarProp.put("inRecordId", String.valueOf(usercrdtm.getRecordid()));
			CarProp.put("reserveTime", reserveTime);
			CarProp.put("reservationNumber", reservationNumber);
			CarProp.put("reservation", reservation);
			log.info("======CarPropCarPropCarProp" + CarProp.toJSONString());
			// == null不是长期用户才需要去行呗查询是否开通代扣信息
			String afterhalfUrl = "withhold/inRecord.shtml";
			String resultjson = RequestUtil.postUrlForJson("withholdUrl", afterhalfUrl, CarProp.toJSONString());
			log.info("======resultjsonresultjsonresultjson" + resultjson);
		}
	}
}
