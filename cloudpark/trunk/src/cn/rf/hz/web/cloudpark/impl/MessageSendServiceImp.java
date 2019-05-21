package cn.rf.hz.web.cloudpark.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.cloudpark.service.KFrameService;
import com.reformer.cloudpark.service.MessageSendService;
import com.reformer.datatunnel.client.DataTunnelPublishClient;
import com.reformer.datatunnel.client.dto.MessageDto;

import cn.rf.hz.web.sharding.dao.Tc_chargerecordinfoMapper;
import cn.rf.hz.web.sharding.dao.Tc_usercrdtm_inMapper;
import cn.rf.hz.web.utils.BigDataAnalyze;
import cn.rf.hz.web.utils.DataConstants;
import cn.rf.hz.web.utils.JedisPoolUtils;

@Service("messageSendService")
public class MessageSendServiceImp implements MessageSendService {
    private final static Logger LOG = Logger.getLogger(MessageSendServiceImp.class);

    @Autowired
    private DataTunnelPublishClient dataTunnelPublishClient;

    @Override
    public void sendMessage(String message) {
        LOG.info("============rollback message intro" +message);
        JSONObject messageDto = JSONObject.parseObject(message);
        String topic = messageDto.getString("topic");// 获取数据类型（topic）           
        String messageType =  messageDto.getString("msgtype");// 获取消息类型          
        String msgType = messageType.substring(0, messageType.indexOf("_"));
        String carParkId = messageDto.getString("carparkid");// 获取停车场ID
        if (topic.equals(DataConstants.CLOUDPARK_CHARGE)) {
            msgType = "charge";
        }

        // TODO Auto-generated method stub
        try {
            //JedisPoolUtils.lock(carParkId, msgType);

            long l = JedisPoolUtils.hincrBy(topic + "_flow", carParkId, 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(JSONObject.parseObject(message));
            LOG.info("=============rollback message " + jsonArray + "=============");
            BigDataAnalyze.sendMess(jsonArray, dataTunnelPublishClient, l, messageType, carParkId,
                    topic);
        } finally {
            //JedisPoolUtils.unlock(carParkId, msgType);
        }
    }

}
