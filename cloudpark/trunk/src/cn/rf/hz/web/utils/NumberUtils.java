package cn.rf.hz.web.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 
 * @author feixiang
 *
 */
public class NumberUtils {

	/**
	 * 判断是否为数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){ 
		   Pattern pattern = Pattern.compile("[0-9]*"); 
		   Matcher isNum = pattern.matcher(str);
		   if( !isNum.matches() ){
		       return false; 
		   } 
		   return true; 
		}
	
}
