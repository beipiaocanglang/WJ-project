package study.project.mq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;

public class MySendMessageMq {

	/**
	 * 点对点：
	 * 		第一个点对点自动应答模式的消息队列测试
	 * @throws Exception 
	 */
	@Test
	public void sendMessageMq() throws Exception{
		//创建消息工厂：参数：协议、地址、端口
		ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://172.16.143.128:61616");
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
		Queue queue = session.createQueue("oneQueue");
		
		//创建消息发送者
		MessageProducer producer = session.createProducer(queue);
		
		//创建模拟消息
		TextMessage tm = new ActiveMQTextMessage();
		tm.setText("这是第一个点对点模式自动应答的消息队列的第一次测试");
		
		//发送消息
		producer.send(tm);
		
		//关闭资源
		producer.close();
		session.close();
		connection.close();
	}
}
