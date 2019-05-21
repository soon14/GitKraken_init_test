package cn.rf.hz.web.cloudpark.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.cloudpark.jedis.ConsumerInstance;
import cn.rf.hz.web.cloudpark.jedis.Message;
import cn.rf.hz.web.cloudpark.jedis.ProducerInstance;
import cn.rf.hz.web.cloudpark.jedis.SubThread;
import cn.rf.hz.web.cloudpark.job.IBuildCallback;
import cn.rf.hz.web.cloudpark.service.CarInoutService;
import cn.rf.hz.web.cloudpark.service.SaveCarChargeCacheService;
import cn.rf.hz.web.cloudpark.service.SaveCarOutCacheService;
import cn.rf.hz.web.cloudpark.service.SaveCainToCacheService;
import cn.rf.hz.web.utils.DataConstants;
import cn.rf.hz.web.utils.IPutil;
import cn.rf.hz.web.utils.JedisPoolUtils;

@Service("saveouttocacheserviceimp")
public class SaveOutToCacheServiceImp implements SaveCarOutCacheService {
	private static ResourceBundle res = ResourceBundle.getBundle("config");
	final static Object object = new Object();
	private final static Logger logger = Logger.getLogger(SaveOutToCacheServiceImp.class);
	private static ThreadPoolExecutor executor;

	private final static int corePoolSize = Integer.parseInt(res.getString("corePoolSize"));
	private final static int maxPoolSize = Integer.parseInt(res.getString("maxPoolSize"));
	@Autowired
	private ParkOutInServiceImp parkOutInService;

	static {
		executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(20000));

		logger.info("executor init sucess param=" + executor.getPoolSize());
	}

	@PostConstruct
	public void init() {
		Thread t1 = new Thread() {
			public void run() {

				startsave();
			}
		};
		t1.start();
	}

	public void startsave() {
		System.out.println("saveouttocacheserviceimp init start");
		while (true) {
			try {

				// logger.info("===============1=====================");
				List<String> list = JedisPoolUtils.lrange(IPutil.getIPaddress() + "_" + DataConstants.CAROUTDATA, 0, 50);
				// logger.info("===============2=====================");
				if (list.size() > 0) {
				
					final CountDownLatch end = new CountDownLatch(list.size());

					IBuildCallback callback = new IBuildCallback() {

						@Override
						// 全量包生成成功后更新状态
						public void savecarin(String rmdata, int state) {
							logger.info("===============5=====================");
							if (state > -1) {
								// logger.info("全量包生成成功后更新状态");
								logger.info("插入成功===========，成功后需要删除redis数据" + rmdata);
								JedisPoolUtils.lrem(IPutil.getIPaddress() + "_" + DataConstants.CAROUTDATA, 1, rmdata);
								// taskMapper.updateByparkingNo(carparkNo);
							} else {
								logger.info("失败，删除插入redis另一张表里面===========" + rmdata);
								JedisPoolUtils.lrem(IPutil.getIPaddress() + "_" + DataConstants.CAROUTDATA, 1, rmdata);
								JedisPoolUtils.rpush(IPutil.getIPaddress() + "_" + DataConstants.CAROUTDATAERROR, rmdata);
							}

						}
					};

					for (int index = 0; index < list.size(); index++) {
						executor.execute(new SavedataRunnable(list.get(index), callback, end));
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

/*	public void addTask(List<String> listData, IBuildCallback callback) {

		
		 * try { executor.ex executor.execute(new BuildKRunnable(carparkNos,
		 * callback)); } catch (Exception e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); }
		 
		for (int i = 0; i < listData.size(); i++) {
			try {
				// executor.execute(new SavecarinRunnable(listData.get(i),
				// callback, new CountDownLatch(1)));
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
		}

	}*/

/*	public void reservationHoldingJob() {

		try {
			// System.out.println("1");
			InetAddress net = InetAddress.getLocalHost();
			String ip = net.getHostAddress();

			List<String> list = JedisPoolUtils.lrange(ip + "_" + DataConstants.CAROUTDATA, 0, 50);

			addTask(list, new IBuildCallback() {

				@Override
				// 全量包生成成功后更新状态
				public void savecarin(String rmdata, int state) {
					if (state > -1) {
						// logger.info("全量包生成成功后更新状态");
						logger.info("插入成功===========，成功后需要删除redis数据" + rmdata);
						JedisPoolUtils.lrem(IPutil.getIPaddress() + "_" + DataConstants.CAROUTDATA, 1, rmdata);
						// taskMapper.updateByparkingNo(carparkNo);
					} else {
						logger.info("失败，删除插入redis另一张表里面===========" + rmdata);
						JedisPoolUtils.lrem(IPutil.getIPaddress() + "_" + DataConstants.CAROUTDATA, 1, rmdata);
						JedisPoolUtils.rpush(IPutil.getIPaddress() + "_" + DataConstants.CAROUTDATAERROR, rmdata);
					}

				}
			});
			// executor.execute(new SavecarinRunnable(list, new
			// IBuildCallback()));
			
			 * if (list.size() > 0) { for (int i = 0; i < list.size(); i++) {
			 * String result = carinoutservice.saveCarin(list.get(i)); if
			 * (result.indexOf("success") > -1) { JedisPoolUtils.lrem(ip +
			 * DataConstants.BOXDATA, list.get(i)); } } } else {
			 * logger.info("redis没有数据需要更新"); }
			 
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

	private class SavedataRunnable implements Runnable {
		private String requestbody;
		private IBuildCallback callback;
		private CountDownLatch end;

		public SavedataRunnable(String listData, IBuildCallback callback, CountDownLatch _end) {
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
			result = parkOutInService.saveParkOut(requestbody);
			// flag=generatetaskservice.getAllK(carparkNo, true);
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
		// logger.info("=======================RPC++++++++++++++++++++++++++");
		logger.info("=======================cloudpark 返回参数" + result);
		int re = -1;
		try {
			JSONObject jso = JSON.parseObject(result);
			re = Integer.parseInt(jso.getString("resCode"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("=======================re" + re);
		if (re > -1) {
			logger.info("入库成功" + re);
			callback.savecarin(requestbody, re);
		} else {
			callback.savecarin(requestbody, re);
		}
		/*
		 * try { //Thread.sleep(300); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		// }
		return null;
	}

	@Override
	public String saveCarOutredis(String requestBody) {
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
			JedisPoolUtils.rpush(ip + "_" + DataConstants.CAROUTDATA, requestBody);
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

}
