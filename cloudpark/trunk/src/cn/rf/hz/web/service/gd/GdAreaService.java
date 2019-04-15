package cn.rf.hz.web.service.gd;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.rf.hz.web.bean.gd.Area;
import cn.rf.hz.web.mapper.gd.GdAreaMapper;

@Service("gdAreaService")
public class GdAreaService<T>
{
	@Autowired
	private GdAreaMapper<T> mapper;
	

	public GdAreaMapper<T> getMapper()
	{
		return mapper;
	}


	public void setMapper(GdAreaMapper<T> mapper)
	{
		this.mapper = mapper;
	}


	public Area findByNodeId(Integer parkingId, Integer carParkId)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("parkingId", parkingId);
		map.put("carParkId", carParkId);
		return getMapper().findByNodeId(map);
	}


	public Area findById(Map<String, Object> map)
	{
		return getMapper().findById(map);
	}


	public int save(Map<String, Object> map)
	{
		return getMapper().save(map);
	}


	public int updateBySelective(Map<String, Object> map)
	{
		return getMapper().updateBySelective(map);
	}


}
