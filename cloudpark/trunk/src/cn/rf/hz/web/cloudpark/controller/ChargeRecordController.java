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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.cloudpark.service.ChargeRecordService;

@Controller
@RequestMapping("/ChargeRecord")
public class ChargeRecordController {

	@Autowired(required = false)
	private ChargeRecordService  chargeRecordService;
	
	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/saveChargeRecord", method = RequestMethod.POST)
	public void saveChargeRecord(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
	  	try {
			Map<String,Object> map = new HashMap<String,Object>(); 
			map.put("result", "content");  
			PrintWriter out = response.getWriter();       
			JSONObject resultJSON =new JSONObject();
			resultJSON.put("aa", "111");
			resultJSON.put("bb", map);
			String jsonpCallback = request.getParameter("jsonpCallback12");//客户端请求参数  
			if(StringUtils.isNotBlank(jsonpCallback)){
				out.println(jsonpCallback+"("+resultJSON.toJSONString()+")");//返回jsonp格式数据  
			}else{
				out.print("\r\n" + resultJSON.toJSONString());//返回json格式数据  
			}
		
			out.flush();  
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		//JSONObject map = JSON.parseObject(requestBody);
		//chargeRecordService.saveChargeRecord(map);
	}
}
