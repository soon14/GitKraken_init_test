package cn.rf.hz.web.mapper.gd;

import java.util.Map;

import com.alibaba.fastjson.JSONArray;

import cn.rf.hz.web.bean.gd.ParkingExportRecord;

public interface GdExportRecognizeMapper<T>
{

	ParkingExportRecord findById(Map<String, Object> map);

	int save(Map<String, Object> map);

	int update(Map<String, Object> map);

	Object save1(JSONArray data);

}
