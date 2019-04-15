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
import cn.rf.hz.web.cloudpark.service.PadInfoService;

@Controller
@RequestMapping("/getInfo")
public class PadInfoController {

	@Autowired(required = false)
	private PadInfoService padinfoservice;

	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getPadInfo", method = RequestMethod.POST)
	public void getPadInfo(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
			JSONObject result = new JSONObject();
			result = padinfoservice.getPadInfo(requestBody);
			if (result != null && !result.isEmpty()) {
				// out.println(java.net.URLDecoder.decode(result.toJSONString(),"UTF-8"));
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
	@RequestMapping(value = "/getNewPadInfo", method = RequestMethod.POST)
	public void getNewPadInfo(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
			JSONObject result = new JSONObject();
			result = padinfoservice.getNewPadInfo(requestBody);
			if (result != null && !result.isEmpty()) {
				// out.println(java.net.URLDecoder.decode(result.toJSONString(),"UTF-8"));
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
