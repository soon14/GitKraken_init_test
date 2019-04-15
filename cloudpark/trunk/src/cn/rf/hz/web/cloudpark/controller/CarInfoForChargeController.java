package cn.rf.hz.web.cloudpark.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.cloudpark.impl.CarInfoForChargeServiceImp;
import cn.rf.hz.web.cloudpark.service.CarInfoForChargeService;
import sun.security.krb5.internal.LoginOptions;

@Controller
public class CarInfoForChargeController {
	private final static Logger LOG = Logger.getLogger(CarInfoForChargeController.class);
	@Autowired
	CarInfoForChargeService carInfoForChargeService;

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getCarInfoForCharge", method = RequestMethod.POST)
	public void getCarInfoForCharge(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain; charset=utf-8");
		try {
			JSONObject data = JSON.parseObject(requestBody);
			String carCode = data.getString("carId");
			LOG.info("getCarInfoForCharge.req:"+carCode+requestBody);
			PrintWriter out = response.getWriter();
			JSONObject resultJSON = carInfoForChargeService.getCarInfoForCharge(requestBody);
			LOG.info("getCarInfoForPad.resp:"+carCode+resultJSON);
			out.print("\r\n" + resultJSON.toJSONString());// 返回json格式数据
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getCarInfoForApp", method = RequestMethod.POST)
	public void getCarInfoForApp(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {

		// LOG.info("getCarInfoForApp.req:"+requestBody);
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain; charset=utf-8");
		try {
			PrintWriter out = response.getWriter();
			JSONObject resultJSON = carInfoForChargeService.getCarInfoForApp(requestBody);
			// LOG.info("getCarInfoForApp.resp:"+resultJSON);
			out.print("\r\n" + resultJSON.toJSONString());// 返回json格式数据
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getHistoryInCarInfoForApp", method = RequestMethod.POST)
	public void getHistoryInCarInfoForApp(@RequestBody String requestBody, HttpServletResponse response) {
	    LOG.info("getHistoryInCarInfoForApp.req:"+requestBody);
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain; charset=utf-8");
		try {
			PrintWriter out = response.getWriter();
			JSONObject resultJSON = carInfoForChargeService.getHistoryInCarInfoForApp(requestBody);
		    LOG.info("getHistoryInCarInfoForApp.resp:"+resultJSON);
			out.print("\r\n" + resultJSON.toJSONString());// 返回json格式数据
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getHistoryOutCarInfoForApp", method = RequestMethod.POST)
	public void getHistoryOutCarInfoForApp(@RequestBody String requestBody, HttpServletResponse response) {
	    LOG.info("getHistoryOutCarInfoForApp.req:"+requestBody);
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain; charset=utf-8");
		try {
			PrintWriter out = response.getWriter();
			JSONObject resultJSON = carInfoForChargeService.getHistoryOutCarInfoForApp(requestBody);
		    LOG.info("getHistoryOutCarInfoForApp.resp:"+resultJSON);
			out.print("\r\n" + resultJSON.toJSONString());// 返回json格式数据
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
