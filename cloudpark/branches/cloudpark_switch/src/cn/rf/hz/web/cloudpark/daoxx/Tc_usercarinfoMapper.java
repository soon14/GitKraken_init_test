package cn.rf.hz.web.cloudpark.daoxx;

import java.util.List;
import java.util.Map;

import cn.rf.hz.web.cloudpark.moder.Tc_usercarinfo;

public interface Tc_usercarinfoMapper {
	int deleteByPrimaryKey(Integer recordid);

	int insert(Tc_usercarinfo record);

	int insertSelective(Tc_usercarinfo record);

	int batchInsert(List<Tc_usercarinfo> list);

	int batchDelete(Integer[] recordids);

	Tc_usercarinfo selectByPrimaryKey(Integer recordid);

	int updateByPrimaryKeySelective(Tc_usercarinfo record);

	int updateByPrimaryKey(Tc_usercarinfo record);

	List<Tc_usercarinfo> selectByUserID(Integer recordid);

	Tc_usercarinfo selectByUserIDAndCarCode(Map<String, Object> map);
}