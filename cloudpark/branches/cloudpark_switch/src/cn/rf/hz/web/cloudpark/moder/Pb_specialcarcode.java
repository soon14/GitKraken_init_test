package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Pb_specialcarcode {
    private Integer recordid;

    private String matchfirstcarcode;

    private String matchallcarcode;

    private String parkinglotno;

    private Integer createpeople;

    private Date createdate;

    private Integer updatepeople;

    private Date updatedate;

    private Integer isdelete;

    public Integer getRecordid() {
        return recordid;
    }

    public void setRecordid(Integer recordid) {
        this.recordid = recordid;
    }

    public String getMatchfirstcarcode() {
        return matchfirstcarcode;
    }

    public void setMatchfirstcarcode(String matchfirstcarcode) {
        this.matchfirstcarcode = matchfirstcarcode == null ? null : matchfirstcarcode.trim();
    }

    public String getMatchallcarcode() {
        return matchallcarcode;
    }

    public void setMatchallcarcode(String matchallcarcode) {
        this.matchallcarcode = matchallcarcode == null ? null : matchallcarcode.trim();
    }

    public String getParkinglotno() {
        return parkinglotno;
    }

    public void setParkinglotno(String parkinglotno) {
        this.parkinglotno = parkinglotno == null ? null : parkinglotno.trim();
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

    public Integer getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(Integer isdelete) {
        this.isdelete = isdelete;
    }
}