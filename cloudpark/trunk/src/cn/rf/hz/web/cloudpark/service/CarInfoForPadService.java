package cn.rf.hz.web.cloudpark.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;


public interface CarInfoForPadService {

	JSONObject getCarInfoForPad(String requestBody) throws Exception;

}