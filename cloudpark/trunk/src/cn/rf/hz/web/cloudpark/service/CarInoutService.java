package cn.rf.hz.web.cloudpark.service;

import com.alibaba.fastjson.JSONObject;

public interface CarInoutService {
	String saveCarin(String requestBody);
	String saveCarout(String requestBody);
	String updateCarin(String requestBody);
	//String saveCarinredis(String requestBody);
}
