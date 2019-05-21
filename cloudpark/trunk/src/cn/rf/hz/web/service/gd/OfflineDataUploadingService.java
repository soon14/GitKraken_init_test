package cn.rf.hz.web.service.gd;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;

import cn.rf.hz.web.mapper.gd.OfflineDataUploadingMapper;

@Service("offlineDataUploadingService")
public class OfflineDataUploadingService
{
	@Autowired
	private OfflineDataUploadingMapper mapper;

	
	
	public OfflineDataUploadingMapper getMapper()
	{
		return mapper;
	}



	public void setMapper(OfflineDataUploadingMapper mapper)
	{
		this.mapper = mapper;
	}



	public int uploadAnomalyOutRecords(Map<String, Object> map)
	{
		return getMapper().uploadAnomalyOutRecords(map);
	}



	public int uploadDeviceAnomalyRecords(Map<String, Object> map)
	{
		return getMapper().uploadDeviceAnomalyRecords(map);
	}



	public int uploadOpenWayRecords(Map<String, Object> map)
	{
		return getMapper().uploadOpenWayRecords(map);
	}



	public void uploadAnomalyOutRecordsPiliang(JSONArray ja1)
	{
		getMapper().uploadAnomalyOutRecordsPiliang(ja1);
		
	}



	public void uploadDeviceAnomalyRecordsPiLiang(JSONArray ja1)
	{
		getMapper().uploadDeviceAnomalyRecordsPiLiang(ja1);
		
	}



	public void uploadOpenWayRecordsPiLiang(JSONArray ja1)
	{
		getMapper().uploadOpenWayRecordsPiLiang(ja1);
		
	}


}
