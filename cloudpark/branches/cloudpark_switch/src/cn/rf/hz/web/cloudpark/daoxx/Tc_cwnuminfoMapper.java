package cn.rf.hz.web.cloudpark.daoxx;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.rf.hz.web.cloudpark.moder.Tc_cwnuminfo;

public interface Tc_cwnuminfoMapper {
    int deleteByPrimaryKey(Integer recordid);

    int insert(Tc_cwnuminfo record);

    int insertSelective(Tc_cwnuminfo record);

    Tc_cwnuminfo selectByPrimaryKey(Integer recordid);

    int updateByPrimaryKeySelective(Tc_cwnuminfo record);

    int updateByPrimaryKey(Tc_cwnuminfo record);
    
    Tc_cwnuminfo queryOnebyParkinglotnoAndAreaid(@Param("parkNo") String parkNo,@Param("areaId") int areaId);
    
    void batchUpdateCwnuminfo(List<Tc_cwnuminfo>list); 
}