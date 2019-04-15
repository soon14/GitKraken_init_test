package cn.rf.hz.web.cloudpark.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.cloudpark.daoxx.DevicestatusMapper;
import cn.rf.hz.web.cloudpark.moder.Devicestatus;
import cn.rf.hz.web.cloudpark.service.UploadDevStatusService;

@Service("uploaddevstatusservice")
public class UploadDevStatusServiceImp implements UploadDevStatusService {

	@Autowired
	DevicestatusMapper devicestatusMapper;
	
	@Override
	public String UploadDevStatusInfo(String requestBody) {
		// TODO Auto-generated method stub
		JSONObject result =new JSONObject();
		result.put("msgtype", "-1");
		result.put("msg", "cloudpark is error");
		try {
			JSONObject requestjson = JSON.parseObject(requestBody);
			List<Devicestatus> devicestatusList =new ArrayList<Devicestatus>();
			
			
			if(requestjson.getString("parkNo")==null || "".equals(requestjson.getString("parkNo"))){
				return null;
			}
			
			String parkNo= requestjson.getString("parkNo");
			if (requestjson.getString("padSn") != null && requestjson.getString("padStatus") != null) {

				Devicestatus paddev = new Devicestatus();
				paddev.setDevno(requestjson.getString("padSn"));
				paddev.setStatus(requestjson.getInteger("padStatus"));
				paddev.setDevtype(1);
				devicestatusList.add(paddev);
			}
			JSONArray devArry = requestjson.getJSONArray("devStatus");
			for (int i = 0; i < devArry.size(); i++) {
				JSONObject devObject =devArry.getJSONObject(i);
				String channelid = devObject.getString("channelId");
				JSONArray ledStatusArry= devObject.getJSONArray("ledStatus");
				JSONArray cameraStatusArry= devObject.getJSONArray("cameraStatus");
				if(ledStatusArry!=null  && ledStatusArry.size()>0){
					for(int index=0;index<ledStatusArry.size();index++){
						JSONObject ledobj =ledStatusArry.getJSONObject(index);
						Devicestatus leddev = new Devicestatus();
						leddev.setChannelid(channelid);
						leddev.setParkno(parkNo);
						leddev.setDevtype(2);//2是设备类型为led
						leddev.setDevno(ledobj.getString("sn"));
						leddev.setIp(ledobj.getString("ip"));
						leddev.setStatus(ledobj.getIntValue("status"));
						leddev.setUpdatetime(new Date());
						leddev.setCreatetime(new Date());
						devicestatusList.add(leddev);
						
					}
				}
				if(cameraStatusArry!=null  && cameraStatusArry.size()>0){
					for(int index=0;index<cameraStatusArry.size();index++){
						Devicestatus cameradev = new Devicestatus();
						JSONObject cameraobj =cameraStatusArry.getJSONObject(index);
						cameradev.setChannelid(channelid);
						cameradev.setParkno(parkNo);
						cameradev.setDevtype(3);//3是设备类型为camera
						cameradev.setDevno(cameraobj.getString("sn"));
						cameradev.setIp(cameraobj.getString("ip"));
						cameradev.setStatus(cameraobj.getIntValue("status"));
						cameradev.setUpdatetime(new Date());
						cameradev.setCreatetime(new Date());
						devicestatusList.add(cameradev);
					}
				}
					
			}
			System.out.println(JSON.toJSONString(devicestatusList));
			int count =devicestatusMapper.batchInsertdevicestatus(devicestatusList);
			result.put("msgtype", "0");
			result.put("msg", "success");
			System.out.println("count=="+count);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return JSON.toJSONString(result);
	}

}
