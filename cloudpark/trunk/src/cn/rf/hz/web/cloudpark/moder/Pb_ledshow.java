package cn.rf.hz.web.cloudpark.moder;

public class Pb_ledshow {
    private Integer recordid;

    private String ip;

    private Integer line;

    private Integer color;

    private Integer method;

    private Integer scrolldelay;

    private Integer staytime;

    private Integer ideltime;

    private Integer swdelay;

    private Boolean toflash;

    private Integer programid;

    private Integer ledtype;

    private String content;

    private String parkinglotno;

    private Integer channelid;

    private String ledname;

    private Integer pid;

    public Integer getRecordid() {
        return recordid;
    }

    public void setRecordid(Integer recordid) {
        this.recordid = recordid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public Integer getMethod() {
        return method;
    }

    public void setMethod(Integer method) {
        this.method = method;
    }

    public Integer getScrolldelay() {
        return scrolldelay;
    }

    public void setScrolldelay(Integer scrolldelay) {
        this.scrolldelay = scrolldelay;
    }

    public Integer getStaytime() {
        return staytime;
    }

    public void setStaytime(Integer staytime) {
        this.staytime = staytime;
    }

    public Integer getIdeltime() {
        return ideltime;
    }

    public void setIdeltime(Integer ideltime) {
        this.ideltime = ideltime;
    }

    public Integer getSwdelay() {
        return swdelay;
    }

    public void setSwdelay(Integer swdelay) {
        this.swdelay = swdelay;
    }

    public Boolean getToflash() {
        return toflash;
    }

    public void setToflash(Boolean toflash) {
        this.toflash = toflash;
    }

    public Integer getProgramid() {
        return programid;
    }

    public void setProgramid(Integer programid) {
        this.programid = programid;
    }

    public Integer getLedtype() {
        return ledtype;
    }

    public void setLedtype(Integer ledtype) {
        this.ledtype = ledtype;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getParkinglotno() {
        return parkinglotno;
    }

    public void setParkinglotno(String parkinglotno) {
        this.parkinglotno = parkinglotno == null ? null : parkinglotno.trim();
    }

    public Integer getChannelid() {
        return channelid;
    }

    public void setChannelid(Integer channelid) {
        this.channelid = channelid;
    }

    public String getLedname() {
        return ledname;
    }

    public void setLedname(String ledname) {
        this.ledname = ledname == null ? null : ledname.trim();
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }
}