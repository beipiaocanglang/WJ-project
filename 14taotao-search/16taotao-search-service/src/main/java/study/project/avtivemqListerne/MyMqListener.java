package study.project.avtivemqListerne;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * activeMQ监听器
 * @author canglang
 */
public class MyMqListener implements MessageListener{

	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			TextMessage tm = (TextMessage)message;
			try {
				System.out.println("自定义监听器之点对点模式的消息："+tm.getText());
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}
}
