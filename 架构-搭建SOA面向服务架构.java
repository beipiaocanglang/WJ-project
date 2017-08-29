���������̳�Ϊ�������ܹ�SOA���̡�
һ������˼·��
	Taotao-manager-web��ȡ���������ٱ�taotao-manager�ۺ�
	
	Taotao-manager-service����war
	
	�����Ժ�ܹ���
	
	��ͼ1

�����������
	1������06taotao-manager-service
		1.1������Ŀ�����������ܹ���֣�
			��Ŀҵ����뵥�����һ�������ṩ�����ֲ������ã������Ҫ��ҵ����ֳ�һ������(war).
			��ͼ2

		1.2������07taotao-manager-web������webapp�ļ��е�06taotao-manager-service��Ŀ��
		
		1.3������spring��������ļ�
			applicationContext-dao.xml
			applicationContext-service.xml
			jdbc.properties
			log4j.properties
			sqlMapConfig.xml
			
			ע�⣺
				Service��ʱ���web��Ŀ����Ҫ���������ļ�
				
		
		1.4��web.xml����spring�������ļ�	
			<listener>
				<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
			</listener>
			<context-param>
				<param-name>contextConfigLocation</param-name>
				<param-value>classpath:applicationContext-*.xml</param-value>
			</context-param>
			
			ע�⣺
				ֻ��Ҫ����spring�����ļ�����
		
		1.5�����dubbo����
			<!-- dubbo��� -->
			<dependency>
				<groupId>com.alibaba</groupId>
				<artifactId>dubbo</artifactId>
				<version>${dubbo.version}</version>
			</dependency>
			<dependency>
				<groupId>org.apache.zookeeper</groupId>
				<artifactId>zookeeper</artifactId>
				<version>${zookeeper.version}</version>
			</dependency>
			<dependency>
				<groupId>com.github.sgroschupf</groupId>
				<artifactId>zkclient</artifactId>
				<version>${zkclient.version}</version>
			</dependency>
		
	2������03taotao-manager
		taotao-manager�ۺϹ�����ɾ��taotao-manager-web���˹������ڶ����ķ��񣬲��ٱ�taotao-manager�ۺϡ�
		��ͼ3
		
	3������07taotao-manager-web
		�ڴ������ҵ���Ŀ·������03taotao-manager�����µ�07taotao-manager-web���е���03taotao-managerͬ��
		
		�ص�myEclipse��F5ˢ�£���ʱ����07taotao-manager-web��ʧ
		
		�Ҽ�����mavne��Ŀ���룬����

	4������service��Ľӿڹ���08taotao-manager-Interface
		��ȡ�ӿ����ã�
			�ѽӿ�ע��Zookeeperע������
			Taotao-manager-webͨ���ӿ�����service����

		����һ��taotao-manager�ۺ��ӹ���taotao-manager-interface
		��ͼ4
		
		��06taotao-manager-service�����еĽӿ�ItemService.java���е�08taotao-manager-Interface������(�ȴ�����)
		
		ʵ���๤��:06taotao-manager-service��Ҫʵ�ֽӿ�,���������ӿڹ���08taotao-manager-interface.
			<!-- �����ӿ� -->
			<dependency>
				<artifactId>03taotao-manager</artifactId>
				<groupId>com.taotao</groupId>
				<version>0.0.1-SNAPSHOT</version>
			</dependency>

		���ڽӿ���Ҫpojo������Ҫ����pojo����
			<dependency>
				<artifactId>03taotao-manager</artifactId>
				<groupId>com.taotao</groupId>
				<version>0.0.1-SNAPSHOT</version>
			</dependency>

	5������tomcat���
		��06taotao-manager-service����������tomcat�����
			<build>
				<plugins>
					<!-- ����Tomcat��� -->
					<plugin>
						<groupId>org.apache.tomcat.maven</groupId>
						<artifactId>tomcat7-maven-plugin</artifactId>
						<version>2.2</version>
						<configuration>
							<!-- ������Ŀ����·�����൱��ֱ�ӷŵ�tomcat��ROOTĿ¼���ڷ���ʱ����Ҫ��Ŀ���ƣ�ֱ�ӷ���·������ -->
							<path>/</path>
							<!-- ����tomcat�˿� -->
							<port>8082</port>
						</configuration>
					</plugin>
				</plugins>
			</build>

	6��ע�����
		Taotao-manager-service��URL����Zookeeperע�����ģ�ʹ��dubbo����
		
		����06taotao-manager-service������application-service.xml	
			���dubboԼ����
				xmlns:dubbo="http://code.alibabatech.com/schema/dubbo" 
				
				http://code.alibabatech.com/schema/dubbo 
				http://code.alibabatech.com/schema/dubbo/dubbo.xsd

			<!-- �������񣺰ѽӿ�service����Zookeeperע������ -->
			<!-- �ṩ��Ӧ����Ϣ�����ڼ���������ϵ -->
			<dubbo:application name="canglang-manager-service"/>
			<!-- ʹ��multicast�㲥ע�����ı�¶�����ַ -->
			<!-- <dubbo:registry address="multicast://224.5.6.7:1234"/> -->
			<!-- ʹ��dubboͨ��ZookeeperЭ��ע����� -->
			<dubbo:registry protocol="zookeeper" address="192.168.203.66:2181"/>
			<!-- ��dubboЭ����20880�˿ڱ�¶���� -->
			<dubbo:protocol name="dubbo" port="20880" />
			<!-- ������Ҫ��¶�ķ���ӿ� -->
			<!-- ������Ҫ��������-->
			<bean id="itemServiceImpl" class="study.project.service.impl.ItemServiceImpl"></bean>
			<!-- �������� -->
			<dubbo:service interface="study.project.ItemService" ref="itemServiceImpl" />

	7������07taotao-manager-web
		������
			taotao-manager-web��taotao-manager-service����Ժ�û���κι�ϵ����������service��jar����Ҳ���ǲ��ܼ������spring��jar��
			
		����taotao-manager-web����springmvc�����ļ�����Ҫspring��jar����Ҫ�Լ�����spring���ꡣ

		����spring������
			<!-- Spring -->
			<dependency>
				<groupId>org.springframework</groupId>
				<artifactId>spring-context</artifactId>
				<version>${spring.version}</version>
			</dependency>
			<dependency>
				<groupId>org.springframework</groupId>
				<artifactId>spring-beans</artifactId>
				<version>${spring.version}</version>
			</dependency>
			<dependency>
				<groupId>org.springframework</groupId>
				<artifactId>spring-webmvc</artifactId>
				<version>${spring.version}</version>
			</dependency>
			<dependency>
				<groupId>org.springframework</groupId>
				<artifactId>spring-jdbc</artifactId>
				<version>${spring.version}</version>
			</dependency>
			<dependency>
				<groupId>org.springframework</groupId>
				<artifactId>spring-aspects</artifactId>
				<version>${spring.version}</version>
			</dependency>
			<dependency>
				<groupId>org.springframework</groupId>
				<artifactId>spring-jms</artifactId>
				<version>${spring.version}</version>
			</dependency>
			<dependency>
				<groupId>org.springframework</groupId>
				<artifactId>spring-context-support</artifactId>
				<version>${spring.version}</version>
			</dependency>
		
		���ֲ���Ҫ������dubbo��������Ҫ����dubbo����
			<!-- dubbo��� -->
			<dependency>
				<groupId>com.alibaba</groupId>
				<artifactId>dubbo</artifactId>
				<version>${dubbo.version}</version>
			</dependency>
			<dependency>
				<groupId>org.apache.zookeeper</groupId>
				<artifactId>zookeeper</artifactId>
				<version>${zookeeper.version}</version>
			</dependency>
			<dependency>
				<groupId>com.github.sgroschupf</groupId>
				<artifactId>zkclient</artifactId>
				<version>${zkclient.version}</version>
			</dependency>
			
		Taotao-manager-service�Ѿ������war�������ܱ�������
			�޸ĳ�����taotao-manager-interface:
				��Ҫʹ�ýӿ�����dubbo����
				�������pojo
				
			ɾ��06taotao-manager-service������
				<!-- ����service���������spring��jar -->
				<dependency>
					<groupId>com.taotao</groupId>
					<artifactId>06taotao-manager-service</artifactId>
					<version>0.0.1-SNAPSHOT</version>
				</dependency>

			���08taotao-manager-interface������
				<!-- �����ӿ� -->
				<dependency>
					<groupId>com.taotao</groupId>
					<artifactId>08taotao-manager-interface</artifactId>
					<version>0.0.1-SNAPSHOT</version>
				</dependency>

		web.xml
			ɾ������spring�����ļ�
			
			<!-- dubbo��� -->
			<dependency>
				<groupId>com.alibaba</groupId>
				<artifactId>dubbo</artifactId>
				<version>${dubbo.version}</version>
			</dependency>
			<dependency>
				<groupId>org.apache.zookeeper</groupId>
				<artifactId>zookeeper</artifactId>
				<version>${zookeeper.version}</version>
			</dependency>
			<dependency>
				<groupId>com.github.sgroschupf</groupId>
				<artifactId>zkclient</artifactId>
				<version>${zkclient.version}</version>
			</dependency>

		spring.xml
			
			�������Zookeeperע������ע����񣬱��ֲ��Ƿ���������ߣ����ֲ���Ҫ����service�������ҪȥZookeeperע�����Ļ�ȡservice������Ҫ����Zookeeperע�����

			����Լ��
				xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
			
				http://code.alibabatech.com/schema/dubbo 
				http://code.alibabatech.com/schema/dubbo/dubbo.xsd

				
			�������
				 <!-- ���÷��� -->
				<dubbo:application name="taotao-manager-web"/>
				<!-- <dubbo:registry address="multicast://224.5.6.7:1234" /> -->
				<!-- ʹ��dubbo��Zookeeperע�����Ļ�ȡ���� -->
				<dubbo:registry protocol="zookeeper" address="192.168.203.66:2181"/>
				<!-- ���÷��� -->
				<dubbo:reference interface="study.project.ItemService" id="itemService" />

	8��ʵ�����л�
		ע�⣺
			���ֲ�ͷ������н�����Զ�̵��ã�javaBean�ǿ����������ݴ��ݣ�������ʵ�����л��ӿ�
			
			implements Serializable
			
	9�����빤��Դ��
		��ͼ5

	10������
		����
			����Id��ѯ��Ʒ������json��ʽ����
			
		ǰ�᣺
			��Ҫ�������������zookeeper����
			
		ItemController.java	
			@Controller
			public class ItemController {
				@Resource
				private ItemService itemService;
				
				/**
				 * ������Ʒid��ѯ��Ʒ
				 * ���ղ�������һ��
				 * 		@RequestMapping("/findItemByItemId/{itemId}")
				 *		public TbItem findItemByItemId(@PathVariable Long itemId){}
				 *		url:
				 *			localhost:8081/findItemByItemId/536563
				 *���ղ�����������
				 *		@RequestMapping("/findItemByItemId")
				 *		public TbItem findItemByItemId(@RequestParam Long itemId){}
				 *		url:
				 *			localhost:8081/findItemByItemId?itemId=536563
				 * @param itemId
				 * @return
				 */
				@ResponseBody
				@RequestMapping("/findItemByItemId/{itemId}")
				public TbItem findItemByItemId(@PathVariable Long itemId){
					
					TbItem tbItem = itemService.findItemByID(itemId);
					
					
					return tbItem;
				}
			}

		ItemService.java
			/**
			 * service��ӿ�
			 * @author yeying
			 */
			public interface ItemService {
				
				/**
				 * ����itemId��ѯItem��Ϣ
				 * @param itemId
				 * @return
				 */
				public TbItem findItemByID(Long itemId);
			}
		ItemServiceImpl.java
			@Service
			public class ItemServiceImpl implements ItemService {
				@Resource
				private TbItemMapper itemMapper;
				/**
				 * ����itemId��ѯItem��Ϣ
				 */
				public TbItem findItemByID(Long itemId) {
					//����TbItemExample����
					TbItemExample example = new TbItemExample();
					//��ȡCriteria����
					Criteria criteria = example.createCriteria();
					//����
					criteria.andIdEqualTo(itemId);
					//ִ�в�ѯ
					List<TbItem> itemList = itemMapper.selectByExample(example);
					
					if (itemList != null && itemList.size() > 0) {
						return itemList.get(0);
					}
					return null;
				}
			}
		
		���ʣ�
			localhost:8081/findItemByItemId/536563
			
	11��ע������
		����˵����
			(taotao-manager-service)�����ṩ������ʱ
				��/dubbo/com.foo.BarService/providersĿ¼��д���Լ���URL��ַ��
			(taotao-manager-web)��������������ʱ
				����/dubbo/com.foo.BarService/providersĿ¼�µ��ṩ��URL��ַ��
				����/dubbo/com.foo.BarService/consumersĿ¼��д���Լ���URL��ַ��
			�����������ʱ
				����/dubbo/com.foo.BarServiceĿ¼�µ������ṩ�ߺ�������URL��ַ��

		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

