package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Tc_recognitioncamera {
    private Integer cameraid;

    private String cameraname;

    private String cameraip;

    private Integer cameraport;

    private String camerausername;

    private String camerauserpwd;

    private Integer cameratype;

    private Integer createpeople;

    private Date createdate;

    private Integer updatepeople;

    private Date updatedate;

    private Integer isstartwhite;

    private String parkinglotno;

    private Integer stationid;

    private String serialnumber;
    
    public Integer getCameraid() {
        return cameraid;
    }

    public void setCameraid(Integer cameraid) {
        this.cameraid = cameraid;
    }

    public String getCameraname() {
        return cameraname;
    }

    public void setCameraname(String cameraname) {
        this.cameraname = cameraname == null ? null : cameraname.trim();
    }

    public String getCameraip() {
        return cameraip;
    }

    public void setCameraip(String cameraip) {
        this.cameraip = cameraip == null ? null : cameraip.trim();
    }

    public Integer getCameraport() {
        return cameraport;
    }

    public void setCameraport(Integer cameraport) {
        this.cameraport = cameraport;
    }

    public String getCamerausername() {
        return camerausername;
    }

    public void setCamerausername(String camerausername) {
        this.camerausername = camerausername == null ? null : camerausername.trim();
    }

    public String getCamerauserpwd() {
        return camerauserpwd;
    }

    public void setCamerauserpwd(String camerauserpwd) {
        this.camerauserpwd = camerauserpwd == null ? null : camerauserpwd.trim();
    }

    public Integer getCameratype() {
        return cameratype;
    }

    public void setCameratype(Integer cameratype) {
        this.cameratype = cameratype;
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

    public Integer getIsstartwhite() {
        return isstartwhite;
    }

    public void setIsstartwhite(Integer isstartwhite) {
        this.isstartwhite = isstartwhite;
    }

    public String getParkinglotno() {
        return parkinglotno;
    }

    public void setParkinglotno(String parkinglotno) {
        this.parkinglotno = parkinglotno == null ? null : parkinglotno.trim();
    }

    public Integer getStationid() {
        return stationid;
    }

    public void setStationid(Integer stationid) {
        this.stationid = stationid;
    }
    
    public String getSerialnumber() {
        return serialnumber;
    }

    public void setSerialnumber(String serialnumber) {
        this.serialnumber = serialnumber == null ? null : serialnumber.trim();
    }
}