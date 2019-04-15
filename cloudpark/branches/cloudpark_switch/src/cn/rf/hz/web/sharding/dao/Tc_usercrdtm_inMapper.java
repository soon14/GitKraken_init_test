package cn.rf.hz.web.sharding.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm_in;

public interface Tc_usercrdtm_inMapper {
	int deleteByPrimaryKey(Integer recordid);

	int insert(Tc_usercrdtm_in record);

	int insertSelective(Tc_usercrdtm_in record);

	Tc_usercrdtm_in selectByPrimaryKey(Integer recordid);

	int updateByPrimaryKeySelective(Tc_usercrdtm_in record);

	int updateByPrimaryKey(Tc_usercrdtm_in record);

	int deleteParkOut(Tc_usercrdtm_in record);

	int deletebyrecordid(@Param("recordid") long recordid, @Param("parkinglotno") String parkinglotno);

	List<Object> queryTc_usercrdtm_in(Map<String, Object> map);

	List<Object> queryTc_usercrdtm_in_limit(Map<String, Object> map);

	List<Tc_usercrdtm_in> selectByParknoAndCarCode(Map<String, Object> param);

	List<Tc_usercrdtm_in> selectByParknoAndCarCode1(Map<String, Object> param);

	List<Tc_usercrdtm_in> selectByParknoAndCarCode2(Map<String, Object> param);

	List<Tc_usercrdtm_in> selectByParknoAndNumber(@Param("parkinglotno") String parkinglotno,
			@Param("topsize") Integer topsize);

	Tc_usercrdtm_in queryParkingStatebyCarCode(@Param("carcode") String carcode,
			@Param("parkinglotno") String parkinglotno, @Param("partitionID") int partitionID);

	Tc_usercrdtm_in queryLaterCode(@Param("carcode") String carcode, @Param("parkinglotno") String parkinglotno,
			@Param("datetime") Date datetime);

	int delcarfrom_in(@Param("carcode") String carcode, @Param("parkinglotno") String parkinglotno,
			@Param("partitionID") int partitionID);

	int batchInsertUsercrdtm_in(ArrayList<Tc_usercrdtm_in> list);

	void batchDelUsercrdtm_in(ArrayList<Tc_usercrdtm_in> list);

	void batchDelUsercrdtm_in_out(ArrayList<Tc_usercrdtm_in> list);
	// Public User selectUser(@param("userName") String
	// name,@param("userArea")String area);
	// List<ParkingInfo> selectParkingBylimit(@Param("offset") int offset,
	// @Param("limit") int limit);
	// Public User
	// selectUser(@param(“userName”)Stringname,@param(“userArea”)String area);

	List<Object> queryByCondition(Map<String, Object> map);

	int batchReplaceOldUsercrdtm_in(ArrayList<Tc_usercrdtm_in> list);

	void batchUpdateUsercrdtm_in(ArrayList<Tc_usercrdtm_in> list);

	void batchDelUsercrdtm_inByRecordId(List<Tc_usercrdtm_in> list);

	Tc_usercrdtm_in queryOneByCarCodeOrderByCrdtm(Map<String, Object> map);

	List<Tc_usercrdtm_in> selectByParknoAndSize(Map<String, Object> map);

	// Tc_usercrdtm_in selectcarin(@Param("carcode") String
	// carcode,@Param("parkinglotno") parkinglotno);
	Tc_usercrdtm_in selectcarin(@Param("carcode") String carcode, @Param("parkinglotno") String parkinglotno);

	int selectcarinCount(@Param("parkinglotno") String parkinglotno);

	List<Tc_usercrdtm_in> selectCarInNoLincenPlateNumber(Map<String, Object> map);

	List<Tc_usercrdtm_in> queryUserCrdtmInListByWhere(Map<String, Object> map);

	int queryUserCrdtmInListCountByWhere(Map<String, Object> map);
}