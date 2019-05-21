package cn.rf.hz.web.utils;


import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 监听 ServletContext 对象
 * @author feixiang
 *
 */
public class ContextLoaderListener implements ServletContextListener {

	private ServletContext context = null;
	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(final ServletContextEvent event) {
		this.context = event.getServletContext();
		//Output a simple message to the server's console
		System.out.println("The App Server. Is Ready");
//		QuartzContext.getInstance().setContext(event.getServletContext());
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(final ServletContextEvent event) {
		// do nothing
	
	}
}
