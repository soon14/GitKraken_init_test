package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Tc_channel {
    private Integer channelid;

    private String channelname;

    private Integer channeltype;

    private Integer mstationno;

    private Integer inorout;

    private Boolean isrepeatinout;

    private Boolean ischannelcontrol;

    private Integer parkingchannelid;

    private String parkinglotno;

    private Integer chargeruleid;

    private Integer createpeople;

    private Date createdate;

    private Integer updatepeople;

    private Date updatedate;

    private Boolean isdelete;

    private Integer deletepeople;

    private Date deletedate;

    private Integer cameraid;

    private Integer cameraid2;

    private Integer parkingareaid;

    private Integer gateid;
    
    private String channelcontrol;
    
    private Boolean isuploadrecord;
    
    private Boolean issendnotice;
    
    private Integer insideoroutside;
    
    private Boolean isallin;
    
    private Boolean ischarge;
    
    private Integer nextareaid; 

    public Integer getChannelid() {
        return channelid;
    }

    public void setChannelid(Integer channelid) {
        this.channelid = channelid;
    }

    public String getChannelname() {
        return channelname;
    }

    public void setChannelname(String channelname) {
        this.channelname = channelname == null ? null : channelname.trim();
    }

    public Integer getChanneltype() {
        return channeltype;
    }

    public void setChanneltype(Integer channeltype) {
        this.channeltype = channeltype;
    }

    public Integer getMstationno() {
        return mstationno;
    }

    public void setMstationno(Integer mstationno) {
        this.mstationno = mstationno;
    }

    public Integer getInorout() {
        return inorout;
    }

    public void setInorout(Integer inorout) {
        this.inorout = inorout;
    }

    public Boolean getIsrepeatinout() {
        return isrepeatinout;
    }

    public void setIsrepeatinout(Boolean isrepeatinout) {
        this.isrepeatinout = isrepeatinout;
    }

    public Boolean getIschannelcontrol() {
        return ischannelcontrol;
    }

    public void setIschannelcontrol(Boolean ischannelcontrol) {
        this.ischannelcontrol = ischannelcontrol;
    }

    public Integer getParkingchannelid() {
        return parkingchannelid;
    }

    public void setParkingchannelid(Integer parkingchannelid) {
        this.parkingchannelid = parkingchannelid;
    }

    public String getParkinglotno() {
        return parkinglotno;
    }

    public void setParkinglotno(String parkinglotno) {
        this.parkinglotno = parkinglotno == null ? null : parkinglotno.trim();
    }

    public Integer getChargeruleid() {
        return chargeruleid;
    }

    public void setChargeruleid(Integer chargeruleid) {
        this.chargeruleid = chargeruleid;
    }

    public Integer getCreatepeople() {
        return createpeople;
    }

    public void setCreatepeople(Integer createpeople) {
        this.createpeople = createpeople;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public Integer getUpdatepeople() {
        return updatepeople;
    }

    public void setUpdatepeople(Integer updatepeople) {
        this.updatepeople = updatepeople;
    }

    public Date getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(Date updatedate) {
        this.updatedate = updatedate;
    }

    public Boolean getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(Boolean isdelete) {
        this.isdelete = isdelete;
    }

    public Integer getDeletepeople() {
        return deletepeople;
    }

    public void setDeletepeople(Integer deletepeople) {
        this.deletepeople = deletepeople;
    }

    public Date getDeletedate() {
        return deletedate;
    }

    public void setDeletedate(Date deletedate) {
        this.deletedate = deletedate;
    }

    public Integer getCameraid() {
        return cameraid;
    }

    public void setCameraid(Integer cameraid) {
        this.cameraid = cameraid;
    }

    public Integer getCameraid2() {
        return cameraid2;
    }

    public void setCameraid2(Integer cameraid2) {
        this.cameraid2 = cameraid2;
    }

    public Integer getParkingareaid() {
        return parkingareaid;
    }

    public void setParkingareaid(Integer parkingareaid) {
        this.parkingareaid = parkingareaid;
    }

    public Integer getGateid() {
        return gateid;
    }

    public void setGateid(Integer gateid) {
        this.gateid = gateid;
    }
    
    public String getChannelcontrol() {
        return channelcontrol;
    }

    public void setChannelcontrol(String channelcontrol) {
        this.channelcontrol = channelcontrol;
    }
    
    
    public Boolean getIsuploadrecord() {
        return isuploadrecord;
    }

    public void setIsuploadrecord(Boolean isuploadrecord) {
        this.isuploadrecord = isuploadrecord;
    }

    public Boolean getIssendnotice() {
        return issendnotice;
    }

    public void setIssendnotice(Boolean issendnotice) {
        this.issendnotice = issendnotice;
    }
    
    
    public Integer getInsideoroutside() {
        return insideoroutside;
    }

    public void setInsideoroutside(Integer insideoroutside) {
        this.insideoroutside = insideoroutside;
    }
    
    public Boolean getIsallin() {
        return isallin;
    }

    public void setIsallin(Boolean isallin) {
        this.isallin = isallin;
    }
    
    
    public Boolean getIscharge() {
        return ischarge;
    }

    public void setIscharge(Boolean ischarge) {
        this.ischarge = ischarge;
    }
    
    public Integer getNextareaid() {
        return nextareaid;
    }

    public void setNextareaid(Integer nextareaid) {
        this.nextareaid = nextareaid;
    }
}