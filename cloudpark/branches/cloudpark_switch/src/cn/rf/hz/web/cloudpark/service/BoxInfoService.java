package cn.rf.hz.web.cloudpark.service;

import com.alibaba.fastjson.JSONObject;

public interface BoxInfoService {
	
	JSONObject getBoxInfo(String requestBody);
	
	JSONObject getLedShowInfo(String requestBody);
	
	JSONObject getParkingParamInfo(String requestBody);
	
	JSONObject uploadBoxMemoryUsage(String requestBody);
}
