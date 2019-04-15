package cn.rf.hz.web.cloudpark.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.alibaba.fastjson.JSONObject;



@Controller
public class ExchangeJsonController {
	 @RequestMapping("/base/json")  
	  void test(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response) {
		  	try {
				Map<String,String> map = new HashMap<String,String>(); 
				map.put("result", "content");  
				PrintWriter out = response.getWriter();       
				JSONObject resultJSON =new JSONObject();
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
	}
}
