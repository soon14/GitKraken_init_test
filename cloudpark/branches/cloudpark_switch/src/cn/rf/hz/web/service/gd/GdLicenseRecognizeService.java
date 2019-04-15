package cn.rf.hz.web.service.gd;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.mapper.gd.GdLicenseRecognizeMapper;
import cn.rf.hz.web.utils.JedisPoolUtils;
import cn.rf.hz.web.utils.RedisVideoStopCarInfo;

@Service("gdLicenseRecognizeService")
public class GdLicenseRecognizeService<T>
{
	private final static Logger LOG = Logger.getLogger(GdLicenseRecognizeService.class);
	@Autowired
	private GdLicenseRecognizeMapper<T> mapper;

	public GdLicenseRecognizeMapper<T> getMapper()
	{
		return mapper;
	}

	public void setMapper(GdLicenseRecognizeMapper<T> mapper)
	{
		this.mapper = mapper;
	}

	/**
	 * 根据停车场id 、车牌号查询场内记录
	 */
	public T getLicenseRecognizePlateNumber(T t)
	{
		return getMapper().getLicenseRecognizePlateNumber(t);
	}

	public int saveGdLicenseRecognize(Map<String, Object> map)
	{
		return getMapper().saveGdLicenseRecognize(map);
	}

	public Map<String, Object> findById(Map<String, Object> map)
	{
		return getMapper().findById(map);
	}

	public int updateBySelective(Map<String, Object> map)
	{
		return getMapper().updateBySelective(map);

	}

	/**
	 * 老平台 场内系统
	 * 
	 * @param map
	 * @return
	 */
	public String saveLicenseRecognize(Map<String, Object> map)
	{

		int rowcount = 0;
		try
		{
			//更新离场
			getMapper().licenseRecognizeOut(map);
			
			//清空redis
			JedisPoolUtils.del("videoLicenseRecognize_" + map.get("licensePlateNumber"));
			 
			//入场
			rowcount = getMapper().saveGdLicenseRecognize(map);

		} catch (Exception e)
		{
			if (e.getClass() == DuplicateKeyException.class)
			{
				rowcount = getMapper().updateLicense(map);
			}
			else
				LOG.error("老平台场内入场异常：" + e.getMessage());
		}

		if (rowcount > 0)
		{
			RedisVideoStopCarInfo.license((JSONObject)JSON.toJSON(map));
			return "{\"errorcode\":0}";
		}
		else
			return "{\"errorcode\":1}";
	}

}
