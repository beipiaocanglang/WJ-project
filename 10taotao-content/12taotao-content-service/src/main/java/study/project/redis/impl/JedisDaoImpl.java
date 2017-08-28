package study.project.redis.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import study.project.redis.JedisDao;
@Repository
public class JedisDaoImpl implements JedisDao {

	//**************集群版*********************
	@Resource
	private JedisCluster jedisCluster;
	
	@Override
	public String set(String key, String value) {
		String set = jedisCluster.set(key, value);
		return set;
	}

	@Override
	public String get(String key) {
		String get = jedisCluster.get(key);
		return get;
	}

	@Override
	public Long incr(String key) {
		Long incr = jedisCluster.incr(key);
		return incr;
	}

	@Override
	public Long decr(String key) {
		Long decr = jedisCluster.decr(key);
		return decr;
	}

	@Override
	public Long hset(String key, String field, String value) {
		Long hset = jedisCluster.hset(key, field, value);
		return hset;
	}

	@Override
	public String hget(String key, String field) {
		String hget = jedisCluster.hget(key, field);
		return hget;
	}

	@Override
	public Long hdel(String key, String field) {
		Long hdel = jedisCluster.hdel(key, field);
		return hdel;
	}

	@Override
	public Long expire(String key, int seconds) {
		Long expire = jedisCluster.expire(key, seconds);
		return expire;
	}

	@Override
	public Long ttl(String key) {
		Long ttl = jedisCluster.ttl(key);
		return ttl;
	}

	
	
	
	//*******************单机版的********************
	//注入spring创建好的JedisPool对象
	/*@Resource
	private JedisPool jedisPool;
	
	@Override
	public String set(String key, String value) {
		//从连接池中获取Jedis对象
		Jedis jedis = jedisPool.getResource();
		String keys = jedis.set(key, value);
		return keys;
	}

	@Override
	public String get(String key) {
		//从连接池中获取Jedis对象
		Jedis jedis = jedisPool.getResource();
		String value = jedis.get(key);
		return value;
	}

	@Override
	public Long incr(String key) {
		//从连接池中获取Jedis对象
		Jedis jedis = jedisPool.getResource();
		Long incr = jedis.incr(key);
		return incr;
	}

	@Override
	public Long decr(String key) {
		//从连接池中获取Jedis对象
		Jedis jedis = jedisPool.getResource();
		Long decr = jedis.decr(key);
		return decr;
	}

	@Override
	public Long hset(String key, String field, String value) {
		//从连接池中获取Jedis对象
		Jedis jedis = jedisPool.getResource();
		Long hset = jedis.hset(key, field, value);
		return hset;
	}

	@Override
	public String hget(String key, String field) {
		//从连接池中获取Jedis对象
		Jedis jedis = jedisPool.getResource();
		String hget = jedis.hget(key, field);
		return hget;
	}

	@Override
	public Long hdel(String key, String field) {
		//从连接池中获取Jedis对象
		Jedis jedis = jedisPool.getResource();
		Long hdel = jedis.hdel(key, field);
		return hdel;
	}

	@Override
	public Long expire(String key, int seconds) {
		//从连接池中获取Jedis对象
		Jedis jedis = jedisPool.getResource();
		Long expire = jedis.expire(key, seconds);
		return expire;
	}

	@Override
	public Long ttl(String key) {
		//从连接池中获取Jedis对象
		Jedis jedis = jedisPool.getResource();
		Long ttl = jedis.ttl(key);
		return ttl;
	}*/

}
