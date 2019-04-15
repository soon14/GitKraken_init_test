package cn.rf.hz.web.mapper.gd;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.bean.gd.Area;

public interface ParkingMapper
{


	public int save(Map<String, Object> map);

	public int updateBySelective(Map<String, Object> map);

	public Object delete(Object map);

	public int savePiliang(JSONArray jsonArray);

	public JSONObject queryById(JSONObject map);

	public int update(JSONObject map);

}
