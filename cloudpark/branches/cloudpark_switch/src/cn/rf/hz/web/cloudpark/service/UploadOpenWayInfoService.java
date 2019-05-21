package cn.rf.hz.web.cloudpark.service;

import com.alibaba.fastjson.JSONObject;

public interface UploadOpenWayInfoService {
	/**
	 * 上传开闸记录
	 * @return 
	 * */
	JSONObject uploadOpenWayRecord(String requestBody);
}
