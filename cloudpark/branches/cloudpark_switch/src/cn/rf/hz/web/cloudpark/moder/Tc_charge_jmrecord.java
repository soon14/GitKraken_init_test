package cn.rf.hz.web.cloudpark.moder;

import java.math.BigDecimal;
import java.util.Date;

public class Tc_charge_jmrecord {
    private Integer recordid;

    private String sysno;

    private String username;

    private String serial;

    private String carcode;

    private Date intime;

    private Integer usertypeid;

    private Integer devicesysid;

    private Integer jmtypeid;

    private Integer jmnum;

    private BigDecimal jmmoney;

    private Float jmtime;

    private Float jmdiscount;

    private Integer jmdept;

    private String jmcodes;

    private String operatorid;

    private Date operatordate;

    private String parkinglotno;

    public Integer getRecordid() {
        return recordid;
    }

    public void setRecordid(Integer recordid) {
        this.recordid = recordid;
    }

    public String getSysno() {
        return sysno;
    }

    public void setSysno(String sysno) {
        this.sysno = sysno == null ? null : sysno.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial == null ? null : serial.trim();
    }

    public String getCarcode() {
        return carcode;
    }

    public void setCarcode(String carcode) {
        this.carcode = carcode == null ? null : carcode.trim();
    }

    public Date getIntime() {
        return intime;
    }

    public void setIntime(Date intime) {
        this.intime = intime;
    }

    public Integer getUsertypeid() {
        return usertypeid;
    }

    public void setUsertypeid(Integer usertypeid) {
        this.usertypeid = usertypeid;
    }

    public Integer getDevicesysid() {
        return devicesysid;
    }

    public void setDevicesysid(Integer devicesysid) {
        this.devicesysid = devicesysid;
    }

    public Integer getJmtypeid() {
        return jmtypeid;
    }

    public void setJmtypeid(Integer jmtypeid) {
        this.jmtypeid = jmtypeid;
    }

    public Integer getJmnum() {
        return jmnum;
    }

    public void setJmnum(Integer jmnum) {
        this.jmnum = jmnum;
    }

    public BigDecimal getJmmoney() {
        return jmmoney;
    }

    public void setJmmoney(BigDecimal jmmoney) {
        this.jmmoney = jmmoney;
    }

    public Float getJmtime() {
        return jmtime;
    }

    public void setJmtime(Float jmtime) {
        this.jmtime = jmtime;
    }

    public Float getJmdiscount() {
        return jmdiscount;
    }

    public void setJmdiscount(Float jmdiscount) {
        this.jmdiscount = jmdiscount;
    }

    public Integer getJmdept() {
        return jmdept;
    }

    public void setJmdept(Integer jmdept) {
        this.jmdept = jmdept;
    }

    public String getJmcodes() {
        return jmcodes;
    }

    public void setJmcodes(String jmcodes) {
        this.jmcodes = jmcodes == null ? null : jmcodes.trim();
    }

    public String getOperatorid() {
        return operatorid;
    }

    public void setOperatorid(String operatorid) {
        this.operatorid = operatorid == null ? null : operatorid.trim();
    }

    public Date getOperatordate() {
        return operatordate;
    }

    public void setOperatordate(Date operatordate) {
        this.operatordate = operatordate;
    }

    public String getParkinglotno() {
        return parkinglotno;
    }

    public void setParkinglotno(String parkinglotno) {
        this.parkinglotno = parkinglotno == null ? null : parkinglotno.trim();
    }
}