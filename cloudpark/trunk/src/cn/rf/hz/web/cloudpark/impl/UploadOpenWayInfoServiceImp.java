package cn.rf.hz.web.cloudpark.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.sharding.sequence.Sequence;
import com.reformer.sharding.sequence.exception.SequenceException;

import cn.rf.hz.web.cloudpark.daoxx.Tc_channelMapper;
import cn.rf.hz.web.cloudpark.moder.Tc_channel;
import cn.rf.hz.web.cloudpark.moder.Tc_opengaterecord;
import cn.rf.hz.web.cloudpark.service.UploadOpenWayInfoService;
import cn.rf.hz.web.sharding.dao.Tc_opengaterecordMapper;
import cn.rf.hz.web.utils.DateUtil;
import cn.rf.hz.web.utils.OSSConfigure;
import cn.rf.hz.web.utils.OSSUtil;
import cn.rf.hz.web.utils.StringUtil;

@Service("uploadOpenWayInfoService")
public class UploadOpenWayInfoServiceImp implements UploadOpenWayInfoService {

	private final static Logger LOG = Logger.getLogger(UploadOpenWayInfoServiceImp.class);
	@Autowired
	Tc_opengaterecordMapper opengaterecordMapper;
	@Autowired
	private ParkOutInServiceImp parkOutInService;

	@Autowired
	private Sequence sequencOpen;

	@Autowired
	private Tc_channelMapper channelMapper;

	/**
	 * 上传开闸记录
	 */
	@Override
	public JSONObject uploadOpenWayRecord(String requestBody) {
		// TODO Auto-generated method stub
		// 返回结果
		JSONObject resultJSON = new JSONObject();
		// System.out.println(requestBody);
		JSONObject data = JSON.parseObject(requestBody);
		LOG.info("=============uploadOpenWayRecord,requestBody:" + requestBody + "=============");
		// System.out.println(data);
		// String sign = data.getString("sign");
		// 停车场编号
		String parkNo = data.getString("parkNo");
		JSONArray dataArrays = data.getJSONArray("data");
		// 开闸记录集合
		List<Tc_opengaterecord> opengaterecordList = new ArrayList<Tc_opengaterecord>();
		if (dataArrays != null) {
			for (int i = 0; i < dataArrays.size(); i++) {
				JSONObject object = dataArrays.getJSONObject(i);
				// 通道Id
				Integer channelId = object.getInteger("channelId");
				Integer openType = object.getInteger("openType");
				Integer operatorID = object.getInteger("operatorID");
				Date eventTime = object.getDate("eventTime");
				String carCode = object.getString("licensePlateNumber");
				if (carCode != null && !carCode.isEmpty()) {
					if (carCode.indexOf("无牌车") > -1) {
						carCode = "无牌车";
					}
				}
				String textReason = object.getString("textReason");
				JSONArray voicearray = object.getJSONArray("voices");
				Tc_opengaterecord opengaterecord = new Tc_opengaterecord();
				opengaterecord.setPartitionid(getPartitionid(parkNo));
				LOG.info("=============uploadOpenWayRecord,1111111=============");
				opengaterecord.setRecordid(getSequenceId());
				LOG.info("=============uploadOpenWayRecord,22222222=============");
				opengaterecord.setChannelid(channelId);
				opengaterecord.setGatetype(openType);
				String userid = object.containsKey("userid") ? object.getString("userid") : "";
				String inOutId = object.containsKey("inOutId") ? object.getString("inOutId") : "";
				LOG.info("=============uploadOpenWayRecord,3333333333=============");
				String imagePath = getImagePath(parkNo, String.valueOf(channelId), object.getString("eventTime"),
						inOutId, userid);
				LOG.info("=============uploadOpenWayRecord,imagePath:" + imagePath + "=============");
				opengaterecord.setImagepath(imagePath);
				opengaterecord.setOperatorid(operatorID);
				opengaterecord.setOperatordate(eventTime);
				opengaterecord.setIsupload(true);
				opengaterecord.setParkinglotno(parkNo);
				opengaterecord.setCarcode(carCode);
				opengaterecord.setTextreason(textReason);
				String voicepaths = getVoicePath(parkNo, String.valueOf(channelId), object.getString("eventTime"),
						voicearray == null ? 0 : voicearray.size());
				opengaterecord.setVoicereason(voicepaths);
				opengaterecordList.add(opengaterecord);
				LOG.info("=============uploadOpenWayRecord,voicepaths:" + voicepaths + "=============");
				if (voicepaths != null && !voicepaths.isEmpty()) {
					for (int j = 0; j < voicepaths.split(",").length; j++) {
						String voice = voicearray.getJSONObject(j).getString("voice");
						InputStream voiceStream = StringUtil.convertStringToStream(voice);
						int m = OSSUtil.putFileSinged(voiceStream, voicepaths.split(",")[j] + ".mp3", "audio/mpeg");
						LOG.info("=============uploadOpenWayRecord,uploadVoiceToAliyun,result:" + m + "=============");
					}
				}
				String bigImage = object.getString("bigImage");
				if (bigImage != null && !bigImage.isEmpty()) {
					InputStream imageStream = StringUtil.convertStringToStream(bigImage);
					int n = OSSUtil.putFileSinged(imageStream, imagePath + ".jpg");
					LOG.info("=============uploadOpenWayRecord,uploadImageToAliyun,result:" + n + "=============");
				}
			}
		}
		if (opengaterecordList.isEmpty()) {
			// 返回错误提示
			resultJSON.put("parkNo", parkNo);
			resultJSON.put("sign", "");
			resultJSON.put("resCode", 2);
			resultJSON.put("resMsg", "upload opengaterecord failed");
		} else {
			try {
				// 批量上传开闸记录
				opengaterecordMapper.batchInsertOpengaterecord(opengaterecordList);
				// 发送开闸消息
				parkOutInService.sendKafkaOpenGateMes(parkNo, opengaterecordList, "in_p");

				resultJSON.put("parkNo", parkNo);
				resultJSON.put("sign", "");
				resultJSON.put("resCode", 0);
				resultJSON.put("resMsg", "upload opengaterecord success");
			} catch (Exception e) {
				e.printStackTrace();
				e.getMessage();
				resultJSON.put("parkNo", parkNo);
				resultJSON.put("sign", "");
				resultJSON.put("resCode", 2);
				resultJSON.put("resMsg", "upload opengaterecord error");
			}
		}
		LOG.info("=============uploadOpenWayRecord,resultJSON:" + resultJSON.toJSONString() + "=============");
		return resultJSON;
	}

	/**
	 * 获取开闸表分区值
	 */
	public Integer getPartitionid(String ParkingLotNo) {
		int hasresult = StringUtil.getAsciiCode(ParkingLotNo) % 16;
		LOG.info("=============hasresult" + hasresult + "=============");
		// 分区字段开始计算
		String partitionIdTemp = "";
		if (hasresult < 10) {
			partitionIdTemp = DateUtil.getFomartDate(new Date(), "yyyyMM") + "0" + hasresult;

		} else {
			partitionIdTemp = DateUtil.getFomartDate(new Date(), "yyyyMM") + hasresult;
		}
		Integer partitionID = Integer.parseInt(partitionIdTemp);
		// 分区字段计算结束
		return partitionID;
	}

	private long getSequenceId() {
		long id;
		try {
			id = sequencOpen.nextValue();
		} catch (SequenceException e) {
			throw new IllegalArgumentException("sequence netValue error");
		}
		return id;
	}

	private String getImagePath(String ParkNo, String channelId, String eventTime, String inoutid, String userid) {
		String result = "";
		String imagePath = "";
		imagePath = OSSConfigure.getInstance().getParkingOpengateVoiceDir().replace("//", "/");
		imagePath += ParkNo + "/" + DateUtil.formatYearMonth(new Date()) + "/";
		if (inoutid != null && !inoutid.isEmpty()) {
			imagePath = "";
			Tc_channel channel = channelMapper.selectByPrimaryKey(Integer.valueOf(channelId));
			if (channel != null) {
				if (channel.getInorout() == 0) {
					imagePath = OSSConfigure.getInstance().getParkingEntranceDir().replace("//", "/");
				} else {
					imagePath = OSSConfigure.getInstance().getParkingDepartureDir().replace("//", "/");
				}
				imagePath += ParkNo + "/" + DateUtil.formatYearMonth(new Date()) + "/";
				result = imagePath + inoutid + "-2";
			}
		} else if (userid != null && !userid.isEmpty()) {
			result = imagePath + userid;
		} else {
			result = imagePath + ParkNo + "_" + channelId + "_" + DateUtil.StringLongFomateDate(eventTime);
		}
		LOG.info("=============uploadVoiceToAliyun,getImagePath:" + result + "=============");
		return result;
	}

	private String getVoicePath(String ParkNo, String channelId, String eventTime, Integer voicenumber) {
		String result = "";
		if (voicenumber > 0) {
			String voicePath = "";
			voicePath = OSSConfigure.getInstance().getParkingOpengateVoiceDir().replace("//", "/");
			voicePath += ParkNo + "/" + DateUtil.formatYearMonth(new Date()) + "/";

			for (int i = 1; i <= voicenumber; i++) {
				result += voicePath + ParkNo + "_" + channelId + "_" + DateUtil.StringLongFomateDate(eventTime) + "_"
						+ String.valueOf(i);
				if (i != voicenumber) {
					result += ",";
				}
			}
		}
		LOG.info("=============uploadVoiceToAliyun,getVoicePath:" + result + "=============");
		return result;
	}

}
