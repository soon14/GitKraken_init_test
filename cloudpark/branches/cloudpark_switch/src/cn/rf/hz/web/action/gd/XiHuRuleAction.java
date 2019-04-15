package cn.rf.hz.web.action.gd;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.service.gd.XiHuRuleService;
import cn.rf.hz.web.utils.RequestUtil;
import cn.rf.hz.web.utils.URLUtils;
import cn.rf.hz.web.utils.WriterUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 西湖减免规则
 * 
 * @author 程依寿 2015年11月4日 下午3:29:44
 */
@Controller
@RequestMapping("/xiHuRuleAction")
public class XiHuRuleAction 
{
	private final static Logger LOG = Logger.getLogger(XiHuRuleAction.class);
	@Autowired(required = false)
	private XiHuRuleService xiHuRuleService;

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/updateXiHuRule", method = RequestMethod.POST)
	public void updateXiHuRule(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{

		LOG.info("西湖减免规则:" + requestBody);
		
		int rowcount = 1;
		try{
			
			JSONObject tollrecords = JSON
					.parseObject(RequestUtil.sendPost(URLUtils.tollrecord + "/mall/vipshoprule/update",requestBody));
			
			rowcount= tollrecords.getInteger("errorcode");
		}catch(Exception e){
			
			LOG.info("西湖减免规则(调用林锋接口异常):" + e);
		}
		
		/*
		
		
		JSONObject m = JSON.parseObject(requestBody, JSONObject.class);

		Integer carParkId = (Integer) m.get("carParkId");
		JSONArray content = JSON.parseArray(((String) m.get("content")));
		int rowcount = 0;
		// 停车场不能为空
		if (carParkId != null)
		{
			// 清空vip 会员
			xiHuRuleService.delXiHuRuleVip(carParkId);
			// 清空商场
			xiHuRuleService.delXiHuRuleShopping(carParkId);

			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < content.size(); i++)
			{
				Map<String, Object> map = content.getJSONObject(i);
				map.put("carParkId", carParkId);
				if (((String) map.get("type")).equalsIgnoreCase("vip"))
				{
					// VIP会员减免规则
					rowcount = xiHuRuleService.insertXiHuRuleVip(map);
				} else if (((String) map.get("type")).equalsIgnoreCase("shopping"))
				{
					// 购物减免规则
					rowcount = xiHuRuleService.insertXiHuRuleShopping(map);
				}
			}
		} else
			rowcount = 0;
		*/

//		if (rowcount > 0)
//			WriterUtil.writer(response, "{\"errorcode\":0}");
//		else
//			WriterUtil.writer(response, "{\"errorcode\":1}");
		WriterUtil.writer(response, "{\"errorcode\":"+rowcount+"}");

	}
}
