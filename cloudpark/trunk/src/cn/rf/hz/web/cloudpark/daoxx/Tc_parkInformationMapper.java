package cn.rf.hz.web.cloudpark.daoxx;

import java.util.List;
import java.util.Map;

import cn.rf.hz.web.cloudpark.moder.Gd_entrance_recognize;
import cn.rf.hz.web.cloudpark.moder.Tc_parkingarea;
import cn.rf.hz.web.cloudpark.moder.Tc_whiteuserinfo;

public interface Tc_parkInformationMapper {
    /**
     * @param 传入一个通道ID
     * @return 返回 改通道所在的停车场的总车位数
     */
    List<Object> queryparkingarea(String ParkingLotNo);

    List<Object> queryBlackWhite(String ParkingLotNo);

    List<Object> queryparkingchannelID(Map<String, Object> map);

    Integer countAll();

    List<Object> queryparkings(Map map);

    Integer countChannelAll();

    List<Object> queryparkingchannels(Map map);

    List<Object> queryparkingchannelById(Map map);
    
    String queryparkingNameById(String parkinglotno);
    
    String queryParkingTypeById(String parkinglotno);
    
    List<Object> queryChannelByParkingLotNoAndAreaID(Map<String, Object> map);
}
