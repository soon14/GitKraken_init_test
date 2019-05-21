package com.reformer.cloudpark.service;

import com.alibaba.fastjson.JSONObject;
import com.reformer.cloudpark.model.CarparkInfo;
import com.reformer.datatunnel.client.dto.MessageDto;


public interface MessageSendService extends BaseService{
    
	void sendMessage(String message);
	
}
