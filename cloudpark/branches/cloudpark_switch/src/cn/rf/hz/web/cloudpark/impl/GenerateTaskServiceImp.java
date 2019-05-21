package cn.rf.hz.web.cloudpark.impl;

import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.cloudpark.service.GenerateTaskService;
import com.reformer.cloudpark.service.ParkingInformation;
import com.reformer.cloudpark.service.Tc_userinfoService;
import com.reformer.datatunnel.client.DataTunnelPublishClient;

import cn.rf.hz.web.cloudpark.daoxx.Tc_orderUserMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tc_parkInformationMapper;
import cn.rf.hz.web.cloudpark.daoxx.Tr_taskMapper;
import cn.rf.hz.web.cloudpark.moder.ParkingLotParameter;
import cn.rf.hz.web.cloudpark.moder.Tc_parkingarea;
import cn.rf.hz.web.cloudpark.service.CarInfoForPadService;
import cn.rf.hz.web.cloudpark.service.Tc_crosspointService;
import cn.rf.hz.web.sharding.dao.Tc_chargerecordinfoMapper;
import cn.rf.hz.web.sharding.dao.Tc_usercrdtm_inMapper;
import cn.rf.hz.web.utils.BigDataAnalyze;
import cn.rf.hz.web.utils.DataConstants;
import cn.rf.hz.web.utils.JedisPoolUtils;
import cn.rf.hz.web.utils.LZ4Compress;
import cn.rf.hz.web.utils.ThreadLocalDateUtil;

@Service("generatetaskservice")
public class GenerateTaskServiceImp implements GenerateTaskService {
	private final static Logger LOG = Logger.getLogger(GenerateTaskServiceImp.class);

	@Autowired
	PublicParkingService publicparkingservice;

	@Autowired
	private Tc_usercrdtm_inMapper usercrdtm_inMapper;

	@Autowired
	private Tr_taskMapper taskMapper;

	@Autowired
	private Tc_orderUserMapper orderUserMapper;

	@Autowired
	Tc_chargerecordinfoMapper chargerecordinfoMapper;

	@Autowired
	private Tc_crosspointService crosspointService;

	@Autowired(required = false)
	public Tc_userinfoService userinfoservice;

	@Autowired(required = false)
	public SendMessServiceImp sendmessserviceimp;

	@Autowired
	private ParkingInformation parkingInformation;

	@Autowired
	private Tc_parkInformationMapper parkinginfoMapper;

	@Autowired
	CarInfoForPadService carInfoForPadService;

	@Autowired
	private DataTunnelPublishClient dataTunnelPublishClient;

	private static long fsize = 5000;

	@Override
	public boolean getAllK(final String parkingNO, final boolean isUpDate) {
		Thread t1 = new Thread() {
			public void run() {

				doktask(parkingNO, isUpDate);
			}
		};
		t1.start();

		/*
		 * if (isUpDate && returnstr) { return true; } else { return false; }
		 */
		return true;
	}

	public void doktask_bak(String parkingNO, boolean isUpDate) {

		long startTime = System.currentTimeMillis();
		boolean returnstr = true;
		try {
			JSONObject jso = new JSONObject();
			jso.put("ParkingLotNo", parkingNO);
			List<Object> list_in = this.usercrdtm_inMapper.queryTc_usercrdtm_in(jso);
			JSONArray array = new JSONArray(list_in);
			array = BigDataAnalyze.fixArray(array, "in");
			for (int i = 0; i < array.size(); i++) {
				JSONObject obj = array.getJSONObject(i);
				String key_out = DataConstants.History_Table_OUT;
				String key_in = DataConstants.History_Table_IN;

				if (JedisPoolUtils.exists(key_out)) {
					if (JedisPoolUtils.hexists(key_out, obj.getString("carcode"))) {
						String result = JedisPoolUtils.hget(key_out, obj.getString("carcode"));
						String[] re = result.split(",");
						long outtime = Long.valueOf(re[1]);
						if (outtime > obj.getDate("intime").getTime()) {
							array.remove(i);
							LOG.info("doktask_bak." + parkingNO + ".改车redis 最后一次出场时间都比场内的入场时间大,需要remove，改车的车牌是："
									+ obj.getString("carcode") + "============");
							publicparkingservice.moveToAbnormal(obj);
						}
					} else if (JedisPoolUtils.hexists(key_in, obj.getString("carcode"))) {
						String result = JedisPoolUtils.hget(key_in, obj.getString("carcode"));
						String[] re = result.split(",");
						long intime = Long.valueOf(re[1]);
						if (intime > obj.getDate("intime").getTime()) {
							array.remove(i);
							publicparkingservice.moveToAbnormal(obj);
							LOG.info("doktask_bak." + parkingNO + ".改车redis 最后一次进场时间都比场内的入场时间大,需要remove，改车的车牌是："
									+ obj.getString("carcode") + "============");
						}

					}

				}
			}
			String key_p = BigDataAnalyze.geListKeyByDataType(parkingNO, DataConstants.CLOUDPARK_INOUT, "p");
			long l = JedisPoolUtils.hincrBy(key_p, "autoincrsn", 1);
			if (true) {
				LOG.info("doktask_bak.CLOUDPARK_INOUT." + parkingNO + ".压缩前大小：" + array.toJSONString().getBytes().length
						+ "============");
				long beginsize = array.toJSONString().getBytes().length;
				byte[] dataB = array.toJSONString().getBytes();
				byte[] keyB = (DataConstants.KPRE + DataConstants.CLOUDPARK_INOUT).getBytes();
				byte[] filedB = parkingNO.getBytes();
				dataB = LZ4Compress.compress(dataB);
				LOG.info("doktask_bak.CLOUDPARK_INOUT." + parkingNO + ".压缩后大小：" + dataB.length + "============");
				JedisPoolUtils.hsetB(keyB, filedB, dataB);
				LOG.info("doktask_bak.CLOUDPARK_INOUT." + parkingNO + ".ALL DATA PUT IN " + DataConstants.KPRE
						+ DataConstants.CLOUDPARK_INOUT + ",FILED IS " + parkingNO);
				sendmessserviceimp.SendMess(new JSONArray(), dataTunnelPublishClient, l, "in_k", parkingNO,
						DataConstants.CLOUDPARK_INOUT, String.valueOf(beginsize));
			}
			// 判断是不是为场中场或者场内切换
			ParkingLotParameter pp = carInfoForPadService.getParkingParamInfo(parkingNO);
			boolean isInparkingOrSwitch = false;
			if (pp.isIsinparkingswitch() || pp.isIsinparkinglot()) {
				isInparkingOrSwitch = true;
			}
			if (isInparkingOrSwitch) {
				// 过车点全量包生成
				array = crosspointService.getCrosspointbyParkinglotsNo(parkingNO);
				key_p = BigDataAnalyze.geListKeyByDataType(parkingNO, DataConstants.CLOUDPARK_SWITCH, "p");
				l = JedisPoolUtils.hincrBy(key_p, "autoincrsn", 1);
				if (true) {
					LOG.info("doktask_bak.CLOUDPARK_SWITCH." + parkingNO + ".压缩前大小："
							+ array.toJSONString().getBytes().length);
					long beginsize = array.toJSONString().getBytes().length;
					byte[] dataB = array.toJSONString().getBytes();
					byte[] keyB = (DataConstants.KPRE + DataConstants.CLOUDPARK_SWITCH).getBytes();
					byte[] filedB = parkingNO.getBytes();
					dataB = LZ4Compress.compress(dataB);
					LOG.info("doktask_bak.CLOUDPARK_SWITCH." + parkingNO + ".压缩后大小：" + dataB.length);
					JedisPoolUtils.hsetB(keyB, filedB, dataB);
					LOG.info("doktask_bak.CLOUDPARK_SWITCH." + parkingNO + ".ALL DATA PUT IN " + DataConstants.KPRE
							+ DataConstants.CLOUDPARK_SWITCH + ",FILED IS " + parkingNO);
					sendmessserviceimp.SendMess(new JSONArray(), dataTunnelPublishClient, l, "in_k", parkingNO,
							DataConstants.CLOUDPARK_SWITCH, String.valueOf(beginsize));
				}
			}

			// 缴费记录全量包生成
			List<Object> list_charge = this.chargerecordinfoMapper.queryTotalAmountChargerecordinfo(jso);
			array = new JSONArray(list_charge);
			if (array != null) {
				JSONArray chargeArray = new JSONArray();
				for (int i = 0; i < array.size(); i++) {
					JSONObject json_charge = array.getJSONObject(i);
					String areaId = json_charge.getString("areaid");
					if (areaId != null && !areaId.isEmpty()) {
						Tc_parkingarea areaInfo = parkingInformation.getAreaInfo(areaId);
						json_charge.put("areaName", areaInfo.getAreaname());
					}
					chargeArray.add(json_charge);
				}
				array = chargeArray;
			}
			key_p = BigDataAnalyze.geListKeyByDataType(parkingNO, DataConstants.CLOUDPARK_CHARGE, "p");
			l = JedisPoolUtils.hincrBy(key_p, "autoincrsn", 1);
			if (true) {
				LOG.info("doktask_bak.CLOUDPARK_CHARGE." + parkingNO + ".压缩前大小："
						+ array.toJSONString().getBytes().length + "============");
				long beginsize = array.toJSONString().getBytes().length;
				byte[] dataB = array.toJSONString().getBytes();
				byte[] keyB = (DataConstants.KPRE + DataConstants.CLOUDPARK_CHARGE).getBytes();
				byte[] filedB = parkingNO.getBytes();
				dataB = LZ4Compress.compress(dataB);
				LOG.info("doktask_bak.CLOUDPARK_CHARGE." + parkingNO + ".压缩后大小：" + dataB.length + "============");
				JedisPoolUtils.hsetB(keyB, filedB, dataB);
				LOG.info("doktask_bak.CLOUDPARK_CHARGE." + parkingNO + ".ALL DATA PUT IN " + DataConstants.KPRE
						+ DataConstants.CLOUDPARK_CHARGE + ",FILED IS " + parkingNO);
				sendmessserviceimp.SendMess(new JSONArray(), dataTunnelPublishClient, l, "in_k", parkingNO,
						DataConstants.CLOUDPARK_CHARGE, String.valueOf(beginsize));

			}
			// 长期用户信息全量包生成
			array = userinfoservice.selectByparkingNo(jso.toJSONString());
			key_p = BigDataAnalyze.geListKeyByDataType(parkingNO, DataConstants.CLOUDPARK_USERGROUP, "p");
			l = JedisPoolUtils.hincrBy(key_p, "autoincrsn", 1);
			if (true) {
				LOG.info("doktask_bak.CLOUDPARK_USERGROUP." + parkingNO + ".压缩前大小："
						+ array.toJSONString().getBytes().length + "============");
				long beginsize = array.toJSONString().getBytes().length;
				byte[] dataB = array.toJSONString().getBytes();
				byte[] keyB = (DataConstants.KPRE + DataConstants.CLOUDPARK_USERGROUP).getBytes();
				byte[] filedB = parkingNO.getBytes();
				dataB = LZ4Compress.compress(dataB);
				LOG.info("doktask_bak.CLOUDPARK_USERGROUP." + parkingNO + ".压缩后大小：" + dataB.length);
				JedisPoolUtils.hsetB(keyB, filedB, dataB);
				LOG.info("doktask_bak.CLOUDPARK_USERGROUP." + parkingNO + ".ALL DATA PUT IN " + DataConstants.KPRE
						+ DataConstants.CLOUDPARK_USERGROUP + ",FILED IS " + parkingNO);
				sendmessserviceimp.SendMess(new JSONArray(), dataTunnelPublishClient, l, "in_k", parkingNO,
						DataConstants.CLOUDPARK_USERGROUP, String.valueOf(beginsize));
			}

			List<Object> list_orderuser = this.orderUserMapper.queryorderuser(jso);
			array = new JSONArray(list_orderuser);
			JSONArray newarry = new JSONArray();
			for (int n = 0; n < array.size(); n++) {
				JSONObject jsb = array.getJSONObject(n);
				jsb.put("endtime", ThreadLocalDateUtil.parse(jsb.getString("endtime")));
				jsb.put("ordertime", ThreadLocalDateUtil.parse(jsb.getString("ordertime")));
				newarry.add(jsb);
			}
			array = newarry;
			LOG.info("doktask_bak.orderuser." + parkingNO + ".newarry：" + newarry.toJSONString());
			key_p = BigDataAnalyze.geListKeyByDataType(parkingNO, DataConstants.CLOUDSHARE_INOUT, "p");
			LOG.info("doktask_bak.orderuser." + parkingNO + ".array：" + array.toJSONString());
			LOG.info("doktask_bak.orderuser." + parkingNO + ".key_p：" + key_p);
			l = JedisPoolUtils.hincrBy(key_p, "autoincrsn", 1);
			if (true) {
				LOG.info("doktask_bak.orderuser." + parkingNO + ".array大小：" + array.size());
				LOG.info("doktask_bak.orderuser." + parkingNO + ".压缩前大小：" + array.toJSONString().getBytes().length);
				long beginsize = array.toJSONString().getBytes().length;
				byte[] dataB = array.toJSONString().getBytes();
				byte[] keyB = (DataConstants.KPRE + DataConstants.CLOUDSHARE_INOUT).getBytes();
				byte[] filedB = parkingNO.getBytes();
				dataB = LZ4Compress.compress(dataB);
				LOG.info("doktask_bak.orderuser." + parkingNO + ".压缩后大小：" + dataB.length);
				JedisPoolUtils.hsetB(keyB, filedB, dataB);
				LOG.info("doktask_bak.orderuser." + parkingNO + ".ALL DATA PUT IN " + DataConstants.KPRE
						+ DataConstants.CLOUDSHARE_INOUT + ",FILED IS " + parkingNO);
				sendmessserviceimp.SendMess(new JSONArray(), dataTunnelPublishClient, l, "in_k", parkingNO,
						DataConstants.CLOUDSHARE_INOUT, String.valueOf(beginsize));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			returnstr = false;
			e.printStackTrace();
		} finally {
		}
		long endTime = System.currentTimeMillis();
		long excTime = (endTime - startTime);
		LOG.info("doktask_bak.parkingNO:" + parkingNO + "全量包生成成功");
		LOG.info("doktask_bak.parkingNO:" + parkingNO + "定时全量时间生成:" + excTime);
		LOG.info("doktask_bak.parkingNO:" + parkingNO + "isUpDateisUpDateisUpDate:" + isUpDate);
		LOG.info("doktask_bak.parkingNO:" + parkingNO + "returnstrreturnstrreturnstr:" + returnstr);
	}

	public void doktask(String parkingNO, boolean isUpDate) {

		long startTime = System.currentTimeMillis();
		boolean returnstr = true;
		JSONArray array = new JSONArray();
		try {
			JSONObject jso = new JSONObject();
			jso.put("ParkingLotNo", parkingNO);
			// 出入场全量
			double count = usercrdtm_inMapper.selectcarinCount(parkingNO);
			LOG.info("doktask." + parkingNO + ".data count" + count);
			String key_p = BigDataAnalyze.geListKeyByDataType(parkingNO, DataConstants.CLOUDPARK_INOUT, "p");
			long l = JedisPoolUtils.hincrBy(key_p, "autoincrsn", 1);
			if (count < fsize) {
				jso.put("limtcount", fsize);
				jso.put("fpage", 0);
				List<Object> list = this.usercrdtm_inMapper.queryTc_usercrdtm_in_limit(jso);
				array.addAll(list);
				array = checkData(array);

				LOG.info("doktask.CLOUDPARK_INOUT." + parkingNO + ".压缩前大小：" + array.toJSONString().getBytes().length);
				long beginsize = array.toJSONString().getBytes().length;
				byte[] dataB = array.toJSONString().getBytes();
				byte[] keyB = (DataConstants.KPRE + DataConstants.CLOUDPARK_INOUT).getBytes();
				byte[] filedB = parkingNO.getBytes();
				dataB = LZ4Compress.compress(dataB);
				LOG.info("doktask.CLOUDPARK_INOUT." + parkingNO + ".压缩后大小：" + dataB.length + "============");
				JedisPoolUtils.hsetB(keyB, filedB, dataB);
				LOG.info("doktask.CLOUDPARK_INOUT." + parkingNO + ".ALL DATA PUT IN " + DataConstants.KPRE
						+ DataConstants.CLOUDPARK_INOUT + ",FILED IS " + parkingNO);
				array.clear();
				sendmessserviceimp.SendMess(array, dataTunnelPublishClient, l, "in_k", parkingNO,
						DataConstants.CLOUDPARK_INOUT, String.valueOf(beginsize));
			} else {

				double spiltcount = Math.ceil(count / fsize);
				LOG.info("doktask.CLOUDPARK_INOUT." + parkingNO + ".spiltcount_count" + spiltcount);
				jso.put("limtcount", fsize);
				long fpage = 0;
				for (int i = 0; i < spiltcount; i++) {
					fpage = ((i + 1) - 1) * fsize;
					jso.put("fpage", fpage);
					List<Object> list = this.usercrdtm_inMapper.queryTc_usercrdtm_in_limit(jso);
					LOG.info("doktask.CLOUDPARK_INOUT." + parkingNO + ".list_count" + list.size());
					array.addAll(list);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				array = checkData(array);

				LOG.info("doktask.CLOUDPARK_INOUT." + parkingNO + ".array_count" + array.size());
				long beginsize = array.toJSONString().getBytes().length;
				byte[] dataB = array.toJSONString().getBytes();
				byte[] keyB = (DataConstants.KPRE + DataConstants.CLOUDPARK_INOUT).getBytes();
				byte[] filedB = parkingNO.getBytes();
				dataB = LZ4Compress.compress(dataB);
				JedisPoolUtils.hsetB(keyB, filedB, dataB);
				array.clear();
				sendmessserviceimp.SendMess(array, dataTunnelPublishClient, l, "in_k", parkingNO,
						DataConstants.CLOUDPARK_INOUT, String.valueOf(beginsize));

			}

			// 判断是不是为场中场或者场内切换
			ParkingLotParameter pp = carInfoForPadService.getParkingParamInfo(parkingNO);
			boolean isInparkingOrSwitch = false;
			if (pp.isIsinparkingswitch() || pp.isIsinparkinglot()) {
				isInparkingOrSwitch = true;
			}
			if (isInparkingOrSwitch) {
				// 过车点记录全量包
				array = crosspointService.getCrosspointbyParkinglotsNo(parkingNO);
				key_p = BigDataAnalyze.geListKeyByDataType(parkingNO, DataConstants.CLOUDPARK_SWITCH, "p");
				l = JedisPoolUtils.hincrBy(key_p, "autoincrsn", 1);
				if (true) {
					LOG.info("doktask.CLOUDPARK_INOUT." + parkingNO + ".压缩前大小："
							+ array.toJSONString().getBytes().length);
					long beginsize = array.toJSONString().getBytes().length;

					byte[] dataB = array.toJSONString().getBytes();

					byte[] keyB = (DataConstants.KPRE + DataConstants.CLOUDPARK_SWITCH).getBytes();
					byte[] filedB = parkingNO.getBytes();
					dataB = LZ4Compress.compress(dataB);
					LOG.info("doktask.CLOUDPARK_INOUT." + parkingNO + ".压缩后大小：" + dataB.length + "============");
					JedisPoolUtils.hsetB(keyB, filedB, dataB);

					LOG.info("doktask.CLOUDPARK_INOUT." + parkingNO + ".ALL DATA PUT IN " + DataConstants.KPRE
							+ DataConstants.CLOUDPARK_SWITCH + ",FILED IS " + parkingNO);
					array.clear();
					sendmessserviceimp.SendMess(array, dataTunnelPublishClient, l, "in_k", parkingNO,
							DataConstants.CLOUDPARK_SWITCH, String.valueOf(beginsize));
				}
			}

			// 缴费记录全量包
			List<Object> list_charge = this.chargerecordinfoMapper.queryTotalAmountChargerecordinfo(jso);
			array.addAll(list_charge);
			if (array != null) {
				JSONArray chargeArray = new JSONArray();
				for (int i = 0; i < array.size(); i++) {
					JSONObject json_charge = array.getJSONObject(i);
					String areaId = json_charge.getString("areaid");
					if (areaId != null && !areaId.isEmpty()) {
						Tc_parkingarea areaInfo = parkingInformation.getAreaInfo(areaId);
						json_charge.put("areaName", areaInfo.getAreaname());
					}
					chargeArray.add(json_charge);
				}
				array = chargeArray;
			}
			key_p = BigDataAnalyze.geListKeyByDataType(parkingNO, DataConstants.CLOUDPARK_CHARGE, "p");
			l = JedisPoolUtils.hincrBy(key_p, "autoincrsn", 1);
			if (true) {
				LOG.info("doktask.CLOUDPARK_CHARGE." + parkingNO + ".压缩前大小:" + array.toJSONString().getBytes().length);
				long beginsize = array.toJSONString().getBytes().length;

				byte[] dataB = array.toJSONString().getBytes();

				byte[] keyB = (DataConstants.KPRE + DataConstants.CLOUDPARK_CHARGE).getBytes();
				byte[] filedB = parkingNO.getBytes();
				dataB = LZ4Compress.compress(dataB);
				LOG.info("doktask.CLOUDPARK_CHARGE." + parkingNO + ".压缩后大小：" + dataB.length + "============");
				JedisPoolUtils.hsetB(keyB, filedB, dataB);

				LOG.info("doktask.CLOUDPARK_CHARGE." + parkingNO + ".ALL DATA PUT IN " + DataConstants.KPRE
						+ DataConstants.CLOUDPARK_CHARGE + ",FILED IS " + parkingNO);
				array.clear();
				sendmessserviceimp.SendMess(array, dataTunnelPublishClient, l, "in_k", parkingNO,
						DataConstants.CLOUDPARK_CHARGE, String.valueOf(beginsize));
			}

			array = userinfoservice.selectByparkingNo(jso.toJSONString());
			key_p = BigDataAnalyze.geListKeyByDataType(parkingNO, DataConstants.CLOUDPARK_USERGROUP, "p");
			l = JedisPoolUtils.hincrBy(key_p, "autoincrsn", 1);
			if (true) {

				LOG.info("doktask.CLOUDPARK_USERGROUP." + parkingNO + ".压缩前大小:"
						+ array.toJSONString().getBytes().length);
				long beginsize = array.toJSONString().getBytes().length;
				byte[] dataB = array.toJSONString().getBytes();

				byte[] keyB = (DataConstants.KPRE + DataConstants.CLOUDPARK_USERGROUP).getBytes();
				byte[] filedB = parkingNO.getBytes();
				dataB = LZ4Compress.compress(dataB);
				LOG.info("doktask.CLOUDPARK_USERGROUP." + parkingNO + ".压缩后大小:" + dataB.length);

				JedisPoolUtils.hsetB(keyB, filedB, dataB);

				LOG.info("doktask.CLOUDPARK_USERGROUP." + parkingNO + ".ALL DATA PUT IN " + DataConstants.KPRE
						+ DataConstants.CLOUDPARK_USERGROUP + ",FILED IS " + parkingNO);
				array.clear();
				sendmessserviceimp.SendMess(array, dataTunnelPublishClient, l, "in_k", parkingNO,
						DataConstants.CLOUDPARK_USERGROUP, String.valueOf(beginsize));

			}

			List<Object> list_orderuser = this.orderUserMapper.queryorderuser(jso);
			array.addAll(list_orderuser);
			array = fixorderuserInfo(array);

			key_p = BigDataAnalyze.geListKeyByDataType(parkingNO, DataConstants.CLOUDSHARE_INOUT, "p");
			LOG.info("doktask.CLOUDSHARE_INOUT." + parkingNO + ".array：" + array.toJSONString());
			LOG.info("doktask.CLOUDSHARE_INOUT." + parkingNO + ".key_p：" + key_p);
			l = JedisPoolUtils.hincrBy(key_p, "autoincrsn", 1);
			if (true) {
				LOG.info("doktask.CLOUDSHARE_INOUT." + parkingNO + ".array大小：" + array.size());
				LOG.info("doktask.CLOUDSHARE_INOUT." + parkingNO + ".压缩前大小：" + array.toJSONString().getBytes().length);
				long beginsize = array.toJSONString().getBytes().length;
				byte[] dataB = array.toJSONString().getBytes();

				byte[] keyB = (DataConstants.KPRE + DataConstants.CLOUDSHARE_INOUT).getBytes();
				byte[] filedB = parkingNO.getBytes();
				dataB = LZ4Compress.compress(dataB);
				LOG.info("doktask.CLOUDSHARE_INOUT." + parkingNO + ".压缩后大小：" + dataB.length);

				JedisPoolUtils.hsetB(keyB, filedB, dataB);

				LOG.info("doktask.CLOUDSHARE_INOUT." + parkingNO + ".ALL DATA PUT IN " + DataConstants.KPRE
						+ DataConstants.CLOUDSHARE_INOUT + ",FILED IS " + parkingNO);
				array.clear();
				sendmessserviceimp.SendMess(array, dataTunnelPublishClient, l, "in_k", parkingNO,
						DataConstants.CLOUDSHARE_INOUT, String.valueOf(beginsize));

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.info("=======this error" + e.getMessage());
			returnstr = false;
			e.printStackTrace();
		} finally {

		}
		if (returnstr) {
			// 如果是true 更新jobttime,job完成时间
			taskMapper.updatejobTime(parkingNO);
		}
		long endTime = System.currentTimeMillis();
		long excTime = (endTime - startTime);
		LOG.info("doktask.parkingNO:" + parkingNO + ".全量包生成成功");
		LOG.error("doktask.parkingNO:" + parkingNO + ".定时全量时间生成:" + excTime + ",车场编号:" + parkingNO);
		LOG.info("doktask.parkingNO:" + parkingNO + ".isUpDateisUpDateisUpDate:" + isUpDate);
		LOG.info("doktask.parkingNO:" + parkingNO + ".returnstrreturnstrreturnstr:" + returnstr);
	}

	/**
	 * @param array
	 * @throws ParseException
	 */
	private JSONArray fixorderuserInfo(JSONArray array) {
		JSONArray arry = new JSONArray();
		try {
			for (int n = 0; n < array.size(); n++) {
				JSONObject jsb = array.getJSONObject(n);
				jsb.put("endtime", ThreadLocalDateUtil.parse(jsb.getString("endtime")));
				jsb.put("ordertime", ThreadLocalDateUtil.parse(jsb.getString("ordertime")));
				arry.add(jsb);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return arry;
	}

	/**
	 * @param array
	 */
	private JSONArray checkData(JSONArray array) {
		try {
			for (int i = 0; i < array.size(); i++) {
				JSONObject obj = array.getJSONObject(i);

				String key_out = DataConstants.History_Table_OUT_ + obj.getString("parkinglotno");
				String key_in = DataConstants.History_Table_IN_ + obj.getString("parkinglotno");

				if (JedisPoolUtils.exists(key_out)) {
					if (JedisPoolUtils.hexists(key_out, obj.getString("carcode"))) {
						String result = JedisPoolUtils.hget(key_out, obj.getString("carcode"));
						// String[] re = result.split(",");
						long outtime = Long.valueOf(result);
						if (outtime > obj.getDate("intime").getTime()) {
							array.remove(i);
							LOG.info("doktask.parkingNO:" + obj.getString("parkinglotno")
									+ "改车redis 最后一次出场时间都比场内的入场时间大,需要remove，改车的车牌是：" + obj.getString("carcode"));
							publicparkingservice.moveToAbnormal(obj);
							break;
						}
					}
					if (JedisPoolUtils.hexists(key_in, obj.getString("carcode"))) {
						String result = JedisPoolUtils.hget(key_in, obj.getString("carcode"));
						// String[] re = result.split(",");
						long intime = Long.valueOf(result);
						if (intime > obj.getDate("intime").getTime()) {
							array.remove(i);
							publicparkingservice.moveToAbnormal(obj);
							LOG.info("doktask.parkingNO:" + obj.getString("parkinglotno")
									+ "改车redis 最后一次进场时间都比场内的入场时间大,需要remove，改车的车牌是：" + obj.getString("carcode"));
						}

					}

				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return array;
	}
}
