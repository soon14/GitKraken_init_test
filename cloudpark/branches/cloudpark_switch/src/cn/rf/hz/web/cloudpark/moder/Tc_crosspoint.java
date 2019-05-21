package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Tc_crosspoint extends Tc_crosspointKey {
	private String carcode;
	private String areaId;
	private String parentAreaId;
	private int status;
	private Date inoutTime;
	private Integer ruleId;
	private Date mstart;
	private Date mend;
	private Integer channelId;
	private Date lastouttime;
	private String parkinglotno;
	private int mruleId;

	/**
	 * @return the carcode
	 */
	public String getCarcode() {
		return carcode;
	}

	/**
	 * @param carcode
	 *            the carcode to set
	 */
	public void setCarcode(String carcode) {
		this.carcode = carcode;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getParentAreaId() {
		return parentAreaId;
	}

	public void setParentAreaId(String parentAreaId) {
		this.parentAreaId = parentAreaId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getInoutTime() {
		return inoutTime;
	}

	public void setInoutTime(Date inoutTime) {
		this.inoutTime = inoutTime;
	}

	public Integer getRuleId() {
		return ruleId;
	}

	public void setRuleId(Integer ruleId) {
		this.ruleId = ruleId;
	}

	public Date getMstart() {
		return mstart;
	}

	public void setMstart(Date mstart) {
		this.mstart = mstart;
	}

	public Date getMend() {
		return mend;
	}

	public void setMend(Date mend) {
		this.mend = mend;
	}

	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}

	public Date getLastouttime() {
		return lastouttime;
	}

	public void setLastouttime(Date lastouttime) {
		this.lastouttime = lastouttime;
	}

	public String getParkinglotno() {
		return parkinglotno;
	}

	public void setParkinglotno(String parkinglotno) {
		this.parkinglotno = parkinglotno;
	}

	public int getMruleId() {
		return mruleId;
	}

	public void setMruleId(int mruleId) {
		this.mruleId = mruleId;
	}

	@Override
	public String toString() {
		return "CrossPoint [carCode=" + carcode + ", areaId=" + areaId + ", parentAreaId=" + parentAreaId + ", status="
				+ status + ", inoutTime=" + inoutTime + ", ruleId=" + ruleId + ", mstart=" + mstart + ", mend=" + mend
				+ ",channelId=" + channelId + ", lastouttime=" + lastouttime + ", parkinglotno=" + parkinglotno + "]";
	}

}
