package cn.rf.hz.web.cloudpark.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.reformer.cloudpark.service.ParkingInformation;

import cn.rf.hz.web.cloudpark.daoxx.Tc_parkInformationMapper;
import cn.rf.hz.web.utils.StringUtil;

@Service("parkingInformation")
public class ParkingInformationImp implements ParkingInformation {
    private final static Logger      LOG = Logger.getLogger(ParkingInformationImp.class);
    @Autowired
    private Tc_parkInformationMapper informationMapper;

    @Override
    public JSONObject queryParkingInformation(String requestBody) {

		LOG.info("=====getCarInfoForCharge:==请求requestBody:"+requestBody);
		JSONObject data = JSON.parseObject(requestBody);
		String ParkingLotNo=data.getString("parkinglotno");
        long startTime1 = System.currentTimeMillis();
        List<Object> list = informationMapper.queryparkingarea(ParkingLotNo);
        int count = 0;		//车位总数
        int stopedCount=0;	//已停车位数
        int prepCount=0;	//剩余车位数
        ArrayList tmpList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            //System.out.println(list.get(i));
            JSONObject area = new JSONObject((Map) list.get(i));
            count = count + area.getIntValue("CountCw");
            stopedCount=stopedCount+area.getIntValue("StopedCw");
            prepCount=prepCount+area.getIntValue("PrepCw");
            List<Object> listchannels = informationMapper.queryparkingchannelID(area);
            area.remove("ParkingLotNo");
            JSONArray channels = new JSONArray(listchannels);
            /*
             * for( int j=0;j<listchannels.size();j++){ channel =new
             * JSONObject((Map)listchannels.get(i)); }
             */
            area.put("channles", channels);
            tmpList.add(area);
        }
        JSONArray areas = new JSONArray(tmpList);

        JSONObject result = new JSONObject();
        result.put("carparkid", ParkingLotNo);
        result.put("areas", areas);
        result.put("count", count);
        result.put("stopedCount",stopedCount);
        result.put("prepCount",prepCount);
        LOG.info(result);
        return result;
    }

    @Override
    public Integer countChannelAll() {
        long startTime1 = System.currentTimeMillis();
        Integer countChannel = informationMapper.countChannelAll();
        LOG.info(countChannel);
        return countChannel;
    }

    @Override
    public Integer countAll() {
        long startTime1 = System.currentTimeMillis();
        Integer count = informationMapper.countAll();
        LOG.info(count);
        return count;
    }

    @Override
    public String queryParkins(int id, String parkname, int offset, int limit) {
        LOG.info("------queryParkins-----id="+id+",parkname="+parkname+",offset="+offset+",limit="+limit);
        long startTime1 = System.currentTimeMillis();
        Map map = new HashMap<String, Integer>();
        map.put("offset", offset);
        map.put("limit", limit);
        if (id > 0) {
            map.put("id", id);
        }
        if (!StringUtil.isEmpty(parkname)) {
            map.put("parkname", parkname);
        }
        List<Object> listchannels = informationMapper.queryparkings(map);
        JSONArray channels = new JSONArray(listchannels);
        LOG.info(channels);
        return channels.toJSONString();
    }

    @Override
    public String queryParkingChannels(int id, String parkname, int offset, int limit) {
        LOG.info("-----queryParkingChannels------id="+id+",parkname="+parkname+",offset="+offset+",limit="+limit);
        long startTime1 = System.currentTimeMillis();
        LOG.info("offset=" + offset + ",limit=" + limit);
        Map map = new HashMap<String, Integer>();
        map.put("offset", offset);
        map.put("limit", limit);
        if (id > 0) {
            map.put("id", id);
        }
        if (!StringUtil.isEmpty(parkname)) {
            map.put("parkname", parkname);
        }
        List<Object> listchannels = informationMapper.queryparkingchannels(map);
        JSONArray channels = new JSONArray(listchannels);
        LOG.info(channels.toJSONString());
        return channels.toJSONString();
    }
    
    @Override
    public String queryParkingChannelsById(int id) {
        LOG.info("-----queryParkingChannels------id="+id);
        long startTime1 = System.currentTimeMillis();
        LOG.info("id=" + id);
        Map map = new HashMap<String, Integer>(); 
        if (id > 0) {
            map.put("id", id);
        }      
        List<Object> listchannels = informationMapper.queryparkingchannelById(map);
        JSONArray channels = new JSONArray(listchannels);
        LOG.info(channels.toJSONString());
        return channels.toJSONString();
    }

}
