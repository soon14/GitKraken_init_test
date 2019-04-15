package cn.rf.hz.web.cloudpark.jedis;
import org.apache.log4j.Logger;

import cn.rf.hz.web.cloudpark.impl.Consumer;
import cn.rf.hz.web.cloudpark.impl.SaveCainToCacheServiceImp;
import cn.rf.hz.web.utils.JedisPoolUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;



public class SubThread extends Thread {
   /*// private  JedisPool jedisPool;
    //private  Subscriber subscriber;
    private final Consumer subscriber = new Consumer();
    private final String channel = "carinout";
	private final static Logger logger = Logger.getLogger(SubThread.class);*/


    @Override
    public void run() {
   /* 	logger.info("subscribe redis, channel, thread will be blocked:"+channel);
        System.out.println(String.format("subscribe redis, channel %s, thread will be blocked", channel));
       // Jedis jedis = null;
        try {
        	logger.info("1");
        	JedisPoolUtils.subscribe(subscriber, channel); 
        	logger.info("2");
        } catch (Exception e) {
        	e.printStackTrace();
            System.out.println(String.format("subsrcibe channel error, %s", e));
        } finally {
            
        }*/
    }
}