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


import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.cloudpark.service.UploadDevStatusService;
import cn.rf.hz.web.cloudpark.service.UploadOpenWayInfoService;

@Controller
@RequestMapping("/DevStatus")
public class UploadDevStatusController {
	private static final Logger logger = LoggerFactory.getLogger(UploadDevStatusController.class);
	
	@Autowired
	UploadDevStatusService uploaddevstatusservice;

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/UploadDevStatusInfo", method = RequestMethod.POST)
	public void UploadDevStatusInfo(@RequestBody String requestBody,HttpServletRequest request,
			HttpServletResponse response){
			logger.info("===============welcome UploadDevStatusInfo========================");
			
			String res =uploaddevstatusservice.UploadDevStatusInfo(requestBody);
			PrintWriter out =null;
			try {
				 out = response.getWriter();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			out.print(res);
			out.flush();
			out.close();
	}	
}
