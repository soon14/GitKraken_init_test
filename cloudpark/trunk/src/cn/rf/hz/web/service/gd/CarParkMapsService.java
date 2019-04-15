package cn.rf.hz.web.service.gd;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.rf.hz.web.mapper.gd.CarParkMapsMapper;
@Service("carParkMapsService")
public class CarParkMapsService<T>
{

	@Autowired
	private CarParkMapsMapper<T> mapper;

	public CarParkMapsMapper<T> getMapper()
	{
		return mapper;
	}

	public void setMapper(CarParkMapsMapper<T> mapper)
	{
		this.mapper = mapper;
	}

	public Map<String, Object> findLicensePlateNumber(Map<String, Object> map)
	{
		return getMapper().findLicensePlateNumber(map);
	}

	public Map<String, Object> findCarInfo(Map<String, Object> map)
	{
		return getMapper().findCarInfo(map);
	}

}
