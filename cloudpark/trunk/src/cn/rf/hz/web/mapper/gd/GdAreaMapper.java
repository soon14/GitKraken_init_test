package cn.rf.hz.web.mapper.gd;

import java.util.Map;

import cn.rf.hz.web.bean.gd.Area;

public interface GdAreaMapper<T>
{

	Area findByNodeId(Map<String, Object> map);
	Area findById(Map<String, Object> map);
	int save(Map<String, Object> map);
	int updateBySelective(Map<String, Object> map);

}
