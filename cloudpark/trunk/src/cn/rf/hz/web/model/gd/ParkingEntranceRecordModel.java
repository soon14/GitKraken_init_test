package cn.rf.hz.web.model.gd;

import cn.rf.hz.web.model.BaseModel;

public class ParkingEntranceRecordModel extends BaseModel {
	private Integer recordId;  //记录ID
	private Integer oldRecordId; //记录ID(外部数据库)
	private Integer carParkId;	//停车场ID
	private String licensePlateNumber; //车牌号
	private String imgName; //车牌图片名称
	private java.util.Date inTime;  //进入停车记录时间
	private Integer discountRate; //折扣率, 以百分比为单位的折扣率，例如折扣为80%，则该值为80
	private Integer discountTime; //折扣时间 ,单位为分
	private Integer discountMoney; //折扣款 ,单位为人民币分
	private java.util.Date addTime;//   添加时间
	private Integer mark; //出场标记  默认 0:没有出场，1:已出场
	private Integer IsRecognition;//是否识别 默认0：未识别，1：已识别
	private String recognitionNumber;//识别后的车牌号
	
	private String carNo; //车牌号
	
	private Integer parkingRecordRecordId;  //场内记录ID  对应gd_license_recognize 表中字段 recordId
	private Integer parkingRecordParkingId;//场内 区域节点ID  对应gd_license_recognize 表中字段 parkingId
	private String parkingRecordImgName;// 场内车牌图片 对应gd_license_recognize 表中字段 imgName
	/**
	 * @return the recordId
	 */
	public Integer getRecordId() {
		return recordId;
	}
	/**
	 * @param recordId the recordId to set
	 */
	public void setRecordId(Integer recordId) {
		this.recordId = recordId;
	}
	/**
	 * @return the oldRecordId
	 */
	public Integer getOldRecordId() {
		return oldRecordId;
	}
	/**
	 * @param oldRecordId the oldRecordId to set
	 */
	public void setOldRecordId(Integer oldRecordId) {
		this.oldRecordId = oldRecordId;
	}
	/**
	 * @return the carParkId
	 */
	public Integer getCarParkId() {
		return carParkId;
	}
	/**
	 * @param carParkId the carParkId to set
	 */
	public void setCarParkId(Integer carParkId) {
		this.carParkId = carParkId;
	}
	/**
	 * @return the licensePlateNumber
	 */
	public String getLicensePlateNumber() {
		return licensePlateNumber;
	}
	/**
	 * @param licensePlateNumber the licensePlateNumber to set
	 */
	public void setLicensePlateNumber(String licensePlateNumber) {
		this.licensePlateNumber = licensePlateNumber;
	}
	/**
	 * @return the imgName
	 */
	public String getImgName() {
		return imgName;
	}
	/**
	 * @param imgName the imgName to set
	 */
	public void setImgName(String imgName) {
		this.imgName = imgName;
	}
	/**
	 * @return the inTime
	 */
	public java.util.Date getInTime() {
		return inTime;
	}
	/**
	 * @param inTime the inTime to set
	 */
	public void setInTime(java.util.Date inTime) {
		this.inTime = inTime;
	}
	/**
	 * @return the discountRate
	 */
	public Integer getDiscountRate() {
		return discountRate;
	}
	/**
	 * @param discountRate the discountRate to set
	 */
	public void setDiscountRate(Integer discountRate) {
		this.discountRate = discountRate;
	}
	/**
	 * @return the discountTime
	 */
	public Integer getDiscountTime() {
		return discountTime;
	}
	/**
	 * @param discountTime the discountTime to set
	 */
	public void setDiscountTime(Integer discountTime) {
		this.discountTime = discountTime;
	}
	/**
	 * @return the discountMoney
	 */
	public Integer getDiscountMoney() {
		return discountMoney;
	}
	/**
	 * @param discountMoney the discountMoney to set
	 */
	public void setDiscountMoney(Integer discountMoney) {
		this.discountMoney = discountMoney;
	}
	/**
	 * @return the addTime
	 */
	public java.util.Date getAddTime() {
		return addTime;
	}
	/**
	 * @param addTime the addTime to set
	 */
	public void setAddTime(java.util.Date addTime) {
		this.addTime = addTime;
	}
	/**
	 * @return the mark
	 */
	public Integer getMark() {
		return mark;
	}
	/**
	 * @param mark the mark to set
	 */
	public void setMark(Integer mark) {
		this.mark = mark;
	}
	/**
	 * @return the parkingRecordRecordId
	 */
	public Integer getParkingRecordRecordId() {
		return parkingRecordRecordId;
	}
	/**
	 * @param parkingRecordRecordId the parkingRecordRecordId to set
	 */
	public void setParkingRecordRecordId(Integer parkingRecordRecordId) {
		this.parkingRecordRecordId = parkingRecordRecordId;
	}
	/**
	 * @return the parkingRecordParkingId
	 */
	public Integer getParkingRecordParkingId() {
		return parkingRecordParkingId;
	}
	/**
	 * @param parkingRecordParkingId the parkingRecordParkingId to set
	 */
	public void setParkingRecordParkingId(Integer parkingRecordParkingId) {
		this.parkingRecordParkingId = parkingRecordParkingId;
	}
	/**
	 * @return the parkingRecordImgName
	 */
	public String getParkingRecordImgName() {
		return parkingRecordImgName;
	}
	/**
	 * @param parkingRecordImgName the parkingRecordImgName to set
	 */
	public void setParkingRecordImgName(String parkingRecordImgName) {
		this.parkingRecordImgName = parkingRecordImgName;
	}
	/**
	 * @return the carNo
	 */
	public String getCarNo() {
		return carNo;
	}
	/**
	 * @param carNo the carNo to set
	 */
	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}
	/**
	 * @return the isRecognition
	 */
	public Integer getIsRecognition() {
		return IsRecognition;
	}
	/**
	 * @param isRecognition the isRecognition to set
	 */
	public void setIsRecognition(Integer isRecognition) {
		IsRecognition = isRecognition;
	}
	/**
	 * @return the recognitionNumber
	 */
	public String getRecognitionNumber() {
		return recognitionNumber;
	}
	/**
	 * @param recognitionNumber the recognitionNumber to set
	 */
	public void setRecognitionNumber(String recognitionNumber) {
		this.recognitionNumber = recognitionNumber;
	}
	
}
