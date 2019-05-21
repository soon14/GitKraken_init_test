package cn.rf.hz.web.cloudpark.controller;

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

import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.cloudpark.service.UploadOpenWayInfoService;

@Controller
public class UploadOpenWayInfoController {

	@Autowired
	UploadOpenWayInfoService uploadOpenWayInfoService;
	private static final Logger logger = LoggerFactory.getLogger(UploadOpenWayInfoController.class);

	/**
	 * 上传开闸记录
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/UploadOpenWayInfo", method = RequestMethod.POST)
	public void UploadOpenWayInfo(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			logger.info("===============welcome UploadOpenWayInfo========================");
			PrintWriter out = response.getWriter();
			JSONObject resultJSON = uploadOpenWayInfoService.uploadOpenWayRecord(requestBody);
			out.print("\r\n" + resultJSON.toJSONString());// 返回json格式数据
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
