package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Activationcode {
    private Integer recordid;

    private Integer stationid;

    private String code;

    private Integer isused;

    private Integer createuserid;

    private Date createtime;

    private Integer isactivated;

    private Date activatedtime;

    private Integer isdelete;

    private Date deletetime;

    private Integer deleteuserid;

    private String parkinglotno;

    public Integer getRecordid() {
        return recordid;
    }

    public void setRecordid(Integer recordid) {
        this.recordid = recordid;
    }

    public Integer getStationid() {
        return stationid;
    }

    public void setStationid(Integer stationid) {
        this.stationid = stationid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Integer getIsused() {
        return isused;
    }

    public void setIsused(Integer isused) {
        this.isused = isused;
    }

    public Integer getCreateuserid() {
        return createuserid;
    }

    public void setCreateuserid(Integer createuserid) {
        this.createuserid = createuserid;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getIsactivated() {
        return isactivated;
    }

    public void setIsactivated(Integer isactivated) {
        this.isactivated = isactivated;
    }

    public Date getActivatedtime() {
        return activatedtime;
    }

    public void setActivatedtime(Date activatedtime) {
        this.activatedtime = activatedtime;
    }

    public Integer getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(Integer isdelete) {
        this.isdelete = isdelete;
    }

    public Date getDeletetime() {
        return deletetime;
    }

    public void setDeletetime(Date deletetime) {
        this.deletetime = deletetime;
    }

    public Integer getDeleteuserid() {
        return deleteuserid;
    }

    public void setDeleteuserid(Integer deleteuserid) {
        this.deleteuserid = deleteuserid;
    }

    public String getParkinglotno() {
        return parkinglotno;
    }

    public void setParkinglotno(String parkinglotno) {
        this.parkinglotno = parkinglotno == null ? null : parkinglotno.trim();
    }
}