package cn.rf.hz.web.cloudpark.impl;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.reformer.datatunnel.client.DataTunnelPublishClient;

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

	@Autowired
	private DataTunnelPublishClient dataTunnelPublishClient;

	@Override
	public String saveCarin(String requestBody) {

		int resCode = 0;
		JSONObject result = new JSONObject();
		// JSONObject object_ = null;
		try {
			JSONObject data = JSON.parseObject(requestBody);
			String ParkingLotNo = data.getString("parkNo");
			result.put("parkNo", ParkingLotNo);
			result.put("sign", "");
			result.put("resCode", resCode);
			result.put("resMsg", "success");
			// object.containsKey("licensePlateNumber")
			// object_ = data.getJSONArray("data").getJSONObject(0);

			// 1.保存出入场数据，只剔重，无论是否晚到所有有效记录都应该保存；
			ArrayList<Tc_usercrdtm> usercrdtm_list = publicparkingservice.setUsercrdtm(requestBody);
			// log.info("====================" + usercrdtm_list +
			// "================");
			ArrayList<Tc_usercrdtm_in> usercrdtmIn_list = publicparkingservice.setUsercrdtm_in(usercrdtm_list);
			// log.fatal("----------0------");
			// TODO: 以下程序先做批量插入，如果有失败，改成单条插入，先保证正确性，再保证性能
			// TODO: 加一张异常记录表，如果有失败，干到异常记录表中，后续重做，备查！
			try {
				this.usercrdtmMapper.batchInsertUsercrdtm(usercrdtm_list);
			} catch (Exception e) {
				log.error("----------The record has been processed, duplicated record, Abaondon it!3------"
						+ e.getMessage());
				e.printStackTrace();
				return result.toJSONString();
			}

			boolean flag = true; // 用于异常（死锁）情况判断，true为正常，false为异常
			// 2.场内表，先尝试插入，如果有UK报错，证明该车已在场内，更新。
			// 这段程序无法判断是否是异常记录
			try {
				// log.fatal("----------3------");
				int i = this.usercrdtm_inMapper.batchInsertUsercrdtm_in(usercrdtmIn_list);
				log.info("saveCarin.usercrdtm_inMapper.batchInsertUsercrdtm_in:" + i);
				// log.fatal("----------4------");
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: 所有字段全部更新
				log.error("CarInoutServiceImp.saveCarin.exception1:" + e.getMessage());
				log.error("CarInoutServiceImp.saveCarin.usercrdtmin:" + usercrdtmIn_list.toString());
				try {
					ArrayList<Tc_usercrdtm_in> problemcars = publicparkingservice.getProblemCarList(usercrdtm_list);
					try {
						int i = this.usercrdtm_inMapper.batchReplaceOldUsercrdtm_in(usercrdtmIn_list);
						log.info("saveCarin.usercrdtm_inMapper.batchReplaceOldUsercrdtm_in:" + i);
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
							usercrdtmin_anomalyList = publicparkingservice.setUsercrdtmin_anomaly(usercrdtmIn_list);
						} else {
							usercrdtmin_anomalyList = publicparkingservice.setUsercrdtmin_anomaly(problemcars);
						}
					}
					if (usercrdtmin_anomalyList.size() > 0) { // 假设有异常就插入异常表
						int i = this.usercrdtmin_anomalyMapper.batchInsertusercrdtmin_anomaly(usercrdtmin_anomalyList);
						log.info("saveCarin.usercrdtm_inMapper.batchInsertusercrdtmin_anomaly:" + i);
						// parkOutInService.sendKafkaAnoMes(ParkingLotNo,
						// usercrdtmin_anomalyList,"in_p");
					}
				} catch (Exception e1) {
					log.error("ERRORMESSAGE" + e1.getMessage());
					e1.printStackTrace();
				}
			}
			log.error("CarInoutServiceImp.saveCarin.flag:" + flag);
			// 无异常出现，则发消息
			if (flag) {
				result.put("usercrdtm_list", usercrdtm_list);
				result.put("usercrdtmin_list", usercrdtmIn_list);
			}
		} catch (Exception e) {
			result.put("resCode", -1);
			result.put("resMsg", "failed");
			e.printStackTrace();
		} finally {
		}
		// TODO 1.设置需要保存场内对象
		return result.toJSONString();

	}

	@Override
	public String updateCarin(String requestBody) {
		JSONObject res = new JSONObject();
		try {
			JSONObject ob = JSON.parseObject(requestBody);
			log.info("CarInoutServiceImp.updateCarin.req:" + ob);
			Tc_usercrdtm_in tc_in = usercrdtm_inMapper.selectcarin(ob.getString("carcode"),
					ob.getString("parkinglotno"));
			if (tc_in != null) {
				JSONObject data = ob.getJSONObject("data");
				tc_in.setUsername(data.getString("username"));
				if (data.containsKey("chargeruleid")) {
					tc_in.setChargeruleid(data.getIntValue("chargeruleid"));
				}
				if (data.containsKey("lastouttime")) {
					tc_in.setLastouttime(data.getDate("lastouttime"));
				}
				tc_in.setCarintype(data.getIntValue("carintype"));
				ArrayList<Tc_usercrdtm_in> usercrdtmin = new ArrayList<Tc_usercrdtm_in>();
				usercrdtmin.add(tc_in);
				usercrdtm_inMapper.batchUpdateUsercrdtm_in(usercrdtmin);
				parkOutInService.sendKafkaMes(ob.getString("parkinglotno"), usercrdtmin, "in_p");
			} else {
				res.put("resultcode", -1);
				res.put("msg", "the car is not in parking");
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

}
