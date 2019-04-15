package cn.rf.hz.web.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;

/**
 * 阿里云OSS工具类
 * 
 * @author cent
 *
 */
public class OSSUtil {
	private final static Logger logger = Logger.getLogger(OSSUtil.class);
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");

	/**
	 * 以InputStream上传APP用户头像
	 * 
	 * @param inputStream
	 *            输入流
	 * @param inputLength
	 *            流长度
	 * @param fileName
	 *            文件名, 要求在在同一个存储路径里面保证唯一
	 * @param contentType
	 *            文件内容类型, 例如: "image/jpeg"
	 * @param second
	 *            有效时间, 单位为秒
	 * @return 0-成功, 1-失败
	 */
	public static int putAvatarStream(InputStream inputStream, long inputLength, String fileName, String contentType,
			int second) {
		// 取APP头像的存储路径
		String pathRoot = OSSConfigure.getInstance().getAppAvatarDir();
		String pathFile = pathRoot + fileName;

		return putFileSinged(inputStream, inputLength, pathFile, contentType, second);
	}

	/**
	 * 以InputStream上传APP保安人员头像
	 * 
	 * @param inputStream
	 *            输入流
	 * @param inputLength
	 *            流长度
	 * @param fileName
	 *            文件名, 要求在在同一个存储路径里面保证唯一
	 * @param contentType
	 *            文件内容类型, 例如: "image/jpeg"
	 * @param second
	 *            有效时间, 单位为秒
	 * @return 0-成功, 1-失败
	 */
	public static int putSecurityStaffStream(InputStream inputStream, long inputLength, String fileName,
			String contentType, int second) {
		// 取APP保安人员头像的存储路径
		String pathRoot = OSSConfigure.getInstance().getAppSecurityStaffDir();
		String pathFile = pathRoot + fileName;

		return putFileSinged(inputStream, inputLength, pathFile, contentType, second);
	}

	/**
	 * 以InputStream上传行驶证照片
	 * 
	 * @param inputStream
	 *            输入流
	 * @param inputLength
	 *            流长度
	 * @param fileName
	 *            文件名, 要求在在同一个存储路径里面保证唯一
	 * @param contentType
	 *            文件内容类型, 例如: "image/jpeg"
	 * @param second
	 *            有效时间, 单位为秒
	 * @return 0-成功, 1-失败
	 */
	public static int putDrivingLicenseStream(InputStream inputStream, long inputLength, String fileName,
			String contentType, int second) {
		// 取行驶证照片的存储路径
		String pathRoot = OSSConfigure.getInstance().getAppDrivingLicenseDir();
		String pathFile = pathRoot + fileName;

		return putFileSinged(inputStream, inputLength, pathFile, contentType, second);
	}

	/**
	 * 本地文件上传到OSS
	 * 
	 * @param srcPathName
	 *            源文件路径和文件名, 例如: "E:\\ferrari.jpg"
	 * @param DestPathName
	 *            目标存储的文件路径和文件名, 不包含域名. 例如: "test/test.jpg"
	 * @param contentType
	 *            文件内容类型, 例如: "image/jpeg"
	 * @param second
	 *            有效时间, 单位为秒
	 * @return 0-成功, 1-失败
	 */
	public static int putFileSinged(String srcPathName, String DestPathName, String contentType, int second) {
		String keyId = OSSConfigure.getInstance().getAccessKeyId();
		String key = OSSConfigure.getInstance().getAccessKeySecret();
		String endpoint = OSSConfigure.getInstance().getEndpoint();
		String bucketName = OSSConfigure.getInstance().getBucketName();

		OSSClient client = null;
		try {
			// 创建OSS客户端
			client = new OSSClient(endpoint, keyId, key);
			client.createBucket(bucketName);

			// 设置过期时间
			Date expires = new Date(new Date().getTime() + 1000 * second);

			// 创建请求
			GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName,
					DestPathName);
			generatePresignedUrlRequest.setBucketName(bucketName);
			// HttpMethod为PUT
			generatePresignedUrlRequest.setMethod(HttpMethod.PUT);
			generatePresignedUrlRequest.setExpiration(expires);
			// ContentType必须设置, 否则签名失败
			generatePresignedUrlRequest.setContentType(contentType);
			// 生成签名的URL
			URL url = client.generatePresignedUrl(generatePresignedUrlRequest);

			File f = new File(srcPathName);
			InputStream inputStream = new FileInputStream(f);

			HashMap<String, String> requestHeaders = new HashMap<String, String>();
			requestHeaders.put("Content-Length", f.length() + "");
			requestHeaders.put("Content-Type", contentType);
			// 使用URL签名方式上传指定输入流
			PutObjectResult result = client.putObject(url, inputStream, f.length(), requestHeaders);
			// System.out.println(result.getETag());
			return 0;
		} catch (Exception e) {
			logger.error("以签名方式上传文件异常: " + e.getMessage());
		} finally {
			if (null != client) {
				client.shutdown();
			}
		}

		return 1;
	}

	/**
	 * 以InputStream上传文件到OSS
	 * 
	 * @param inputStream
	 *            输入流
	 * @param inputLength
	 *            输入流长度
	 * @param DestPathName
	 *            目标存储的文件路径和文件名, 不包含域名. 例如: "test/test.jpg"
	 * @param contentType
	 *            文件内容类型, 例如: "image/jpeg"
	 * @param second
	 *            有效时间, 单位为秒
	 * @return 0-成功, 1-失败
	 */
	public static int putFileSinged(InputStream inputStream, long inputLength, String DestPathName, String contentType,
			int second) {
		String keyId = OSSConfigure.getInstance().getAccessKeyId();
		String key = OSSConfigure.getInstance().getAccessKeySecret();
		String endpoint = OSSConfigure.getInstance().getEndpoint();
		String bucketName = OSSConfigure.getInstance().getBucketName();

		OSSClient client = null;
		try {
			// 创建OSS客户端
			client = new OSSClient(endpoint, keyId, key);
			client.createBucket(bucketName);

			// 设置过期时间
			Date expires = new Date(new Date().getTime() + 1000 * second);

			// 创建请求
			GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName,
					DestPathName);
			generatePresignedUrlRequest.setBucketName(bucketName);
			// HttpMethod为PUT
			generatePresignedUrlRequest.setMethod(HttpMethod.PUT);
			generatePresignedUrlRequest.setExpiration(expires);
			// ContentType必须设置, 否则签名失败
			generatePresignedUrlRequest.setContentType(contentType);
			// 生成签名的URL
			URL url = client.generatePresignedUrl(generatePresignedUrlRequest);

			HashMap<String, String> requestHeaders = new HashMap<String, String>();
			requestHeaders.put("Content-Length", inputLength + "");
			requestHeaders.put("Content-Type", contentType);
			// 使用URL签名方式上传指定输入流
			PutObjectResult result = client.putObject(url, inputStream, inputLength, requestHeaders);
			System.out.println(result.getETag());
			return 0;
		} catch (Exception e) {
			logger.error("以签名方式上传流异常: " + e.getMessage());
		} finally {
			if (null != client) {
				client.shutdown();
			}
		}

		return 1;
	}

	/**
	 * 以InputStream上传文件到OSS
	 * 
	 * @param inputStream
	 *            输入流
	 * @param DestPathName
	 *            目标存储的文件路径和文件名, 不包含域名. 例如: "test/test.jpg"
	 * @return 0-成功, 1-失败
	 */
	public static int putFileSinged(InputStream inputStream, String DestPathName) {
		String keyId = OSSConfigure.getInstance().getAccessKeyId();
		String key = OSSConfigure.getInstance().getAccessKeySecret();
		String endpoint = OSSConfigure.getInstance().getEndpoint();
		String bucketName = OSSConfigure.getInstance().getBucketName();

		OSSClient client = null;
		try {
			// 创建OSS客户端
			client = new OSSClient(endpoint, keyId, key);

			ObjectMetadata objectMeta = new ObjectMetadata();

			PutObjectResult result = client.putObject(bucketName, DestPathName, inputStream, objectMeta);
			System.out.println(result.getETag());
			return 0;
		} catch (Exception e) {
			logger.error("以签名方式上传流异常: " + e.getMessage());
		} finally {
			if (null != client) {
				client.shutdown();
			}
		}
		return 1;
	}

	/**
	 * 以InputStream上传文件到OSS
	 * 
	 * @param inputStream
	 *            输入流
	 * @param DestPathName
	 *            目标存储的文件路径和文件名, 不包含域名. 例如: "test/test.jpg"
	 * @return 0-成功, 1-失败
	 */
	public static int putFileSinged(InputStream inputStream, String DestPathName, String ContentType) {
		String keyId = OSSConfigure.getInstance().getAccessKeyId();
		String key = OSSConfigure.getInstance().getAccessKeySecret();
		String endpoint = OSSConfigure.getInstance().getEndpoint();
		String bucketName = OSSConfigure.getInstance().getBucketName();

		OSSClient client = null;
		try {
			// 创建OSS客户端
			client = new OSSClient(endpoint, keyId, key);

			ObjectMetadata objectMeta = new ObjectMetadata();

			objectMeta.setContentType(ContentType);
			objectMeta.setContentLength(inputStream.available());
			// objectMeta.setContentDisposition("inline");

			PutObjectResult result = client.putObject(bucketName, DestPathName, inputStream, objectMeta);
			System.out.println(result.getETag());
			return 0;
		} catch (Exception e) {
			logger.error("以签名方式上传流异常: " + e.getMessage());
		} finally {
			if (null != client) {
				client.shutdown();
			}
		}
		return 1;
	}

	/**
	 * 获取文件的签名URL
	 * 
	 * @param pathName
	 *            OSS的文件路径和文件名, 不包含域名. 例如: "test/test.jpg"
	 * @param second
	 *            url的有效时间, 单位为秒
	 * @return null-失败, 其它-成功
	 */
	public static String getUrlSigned(String pathName, int second) {
		String keyId = OSSConfigure.getInstance().getAccessKeyId();
		String key = OSSConfigure.getInstance().getAccessKeySecret();
		String endpoint = OSSConfigure.getInstance().getEndpoint();
		String bucketName = OSSConfigure.getInstance().getBucketName();

		Date expires = new Date(new Date().getTime() + 1000 * second);
		OSSClient client = null;
		try {
			client = new OSSClient(endpoint, keyId, key);
			URL url = client.generatePresignedUrl(bucketName, pathName, expires, HttpMethod.GET);
			return url.toString();
		} catch (Exception e) {
			logger.error("获取文件的签名URL异常: " + e.getMessage());
		} finally {
			if (null != client) {
				client.shutdown();
			}
		}

		return null;
	}

	/**
	 * 获取APP用户头像的存储路径
	 * 
	 * @return APP用户头像的存储路径
	 */
	public static String getAppAvatarDir() {
		return OSSConfigure.getInstance().getAppAvatarDir();
	}

	/**
	 * 获取APP保安人员头像的存储路径
	 * 
	 * @return APP保安人员头像的存储路径
	 */
	public static String getAppSecurityStaffDir() {
		return OSSConfigure.getInstance().getAppSecurityStaffDir();
	}

	/**
	 * 获取申诉时提交行驶证照片的存储路径
	 * 
	 * @return 行驶证照片的存储路径
	 */
	public static String getAppDrivingLicenseDir() {
		return OSSConfigure.getInstance().getAppDrivingLicenseDir();
	}

	/**
	 * 获取停车场图片的存储路径
	 * 
	 * @return 停车场图片的存储路径
	 */
	public static String getParkImgDir() {
		return OSSConfigure.getInstance().getParkImgDir();
	}

	/**
	 * 获取停车场场内地图的存储路径
	 * 
	 * @return 停车场场内地图的存储路径
	 */
	public static String getParkMapDir() {
		return OSSConfigure.getInstance().getParkMapDir();
	}

	/**
	 * 获取车辆入场照片的存储路径
	 * 
	 * @return 车辆入场照片的存储路径
	 */
	public static String getParkingEntranceDir() {

		return OSSConfigure.getInstance().getParkingEntranceDir();
	}

	/**
	 * 获取车辆离场照片的存储路径
	 * 
	 * @return 车辆离场照片的存储路径
	 */
	public static String getParkingDepartureDir() {
		return OSSConfigure.getInstance().getParkingDepartureDir();
	}

	/**
	 * 获取车辆在停车场内的照片存储路径
	 * 
	 * @return 车辆在停车场内的照片存储路径
	 */
	public static String getParkingLotDir() {
		return OSSConfigure.getInstance().getParkingLotDir();
	}

	/**
	 * 单元测试
	 * 
	 * @param args
	 */
	public static void main(String args[]) {

		putFileSinged(null, 0L, null, null, 0);
		// int res = putFileSinged("E:\\ferrari.jpg", "park/ferrari.jpg",
		// "image/jpeg", 600);
		// System.out.println(res);
		// String url = getUrlSigned("park/ferrari.jpg", 600);
		// System.out.println(url);

		// try { File f = new File("D:\\chart.png");
		// InputStream inputStream =
		// new FileInputStream(f);
		// String res = p(inputStream,
		// inputStream.available(), "chart.png", "image/*", 600);
		// System.out.println(res);
		//
		// String url = getUrlSigned(getParkImgDir() + res, 600);
		// System.out.println(url);
		//
		// } catch (Exception e) { System.out.println(e.getMessage()); }

		// System.out.println(new Date().getTime());

	}
}
