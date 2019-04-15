package cn.rf.hz.web.utils.httputils;

public class HttpClientConfig {

	/** 默认编码方式 **/
	public static final String DEFAULT_CHARSET = "UTF-8";
	/** 连接超时时间，由bean factory设置，缺省为8秒钟 */
	public static final int DEFAULT_CONNECTION_TIMEOUT = 3000;
	/** 回应超时时间, 由bean factory设置，缺省为30秒钟 */
	public static final int DEFAULT_SO_TIMEOUT = 5000;
	/** 闲置连接超时时间, 由bean factory设置，缺省为60秒钟 */
	public static final int DEFAULT_IDLE_CONN_TIMEOUT = 60000;
	/** 默认最大连接时间 **/
	public static final int DEFAULT_MAX_CONN_PER_HOST = 30;
	/** 默认最大连接数 **/
	public static final int DEFAULT_MAX_TOTAL_CONN = 80;
	/** 默认等待HttpConnectionManager返回连接超时（只有在达到最大连接数时起作用）：1秒 */
	public static final long DEFAULT_HTTP_CONNECTION_MANAGER_TIMEOUT = 3 * 1000;

	/** HTTP GET method */
	public static final String REQUEST_METHOD_GET = "GET";

	/** HTTP POST method */
	public static final String REQUEST_METHOD_POST = "POST";

}
