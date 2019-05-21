package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Tc_userinfocarin implements Comparable<Tc_userinfocarin> {
	private String carcode;
	private Date intime;
	private String outtime;
	private String carintype;
	private String channelid;
	private String areaid;
	private String chargeruleid;
	private String parkinglotno;
	private String partitionid;
	private String username;
	private String lastouttime;
	private String money;

	public String getCarcode() {
		return carcode;
	}

	public void setCarcode(String carcode) {
		this.carcode = carcode;
	}

	public Date getIntime() {
		return intime;
	}

	public void setIntime(Date intime) {
		this.intime = intime;
	}

	public String getOuttime() {
		return outtime;
	}

	public void setOuttime(String outtime) {
		this.outtime = outtime;
	}

	public String getCarintype() {
		return carintype;
	}

	public void setCarintype(String carintype) {
		this.carintype = carintype;
	}

	public String getChannelid() {
		return channelid;
	}

	public void setChannelid(String channelid) {
		this.channelid = channelid;
	}

	public String getAreaid() {
		return areaid;
	}

	public void setAreaid(String areaid) {
		this.areaid = areaid;
	}

	public String getChargeruleid() {
		return chargeruleid;
	}

	public void setChargeruleid(String chargeruleid) {
		this.chargeruleid = chargeruleid;
	}

	public String getParkinglotno() {
		return parkinglotno;
	}

	public void setParkinglotno(String parkinglotno) {
		this.parkinglotno = parkinglotno;
	}

	public String getPartitionid() {
		return partitionid;
	}

	public void setPartitionid(String partitionid) {
		this.partitionid = partitionid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getLastouttime() {
		return lastouttime;
	}

	public void setLastouttime(String lastouttime) {
		this.lastouttime = lastouttime;
	}

	@Override
	public int compareTo(Tc_userinfocarin o) {
		return this.getIntime().compareTo(o.getIntime());
	}
}
