package cn.rf.hz.web.cloudpark.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.cloudpark.service.Tc_chargejmService;

@Controller
@RequestMapping("/getInfo")
public class ChargeJmController {

	@Autowired(required = false)
	public Tc_chargejmService chargejmservice;

	private static final Logger logger = LoggerFactory.getLogger(ChargeJmController.class);
	
	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getChargeJmInfo", method = RequestMethod.POST)
	public void getChargeJmInfo(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response) {
		logger.info("=============getChargeJmInfo:begin==============");
		try {
			PrintWriter out = response.getWriter();
			JSONObject result = new JSONObject();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			result = chargejmservice.getChargeJmInfo(requestBody);
			if (result != null && !result.isEmpty()) {
				out.println(result.toJSONString());
			}
			out.flush();
			out.close();
			logger.info("=============getChargeJmInfo:end==============");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
