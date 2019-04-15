package cn.rf.hz.web.cloudpark.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.reformer.datatunnel.client.DataTunnelPublishClient;

import cn.rf.hz.web.utils.BigDataAnalyze;
import cn.rf.hz.web.utils.DataConstants;

@Service("sendmessserviceimp")
public class SendMessServiceImp {

	public void SendMess(JSONArray array, DataTunnelPublishClient dataTunnelPublishClient, long l, String messageType, String ParkingLotNo,
    		String topicName,String beginsize){
		 BigDataAnalyze.sendMessK(array, dataTunnelPublishClient, l, messageType, ParkingLotNo,
				 topicName,beginsize);
	}
	public void SendMessshare(JSONArray array, DataTunnelPublishClient dataTunnelPublishClient, long l, String messageType, String ParkingLotNo,
    		String topicName){
		 BigDataAnalyze.sendMess(array, dataTunnelPublishClient, l, messageType, ParkingLotNo,
				 topicName);
	}
}
