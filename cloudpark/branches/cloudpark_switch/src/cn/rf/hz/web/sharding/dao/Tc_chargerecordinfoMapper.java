package cn.rf.hz.web.sharding.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.rf.hz.web.cloudpark.moder.Tc_chargerecordinfo;
import cn.rf.hz.web.cloudpark.moder.Tc_chargerecordinfoKey;

public interface Tc_chargerecordinfoMapper {
	int deleteByPrimaryKey(Tc_chargerecordinfoKey key);

	int insert(Tc_chargerecordinfo record);

	int insertSelective(Tc_chargerecordinfo record);

	Tc_chargerecordinfo selectByPrimaryKey(Tc_chargerecordinfoKey key);

	int updateByPrimaryKeySelective(Tc_chargerecordinfo record);

	int updateByPrimaryKey(Tc_chargerecordinfo record);

	int insertChargerecordInfo(Map<String, Object> map);

	void batchInsertChargerecordinfo(ArrayList<Tc_chargerecordinfo> list);

	List<Object> queryChargerecordinfo(Map<String, Object> map);

	List<Object> queryByCondition(Map<String, Object> map);

	int updatestatebycarcode(@Param("carcode") String carcode, @Param("parkinglotno") String parkinglotno,
			@Param("partitionID") int partitionID);

	int updatestatebyrecordid(@Param("recordid") long recordid, @Param("parkinglotno") String parkinglotno,
			@Param("partitionID") int partitionID);

	List<Object> queryChargeHistorybyStatus(Map<String, Object> map);

	List<Object> queryChargeHistory(Map<String, Object> map);

	/**
	 * @Title: queryTotalAmountChargerecordinfo @Description:
	 *         box获取全量包缴费记录信息（不包含充值费用） @param map 缴费参数
	 * @return List<Object> @throws
	 */
	List<Object> queryTotalAmountChargerecordinfo(Map<String, Object> map);
}
