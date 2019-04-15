package cn.rf.hz.web.action.gd;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
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
import cn.rf.hz.web.bean.gd.CarParks;
import cn.rf.hz.web.service.gd.CarParksService;
import cn.rf.hz.web.service.gd.GdCarParkBoxService;
import cn.rf.hz.web.utils.RequestUtil;
import cn.rf.hz.web.utils.URLUtils;
import cn.rf.hz.web.utils.WriterUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("/carParkBoxAction")
public class GdCarParkBoxAction
{
	private final static Logger LOG = Logger.getLogger(GdCarParkBoxAction.class);

	@Autowired(required = false)
	private GdCarParkBoxService carParkBoxService;

	@Autowired(required = false)
	private CarParksService<CarParks> carParksService;

	/**
	 * 
	 * 停车场盒子信息录入
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/saveCarParkBoxInfoNew", method = RequestMethod.POST)
	public void saveCarParkBoxInfoNew(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{

		JSONObject data = JSON.parseObject(requestBody);

		JSONObject resultData = new JSONObject();
		// 添加停车场
		Map<String, Object> carParkInfo = carParksService.saveCarPark(requestBody);
		// 添加成功
		if ((Integer) carParkInfo.get("errorcode") == 0)
		{
			Integer carParkId = (Integer) carParkInfo.get("recordId");
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("carParkId", carParkId);

			// 销售员，施万义持久层
			JSONObject salesMan = new JSONObject();
			try
			{
				salesMan = JSON.parseObject(RequestUtil.requestUrl(URLUtils.userSystem + "/SalesmanInfo/addSalesmanInfo.do?uuid=" + data.getString("uuid")
						+ "&carParkId=" + carParkId));
				
				if(salesMan == null)
				{
					salesMan = new JSONObject();
					salesMan.put("errorcode", 1);
				}
			} catch (Exception e)
			{
				salesMan = new JSONObject();
				salesMan.put("errorcode", 1);
			}
			if (salesMan.getInteger("errorcode") == 0)
			{

				int rowcount = 0;
				JSONArray jsonArray = ((JSONArray) data.get("InOutward"));
				// carParkBoxService.delGdCarparkInout(carParkId);
				// carParkBoxService.delGdCarparkEnterclose(carParkId);
				for (int i = 0; i < jsonArray.size(); i++)
				{
					Map<String, Object> m1 = (Map) jsonArray.get(i);
					m1.put("carparkId", carParkId);
					rowcount = carParkBoxService.saveGdCarparkInout(m1);
					JSONArray jsonArray1 = ((JSONArray) m1.get("channel"));
					for (int i1 = 0; i1 < jsonArray1.size(); i1++)
					{
						Map<String, Object> m2 = (Map) jsonArray1.get(i1);
						m2.put("carparkId", carParkId);
						m2.put("inOutId", m1.get("recordId"));
						rowcount = carParkBoxService.saveGdCarparkEnterclose(m2);
					}
				}

				jsonArray = new JSONArray();
				// 更新停车场
				// 根据停车场Id获取所有出入口信息
				List<Map<String, Object>> carparkinout = carParkBoxService.queryByList(m);
				for (int i = 0; i < carparkinout.size(); i++)
				{
					JSONObject inoutObj = new JSONObject();
					JSONArray array = new JSONArray();
					inoutObj.put("name", carparkinout.get(i).get("inOutName"));
					inoutObj.put("lng", carparkinout.get(i).get("longitude"));
					inoutObj.put("lat", carparkinout.get(i).get("latitude"));
					// 根据出入口Id获取所有的通道信息
					m.put("inOutId", carparkinout.get(i).get("recordid"));
					List<Map<String, Object>> carparkenterclose = carParkBoxService.queryByList1(m);
					for (int j = 0; j < carparkenterclose.size(); j++)
					{
						JSONObject channelObj = new JSONObject();
						channelObj.put("name", carparkenterclose.get(j).get("entercloseName"));
						channelObj.put("btsn", carparkenterclose.get(j).get("boxImei"));
						array.add(channelObj);
					}
					inoutObj.put("passage", array);
					jsonArray.add(inoutObj);
				}
				requestBody = jsonArray.toJSONString();

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("gateInfo", requestBody);
				map.put("carParkId", carParkId);
				int updrow = carParksService.updateCarParksGateInfo(map);

				resultData.put("errorMsg", "");
				resultData.put("errorCode", 0);
			} else
			{
				// 失败的的话，就删除停车场
				carParksService.delCarPark(carParkId);

				resultData.put("errorMsg", "添加停车场失败");
				resultData.put("errorCode", 1);
			}

		} else
		{
			resultData.put("errorMsg", "添加停车场失败");
			resultData.put("errorCode", 1);
		}

		WriterUtil.writer(response, resultData.toJSONString());
	}

	/**
	 * 
	 * 停车场盒子信息录入
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/saveCarParkBoxInfo", method = RequestMethod.POST)
	public void saveCarParkBoxInfo(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> m = JSON.parseObject(requestBody, Map.class);
		int rowcount = 0;
		JSONArray jsonArray = ((JSONArray) m.get("gateInfo"));
		Integer carParkId = (Integer) m.get("carParkId");
		// carParkBoxService.delGdCarparkInout(carParkId);
		// carParkBoxService.delGdCarparkEnterclose(carParkId);
		for (int i = 0; i < jsonArray.size(); i++)
		{
			Map<String, Object> m1 = (Map) jsonArray.get(i);
			rowcount = carParkBoxService.saveGdCarparkInout(m1);
			JSONArray jsonArray1 = ((JSONArray) m1.get("enterClose"));
			for (int i1 = 0; i1 < jsonArray1.size(); i1++)
			{
				Map<String, Object> m2 = (Map) jsonArray1.get(i1);
				m2.put("inOutId", m1.get("recordId"));
				rowcount = carParkBoxService.saveGdCarparkEnterclose(m2);
			}
		}

		jsonArray = new JSONArray();
		// 更新停车场
		// 根据停车场Id获取所有出入口信息
		List<Map<String, Object>> carparkinout = carParkBoxService.queryByList(m);
		for (int i = 0; i < carparkinout.size(); i++)
		{
			JSONObject inoutObj = new JSONObject();
			JSONArray array = new JSONArray();
			inoutObj.put("name", carparkinout.get(i).get("inOutName"));
			inoutObj.put("lng", carparkinout.get(i).get("longitude"));
			inoutObj.put("lat", carparkinout.get(i).get("latitude"));
			// 根据出入口Id获取所有的通道信息
			m.put("inOutId", carparkinout.get(i).get("recordid"));
			List<Map<String, Object>> carparkenterclose = carParkBoxService.queryByList1(m);
			for (int j = 0; j < carparkenterclose.size(); j++)
			{
				JSONObject channelObj = new JSONObject();
				channelObj.put("name", carparkenterclose.get(j).get("entercloseName"));
				channelObj.put("btsn", carparkenterclose.get(j).get("boxImei"));
				array.add(channelObj);
			}
			inoutObj.put("passage", array.toString());
			jsonArray.add(inoutObj);
		}
		requestBody = jsonArray.toJSONString();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("gateInfo", requestBody);
		map.put("carParkId", carParkId);
		int updrow = carParksService.updateCarParksGateInfo(map);

		if (rowcount > 0)
		{
			if (updrow > 0)
			{
				WriterUtil.writer(response, "{\"errorcode\":0}");
			} else
				WriterUtil.writer(response, "{\"errorcode\":1}");
		} else
			WriterUtil.writer(response, "{\"errorcode\":1}");

	}

	/**
	 * 省市区
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/findRegion", method = RequestMethod.POST)
	public void findRegion(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> m = JSON.parseObject(requestBody, Map.class);

		List<Map<String, Object>> list = carParkBoxService.findRegion(m);
		WriterUtil.writer(response, JSON.toJSONString(list));

	}

	/**
	 * 
	 * 根据id 省市区
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/findRegionByid", method = RequestMethod.POST)
	public void findRegionByid(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> m = JSON.parseObject(requestBody, Map.class);

		Map<String, Object> map = carParkBoxService.findRegionByid(m);
		WriterUtil.writer(response, JSON.toJSONString(map));

	}

	/**
	 * 
	 * 根据id 大门、通道盒子信息
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/findInOutInfo", method = RequestMethod.POST)
	public void findInOutInfo(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		System.out.println(requestBody);
		Map<String, Object> m = JSON.parseObject(requestBody, Map.class);
		List list = carParkBoxService.findInOutInfo(m);
		WriterUtil.writer(response, JSON.toJSONString(list));
	}

	/**
	 * 
	 * 根据id 大门、通道盒子信息 岑挺
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateInOutInfo", method = RequestMethod.POST)
	public void updateInOutInfo(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> m = JSON.parseObject(requestBody, Map.class);
		int rowcount = carParkBoxService.updateInOutInfo(m);
		if (rowcount > 0)
			WriterUtil.writer(response, "{\"errorcode\":0}");
		else
			WriterUtil.writer(response, "{\"errorcode\":1}");
	}


	/**
	 * get请求
	 * 
	 * @param httpUrl
	 * @return
	 */
	public String requestUrl(String httpUrl)
	{
		String forward = "";
		String str = "";
		InputStream is = null;
		try
		{
			URL url = new URL(httpUrl);
			URLConnection con = url.openConnection();
			is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder tempStr = new StringBuilder();
			while ((str = br.readLine()) != null)
			{
				tempStr.append(str);
			}
			forward = tempStr.toString();
			is.close();
		} catch (Exception e)
		{
			LOG.error("requestUrl请求错误" + e.getMessage());
		}
		return forward;
	}
}
