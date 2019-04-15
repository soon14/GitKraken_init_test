package cn.rf.hz.web.action.gd;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.bean.gd.CarParks;
import cn.rf.hz.web.bean.gd.OrderForm;
import cn.rf.hz.web.service.gd.CarOrdersService;
import cn.rf.hz.web.utils.OSSUtil;
import cn.rf.hz.web.utils.URLUtils;
import cn.rf.hz.web.utils.WriterUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
@Controller
@RequestMapping("/ordersForm")
public class CarOrdersAction 
{
	private final static Logger LOG = Logger.getLogger(CarOrdersAction.class);
	@Autowired(required = false)
	private CarOrdersService<CarParks> carOrdersService;

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateQrCodeVerification", method = RequestMethod.GET)
	@ResponseBody
	public void updateQrCodeVerification(String staffId, String userPId, HttpServletRequest request, HttpServletResponse response)
	{

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("staffId", staffId);
		map.put("userPId", userPId);

		int updrow = this.carOrdersService.update1(map);

		this.carOrdersService.update2(map);
		if (updrow > 0)
		{
			WriterUtil.writer(response, "{\"errorcode\":0}");
		} else
		{
			WriterUtil.writer(response, "{\"errorcode\":4}");
		}
	}


	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/queryOrderFormWhether", method = RequestMethod.GET)
	@ResponseBody
	public void queryOrderFormWhether(String userPhoneId, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userPhoneId", userPhoneId);
		int rowcount = this.carOrdersService.queryOrderFormWhether(map);

		if (rowcount > 0)
			WriterUtil.writer(response, "{\"errorcode\":0}");
		else
			WriterUtil.writer(response, "{\"errorcode\":1}");
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateStopParking", method = RequestMethod.GET)
	@ResponseBody
	public void updateStopParking(String userPhoneId, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userPhoneId", userPhoneId);
		int rowcount = this.carOrdersService.updateStopParking(map);

		if (rowcount > 0)
			WriterUtil.writer(response, "{\"errorcode\":0}");
		else
			WriterUtil.writer(response, "{\"errorcode\":1}");
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateStopParkingBox", method = RequestMethod.GET)
	@ResponseBody
	public void updateStopParkingBox(String userPhoneId, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userPhoneId", userPhoneId);
		int rowcount = this.carOrdersService.updateStopParkingBox(map);

		if (rowcount > 0)
			WriterUtil.writer(response, "{\"errorcode\":0}");
		else
			WriterUtil.writer(response, "{\"errorcode\":1}");
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateCancelParkingSpace", method = RequestMethod.GET)
	@ResponseBody
	public void updateCancelParkingSpace(String userPhoneId, String reason, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userPhoneId", userPhoneId);
		map.put("reason", reason);
		int rowcount = this.carOrdersService.updateCancelParkingSpace(map);

		if (rowcount > 0)
		{
			Map<String, Object> map1 = this.carOrdersService.findCancelParkingSpace(map);
			map1.put("errorcode", 0);
			WriterUtil.writer(response, JSON.toJSONString(map1));
		} else
			WriterUtil.writer(response, "{\"errorcode\":1}");
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateCancelParkingSpaceBox", method = RequestMethod.GET)
	@ResponseBody
	public void updateCancelParkingSpaceBox(String userPhoneId, String reason,String orderFormNo, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userPhoneId", userPhoneId);
		map.put("reason", reason);
		map.put("orderFormNo",orderFormNo);
		int rowcount = this.carOrdersService.updateCancelParkingSpaceBox(map);

		if (rowcount > 0)
		{
			Map<String, Object> map1 = this.carOrdersService.findCancelParkingSpace(map);
			map1.put("errorcode", 0);
			WriterUtil.writer(response, JSON.toJSONString(map1));
		} else
			WriterUtil.writer(response, "{\"errorcode\":1}");
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/admissionScanCode", method = RequestMethod.GET)
	@ResponseBody
	public void admissionScanCode(String userPId, String staffId, String licensePlateNumber, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userPId", userPId);
		map.put("staffId", staffId);
		try
		{
			map.put("licensePlateNumber", new String(licensePlateNumber.getBytes("iso-8859-1"), "UTF-8"));
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

		int count1 = this.carOrdersService.admissionScanCodeCount1(map);
		if (count1 <= 0)
		{
			WriterUtil.writer(response, "{\"errorcode\":" + count1 + "}");
		} else
			WriterUtil.writer(response, "{\"errorcode\":4}");
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/admissionScanCodeSave", method = RequestMethod.GET)
	@ResponseBody
	public void admissionScanCodeSave(String orderFormNo, String userPhoneId, String staffId, String phone, String licensePlateNumber, Integer carParkId,
			HttpServletRequest request, HttpServletResponse response)
	{
		OrderForm orderForm = new OrderForm();
		orderForm.setOrderFormNo(orderFormNo);
		orderForm.setUserPhoneId(userPhoneId);
		orderForm.setStaffId(staffId);
		orderForm.setAreFare(0);
		orderForm.setPhone(phone);
		try
		{
			orderForm.setLicensePlateNumber(new String(licensePlateNumber.getBytes("iso-8859-1"), "UTF-8"));
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		orderForm.setOrderAmount(0);
		orderForm.setAcquiringPerson(staffId);
		orderForm.setCarParkId(carParkId);
		int rowcount = this.carOrdersService.admissionScanCodeSave(orderForm);
		if (rowcount > 0)
			WriterUtil.writer(response, "{\"errorcode\":0}");
		else
			WriterUtil.writer(response, "{\"errorcode\":1}");
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/queryOrderByUserPhoneId", method = RequestMethod.GET)
	@ResponseBody
	public void queryOrderByUserPhoneId(String userPhoneId, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userPhoneId", userPhoneId);
		Map<String, Object> resultList = this.carOrdersService.queryOrderByUserPhoneId(map);

		WriterUtil.writer(response, JSON.toJSONString(resultList));
	}

	/**
	 * 已找到车位，未离场， 不能在找车位
	 * 
	 * @param userPhoneId
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/queryIsItEffective", method = RequestMethod.GET)
	public void queryIsItEffective(String userPhoneId, HttpServletRequest request, HttpServletResponse response)
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userPhoneId", userPhoneId);
		Map<String, Object> resultMap = this.carOrdersService.queryIsItEffective(map);

		Map<String, Object> map1 = new HashMap<String, Object>();
		if (resultMap != null && resultMap.size() > 0)
		{
			String hourStr = "";
			int status=Integer.parseInt(resultMap.get("status")+"");
			try
			{
				hourStr = dateSubtract(df.parse(resultMap.get("changeOrderTime") + ""), new Date());
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			if(status==1||status==0){
			int hour = Integer.parseInt(hourStr.substring(0, hourStr.indexOf("小")));
			if (hour >= 1)
			{
				map1.put("errorcode", 1);

			} else
			{
				if (resultMap.get("imageName") != null)
				{
					resultMap.put("imgurl", OSSUtil.getUrlSigned(OSSUtil.getAppSecurityStaffDir() + resultMap.get("imageName"), 600));
				}
				map1.put("errorcode", 0);
				map1.putAll(resultMap);
			}
			}else{
				if (resultMap.get("imageName") != null)
				{
					resultMap.put("imgurl", OSSUtil.getUrlSigned(OSSUtil.getAppSecurityStaffDir() + resultMap.get("imageName"), 600));
				}
				map1.put("errorcode", 0);
				map1.putAll(resultMap);
			}
		} else
		{
			map1.put("errorcode", 1);
		}

		WriterUtil.writer(response, JSON.toJSONString(map1));
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/queryEarnings", method = RequestMethod.GET)
	public void queryEarnings(String staffId, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map1 = new HashMap<String, Object>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("staffId", staffId);
		Integer dayMap = this.carOrdersService.queryEarningsDayMap(map);// 收费
		if (dayMap != null)
		{
			map1.put("dayToll", (double) dayMap / 100);
		} else
		{
			map1.put("dayToll", 0);
		}
		// 当前员工本日接单的收益
		Integer earningsJdMap = this.carOrdersService.queryEarningsEarningsJdMap(map);
		if (earningsJdMap == null)
		{
			earningsJdMap = 0;
		}
		// 当前员工本日收单的收益
		Integer earningsSdMap = this.carOrdersService.queryEarningsEarningsSdMap(map);
		if (earningsSdMap == null)
		{
			earningsSdMap = 0;
		}
		map1.put("dayBenefits", (earningsJdMap + earningsSdMap));

		Integer monthMap = this.carOrdersService.queryEarningsMonthMap(map);// 当前员工本月的收益和收费
		if (monthMap != null)
		{
			map1.put("monthToll", (double) monthMap / 100);
		} else
		{
			map1.put("monthToll", 0);
		}
		// 当前员工本日接单的收益
		Integer earningsJdMapmon = this.carOrdersService.queryEarningsEarningsJdMapmon(map);
		if (earningsJdMapmon == null)
		{
			earningsJdMapmon = 0;
		}
		// 当前员工本日收单的收益
		Integer earningsSdMapmon = this.carOrdersService.queryEarningsEarningsSdMapmon(map);
		if (earningsSdMapmon == null)
		{
			earningsSdMapmon = 0;
		}
		map1.put("monthBenefits", (earningsJdMapmon + earningsSdMapmon));

		// List<Map<String, Object>> yearList =
		// this.carOrdersService.queryEarningsYearList(map);// 当前员工本年最近6个月的收费
		// 当前员工本年最近6个月的收益
		List<Map<String, Object>> yearListjssy = this.carOrdersService.queryEarningsYearListjssy(map);

		// 当前员工本年最近6个月的收益
		// List<Map<String, Object>> yearListsdsy =
		// this.carOrdersService.queryEarningsYearListsdsy(map);

		for (int i = 0; i < yearListjssy.size() - 6; i++)
		{
			yearListjssy.remove(i);
		}
		for (int i = 0; i < yearListjssy.size(); i++)
		{
			map.put("months", yearListjssy.get(i).get("months"));
			// 根据yearListjssy.get(i).get("yufen")月份查询收单 如果有
			Map<String, Object> yearListsdsy = this.carOrdersService.queryEarningsYearListsdsy(map);
			if (yearListsdsy != null && yearListsdsy.size() > 0)
			{
				if (yearListsdsy.get("orderFormId") != null)
				{
					yearListjssy.get(i).put("orderFormId", Integer.parseInt(yearListjssy.get(i).get("orderFormId") + "") + Integer.parseInt(yearListsdsy.get("orderFormId") + ""));
				}
			}
			// 根据月份查询收单 如果有
			Map<String, Object> yearList = this.carOrdersService.queryEarningsYearList(map);// 当前员工本年最近6个月的收费
			if (yearList != null && yearList.size() > 0)
			{
				if (yearList.get("orderAmount") != null)
				{
					yearListjssy.get(i).put("orderAmount", Double.parseDouble(yearList.get("orderAmount").toString()) / 100);
				} else
				{
					yearListjssy.get(i).put("orderAmount", 0);
				}
			} else
			{
				yearListjssy.get(i).put("orderAmount", 0);
			}
		}
		for (int i = 0; i < yearListjssy.size(); i++)
		{
			yearListjssy.get(i).put("months", yearListjssy.get(i).get("months") + "月");
		}
		// return "{\"dayBenefits\":" + dayBenefits + ",\"dayToll\":" + dayToll
		// + ",\"monthBenefits\":" + monthBenefits + ",\"monthToll\":" +
		// monthToll + ",\"errorcode\":0,\"data\":"
		// + yearList + "}";

		map1.put("errorcode", 0);
		map1.put("data", yearListjssy);

		WriterUtil.writer(response, JSON.toJSONString(map1));
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateCarParks", method = RequestMethod.GET)
	public void updateCarParks(int carParkId, int ibit, HttpServletRequest request, HttpServletResponse response)
	{
		int errorcode = 0;

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("carParkId", carParkId);
		map.put("parkingEmptyNum", ibit);

		int updrows = carOrdersService.updateCarParks(map);
		if (updrows == 0)
		{
			WriterUtil.writer(response, "{\"errorcode\":1}");
		} else
		{
			WriterUtil.writer(response, "{\"errorcode\":0}");
		}
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateScanCodeAppearances", method = RequestMethod.GET)
	public void updateScanCodeAppearances(String userPId, String carParkId, String playingPeople, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userPId", userPId);
		map.put("carParkId", carParkId);
		map.put("playingPeople", playingPeople);

		int count1 = carOrdersService.updateScanCodeAppearancesCount1(map);
		if (count1 > 0)
		{
			int updrow = carOrdersService.updateScanCodeAppearancesUpdrow(map);
			if (updrow > 0)
			{
				Map<String, Object> resultMap = carOrdersService.updateScanCodeAppearancesResultMap(map);
				if (resultMap != null && resultMap.size() > 0)
				{
					String uuid = resultMap.get("uuid") + "";
					String licensePlateNumber = resultMap.get("licensePlateNumber") + "";
					WriterUtil.writer(response, "{\"errorcode\":0,\"uuid\":\"" + uuid + "\",\"licensePlateNumber\":\"" + licensePlateNumber + "\"}");

				} else
				{
					WriterUtil.writer(response, "{\"errorcode\":1}");
				}
			} else
			{
				WriterUtil.writer(response, "{\"errorcode\":1}");
			}
		} else
		{
			WriterUtil.writer(response, "{\"errorcode\":2}");
		}
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/saveOrderForm", method = RequestMethod.GET)
	public void saveOrderForm(String orderFormNo, String uuid, String phone, String licensePlateNumber, Integer carParkId, int areFare, HttpServletRequest request,
			HttpServletResponse response)
	{
		OrderForm orderForm = new OrderForm();
		orderForm.setOrderFormNo(generatedOrderNumber());
		orderForm.setUuid(uuid);
		orderForm.setAreFare(areFare);
		orderForm.setPhone(phone);
		try
		{
			orderForm.setLicensePlateNumber(new String(licensePlateNumber.getBytes("iso-8859-1"), "UTF-8"));

		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		orderForm.setOrderAmount(0);

		int rowcount = this.carOrdersService.saveOrderForm(orderForm);

		if (rowcount > 0)
			WriterUtil.writer(response, "{\"errorcode\":0}");
		else
			WriterUtil.writer(response, "{\"errorcode\":1}");
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateOrderForm1", method = RequestMethod.GET)
	public void updateOrderForm1(String orderFormNo, String staffId, int carParkId, HttpServletRequest request, HttpServletResponse response)
	{
		int errorcode = 0;

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("staffId", staffId);
		map.put("carParkId", carParkId);
		map.put("orderFormNo", orderFormNo);

		int rowcount = carOrdersService.updateOrderFormRowcount(map);
		if (rowcount == 0)
		{
			WriterUtil.writer(response, "{\"errorcode\":1}");
		} else
		{

			Map<String, Object> resultMap = carOrdersService.updateOrderFormResultMap(map);
			resultMap.put("portrait", OSSUtil.getUrlSigned(OSSUtil.getAppSecurityStaffDir() + resultMap.get("imageName"), 600));
			resultMap.put("errorcode", 0);
			WriterUtil.writer(response, JSON.toJSONString(resultMap));
		}
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/deleteOrderForm", method = RequestMethod.GET)
	public void deleteOrderForm(int orderFormId, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderFormId", orderFormId);
		int rowcount = carOrdersService.deleteOrderFormRowcount(map);
		if (rowcount == 0)
		{
			WriterUtil.writer(response, "{\"errorcode\":1}");
		} else
		{
			WriterUtil.writer(response, "{\"errorcode\":0}");
		}
	}

	/**
	 * 已缴费订单记录
	 * 
	 * @param orderFormId
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/queryOrderForm1", method = RequestMethod.GET)
	public void queryOrderForm1(String orderFormId, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderFormId", orderFormId);
		List<Map<String, Object>> resultList = carOrdersService.queryOrderFormResultList(map);

		WriterUtil.writer(response, JSON.toJSONString(resultList));
	}

	/**
	 * 已缴费订单记录
	 * 
	 * @param orderFormId
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/queryOrderForm1Box", method = RequestMethod.GET)
	public void queryOrderForm1Box(String orderFormId, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderFormId", orderFormId);
		List<Map<String, Object>> resultList = carOrdersService.queryOrderFormResultListBox(map);

		WriterUtil.writer(response, JSON.toJSONString(resultList));
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/queryOrderForm2", method = RequestMethod.GET)
	public void queryOrderForm2(String parkRecordID, String carParkId, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("parkRecordID", parkRecordID);
		map.put("carParkId", carParkId);
		List<Map<String, Object>> resultList = carOrdersService.queryOrderFormResultList2(map);

		WriterUtil.writer(response, JSON.toJSONString(resultList));
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/queryOrderForm3", method = RequestMethod.GET)
	public void queryOrderForm3(String userPhoneId, String parkRecordID, String carParkId, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("parkRecordID", parkRecordID);
		map.put("carParkId", carParkId);
		map.put("userPhoneId", userPhoneId);

		int count = carOrdersService.queryOrderFormResultList3(map);
		int count1 = carOrdersService.queryOrderFormResultList4(map);

		WriterUtil.writer(response, "{\"count\":" + count + count1 + "}");
	}

	/**
	 * 订单查询
	 * 
	 * @param staffId
	 * @param type
	 * @param offset
	 * @param limit
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/queryOrderForm4", method = RequestMethod.GET)
	public void queryOrderForm4(String staffId, int type, int offset, int limit, HttpServletRequest request, HttpServletResponse response)
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("staffId", staffId);
		map.put("type", type);
		map.put("offset", offset);
		map.put("limit", limit);

		int count = 0;

		List<Map<String, Object>> resultList = null;
		if (type == 0)// 未接单的订单
		{
			resultList = carOrdersService.queryOrderForm4ResultList1(map);

			count = carOrdersService.queryOrderForm4ResuCount1(map);
		} else if (type == 1)// 未入场，已接单
		{
			resultList = carOrdersService.queryOrderForm4ResultList2(map);
			count = carOrdersService.queryOrderForm4ResuCount2(map);
		} else if (type == 2)// 已入场
		{
			resultList = carOrdersService.queryOrderForm4ResultList3(map);
			count = carOrdersService.queryOrderForm4ResuCount3(map);
		} else
		// 已离场
		{
			resultList = carOrdersService.queryOrderForm4ResultList4(map);
			count = carOrdersService.queryOrderForm4ResuCount4(map);
		}

		for (int i = 0; i < resultList.size(); i++)
		{
			resultList.get(i).put("orderFormNo", resultList.get(i).get("orderFormNo"));
			resultList.get(i).put("licensePlateNumber", resultList.get(i).get("licensePlateNumber"));
			resultList.get(i).put("phone", resultList.get(i).get("phone"));

			if (type == 0)
			{
				DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String orderGenerationTime = resultList.get(i).get("orderGenerationTime") + "";
				try
				{
					String parkingDuration = dateSubtract(df1.parse(orderGenerationTime), new Date());
					resultList.get(i).put("interval", parkingDuration);
					resultList.get(i).put("orderGenerationTime", df1.format(df1.parse(orderGenerationTime)));
				} catch (Exception e)
				{
					e.printStackTrace();
				}

				double latitude = 120.068234;// Double.parseDouble(JedisPoolUtils.get("appW"
												// +
												// resultList.get(i).get("uuid")));
				resultList.get(i).put("latitude", latitude);
				double longitude = 30.280138;// Double.parseDouble(JedisPoolUtils.get("appJ"
												// +
												// resultList.get(i).get("uuid")));
				resultList.get(i).put("longitude", longitude);

			}
			if (type == 1)
			{
				DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				try
				{
					resultList.get(i).put("changeOrderTime", df1.format(df1.parse(resultList.get(i).get("changeOrderTime") + "")));
					String changeOrderTime = resultList.get(i).get("changeOrderTime") + "";
					resultList.get(i).put("changeOrderTime", df1.format(df1.parse(changeOrderTime)));
				} catch (ParseException e)
				{
					e.printStackTrace();
				}

			}
			if (type == 2)
			{

				String changeOrderTime = resultList.get(i).get("changeOrderTime") + "";
				try
				{
					String parkingDuration = dateSubtract(df.parse(changeOrderTime), new Date());
					resultList.get(i).put("interval", parkingDuration);
					resultList.get(i).put("type", 1);
					resultList.get(i).put("acquiringTime", df.format(df.parse(resultList.get(i).get("acquiringTime") + "")));
					resultList.get(i).put("changeOrderTime", df.format(df.parse(resultList.get(i).get("changeOrderTime") + "")));
					resultList.get(i).put("playingTime", df.format(new Date()));
				} catch (Exception e)
				{
					e.printStackTrace();
				}

			}
			if (type == 3)
			{
				DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String changeOrderTime = resultList.get(i).get("changeOrderTime") + "";

				String acquiringTime = resultList.get(i).get("acquiringTime") + "";
				String playingTime = resultList.get(i).get("playingTime") + "";

				try
				{
					resultList.get(i).put("changeOrderTime", df1.format(df1.parse(changeOrderTime)));
					resultList.get(i).put("playingTime", df1.format(df1.parse(playingTime)));
					resultList.get(i).put("acquiringTime", df1.format(df1.parse(acquiringTime)));
					String parkingDuration = dateSubtract(df1.parse(acquiringTime), df1.parse(playingTime));
					resultList.get(i).put("interval", parkingDuration);
				} catch (Exception e)
				{
					e.printStackTrace();
				}

				int orderAmount = Integer.parseInt(resultList.get(i).get("orderAmount") + "");
				resultList.get(i).put("orderAmount", ((float) orderAmount / 100));

			}
		}
		WriterUtil.writer(response, "{\"errorcode\":0,\"count\":" + count + ",\"type\":" + type + ",\"data\":" + JSON.toJSONString(resultList) + "}");
	}

	/**
	 * 订单查询
	 * 
	 * @param staffId
	 * @param type
	 * @param offset
	 * @param limit
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/queryOrderForm4Box", method = RequestMethod.GET)
	public void queryOrderForm4Box(String staffId, int type, int offset, int limit, int carParkId, HttpServletRequest request, HttpServletResponse response)
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("staffId", staffId);
		map.put("type", type);
		map.put("offset", offset);
		map.put("limit", limit);
		map.put("carParkId", carParkId);
		int count = 0;

		List<Map<String, Object>> resultList = null;
		if (type == 0)// 未接单的订单
		{
			resultList = carOrdersService.queryOrderForm4ResultList1Box(map);

			count = carOrdersService.queryOrderForm4ResuCount1Box(map);
		} else if (type == 1)// 未入场，已接单
		{
			resultList = carOrdersService.queryOrderForm4ResultList2Box(map);
			count = carOrdersService.queryOrderForm4ResuCount2Box(map);
		} else if (type == 2)// 已入场
		{
			resultList = carOrdersService.queryOrderForm4ResultList3Box(map);
			count = carOrdersService.queryOrderForm4ResuCount3Box(map);
		} else
		// 已离场
		{
			resultList = carOrdersService.queryOrderForm4ResultList4Box(map);
			count = carOrdersService.queryOrderForm4ResuCount4Box(map);
		}

		for (int i = 0; i < resultList.size(); i++)
		{
			resultList.get(i).put("orderFormNo", resultList.get(i).get("orderFormNo"));
			resultList.get(i).put("licensePlateNumber", resultList.get(i).get("licensePlateNumber"));
			resultList.get(i).put("phone", resultList.get(i).get("phone"));

			if (type == 0)
			{
				DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String orderGenerationTime = resultList.get(i).get("orderGenerationTime") + "";
				try
				{
					String parkingDuration = dateSubtract(df1.parse(orderGenerationTime), new Date());
					resultList.get(i).put("interval", parkingDuration);
					resultList.get(i).put("orderGenerationTime", df1.format(df1.parse(orderGenerationTime)));
				} catch (Exception e)
				{
					e.printStackTrace();
				}

//				double latitude = 120.068234;// Double.parseDouble(JedisPoolUtils.get("appW"
//												// +
//												// resultList.get(i).get("uuid")));
//				resultList.get(i).put("latitude", latitude);
//				double longitude = 30.280138;// Double.parseDouble(JedisPoolUtils.get("appJ"
//												// +
//												// resultList.get(i).get("uuid")));
//				resultList.get(i).put("longitude", longitude);
			}
			if (type == 1)
			{
				DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				try
				{
					resultList.get(i).put("changeOrderTime", df1.format(df1.parse(resultList.get(i).get("changeOrderTime") + "")));
					String changeOrderTime = resultList.get(i).get("changeOrderTime") + "";
					resultList.get(i).put("changeOrderTime", df1.format(df1.parse(changeOrderTime)));
				} catch (ParseException e)
				{
					e.printStackTrace();
				}
			}
			if (type == 2)
			{
				String changeOrderTime = resultList.get(i).get("changeOrderTime") + "";
				try
				{
					String parkingDuration = dateSubtract(df.parse(changeOrderTime), new Date());
					resultList.get(i).put("interval", parkingDuration);
					resultList.get(i).put("type", 1);
					resultList.get(i).put("acquiringTime", df.format(df.parse(resultList.get(i).get("acquiringTime") + "")));
					resultList.get(i).put("changeOrderTime", df.format(df.parse(resultList.get(i).get("changeOrderTime") + "")));
					resultList.get(i).put("playingTime", df.format(new Date()));
				} catch (Exception e)
				{
					e.printStackTrace();
				}

			}
			if (type == 3)
			{
				DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String changeOrderTime = resultList.get(i).get("changeOrderTime") + "";

				String acquiringTime = resultList.get(i).get("acquiringTime") + "";
				String playingTime = resultList.get(i).get("playingTime") + "";

				try
				{
					resultList.get(i).put("changeOrderTime", df1.format(df1.parse(changeOrderTime)));
					resultList.get(i).put("playingTime", df1.format(df1.parse(playingTime)));
					resultList.get(i).put("acquiringTime", df1.format(df1.parse(acquiringTime)));
					String parkingDuration = dateSubtract(df1.parse(acquiringTime), df1.parse(playingTime));
					resultList.get(i).put("interval", parkingDuration);
					resultList.get(i).put("parkingType", 1);
					
				} catch (Exception e)
				{
					e.printStackTrace();
				}

//				int orderAmount = Integer.parseInt(resultList.get(i).get("orderAmount") + "");
				resultList.get(i).put("orderAmount", 0);

			}
		}
		WriterUtil.writer(response, "{\"errorcode\":0,\"count\":" + count + ",\"type\":" + type + ",\"data\":" + JSON.toJSONString(resultList) + "}");
	}

	public static String dateSubtract(Date d1, Date d2)
	{
		long diff = d2.getTime() - d1.getTime();// 这样得到的差值是微秒级别
		long days = diff / (1000 * 60 * 60 * 24);
		long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
		long hours1 = (days * 24) + hours;
		return hours1 + "小时" + minutes + "分钟";
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/earningsJd", method = RequestMethod.GET)
	public void earningsJd(String uuid, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("staffId", uuid);

		int count = carOrdersService.earningsJd1(map);
		int count1 = carOrdersService.earningsJd2(map);

		WriterUtil.writer(response, "{\"count\":" + (count + count1) + "}");
	}

	public static String generatedOrderNumber()
	{
		StringBuffer OrderNumbe = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss|SSS");
		String dateStr = sdf.format(new Date());
		String year = dateStr.substring(0, dateStr.indexOf("-"));
		String month = dateStr.substring(dateStr.indexOf("-") + 1, dateStr.lastIndexOf("-"));
		String day = dateStr.substring(dateStr.lastIndexOf("-") + 1, dateStr.indexOf(" "));
		String hour = dateStr.substring(dateStr.indexOf(" ") + 1, dateStr.indexOf(":"));
		String minute = dateStr.substring(dateStr.indexOf(":") + 1, dateStr.lastIndexOf(":"));
		String Second = dateStr.substring(dateStr.lastIndexOf(":") + 1, dateStr.indexOf("|"));
		String millisecond = dateStr.substring(dateStr.lastIndexOf("|") + 1);
		StringBuffer newDate = new StringBuffer();
		newDate.append(year).append(month).append(day).append(hour).append(minute).append(Second).append(millisecond);

		Random random = new Random();
		StringBuffer randombuf = new StringBuffer();
		for (int i = 0; i < 5; i++)
		{
			randombuf.append(random.nextInt(10));
		}
		OrderNumbe.append(newDate.toString()).append(randombuf.toString());
		return OrderNumbe.toString();
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateNotifyAppearances", method = RequestMethod.GET)
	public void updateNotifyAppearances(String orderFormNo, String playingPeople, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderFormNo", orderFormNo);
		map.put("playingPeople", playingPeople);

		Map<String, Object> resultMap = new HashMap<String, Object>();

		int updrow = carOrdersService.updateNotifyAppearances(map);
		resultMap.put("errorcode", 1);
		if (updrow > 0)
		{
			resultMap = carOrdersService.updateNotifyAppearancesResultMap(map);
			resultMap.put("errorcode", 0);
		}

		WriterUtil.writer(response, JSON.toJSONString(resultMap));
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/queryChargeAmountEx", method = RequestMethod.GET)
	public void queryChargeAmountEx(String userPhoneId, String licensePlateNumber, HttpServletRequest request, HttpServletResponse response)
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userPhoneId", userPhoneId);
		if (!"".equals(licensePlateNumber) && null != licensePlateNumber)
		{
			try
			{
				licensePlateNumber = new String(licensePlateNumber.getBytes("iso-8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
			List<Map<String, Object>> orderList = carOrdersService.queryChargeAmountExOrderList(map);// 查询aap找车位的纪录

			// 根据车牌查询入场纪录
			Map<String, Object> enterInfoMap = null;
			String[] licensePlateNumbers = licensePlateNumber.split(",");
			for (String str : licensePlateNumbers)
			{
				enterInfoMap = new HashMap<String, Object>();
				map.put("licensePlateNumber", str);
				List<Map<String, Object>> EnterrecoedList = carOrdersService.queryChargeAmountEnterrecoedList(map);// 根据车牌查询入场纪录

				if (EnterrecoedList.size() > 0)
				{
					if (EnterrecoedList.get(0).get("mark") != null && "0".equals(EnterrecoedList.get(0).get("mark") + ""))
					{// 如果没有离场
						enterInfoMap.put("type", 0);
						enterInfoMap.put("orderFormNo", generatedOrderNumber());
						enterInfoMap.put("recordId", EnterrecoedList.get(0).get("recordId"));
						enterInfoMap.put("oldRecordId", EnterrecoedList.get(0).get("oldRecordId"));
						enterInfoMap.put("carParkId", Integer.parseInt(EnterrecoedList.get(0).get("carParkId") + ""));
						enterInfoMap.put("carParkName", EnterrecoedList.get(0).get("carParkName") + "");
						enterInfoMap.put("licensePlateNumber", EnterrecoedList.get(0).get("recognitionNumber") + "");
						enterInfoMap.put("acquiringTime", EnterrecoedList.get(0).get("inTime") + "");
						enterInfoMap.put("playingTime", df.format(new Date()));
						enterInfoMap.put("areFare", 0);
						enterInfoMap.put("picUrl", EnterrecoedList.get(0).get("picUrl") + "");
						orderList.add(enterInfoMap);
					}
				}
			}
			for (int i = 0; i < orderList.size(); i++)
			{
				String picUrl = null;
				String portrait = null;

				if (!orderList.get(i).containsKey("type") && null != orderList.get(i).get("acquiringPerson") && null != orderList.get(i).get("playingPeople")
						&& null != orderList.get(i).get("acquiringPerson") && null != orderList.get(i).get("playingPeople"))
					orderList.get(i).put("type", 1);
				else if (!orderList.get(i).containsKey("type") && (null == orderList.get(i).get("acquiringPerson") || null == orderList.get(i).get("playingPeople")))
					orderList.get(i).put("type", 0);
				else
					orderList.get(i).put("type", 0);

				if (!orderList.get(i).containsKey("recordId"))
					orderList.get(i).put("recordId", orderList.get(i).get("orderFormId"));

				if (orderList.get(i).get("picUrl") != null && !"null".equals(orderList.get(i).get("picUrl")) && orderList.get(i).get("picUrl") != ""
						&& !"".equals(orderList.get(i).get("picUrl")))
				{
					picUrl = orderList.get(i).get("picUrl") + "";

				}
				if (picUrl != null)
				{
					portrait = OSSUtil.getUrlSigned(OSSUtil.getParkImgDir() + picUrl, 600);
				}
				if (portrait != null)
				{
					orderList.get(i).put("picUrl", picUrl);
					orderList.get(i).put("imgurl", portrait);
				} else
				{
					orderList.get(i).put("picUrl", "");
					orderList.get(i).put("imgurl", "");
				}
				String acquiringTime = df.format(new Date());// 入场时间
				if (orderList.get(i).get("acquiringTime") != null)
				{
					acquiringTime = orderList.get(i).get("acquiringTime") + "";
				}
				String playingTime = null;// 出场时间
				if (orderList.get(i).get("playingTime") != null)
				{
					playingTime = orderList.get(i).get("playingTime") + "";
				}
				try
				{
					orderList.get(i).put("acquiringTime", df.format(df.parse(acquiringTime)));
				} catch (ParseException e)
				{
					e.printStackTrace();
				}
				if (playingTime != null && !"null".equals(playingTime))
				{
					try
					{
						orderList.get(i).put("playingTime", df.format(df.parse(playingTime)));
					} catch (ParseException e)
					{
						e.printStackTrace();
					}
				} else
				{
					orderList.get(i).put("playingTime", "");
					playingTime = df.format(new Date());
				}
			}

			WriterUtil.writer(response, JSON.toJSONString(orderList));

		}

	}

	/**
	 * 盒子
	 * 
	 * 入场产生的缴费记录
	 * 
	 * @param userPhoneId
	 * @param licensePlateNumber
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/queryChargeAmountExBox", method = RequestMethod.GET)
	public void queryChargeAmountExBox(String userPhoneId, String licensePlateNumber, HttpServletRequest request, HttpServletResponse response)
	{
		System.out.println(userPhoneId);
		System.out.println(licensePlateNumber);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userPhoneId", userPhoneId);
		if (!"".equals(licensePlateNumber) && null != licensePlateNumber)
		{
			try
			{
				licensePlateNumber = new String(licensePlateNumber.getBytes("iso-8859-1"), "UTF-8");
				
			} catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}
			List<Map<String, Object>> orderList = carOrdersService.queryChargeAmountExOrderBoxList(map);// 查询aap找车位的纪录
			// 根据车牌查询入场纪录
			Map<String, Object> enterInfoMap = null;
			String[] licensePlateNumbers = licensePlateNumber.split(",");
			for (String str : licensePlateNumbers)
			{
				enterInfoMap = new HashMap<String, Object>();
				map.put("licensePlateNumber", str);
				List<Map<String, Object>> EnterrecoedList = carOrdersService.queryChargeAmountEnterrecoedList(map);// 根据车牌查询入场纪录

				if (EnterrecoedList.size() > 0)
				{
					JSONObject jsonData = (JSONObject)JSON.toJSON(EnterrecoedList.get(0));
					if (jsonData.getString("mark") != null && "0".equals(jsonData.getString("mark")))
					{// 如果没有离场
						enterInfoMap.put("type", 0);
						enterInfoMap.put("orderFormNo", generatedOrderNumber());
						enterInfoMap.put("recordId", jsonData.getInteger("recordId"));
						enterInfoMap.put("oldRecordId", jsonData.getInteger("oldRecordId"));
						enterInfoMap.put("carParkId", jsonData.getInteger("carParkId"));
						enterInfoMap.put("carParkName", jsonData.getString("carParkName")) ;
						enterInfoMap.put("licensePlateNumber", jsonData.getString("recognitionNumber") );
						enterInfoMap.put("acquiringTime", jsonData.getDate("inTime"));
						enterInfoMap.put("playingTime", df.format(new Date()));
						enterInfoMap.put("areFare", 0);
						enterInfoMap.put("picUrl", jsonData.getString("imgName"));
						enterInfoMap.put("latitude", jsonData.getString("latitude"));
						enterInfoMap.put("longitude", jsonData.getString("longitude"));
						enterInfoMap.put("province", jsonData.getString("province"));
						enterInfoMap.put("mycity", jsonData.getString("mycity"));
						enterInfoMap.put("dist", jsonData.getString("dist"));
						orderList.add(enterInfoMap);
					}
				}
			}
			
			//线上
			for (int i = 0; i < orderList.size(); i++)
			{
				String picUrl = null;
				String portrait = null;

				if (!orderList.get(i).containsKey("type"))
					orderList.get(i).put("type", 1);

				if (!orderList.get(i).containsKey("recordId"))
					orderList.get(i).put("recordId", orderList.get(i).get("orderFormId"));

				if (orderList.get(i).get("picUrl") != null && !"null".equals(orderList.get(i).get("picUrl")) && orderList.get(i).get("picUrl") != ""
						&& !"".equals(orderList.get(i).get("picUrl")))
				{
					picUrl = orderList.get(i).get("picUrl") + "";

				}
				if (picUrl != null)
				{
					
					if (picUrl.indexOf("/") == -1)
						picUrl = OSSUtil.getUrlSigned(OSSUtil.getParkingEntranceDir() + picUrl, 600);
					else
						picUrl = URLUtils.imgUrl + picUrl;
					
					
					portrait = picUrl;
				}
				if (portrait != null)
				{
					orderList.get(i).put("picUrl", picUrl);
					orderList.get(i).put("imgurl", portrait);
				} else
				{
					orderList.get(i).put("picUrl", "");
					orderList.get(i).put("imgurl", "");
				}
				String acquiringTime = df.format(new Date());// 入场时间
				if (orderList.get(i).get("acquiringTime") != null)
				{
					acquiringTime = orderList.get(i).get("acquiringTime") + "";
				}
				String playingTime = null;// 出场时间
				if (orderList.get(i).get("playingTime") != null)
				{
					playingTime = orderList.get(i).get("playingTime") + "";
				}
				try
				{
					orderList.get(i).put("acquiringTime", df.format(df.parse(acquiringTime)));
				} catch (ParseException e)
				{
					e.printStackTrace();
				}
				if (playingTime != null && !"null".equals(playingTime))
				{
					try
					{
						orderList.get(i).put("playingTime", df.format(df.parse(playingTime)));
					} catch (ParseException e)
					{
						e.printStackTrace();
					}
				} else
				{
					orderList.get(i).put("playingTime", "");
					playingTime = df.format(new Date());
				}
			}
			Map<String, Object> m1 = new HashMap<String, Object>();
			m1.put("list", orderList);
			if(orderList == null || orderList.size() == 0)
				m1.put("count", 0);
			else
				m1.put("count", 1);
			
			WriterUtil.writer(response, JSON.toJSONString(m1));

	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/queryChargeAmountExByPlate", method = RequestMethod.GET)
	public void queryChargeAmountExByPlate(String licensePlateNumber, HttpServletRequest request, HttpServletResponse response)
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> map = new HashMap<String, Object>();

		if (!"".equals(licensePlateNumber) && null != licensePlateNumber)
		{
			try
			{
				licensePlateNumber = new String(licensePlateNumber.getBytes("iso-8859-1"), "UTF-8");
				map.put("licensePlateNumber", licensePlateNumber);

			} catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
			List<Map<String, Object>> orderList = carOrdersService.queryChargeAmountExByPlateOrderList(map);// 查询aap找车位的纪录

			// 根据车牌查询入场纪录
			Map<String, Object> enterInfoMap = null;
			String[] licensePlateNumbers = licensePlateNumber.split(",");
			for (String str : licensePlateNumbers)
			{
				enterInfoMap = new HashMap<String, Object>();
				map.put("licensePlateNumber", str);
				List<Map<String, Object>> EnterrecoedList = carOrdersService.queryChargeAmountEntByPlateerrecoedList(map);// 根据车牌查询入场纪录

				if (EnterrecoedList.size() > 0)
				{
					if (EnterrecoedList.get(0).get("mark") != null && "0".equals(EnterrecoedList.get(0).get("mark") + ""))
					{// 如果没有离场
						enterInfoMap.put("type", 0);
						enterInfoMap.put("orderFormNo", generatedOrderNumber());
						enterInfoMap.put("recordId", EnterrecoedList.get(0).get("recordId"));
						enterInfoMap.put("oldRecordId", EnterrecoedList.get(0).get("oldRecordId"));
						enterInfoMap.put("carParkId", Integer.parseInt(EnterrecoedList.get(0).get("carParkId") + ""));
						enterInfoMap.put("carParkName", EnterrecoedList.get(0).get("carParkName") + "");
						enterInfoMap.put("licensePlateNumber", EnterrecoedList.get(0).get("recognitionNumber") + "");
						enterInfoMap.put("acquiringTime", EnterrecoedList.get(0).get("inTime") + "");
						enterInfoMap.put("playingTime", df.format(new Date()));
						enterInfoMap.put("areFare", 0);
						enterInfoMap.put("picUrl", EnterrecoedList.get(0).get("imgName") + "");
						orderList.add(enterInfoMap);
					}
				}
			}
			for (int i = 0; i < orderList.size(); i++)
			{
				String picUrl = null;
				String portrait = null;
				if (!orderList.get(i).containsKey("type"))
					orderList.get(i).put("type", 1);

				if (!orderList.get(i).containsKey("recordId"))
					orderList.get(i).put("recordId", orderList.get(i).get("orderFormId"));

				if (orderList.get(i).get("picUrl") != null && !"null".equals(orderList.get(i).get("picUrl")) && orderList.get(i).get("picUrl") != ""
						&& !"".equals(orderList.get(i).get("picUrl")))
				{
					picUrl = orderList.get(i).get("picUrl") + "";

				}
				if (picUrl != null)
				{
//					portrait = OSSUtil.getUrlSigned(OSSUtil.getParkImgDir() + picUrl, 600);
					
					if (picUrl.indexOf("/") == -1)
						picUrl = OSSUtil.getUrlSigned(OSSUtil.getParkingEntranceDir() + picUrl, 600);
					else
						picUrl = "http://121.199.47.11/" + picUrl;
					
					
					portrait = picUrl;
					
					
				}
				if (portrait != null)
				{
					orderList.get(i).put("picUrl", picUrl);
					orderList.get(i).put("imgurl", portrait);
				} else
				{
					orderList.get(i).put("picUrl", "");
					orderList.get(i).put("imgurl", "");
				}
				String acquiringTime = df.format(new Date());// 入场时间
				if (orderList.get(i).get("acquiringTime") != null)
				{
					acquiringTime = orderList.get(i).get("acquiringTime") + "";
				}
				String playingTime = null;// 出场时间
				if (orderList.get(i).get("playingTime") != null)
				{
					playingTime = orderList.get(i).get("playingTime") + "";
				}
				try
				{
					orderList.get(i).put("acquiringTime", df.format(df.parse(acquiringTime)));
				} catch (ParseException e)
				{
					e.printStackTrace();
				}
				if (playingTime != null && !"null".equals(playingTime))
				{
					try
					{
						orderList.get(i).put("playingTime", df.format(df.parse(playingTime)));
					} catch (ParseException e)
					{
						e.printStackTrace();
					}
				} else
				{
					orderList.get(i).put("playingTime", "");
					playingTime = df.format(new Date());
				}
			}

			WriterUtil.writer(response, JSON.toJSONString(orderList));

		}

	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/queryChargeAmountExByPlateBox", method = RequestMethod.GET)
	public void queryChargeAmountExByPlateBox(String licensePlateNumber, HttpServletRequest request, HttpServletResponse response)
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> map = new HashMap<String, Object>();

		if (!"".equals(licensePlateNumber) && null != licensePlateNumber)
		{
			try
			{
				licensePlateNumber = new String(licensePlateNumber.getBytes("iso-8859-1"), "UTF-8");
				map.put("licensePlateNumber", licensePlateNumber);

			} catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
			List<Map<String, Object>> orderList = carOrdersService.queryChargeAmountExByPlateOrderListBox(map);// 查询aap找车位的纪录

			// 根据车牌查询入场纪录
			Map<String, Object> enterInfoMap = null;
			String[] licensePlateNumbers = licensePlateNumber.split(",");
			for (String str : licensePlateNumbers)
			{
				enterInfoMap = new HashMap<String, Object>();
				map.put("licensePlateNumber", str);
				List<Map<String, Object>> EnterrecoedList = carOrdersService.queryChargeAmountEntByPlateerrecoedList(map);// 根据车牌查询入场纪录

				if (EnterrecoedList.size() > 0)
				{
					JSONObject jsonData = (JSONObject)JSON.toJSON(EnterrecoedList.get(0));
					if (jsonData.getString("mark") != null && "0".equals(jsonData.getString("mark")))
					{// 如果没有离场
						enterInfoMap.put("type", 0);
						enterInfoMap.put("orderFormNo", generatedOrderNumber());
						enterInfoMap.put("recordId", jsonData.getInteger("recordId"));
						enterInfoMap.put("oldRecordId", jsonData.getInteger("oldRecordId"));
						enterInfoMap.put("carParkId", jsonData.getInteger("carParkId") );
						enterInfoMap.put("carParkName", jsonData.getString("carParkName"));
						enterInfoMap.put("licensePlateNumber", jsonData.getString("recognitionNumber") );
						enterInfoMap.put("acquiringTime", jsonData.getDate("inTime"));
						
						enterInfoMap.put("playingTime", df.format(new Date()));
						enterInfoMap.put("areFare", 0);
						enterInfoMap.put("picUrl", jsonData.getString("imgName"));
						enterInfoMap.put("province", jsonData.getString("province"));
						enterInfoMap.put("mycity", jsonData.getString("mycity"));
						enterInfoMap.put("dist", jsonData.getString("dist"));
						orderList.add(enterInfoMap);
					}
				}
			}
			for (int i = 0; i < orderList.size(); i++)
			{
				String picUrl = null;
				String portrait = null;
				if (!orderList.get(i).containsKey("type"))
					orderList.get(i).put("type", 1);

				if (!orderList.get(i).containsKey("recordId"))
					orderList.get(i).put("recordId", orderList.get(i).get("orderFormId"));

				if (orderList.get(i).get("picUrl") != null && !"null".equals(orderList.get(i).get("picUrl")) && orderList.get(i).get("picUrl") != ""
						&& !"".equals(orderList.get(i).get("picUrl")))
				{
					picUrl = orderList.get(i).get("picUrl") + "";

				}
				if (picUrl != null)
				{
//					portrait = OSSUtil.getUrlSigned(OSSUtil.getParkImgDir() + picUrl, 600);
					if (picUrl.indexOf("/") == -1)
						picUrl = OSSUtil.getUrlSigned(OSSUtil.getParkingEntranceDir() + picUrl, 600);
					else
						picUrl = "http://121.199.47.11/" + picUrl;
					
					
					portrait = picUrl;
				}
				if (portrait != null)
				{
					orderList.get(i).put("picUrl", picUrl);
					orderList.get(i).put("imgurl", portrait);
				} else
				{
					orderList.get(i).put("picUrl", "");
					orderList.get(i).put("imgurl", "");
				}
				String acquiringTime = df.format(new Date());// 入场时间
				if (orderList.get(i).get("acquiringTime") != null)
				{
					acquiringTime = orderList.get(i).get("acquiringTime") + "";
				}
				String playingTime = null;// 出场时间
				if (orderList.get(i).get("playingTime") != null)
				{
					playingTime = orderList.get(i).get("playingTime") + "";
				}
				try
				{
					orderList.get(i).put("acquiringTime", df.format(df.parse(acquiringTime)));
				} catch (ParseException e)
				{
					e.printStackTrace();
				}
				if (playingTime != null && !"null".equals(playingTime))
				{
					try
					{
						orderList.get(i).put("playingTime", df.format(df.parse(playingTime)));
					} catch (ParseException e)
					{
						e.printStackTrace();
					}
				} else
				{
					orderList.get(i).put("playingTime", "");
					playingTime = df.format(new Date());
				}
			}

			Map<String, Object> m1 = new HashMap<String, Object>();
			m1.put("list", orderList);
			if(orderList == null || orderList.size() == 0)
				m1.put("count", 0);
			else
				m1.put("count", 1);
			
			WriterUtil.writer(response, JSON.toJSONString(m1));

		}

	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/findOrderformByid", method = RequestMethod.GET)
	public void findOrderformByid(String orderFormNo, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderFormNo", orderFormNo);
		Map<String, Object> map1 = carOrdersService.findOrderformByid(map);
		WriterUtil.writer(response, JSON.toJSONString(map1));
	}

	
	/**
	 * 已出场
	 * 
	 * @param uuid
	 * @param carParkId
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateOrderform", method = RequestMethod.GET)
	public void updateOrderform(String uuid,Integer carParkId, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uuid", uuid);
		map.put("carParkId", carParkId);
		
		Map<String, Object> map1 =carOrdersService.updatePlayingTime1(map);
		
		int rowcount = carOrdersService.updatePlayingTime(map);

		if (rowcount == 0)
		{
			WriterUtil.writer(response, "{\"errorcode\":1,data:"+JSON.toJSONString(map1)+"}");
		} else
		{
			WriterUtil.writer(response, "{\"errorcode\":0,data:"+JSON.toJSONString(map1)+"}");
		}
	}

	public void contextInitialized() throws ParseException
	{
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = carOrdersService.contextInitializedFind(map);

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		for (Map<String, Object> m : list)
		{
			String orderFormNo = m.get("orderFormNo") + "";
			String changeOrderTime = m.get("changeOrderTime") + "";
			String hourStr = dateSubtract(df.parse(changeOrderTime), new Date());
			int hour = Integer.parseInt(hourStr.substring(0, hourStr.indexOf("小")));
			
			if (hour >= 1)
			{
				map.put("orderFormNo", orderFormNo);
				carOrdersService.contextInitializedUpdate(map);
			}

		}

		// 蓝牙盒子
		list = carOrdersService.contextInitializedFindBox(map);

		for (Map<String, Object> m : list)
		{
			String orderFormNo = m.get("orderFormNo") + "";
			String changeOrderTime = m.get("changeOrderTime") + "";
			String hourStr = dateSubtract(df.parse(changeOrderTime), new Date());
			int hour = Integer.parseInt(hourStr.substring(0, hourStr.indexOf("小")));
			if (hour >= 1)
			{
				map.put("orderFormNo", orderFormNo);
				carOrdersService.contextInitializedUpdate(map);
			}

		}
	}

	/**
	 * 根据订单号查询订单记录 结算用
	 * 
	 * @param orderFormNo
	 * @param licensePlateNumber
	 * @param uuid
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/findOrderform", method = RequestMethod.GET)
	public void findOrderform(String orderFormNo, String licensePlateNumber, String uuid, HttpServletRequest request, HttpServletResponse response)
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderFormNo", orderFormNo);

		try
		{
			licensePlateNumber = new String(licensePlateNumber.getBytes("iso-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		Map<String, Object> map1 = carOrdersService.findOrderformByid(map);// 线上

		if (null != map1 && 0 != map1.size())
		{
			map1.put("recordId", map1.get("orderFormId"));
			map1.put("type", 1);
			WriterUtil.writer(response, JSON.toJSONString(map1));
		} else
		{

			if (!"".equals(licensePlateNumber) && null != licensePlateNumber)
			{
				// 根据车牌查询入场纪录
				Map<String, Object> enterInfoMap = null;
				enterInfoMap = new HashMap<String, Object>();
				map.put("licensePlateNumber", licensePlateNumber);
				List<Map<String, Object>> EnterrecoedList = carOrdersService.queryChargeAmountEnterrecoedList(map);// 根据车牌查询入场纪录

				if (EnterrecoedList.size() > 0)
				{
					if (EnterrecoedList.get(0).get("mark") != null && "0".equals(EnterrecoedList.get(0).get("mark") + ""))
					{// 如果没有离场
						enterInfoMap.put("type", 0);
						enterInfoMap.put("orderFormNo", orderFormNo);
						enterInfoMap.put("recordId", EnterrecoedList.get(0).get("recordId"));
						enterInfoMap.put("oldRecordId", EnterrecoedList.get(0).get("oldRecordId"));
						enterInfoMap.put("carParkId", Integer.parseInt(EnterrecoedList.get(0).get("carParkId") + ""));
						enterInfoMap.put("carParkName", EnterrecoedList.get(0).get("carParkName") + "");
						enterInfoMap.put("licensePlateNumber", EnterrecoedList.get(0).get("recognitionNumber") + "");
						enterInfoMap.put("province", EnterrecoedList.get(0).get("province"));
						enterInfoMap.put("mycity", EnterrecoedList.get(0).get("mycity"));
						enterInfoMap.put("dist", EnterrecoedList.get(0).get("dist"));
						try
						{
							enterInfoMap.put("acquiringTime", df.parse(EnterrecoedList.get(0).get("inTime") + "").getTime());
						} catch (ParseException e)
						{
							e.printStackTrace();
						}
						enterInfoMap.put("playingTime", new Date().getTime());
						enterInfoMap.put("areFare", 0);
						enterInfoMap.put("picUrl", EnterrecoedList.get(0).get("picUrl") + "");
						enterInfoMap.put("uuid", uuid);
						Object[] recordInfo = new Object[1];
						recordInfo[0] = Integer.parseInt(EnterrecoedList.get(0).get("recordId") + "");

						WriterUtil.writer(response, JSON.toJSONString(enterInfoMap));
					}
				}
			}

		}

	}

	/**
	 * 
	 * 生成订单，通用方法
	 * 
	 * @param jsonData
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/saveOrderForms", method = RequestMethod.GET)
	public void saveOrderForms(cn.rf.hz.web.model.gd.OrderForm orderForm, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		try
		{
			map = getFieldVlaue(orderForm);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		int rowcount = carOrdersService.save(map);

		Map<String, Object> map1 = new HashMap<String, Object>();
		if (rowcount > 0)
			map1.put("errorCode", 0);
		else
			map1.put("errorCode", 1);
		WriterUtil.writer(response, JSON.toJSONString(map1));
	}

	/**
	 * 
	 * 待出场
	 * 
	 * @param jsonData
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateOrderForms", method = RequestMethod.GET)
	public void updateOrderForms(cn.rf.hz.web.model.gd.OrderForm orderForm, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		try
		{
			map = getFieldVlaue(orderForm);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		// int rowcount = carOrdersService.update(map);
		int rowcount = carOrdersService.updateAcquiringTime(map);

		Map<String, Object> map1 = new HashMap<String, Object>();
		if (rowcount > 0)
			map1.put("errorCode", 0);
		else
			map1.put("errorCode", 1);

		WriterUtil.writer(response, JSON.toJSONString(map1));
	}

	/**
	 * 查询有入场时间，没有出场时间的订单信息
	 * 
	 * @param jsonData
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/findOrderformInOutInfo", method = RequestMethod.GET)
	public void findOrderformInOutInfo(String uuid, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uuid", uuid);
		Map<String, Object> map1 = carOrdersService.findOrderformInOutInfo(map);
		WriterUtil.writer(response, JSON.toJSONString(map1));
	}

	/**
	 * 查询有uuId、carparkId 查询最近一条（生成时间倒序）订单生成信息
	 * 
	 * @param jsonData
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/findOrderFormCreateTime", method = RequestMethod.GET)
	public void findOrderFormCreateTime(String uuid, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uuid", uuid);

		Map<String, Object> map1 = new HashMap<String, Object>();

		try
		{
			map1 = carOrdersService.findOrderFormCreateTime(map);

			//
			// if (!"".equals(map1.get("acquiringPerson")) && null !=
			// map1.get("acquiringPerson"))
			// map1.put("type", 1);// APP线上
			// else
			// map1.put("type", 0);// 盒子

			map1.put("type", 1);

			if (map1 != null)
				map1.put("count", 1);
			else
				map1.put("count", 0);

			map1.put("errorCode", 0);

		} catch (Exception e)
		{
			map1 = new HashMap<String, Object>();
			map1.put("errorCode", 1);
			map1.put("count", 0);
		}

		WriterUtil.writer(response, JSON.toJSONString(map1));
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
	 * 生成订单，蓝牙盒子
	 * 
	 * @param jsonData
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/saveOrderFormsBox", method = RequestMethod.GET)
	public void saveOrderFormsBox(String phone, String uuid, String carParkId, String licensePlateNumber, String areFare, Integer sources,Integer orderMode, HttpServletRequest request,
			HttpServletResponse response)
	{
		String orderFormNo = generatedOrderNumber();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("phone", phone);
		map.put("uuid", uuid);
		map.put("carParkId", carParkId);
		map.put("licensePlateNumber", licensePlateNumber);
		map.put("areFare", areFare);
		map.put("orderFormNo", orderFormNo);
		map.put("sources", sources);
		map.put("orderMode", orderMode);
		
		Map<String, Object> map1 = new HashMap<String, Object>();
		try{
			
			int rowcount = carOrdersService.saveOrderFormsBox(map);
			
			
			
			map1.put("orderFormNo", orderFormNo);
			if (rowcount > 0)
				map1.put("errorCode", 0);
			else
				map1.put("errorCode", 1);
			WriterUtil.writer(response, JSON.toJSONString(map1));
		}catch(Exception e){
			map1.put("errorCode", 1);
			WriterUtil.writer(response, JSON.toJSONString(map1));
		}
	}
	
	
	
	/**
	 * 
	 * 待出场
	 * 
	 * @param jsonData
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateOrderFormIsItEffective", method = RequestMethod.GET)
	public void updateOrderFormIsItEffective(String uuid, Long playingTime, HttpServletRequest request, HttpServletResponse response)
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uuid", uuid);
		map.put("playingTime", df.format(new Date(playingTime)));
		
		int rowcount = carOrdersService.updateOrderFormIsItEffective(map);

		Map<String, Object> map1 = new HashMap<String, Object>();
		if (rowcount > 0)
			map1.put("errorCode", 0);
		else
			map1.put("errorCode", 1);

		WriterUtil.writer(response, JSON.toJSONString(map1));
	}
	
	/**
	 * 是否有效订单
	 * 更新状态
	 * 
	 * @param orderFormNo
	 * @param isItEffective
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateItEffective", method = RequestMethod.GET)
	public void updateItEffective(String orderFormNo,Integer isItEffective,Long playingTime, Integer status, HttpServletRequest request, HttpServletResponse response)
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderFormNo", orderFormNo);
		map.put("isItEffective", isItEffective);
		map.put("playingTime", df.format(new Date(playingTime)));
		map.put("status", status);
		
		int rowcount = carOrdersService.updateItEffective(map);

		Map<String, Object> map1 = new HashMap<String, Object>();
		if (rowcount > 0)
			map1.put("errorCode", 0);
		else
			map1.put("errorCode", 1);

		WriterUtil.writer(response, JSON.toJSONString(map1));
	}

}
