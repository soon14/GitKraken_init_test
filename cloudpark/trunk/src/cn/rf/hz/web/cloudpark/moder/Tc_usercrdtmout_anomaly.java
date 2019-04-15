package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Tc_usercrdtmout_anomaly {
    private Integer recordid;

    private String username;

    private String carcode;

    private Integer chargeruleid;

    private String carlabel;

    private String carcolor;

    private Integer carstyleid;

    private Date outcrdtm;

    private Integer channelid;

    private String imagepath;

    private Date intime;

    private Float chargemoney;

    private Boolean isupload;

    private Integer operatorid;

    private String parkinglotno;

    public Integer getRecordid() {
        return recordid;
    }

    public void setRecordid(Integer recordid) {
        this.recordid = recordid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getCarcode() {
        return carcode;
    }

    public void setCarcode(String carcode) {
        this.carcode = carcode == null ? null : carcode.trim();
    }

    public Integer getChargeruleid() {
        return chargeruleid;
    }

    public void setChargeruleid(Integer chargeruleid) {
        this.chargeruleid = chargeruleid;
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

    public Date getOutcrdtm() {
        return outcrdtm;
    }

    public void setOutcrdtm(Date outcrdtm) {
        this.outcrdtm = outcrdtm;
    }

    public Integer getChannelid() {
        return channelid;
    }

    public void setChannelid(Integer channelid) {
        this.channelid = channelid;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath == null ? null : imagepath.trim();
    }

    public Date getIntime() {
        return intime;
    }

    public void setIntime(Date intime) {
        this.intime = intime;
    }

    public Float getChargemoney() {
        return chargemoney;
    }

    public void setChargemoney(Float chargemoney) {
        this.chargemoney = chargemoney;
    }

    public Boolean getIsupload() {
        return isupload;
    }

    public void setIsupload(Boolean isupload) {
        this.isupload = isupload;
    }

    public Integer getOperatorid() {
        return operatorid;
    }

    public void setOperatorid(Integer operatorid) {
        this.operatorid = operatorid;
    }

    public String getParkinglotno() {
        return parkinglotno;
    }

    public void setParkinglotno(String parkinglotno) {
        this.parkinglotno = parkinglotno == null ? null : parkinglotno.trim();
    }
}