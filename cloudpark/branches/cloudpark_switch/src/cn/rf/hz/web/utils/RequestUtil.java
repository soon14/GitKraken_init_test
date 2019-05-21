package cn.rf.hz.web.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * 远程请求工具类
 * 
 * @author 程依寿 2015年12月2日 下午10:03:34
 */
public class RequestUtil
{

	protected static final Logger LOG = Logger.getLogger(RequestUtil.class);

	/**
	 * get请求
	 * 
	 * @param httpUrl
	 * @return
	 */
	public static String requestUrl(String httpUrl)
	{
		String forward = "";
		String str = "";
		InputStream is = null;
		try
		{
			URL url = new URL(httpUrl);
			URLConnection con = url.openConnection();
			is = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuilder tempStr = new StringBuilder();
			while ((str = br.readLine()) != null)
			{
				tempStr.append(str);
			}
			forward = tempStr.toString();
			is.close();
		} catch (Exception e)
		{
			LOG.error("GET请求错误异常：" + e);
			LOG.error("GET请求错误URL：" + httpUrl);
		}
		return forward;
	}

	public static String sendPost(String url, String param)
	{
		OutputStream out = null;
		BufferedReader in = null;
		String result = "";

		try
		{
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setDoOutput(true);
			conn.setDoInput(true);

			conn.setRequestProperty("Content-length", "" + param.getBytes("UTF-8").length);
			conn.setRequestProperty("Content-Type", "application/octet-stream");
			conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
			conn.setRequestProperty("Charset", "UTF-8");

			// / 建立输出流，并写入数据
			out = conn.getOutputStream();
			out.write(param.getBytes("utf-8"));
			out.close();
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null)
				result += line;
		} catch (Exception e)
		{
			LOG.error("POST请求错误异常：" + e);
			LOG.error("POST请求错误URL：" + url + ",参数：  " + param);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally
		{
			try
			{
				if (out != null)
					out.close();
				if (in != null)
					in.close();
			} catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		return result;
	}
	
    public static String postUrlForJson(String systemurl, String url, String par) {
        String result = "";
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("ControlResource");
            URL httpurl = new URL(bundle.getString(systemurl).trim() + url);
            HttpURLConnection httpConn = (HttpURLConnection) httpurl.openConnection();
            httpConn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setConnectTimeout(30000);
            httpConn.setReadTimeout(30000);
            out = new PrintWriter(httpConn.getOutputStream());
            if (out != null) {
                out.print(par);
                out.flush();
            }
            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

	
	
	
}
