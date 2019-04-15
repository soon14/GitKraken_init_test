package cn.rf.hz.web.cloudpark.daoxx;

import cn.rf.hz.web.cloudpark.moder.Pb_specialcarcode;

public interface Pb_specialcarcodeMapper {
	int deleteByPrimaryKey(Integer recordid);

	int insert(Pb_specialcarcode record);

	int insertSelective(Pb_specialcarcode record);

	Pb_specialcarcode selectByPrimaryKey(Integer recordid);

	int updateByPrimaryKeySelective(Pb_specialcarcode record);

	int updateByPrimaryKey(Pb_specialcarcode record);

	Pb_specialcarcode selectByParkingLotNo(String parkinglotno);
}