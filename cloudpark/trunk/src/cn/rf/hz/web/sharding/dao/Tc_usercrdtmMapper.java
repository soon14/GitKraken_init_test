package cn.rf.hz.web.sharding.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtm;
import cn.rf.hz.web.cloudpark.moder.Tc_usercrdtmKey;

public interface Tc_usercrdtmMapper {
    int deleteByPrimaryKey(Tc_usercrdtmKey key);

    int insert(Tc_usercrdtm record);

    int insertSelective(Tc_usercrdtm record);

    Tc_usercrdtm selectByPrimaryKey(Tc_usercrdtmKey key);

    int updateByPrimaryKeySelective(Tc_usercrdtm record);

    int updateByPrimaryKey(Tc_usercrdtm record);
    List<Object> queryCarInOutRecord(Map<String, Object> map);
    void batchInsertUsercrdtm(ArrayList<Tc_usercrdtm> list);
    
    List<Object> queryByCondition(Map<String, Object> map);
    
    List<Object> queryUserCrdtmForApp(Map<String, Object> map);
    
    void batchUpdateUsercrdtm(ArrayList<Tc_usercrdtm> list);

}