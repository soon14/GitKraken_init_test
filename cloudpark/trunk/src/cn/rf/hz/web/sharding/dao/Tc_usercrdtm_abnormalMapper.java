package cn.rf.hz.web.sharding.dao;

import java.util.Map;

import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm_abnormal;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm_abnormalKey;

public interface Tc_usercrdtm_abnormalMapper {
    int deleteByPrimaryKey(Tc_usercrdtm_abnormalKey key);

    int insert(Tc_usercrdtm_abnormal record);

    int insertSelective(Tc_usercrdtm_abnormal record);

    Tc_usercrdtm_abnormal selectByPrimaryKey(Tc_usercrdtm_abnormalKey key);

    int updateByPrimaryKeySelective(Tc_usercrdtm_abnormal record);

    int updateByPrimaryKey(Tc_usercrdtm_abnormal record);
    int insertabnomal(Map<String, Object> map);
}