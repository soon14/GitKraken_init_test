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

import cn.rf.hz.web.cloudpark.impl.GenerateTaskServiceImp;
@Controller
@RequestMapping("/GenerateK")
public class GeneratekController {
	@Autowired(required = false)
	GenerateTaskServiceImp generatetaskservice;
	private static final Logger logger = LoggerFactory.getLogger(GeneratekController.class);
	@RequestMapping(value = "/getall", method = RequestMethod.POST)
	public void GenerateK(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response){
		logger.info("requestBody=============================="+requestBody);
		JSONObject obj=JSON.parseObject(requestBody);
		logger.info("obj.getString=============================="+obj.getString("parkingNO"));
		String parkingNO=obj.getString("parkingNO");
		boolean isUpDate=false;
		
		boolean flag=false;
		try {
			flag=generatetaskservice.getAllK(parkingNO, isUpDate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if (flag) {
				response.getWriter().println("非定时生成全量成功");
			}else{
				response.getWriter().println("非定时生成全量失败");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
