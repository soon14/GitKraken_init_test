package cn.rf.hz.web.cloudpark.daoxx;

import cn.rf.hz.web.cloudpark.moder.devicebaseinfo;

public interface devicebaseinfoMapper {
    int deleteByPrimaryKey(Integer recordid);

    int insert(devicebaseinfo record);

    int insertSelective(devicebaseinfo record);

    devicebaseinfo selectByPrimaryKey(Integer recordid);

    int updateByPrimaryKeySelective(devicebaseinfo record);

    int updateByPrimaryKey(devicebaseinfo record);
    
    String selectMaxSerialNumber(String devicetype);
}