package cn.rf.hz.web.cloudpark.daoxx;

import cn.rf.hz.web.cloudpark.moder.Tc_userexpiryinfo;

public interface Tc_userexpiryinfoMapper {
    int deleteByPrimaryKey(Integer recordid);

    int insert(Tc_userexpiryinfo record);

    int insertSelective(Tc_userexpiryinfo record);

    Tc_userexpiryinfo selectByPrimaryKey(Integer recordid);

    int updateByPrimaryKeySelective(Tc_userexpiryinfo record);

    int updateByPrimaryKey(Tc_userexpiryinfo record);
}