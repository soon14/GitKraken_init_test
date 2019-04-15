package cn.rf.hz.web.cloudpark.daoxx;

import java.util.List;

import cn.rf.hz.web.cloudpark.moder.Pb_parkingbox;

public interface Pb_parkingboxMapper {
    int deleteByPrimaryKey(Integer recordid);

    int insert(Pb_parkingbox record);

    int insertSelective(Pb_parkingbox record);

    Pb_parkingbox selectByPrimaryKey(Integer recordid);

    int updateByPrimaryKeySelective(Pb_parkingbox record);

    int updateByPrimaryKey(Pb_parkingbox record);
    
    Pb_parkingbox selectBySerialNumber(String serialnumber);
    
    List<Pb_parkingbox> selectByAreaID(Integer parkingareaid);
}