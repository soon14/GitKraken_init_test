package cn.rf.hz.web.service.gd;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.rf.hz.web.bean.gd.AreaNodeLine;
import cn.rf.hz.web.mapper.gd.GdNodeLineMapper;

@Service("gdNodeLineService")
public class GdNodeLineService<T>
{
	@Autowired
	private GdNodeLineMapper<T> mapper;

	public GdNodeLineMapper<T> getMapper()
	{
		return mapper;
	}

	public void setMapper(GdNodeLineMapper<T> mapper)
	{
		this.mapper = mapper;
	}

	public AreaNodeLine findById(Map<String, Object> map)
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
	