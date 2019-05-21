package cn.rf.hz.web.cloudpark.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.cloudpark.service.OrderUserInfoService;

/**
 * 提供给行呗上传预约用户信息
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/uploadorderuserInfo")
public class OrderUserInfoController {
	@Autowired
	OrderUserInfoService orderuserinfoservice;
	private static final Logger logger = LoggerFactory.getLogger(OrderUserInfoController.class);
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public void uploadorderuser(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response){
		logger.info("行呗上传用户预约信息:"+requestBody+"===========");
		JSONObject obj=JSON.parseObject(requestBody);
		logger.info("行呗上传用户预约信息,转成json，json=:"+obj+"===========");
		JSONObject jso=orderuserinfoservice.uploadorderuser(obj);
		
		try {
			response.getWriter().println(jso.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/uploadwithholdinfo", method = RequestMethod.POST)
	public void UploadWithholdInfo(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response){
		logger.info("代扣信息:"+requestBody+"===========");
		JSONObject obj=JSON.parseObject(requestBody);
		logger.info("行呗上传代扣信息,转成json，json=:"+obj+"===========");
		JSONObject jso=orderuserinfoservice.uploadwithholdinfo(obj);
		
		try {
			response.getWriter().println(jso.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	WithholdInfo*/
	
}
