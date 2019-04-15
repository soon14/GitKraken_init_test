package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Pb_station {
    private Integer recordid;

    private String stationno;

    private String stationname;

    private String stationip;

    private Integer port;

    private String serverip;

    private String mac;

    private String subnetmask;

    private String defaultgateway;

    private String firstdns;

    private String seconddns;

    private String version;

    private String serialnumber;

    private Integer serverport;

    private String stationdesc;

    private Integer channelid;

    private String stationtype;

    private String parkinglotno;

    private Date lastupdatetime;

    private Integer runstate;

    private Boolean isdelete;

    public Integer getRecordid() {
        return recordid;
    }

    public void setRecordid(Integer recordid) {
        this.recordid = recordid;
    }

    public String getStationno() {
        return stationno;
    }

    public void setStationno(String stationno) {
        this.stationno = stationno == null ? null : stationno.trim();
    }

    public String getStationname() {
        return stationname;
    }

    public void setStationname(String stationname) {
        this.stationname = stationname == null ? null : stationname.trim();
    }

    public String getStationip() {
        return stationip;
    }

    public void setStationip(String stationip) {
        this.stationip = stationip == null ? null : stationip.trim();
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getServerip() {
        return serverip;
    }

    public void setServerip(String serverip) {
        this.serverip = serverip == null ? null : serverip.trim();
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac == null ? null : mac.trim();
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

    public String getSerialnumber() {
        return serialnumber;
    }

    public void setSerialnumber(String serialnumber) {
        this.serialnumber = serialnumber == null ? null : serialnumber.trim();
    }

    public Integer getServerport() {
        return serverport;
    }

    public void setServerport(Integer serverport) {
        this.serverport = serverport;
    }

    public String getStationdesc() {
        return stationdesc;
    }

    public void setStationdesc(String stationdesc) {
        this.stationdesc = stationdesc == null ? null : stationdesc.trim();
    }

    public Integer getChannelid() {
        return channelid;
    }

    public void setChannelid(Integer channelid) {
        this.channelid = channelid;
    }

    public String getStationtype() {
        return stationtype;
    }

    public void setStationtype(String stationtype) {
        this.stationtype = stationtype == null ? null : stationtype.trim();
    }

    public String getParkinglotno() {
        return parkinglotno;
    }

    public void setParkinglotno(String parkinglotno) {
        this.parkinglotno = parkinglotno == null ? null : parkinglotno.trim();
    }

    public Date getLastupdatetime() {
        return lastupdatetime;
    }

    public void setLastupdatetime(Date lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }

    public Integer getRunstate() {
        return runstate;
    }

    public void setRunstate(Integer runstate) {
        this.runstate = runstate;
    }

    public Boolean getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(Boolean isdelete) {
        this.isdelete = isdelete;
    }
}