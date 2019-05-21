package cn.rf.hz.web.cloudpark.daoxx;

import java.util.List;

import cn.rf.hz.web.cloudpark.moder.Tc_parkingarea;

public interface Tc_parkingareaMapper {
    int deleteByPrimaryKey(Integer areaid);

    int insert(Tc_parkingarea record);

    int insertSelective(Tc_parkingarea record);

    Tc_parkingarea selectByPrimaryKey(Integer areaid);

    int updateByPrimaryKeySelective(Tc_parkingarea record);

    int updateByPrimaryKey(Tc_parkingarea record);
    
    List<Tc_parkingarea> queryparkingAreaByParkingLotNo(String parkinglotno);
}