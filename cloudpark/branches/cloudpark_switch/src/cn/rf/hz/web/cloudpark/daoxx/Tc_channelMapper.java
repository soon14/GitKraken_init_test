package cn.rf.hz.web.cloudpark.daoxx;

import java.util.List;

import cn.rf.hz.web.cloudpark.moder.Tc_channel;

public interface Tc_channelMapper {
    int deleteByPrimaryKey(Integer channelid);

    int insert(Tc_channel record);

    int insertSelective(Tc_channel record);

    Tc_channel selectByPrimaryKey(Integer channelid);

    int updateByPrimaryKeySelective(Tc_channel record);

    int updateByPrimaryKey(Tc_channel record);
    
    List<Tc_channel> selectByParkingChannelID(Integer parkingchannelid);
    
    List<Tc_channel> selectByMStationno(Integer mstationno);
    
    List<Object> selectStationIDByParkingChannelID(Integer parkingchannelid);
}