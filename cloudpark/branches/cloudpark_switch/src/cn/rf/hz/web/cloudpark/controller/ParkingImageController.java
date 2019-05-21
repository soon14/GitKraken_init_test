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
import cn.rf.hz.web.cloudpark.service.ParkingImageService;

@Controller
@RequestMapping("/getInfo")
public class ParkingImageController {
	@Autowired(required = false)
	private ParkingImageService parkingiamgeservice;

	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getSmallImageInfo", method = RequestMethod.POST)
	public void getSmallImageInfo(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		/*
		 * response.setCharacterEncoding("UTF-8");
		 * response.setContentType("text/html;charset=utf-8");
		 * response.setHeader("Content-type", "text/html;charset=UTF-8");
		 */
		try {
			PrintWriter out = response.getWriter();
			JSONObject result = new JSONObject();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			result = parkingiamgeservice.getSmallImageInfo(requestBody);
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

	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getAliyunImageUrl", method = RequestMethod.POST)
	public void getAliyunImageUrl(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		/*
		 * response.setCharacterEncoding("UTF-8");
		 * response.setContentType("text/html;charset=utf-8");
		 * response.setHeader("Content-type", "text/html;charset=UTF-8");
		 */
		try {
			PrintWriter out = response.getWriter();
			JSONObject result = new JSONObject();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			result = parkingiamgeservice.getAliyunImageUrl(requestBody);
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

	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/uploadImageToAliyun", method = RequestMethod.POST)
	public void uploadImageToAliyun(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			long startTime=System.currentTimeMillis();
			PrintWriter out = response.getWriter();
			JSONObject result = new JSONObject();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			result = parkingiamgeservice.uploadImageToAliyun(requestBody);
			long endTime=System.currentTimeMillis();
			System.out.println("====uploadImageToAliyunuploadImageToAliyun=====" + result.toJSONString());
			System.out.println("callbacktime uploadImageToAliyun" + (endTime-startTime)+"ms");
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

	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/batchGetSmallImagePath", method = RequestMethod.POST)
	public void batchGetSmallImagePath(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			PrintWriter out = response.getWriter();
			JSONObject result = new JSONObject();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			result = parkingiamgeservice.batchGetSmallImagePath(requestBody);
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
	
	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/uploadVoiceToAliyun", method = RequestMethod.POST)
	public void uploadVoiceToAliyun(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			long startTime=System.currentTimeMillis();
			PrintWriter out = response.getWriter();
			JSONObject result = new JSONObject();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			result = parkingiamgeservice.uploadVoiceToAliyun(requestBody);
			long endTime=System.currentTimeMillis();
			System.out.println("====uploadImageToAliyunuploadImageToAliyun=====" + result.toJSONString());
			System.out.println("callbacktime uploadImageToAliyun" + (endTime-startTime)+"ms");
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
