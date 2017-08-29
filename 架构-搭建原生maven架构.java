
һ����Ŀ��ܴ
	0��ϵͳ�ܹ���ϵ
		��ͼ0
	
	1�������游��Ŀ(01taotao_parent)
		��ͼ1

	2��������������Ŀ(02taotao-common)
		��ͼ2

	3��������̨������(03taotao-manager)
		��ͼ3
		3.1����̨�����̵�pojo����(04taotao-manager-pojo)
			��ͼ4
		3.2����̨�����̵�dao(05taotao-manager-dao)
			��ͼ5
		3.3����̨�����̵�service(06taotao-manager-service)
			��ͼ6
		3.4����̨�����̵ı��ֲ�(07taotao-manager)
			��ͼ7

	������������ֱ�Ӵӹ����и���
	
	4������maven������tomcat���
		�ڼ��Ϲ���taotao-manager��pom.xml������maven������tomcat���
		<project>
			<dependencies></dependencies>
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
							<port>8081</port>
						</configuration>
					</plugin>
				</plugins>
			</build>
		</project>

	5����toatoa-manager-web�����е���web.xml
	
����SSH�������
	
	1��spring�������ļ�
		1.1��springmvc.xml
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

				<!-- ɨ��controller -->
				<context:component-scan base-package="study.project"></context:component-scan>

				<!-- ����ע�� -->
				<mvc:annotation-driven></mvc:annotation-driven>
				
				<!-- ��ͼ������ -->
				<bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
					<property name="prefix" value="/WEB-INF/jsp/"></property>
					<property name="suffix" value=".jsp"></property>
				</bean>
			</beans>

			
		1.2��applicationContext-service.xml
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
				
				<!-- ���������������DataSourceTransactionManager -->
				<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
					<property name="dataSource" ref="dataSource"></property>
				</bean>
				
				<!-- ����֪ͨ -->
				<tx:advice id="txAdvice" transaction-manager="transactionManager">
					<tx:attributes>
						<!-- ������Ϊ -->
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
				<!-- ���� -->
				<aop:config>
					<aop:advisor advice-ref="txAdvice" pointcut="execution(* study.project.service.*.*(..))"/>
				</aop:config>
			</beans>

		1.3��applicationContext-dao.xml
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
				<!-- ɨ����Դ�����ļ� -->
				<context:property-placeholder location="classpath:*.properties" file-encoding="UTF-8"/>
				
				<!-- ����Դ -->
				<bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
					<property name="driverClassName" value="${jdbc.driver}"></property>
					<property name="url" value="${jdbc.url}"></property>
					<property name="username" value="${jdbc.username}"></property>
					<property name="password" value="${jdbc.password}"></property>
				</bean>
				
				<!-- sqlSessioinFactory����������sqlSession -->
				<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
					<property name="dataSource" ref="dataSource"></property>
					<!-- ������� -->
					<property name="typeAliasesPackage" value="study.project.domain"></property>
					<!-- ����sqlMapConfig�����ļ� -->
					<property name="configLocation" value="classpath:sqlMapConfig.xml"></property>
				</bean>
				<!-- �ӿڴ�������ɨ��ӿ� -->
				<!-- 
					1.�ӿ����ƺ�ӳ���ļ�������ͬ������ͬһ��Ŀ¼��
					2.ӳ���ļ�namespace���Ʊ����ǽӿڵ�ȫ��·����
					3.ӳ���ļ�sql�����ֶ�Id�����ǽӿڷ�����
				 -->
				 <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
					<property name="basePackage" value="study.project.mapper"></property>
					<property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"></property>
				 </bean>
			</beans>

		1.4��sqlMapConfig.xml
			<?xml version="1.0" encoding="UTF-8"?>
			<!DOCTYPE configuration
					PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
					"http://mybatis.org/dtd/mybatis-3-config.dtd">
			<configuration>
				<!-- ���ò��ʹ�ã��ȿ��� -->
			</configuration>
		
		1.5��JDBC.properties
			#url
			jdbc.url = jdbc\:mysql\:///taotao
			#mysql����Դ����
			jdbc.driver = com.mysql.jdbc.Driver
			#�û���
			jdbc.username = root
			#����
			jdbc.password =root

		1.6��log4j.properties
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
	
	2��taotao-manager-web���̵�web.xml����
		<?xml version="1.0" encoding="UTF-8"?>
		<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				xmlns="http://java.sun.com/xml/ns/javaee" 
				xmlns:web="http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
				xsi:schemaLocation="http://java.sun.com/xml/ns/javaee 
									http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd" id="taotao" version="2.5">

			<!-- ������� -->
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
				<url-pattern>/��</url-pattern>
			</filter-mapping>
			
			<!-- ����spring�����ļ� 
				1.�������jar�������ļ�����Ҫ���¼��ط�ʽ��classpath*:applicatonContext-*.xml
				2.�������ļ�ȫ������web��Ŀ��
				���ط�ʽ��classpath��applicationContext-*.xml(ʹ��MAVEN����tomcat���)
			-->
			<listener>
				<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
			</listener>
			<context-param>
				<param-name>contextConfigLocation</param-name>
				<param-value>classpath:applicationContext-*.xml</param-value>
			</context-param>
				
			<!-- ����springmvc�����ļ� -->
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
		
	3������DAO--�������ݿ�
		�������ݿ�����,�������taotao.sql

		�������ļ�����web�����maven����tomcat����ʱ����������ļ�
		
		��
			applicationContext-dao.xml
			applicationContext-service.xml
			jdbc.properties
			log4j.properties
			springmvc.xml
			sqlMapConfig.xml
			
		��Щ�ļ�����07taotao-manager-web��
			
	4������DAO--���򹤳�
		4.1�����ϲ��
			������е�features��plugins�ļ��Ӹ��Ƶ�myeclipse��dropinsĿ¼�У������ڴ�Ŀ¼���´���һ���ļ���
		4.2������myeclipse
			��ͼ8
		4.3����dao�㴴��һ��generatorConfig-base.xml
			<?xml version="1.0" encoding="UTF-8"?>
			<!DOCTYPE generatorConfiguration
			  PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
			  "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
			<generatorConfiguration>
				<!-- classPathEntry:�������ݿ��JDBC(mysql)������jar����ַ -->
				<classPathEntry
					location="D:\studySoftware\software\mysql-connector-java-5.1.7-bin.jar" />
				<context id="caigouTables" targetRuntime="MyBatis3">
					<commentGenerator>
						<!-- �Ƿ�ȥ���Զ����ɵ�ע�� true���� �� false:�� -->
						<property name="suppressAllComments" value="true" />
					</commentGenerator>
					<!--���ݿ����ӵ���Ϣ�������ࡢ���ӵ�ַ���û��������� -->
					<jdbcConnection driverClass="com.mysql.jdbc.Driver"
						connectionURL="jdbc:mysql://localhost:3306/mybatis" 
						userId="root"
						password="root">
					</jdbcConnection>
					
					<!--oracle���ݿ����ӵ���Ϣ�������ࡢ���ӵ�ַ���û��������� -->
					<!-- <jdbcConnection driverClass="oracle.jdbc.OracleDriver"
						connectionURL="jdbc:oracle:thin:@127.0.0.1:1521:yycg" 
						userId="yycg"
						password="yycg">
					</jdbcConnection> -->
					<!-- Ĭ��false����JDBC DECIMAL �� NUMERIC ���ͽ���Ϊ Integer true����JDBC DECIMAL �� 
						NUMERIC ���ͽ���Ϊjava.math.BigDecimal -->
					<javaTypeResolver>
						<property name="forceBigDecimals" value="false" />
					</javaTypeResolver>
					<!-- 
						targetPackage:����ʵ����İ���
						targetProject:����ʵ�����λ�ã���������
					-->
					<javaModelGenerator targetPackage="cn.itcast.domain"
						targetProject="springmvc-02">
						<!-- enableSubPackages:�Ƿ���schema��Ϊ���ĺ�׺ -->
						<property name="enableSubPackages" value="true" />
						<!-- �����ݿⷵ�ص�ֵ������ǰ��Ŀո� -->
						<property name="trimStrings" value="true" />
					</javaModelGenerator>
					<!-- 
						targetPackage������dao�ӿڵİ���
						targetProject:�Զ�mapper(dao)��λ��(������) 
					-->
					<sqlMapGenerator targetPackage="cn.itcast.mapper" 
						targetProject="springmvc-02">
						<property name="enableSubPackages" value="false" />
					</sqlMapGenerator>
					<!-- 
						targetPackage������dao�ӿ�ӳ�������ļ��ĵİ�����ע�⣺Ҫ��dao�ӿ���ͬһ�����£�
						targetProject:�Զ�mapper(dao)��λ��(������) 
						implementationPackage��dao�ӿڵİ���
					-->
					<javaClientGenerator type="XMLMAPPER"
						targetPackage="cn.itcast.mapper" implementationPackage="cn.itcast.mapper"
						targetProject="springmvc-02">
						<property name="enableSubPackages" value="false" />
					</javaClientGenerator>
					<!-- ��Ӧ��Ҫ���ɵ����ݿ��еı��� -->	
					<table schema="" tableName="items"></table>
					<!-- <table schema="" tableName="userjd" />
					<table schema="" tableName="usergys" />
					<table schema="" tableName="dictinfo" />
					<table schema="" tableName="dicttype" />
					<table schema="" tableName="basicinfo" /> -->
				</context>
			</generatorConfiguration>	
	
	5������dao�������ļ�
		Dao��ӿں�xml�����ļ���ͬһ��Ŀ¼src/main/java,���ڴ�Ŀ¼��ֻ����java���롣�����������ļ���
		
		�޸�dao��pom�ļ�������xml�ļ�
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

	6������
		���󣺸���Id��ѯ��Ʒ������json��ʽ����
		
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
				@RequestMapping("/findItemByItemId")
				public TbItem findItemByItemId(@RequestParam Long itemId){
					
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
		
		








































































































































































