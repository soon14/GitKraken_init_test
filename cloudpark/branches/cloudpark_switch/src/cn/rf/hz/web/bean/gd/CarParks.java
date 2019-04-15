package cn.rf.hz.web.bean.gd;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.format.annotation.DateTimeFormat;


public class CarParks {
	
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1494070080121504470L;
	private Integer recordId;// 主鍵
	private Integer groupId;//集团id
	private Integer mallId;//商场id
	
	private String companyName;//   公司名称
	private String carParkName;//   停车场名称
	private String loginName;//   停车场通讯的登录账号
	private String loginPass;//   停车场通讯的登录密码
	private String machineCode;//   停车场服务器机器码
	private Integer parkingTotalNum;//   总的车位数
	private Integer parkingEmptyNum;//   空余车位数
	private String  parkLocation;//停车场地址
	private String parkDesc1;//层级关系描述例如：三层2847个车位
	private Integer parkPrice;//收费价格：单位：小时
	private String parkPriceDesc;//收费价格描述：例如：会员卡享8折优惠
	private String remarks;//   备注
	private java.lang.Boolean disabled;//   使用状态：默认为可用
	private java.lang.Boolean delFlag;//   逻辑删除标记
	private java.util.Date settingPerDayTime;//   配置信息：每天上传基础数据的时间点
	private java.lang.Double settingAreaPicScale;//   区域图片压缩比例：默认0.60
	private java.lang.Double settingCarNumberScale;//   车牌图片压缩比例：默认0.60
	private Integer settingReadRecordInterval;//   停车记录读取间隔的时间：单位秒
	private java.lang.Double latitude;//   纬度
	private java.lang.Double longitude;//   经度
	private Integer province;//   省份代码
	private Integer mycity;//   城市代码
	private String city;//   
	private Integer dist;//   县级代码
	private String address;//   地址
	private String link;//   停车场地图位置链接
	private Integer type;//   停车场类型：0 视频车位，1 视频通道
	private String contactName;//   联系人名称
	private String telephone;//   电话
	private String weixinId;//   
	private String picUrl;//   背景图片url
	private String title;//   页面title
	private String description;//   
	private java.util.Date startDate;//   付费开始时间
	private java.util.Date endDate;//   付费结束时间
	private java.util.Date createDate;//   创建时间
	private Integer parkPayType;//停车场付费类型 1：支持支付宝付费停车场  2： 普通付费停车场 3：信息停车场
	
	private String bannerUrl; //自助机头部背景图片
	private String qrCodeUrl;//自助机二维码处背景图片
	
	private Integer isPayment; //是否支持付费 : 0 支持，1 不支持
	private Integer dataType;  // 1 场内数据，2 进出口数据，3 场内和进出口都有
	private Integer isRecognition; //是否支持云识别：默认0：不，1：是
	private Integer parentId; //父节点Id
	private Integer subCount; //子停车场数量
	
	/* 业务员信息 */
	private String salesmanName; //业务员姓名
	private String salesmanCheckPassword; //查询密码
	private String salesmanPhoneNumber; //手机号码
	private double salesmanInterestRate;//分润利率
	private Integer salesmanCarParkId; //停车场ID
	private Integer salesmanState; //状态 默认0
	private java.util.Date salesmanCreateDate;//创建日期
	private java.util.Date salesmanUpdateDate;//更新日期
	private String aliPartner;
	
	
	
	private Integer nodeId1;
	private Integer nodeId2;
	private Double positionX1;
	private Double positionY1;
	private Double positionX2;
	private Double positionY2;
	
	
	
	
	private Integer createBy;
	private Integer updateBy;
	
	private String recordid;
	private String parkid;
	private String parkname;
	private Double price;
	private Double distance;
	private String staffId;
	
	
	
	
	public String getRecordid()
	{
		return recordid;
	}
	public void setRecordid(String recordid)
	{
		this.recordid = recordid;
	}
	public String getParkid()
	{
		return parkid;
	}
	public void setParkid(String parkid)
	{
		this.parkid = parkid;
	}
	public String getStaffId()
	{
		return staffId;
	}
	public void setStaffId(String staffId)
	{
		this.staffId = staffId;
	}
	public String getParkname()
	{
		return parkname;
	}
	public void setParkname(String parkname)
	{
		this.parkname = parkname;
	}
	public Double getPrice()
	{
		return price;
	}
	public void setPrice(Double price)
	{
		this.price = price;
	}
	public Double getDistance()
	{
		return distance;
	}
	public void setDistance(Double distance)
	{
		this.distance = distance;
	}
	public String getAliPartner()
	{
		return aliPartner;
	}
	public void setAliPartner(String aliPartner)
	{
		this.aliPartner = aliPartner;
	}
	public Integer getNodeId1()
	{
		return nodeId1;
	}
	public void setNodeId1(Integer nodeId1)
	{
		this.nodeId1 = nodeId1;
	}
	public Integer getNodeId2()
	{
		return nodeId2;
	}
	public void setNodeId2(Integer nodeId2)
	{
		this.nodeId2 = nodeId2;
	}
	public Double getPositionX1()
	{
		return positionX1;
	}
	public void setPositionX1(Double positionX1)
	{
		this.positionX1 = positionX1;
	}
	public Double getPositionY1()
	{
		return positionY1;
	}
	public void setPositionY1(Double positionY1)
	{
		this.positionY1 = positionY1;
	}
	public Double getPositionX2()
	{
		return positionX2;
	}
	public void setPositionX2(Double positionX2)
	{
		this.positionX2 = positionX2;
	}
	public Double getPositionY2()
	{
		return positionY2;
	}
	public void setPositionY2(Double positionY2)
	{
		this.positionY2 = positionY2;
	}
	public Integer getParkPayType() {
		return parkPayType;
	}
	public void setParkPayType(Integer parkPayType) {
		this.parkPayType = parkPayType;
	}
	/**
	 * 下行服务器更新数据时间
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")   
	private java.util.Date updTime;//   
	/**
	 * 更新停车场信息时间
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")   
	private java.util.Date updateDate;// 
	
	// 特征字符串，优惠验证使用
	private String trait="";
	
	public Integer getRecordId() {
		return recordId;
	}
	public void setRecordId(Integer recordId) {
		this.recordId = recordId;
	}
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
	public String getCarParkName() {
		return carParkName;
	}
	public void setCarParkName(String carParkName) {
		this.carParkName = carParkName;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getLoginPass() {
		return loginPass;
	}
	public void setLoginPass(String loginPass) {
		this.loginPass = loginPass;
	}
	public String getMachineCode() {
		return machineCode;
	}
	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}
	public Integer getParkingTotalNum() {
		return parkingTotalNum;
	}
	public void setParkingTotalNum(Integer parkingTotalNum) {
		this.parkingTotalNum = parkingTotalNum;
	}
	public Integer getParkingEmptyNum() {
		return parkingEmptyNum;
	}
	public void setParkingEmptyNum(Integer parkingEmptyNum) {
		this.parkingEmptyNum = parkingEmptyNum;
	}
	public String getRemarks() {
		return remarks;
	}
	
	
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
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public java.lang.Boolean getDisabled() {
		return disabled;
	}
	public void setDisabled(java.lang.Boolean disabled) {
		this.disabled = disabled;
	}
	public java.lang.Boolean getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(java.lang.Boolean delFlag) {
		this.delFlag = delFlag;
	}
	public java.util.Date getUpdTime() {
		return updTime;
	}
	public void setUpdTime(java.util.Date updTime) {
		this.updTime = updTime;
	}
	
	public java.util.Date getSettingPerDayTime() {
		return settingPerDayTime;
	}
	public void setSettingPerDayTime(java.util.Date settingPerDayTime) {
		this.settingPerDayTime = settingPerDayTime;
	}
	public java.lang.Double getSettingAreaPicScale() {
		return settingAreaPicScale;
	}
	public void setSettingAreaPicScale(java.lang.Double settingAreaPicScale) {
		this.settingAreaPicScale = settingAreaPicScale;
	}
	public java.lang.Double getSettingCarNumberScale() {
		return settingCarNumberScale;
	}
	public void setSettingCarNumberScale(java.lang.Double settingCarNumberScale) {
		this.settingCarNumberScale = settingCarNumberScale;
	}
	public Integer getSettingReadRecordInterval() {
		return settingReadRecordInterval;
	}
	public void setSettingReadRecordInterval(Integer settingReadRecordInterval) {
		this.settingReadRecordInterval = settingReadRecordInterval;
	}
	public java.lang.Double getLatitude() {
		return latitude;
	}
	public void setLatitude(java.lang.Double latitude) {
		this.latitude = latitude;
	}
	public java.lang.Double getLongitude() {
		return longitude;
	}
	public void setLongitude(java.lang.Double longitude) {
		this.longitude = longitude;
	}
	public Integer getProvince() {
		return province;
	}
	public void setProvince(Integer province) {
		this.province = province;
	}
	public Integer getMycity() {
		return mycity;
	}
	public void setMycity(Integer mycity) {
		this.mycity = mycity;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public Integer getDist() {
		return dist;
	}
	public void setDist(Integer dist) {
		this.dist = dist;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getWeixinId() {
		return weixinId;
	}
	public void setWeixinId(String weixinId) {
		this.weixinId = weixinId;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public java.util.Date getStartDate() {
		return startDate;
	}
	public void setStartDate(java.util.Date startDate) {
		this.startDate = startDate;
	}
	public java.util.Date getEndDate() {
		return endDate;
	}
	public void setEndDate(java.util.Date endDate) {
		this.endDate = endDate;
	}
	public java.util.Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(java.util.Date createDate) {
		this.createDate = createDate;
	}
	
	
	
	public java.util.Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(java.util.Date updateDate) {
		this.updateDate = updateDate;
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
	 * @return the salesmanName
	 */
	public String getSalesmanName() {
		return salesmanName;
	}
	/**
	 * @param salesmanName the salesmanName to set
	 */
	public void setSalesmanName(String salesmanName) {
		this.salesmanName = salesmanName;
	}
	/**
	 * @return the salesmanCheckPassword
	 */
	public String getSalesmanCheckPassword() {
		return salesmanCheckPassword;
	}
	/**
	 * @param salesmanCheckPassword the salesmanCheckPassword to set
	 */
	public void setSalesmanCheckPassword(String salesmanCheckPassword) {
		this.salesmanCheckPassword = salesmanCheckPassword;
	}
	/**
	 * @return the salesmanPhoneNumber
	 */
	public String getSalesmanPhoneNumber() {
		return salesmanPhoneNumber;
	}
	/**
	 * @param salesmanPhoneNumber the salesmanPhoneNumber to set
	 */
	public void setSalesmanPhoneNumber(String salesmanPhoneNumber) {
		this.salesmanPhoneNumber = salesmanPhoneNumber;
	}
	/**
	 * @return the salesmanInterestRate
	 */
	public double getSalesmanInterestRate() {
		return salesmanInterestRate;
	}
	/**
	 * @param salesmanInterestRate the salesmanInterestRate to set
	 */
	public void setSalesmanInterestRate(double salesmanInterestRate) {
		this.salesmanInterestRate = salesmanInterestRate;
	}
	/**
	 * @return the salesmanCarParkId
	 */
	public Integer getSalesmanCarParkId() {
		return salesmanCarParkId;
	}
	/**
	 * @param salesmanCarParkId the salesmanCarParkId to set
	 */
	public void setSalesmanCarParkId(Integer salesmanCarParkId) {
		this.salesmanCarParkId = salesmanCarParkId;
	}
	/**
	 * @return the salesmanState
	 */
	public Integer getSalesmanState() {
		return salesmanState;
	}
	/**
	 * @param salesmanState the salesmanState to set
	 */
	public void setSalesmanState(Integer salesmanState) {
		this.salesmanState = salesmanState;
	}
	/**
	 * @return the salesmanCreateDate
	 */
	public java.util.Date getSalesmanCreateDate() {
		return salesmanCreateDate;
	}
	/**
	 * @param salesmanCreateDate the salesmanCreateDate to set
	 */
	public void setSalesmanCreateDate(java.util.Date salesmanCreateDate) {
		this.salesmanCreateDate = salesmanCreateDate;
	}
	/**
	 * @return the salesmanUpdateDate
	 */
	public java.util.Date getSalesmanUpdateDate() {
		return salesmanUpdateDate;
	}
	/**
	 * @param salesmanUpdateDate the salesmanUpdateDate to set
	 */
	public void setSalesmanUpdateDate(java.util.Date salesmanUpdateDate) {
		this.salesmanUpdateDate = salesmanUpdateDate;
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
	/**
	 * @return the subCount
	 */
	public Integer getSubCount() {
		return subCount;
	}
	/**
	 * @param subCount the subCount to set
	 */
	public void setSubCount(Integer subCount) {
		this.subCount = subCount;
	}
	public Integer getCreateBy() {
		return createBy;
	}
	public void setCreateBy(Integer createBy) {
		this.createBy = createBy;
	}
	public Integer getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(Integer updateBy) {
		this.updateBy = updateBy;
	}
	
}
