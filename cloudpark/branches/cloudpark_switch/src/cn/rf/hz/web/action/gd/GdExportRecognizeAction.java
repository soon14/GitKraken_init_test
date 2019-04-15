package cn.rf.hz.web.action.gd;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.rf.hz.web.annotation.Auth;
import cn.rf.hz.web.service.gd.CarParksService;
import cn.rf.hz.web.service.gd.GdEntranceRecognizeService;
import cn.rf.hz.web.service.gd.GdExportRecognizeService;
import cn.rf.hz.web.utils.JedisPoolUtils;
import cn.rf.hz.web.utils.RequestUtil;
import cn.rf.hz.web.utils.URLUtils;
import cn.rf.hz.web.utils.WriterUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 出场记录表
 * 
 * @author 程依寿 2015年10月22日 下午11:08:34
 */
@Controller
@RequestMapping("/gdExportRecognize")
public class GdExportRecognizeAction 
{

	private final static Logger LOG = Logger.getLogger(GdExportRecognizeAction.class);

	@Autowired(required = false)
	public GdExportRecognizeService gdExportRecognizeService;

	@Autowired
	private GdEntranceRecognizeService gdEntranceRecognizeService;

	@Autowired
	private CarParksService carParksService;

	/**
	 * 新系统
	 * 
	 * 4.2上传出场数据
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/saveGdExportRecognize", method = RequestMethod.POST)
	public void saveGdExportRecognize(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		WriterUtil.writer(response, this.gdExportRecognizeService.saveGdExportRecognize(requestBody));
	}

	/**
	 * 老系统
	 * 
	 * @param requestBody
	 * @param request
	 * @param response
	 */
	@Auth(verifyLogin = false, verifyURL = false)
	@RequestMapping(value = "/saveOldGdExportRecognize", method = RequestMethod.POST)
	public void saveOldGdExportRecognize(@RequestBody String requestBody, HttpServletRequest request, HttpServletResponse response)
	{
		WriterUtil.writer(response, this.gdExportRecognizeService.saveOldGdExportRecognize(requestBody));

	}

	/**
	 * 处理代扣
	 * 
	 */
	public synchronized void withholdAliPay()
	{
		List<String> withholdList = JedisPoolUtils.hvals("withhold");

		if (withholdList.size() != 0)
		{

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (int i = 0; i < withholdList.size(); i++)
			{
				JSONObject dkObject = JSON.parseObject(withholdList.get(i));

				LOG.info("【" + dkObject.getString("licensePlateNumber") + "】启动代扣---------start");

				LOG.info("代扣信息：" + dkObject.toJSONString());

				Integer carParkId = dkObject.getInteger("carParkId");
				// 入场信息
				Map<String, Object> entranceInfo = this.gdEntranceRecognizeService.findEntranceInfo(carParkId, dkObject.getInteger("record_id"));
				if (entranceInfo != null)
				{

					// 停车场信息
					Map<String, Object> carParkInfo = this.carParksService.getCarParkById(carParkId);

					JSONObject dkResult = new JSONObject();
					dkResult.put("enterTime", df.format(entranceInfo.get("inTime")));// 必要条件,入场时间
					dkResult.put("parkId", carParkId);// 必要条件 :停车场Id
					dkResult.put("parkingRecordId", entranceInfo.get("recordId"));// 必要条件,停车场记录Id
					dkResult.put("parikingType", carParkInfo.get("parkPayType"));// 必要条件,停车类型
					dkResult.put("oldParkingRecordId", dkObject.getInteger("record_id"));// 必要条件,线下停车记录Id
					dkResult.put("uuid", "s123123-weqwe-qewqweqw-qsdasda");// 必要条件,立方用户管理Id
					dkResult.put("payType", 4);// 必要条件 :支付类型
					dkResult.put("userType", 1);// 必要条件 :用户类型
					dkResult.put("userCode", "");// 用户账号
					dkResult.put("carNo", dkObject.getString("licensePlateNumber"));// 必要条件,车牌号码
					dkResult.put("areFare", 0);// 服务费
					dkResult.put("leaveTime", df.format(dkObject.getDate("outTime")));// 车辆离场时间
					dkResult.put("buyerId", "2088902224114311");// 买家账户
					dkResult.put("sellerId", "2088511437662207");// 卖家账户
					dkResult.put("carParkName", carParkInfo.get("carParkName"));// 停车场名称
					dkResult.put("agreementNo", "20151228153009372331");// 签约协议号

					try
					{
						LOG.info("发给林锋的数据：" + dkResult.toJSONString());
						JSONObject result = JSON.parseObject(RequestUtil.sendPost(URLUtils.tollrecord + "/v1_2/alipay/withholding/pay/cost", dkResult.toJSONString()));
						if (result.getBooleanValue("success") && "alipay.withholding.err".equals(result.getString("code")))
						{
							// 重发
							LOG.info("代扣失败，需重发代扣：" + result.toJSONString());
						} else
						{
							LOG.info("【" + dkObject.getString("licensePlateNumber") + "】代扣成功");
							LOG.info("林锋返回的代扣信息：" + result.toJSONString());

							// 清除redis缓存
							JedisPoolUtils.hdel("withhold", carParkId + "_" + dkObject.getInteger("oldRecordId"));
						}
					} catch (Exception e)
					{
						LOG.info("【" + dkObject.getString("licensePlateNumber") + "】代扣异常：" + e.getMessage());
					}
				} else
				{

					LOG.info("【" + dkObject.getString("licensePlateNumber") + "】垃圾数据，找不到入场记录，本条记录不参与代扣：" + dkObject.toJSONString());
					// 垃圾数据
					// 清除redis缓存
					JedisPoolUtils.hdel("withhold", carParkId + "_" + dkObject.getInteger("oldRecordId"));
				}

				LOG.info("【" + dkObject.getString("licensePlateNumber") + "】代扣结束---------end");
			}

		}

	}


}
