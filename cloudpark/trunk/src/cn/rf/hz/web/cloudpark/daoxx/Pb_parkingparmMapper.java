package cn.rf.hz.web.cloudpark.daoxx;

import java.util.List;

import cn.rf.hz.web.cloudpark.moder.Pb_parkingparm;
import cn.rf.hz.web.cloudpark.moder.Pb_parkingparmKey;

public interface Pb_parkingparmMapper {
    int deleteByPrimaryKey(Pb_parkingparmKey key);

    int insert(Pb_parkingparm record);

    int insertSelective(Pb_parkingparm record);

    Pb_parkingparm selectByPrimaryKey(String parkinglotno,Integer parmid);

    int updateByPrimaryKeySelective(Pb_parkingparm record);

    int updateByPrimaryKey(Pb_parkingparm record);
    
    List<Pb_parkingparm> selectByParkingLotNo(String parkinglotno);
}