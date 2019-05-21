package cn.rf.hz.web.bean.gd;



/**
 * 停车场出场记录类
 * 
 */
public class ParkingExportRecord  {

	private static final long serialVersionUID = -278506271332986771L;
	
	private Integer recordId;  //记录ID
	private Integer oldRecordId; //记录ID(外部数据库)
	private Integer carParkId;	//停车场ID
	private String licensePlateNumber; //车牌号
	private String imgName; //车牌图片名称
	private java.util.Date inTime;  //进入停车记录时间
	private String inTimeStr; //进入停车场记录时间字符串;
	private java.util.Date addTime;//   添加时间
	private String addTimeStr; //添加时间字符串
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
	
}
