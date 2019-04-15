package cn.rf.hz.web.cloudpark.moder;

import java.math.BigDecimal;
import java.util.Date;

public class Tc_parkingboxmemory {
    private Integer recordid;

    private String parkinglotno;

    private String serialnumber;

    private Long memorysize;

    private Long memoryusedsize;

    private Long memorynotusedsize;

    private BigDecimal memoryusage;

    private BigDecimal memorynotusage;

    private Date createtime;

    public Integer getRecordid() {
        return recordid;
    }

    public void setRecordid(Integer recordid) {
        this.recordid = recordid;
    }

    public String getParkinglotno() {
        return parkinglotno;
    }

    public void setParkinglotno(String parkinglotno) {
        this.parkinglotno = parkinglotno == null ? null : parkinglotno.trim();
    }

    public String getSerialnumber() {
        return serialnumber;
    }

    public void setSerialnumber(String serialnumber) {
        this.serialnumber = serialnumber == null ? null : serialnumber.trim();
    }

    public Long getMemorysize() {
        return memorysize;
    }

    public void setMemorysize(Long memorysize) {
        this.memorysize = memorysize;
    }

    public Long getMemoryusedsize() {
        return memoryusedsize;
    }

    public void setMemoryusedsize(Long memoryusedsize) {
        this.memoryusedsize = memoryusedsize;
    }

    public Long getMemorynotusedsize() {
        return memorynotusedsize;
    }

    public void setMemorynotusedsize(Long memorynotusedsize) {
        this.memorynotusedsize = memorynotusedsize;
    }

    public BigDecimal getMemoryusage() {
        return memoryusage;
    }

    public void setMemoryusage(BigDecimal memoryusage) {
        this.memoryusage = memoryusage;
    }

    public BigDecimal getMemorynotusage() {
        return memorynotusage;
    }

    public void setMemorynotusage(BigDecimal memorynotusage) {
        this.memorynotusage = memorynotusage;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
}