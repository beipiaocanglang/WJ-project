改造淘淘商城为面向服务架构SOA工程。
一、改造思路：
	Taotao-manager-web提取出来，不再被taotao-manager聚合
	
	Taotao-manager-service改造war
	
	改造以后架构：
	
	见图1

二、服务改造
	1、改造06taotao-manager-service
		1.1、对项目进行面向服务架构拆分：
			项目业务代码单独变成一个服务，提供给表现层来调用，因此需要将业务层拆分成一个服务(war).
			见图2

		1.2、复制07taotao-manager-web工程中webapp文件夹到06taotao-manager-service项目中
		
		1.3、复制spring相关配置文件
			applicationContext-dao.xml
			applicationContext-service.xml
			jdbc.properties
			log4j.properties
			sqlMapConfig.xml
			
			注意：
				Service此时变成web项目，需要加载配置文件
				
		
		1.4、web.xml加载spring的配置文件	
			<listener>
				<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
			</listener>
			<context-param>
				<param-name>contextConfigLocation</param-name>
				<param-value>classpath:applicationContext-*.xml</param-value>
			</context-param>
			
			注意：
				只需要加载spring配置文件即可
		
		1.5、添加dubbo依赖
			<!-- dubbo相关 -->
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
		
	2、改造03taotao-manager
		taotao-manager聚合工程中删除taotao-manager-web，此工程属于独立的服务，不再被taotao-manager聚合。
		见图3
		
	3、改造07taotao-manager-web
		在磁盘上找到项目路径，将03taotao-manager工程下的07taotao-manager-web剪切到和03taotao-manager同级
		
		回到myEclipse中F5刷新，此时工程07taotao-manager-web消失
		
		右键，以mavne项目导入，即可

	4、创建service层的接口工程08taotao-manager-Interface
		抽取接口作用：
			把接口注册Zookeeper注册中心
			Taotao-manager-web通过接口引入service服务

		创建一个taotao-manager聚合子工程taotao-manager-interface
		见图4
		
		将06taotao-manager-service工程中的接口ItemService.java剪切到08taotao-manager-Interface工程下(先创建包)
		
		实现类工程:06taotao-manager-service需要实现接口,必须依赖接口工程08taotao-manager-interface.
			<!-- 依赖接口 -->
			<dependency>
				<artifactId>03taotao-manager</artifactId>
				<groupId>com.taotao</groupId>
				<version>0.0.1-SNAPSHOT</version>
			</dependency>

		由于接口需要pojo包，需要依赖pojo坐标
			<dependency>
				<artifactId>03taotao-manager</artifactId>
				<groupId>com.taotao</groupId>
				<version>0.0.1-SNAPSHOT</version>
			</dependency>

	5、配置tomcat插件
		在06taotao-manager-service工程中配置tomcat插件：
			<build>
				<plugins>
					<!-- 配置Tomcat插件 -->
					<plugin>
						<groupId>org.apache.tomcat.maven</groupId>
						<artifactId>tomcat7-maven-plugin</artifactId>
						<version>2.2</version>
						<configuration>
							<!-- 定义项目发布路径，相当于直接放到tomcat的ROOT目录，在访问时不需要项目名称，直接访问路径就行 -->
							<path>/</path>
							<!-- 配置tomcat端口 -->
							<port>8082</port>
						</configuration>
					</plugin>
				</plugins>
			</build>

	6、注册服务
		Taotao-manager-service把URL发布Zookeeper注册中心，使用dubbo发布
		
		改造06taotao-manager-service工程中application-service.xml	
			添加dubbo约束：
				xmlns:dubbo="http://code.alibabatech.com/schema/dubbo" 
				
				http://code.alibabatech.com/schema/dubbo 
				http://code.alibabatech.com/schema/dubbo/dubbo.xsd

			<!-- 发布服务：把接口service发布Zookeeper注册中心 -->
			<!-- 提供方应用信息，用于计算依赖关系 -->
			<dubbo:application name="canglang-manager-service"/>
			<!-- 使用multicast广播注册中心暴露服务地址 -->
			<!-- <dubbo:registry address="multicast://224.5.6.7:1234"/> -->
			<!-- 使用dubbo通过Zookeeper协议注册服务 -->
			<dubbo:registry protocol="zookeeper" address="192.168.203.66:2181"/>
			<!-- 用dubbo协议在20880端口暴露服务 -->
			<dubbo:protocol name="dubbo" port="20880" />
			<!-- 声明需要暴露的服务接口 -->
			<!-- 创建需要发布对象-->
			<bean id="itemServiceImpl" class="study.project.service.impl.ItemServiceImpl"></bean>
			<!-- 发布服务 -->
			<dubbo:service interface="study.project.ItemService" ref="itemServiceImpl" />

	7、改造07taotao-manager-web
		分析：
			taotao-manager-web和taotao-manager-service拆分以后，没有任何关系。不能依赖service的jar包，也就是不能间接依赖spring的jar。
			
		由于taotao-manager-web加载springmvc配置文件，需要spring的jar。需要自己导入spring坐标。

		导入spring的依赖
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
		
		表现层需要以利用dubbo服务，所以要引入dubbo依赖
			<!-- dubbo相关 -->
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
			
		Taotao-manager-service已经改造成war包，不能被依赖。
			修改成依赖taotao-manager-interface:
				需要使用接口引入dubbo服务
				间接依赖pojo
				
			删除06taotao-manager-service的依赖
				<!-- 依赖service，间接依赖spring的jar -->
				<dependency>
					<groupId>com.taotao</groupId>
					<artifactId>06taotao-manager-service</artifactId>
					<version>0.0.1-SNAPSHOT</version>
				</dependency>

			添加08taotao-manager-interface的依赖
				<!-- 依赖接口 -->
				<dependency>
					<groupId>com.taotao</groupId>
					<artifactId>08taotao-manager-interface</artifactId>
					<version>0.0.1-SNAPSHOT</version>
				</dependency>

		web.xml
			删除加载spring配置文件
			
			<!-- dubbo相关 -->
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
			
			服务层在Zookeeper注册中心注册服务，表现层是服务的消费者，表现层需要调用service层对象，需要去Zookeeper注册中心获取service对象。需要引入Zookeeper注册服务。

			导入约束
				xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
			
				http://code.alibabatech.com/schema/dubbo 
				http://code.alibabatech.com/schema/dubbo/dubbo.xsd

				
			引入服务
				 <!-- 引用服务 -->
				<dubbo:application name="taotao-manager-web"/>
				<!-- <dubbo:registry address="multicast://224.5.6.7:1234" /> -->
				<!-- 使用dubbo从Zookeeper注册中心获取服务 -->
				<dubbo:registry protocol="zookeeper" address="192.168.203.66:2181"/>
				<!-- 引用服务 -->
				<dubbo:reference interface="study.project.ItemService" id="itemService" />

	8、实现序列化
		注意：
			表现层和服务层进行交互是远程调用，javaBean是跨服务进行数据传递，必须是实现序列化接口
			
			implements Serializable
			
	9、导入工程源码
		见图5

	10、测试
		需求：
			根据Id查询商品，返回json格式数据
			
		前提：
			需要在虚拟机中启动zookeeper服务
			
		ItemController.java	
			@Controller
			public class ItemController {
				@Resource
				private ItemService itemService;
				
				/**
				 * 根据商品id查询商品
				 * 接收参数方法一：
				 * 		@RequestMapping("/findItemByItemId/{itemId}")
				 *		public TbItem findItemByItemId(@PathVariable Long itemId){}
				 *		url:
				 *			localhost:8081/findItemByItemId/536563
				 *接收参数方法二：
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
			 * service层接口
			 * @author yeying
			 */
			public interface ItemService {
				
				/**
				 * 根据itemId查询Item信息
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
				 * 根据itemId查询Item信息
				 */
				public TbItem findItemByID(Long itemId) {
					//创建TbItemExample对象
					TbItemExample example = new TbItemExample();
					//获取Criteria对象
					Criteria criteria = example.createCriteria();
					//传参
					criteria.andIdEqualTo(itemId);
					//执行查询
					List<TbItem> itemList = itemMapper.selectByExample(example);
					
					if (itemList != null && itemList.size() > 0) {
						return itemList.get(0);
					}
					return null;
				}
			}
		
		访问：
			localhost:8081/findItemByItemId/536563
			
	11、注册流程
		流程说明：
			(taotao-manager-service)服务提供者启动时
				向/dubbo/com.foo.BarService/providers目录下写入自己的URL地址。
			(taotao-manager-web)服务消费者启动时
				订阅/dubbo/com.foo.BarService/providers目录下的提供者URL地址。
				并向/dubbo/com.foo.BarService/consumers目录下写入自己的URL地址。
			监控中心启动时
				订阅/dubbo/com.foo.BarService目录下的所有提供者和消费者URL地址。

		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

