package cn.rf.hz.web.action.gd;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.service.gd.GdLicenseRecognizeService;
import cn.rf.hz.web.utils.WriterUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 场内
 * 
 * @author 程依寿 2015年10月22日 下午4:56:49
 */
@Controller
@RequestMapping("/gdLicenseRecognize")
public class GdLicenseRecognizeAction 
{

	private final static Logger LOG = Logger.getLogger(GdLicenseRecognizeAction.class);

	@Autowired(required = false)
	private GdLicenseRecognizeService gdLicenseRecognizeService;

	/**
	 * 
	 * 线下场内录入
	 * 
	 * 停车、图片等信息
	 * 
	 * @param parkingId
	 * @param realParkingId
	 * @param oldRecordId
	 * @param licensePlateNumber
	 * @param inTime
	 * @param imgName
	 * @param carParkId
	 * @param mark
	 * @param groupId
	 * @param mallId
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/saveGdLicenseRecognize", method = RequestMethod.POST)
	@ResponseBody
	public void saveGdLicenseRecognize(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = (Map) JSON.parseObject(requestBody, Map.class);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try
		{
			map.put("addTime", new Date());
			String inTime = (String) map.get("inTime");
			if (!"".equals(inTime) && null != inTime)
				map.put("inTime", df.parse(inTime));
			else
				map.put("inTime", new Date());
		} catch (Exception e)
		{
		}

		Map<String, Object> map1 = gdLicenseRecognizeService.findById(map);
		int rowcount = 0;
		if (map1 == null)
		{

			rowcount = this.gdLicenseRecognizeService.saveGdLicenseRecognize(map);

			if (rowcount > 0)
				WriterUtil.writer(response, "{\"errorcode\":0}");
			else
				WriterUtil.writer(response, "{\"errorcode\":1}");
		} else
		{
			map.put("recordId", map1.get("recordId"));
			rowcount = this.gdLicenseRecognizeService.updateBySelective(map);

			if (rowcount > 0)
				WriterUtil.writer(response, "{\"errorcode\":0}");
			else
				WriterUtil.writer(response, "{\"errorcode\":1}");
		}

		if (rowcount > 0)
			WriterUtil.writer(response, "{\"errorcode\":0}");
		else
		{

			WriterUtil.writer(response, "{\"errorcode\":1}");
		}

	}


	public static Map<String, Object> getFieldVlaue(Object obj) throws Exception
	{
		Map<String, Object> mapValue = new HashMap<String, Object>();
		Class<?> cls = obj.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields)
		{
			String name = field.getName();
			String strGet = "get" + name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
			Method methodGet = cls.getDeclaredMethod(strGet);
			Object object = methodGet.invoke(obj);
			String value = object != null ? object.toString() : "";
			mapValue.put(name, value);
		}
		return mapValue;
	}

	/**
	 * 
	 * 老平台 场内入场
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/oldLicenseRecognizeIn", method = RequestMethod.POST)
	@ResponseBody
	public String oldLicenseRecognizeIn(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		LOG.debug("场内入场信息（老系统）：" + requestBody);
		JSONObject jsonData = null;
		String result = "{\"errorcode\":1}";
		try
		{
			jsonData = JSON.parseObject(requestBody);
			jsonData.put("inTime", jsonData.getDate("inTime"));
			jsonData.put("addTime", new Date());
			result = this.gdLicenseRecognizeService.saveLicenseRecognize(jsonData);
		} catch (Exception e)
		{
			jsonData = new JSONObject();
			LOG.error("场内入场异常（老系统）：" + e);
		}
		LOG.debug("场内入场结果（老系统）：" + result);

		return result;

	}

}
