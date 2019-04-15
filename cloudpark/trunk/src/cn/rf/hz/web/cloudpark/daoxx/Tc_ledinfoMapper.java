package cn.rf.hz.web.cloudpark.daoxx;

import java.util.List;

import cn.rf.hz.web.cloudpark.moder.Tc_ledinfo;

public interface Tc_ledinfoMapper {
    int deleteByPrimaryKey(Integer recordid);

    int insert(Tc_ledinfo record);

    int insertSelective(Tc_ledinfo record);

    Tc_ledinfo selectByPrimaryKey(Integer recordid);

    int updateByPrimaryKeySelective(Tc_ledinfo record);

    int updateByPrimaryKey(Tc_ledinfo record);
    
    List<Tc_ledinfo> selectByChannelID(Integer channelid);
    
    //List<Tc_ledinfo> selectByChannelID1(Integer channelid);
    List<Tc_ledinfo> selectByStationID(Integer stationid);
}