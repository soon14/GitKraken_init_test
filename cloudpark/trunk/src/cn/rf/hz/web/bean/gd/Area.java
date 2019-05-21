/**
 * &copy; 2012 杭州立方自动化有限公司
 */
package cn.rf.hz.web.bean.gd;



public class Area   {
	
		private Integer recordId;//   	private Integer areaId;//   区域ID(外部数据库)	private String areaName;//   区域名	private String picPath;//   区域图路径	private Integer carParkId;//   所属停车场ID	private java.util.Date updTime;//   
	private String floor;
	private Integer parkingNum;
	private Double poiX;
	private Double poiY;
	
	
		public String getFloor()
	{
		return floor;
	}
	public void setFloor(String floor)
	{
		this.floor = floor;
	}
	public Integer getParkingNum()
	{
		return parkingNum;
	}
	public void setParkingNum(Integer parkingNum)
	{
		this.parkingNum = parkingNum;
	}
	public Double getPoiX()
	{
		return poiX;
	}
	public void setPoiX(Double poiX)
	{
		this.poiX = poiX;
	}
	
	public Double getPoiY()
	{
		return poiY;
	}
	public void setPoiY(Double poiY)
	{
		this.poiY = poiY;
	}
	public Integer getRecordId() {	    return this.recordId;	}	public void setRecordId(Integer recordId) {	    this.recordId=recordId;	}	public Integer getAreaId() {	    return this.areaId;	}	public void setAreaId(Integer areaId) {	    this.areaId=areaId;	}	public String getAreaName() {	    return this.areaName;	}	public void setAreaName(String areaName) {	    this.areaName=areaName;	}	public String getPicPath() {	    return this.picPath;	}	public void setPicPath(String picPath) {	    this.picPath=picPath;	}	public Integer getCarParkId() {	    return this.carParkId;	}	public void setCarParkId(Integer carParkId) {	    this.carParkId=carParkId;	}	public java.util.Date getUpdTime() {	    return this.updTime;	}	public void setUpdTime(java.util.Date updTime) {	    this.updTime=updTime;	}
}
