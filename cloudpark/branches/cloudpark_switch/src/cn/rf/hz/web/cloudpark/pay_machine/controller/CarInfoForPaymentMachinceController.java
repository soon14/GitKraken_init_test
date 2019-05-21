package cn.rf.hz.web.cloudpark.pay_machine.controller;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.cloudpark.exception.ParameterException;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm_in;
import cn.rf.hz.web.cloudpark.pay_machine.service.CarInfoForPaymentMachineService;
import cn.rf.hz.web.utils.DateUtil;
import cn.rf.hz.web.utils.OSSUtil;

@Controller
@RequestMapping("/machine")
public class CarInfoForPaymentMachinceController {
	private final static Logger LOG = Logger.getLogger(CarInfoForPaymentMachinceController.class);

	private final static String image_url_prefix = "http://cloud.parking24.cn:9090";
	
	@Autowired
	CarInfoForPaymentMachineService carInfoForPaymentMachineService;

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getCarInfoListbyCarcode", method = RequestMethod.POST)
	public void getCarInfoListbyCarcode(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain; charset=utf-8");
		try {
			JSONObject data = JSON.parseObject(requestBody);
			String parkinglotNo = data.getString("park_code"); // 车场编号
			String carcode = data.getString("car_code"); // 车牌
			// 车场编号
			if (null == parkinglotNo || parkinglotNo.isEmpty()) {
				throw new ParameterException("parkinglotNo is null");
			}
			// 车牌
			if (null == carcode || carcode.isEmpty()) {
				throw new ParameterException("carcode is null");
			}
			LOG.info("getCarInfoListbyCarcode.req:" + carcode + requestBody);
			// 根据车场编号和车牌模糊匹配出场内车辆信息集合
			List<Tc_usercrdtm_in> in_list = carInfoForPaymentMachineService.getCarInfoListbyCarcode(parkinglotNo,
					carcode);

			JSONArray jsonArray = new JSONArray();
			int rows = 0;
			// 构建返回结果
			for (Tc_usercrdtm_in usercrdtmIn : in_list) {
				JSONObject obj = new JSONObject();
				String image_url = OSSUtil.getUrlSigned(usercrdtmIn.getImagepath() + "-1.jpg", 1800);
				image_url = image_url.replace("http://cloudpark.oss-cn-hangzhou.aliyuncs.com", image_url_prefix);
				obj.put("image_url", image_url);
				obj.put("car_code", usercrdtmIn.getCarcode());
				obj.put("in_time", DateUtil.getPlusTime(usercrdtmIn.getCrdtm()));
				obj.put("area_id", usercrdtmIn.getAreaId());
				jsonArray.add(obj);
				rows++;
			}
			JSONObject resultJSON = new JSONObject();
			resultJSON.put("rows", rows);
			resultJSON.put("total", jsonArray);
			LOG.info("getCarInfoListbyCarcode.resp:" + carcode + resultJSON);
			PrintWriter out = response.getWriter();
			// 返回结果
			out.print("\r\n" + resultJSON.toJSONString());// 返回json格式数据
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getCarInfoCountbyCarcode", method = RequestMethod.POST)
	public void getCarInfoCountbyCarcode(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain; charset=utf-8");
		try {
			JSONObject data = JSON.parseObject(requestBody);
			String parkinglotNo = data.getString("park_code"); // 车场编号
			String carcode = data.getString("car_code"); // 车牌
			// 车场编号
			if (null == parkinglotNo || parkinglotNo.isEmpty()) {
				throw new ParameterException("parkinglotNo is null");
			}
			// 车牌
			if (null == carcode || carcode.isEmpty()) {
				throw new ParameterException("carcode is null");
			}
			LOG.info("getCarInfoCountbyCarcode.req:" + carcode + requestBody);
			// 根据车场编号和车牌模糊匹配出场内车辆数量
			int rows = carInfoForPaymentMachineService.getCarInfoCountbyCarcode(parkinglotNo, carcode);
			// 构建返回结果
			JSONObject resultJSON = new JSONObject();
			resultJSON.put("rows", rows);
			LOG.info("getCarInfoCountbyCarcode.resp:" + carcode + resultJSON);
			PrintWriter out = response.getWriter();
			// 返回结果
			out.print("\r\n" + resultJSON.toJSONString());// 返回json格式数据
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getCarInfoListbyTime", method = RequestMethod.POST)
	public void getCarInfoListbyTime(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain; charset=utf-8");
		try {
			JSONObject data = JSON.parseObject(requestBody);
			String parkinglotNo = data.getString("park_code"); // 车场编号
			String in_date = data.getString("in_date"); // 入场日期
			String start_time = data.getString("start_time"); // 入场时间范围开始时间
			String end_time = data.getString("end_time"); // 入场时间范围结束时间
			if (null == parkinglotNo || parkinglotNo.isEmpty()) {
				throw new ParameterException("parkinglotNo is null");
			}
			if (null == in_date || in_date.isEmpty()) {
				throw new ParameterException("in_date is null");
			}
			if (null == start_time || start_time.isEmpty()) {
				throw new ParameterException("start_time is null");
			}
			if (null == end_time || end_time.isEmpty()) {
				throw new ParameterException("end_time is null");
			}
			String startDate = in_date + " " + start_time;
			String endDate = in_date + " " + end_time;
			LOG.info("getCarInfoListbyTime.req:" + parkinglotNo + requestBody);
			// 根据车场编号和车牌模糊匹配出场内车辆信息集合
			List<Tc_usercrdtm_in> in_list = carInfoForPaymentMachineService.getCarInfoListbyTime(parkinglotNo,
					startDate, endDate);
			JSONArray jsonArray = new JSONArray();
			int rows = 0;
			// 构建返回结果
			for (Tc_usercrdtm_in usercrdtmIn : in_list) {
				JSONObject obj = new JSONObject();
				String image_url = OSSUtil.getUrlSigned(usercrdtmIn.getImagepath() + "-1.jpg", 1800);
				image_url = image_url.replace("http://cloudpark.oss-cn-hangzhou.aliyuncs.com", image_url_prefix);
				obj.put("image_url", image_url);
				obj.put("car_code", usercrdtmIn.getCarcode());
				obj.put("in_time", DateUtil.getPlusTime(usercrdtmIn.getCrdtm()));
				obj.put("area_id", usercrdtmIn.getAreaId());
				jsonArray.add(obj);
				rows++;
			}
			JSONObject resultJSON = new JSONObject();
			resultJSON.put("rows", rows);
			resultJSON.put("total", jsonArray);
			LOG.info("getCarInfoListbyTime.resp:" + parkinglotNo + resultJSON);
			PrintWriter out = response.getWriter();
			// 返回结果
			out.print("\r\n" + resultJSON.toJSONString());// 返回json格式数据
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getCarInfoCountbyTime", method = RequestMethod.POST)
	public void getCarInfoCountbyTime(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain; charset=utf-8");
		try {
			JSONObject data = JSON.parseObject(requestBody);
			String parkinglotNo = data.getString("park_code"); // 车场编号
			String in_date = data.getString("in_date"); // 入场日期
			String start_time = data.getString("start_time"); // 入场时间范围开始时间
			String end_time = data.getString("end_time"); // 入场时间范围结束时间
			if (null == parkinglotNo || parkinglotNo.isEmpty()) {
				throw new ParameterException("parkinglotNo is null");
			}
			if (null == in_date || in_date.isEmpty()) {
				throw new ParameterException("in_date is null");
			}
			if (null == start_time || start_time.isEmpty()) {
				throw new ParameterException("start_time is null");
			}
			if (null == end_time || end_time.isEmpty()) {
				throw new ParameterException("end_time is null");
			}
			String startDate = in_date + " " + start_time;
			String endDate = in_date + " " + end_time;
			LOG.info("getCarInfoListbyTime.req:" + parkinglotNo + requestBody);
			// 根据车场编号和车牌模糊匹配出场内车辆数量
			int rows = carInfoForPaymentMachineService.getCarInfoCountbyTime(parkinglotNo, startDate, endDate);
			// 构建返回结果
			JSONObject resultJSON = new JSONObject();
			resultJSON.put("rows", rows);
			LOG.info("getCarInfoCountbyTime.resp:" + parkinglotNo + resultJSON);
			PrintWriter out = response.getWriter();
			// 返回结果
			out.print("\r\n" + resultJSON.toJSONString());// 返回json格式数据
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/uploadHeartBeatRecord", method = RequestMethod.POST)
	public void uploadHeartBeatRecord(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain; charset=utf-8");
		PrintWriter out;
		try {
			out = response.getWriter();
			JSONObject resultJSON = new JSONObject();
			// 解析心跳json
			JSONObject jsonHeart = JSONObject.parseObject(requestBody);
			String parkinglotNo = jsonHeart.getString("park_code");
			LOG.info("uploadHeartBeatRecord.req:" + parkinglotNo + requestBody);
			// 上传心跳信息
			resultJSON = carInfoForPaymentMachineService.uploadHeartBeatRecord(jsonHeart);
			LOG.info("uploadHeartBeatRecord.resp:" + parkinglotNo + resultJSON);
			// 返回结果
			out.print("\r\n" + resultJSON.toJSONString());// 返回json格式数据
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getCarChargeInfo", method = RequestMethod.POST)
	public void getCarChargeInfo(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain; charset=utf-8");
		PrintWriter out;
		try {
			out = response.getWriter();
			JSONObject resultJSON = new JSONObject();
			JSONObject carInfo = JSONObject.parseObject(requestBody);
			String parkinglotNo = carInfo.getString("park_code");
			String carCode = carInfo.getString("car_code");
			String inTime = carInfo.getString("in_time");
			resultJSON = carInfoForPaymentMachineService.getCarChargeInfo(parkinglotNo, carCode, inTime);
			// 返回结果
			out.print("\r\n" + resultJSON.toJSONString());// 返回json格式数据
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/uploadChargeRecordInfo", method = RequestMethod.POST)
	public void uploadChargeRecordInfo(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain; charset=utf-8");
		PrintWriter out;
		try {
			out = response.getWriter();
			JSONObject resultJSON = new JSONObject();
			JSONObject chargeInfo = JSONObject.parseObject(requestBody);
			resultJSON = carInfoForPaymentMachineService.uploadChargeRecordInfo(chargeInfo);
			// 返回结果
			out.print("\r\n" + resultJSON.toJSONString());// 返回json格式数据
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
