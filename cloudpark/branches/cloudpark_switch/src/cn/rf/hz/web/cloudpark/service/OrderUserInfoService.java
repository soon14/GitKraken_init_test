package cn.rf.hz.web.cloudpark.service;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;

public interface OrderUserInfoService {
	JSONObject uploadorderuser(JSONObject jso);
	JSONObject uploadwithholdinfo(JSONObject jso);
	boolean islater(String parkinglotno, String carcode, Date intime);
	void cancleorder(JSONObject jso,String _carcode,String parkinglotno);
}
