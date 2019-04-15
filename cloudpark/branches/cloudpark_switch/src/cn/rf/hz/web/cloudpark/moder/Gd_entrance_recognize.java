package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Gd_entrance_recognize extends Gd_entrance_recognizeKey {
    private Integer oldrecordid;

    private Integer carparkid;

    private String licenseplatenumber;

    private String imgname;

    private Date intime;

    private Integer discountrate;

    private Integer discounttime;

    private Integer discountmoney;

    private Date addtime;

    private Integer mark;

    private Integer isrecognition;

    private String recognitionnumber;

    private Integer groupid;

    private Integer mallid;

    private Long hashcode;

    private String testSign;

    public Integer getOldrecordid() {
        return oldrecordid;
    }

    public void setOldrecordid(Integer oldrecordid) {
        this.oldrecordid = oldrecordid;
    }

    public Integer getCarparkid() {
        return carparkid;
    }

    public void setCarparkid(Integer carparkid) {
        this.carparkid = carparkid;
    }

    public String getLicenseplatenumber() {
        return licenseplatenumber;
    }

    public void setLicenseplatenumber(String licenseplatenumber) {
        this.licenseplatenumber = licenseplatenumber == null ? null : licenseplatenumber.trim();
    }

    public String getImgname() {
        return imgname;
    }

    public void setImgname(String imgname) {
        this.imgname = imgname == null ? null : imgname.trim();
    }

    public Date getIntime() {
        return intime;
    }

    public void setIntime(Date intime) {
        this.intime = intime;
    }

    public Integer getDiscountrate() {
        return discountrate;
    }

    public void setDiscountrate(Integer discountrate) {
        this.discountrate = discountrate;
    }

    public Integer getDiscounttime() {
        return discounttime;
    }

    public void setDiscounttime(Integer discounttime) {
        this.discounttime = discounttime;
    }

    public Integer getDiscountmoney() {
        return discountmoney;
    }

    public void setDiscountmoney(Integer discountmoney) {
        this.discountmoney = discountmoney;
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public Integer getMark() {
        return mark;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public Integer getIsrecognition() {
        return isrecognition;
    }

    public void setIsrecognition(Integer isrecognition) {
        this.isrecognition = isrecognition;
    }

    public String getRecognitionnumber() {
        return recognitionnumber;
    }

    public void setRecognitionnumber(String recognitionnumber) {
        this.recognitionnumber = recognitionnumber == null ? null : recognitionnumber.trim();
    }

    public Integer getGroupid() {
        return groupid;
    }

    public void setGroupid(Integer groupid) {
        this.groupid = groupid;
    }

    public Integer getMallid() {
        return mallid;
    }

    public void setMallid(Integer mallid) {
        this.mallid = mallid;
    }

    public Long getHashcode() {
        return hashcode;
    }

    public void setHashcode(Long hashcode) {
        this.hashcode = hashcode;
    }

    public String getTestSign() {
        return testSign;
    }

    public void setTestSign(String testSign) {
        this.testSign = testSign == null ? null : testSign.trim();
    }
}