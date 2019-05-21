package cn.rf.hz.web.cloudpark.daoxx;

import cn.rf.hz.web.cloudpark.moder.Tc_charge_jmrecord;

public interface Tc_charge_jmrecordMapper {
    int deleteByPrimaryKey(Integer recordid);

    int insert(Tc_charge_jmrecord record);

    int insertSelective(Tc_charge_jmrecord record);

    Tc_charge_jmrecord selectByPrimaryKey(Integer recordid);

    int updateByPrimaryKeySelective(Tc_charge_jmrecord record);

    int updateByPrimaryKey(Tc_charge_jmrecord record);
}