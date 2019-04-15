package cn.rf.hz.web.cloudpark.impl;

import java.io.IOException;

import cn.rf.hz.web.utils.JedisPoolUtils;
import redis.clients.jedis.JedisPubSub;

public class Consumer {



	public Consumer() {
		
	}

	public void consum(String channel) {
		try {
			JedisPubSub jedisPubSub = new JedisPubSub() {
				// 取得订阅的消息后的处理
				public void onMessage(String channel, String message) {
					System.out.println("Channel:" + channel);
					System.out.println("Message:" + message.toString());
				}

				// 初始化订阅时候的处理
				public void onSubscribe(String channel, int subscribedChannels) {
					System.out.println("onSubscribe:" + channel);
				}

				// 取消订阅时候的处理
				public void onUnsubscribe(String channel, int subscribedChannels) {
					System.out.println("onUnsubscribe:" + channel);
				}

				// 初始化按表达式的方式订阅时候的处理
				public void onPSubscribe(String pattern, int subscribedChannels) {
					// System.out.println(pattern + "=" + subscribedChannels);
				}

				// 取消按表达式的方式订阅时候的处理
				public void onPUnsubscribe(String pattern, int subscribedChannels) {
					// System.out.println(pattern + "=" + subscribedChannels);
				}

				// 取得按表达式的方式订阅的消息后的处理
				public void onPMessage(String pattern, String channel, String message) {
					System.out.println(pattern + "=" + channel + "=" + message);
				}
			};
			JedisPoolUtils.subscribe(jedisPubSub, channel);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//jedis.subscribe(jedisPubSub, channel);
	}

	// close the connection

}
