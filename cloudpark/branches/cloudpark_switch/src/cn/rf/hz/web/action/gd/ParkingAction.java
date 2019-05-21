package cn.rf.hz.web.action.gd;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.service.gd.ParkingService;
import cn.rf.hz.web.utils.WriterUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("/parkingAction")
public class ParkingAction
{
	private final static Logger LOG = Logger.getLogger(ParkingAction.class);
	@Autowired(required = false)
	private ParkingService parkingService;

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/saveParking", method = RequestMethod.POST)
	public void saveArea(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		LOG.info("车位信息（老系统）：" + requestBody);

		String result = "{\"errorcode\":1}";
		int rowcount = 0;
		JSONObject map = JSON.parseObject(requestBody);
		try
		{

			// JSONObject parKing = parkingService.queryById(map);
			// if (parKing == null)
			// rowcount = parkingService.save(map);
			// else
			// {
			// map.put("recordId", parKing.getInteger("recordId"));
			// rowcount = parkingService.update(map);
			// }

			 parkingService.delete(map.getInteger("carParkId"));
			 rowcount = parkingService.savePiliang(map.getJSONArray("data"));
//			rowcount = parkingService.save(map);

		} catch (Exception e)
		{
//			if (e.getClass() == DuplicateKeyException.class)
//			{
//				rowcount = parkingService.update(map);
//			} else
//				LOG.error("车位异常（老系统）：" + e);
			
			LOG.error("车位异常（老系统）：" + e);
		}

		if (rowcount > 0)
			result = "{\"errorcode\":0}";

		LOG.info("车位结果（老系统）：" + result);

		WriterUtil.writer(response, result);

	}

	
	
	
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/test", method = RequestMethod.POST)
	public void test(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		System.out.println(requestBody);
		String str = "{\"errorcode\":0,\"errormsg\":\"asfsafsdf\",\"data\":[    {        \"carParkName\": \"杭州美莱大厦停车场\",        \"companyName\": \"杭州美仁物业管理有限公司\",        \"latitude\": 30.281483,        \"longitude\": 120.149113,        \"parkLocation\": \"杭州市西湖区莫干山路333号\",        \"parkPayType\": 1,        \"parkPrice\": 6,        \"parkingEmptyNum\": \"22\",        \"parkingTotalNum\": \"46\",        \"recordId\": 495072,        \"type\": 3    }]}";
		JSONObject js = new JSONObject();
		js.put("errorcode", "");
		js.put("errormsg", "sdf");
		js.put("data", "sdfsdfsd");
		
		
		WriterUtil.writer(response, str);
	}
}
