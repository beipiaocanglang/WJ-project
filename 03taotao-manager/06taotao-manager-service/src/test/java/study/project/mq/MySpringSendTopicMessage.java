package study.project.mq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;

/**
 * 订阅模式发送消息
 * @author canglang
 */
public class MySpringSendTopicMessage {

	/**
	 * 发布订阅模式
	 */
	@Test
	public void springSendTopicMessage() throws Exception{
		/**
		 * 创建消息工厂：
		 * 		参数：协议、地址、端口
		 */
		ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://192.168.145.129:61616");
		
		//获取连接
		Connection connection = cf.createConnection();
		
		//开启连接
		connection.start();
		
		/**
		 * 从连接中获取Session
		 * 第一个参数：消息事物
		 * 第二个参数：事物使用自动应答模式
		 */
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		/**
		 * 获取消息发送的目的地
		 * 创建消息发送的目的地，相当于在JMS消息服务器中开辟一块叫oneQueue的空间
		 * 发送消息时就发送到oneQueue消息目的地
		 */
		Topic topic = session.createTopic("myTopic");
		
		//创建消息发送者
		MessageProducer producer = session.createProducer(topic);
		
		//创建模拟消息
		TextMessage tm = new ActiveMQTextMessage();
		tm.setText("这是订阅模式消息队列的第一次测试");
		
		//发送消息
		producer.send(tm);
		
		//关闭资源
		producer.close();
		session.close();
		connection.close();
	}
}
