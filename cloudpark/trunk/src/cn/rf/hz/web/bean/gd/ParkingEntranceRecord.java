package cn.rf.hz.web.bean.gd;

import org.springframework.format.annotation.DateTimeFormat;


/**
 * 停车场入场记录类
 * 
 */
public class ParkingEntranceRecord  {
	private Integer recordId;  //记录ID
	private Integer oldRecordId; //记录ID(外部数据库)
	private Integer carParkId;	//停车场ID
	private String licensePlateNumber; //车牌号
	private String imgName; //车牌图片名称
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:SS")
	private java.util.Date inTime;  //进入停车记录时间
	private String inTimeStr; //进入停车场记录时间字符串;
	private Integer discountRate; //折扣率, 以百分比为单位的折扣率，例如折扣为80%，则该值为80
	private Integer discountTime; //折扣时间 ,单位为分
	private Integer discountMoney; //折扣款 ,单位为人民币分
	private java.util.Date addTime;//   添加时间
	private String addTimeStr; //添加时间字符串
	private Integer mark; //出场标记  默认 0:没有出场，1:已出场
	private Integer IsRecognition;//是否识别 默认0：未识别，1：已识别
	private String recognitionNumber;//识别后的车牌号
	private Integer groupId;	//集团Id
	private Integer mallId;	//商场Id
	
	
	public Integer getGroupId() {
		return groupId;
	}
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	public Integer getMallId() {
		return mallId;
	}
	public void setMallId(Integer mallId) {
		this.mallId = mallId;
	}
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
	 * @return the inTimeStr
	 */
	public String getInTimeStr() {
		return inTimeStr;
	}
	/**
	 * @param inTimeStr the inTimeStr to set
	 */
	public void setInTimeStr(String inTimeStr) {
		this.inTimeStr = inTimeStr;
	}
	/**
	 * @return the addTimeStr
	 */
	public String getAddTimeStr() {
		return addTimeStr;
	}
	/**
	 * @param addTimeStr the addTimeStr to set
	 */
	public void setAddTimeStr(String addTimeStr) {
		this.addTimeStr = addTimeStr;
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
