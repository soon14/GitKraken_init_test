package cn.rf.hz.web.action.gd;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cn.rf.hz.web.bean.gd.CarParks;
import cn.rf.hz.web.service.gd.CarOrdersService;

public class FirstJobAction
{
	@Autowired(required = false)
	private CarOrdersService<CarParks> carOrdersService;
	/**
	 * app
	 * 
	 * @throws ParseException
	 */
	public void contextInitialized() throws ParseException
	{
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = carOrdersService.contextInitializedFind(map);

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		for (Map<String, Object> m : list)
		{
			String orderFormNo = m.get("orderFormNo") + "";
			String changeOrderTime = m.get("changeOrderTime") + "";
			String hourStr = dateSubtract(df.parse(changeOrderTime), new Date());
			int hour = Integer.parseInt(hourStr.substring(0, hourStr.indexOf("小")));
			
			if (hour >= 1)
			{
				map.put("orderFormNo", orderFormNo);
				carOrdersService.contextInitializedUpdate(map);
			}

		}

		// 蓝牙盒子
		list = carOrdersService.contextInitializedFindBox(map);

		for (Map<String, Object> m : list)
		{
			String orderFormNo = m.get("orderFormNo") + "";
			String changeOrderTime = m.get("changeOrderTime") + "";
			String hourStr = dateSubtract(df.parse(changeOrderTime), new Date());
			int hour = Integer.parseInt(hourStr.substring(0, hourStr.indexOf("小")));
			if (hour >= 1)
			{
				map.put("orderFormNo", orderFormNo);
				carOrdersService.contextInitializedUpdate(map);
			}

		}
	}
	
	
	public static String dateSubtract(Date d1, Date d2)
	{
		long diff = d2.getTime() - d1.getTime();// 这样得到的差值是微秒级别
		long days = diff / (1000 * 60 * 60 * 24);
		long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
		long hours1 = (days * 24) + hours;
		return hours1 + "小时" + minutes + "分钟";
	}
	
	
}
