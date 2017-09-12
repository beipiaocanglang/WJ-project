
虚拟机
	CentOS 
		用户名：root/canglang
		密码：canglang
		
	分布式文件系统：
		用户名：root/canglang
		密码：canglang


linux系统上安装的软件
	安装方式：
		第一种安装方式：rpm(麻烦不用)
			rpm的常用参数
				i：安装应用程序（install）
				e：卸载应用程序（erase）
				vh：显示安装进度；（verbose   hash） 
				U：升级软件包；（update） 
				qa: 显示所有已安装软件包（query all）
			结合grep命令使用
			例子：
				rmp  -ivh  gcc-c++-4.4.7-3.el6.x86_64.rpm

		第二种安装方式：yum
			Yum命令安装软件，直接从互联网进行下载，自动安装。
			Yum从本地磁盘进行安装：了解yum命令安装原理。

	1、yum

		第一步：关联本地磁盘到linux系统	
			
		第二步：yum命令安装原理
			联网的情况下：yum -y install mysql-server
			当执行yum命令时，从/etc/yum.repos.d/目录下repo文件中获取软件下载网址，然后yum才能从互联网下载安装软件。

		第三步：备份本地repo文件
			去cd /etc/yum.repos.d/目录
			执行 rename .repo .bak *  把所有以扩展名为repo的修改为扩展名为bak

		第四步：挂载磁盘文件到临时目录
			命令：
				mount /dev/cdrom /mnt
				拷贝磁盘镜像文件到临时目录

		第五步：创建repo文件
			去cd /etc/yum.repos.d
			执行 touch mine.repo
			执行 vim mine.repo
			按   i
			输入：
				[centos]
				name=centos
				baseurl=file:///mnt
				gpgcheck=0
				enabled=1
			保存

		第六步：清空yum源
			在/etc/yum.repos.d目录下
			执行 yum clean all
			
		第七步：加载yum源
			在/etc/yum.repos.d目录下
			执行 yum repolist all

	2、gcc
		注意：
			c语言开发的需要使用gcc进行编译程序，挂载好镜像后如果服务重启则需要重新挂载镜像
		前提：
			需要配置yum的配置
		步骤：
			执行 cd  ~
			
			挂载：mount /dev/cdrom   /mnt
			
			查看是否挂载成功    cd /mnt/
			
			在/mnt/目录下执行安装gcc命令：yum -y install gcc

	3、lrzsz
		执行完上面的命令后可直接安装(在/mnt/目录下)
		执行 yum –y install lrzsz

		作用：导入jar包
			直接在指定目录中使用rz命令即可（会弹出一个框，找到本地磁盘上的安装包）

	4、jdk
		第一步：导入jdk安装包
			方式一：使用工具导入
				见fileZilla的使用
			
			方式二：Alt+p
				直接将tomcat压缩包拖到新打开的窗口中就行
				注意：
					默认的是root目录
					tomcat压缩包不能有中文目录
			
		第二步：解压
			去 cd /usr/local/hadoop/jar/
			执行 tar -zxvf jdk-7u75-linux-x64.tar.gz

		第三步：配置环境变量
			Linux系统配置文件都在etc目录下面，
			
			配置jdk环境变量文件是etc目录下profile文件。
			
			编辑profile文件：vim /etc/profile
			
			在配置文件的最后：
				export JAVA-HOME=/usr/local/hadoop/jar/-zxvf jdk-7u75-linux-x64.tar.gz
				export PATH=$JAVA-HOME/bin:$PATH
		第四步：刷新配置文件
			命令：source /etc/profile
			

		第五步：测试
			直接执行 java -version
		
	5、tomcat
		第一步：使用方式二：Alt+p
			直接将tomcat压缩包拖到新打开的窗口中就行
			注意：
				默认的是root目录
				tomcat压缩包不能有中文目录

		第二步：解压
			在 cd /usr/local/hadoop/jar/
			执行 tar -zxvf apache-tomcat-7.0.61.tar.gz

		第三步：启动tomcat
			执行 ./apache-tomcat-7.0.61/bin/startup.sh

		第四步：在外部访问
			http://192.168.254.66:8080/ 

	6、zookeeper/dubbo/moniter
		详见：项目/30taotao/项目中用到的新知识点/dubbo

	7、nginx
		详见：项目/30taotao/项目中用到的新知识点/nginx

	8、fastDFS
		详见：项目/30taotao/项目中用到的新知识点/fastDFS

	9、redis
		详见：web/28Redis/redis详细总结

	10、activemq
		详见：项目中用到的新知识点/ActiveMQ/ActiveMQ详细总结

	11、sorl
		详见：JAVAEE个人总结/web/27solr

	12、安装mysql
		第一步：安装mysql
			命令： yum -y install mysql-server

		第二步：启动mysql
			命令：service mysqld start
			退出ysql服务 quit
		第三步：登录
			命令：mysql –uroot –p

		第四步：测试
			执行 show databases:
    

linux需要启动的服务：

	项目运行依赖：1、2、4、5、6、7

	1、zookeeper
		单机版
			cd /usr/local/hadoop/canglang/zk-dubbo/zookeeper/bin

			启动：./zkServer.sh start

			查看状态：./zkServer.sh status
		
			端口：2181

		集群版
			同solr集群版中的zk集群搭建和启动一样
		
			等待搭建

			端口：2185、2186、2187

	2、dubbo
		cd /usr/local/hadoop/canglang/zk-dubbo/tomcat-dubbo/bin

		启动：./startup.sh

		查看日志：tail -f ../logs/catalina.out
		
		端口：8080

		访问：http://192.168.254.66:8080/dubbo(用户名和密码都是root)

	3、nginx
		cd /usr/local/hadoop/canglang/nginx/nginx-server/sbin

		启动：./nginx

	4、打开fastdfs系统
		已经在开机启动里面配置了，但是如果不能访问 要查看服务是否都启动

		/usr/bin/fdfs_trackerd /etc/fdfs/tracker.conf restart
		/usr/bin/fdfs_storaged /etc/fdfs/storage.conf restart
		/etc/init.d/nginx start

	5、redis
		单机版：
			cd /usr/local/hadoop/canglang/redis/redis-install/6379/bin
			启动：./redis-server redis.conf
			登录：./redis-cli

			端口：6379(默认端口)

		集群：
			cd /usr/local/hadoop/canglang/redis/redis-jiqun-install
			启动：./redis-start-all.sh
			登录：
				cd 7001/bin
				./redis-cli -c -h 192.168.254.66 -p 7001

			端口：7001、7002、7003、7004、7005、7006、7007、7008

	6、solr
		注意：
			单机版和集群版只需启动其中一个就行，因为集群的消耗性能所以在测试阶段使用单机版的就行
			
		单机版：
			可能和dubbo服务的端口冲突，所以需要修改端口
			
			执行：sh /usr/local/hadoop/canglang/solr/tomcat-solr/bin/startup.sh
			
			监控日志：tail -f tomcat-solr/logs/catalina.out
			
			访问：http://192.168.254.66:8081/solr
			
			端口：8081

		集群版：
			进入：cd /usr/local/hadoop/canglang/solr/solrcluster/
			
			执行；./start-solrCluster-all.sh
			
			查看进程：ps -ef | grep solr (此时会看到7个关于sole的进程(3个zk、4个solr))

			tomcat：端口 8082、8083、8084、8085

			zookeeper端口：2182、2183、2184
		
	7、ActiveMQ
		进入：cd /usr/local/hadoop/canglang/activeMQ/bin

		执行：./activemq start

		访问：http://192.168.6.66:8161/admin/(密码和用户名都是admin)
			
		端口：8161

		通讯端口：tcp://192.168.254.66:61616
			

项目服务：

	1、03taotao-manager  端口：8082

	2、07taotao-manager-web  端口：8081

	3、10taotao-content  端口：8084

	4、09taotao-portal-web  端口：8083

	5、13taotao-search-web  端口：8085

	6、16taotao-search  端口：8086

	7、17taotao-item-web  端口：8087

    8、18taotao-user  端口：8088

    9、21taotao-user-sso-web  端口：8089

    10、22taotao-order   端口：8090

    11、25taotao-order-web   端口：8091

	后台管理系统：1、2、3、6

	前台门户系统：3、4

	前台搜索系统：3、4、5、6

	前台商品详情系统：1、7

    登录系统：3、4、8、9

    订单系统：1、3、4、5、6、7、8、9、10、11


模块划分
	架构
		1、架构-搭建原生maven架构

		2、架构-搭建SOA面向服务架构

	后台管理系统
		3、后台-测试分页插件

		4、后台-后台查询商品-分页查询商品(1)

		5、后台-后台添加商品-添加类目(2)

		* 6、后台管理系统-nginx负载均衡

		* 7、后台管理系统-fastDFS分布式文件系统

		8、后台-后台添加商品-图片上传(3)

		9、后台-后台添加商品-保存商品(4)

	前台门户系统-后台管理系统
		10、前台-搭建前台门户系统

		11、前台门户系统-后台内容分类管理-加载树形分类(5)

		12、前台门户系统-后台内容分类管理-创建节点(6)

		13、前台门户系统-后台内容管理-根据子节点id查询分类内容(7)

		14、前台门户系统-后台内容管理-新增分类内容(8)

	前台门户系统
		15、前台门户系统-查询轮播图(9)

		* 16、前台门户系统-首页添加redis缓存(10)

	前台系统
		17、前台系统-搭建搜索系统

		* 18、前台系统-搭建solr集群
		
		19、前台系统-导入数据库数据到索引库(11)
		
		20、前台系统-实现搜索功能(12)
		
		* 21、前台系统-solrCloud简介及搭建
		
		* 22、前台系统-ActiveMQ简介、安装
		
		23、前台系统-同步索引库(13)
		
		* 24、前台系统-搭建商品详情服务
		
		25、前台系统-展示商品详情(14)

        26、前台系统-商品详情也添加缓存(15)

        * 27、前台系统-Freemarker简介及使用

        28、前台系统-页面实现静态化(16)

        29、前台系统-单点登录(sso)

        * 30、前台系统-搭建登录系统

        31、前台系统-单点登录前检查数据是否可用(17)

        32、前台系统-用户注册(18)

        33、前台系统-用户登录(19)

        34、前台系统-根据cookie中token跨服务器查询redis中的用户登录信息(20)

        * 35、前台系统-搭建订单和购物车系统

        36、前台系统-添加购物车(未登录)(21)

        37、前台系统-查询购物车列表(未登录)(22)

        38、前台系统-购物车数量加减(未登录)(23)

        39、前台系统-购物车删除商品(未登录)(24)

        40、前台系统-购物车结算(未登录)(25)

        41、前台系统-提交订单(未登录)(26)

		
		


要学的：
    项目
    
    私服

    solr

    mybatis

    p2p

    oracle
		
		
		
		
		
		
		
		
		
		
