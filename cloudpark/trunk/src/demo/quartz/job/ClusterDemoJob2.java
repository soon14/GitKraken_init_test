package demo.quartz.job;

import java.io.Serializable;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;



import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.opensymphony.xwork2.ActionContext;

/**
 * ClusterDemoJob.
 * 
 * @author
 * 
 */

public class ClusterDemoJob2 implements Serializable {

	/**
	 * Log
	 */

	
	private final static Logger LOG = Logger.getLogger(ClusterDemoJob.class);
    /*@Autowired
	KtaskServiceImp  ktaskserviceimp;*/
	/**
	 * Execute.
	 * 
	 * @throws InterruptedException
	 */
	public void execute()  {
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();    
		ServletContext servletContext = webApplicationContext.getServletContext();  
		ApplicationContext ac1 = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
	//	ac1.getBean("beanId"); 
	
		//KtaskServiceImp  ktaskserviceimp= (KtaskServiceImp)ac1.getBean("ktaskservice"); 
		LOG.info("Cluster demo execute begin!");
		//Thread.sleep(15000);
		//LOG.info(ktaskserviceimp.getClass().getName());
		try {
			//ktaskserviceimp.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sys() throws InterruptedException{
		LOG.info("hello");
		
		LOG.info("nihao");
	}

}
