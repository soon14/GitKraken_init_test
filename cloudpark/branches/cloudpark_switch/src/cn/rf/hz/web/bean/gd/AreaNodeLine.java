package cn.rf.hz.web.bean.gd;



public class AreaNodeLine  {
	
		private Integer recordId;//   	private Integer oldRecordId;//   记录ID(外部数据库)	private Integer nodeId1;//   区域节点ID1(外部数据库)	private Integer nodeId2;//   区域节点ID2(外部数据库)	
	private AreaNode areaNode1;    	//节点1	private AreaNode areaNode2;    	//节点2
	private Area area;             	//所属区域
	
	
	private Integer areaId;//   
	private java.util.Date updTime;//   	private Integer carParkId;//   	public Integer getRecordId() {	    return this.recordId;	}	public void setRecordId(Integer recordId) {	    this.recordId=recordId;	}	public Integer getOldRecordId() {	    return this.oldRecordId;	}	public void setOldRecordId(Integer oldRecordId) {	    this.oldRecordId=oldRecordId;	}	public Integer getNodeId1() {	    return this.nodeId1;	}	public void setNodeId1(Integer nodeId1) {	    this.nodeId1=nodeId1;	}	public Integer getNodeId2() {	    return this.nodeId2;	}	public void setNodeId2(Integer nodeId2) {	    this.nodeId2=nodeId2;	}	public Integer getAreaId() {	    return this.areaId;	}	public void setAreaId(Integer areaId) {	    this.areaId=areaId;	}	public java.util.Date getUpdTime() {	    return this.updTime;	}	public void setUpdTime(java.util.Date updTime) {	    this.updTime=updTime;	}	public Integer getCarParkId() {	    return this.carParkId;	}	public void setCarParkId(Integer carParkId) {	    this.carParkId=carParkId;	}
	public AreaNode getAreaNode1() {
		return areaNode1;
	}
	public void setAreaNode1(AreaNode areaNode1) {
		this.areaNode1 = areaNode1;
	}
	public AreaNode getAreaNode2() {
		return areaNode2;
	}
	public void setAreaNode2(AreaNode areaNode2) {
		this.areaNode2 = areaNode2;
	}
	public Area getArea() {
		return area;
	}
	public void setArea(Area area) {
		this.area = area;
	}
	
	
}
