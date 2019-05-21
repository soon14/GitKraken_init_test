package com.reformer.cloudpark.service;

import com.alibaba.fastjson.JSONObject;
import com.reformer.cloudpark.model.CarparkInfo;
import com.reformer.cloudpark.model.ChargeRecord;

/**
 * 全量包接口4cloudlong
 * @author Administrator
 *
 */
public interface KFrameService extends BaseService<CarparkInfo, Integer>{
	String parkingcarAll(JSONObject mapparam);
	String chargerecordAll(JSONObject mapparam);
	String userinfodAll(JSONObject mapparam);
	String delcarfrom_in(String carcode,String parkingNo);
	String changestatebycarcode(String carcode,String parkingNo);
	String changestatebyrecordid(long recordid,String parkingNo);
	String crosspointAll(JSONObject mapparam);
	void saveChargeRecord(String parkingNo, String carcode,ChargeRecord chargeRecord);
}
