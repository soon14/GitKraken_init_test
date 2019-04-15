package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Tc_usercarinfo {
    private Integer recordid;

    private Integer userid;

    private String parkinglotno;

    private String carcode;

    private String carlabel;

    private String carcolor;

    private Integer carstyleid;

    private String personalname;

    private String personalphone;

    private Integer createuser;

    private Date createtime;

    private String remarks;

    public Integer getRecordid() {
        return recordid;
    }

    public void setRecordid(Integer recordid) {
        this.recordid = recordid;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getParkinglotno() {
        return parkinglotno;
    }

    public void setParkinglotno(String parkinglotno) {
        this.parkinglotno = parkinglotno == null ? null : parkinglotno.trim();
    }

    public String getCarcode() {
        return carcode;
    }

    public void setCarcode(String carcode) {
        this.carcode = carcode == null ? null : carcode.trim();
    }

    public String getCarlabel() {
        return carlabel;
    }

    public void setCarlabel(String carlabel) {
        this.carlabel = carlabel == null ? null : carlabel.trim();
    }

    public String getCarcolor() {
        return carcolor;
    }

    public void setCarcolor(String carcolor) {
        this.carcolor = carcolor == null ? null : carcolor.trim();
    }

    public Integer getCarstyleid() {
        return carstyleid;
    }

    public void setCarstyleid(Integer carstyleid) {
        this.carstyleid = carstyleid;
    }

    public String getPersonalname() {
        return personalname;
    }

    public void setPersonalname(String personalname) {
        this.personalname = personalname == null ? null : personalname.trim();
    }

    public String getPersonalphone() {
        return personalphone;
    }

    public void setPersonalphone(String personalphone) {
        this.personalphone = personalphone == null ? null : personalphone.trim();
    }

    public Integer getCreateuser() {
        return createuser;
    }

    public void setCreateuser(Integer createuser) {
        this.createuser = createuser;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }
}