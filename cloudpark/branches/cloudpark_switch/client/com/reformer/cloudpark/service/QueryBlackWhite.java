package com.reformer.cloudpark.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.cloudpark.model.CarparkInfo;

import cn.rf.hz.web.cloudpark.moder.Tc_whiteuserinfo;

public interface QueryBlackWhite extends BaseService<CarparkInfo, Integer>{
	JSONArray queryBlackWhite(String ParkingLotNo);
}
