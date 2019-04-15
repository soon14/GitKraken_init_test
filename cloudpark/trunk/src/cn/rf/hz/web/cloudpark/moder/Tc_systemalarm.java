package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Tc_systemalarm {
    private Integer recordid;

    private Integer channleid;

    private Integer alarmtype;

    private String alarmdesc;

    private Date alarmdate;

    private Boolean isupload;

    private String parkinglotno;

    public Integer getRecordid() {
        return recordid;
    }

    public void setRecordid(Integer recordid) {
        this.recordid = recordid;
    }

    public Integer getChannleid() {
        return channleid;
    }

    public void setChannleid(Integer channleid) {
        this.channleid = channleid;
    }

    public Integer getAlarmtype() {
        return alarmtype;
    }

    public void setAlarmtype(Integer alarmtype) {
        this.alarmtype = alarmtype;
    }

    public String getAlarmdesc() {
        return alarmdesc;
    }

    public void setAlarmdesc(String alarmdesc) {
        this.alarmdesc = alarmdesc == null ? null : alarmdesc.trim();
    }

    public Date getAlarmdate() {
        return alarmdate;
    }

    public void setAlarmdate(Date alarmdate) {
        this.alarmdate = alarmdate;
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
}