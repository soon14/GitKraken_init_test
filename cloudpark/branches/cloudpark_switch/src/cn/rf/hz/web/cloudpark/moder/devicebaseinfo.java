package cn.rf.hz.web.cloudpark.moder;

public class devicebaseinfo {
    private Integer recordid;

    private String devicetype;

    private String serialnumber;

    private String encryptcode;

    private String deviceip;

    public Integer getRecordid() {
        return recordid;
    }

    public void setRecordid(Integer recordid) {
        this.recordid = recordid;
    }

    public String getDevicetype() {
        return devicetype;
    }

    public void setDevicetype(String devicetype) {
        this.devicetype = devicetype == null ? null : devicetype.trim();
    }

    public String getSerialnumber() {
        return serialnumber;
    }

    public void setSerialnumber(String serialnumber) {
        this.serialnumber = serialnumber == null ? null : serialnumber.trim();
    }

    public String getEncryptcode() {
        return encryptcode;
    }

    public void setEncryptcode(String encryptcode) {
        this.encryptcode = encryptcode == null ? null : encryptcode.trim();
    }

    public String getDeviceip() {
        return deviceip;
    }

    public void setDeviceip(String deviceip) {
        this.deviceip = deviceip == null ? null : deviceip.trim();
    }
}