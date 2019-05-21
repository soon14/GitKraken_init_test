package cn.rf.hz.web.cloudpark.jedis;

import cn.rf.hz.web.cloudpark.impl.Consumer;

public class ConsumerInstance {
	private static Consumer consumer=null;
	
	public static synchronized Consumer getinstance(){
		if(consumer==null){
			consumer =new Consumer();
		}
		System.out.println(consumer);
		System.out.println(consumer.hashCode());
		return consumer;
	}
}
