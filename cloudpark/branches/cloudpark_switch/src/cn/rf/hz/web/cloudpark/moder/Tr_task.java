package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Tr_task {
    private Integer fid;

    private String fparkingno;

    private String fparkingname;

    private Date fjobtime;

    private String fipaddress;

    private Date fcreatetime;

    private Date fmodifytime;

    private Boolean fstate;

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public String getFparkingno() {
        return fparkingno;
    }

    public void setFparkingno(String fparkingno) {
        this.fparkingno = fparkingno == null ? null : fparkingno.trim();
    }

    public String getFparkingname() {
        return fparkingname;
    }

    public void setFparkingname(String fparkingname) {
        this.fparkingname = fparkingname == null ? null : fparkingname.trim();
    }

    public Date getFjobtime() {
        return fjobtime;
    }

    public void setFjobtime(Date fjobtime) {
        this.fjobtime = fjobtime;
    }

    public String getFipaddress() {
        return fipaddress;
    }

    public void setFipaddress(String fipaddress) {
        this.fipaddress = fipaddress == null ? null : fipaddress.trim();
    }

    public Date getFcreatetime() {
        return fcreatetime;
    }

    public void setFcreatetime(Date fcreatetime) {
        this.fcreatetime = fcreatetime;
    }

    public Date getFmodifytime() {
        return fmodifytime;
    }

    public void setFmodifytime(Date fmodifytime) {
        this.fmodifytime = fmodifytime;
    }

    public Boolean getFstate() {
        return fstate;
    }

    public void setFstate(Boolean fstate) {
        this.fstate = fstate;
    }
}