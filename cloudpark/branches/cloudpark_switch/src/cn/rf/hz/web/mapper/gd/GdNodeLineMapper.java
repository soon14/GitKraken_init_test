package cn.rf.hz.web.mapper.gd;

import java.util.Map;

import cn.rf.hz.web.bean.gd.AreaNodeLine;

public interface GdNodeLineMapper<T>
{

	AreaNodeLine findById(Map<String, Object> map);

	int save(Map<String, Object> map);

	int updateBySelective(Map<String, Object> map);

}
