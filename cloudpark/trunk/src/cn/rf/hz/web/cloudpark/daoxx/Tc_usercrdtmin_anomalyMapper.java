package cn.rf.hz.web.cloudpark.daoxx;

import java.util.ArrayList;

import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtmin_anomaly;

public interface Tc_usercrdtmin_anomalyMapper {
    int deleteByPrimaryKey(Integer recordid);

    int insert(Tc_usercrdtmin_anomaly record);

    int insertSelective(Tc_usercrdtmin_anomaly record);

    Tc_usercrdtmin_anomaly selectByPrimaryKey(Integer recordid);

    int updateByPrimaryKeySelective(Tc_usercrdtmin_anomaly record);

    int updateByPrimaryKey(Tc_usercrdtmin_anomaly record);
    

    
   void batchInsertusercrdtmin_anomaly(ArrayList<Tc_usercrdtmin_anomaly> list);
}