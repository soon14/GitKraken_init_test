package cn.rf.hz.web.cloudpark.job;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.rf.hz.web.utils.DataConstants;
import cn.rf.hz.web.utils.JedisPoolUtils;
import cn.rf.hz.web.utils.ListUtil;



public class ThreadPoolExecutorTest
{

    private static int queueDeep = 20;

    public void createThreadPool()
    {
        /* 
         * 创建线程池，最小线程数为2，最大线程数为4，线程池维护线程的空闲时间为3秒， 
         * 使用队列深度为4的有界队列，如果执行程序尚未关闭，则位于工作队列头部的任务将被删除， 
         * 然后重试执行程序（如果再次失败，则重复此过程），里面已经根据队列深度对任务加载进行了控制。 
         */ 
        ThreadPoolExecutor tpe = new ThreadPoolExecutor(4, 8, 3, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(queueDeep),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        
        //List<String> list = JedisPoolUtils.lrange(ip + DataConstants.BOXDATA, 0, 0);
/*        List<String> carparkNos = null;
        
    	int totalCount = 100;
		int pageSize = 30;
		for (int currentPage = 0, doneCount = 0; doneCount < totalCount; currentPage++) {
			List<String> subList = ListUtil.getNextPage(carparkNos, pageSize, currentPage);
			doneCount += subList.size();
		}*/
        // 向线程池中添加 10 个任务
        
        
        for (int i = 0; i < 10; i++)
        {
            while (getQueueSize(tpe.getQueue()) >= queueDeep)
            {
                System.out.println("队列已满，等1秒再添加任务");
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            TaskThreadPool ttp = new TaskThreadPool("111");
            System.out.println("put i:" + i);
            tpe.execute(ttp);
        }

        tpe.shutdown();
    }

    private synchronized int getQueueSize(Queue queue)
    {
        return queue.size();
    }
    
    
    class TaskThreadPool implements Runnable
    {
        private String data;

        public TaskThreadPool(String data)
        {
            this.data = data;
        }

        public void run()
        {
            System.out.println(Thread.currentThread() + " data:" + data);
            try
            {
                Thread.sleep(3000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
    
}
