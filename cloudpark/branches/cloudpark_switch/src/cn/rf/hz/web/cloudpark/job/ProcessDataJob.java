package cn.rf.hz.web.cloudpark.job;

import org.springframework.stereotype.Service;

import cn.rf.hz.web.cloudpark.service.CarInoutService;
import cn.rf.hz.web.utils.DataConstants;
import cn.rf.hz.web.utils.JedisPoolUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("processdatajob")
public class ProcessDataJob {
	@Autowired

	private CarInoutService carinoutservice;
	private final static Logger log = Logger.getLogger(ProcessDataJob.class);
	 final Object object = new Object();
	@Scheduled(cron = "0/5 * * * * ?")
	public void reservationHoldingJob() {

		try {
			// System.out.println("1");
			InetAddress net = InetAddress.getLocalHost();
			String ip = net.getHostAddress();
			List<String> list = JedisPoolUtils.lrange(ip + DataConstants.CARINDATA, 0, 0);
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					String result = carinoutservice.saveCarin(list.get(i));
					if (result.indexOf("success") > -1) {
						//JedisPoolUtils.lrem(ip + DataConstants.BOXDATA, list.get(i));
					}
				}
			} else {
				log.info("redis没有数据需要更新");
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void test() {
		 Thread t1 = new Thread() {
			 public void run(){
				 synchronized (object) {
					 System.out.println("T1 start!");
					 while(true){
						  try {
		                        object.wait(500);
		                    } catch (InterruptedException e) {
		                        e.printStackTrace();
		                    } 
						  System.out.println("T1 end!");
					 }
					  
				 }
			 }
		 };
		// t1.start();
		 
/*	     Thread t2 = new Thread() {
	            public void run()
	            {
	                synchronized (object) {
	                    System.out.println("T2 start!");
	                    object.notify();
	                    System.out.println("T2 end!");
	                }
	            }
	        };
		 */

	}
	public void test22() {
		synchronized (object) {
			object.notify();
		}
	}
	/*
	 * public static void main(String[] args) { new
	 * ClassPathXmlApplicationContext("spring-jobbean.xml"); }
	 */
}
