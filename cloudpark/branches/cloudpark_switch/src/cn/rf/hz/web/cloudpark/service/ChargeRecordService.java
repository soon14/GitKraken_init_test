package cn.rf.hz.web.cloudpark.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.cloudpark.model.CarparkInfo;
import com.reformer.cloudpark.service.BaseService;

public interface ChargeRecordService extends BaseService<CarparkInfo, Integer>{
    void saveChargeRecord(JSONObject mapparam);
}
