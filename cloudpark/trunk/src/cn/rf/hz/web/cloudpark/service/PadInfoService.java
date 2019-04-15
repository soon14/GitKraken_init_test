package cn.rf.hz.web.cloudpark.service;

import com.alibaba.fastjson.JSONObject;

public interface PadInfoService {
	
	 JSONObject getPadInfo(String requestBody);
	
	 JSONObject getNewPadInfo(String requestBody);
}
