package cn.rf.hz.web.action.gd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.bean.gd.Area;
import cn.rf.hz.web.bean.gd.AreaNode;
import cn.rf.hz.web.bean.gd.CarParks;
import cn.rf.hz.web.bean.gd.ParkingEntranceRecord;
import cn.rf.hz.web.bean.gd.ParkingRecord;
import cn.rf.hz.web.service.gd.CarParksService;
import cn.rf.hz.web.service.gd.GdAreaNodeService;
import cn.rf.hz.web.service.gd.GdAreaService;
import cn.rf.hz.web.service.gd.GdEntranceRecognizeService;
import cn.rf.hz.web.service.gd.GdLicenseRecognizeService;
import cn.rf.hz.web.utils.DateUtil;
import cn.rf.hz.web.utils.JedisPoolUtils;
import cn.rf.hz.web.utils.OSSUtil;
import cn.rf.hz.web.utils.StringUtil;
import cn.rf.hz.web.utils.URLUtils;
import cn.rf.hz.web.utils.WriterUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 与停车场相关的业务查询、入库等
 * 
 * @author 程依寿 2016年2月26日 上午11:09:52
 */
@Controller
@RequestMapping("/p")
public class CarParkSearchAction
{
	private final static Logger LOG = Logger.getLogger(CarParkSearchAction.class);

	@Autowired(required = false)
	private CarParksService<CarParks> carParksService;

	@Autowired(required = false)
	private GdEntranceRecognizeService parkingEntranceRecordService;

	@Autowired(required = false)
	private GdLicenseRecognizeService parkingRecordService;

	@Autowired(required = false)
	private GdAreaService areaService;

	@Autowired(required = false)
	private GdAreaNodeService areaNodeService;

	/**
	 * 停车场登录信息Session列表
	 */
	public static HashMap<String, CarParks> loginSessionMap = new HashMap<String, CarParks>();

	/**
	 * 
	 * 新 停车场查询
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/newPay", method = RequestMethod.GET)
	@ResponseBody
	public String searchNewPay(@RequestParam(value = "pid", required = true) Integer pid, @RequestParam(value = "search", required = true) String search,
			@RequestParam(value = "userID", required = false) String userID, @RequestParam(value = "appId", required = false) String appId,
			@RequestParam(value = "userType", required = false) String userType, @RequestParam(value = "urlType", required = false) String urlType,
			@RequestParam(value = "src", required = false) String src, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		// 停车开始时间
		Date inTime = null;
		// 停车时间
		long time = 0;
		// X天X小时X分钟X秒
		String parkTime = "";
		Map<String, Object> context = new HashMap<String, Object>();
		try
		{
			userID = ("".equals(userID) || null == userID) ? "0" : URLEncoder.encode(userID, "utf-8");

			context.put("userID", ("".equals(userID) || null == userID) ? "0" : userID);
			context.put("userType", ("".equals(userType) || null == userType) ? "0" : userType);
			context.put("urlType", ("".equals(urlType) || null == urlType) ? "0" : urlType);
			// 停车场id存入session
			context.put("carParkId", pid);

			if (!StringUtils.isEmpty(search))
			{
				search = new String(search.getBytes("iso8859-1"), "UTF-8");
			}
		} catch (Exception e)
		{
			LOG.error("查询车牌异常："+e.getMessage());
		}

		ParkingEntranceRecord entranceRecord = null;
		ParkingRecord record = null;
		JSONObject carPark = null;
		context.put("src", src);


		// 从redis里查询车牌
		entranceRecord = (ParkingEntranceRecord) JSON.parseObject(JedisPoolUtils.get("video_" + search), ParkingEntranceRecord.class);

		if (entranceRecord == null)
		{
			// 如果redis里无此车牌，则从数据库里在检索一遍，以确保数据不会被丢失
			entranceRecord = (ParkingEntranceRecord) parkingEntranceRecordService.getEntranceRecognizePlateNumber(null, search);
		}

		// 是否有入场数据
		if (entranceRecord != null)
		{
			// 是否离场
			if (entranceRecord.getMark() == 1)
			{
				context.put("error", "未找到车牌号");
			} else
			{
				// 未传停车场id，不参与判断
				pid = (pid == 0) ? entranceRecord.getCarParkId() : pid;

				// 是否是同一停车场
				if (pid.equals(entranceRecord.getCarParkId()))
				{
					
					//停车场信息
					carPark = (JSONObject) JSON.toJSON(carParksService.getCarParkById(entranceRecord.getCarParkId()));

					inTime = entranceRecord.getInTime();
					time = (System.currentTimeMillis() - inTime.getTime());
					parkTime = DateUtil.getTime(time);
					context.put("licensePlateNumber", search);
					context.put("inTime", inTime);
					context.put("imgName", entranceRecord.getImgName());
					context.put("parkTime", parkTime);
					context.put("carPark", carPark);
					context.put("recordId", entranceRecord.getRecordId());
					context.put("carParkId", entranceRecord.getCarParkId());
					context.put("mark", entranceRecord.getMark());
					context.put("oldRecordId", entranceRecord.getOldRecordId());
					context.put("carParkId", entranceRecord.getCarParkId());
					context.put("discountRate", entranceRecord.getDiscountRate());
					context.put("discountTime", entranceRecord.getDiscountTime());
					context.put("title", carPark.getString("title"));
					context.put("aliPartner", carPark.getString("aliPartner"));

					// 1、场内数据 3、场内和进出口都有。 只有在停车场类型是场的时候才会查询场内表，以减少redis 和 数据库的压力
					if (carPark.getInteger("dataType").equals(1) || carPark.getInteger("dataType").equals(3))
					{
						ParkingRecord t = new ParkingRecord();
						t.setCarParkId(null);
						t.setLicensePlateNumber(search);

						// 从redis里查询车牌
						record = JSON.parseObject(JedisPoolUtils.get("videoLicenseRecognize_" + search), ParkingRecord.class);
						if (record == null)
						{
							// 如果redis里无此车牌，则从数据库里在检索一遍，以确保数据不会被丢失
							record = (ParkingRecord) parkingRecordService.getLicenseRecognizePlateNumber(t);
						}
						
						if (record != null)
						{
							if (record.getCarParkId().equals(pid))
							{
								if (record.getMark() == 0)
								{
									if (record.getRecordId() != null && record.getParkingId() > 0)
									{
										// 获得区域对象
										Area area = areaService.findByNodeId(record.getParkingId(), record.getCarParkId());
										context.put("imgName", record.getImgName());
										context.put("area", area);
										context.put("parkingId", record.getParkingId());
									}
								}
							}
						}

					}

				} else
					context.put("error", "未找到车牌号");
			}

		} else
			context.put("error", "未找到车牌号");
		

		context.put("search", search);
		context.put("appId", appId);
		context.put("userId", userID);
		context.put("userPhoneId", request.getParameter("userPhoneId"));

		String imgName = "";
		Area area = (Area) context.get("area");
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.putAll(context);
		
		imgName = context.get("imgName") + "";
		if (!"".equals(context.get("imgName")) && null != context.get("imgName"))
		{
			if (imgName.indexOf("//") == -1)
				imgName = URLUtils.imgUrl + imgName;
			else
			{
//				//显示场内图片
//				if (null != area && !"".equals(area))
//					imgName = OSSUtil.getUrlSigned(OSSUtil.getParkingLotDir() + imgName, 600);
//				else//出入口图片
//					imgName = OSSUtil.getUrlSigned(imgName.replace("//", "/"), 600);
				
				//线下已上传路径
				imgName = OSSUtil.getUrlSigned(imgName.replace("//", "/"), 600);
			}
			
			map1.put("imgName", imgName);
		}
		
		
//		if (!"".equals(context.get("imgName")) && null != context.get("imgName"))
//		{
//			imgName = context.get("imgName") + "";
//
//			if (null != area && !"".equals(area))
//			{
//
//				if (imgName.indexOf("/") == -1)
//					imgName = OSSUtil.getUrlSigned(OSSUtil.getParkingLotDir() + imgName, 600);
//				else
//					imgName = URLUtils.imgUrl + imgName;
//				map1.put("area", area);
//			} else
//			{
//				if (imgName.indexOf("/") == -1)
//					imgName = OSSUtil.getUrlSigned(OSSUtil.getParkingEntranceDir() + imgName, 600);
//				else
//					imgName = URLUtils.imgUrl + imgName;
//			}
//			map1.put("imgName", imgName);
//		}

		map1.remove("areanode");
		map1.remove("record");
		map1.remove("entranceRecord");
		map1.remove("easyuiUrl");
		map1.remove("markitupUrl");
		map1.remove("msUrl");
		map1.remove("uploadUrl");
		map1.remove("webUrl");
		map1.remove("userId");
		
		
		LOG.error("查找车牌结果："+map1.toString());
		
		return URLEncoder.encode(JSON.toJSONString(map1, true), "utf-8");
	}

	/**
	 * 根据经纬度，查找distance范围内的停车场
	 * 
	 * @param latitude
	 * @param longitude
	 * @param distance
	 * @param offset
	 * @param limit
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/queryParkingList", method = RequestMethod.GET)
	@ResponseBody
	public void queryParkingList(double latitude, double longitude, double distance, int offset, int limit, HttpServletRequest request, HttpServletResponse response)
	{

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("latitude", latitude);
		map.put("longitude", longitude);
		map.put("distance", distance);
		map.put("offset", offset);
		map.put("limit", limit);

		List<Map<String, Object>> resultList = this.carParksService.findLatitudeAndLongitude(map);

		for (int i = 0; i < resultList.size(); i++)
		{
			resultList.get(i).put("parkname", "\"" + resultList.get(i).get("parkname") + "\"");
			resultList.get(i).put("address", "\"" + resultList.get(i).get("address") + "\"");
			int parkingEmptyNum = 0;
			if (resultList.get(i).get("parkingEmptyNum") != null && !"null".equals(resultList.get(i).get("parkingEmptyNum")) && resultList.get(i).get("parkingEmptyNum") != ""
					&& !"".equals(resultList.get(i).get("parkingEmptyNum")))
			{
				parkingEmptyNum = Integer.parseInt(resultList.get(i).get("parkingEmptyNum") + "");
			}
			if (parkingEmptyNum >= 50)
			{
				resultList.get(i).put("status", 1);
			} else if (parkingEmptyNum > 20 && parkingEmptyNum < 50)
			{
				resultList.get(i).put("status", 2);
			} else
			{
				resultList.get(i).put("status", 3);
			}

			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("carParkId", Integer.parseInt(resultList.get(i).get("parkid") + ""));

			// 算评分
			Map<String, Object> resultRating = this.carParksService.getCarParkScoreById(map1);
			int rating = 5;
			if (resultRating != null && resultRating.size() > 0)
			{
				rating = Integer.parseInt(resultRating.get("rating") + "");
				if (rating > 5)
				{
					rating = 5;
				}
			}
			resultList.get(i).put("star", rating);
			String portrait = "";
			if (resultList.get(i).get("picUrl") != null && !"null".equals(resultList.get(i).get("picUrl")) && resultList.get(i).get("picUrl") != ""
					&& !"".equals(resultList.get(i).get("picUrl")))
			{
				portrait = OSSUtil.getUrlSigned(OSSUtil.getParkImgDir() + resultList.get(i).get("picUrl"), 600);
			}
			resultList.get(i).put("picUrl", "\"" + resultList.get(i).get("picUrl") + "\"");
			resultList.get(i).put("imgurl", "\"" + portrait + "\"");
		}

		int count = this.carParksService.findLatitudeAndLongitudeCount(map);

		WriterUtil.writer(response, "{\"count\":" + count + ",\"errorcode\":0,\"data\":" + resultList + "}");
		// return ;
	}

	/**
	 * 根据carParkId查询停车场信息
	 * 
	 * @param carParkId
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/queryParkingInfo", method = RequestMethod.GET)
	@ResponseBody
	public void queryParkingInfo(int carParkId, HttpServletRequest request, HttpServletResponse response)
	{
		/*
		 * //先找缓存， String carParkInfo = JedisPoolUtils.hget("carParkInfo",
		 * carParkId+"");
		 * 
		 * if(!"".equals(carParkInfo) && null != carParkInfo)
		 * WriterUtil.writer(response, carParkInfo); else { Map<String, Object>
		 * map = this.carParksService.getCarParkById(carParkId);
		 * 
		 * if (map != null) { carParkInfo = JSON.toJSONString(map); //存入缓存
		 * JedisPoolUtils.hset("carParkInfo", carParkId+"", carParkInfo);
		 * WriterUtil.writer(response, carParkInfo); } else
		 * WriterUtil.writer(response, "{}"); }
		 */

		Map<String, Object> map = this.carParksService.getCarParkById(carParkId);
		if (map != null)
			WriterUtil.writer(response, JSON.toJSONString(map));
		else
			WriterUtil.writer(response, "{}");

	}

	/**
	 * 根据carParkId查询停车场信息
	 * 
	 * @param carParkId
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/findParkingInfo", method = RequestMethod.GET)
	@ResponseBody
	public void findParkingInfo(String carParkId, HttpServletRequest request, HttpServletResponse response)
	{

		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("carParkId", carParkId.split(","));
		List<Map<String, Object>> map = this.carParksService.findParkingInfo(map1);
		WriterUtil.writer(response, JSON.toJSONString(map));
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/queryCarParkId", method = RequestMethod.GET)
	@ResponseBody
	public void queryCarParkId(double latitude, double longitude, double distance, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("latitude", latitude);
		map.put("longitude", longitude);
		map.put("distance", distance);
		List<Map<String, Object>> list = this.carParksService.queryCarParkId(map);
		WriterUtil.writer(response, JSON.toJSONString(list));
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/findParkingEntranceRecordByList", method = RequestMethod.GET)
	@ResponseBody
	public void findParkingEntranceRecordList(String recordId, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("recordId", recordId);

		List<Map<String, Object>> list = this.carParksService.findParkingEntranceRecordList(m);

		WriterUtil.writer(response, JSON.toJSONString(list));
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/findParkingEntranceRecordByid", method = RequestMethod.GET)
	@ResponseBody
	public void findParkingEntranceRecordByid(Integer recordId, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("recordId", recordId);

		Map<String, Object> map = this.carParksService.findParkingEntranceRecordByid(m);

		WriterUtil.writer(response, JSON.toJSONString(map));
	}

	/**
	 * 查询评分
	 * 
	 * @param carParkId
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getCarParkScoreById", method = RequestMethod.GET)
	@ResponseBody
	public void getCarParkScoreById(int carParkId, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("carParkId", carParkId);

		Map<String, Object> resultRating = this.carParksService.getCarParkScoreById(map1);

		WriterUtil.writer(response, JSON.toJSONString(resultRating));
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getTest", method = RequestMethod.GET)
	@ResponseBody
	public void getTest(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			Thread.sleep(1000);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		WriterUtil.writer(response, JSON.toJSONString(new Date()));
	}

	/**
	 * 
	 * 更新停车场车位数
	 * 
	 * @param carParkId
	 * @param parkSumNum
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateCarParkTotalNum", method = RequestMethod.GET)
	@ResponseBody
	public void updateCarParkTotalNum(int carParkId, int parkSumNum, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("carParkId", carParkId);
		map1.put("parkSumNum", parkSumNum);

		int update = this.carParksService.updateCarParkTotalNum(map1);

		Map<String, Object> map = new HashMap<String, Object>();
		if (update == 1)
			map.put("errorcode", 0);
		else
			map.put("errorcode", 1);
		WriterUtil.writer(response, JSON.toJSONString(map));
	}

	/**
	 * 更新停车场空余车位数
	 * 
	 * @param carParkId
	 * @param parkIdleNum
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateCarParkEmptyNum", method = RequestMethod.GET)
	@ResponseBody
	public void updateCarParkEmptyNum(int carParkId, int parkIdleNum, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("carParkId", carParkId);
		map1.put("parkIdleNum", parkIdleNum);

		int update = this.carParksService.updateCarParkEmptyNum(map1);

		Map<String, Object> map = new HashMap<String, Object>();
		if (update == 1)
			map.put("errorcode", 0);
		else
			map.put("errorcode", 1);

		WriterUtil.writer(response, JSON.toJSONString(map));
	}

	/**
	 * 更新停车场车位数
	 * 
	 * @param carParkId
	 * @param parkSumNum
	 *            总车位
	 * @param parkIdleNum
	 *            空余车位
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateCarParkTotalNumAndEmptyNum", method = RequestMethod.GET)
	public void updateCarParkTotalNumAndEmptyNum(int carParkId, int parkSumNum, int parkIdleNum, HttpServletRequest request, HttpServletResponse response)
	{

		String result = this.carParksService.updateCarParkTotalNumAndEmptyNum(carParkId, parkSumNum, parkIdleNum);

		WriterUtil.writer(response, result);
	}

	/**
	 * 更新停车场空余车位数 当前车位-subtractNum
	 * 
	 * @param carParkId
	 * @param parkIdleNum
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateCarParkEmptyNumSubtract", method = RequestMethod.GET)
	@ResponseBody
	public void updateCarParkEmptyNumSubtract(int carParkId, int subtractNum, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("carParkId", carParkId);
		map1.put("subtractNum", subtractNum);

		int update = this.carParksService.updateCarParkEmptyNumSubtract(map1);

		Map<String, Object> map = new HashMap<String, Object>();
		if (update == 1)
			map.put("errorcode", 0);
		else
			map.put("errorcode", 1);

		WriterUtil.writer(response, JSON.toJSONString(map));
	}

	/**
	 * 更新停车场空余车位数 当前车位-subtractNum
	 * 
	 * @param carParkId
	 * @param parkIdleNum
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateCarParkEmptyNumAdd", method = RequestMethod.GET)
	@ResponseBody
	public void updateCarParkEmptyNumAdd(int carParkId, int addNum, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("carParkId", carParkId);
		map1.put("addNum", addNum);

		int update = this.carParksService.updateCarParkEmptyNumAdd(map1);

		Map<String, Object> map = new HashMap<String, Object>();
		if (update == 1)
			map.put("errorcode", 0);
		else
			map.put("errorcode", 1);

		WriterUtil.writer(response, JSON.toJSONString(map));
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/setLoginInfo", method = RequestMethod.GET)
	public void setLoginInfo(String loginName, String loginPass, String machineCode, HttpServletRequest request, HttpServletResponse response)
	{
		String key = null;
		JSONObject json = new JSONObject();

		String result = "{\"errorcode\":1}";
		try
		{
			/* 内存登录信息校验 */
			CarParks carPark = carParksService.findByWSLoginInfo(loginName, loginPass, machineCode);
			if (carPark != null)
			{
				key = DateUtil.getNowTimeString() + StringUtil.getConfuseString(20);

				// JSONObject carParkData = new JSONObject();
				// carParkData.put("recordId",
				// Integer.parseInt(carPark.getRecordid()));
				// carParkData.put("groupId", carPark.getGroupId());
				// carParkData.put("mallId", carPark.getMallId());
				// carParkData.put("companyName", carPark.getCompanyName());
				// carParkData.put("carParkName", carPark.getCarParkName());
				// carParkData.put("loginName", carPark.getLoginName());
				// carParkData.put("loginPass", carPark.getLoginPass());
				// carParkData.put("machineCode", carPark.getMachineCode());
				// carParkData.put("parkingTotalNum",
				// carPark.getParkingTotalNum());
				// carParkData.put("parkingEmptyNum",
				// carPark.getParkingEmptyNum());
				// carParkData.put("parkLocation", carPark.getParkLocation());

				json.put("carPark", carPark);
				json.put("errorcode", 0);
			} else
				json.put("errorcode", 1);

			json.put("result", key);

			result = URLEncoder.encode(json.toJSONString(), "UTF-8");

		} catch (Exception e)
		{
			e.printStackTrace();
			json.put("result", "");
		}

		WriterUtil.writer(response, result);

	}

	/**
	 * 根据登录名 获取KEY
	 * 
	 * @param loginName
	 *            登录名
	 * @return KEY
	 */
	private String getFromMapByLoginName(String loginName)
	{
		String exsitKey = null;
		if (loginName == null)
			return exsitKey;

		Iterator<Entry<String, CarParks>> iter = loginSessionMap.entrySet().iterator();
		while (iter.hasNext())
		{
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			CarParks val = (CarParks) entry.getValue();
			if (loginName.equals(val.getLoginName()))
			{
				exsitKey = key;
				break;
			}
		}
		return exsitKey;
	}

	/**
	 * 更新停车场空余车位数
	 * 
	 * @param carParkId
	 * @param parkIdleNum
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/findCarParkInfoName", method = RequestMethod.GET)
	@ResponseBody
	public void findCarParkInfoName(String carParkName, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map1 = new HashMap<String, Object>();

		if (!StringUtils.isEmpty(carParkName))
		{
			try
			{
				carParkName = new String(carParkName.getBytes("iso8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		map1.put("carParkName", carParkName);

		List<Map<String, Object>> map = this.carParksService.findCarParkInfoName(map1);

		WriterUtil.writer(response, JSON.toJSONString(map));
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/saveOrUpdateCarParksScore", method = RequestMethod.POST)
	public void saveOrUpdateCarParksScore(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map = JSON.parseObject(requestBody, Map.class);

		int rownum = carParksService.saveGdCarparkEvaluate(map);

		int errorcode = 0;
		Map<String, Object> map2 = carParksService.saveOrUpdateCarParksScoreCountAll(map);
		if (map2 != null)
		{
			int updrow = carParksService.saveOrUpdateCarParksScoreUpdate(map);
			if (updrow > 0)
			{
				errorcode = 0;
			} else
			{
				errorcode = 1;
			}
		} else
		{

			int updrow = carParksService.saveOrUpdateCarParksScoreSave(map);

			// CarParksScore cps = new CarParksScore();
			// cps.setCarParkId(id);
			// cps.setFraction(fraction);
			// cps.setFrequency(1);
			// cps.setCreateTime(new Date());
			// int saveres = baseDao.save(cps);
			if (updrow != 0)
			{
				errorcode = 0;
			} else
			{
				errorcode = 1;
			}
		}

		Map<String, Object> map1 = new HashMap<String, Object>();

		map1.put("errorcode", errorcode);
		WriterUtil.writer(response, JSON.toJSONString(map1));
	}

	/**
	 * 
	 * 闸门信息 JSON格式
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateCarParksGateInfo", method = RequestMethod.POST)
	public void updateCarParksGateInfo(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map = JSON.parseObject(requestBody, Map.class);
		map.put("gateInfo", map.get("gateInfo").toString());

		int updrow = carParksService.updateCarParksGateInfo(map);

		Map<String, Object> map1 = new HashMap<String, Object>();
		if (updrow == 1)
			map1.put("errorcode", 0);
		else
			map1.put("errorcode", 1);

		WriterUtil.writer(response, JSON.toJSONString(map1));
	}

	/**
	 * 
	 * 添加停车场信息
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/saveCarPark", method = RequestMethod.POST)
	public void saveCarPark(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		WriterUtil.writer(response, JSON.toJSONString(carParksService.saveCarPark(requestBody)));
	}

	/**
	 * 是否加价，
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateShareMode", method = RequestMethod.GET)
	public void updateShareMode(Integer carParkId, Integer shareMode, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("carParkId", carParkId);
		map.put("shareMode", shareMode);

		int updrow = carParksService.updateShareMode(map);

		Map<String, Object> map1 = new HashMap<String, Object>();

		if (updrow == 1)
			map1.put("errorcode", 0);
		else
			map1.put("errorcode", 1);

		WriterUtil.writer(response, JSON.toJSONString(map1));
	}

	/**
	 * 林锋
	 * 
	 * 3 获取停车场缴费规则 参数: 停车场Id parkId 结果:comment
	 * 
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/findCarParkRule", method = RequestMethod.POST)
	public void findCarParkRule(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{

		JSONObject jsonData = JSON.parseObject(requestBody);

		Map<String, Object> map = carParksService.findCarParkRule(jsonData);

		if (map == null)
		{
			map = new HashMap<String, Object>();
			map.put("error", "没有规则描述");
		}

		WriterUtil.writer(response, JSON.toJSONString(map));
	}

	/**
	 * 林锋
	 * 
	 * 2 获取空车位数 参数: 停车场Id parkId 结果: GbCarParks GdArea 数据
	 * 
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/findCarParkEmptyNumber", method = RequestMethod.POST)
	public void findCarParkEmptyNumber(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{

		JSONObject jsonData = JSON.parseObject(requestBody);

		Map<String, Object> map = this.carParksService.getCarParkById(jsonData.getInteger("parkId"));

		List<Map<String, Object>> area = this.carParksService.findArea(jsonData);

		JSONObject resultData = new JSONObject();

		resultData.put("carPark", map);
		resultData.put("area", area);
		resultData.put("areaCount", area.size());
		if (map == null)
			resultData.put("carParkCount", 0);
		else
			resultData.put("carParkCount", 1);

		WriterUtil.writer(response, resultData.toJSONString());
	}

	/**
	 * 
	 * 添加销售员、停车场信息
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/saveSalesmanCarPark", method = RequestMethod.POST)
	public void saveSalesmanCarPark(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{

		WriterUtil.writer(response, JSON.toJSONString(carParksService.saveSalesmanCarPark(requestBody)));
	}

	/**
	 * 停车场 报表
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/carParkReport", method = RequestMethod.GET)
	public void carParkReport(Integer province, Integer mycity, Integer dist, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("province", province);
		map.put("mycity", mycity);
		map.put("dist", dist);

		map = carParksService.carParkReport(map);

		JSONObject data = new JSONObject();

		data.put("sumCount", map.get("sumCount"));
		data.put("notVideoNotBlue", map.get("notVideoNotBlue"));
		data.put("videoBlue", map.get("videoBlue"));
		data.put("blueNotVideo", map.get("blueNotVideo"));
		data.put("notBlueVideo", map.get("notBlueVideo"));
		data.put(
				"newNotVideoNotBlue",
				new Object[] { map.get("notVideoNotBlue1"), map.get("notVideoNotBlue2"), map.get("notVideoNotBlue3"), map.get("notVideoNotBlue4"), map.get("notVideoNotBlue5"),
						map.get("notVideoNotBlue6"), map.get("notVideoNotBlue7") });
		data.put(
				"newVideoBlue",
				new Object[] { map.get("videoBlue1"), map.get("videoBlue2"), map.get("videoBlue3"), map.get("videoBlue4"), map.get("videoBlue5"), map.get("videoBlue6"),
						map.get("videoBlue7") });
		data.put("newBlueNotVideo", new Object[] { map.get("blueNotVideo1"), map.get("blueNotVideo2"), map.get("blueNotVideo3"), map.get("blueNotVideo4"),
				map.get("blueNotVideo5"), map.get("blueNotVideo6"), map.get("blueNotVideo7") });
		data.put("newNotBlueVideo", new Object[] { map.get("notBlueVideo1"), map.get("notBlueVideo2"), map.get("notBlueVideo3"), map.get("notBlueVideo4"),
				map.get("notBlueVideo5"), map.get("notBlueVideo6"), map.get("notBlueVideo7") });

		WriterUtil.writer(response, data.toJSONString());
	}

	/**
	 * 添加停车场开放段以及车位上限
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/saveDisparkTime", method = RequestMethod.POST)
	public void saveDisparkTime(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{

		WriterUtil.writer(response, carParksService.saveDisparkTime(requestBody));
	}

}
