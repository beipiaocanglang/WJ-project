package study.project.test;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class MySpringRedis {

	/**
	 * 测试单机版的spring连接Redis
	 */
	@Test
	public void testSpringRedis(){
		//加载spring的配置文件
		ApplicationContext app = new ClassPathXmlApplicationContext("classpath*:applicationContext-redis.xml");
		
		Jedis jedis = app.getBean(Jedis.class);
		
		jedis.set("name", "lisi");
		
		String name = jedis.get("name");
		
		System.out.println(name);
	}
	/**
	 * 使用spring整合单机版的jedis连接池连接redis
	 */
	@Test
	public void testSpringPoolRedis(){
		//加载spring的配置文件
		ApplicationContext app = new ClassPathXmlApplicationContext("classpath*:applicationContext-redis.xml");
		
		JedisPool jedisPool = app.getBean(JedisPool.class);
		
		Jedis jedis = jedisPool.getResource();
		
		jedis.set("name", "zhangsan");
		
		String name = jedis.get("name");
		
		System.out.println(name);
	}
	/**
	 * 使用spring整合集群版的jedis连接池连接redis集群
	 */
	@Test
	public void testSpringJedisCluster(){
		//加载spring的配置文件
		ApplicationContext app = new ClassPathXmlApplicationContext("classpath*:applicationContext-redis.xml");
		
		JedisCluster jedisCluster = app.getBean(JedisCluster.class);
		
		jedisCluster.set("itemName", "iphone6s plus iphone6s");
		String itemName = jedisCluster.get("itemName");
		System.out.println(itemName);
				
	}
}



















