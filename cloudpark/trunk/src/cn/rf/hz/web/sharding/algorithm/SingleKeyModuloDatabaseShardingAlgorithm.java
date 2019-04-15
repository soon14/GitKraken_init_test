package cn.rf.hz.web.sharding.algorithm;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.SingleKeyDatabaseShardingAlgorithm;


import com.google.common.collect.Range;
import cn.rf.hz.web.utils.StringUtil;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SingleKeyModuloDatabaseShardingAlgorithm implements SingleKeyDatabaseShardingAlgorithm<String> {
    
	//private final static Logger LOG = Logger.getLogger(SingleKeyModuloDatabaseShardingAlgorithm.class);
	private Logger logger = LoggerFactory.getLogger(SingleKeyModuloDatabaseShardingAlgorithm.class);


	@Override
	public String doEqualSharding(Collection<String> availableTargetNames, ShardingValue<String> shardingValue) {

		String parkNo=shardingValue.getValue();

        for (String each : availableTargetNames) {
        	 if (each.endsWith((StringUtil.getAsciiCode(parkNo))% 4+ "")) {
        		 logger.info("database-->select:[{}]",each);
                return each;
            }
        }
        throw new UnsupportedOperationException();
	}

	@Override
	public Collection<String> doInSharding(Collection<String> arg0, ShardingValue<String> arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Collection<String> doBetweenSharding(Collection<String> arg0, ShardingValue<String> arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
