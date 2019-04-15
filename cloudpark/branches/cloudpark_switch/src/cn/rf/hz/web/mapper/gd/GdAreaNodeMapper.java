package cn.rf.hz.web.mapper.gd;

import java.util.Map;

import cn.rf.hz.web.bean.gd.AreaNode;

public interface GdAreaNodeMapper<T>
{

	AreaNode findById(Integer parkingId, Integer carParkId);

	AreaNode findById(Map<String, Object> map);

	int save(Map<String, Object> map);

	int updateById(Map<String, Object> map);

}
