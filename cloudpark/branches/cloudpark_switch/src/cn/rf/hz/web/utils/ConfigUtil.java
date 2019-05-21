package cn.rf.hz.web.utils;

import java.util.ResourceBundle;

/**
 * 配置工具类
 * 
 * 配置文件信息
 * 
 * @author 程依寿 2015年12月16日 上午10:38:04
 */
public class ConfigUtil
{
	private static ResourceBundle res = ResourceBundle.getBundle("config");

	
	//redis配置信息
	public static final String jedisHost = res.getString("jedisHost");
	public static final Integer jedisPort = Integer.valueOf(res.getString("jedisPort"));
	public static final String jedisAuth = res.getString("jedisAuth");
	public static final Integer database = Integer.parseInt(res.getString("jedisDatabase"));
	public static final Integer timeout = Integer.parseInt(res.getString("jedisTimeout"));
	public static final String password = res.getString("jedisPassword");
	
	

	
	
	//视频停车，时长
	public static final Integer videoTimeLong = Integer.valueOf(res.getString("videoTimeLong"));
	
	//出入场全量包生成并发数控制
    public static final Integer inoutTotalSemaphore = Integer.valueOf(res.getString("inout_semaphore_total"));
	
    //缴费全量包生成并发数控制
    public static final Integer chargeTotalSemaphore = Integer.valueOf(res.getString("charge_semaphore_total"));

}
