package cn.rf.hz.web.cloudpark.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.cloudpark.moder.Pb_operator;
import cn.rf.hz.web.cloudpark.service.LoginInfoService;
import cn.rf.hz.web.utils.WriterUtil;

@Controller
@RequestMapping("/getInfo")
public class LoginInfoController {

	@Autowired(required = false)
	private LoginInfoService operatorinfoservice;

	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getLoginInfo", method = RequestMethod.POST)
	public void getLoginInfo(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			PrintWriter out = response.getWriter();
			JSONObject result = new JSONObject();
			result = operatorinfoservice.getLoginInfo(requestBody);
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
}
