package cn.rf.hz.web.cloudpark.daoxx;

import cn.rf.hz.web.cloudpark.moder.Activationcode;

public interface ActivationcodeMapper {
    int deleteByPrimaryKey(Integer recordid);

    int insert(Activationcode record);

    int insertSelective(Activationcode record);

    Activationcode selectByPrimaryKey(Integer recordid);

    int updateByPrimaryKeySelective(Activationcode record);

    int updateByPrimaryKey(Activationcode record);
    
    Activationcode selectByCode(String code);
}