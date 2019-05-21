package cn.rf.hz.web.cloudpark.daoxx;

import cn.rf.hz.web.cloudpark.moder.Tc_parkingboxmemory;

public interface Tc_parkingboxmemoryMapper {
    int deleteByPrimaryKey(Integer recordid);

    int insert(Tc_parkingboxmemory record);

    int insertSelective(Tc_parkingboxmemory record);

    Tc_parkingboxmemory selectByPrimaryKey(Integer recordid);

    int updateByPrimaryKeySelective(Tc_parkingboxmemory record);

    int updateByPrimaryKey(Tc_parkingboxmemory record);
}