package cn.rf.hz.web.cloudpark.daoxx;

import cn.rf.hz.web.cloudpark.moder.Tc_recognitioncamera;

public interface Tc_recognitioncameraMapper {
    int deleteByPrimaryKey(Integer cameraid);

    int insert(Tc_recognitioncamera record);

    int insertSelective(Tc_recognitioncamera record);

    Tc_recognitioncamera selectByPrimaryKey(Integer cameraid);

    int updateByPrimaryKeySelective(Tc_recognitioncamera record);

    int updateByPrimaryKey(Tc_recognitioncamera record);
}