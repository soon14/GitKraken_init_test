package cn.rf.hz.web.cloudpark.impl;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.cloudpark.daoxx.Tc_usercrdtmin_anomalyMapper;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm_in;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtmin_anomaly;
import cn.rf.hz.web.cloudpark.service.CarInoutService;
import cn.rf.hz.web.cloudpark.service.OrderUserInfoService;
import cn.rf.hz.web.sharding.dao.Tc_usercrdtmMapper;
import cn.rf.hz.web.sharding.dao.Tc_usercrdtm_inMapper;

@Service("carinoutservice")
public class CarInoutServiceImp implements CarInoutService {
	final static Object object = new Object();
	private final static Logger log = Logger.getLogger(CarInoutServiceImp.class);
	@Autowired
	PublicParkingService publicparkingservice;

	@Autowired
	private Tc_usercrdtmMapper usercrdtmMapper;

	@Autowired
	private Tc_usercrdtm_inMapper usercrdtm_inMapper;

	@Autowired
	Tc_usercrdtmin_anomalyMapper usercrdtmin_anomalyMapper;

	@Autowired
	private ParkOutInServiceImp parkOutInService;

	@Autowired
	private OrderUserInfoService orderuserinfoservice;

	@Override
	public String saveCarin(String requestBody) {

		int resCode = 0;
		JSONObject reMsg = new JSONObject();
		// JSONObject object_ = null;
		try {
			JSONObject data = JSON.parseObject(requestBody);
			String ParkingLotNo = data.getString("parkNo");
			reMsg.put("parkNo", ParkingLotNo);
			reMsg.put("sign", "");
			reMsg.put("resCode", resCode);
			reMsg.put("resMsg", "success");
			// object.containsKey("licensePlateNumber")
			// object_ = data.getJSONArray("data").getJSONObject(0);

			// 1.保存出入场数据，只剔重，无论是否晚到所有有效记录都应该保存；
			ArrayList<Tc_usercrdtm> usercrdtm_list = publicparkingservice.setUsercrdtm(requestBody);
			// log.info("====================" + usercrdtm_list +
			// "================");

			ArrayList<Tc_usercrdtm_in> usercrdtmin = publicparkingservice.setUsercrdtm_in(usercrdtm_list);
			// log.fatal("----------0------");
			// TODO: 以下程序先做批量插入，如果有失败，改成单条插入，先保证正确性，再保证性能
			// TODO: 加一张异常记录表，如果有失败，干到异常记录表中，后续重做，备查！
			try {
				this.usercrdtmMapper.batchInsertUsercrdtm(usercrdtm_list);
			} catch (Exception e) {
				log.error("----------The record has been processed, duplicated record, Abaondon it!3------"
						+ e.getMessage());
				e.printStackTrace();
				return reMsg.toJSONString();
			}

			boolean flag = true; // 用于异常（死锁）情况判断，true为正常，false为异常
			// 2.场内表，先尝试插入，如果有UK报错，证明该车已在场内，更新。
			// 这段程序无法判断是否是异常记录
			try {
				// log.fatal("----------3------");
				this.usercrdtm_inMapper.batchInsertUsercrdtm_in(usercrdtmin);
				// log.fatal("----------4------");
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: 所有字段全部更新
				log.error("CarInoutServiceImp.saveCarin.exception1:" + e.getMessage());
				log.error("CarInoutServiceImp.saveCarin.usercrdtmin:" + usercrdtmin.toString());
				try {
					ArrayList<Tc_usercrdtm_in> problemcars = publicparkingservice.getProblemCarList(usercrdtm_list);
					try {
						this.usercrdtm_inMapper.batchReplaceOldUsercrdtm_in(usercrdtmin);
					} catch (Exception ex) {
						ex.printStackTrace();
						// 更新出错
						flag = false;
						log.error("CarInoutServiceImp.saveCarin.exception2:" + e.getMessage());
						log.error(
								"CarInoutServiceImp.saveCarin.problemcars:" + problemcars.toString() + ",flag:" + flag);
					}
					ArrayList<Tc_usercrdtmin_anomaly> usercrdtmin_anomalyList = new ArrayList<Tc_usercrdtmin_anomaly>();
					if (problemcars.size() > 0) {
						if (problemcars.get(0).getCrdtm().getTime() > usercrdtm_list.get(0).getCrdtm().getTime()) {
							usercrdtmin_anomalyList = publicparkingservice.setUsercrdtmin_anomaly(usercrdtmin);
						} else {
							usercrdtmin_anomalyList = publicparkingservice.setUsercrdtmin_anomaly(problemcars);
						}
					}
					if (usercrdtmin_anomalyList.size() > 0) { // 假设有异常就插入异常表
						this.usercrdtmin_anomalyMapper.batchInsertusercrdtmin_anomaly(usercrdtmin_anomalyList);
						// parkOutInService.sendKafkaAnoMes(ParkingLotNo,
						// usercrdtmin_anomalyList,"in_p");
					}
				} catch (Exception e1) {
					log.error("ERRORMESSAGE===" + e1.getMessage() + "====");
					e1.printStackTrace();
				}
			}
			log.error("CarInoutServiceImp.saveCarin.flag:" + flag);
			// 无异常出现，则发消息
			if (flag) {
				parkOutInService.sendKafkaMes(ParkingLotNo, usercrdtmin, "in_p");
				parkOutInService.sendKafkaMesusercrdtm(ParkingLotNo, usercrdtm_list, "in_p");
				publicparkingservice.GetWithholdInfo(usercrdtm_list);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			reMsg.put("resCode", -1);
			reMsg.put("resMsg", "fail");
			log.error("ssssssss" + e.getMessage());
			e.printStackTrace();

		} finally {
			// JedisPoolUtils.unlock("carin_lock"+object_.getString("licensePlateNumber"),"carin");
		}
		// TODO 1.设置需要保存场内对象
		return reMsg.toJSONString();

	}

	/*
	 * @Override public String saveCarin(String requestBody) {
	 * 
	 * int resCode = 0; JSONObject reMsg = new JSONObject(); JSONObject
	 * object_=null; try {
	 * 
	 * JSONObject data = JSON.parseObject(requestBody); String ParkingLotNo =
	 * data.getString("parkNo"); reMsg.put("parkNo", ParkingLotNo);
	 * reMsg.put("sign", ""); reMsg.put("resCode", resCode); reMsg.put("resMsg",
	 * "success"); //object.containsKey("licensePlateNumber") object_ =
	 * data.getJSONArray("data").getJSONObject(0); //long l =
	 * JedisPoolUtils.hincrBy(, "autoincrsn", 1);
	 * //JedisPoolUtils.lock("carin_lock"+object_.getString("licensePlateNumber"
	 * ), "carin"); // TODO 1.设置需要保存出入场对象 ArrayList<Tc_usercrdtm> usercrdtm_list
	 * = publicparkingservice.setUsercrdtm(requestBody);
	 * log.info("====================" + usercrdtm_list + "================");
	 * // TODO 2进场车辆场内表有，返回场内存在有问题的记录集合 ArrayList<Tc_usercrdtm_in> problemcars =
	 * publicparkingservice.getProblemCarList(usercrdtm_list);
	 * 
	 * ArrayList<Tc_usercrdtm_in> lastercars =
	 * publicparkingservice.queryLaterCode(usercrdtm_list);
	 * 
	 * // problemcars.size() > 0 场内有该车,需要删除改车辆，并且把这条记录移到异常出场记录
	 * log.info("====================problemcars.size" + problemcars.size() +
	 * "================");
	 * log.info("====================lastercars.size11111111" +
	 * lastercars.size() + "================");
	 * 
	 * if (lastercars.size() == 0) { //是否有晚到入场 if (problemcars.size() > 0) {
	 * //没有，再判断场内是否有数据 // 场内有该车,需要删除改车辆
	 * usercrdtm_inMapper.batchDelUsercrdtm_in(problemcars); // 通过场内问题车辆获得异常记录
	 * ArrayList<Tc_usercrdtmin_anomaly> usercrdtmin_anomalyList =
	 * publicparkingservice .setUsercrdtmin_anomaly(problemcars); if
	 * (usercrdtmin_anomalyList.size() > 0) { // 假设有异常就插入异常表
	 * this.usercrdtmin_anomalyMapper.batchInsertusercrdtmin_anomaly(
	 * usercrdtmin_anomalyList); } }
	 * 
	 * }
	 * 
	 * ArrayList<Tc_usercrdtm_in> usercrdtmin =
	 * publicparkingservice.setUsercrdtm_in(usercrdtm_list);
	 * log.fatal("----------0------"); try {
	 * this.usercrdtmMapper.batchInsertUsercrdtm(usercrdtm_list); } catch(final
	 * DuplicateKeyException e) { e.printStackTrace();
	 * log.fatal("----------GOON------"); }
	 * 
	 * log.fatal("----------1------"); if (lastercars.size() == 0) {
	 * //是否是晚到入场，不是就新增
	 * //this.usercrdtm_inMapper.batchInsertUsercrdtm_in(usercrdtmin);
	 * 
	 * try { log.fatal("----------2------"); //usercrdtmin1 = usercrdtmin;
	 * Tc_usercrdtm_in usercrdtmin2 = usercrdtmin.get(0);
	 * usercrdtmin2.setRecordid(usercrdtmin2.getRecordid()+10000);
	 * ArrayList<Tc_usercrdtm_in> usercrdtminc =new
	 * ArrayList<Tc_usercrdtm_in>(); usercrdtminc.add(usercrdtmin2);
	 * log.fatal("----------3------");
	 * this.usercrdtm_inMapper.batchInsertUsercrdtm_in(usercrdtminc);
	 * log.fatal("----------4------"); } catch(final DuplicateKeyException e) {
	 * e.printStackTrace(); log.fatal(
	 * "----------TODO: UPDATE THE DUPLICAED RECORDS------"); } } else { //
	 * 是，先把该次入场放到出入场，不新增到场内 ArrayList<Tc_usercrdtmin_anomaly>
	 * usercrdtmin_anomalyList = publicparkingservice
	 * .setUsercrdtmin_anomaly(usercrdtmin); if (usercrdtmin_anomalyList.size()
	 * > 0) { // 假设有异常就插入异常表
	 * this.usercrdtmin_anomalyMapper.batchInsertusercrdtmin_anomaly(
	 * usercrdtmin_anomalyList); }
	 * 
	 * } //parkOutInService.sendKafkaMes(ParkingLotNo, usercrdtmin);
	 * log.info("准备调用行呗上传入场记录");
	 * 
	 * //publicparkingservice.GetWithholdInfo(usercrdtm_list); } catch
	 * (Exception e) { // TODO Auto-generated catch block
	 * 
	 * reMsg.put("resCode", -1); reMsg.put("resMsg", "fail");
	 * e.printStackTrace(); }finally {
	 * //JedisPoolUtils.unlock("carin_lock"+object_.getString(
	 * "licensePlateNumber"),"carin"); } // TODO 1.设置需要保存场内对象 return
	 * reMsg.toJSONString(); }
	 */
	@Override
	public String updateCarin(String requestBody) {
		// TODO Auto-generated method stub
		JSONObject res = new JSONObject();
		try {
			JSONObject ob = JSON.parseObject(requestBody);
			Tc_usercrdtm_in tc_in = usercrdtm_inMapper.selectcarin(ob.getString("carcode"),
					ob.getString("parkinglotno"));
			if (tc_in != null) {
				JSONObject data = ob.getJSONObject("data");
				tc_in.setUsername(data.getString("username"));
				if (data.containsKey("chargeruleid")) {
					tc_in.setChargeruleid(data.getIntValue("chargeruleid"));
				}
				tc_in.setCarintype(data.getIntValue("carintype"));
				ArrayList<Tc_usercrdtm_in> usercrdtmin = new ArrayList<Tc_usercrdtm_in>();
				usercrdtmin.add(tc_in);
				usercrdtm_inMapper.batchUpdateUsercrdtm_in(usercrdtmin);
				parkOutInService.sendKafkaMes(ob.getString("parkinglotno"), usercrdtmin, "in_p");
			} else {
				res.put("resultcode", -1);
				res.put("msg", "该车辆不在场内");
			}

		} catch (Exception e) {
			res.put("resultcode", -1);
			res.put("msg", e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res.toJSONString();
	}

	@Override
	public String saveCarout(String requestBody) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @Override public String saveCarinredis(String requestBody) { // TODO
	 * Auto-generated method stub JSONObject reMsg = new JSONObject(); int
	 * resCode = 0; try { JSONObject data = JSON.parseObject(requestBody);
	 * String ParkingLotNo = data.getString("parkNo"); reMsg.put("parkNo",
	 * ParkingLotNo); reMsg.put("sign", ""); reMsg.put("resCode", resCode);
	 * reMsg.put("resMsg", "success"); InetAddress net =
	 * InetAddress.getLocalHost(); String ip = net.getHostAddress();
	 * JedisPoolUtils.rpush(ip + DataConstants.BOXDATA, requestBody); } catch
	 * (Exception e) { // TODO Auto-generated catch block reMsg.put("resCode",
	 * -1); reMsg.put("resMsg", "fail," + e.getMessage()); e.printStackTrace();
	 * } synchronized (object) { object.notify(); } return reMsg.toJSONString();
	 * }
	 */

	/*
	 * public void init() { Thread t1 = new Thread() { public void run(){
	 * synchronized (object) { System.out.println("T1 start!"); while(true){ try
	 * { object.wait(200); reservationHoldingJob(); } catch
	 * (InterruptedException e) { e.printStackTrace(); } System.out.println(
	 * "T1 end!"); }
	 * 
	 * } } }; t1.start(); }
	 */

	/*
	 * public void reservationHoldingJob() {
	 * 
	 * try { // System.out.println("1"); InetAddress net =
	 * InetAddress.getLocalHost(); String ip = net.getHostAddress();
	 * List<String> list = JedisPoolUtils.lrange(ip + DataConstants.BOXDATA, 0,
	 * 50);
	 * 
	 * if (list.size() > 0) { for (int i = 0; i < list.size(); i++) { String
	 * result = saveCarin(list.get(i)); if (result.indexOf("success") > -1) {
	 * JedisPoolUtils.lrem(ip + DataConstants.BOXDATA, list.get(i)); } } } else
	 * { log.info("redis没有数据需要更新"); } } catch (UnknownHostException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } }
	 */

	/*
	 * public static void main(String[] args) { new
	 * ClassPathXmlApplicationContext("spring-jobbean.xml"); try {
	 * Thread.sleep(3000); synchronized (object) { object.notify(); }
	 * 
	 * } catch (InterruptedException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 */

}
