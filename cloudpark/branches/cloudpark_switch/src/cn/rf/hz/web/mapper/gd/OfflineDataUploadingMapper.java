package cn.rf.hz.web.mapper.gd;

import java.util.Map;

import com.alibaba.fastjson.JSONArray;

public interface OfflineDataUploadingMapper
{

	int uploadAnomalyOutRecords(Map<String, Object> map);

	int uploadDeviceAnomalyRecords(Map<String, Object> map);

	int uploadOpenWayRecords(Map<String, Object> map);

	void uploadOpenWayRecords(JSONArray ja1);

	void uploadAnomalyOutRecordsPiliang(JSONArray ja1);

	void uploadDeviceAnomalyRecordsPiLiang(JSONArray ja1);

	void uploadOpenWayRecordsPiLiang(JSONArray ja1);

}
