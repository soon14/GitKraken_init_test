package cn.rf.hz.web.cloudpark.daoxx;

import org.apache.ibatis.annotations.Param;

import cn.rf.hz.web.cloudpark.moder.Pb_operator;

import java.util.Map;

public interface Pb_operatorMapper {
	
	Pb_operator selectByAccountAndPassword(Map<String, Object> map);
	
}
