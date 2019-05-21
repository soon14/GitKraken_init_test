package com.reformer.cloudpark.service;

import com.alibaba.fastjson.JSONObject;
import com.reformer.cloudpark.model.CarparkInfo;

import cn.rf.hz.web.cloudpark.moder.Tc_parkingarea;

public interface ParkingInformation extends BaseService<CarparkInfo, Integer> {
	JSONObject queryParkingInformation(String requestBody);

	Integer countAll();

	Integer countChannelAll();

	String queryParkins(int id, String parkname, int offset, int limit);

	String queryParkingChannels(int id, String parkname, int offset, int limit);

	String queryParkingChannelsById(int id);

	Tc_parkingarea getAreaInfo(String areaid);
}
