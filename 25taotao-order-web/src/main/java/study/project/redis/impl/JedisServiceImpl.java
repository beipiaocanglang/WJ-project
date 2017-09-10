package study.project.redis.impl;

import org.springframework.stereotype.Repository;
import redis.clients.jedis.JedisCluster;
import study.project.redis.dao.JedisDao;
import study.project.redis.service.JedisService;

import javax.annotation.Resource;

@Repository
public class JedisServiceImpl implements JedisService {

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
}
