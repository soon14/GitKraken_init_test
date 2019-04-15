package com.reformer.cloudpark.model;

import java.io.Serializable;

public class CarparkInfo implements Serializable{

	private static final long serialVersionUID = 2139275980862103216L;

	private Integer deptid;

    private String deptduty;

    private String deptname;

    private String deptno;

    public Integer getDeptid() {
        return deptid;
    }

    public void setDeptid(Integer deptid) {
        this.deptid = deptid;
    }

    public String getDeptduty() {
        return deptduty;
    }

    public void setDeptduty(String deptduty) {
        this.deptduty = deptduty == null ? null : deptduty.trim();
    }

    public String getDeptname() {
        return deptname;
    }

    public void setDeptname(String deptname) {
        this.deptname = deptname == null ? null : deptname.trim();
    }

    public String getDeptno() {
        return deptno;
    }

    public void setDeptno(String deptno) {
        this.deptno = deptno == null ? null : deptno.trim();
    }
}