package study.project.mq;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class MySpringSendMessageMq {

	/**
	 * 点对点：
	 * 		第一个点对点自动应答模式的消息队列测试
	 * @throws Exception 
	 */
	@Test
	public void springSendMessageMq() throws Exception{
		//加载spring的配置文件
		ApplicationContext ac  = new ClassPathXmlApplicationContext("classpath*:applicationContext-mq.xml");
		
		//获取发送消息的模版对象JmsTemplate
		JmsTemplate jmsTemplate = ac.getBean(JmsTemplate.class);
		
		//获取消息发送目的地
		//Destination destination = (Destination)ac.getBean("myqueue");
		
		//使用订阅模式：把上面的注释掉，把这一行放开就行
		Destination destination = (Destination)ac.getBean("mytopic");
		
		//发送消息
		jmsTemplate.send(destination , new MessageCreator() {
			
			public Message createMessage(Session session) throws JMSException {
				
				return session.createTextMessage("凤姐喜欢你！！！！！！！！！！！！！！！");
			}
		});
	}
}
