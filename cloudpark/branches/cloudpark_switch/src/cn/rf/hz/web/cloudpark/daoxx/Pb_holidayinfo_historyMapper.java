package cn.rf.hz.web.cloudpark.daoxx;

import cn.rf.hz.web.cloudpark.moder.Pb_holidayinfo_history;
import cn.rf.hz.web.cloudpark.moder.Pb_holidayinfo_historyKey;

public interface Pb_holidayinfo_historyMapper {
    int deleteByPrimaryKey(Pb_holidayinfo_historyKey key);

    int insert(Pb_holidayinfo_history record);

    int insertSelective(Pb_holidayinfo_history record);

    Pb_holidayinfo_history selectByPrimaryKey(Pb_holidayinfo_historyKey key);

    int updateByPrimaryKeySelective(Pb_holidayinfo_history record);

    int updateByPrimaryKey(Pb_holidayinfo_history record);
}