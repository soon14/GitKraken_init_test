package cn.rf.hz.web.cloudpark.impl;

import java.text.ParseException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.datatunnel.client.DataTunnelPublishClient;

import cn.rf.hz.web.cloudpark.daoxx.Tc_orderUserMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_whiteuserinfoMapper;
import cn.rf.hz.web.cloudpark.moder.Tc_whiteuserinfo;
import cn.rf.hz.web.cloudpark.service.OrderUserInfoService;
import cn.rf.hz.web.sharding.dao.Tc_usercrdtm_inMapper;
import cn.rf.hz.web.utils.BigDataAnalyze;
import cn.rf.hz.web.utils.DataConstants;
import cn.rf.hz.web.utils.JedisPoolUtils;
import cn.rf.hz.web.utils.ThreadLocalDateUtil;

@Service("orderuserinfoservice")
public class OrderUserInfoServiceImp implements OrderUserInfoService {
	private static final Logger logger = LoggerFactory.getLogger(OrderUserInfoServiceImp.class);
	@Autowired
	Tc_orderUserMapper orderUserMapper;

	@Autowired(required = false)
	public SendMessServiceImp sendmessserviceimp;

	@Autowired
	private DataTunnelPublishClient dataTunnelPublishClient;

	@Autowired
	private Tc_whiteuserinfoMapper whiteuserinfoMapper;
	
	@Autowired
	private Tc_usercrdtm_inMapper  usercrdtm_inMapper;
	
	/**
	 * 行呗上传预约用户信息
	 */
	@Override
	public JSONObject uploadorderuser(JSONObject jso) {
		logger.info("welcome，uploadorderuser");
		// TODO Auto-generated method stub
		/**
		 * 1.把上传信息存到数据库，2.存到redis
		 */
		// JSONObject jsop=jso;
		String redisvalue = jso.toJSONString();
		JSONObject resultjso = new JSONObject();
		resultjso.put("index", jso.getString("index"));
		resultjso.put("result", 0);
		String parkinglotno = jso.getString("parkId");
		String _carcode = jso.getString("licensePlateNumber");
		String datetime = jso.getString("ordertime");
		logger.info(jso.toJSONString());
		try {
			if (jso.getString("act").equals("1")) {
				// 预约
				
				
				// 先进场再预约，加判断限制
				
				if(usercrdtm_inMapper.selectcarin(_carcode, parkinglotno)==null){
					//为null ,表示场内没有该车
					Tc_whiteuserinfo whiteuserinfo = whiteuserinfoMapper.orderuser_Is_blackuser(_carcode, parkinglotno,
							ThreadLocalDateUtil.parse(datetime));
					if (whiteuserinfo != null) {
						resultjso.put("result", -2);
						resultjso.put("errorMsg", "该车在该车场属于黑名单");
					} else {
						String endTime = jso.getString("endTime");
						/*jso.remove("endTime");
						jso.put("endTime", ThreadLocalDateUtil.parse(endTime));*/

					/*	String ordertime = jso.getString("ordertime");
						jso.remove("ordertime");
						jso.put("ordertime", ThreadLocalDateUtil.parse(ordertime));*/

						JSONArray arry = new JSONArray();

						arry.add(jso);
						JSONArray arry_new = fixArray(arry);
						
						orderUserMapper.insertorderuserinfo(arry_new.getJSONObject(0));// 插入数据库表

						JedisPoolUtils.hset(DataConstants.SHARE_INOUT, _carcode, redisvalue);

						String key_p = BigDataAnalyze.geListKeyByDataType(parkinglotno, DataConstants.CLOUDSHARE_INOUT,
								"p");

						//long l = JedisPoolUtils.hincrBy(key_p, "autoincrsn", 1);

			
						logger.info("arry11111111111111111" + arry.toJSONString());
						
						logger.info("arry222222222222222" + arry.toJSONString());
						sendmessserviceimp.SendMessshare(arry_new, dataTunnelPublishClient, 0, "in_p", parkinglotno,
								DataConstants.CLOUDSHARE_INOUT);
					}
					
				}else{
					//预约车辆在场内
					resultjso.put("result", -3);
					resultjso.put("errorMsg", "预约车辆在场内，不允许预约");
				}
		

			} else if (jso.getString("act").equals("0")) {
				// 取消预约
				cancleorder(jso,_carcode,parkinglotno);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			resultjso.put("result", -1);
			resultjso.put("errorMsg", e.getMessage());
			e.printStackTrace();
		}

		return resultjso;
	}
	
	@Override
	public void cancleorder(JSONObject jso,String _carcode,String parkinglotno){
		orderUserMapper.deleteorderuserinfo(jso);
		JedisPoolUtils.hdel(DataConstants.SHARE_INOUT, _carcode);

		String key_p = BigDataAnalyze.geListKeyByDataType(parkinglotno, DataConstants.CLOUDSHARE_INOUT, "p");
		logger.info("key_p===" + key_p);
		//long l = JedisPoolUtils.hincrBy(key_p, "autoincrsn", 1);
		//logger.info("l===" + l);
		JSONArray arry = new JSONArray();
		arry.add(jso);
		logger.info("arry11144444" + arry.toJSONString());
		JSONArray arry_new = fixArray(arry);
		logger.info("arry33333333333333" + arry.toJSONString());

		sendmessserviceimp.SendMessshare(arry_new, dataTunnelPublishClient, 0, "out_p", parkinglotno,
				DataConstants.CLOUDSHARE_INOUT);
	}

	@Override
	public JSONObject uploadwithholdinfo(JSONObject jso) {
		JSONObject resultjso =new JSONObject();
		try {
			// TODO Auto-generated method stub
			logger.info("welcome，uploadwithholdinfo");
			logger.info(jso.toJSONString());
			String redisvalue = jso.toJSONString();
			//resultjso = ;
			resultjso.put("result", 0);
			String parkinglotno = jso.getString("parkId");
			String _carcode = jso.getString("licensePlateNumber");
			// 代扣或取消标识, 1-代扣, 0-取消代扣
			if (jso.getString("act").equals("1")) {

				logger.info("开通代扣");
			} else if (jso.getString("act").equals("0")) {
				logger.info("取消代扣代扣");
				logger.info(JedisPoolUtils.hexists(DataConstants.SHARE_INOUT, _carcode)+"");
				if (JedisPoolUtils.hexists(DataConstants.SHARE_INOUT, _carcode)) {
					String res = JedisPoolUtils.hget(DataConstants.SHARE_INOUT, _carcode);
					JedisPoolUtils.hdel(DataConstants.SHARE_INOUT, _carcode);
					orderUserMapper.deleteorderuserinfobycarcode(jso);

					JSONObject jso_ = JSON.parseObject(res);
					parkinglotno=jso_.getString("parkinglotno");
					
					
					String key_p = BigDataAnalyze.geListKeyByDataType(parkinglotno, DataConstants.CLOUDSHARE_INOUT, "p");

					//long l = JedisPoolUtils.hincrBy(key_p, "autoincrsn", 1);

					JSONArray arry = new JSONArray();
					arry.add(jso_);
					// logger.info("arry11144444"+arry.toJSONString());
					JSONArray arry_new = fixArray(arry);
					logger.info("arry2" + arry.toJSONString());
					sendmessserviceimp.SendMessshare(arry_new, dataTunnelPublishClient, 0, "out_p", parkinglotno,
							DataConstants.CLOUDSHARE_INOUT);
				}
				logger.info("取消代扣，但是改车rides没有预约信息，没有预约信息");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			resultjso.put("result", -1);
			e.printStackTrace();
		}
		return resultjso;
	}

	public JSONArray fixArray(JSONArray arry) {

		for (int i = 0; i < arry.size(); i++) {
			// LOG.info("===========arry.size()====================" +
			// arry.size());
			JSONObject object = arry.getJSONObject(i);
			if (object.containsKey("licensePlateNumber")) {
				object.put("carcode", object.getString("licensePlateNumber"));
				object.remove("licensePlateNumber");
			}
			if (object.containsKey("parkId")) {
				object.put("parkinglotno", object.getString("parkId"));
				object.remove("parkId");
			}

			if (object.containsKey("endTime")) {
				try {
					object.put("endtime", ThreadLocalDateUtil.parse(object.getString("endTime")));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				object.remove("endTime");
			}
			if (object.containsKey("ordertime")) {
			
				try {
					object.put("ordertime", ThreadLocalDateUtil.parse(object.getString("ordertime")));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
			}
			

			if (object.containsKey("parkingNumber")) {
				object.put("parkingnumber", object.getString("parkingNumber"));
				object.remove("parkingNumber");
			}
			arry.set(i, object);
		}
		// LOG.info("============消息体：arry修正后" + arry.toJSONString());
		return arry;
	}

	@Override
	public boolean islater(String parkinglotno, String carcode, Date intime) {
		logger.info("判断预约车是否晚到,晚到就要下发预约消息删除接口");
		logger.info("DataConstants.SHARE_INOUT"+DataConstants.SHARE_INOUT);
		logger.info("carcode"+carcode);
		if (JedisPoolUtils.hexists(DataConstants.SHARE_INOUT, carcode)) {
			String res = JedisPoolUtils.hget(DataConstants.SHARE_INOUT, carcode);
			JSONObject jso_ = JSON.parseObject(res);
			long endtime_=0;
			try {
				endtime_ = ThreadLocalDateUtil.parse(jso_.getString("endTime")).getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		/*	1465303535000
			1465309650000*/
			Date f=new Date();

			logger.info("=============Date intime"+intime);
			logger.info("=============intime_"+intime.getTime());
			logger.info("============endtime_"+endtime_);
			long intime_ = intime.getTime();
			if (endtime_ > intime_) {
				return false;
			} else {
				return true;
			}

		}else{
			return false;
		}

	}
	
	
	
	
}
