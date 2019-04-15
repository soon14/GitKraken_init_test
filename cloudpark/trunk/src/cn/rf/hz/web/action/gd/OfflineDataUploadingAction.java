package cn.rf.hz.web.action.gd;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.service.gd.OfflineDataUploadingService;
import cn.rf.hz.web.utils.WriterUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 线下数据上传
 * 
 * @author 程依寿 2015年11月27日 上午9:08:05
 */
@Controller
@RequestMapping("/offlineDataUploadingAction")
public class OfflineDataUploadingAction
{

	private final static Logger LOG = Logger.getLogger(OfflineDataUploadingAction.class);

	@Autowired
	private OfflineDataUploadingService offlineDataUploadingService;

	/**
	 * 
	 * 4.9上传异常出场记录数据 使用对象：线下系统 通过该接口上报异常出场信息到智能停车云端
	 * 
	 * @param requestBody
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/uploadAnomalyOutRecords", method = RequestMethod.POST)
	public void uploadAnomalyOutRecords(@RequestBody String requestBody, HttpServletResponse response)
	{

		LOG.info("4.8上传异常出场记录数据:" + requestBody);

		JSONObject map = JSON.parseObject(requestBody);

		JSONObject resultData = new JSONObject();
		resultData.put("count", map.getInteger("count"));

		if (map.getInteger("count") != 0)
		{

			JSONArray ja = map.getJSONArray("data");
			JSONArray ja1 = new JSONArray();
			JSONArray ja2 = new JSONArray();
			for (int i = 0; i < map.getInteger("count"); i++)
			{
				JSONObject jo = ja.getJSONObject(i);
				JSONObject jo1 = new JSONObject();

				jo.put("parkId", map.getString("parkId"));

				jo1.put("licensePlateNumber", jo.get("licensePlateNumber"));
				jo1.put("record_id", jo.get("record_id"));
				jo1.put("result", 0);

				ja1.add(jo);
				ja2.add(jo1);
			}
			try
			{
				this.offlineDataUploadingService.uploadAnomalyOutRecordsPiliang(ja1);

			} catch (Exception e)
			{
				e.printStackTrace();
				ja2 = new JSONArray();

				int rownum = 1;
				for (int i = 0; i < map.getInteger("count"); i++)
				{
					JSONObject jo = ja.getJSONObject(i);
					JSONObject jo1 = new JSONObject();

					// try{
					//
					// rownum =
					// this.offlineDataUploadingService.uploadAnomalyOutRecords(jo);
					//
					// }catch(Exception e1)
					// {
					// jo1.put("errorcode", e.getMessage());
					// }

					jo.put("parkId", map.getString("parkId"));

					jo1.put("licensePlateNumber", jo.get("licensePlateNumber"));
					jo1.put("record_id", jo.get("record_id"));
					jo1.put("result", 1);

					ja2.add(jo1);

					resultData.put("errorcode", e.getMessage());
					LOG.error("4.8上传异常出场记录数据:" + e.getMessage());
				}

			}

			resultData.put("data", ja2);
		}

		JSONObject resultData1 = new JSONObject();
		resultData1.put("AnomayOutInfo", resultData);
		WriterUtil.writer(response, resultData1.toJSONString());
	}

	/**
	 * 
	 * 4.9上传设备报警记录数据 使用对象：线下系统 通过该接口上报异常出场信息到智能停车云端
	 * 
	 * @param requestBody
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/uploadDeviceAnomalyRecords", method = RequestMethod.POST)
	public void uploadDeviceAnomalyRecords(@RequestBody String requestBody, HttpServletResponse response)
	{

		LOG.info("4.9上传设备报警记录数据:" + requestBody);

		JSONObject map = JSON.parseObject(requestBody);

		JSONObject resultData = new JSONObject();
		resultData.put("count", map.getInteger("count"));

		if (map.getInteger("count") != 0)
		{

			JSONArray ja = map.getJSONArray("data");
			JSONArray ja1 = new JSONArray();
			JSONArray ja2 = new JSONArray();
			for (int i = 0; i < map.getInteger("count"); i++)
			{
				JSONObject jo = ja.getJSONObject(i);
				JSONObject jo1 = new JSONObject();

				jo.put("parkId", map.getString("parkId"));

				jo1.put("deviceID", jo.get("deviceID"));
				jo1.put("record_id", jo.get("record_id"));
				jo1.put("result", 0);

				ja1.add(jo);
				ja2.add(jo1);
			}
			try
			{
				this.offlineDataUploadingService.uploadDeviceAnomalyRecordsPiLiang(ja1);

			} catch (Exception e)
			{
				ja2 = new JSONArray();
				for (int i = 0; i < map.getInteger("count"); i++)
				{
					JSONObject jo = ja.getJSONObject(i);
					JSONObject jo1 = new JSONObject();

					jo.put("parkId", map.getString("parkId"));

					jo1.put("licensePlateNumber", jo.get("licensePlateNumber"));
					jo1.put("record_id", jo.get("record_id"));
					jo1.put("result", 1);

					ja2.add(jo1);
				}
				resultData.put("errorcode", e.getMessage());
				LOG.error("4.9上传设备报警记录数据:" + e.getMessage());
			}

			resultData.put("data", ja2);
		}

		JSONObject resultData1 = new JSONObject();
		resultData1.put("DeviceAnomalyInfo", resultData);
		WriterUtil.writer(response, resultData1.toJSONString());
	}

	/**
	 * 4.10上传设备开闸记录数据
	 * 
	 * 使用对象：线下系统 通过该接口上报设备开闸信息到智能停车云端
	 * 
	 * @param requestBody
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/uploadOpenWayRecords", method = RequestMethod.POST)
	public void uploadOpenWayRecords(@RequestBody String requestBody, HttpServletResponse response)
	{

		LOG.info("4.10上传设备开闸记录数据:" + requestBody);

		JSONObject map = JSON.parseObject(requestBody);

		JSONObject resultData = new JSONObject();
		resultData.put("count", map.getInteger("count"));

		if (map.getInteger("count") != 0)
		{

			JSONArray ja = map.getJSONArray("data");
			JSONArray ja1 = new JSONArray();
			JSONArray ja2 = new JSONArray();
			for (int i = 0; i < map.getInteger("count"); i++)
			{
				JSONObject jo = ja.getJSONObject(i);
				JSONObject jo1 = new JSONObject();

				jo.put("parkId", map.getString("parkId"));

				jo1.put("deviceID", jo.get("deviceID"));
				jo1.put("record_id", jo.get("record_id"));
				jo1.put("result", 0);

				ja1.add(jo);
				ja2.add(jo1);
			}
			try
			{
				this.offlineDataUploadingService.uploadOpenWayRecordsPiLiang(ja1);

			} catch (Exception e)
			{
				ja2 = new JSONArray();
				for (int i = 0; i < map.getInteger("count"); i++)
				{
					JSONObject jo = ja.getJSONObject(i);
					JSONObject jo1 = new JSONObject();

					jo.put("parkId", map.getString("parkId"));

					jo1.put("licensePlateNumber", jo.get("licensePlateNumber"));
					jo1.put("record_id", jo.get("record_id"));
					jo1.put("result", 1);

					ja2.add(jo1);
				}
				resultData.put("errorcode", e.getMessage());
				LOG.error("4.10上传设备开闸记录数据:" + e.getMessage());
			}

			resultData.put("data", ja2);

		}

		JSONObject resultData1 = new JSONObject();
		resultData1.put("OpenWayInfo", resultData);
		WriterUtil.writer(response, resultData1.toJSONString());
	}

}
