package cn.rf.hz.web.model.gd;

import cn.rf.hz.web.model.BaseModel;

public class ParkDeviceModel extends BaseModel {
	

	private Integer id;// 
	private Integer num;//id
	private String name;//  
	private String bannerUrl;//   banner图片url
	private String qrCodeUrl;//   二维码图片url
	private String parkinfo;//   停车场收费html信息
	private String description;//
	private java.lang.Boolean disabled;//   使用状态：默认为可用
	private java.util.Date createDate;//   
	private java.util.Date updateDate;//   
	private Integer carParkId;//   停车场Id
	private Integer type;//   1是手持设备，2是自助机
	private Integer nodeId;//节点ID
	
	public Integer getId() {
	    return this.id;
	}
	public void setId(Integer id) {
	    this.id=id;
	}
	
	public java.lang.Boolean getDisabled() {
		return disabled;
	}
	public void setDisabled(java.lang.Boolean disabled) {
		this.disabled = disabled;
	}
	public String getBannerUrl() {
		return bannerUrl;
	}
	public void setBannerUrl(String bannerUrl) {
		this.bannerUrl = bannerUrl;
	}
	public String getQrCodeUrl() {
		return qrCodeUrl;
	}
	public void setQrCodeUrl(String qrCodeUrl) {
		this.qrCodeUrl = qrCodeUrl;
	}
	public String getParkinfo() {
		return parkinfo;
	}
	public void setParkinfo(String parkinfo) {
		this.parkinfo = parkinfo;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getName() {
	    return this.name;
	}
	public void setName(String name) {
	    this.name=name;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public java.util.Date getCreateDate() {
	    return this.createDate;
	}
	public void setCreateDate(java.util.Date createDate) {
	    this.createDate=createDate;
	}
	public java.util.Date getUpdateDate() {
	    return this.updateDate;
	}
	public void setUpdateDate(java.util.Date updateDate) {
	    this.updateDate=updateDate;
	}
	public Integer getCarParkId() {
	    return this.carParkId;
	}
	public void setCarParkId(Integer carParkId) {
	    this.carParkId=carParkId;
	}
	public Integer getType() {
	    return this.type;
	}
	public void setType(Integer type) {
	    this.type=type;
	}
	/**
	 * @return the nodeId
	 */
	public Integer getNodeId() {
		return nodeId;
	}
	/**
	 * @param nodeId the nodeId to set
	 */
	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}
	
}
