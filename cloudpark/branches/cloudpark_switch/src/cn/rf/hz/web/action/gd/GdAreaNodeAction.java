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
import cn.rf.hz.web.service.gd.GdAreaNodeService;
import cn.rf.hz.web.utils.WriterUtil;

import com.alibaba.fastjson.JSON;

/**
 * 节点信息
 * 
 * @author 程依寿 2015年10月22日 下午5:05:42
 */
@Controller
@RequestMapping("/gdAreaNode")
public class GdAreaNodeAction 
{
	@Autowired(required = false)
	private GdAreaNodeService gdAreaNodeService;
	private final static Logger LOG = Logger.getLogger(GdAreaNodeAction.class);

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/saveAreaNode", method = RequestMethod.POST)
	public void saveAreaNode(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		LOG.info("区域节点信息（老系统）：" + requestBody);
		Map<String, Object> map = new HashMap<String, Object>();
		String result = "{\"errorcode\":1}";
		int rowcount = 0;
		try
		{
			map = JSON.parseObject(requestBody);
			map.put("updTime", new Date());
			// AreaNode oldNode = gdAreaNodeService.findById(map);
			// if (oldNode == null)
			// rowcount = gdAreaNodeService.save(map);
			// else
			// {
			// map.put("recordId", oldNode.getRecordId());
			// rowcount = gdAreaNodeService.updateById(map);
			// }

			rowcount = gdAreaNodeService.save(map);

		} catch (Exception e)
		{
			if (e.getClass() == DuplicateKeyException.class)
			{
				rowcount = gdAreaNodeService.updateById(map);
			} else
				LOG.error("区域节点异常（老系统）：" + e);
		}

		if (rowcount > 0)
			result = "{\"errorcode\":0}";

		LOG.info("区域节点结果（老系统）：" + result);

		WriterUtil.writer(response, result);

	}

}
