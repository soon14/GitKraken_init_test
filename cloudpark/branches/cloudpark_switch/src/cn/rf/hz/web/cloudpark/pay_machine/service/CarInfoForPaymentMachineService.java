package cn.rf.hz.web.cloudpark.pay_machine.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.cloudpark.moder.Tc_chargerecordinfo;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm_in;

public interface CarInfoForPaymentMachineService {

	List<Tc_usercrdtm_in> getCarInfoListbyCarcode(String parkinglotNo, String carcode);

	int getCarInfoCountbyCarcode(String parkinglotNo, String carcode);

	List<Tc_usercrdtm_in> getCarInfoListbyTime(String parkinglotNo, String startDate, String endDate);

	int getCarInfoCountbyTime(String parkinglotNo, String startDate, String endDate);

	List<Tc_chargerecordinfo> getChargerecordListbyCarcode(String parkinglotNo, String carcode);

	JSONObject uploadHeartBeatRecord(JSONObject jsonHeart);
	
	JSONObject getCarChargeInfo(String parkinglotNo,String carCode,String inTime);
	
	JSONObject uploadChargeRecordInfo(JSONObject chargeInfo);
}
