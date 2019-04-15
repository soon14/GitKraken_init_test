package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Tc_chargerule extends Tc_chargeruleKey {
    private String chargerulename;

    private Integer usertype;

    private Integer chargeno;

    private Integer childruleid;

    private String allfilesname;

    private String pagename;

    private String chargeruledescribe;

    private Boolean isfixed;

    private Integer createpeople;

    private Date createdate;

    private Integer updatepeople;

    private Date updatedate;

    private Boolean isdelete;

    private Integer deletepeople;

    private Date deletedate;

    private Integer parkingtype;

    private Boolean isupload;

    private Boolean isweekrule;

    public String getChargerulename() {
        return chargerulename;
    }

    public void setChargerulename(String chargerulename) {
        this.chargerulename = chargerulename == null ? null : chargerulename.trim();
    }

    public Integer getUsertype() {
        return usertype;
    }

    public void setUsertype(Integer usertype) {
        this.usertype = usertype;
    }

    public Integer getChargeno() {
        return chargeno;
    }

    public void setChargeno(Integer chargeno) {
        this.chargeno = chargeno;
    }

    public Integer getChildruleid() {
        return childruleid;
    }

    public void setChildruleid(Integer childruleid) {
        this.childruleid = childruleid;
    }

    public String getAllfilesname() {
        return allfilesname;
    }

    public void setAllfilesname(String allfilesname) {
        this.allfilesname = allfilesname == null ? null : allfilesname.trim();
    }

    public String getPagename() {
        return pagename;
    }

    public void setPagename(String pagename) {
        this.pagename = pagename == null ? null : pagename.trim();
    }

    public String getChargeruledescribe() {
        return chargeruledescribe;
    }

    public void setChargeruledescribe(String chargeruledescribe) {
        this.chargeruledescribe = chargeruledescribe == null ? null : chargeruledescribe.trim();
    }

    public Boolean getIsfixed() {
        return isfixed;
    }

    public void setIsfixed(Boolean isfixed) {
        this.isfixed = isfixed;
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

    public Boolean getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(Boolean isdelete) {
        this.isdelete = isdelete;
    }

    public Integer getDeletepeople() {
        return deletepeople;
    }

    public void setDeletepeople(Integer deletepeople) {
        this.deletepeople = deletepeople;
    }

    public Date getDeletedate() {
        return deletedate;
    }

    public void setDeletedate(Date deletedate) {
        this.deletedate = deletedate;
    }

    public Integer getParkingtype() {
        return parkingtype;
    }

    public void setParkingtype(Integer parkingtype) {
        this.parkingtype = parkingtype;
    }

    public Boolean getIsupload() {
        return isupload;
    }

    public void setIsupload(Boolean isupload) {
        this.isupload = isupload;
    }

    public Boolean getIsweekrule() {
        return isweekrule;
    }

    public void setIsweekrule(Boolean isweekrule) {
        this.isweekrule = isweekrule;
    }
}