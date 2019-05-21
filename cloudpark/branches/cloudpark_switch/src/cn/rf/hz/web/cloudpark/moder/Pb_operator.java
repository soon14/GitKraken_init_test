package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Pb_operator {
    private Integer operatorid;

    private String loginaccounts;

    private String loginpassword;

    private String operatorname;

    private String pagemenuvalues;

    private Integer roleid;

    private String rolename;

    private Integer createpeople;

    private Date createdate;

    private Integer updatepeople;

    private Date updatedate;

    private Boolean isdelete;

    private Integer deletepeople;

    private Date deletedate;

    private Boolean isqueryweb;

    private String reliefhours;

    private Integer merchanttype;

    private String parkinglotnos;

    private Integer sex;

    private String mobilephone;

    private String tel;

    private String email;

    private Integer state;

    public Integer getOperatorid() {
        return operatorid;
    }

    public void setOperatorid(Integer operatorid) {
        this.operatorid = operatorid;
    }

    public String getLoginaccounts() {
        return loginaccounts;
    }

    public void setLoginaccounts(String loginaccounts) {
        this.loginaccounts = loginaccounts == null ? null : loginaccounts.trim();
    }

    public String getLoginpassword() {
        return loginpassword;
    }

    public void setLoginpassword(String loginpassword) {
        this.loginpassword = loginpassword == null ? null : loginpassword.trim();
    }

    public String getOperatorname() {
        return operatorname;
    }

    public void setOperatorname(String operatorname) {
        this.operatorname = operatorname == null ? null : operatorname.trim();
    }

    public String getPagemenuvalues() {
        return pagemenuvalues;
    }

    public void setPagemenuvalues(String pagemenuvalues) {
        this.pagemenuvalues = pagemenuvalues == null ? null : pagemenuvalues.trim();
    }

    public Integer getRoleid() {
        return roleid;
    }

    public void setRoleid(Integer roleid) {
        this.roleid = roleid;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename == null ? null : rolename.trim();
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

    public Boolean getIsqueryweb() {
        return isqueryweb;
    }

    public void setIsqueryweb(Boolean isqueryweb) {
        this.isqueryweb = isqueryweb;
    }

    public String getReliefhours() {
        return reliefhours;
    }

    public void setReliefhours(String reliefhours) {
        this.reliefhours = reliefhours == null ? null : reliefhours.trim();
    }

    public Integer getMerchanttype() {
        return merchanttype;
    }

    public void setMerchanttype(Integer merchanttype) {
        this.merchanttype = merchanttype;
    }

    public String getParkinglotnos() {
        return parkinglotnos;
    }

    public void setParkinglotnos(String parkinglotnos) {
        this.parkinglotnos = parkinglotnos == null ? null : parkinglotnos.trim();
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getMobilephone() {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone == null ? null : mobilephone.trim();
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel == null ? null : tel.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}