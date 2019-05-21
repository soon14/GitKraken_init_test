package cn.rf.hz.web.service.gd;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.rf.hz.web.mapper.gd.XiHuRuleMapper;

@Service("xiHuRuleService")
public class XiHuRuleService
{
	@Autowired
	private XiHuRuleMapper mapper;

	public XiHuRuleMapper getMapper()
	{
		return mapper;
	}

	public void setMapper(XiHuRuleMapper mapper)
	{
		this.mapper = mapper;
	}

	public void delXiHuRuleVip(Integer carParkId)
	{
		getMapper().delXiHuRuleVip(carParkId);
	}

	public void delXiHuRuleShopping(Integer carParkId)
	{
		getMapper().delXiHuRuleShopping(carParkId);
		
	}

	public int insertXiHuRuleVip(Map<String, Object> map)
	{
		return getMapper().insertXiHuRuleVip(map);
	}

	public int insertXiHuRuleShopping(Map<String, Object> map)
	{
		return getMapper().insertXiHuRuleShopping(map);
	}
	
	
}
