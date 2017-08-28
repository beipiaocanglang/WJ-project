package study.project.mq;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 测试接收消息
 * @author canglang
 */
public class MySpringReceiveMessageMq {

	/**
	 * spring整合：接收消息
	 * 		点对点模式的消息的接收
	 * 		订阅模式的接收
	 * @throws Exception 
	 */
	@Test
	public void receiveSendMessge() throws Exception{
		//加载spring的配置文件
		ApplicationContext ac  = new ClassPathXmlApplicationContext("classpath*:applicationContext-mq-receive.xml");
		
		//豹祠阻塞状态就行，其他的spring来完成
		System.in.read();
	}
}
