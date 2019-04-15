package cn.rf.hz.web.mapper.gd;

import java.util.Map;

public interface XiHuRuleMapper
{

	void delXiHuRuleVip(Integer carParkId);

	void delXiHuRuleShopping(Integer carParkId);

	int insertXiHuRuleVip(Map<String, Object> map);

	int insertXiHuRuleShopping(Map<String, Object> map);
	
}
