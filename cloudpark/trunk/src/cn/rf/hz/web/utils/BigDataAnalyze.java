package cn.rf.hz.web.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.datatunnel.client.DataTunnelPublishClient;
import com.reformer.datatunnel.client.dto.MessageDto;

/**
 * 
 * 张泉金大数据分析
 * 
 * @author 程依寿 2016年1月26日 下午4:06:06
 */
public class BigDataAnalyze {

	private final static Logger LOG = Logger.getLogger(BigDataAnalyze.class);

	/**
	 * 进场
	 * 
	 * @param jsonData
	 * @param dataTunnelPublishClient
	 */
	public static void inBigDataMessage(JSONObject jsonData,
			DataTunnelPublishClient<MessageDto> dataTunnelPublishClient) {
		try {
			// LOG.debug("进场数据，张泉金大数据信息:" + jsonData.toJSONString());
			// // 张泉金大数据统计
			MessageDto messageDto = new MessageDto();
			messageDto.setTopic("carpark_inout");
			messageDto.setMsgtype("in");
			messageDto.setUuid("");
			messageDto.setPhone("");
			messageDto.setCarparkid(jsonData.getString("carParkId"));
			messageDto.setEventtime(jsonData.getDate("inTime").getTime());
			messageDto.setData(jsonData);
			// dataTunnelPublishClient.sendMessage(messageDto);

		} catch (Exception e) {
			LOG.error("进场数据，张泉金大数据异常:" + e);
		}
	}

	/**
	 * 离场
	 * 
	 * @param message
	 * @param dataTunnelPublishClient
	 */
	public static void outBigDataMessage(JSONObject jsonData,
			DataTunnelPublishClient<MessageDto> dataTunnelPublishClient) {
		try {
			// LOG.debug("出场数据，张泉金大数据信息:" + jsonData.toJSONString());
			// 数据
			Integer carParkId = jsonData.getInteger("carParkId");
			String oldRecordId = jsonData.getString("oldRecordId");
			Date inTime = new Date();
			String keyName = "export_" + carParkId + "_" + oldRecordId;

			JedisPoolUtils.del(keyName);

			String inTimeStr = jsonData.getString("inTimeStr");

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			if (!"".equals(inTimeStr) && null != inTimeStr)
				inTime = df.parse(inTimeStr);
			else
				inTime = jsonData.getDate("outTime");

			// 张泉金大数据统计
			MessageDto messageDto = new MessageDto();
			messageDto.setTopic("carpark_inout");
			messageDto.setMsgtype("out");
			messageDto.setUuid("");
			messageDto.setPhone("");
			messageDto.setCarparkid(carParkId + "");
			messageDto.setEventtime(inTime.getTime());
			messageDto.setData(jsonData);
			// dataTunnelPublishClient.sendMessage(messageDto);

			// JedisPoolUtils.setex(keyName, 3600, jsonData.toJSONString());

		} catch (Exception e) {
			LOG.error("出场数据，张泉金大数据异常:" + e);
		}
	}

	/*
	 * public static void parkInDataMessage(JSONArray jsonData,
	 * DataTunnelPublishClient<MessageDto> dataTunnelPublishClient, long l,
	 * String msgtype, String parkingLotNo) { try { Date inTime = new Date();
	 * SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 * LOG.info("线下停车场进场数据:" + jsonData.toJSONString()); MessageDto messageDto =
	 * new MessageDto(); messageDto.setKey(String.valueOf(l));
	 * messageDto.setTopic("cloudpark_inout"); messageDto.setMsgtype("in_" +
	 * msgtype);// in_p,in_k, messageDto.setCarparkid(parkingLotNo);
	 * messageDto.setData(jsonData);
	 * dataTunnelPublishClient.sendMessage(messageDto);
	 * System.out.println(messageDto.toString()); System.out.println("消息发送成功！");
	 * 
	 * } catch (Exception e) { LOG.error("线下停车场进场数据异常:" + e); } }
	 * 
	 * public static void parkOutDataMessage(JSONArray jsonData,
	 * DataTunnelPublishClient<MessageDto> dataTunnelPublishClient, long l,
	 * String msgtype, String parkingLotNo) { try { Date inTime = new Date();
	 * //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 * LOG.info("线下停车场出场数据:" + jsonData.toJSONString()); MessageDto messageDto =
	 * new MessageDto(); messageDto.setKey(String.valueOf(l));
	 * messageDto.setTopic("cloudpark_inout"); messageDto.setMsgtype("out_" +
	 * msgtype);// in_p,in_k, messageDto.setCarparkid(parkingLotNo);
	 * messageDto.setData(jsonData);
	 * dataTunnelPublishClient.sendMessage(messageDto);
	 * System.out.println(messageDto.toString()); System.out.println("消息发送成功！");
	 * 
	 * } catch (Exception e) { LOG.error("线下停车场进场数据异常:" + e); }
	 * 
	 * }
	 * 
	 * public static void chargePayMessage(JSONArray jsonData,
	 * DataTunnelPublishClient<MessageDto> dataTunnelPublishClient, long l,
	 * String msgtype, String parkingLotNo) { try { Date inTime = new Date();
	 * //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 * LOG.info("缴费记录消息:" + jsonData.toJSONString()); MessageDto messageDto =
	 * new MessageDto(); messageDto.setKey(String.valueOf(l));
	 * messageDto.setTopic("cloudpark_charge"); messageDto.setMsgtype("in_" +
	 * msgtype);// charge_p,charge_k, messageDto.setCarparkid(parkingLotNo);
	 * messageDto.setData(jsonData);
	 * dataTunnelPublishClient.sendMessage(messageDto);
	 * System.out.println(messageDto.toString()); System.out.println("消息发送成功！");
	 * } catch (Exception e) { LOG.error("缴费记录消息异常:" + e); }
	 * 
	 * }
	 */
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
	public static void sendMess(JSONArray arry, DataTunnelPublishClient<MessageDto> dataTunnelPublishClient, long l,
			String msgtype, String parkingLotNo, String topic) {
		try {
			// LOG.info("开始发送消息");
			// LOG.info("============消息体：arry" + arry.toJSONString() +
			// "============");
			String _type = msgtype.contains("in") ? "in" : "out";
			if (msgtype.contains("in")) {
				_type = "in";
			} else if (msgtype.contains("out")) {
				_type = "out";
			} else if (msgtype.contains("update")) {
				_type = "in";
			}
			JSONArray arry_new = fixArray(arry, _type);
			// Date inTime = new Date();
			MessageDto messageDto = generateMessageDto(l, topic, msgtype, parkingLotNo, arry_new);
			/*
			 * //MessageDto messageDto = new MessageDto();
			 * messageDto.setKey(String.valueOf(l)); messageDto.setTopic(topic);
			 * messageDto.setMsgtype (msgtype); // in_p,in_k,out_p,update_p
			 * messageDto.setCarparkid(parkingLotNo);
			 * messageDto.setData(arry_new);
			 */
			// LOG.info("============消息内容是：" + messageDto.toString() +
			// "============");
			// LOG.info("============消息内容是123：" +
			// JSONObject.toJSONString(messageDto) + "============");

			dataTunnelPublishClient.sendMessage(messageDto);

			LOG.info("============消息" + topic + "发送成功============");
		} catch (Exception e) {
			LOG.error("=============发送消息异常====" + e.getMessage());
		}
	}

	public static void sendMessK(JSONArray arry, DataTunnelPublishClient<MessageDto> dataTunnelPublishClient, long l,
			String msgtype, String parkingLotNo, String topic, String ver1) {
		try {
			// LOG.info("开始发送消息");
			// LOG.info("============消息体：arry" + arry.toJSONString() +
			// "============");
			String _type = msgtype.contains("in") ? "in" : "out";
			JSONArray arry_new = fixArray(arry, _type);
			// Date inTime = new Date();
			MessageDto messageDto = generateMessageDtoK(l, topic, msgtype, parkingLotNo, arry_new, ver1);
			/*
			 * //MessageDto messageDto = new MessageDto();
			 * messageDto.setKey(String.valueOf(l)); messageDto.setTopic(topic);
			 * messageDto.setMsgtype (msgtype); // in_p,in_k,out_p,update_p
			 * messageDto.setCarparkid(parkingLotNo);
			 * messageDto.setData(arry_new);
			 */
			// LOG.info("============消息内容是：" + messageDto.toString() +
			// "============");
			// LOG.info("============消息内容是123：" +
			// JSONObject.toJSONString(messageDto) + "============");

			dataTunnelPublishClient.sendMessage(messageDto);

			// LOG.info("============消息" + topic + "发送成功============");
		} catch (Exception e) {
			LOG.error("=============发送消息异常====" + e.getMessage());
		}
	}

	public static MessageDto generateMessageDto(long l, String topic, String msgtype, String parkingLotNo,
			JSONArray arry_new) {
		MessageDto messageDto = new MessageDto();
		messageDto.setSerialNo(String.valueOf(l));
		messageDto.setKey(parkingLotNo);
		messageDto.setTopic(topic);
		messageDto.setMsgtype(msgtype); // in_p,in_k,out_p,update_p
		messageDto.setCarparkid(parkingLotNo);
		messageDto.setData(arry_new);
		return messageDto;

	}

	public static MessageDto generateMessageDtoK(long l, String topic, String msgtype, String parkingLotNo,
			JSONArray arry_new, String ver1) {
		MessageDto messageDto = new MessageDto();
		messageDto.setSerialNo(String.valueOf(l));
		messageDto.setKey(parkingLotNo);
		messageDto.setTopic(topic);
		messageDto.setMsgtype(msgtype); // in_p,in_k,out_p,update_p
		messageDto.setCarparkid(parkingLotNo);
		messageDto.setData(arry_new);
		messageDto.setReserve1(ver1);
		return messageDto;

	}

	/**
	 * 
	 * 
	 * 长期车黑白 名单
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
	public static JSONObject sendMess2(JSONArray arry, DataTunnelPublishClient<MessageDto> dataTunnelPublishClient,
			long l, String msgtype, String parkingLotNo, String topic) {
		JSONObject result = new JSONObject();
		try {
			// LOG.info("开始发送消息");
			// LOG.info("============消息体：arry" + arry.toJSONString() +
			// "============");
			String _type = msgtype.contains("in") ? "in" : "out";
			JSONArray arry_new = fixArray(arry, _type);
			// Date inTime = new Date();
			MessageDto messageDto = generateMessageDto(l, topic, msgtype, parkingLotNo, arry_new);
			/*
			 * //MessageDto messageDto = new MessageDto();
			 * messageDto.setKey(String.valueOf(l)); messageDto.setTopic(topic);
			 * messageDto.setMsgtype (msgtype); // in_p,in_k,out_p,update_p
			 * messageDto.setCarparkid(parkingLotNo);
			 * messageDto.setData(arry_new);
			 */
			// LOG.info("============消息内容是：" + messageDto.toString() +
			// "============");
			dataTunnelPublishClient.sendMessage(messageDto);

			// LOG.info("============消息" + topic + "发送成功============");
			result.put("resCode", 0);
			result.put("resMsg", "Succeed to send message");
		} catch (Exception e) {
			LOG.error("=============发送消息异常====" + e.getMessage());
			result.put("resCode", 1);
			result.put("resMsg", "Failed to send message");
		}
		return result;
	}

	/**
	 * TODO: 字段名称修改
	 * 
	 * @param arry
	 * @return
	 */
	public static JSONArray fixArray(JSONArray arry, String _type) {
		// LOG.info("============消息体：arry修正字段名称开始");
		// LOG.info("============消息体：arry修正前" + arry.toJSONString());
		// LOG.info("===========arry.size()====================" + arry.size());
		for (int i = 0; i < arry.size(); i++) {
			// LOG.info("===========arry.size()====================" +
			// arry.size());
			JSONObject object = arry.getJSONObject(i);
			if (object.containsKey("crdtm")) {
				// LOG.info("============包含crdtm");
				// LOG.info("============包含_type==="+_type);
				object.put(_type + "time", object.getTimestamp("crdtm"));
				object.remove("crdtm");
			}
			if (object.containsKey("carstyleid")) {
				object.remove("carstyleid");
			}
			/*
			 * if(object.containsKey("imagepath")){ object.remove("imagepath");
			 * }
			 */
			arry.set(i, object);
		}
		// LOG.info("============消息体：arry修正后" + arry.toJSONString());
		return arry;
	}

	public static String geListKeyByDataType(String carParkId, String dataType, String pk) {
		return DataConstants.DATA_REDIS_LIST_KEY_MAP.get(dataType) == null
				|| DataConstants.DATA_REDIS_LIST_KEY_MAP.get(dataType).equals("") ? "cloud_error"
						: carParkId + "_" + DataConstants.DATA_REDIS_LIST_KEY_MAP.get(dataType) + "_" + pk;

	}

	public static JSONObject send_updateplateno(JSONArray arry,
			DataTunnelPublishClient<MessageDto> dataTunnelPublishClient, String parkingLotNo) {
		JSONObject result = new JSONObject();
		try {
			MessageDto messageDto = new MessageDto();
			messageDto.setKey(parkingLotNo);
			messageDto.setTopic("update_plate_no");
			messageDto.setMsgtype("in_p");
			messageDto.setCarparkid(parkingLotNo);
			messageDto.setData(arry);
			dataTunnelPublishClient.sendMessage(messageDto);
			LOG.error("=============发送车牌更新消息成功====" + JSON.toJSONString(messageDto));
			result.put("resCode", 0);
			result.put("resMsg", "Succeed to send message");
		} catch (Exception e) {
			LOG.error("=============发送消息异常====" + e.getMessage());
			result.put("resCode", 1);
			result.put("resMsg", "Failed to send message");
		}
		return result;
	}

}