package cn.rf.hz.web.model.gd;

import cn.rf.hz.web.model.BaseModel;

public class AreaNodeModel extends BaseModel {
	

		private Integer recordId;//   	private Integer nodeId;//   节点ID(外部数据库)	private Integer areaId;//   所属区域ID(外部数据库)	private java.lang.Double positionX;//   	private java.lang.Double positionY;//   	private Integer type;//   节点类型：0:转折点 1:查询机 2:出入口 3:摄像机 4:签停机 5:中央收费处 6:通道摄像机	private java.util.Date updTime;//   	private Integer carParkId;//   	public Integer getRecordId() {	    return this.recordId;	}	public void setRecordId(Integer recordId) {	    this.recordId=recordId;	}	public Integer getNodeId() {	    return this.nodeId;	}	public void setNodeId(Integer nodeId) {	    this.nodeId=nodeId;	}	public Integer getAreaId() {	    return this.areaId;	}	public void setAreaId(Integer areaId) {	    this.areaId=areaId;	}	public java.lang.Double getPositionX() {	    return this.positionX;	}	public void setPositionX(java.lang.Double positionX) {	    this.positionX=positionX;	}	public java.lang.Double getPositionY() {	    return this.positionY;	}	public void setPositionY(java.lang.Double positionY) {	    this.positionY=positionY;	}	public Integer getType() {	    return this.type;	}	public void setType(Integer type) {	    this.type=type;	}	public java.util.Date getUpdTime() {	    return this.updTime;	}	public void setUpdTime(java.util.Date updTime) {	    this.updTime=updTime;	}	public Integer getCarParkId() {	    return this.carParkId;	}	public void setCarParkId(Integer carParkId) {	    this.carParkId=carParkId;	}
	
}
