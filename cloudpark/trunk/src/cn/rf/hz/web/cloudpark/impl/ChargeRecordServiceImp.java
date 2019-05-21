package cn.rf.hz.web.cloudpark.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.reformer.sharding.sequence.Sequence;

import cn.rf.hz.web.cloudpark.service.ChargeRecordService;
import cn.rf.hz.web.sharding.dao.Tc_chargerecordinfoMapper;




@Service("chargeRecordService")
public class ChargeRecordServiceImp implements ChargeRecordService {

	
	private final static Logger LOG = Logger.getLogger(ChargeRecordServiceImp.class);
	
	
	@Autowired
	private Tc_chargerecordinfoMapper chargerecordinfoMapper;
	
	@Autowired
	private Sequence sequencEntrance;
	
	@Override
	public void saveChargeRecord(JSONObject mapparam) {
		this.chargerecordinfoMapper.insertChargerecordInfo(mapparam);
	}

}
