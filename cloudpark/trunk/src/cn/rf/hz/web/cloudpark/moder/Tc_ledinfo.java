package cn.rf.hz.web.cloudpark.moder;

public class Tc_ledinfo {
    private Integer recordid;

    private Integer ledtype;

    private Integer ledno;

    private String ledname;

    private String ledip;

    private Integer port;

    private String mac;

    private String serialnumber;

    private String subnetmask;

    private String defaultgateway;

    private String firstdns;

    private String seconddns;

    private String version;

    private Integer channelid;

    private Integer parkingchannelid;

    private String parkinglotno;

    private Integer runstate;

    private String lednumno;

    private Integer stationid;

    public Integer getRecordid() {
        return recordid;
    }

    public void setRecordid(Integer recordid) {
        this.recordid = recordid;
    }

    public Integer getLedtype() {
        return ledtype;
    }

    public void setLedtype(Integer ledtype) {
        this.ledtype = ledtype;
    }

    public Integer getLedno() {
        return ledno;
    }

    public void setLedno(Integer ledno) {
        this.ledno = ledno;
    }

    public String getLedname() {
        return ledname;
    }

    public void setLedname(String ledname) {
        this.ledname = ledname == null ? null : ledname.trim();
    }

    public String getLedip() {
        return ledip;
    }

    public void setLedip(String ledip) {
        this.ledip = ledip == null ? null : ledip.trim();
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac == null ? null : mac.trim();
    }

    public String getSerialnumber() {
        return serialnumber;
    }

    public void setSerialnumber(String serialnumber) {
        this.serialnumber = serialnumber == null ? null : serialnumber.trim();
    }

    public String getSubnetmask() {
        return subnetmask;
    }

    public void setSubnetmask(String subnetmask) {
        this.subnetmask = subnetmask == null ? null : subnetmask.trim();
    }

    public String getDefaultgateway() {
        return defaultgateway;
    }

    public void setDefaultgateway(String defaultgateway) {
        this.defaultgateway = defaultgateway == null ? null : defaultgateway.trim();
    }

    public String getFirstdns() {
        return firstdns;
    }

    public void setFirstdns(String firstdns) {
        this.firstdns = firstdns == null ? null : firstdns.trim();
    }

    public String getSeconddns() {
        return seconddns;
    }

    public void setSeconddns(String seconddns) {
        this.seconddns = seconddns == null ? null : seconddns.trim();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public Integer getChannelid() {
        return channelid;
    }

    public void setChannelid(Integer channelid) {
        this.channelid = channelid;
    }

    public Integer getParkingchannelid() {
        return parkingchannelid;
    }

    public void setParkingchannelid(Integer parkingchannelid) {
        this.parkingchannelid = parkingchannelid;
    }

    public String getParkinglotno() {
        return parkinglotno;
    }

    public void setParkinglotno(String parkinglotno) {
        this.parkinglotno = parkinglotno == null ? null : parkinglotno.trim();
    }

    public Integer getRunstate() {
        return runstate;
    }

    public void setRunstate(Integer runstate) {
        this.runstate = runstate;
    }

    public String getLednumno() {
        return lednumno;
    }

    public void setLednumno(String lednumno) {
        this.lednumno = lednumno == null ? null : lednumno.trim();
    }

    public Integer getStationid() {
        return stationid;
    }

    public void setStationid(Integer stationid) {
        this.stationid = stationid;
    }
}