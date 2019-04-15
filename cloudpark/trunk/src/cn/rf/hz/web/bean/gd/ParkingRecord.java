package cn.rf.hz.web.bean.gd;


/**
 * 停车记录类
 * 两种情况，
 * 一种是1视频通道
 *  一种是0视频车位 
 */
public class ParkingRecord  {
	
	private Integer recordId;//   	private Integer parkingId;//   区域节点ID(外部数据库)对应gd_license_recognize 表字段parkingId；对应area_node 的nodeId	private Integer oldRecordId;//   记录ID(外部数据库)	private String licensePlateNumber;//   车牌号	private java.util.Date inTime;////停车时间   	private String imgName;//   车牌图片	private Integer carParkId;//   所属停车场ID	private java.util.Date addTime;//   添加时间
	private Integer mark; //是否 出场  默认0：未出场，1：已出场
	private Integer realParkingId;//线下数据库的车位ID
	private Integer groupId;
	private String mallId;
	
	
	
		public String getMallId()
	{
		return mallId;
	}
	public void setMallId(String mallId)
	{
		this.mallId = mallId;
	}
	public Integer getGroupId()
	{
		return groupId;
	}
	public void setGroupId(Integer groupId)
	{
		this.groupId = groupId;
	}
	public Integer getRealParkingId()
	{
		return realParkingId;
	}
	public void setRealParkingId(Integer realParkingId)
	{
		this.realParkingId = realParkingId;
	}
	public Integer getRecordId() {	    return this.recordId;	}	public void setRecordId(Integer recordId) {	    this.recordId=recordId;	}	public Integer getParkingId() {	    return this.parkingId;	}	public void setParkingId(Integer parkingId) {	    this.parkingId=parkingId;	}	public Integer getOldRecordId() {	    return this.oldRecordId;	}	public void setOldRecordId(Integer oldRecordId) {	    this.oldRecordId=oldRecordId;	}	public String getLicensePlateNumber() {	    return this.licensePlateNumber;	}	public void setLicensePlateNumber(String licensePlateNumber) {	    this.licensePlateNumber=licensePlateNumber;	}
	/**
	 * 停车时间
	 * @return
	 */	public java.util.Date getInTime() {	    return this.inTime;	}
	/**
	 * 停车时间
	 * @param inTime
	 */	public void setInTime(java.util.Date inTime) {	    this.inTime=inTime;	}	public String getImgName() {	    return this.imgName;	}	public void setImgName(String imgName) {	    this.imgName=imgName;	}	public Integer getCarParkId() {	    return this.carParkId;	}	public void setCarParkId(Integer carParkId) {	    this.carParkId=carParkId;	}	public java.util.Date getAddTime() {	    return this.addTime;	}	public void setAddTime(java.util.Date addTime) {	    this.addTime=addTime;	}
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
	
}
