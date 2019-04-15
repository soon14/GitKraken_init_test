package cn.rf.hz.web.cloudpark.service;

import com.alibaba.fastjson.JSONArray;

import cn.rf.hz.web.cloudpark.moder.Tc_crosspoint;

public interface Tc_crosspointService {

	JSONArray getCrosspointbyParkinglotsNo(String parkinglotNo);
	
	JSONArray getCrosspointbyCarcode(String carcode,String parkinglotNo,int partitionID);
	
	Tc_crosspoint getFirstCrosspointbyCarcode(String carcode,String parkinglotNo,int partitionID);

}