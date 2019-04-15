package cn.rf.hz.web.cloudpark.daoxx;

import java.util.List;

import cn.rf.hz.web.cloudpark.moder.Pb_station;

public interface Pb_stationMapper {
	int deleteByPrimaryKey(Integer recordid);

	int insert(Pb_station record);

	int insertSelective(Pb_station record);

	Pb_station selectByPrimaryKey(Integer recordid);

	int updateByPrimaryKeySelective(Pb_station record);

	int updateByPrimaryKey(Pb_station record);

	Pb_station selectBySerialNumber(String serialnumber);

	List<Pb_station> selectByChannelID(Integer channelid);

	String selectMaxSerialNumber();

	String selectMaxSerialNumberByParkingLotNo(Integer parkinglotno);
}