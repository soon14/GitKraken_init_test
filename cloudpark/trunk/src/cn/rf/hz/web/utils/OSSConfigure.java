package cn.rf.hz.web.utils;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * 阿里云OSS配置信息 从ossConfig.properties读取
 * 
 * @author cent
 *
 */
public class OSSConfigure {
	private static final OSSConfigure ossConfigure = new OSSConfigure();
	private final static Logger logger = Logger.getLogger(OSSConfigure.class);
	private String endpoint = "";
	private String bucketName = "";
	private String accessKeyId = "";
	private String accessKeySecret = "";
	private String appAvatarDir = "";
	private String appSecurityStaffDir = "";
	private String appDrivingLicenseDir = "";
	private String parkImgDir = "";
	private String parkMapDir = "";
	private String parkingEntranceDir = "";
	private String parkingDepartureDir = "";
	private String parkingLotDir = "";
	private String parkingOpengateVoiceDir = "";
	// parkImgDir=persistence/park_img/
	// parkMapDir=persistence/park_map/
	// parkingEntranceDir=temporary/parking_entrance/
	// parkingDepartureDir=temporary/parking_departure/

	private OSSConfigure() {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("ossConfig");
			// 取OSS的端点
			try {
				endpoint = bundle.getString("ossEndpoint").trim();
			} catch (Exception e) {
				logger.error("取endpoint异常" + e.getMessage());
			}
			// 取OSS Bucket名
			try {
				bucketName = bundle.getString("ossBucketName").trim();
			} catch (Exception e) {
				logger.error("取bucketName异常" + e.getMessage());
			}
			// 取OSS AccessKey ID
			try {
				accessKeyId = bundle.getString("ossAccessKeyId").trim();
			} catch (Exception e) {
				logger.error("取accessKeyId异常" + e.getMessage());
			}
			// 取OSS AccessKey
			try {
				accessKeySecret = bundle.getString("ossAccessKeySecret").trim();
			} catch (Exception e) {
				logger.error("取accessKeySecret异常" + e.getMessage());
			}
			/*
			 * // 取APP的用户头像存储路径 try { appAvatarDir =
			 * bundle.getString("appAvatarDir").trim(); } catch (Exception e) {
			 * logger.error("取appAvatarDir异常" + e.getMessage()); } //
			 * 取APP的保安人员头像存储路径 try { appSecurityStaffDir =
			 * bundle.getString("appSecurityStaffDir").trim(); } catch
			 * (Exception e) { logger.error("取appSecurityStaffDir异常" +
			 * e.getMessage()); } // 取APP行驶证照片存储路径 try { appDrivingLicenseDir =
			 * bundle.getString("appDrivingLicenseDir").trim(); } catch
			 * (Exception e) { logger.error("取appDrivingLicenseDir异常" +
			 * e.getMessage()); } // 取停车场图片的存储路径 try { parkImgDir =
			 * bundle.getString("parkImgDir").trim(); } catch (Exception e) {
			 * logger.error("取parkImgDir异常" + e.getMessage()); } //
			 * 取停车场场内地图的存储路径 try { parkMapDir =
			 * bundle.getString("parkMapDir").trim(); } catch (Exception e) {
			 * logger.error("取parkMapDir异常" + e.getMessage()); }
			 */
			// 取车辆入场照片的存储路径
			try {
				parkingEntranceDir = bundle.getString("parkingEntranceDir").trim();
			} catch (Exception e) {
				logger.error("取parkingEntranceDir异常" + e.getMessage());
			}
			// 取车辆离场照片的存储路径
			try {
				parkingDepartureDir = bundle.getString("parkingDepartureDir").trim();
			} catch (Exception e) {
				logger.error("取parkingDepartureDir异常" + e.getMessage());
			}
			// 取车辆离场照片的存储路径
			try {
				parkingOpengateVoiceDir = bundle.getString("parkingOpengateVoiceDir").trim();
			} catch (Exception e) {
				logger.error("取parkingOpengateVoiceDir异常" + e.getMessage());
			}
		} catch (Exception e) {
			logger.error("读ossConfig文件异常" + e.getMessage());
		}
	}

	public static OSSConfigure getInstance() {
		return ossConfigure;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public String getAccessKeySecret() {
		return accessKeySecret;
	}

	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}

	public String getAppAvatarDir() {
		return appAvatarDir;
	}

	public void setAppAvatarDir(String appAvatarDir) {
		this.appAvatarDir = appAvatarDir;
	}

	public String getAppSecurityStaffDir() {
		return appSecurityStaffDir;
	}

	public void setAppSecurityStaffDir(String appSecurityStaffDir) {
		this.appSecurityStaffDir = appSecurityStaffDir;
	}

	public String getAppDrivingLicenseDir() {
		return appDrivingLicenseDir;
	}

	public void setAppDrivingLicenseDir(String appDrivingLicenseDir) {
		this.appDrivingLicenseDir = appDrivingLicenseDir;
	}

	public String getParkImgDir() {
		return parkImgDir;
	}

	public void setParkImgDir(String parkImgDir) {
		this.parkImgDir = parkImgDir;
	}

	public String getParkMapDir() {
		return parkMapDir;
	}

	public void setParkMapDir(String parkMapDir) {
		this.parkMapDir = parkMapDir;
	}

	public String getParkingEntranceDir() {
		return parkingEntranceDir;
	}

	public void setParkingEntranceDir(String parkingEntranceDir) {
		this.parkingEntranceDir = parkingEntranceDir;
	}

	public String getParkingDepartureDir() {
		return parkingDepartureDir;
	}

	public void setParkingDepartureDir(String parkingDepartureDir) {
		this.parkingDepartureDir = parkingDepartureDir;
	}

	public String getParkingLotDir() {
		return parkingLotDir;
	}

	public void setParkingLotDir(String parkingLotDir) {
		this.parkingLotDir = parkingLotDir;
	}

	public String getParkingOpengateVoiceDir() {
		return parkingOpengateVoiceDir;
	}

	public void setParkingOpengateVoiceDir(String parkingOpengateVoiceDir) {
		this.parkingOpengateVoiceDir = parkingOpengateVoiceDir;
	}
}
