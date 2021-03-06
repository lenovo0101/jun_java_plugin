package com.caland.common.web.session.cache;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.InitializingBean;
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class MemcachedDangaCache implements SessionCache, InitializingBean {
	private MemCachedClient client;
//	private String[] servers = {"192.168.100.201:11211","192.168.100.150:11211"};
	private String[] servers = {"192.168.2.128:11211"};
//	private Integer[] weights = {1,6};
	private Integer[] weights = {1};

	@SuppressWarnings("unchecked")
	public HashMap<String, Serializable> getSession(String root) {
		return (HashMap<String, Serializable>) client.get(root);
	}

	public void setSession(String root, Map<String, Serializable> session,
			int exp) {
		client.set(root, session, new Date(System.currentTimeMillis() + exp* 60 * 1000));
	}

	public Serializable getAttribute(String root, String name) {
		HashMap<String, Serializable> session = getSession(root);
		return session != null ? session.get(name) : null;
	}

	public void setAttribute(String root, String name, Serializable value,
			int exp) {
		HashMap<String, Serializable> session = getSession(root);
		if (session == null) {
			session = new HashMap<String, Serializable>();
		}
		session.put(name, value);
		Date expDate = new Date(System.currentTimeMillis() + exp * 60 * 1000);
		client.set(root, session, expDate);
	}

	public void clear(String root) {
		client.delete(root);
	}

	public boolean exist(String root) {
		return client.keyExists(root);
	}

	public void afterPropertiesSet() throws Exception {
		client = new MemCachedClient();
		// grab an instance of our connection pool
		SockIOPool pool = SockIOPool.getInstance();

		// set the servers and the weights
		pool.setServers(servers);
		pool.setWeights(weights);

		// set some basic pool settings
		// 5 initial, 5 min, and 250 max conns
		// and set the max idle time for a conn
		// to 6 hours
		pool.setInitConn(5);
		pool.setMinConn(5);
		pool.setMaxConn(250);
		pool.setMaxIdle(1000 * 60 * 60 * 6);

		// set the sleep for the maint thread
		// it will wake up every x seconds and
		// maintain the pool size
		pool.setMaintSleep(30);

		// set some TCP settings
		// disable nagle
		// set the read timeout to 3 secs
		// and don't set a connect timeout
		pool.setNagle(false);
		pool.setSocketTO(3000);
		pool.setSocketConnectTO(0);

		// initialize the connection pool
		pool.initialize();

		// lets set some compression on for the client
		// compress anything larger than 64k
		client.setCompressEnable(true);
		client.setCompressThreshold(64 * 1024);
	}

	public String[] getServers() {
		return servers;
	}

	public void setServers(String[] servers) {
		this.servers = servers;
	}

	public Integer[] getWeights() {
		return weights;
	}

	public void setWeights(Integer[] weights) {
		this.weights = weights;
	}
	@Test
//	@Ignore
	public void testMemcached(){

		MemCachedClient c = new MemCachedClient();
		SockIOPool pool = SockIOPool.getInstance();
		pool.setServers(servers);
		pool.setWeights(weights);
		pool.setInitConn(5);
		pool.setMinConn(5);
		pool.setMaxConn(250);
		pool.setMaxIdle(1000 * 60 * 60 * 6);
		pool.setMaintSleep(30);
		pool.setNagle(false);
		pool.setSocketTO(3000);
		pool.setSocketConnectTO(0);
		pool.initialize();
		c.setCompressEnable(true);
		c.setCompressThreshold(64 * 1024);
		String key = "t11";//???????????????
		Object object = c.get(key);
		System.out.println(object);
//		String value = "lance";
//		int exp = 1;
//		c.set(key, value);//t11 : lance
//		boolean keyExists1 = c.keyExists(key);
//		System.out.println("1??????????" + keyExists1);
//		Object s = c.get(key);
//		System.out.println(s);
//		c.set(key, value);
//		boolean keyExists2 = c.keyExists(key);
//		System.out.println("2??????????" + keyExists2);
		
	}
	
	@Test
	public void testRedis(){
//		   JedisPool pool;  
//		    Jedis jedis;  
//		        pool = new JedisPool(new JedisPoolConfig(), "172.16.100.184");  
		  
//		        jedis = pool.getResource();  
//		        jedis.auth("password");  
//		Jedis jedis = new Jedis("192.168.100.150");  
//		String keys = "foo";  
		// ?????????  
//		jedis.del(keys);
		// ?????????  
//		jedis.set(keys, "zhangsan");
		// ?????????  
//		String value = jedis.get(keys);
//		System.out.println(value); 
		 
		
		//??????Jedis??????????????????Jedis?????????redis??????????????????
		//????????????
//		JedisPoolConfig config = new JedisPoolConfig();
//		config.setMaxActive(100);
//		config.setMaxIdle(10);
//		config.setMaxWait(100);
//		config.setTestOnBorrow(true);
//		config.setTestWhileIdle(true);
//		config.setMinEvictableIdleTimeMillis(1000);
//		config.setTimeBetweenEvictionRunsMillis(1000);
//		config.setNumTestsPerEvictionRun(1000);
//		new JedisPool(config, "192.168.100.150", 6379);
		 //????????????jedis??????
//		Jedis jedis = pool.getResource();
		// ??????jedis???????????????
//		pool.returnResource(jedis);
		 
		/*
		 
		//Jedis??????API
		//????????????API
		//????????????0?????????????????????????????????
		jedis.select(1);
		//?????????????????????
		jedis.flushDB();
		//?????????????????????
		jedis.flushAll();
		 //key??????API
		String key = "zhang";
		//key????????????3s
		jedis.expire(key, 3);
		//?????????key
		jedis.rename("zhang", "zhang1");
		//????????????key?????????boolean
		jedis.exists(key);
		//??????key
		Set<String> keys = jedis.keys("*");
		 //String??????API
		//??????key-value
		jedis.set("ye", "liang");
		//??????key??????value
		System.out.println(jedis.get("ye"));
		//??????key
		jedis.del("ye");
		 //Map??????API
		//???map??????redis
		jedis.hset("student:1", "NUM", "1");
		jedis.hset("student:1", "NAME", "zhangsan");
		jedis.hset("student:2", "NUM", "2");
		jedis.hset("student:2", "NAME", "lisi");
		//????????????map
		jedis.hdel("student:1", "NUM");
		//??????map
		System.out.println(jedis.hget("student:1", "NAME"));
		//??????map
		Set<String> fields = jedis.hkeys("student:2");
		for(String field : fields){
		    System.out.println(field+":"+jedis.hget("student:2", field));
		}
		//List??????API
		//????????????  
		jedis.lpush("lists", "aa");  
		jedis.lpush("lists", "bb");  
		jedis.lpush("lists", "cc");  
		//list??????
		System.out.println(jedis.llen("lists"));
		//??????????????????????????????
		System.out.println(jedis.lrange("lists", 0, 2));  
		//?????????????????????
		jedis.lset("lists", 0, "dd");  
		//????????????????????? 
		System.out.println(jedis.lindex("lists", 0));  
		//?????????????????????value???bb???????????????
		System.out.println(jedis.lrem("lists", 1, "bb"));  
		//???????????????????????????  
		System.out.println(jedis.ltrim("lists", 0, 1));
		 //Set??????API
		//????????????  
		jedis.sadd("sets", "aa");  
		jedis.sadd("sets", "bb");  
		jedis.sadd("sets", "cc");  
		//??????value??????????????????  
		System.out.println(jedis.sismember("sets", "bb"));  
		//???????????????  
		System.out.println(jedis.smembers("sets"));  
		//??????????????????  
		System.out.println(jedis.srem("sets", "bb"));
		//SortedSet??????API 
		//????????????  
		jedis.zadd("zset", 11, "aa");  
		jedis.zadd("zset", 12, "bb");  
		jedis.zadd("zset", 13, "cc");  
		jedis.zadd("zset", 14, "dd");  
		//????????????  
		System.out.println(jedis.zcard("zset"));  
		//????????????  
		System.out.println(jedis.zscore("zset", "cc"));  
		//???????????????  
		System.out.println(jedis.zrange("zset", 0, -1));  
		//????????????  
		System.out.println(jedis.zrem("zset", "cc"));
		//score????????????????????????
		System.out.println(jedis.zcount("zset", 11, 14));
		*/
	}
	
}
