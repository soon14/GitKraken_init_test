package cn.rf.hz.web.cloudpark.service;

import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.cloudpark.moder.ParkingLotParameter;

public interface CarInfoForPadService {

	JSONObject getCarInfoForPad(String requestBody) throws Exception;

	ParkingLotParameter getParkingParamInfo(String parkNo);

	// JSONObject getHistoryChargeForPad(String requestBody);

}