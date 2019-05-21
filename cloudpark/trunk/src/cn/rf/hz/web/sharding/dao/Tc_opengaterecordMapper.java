package cn.rf.hz.web.sharding.dao;

import java.util.List;

import cn.rf.hz.web.cloudpark.moder.Tc_opengaterecord;

public interface Tc_opengaterecordMapper {
    int deleteByPrimaryKey(Integer recordid);

    int insert(Tc_opengaterecord record);

    int insertSelective(Tc_opengaterecord record);

    Tc_opengaterecord selectByPrimaryKey(Integer recordid);

    int updateByPrimaryKeySelective(Tc_opengaterecord record);

    int updateByPrimaryKey(Tc_opengaterecord record);
    
    void batchInsertOpengaterecord(List<Tc_opengaterecord>list); 
}