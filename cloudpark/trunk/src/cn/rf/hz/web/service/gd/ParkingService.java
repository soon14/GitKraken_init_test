package cn.rf.hz.web.service.gd;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.rf.hz.web.mapper.gd.ParkingMapper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service("parkingService")
public class ParkingService 
{
	@Autowired
	private ParkingMapper mapper;
	
	public ParkingMapper getMapper() {
		return mapper;
	}


	public int save(Map<String, Object> map)
	{
		return this.getMapper().save(map);
	}

	public int updateBySelective(Map<String, Object> map)
	{
		return this.getMapper().updateBySelective(map);
	}

	public void delete(Object map)
	{
		this.getMapper().delete(map);
		
	}

	public int savePiliang(JSONArray jsonArray)
	{
		return this.getMapper().savePiliang(jsonArray);
	}

	public JSONObject queryById(JSONObject map)
	{
		return this.getMapper().queryById(map);
	}

	public int update(JSONObject map)
	{
		return this.getMapper().update(map);
	}
		
	
}
