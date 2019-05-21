package cn.rf.hz.web.cloudpark.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.cloudpark.service.UpdateLicensePlateService;

@Controller
public class UpdateLicensePlateController {

	@Autowired
	UpdateLicensePlateService updateLicensePlateService;

	/**
	 * 上传纠正记录
	 * */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/UploadUpdateLicensePlateInfo", method = RequestMethod.POST)
	public void UploadUpdateLicensePlateInfo(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			PrintWriter out = response.getWriter();
			JSONObject resultJSON = updateLicensePlateService.uploadCorrectRecord(requestBody);
			out.print("\r\n" + resultJSON.toJSONString());// 返回json格式数据
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	

	/**
	 * 上传无牌车纠正记录
	 * */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/UploadUnlicensedCarEntranceInfo", method = RequestMethod.POST)
	public void UploadUnlicensedCarEntranceInfo(@RequestBody String requestBody,HttpServletRequest request,
			HttpServletResponse response){
		try {
			PrintWriter out = response.getWriter();
			JSONObject resultJSON = updateLicensePlateService.uploadCorrectUnlicensedRecord(requestBody);
			out.print("\r\n" + resultJSON.toJSONString());// 返回json格式数据
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
