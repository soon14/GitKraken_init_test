package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Pb_product_history extends Pb_product_historyKey {
    private Integer holidayid;

    private Integer weekid;

    private Integer ruleid;

    private Integer islong;

    private String productname;

    private Integer longruleid;

    private Integer createpeople;

    private Date createdate;

    private Integer updatepeople;

    private Date updatedate;

    private Integer isdelete;

    private Integer overproductid;

    private Integer isover;

    private String channelids;

    private String areaids;

    private String datasource;

    private String sourceid;

    private Date eventtime;

    public Integer getHolidayid() {
        return holidayid;
    }

    public void setHolidayid(Integer holidayid) {
        this.holidayid = holidayid;
    }

    public Integer getWeekid() {
        return weekid;
    }

    public void setWeekid(Integer weekid) {
        this.weekid = weekid;
    }

    public Integer getRuleid() {
        return ruleid;
    }

    public void setRuleid(Integer ruleid) {
        this.ruleid = ruleid;
    }

    public Integer getIslong() {
        return islong;
    }

    public void setIslong(Integer islong) {
        this.islong = islong;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname == null ? null : productname.trim();
    }

    public Integer getLongruleid() {
        return longruleid;
    }

    public void setLongruleid(Integer longruleid) {
        this.longruleid = longruleid;
    }

    public Integer getCreatepeople() {
        return createpeople;
    }

    public void setCreatepeople(Integer createpeople) {
        this.createpeople = createpeople;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public Integer getUpdatepeople() {
        return updatepeople;
    }

    public void setUpdatepeople(Integer updatepeople) {
        this.updatepeople = updatepeople;
    }

    public Date getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(Date updatedate) {
        this.updatedate = updatedate;
    }

    public Integer getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(Integer isdelete) {
        this.isdelete = isdelete;
    }

    public Integer getOverproductid() {
        return overproductid;
    }

    public void setOverproductid(Integer overproductid) {
        this.overproductid = overproductid;
    }

    public Integer getIsover() {
        return isover;
    }

    public void setIsover(Integer isover) {
        this.isover = isover;
    }

    public String getChannelids() {
        return channelids;
    }

    public void setChannelids(String channelids) {
        this.channelids = channelids == null ? null : channelids.trim();
    }

    public String getAreaids() {
        return areaids;
    }

    public void setAreaids(String areaids) {
        this.areaids = areaids == null ? null : areaids.trim();
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource == null ? null : datasource.trim();
    }

    public String getSourceid() {
        return sourceid;
    }

    public void setSourceid(String sourceid) {
        this.sourceid = sourceid == null ? null : sourceid.trim();
    }

    public Date getEventtime() {
        return eventtime;
    }

    public void setEventtime(Date eventtime) {
        this.eventtime = eventtime;
    }
}