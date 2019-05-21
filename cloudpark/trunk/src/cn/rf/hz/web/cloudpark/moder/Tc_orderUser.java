package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Tc_orderUser {
    private Integer id;

    private String parkid;

    private String licenseplatenumber;
    private String parkingNumber;
    


	private String act;

    private String starttime;

    private String endtime;

    private String xbindex;

    private String xbcallback;

    private String ordertime;

    private String lastintime;

    private String lastouttime;

    private String xbtype;

    private String tip;

    private String price;

    private String chargingruleid;

    private String reserve1;

    private String reserve2;

    private Date createtime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getParkid() {
        return parkid;
    }

    public void setParkid(String parkid) {
        this.parkid = parkid == null ? null : parkid.trim();
    }

    public String getLicenseplatenumber() {
        return licenseplatenumber;
    }

    public void setLicenseplatenumber(String licenseplatenumber) {
        this.licenseplatenumber = licenseplatenumber == null ? null : licenseplatenumber.trim();
    }

    public String getAct() {
        return act;
    }

    public void setAct(String act) {
        this.act = act == null ? null : act.trim();
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime == null ? null : starttime.trim();
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime == null ? null : endtime.trim();
    }

    public String getXbindex() {
        return xbindex;
    }

    public void setXbindex(String xbindex) {
        this.xbindex = xbindex == null ? null : xbindex.trim();
    }

    public String getXbcallback() {
        return xbcallback;
    }

    public void setXbcallback(String xbcallback) {
        this.xbcallback = xbcallback == null ? null : xbcallback.trim();
    }

    public String getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(String ordertime) {
        this.ordertime = ordertime == null ? null : ordertime.trim();
    }

    public String getLastintime() {
        return lastintime;
    }

    public void setLastintime(String lastintime) {
        this.lastintime = lastintime == null ? null : lastintime.trim();
    }

    public String getLastouttime() {
        return lastouttime;
    }

    public void setLastouttime(String lastouttime) {
        this.lastouttime = lastouttime == null ? null : lastouttime.trim();
    }

    public String getXbtype() {
        return xbtype;
    }

    public void setXbtype(String xbtype) {
        this.xbtype = xbtype == null ? null : xbtype.trim();
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip == null ? null : tip.trim();
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price == null ? null : price.trim();
    }

    public String getChargingruleid() {
        return chargingruleid;
    }

    public void setChargingruleid(String chargingruleid) {
        this.chargingruleid = chargingruleid == null ? null : chargingruleid.trim();
    }

    public String getReserve1() {
        return reserve1;
    }

    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1 == null ? null : reserve1.trim();
    }

    public String getReserve2() {
        return reserve2;
    }

    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2 == null ? null : reserve2.trim();
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
    public String getParkingNumber() {
		return parkingNumber;
	}

	public void setParkingNumber(String parkingNumber) {
		this.parkingNumber = parkingNumber;
	}
}