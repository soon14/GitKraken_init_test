package cn.rf.hz.web.cloudpark.controller;

import java.io.IOException;
import java.util.List;

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
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.reformer.cloudpark.service.ParkOutInService;
import com.reformer.cloudpark.service.QueryBlackWhite;

import cn.rf.hz.web.action.gd.GdCarParkBoxAction;
import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.cloudpark.impl.CarInoutServiceImp;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm;
import cn.rf.hz.web.cloudpark.moder.Tc_whiteuserinfo;
import cn.rf.hz.web.cloudpark.service.SaveCainToCacheService;
import cn.rf.hz.web.cloudpark.service.SaveCarOutCacheService;
import cn.rf.hz.web.service.gd.GdCarParkBoxService;
import cn.rf.hz.web.utils.json.JSONUtils;

@Controller
@RequestMapping("/carOutIn")
public class ParkOutInController {

	private static final Logger logger = LoggerFactory.getLogger(ParkOutInController.class);
	
	@Autowired(required = false)
	private ParkOutInService parkOutInService;
	
	
	@Autowired(required = false)
	private QueryBlackWhite queryBlackWhite;
	
	@Autowired(required = false)
	private CarInoutServiceImp carinoutservice;
	
	@Autowired(required = false)
	private SaveCainToCacheService saveCainToCacheService;
	
	
	
	@Autowired(required = false)
	private SaveCarOutCacheService saveouttocacheserviceimp;
	
	/**
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@ResponseBody
	@RequestMapping(value = "/saveCarParkIn", method = RequestMethod.POST)
	public void saveCarParkIn(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
/*		logger.info("===========WELCOME==============");
		logger.info("===========requestBody=============="+requestBody);
		String remsg=parkOutInService.saveParkIn(requestBody);
		
		
		
		String 	result=carinoutservice.saveCarin(requestBody);
		//response.getWriter().write(result);
		logger.info("===========remsg=============="+remsg);
		try {
			response.getWriter().write(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	long startTime=System.currentTimeMillis();
    	long endTime=System.currentTimeMillis();
    	long excTime=(endTime-startTime);*/
		
		long startTime=System.currentTimeMillis();
		logger.info("===========welcomeSaveCarin2==============");
		logger.info("===========welcomeSaveCarinrequestBody2=============="+requestBody);
		String 	result=saveCainToCacheService.saveCarinredis(requestBody);
		long endTime=System.currentTimeMillis();
    	long excTime=(endTime-startTime);
    	//if(excTime>180){
    		logger.info("===========上传入场记录返回盒子时间=============="+excTime);
    		logger.info("===========result=============="+result);
    	//}
		//response.getWriter().write(result);
	}
	
	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@ResponseBody
	@RequestMapping(value = "/queryParkRecord", method = RequestMethod.POST)
	public void queryParkRecord(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{	
		parkOutInService.queryCarInOutRecord(requestBody);

	}
	
	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@ResponseBody
	@RequestMapping(value = "/saveCarParkOut", method = RequestMethod.POST)
	public void saveCarParkOut(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		
		logger.info("=========== welcome saveCarParkOut==============");
		logger.info("=========== requestBody=============="+requestBody);
	//	String returnmsg=parkOutInService.saveParkOut(requestBody);
		String returnmsg=saveouttocacheserviceimp.saveCarOutredis(requestBody);
		try {
			response.getWriter().write(returnmsg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@ResponseBody
	@RequestMapping(value = "/ParkState", method = RequestMethod.POST)
	public void queryparkState(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		JSONObject mapparam = JSON.parseObject(requestBody);
		JSONArray arry=parkOutInService.queryParkState(mapparam);
		logger.info("===========arryarryarryarry=============="+arry.toJSONString());
		String parkStateInformation = JSON.toJSONString(arry, SerializerFeature.WriteDateUseDateFormat);
		//String myuserjsondateformat = JSON.toJSONStringWithDateFormat(arry, "yyyy-MM-dd HH:mm:ss", SerializerFeature.WriteDateUseDateFormat);
		//logger.info("===========parkStateInformation=============="+parkStateInformation);
		//logger.info("===========myuserjsondateformat=============="+myuserjsondateformat);
		try {
			response.getWriter().write(parkStateInformation);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@ResponseBody
	@RequestMapping(value = "/queryChargerecordinfo", method = RequestMethod.POST)
	public void querychargepay(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		JSONObject mapparam = JSON.parseObject(requestBody);
		JSONArray arry=parkOutInService.queryChargerecordinfo(mapparam);
	//	logger.info("===========arryarryarryarry=============="+arry.toJSONString());
		String myuserjson = JSON.toJSONString(arry, SerializerFeature.WriteDateUseDateFormat);
		
		//String myuserjsondateformat = JSON.toJSONStringWithDateFormat(arry, "yyyy-MM-dd HH:mm:ss", SerializerFeature.WriteDateUseDateFormat);
		//logger.info("===========myuserjson=============="+myuserjson);
		logger.info("===========myuserjsondateformat=============="+myuserjson);
		try {
			response.getWriter().write(myuserjson);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@ResponseBody
	@RequestMapping(value = "/queryBlackWhite", method = RequestMethod.POST)
	public void queryBlackWhite(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		String parkingid = "196423";
		List<Object> list=queryBlackWhite.queryBlackWhite(parkingid);
		String result=JSONUtils.toJSONString(list);
		try {
			response.getWriter().write(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@ResponseBody
	@RequestMapping(value = "/SaveCarin", method = RequestMethod.POST)
	public void queryBlackWhite1(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
/*		logger.info("===========welcomeSaveCarin==============");
		logger.info("===========welcomeSaveCarinrequestBody=============="+requestBody);
		String 	result=carinoutservice.saveCarin(requestBody);
		response.getWriter().write(result);*/
		long startTime=System.currentTimeMillis();
		logger.info("===========welcomeSaveCarin2==============");
		logger.info("===========welcomeSaveCarinrequestBody2=============="+requestBody);
		String 	result=saveCainToCacheService.saveCarinredis(requestBody);
		long endTime=System.currentTimeMillis();
    	long excTime=(endTime-startTime);
    	//if(excTime>180){
    		logger.info("===========上传入场记录返回盒子时间=============="+excTime);
    		logger.info("===========result=============="+result);
    	//}
		response.getWriter().write(result);
	}
	
	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@ResponseBody
	@RequestMapping(value = "/SaveCarin2", method = RequestMethod.POST)
	public void SaveCarin(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		long startTime=System.currentTimeMillis();
		logger.info("===========welcomeSaveCarin2==============");
		logger.info("===========welcomeSaveCarinrequestBody2=============="+requestBody);
		String 	result=saveCainToCacheService.saveCarinredis(requestBody);
		long endTime=System.currentTimeMillis();
    	long excTime=(endTime-startTime);
    	//if(excTime>180){
    		logger.info("===========上传入场记录返回盒子时间=============="+excTime);
    		logger.info("===========result=============="+result);
    	//}
		response.getWriter().write(result);
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Auth(verifyLogin = false, verifyURL = false)
	@ResponseBody
	@RequestMapping(value = "/updatecarin", method = RequestMethod.POST)
	public void updatecarin(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		long startTime=System.currentTimeMillis();

		logger.info("===========updatecarinupdatecarin=============="+requestBody);
		String 	result=carinoutservice.updateCarin(requestBody);
		long endTime=System.currentTimeMillis();
    	long excTime=(endTime-startTime);
    	//if(excTime>180){
    		logger.info("===========上传入场记录返回盒子时间=============="+excTime);
    		logger.info("===========result=============="+result);
    	//}
		response.getWriter().write(result);
	}

}
