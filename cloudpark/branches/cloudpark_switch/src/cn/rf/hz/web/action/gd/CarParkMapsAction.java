package cn.rf.hz.web.action.gd;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.bean.gd.CarParks;
import cn.rf.hz.web.service.gd.CarParkMapsService;
import cn.rf.hz.web.utils.WriterUtil;

import com.alibaba.fastjson.JSON;


@Controller
@RequestMapping("/carParkMaps")
public class CarParkMapsAction  
{
	private final static Logger LOG = Logger.getLogger(CarParkMapsAction.class);
	@Autowired(required = false)
	private CarParkMapsService<CarParks> carParkMapsService;
	
	  
	/**
	 * 
	 * 场内寻车
	 * 
	 * @param licensePlateNumber
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/findLicensePlateNumber", method = RequestMethod.GET)
	public void findLicensePlateNumber(String licensePlateNumber, HttpServletRequest request, HttpServletResponse response)
	{

		Map<String, Object> map = new HashMap<String, Object>();
		try{
			
			licensePlateNumber =  new String(licensePlateNumber.getBytes("iso-8859-1"), "UTF-8");
		}catch(Exception e)
		{
		}
		map.put("licensePlateNumber", licensePlateNumber);
		Map<String, Object> map1 = this.carParkMapsService.findLicensePlateNumber(map);
		
		Map<String, Object> map2 = null;
		
		if(map1 == null )
		{
			map2 = new HashMap<String, Object>();
			map2.put("errorcode", 1);
		}else
		{
			map.put("nodeId", map1.get("parkingId"));
			map.put("carParkId", map1.get("carParkId"));
			map2 = this.carParkMapsService.findCarInfo(map);
			map1.put("errorcode", 0);
			
			map2.putAll(map1);
			
		}
		
		WriterUtil.writer(response,JSON.toJSONString(map2));
	}
	
	
	/**
	 * 
	 * 
	 * @param carParkId
	 * @param area
	 * @param node
	 * @param request
	 * @param response
	 * @return
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/findSignatureMaps", method = RequestMethod.GET)
	public void findSignatureMaps(int carParkId,int area, int node, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("nodeId", node);
		map.put("carParkId", carParkId);
		Map<String, Object> map2 = this.carParkMapsService.findCarInfo(map);
		if(map2 == null)
		{
			map2 = new HashMap<String, Object>();
			map2.put("errorcode", 1);
		}
		else
		{
			map2.put("area", area+"");
			map2.put("nodeId", node+"");
			map2.put("errorcode", 0);
		}
		
		WriterUtil.writer(response,JSON.toJSONString(map2));
		
//		return forword("index", map2);
	}
	
	
	
	/**
	 * 测试
	 * 
	 * @param carParkId
	 * @param area
	 * @param node
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/findSignatureMaps1", method = RequestMethod.GET)
	public ModelAndView findSignatureMaps1(int carParkId,int area, int node, HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("nodeId", node);
		map.put("carParkId", carParkId);
		Map<String, Object> map2 = this.carParkMapsService.findCarInfo(map);
		map2.put("area", area+"");
		map2.put("nodeId", node+"");
//		WriterUtil.writer(response,JSON.toJSONString(map2));
		
//		return forword("index", map2);
		return null;
	}
	
	

	
	
	
	
	
	
}
