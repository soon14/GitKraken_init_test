package cn.rf.hz.web.cloudpark.service;

import com.alibaba.fastjson.JSONObject;

/**
 * 纠正车牌数据sevice
 * */
public interface UpdateLicensePlateService {
	/**
	 * 上传纠正记录
	 * @return 
	 * */
	JSONObject uploadCorrectRecord(String requestBody);
	
	/**
	 * 上传无牌车纠正记录
	 * @return 
	 * */
	JSONObject uploadCorrectUnlicensedRecord(String requestBody);
}
