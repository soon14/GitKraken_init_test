package cn.rf.hz.web.cloudpark.daoxx;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import cn.rf.hz.web.cloudpark.moder.Tc_orderUser;

public interface Tc_orderUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Tc_orderUser record);

    int insertSelective(Tc_orderUser record);

    Tc_orderUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Tc_orderUser record);

    int updateByPrimaryKey(Tc_orderUser record);
    int insertorderuserinfo(Map<String, Object> map);
    int deleteorderuserinfo(Map<String, Object> map);
    int deleteorderuserinfobycarcode(Map<String, Object> map);//通过车牌删除预约记录，应该是一个车牌只有一条预约记录
    void cancleorder(JSONObject jso,String _carcode,String parkinglotno);
    List<Object> queryorderuser(Map<String, Object> map);
}