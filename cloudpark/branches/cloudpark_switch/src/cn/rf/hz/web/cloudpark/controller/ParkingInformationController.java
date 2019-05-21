package cn.rf.hz.web.cloudpark.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.reformer.cloudpark.service.ParkingInformation;

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.cloudpark.daoxx.Tc_userinfoMapper;
import cn.rf.hz.web.cloudpark.moder.Tc_userinfo;

@Controller
@RequestMapping("/ParkingInformation")

public class ParkingInformationController {
	private static final Logger logger = LoggerFactory.getLogger(ParkingInformationController.class);
	@Autowired(required = false)
	private ParkingInformation parkingInformation;
	
	@Autowired
	private Tc_userinfoMapper userinfomapper;
	
	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/query", method = RequestMethod.POST)
	public void queryParkingInformation(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain; charset=utf-8");
		try {
			PrintWriter out = response.getWriter();
			JSONObject resultJSON = parkingInformation.queryParkingInformation(requestBody);
			out.print("\r\n" + resultJSON.toJSONString());// 返回json格式数据
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/queryUsers", method = RequestMethod.POST)
	public void queryParkingUserlist(@RequestBody String requestBody,HttpServletRequest request, HttpServletResponse response ){
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain; charset=utf-8");
		try {
			PrintWriter out = response.getWriter();
			JSONObject jsonObject=JSONObject.parseObject(requestBody);
			String parkinglotNo=jsonObject.getString("parkinglotno");	
			List<com.reformer.cloudpark.model.Tc_userinfo> userList= userinfomapper.selectByParkinglotno(parkinglotNo);	
			JSONObject resultJSON=new JSONObject();
			resultJSON.put("users",userList);
			out.print("\r\n" + resultJSON.toJSONString());// 返回json格式数据
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
