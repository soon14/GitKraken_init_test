package cn.rf.hz.web.action.gd;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.service.gd.GdEntranceRecognizeService;
import cn.rf.hz.web.utils.RedisVideoStopCarInfo;
import cn.rf.hz.web.utils.WriterUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 出入口
 * 
 * @author 程依寿 2015年10月22日 下午4:53:12
 */
@Controller
@RequestMapping("/gdEntranceRecognize")
public class GdEntranceRecognizeAction
{
	private final static Logger LOG = Logger.getLogger(GdEntranceRecognizeAction.class);

	@SuppressWarnings("rawtypes")
	@Autowired(required = false)
	private GdEntranceRecognizeService gdEntranceRecognizeService;

	/**
	 * 
	 * 新平台
	 * 
	 * 4.1上传进场数据
	 * 
	 * 保存线下停车记录 停车信息、图片等
	 * 
	 * @param per
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/saveGdEntranceRecognize", method = RequestMethod.POST)
	public void saveGdEntranceRecognize(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		WriterUtil.writer(response, this.gdEntranceRecognizeService.saveGdEntranceRecognize(requestBody));

	}

	/**
	 * 更新车位离场
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateMark", method = RequestMethod.POST)
	public String updateMark(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		return this.gdEntranceRecognizeService.updateMark(requestBody);
	}

	/**
	 * 兼容老系统
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/saveOldGdEntranceRecognize", method = RequestMethod.POST)
	public void saveOldGdEntranceRecognize(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		WriterUtil.writer(response, this.gdEntranceRecognizeService.saveOldGdEntranceRecognize(requestBody));
	}

	/**
	 * 线下纠正车牌 老系统
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateOldGdEntranceRecognize", method = RequestMethod.POST)
	public void updateOldGdEntranceRecognize(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		LOG.info("线下纠正车牌信息（老系统）:" + requestBody);
		String result = "{\"errorcode\":1}";
		try
		{
			Map<String, Object> map = JSON.parseObject(requestBody);

			map.put("recognitionNumber", map.get("licensePlateNumber"));
			map.remove("licensePlateNumber");

			Map<String, Object> entranceInfo = this.gdEntranceRecognizeService.findGdEntranceRecognize(map);

			String oldLicensePlateNumber = entranceInfo.get("recognitionNumber") + "";

			int rowcount = this.gdEntranceRecognizeService.updateBySelective(map);
			if (rowcount > 0)
			{
				// 更新redis
				entranceInfo = this.gdEntranceRecognizeService.findGdEntranceRecognize(map);
				entranceInfo.put("oldLicensePlateNumber", oldLicensePlateNumber);

				RedisVideoStopCarInfo.offlineUpdateLicence((JSONObject) JSON.toJSON(entranceInfo));

				result = "{\"errorcode\":0}";
			} else
				result = "{\"errorcode\":1}";
		} catch (Exception e)
		{
			result = "{\"errorcode\":1}";
			LOG.error("线下纠正车牌异常（老系统）:" + e);
		}
		LOG.info("线下纠正车牌结果（老系统）:" + result);

		WriterUtil.writer(response, result);
	}

	/**
	 * 线上更改车牌 云更新
	 * 
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateYunOldGdEntranceRecognize", method = RequestMethod.POST)
	public void updateYunOldGdEntranceRecognize(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		LOG.info("云更新车牌信息（老系统）:" + requestBody);
		String result = "{\"result\":1}";
		try
		{
			JSONObject jsonObject = JSON.parseObject(requestBody);
			JSONObject jsonData = jsonObject.getJSONArray("data").getJSONObject(0);

			jsonData.put("recordId", jsonData.get("recordId"));
			jsonData.put("carParkId", jsonData.get("carParkId"));
			Map<String, Object> map = this.gdEntranceRecognizeService.yunUpdate(jsonData);
			jsonData.put("oldRecordId", map.get("oldRecordId"));
			String oldLicensePlateNumber = map.get("recognitionNumber") + "";
			int rowcount = this.gdEntranceRecognizeService.updateBySelective(jsonData);
			if (rowcount > 0)
			{
				//redis
				Map<String, Object> entranceInfo = this.gdEntranceRecognizeService.findGdEntranceRecognize(map);
				entranceInfo.put("oldLicensePlateNumber", oldLicensePlateNumber);
				RedisVideoStopCarInfo.offlineUpdateLicence((JSONObject) JSON.toJSON(entranceInfo));

				jsonData.put("result", 0);
			} else
				jsonData.put("result", 1);

			result = jsonData.toJSONString();

		} catch (Exception e)
		{
			result = "{\"result\":1}";
			LOG.error("云更新车牌异常（老系统）:" + e);
		}

		LOG.info("云更新车牌结果（老系统）:" + result);
		WriterUtil.writer(response, result);
	}

	/**
	 * 线下更新车牌，新系统
	 * 
	 * 4.3上传线下纠正车牌数据
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateGdEntranceRecognize", method = RequestMethod.POST)
	public void updateGdEntranceRecognize(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		JSONObject licenMap = JSON.parseObject(requestBody);

		Integer count = (Integer) licenMap.get("count");
		Integer carParkId = (Integer) licenMap.get("parkId");
		JSONArray jsonDataArray = licenMap.getJSONArray("data");

		JSONObject resultObject = new JSONObject();// 返回结果集
		JSONArray resultArray = new JSONArray();

		resultObject.put("count", count);
		resultObject.put("carParkId", carParkId);
		for (int i = 0; i < count; i++)
		{
			JSONObject jsonData = jsonDataArray.getJSONObject(i);

			jsonData.put("carParkId", carParkId);
			jsonData.put("oldRecordId", jsonData.getInteger("record_id"));
			jsonData.put("recognitionNumber", jsonData.getString("licensePlateNumber"));
			jsonData.remove("licensePlateNumber");
			int rowcount = this.gdEntranceRecognizeService.updateBySelective(jsonData);
			jsonData.put("licensePlateNumber", jsonData.getString("recognitionNumber"));

			if (rowcount > 0)
			{
				// 更新redis
				Map<String, Object> entranceInfo = this.gdEntranceRecognizeService.findGdEntranceRecognize(jsonData);
				entranceInfo.put("oldLicensePlateNumber", jsonData.getString("oldLicensePlateNumber"));
				RedisVideoStopCarInfo.offlineUpdateLicence((JSONObject) JSON.toJSON(entranceInfo));

				jsonData.put("result", 0);
			} else
			{
				jsonData.put("result", 1);
			}

			jsonData.remove("recognitionNumber");
			jsonData.remove("oldRecordId");
			jsonData.remove("oldLicensePlateNumber");
			resultArray.add(jsonData);
		}
		resultObject.put("data", resultArray);
		JSONObject j = new JSONObject();
		j.put("UpdateLicensePlateInfo", resultObject);

		WriterUtil.writer(response, j.toJSONString());
	}

	/**
	 * 林锋 查找时间段内的入场数
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/findEntranceNumber", method = RequestMethod.POST)
	public void findEntranceNumber(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{

		Map<String, Object> map = JSON.parseObject(requestBody);

		int count = this.gdEntranceRecognizeService.findEntranceNumber(map);

		WriterUtil.writer(response, "{\"count\":" + count + "}");
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

}
