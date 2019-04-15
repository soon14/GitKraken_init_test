package cn.rf.hz.web.cloudpark.service;

import com.alibaba.fastjson.JSONObject;

public interface ParkingImageService {
	
	JSONObject getSmallImageInfo(String requestBody);
	
	JSONObject getAliyunImageUrl(String requestBody);

	JSONObject uploadImageToAliyun(String requestBody);
	
	JSONObject batchGetSmallImagePath(String requestBody);

	JSONObject uploadVoiceToAliyun(String requestBody);
}
