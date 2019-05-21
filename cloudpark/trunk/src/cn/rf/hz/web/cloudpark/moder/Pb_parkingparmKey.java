package cn.rf.hz.web.cloudpark.moder;

public class Pb_parkingparmKey {
    private String parkinglotno;

    private Integer parmid;

    public String getParkinglotno() {
        return parkinglotno;
    }

    public void setParkinglotno(String parkinglotno) {
        this.parkinglotno = parkinglotno == null ? null : parkinglotno.trim();
    }

    public Integer getParmid() {
        return parmid;
    }

    public void setParmid(Integer parmid) {
        this.parmid = parmid;
    }
}