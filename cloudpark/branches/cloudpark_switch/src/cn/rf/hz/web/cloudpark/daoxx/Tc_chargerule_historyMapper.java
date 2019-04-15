package cn.rf.hz.web.cloudpark.daoxx;

import cn.rf.hz.web.cloudpark.moder.Tc_chargerule_history;
import cn.rf.hz.web.cloudpark.moder.Tc_chargerule_historyKey;

public interface Tc_chargerule_historyMapper {
    int deleteByPrimaryKey(Tc_chargerule_historyKey key);

    int insert(Tc_chargerule_history record);

    int insertSelective(Tc_chargerule_history record);

    Tc_chargerule_history selectByPrimaryKey(Tc_chargerule_historyKey key);

    int updateByPrimaryKeySelective(Tc_chargerule_history record);

    int updateByPrimaryKey(Tc_chargerule_history record);
}