package cn.rf.hz.web.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils2 {
	 private static final String date_format = "yyyymmdd";

	 private static ThreadLocal threadlocal = new ThreadLocal() {
		 
		 
		 
	  protected synchronized Object initialvalue() {
	   return new SimpleDateFormat(date_format);
	  }
	 };
	 
	 public static DateFormat getdateformat() {
	  return (DateFormat) threadlocal.get();
	 }
	 
	 public static Date parse(String textdate) throws Exception {
	  return getdateformat().parse(textdate);
	 }
}
