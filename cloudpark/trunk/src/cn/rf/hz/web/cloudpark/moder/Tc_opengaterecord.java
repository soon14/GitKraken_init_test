package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Tc_opengaterecord extends Tc_opengaterecordKey {

    private Integer channelid;

    private Integer gatetype;

    private String imagepath;

    private Integer operatorid;

    private Date operatordate;

    private Boolean isupload;

    private String parkinglotno;
    
    private String carcode;
    
    private String textreason;
    
    private String voicereason;


    public Integer getChannelid() {
        return channelid;
    }

    public void setChannelid(Integer channelid) {
        this.channelid = channelid;
    }

    public Integer getGatetype() {
        return gatetype;
    }

    public void setGatetype(Integer gatetype) {
        this.gatetype = gatetype;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath == null ? null : imagepath.trim();
    }

    public Integer getOperatorid() {
        return operatorid;
    }

    public void setOperatorid(Integer operatorid) {
        this.operatorid = operatorid;
    }

    public Date getOperatordate() {
        return operatordate;
    }

    public void setOperatordate(Date operatordate) {
        this.operatordate = operatordate;
    }

    public Boolean getIsupload() {
        return isupload;
    }

    public void setIsupload(Boolean isupload) {
        this.isupload = isupload;
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
    
    public String getTextreason() {
        return textreason;
    }

    public void setTextreason(String textreason) {
        this.textreason = textreason == null ? null : textreason.trim();
    }
    
    public String getVoicereason() {
        return voicereason;
    }

    public void setVoicereason(String voicereason) {
        this.voicereason = voicereason == null ? null : voicereason.trim();
    }
}