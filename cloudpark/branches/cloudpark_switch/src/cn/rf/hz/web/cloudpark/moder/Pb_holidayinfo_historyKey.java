package cn.rf.hz.web.cloudpark.moder;

public class Pb_holidayinfo_historyKey {
    private Integer id;

    private String parkinglotno;

    private String versionnumber;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getParkinglotno() {
        return parkinglotno;
    }

    public void setParkinglotno(String parkinglotno) {
        this.parkinglotno = parkinglotno == null ? null : parkinglotno.trim();
    }

    public String getVersionnumber() {
        return versionnumber;
    }

    public void setVersionnumber(String versionnumber) {
        this.versionnumber = versionnumber == null ? null : versionnumber.trim();
    }
}