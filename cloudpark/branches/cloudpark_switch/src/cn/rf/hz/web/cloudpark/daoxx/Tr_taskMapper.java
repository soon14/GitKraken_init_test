package cn.rf.hz.web.cloudpark.daoxx;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.rf.hz.web.cloudpark.moder.Tr_task;




public interface Tr_taskMapper {
    int deleteByPrimaryKey(Integer fid);

    int insert(Tr_task record);

    int insertSelective(Tr_task record);

    Tr_task selectByPrimaryKey(Integer fid);

    int updateByPrimaryKeySelective(Tr_task record);
   
    int updateByPrimaryKey(Tr_task record);
    List<Tr_task> findJobparkingNO();
    int updateByparkingNo(@Param("parkingNo") String parkingNo);
    int updatejobTime(@Param("parkingNo") String parkingNo);
    void recorveryState();
   // Tc_usercrdtm_in queryParkingStatebyCarCode(@Param("carcode") String carcode,@Param("parkinglotno") String parkinglotno);
}