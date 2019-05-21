package cn.rf.hz.web.cloudpark.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.cloudpark.service.BoxInfoService;

@Controller
@RequestMapping("/getInfo")
public class BoxInfoController {
	@Autowired(required = false)
	private BoxInfoService boxinfoservice;

	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getBoxInfo", method = RequestMethod.POST)
	public void getBoxInfo(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter out = response.getWriter();
			JSONObject result = new JSONObject();
			result = boxinfoservice.getBoxInfo(requestBody);
			if (result != null && !result.isEmpty()) {
				out.println(result.toJSONString());
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getLedShowInfo", method = RequestMethod.POST)
	public void getLedShowInfo(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter out = response.getWriter();
			JSONObject result = new JSONObject();
			result = boxinfoservice.getLedShowInfo(requestBody);
			if (result != null && !result.isEmpty()) {
				out.println(result.toJSONString());
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getParkingParamInfo", method = RequestMethod.POST)
	public void getParkingParamInfo(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			PrintWriter out = response.getWriter();
			JSONObject result = new JSONObject();
			result = boxinfoservice.getParkingParamInfo(requestBody);
			if (result != null && !result.isEmpty()) {
				out.println(result.toJSONString());
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/uploadBoxMemoryUsage", method = RequestMethod.POST)
	public void uploadBoxMemoryUsage(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			PrintWriter out = response.getWriter();
			JSONObject result = new JSONObject();
			result = boxinfoservice.uploadBoxMemoryUsage(requestBody);
			if (result != null && !result.isEmpty()) {
				out.println(result.toJSONString());
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
