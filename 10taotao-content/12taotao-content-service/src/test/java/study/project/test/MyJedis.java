package study.project.test;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class MyJedis {

	/**
	 * 测试单机版的Jedis连接Redis
	 */
	@Test
	public void testRedis01(){
		//创建Jedis对象，连接redis缓存数据库
		Jedis jedis = new Jedis("192.168.254.66", 6379);
		jedis.set("itemName", "洗脚盆");
		String itemName = jedis.get("itemName");
		System.out.println(itemName);
	}
	/**
	 * 使用单机版的jedis连接池连接redis
	 */
	@Test
	public void testPoolRedis(){
		//创建jedis配置对象
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		//设置最大空闲数
		poolConfig.setMaxIdle(20);
		//设置最大连接数
		poolConfig.setMaxTotal(10000);
		//创建JedisPool对象
		JedisPool jedisPool = new JedisPool(poolConfig, "192.168.254.66", 6379);
		//获取redis对象
		Jedis jedis = jedisPool.getResource();
		jedis.set("itemName", "洗脚盆wwww");
		String itemName = jedis.get("itemName");
		System.out.println(itemName);
	}
	/**
	 * 使用集群版的jedis连接池连接redis集群
	 */
	@Test
	public void testPoolClusterRedis(){
		//创建jedis配置对象
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		//设置最大空闲数
		poolConfig.setMaxIdle(20);
		//设置最大连接数
		poolConfig.setMaxTotal(10000);
		
		Set<HostAndPort> nodes = new HashSet<HostAndPort>();
		nodes.add(new HostAndPort("192.168.254.66", 7001));
		nodes.add(new HostAndPort("192.168.254.66", 7002));
		nodes.add(new HostAndPort("192.168.254.66", 7003));
		nodes.add(new HostAndPort("192.168.254.66", 7004));
		nodes.add(new HostAndPort("192.168.254.66", 7005));
		nodes.add(new HostAndPort("192.168.254.66", 7006));
		nodes.add(new HostAndPort("192.168.254.66", 7007));
		nodes.add(new HostAndPort("192.168.254.66", 7008));
		//创建JedisCluster对象
		JedisCluster jedisCluster = new JedisCluster(nodes, poolConfig);
		jedisCluster.set("itemName", "iphone6s plus");
		String itemName = jedisCluster.get("itemName");
		System.out.println(itemName);
	}
}






















