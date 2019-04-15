/**
 * &copy; 2012 杭州立方自动化有限公司
 */
package cn.rf.hz.web.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 独立日志工具
 * 
 * @author  ZHUWEI
 * @create  2012-9-7
 * @version 1.0
 */
public class LogUtil {
	
	/**
	 * 写日志
	 * @param msg 日志内容
	 */
	public void log(String msg){
		String fileName = getFileNameByDate();
		String filePath = this.getClass().getResource("/").getPath()+"../log_ws/";
		String fullPath = filePath + fileName;
		PrintWriter logPrint = null;
		
		try {
			createDir(filePath);
			File file = new File(fullPath);
			if(!file.exists()) {
				file.createNewFile();
			}
			logPrint = new PrintWriter(new FileWriter(fullPath, true), true);
			logPrint.println(getNowDateTime() + ": " + msg);
			
		} catch (IOException e) {
			System.err.println("通讯日志目录或文件创建失败：" + fullPath);
			logPrint = new PrintWriter(System.err);
		}
		finally{
			
			if(null !=logPrint){
				logPrint.close();
			}
		}
	}
	/**
	 * 写日志：记录通讯日志用
	 * 
	 * @param methodName 方法名
	 * @param isParam 是否是参数
	 * @param values  各个值
	 */
	public void log(String methodName, boolean isParam, String[] values){
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append(methodName);
		buffer.append("]--");
		if(isParam)
			buffer.append("[PARAMS]:");
		else 
			buffer.append("[RETURN]:");
		
		if(values != null){
			for (int i = 0; i < values.length; i++) {
				buffer.append( values[i] );
				buffer.append( "#" );
			}
		}
		log(buffer.toString());
	}
	/**
	 * @return 日期型文件名
	 */
	private String getFileNameByDate(){
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date())+".log";
	}
	/**
	 * @return 当前时间的字符串
	 */
	private String getNowDateTime(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	/**
	 * 创建文件夹
	 * @param path 目录
	 */
	private void createDir(String path) {
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdir();
	}
}
