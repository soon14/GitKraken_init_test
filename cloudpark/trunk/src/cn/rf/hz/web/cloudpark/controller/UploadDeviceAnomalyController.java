package cn.rf.hz.web.cloudpark.controller;

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
import cn.rf.hz.web.cloudpark.service.UploadDeviceAnomalyService;

@Controller
public class UploadDeviceAnomalyController {

	@Autowired
	UploadDeviceAnomalyService uploadDeviceAnomalyService;

	/**
	 * 上传设备记录
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/UploadDeviceAnomalyInfo", method = RequestMethod.POST)
	public void UploadDeviceAnomalyInfo(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			PrintWriter out = response.getWriter();
			JSONObject resultJSON = uploadDeviceAnomalyService.uploadDeviceAnomalyRecord(requestBody);
			out.print("\r\n" + resultJSON.toJSONString());// 返回json格式数据
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
