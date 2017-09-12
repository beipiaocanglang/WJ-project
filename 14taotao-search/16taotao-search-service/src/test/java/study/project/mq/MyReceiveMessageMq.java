package study.project.mq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

/**
 * 测试接收消息
 * @author canglang
 */
public class MyReceiveMessageMq {

	//使用自动接收模式接收消息
	@Test
	public void sendReceive() throws Exception{
		/**
		 * 创建消息工厂：
		 * 		参数：协议、地址、端口
		 */
		ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://192.168.254.66:61616");
		
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
		
		//创建消息接受者从queue中接收消息
		MessageConsumer consumer = session.createConsumer(queue);
		
		//同步接收
		Message message = consumer.receive();
		
		if (message instanceof TextMessage) {
			TextMessage tm = (TextMessage) message;
			System.out.println("************"+tm.getText()+"************");
		}
		
		//关闭资源
		consumer.close();
		session.close();
		connection.close();
	}
	
	/**
	 * 使用异步接收模式接收消息
	 * @throws Exception
	 */
	@Test
	public void sendReceiveTwo() throws Exception{
		/**
		 * 创建消息工厂：
		 * 		参数：协议、地址、端口
		 */
		ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://192.168.254.66:61616");
		
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
		
		//创建消息接受者从queue中接收消息
		MessageConsumer consumer = session.createConsumer(queue);
		
		//使用监听器
		consumer.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message) {
				
				if (message instanceof TextMessage) {
					
					TextMessage tm = (TextMessage) message;
					System.out.println(tm);
				}
			}
		});
		
		//使应用处于等待状态
		System.in.read();
		
		//关闭资源
		consumer.close();
		session.close();
		connection.close();
	}
}
