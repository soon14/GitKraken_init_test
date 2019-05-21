package cn.rf.hz.web.cloudpark.daoxx;

import cn.rf.hz.web.cloudpark.moder.Tc_weekrule;
import cn.rf.hz.web.cloudpark.moder.Tc_weekruleKey;

public interface Tc_weekruleMapper {
	int deleteByPrimaryKey(Tc_weekruleKey key);

	int insert(Tc_weekrule record);

	int insertSelective(Tc_weekrule record);

	Tc_weekrule selectByPrimaryKey(Tc_weekruleKey key);

	int updateByPrimaryKeySelective(Tc_weekrule record);

	int updateByPrimaryKey(Tc_weekrule record);

	int deleteByParkingLotNo(String parkinglotno);
}