package cn.rf.hz.web.service.gd;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.rf.hz.web.bean.gd.AreaNode;
import cn.rf.hz.web.mapper.gd.GdAreaNodeMapper;

@Service("gdAreaNodeService")
public class GdAreaNodeService<T>
{
	@Autowired
	private GdAreaNodeMapper<T> mapper;

	public GdAreaNodeMapper<T> getMapper()
	{
		return mapper;
	}

	public void setMapper(GdAreaNodeMapper<T> mapper)
	{
		this.mapper = mapper;
	}

	public AreaNode findById(Integer parkingId, Integer carParkId)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("parkingId", parkingId);
		map.put("carParkId", carParkId);
		return getMapper().findById(map);
	}

	public AreaNode findById(Map<String, Object> map)
	{
		return getMapper().findById(map);
	}

	public int save(Map<String, Object> map)
	{
		return getMapper().save(map);
		
	}

	public int updateById(Map<String, Object> map)
	{
		return getMapper().updateById(map);
		
	}
}
