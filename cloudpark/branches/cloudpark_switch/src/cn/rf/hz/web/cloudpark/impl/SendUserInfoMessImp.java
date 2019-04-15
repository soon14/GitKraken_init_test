package cn.rf.hz.web.cloudpark.impl;

import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.datatunnel.client.DataTunnelPublishClient;
import com.reformer.datatunnel.client.dto.MessageDto;

import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm_in;
import cn.rf.hz.web.cloudpark.service.SendUserInfoMess;
import cn.rf.hz.web.utils.BigDataAnalyze;
import cn.rf.hz.web.utils.DataConstants;
import cn.rf.hz.web.utils.JedisPoolUtils;

@Service("senduserinfomess")
public class SendUserInfoMessImp implements SendUserInfoMess {

	@Autowired
	private DataTunnelPublishClient dataTunnelPublishClient;
	private final static Logger logger = Logger.getLogger(SendUserInfoMessImp.class);

	@Override
	public void addUserInfomation(String data) {
		logger.info("Welcome=====SendUserInfoMessImp");
		JSONObject object = JSON.parseObject(data);

		logger.info("object=====object" + object.toJSONString());
		long l = JedisPoolUtils.hincrBy("cloudpark_userinfo_flow", object.getString("parkingid"), 1);
		logger.info("object=====object" + object.toJSONString());
		// sendMess(object.getJSONArray("data"),dataTunnelPublishClient,l,object.getString("msgtype"),object.getString("parkingid"),object.getString("topic"));
		BigDataAnalyze.sendMess(object.getJSONArray("data"), dataTunnelPublishClient, l, object.getString("msgtype"),
				object.getString("parkingid"), object.getString("topic"));
	}

	@Override
	public JSONObject addUserInfomation2(String data) {
		JSONObject result = new JSONObject();
		logger.info("Welcome=====SendUserInfoMessImp");
		JSONObject object = JSON.parseObject(data);

		logger.info("object=====object" + object.toJSONString());
		String key_p = BigDataAnalyze.geListKeyByDataType(object.getString("parkingid"), DataConstants.CLOUDPARK_USERGROUP, "p");
		//long l = JedisPoolUtils.hincrBy(key_p, "autoincrsn", 1);
		logger.info("object=====object" + object.toJSONString());
		// sendMess(object.getJSONArray("data"),dataTunnelPublishClient,l,object.  ("msgtype"),object.getString("parkingid"),object.getString("topic"));
		result = BigDataAnalyze.sendMess2(object.getJSONArray("data"), dataTunnelPublishClient, 0,
				object.getString("msgtype"), object.getString("parkingid"),DataConstants.CLOUDPARK_USERGROUP);
		return result;
	}
	
	@Override
	public JSONObject addUserInfomation3(String data) {
		JSONObject result = new JSONObject();
		logger.info("Welcome=====SendUserInfoMessImp");
		JSONObject object = JSON.parseObject(data);

		logger.info("object=====object" + object.toJSONString());
		String key_p = BigDataAnalyze.geListKeyByDataType(object.getString("parkingid"), DataConstants.CLOUDPLACE_USERGROUP, "p");
		//long l = JedisPoolUtils.hincrBy(key_p, "autoincrsn", 1);
		logger.info("object=====object" + object.toJSONString());
		// sendMess(object.getJSONArray("data"),dataTunnelPublishClient,l,object.  ("msgtype"),object.getString("parkingid"),object.getString("topic"));
		result = BigDataAnalyze.sendMess2(object.getJSONArray("data"), dataTunnelPublishClient, 0,
				object.getString("msgtype"), object.getString("parkingid"),DataConstants.CLOUDPLACE_USERGROUP);
		return result;
	}
	
	
	/**
	 * 
	 * @param arry
	 *            消息体
	 * @param dataTunnelPublishClient
	 * @param l
	 *            计数器
	 * @param msgtype
	 *            操作类型
	 * @param parkingLotNo
	 *            车场编号
	 * @param topic
	 */
	/*
	 * public static void sendMess(JSONArray arry,
	 * DataTunnelPublishClient<MessageDto> dataTunnelPublishClient, long l,
	 * String msgtype, String parkingLotNo, String topic) { try {
	 * logger.info("长期车黑白名单消息发送成功"); Date inTime = new Date();
	 * //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 * //LOG.info("线下停车场出场数据:" + arry.toJSONString()); MessageDto messageDto =
	 * new MessageDto(); messageDto.setKey(String.valueOf(l));
	 * messageDto.setTopic(topic); messageDto.setMsgtype (msgtype);// in_p,in_k,
	 * messageDto.setCarparkid(parkingLotNo); messageDto.setData(arry);
	 * dataTunnelPublishClient.sendMessage(messageDto);
	 * logger.info("长期车黑白名单消息发送成功"); } catch (Exception e) {
	 * //LOG.error("线下停车场进场数据异常:" + e); } }
	 */

}
