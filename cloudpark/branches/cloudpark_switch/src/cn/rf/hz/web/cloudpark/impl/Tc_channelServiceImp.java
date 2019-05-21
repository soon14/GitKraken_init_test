package cn.rf.hz.web.cloudpark.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.rf.hz.web.cloudpark.daoxx.Tc_channelMapper;
import cn.rf.hz.web.cloudpark.moder.Tc_channel;
import cn.rf.hz.web.cloudpark.service.Tc_channelService;


@Service("channelService")
public class Tc_channelServiceImp implements Tc_channelService{

	@Autowired
	Tc_channelMapper channelmapper;
	
	@Override
	public Tc_channel queryRuleByChannelid(int channelId) {
		// TODO Auto-generated method stub
		Tc_channel channel =	channelmapper.selectByPrimaryKey(channelId);
		return channel;
	}

}
