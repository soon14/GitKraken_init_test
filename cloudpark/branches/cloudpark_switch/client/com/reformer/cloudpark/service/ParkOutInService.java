package com.reformer.cloudpark.service;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.cloudpark.model.CarparkInfo;

import cn.rf.hz.web.cloudpark.moder.Tc_chargerecordinfo;

public interface ParkOutInService extends BaseService<CarparkInfo, Integer>{
			String saveParkIn(String requestBody);
			String saveParkOut(String requestBody);
			void queryCarInOutRecord(String requestBody);
		    JSONArray queryParkState(JSONObject mapparam);
		    String parkingState(JSONObject mapparam);
		    String  uploadExecptionOut(String requestBody);
		    String  DelBoxPayRecord(String requestBody);
		    String uploadBoxPayRecord(String requestBody);
		    JSONArray queryChargerecordinfo(JSONObject mapparam);
		    String queryChargerecordinfo4string(JSONObject mapparam);
		    void sendKafkaMesUploadPayRecord(String ParkingLotNo, ArrayList<Tc_chargerecordinfo> chargerecordinfolist);
		    Integer getPartitionid(String ParkingLotNo);
			
}
