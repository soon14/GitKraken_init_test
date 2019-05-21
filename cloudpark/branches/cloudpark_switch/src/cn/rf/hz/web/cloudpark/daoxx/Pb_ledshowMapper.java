package cn.rf.hz.web.cloudpark.daoxx;

import java.util.List;

import cn.rf.hz.web.cloudpark.moder.Pb_ledshow;

public interface Pb_ledshowMapper {
    int deleteByPrimaryKey(Integer recordid);

    int insert(Pb_ledshow record);

    int insertSelective(Pb_ledshow record);

    Pb_ledshow selectByPrimaryKey(Integer recordid);

    int updateByPrimaryKeySelective(Pb_ledshow record);

    int updateByPrimaryKey(Pb_ledshow record);
    
    List<Pb_ledshow> selectByPid(Integer pid);
}