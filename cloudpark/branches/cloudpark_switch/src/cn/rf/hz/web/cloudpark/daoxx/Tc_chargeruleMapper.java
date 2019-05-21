package cn.rf.hz.web.cloudpark.daoxx;

import java.util.List;
import java.util.Map;

import cn.rf.hz.web.cloudpark.moder.Tc_chargerule;
import cn.rf.hz.web.cloudpark.moder.Tc_chargeruleKey;

public interface Tc_chargeruleMapper {
	int deleteByPrimaryKey(Tc_chargeruleKey key);

	int insert(Tc_chargerule record);

	int insertSelective(Tc_chargerule record);

	Tc_chargerule selectByPrimaryKey(Tc_chargeruleKey key);

	int updateByPrimaryKeySelective(Tc_chargerule record);

	int updateByPrimaryKey(Tc_chargerule record);

	List<Tc_chargerule> selectShortRuleByWhere(String parkinglotno);

	List<Tc_chargerule> selectLongRuleByWhere(String parkinglotno);

	int getMaxID(String parkinglotno);

	List<Tc_chargerule> selectRuleByWhere(String parkinglotno);

	int deleteRuleByPrimaryKey(Tc_chargeruleKey key);

	Tc_chargerule selectByMap(Map<String, Object> map);

	List<Tc_chargerule> queryRuleByParkinglotno(Map<String, Object> map);

	Tc_chargerule selectByChargeRuleIDAndparkingLotNo(Map<String, Object> map);

	int deleteByParkingLotNo(String parkinglotno);

}