package cn.rf.hz.web.mapper.gd;

import java.util.Map;

public interface CarParkMapsMapper<T>
{

	Map<String, Object> findLicensePlateNumber(Map<String, Object> map);

	Map<String, Object> findCarInfo(Map<String, Object> map);

}
