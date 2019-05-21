package cn.rf.hz.web.cloudpark.service;

import com.alibaba.fastjson.JSONObject;

public interface CarInfoForChargeService {

	JSONObject getCarInfoForCharge(String requestBody) throws Exception;
	JSONObject getCarInfoForApp(String requestBody);
	JSONObject getHistoryInCarInfoForApp(String requestBody);
	JSONObject getHistoryOutCarInfoForApp(String requestBody);

}