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
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.cloudpark.service.Tc_userinfoService;

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.cloudpark.impl.KFrameServiceImp;
import cn.rf.hz.web.cloudpark.impl.SaveUserInfoToCacheServiceImp;
import cn.rf.hz.web.cloudpark.service.SendUserInfoMess;
import cn.rf.hz.web.utils.StringUtil;

@Controller
@RequestMapping("/getInfo")
public class UserInfoController {

	private static final Logger logger = LoggerFactory.getLogger(UserInfoController.class);

	@Autowired(required = false)
	public Tc_userinfoService userinfoservice;

	@Autowired(required = false)
	public SendUserInfoMess senduserinfomess;

	@Autowired(required = false)
	public KFrameServiceImp kframeService;

	@Autowired(required = false)
	public SaveUserInfoToCacheServiceImp saveuserinfotocacheserviceimp;

	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getUserInfo", method = RequestMethod.POST)
	public void getUserInfo(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter out = response.getWriter();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			// String result =
			// userinfoservice.selectByparkingNo(requestBody).toJSONString();
			JSONObject mapparam = JSON.parseObject(requestBody);
			String result = kframeService.userinfodAll(mapparam);
			logger.info("return=========" + result + "==========end");
			if (result != null && !result.isEmpty()) {
				out.println(result);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/uploadUserInfoIncrement", method = RequestMethod.POST)
	public void uploadUserInfoIncrement(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			logger.info("uploadUserInfoIncrement,RequestBody:" + requestBody + "");
			JSONObject result = new JSONObject();
			result = senduserinfomess.addUserInfomation2(requestBody);
			/*
			 * PrintWriter out = response.getWriter(); JSONObject result
			 * =userinfoservice.selectByparkingNo(requestBody);
			 * if(result!=null&&!result.isEmpty()) {
			 * out.println(result.toJSONString()); } out.flush(); out.close();
			 */

			logger.info("uploadUserInfoIncrement,return:" + result.toJSONString());
			PrintWriter out = response.getWriter();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			if (result != null) {
				out.println(result.toJSONString());
			}
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/uploadPlaceUserInfoIncrement", method = RequestMethod.POST)
	public void uploadPlaceUserInfoIncrement(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			logger.info("uploadPlaceUserInfoIncrement,RequestBody:" + requestBody + "");
			JSONObject result = new JSONObject();
			result = senduserinfomess.addUserInfomation3(requestBody);
			/*
			 * PrintWriter out = response.getWriter(); JSONObject result
			 * =userinfoservice.selectByparkingNo(requestBody);
			 * if(result!=null&&!result.isEmpty()) {
			 * out.println(result.toJSONString()); } out.flush(); out.close();
			 */

			logger.info("uploadPlaceUserInfoIncrement,return:" + result.toJSONString());
			PrintWriter out = response.getWriter();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			if (result != null) {
				out.println(result.toJSONString());
			}
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/uploadUserInfoForXb", method = RequestMethod.POST)
	public void uploadUserInfoForXb(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			logger.info("uploadUserInfoForXb:begin===========" + requestBody + "===============end");
			JSONObject result = new JSONObject();

			result = userinfoservice.uploadUserInfoForXb(requestBody);

			logger.info("return=========" + result.toJSONString() + "==========end");
			PrintWriter out = response.getWriter();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			if (result != null) {
				out.println(result.toJSONString());
			}
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getHashCode", method = RequestMethod.POST)
	public void getHashCode(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response) {
		try {
			logger.info("getHashCode:begin===========" + requestBody + "===============end");
			JSONObject result = new JSONObject();

			if (requestBody == null || requestBody.isEmpty()) {
				result.put("resCode", "100");
				result.put("resMsg", "the requestBody is null");
				result.put("resParam", null);
			} else {
				JSONObject object = JSON.parseObject(requestBody);
				String parkinglotno = object.getString("parkinglotno");
				if (parkinglotno != null && !parkinglotno.isEmpty()) {
					Integer hasecode = StringUtil.getAsciiCode(parkinglotno);
					result.put("resCode", "0");
					result.put("resMsg", "get HashCode success");
					result.put("resParam", hasecode);
				} else {
					result.put("resCode", "100");
					result.put("resMsg", "the parkinglotno is null");
					result.put("resParam", null);
				}
			}
			logger.info("return=========" + result.toJSONString() + "==========end");
			PrintWriter out = response.getWriter();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			if (result != null) {
				out.println(result.toJSONString());
			}
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getProductInfo", method = RequestMethod.POST)
	public void getProductInfo(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			logger.info("getProductInfo:begin===========" + requestBody + "===============end");
			JSONObject result = new JSONObject();
			result = userinfoservice.getProductInfo(requestBody);
			logger.info("getProductInfo:return=========" + result.toJSONString() + "==========end");
			PrintWriter out = response.getWriter();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			if (result != null) {
				out.println(result.toJSONString());
			}
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/syncUserInfoForApp", method = RequestMethod.POST)
	public void syncUserInfoForApp(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			logger.info("syncUserInfoForApp:begin===========" + requestBody + "===============end");
			JSONObject result = new JSONObject();
			result = userinfoservice.syncUserInfoForApp(requestBody);
			logger.info("syncUserInfoForApp,return=========" + result.toJSONString() + "==========end");
			PrintWriter out = response.getWriter();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			if (result != null) {
				out.println(result.toJSONString());
			}
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@ResponseBody
	@RequestMapping(value = "/getUserInfoCarIn", method = RequestMethod.POST)
	public void getUserInfoCarIn(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.info("===========getUserInfoCarIn,requestBody==============" + requestBody);
		JSONObject result = userinfoservice.getUserInfoCarIn(requestBody);
		response.getWriter().write(result.toJSONString());
	}
	
	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/getUserInfoByID", method = RequestMethod.POST)
	public void getUserInfoByID(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			PrintWriter out = response.getWriter();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			JSONObject result = userinfoservice.getUserInfoByID(requestBody);
			logger.info("return=========" + result + "==========end");
			if (result != null && !result.isEmpty()) {
				out.println(result); 
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@ResponseBody
	@RequestMapping(value = "/uploadDeptUserInfoIncrement", method = RequestMethod.POST)
	public void uploadDeptUserInfoIncrement(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("uploadDeptUserInfoIncrement,requestBody:" + requestBody);
		String returnmsg = saveuserinfotocacheserviceimp.saveUserInfoRedis(requestBody);
		try {
			response.getWriter().write(returnmsg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
