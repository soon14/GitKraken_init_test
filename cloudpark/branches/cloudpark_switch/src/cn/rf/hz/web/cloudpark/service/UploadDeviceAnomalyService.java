package cn.rf.hz.web.cloudpark.service;

import com.alibaba.fastjson.JSONObject;

public interface UploadDeviceAnomalyService {
	/**
	 * 上传设备警报记录
	 * @return 
	 * */
	JSONObject uploadDeviceAnomalyRecord(String requestBody);
}
