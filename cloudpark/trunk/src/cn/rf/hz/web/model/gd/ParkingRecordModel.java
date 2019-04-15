package cn.rf.hz.web.model.gd;

import cn.rf.hz.web.model.BaseModel;

public class ParkingRecordModel extends BaseModel
{

	private String trad_no; // 收费信息中的recordID，交易号

	private Integer recordId;//
	private Integer parkingId;// 节点ID(外部数据库)
	private Integer oldRecordId;// 记录ID(外部数据库)
	private String licensePlateNumber;// 车牌号
	private java.util.Date inTime;//
	private String imgName;// 车牌图片
	private Integer groupId;// 集团ID
	private Integer mallId;// 商场ID

	private Integer carParkId;// 所属停车场ID

	private java.util.Date addTime;// 添加时间
	private String carNo; // 车牌号
	private Integer mark; // 是否 出场 默认0：未出场，1：已出场
	private String aliPartner; // 停车场收费帐户
	private Integer parentId;// 父节点ID
	private String barCode;// 购物条码，用于计算免停时间
	private String userID;
	private String appId;
	private Integer userType;
	private Integer urlType;
	private String src;

	public String getSrc()
	{
		return src;
	}

	public void setSrc(String src)
	{
		this.src = src;
	}

	public String getUserID()
	{
		return userID;
	}

	public void setUserID(String userID)
	{
		this.userID = userID; 
	}

	public Integer getUserType()
	{
		return userType;
	}

	public void setUserType(Integer userType)
	{
		this.userType = userType;
	}

	public Integer getUrlType()
	{
		return urlType;
	}

	public void setUrlType(Integer urlType)
	{
		this.urlType = urlType;
	}

	public String getAppId()
	{
		return appId;
	}

	public void setAppId(String appId)
	{
		this.appId = appId;
	}

	public String getBarCode()
	{
		return barCode;
	}

	public void setBarCode(String barCode)
	{
		this.barCode = barCode;
	}

	public Integer getParentId()
	{
		return parentId;
	}

	public void setParentId(Integer parentId)
	{
		this.parentId = parentId;
	}

	public String getAliPartner()
	{
		return aliPartner;
	}

	public void setAliPartner(String aliPartner)
	{
		this.aliPartner = aliPartner;
	}

	public String getTrad_no()
	{
		return trad_no;
	}

	public void setTrad_no(String tradNo)
	{
		trad_no = tradNo;
	}

	public Integer getRecordId()
	{
		return this.recordId;
	}

	public void setRecordId(Integer recordId)
	{
		this.recordId = recordId;
	}

	public Integer getParkingId()
	{
		return this.parkingId;
	}

	public void setParkingId(Integer parkingId)
	{
		this.parkingId = parkingId;
	}

	public Integer getOldRecordId()
	{
		return this.oldRecordId;
	}

	public void setOldRecordId(Integer oldRecordId)
	{
		this.oldRecordId = oldRecordId;
	}

	public String getLicensePlateNumber()
	{
		return this.licensePlateNumber;
	}

	public void setLicensePlateNumber(String licensePlateNumber)
	{
		this.licensePlateNumber = licensePlateNumber;
	}

	public java.util.Date getInTime()
	{
		return this.inTime;
	}

	public void setInTime(java.util.Date inTime)
	{
		this.inTime = inTime;
	}

	public String getImgName()
	{
		return this.imgName;
	}

	public void setImgName(String imgName)
	{
		this.imgName = imgName;
	}

	public Integer getCarParkId()
	{
		return this.carParkId;
	}

	public void setCarParkId(Integer carParkId)
	{
		this.carParkId = carParkId;
	}

	public Integer getGroupId()
	{
		return groupId;
	}

	public void setGroupId(Integer groupId)
	{
		this.groupId = groupId;
	}

	public Integer getMallId()
	{
		return mallId;
	}

	public void setMallId(Integer mallId)
	{
		this.mallId = mallId;
	}

	public java.util.Date getAddTime()
	{
		return this.addTime;
	}

	public void setAddTime(java.util.Date addTime)
	{
		this.addTime = addTime;
	}

	public String getCarNo()
	{
		return carNo;
	}

	public void setCarNo(String carNo)
	{
		this.carNo = carNo;
	}

	/**
	 * @return the mark
	 */
	public Integer getMark()
	{
		return mark;
	}

	/**
	 * @param mark
	 *            the mark to set
	 */
	public void setMark(Integer mark)
	{
		this.mark = mark;
	}
}
