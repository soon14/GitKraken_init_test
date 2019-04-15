package cn.rf.hz.web.model.gd;

import cn.rf.hz.web.model.BaseModel;

public class ParkingGroupModel extends BaseModel {
	

		private Integer recordId;//   	private Integer groupId;//   分组ID(外部数据库)	private Integer parkingId;//   停车位ID	private String groupName;//   分组名称	private java.util.Date updTime;//   	private Integer carParkId;//   所属停车场ID,外键	public Integer getRecordId() {	    return this.recordId;	}	public void setRecordId(Integer recordId) {	    this.recordId=recordId;	}	public Integer getGroupId() {	    return this.groupId;	}	public void setGroupId(Integer groupId) {	    this.groupId=groupId;	}	public Integer getParkingId() {	    return this.parkingId;	}	public void setParkingId(Integer parkingId) {	    this.parkingId=parkingId;	}	public String getGroupName() {	    return this.groupName;	}	public void setGroupName(String groupName) {	    this.groupName=groupName;	}	public java.util.Date getUpdTime() {	    return this.updTime;	}	public void setUpdTime(java.util.Date updTime) {	    this.updTime=updTime;	}	public Integer getCarParkId() {	    return this.carParkId;	}	public void setCarParkId(Integer carParkId) {	    this.carParkId=carParkId;	}
	
}
