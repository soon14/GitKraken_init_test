package cn.rf.hz.web.cloudpark.impl;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.cloudpark.job.IBuildCallback;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm_in;
import cn.rf.hz.web.cloudpark.service.CarInoutService;
import cn.rf.hz.web.cloudpark.service.SaveCainToCacheService;
import cn.rf.hz.web.utils.DataConstants;
import cn.rf.hz.web.utils.IPutil;
import cn.rf.hz.web.utils.JedisPoolUtils;

@Service("saveCainToCacheService")
public class SaveCainToCacheServiceImp implements SaveCainToCacheService {

	private static ResourceBundle res = ResourceBundle.getBundle("config");

	final static Object object = new Object();
	private final static Logger logger = Logger.getLogger(SaveCainToCacheServiceImp.class);
	private static ThreadPoolExecutor executor;

	private final static int corePoolSize = Integer.parseInt(res.getString("corePoolSize"));
	private final static int maxPoolSize = Integer.parseInt(res.getString("maxPoolSize"));
	@Autowired
	private CarInoutService carinoutservice;
	@Autowired
	private ParkOutInServiceImp parkOutInService;

	@Autowired
	private PublicParkingService publicparkingservice;
	static {
		executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(20000));

		logger.info("executor init sucess param=" + executor.getPoolSize());

		// init();
	}

	/*
	 * public static void init() { logger.info("111111"); //for (int a = 0; a <
	 * 40; a++) { SubThread subThread = new SubThread(); logger.info("22222");
	 * //subThread.start(); logger.info("333"); //}
	 * 
	 * Thread t1 = new Thread() { public void run() { System.out.println(
	 * "T1 start!"); try { ConsumerInstance.getinstance().consum("carinout"); }
	 * catch (IOException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } } }; t1.start();
	 * 
	 * // }
	 * 
	 * // consumer.consum("chn1"); }
	 */

	public void init2() {
		// System.out.println("T1 end!");
		/*
		 * Thread t1 = new Thread() { public void run() { synchronized (object)
		 * { System.out.println("T1 start!"); while (true) { try {
		 * object.wait(300);
		 * 
		 * reservationHoldingJob(); } catch (InterruptedException e) {
		 * e.printStackTrace(); } System.out.println("T1 end!"); }
		 * 
		 * } } };
		 */

	}

	@PostConstruct
	public void init() {
		System.out.println("SaveCainToCacheServiceImp init start");
		Thread t1 = new Thread() {
			public void run() {

				startsave();
			}
		};
		t1.start();
	}

	public void startsave() {
		// System.out.println("init start");

		System.out.println("init start");
		while (true) {
			try {

				// logger.info("===============1=====================");
				List<String> list = JedisPoolUtils.lrange(IPutil.getIPaddress() + "_" + DataConstants.CARINDATA, 0, 50);
				// logger.info("===============2=====================");
				if (list.size() > 0) {
					logger.info("===============3=====================");
					final CountDownLatch end = new CountDownLatch(list.size());
					logger.info("===============4=====================");
					IBuildCallback callback = new IBuildCallback() {

						@Override
						// 全量包生成成功后更新状态
						public void savecarin(String rmdata, int state) {
							logger.info("===============5=====================");
							if (state > -1) {
								// logger.info("全量包生成成功后更新状态");
								logger.info("插入成功===========，成功后需要删除redis数据" + rmdata);
								JedisPoolUtils.lrem(IPutil.getIPaddress() + "_" + DataConstants.CARINDATA, 1, rmdata);

								// taskMapper.updateByparkingNo(carparkNo);
							} else {
								logger.info("===============6=====================");
								logger.info("失败，删除插入redis另一张表里面===========" + rmdata);
								JedisPoolUtils.lrem(IPutil.getIPaddress() + "_" + DataConstants.CARINDATA, 1, rmdata);
								JedisPoolUtils.rpush(IPutil.getIPaddress() + "_" + DataConstants.CARINDATAERROR,
										rmdata);
							}

						}
					};

					for (int index = 0; index < list.size(); index++) {
						logger.info("===============7=====================");
						executor.execute(new SavecarinRunnable(list.get(index), callback, end));
					}
					try {
						logger.info("===============8=====================");
						end.await();
						logger.info("===============任务结束 Over===========");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						// logger.info("===============9=====================");
						Thread.sleep(300);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

	}

	private class SavecarinRunnable implements Runnable {
		private String requestbody;
		private IBuildCallback callback;
		private CountDownLatch end;

		public SavecarinRunnable(String listData, IBuildCallback callback, CountDownLatch _end) {
			// logger.info(listData);
			// logger.info(callback);
			this.requestbody = listData;
			this.callback = callback;
			this.end = _end;
			Thread current = Thread.currentThread();
			logger.info("main id=" + current.getId());
		}

		@Override
		public void run() {
			try {
				logger.info("1");
				logger.info("2");
				// 生成全量包代码
				Object obj = saveData(requestbody, callback);
				logger.info("3");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				logger.info("8");
				end.countDown();
			}
		}
	}

	public Object saveData(String requestbody, IBuildCallback callback) {
		// for (String list : lists) {
		logger.info("4");
		logger.info("4");
		String result = "";
		// boolean flag=false;
		try {
			result = carinoutservice.saveCarin(requestbody);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Error exx) {
			logger.info("14");
			exx.printStackTrace();
			exx.getStackTrace();

		} catch (Throwable ex) {
			logger.info("15");
			ex.printStackTrace();
			ex.getStackTrace();

		}
		logger.info("SaveCainToCacheServiceImp.saveData.result:" + result);
		int resCode = -1;
		try {
			JSONObject jsonResult = JSON.parseObject(result);
			resCode = Integer.parseInt(jsonResult.getString("resCode"));
			logger.info("SaveCainToCacheServiceImp.saveData.rescode:" + resCode);
			if (resCode > -1) {

				// 场内记录
				String arrayUsercrdtm = jsonResult.getString("usercrdtm_list");
				ArrayList<Tc_usercrdtm> usercrdtmList = (ArrayList<Tc_usercrdtm>) JSONArray.parseArray(arrayUsercrdtm,
						Tc_usercrdtm.class);
				// 进出场记录
				String arrayUsercrdtmIn = jsonResult.getString("usercrdtmin_list");
				ArrayList<Tc_usercrdtm_in> usercrdtmInList = (ArrayList<Tc_usercrdtm_in>) JSONArray
						.parseArray(arrayUsercrdtmIn, Tc_usercrdtm_in.class);
				// 车场ID
				String parkinglotNo = jsonResult.getString("parkNo");
				// 入库成功,则发kafka消息
				sendMessage2KafKa(parkinglotNo, usercrdtmInList, usercrdtmList);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			callback.savecarin(requestbody, resCode);
		}
		return null;
	}

	@Override
	public String saveCarinredis(String requestBody) {
		// TODO Auto-generated method stub
		JSONObject reMsg = new JSONObject();
		int resCode = 0;
		try {
			JSONObject data = JSON.parseObject(requestBody);
			String ParkingLotNo = data.getString("parkNo");
			reMsg.put("parkNo", ParkingLotNo);
			reMsg.put("sign", "");
			reMsg.put("resCode", resCode);
			reMsg.put("resMsg", "success");
			InetAddress net = InetAddress.getLocalHost();
			String ip = net.getHostAddress();
			JedisPoolUtils.rpush(ip + "_" + DataConstants.CARINDATA, requestBody);
			/* logger.info("=======================shengchang"); */
			/*
			 * String channel = "carinout";
			 * ProducerInstance.getinstance().provide(channel, requestBody);
			 * 
			 * ConsumerInstance.getinstance().consum(channel);
			 * logger.info("=======================shengchangjieshu");
			 */
			// producer.provide("chn1",msg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			reMsg.put("resCode", -1);
			reMsg.put("resMsg", "fail," + e.getMessage());
			e.printStackTrace();
		}
		/*
		 * synchronized (object) { object.notify(); }
		 */
		return reMsg.toJSONString();
	}

	/**
	 * @Title: sendMessage2KafKa @Description: 发送消息到kafka @param parkinglotNo
	 *         停车场ID @param usercrdtm_inList 场内记录集合 @param usercrdtmList
	 *         进出场记录集合 @return void @throws
	 */
	private void sendMessage2KafKa(String parkinglotNo, ArrayList<Tc_usercrdtm_in> usercrdtm_inList,
			ArrayList<Tc_usercrdtm> usercrdtmList) {
		// 循环判断车辆标签是否异常
		for (Tc_usercrdtm_in item : usercrdtm_inList) {
			String key = "carTag_" + item.getCarcode();
			logger.error("doCarTagSetting.carTag1:" + JedisPoolUtils.get(key));
			String jsonArrayStr = JedisPoolUtils.get(key);
			if (null != jsonArrayStr) {
				JSONArray jsonArray = JSONArray.parseArray(jsonArrayStr);
				// 对车辆标签（长期车标、临停车标）异常的数据发消息到kafka
				parkOutInService.sendcarTagAnomaly2KafKa(parkinglotNo, jsonArray);
				JedisPoolUtils.del(key);
				logger.error("doCarTagSetting.carTag2:" + JedisPoolUtils.get(key));
			}
		}
		parkOutInService.sendKafkaMes(parkinglotNo, usercrdtm_inList, "in_p");
		parkOutInService.sendKafkaMesusercrdtm(parkinglotNo, usercrdtmList, "in_p");
		publicparkingservice.GetWithholdInfo(usercrdtmList);
	}

}
