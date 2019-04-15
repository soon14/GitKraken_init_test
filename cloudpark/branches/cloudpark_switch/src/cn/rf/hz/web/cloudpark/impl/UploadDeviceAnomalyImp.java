package cn.rf.hz.web.cloudpark.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.cloudpark.daoxx.Pb_stationMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_systemalarmMapper;
import cn.rf.hz.web.cloudpark.moder.Pb_station;
import cn.rf.hz.web.cloudpark.moder.Tc_systemalarm;
import cn.rf.hz.web.cloudpark.service.UploadDeviceAnomalyService;

@Service("uploadDeviceAnomalyService")
public class UploadDeviceAnomalyImp implements UploadDeviceAnomalyService {
	@Autowired
	Pb_stationMapper pb_stationMapper;
	@Autowired
	Tc_systemalarmMapper systemalarmMapper;

	/**
	 * 上传设备报警记录
	 */
	@Override
	public JSONObject uploadDeviceAnomalyRecord(String requestBody) {
		// TODO Auto-generated method stub
		// 返回结果
		JSONObject resultJSON = new JSONObject();
		System.out.println(requestBody);
		JSONObject data = JSON.parseObject(requestBody);
		System.out.println(data);
		// String sign = data.getString("sign");
		// 停车场编号
		String parkNo = data.getString("parkNo");
		JSONArray dataArrays = data.getJSONArray("data");
		// 设备报警记录集合
		List<Tc_systemalarm> systemalarmList = new ArrayList<Tc_systemalarm>();
		if (dataArrays != null) {
			for (int i = 0; i < dataArrays.size(); i++) {
				JSONObject object = dataArrays.getJSONObject(i);
				String serialNumber = object.getString("serialNumber");
				Integer alarmType = object.getInteger("alarmType");
				String remarks1 = object.getString("remarks1");
				Date alarmDate = object.getDate("eventTime");

				// 从Box设备表中取数据
				int cid = 0;
				Pb_station pb_station = pb_stationMapper.selectBySerialNumber(serialNumber);
				if (pb_station != null && pb_station.getChannelid() > 0) {
					cid = pb_station.getChannelid();
				}

				Tc_systemalarm systemalarm = new Tc_systemalarm();
				systemalarm.setChannleid(cid);
				systemalarm.setAlarmtype(alarmType);
				systemalarm.setAlarmdesc(remarks1);
				systemalarm.setAlarmdate(alarmDate);
				systemalarm.setIsupload(true);
				systemalarm.setParkinglotno(parkNo);
				systemalarmList.add(systemalarm);
			}
		}
		if (systemalarmList.isEmpty()) {
			// 返回错误提示
			resultJSON.put("parkNo", parkNo);
			resultJSON.put("sign", "");
			resultJSON.put("resCode", 2);
			resultJSON.put("resMsg", "处理上传设备报警信息失败");
		} else {
			try {
				// 批量上传设备报警记录
				systemalarmMapper.batchInsertSystemalarm(systemalarmList);

				resultJSON.put("parkNo", parkNo);
				resultJSON.put("sign", "");
				resultJSON.put("resCode", 0);
				resultJSON.put("resMsg", "处理上传设备报警信息成功");
			} catch (Exception e) {
				e.printStackTrace();
				resultJSON.put("parkNo", parkNo);
				resultJSON.put("sign", "");
				resultJSON.put("resCode", 2);
				resultJSON.put("resMsg", "处理上传设备报警信息失败");
			}
		}

		return resultJSON;
	}
}
