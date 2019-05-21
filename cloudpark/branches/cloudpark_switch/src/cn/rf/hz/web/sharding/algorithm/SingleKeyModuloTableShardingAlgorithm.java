package cn.rf.hz.web.sharding.algorithm;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.apache.log4j.Logger;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.SingleKeyTableShardingAlgorithm;
import com.google.common.collect.Range;

public final class SingleKeyModuloTableShardingAlgorithm
        implements SingleKeyTableShardingAlgorithm<String> {
	private final static Logger LOG = Logger.getLogger(SingleKeyModuloTableShardingAlgorithm.class);
    @Override
    public String doEqualSharding(final Collection<String> availableTargetNames,
                                  final ShardingValue<String> shardingValue) {
    	
    	long startTime=System.currentTimeMillis();
    	
        for (String each : availableTargetNames) {
            //if (each.endsWith(shardingValue.getValue() % 2 + "")) { 
           // if (each.endsWith(Math.abs(shardingValue.getValue().hashCode()) % 4 + "")) {
        	//System.out.println(each);
        	long endTime=System.currentTimeMillis();
        	long excTime=(endTime-startTime);
           	LOG.info("查找表："+excTime+"s");
                return each;
         //   }
        }
   
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> doInSharding(final Collection<String> availableTargetNames,
                                           final ShardingValue<String> shardingValue) {
        Collection<String> result = new LinkedHashSet<>(availableTargetNames.size());
        Collection<String> values = shardingValue.getValues();
       	LOG.info("查找表：doInShardingdoInShardingdoInSharding");
        for (String value : values) {
            for (String tableNames : availableTargetNames) {
                if (tableNames.endsWith(value.hashCode() % 2 + "")) {
                    result.add(tableNames);
                }
            }
        }
        return result;
    }

    @Override
    public Collection<String> doBetweenSharding(final Collection<String> availableTargetNames,
                                                final ShardingValue<String> shardingValue) {
        Collection<String> result = new LinkedHashSet<>(availableTargetNames.size());
        Range<String> range = shardingValue.getValueRange();
        //        for (Integer i = range.lowerEndpoint(); i <= range.upperEndpoint(); i++) {
        //            for (String each : availableTargetNames) {
        //                if (each.endsWith(i % 2 + "")) {
        //                    result.add(each);
        //                }
        //            }
        //        }
        return result;
    }
}
