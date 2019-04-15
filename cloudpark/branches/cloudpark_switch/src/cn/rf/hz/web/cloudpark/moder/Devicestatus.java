package cn.rf.hz.web.cloudpark.moder;

import java.util.Date;

public class Devicestatus {

    private Long id;

    private String devno;

    private String channelid;

    private String parkno;

    private String ip;

    private Integer status;

    private Integer devtype;

    private Date createtime;

    private Date updatetime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDevno() {
        return devno;
    }

    public void setDevno(String devno) {
        this.devno = devno == null ? null : devno.trim();
    }

    public String getChannelid() {
        return channelid;
    }

    public void setChannelid(String channelid) {
        this.channelid = channelid == null ? null : channelid.trim();
    }

    public String getParkno() {
        return parkno;
    }

    public void setParkno(String parkno) {
        this.parkno = parkno == null ? null : parkno.trim();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDevtype() {
        return devtype;
    }

    public void setDevtype(Integer devtype) {
        this.devtype = devtype;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}