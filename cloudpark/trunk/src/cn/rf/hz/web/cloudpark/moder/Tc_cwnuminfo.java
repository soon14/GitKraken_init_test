package cn.rf.hz.web.cloudpark.moder;

public class Tc_cwnuminfo {
    private Integer recordid;

    private String parkinglotno;

    private Integer areaid;

    private Integer countcw;

    private Integer stopedcw;

    private Integer prepcw;

    public Integer getRecordid() {
        return recordid;
    }

    public void setRecordid(Integer recordid) {
        this.recordid = recordid;
    }

    public String getParkinglotno() {
        return parkinglotno;
    }

    public void setParkinglotno(String parkinglotno) {
        this.parkinglotno = parkinglotno == null ? null : parkinglotno.trim();
    }

    public Integer getAreaid() {
        return areaid;
    }

    public void setAreaid(Integer areaid) {
        this.areaid = areaid;
    }

    public Integer getCountcw() {
        return countcw;
    }

    public void setCountcw(Integer countcw) {
        this.countcw = countcw;
    }

    public Integer getStopedcw() {
        return stopedcw;
    }

    public void setStopedcw(Integer stopedcw) {
        this.stopedcw = stopedcw;
    }

    public Integer getPrepcw() {
        return prepcw;
    }

    public void setPrepcw(Integer prepcw) {
        this.prepcw = prepcw;
    }
}