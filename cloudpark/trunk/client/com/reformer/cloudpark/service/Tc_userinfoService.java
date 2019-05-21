package com.reformer.cloudpark.service;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.cloudpark.model.Tc_userinfo;

public interface Tc_userinfoService {
	Tc_userinfo selectBylicensePlateNumber(String carCode, String parkinglotno, Date datetime);

	JSONArray selectByparkingNo(String parkinglotno);

	JSONObject uploadUserInfoForXb(String requestBody);
	
	JSONObject getProductInfo(String requestBody);
	
	JSONObject syncUserInfoForApp(String requestBody);
	
    JSONObject getUserInfoCarIn(String requestBody);
	
	JSONObject getUserInfoByID(String requestBody);
}
