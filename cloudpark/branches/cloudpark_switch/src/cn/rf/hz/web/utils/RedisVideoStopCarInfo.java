package cn.rf.hz.web.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.reformer.datatunnel.client.DataTunnelPublishClient;
import com.reformer.datatunnel.client.dto.MessageDto;

/**
 * 停车信息放到redis里
 * 
 * @author 程依寿 2016年3月3日 下午6:55:54
 */
public class RedisVideoStopCarInfo
{
	private final static Logger LOG = Logger.getLogger(RedisVideoStopCarInfo.class);

	/**
	 * 视频停车 进场
	 * 
	 * @param videoData
	 */
	public static void entrance(JSONObject videoData, DataTunnelPublishClient<MessageDto> dataTunnelPublishClient)
	{
		try
		{
			LOG.debug("进场数据信息，存入redis信息：" + videoData.toJSONString());
			Long l1 = new Date(videoData.getDate("inTime").getTime() + ConfigUtil.videoTimeLong * 1000).getTime();
			Long l2 = new Date().getTime();
			Long videoTimeLong = (l1 - l2) <= 0 ? 0 : (l1 - l2);

			String licensePlateNumber = videoData.getString("licensePlateNumber");

			JedisPoolUtils.del("video_" + licensePlateNumber);
			if (videoTimeLong.intValue() > 0)
			{
				// 在redis里保存72小时
				JedisPoolUtils.setex("video_" + licensePlateNumber, videoTimeLong.intValue() / 1000, videoData.toJSONString());
			}

			// 张泉金大数据
			BigDataAnalyze.inBigDataMessage(videoData, dataTunnelPublishClient);

		} catch (Exception e)
		{
			LOG.error("进场数据信息，存入redis异常：" + e);
		}
	}

	/**
	 * 线下更新车牌
	 * 
	 * @param videoData
	 */
	public static void offlineUpdateLicence(JSONObject videoData)
	{
		try
		{
			LOG.debug("线下更新车牌，存入redis信息：" + videoData.toJSONString());
			Long l1 = new Date(videoData.getDate("inTime").getTime() + ConfigUtil.videoTimeLong * 1000).getTime();
			Long l2 = new Date().getTime();
			Long videoTimeLong = (l1 - l2) <= 0 ? 0 : (l1 - l2);

			String licensePlateNumber = videoData.getString("licensePlateNumber");
			String recognitionNumber = videoData.getString("recognitionNumber");
			String oldLicensePlateNumber = videoData.getString("oldLicensePlateNumber");

			// 清空识别错的车牌,
			JedisPoolUtils.del("video_" + licensePlateNumber);
			
			//清空纠正后的车牌，以防止多次纠正照成的垃圾数据
			JedisPoolUtils.del("video_" + oldLicensePlateNumber);
			if (videoTimeLong.intValue() > 0)
			{
				// 在redis里保存72小时
				JedisPoolUtils.setex("video_" + recognitionNumber, videoTimeLong.intValue() / 1000, videoData.toJSONString());
			}

		} catch (Exception e)
		{
			LOG.error("线下更新车牌，存入redis异常：" + e);
		}
	}

	/**
	 * 进场数据 redis 老系统
	 * 
	 * @param message
	 * @param dataTunnelPublishClient
	 */
	public static void oldEntrance(String message, DataTunnelPublishClient<MessageDto> dataTunnelPublishClient)
	{
		try
		{
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// 数据
			JSONObject data = JSON.parseObject(message);
			Integer carParkId = data.getInteger("carParkId");
			String oldRecordId = data.getString("oldRecordId");
			Date inTime = new Date();
			String keyName = "entrance_" + carParkId + "_" + oldRecordId;
			// JedisPoolUtils.del(keyName);

			JSONObject oldData = JSON.parseObject(JedisPoolUtils.get(keyName));
			if (null == oldData)
			{
				data.put("status", 1);
				JedisPoolUtils.setex(keyName, 3600, data.toJSONString());
			} else
			{
				// 1、第一条数据，2、第二条数据，3、已传给大数据
				Integer status = oldData.getInteger("status");
				if (status == 1)
				{
					for (String str : data.keySet())
						oldData.put(str, data.get(str));

					String imgName = oldData.getString("imgName");
					String licensePlateNumber = oldData.getString("licensePlateNumber");

					// 车牌号和图片都不为空，说明数据已完整，两条数据均已上传
					if (!StringUtil.isEmpty(licensePlateNumber) && !StringUtil.isEmpty(imgName))
					{
						status = 2;
						oldData.put("status", status);

						JedisPoolUtils.setex(keyName, 3600, oldData.toJSONString());
					}
				}

				// 存入redis
				if (status == 2)
				{
					inTime = df.parse(oldData.getString("inTimeStr"));
					// 存入redis
					oldData.put("inTime", inTime);
					RedisVideoStopCarInfo.entrance(oldData, dataTunnelPublishClient);
				}
			}
		} catch (Exception e)
		{
			LOG.error("进场数据,redis两条数据拼装成一条完整信息异常(老系统):" + e);
		}
	}

	/**
	 * 视频停车 离场
	 * 
	 * @param videoData
	 * @param dataTunnelPublishClient
	 */
	public static void export(JSONObject videoData, DataTunnelPublishClient<MessageDto> dataTunnelPublishClient)
	{
		try
		{
			LOG.debug("出场数据信息，删除redis信息：" + videoData.toJSONString());
			// 清空redis
			String licensePlateNumber = videoData.getString("licensePlateNumber");
			JedisPoolUtils.del("video_" + licensePlateNumber);
			// 张泉金大数据
			BigDataAnalyze.outBigDataMessage(videoData, dataTunnelPublishClient);

		} catch (Exception e)
		{
			LOG.error("出场数据信息，删除redis异常：" + e);
		}
	}
	
	
	/**
	 * 视频停车 
	 * 场内
	 * 
	 * @param videoData
	 */
	public static void license(JSONObject videoData)
	{
		try
		{
			LOG.debug("场内数据信息,redis：" + videoData.toJSONString());
			// 
			String licensePlateNumber = videoData.getString("licensePlateNumber");
			
			Long l1 = new Date(videoData.getDate("inTime").getTime() + ConfigUtil.videoTimeLong * 1000).getTime();
			Long l2 = new Date().getTime();
			Long videoTimeLong = (l1 - l2) <= 0 ? 0 : (l1 - l2);

			if (videoTimeLong.intValue() > 0)
			{
				// 在redis里保存72小时
				JedisPoolUtils.setex("videoLicenseRecognize_" + licensePlateNumber, videoTimeLong.intValue() / 1000, videoData.toJSONString());
			}
		} catch (Exception e)
		{
			LOG.error("场内数据信息，redis异常：" + e);
		}
	}

}
