package cn.rf.hz.web.cloudpark.daoxx;

import java.util.List;

import cn.rf.hz.web.cloudpark.moder.Tc_charge_jm;

public interface Tc_charge_jmMapper {
    int deleteByPrimaryKey(Integer jmtypeid);

    int insert(Tc_charge_jm record);

    int insertSelective(Tc_charge_jm record);

    Tc_charge_jm selectByPrimaryKey(Integer jmtypeid);

    int updateByPrimaryKeySelective(Tc_charge_jm record);

    int updateByPrimaryKey(Tc_charge_jm record);
    
    List<Tc_charge_jm> selectByparkinglotno(String parkinglotno);
}