package cn.rf.hz.web.cloudpark.daoxx;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.reformer.cloudpark.model.Tc_userinfo;

import cn.rf.hz.web.cloudpark.moder.Tc_whiteuserinfo;

public interface Tc_whiteuserinfoMapper {
	int deleteByPrimaryKey(Integer recordid);

	int insert(Tc_whiteuserinfo record);

	int insertSelective(Tc_whiteuserinfo record);

	Tc_whiteuserinfo selectByPrimaryKey(Integer recordid);

	int updateByPrimaryKeySelective(Tc_whiteuserinfo record);

	int updateByPrimaryKey(Tc_whiteuserinfo record);

	List<Tc_whiteuserinfo> selectByParkingNo(String parkingno);

	Tc_whiteuserinfo orderuser_Is_blackuser(@Param("carcode") String carcode,
			@Param("parkinglotno") String parkinglotno, @Param("datetime") Date datetime);

	Tc_whiteuserinfo selectByParkNoAndCarCode(@Param("parkNo") String parkNo, @Param("carCode") String carCode);

	Tc_whiteuserinfo selectByDataSourceAndSourceID(@Param("parkNo") String parkNo,
			@Param("datasource") String datasource, @Param("sourceid") String sourceid);

	int deleteByDataSourceAndSourceID(@Param("parkNo") String parkNo, @Param("datasource") String datasource,
			@Param("sourceid") String sourceid);
}