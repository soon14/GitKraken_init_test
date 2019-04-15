package cn.rf.hz.web.model.gd;

import cn.rf.hz.web.model.BaseModel;

public class AreaModel extends BaseModel {
	

		private Integer recordId;//   	private Integer areaId;//   区域ID(外部数据库)	private String areaName;//   区域名	private String picPath;//   区域图路径	private Integer carParkId;//   所属停车场ID	private java.util.Date updTime;//   	public Integer getRecordId() {	    return this.recordId;	}	public void setRecordId(Integer recordId) {	    this.recordId=recordId;	}	public Integer getAreaId() {	    return this.areaId;	}	public void setAreaId(Integer areaId) {	    this.areaId=areaId;	}	public String getAreaName() {	    return this.areaName;	}	public void setAreaName(String areaName) {	    this.areaName=areaName;	}	public String getPicPath() {	    return this.picPath;	}	public void setPicPath(String picPath) {	    this.picPath=picPath;	}	public Integer getCarParkId() {	    return this.carParkId;	}	public void setCarParkId(Integer carParkId) {	    this.carParkId=carParkId;	}	public java.util.Date getUpdTime() {	    return this.updTime;	}	public void setUpdTime(java.util.Date updTime) {	    this.updTime=updTime;	}
	
}
