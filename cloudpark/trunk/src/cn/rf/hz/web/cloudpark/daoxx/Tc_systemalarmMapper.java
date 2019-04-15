package cn.rf.hz.web.cloudpark.daoxx;

import java.util.List;

import cn.rf.hz.web.cloudpark.moder.Tc_systemalarm;

public interface Tc_systemalarmMapper {
    int deleteByPrimaryKey(Integer recordid);

    int insert(Tc_systemalarm record);

    int insertSelective(Tc_systemalarm record);

    Tc_systemalarm selectByPrimaryKey(Integer recordid);

    int updateByPrimaryKeySelective(Tc_systemalarm record);

    int updateByPrimaryKey(Tc_systemalarm record);
    
    void batchInsertSystemalarm(List<Tc_systemalarm>list); 
}