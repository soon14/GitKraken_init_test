package cn.rf.hz.web.service.gd;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.rf.hz.web.mapper.gd.GdCarParkBoxMapper;

@Service("gdCarParkBoxService")
public class GdCarParkBoxService
{
	@Autowired
	private GdCarParkBoxMapper mapper;

	public GdCarParkBoxMapper getMapper()
	{
		return mapper;
	}

	public void setMapper(GdCarParkBoxMapper mapper)
	{
		this.mapper = mapper;
	}

	public int saveGdCarparkInout(Map<String, Object> m1)
	{
		return getMapper().saveGdCarparkInout(m1);
	}

	public int saveGdCarparkEnterclose(Map<String, Object> m1)
	{
		return getMapper().saveGdCarparkEnterclose(m1);
	}

	public void delGdCarparkInout(Integer carParkId)
	{
		getMapper().delGdCarparkInout(carParkId);
	}

	public void delGdCarparkEnterclose(Integer carParkId)
	{
		getMapper().delGdCarparkEnterclose(carParkId);
		
	}

	public List<Map<String, Object>> findRegion(Map<String, Object> m)
	{
		return getMapper().findRegion(m);
	}

	public Map<String, Object> findRegionByid(Map<String, Object> m)
	{
		return getMapper().findRegionByid(m);
	}

	public List findInOutInfo(Map<String, Object> m)
	{
		return getMapper().findInOutInfo(m);
	}

	public int updateInOutInfo(Map<String, Object> m)
	{
		return getMapper().updateInOutInfo(m);
	}

	public List<Map<String, Object>> queryByList(Map<String, Object> m)
	{
		return getMapper().queryByList(m);
	}

	public List<Map<String, Object>> queryByList1(Map<String, Object> m)
	{
		// TODO Auto-generated method stub
		return getMapper().queryByList1(m);
	}


}
