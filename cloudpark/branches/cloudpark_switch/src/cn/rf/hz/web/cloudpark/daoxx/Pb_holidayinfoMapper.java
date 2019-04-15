package cn.rf.hz.web.cloudpark.daoxx;

import cn.rf.hz.web.cloudpark.moder.Pb_holidayinfo;
import cn.rf.hz.web.cloudpark.moder.Pb_holidayinfoKey;

public interface Pb_holidayinfoMapper {
	int deleteByPrimaryKey(Pb_holidayinfoKey key);

	int insert(Pb_holidayinfo record);

	int insertSelective(Pb_holidayinfo record);

	Pb_holidayinfo selectByPrimaryKey(Pb_holidayinfoKey key);

	int updateByPrimaryKeySelective(Pb_holidayinfo record);

	int updateByPrimaryKey(Pb_holidayinfo record);

	int deleteByParkingLotNo(String parkinglotno);
}