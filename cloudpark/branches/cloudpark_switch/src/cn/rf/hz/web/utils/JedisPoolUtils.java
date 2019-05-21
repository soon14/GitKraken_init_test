package cn.rf.hz.web.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

/**
 * 链接 停车助手 数据库的basedao Dao的基类，使用JDBC连接池数据库、释放资源、执行sql，可以被其他Dao实现类继承或实例化使用
 * 
 * @author xuhao
 * @version 1.0
 */
public class JedisPoolUtils {
	private final static Logger logger = Logger.getLogger(JedisPoolUtils.class);

	// private int expireTime = ShardedJedisConfig.expireTime(); //
	private int expireTime = 0;

	/** 加锁标志 */
	public static final String LOCKED = "TRUE";
	/** 毫秒与毫微秒的换算单位 1毫秒 = 1000000毫微秒 */
	public static final long MILLI_NANO_CONVERSION = 1000 * 1000L;
	/** 默认超时时间（毫秒） */
	public static final long DEFAULT_TIME_OUT = 3000 * 2;
	public static final Random RANDOM = new Random();

	/** 锁的超时时间（秒），过期删除 */
	// public static final int EXPIRE = 3 * 60;
	public static final int EXPIRE = 60;

	private static JedisPool pool;

	/**
	 * 建立连接池 真实环境，一般把配置参数缺抽取出来。
	 */
	private static void createJedisPool() {

		// 建立连接池配置参数
		JedisPoolConfig config = new JedisPoolConfig();

		/*
		 * config.setTestOnBorrow(true); // 设置最大连接数 config.setMaxTotal(300); //
		 * 设置空间连接 config.setMaxIdle(100); // 设置最大阻塞时间，记住是毫秒数milliseconds
		 * config.setMaxWaitMillis(1000);
		 */

		config.setMaxTotal(70);//最大连接数
		config.setMaxIdle(10);//最大空闲连接数
		config.setMinIdle(5);//最小空闲连接数
		config.setMaxWaitMillis(5000);//获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
		config.setTestOnBorrow(false);//在获取连接的时候检查有效性, 默认false
		config.setLifo(true);
		config.setTestWhileIdle(true);//在空闲时检查有效性, 默认false
		config.setTimeBetweenEvictionRunsMillis(5000);//逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程
		config.setNumTestsPerEvictionRun(10);//每次逐出检查时 逐出的最大数目
		config.setMinEvictableIdleTimeMillis(300000);//逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
		// config.setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy");

		// 创建连接池
		// pool = new JedisPool(config, "121.41.112.86", 6379);

		pool = new JedisPool(config, ConfigUtil.jedisHost, ConfigUtil.jedisPort, ConfigUtil.timeout,
				ConfigUtil.password, ConfigUtil.database);

	}

	/**
	 * 在多线程环境同步初始化
	 */
	private static synchronized void poolInit() {
		if (pool == null)
			createJedisPool();
	}

	/**
	 * ��ȡһ��jedis ����
	 * 
	 * @return
	 */
	public static Jedis getJedis() {

		if (pool == null)
			poolInit();
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			// jedis.auth("rFu40nr72hc93bv5dnmirdalj90jf5jw");
			if (!"".equals(ConfigUtil.jedisAuth) && null != ConfigUtil.jedisAuth)
				jedis.auth(ConfigUtil.jedisAuth);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// pool.returnBrokenResource(jedis);
			 logger.error("池中所有在用实例数量pool.getNumActive()："+pool.getNumActive());
			 logger.error("池中处于闲置状态的实例pool.getNumIdle()"+pool.getNumIdle());
			 logger.error("等待的连接数：getNumWaiters"+pool.getNumWaiters());
			 logger.error("Get jedis error,error is  : "+e.getMessage());
			e.printStackTrace();
		}
		return jedis;
	}

	/**
	 * 获取一个jedis 对象
	 * 
	 * @return
	 */
	public static void returnRes(Jedis jedis) {
		try {
			// pool.returnResourceObject(jedis);
			if (jedis != null) {
				// pool.close();
				jedis.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /*finally {
			if (jedis != null) {
				jedis.close();
			}
		}*/
	}

	public static void set(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.set(key, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// pool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally {
			// 返还到连接池
			returnRes(jedis);
		}

	}

	public static void setex(String key, int seconds, String value) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.setex(key, seconds, value);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnRes(jedis);
		}

	}

	public static String get(String key) {
		String value = null;
		Jedis jedis = null;
		try {
			jedis = getJedis();
			value = jedis.get(key);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnRes(jedis);
		}
		return value;
	}

	public static long del(String key) {
		Jedis jedis = null;
		long l = 0;
		try {
			jedis = getJedis();
			l = jedis.del(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnRes(jedis);
		}

		return l;
	}

	public static Set<String> keys(String pattern) {
		Jedis jedis = null;
		Set<String> set = null;
		try {
			jedis = getJedis();
			set = jedis.keys(pattern);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnRes(jedis);
		}
		return set;
	}

	public static boolean exists(String key) {
		Jedis jedis = null;
		boolean bl = false;
		try {
			jedis = getJedis();
			bl = jedis.exists(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			returnRes(jedis);
		}

		return bl;
	}

	public static boolean hexists(String key, String field) {
		Jedis jedis = null;
		boolean bl = false;
		try {
			jedis = getJedis();
			bl = jedis.hexists(key, field);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnRes(jedis);
		}

		return bl;
	}

	/**
	 * Hash 保存数据
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public static Long hset(String key, String field, String value) {
		Jedis jedis = null;
		Long l = 0L;
		try {
			jedis = JedisPoolUtils.getJedis();
			l = jedis.hset(key, field, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnRes(jedis);
		}

		return l;
	}

	public static Long hsetB(byte[] key, byte[] field, byte[] value) {
		Jedis jedis = null;
		Long l = 0L;
		try {
			jedis = JedisPoolUtils.getJedis();
			l = jedis.hset(key, field, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnRes(jedis);
		}

		return l;
	}

	/**
	 * hash 获取数据
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public static String hget(String key, String field) {
		Jedis jedis = null;
		String str = null;
		try {
			jedis = JedisPoolUtils.getJedis();
			str = jedis.hget(key, field);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnRes(jedis);
		}

		return str;
	}

	/**
	 * hash 删除
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public static Long hdel(String key, String field) {
		Jedis jedis = null;
		Long l = 0L;
		try {
			jedis = JedisPoolUtils.getJedis();
			l = jedis.hdel(key, field);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnRes(jedis);
		}

		return l;
	}

	/**
	 * 根据key获取所有value
	 * 
	 * @param key
	 * @return
	 */
	public static List<String> hvals(String key) {
		Jedis jedis = null;
		List<String> list = null;
		try {
			jedis = JedisPoolUtils.getJedis();
			list = jedis.hvals(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnRes(jedis);
		}
		return list;
	}

	public static Set<String> hkeys(String key) {
		Jedis jedis = null;
		Set<String> set = null;
		try {
			jedis = JedisPoolUtils.getJedis();
			set = jedis.hkeys(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnRes(jedis);
		}

		return set;
	}

	public static Long incr(String key) {
		Jedis jedis = null;
		Long l = 0L;
		try {
			jedis = JedisPoolUtils.getJedis();
			l = jedis.incr(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnRes(jedis);
		}

		return l;

	}

	public static Long hincrBy(final String key, final String field, final long value) {
		Jedis jedis = null;
		Long l = 0L;
		try {
			jedis = JedisPoolUtils.getJedis();
			l = jedis.hincrBy(key, field, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnRes(jedis);
		}
		return l;

	}

	public static Long rpush(String key, String data) {
		Jedis jedis = null;
		Long l = 0L;
		try {
			jedis = JedisPoolUtils.getJedis();
			l = jedis.rpush(key, data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnRes(jedis);
		}

		return l;

	}

	public static List<String> lrange(String key, long start, long end) {
		Jedis jedis = null;
		List<String> list = null;
		try {
			jedis = JedisPoolUtils.getJedis();
			list = jedis.lrange(key, start, end);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnRes(jedis);
		}

		return list;

	}

	public static Long lrem(String key, long count, String data) {
		Jedis jedis = null;
		Long l = 0L;
		try {
			jedis = JedisPoolUtils.getJedis();
			l = jedis.lrem(key, count, data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnRes(jedis);
		}

		return l;

	}

	public static Long publish(String st1, String st2) {
		Jedis jedis = null;
		Long l = 0L;
		try {
			jedis = JedisPoolUtils.getJedis();
			l = jedis.publish(st1, st2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnRes(jedis);
		}

		return l;

	}

	public static void subscribe(JedisPubSub jedisPubSub, String channels) {
		Jedis jedis = null;
		try {
			jedis = JedisPoolUtils.getJedis();
			jedis.subscribe(jedisPubSub, channels);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			returnRes(jedis);
		}

	}

	/**
	 * 加锁 应该以： lock(); try { doSomething(); } finally { unlock()； } 的方式调用
	 * 
	 * @param timeout
	 *            超时时间
	 * @return 成功或失败标志
	 */

	// lrange mylist 0 2
	public static boolean lock(String keyCode, String bizId, long timeout) {
		Jedis jedis = JedisPoolUtils.getJedis();
		String key = keyCode + "_" + bizId + "_lock";
		long nano = System.nanoTime();
		timeout *= MILLI_NANO_CONVERSION;
		try {
			while ((System.nanoTime() - nano) < timeout) {
				if (jedis.setnx(key, LOCKED) == 1) {
					logger.warn("keyCode=" + keyCode + ",bizId=" + bizId + ":acquire a lock sucess");
					jedis.expire(key, EXPIRE);
					return true;
				}
				// 短暂休眠，避免出现活锁 ,减少对redis的压力,防止在大并发情况下把redis压垮
				logger.warn("keyCode=" + keyCode + ",bizId=" + bizId + ":wait a lock");
				Thread.sleep(3, RANDOM.nextInt(500));
			}
		} catch (Exception e) {
			throw new RuntimeException("Locking error", e);
		} finally {
			returnRes(jedis);
		}
		logger.warn("keyCode=" + keyCode + ",bizId=" + bizId + ":acquire a lock fail");
		return false;
	}

	/**
	 * 加锁 应该以： lock(); try { doSomething(); } finally { unlock()； } 的方式调用
	 * 
	 * @param timeout
	 *            超时时间
	 * @param expire
	 *            锁的超时时间（秒），过期删除
	 * @return 成功或失败标志
	 */
	public static boolean lock(String keyCode, String bizId, long timeout, int expire) {
		Jedis jedis = JedisPoolUtils.getJedis();
		long nano = System.nanoTime();
		String key = keyCode + "_" + bizId + "_lock";
		timeout *= MILLI_NANO_CONVERSION;
		try {
			while ((System.nanoTime() - nano) < timeout) {
				if (jedis.setnx(key, LOCKED) == 1) {
					logger.warn("keyCode=" + keyCode + ",bizId=" + bizId + ":acquire a lock sucess");
					jedis.expire(key, expire);
					return true;
				}
				logger.warn("keyCode=" + keyCode + ",bizId=" + bizId + ":wait a lock");
				// 短暂休眠，避免出现活锁 ,减少对redis的压力,防止在大并发情况下把redis压垮
				Thread.sleep(3, RANDOM.nextInt(500));
			}
		} catch (Exception e) {
			throw new RuntimeException("Locking error", e);
		} finally {
			returnRes(jedis);
		}
		logger.warn("keyCode=" + keyCode + ",bizId=" + bizId + ":acquire a lock fail");
		return false;
	}

	/**
	 * 加锁 应该以： lock(); try { doSomething(); } finally { unlock()； } 的方式调用
	 * 
	 * @return 成功或失败标志
	 */
	public static boolean lock(String keyCode, String bizId) {
		return lock(keyCode, bizId, DEFAULT_TIME_OUT);
	}

	/**
	 * 解锁 无论是否加锁成功，都需要调用unlock 应该以： lock(); try { doSomething(); } finally {
	 * unlock()； } 的方式调用
	 */
	public static void unlock(String keyCode, String bizId) {
		Jedis jedis = JedisPoolUtils.getJedis();
		try {
			String key = keyCode + "_" + bizId + "_lock";
			Long l = jedis.del(key);
			logger.warn("keyCode=" + keyCode + ",bizId=" + bizId + ",ret=" + String.valueOf(l) + ":release a lock");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnRes(jedis);
		}
	}

	// public static String setex(String key, Integer seconds, String value )
	// {
	// Jedis jedis = JedisPoolUtils.getJedis();
	// String str = jedis.setex(key, seconds, value);
	// returnRes(jedis);
	// return str;
	//
	// }

	public static void main(String[] args) {
		Jedis jedis = JedisPoolUtils.getJedis();
		// hdel("App","count");
		// long l=hincrBy("App","count",1);
		// String ss=hget("App","count");
		// hdel("App","count");
		// System.out.println(l);
		// System.out.println(ss);

		// jedis.setex(key, seconds, value)

		// Set<String> set = jedis.hkeys("license634902");
		// System.out.println(set.toString());
		//
		// System.out.println(jedis.hget("license634902", "450"));

		// for(String str : set)
		// {
		// jedis.hdel("toll634902", str);
		// }
		//
		// toll634902,936621106_-1537857017
		// hdel("toll634902", field)

		returnRes(jedis);
	}

}
