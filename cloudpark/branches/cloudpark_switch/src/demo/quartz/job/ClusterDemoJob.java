package demo.quartz.job;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import cn.rf.hz.web.cloudpark.impl.ChargeJmServiceImp;

/**
 * ClusterDemoJob.
 * 
 * @author
 * 
 */
public class ClusterDemoJob implements Serializable {

	/**
	 * Log
	 */

	
	private final static Logger LOG = Logger.getLogger(ClusterDemoJob.class);

	/**
	 * Execute.
	 * 
	 * @throws InterruptedException
	 */
	public void execute() throws InterruptedException {
		LOG.info("Cluster demo execute begin!");
		//Thread.sleep(15000);
		LOG.info("Cluster demo execute end!");
	}
	
	public void sys() throws InterruptedException{
		LOG.info("hello");
		
		LOG.info("nihao");
	}

}
