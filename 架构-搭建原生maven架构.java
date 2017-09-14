
一、项目框架搭建
	0、系统架构关系
		见图0
	
	1、创建祖父项目(01taotao_parent)
		见图1

	2、创建工具类项目(02taotao-common)
		见图2

	3、创建后台父工程(03taotao-manager)
		见图3
		3.1、后台父工程的pojo工程(04taotao-manager-pojo)
			见图4
		3.2、后台父工程的dao(05taotao-manager-dao)
			见图5
		3.3、后台父工程的service(06taotao-manager-service)
			见图6
		3.4、后台父工程的表现层(07taotao-manager)
			见图7

	拷贝依赖坐标直接从工程中复制
	
	4、配置maven的内置tomcat插件
		在集合工程taotao-manager的pom.xml中配置maven的内置tomcat插件
		<project>
			<dependencies></dependencies>
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
							<port>8081</port>
						</configuration>
					</plugin>
				</plugins>
			</build>
		</project>

	5、在toatoa-manager-web工程中导入web.xml
	
二、SSH框架整合
	
	1、spring的配置文件
		1.1、springmvc.xml
			<?xml version="1.0" encoding="UTF-8"?>
			<beans xmlns="http://www.springframework.org/schema/beans"
				   xmlns:context="http://www.springframework.org/schema/context"
				   xmlns:mvc="http://www.springframework.org/schema/mvc"
				   xmlns:aop="http://www.springframework.org/schema/aop"
				   xmlns:tx="http://www.springframework.org/schema/tx"
				   xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
				   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				   xsi:schemaLocation="http://www.springframework.org/schema/beans
										http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
										http://www.springframework.org/schema/context 
										http://www.springframework.org/schema/context/spring-context-4.0.xsd
										http://www.springframework.org/schema/aop 
										http://www.springframework.org/schema/aop/spring-aop-4.0.xsd
										http://www.springframework.org/schema/tx 
										http://www.springframework.org/schema/tx/spring-tx-4.0.xsd
										http://www.springframework.org/schema/mvc
										http://www.springframework.org/schema/mvc/spring-mvc-4.0.xsd
										http://www.springframework.org/schema/util
										http://www.springframework.org/schema/util/spring-util-4.0.xsd
										http://code.alibabatech.com/schema/dubbo 
										http://code.alibabatech.com/schema/dubbo/dubbo.xsd">

				<!-- 扫描controller -->
				<context:component-scan base-package="study.project"></context:component-scan>

				<!-- 驱动注解 -->
				<mvc:annotation-driven></mvc:annotation-driven>
				
				<!-- 视图解析器 -->
				<bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
					<property name="prefix" value="/WEB-INF/jsp/"></property>
					<property name="suffix" value=".jsp"></property>
				</bean>
			</beans>

			
		1.2、applicationContext-service.xml
			<?xml version="1.0" encoding="UTF-8"?>
			<beans xmlns="http://www.springframework.org/schema/beans"
				xmlns:context="http://www.springframework.org/schema/context" 
				xmlns:mvc="http://www.springframework.org/schema/mvc"
				xmlns:aop="http://www.springframework.org/schema/aop" 
				xmlns:tx="http://www.springframework.org/schema/tx"
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				xsi:schemaLocation="http://www.springframework.org/schema/beans 
									http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
									http://www.springframework.org/schema/context 
									http://www.springframework.org/schema/context/spring-context-4.0.xsd
									http://www.springframework.org/schema/aop 
									http://www.springframework.org/schema/aop/spring-aop-4.0.xsd
									http://www.springframework.org/schema/tx 
									http://www.springframework.org/schema/tx/spring-tx-4.0.xsd
									http://www.springframework.org/schema/mvc
									http://www.springframework.org/schema/mvc/spring-mvc-4.0.xsd
									http://www.springframework.org/schema/util
									http://www.springframework.org/schema/util/spring-util-4.0.xsd">	
				
				<!-- 创建管理事务对象：DataSourceTransactionManager -->
				<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
					<property name="dataSource" ref="dataSource"></property>
				</bean>
				
				<!-- 事务通知 -->
				<tx:advice id="txAdvice" transaction-manager="transactionManager">
					<tx:attributes>
						<!-- 传播行为 -->
						<tx:method name="save*" propagation="REQUIRED" />
						<tx:method name="insert*" propagation="REQUIRED" />
						<tx:method name="add*" propagation="REQUIRED" />
						<tx:method name="create*" propagation="REQUIRED" />
						<tx:method name="delete*" propagation="REQUIRED" />
						<tx:method name="update*" propagation="REQUIRED" />
						<tx:method name="find*" propagation="SUPPORTS" read-only="true" />
						<tx:method name="select*" propagation="SUPPORTS" read-only="true" />
						<tx:method name="get*" propagation="SUPPORTS" read-only="true" />		
					</tx:attributes>
				</tx:advice>
				<!-- 切面 -->
				<aop:config>
					<aop:advisor advice-ref="txAdvice" pointcut="execution(* study.project.service.*.*(..))"/>
				</aop:config>
			</beans>

		1.3、applicationContext-dao.xml
			<?xml version="1.0" encoding="UTF-8"?>
			<beans xmlns="http://www.springframework.org/schema/beans"
					xmlns:context="http://www.springframework.org/schema/context" 
					xmlns:mvc="http://www.springframework.org/schema/mvc"
					xmlns:aop="http://www.springframework.org/schema/aop" 
					xmlns:tx="http://www.springframework.org/schema/tx"
					xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
					xsi:schemaLocation="http://www.springframework.org/schema/beans 
										http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
										http://www.springframework.org/schema/context 
										http://www.springframework.org/schema/context/spring-context-4.0.xsd
										http://www.springframework.org/schema/aop 
										http://www.springframework.org/schema/aop/spring-aop-4.0.xsd
										http://www.springframework.org/schema/tx 
										http://www.springframework.org/schema/tx/spring-tx-4.0.xsd
										http://www.springframework.org/schema/mvc
										http://www.springframework.org/schema/mvc/spring-mvc-4.0.xsd
										http://www.springframework.org/schema/util
										http://www.springframework.org/schema/util/spring-util-4.0.xsd">
				<!-- 扫描资源配置文件 -->
				<context:property-placeholder location="classpath:*.properties" file-encoding="UTF-8"/>
				
				<!-- 数据源 -->
				<bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
					<property name="driverClassName" value="${jdbc.driver}"></property>
					<property name="url" value="${jdbc.url}"></property>
					<property name="username" value="${jdbc.username}"></property>
					<property name="password" value="${jdbc.password}"></property>
				</bean>
				
				<!-- sqlSessioinFactory工厂：生产sqlSession -->
				<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
					<property name="dataSource" ref="dataSource"></property>
					<!-- 定义别名 -->
					<property name="typeAliasesPackage" value="study.project.domain"></property>
					<!-- 加载sqlMapConfig配置文件 -->
					<property name="configLocation" value="classpath:sqlMapConfig.xml"></property>
				</bean>
				<!-- 接口代理开发，扫描接口 -->
				<!-- 
					1.接口名称和映射文件名称相同，且在同一个目录下
					2.映射文件namespace名称必须是接口的全类路径名
					3.映射文件sql语句的字段Id必须是接口方法名
				 -->
				 <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
					<property name="basePackage" value="study.project.mapper"></property>
					<property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"></property>
				 </bean>
			</beans>

		1.4、sqlMapConfig.xml
			<?xml version="1.0" encoding="UTF-8"?>
			<!DOCTYPE configuration
					PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
					"http://mybatis.org/dtd/mybatis-3-config.dtd">
			<configuration>
				<!-- 配置插件使用，先空着 -->
			</configuration>
		
		1.5、JDBC.properties
			#url
			jdbc.url = jdbc\:mysql\://WJ
			#mysql数据源驱动
			jdbc.driver = com.mysql.jdbc.Driver
			#用户名
			jdbc.username = root
			#密码
			jdbc.password =root

		1.6、log4j.properties
			### \u8BBE\u7F6E###
			log4j.rootLogger = debug,stdout,D,info,warn,E,fatal
			### ConsoleAppender
			log4j.appender.stdout = org.apache.log4j.ConsoleAppender
			log4j.appender.stdout.Target = System.out
			log4j.appender.stdout.layout = org.apache.log4j.PatternLayout
			log4j.appender.stdout.layout.ConversionPattern = [%-5p] %d{yyyy-MM-dd HH:mm:ss,SSS} method:%l%n%m%n
			### DEBUG
			log4j.appender.D = org.apache.log4j.DailyRollingFileAppender
			log4j.appender.D.File = logs/debug.log
			log4j.appender.D.Append = true
			log4j.appender.D.Threshold = DEBUG
			log4j.appender.D.layout = org.apache.log4j.PatternLayout
			log4j.appender.D.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n
			#INFO
			log4j.appender.info = org.apache.log4j.DailyRollingFileAppender
			log4j.appender.info.File =logs/info.log
			log4j.appender.info.Append = true
			log4j.appender.info.Threshold = INFO
			log4j.appender.info.layout = org.apache.log4j.PatternLayout
			log4j.appender.info.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n
			#warn
			log4j.appender.warn = org.apache.log4j.DailyRollingFileAppender
			log4j.appender.warn.File =logs/warn.log
			log4j.appender.warn.Append = true
			log4j.appender.warn.Threshold = WARN
			log4j.appender.warn.layout = org.apache.log4j.PatternLayout
			log4j.appender.warn.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n
			### ERROR
			log4j.appender.E = org.apache.log4j.DailyRollingFileAppender
			log4j.appender.E.File =logs/error.log
			log4j.appender.E.Append = true
			log4j.appender.E.Threshold = ERROR
			log4j.appender.E.layout = org.apache.log4j.PatternLayout
			log4j.appender.E.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n
			### fatal
			log4j.appender.fatal = org.apache.log4j.DailyRollingFileAppender
			log4j.appender.fatal.File =logs/fatal.log
			log4j.appender.fatal.Append = true
			log4j.appender.fatal.Threshold = fatal
			log4j.appender.fatal.layout = org.apache.log4j.PatternLayout
			log4j.appender.fatal.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n
	
	2、taotao-manager-web工程的web.xml配置
		<?xml version="1.0" encoding="UTF-8"?>
		<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				xmlns="http://java.sun.com/xml/ns/javaee" 
				xmlns:web="http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
				xsi:schemaLocation="http://java.sun.com/xml/ns/javaee 
									http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd" id="taotao" version="2.5">

			<!-- 编码过滤 -->
			<filter>
				<filter-name>characterEncoding</filter-name>
				<filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
				<init-param>
					<param-name>encoding</param-name>
					<param-value>UTF-8</param-value>
				</init-param>
			</filter>
			<filter-mapping>
				<filter-name>characterEncoding</filter-name>
				<url-pattern>/星</url-pattern>
			</filter-mapping>
			
			<!-- 加载spring配置文件 
				1.如果加载jar包配置文件，需要如下加载方式：classpath*:applicatonContext-*.xml
				2.把配置文件全部放入web项目。
				加载方式：classpath：applicationContext-*.xml(使用MAVEN内置tomcat插件)
			-->
			<listener>
				<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
			</listener>
			<context-param>
				<param-name>contextConfigLocation</param-name>
				<param-value>classpath:applicationContext-*.xml</param-value>
			</context-param>
				
			<!-- 加载springmvc配置文件 -->
			<servlet>
				<servlet-name>springmvc</servlet-name>
				<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
				<init-param>
					<param-name>contextConfigLocation</param-name>
					<param-value>classpath:springmvc.xml</param-value>
				</init-param>
			</servlet>
			
			<servlet-mapping>
				<servlet-name>springmvc</servlet-name>
				<url-pattern>/</url-pattern>
			</servlet-mapping>
		</web-app>
		
	3、整合DAO--导入数据库
		导入数据库数据,详见附件taotao.sql

		将配置文件交给web层管理，maven内置tomcat启动时会加载配置文件
		
		将
			applicationContext-dao.xml
			applicationContext-service.xml
			jdbc.properties
			log4j.properties
			springmvc.xml
			sqlMapConfig.xml
			
		这些文件放在07taotao-manager-web中
			
	4、整合DAO--逆向工程
		4.1、整合插件
			将插件中的features和plugins文件加复制到myeclipse的dropins目录中，或者在此目录中新创建一个文件夹
		4.2、重启myeclipse
			见图8
		4.3、在dao层创建一个generatorConfig-base.xml
			<?xml version="1.0" encoding="UTF-8"?>
			<!DOCTYPE generatorConfiguration
			  PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
			  "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
			<generatorConfiguration>
				<!-- classPathEntry:本地数据库的JDBC(mysql)驱动的jar包地址 -->
				<classPathEntry
					location="D:\studySoftware\software\mysql-connector-java-5.1.7-bin.jar" />
				<context id="caigouTables" targetRuntime="MyBatis3">
					<commentGenerator>
						<!-- 是否去除自动生成的注释 true：是 ： false:否 -->
						<property name="suppressAllComments" value="true" />
					</commentGenerator>
					<!--数据库连接的信息：驱动类、连接地址、用户名、密码 -->
					<jdbcConnection driverClass="com.mysql.jdbc.Driver"
						connectionURL="jdbc:mysql://localhost:3306/mybatis" 
						userId="root"
						password="root">
					</jdbcConnection>
					
					<!--oracle数据库连接的信息：驱动类、连接地址、用户名、密码 -->
					<!-- <jdbcConnection driverClass="oracle.jdbc.OracleDriver"
						connectionURL="jdbc:oracle:thin:@127.0.0.1:1521:yycg" 
						userId="yycg"
						password="yycg">
					</jdbcConnection> -->
					<!-- 默认false，把JDBC DECIMAL 和 NUMERIC 类型解析为 Integer true，把JDBC DECIMAL 和 
						NUMERIC 类型解析为java.math.BigDecimal -->
					<javaTypeResolver>
						<property name="forceBigDecimals" value="false" />
					</javaTypeResolver>
					<!-- 
						targetPackage:生成实体类的包名
						targetProject:生成实体类的位置（工程名）
					-->
					<javaModelGenerator targetPackage="cn.itcast.domain"
						targetProject="springmvc-02">
						<!-- enableSubPackages:是否让schema作为包的后缀 -->
						<property name="enableSubPackages" value="true" />
						<!-- 从数据库返回的值被清理前后的空格 -->
						<property name="trimStrings" value="true" />
					</javaModelGenerator>
					<!-- 
						targetPackage：生成dao接口的包名
						targetProject:自动mapper(dao)的位置(工程名) 
					-->
					<sqlMapGenerator targetPackage="cn.itcast.mapper" 
						targetProject="springmvc-02">
						<property name="enableSubPackages" value="false" />
					</sqlMapGenerator>
					<!-- 
						targetPackage：生成dao接口映射配置文件的的包名（注意：要和dao接口在同一个包下）
						targetProject:自动mapper(dao)的位置(工程名) 
						implementationPackage：dao接口的包名
					-->
					<javaClientGenerator type="XMLMAPPER"
						targetPackage="cn.itcast.mapper" implementationPackage="cn.itcast.mapper"
						targetProject="springmvc-02">
						<property name="enableSubPackages" value="false" />
					</javaClientGenerator>
					<!-- 对应的要生成的数据库中的表名 -->	
					<table schema="" tableName="items"></table>
					<!-- <table schema="" tableName="userjd" />
					<table schema="" tableName="usergys" />
					<table schema="" tableName="dictinfo" />
					<table schema="" tableName="dicttype" />
					<table schema="" tableName="basicinfo" /> -->
				</context>
			</generatorConfiguration>	
	
	5、加载dao的配置文件
		Dao层接口和xml配置文件在同一个目录src/main/java,由于此目录下只加载java代码。不加载配置文件。
		
		修改dao层pom文件，加载xml文件
			<dependencies></dependencies>
			<build>
				<finalName>bizcloud-tcb2b</finalName>
				<resources>
					<resource>
						<directory>src/main/java</directory>
						<includes>
							<include>**/*.properties</include>
							<include>**/*.xml</include>
						</includes>
						<filtering>false</filtering>
					</resource>
					<resource>
						<directory>src/main/resources</directory>
						<includes>
							<include>**/*.properties</include>
							<include>**/*.xml</include>
						</includes>
						<filtering>false</filtering>
					</resource>
				</resources>
			</build>

	6、测试
		需求：根据Id查询商品，返回json格式数据
		
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
				@RequestMapping("/findItemByItemId")
				public TbItem findItemByItemId(@RequestParam Long itemId){
					
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
		
		








































































































































































