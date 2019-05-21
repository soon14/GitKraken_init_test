package cn.rf.hz.web.utils.httputils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.FilePartSource;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;

public class HttpProtocolHandler {
	/** HTTP连接管理器，该连接管理器必须是线程安全的 */
	private HttpConnectionManager connectionManager;

	private static HttpProtocolHandler httpProtocolHandler = new HttpProtocolHandler();

	/**
	 * 工厂方法
	 * @return
	 */
	public static HttpProtocolHandler getInstance() {
		return httpProtocolHandler;
	}

	/**
	 * 私有的构造方法
	 */
	private HttpProtocolHandler() {
		// 创建一个线程安全的HTTP连接池
		connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.getParams().setDefaultMaxConnectionsPerHost(HttpClientConfig.DEFAULT_MAX_CONN_PER_HOST);
		connectionManager.getParams().setMaxTotalConnections(HttpClientConfig.DEFAULT_MAX_TOTAL_CONN);

		IdleConnectionTimeoutThread ict = new IdleConnectionTimeoutThread();
		ict.addConnectionManager(connectionManager);
		ict.setConnectionTimeout(HttpClientConfig.DEFAULT_IDLE_CONN_TIMEOUT);

		ict.start();
	}

	/** 不带上传文件的请求，支持GET/POST **/
	public HttpResponse execute(HttpRequest request) {
		String charset = request.getCharset();
		charset = charset == null ? HttpClientConfig.DEFAULT_CHARSET : charset;

		HttpMethod method = null;
		// GET请求
		if (request.getMethod().equals(HttpClientConfig.REQUEST_METHOD_GET)) {
			method = new GetMethod(request.getUrl());
			method.getParams().setCredentialCharset(charset);
			// parseNotifyConfig会保证使用GET方法时，request一定使用QueryString
			method.setQueryString(request.getQueryString());
		} else {
			// post模式且不带上传文件
			method = new PostMethod(request.getUrl());
			((PostMethod) method).addParameters(request.getParameters());
		}
		// 设置请求头文件
		Map<String, String> headMap = request.getHeadMap();
		List<String> keys = new ArrayList<String>(headMap.keySet());
		for(int i = 0; i< keys.size(); i++) {
            String key = (String) keys.get(i);
            String value = (String) headMap.get(key);
			method.addRequestHeader(key, value);
		}

		return _execute(request, method);
	}

	/** 带上传文件的请求,支持POST **/
	public HttpResponse execute(HttpRequest request, String fileName, String filePath) {
		String charset = request.getCharset();
		charset = charset == null ? HttpClientConfig.DEFAULT_CHARSET : charset;
		HttpMethod method = new PostMethod(request.getUrl());
		// 设置请求头文件
		Map<String, String> headMap = request.getHeadMap();
		List<String> keys = new ArrayList<String>(headMap.keySet());
		for(int i = 0; i< keys.size(); i++) {
            String key = (String) keys.get(i);
            String value = (String) headMap.get(key);
			method.addRequestHeader(key, value);
		}
		
        List<Part> parts = new ArrayList<Part>();
        for (int i = 0; i < request.getParameters().length; i++) {
        	parts.add(new StringPart(request.getParameters()[i].getName(), request.getParameters()[i].getValue(), charset));
        }
        //增加文件参数，strParaFileName是参数名，使用本地文件
        try {
			parts.add(new FilePart(fileName, new FilePartSource(new File(filePath))));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        // 设置请求体
        ((PostMethod) method).setRequestEntity(new MultipartRequestEntity(parts.toArray(new Part[0]), new HttpMethodParams()));
		
		return _execute(request, method);
	}

	/** 字符全形式的参数， 支持POST **/
	public HttpResponse execute(HttpRequest request, String body) {
		String charset = request.getCharset();
		charset = charset == null ? HttpClientConfig.DEFAULT_CHARSET : charset;
		PostMethod method = new PostMethod(request.getUrl());
		// 设置请求头文件
		Map<String, String> headMap = request.getHeadMap();
		List<String> keys = new ArrayList<String>(headMap.keySet());
		for(int i = 0; i< keys.size(); i++) {
            String key = (String) keys.get(i);
            String value = (String) headMap.get(key);
			method.addRequestHeader(key, value);
		}

		// 设置参数
		method.setRequestEntity(new StringRequestEntity(body));
		
		return _execute(request, method);
	}
	
	protected HttpClient getHttpClient(HttpRequest request) {
		HttpClient httpclient = new HttpClient(connectionManager);

		int connectionTimeout = HttpClientConfig.DEFAULT_CONNECTION_TIMEOUT;
		if (request.getConnectionTimeout() > 0) {
			connectionTimeout = request.getConnectionTimeout();
		}
		httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);

		// 设置回应超时
		int soTimeout = HttpClientConfig.DEFAULT_SO_TIMEOUT;
		if (request.getTimeout() > 0) {
			soTimeout = request.getTimeout();
		}
		httpclient.getHttpConnectionManager().getParams().setSoTimeout(soTimeout);

		// 设置等待ConnectionManager释放connection的时间
		httpclient.getParams().setConnectionManagerTimeout(HttpClientConfig.DEFAULT_HTTP_CONNECTION_MANAGER_TIMEOUT);

		return httpclient;
	}
	
	protected HttpResponse _execute(HttpRequest request, HttpMethod method) {
		HttpResponse response = new HttpResponse();
		HttpClient httpClient = getHttpClient(request);
		try {
			httpClient.executeMethod(method);
            if (request.getResultType().equals(HttpResultType.STRING)) {
                response.setStringResult(method.getResponseBodyAsString());
            } else if (request.getResultType().equals(HttpResultType.BYTES)) {
                response.setByteResult(method.getResponseBody());
            }
            response.setResponseHeaders(method.getResponseHeaders());
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
			return null;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			method.releaseConnection();
		}

		return response;
	}
	
	
	/**
	 * 将NameValuePairs数组转变为字符串
	 * 
	 * @param nameValues
	 * @return
	 */
	protected String toString(NameValuePair[] nameValues) {
		if (nameValues == null || nameValues.length == 0) {
			return "null";
		}

		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < nameValues.length; i++) {
			NameValuePair nameValue = nameValues[i];

			if (i == 0) {
				buffer.append(nameValue.getName() + "=" + nameValue.getValue());
			} else {
				buffer.append("&" + nameValue.getName() + "=" + nameValue.getValue());
			}
		}

		return buffer.toString();
	}
}
