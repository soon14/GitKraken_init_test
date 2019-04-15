package cn.rf.hz.web.cloudpark.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.cloudpark.service.CarInfoForPadService;

@Controller
public class CarInfoForPadController {
	private final static Logger LOG = Logger.getLogger(CarInfoForPadController.class);
	@Autowired
	CarInfoForPadService carInfoForPadService;

	/**
	 * 获取进场车辆相关信息到PAD
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getCarInfoForPad", method = RequestMethod.POST)
	public void getCarInfoForPad(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain; charset=utf-8");
		try {
			PrintWriter out = response.getWriter();
			LOG.info("getCarInfoForPad.req" + requestBody);
			JSONObject data = JSON.parseObject(requestBody);
			String carCode = data.getString("carCode");
			JSONObject resultJSON = carInfoForPadService.getCarInfoForPad(requestBody);
			LOG.info("getCarInfoForPad.resp" + carCode + "-" + resultJSON);
			out.print(resultJSON.toJSONString());// 返回json格式数据
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Auth(verifyLogin = false, verifyURL = false)
	// @RequestMapping(value = "/getHistoryChargeForPad", method =
	// RequestMethod.POST)
	// public void getHistoryChargeForPad(@RequestBody String requestBody,
	// HttpServletRequest request,
	// HttpServletResponse response) {
	// response.setCharacterEncoding("utf-8");
	// response.setContentType("text/plain; charset=utf-8");
	// try {
	// PrintWriter out = response.getWriter();
	// LOG.info("getHistoryChargeForPad.req:" + requestBody);
	// JSONObject data = JSON.parseObject(requestBody);
	// String carCode = data.getString("carcode");
	// JSONObject resultJSON =
	// carInfoForPadService.getHistoryChargeForPad(requestBody);
	// LOG.info("getHistoryChargeForPad.resp:" + carCode + "-" + resultJSON);
	// out.print("\r\n" + resultJSON.toJSONString());// 返回json格式数据
	// out.flush();
	// out.close();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
}
