package cn.rf.hz.web.utils;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;



public class ThreadPoolExecutorUtil {
		private static ThreadPoolExecutorUtil instance=null;

		private ThreadPoolExecutor executor;
		public static int queueDeep=4;
		public static synchronized  ThreadPoolExecutorUtil getinstance(){
			if(instance==null){
				instance =new ThreadPoolExecutorUtil();
			}
			return instance;
		}

		public ThreadPoolExecutorUtil() {
			super();
			
			executor= new ThreadPoolExecutor(2, 4, 3, TimeUnit.MILLISECONDS,
					new LinkedBlockingQueue<Runnable>(queueDeep),new ThreadPoolExecutor.DiscardOldestPolicy()); 
			// TODO Auto-generated constructor stub
		}
		public ThreadPoolExecutor getExecutor() {
			return executor;
		}
		public synchronized static int getQueueSize(Queue queue)
	    {
	        return queue.size();
	    }

}
