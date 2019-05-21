package cn.rf.hz.web.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPutil {
	
	
	
	public static String getIPaddress() {
		String ip="";
		try {
			InetAddress net = InetAddress.getLocalHost();
			 ip = net.getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ip;
	}
}
