package cn.rf.hz.web.utils.httputils;

import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;

/* *
 *类名：HttpRequest
 *功能：Http请求对象的封装
 *详细：封装Http请求
 *版本：3.3
 *日期：2011-08-17
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class HttpRequest {

	/** 待请求的url */
	private String url = null;

	/** 默认的请求方式 */
	private String method = HttpClientConfig.REQUEST_METHOD_POST;

	private int timeout = 0;

	private int connectionTimeout = 0;

	/** Post方式请求时组装好的参数值对 */
	private NameValuePair[] parameters = null;

	/** Get方式请求时对应的参数 */
	private String queryString = null;

	/** 默认的请求编码方式 */
	private String charset = HttpClientConfig.DEFAULT_CHARSET;

	/** 请求发起方的ip地址 */
	private String clientIp;

	/** 请求返回的方式 */
	private HttpResultType resultType = HttpResultType.BYTES;

	/** 请求头信息 **/
	private Map<String, String> headMap;

	public HttpRequest(HttpResultType resultType) {
		super();
		this.resultType = resultType;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public NameValuePair[] getParameters() {
		return parameters;
	}

	public void setParameters(NameValuePair[] parameters) {
		this.parameters = parameters;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public HttpResultType getResultType() {
		return resultType;
	}

	public void setResultType(HttpResultType resultType) {
		this.resultType = resultType;
	}

	public Map<String, String> getHeadMap() {
		return headMap;
	}

	public void setHeadMap(Map<String, String> headMap) {
		this.headMap = headMap;
	}
	
}
