package cn.rf.hz.web.cloudpark.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.cloudpark.daoxx.Pb_operatorMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_parkInformationMapper;
import cn.rf.hz.web.cloudpark.moder.Pb_operator;
import cn.rf.hz.web.cloudpark.service.LoginInfoService;
import cn.rf.hz.web.utils.DateUtil;

@Service("logininfo")
public class LoginInfoServiceImp implements LoginInfoService {
	private final static Logger LOG = Logger.getLogger(LoginInfoServiceImp.class);
	@Autowired
	private Pb_operatorMapper operatorMapper;

	@Autowired
	private Tc_parkInformationMapper parkinfoMapper;

	@Override
	public JSONObject getLoginInfo(String requestBody) {
		// System.out.println("=================="+ParkingLotNo);
		System.out.println("===========getLoginInfo,requestBody:" + requestBody + "==================");
		System.out.println("===========getLoginInfo,begin:" + DateUtil.getNowTimeString() + "==================");

		JSONObject result = new JSONObject();
		JSONObject model = new JSONObject();
		result.put("sign", "");

		if (requestBody == null || requestBody.isEmpty()) {
			result.put("parkNo", new JSONArray());
			result.put("data", "");
			result.put("resCode", 100);
			result.put("resMsg", "The method parameter is null");
			return result;
		}
		try {
			JSONObject data = JSON.parseObject(requestBody);// 暂时未加密
			Map<String, Object> map = new HashMap<String, Object>();
			String ParkingLotNo = data.getString("parkNo");
			if (ParkingLotNo.indexOf("_") > -1) {
				ParkingLotNo = ParkingLotNo.split("_")[0];
			}
			map.put("parkinglotno", ParkingLotNo);
			String loginAccount = data.getString("loginAccount");
			map.put("loginaccounts", loginAccount);
			String loginPassword = data.getString("loginPassword");
			map.put("loginpassword", loginPassword);
			boolean isFlag = false;
			if (ParkingLotNo == null || ParkingLotNo.isEmpty()) {
				result.put("parkNo", "");
				result.put("data", new JSONArray());
				result.put("resCode", 100);
				result.put("resMsg", "the parkinglotno is null");
				return result;
			}
			if (loginAccount == null || loginAccount.isEmpty()) {
				result.put("parkNo", "");
				result.put("data", new JSONArray());
				result.put("resCode", 100);
				result.put("resMsg", "the loginaccount is null");
				return result;
			}
			if (loginPassword == null || loginPassword.isEmpty()) {
				result.put("parkNo", "");
				result.put("data", new JSONArray());
				result.put("resCode", 100);
				result.put("resMsg", "the loginpassword is null");
				return result;
			}
			result.put("parkNo", ParkingLotNo);

			Pb_operator operator = operatorMapper.selectByAccountAndPassword(map);
			if (operator != null && operator.getOperatorid() > 0) {
				model.put("operatorID", operator.getOperatorid());
				model.put("loginAccount", operator.getLoginaccounts());
				model.put("operatorName", operator.getOperatorname());
				model.put("sex", operator.getSex());
				model.put("mobilePhone", operator.getMobilephone());
				model.put("tel", operator.getTel());
				model.put("email", operator.getEmail());
				model.put("userState", operator.getState());
				model.put("remarks1", "");
				String parkinglotname = parkinfoMapper.queryparkingNameById(ParkingLotNo);
				model.put("parkingLotName", parkinglotname);

				result.put("data", model);
				result.put("resCode", 0);
				result.put("resMsg", "Succeed to obtain user information");
				LOG.error("getLoginInfo:Succeed to obtain user information," + requestBody);
			} else {
				result.put("data", new JSONArray());
				result.put("resCode", 1);
				result.put("resMsg", "failed to obtain user information");
				LOG.error("getLoginInfo:failed to obtain user information," + requestBody);
			}
		} catch (Exception e) {
			result.put("data", new JSONArray());
			result.put("resCode", 2);
			result.put("resMsg", "An exception occurs for user information");
			LOG.error("getLoginInfo:An exception occurs for user information," + e.toString());
		}

		System.out.println("===========getLoginInfo,begin:" + DateUtil.getNowTimeString() + "==================");
		return result;
	}
}
