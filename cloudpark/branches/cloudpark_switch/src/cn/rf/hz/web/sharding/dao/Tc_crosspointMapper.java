package cn.rf.hz.web.sharding.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.rf.hz.web.cloudpark.moder.Tc_crosspoint;



public interface Tc_crosspointMapper {
    int deleteByPrimaryKey(long recordid);

    int insert(Tc_crosspoint record);

    int insertSelective(Tc_crosspoint record);

    Tc_crosspoint selectByPrimaryKey(long recordid);

    int updateByPrimaryKeySelective(Tc_crosspoint record);

    int updateByPrimaryKey(Tc_crosspoint record);

    int deleteParkOut(Tc_crosspoint record);

    int deletebyrecordid(@Param("recordid") long recordid,
                         @Param("parkinglotno") String parkinglotno,
                         @Param("partitionid") int partitionid);

    List<Object> queryTc_crosspoint(Map<String, Object> map);

    List<Object> queryTc_crosspoint_limit(Map<String, Object> map);

    List<Tc_crosspoint> selectByParknoAndCarCode(Map<String, Object> param);

    List<Tc_crosspoint> selectByParknoAndNumber(@Param("parkinglotno") String parkinglotno,
                                                @Param("topsize") Integer topsize);

    List<Tc_crosspoint> queryParkingStatebyCarCode(@Param("carcode") String carcode,
                                             @Param("parkinglotno") String parkinglotno,
                                             @Param("partitionID") int partitionID);

    Tc_crosspoint queryLaterCode(@Param("carcode") String carcode,
                                 @Param("parkinglotno") String parkinglotno,
                                 @Param("datetime") Date datetime);

    int delcarfrom_in(@Param("carcode") String carcode, @Param("parkinglotno") String parkinglotno,
                      @Param("partitionID") int partitionID);

    //  Public User selectUser(@param("userName") String name,@param("userArea")String area);
    //List<ParkingInfo> selectParkingBylimit(@Param("offset") int offset, @Param("limit") int limit); 
    //Public User selectUser(@param(“userName”)Stringname,@param(“userArea”)String area);

    List<Object> queryByCondition(Map<String, Object> map);

    List<Tc_crosspoint> selectByParknoAndSize(Map<String, Object> map);

    //Tc_corsspoint selectcarin(@Param("carcode") String carcode,@Param("parkinglotno") parkinglotno);
    Tc_crosspoint selectcarin(@Param("carcode") String carcode,
                              @Param("parkinglotno") String parkinglotno);

    int selectcarinCount(@Param("parkinglotno") String parkinglotno);

    List<Tc_crosspoint> selectCarInNoLincenPlateNumber(Map<String, Object> map);
    
    Tc_crosspoint queryFirstCrosspointByCarCode(Map<String, Object> map);
}
