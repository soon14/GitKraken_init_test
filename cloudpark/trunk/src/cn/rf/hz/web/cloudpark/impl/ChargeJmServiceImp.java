package cn.rf.hz.web.cloudpark.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.cloudpark.daoxx.Tc_charge_jmMapper;
import cn.rf.hz.web.cloudpark.moder.Tc_charge_jm;
import cn.rf.hz.web.cloudpark.service.BoxInfoService;
import cn.rf.hz.web.cloudpark.service.Tc_chargejmService;

@Service("chargejminfo")
public class ChargeJmServiceImp implements Tc_chargejmService  {
	private final static Logger LOG = Logger.getLogger(ChargeJmServiceImp.class);
	@Autowired
	private Tc_charge_jmMapper chargejmMapper;
	
	@Override
	public JSONObject getChargeJmInfo(String requestBody) {
		// System.out.println("=================="+ParkingLotNo);
		JSONObject result = new JSONObject();

		result.put("sign", "");
		if (requestBody == null || requestBody.isEmpty()) {
			result.put("data", "[]");
			result.put("resCode", 100);
			result.put("resMsg", "The method parameter is null");
			LOG.error("getChargeJmInfo:The method parameter is null," + requestBody);
			return result;
		}

		try {
			JSONObject data = JSON.parseObject(requestBody);// 暂时未加密
			String parkinglotno = data.getString("parkinglotno");
			parkinglotno = parkinglotno.indexOf("_") >= 0 ? parkinglotno.substring(0, parkinglotno.indexOf("_"))
					: parkinglotno;
			List<Tc_charge_jm> list = chargejmMapper.selectByparkinglotno(parkinglotno);
			if (list != null && list.size() > 0) {
				JSONArray array = new JSONArray();
				for (Tc_charge_jm model : list) {
					JSONObject object = new JSONObject();
					object = JSONObject.parseObject(JSONObject.toJSONString(model));
					object.put("type", String.valueOf(model.getJmcode()));
					array.add(object);
				}

				result.put("data", array);
				result.put("parkNo", parkinglotno);
				result.put("resCode", 0);
				result.put("resMsg", "Succeed to obtain chargejm information");
				LOG.info("getChargeJmInfo:Succeed to obtain chargejm information," + result.toJSONString());
			} else {
				result.put("data", new JSONArray());
				result.put("parkNo", parkinglotno);
				result.put("resCode", 0);
				result.put("resMsg", "Succeed to obtain chargejm information,but the information is null");
				LOG.info("ggetChargeJmInfo:failed to obtain chargejm information," + result.toJSONString());
			}

		} catch (Exception e) {
			result.put("data", new JSONArray());
			result.put("parkNo", "");
			result.put("resCode", 2);
			result.put("resMsg", "An exception occurs for chargejm information");
			LOG.error("getChargeJmInfo:An exception occurs for chargejm information," + e.toString());
		}

		return result;
	}
}
