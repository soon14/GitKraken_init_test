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
import cn.rf.hz.web.service.gd.GdAreaService;
import cn.rf.hz.web.utils.WriterUtil;

import com.alibaba.fastjson.JSON;

/**
 * 区域
 * 
 * @author 程依寿 2015年10月22日 下午5:02:35
 */
@Controller
@RequestMapping("/gdArea")
public class GdAreaAction 
{
	private final static Logger LOG = Logger.getLogger(GdAreaAction.class);
	@Autowired(required = false)
	private GdAreaService gdAreaService;

	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/saveArea", method = RequestMethod.POST)
	public void saveArea(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		LOG.info("区域信息（老系统）：" + requestBody);
		Map<String, Object> map = new HashMap<String, Object>();

		String result = "{\"errorcode\":1}";
		int rowcount = 0;
		try
		{
			map = JSON.parseObject(requestBody);
			map.put("updTime", new Date());
//			Area oldArea = gdAreaService.findById(map);
//			if (oldArea == null)
				rowcount = gdAreaService.save(map);
//			else
//			{
//				map.put("recordId", oldArea.getRecordId());
//				rowcount = gdAreaService.updateBySelective(map);
//			}

		} catch (Exception e)
		{
			if (e.getClass() == DuplicateKeyException.class)
			{
				rowcount = gdAreaService.updateBySelective(map);
			} else
				LOG.error("车位异常（老系统）：" + e);
		}

		if (rowcount > 0)
			result = "{\"errorcode\":0}";

		LOG.info("区域结果（老系统）：" + result);

		WriterUtil.writer(response, result);

	}


}
