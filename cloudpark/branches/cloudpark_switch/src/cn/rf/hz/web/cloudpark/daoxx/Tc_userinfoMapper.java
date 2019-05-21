package cn.rf.hz.web.cloudpark.daoxx;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.reformer.cloudpark.model.Tc_userinfo;


//import cn.rf.hz.web.cloudpark.moder.Tc_userinfo;

public interface Tc_userinfoMapper {
    int deleteByPrimaryKey(Integer recordid);

    int insert(Tc_userinfo record);

    int insertSelective(Tc_userinfo record);

    Tc_userinfo selectByPrimaryKey(Integer recordid);

    int updateByPrimaryKeySelective(Tc_userinfo record);
    
    int updateByPrimaryKeyWithBLOBs(Tc_userinfo record);

    int updateByPrimaryKey(Tc_userinfo record);
    Tc_userinfo selectBylicensePlateNumber(@Param("carcode") String carcode,@Param("parkinglotno") String parkinglotno,@Param("datetime") Date datetime);
    
    List<Tc_userinfo> selectByParkinglotno(String parkinglotno);
    
    Tc_userinfo selectByParkCodeAndEnddt(@Param("carcode") String carcode,@Param("parkinglotno") String parkinglotno,@Param("datetime") Date datetime);

    Tc_userinfo selectByCarCodeAndParkingLotNo(@Param("carcode") String carcode,@Param("parkinglotno") String parkinglotno);
    
    Tc_userinfo selectByUserNoAndParkingLotNo(@Param("carcode") String carcode,@Param("carcode1") String carcode1,@Param("carcode2") String carcode2,@Param("parkinglotno") String parkinglotno);

	Tc_userinfo selectByDataSourceAndParkingLotNo(@Param("parkinglotno") String parkinglotno,
			@Param("datasource") String datasource, @Param("sourceid") String sourceid);
}