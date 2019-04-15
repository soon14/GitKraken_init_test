package cn.rf.hz.web.cloudpark.daoxx;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.apache.ibatis.annotations.Param;

import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtmin_anomaly;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtmout_anomaly;

public interface Tc_usercrdtmout_anomalyMapper {
    int deleteByPrimaryKey(Integer recordid);

    int insert(Tc_usercrdtmout_anomaly record);

    int insertSelective(Tc_usercrdtmout_anomaly record);

    Tc_usercrdtmout_anomaly selectByPrimaryKey(Integer recordid);

    int updateByPrimaryKeySelective(Tc_usercrdtmout_anomaly record);

    int updateByPrimaryKey(Tc_usercrdtmout_anomaly record);
    void batchInsertusercrdtmout_anomaly(ArrayList<Tc_usercrdtmout_anomaly> list);
    
    int deleteByLotNoAndCarcode(@Param("parkinglotno") String parkinglotno,@Param("carcode") String carcode,@Param("intime") Timestamp intime);
}