package cn.rf.hz.web.cloudpark.daoxx;

import java.util.List;

import cn.rf.hz.web.cloudpark.moder.Devicestatus;

public interface DevicestatusMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Devicestatus record);

    int insertSelective(Devicestatus record);

    Devicestatus selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Devicestatus record);

    int updateByPrimaryKey(Devicestatus record);
    
    int batchInsertdevicestatus(List<Devicestatus> record);
}