package cn.rf.hz.web.cloudpark.moder;

import java.math.BigDecimal;

public class Tc_charge_jm {
    private Integer jmtypeid;

    private String jmtypename;

    private Integer jmcode;

    private Integer jmkind;

    private BigDecimal jmmoney;

    private Float jmtime;

    private Float jmdiscount;

    private String remark;

    private Integer jmorder;

    private String parkinglotno;

    private Integer jmtype;

    public Integer getJmtypeid() {
        return jmtypeid;
    }

    public void setJmtypeid(Integer jmtypeid) {
        this.jmtypeid = jmtypeid;
    }

    public String getJmtypename() {
        return jmtypename;
    }

    public void setJmtypename(String jmtypename) {
        this.jmtypename = jmtypename == null ? null : jmtypename.trim();
    }

    public Integer getJmcode() {
        return jmcode;
    }

    public void setJmcode(Integer jmcode) {
        this.jmcode = jmcode;
    }

    public Integer getJmkind() {
        return jmkind;
    }

    public void setJmkind(Integer jmkind) {
        this.jmkind = jmkind;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getJmorder() {
        return jmorder;
    }

    public void setJmorder(Integer jmorder) {
        this.jmorder = jmorder;
    }

    public String getParkinglotno() {
        return parkinglotno;
    }

    public void setParkinglotno(String parkinglotno) {
        this.parkinglotno = parkinglotno == null ? null : parkinglotno.trim();
    }

    public Integer getJmtype() {
        return jmtype;
    }

    public void setJmtype(Integer jmtype) {
        this.jmtype = jmtype;
    }
}