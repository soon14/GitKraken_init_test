package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Tc_chargerecordinfo extends Tc_chargerecordinfoKey {

    private Integer areaid;

    private Integer channelid;

    private Integer chargeruleid;

    private String chargeuserid;

    private Integer amounttype;

    private String emplyno;

    private String emplyname;

    private String carcode;

    private Integer carstyleid;

    private String carlabel;

    private String carcolor;

    private Date intime;

    private Date outtime;

    private String stoptime;

    private Float realchargeamount;

    private Date chargedate;

    private Float reductionamount;

    private String reductiontype;

    private Integer chargetype;

    private Integer inrecordid;

    private Integer outrecordid;

    private String reductionsname;

    private String barcodeno;

    private Float chargemoney;

    private Float chargeamount;

    private Float notchargeamount;

    private Boolean ischarge;

    private Integer reliefhour;

    private Boolean isupload;

    private Date lastouttime;

    private String chargesource;

    private Integer deviceid;

    private Integer payrecordid;

    private String parkinglotno;

    private String remarks1;
    private String remarks2;


    public Integer getAreaid() {
        return areaid;
    }

    public void setAreaid(Integer areaid) {
        this.areaid = areaid;
    }
    
    public String getRemarks1() {
        return remarks1;
    }

    public void setRemarks1(String remarks1) {
        this.remarks1 = remarks1;
    }

    public String getRemarks2() {
        return remarks2;
    }

    public void setRemarks2(String remarks2) {
        this.remarks2 = remarks2;
    }

    public Integer getChannelid() {
        return channelid;
    }

    public void setChannelid(Integer channelid) {
        this.channelid = channelid;
    }

    public Integer getChargeruleid() {
        return chargeruleid;
    }

    public void setChargeruleid(Integer chargeruleid) {
        this.chargeruleid = chargeruleid;
    }

    public String getChargeuserid() {
        return chargeuserid;
    }

    public void setChargeuserid(String chargeuserid) {
        this.chargeuserid = chargeuserid == null ? null : chargeuserid.trim();
    }

    public Integer getAmounttype() {
        return amounttype;
    }

    public void setAmounttype(Integer amounttype) {
        this.amounttype = amounttype;
    }

    public String getEmplyno() {
        return emplyno;
    }

    public void setEmplyno(String emplyno) {
        this.emplyno = emplyno == null ? null : emplyno.trim();
    }

    public String getEmplyname() {
        return emplyname;
    }

    public void setEmplyname(String emplyname) {
        this.emplyname = emplyname == null ? null : emplyname.trim();
    }

    public String getCarcode() {
        return carcode;
    }

    public void setCarcode(String carcode) {
        this.carcode = carcode == null ? null : carcode.trim();
    }

    public Integer getCarstyleid() {
        return carstyleid;
    }

    public void setCarstyleid(Integer carstyleid) {
        this.carstyleid = carstyleid;
    }

    public String getCarlabel() {
        return carlabel;
    }

    public void setCarlabel(String carlabel) {
        this.carlabel = carlabel == null ? null : carlabel.trim();
    }

    public String getCarcolor() {
        return carcolor;
    }

    public void setCarcolor(String carcolor) {
        this.carcolor = carcolor == null ? null : carcolor.trim();
    }

    public Date getIntime() {
        return intime;
    }

    public void setIntime(Date intime) {
        this.intime = intime;
    }

    public Date getOuttime() {
        return outtime;
    }

    public void setOuttime(Date outtime) {
        this.outtime = outtime;
    }

    public String getStoptime() {
        return stoptime;
    }

    public void setStoptime(String stoptime) {
        this.stoptime = stoptime == null ? null : stoptime.trim();
    }

    public Float getRealchargeamount() {
        return realchargeamount;
    }

    public void setRealchargeamount(Float realchargeamount) {
        this.realchargeamount = realchargeamount;
    }

    public Date getChargedate() {
        return chargedate;
    }

    public void setChargedate(Date chargedate) {
        this.chargedate = chargedate;
    }

    public Float getReductionamount() {
        return reductionamount;
    }

    public void setReductionamount(Float reductionamount) {
        this.reductionamount = reductionamount;
    }

    public String getReductiontype() {
        return reductiontype;
    }

    public void setReductiontype(String reductiontype) {
        this.reductiontype = reductiontype;
    }

    public Integer getChargetype() {
        return chargetype;
    }

    public void setChargetype(Integer chargetype) {
        this.chargetype = chargetype;
    }

    public Integer getInrecordid() {
        return inrecordid;
    }

    public void setInrecordid(Integer inrecordid) {
        this.inrecordid = inrecordid;
    }

    public Integer getOutrecordid() {
        return outrecordid;
    }

    public void setOutrecordid(Integer outrecordid) {
        this.outrecordid = outrecordid;
    }

    public String getReductionsname() {
        return reductionsname;
    }

    public void setReductionsname(String reductionsname) {
        this.reductionsname = reductionsname == null ? null : reductionsname.trim();
    }

    public String getBarcodeno() {
        return barcodeno;
    }

    public void setBarcodeno(String barcodeno) {
        this.barcodeno = barcodeno == null ? null : barcodeno.trim();
    }

    public Float getChargemoney() {
        return chargemoney;
    }

    public void setChargemoney(Float chargemoney) {
        this.chargemoney = chargemoney;
    }

    public Float getChargeamount() {
        return chargeamount;
    }

    public void setChargeamount(Float chargeamount) {
        this.chargeamount = chargeamount;
    }

    public Float getNotchargeamount() {
        return notchargeamount;
    }

    public void setNotchargeamount(Float notchargeamount) {
        this.notchargeamount = notchargeamount;
    }

    public Boolean getIscharge() {
        return ischarge;
    }

    public void setIscharge(Boolean ischarge) {
        this.ischarge = ischarge;
    }

    public Integer getReliefhour() {
        return reliefhour;
    }

    public void setReliefhour(Integer reliefhour) {
        this.reliefhour = reliefhour;
    }

    public Boolean getIsupload() {
        return isupload;
    }

    public void setIsupload(Boolean isupload) {
        this.isupload = isupload;
    }

    public Date getLastouttime() {
        return lastouttime;
    }

    public void setLastouttime(Date lastouttime) {
        this.lastouttime = lastouttime;
    }

    public String getChargesource() {
        return chargesource;
    }

    public void setChargesource(String chargesource) {
        this.chargesource = chargesource == null ? null : chargesource.trim();
    }

    public Integer getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(Integer deviceid) {
        this.deviceid = deviceid;
    }

    public Integer getPayrecordid() {
        return payrecordid;
    }

    public void setPayrecordid(Integer payrecordid) {
        this.payrecordid = payrecordid;
    }

    public String getParkinglotno() {
        return parkinglotno;
    }

    public void setParkinglotno(String parkinglotno) {
        this.parkinglotno = parkinglotno == null ? null : parkinglotno.trim();
    }
}
