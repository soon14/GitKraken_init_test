package cn.rf.hz.web.cloudpark.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import cn.rf.hz.web.utils.ConfigUtil;

public class SemaphoreManager {

    private final static Logger LOG = Logger.getLogger(SemaphoreManager.class);

    private Map<String, Map<String, Semaphore>> sema;

    public final static String KDATA_INOUT   = "kData_inout";
    public final static String KDATA_CHARGE   = "kData_charge";
    public final static String VERSION = "version";

    public final static String PARK_SEMAPHORE = "park";
    // 总流量键值
    public final static String TOTAL_SEMA     = "totalSema";    
    //车场总资源获取超时限制
    public final static int    TOTAL_SEMA_TIMEOUT = 3 * 1000;

    public Map<String, Map<String, Semaphore>> getSema() {
        return sema;
    }

    public void setSema(Map<String, Map<String, Semaphore>> sema) {
        this.sema = sema;
    }

    public void init() {
        for (Entry<String, Map<String, Semaphore>> chainEntry : sema.entrySet()) {
            Map<String, Semaphore> item = new HashMap<String, Semaphore>();
            chainEntry.setValue(item);
        }
        
        LOG.info("============TotalSemaphore init===================");
        
        int inoutTotal = ConfigUtil.inoutTotalSemaphore;
        Semaphore inoutTotalSema = new Semaphore(inoutTotal);
        setSema(PARK_SEMAPHORE, KDATA_INOUT, inoutTotalSema);
        
        int chargeTotal = ConfigUtil.chargeTotalSemaphore;
        Semaphore chargeTotalSema = new Semaphore(chargeTotal);
        setSema(PARK_SEMAPHORE, KDATA_CHARGE, chargeTotalSema);
    }

    public Map<String, Semaphore> getParkSema(String key) {
        return sema.get(key);
    }

    public void setParkSema(String key, Map<String, Semaphore> parkCharge) {
        sema.put(key, parkCharge);
    }

    public void setSema(String key, String parkId, Semaphore _sema) {
        Map<String, Semaphore> ch = getParkSema(key);
        if (ch == null) {
            ch = new HashMap<>();
        }
        ch.put(parkId, _sema);
        sema.put(key, ch);
    }
    
    public Semaphore getSema(String key, String parkId) {
        Map<String, Semaphore> ch = getParkSema(key);
        if (ch != null && ch.size() > 0) {
            return ch.get(parkId);
        }

        return null;
    }
    

    public void delSema(String key, String parkId) {
        Map<String, Semaphore> ch = getParkSema(key);
        if (ch != null) {
            ch.remove(parkId);
        }
        sema.put(key, ch);
    }
}
