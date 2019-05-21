package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Tc_usercrdtm extends Tc_usercrdtmKey {
    private String username;

    private String carcode;

    private Integer chargeruleid;

    private String carlabel;

    private String carcolor;

    private Integer carstyleid;

    private Integer inorout;

    private Date crdtm;


	private Date lastoutTime;

    private Integer channelid;

    private String imagepath;

    private String oldcarcode;

    private Boolean isupload;

    private String carcode2;

    private String parkinglotno;

    private String parkinglocation;

    

	private Integer carintype;
    
	private String areaid;
	
    public String getAreaId() {
		return areaid;
	}

	public void setAreaId(String areaId) {
		this.areaid = areaId;
	}

	public Integer getCarintype() {
		return carintype;
	}

	public void setCarintype(Integer carintype) {
		this.carintype = carintype;
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

    public Integer getInorout() {
        return inorout;
    }

    public void setInorout(Integer inorout) {
        this.inorout = inorout;
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

    public String getOldcarcode() {
        return oldcarcode;
    }

    public void setOldcarcode(String oldcarcode) {
        this.oldcarcode = oldcarcode == null ? null : oldcarcode.trim();
    }

    public Boolean getIsupload() {
        return isupload;
    }

    public void setIsupload(Boolean isupload) {
        this.isupload = isupload;
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

	public Date getLastoutTime() {
		return lastoutTime;
	}

	public void setLastoutTime(Date lastoutTime) {
		this.lastoutTime = lastoutTime;
	}
    
}