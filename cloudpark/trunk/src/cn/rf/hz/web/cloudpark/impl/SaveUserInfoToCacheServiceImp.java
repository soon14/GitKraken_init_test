package cn.rf.hz.web.cloudpark.impl;

import java.net.InetAddress;
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
import cn.rf.hz.web.cloudpark.service.SaveUserInfoToCacheService;
import cn.rf.hz.web.cloudpark.service.SendUserInfoMess;
import cn.rf.hz.web.utils.DataConstants;
import cn.rf.hz.web.utils.IPutil;
import cn.rf.hz.web.utils.JedisPoolUtils;

@Service("saveuserinfotocacheserviceimp")
public class SaveUserInfoToCacheServiceImp implements SaveUserInfoToCacheService {
	private static ResourceBundle res = ResourceBundle.getBundle("config");
	final static Object object = new Object();
	private final static Logger logger = Logger.getLogger(SaveUserInfoToCacheServiceImp.class);
	private static ThreadPoolExecutor executor;

	private final static int corePoolSize = Integer.parseInt(res.getString("corePoolSize"));
	private final static int maxPoolSize = Integer.parseInt(res.getString("maxPoolSize"));

	@Autowired(required = false)
	public SendUserInfoMess senduserinfomess;

	static {
		executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(20000));

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
		System.out.println("saveuserinfotocacheserviceimp init start");
		while (true) {
			try {
				List<String> list = JedisPoolUtils
						.lrange(IPutil.getIPaddress() + "_" + DataConstants.CLOUDPARK_USERGROUP, 0, 50);
				// logger.info("saveuserinfotocacheserviceimp:list.size:" +
				// list.size());
				if (list.size() > 0) {
					final CountDownLatch end = new CountDownLatch(list.size());
					IBuildCallback callback = new IBuildCallback() {
						@Override
						// 全量包生成成功后更新状态
						public void savecarin(String rmdata, int state) {
							if (state > -1) {
								logger.info("SaveUserInfoToCacheServiceImp,DeleteRedis,rmdata:" + rmdata);
								JedisPoolUtils.lrem(IPutil.getIPaddress() + "_" + DataConstants.CLOUDPARK_USERGROUP, 1,
										rmdata);
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
				// 生成全量包代码
				Object obj = saveData(requestbody, callback);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				end.countDown();
			}
		}
	}

	public Object saveData(String requestbody, IBuildCallback callback) {
		try {
			uploadUserInfo(requestbody);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error exx) {
			exx.printStackTrace();
			exx.getStackTrace();

		} catch (Throwable ex) {
			ex.printStackTrace();
			ex.getStackTrace();
		}
		callback.savecarin(requestbody, 0);
		return null;
	}

	public void uploadUserInfo(String requestbody) {
		JSONObject body = JSONObject.parseObject(requestbody);
		JSONArray data = body.getJSONArray("data");
		for (int i = 0; i < data.size(); i++) {
			JSONArray carcodes = data.getJSONObject(i).getJSONArray("change_carcodes");
			JSONObject result = new JSONObject();
			result.put("parkingid", body.getString("parkingid"));
			result.put("topic", body.getString("topic"));
			result.put("msgtype", body.getString("msgtype"));
			JSONArray array = new JSONArray();
			for (int j = 0; j < carcodes.size(); j++) {
				JSONObject object = new JSONObject();
				object.put("carcode", carcodes.getString(j));
				object.put("userpropertiy", data.getJSONObject(i).getInteger("userpropertiy"));
				object.put("chargeruleid", data.getJSONObject(i).getInteger("chargeruleid"));
				object.put("parkinglot", data.getJSONObject(i).getInteger("parkinglot"));
				object.put("bgndt", data.getJSONObject(i).getLong("bgndt"));
				object.put("enddt", data.getJSONObject(i).getLong("enddt"));
				object.put("type", data.getJSONObject(i).getInteger("type"));
				object.put("userid", data.getJSONObject(i).getInteger("userid"));
				object.put("isfixed", data.getJSONObject(i).getInteger("isfixed"));
				object.put("username", data.getJSONObject(i).getString("username"));
				object.put("carcodes", new JSONArray());
				object.put("channelids", data.getJSONObject(i).getJSONArray("channelids"));
				object.put("areaids", new JSONArray());
				object.put("change_carcodes", new JSONArray());
				array.add(object);
				
			}
			result.put("data", array);
			logger.info("SaveUserInfoToCacheServiceImp,uploadUserInfo,result:" + result.toJSONString());
			JSONObject returnJson = senduserinfomess.addUserInfomation2(result.toJSONString());
			if (returnJson.getInteger("resCode") == 0) {
				logger.info("SaveUserInfoToCacheServiceImp,uploadUserInfo,returnJson:" + returnJson.toJSONString());
			} else {
				Boolean isSuccess = false;
				for (int m = 0; m < 3; m++) {
					JSONObject newreturnJson = senduserinfomess.addUserInfomation2(result.toJSONString());
					if (newreturnJson.getInteger("resCode") == 0) {
						isSuccess = true;
						logger.info("SaveUserInfoToCacheServiceImp,uploadUserInfo,returnJson:"
								+ returnJson.toJSONString() + ",success m:" + m);
						break;
					}
				}
				if (!isSuccess) {
					logger.info("SaveUserInfoToCacheServiceImp,uploadUserInfo,failed:" + result.toJSONString());
				}
			}
		}
	}

	@Override
	public String saveUserInfoRedis(String requestBody) {
		JSONObject reMsg = new JSONObject();
		int resCode = 0;
		try {
			JSONObject data = JSON.parseObject(requestBody);
			String ParkingLotNo = data.getString("parkingid");

			reMsg.put("parkNo", ParkingLotNo);
			reMsg.put("sign", "");
			reMsg.put("resCode", resCode);
			reMsg.put("resMsg", "success");
			InetAddress net = InetAddress.getLocalHost();
			String ip = net.getHostAddress();
			JedisPoolUtils.rpush(ip + "_" + DataConstants.CLOUDPARK_USERGROUP, requestBody);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			reMsg.put("resCode", -1);
			reMsg.put("resMsg", "fail," + e.getMessage());
			e.printStackTrace();
		}
		return reMsg.toJSONString();
	}
}
