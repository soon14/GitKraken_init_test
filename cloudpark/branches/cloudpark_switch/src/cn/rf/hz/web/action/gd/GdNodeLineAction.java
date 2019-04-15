package cn.rf.hz.web.action.gd;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.service.gd.GdNodeLineService;
import cn.rf.hz.web.utils.WriterUtil;

import com.alibaba.fastjson.JSON;

/**
 * 节点线表
 * 
 * 
 * @author 程依寿 2015年10月22日 下午10:17:45
 */
@Controller
@RequestMapping("/gdNodeLine")
public class GdNodeLineAction 
{
	
	private final static Logger LOG = Logger.getLogger(GdNodeLineAction.class);
	
	
	@Autowired(required = false)
	public GdNodeLineService gdNodeLineService;

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/saveNodeLine", method = RequestMethod.POST)
	public void saveNodeLine(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		LOG.info("区域节点线信息（老系统）：" + requestBody);
		Map<String, Object> map = new HashMap<String, Object>();
		String result = "{\"errorcode\":1}";
		int rowcount = 0;
		try
		{
			map = JSON.parseObject(requestBody);
			map.put("updTime", new Date());
//			AreaNodeLine oldNodeLine = gdNodeLineService.findById(map);
//			if (oldNodeLine == null)
				rowcount = gdNodeLineService.save(map);
//			else
//			{
//				map.put("recordId", oldNodeLine.getRecordId());
//				rowcount = gdNodeLineService.updateBySelective(map);
//			}

		} catch (Exception e)
		{
			if (e.getClass() == DuplicateKeyException.class)
			{
				rowcount = gdNodeLineService.updateBySelective(map);
			} else
			LOG.error("区域节点线异常（老系统）：" + e);
		}

		if (rowcount > 0)
			result = "{\"errorcode\":0}";

		LOG.info("区域节点线结果（老系统）：" + result);

		WriterUtil.writer(response, result);
		
	}

}
