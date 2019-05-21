package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Tc_whiteuserinfo {
    private Integer recordid;

    private String licenseplatenumber;

    private Date starttime;

    private Date endtine;

    private Integer usecount;

    private String remarks1;

    private Integer whitetype;

    private String parkingno;

    private Integer createpeople;

    private Date createdate;

    private Integer updatepeople;

    private Date updatedate;

    public Integer getRecordid() {
        return recordid;
    }

    public void setRecordid(Integer recordid) {
        this.recordid = recordid;
    }

    public String getLicenseplatenumber() {
        return licenseplatenumber;
    }

    public void setLicenseplatenumber(String licenseplatenumber) {
        this.licenseplatenumber = licenseplatenumber == null ? null : licenseplatenumber.trim();
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtine() {
        return endtine;
    }

    public void setEndtine(Date endtine) {
        this.endtine = endtine;
    }

    public Integer getUsecount() {
        return usecount;
    }

    public void setUsecount(Integer usecount) {
        this.usecount = usecount;
    }

    public String getRemarks1() {
        return remarks1;
    }

    public void setRemarks1(String remarks1) {
        this.remarks1 = remarks1 == null ? null : remarks1.trim();
    }

    public Integer getWhitetype() {
        return whitetype;
    }

    public void setWhitetype(Integer whitetype) {
        this.whitetype = whitetype;
    }

    public String getParkingno() {
        return parkingno;
    }

    public void setParkingno(String parkingno) {
        this.parkingno = parkingno == null ? null : parkingno.trim();
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
}