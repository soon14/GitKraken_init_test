package cn.rf.hz.web.cloudpark.daoxx;

import cn.rf.hz.web.cloudpark.moder.Tc_weekrule_history;
import cn.rf.hz.web.cloudpark.moder.Tc_weekrule_historyKey;

public interface Tc_weekrule_historyMapper {
    int deleteByPrimaryKey(Tc_weekrule_historyKey key);

    int insert(Tc_weekrule_history record);

    int insertSelective(Tc_weekrule_history record);

    Tc_weekrule_history selectByPrimaryKey(Tc_weekrule_historyKey key);

    int updateByPrimaryKeySelective(Tc_weekrule_history record);

    int updateByPrimaryKey(Tc_weekrule_history record);
}