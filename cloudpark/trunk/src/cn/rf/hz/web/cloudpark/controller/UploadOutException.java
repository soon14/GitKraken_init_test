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

import com.reformer.cloudpark.service.ParkOutInService;

import cn.rf.hz.web.cloudpark.service.SaveCarChargeCacheService;

@Controller
@RequestMapping("/upload")
public class UploadOutException {
	private static final Logger logger = LoggerFactory.getLogger(UploadOutException.class);
	@Autowired(required = false)
	private ParkOutInService parkOutInService;
	
	@Autowired(required = false)
	private SaveCarChargeCacheService savechaegeToCacheService;
	
	@RequestMapping(value = "/uploadExecptionOut", method = RequestMethod.POST)
	public void uploadExecptionOut(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("======================WELCOME  uploadExecptionOut=========================" + requestBody);
		String result = parkOutInService.uploadExecptionOut(requestBody);
		logger.info("========result========" + result + "=================");
		try {
			response.getWriter().write(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/DeleteAnomalyOutInfo", method = RequestMethod.POST)
	public void DeleteAnomalyOutInfo(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("======================WELCOME DeleteAnomalyOutInfo=========================");
		logger.info("======================requestBody====" + requestBody);
		String result = parkOutInService.DelBoxPayRecord(requestBody);
		logger.info("========result========" + result + "=================");
		try {
			response.getWriter().write(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/uploadBoxPayRecord", method = RequestMethod.POST)
	public void uploadBoxPayRecord(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		long startTime=System.currentTimeMillis();
		logger.info("======================WELCOME uploadBoxPayRecord=========================");
		logger.info("======================requestBody=========================" + requestBody + "============");
		//String result = parkOutInService.uploadBoxPayRecord(requestBody);
		String result="";
		try {
			result = savechaegeToCacheService.saveCarChargeredis(requestBody);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			long endTime=System.currentTimeMillis();
	    	long excTime=(endTime-startTime);
			logger.info("======================response=========================" + result + "============");
			logger.info("======================responseuploadBoxPayRecordtime=========================" + excTime + "============");
			response.getWriter().write(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
