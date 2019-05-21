package cn.rf.hz.web.mapper.gd;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.bean.gd.ParkingEntranceRecord;

public interface GdEntranceRecognizeMapper
{

	ParkingEntranceRecord getEntranceRecognizePlateNumber(@Param("carParkId")Integer carParkId,@Param("licensePlateNumber")String licensePlateNumber);

	int getEntranceRecognizePlateNumber(Map<String, Object> map);

	int updateParkingEntranceRecordImagePath(Map<String, Object> map);

	Map<String, Object> findByWSEntranceInfo(Map<String, Object> map);

	int updateBySelective(Map<String, Object> map);

	int saveGdEntranceRecognize(Map<String, Object> map);

	Map<String, Object> findGdEntranceRecognize(Map<String, Object> map);

	int updateMark1(Map<String, Object> map);

	Object saveGdEntranceRecognize1(JSONArray ja);

	int findEntranceNumber(Map<String, Object> map);

	Map<String, Object> findEntranceInfo(Map<String, Integer> map);

	Map<String, Object> yunUpdate(JSONObject jsonData);

}
