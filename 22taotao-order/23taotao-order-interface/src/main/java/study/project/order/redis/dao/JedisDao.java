package study.project.order.redis.dao;

public interface JedisDao {

	//抽取Jedis的常用方法
	//数据结构string
	public String set(String key, String value);
	public String get(String key);
	//自增、自减
	public Long incr(String key);
	public Long decr(String key);
	
	//数据结构hash
	public Long hset(String key, String field, String value);
	public String hget(String key, String field);
	//删除
	public Long hdel(String key, String field);
	
	//过期设置
	public Long expire(String key, int seconds);
	//查看过期时间
	public Long ttl(String key);
}