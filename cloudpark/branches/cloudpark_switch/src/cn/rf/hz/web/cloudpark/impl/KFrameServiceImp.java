package cn.rf.hz.web.cloudpark.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.cloudpark.model.ChargeRecord;
import com.reformer.cloudpark.service.KFrameService;
import com.reformer.cloudpark.service.ParkOutInService;
import com.reformer.cloudpark.service.Tc_userinfoService;
import com.reformer.datatunnel.client.DataTunnelPublishClient;

import cn.rf.hz.web.cloudpark.moder.Tc_chargerecordinfo;
import cn.rf.hz.web.sharding.dao.Tc_chargerecordinfoMapper;
import cn.rf.hz.web.sharding.dao.Tc_crosspointMapper;
import cn.rf.hz.web.sharding.dao.Tc_usercrdtm_inMapper;
import cn.rf.hz.web.utils.BigDataAnalyze;
import cn.rf.hz.web.utils.DataConstants;
import cn.rf.hz.web.utils.JedisPoolUtils;
import cn.rf.hz.web.utils.LZ4Compress;

@Service("kframeService")
public class KFrameServiceImp implements KFrameService {

    private final static Logger   LOG = Logger.getLogger(KFrameServiceImp.class);
    @Autowired
    private Tc_usercrdtm_inMapper usercrdtm_inMapper;

    @Autowired
    private Tc_crosspointMapper crosspointMapper;

    @Autowired
    Tc_chargerecordinfoMapper chargerecordinfoMapper;

    @Autowired(required = false)
    public Tc_userinfoService userinfoservice;

    @Autowired(required = false)
    public PublicParkingService publicparkingservice;

    @Autowired(required = false)
    public SendMessServiceImp sendmessserviceimp;

    @Autowired
    private DataTunnelPublishClient dataTunnelPublishClient;
    
    @Autowired(required = false)
    private ParkOutInService parkOutInService;

    @Resource
    private SemaphoreManager semaphore;

    private static long fsize = 5000;

    @Override
    public String parkingcarAll(JSONObject mapparam) {

        LOG.info(" rpc请求参数=============mapparam============" + mapparam + "==============");
        String ParkingLotNo = mapparam.getString("ParkingLotNo");
        String key_p = BigDataAnalyze.geListKeyByDataType(ParkingLotNo,
                DataConstants.CLOUDPARK_INOUT, "p");
        // long l = JedisPoolUtils.hincrBy(key_p, "autoincrsn", 1);
        long l = JedisPoolUtils.hincrBy(key_p, "autoincrsn", 1);
        // 获取初始化总流量管制、获取停车场流量控制对象
        Semaphore totalSema = semaphore.getSema(SemaphoreManager.PARK_SEMAPHORE,
                SemaphoreManager.KDATA_INOUT);
        JSONArray array = new JSONArray();
        // 尝试总流信息量
        long totalMills = System.currentTimeMillis();
        boolean acquiredFlg = false;
        while ((System.currentTimeMillis() - totalMills) < (SemaphoreManager.TOTAL_SEMA_TIMEOUT)) {
            if (totalSema.tryAcquire()) {
                LOG.info("============" + ParkingLotNo + "totalSema acquire===================");
                // 等待一段时间后，重新查看是否有内存对象
                try {
                    double count = usercrdtm_inMapper.selectcarinCount(ParkingLotNo);
                    LOG.info("countcountcount：" + count + "============");
                    if (count < fsize) {
                        mapparam.put("limtcount", fsize);
                        mapparam.put("fpage", 0);
                        List<Object> list = this.usercrdtm_inMapper
                                .queryTc_usercrdtm_in_limit(mapparam);

                        array.addAll(list);
                        //array = BigDataAnalyze.fixArray(array, "in");
                        LOG.info("array：" + array.size() + "============");
                        long beginsize = array.toJSONString().getBytes().length;
                        byte[] dataB = array.toJSONString().getBytes();
                        byte[] keyB = (DataConstants.KPRE + DataConstants.CLOUDPARK_INOUT)
                                .getBytes();
                        byte[] filedB = ParkingLotNo.getBytes();
                        dataB = LZ4Compress.compress(dataB);
                        JedisPoolUtils.hsetB(keyB, filedB, dataB);
                        array.clear();
                        sendmessserviceimp.SendMess(array, dataTunnelPublishClient, l, "in_k",
                                ParkingLotNo, DataConstants.CLOUDPARK_INOUT,
                                String.valueOf(beginsize));
                    } else {
                        double spiltcount = Math.ceil(count / fsize);
                        mapparam.put("limtcount", fsize);
                        long fpage = 0;
                        for (int i = 0; i < spiltcount; i++) {
                            fpage = ((i + 1) - 1) * fsize;
                            mapparam.put("fpage", fpage);
                            List<Object> list = this.usercrdtm_inMapper
                                    .queryTc_usercrdtm_in_limit(mapparam);
                            array.addAll(list);

                            // LOG.info("fpage：" + fpage + "============");

                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        //array = BigDataAnalyze.fixArray(array, "in");
                        LOG.info("array：" + array.size() + "============");
                        long beginsize = array.toJSONString().getBytes().length;
                        byte[] dataB = array.toJSONString().getBytes();
                        byte[] keyB = (DataConstants.KPRE + DataConstants.CLOUDPARK_INOUT)
                                .getBytes();
                        byte[] filedB = ParkingLotNo.getBytes();
                        dataB = LZ4Compress.compress(dataB);
                        JedisPoolUtils.hsetB(keyB, filedB, dataB);
                        array.clear();
                        sendmessserviceimp.SendMess(array, dataTunnelPublishClient, l, "in_k",
                                ParkingLotNo, DataConstants.CLOUDPARK_INOUT,
                                String.valueOf(beginsize));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    array.clear();
                    acquiredFlg = true;
                    totalSema.release();
                }
            }

            if (acquiredFlg) {
                break;
            }

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (!acquiredFlg) {
            LOG.info("生成出入场全量包超过并发数，并且等待时间超时，parkId=" + ParkingLotNo);
        }
        JSONObject jso = new JSONObject();
        jso.put("data", array);
        jso.put("version", l);
        LOG.info("==================================== parkingcarAllrcp 返回"
                + jso.toJSONString().getBytes().length + "====================================");
        return jso.toJSONString();

    }

    @Override
    public String chargerecordAll(JSONObject mapparam) {
        LOG.error(" rpc请求参数=============mapparam============" + mapparam + "==============");
        String ParkingLotNo = mapparam.getString("ParkingLotNo");
        String key_p = BigDataAnalyze.geListKeyByDataType(ParkingLotNo,
                DataConstants.CLOUDPARK_CHARGE, "p");
        long l = JedisPoolUtils.hincrBy(key_p, "autoincrsn", 1);
        JSONArray array = new JSONArray();

        // 获取初始化总流量管制、获取停车场流量控制对象
        Semaphore totalSema = semaphore.getSema(SemaphoreManager.PARK_SEMAPHORE,
                SemaphoreManager.KDATA_CHARGE);
        // 尝试总流信息量
        long totalMills = System.currentTimeMillis();
        boolean acquiredFlg = false;
        while ((System.currentTimeMillis() - totalMills) < (SemaphoreManager.TOTAL_SEMA_TIMEOUT)) {
            if (totalSema.tryAcquire()) {
                try {
                    List<Object> list = this.chargerecordinfoMapper.queryChargerecordinfo(mapparam);
                    array.addAll(list);
                    if (array.size() > fsize) {

                        LOG.error("array：" + array.size() + "============");
                        long beginsize = array.toJSONString().getBytes().length;

                        byte[] dataB = array.toJSONString().getBytes();

                        byte[] keyB = (DataConstants.KPRE + DataConstants.CLOUDPARK_CHARGE)
                                .getBytes();
                        byte[] filedB = ParkingLotNo.getBytes();
                        dataB = LZ4Compress.compress(dataB);
                        // LOG.info("=======压缩后大小：" + dataB.length +
                        // "============");
                        JedisPoolUtils.hsetB(keyB, filedB, dataB);
                        array.clear();
                        sendmessserviceimp.SendMess(array, dataTunnelPublishClient, l, "in_k",
                                ParkingLotNo, DataConstants.CLOUDPARK_CHARGE,
                                String.valueOf(beginsize));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    array.clear();
                    acquiredFlg = true;
                    totalSema.release();
                }
            }
            if (acquiredFlg) {
                break;
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (!acquiredFlg) {
            LOG.error("生成缴费全量包超过并发数，并且等待时间超时，parkId=" + ParkingLotNo);
        }
        JSONObject jso = new JSONObject();
        jso.put("data", array);
        jso.put("version", l);
        LOG.error("==================================== chargerercp 返回"
                + jso.toJSONString().getBytes().length + "====================================");
        return jso.toJSONString();
    }

    @Override
    public String userinfodAll(JSONObject mapparam) {
        // TODO Auto-generated method stub
        String ParkingLotNo = mapparam.getString("ParkingLotNo");
        String key_p = BigDataAnalyze.geListKeyByDataType(ParkingLotNo,
                DataConstants.CLOUDPARK_USERGROUP, "p");

        long l = JedisPoolUtils.hincrBy(key_p, "autoincrsn", 1);
        JSONArray array = userinfoservice.selectByparkingNo(mapparam.toJSONString());

        if (array.size() > fsize) {
            LOG.info("array：" + array.size() + "============");
            long beginsize = array.toJSONString().getBytes().length;
            byte[] dataB = array.toJSONString().getBytes();
            byte[] keyB = (DataConstants.KPRE + DataConstants.CLOUDPARK_USERGROUP).getBytes();
            byte[] filedB = ParkingLotNo.getBytes();
            dataB = LZ4Compress.compress(dataB);
            JedisPoolUtils.hsetB(keyB, filedB, dataB);
            array.clear();
            sendmessserviceimp.SendMess(array, dataTunnelPublishClient, l, "in_k", ParkingLotNo,
                    DataConstants.CLOUDPARK_USERGROUP, String.valueOf(beginsize));
        }
        JSONObject jso = new JSONObject();
        jso.put("data", array);
        jso.put("version", l);
        LOG.info("==================================== userinfrcp 返回"
                + jso.toJSONString().getBytes().length + "====================================");
        return jso.toJSONString();
    }

    @Override
    public String crosspointAll(JSONObject mapparam) {
        LOG.info("crosspointAll_rpc请求参数:" + mapparam);
        String ParkingLotNo = mapparam.getString("ParkingLotNo");
        String key_p = BigDataAnalyze.geListKeyByDataType(ParkingLotNo,
                DataConstants.CLOUDPARK_SWITCH, "p");
        // long l = JedisPoolUtils.hincrBy(key_p, "autoincrsn", 1);
        long l = JedisPoolUtils.hincrBy(key_p, "autoincrsn", 1);
        // 获取初始化总流量管制、获取停车场流量控制对象
        Semaphore totalSema = semaphore.getSema(SemaphoreManager.PARK_SEMAPHORE,
                SemaphoreManager.KDATA_INOUT);
        JSONArray array = new JSONArray();
        // 尝试总流信息量
        long totalMills = System.currentTimeMillis();
        boolean acquiredFlg = false;
        while ((System.currentTimeMillis() - totalMills) < (SemaphoreManager.TOTAL_SEMA_TIMEOUT)) {
            if (totalSema.tryAcquire()) {
                LOG.info("crosspointAll_parkinglotno_" + ParkingLotNo + "totalSema acquire");
                // 等待一段时间后，重新查看是否有内存对象
                try {
                    double count = crosspointMapper.selectcarinCount(ParkingLotNo);
                    LOG.info("crosspointAll_countcountcount：" + count + "============");
                    if (count < fsize) {
                        mapparam.put("limtcount", fsize);
                        mapparam.put("fpage", 0);
                        List<Object> list = crosspointMapper.queryTc_crosspoint_limit(mapparam);
                        array.addAll(list);
                        //array = BigDataAnalyze.fixArray(array, "in");
                        LOG.info("array：" + array.size() + "============");
                        long beginsize = array.toJSONString().getBytes().length;
                        byte[] dataB = array.toJSONString().getBytes();
                        byte[] keyB = (DataConstants.KPRE + DataConstants.CLOUDPARK_SWITCH)
                                .getBytes();
                        byte[] filedB = ParkingLotNo.getBytes();
                        dataB = LZ4Compress.compress(dataB);
                        JedisPoolUtils.hsetB(keyB, filedB, dataB);
                        array.clear();
                        sendmessserviceimp.SendMess(array, dataTunnelPublishClient, l, "in_k",
                                ParkingLotNo, DataConstants.CLOUDPARK_SWITCH,
                                String.valueOf(beginsize));
                    } else {
                        double spiltcount = Math.ceil(count / fsize);
                        mapparam.put("limtcount", fsize);
                        long fpage = 0;
                        for (int i = 0; i < spiltcount; i++) {
                            fpage = ((i + 1) - 1) * fsize;
                            mapparam.put("fpage", fpage);
                            List<Object> list = crosspointMapper.queryTc_crosspoint_limit(mapparam);
                            array.addAll(list);

                            // LOG.info("fpage：" + fpage + "============");

                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        //array = BigDataAnalyze.fixArray(array, "in");
                        LOG.info("array：" + array.size() + "============");
                        long beginsize = array.toJSONString().getBytes().length;
                        byte[] dataB = array.toJSONString().getBytes();
                        byte[] keyB = (DataConstants.KPRE + DataConstants.CLOUDPARK_SWITCH)
                                .getBytes();
                        byte[] filedB = ParkingLotNo.getBytes();
                        dataB = LZ4Compress.compress(dataB);
                        JedisPoolUtils.hsetB(keyB, filedB, dataB);
                        array.clear();
                        sendmessserviceimp.SendMess(array, dataTunnelPublishClient, l, "in_k",
                                ParkingLotNo, DataConstants.CLOUDPARK_SWITCH,
                                String.valueOf(beginsize));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    array.clear();
                    acquiredFlg = true;
                    totalSema.release();
                }
            }

            if (acquiredFlg) {
                break;
            }

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (!acquiredFlg) {
            LOG.info("生成过车点crosspoint全量包超过并发数，并且等待时间超时，parkId=" + ParkingLotNo);
        }
        JSONObject jso = new JSONObject();
        jso.put("data", array);
        jso.put("version", l);
        LOG.info("crosspointAllrcp_return:" + jso.toJSONString().getBytes().length + "");
        return jso.toJSONString();
    }

    @Override
    public String delcarfrom_in(String carcode, String parkingNo) {
        LOG.info("carcode========" + carcode + "=======");
        LOG.info("parkingNo=" + parkingNo + "===============");
        int PartitionID = publicparkingservice.getPartitionidin(parkingNo);
        this.usercrdtm_inMapper.delcarfrom_in(carcode, parkingNo, PartitionID);
        LOG.info("cloudlong 删除 cloudpark 场内数据成功 ===============");
        return null;
    }

    @Override
    public String changestatebycarcode(String carcode, String parkingNo) {
        LOG.info("carcode========" + carcode + "=======");
        LOG.info("parkingNo=" + parkingNo + "===============");
        int partitionid = publicparkingservice.getPartitionid(parkingNo);
        this.chargerecordinfoMapper.updatestatebycarcode(carcode, parkingNo, partitionid);
        return null;
    }

    @Override
    public String changestatebyrecordid(long recordid, String parkingNo) {
        LOG.info("recordid========" + recordid + "=======");
        LOG.info("parkingNo=" + parkingNo + "===============");
        int partitionid = publicparkingservice.getPartitionid(parkingNo);
        this.chargerecordinfoMapper.updatestatebyrecordid(recordid, parkingNo, partitionid);
        return null;
    }

    @Override
    public void saveChargeRecord(String parkingNo, String carcode, ChargeRecord chargeRecord) {
        ArrayList<Tc_chargerecordinfo> chargeRecordList = new ArrayList<Tc_chargerecordinfo>();
        Tc_chargerecordinfo tc_chargerecordinfo = new Tc_chargerecordinfo();
        BeanUtils.copyProperties(chargeRecord, tc_chargerecordinfo);        
        tc_chargerecordinfo.setPartitionid(parkOutInService.getPartitionid(parkingNo));
        tc_chargerecordinfo.setRecordid(publicparkingservice.getChargesequenceId());
        chargeRecordList.add(tc_chargerecordinfo);
        chargerecordinfoMapper.batchInsertChargerecordinfo(chargeRecordList);
        for (int i = 0; i < chargeRecordList.size(); i++) {
            if (chargeRecordList.get(i).getAmounttype() == null
                    || chargeRecordList.get(i).getAmounttype() == 0) {
                parkOutInService.sendKafkaMesUploadPayRecord(parkingNo, chargeRecordList);
            }
        }
    }

}
