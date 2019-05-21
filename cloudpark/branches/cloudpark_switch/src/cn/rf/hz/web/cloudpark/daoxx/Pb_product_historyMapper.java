package cn.rf.hz.web.cloudpark.daoxx;

import cn.rf.hz.web.cloudpark.moder.Pb_product_history;
import cn.rf.hz.web.cloudpark.moder.Pb_product_historyKey;

public interface Pb_product_historyMapper {
    int deleteByPrimaryKey(Pb_product_historyKey key);

    int insert(Pb_product_history record);

    int insertSelective(Pb_product_history record);

    Pb_product_history selectByPrimaryKey(Pb_product_historyKey key);

    int updateByPrimaryKeySelective(Pb_product_history record);

    int updateByPrimaryKey(Pb_product_history record);
}