package cn.rf.hz.web.cloudpark.service;

import com.alibaba.fastjson.JSONObject;

public interface LoginInfoService {
	
	JSONObject getLoginInfo(String requestBody);
	
}
