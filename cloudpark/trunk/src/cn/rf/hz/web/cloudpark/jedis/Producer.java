package cn.rf.hz.web.cloudpark.jedis;

import java.io.IOException;

import org.apache.log4j.Logger;


import cn.rf.hz.web.utils.JedisPoolUtils;

public class Producer {

	
	private final static Logger logger = Logger.getLogger(Producer.class);

	public void provide_bak(String channel, Message message) throws IOException {
		String str1 = MessageUtil.convertToString(channel, "UTF-8");
		String str2 = MessageUtil.convertToString(message, "UTF-8");
		JedisPoolUtils.publish(str1, str2);
	}
	
	public void provide(String channel, String message) {
		try {
			logger.info("channelchannelchannelchannelchannelchannel"+channel);
			logger.info("messagemessagemessagemessagemessagemessage"+message);
			///String str1 = MessageUtil.convertToString(channel, "UTF-8");
			logger.info("1==================");
			//String str2 = MessageUtil.convertToString(message, "UTF-8");
			logger.info("2==================");
			//logger.info("2=================="+str1);
			//logger.info("2=================="+str2);
			JedisPoolUtils.publish(channel, message);
			logger.info("2==================");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("errorerrorerrorerrorerrorerrorerror"+message);
			e.printStackTrace();
		}
	}

	// close the connection
	/*public void close() throws IOException {
		// 将Jedis对象归还给连接池，关闭连接
		pool.returnResource(jedis);
	}*/
}
