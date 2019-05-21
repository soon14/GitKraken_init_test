package cn.rf.hz.web.model.gd;

import org.apache.commons.lang.builder.ToStringBuilder;

import cn.rf.hz.web.model.BaseModel;

public class CarParksModel extends BaseModel {
	

		private Integer recordId;//   
	private String companyName;//   公司名称	private String carParkName;//   停车场名称	private String loginName;//   停车场通讯的登录账号	private String loginPass;//   停车场通讯的登录密码	private String machineCode;//   停车场服务器机器码	private Integer parkingTotalNum;//   总的车位数	private Integer parkingEmptyNum;//   空余车位数
	
	private String  parkLocation;//停车场地址
	private String parkDesc1;//层级关系描述例如：三层2847个车位
	private Integer parkPrice;//收费价格：单位：小时
	private String parkPriceDesc;//收费价格描述：例如：会员卡享8折优惠
		private String remarks;//   备注	private java.lang.Boolean disabled;//   使用状态：默认为可用	private java.lang.Boolean delFlag;//   逻辑删除标记	private java.util.Date settingPerDayTime;//   配置信息：每天上传基础数据的时间点	private java.lang.Double settingAreaPicScale;//   区域图片压缩比例：默认0.60	private java.lang.Double settingCarNumberScale;//   车牌图片压缩比例：默认0.60	private Integer settingReadRecordInterval;//   停车记录读取间隔的时间：单位秒	private java.lang.Double latitude;//   纬度	private java.lang.Double longitude;//   经度	private Integer province;//   省份代码	private Integer mycity;//   城市代码	private String city;//   	private Integer dist;//   县级代码	private String address;//   地址	private String link;//   停车场地图位置链接	private Integer type;//   停车场类型：0 视频车位，1 视频通道	private String contactName;//   联系人名称	private String telephone;//   电话	private String weixinId;//   	private String picUrl;//   背景图片url	private String title;//   页面title	private String description;//   	private java.util.Date startDate;//   付费开始时间	private java.util.Date endDate;//   付费结束时间	private java.util.Date createDate;//   创建时间
	private Integer parkPayType;//停车场付费类型 1：支持支付宝付费停车场  2： 普通付费停车场 3：信息停车场
	private String bannerUrl; //自助机头部背景图片
	private String qrCodeUrl;//自助机二维码处背景图片
	private Integer isPayment; //是否支持付费 : 0 支持，1 不支持
	private Integer dataType;  // 1 场内数据，2 进出口数据，3 场内和进出口都有
	private Integer isRecognition; //是否支持云识别：默认0：不，1：是
	private Integer parentId;//父节点ID
	
	/**
	 * 数据库更新时间
	 */
	private java.util.Date updateDate;
	/**
	 * 服务器下行数据提交更新时间
	 */	private java.util.Date updTime;//   	
	private String value;//关键字
	/**
	 * 续费规则:多少天提示待续费用户，默认三十天
	 */
	private Integer renewDay;
	
	// 特征字符串，优惠验证使用
	private String trait;
		public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Integer getRecordId() {	    return this.recordId;	}	public void setRecordId(Integer recordId) {	    this.recordId=recordId;	}	public String getCarParkName() {	    return this.carParkName;	}	public void setCarParkName(String carParkName) {	    this.carParkName=carParkName;	}	public String getLoginName() {	    return this.loginName;	}	public void setLoginName(String loginName) {	    this.loginName=loginName;	}	public String getLoginPass() {	    return this.loginPass;	}	public void setLoginPass(String loginPass) {	    this.loginPass=loginPass;	}	public String getMachineCode() {	    return this.machineCode;	}	public void setMachineCode(String machineCode) {	    this.machineCode=machineCode;	}	public Integer getParkingTotalNum() {	    return this.parkingTotalNum;	}	public void setParkingTotalNum(Integer parkingTotalNum) {	    this.parkingTotalNum=parkingTotalNum;	}	public Integer getParkingEmptyNum() {	    return this.parkingEmptyNum;	}	public void setParkingEmptyNum(Integer parkingEmptyNum) {	    this.parkingEmptyNum=parkingEmptyNum;	}
		public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getParkLocation() {
		return parkLocation;
	}
	public void setParkLocation(String parkLocation) {
		this.parkLocation = parkLocation;
	}
	public String getParkDesc1() {
		return parkDesc1;
	}
	public void setParkDesc1(String parkDesc1) {
		this.parkDesc1 = parkDesc1;
	}
	public Integer getParkPrice() {
		return parkPrice;
	}
	public void setParkPrice(Integer parkPrice) {
		this.parkPrice = parkPrice;
	}
	public String getParkPriceDesc() {
		return parkPriceDesc;
	}
	public void setParkPriceDesc(String parkPriceDesc) {
		this.parkPriceDesc = parkPriceDesc;
	}
	public String getRemarks() {	    return this.remarks;	}	public void setRemarks(String remarks) {	    this.remarks=remarks;	}	public java.lang.Boolean getDisabled() {	    return this.disabled;	}	public void setDisabled(java.lang.Boolean disabled) {	    this.disabled=disabled;	}	public java.lang.Boolean getDelFlag() {	    return this.delFlag;	}	public void setDelFlag(java.lang.Boolean delFlag) {	    this.delFlag=delFlag;	}	public java.util.Date getUpdTime() {	    return this.updTime;	}	public void setUpdTime(java.util.Date updTime) {	    this.updTime=updTime;	}		public java.util.Date getSettingPerDayTime() {
		return settingPerDayTime;
	}
	public void setSettingPerDayTime(java.util.Date settingPerDayTime) {
		this.settingPerDayTime = settingPerDayTime;
	}
	public java.lang.Double getSettingAreaPicScale() {	    return this.settingAreaPicScale;	}	public void setSettingAreaPicScale(java.lang.Double settingAreaPicScale) {	    this.settingAreaPicScale=settingAreaPicScale;	}	public java.lang.Double getSettingCarNumberScale() {	    return this.settingCarNumberScale;	}	public void setSettingCarNumberScale(java.lang.Double settingCarNumberScale) {	    this.settingCarNumberScale=settingCarNumberScale;	}	public Integer getSettingReadRecordInterval() {	    return this.settingReadRecordInterval;	}	public void setSettingReadRecordInterval(Integer settingReadRecordInterval) {	    this.settingReadRecordInterval=settingReadRecordInterval;	}	public java.lang.Double getLatitude() {	    return this.latitude;	}	public void setLatitude(java.lang.Double latitude) {	    this.latitude=latitude;	}	public java.lang.Double getLongitude() {	    return this.longitude;	}	public void setLongitude(java.lang.Double longitude) {	    this.longitude=longitude;	}	public Integer getProvince() {	    return this.province;	}	public void setProvince(Integer province) {	    this.province=province;	}	public Integer getMycity() {	    return this.mycity;	}	public void setMycity(Integer mycity) {	    this.mycity=mycity;	}	public String getCity() {	    return this.city;	}	public void setCity(String city) {	    this.city=city;	}	public Integer getDist() {	    return this.dist;	}	public void setDist(Integer dist) {	    this.dist=dist;	}	public String getAddress() {	    return this.address;	}	public void setAddress(String address) {	    this.address=address;	}	public String getLink() {	    return this.link;	}	public void setLink(String link) {	    this.link=link;	}	public Integer getType() {	    return this.type;	}	public void setType(Integer type) {	    this.type=type;	}	public String getContactName() {	    return this.contactName;	}	public void setContactName(String contactName) {	    this.contactName=contactName;	}	public String getTelephone() {	    return this.telephone;	}	public void setTelephone(String telephone) {	    this.telephone=telephone;	}	public String getWeixinId() {	    return this.weixinId;	}	public void setWeixinId(String weixinId) {	    this.weixinId=weixinId;	}	public String getPicUrl() {	    return this.picUrl;	}	public void setPicUrl(String picUrl) {	    this.picUrl=picUrl;	}	public String getTitle() {	    return this.title;	}	public void setTitle(String title) {	    this.title=title;	}	public String getDescription() {	    return this.description;	}	public void setDescription(String description) {	    this.description=description;	}	public java.util.Date getStartDate() {	    return this.startDate;	}	public void setStartDate(java.util.Date startDate) {	    this.startDate=startDate;	}	public java.util.Date getEndDate() {	    return this.endDate;	}	public void setEndDate(java.util.Date endDate) {	    this.endDate=endDate;	}	public java.util.Date getCreateDate() {	    return this.createDate;	}	public void setCreateDate(java.util.Date createDate) {	    this.createDate=createDate;	}
	
	
	public java.util.Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(java.util.Date updateDate) {
		this.updateDate = updateDate;
	}
	public Integer getRenewDay() {
		return renewDay;
	}
	public void setRenewDay(Integer renewDay) {
		this.renewDay = renewDay;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);  
	}
	public String getTrait() {
		return trait;
	}
	public void setTrait(String trait) {
		this.trait = trait;
	}
	public Integer getParkPayType() {
		return parkPayType;
	}
	public void setParkPayType(Integer parkPayType) {
		this.parkPayType = parkPayType;
	}
	/**
	 * @return the bannerUrl
	 */
	public String getBannerUrl() {
		return bannerUrl;
	}
	/**
	 * @param bannerUrl the bannerUrl to set
	 */
	public void setBannerUrl(String bannerUrl) {
		this.bannerUrl = bannerUrl;
	}
	/**
	 * @return the qrCodeUrl
	 */
	public String getQrCodeUrl() {
		return qrCodeUrl;
	}
	/**
	 * @param qrCodeUrl the qrCodeUrl to set
	 */
	public void setQrCodeUrl(String qrCodeUrl) {
		this.qrCodeUrl = qrCodeUrl;
	}
	/**
	 * @return the isPayment
	 */
	public Integer getIsPayment() {
		return isPayment;
	}
	/**
	 * @param isPayment the isPayment to set
	 */
	public void setIsPayment(Integer isPayment) {
		this.isPayment = isPayment;
	}
	/**
	 * @return the dataType
	 */
	public Integer getDataType() {
		return dataType;
	}
	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}
	/**
	 * @return the isRecognition
	 */
	public Integer getIsRecognition() {
		return isRecognition;
	}
	/**
	 * @param isRecognition the isRecognition to set
	 */
	public void setIsRecognition(Integer isRecognition) {
		this.isRecognition = isRecognition;
	}
	/**
	 * @return the parentId
	 */
	public Integer getParentId() {
		return parentId;
	}
	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	
}
