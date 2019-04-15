package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Tc_usercrdtm_abnormal extends Tc_usercrdtm_abnormalKey {
    private String username;

    private String carcode;

    private Integer chargeruleid;

    private String carlabel;

    private String carcolor;

    private Integer carstyleid;

    private Date crdtm;

    private Integer channelid;

    private String imagepath;

    private String recognizecarcode;

    private String note;

    private String carcode2;

    private String parkinglotno;

    private String parkinglocation;

    private Date createtime;

    private Date lastouttime;

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

    public Date getCrdtm() {
        return crdtm;
    }

    public void setCrdtm(Date crdtm) {
        this.crdtm = crdtm;
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

    public String getRecognizecarcode() {
        return recognizecarcode;
    }

    public void setRecognizecarcode(String recognizecarcode) {
        this.recognizecarcode = recognizecarcode == null ? null : recognizecarcode.trim();
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? null : note.trim();
    }

    public String getCarcode2() {
        return carcode2;
    }

    public void setCarcode2(String carcode2) {
        this.carcode2 = carcode2 == null ? null : carcode2.trim();
    }

    public String getParkinglotno() {
        return parkinglotno;
    }

    public void setParkinglotno(String parkinglotno) {
        this.parkinglotno = parkinglotno == null ? null : parkinglotno.trim();
    }

    public String getParkinglocation() {
        return parkinglocation;
    }

    public void setParkinglocation(String parkinglocation) {
        this.parkinglocation = parkinglocation == null ? null : parkinglocation.trim();
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getLastouttime() {
        return lastouttime;
    }

    public void setLastouttime(Date lastouttime) {
        this.lastouttime = lastouttime;
    }
}