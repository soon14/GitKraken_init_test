package cn.rf.hz.web.cloudpark.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.cloudpark.service.ParkingInformation;
import com.reformer.cloudpark.service.QueryBlackWhite;

import cn.rf.hz.web.cloudpark.daoxx.Tc_parkInformationMapper;

@Service("queryBlackWhite")
public class QueryBlackWhiteServiceImp implements QueryBlackWhite{
	private final static Logger LOG = Logger.getLogger(ParkingInformationImp.class);
	@Autowired
	private  Tc_parkInformationMapper informationMapper;
	
	
	@Override
	public JSONArray queryBlackWhite(String ParkingLotNo) {
		LOG.info(" ParkingLotNo============" + ParkingLotNo + "==============");
		List<Object> list = this.informationMapper.queryBlackWhite(ParkingLotNo);
		JSONArray array = new JSONArray(list);
		LOG.info("arrayarrayarray==========" + array + "================");
		return array;
	
		
	}
}
