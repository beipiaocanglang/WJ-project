继TTSOA面向服务架构之后

注意：
	写完接口和实现类以后一定要查看dubbo服务是否发布，再去写controller

第一、架构
一、架构-搭建原生maven架构

二、架构-搭建SOA面向服务架构


第二、后台管理系统
三、后台-测试分页插件
    1、分页插件介绍
        插件名称：pagehelper

    2、使用pagehelper插件的好处
        为什么要使用pagehelper插件而不使用sql语句的limit

        答：
            pagehelper插件是内存版的，第一查询也会从数据库中查询并且加载到内存中，下一次查询会从内存中查询。提高了查询效率和速度

            使用limit查询每一次都需要读取数据库数据，相当于每一次都去磁盘读取数据，这样会降低查询速度和效率

            因为互联网是追求极致的用户体验

    3、怎么用
        3.1、导入依赖
            所有的业务逻辑的处理都在server层，所以要在service工程的pom文件中导入依赖

            <!-- 分页 -->
            <dependency>
                <groupId>com.github.miemiedev</groupId>
                <artifactId>mybatis-paginator</artifactId>
                <version>${mybatis.paginator.version}</version>
            </dependency>
            <dependency>
                <groupId>com.github.pagehelper</groupId>
                <artifactId>pagehelper</artifactId>
                <version>${pagehelper.version}</version>
            </dependency>

        3.2、配置插件
            在service工程的sqlMapConfig.xml配置文件中配置分页的插件
            sqlMapConfig.xml配置文件是在applicationContext-dao.xml配置文件中加载的

            <plugins>
                <!-- com.github.pagehelper为PageHelper类所在包名 -->
                <plugin interceptor="com.github.pagehelper.PageHelper">
                    <!-- 设置数据库类型 Oracle,Mysql,MariaDB,SQLite,Hsqldb,PostgreSQL六种数据库-->
                    <property name="dialect" value="mysql"/>
                </plugin>
            </plugins>

        3.3、测试
            在src/test/java下创建study.project.pagehelperTest包，再创建MyPagehelper类
            注意：
                如果applicationContext-dao.xml配置文件中的连接数据库的占位符报错，可以直接使用对应的值

            public class MyPagehelper {

                /**
                * 测试pagehelper分页插件
                * @author canglang
                */
                @Test
                public void pagehelperTest(){
                    //加载spring的配置文件,获取itenMapper接口代理对象
                    ApplicationContext ac = new ClassPathXmlApplicationContext("classpath*:applicationContext-dao.xml");
                    //获取TbItenMapper接口代理对象
                    TbItemMapper itemMapper = ac.getBean(TbItemMapper.class);
                    //创建
                    TbItemExample example = new TbItemExample();
                    //查询所有之前设置分页信息：使用Pagehelper插件
                    PageHelper.startPage(0, 10);//参数1：其实位置(从0开始)、参数2：每一页的长度
                    //上面不设置参数查询 所有
                    List<TbItem> itemList = itemMapper.selectByExample(example);
                    //获取分页信息，pagehelper将分页信息封装到PageInfo对象中
                    PageInfo<TbItem> pageInfo = new PageInfo<TbItem>(itemList);

                    System.out.println("分页总记录数："+pageInfo.getTotal());
                    System.out.println("所有分页信息："+pageInfo.getPages());
                }
            }

四、后台-后台查询商品-分页查询商品(1)
    查询的是item表

    功能描述：
        点击查询商品--分页显示商品列表

    1、jsp
        访问index.jsp
        点击"查询商品"（27行）跳转到"item-list"页面
        item-list页面加载时会自动发请求（3行）"/item/list"

    2、pojo
        在common工程下创建study.project包，在当前包下创建EasyUIResult包装类

        public class EasyUIResult implements Serializable{
            private Long total;//总记录数
            private List<?> rows;//商品集合
            //生成set、get方法
        }

    3、Interface
        在Interface工程的study.project包下ItemService类中创建接口方法

        public interface ItemService {
            /**
            * 功能1：使用分页插件查询后台商品列表
            * 参数：--参数不可变，因为是前端框架EasyUI框架需要的参数
            * 		当前页：Integer page
            * 		每页长度：Integer rows
            * 返回值：--json
            * 		{total:3224,rows:[{},{}]}
            * 使用包装对象EasyUIResult封装参数：
            * 		Long total
            * 		List<?> rows
            * 使用@ResponseBody自动转换json格式
            */
            public EasyUIResult findItemListByPage(Integer page, Integer rows);
        }

    4、impl
        在service工程的study.project.service.impl包下ItemServiceImpl类中实现接口中的方法

        public class ItemServiceImpl implements ItemService {

            @Resource
            private TbItemMapper itemMapper;

            /**
            * 功能1：使用分页插件查询后台商品列表
            * 参数：--参数不可变，因为是前端框架EasyUI框架需要的参数
            * 		当前页：Integer page
            * 		每页长度：Integer rows
            * 返回值：--json
            * 		{total:3224,rows:[{},{}]}
            * 使用包装对象EasyUIResult封装参数：
            * 		Long total
            * 		List<?> rows
            * 使用@ResponseBody自动转换json格式
            */
            public EasyUIResult findItemListByPage(Integer page, Integer rows) {
                //创建TbItemExample对象
                TbItemExample example = new TbItemExample();
                //查询前设置分页信息
                PageHelper.startPage(page, rows);
                //查询
                List<TbItem> itemList = itemMapper.selectByExample(example);
                //创建PageInfo对象
                PageInfo<TbItem> pageInfo = new PageInfo<TbItem>(itemList);
                //创建包装对象封装分页信息
                EasyUIResult result = new EasyUIResult();
                result.setTotal(pageInfo.getTotal());
                result.setRows(itemList);

                return result;
            }
        }
    5、controller
        在web工程的study.project.controller包下的ItemController类中调用接口方法

        public class ItemController {

            @Resource
            private ItemService itemService;

            /**
            * 功能1：使用分页插件查询后台商品列表
            * 请求：
            * 		/item/list ---easyUI框架需要的
            * 参数：--参数不可变，因为是前端框架EasyUI框架需要的参数
            * 		当前页：Integer page--当入参为空时使用@RequestParam注解给默认值
            * 		每页长度：Integer rows--当入参为空时使用@RequestParam注解给默认值
            * 返回值：--json
            * 		{total:3224,rows:[{},{}]}
            * 使用包装对象EasyUIResult封装参数：
            * 		Long total
            * 		List<?> rows
            * 使用@ResponseBody自动转换json格式
            */
            @ResponseBody
            @RequestMapping("/item/list")
            public EasyUIResult fingItemListByPage(@RequestParam(defaultValue="1") Integer page, @RequestParam(defaultValue="20")Integer rows){

                //分页查询商品集合
                EasyUIResult easyUIResult = itemService.findItemListByPage(page, rows);
                return easyUIResult;
            }
        }

五、后台-后台添加商品-添加类目(2)
    查询的是tb_item_cat表

    功能描述：
        点击 新增商品--选择类目--弹框（见图）
        见图2

    EasyUI框架所需的返回数据格式：
        [
            {
                "id": 1,
                "text": "Node 1",
                "state": "closed",
                "children": [
                    {
                        "id": 11,
                        "text": "Node 11"
                    },
                    {
                        "id": 12,
                        "text": "Node 12"
                    }
                ]
            },
            {//可以封装成包装类(树形节点包装类)
                "id": 2,
                "text": "Node 2", //节点名称
                "state": "closed" //状态，判断此节点下是否还有子节点
            }
        ]

    数据库树形菜单设计：
        设计一个属性类目菜单三个参数搞定
        id:
            如果id在parentId字段中出现，必然也就是父id，有子节点
        parentId:
            顶级目录是0，如果次节点有子节点，那么parentId对应的id必然会在parentId字段出现
        isParent:
            节点状态为1：说面是父节点，有子节点
            节点状态为0：说面是子节点，没有子节点
        页面只需要获取id当parentId参数传递查询就行，如果此节点是父节点，必然会在parentId字段出现
        见图3

    1、pojo
        /**
        * EasyUI树形菜单节点包装类
        * @author canglang
        */
        public class EasyUITreeNode {
            private Integer id;
            //节点文本来显示
            private String text;
            //节点状态,“open”或“closed”,默认是“open”。当设置为“closed”,节点有子节点,并将负载从远程站点
            private String state;
            //生成set、get方法
        }

    2、Interface
        在interface工程的study.project包下创建ItemCatService接口类

        /**
        * 商品类目接口
        */
        public interface ItemCatService {
            /**
            * 功能：
            * 		根据parentId查询商品类目
            * 参数：
            * 		Long parentId--父id
            * 返回值：
            * 		List<EasyUITreeNode>
            * 业务描述：
            * 		1、根据父节点查询此节点下面的子节点，如果有子节点，必然也是parentId
            * 		2、状态：isParent
            * 			1：表示当前节点是父节点，有子节点
            * 			0：表示当前父节点就是子节点，没有子节点
            */
            public List<EasyUITreeNode> findItemCatByParentId(Long parentId);
        }

    3、impl
        在service工程下的study.project.service.impl包下创建ItemCatServiceImpl实现类

        /**
        * 查询商品类目的实现类
        * @author canglang
        */
        @Service
        public class ItemCatServiceImpl implements ItemCatService {

            @Autowired
            private TbItemCatMapper itemCatMapper;

            /**
            * 功能：
            * 		根据parentId查询商品类目
            * 参数：
            * 		Long parentId--父id
            * 返回值：
            * 		List<EasyUITreeNode>
            * 业务描述：
            * 		1、根据父节点查询此节点下面的子节点，如果有子节点，必然也是parentId
            * 		2、状态：isParent
            * 			1：表示当前节点是父节点，有子节点
            * 			0：表示当前父节点就是子节点，没有子节点
            */
            public List<EasyUITreeNode> findItemCatByParentId(Long parentId) {
                //创建TbItemCatExample对象
                TbItemCatExample example = new TbItemCatExample();
                //创建Criteria对象
                Criteria criteria = example.createCriteria();
                //设置参数parentId
                criteria.andParentIdEqualTo(parentId);
                //执行查询
                List<TbItemCat> itemsCatList = itemCatMapper.selectByExample(example);
                //创建List<EasyUITreeNode>集合对象，封装节点信息
                List<EasyUITreeNode> treeNodeList = new ArrayList<EasyUITreeNode>();

                for (TbItemCat tbItemCat : itemsCatList) {
                    EasyUITreeNode treeNode = new EasyUITreeNode();
                    treeNode.setId(tbItemCat.getId().intValue());
                    treeNode.setText(tbItemCat.getName());
                    //节点状态是否为1：closed、0：open
                    treeNode.setState(tbItemCat.getIsParent()?"closed":"open");
                    //封装到list集合
                    treeNodeList.add(treeNode);
                }

                return treeNodeList;
            }
        }

    4、controller
        在web工程的study.project.controller包下创建ItemCatController接口调用类

        /**
        * 查询商品类目
        * @author canglang
        */
        @Controller
        public class ItemCatController {
            @Autowired
            private ItemCatService itemCatService;

            /**
            * 功能：
            * 		根据parentId查询商品类目
            * 参数：
            * 		Long parentId--父id
            * 		注意：
            * 			第一次加载时没有parentId，需要给默认值
            * 			框架传递的参数是id，我们接收的参数名称是parendID,所以要使用value值
            * 返回值：
            * 		List<EasyUITreeNode>
            * 业务描述：
            * 		1、根据父节点查询此节点下面的子节点，如果有子节点，必然也是parentId
            * 		2、状态：isParent
            * 			1：表示当前节点是父节点，有子节点
            * 			0：表示当前父节点就是子节点，没有子节点
            */
            @ResponseBody
            @RequestMapping("/item/cat/list")
            public List<EasyUITreeNode> findItemCatList(@RequestParam(defaultValue="0", value="id") Long parentId){
                List<EasyUITreeNode> itemCatList = itemCatService.findItemCatByParentId(parentId);
                return itemCatList;
            }
        }

    5、jsp
        访问index.jsp

        点击"新增商品"（26行），跳转到"item-add"页面

        item-add页面引用了common.js

        item-add页面在加载时执行了69行的js，并调用了common.js中的TAOTAO.init方法

        common.js的59行init方法执行了初始化商品类目的加载this.initItemCat(data);

        initItemCat方法具体的初始化了类目


六、后台管理系统-nginx负载均衡
    详见JAVAEE个人总结/项目/36taotao/项目中用到的新知识点/nginx/nginx负载均衡详细总结

七、后台管理系统-fastDFS分布式文件系统
    详见JAVAEE个人总结/项目/36taotao/项目中用到的新知识点/fastDFS/fastDFS安装和简介详细总结

八、后台-后台添加商品-图片上传(3)
    1、图片上传的演变
        见图4、5、6

    2、测试图片上传
        2.1、环境准备
            在web工程中操作

            第一步：导入坐标
            <dependency>
                <groupId>commons-fileupload</groupId>
                <artifactId>commons-fileupload</artifactId>
                <version>${commons-fileupload.version}</version>
            </dependency>
            <dependency>
                <groupId>fastdfs_client</groupId>
                <artifactId>fastdfs_client</artifactId>
                <version>1.25</version>
            </dependency>

            第二步：配置图片上传的解析器
                在springmvc.xml中配置
                <!-- 配置图片上传解析器 -->
                <bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
                    <property name="maxUploadSize" value="10240000"></property>
                </bean>

            第三步：新建配置文件
                在resourece包下创建client.conf
                用于连接tracker_server服务

                也可以直接从fastDFS中下载：
                    Alt+p
                    get /etc/fdfs/client.conf(在本地文档中)
                    剪切到项目的配置文件中，只要下面的一行

                tracker_server=192.168.254.67:22122

        2.2、MyUploadPic.java
            在web工程的测试包下创建study.project.upload.test包，创建

            /**
            * 使用fastDFS测试图片上传
            * 访问：http://192.168.254.67/group1/M00/00/00/wKj-Q1liKvGAJPlVAAsuME-Y23k010.jpg
            * @author canglang
            */
            public class MyUploadPic {

                @Test
                public void uploadPicTest01() throws Exception{

                    //指定图片路径
                    String picPath = "D:\\lang.jpg";
                    //指定client配置文件的绝对路径
                    String clitntPath = "E:\\JAVA\\ReStudy\\Study\\Project\\taotao\\07taotao-manager-web\\src\\main\\resources\\client.conf";
                    //加载客户端的配置文件client.conf，连接fastDFS服务器
                    ClientGlobal.init(clitntPath);

                    //创建TrackerClient客户端
                    TrackerClient trackerClient = new TrackerClient();
                    //获取trackerServer对象
                    TrackerServer trackerServer = trackerClient.getConnection();

                    StorageServer storageServer = null;
                    //创建StorageClient客户端
                    StorageClient storageClient = new StorageClient(trackerServer, storageServer );
                    //上传图片,参数1：图片路径、参数2：图片后缀、参数3：图片描述
                    String[] str = storageClient.upload_file(picPath, "jpg", null);

                    for (String string : str) {
                    /**
                    * group1
                    * M00/00/00/wKj-Q1liKvGAJPlVAAsuME-Y23k010.jpg
                    */
                    System.out.println(string);
                    }
                }
            }

        2.3、抽取工具类
            将工具类放在common工程下

            需要导入fileUpload和fasfDFS依赖

            public class FastDFSClient {

            private TrackerClient trackerClient = null;
            private TrackerServer trackerServer = null;
            private StorageServer storageServer = null;
            private StorageClient1 storageClient = null;

            public FastDFSClient(String conf) throws Exception {
                if (conf.contains("classpath:")) {
                    conf = conf.replace("classpath:", this.getClass().getResource("/").getPath());
                }
                ClientGlobal.init(conf);
                trackerClient = new TrackerClient();
                trackerServer = trackerClient.getConnection();
                storageServer = null;
                storageClient = new StorageClient1(trackerServer, storageServer);
            }

            /**
            * 上传文件方法
            * <p>Title: uploadFile</p>
            * <p>Description: </p>
            * @param fileName 文件全路径
            * @param extName 文件扩展名，不包含（.）
            * @param metas 文件扩展信息
            * @return
            * @throws Exception
            */
            public String uploadFile(String fileName, String extName, NameValuePair[] metas) throws Exception {
                String result = storageClient.upload_file1(fileName, extName, metas);
                return result;
            }

            public String uploadFile(String fileName) throws Exception {
                return uploadFile(fileName, null, null);
            }

            public String uploadFile(String fileName, String extName) throws Exception {
                return uploadFile(fileName, extName, null);
            }

            /**
            * 上传文件方法
            * <p>Title: uploadFile</p>
            * <p>Description: </p>
            * @param fileContent 文件的内容，字节数组
            * @param extName 文件扩展名
            * @param metas 文件扩展信息
            * @return
            * @throws Exception
            */
            public String uploadFile(byte[] fileContent, String extName, NameValuePair[] metas) throws Exception {

                String result = storageClient.upload_file1(fileContent, extName, metas);
                return result;
            }

            public String uploadFile(byte[] fileContent) throws Exception {
                return uploadFile(fileContent, null, null);
            }

            public String uploadFile(byte[] fileContent, String extName) throws Exception {
                return uploadFile(fileContent, extName, null);
            }
            }

        2.4、使用工具类测试
            还是在web工程中/src/test/java/study/project/upload/test/MyUploadPic.java中

            /**
            * 抽取工具类
            * @throws Exception
            */
            @Test
            public void uploadPicTest02() throws Exception{
                //指定图片路径
                String picPath = "D:\\lang.jpg";

                FastDFSClient fClient = new FastDFSClient("classpath:client.conf");

                String file = fClient.uploadFile(picPath, "jpg");

                //group1/M00/00/00/wKj-Q1liMDCAF9DrAAsuME-Y23k132.jpg
                System.out.println(file);
            }

    3、整合到项目-实现图片上传
        3.1、jsp
            访问index页面

            点击 新增商品 加载item-add.jsp页面

            item-add.jsp页面引用了common.js，页面加载时会初始化73行代码

            根据77行代码TAOTAO.init({fun:function(node){}找common.js的59行

            61行代码初始化上传图片插件this.initPicUpload(data);

            initPicUpload方法会执行66行代码

            在执行到88行时会根据符文本编辑器找到kingEditorParams方法

            kingEditorParams : {
                //指定上传文件参数名称
                filePostName  : "uploadFile",
                //指定上传文件请求的url。
                uploadJson : '/pic/upload',
                //上传类型，分别为image、flash、media、file
                dir : "image"
            },
        3.2、根据easyUI所需的参数定义包装类对象
            public class PicResult {

                //图片上传的状态error，0：成功、1：失败
                private int error;

                //上传成功后的url地址，用于保存到数据库和图片回显
                private String url;

                //失败时的错误信息
                private String message;
                //生成get、set方法
            }

        3.3、配置文件
            在配置文件所在的包下创建client.conf和resource.properties

            client.conf
            tracker_server=192.168.254.67:22122

            resource.properties
            #分布式文件系统路径
            IMAGE_SERVER_PATH=http://192.168.254.67/

        3.4、springmvc.xml
            <!-- 扫描配置文件 -->
            <context:property-placeholder location="classpath:resource.properties"/>

        3.5、controller
            在web工程的/study/project/controller/包下创建UploadController类

            package study.project.controller;

            @Controller
            public class UploadController {

                @Value("${IMAGE_SERVER_PATH}")
                private String IMAGE_SERVER_PATH;

                /**
                * 上传图片到fasfDFS分布式系统上
                * 请求：
                * 		common.js：/pic/upload
                * 参数：
                * 		common.js：uploadFile
                * 业务需求：
                * 		图片不跟随表单一起提交，图片上传完成后直接回显
                * @param uploadFile
                * @return
                */
                @ResponseBody
                @RequestMapping("/pic/upload")
                public PicResult uploadPic(MultipartFile uploadFile){

                    //获取上传的文件全名称
                    String originalFilename = uploadFile.getOriginalFilename();
                    String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);

                    PicResult picResult = new PicResult();
                    String url = "";
                    //创建工具类对象
                    try {
                        FastDFSClient fastDFSClient = new FastDFSClient("classpath:client.conf");
                        url = fastDFSClient.uploadFile(uploadFile.getBytes(), extName);
                        url = IMAGE_SERVER_PATH + url;

                        //上传成功
                        picResult.setError(0);
                        picResult.setUrl(url);

                    } catch (Exception e) {
                        picResult.setError(1);
                        picResult.setMessage("图片上传失败");
                        e.printStackTrace();
                    }
                    return picResult;
                }
            }

九、后台-后台添加商品-保存商品(4)
    1、jsp
        表单中的name应该跟实体类中的属性名称一致

        item_add.jsp中执行66行提交表单时会执行一个点击函数submitForm()

        执行83行的submitForm()函数

        在执行118行提交表单发送请求

    2、pojo
        根据ajax的返回值定义一个包装类(在common工程中)

        生成get、set方法

        /**
        * 淘淘商城自定义响应结构
        */
        public class ProjectResultDTO implements Serializable{

            // 定义jackson对象
            private static final ObjectMapper MAPPER = new ObjectMapper();

            // 响应业务状态
            private Integer status;

            // 响应消息
            private String msg;

            // 响应中的数据
            private Object data;

            public static ProjectResultDTO build(Integer status, String msg, Object data) {
                return new ProjectResultDTO(status, msg, data);
            }

            public static ProjectResultDTO ok(Object data) {
                return new ProjectResultDTO(data);
            }

            public static ProjectResultDTO ok() {
                return new ProjectResultDTO(null);
            }

            public ProjectResultDTO() {

            }

            public static ProjectResultDTO build(Integer status, String msg) {
                return new ProjectResultDTO(status, msg, null);
            }

            public ProjectResultDTO(Integer status, String msg, Object data) {
                this.status = status;
                this.msg = msg;
                this.data = data;
            }

            public ProjectResultDTO(Object data) {
                this.status = 200;
                this.msg = "OK";
                this.data = data;
            }

            /**
            * 将json结果集转化为TaotaoResult对象
            *
            * @param jsonData json数据
            * @param clazz TaotaoResult中的object类型
            * @return
            */
            public static ProjectResultDTO formatToPojo(String jsonData, Class<?> clazz) {
                try {
                    if (clazz == null) {
                        return MAPPER.readValue(jsonData, ProjectResultDTO.class);
                    }
                    JsonNode jsonNode = MAPPER.readTree(jsonData);
                    JsonNode data = jsonNode.get("data");
                    Object obj = null;
                    if (clazz != null) {
                        if (data.isObject()) {
                            obj = MAPPER.readValue(data.traverse(), clazz);
                        } else if (data.isTextual()) {
                            obj = MAPPER.readValue(data.asText(), clazz);
                        }
                    }
                    return build(jsonNode.get("status").intValue(), jsonNode.get("msg").asText(), obj);
                } catch (Exception e) {
                    return null;
                }
            }

            /**
            * 没有object对象的转化
            *
            * @param json
            * @return
            */
            public static ProjectResultDTO format(String json) {
                try {
                    return MAPPER.readValue(json, ProjectResultDTO.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            /**
            * Object是集合转化
            *
            * @param jsonData json数据
            * @param clazz 集合中的类型
            * @return
            */
            public static ProjectResultDTO formatToList(String jsonData, Class<?> clazz) {
                try {
                    JsonNode jsonNode = MAPPER.readTree(jsonData);
                    JsonNode data = jsonNode.get("data");
                    Object obj = null;
                    if (data.isArray() && data.size() > 0) {
                    obj = MAPPER.readValue(data.traverse(),
                        MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
                    }
                    return build(jsonNode.get("status").intValue(), jsonNode.get("msg").asText(), obj);
                } catch (Exception e) {
                    return null;
                }
            }

        }

        保存商品时需要手动设置id，在common工程中定义id生成的工具类

        /**
        * 各种id生成策略
        * <p>Title: IDUtils</p>
        * <p>Description: </p>
        * <p>Company: www.itcast.com</p>
        * @author	入云龙
        * @date	2015年7月22日下午2:32:10
        * @version 1.0
        */
        public class IDUtils {

            /**
            * 图片名生成
            */
            public static String genImageName() {
                //取当前时间的长整形值包含毫秒
                long millis = System.currentTimeMillis();
                //long millis = System.nanoTime();
                //加上三位随机数
                Random random = new Random();
                int end3 = random.nextInt(999);
                //如果不足三位前面补0
                String str = millis + String.format("%03d", end3);

                return str;
            }

            /**
            * 商品id生成
            */
            public static long genItemId() {
                //取当前时间的长整形值包含毫秒
                long millis = System.currentTimeMillis();
                //long millis = System.nanoTime();
                //加上两位随机数
                Random random = new Random();
                int end2 = random.nextInt(99);
                //如果不足两位前面补0
                String str = millis + String.format("%02d", end2);
                long id = new Long(str);
                return id;
            }
        }

    3、interface
        在interface工程中ItemService接口类中定义接口方法

        /**
        * 功能4：
        * 		添加商品后保存商品到数据库
        * 功能13：
        * 		同步索引库
        * 需要保存两张表：
        * 		TbItem：商品表
        * 		TbItemDesc:商品描述表
        * 返回值：
        * 		使用包装类对象ProjectResultDTO
        */
        public ProjectResultDTO saveItem(TbItem item, TbItemDesc itemDesc);

    4、service
        在service工程的ItemServiceImpl类中实现接口中的方法

        /**
        * 功能4：
        * 		添加商品后保存商品到数据库
        * 功能13：
        * 		同步索引库
        * 需要保存两张表：
        * 		TbItem：商品表
        * 		TbItemDesc:商品描述表
        */
        public ProjectResultDTO saveItem(TbItem item, TbItemDesc itemDesc) {
            /**
            * 根据数据库设计表可以知道，id需要手动设置
            * 1、使用redis的主键自增长
            * 2、使用UUID
            * 3、使用时间戳：毫秒+随机数
            */
            long itemId = IDUtils.genItemId();

            //补全参数
            //设置id
            item.setId(itemId);
            //商品状态，1-正常，2-下架，3-删除
            item.setStatus((byte)1);
            //补全时间
            Date date = new Date();
            //创建时间
            item.setCreated(date);
            //更新时间
            item.setUpdated(date);

            //执行保存
            itemMapper.insert(item);

            //商品表和商品描述表的关系是一对一，所以id应该一样
            itemDesc.setItemId(itemId);
            itemDesc.setCreated(date);
            itemDesc.setUpdated(date);

            itemDescMapper.insert(itemDesc);

            return ProjectResultDTO.ok();
        }

    5、controller
        在web工程的ItemController类中调用接口

        /**
        * 功能4：
        * 		添加商品后保存商品到数据库
        * 功能13：
        * 		同步索引库
        * 请求：
        * 		$.post("/item/save",$("#itemAddForm").serialize(), function(data){
        * 参数：
        * 		保存两张表的数据，商品表和商品描述表
        * 返回值：
        * 		根据ajax的毁掉函数来判断需要的返回值
        * 需要保存两张表：
        * 		TbItem：商品表
        * 		TbItemDesc:商品描述表
        */
        @ResponseBody
        @RequestMapping("/item/save")
        public ProjectResultDTO saveItem(TbItem item, TbItemDesc itemDesc){
            ProjectResultDTO resultDTO = itemService.saveItem(item, itemDesc);

            return resultDTO;
        }

    6、测试
        见图1

    7、解决浏览器兼容星问题

        分析：
            上传图片使用kindEditor插件，发现google浏览器非常好使用，但是火狐上传失败，

            这主要是由于kindEditor插件对浏览器兼容器不是很好。需要修改相关配置代码：

        问题原因：
            浏览器不解析返回值类型。

            返回值类型：contentType:application/json

            火狐不支持返回的json格式的数据。

            但是不管什么浏览器都支持text/html类型的数据。

        解决思路：
            指定返回数据类型是字符串类型文本类型。

            让回把json格式的字符串转换成json对象。

        改造：
            改造上传图片时的返回值

            在web工程的UploadController类的uploadPic方法中修改

            第一步：导入jsonutil转换工具

            /**
            * 淘淘商城自定义响应结构
            */
            public class JsonUtils {

                // 定义jackson对象
                private static final ObjectMapper MAPPER = new ObjectMapper();

                /**
                * 将对象转换成json字符串。
                * <p>Title: pojoToJson</p>
                * <p>Description: </p>
                * @param data
                * @return
                */
                public static String objectToJson(Object data) {
                    try {
                        String string = MAPPER.writeValueAsString(data);
                        return string;
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                /**
                * 将json结果集转化为对象
                *
                * @param jsonData json数据
                * @param clazz 对象中的object类型
                * @return
                */
                public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
                    try {
                        T t = MAPPER.readValue(jsonData, beanType);
                        return t;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                /**
                * 将json数据转换成pojo对象list
                * <p>Title: jsonToList</p>
                * <p>Description: </p>
                * @param jsonData
                * @param beanType
                * @return
                */
                public static <T>List<T> jsonToList(String jsonData, Class<T> beanType) {
                    JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
                    try {
                        List<T> list = MAPPER.readValue(jsonData, javaType);
                        return list;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return null;
                }
            }

            第二步：修改代码
                将返回值
                return picResult;
                改成
                return JsonUtils.objectToJson(picResult);

第三、前台门户系统-后台管理系统
十、前台-搭建前台门户系统
    门户系统架构
    见图2

    1、搭建
        前端门户系统
        见图3

        服务端聚合系统(用于聚合门户系统的pojo、interface等)
        见图4

        创建聚合工程中的接口工程
        见图5

        创建聚合工程中的服务工程
        见图6

    2、整合
        2.1、整合portal-web工程
            2.1.1、pom.xml
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                            http://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>
                    <parent>
                        <artifactId>01taotao-parent</artifactId>
                        <groupId>com.taotao</groupId>
                        <version>0.0.1-SNAPSHOT</version>
                    </parent>
                    <groupId>com.taotao</groupId>
                    <artifactId>09taotao-portal-web</artifactId>
                    <version>0.0.1-SNAPSHOT</version>
                    <packaging>war</packaging>

                    <!--
                        需要的依赖：
                            1、加载springmvc.xml配置文件：spring依赖(包含springmvc)
                            2、页面展示：jstl、jsp、servlet
                            3、引入服务：dubbo、zookeeper
                            4、接口：content-interface
                            5、日志
                            6、tomcat插件：端口8083
                        参考：
                            manager-web
                    -->
                    <dependencies>

                        其他依赖和  manager-web  工程的pom.xml一样

                        <!-- 依赖接口 -->
                        <dependency>
                            <groupId>com.taotao</groupId>
                            <artifactId>11taotao-content-interface</artifactId>
                            <version>0.0.1-SNAPSHOT</version>
                        </dependency>
                    </dependencies>
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
                                    <port>8083</port>
                                </configuration>
                            </plugin>
                        </plugins>
                    </build>
                </project>

            2.1.2、复制web.xml
                <?xml version="1.0" encoding="UTF-8"?>
                <web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xmlns="http://java.sun.com/xml/ns/javaee"
                    xmlns:web="http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
                    xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
                                http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd" id="taotao" version="2.5">

                    其他配置和  manager-web  工程的web.xml一样

                    <servlet-mapping>
                        <servlet-name>springmvc</servlet-name>
                        <!-- 伪静态化 -->
                        <url-pattern>*.html</url-pattern>
                    </servlet-mapping>
                </web-app>

            2.1.3、配置文件
                复制  manager-web  工程的log4j.properties和springmvc.xml配置文件

                log4j.properties和manager-web工程的一样

                springmvc.xml
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

                        <!-- 扫描配置文件 -->
                        <context:property-placeholder location="classpath:resource.properties"/>

                        <!-- 扫描controller -->
                        <context:component-scan base-package="study.project"></context:component-scan>

                        <!-- 驱动注解 -->
                        <mvc:annotation-driven/>

                        <!-- 视图解析器 -->
                        <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
                            <property name="prefix" value="/WEB-INF/jsp/"></property>
                            <property name="suffix" value=".jsp"></property>
                        </bean>

                        <!-- 引用服务 -->
                        <dubbo:application name="taotao-manager-web"/>
                        <!-- <dubbo:registry address="multicast://224.5.6.7:1234" /> -->
                        <!-- 使用dubbo从Zookeeper注册中心获取服务 -->
                        <dubbo:registry protocol="zookeeper" address="192.168.254.66:2181"/>

                        <!-- 引用服务(包含根据id查询商品信息、分页查询商品列表) -->
                        <dubbo:reference id="itemService" interface="study.project.ItemService" version="1.0.0" timeout="5000"/>

                        <!-- 引用服务(查询商品类目) -->
                        <dubbo:reference id="itemCatService" interface="study.project.ItemCatService" version="1.0.0" timeout="5000"/>

                    </beans>

            2.1.4、复制静态资源
                位置：
                    E:\JAVA\ReStudy\DataWord\30TaoTao商城\资料\静态页面\前台系统静态页面

                    将此目录下的css、js、image复制到webapp下

                    将此目录下的jsp复制到WEB-INF下

        2.2、整合inatrface工程
            pom.xml
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                            http://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>
                    <parent>
                        <artifactId>10taotao-content</artifactId>
                        <groupId>com.taotao</groupId>
                        <version>0.0.1-SNAPSHOT</version>
                        <relativePath>..</relativePath>
                    </parent>
                    <artifactId>11taotao-content-interface</artifactId>
                    <!--
                        所需坐标：
                        pojo
                        参考：
                        manager-interface
                    -->
                    <dependencies>
                        <dependency>
                            <groupId>com.taotao</groupId>
                            <artifactId>04taotao-manager-pojo</artifactId>
                            <version>0.0.1-SNAPSHOT</version>
                        </dependency>
                    </dependencies>
                </project>

        2.3、整合service工程
            2.3.1、pom.xml
                除了接口依赖不一样其他的都一样

            2.3.2、配置文件
                applicationContext-dao.xml
                applicationContext-service.xml
                jdbc.properties
                log4j.properties
                sqlMapConfig.xml


                修改applicationContext-service.xml配置文件，其他配置文件都一样
                部署到同一台服务器上时 端口需要改变一下，如果是不同服务器则端口可以一样
                <dubbo:protocol name="dubbo" port="20881" />

            2.3.3、web.xml
                在service工程中添加web.xml配置文件

                和manager-service一样

        2.4、测试
            启动content-web工程
            见图7

十一、前台门户系统-后台内容分类管理-加载树形分类(5)
    1、interface
        在content-interface工程下创建study.project包 再创建ContentCategoryService.java类

        /**
        * 功能5：
        * 		加载门户系统商城首页内容分类树形菜单
        * 参数：
        * 		父id：parentId
        * 返回值：
        * 		List<EasyUITreeNode>:转成json
        */
        public List<EasyUITreeNode> findContentCategoryList(Long parentId);

    2、service
        在content-service工程下创建study.project包 再创建ContentCategoryServiceImpl.java实现类

        //注入到接口
        @Resource
        private TbContentCategoryMapper contentCategoryMapper;

        public List<EasyUITreeNode> findContentCategoryList(Long parentId) {
            //创建TbContentCategoryExample对象
            TbContentCategoryExample example = new TbContentCategoryExample();
            //创建Criteria对象
            Criteria createCriteria = example.createCriteria();
            //设置参数
            createCriteria.andParentIdEqualTo(parentId);
            //执行查询
            List<TbContentCategory> cList = contentCategoryMapper.selectByExample(example);
            //创建List<EasyUITreeNode>集合封装节点信息
            List<EasyUITreeNode> treeNodeList = new ArrayList<EasyUITreeNode>();

            for (TbContentCategory contentCategory : cList) {
                //创建EasyUITreeNode对象封装树形节点信息
                EasyUITreeNode treeNode = new EasyUITreeNode();
                treeNode.setId(contentCategory.getId().intValue());
                treeNode.setText(contentCategory.getName());
                treeNode.setState(contentCategory.getIsParent()?"closed":"open");
                treeNodeList.add(treeNode);
            }

            return treeNodeList;
        }
    3、发布服务
        在content-service工程下的applicationContext-service.xml配置文件中发布服务

        <dubbo:protocol name="dubbo" port="20881" /><!-- 同一个tomcat下端口不能重复 -->
        <!-- 声明需要暴露的服务接口 -->

        <!-- 创建需要发布对象(包含根据id查询商品信息、分页查询商品列表)-->
        <bean id="contentCategoryServiceImpl" class="study.project.service.impl.ContentCategoryServiceImpl"></bean>
        <!-- 发布服务 -->
        <dubbo:service ref="contentCategoryServiceImpl" interface="study.project.ContentCategoryService" version="1.0.0" timeout="10000"/>

    4、controller
        因为门户系统的后台也属于后台系统，所以要在07taotao-manager-web工程下创建controller

        创建ContentCategoryController.java类

            @Resource
            private ContentCategoryService contentCategoryService;

            /**
            * 功能5：
            * 		后台内容分类管理查询-加载树形分类
            * 请求：
            * 		/content/category/list
            * 参数：
            * 		父id：parentId
            * 返回值：
            * 		List<EasyUITreeNode>:转成json
            */
            @ResponseBody
            @RequestMapping("/content/category/list")
            public List<EasyUITreeNode> findContentCategoryList(@RequestParam(defaultValue="0",value="id") Long parentId){
                List<EasyUITreeNode> categoryList = contentCategoryService.findContentCategoryList(parentId);
                return categoryList;
            }

        pom.xml
            <dependency>
                <groupId>com.taotao</groupId>
                <artifactId>11taotao-content-interface</artifactId>
                <version>0.0.1-SNAPSHOT</version>
            </dependency>

        引用服务
            <!-- 引用服务(后台管理系统加载首页分类的树形菜单) -->
            <dubbo:reference id="contentCategoryService" interface="study.project.ContentCategoryService" version="1.0.0" timeout="5000"/>

    5、jsp
        访问manager-web工程下的index.jsp

        点击34行内容分类管理，跳转到content-category.jsp页面

        content-category.jsp页面加载时会默认执行13行的jquery方法，发送请求/content/category/list

十二、前台门户系统-后台内容分类管理-创建节点(6)
    1、interface
        在content-interface工程study.project包下的ContentCategoryService.java类中

        /**
        * 功能6：
        * 		后台内容分类管理查询-创建节点
        * 参数：
        * 		parentId(上一级节点id)，name(当前节点名称)
        * 返回值：
        * 		ProjectResultDTO.ok(TbContentCategory)
        * 业务：
        * 		如果创建的节点id是子节点，就需要将子节点修改成父节点，修改isParent=1
        */
        public ProjectResultDTO creatNode(Long parentId, String name);

    2、service
        在content-service工程下的study.project包下的ContentCategoryServiceImpl.java类中

        public ProjectResultDTO creatNode(Long parentId, String name) {
            //创建节点就是插入一条数据
            //创建TbContentCategory对象，补齐其他属性
            TbContentCategory contentCategory = new TbContentCategory();
            //id自增长不需要手动设置
            //设置parentId
            contentCategory.setParentId(parentId);
            contentCategory.setName(name);
            //设置排序类型
            contentCategory.setSortOrder(1);
            //设置当前创建的节点的状态1：正常、2：删除
            contentCategory.setStatus(1);
            //设置是否是父节点(新创建的一定是子节点)
            contentCategory.setIsParent(false);

            Date date = new Date();
            contentCategory.setCreated(date);
            contentCategory.setUpdated(date);

            //执行插入
            int insert = contentCategoryMapper.insert(contentCategory);

            //根据上一级节点的id也就是新创建节点的父id查询上一级节点是否是父节点
            TbContentCategory cCategory = contentCategoryMapper.selectByPrimaryKey(parentId);

            //判断是否为父节点
            if (!cCategory.getIsParent()) {
                //上级节点是子节点，更新节点状态
                cCategory.setIsParent(true);
                //保存
                contentCategoryMapper.updateByPrimaryKey(cCategory);
            }

            return ProjectResultDTO.ok(contentCategory);
        }

    3、controller
        在manager-web工程下的ContentCategoryController.java类中

        @ResponseBody
        @RequestMapping("/content/category/create")
        public ProjectResultDTO creatNode(Long parentId, String name){
            ProjectResultDTO creatNode = contentCategoryService.creatNode(parentId, name);
            return creatNode;
        }

    4、jsp
        content-category.jsp页面加载时默认执行13行代码加载树形菜单

        在数据回调成功后会给每一个mune添加一个右键显示的效果(21行，右键时显示一个id=contentCategoryMenu的效果)

        右键时会执行6行代码，显示添加(add)、删除(delete)、重命名(rename)按钮

        点击时会执行点击事件menuHandler

        执行43行代码是会根据选择的是那一个按钮而操作

        根据59行选择当前节点而执行26行创建节点并且发送请求

十三、前台门户系统-后台内容管理-根据子节点id查询分类内容(7)
    1、jsp
        在manager-web工程中点击index.jsp页面中的35行"内容管理",跳转到content.jsp

        content.jsp页面加载时会加载28行jquery的方法

        根据jquery的id选择器(29行)会默认发送请求/content/category/list(5行)，加载树形分类

        根据jquery的id选择器(30行)会默认加载右侧(9行)的分类内容

        当点击的分类是子节点时会执行9行发送请求查询分类内容显示在右侧

    2、interface
        在congtent-interface工程下创建ContentService.java接口

        /**
        * 功能7：
        * 		根据categoryId分页查询分类内容表数据
        * 参数：
        * 		categoryId：子节点分类id
        * 返回值：
        * 		EasyUIResult:easyui分页查询
        */
        public EasyUIResult findContentByCategoeyId(Long categoryId, Integer page, Integer rows);

    3、service
        在content-service工程下创建ContentServiceImpl.java接口实现类

        @Resource
        private TbContentMapper contentMapper;

        public EasyUIResult findContentByCategoeyId(Long categoryId, Integer page, Integer rows) {
            //创建TbContentExample对象
            TbContentExample example = new TbContentExample();
            //创建Criteria对象
            Criteria createCriteria = example.createCriteria();
            //设置参数
            createCriteria.andCategoryIdEqualTo(categoryId);
            //执行查询之前设置分页参数
            PageHelper.startPage(page, rows);
            //执行查询
            List<TbContent> contentlList = contentMapper.selectByExample(example);
            //封装分页信息
            PageInfo<TbContent> pageInfo = new PageInfo<TbContent>(contentlList);
            //创建EasyUIResult对象，封装前端页面需要的格式
            EasyUIResult easyUIResult = new EasyUIResult();
            easyUIResult.setTotal(pageInfo.getTotal());
            easyUIResult.setRows(contentlList);

            return easyUIResult;
        }

    4、发布服务
        在content-service的applicationContext-service.xml配置文件中发布服务

        <!-- 创建需要发布对象(根据分类id查询分类内容)-->
        <bean id="contentServiceImpl" class="study.project.service.impl.ContentServiceImpl"></bean>
        <!-- 发布服务 -->
        <dubbo:service ref="contentServiceImpl" interface="study.project.ContentService" version="1.0.0" timeout="10000"/>

    5、controller
        在manager-web工程中创建ContentController.java类

        @Controller
        public class ContentController {

            @Resource
            private ContentService contentService;

            /**
            * 功能7：
            * 		根据categoryId分页查询分类内容表数据
            * 请求：
            * 		/content/query/list
            * 参数：
            * 		Long categoryId：子节点分类id
            * 		Integer page:当前页
            * 		Integer rows:内容集合
            * 返回值：
            * 		json格式EasyUIResult
            */
            @ResponseBody
            @RequestMapping("/content/query/list")
            public EasyUIResult findContentByCategoryId(Long categoryId, Integer page, Integer rows){
                EasyUIResult contentList = contentService.findContentByCategoeyId(categoryId, page, rows);
                return contentList;
            }
        }

    5、测试
        见图1

十四、前台门户系统-后台内容管理-新增分类内容(8)
    1、jsp
        点击content.jsp页面的第9行会根据contentListToolbar找到41行

        点击新增时会根据id选择器#contentCategoryTree获取当前节点

        如果不是子节点就给出提示语

        如果是子节点就跳转到content-add.jsp页面

        在页面加载时会执行55行初始化父文本编辑器、图片上传组件和将分类id赋值给隐藏域

        点击49行的提交时会找contentAddPage对象中的submitForm()方法(61和62行)

        校验表单、同步父文本编辑器和发送请求

    2、interface
        在congtent-interface工程下的ContentService.java接口中

        /**
        * 功能8：
        * 		根据分类id添加此分类下的内容数据
        * 参数：
        * 		TbContent
        * 返回值：
        * 		ProjectResultDTO
        */
        public ProjectResultDTO saveContent(TbContent content);

    3、service
        在content-service工程下的ContentServiceImpl.java接口实现类中

        /**
        * 功能8：
        * 		根据分类id添加此分类下的内容数据
        * 参数：
        * 		TbContent
        * 返回值：
        * 		ProjectResultDTO
        */
        public ProjectResultDTO saveContent(TbContent content) {

            //补全时间
            Date date = new Date();
            content.setCreated(date);
            content.setUpdated(date);
            int insert = contentMapper.insert(content);

            return ProjectResultDTO.ok();
        }

    4、controller
        在manager-web工程中的ContentController.java类中

        /**
        * 功能8：
        * 		根据分类id添加此分类下的内容数据
        * 请求：
        * 		/content/save
        * 参数：
        * 		TbContent
        * 返回值：
        * 		json格式的ProjectResultDTO
        */
        @ResponseBody
        @RequestMapping("/content/save")
        public ProjectResultDTO saveContent(TbContent content){
            ProjectResultDTO saveContent = contentService.saveContent(content);
            return saveContent;
        }
	  

第四、前台门户系统
	商城架构回顾   见图1

十五、前台门户系统-查询轮播图(9)
    1、jsp
        portal-web工程的index.html，36-61行是首页轮播图的代码

        轮播图所需的json数据格式是43行

        44行是首页加载时通过EL表达式获取的数据及名称

        48行是判断轮播图的数量大于1时，初始化一个轮播图并且显示轮播图右下角的圆点

    2、pojo
        在manger-pojo工程中创建首页轮播图所需要的数据格式的包装类对象

        注意：
            跨服务器一定要实现序列化

        /**
        * 首页轮播图展示所需的json格式数据
        * @author canglang
        */
        public class ADItem implements Serializable{

            private String srcB;
            private Integer height;
            private String alt;
            private Integer width;
            private String src;
            private Integer widthB;
            private String href;
            private Integer heightB;

        }

    3、resource.properties
        在content-service工程中的resource.properties配置文件中将图片的宽和高固定，用于serviceImpl的依赖注入

        注意：
            在content-service工程中既有resource.properties又有jdbc.properties配置文件，在applicationContext-dao.xml中

            要将
            <context:property-placeholder location="classpath:jdbc.properties" file-encoding="UTF-8"/>
            改成
            <context:property-placeholder location="classpath:*.properties" file-encoding="UTF-8"/>

            因为两个配置文件不能同时扫描，否则会报错

        resource.properties配置文件内容
            #设置图片的宽
            WIDTH=670
            WIDTHB=550
            #设置图片的高
            HEIGTH=240
            HEIGTHB=240

    4、interface
        在content-interface工程中，添加查询首页轮播图的接口

        /**
        * 功能9：
        * 		前台门户系统-查询轮播图
        * 参数：
        * 		categoryId
        * 返回值：
        * 		List<ADItem>,因为有多张图片
        */
        public List<ADItem> findContentListByCategoryId(Long categoryId);

    5、service
        在content-service工程中实现接口中的方法

        //注入图片的宽和高
        @Value("${WIDTH}")
        private Integer WIDTH;

        @Value("${WIDTHB}")
        private Integer WIDTHB;

        @Value("${HEIGTH}")
        private Integer HEIGTH;

        @Value("${HEIGTHB}")
        private Integer HEIGTHB;

        /**
        * 功能9：
        * 		前台门户系统-查询轮播图
        * 参数：
        * 		categoryId
        * 返回值：
        * 		List<ADItem>,因为有多张图片
        */
        public List<ADItem> findContentListByCategoryId(Long categoryId) {

            TbContentExample example = new TbContentExample();

            Criteria createCriteria = example.createCriteria();

            createCriteria.andCategoryIdEqualTo(categoryId);

            List<TbContent> contentList = contentMapper.selectByExample(example);

            //创建List<ADItem>集合封装轮播图所需的数据
            List<ADItem> itemList = new ArrayList<ADItem>();

            for (TbContent content : contentList) {
                ADItem ad = new ADItem();
                //图片描述信息
                ad.setAlt(content.getSubTitle());
                //设置图片地址
                ad.setSrc(content.getPic());
                ad.setSrcB(content.getPic2());
                //设置图片购买地址
                ad.setHref(content.getUrl());
                //设置图片的宽、高
                ad.setWidth(WIDTH);
                ad.setWidthB(WIDTHB);
                ad.setHeight(HEIGTH);
                ad.setHeightB(HEIGTHB);

                itemList.add(ad);
            }

            return itemList;
        }

    6、resource.properties
        在portal-web工程的resource.properties配置文件中将首页轮播图的分类id写入，用于首页轮播图的依赖注入

        注意：
            springmvc.xml配置文件中需要注入

            <context:property-placeholder location="classpath:resource.properties"/>

        resource.properties
            #首页轮播图的分类id
            LUNBOTU_CATEGORYID=89

    7、controller
        在portal-web工程中的PageController.java中完成交互

        @Resource
        private ContentService contentService;

        @Value("${LUNBOTU_CATEGORYID}")
        private Long LUNBOTU_CATEGORYID;

        /**
        * 跳转到首页
        *
        * 因为在首页加载时就获取首页的数据，所以需要放在跳转到首页的请求中来操作
        *
        * 功能9：
        * 		前台门户系统-查询轮播图
        * 参数：
        * 		categoryId
        * 返回值：
        * 		将List<ADItem>集合转成json放入model中，页面通过el表达式获取
        * @author canglang
        */
        @RequestMapping("/index")
        public String showIndex(Model model){

            List<ADItem> itemList = contentService.findContentListByCategoryId(LUNBOTU_CATEGORYID);
            //将结果转成json格式的字符串
            String adJson = JsonUtils.objectToJson(itemList);
            model.addAttribute("adJson", adJson);
            return "index";
        }
	  

十六、前台门户系统-首页添加redis缓存(10)
    1、架构分析
        为了减轻数据库的压力，在一些并发量比较大的页面提价redis缓存

        redis缓存是内存版的 访问的效率高、速度快
        见图2

    2、redis服务搭建、资料及总结
        详见28Redis

    3、启动redis
        单机版：
            cd /usr/local/hadoop/canglang/redis/redis-install/6379/bin
            启动：./redis-server redis.conf
            登录：./redis-cli

        集群：
            cd /usr/local/hadoop/canglang/redis/redis-jiqun-install
            启动：./redis-start-all.sh
            登录：
                cd 7001/bin
                ./redis-cli -c -h 192.168.254.66 -p 7001

    4、redis测试
        在content-service工程中需要使用redis缓存，以为此服务是展示首页数据的

        4.1、使用Jedis连接Redis
            步骤一：复制依赖
                从parent工程中复制redis所需的依赖Jedis到content-service工程的pom中
                <!-- Redis客户端 -->
                <dependency>
                    <groupId>redis.clients</groupId>
                    <artifactId>jedis</artifactId>
                    <version>${jedis.version}</version>
                </dependency>

            步骤二：编写测试类
                在content-service工程中的/src/test/java/包下创建study.project.test包

                public class MyJedis {
                    /**
                    * 测试单机版的Jedis连接Redis
                    */
                    @Test
                    public void testRedis01(){
                        //创建Jedis对象，连接redis缓存数据库
                        Jedis jedis = new Jedis("192.168.254.66", 6379);
                        jedis.set("itemName", "洗脚盆");
                        String itemName = jedis.get("itemName");
                        System.out.println(itemName);
                        }
                        /**
                        * 使用单机版的连接池连接redis
                        */
                        @Test
                        public void testPoolRedis(){
                        //创建jedis配置对象
                        JedisPoolConfig poolConfig = new JedisPoolConfig();
                        //设置最大空闲数
                        poolConfig.setMaxIdle(20);
                        //设置最大连接数
                        poolConfig.setMaxTotal(10000);
                        //创建JedisPool对象
                        JedisPool jedisPool = new JedisPool(poolConfig, "192.168.254.66", 6379);
                        //获取redis对象
                        Jedis jedis = jedisPool.getResource();
                        jedis.set("itemName", "洗脚盆wwww");

                        String itemName = jedis.get("itemName");

                        System.out.println(itemName);
                    }

                    /**
                    * 使用集群版的jedis连接池连接redis集群
                    */
                    @Test
                    public void testPoolClusterRedis(){
                        //创建jedis配置对象
                        JedisPoolConfig poolConfig = new JedisPoolConfig();
                        //设置最大空闲数
                        poolConfig.setMaxIdle(20);
                        //设置最大连接数
                        poolConfig.setMaxTotal(10000);

                        Set<HostAndPort> nodes = new HashSet<HostAndPort>();
                        nodes.add(new HostAndPort("192.168.254.66", 7001));
                        nodes.add(new HostAndPort("192.168.254.66", 7002));
                        nodes.add(new HostAndPort("192.168.254.66", 7003));
                        nodes.add(new HostAndPort("192.168.254.66", 7004));
                        nodes.add(new HostAndPort("192.168.254.66", 7005));
                        nodes.add(new HostAndPort("192.168.254.66", 7006));
                        nodes.add(new HostAndPort("192.168.254.66", 7007));
                        nodes.add(new HostAndPort("192.168.254.66", 7008));

                        //创建JedisCluster对象
                        JedisCluster jedisCluster = new JedisCluster(nodes, poolConfig);
                        jedisCluster.set("itemName", "iphone6s plus");

                        String itemName = jedisCluster.get("itemName");
                        System.out.println(itemName);
                    }
                }

        4.2、整合spring
            4.2.1、applicationContext-redis.xml
                在content-service工程下新创建一个spring的配置文件

                <?xml version="1.0" encoding="UTF-8"?>
                <beans xmlns="http://www.springframework.org/schema/beans"
                    xmlns:context="http://www.springframework.org/schema/context"
                    xmlns:mvc="http://www.springframework.org/schema/mvc"
                    xmlns:aop="http://www.springframework.org/schema/aop"
                    xmlns:tx="http://www.springframework.org/schema/tx"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
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

                    <!--1、 测试单机版的Jedis连接Redis -->
                    <bean class="redis.clients.jedis.Jedis">
                        <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                        <constructor-arg name="port" value="6379"></constructor-arg>
                    </bean>

                    <!--2、使用单机版的jedis连接池连接redis 把JedisPoolConfig对象交给spring创建 -->
                    <bean id="poolConfig" class="redis.clients.jedis.JedisPoolConfig">
                        <property name="maxIdle" value="20"></property>
                        <property name="maxTotal" value="1000"></property>
                    </bean>

                    <!-- 将JedisPool对象交给spring创建 -->
                    <bean class="redis.clients.jedis.JedisPool">
                        <constructor-arg name="poolConfig" ref="poolConfig"></constructor-arg>
                        <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                        <constructor-arg name="port" value="6379"></constructor-arg>
                    </bean>

                    <!-- 3、使用spring整合集群版的jedis连接池连接redis集群 -->
                    <bean class="redis.clients.jedis.JedisCluster">
                        <constructor-arg name="nodes">
                            <set>
                                <bean class="redis.clients.jedis.HostAndPort">
                                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                                    <constructor-arg name="port" value="7001"></constructor-arg>
                                </bean>
                                <bean class="redis.clients.jedis.HostAndPort">
                                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                                    <constructor-arg name="port" value="7002"></constructor-arg>
                                </bean>
                                <bean class="redis.clients.jedis.HostAndPort">
                                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                                    <constructor-arg name="port" value="7003"></constructor-arg>
                                </bean>
                                <bean class="redis.clients.jedis.HostAndPort">
                                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                                    <constructor-arg name="port" value="7004"></constructor-arg>
                                </bean>
                                <bean class="redis.clients.jedis.HostAndPort">
                                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                                    <constructor-arg name="port" value="7005"></constructor-arg>
                                </bean>
                                <bean class="redis.clients.jedis.HostAndPort">
                                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                                    <constructor-arg name="port" value="7006"></constructor-arg>
                                </bean>
                                <bean class="redis.clients.jedis.HostAndPort">
                                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                                    <constructor-arg name="port" value="7007"></constructor-arg>
                                </bean>
                                <bean class="redis.clients.jedis.HostAndPort">
                                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                                    <constructor-arg name="port" value="7008"></constructor-arg>
                                </bean>
                            </set>
                        </constructor-arg>
                        <constructor-arg name="poolConfig" ref="poolConfig"></constructor-arg>
                    </bean>
                </beans>

            4.2.2、MySpringRedis.java
                public class MySpringRedis {

                    /**
                    * 测试单机版的spring连接Redis
                    */
                    @Test
                    public void testSpringRedis(){
                        //加载spring的配置文件
                        ApplicationContext app = new ClassPathXmlApplicationContext("classpath*:applicationContext-redis.xml");

                        Jedis jedis = app.getBean(Jedis.class);

                        jedis.set("name", "lisi");

                        String name = jedis.get("name");

                        System.out.println(name);
                    }

                    /**
                    * 使用spring整合单机版的jedis连接池连接redis
                    */
                    @Test
                    public void testSpringPoolRedis(){
                        //加载spring的配置文件
                        ApplicationContext app = new ClassPathXmlApplicationContext("classpath*:applicationContext-redis.xml");

                        JedisPool jedisPool = app.getBean(JedisPool.class);

                        Jedis jedis = jedisPool.getResource();

                        jedis.set("name", "zhangsan");

                        String name = jedis.get("name");

                        System.out.println(name);
                        }
                        /**
                        * 使用spring整合集群版的jedis连接池连接redis集群
                        */
                        @Test
                        public void testSpringJedisCluster(){
                        //加载spring的配置文件
                        ApplicationContext app = new ClassPathXmlApplicationContext("classpath*:applicationContext-redis.xml");

                        JedisCluster jedisCluster = app.getBean(JedisCluster.class);

                        jedisCluster.set("itemName", "iphone6s plus iphone6s");
                        String itemName = jedisCluster.get("itemName");
                        System.out.println(itemName);

                    }
                }

    5、整合到项目中--实现从缓存中取数据
        5.1、单机版
            5.1.1、抽取常用的jedis的Dao接口
                在content-interface工程下创建study.project.redis/JedisDao.java

                public interface JedisDao {
                    //抽取Jedis的常用方法
                    //数据结构string
                    public String set(String key, String value);
                    public String get(String key);
                    //自增、自减
                    public Long incr(String key);
                    public Long decr(String key);

                    //数据结构hash
                    public Long hset(String key, String field, String value);
                    public String hget(String key, String field);
                    //删除
                    public Long hdel(String key, String field);

                    //过期设置
                    public Long expire(String key, int seconds);
                    //查看过期时间
                    public Long ttl(String key);
                }

            5.1.2、Dao接口实现类
                在content-service工程下创建实现类
                @Repository
                public class JedisDaoImpl implements JedisDao {
                    //*******************单机版的********************
                    //注入spring创建好的JedisPool对象
                    @Resource
                    private JedisPool jedisPool;

                    @Override
                    public String set(String key, String value) {
                        //从连接池中获取Jedis对象
                        Jedis jedis = jedisPool.getResource();
                        String keys = jedis.set(key, value);
                        return keys;
                    }

                    @Override
                    public String get(String key) {
                        //从连接池中获取Jedis对象
                        Jedis jedis = jedisPool.getResource();
                        String value = jedis.get(key);
                        return value;
                    }

                    @Override
                    public Long incr(String key) {
                        //从连接池中获取Jedis对象
                        Jedis jedis = jedisPool.getResource();
                        Long incr = jedis.incr(key);
                        return incr;
                    }

                    @Override
                    public Long decr(String key) {
                        //从连接池中获取Jedis对象
                        Jedis jedis = jedisPool.getResource();
                        Long decr = jedis.decr(key);
                        return decr;
                    }

                    @Override
                    public Long hset(String key, String field, String value) {
                        //从连接池中获取Jedis对象
                        Jedis jedis = jedisPool.getResource();
                        Long hset = jedis.hset(key, field, value);
                        return hset;
                    }

                    @Override
                    public String hget(String key, String field) {
                        //从连接池中获取Jedis对象
                        Jedis jedis = jedisPool.getResource();
                        String hget = jedis.hget(key, field);
                        return hget;
                    }

                    @Override
                    public Long hdel(String key, String field) {
                        //从连接池中获取Jedis对象
                        Jedis jedis = jedisPool.getResource();
                        Long hdel = jedis.hdel(key, field);
                        return hdel;
                    }

                    @Override
                    public Long expire(String key, int seconds) {
                        //从连接池中获取Jedis对象
                        Jedis jedis = jedisPool.getResource();
                        Long expire = jedis.expire(key, seconds);
                        return expire;
                    }

                    @Override
                    public Long ttl(String key) {
                        //从连接池中获取Jedis对象
                        Jedis jedis = jedisPool.getResource();
                        Long ttl = jedis.ttl(key);
                        return ttl;
                    }
                }

            5.1.3、applicationContext-redis.xml
                <?xml version="1.0" encoding="UTF-8"?>
                <beans xmlns="http://www.springframework.org/schema/beans"
                    xmlns:context="http://www.springframework.org/schema/context"
                    xmlns:mvc="http://www.springframework.org/schema/mvc"
                    xmlns:aop="http://www.springframework.org/schema/aop"
                    xmlns:tx="http://www.springframework.org/schema/tx"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
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

                    <!--使用单机版的jedis连接池连接redis 把JedisPoolConfig对象交给spring创建 -->
                    <bean id="poolConfig" class="redis.clients.jedis.JedisPoolConfig">
                        <property name="maxIdle" value="20"></property>
                        <property name="maxTotal" value="1000"></property>
                    </bean>
                    <!-- 将JedisPool对象交给spring创建 -->
                    <!-- <bean class="redis.clients.jedis.JedisPool">
                        <constructor-arg name="poolConfig" ref="poolConfig"></constructor-arg>
                        <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                        <constructor-arg name="port" value="6379"></constructor-arg>
                    </bean> -->
                </beans>

            5.1.4、设置hash固定key
                本次修改采用hash数据结构，所以将首页固定可以提取到配置文件
                content-service工程中的resource.properties配置文件
                内容：AD_CHACHE=AD_CHACHE

            5.1.5、改造service实现类
                修改content-service工程的的ContentServiceImpl.java实现类

                在首页加载数据时操作

                功能9方法代码

                //注入首页缓存的key
                @Value("${AD_CHACHE}")
                private String AD_CHACHE;

                //注入jedisDao
                @Resource
                private JedisDao jedisDao;

                /**
                * 功能9：
                * 		前台门户系统-查询轮播图
                * 参数：
                * 		categoryId
                * 返回值：
                * 		List<ADItem>,因为有多张图片
                * 添加缓存：
                * 		为了提高项目的并发量和查询效率(QPS: query per second(每一秒的查询效率))
                * 		给首页添加缓存：内存版的redis
                * 		redis的数据结构：key:value
                * 		redis 的key设计：采用hash类型存储首页缓存
                * 			key:AD_CHACHE
                * 			field:categoryId
                * 			value:json格式数组字符串
                */
                public List<ADItem> findContentListByCategoryId(Long categoryId) {

                    try {
                        //查询数据库之前先查选缓存
                        //1、如果缓存中有数据，直接返回
                        //2、如果缓存中没有数据，去查询数据库，并把查到的数据存入缓存
                        String json = jedisDao.hget(AD_CHACHE, categoryId.toString());

                        if (StringUtils.isNotBlank(json)) {
                            List<ADItem> adItems = JsonUtils.jsonToList(json, ADItem.class);
                            return adItems;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    TbContentExample example = new TbContentExample();

                    Criteria createCriteria = example.createCriteria();

                    createCriteria.andCategoryIdEqualTo(categoryId);

                    List<TbContent> contentList = contentMapper.selectByExample(example);

                    //创建List<ADItem>集合封装轮播图所需的数据
                    List<ADItem> itemList = new ArrayList<ADItem>();

                    for (TbContent content : contentList) {
                        ADItem ad = new ADItem();
                        //图片描述信息
                        ad.setAlt(content.getSubTitle());
                        //设置图片地址
                        ad.setSrc(content.getPic());
                        ad.setSrcB(content.getPic2());
                        //设置图片购买地址
                        ad.setHref(content.getUrl());
                        //设置图片的宽、高
                        ad.setWidth(WIDTH);
                        ad.setWidthB(WIDTHB);
                        ad.setHeight(HEIGTH);
                        ad.setHeightB(HEIGTHB);

                        itemList.add(ad);
                    }

                    //如果缓存中没有数据就去查询数据库，并且把查到的数据存入缓存,以便于下次从缓存中查询
                    jedisDao.hset(AD_CHACHE, categoryId.toString(), JsonUtils.objectToJson(itemList));

                    return itemList;
                }

		5.2、集群版
			集群版只需要修改
			applicationContext-redis.xml配置文件和JedisDaoImpl.java DAO接口类就行，其他不需要修改

			5.2.1、applicationContext-redis.xml
			    <?xml version="1.0" encoding="UTF-8"?>
			    <beans xmlns="http://www.springframework.org/schema/beans"
                    xmlns:context="http://www.springframework.org/schema/context"
                    xmlns:mvc="http://www.springframework.org/schema/mvc"
                    xmlns:aop="http://www.springframework.org/schema/aop"
                    xmlns:tx="http://www.springframework.org/schema/tx"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
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

                    <!--使用单机版的jedis连接池连接redis 把JedisPoolConfig对象交给spring创建 -->
                    <bean id="poolConfig" class="redis.clients.jedis.JedisPoolConfig">
                        <property name="maxIdle" value="20"></property>
                        <property name="maxTotal" value="1000"></property>
                    </bean>
                    <!-- 使用spring整合集群版的jedis连接池连接redis集群 -->
                    <bean class="redis.clients.jedis.JedisCluster">
                        <constructor-arg name="nodes">
                            <set>
                                <bean class="redis.clients.jedis.HostAndPort">
                                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                                    <constructor-arg name="port" value="7001"></constructor-arg>
                                </bean>
                                <bean class="redis.clients.jedis.HostAndPort">
                                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                                    <constructor-arg name="port" value="7002"></constructor-arg>
                                </bean>
                                <bean class="redis.clients.jedis.HostAndPort">
                                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                                    <constructor-arg name="port" value="7003"></constructor-arg>
                                </bean>
                                <bean class="redis.clients.jedis.HostAndPort">
                                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                                    <constructor-arg name="port" value="7004"></constructor-arg>
                                </bean>
                                <bean class="redis.clients.jedis.HostAndPort">
                                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                                    <constructor-arg name="port" value="7005"></constructor-arg>
                                </bean>
                                <bean class="redis.clients.jedis.HostAndPort">
                                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                                    <constructor-arg name="port" value="7006"></constructor-arg>
                                </bean>
                                <bean class="redis.clients.jedis.HostAndPort">
                                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                                    <constructor-arg name="port" value="7007"></constructor-arg>
                                </bean>
                                <bean class="redis.clients.jedis.HostAndPort">
                                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                                    <constructor-arg name="port" value="7008"></constructor-arg>
                                </bean>
                            </set>
                        </constructor-arg>
                        <constructor-arg name="poolConfig" ref="poolConfig"></constructor-arg>
                    </bean>
			    </beans>

			5.2.2、JedisDaoImpl.java
				@Repository
				public class JedisDaoImpl implements JedisDao {

					//**************集群版*********************
					@Resource
					private JedisCluster jedisCluster;

					@Override
					public String set(String key, String value) {
						String set = jedisCluster.set(key, value);
						return set;
					}

					@Override
					public String get(String key) {
						String get = jedisCluster.get(key);
						return get;
					}

					@Override
					public Long incr(String key) {
						Long incr = jedisCluster.incr(key);
						return incr;
					}

					@Override
					public Long decr(String key) {
						Long decr = jedisCluster.decr(key);
						return decr;
					}

					@Override
					public Long hset(String key, String field, String value) {
						Long hset = jedisCluster.hset(key, field, value);
						return hset;
					}

					@Override
					public String hget(String key, String field) {
						String hget = jedisCluster.hget(key, field);
						return hget;
					}

					@Override
					public Long hdel(String key, String field) {
						Long hdel = jedisCluster.hdel(key, field);
						return hdel;
					}

					@Override
					public Long expire(String key, int seconds) {
						Long expire = jedisCluster.expire(key, seconds);
						return expire;
					}

					@Override
					public Long ttl(String key) {
						Long ttl = jedisCluster.ttl(key);
						return ttl;
					}
				}

第五、前台系统
十七、前台系统--搭建搜索系统
    架构图 见图1

    1、搭建
		1.1、13taotao-search-web
			在parent工程上右键创建maven-project工程
			见图1

		1.2、14taotao-search
			在parent工程上右键创建maven-project工程管理搜索工程的接口和服务
			见图2

		1.3、15taotao-search-interface
			在聚合工程search工程上右键创建mavne-module工程
			见图3

		1.4、16taotao-search-service
			在聚合工程search工程上右键创建mavne-module工程
			见图4

    2、整合
		2.1、search-web工程
			2.1.1、pom.xml
				<!--
					需要的依赖：
						1、加载springmvc的配置文件：spring的jar包
						2、表现层：jsp/jstl/servlet
						3、引入服务：dubbo、zookeper
						4、调用search工程接口：search-interface依赖
						5、启动服务：tomcat依赖-8085
					参考：
						portal-web工程

					区别：
						1、引入15taotao-search-interface工程依赖
						2、改tomcat端口 8085
					其他和portal-web工程的pom.xml文件内容一样
				-->

			2.1.2、配置文件
				log4j.properties
				resource.properties
				springmvc.xml

				注意：
					log4j的配置文件一样
					resource配置文件中的内容先删除，后面用到载添加
					springmvc配置文件除了扫描的包和引用的服务，其他都一样

			2.1.3、webapp
				将portal-web工程的下的webapp目录复制到search-web工程中(将原有的替换)

				删除不必要的jsp页面，只留search.jsp和success.jsp，其他都一样

		2.2、search工程
			pom.xml

			和content工程一样，只需要修改tomcat端口 8086即可

		2.3、search-interface工程
			pom.xml
			<!--
				所需坐标：
					pojo
				参考：
					manager-interface

				和content-interface工程的pom.xml一样
			-->

		2.4、search-service工程
			2.4.1、pom.xml
				<!--
					需要的依赖
					1、加载spring的配置文件：spring的依赖
					2、连接dao：manager-dao工程的依赖
					3、发布服务：dubbo、zookeeper
					4、搜索接口：search-service

					除了15taotao-search-interface依赖以外其他都一样
				-->

			2.4.2、配置文件
				applicationContext-dao.xml
				applicationContext-service.xml
				jdbc.properties
				log4j.properties
				resource.properties
				sqlMapConfig.xml
		
				删除sqlMapConfig和resource配置文件中无用的配置
		
				修改applicationContext-service配置文件
					修改dubbo的对外暴露端口：<dubbo:protocol name="dubbo" port="20882" />
					删除没有的dubbo服务
					注意切面配置的包和扫描的包

			2.4.3、web.xml
				将content-service工程下的web.xml文件复制到search-service工程的WEB-INF下，替换原来的

		2.5、测试
			2.5.1、jsp页面分析
				在portal-web工程中的index.jsp的31行引入了<jsp:include page="commons/header.jsp" />
		
				在header.jsp的17行有一个点击事件onclick="search('key');return false;"
		
				在header.jsp的3行引入了一个base-v1.js
		
				在base-v1.js的33行执行的点击事件：
					function search(a) {
						var b = "http://localhost:8085/search.html?q=" + encodeURIComponent(document.getElementById(a).value);
						return window.location.href = b;
					}
				此时点击搜索将跳转到search-web(8085)工程，加载WEB-INF/jsp/search.jsp
		
				search.jsp的22行页面引入了<jsp:include page="commons/header.jsp" />页面
		
				在header.jsp的17行有一个点击事件onclick="search('key');return false;"
		
				在header.jsp的3行引入了一个base-v1.js
		
				在base-v1.js的33行执行的点击事件：
					function search(a) {
						var b = "http://localhost:8085/search.html?q=" + encodeURIComponent(document.getElementById(a).value);
						return window.location.href = b;
					}
				在search-web工程中点击搜索将会在本页面进行搜索

			2.5.2、java
				在search-web工程中创建study.project包，在此包下创建页面跳转的controller

				@Controller
				public class SearchItemController {

					@RequestMapping("/search")
					public String searchItem(){
					  return "search";
					}
				}

			2.5.3、启动服务
				10taotao-content  端口：8084
				
				09taotao-portal-web  端口：8083
				
				13taotao-search-web  端口：8085
				
				16taotao-search  端口：8086

十七、前台系统-搭建solr索引库
	1、准备环境
		sorl是一个web服务，需要一个tomcat服务

	2、安装solr
		第一步：导入安装包/usr/local/hadoop/canglang/tar
			rz命令
			安装包目录：E:\JAVA\ReStudy\DataWord\Tools\linux\linux系统上的安装包\安装包\solr

		第二步：复制
			复制安装包tomcat和solr到/usr/local/hadoop/canglang/solr目录
			解压两个文件：
			  tar -zxvf apache-tomcat-7.0.61.tar.gz
			  tar -zxvf solr-4.10.3.tgz.tgz

		第三步：复制war到tomcat
			进入：cd solr-4.10.3/example/webapps/

			复制：cp solr.war ../../../tomcat-solr/webapps/

			进入：cd ../../../tomcat-solr/webapps/

			解压：unzip -oq solr.war -d solr

		第四步：复制solr必须的日志jar
			进入：cd /usr/local/hadoop/canglang/solr/solr-4.10.3/example/lib/ext

			复制：cp * ../../../../tomcat-solr/webapps/solr/WEB-INF/lib/

		第五步：导入log4j配置文件
			进入：../../../../tomcat-solr/webapps/solr/WEB-INF/

			创建classes目录：mkdir classes

			导入log4j.properties配置文件(随便找一个项目导入一个配置文件就行)

		第六步：创建solr的索引库
			进入：cd /usr/local/hadoop/canglang/solr/

			创建solr索引库目录：mkdir solrhome

			进入：cd solr-4.10.3/example/

			复制solr解压目录下自带的索引库到自定义的索引库中：cp -r solr ../../solrhome/

			查看：/usr/local/hadoop/canglang/solr/solrhome

		第七步：tomcat加载solr索引库
			进入：cd tomcat-solr/bin/

			编辑tomcat/bin/catalina.sh配置文件指定solr索引库路径
				vim catalina.sh

				添加内容：
					export JAVA_OPTS="-Dsolr.solr.home=/usr/local/hadoop/canglang/solr/solrhome/solr/"

		第八步：复制solr解压目录下的solr类库到solr索引库中
			复制：
                cp -r solr-4.10.3/contrib solrhome/solr/
                cp -r solr-4.10.3/dist/ solrhome/solr/

			查看：cd solrhome/solr/

		第九步：solrconfig.xml配置文件
			进入：/usr/local/hadoop/canglang/solr/solrhome/solr/collection1/conf

			编辑：vim solrconfig.xml

			内容：将../../..改成..
				<lib dir="${solr.install.dir:../../..}/contrib/extraction/lib" regex=".*\.jar" />
				<lib dir="${solr.install.dir:../../..}/dist/" regex="solr-cell-\d.*\.jar" />

				<lib dir="${solr.install.dir:../../..}/contrib/clustering/lib/" regex=".*\.jar" />
				<lib dir="${solr.install.dir:../../..}/dist/" regex="solr-clustering-\d.*\.jar" />

				<lib dir="${solr.install.dir:../../..}/contrib/langid/lib/" regex=".*\.jar" />
				<lib dir="${solr.install.dir:../../..}/dist/" regex="solr-langid-\d.*\.jar" />

				<lib dir="${solr.install.dir:../../..}/contrib/velocity/lib" regex=".*\.jar" />
				<lib dir="${solr.install.dir:../../..}/dist/" regex="solr-velocity-\d.*\.jar" />

	3、启动
		可能和dubbo服务的端口冲突，所以需要修改端口

		执行：sh tomcat-solr/bin/startup.sh

		监控日志：tail -f tomcat-solr/logs/catalina.out

		访问：http://192.168.254.66:8081/solr

		见图1

	4、安装IK分词器
		第一步、装备环境
			在linux系统的solr的tomcat中导入IK分词器的jar包：在linux安装包的IK下：IKAnalyzer2012FF_u1.jar
			
			将jar导入到：cd /usr/local/hadoop/canglang/tar
			
			复制：cp IKAnalyzer2012FF_u1.jar  ../solr/tomcat-solr/webapps/solr/WEB-INF/lib/
			
			查看是否复制成功：cd ../solr/tomcat-solr/webapps/solr/WEB-INF/lib/
			
		第二步：导入配置文件
			配置文件在linux安装包的IK/dic下
			
			注意：ext.dic和stopword.dic配置文件的内容必须是无BOM的UTF-8格式
			
			ext.dic
			IKAnalyzer.cfg.xml
			stopword.dic
		
		第三步：配置solr使用IK分词器
			类型 和 域在/usr/local/hadoop/canglang/solr/solrhome/solr/collection1/conf/schema.xml配置文件中配置
		
			进入：/usr/local/hadoop/canglang/solr/solrhome/solr/collection1/conf
			
			编辑：vim schema.xml
			
			定义使用IK分词器类型
				在配置文件：scheme.xml配置文件的312行定义：域类型：FieldType，域类型定义使用何种分词器
				
				<fieldType name="text_ik" class="solr.TextField">
						<analyzer class="org.wltea.analyzer.lucene.IKAnalyzer"/>
				</fieldType>

			定义业务域
				定义业务域，业务域使用Ik分词器
				
				<field name="item_title" type="text_ik" indexed="true" stored="true"/>
				<field name="item_sell_point" type="text_ik" indexed="true" stored="true"/>
				<field name="item_price"  type="long" indexed="true" stored="true"/>
				<field name="item_image" type="string" indexed="false" stored="true" />
				<field name="item_category_name" type="string" indexed="true" stored="true" />
				<field name="item_desc" type="text_ik" indexed="true" stored="false" />
				<field name="item_keywords" type="text_ik" indexed="true" stored="false" multiValued="true"/>
				
				<copyField source="item_title" dest="item_keywords"/>
				<copyField source="item_sell_point" dest="item_keywords"/>
				<copyField source="item_category_name" dest="item_keywords"/>
				<copyField source="item_desc" dest="item_keywords"/>

	5、重启solr的tomcat
		
		结束进程：sh /usr/local/hadoop/canglang/solr/tomcat-solr/bin/shutdown.sh
		
		重启进程：sh /usr/local/hadoop/canglang/solr/tomcat-solr/bin/startup.sh
		
		查看进程：tail -f ../../../../tomcat-solr/logs/catalina.out
			
	6、测试
	
		见图2
			
			
十九、前台系统--导入数据库数据到索引库(11)
	1、分析
		业务域需要从3张表中获取数据，tb_item（商品表），tb_item_cat(商品类别)，tb_item_desc(商品描述)
		
		1.1、查询3张表数据导入索引库
		
		1.2、查询数据映射pojo。
		
		1.3、把查询列表集合使用solrServer对象直接写入索引库。
		
	2、环境准备
		
		2.1、创建导入按钮
			索引库的维护是由后台管理人员维护的，所以要在后台管理系统中添加一个导入数据库数据到索引库的入口
			
			在manager-web工程的index.html的32行“网站内容管理”内添加一个“索引库维护”按钮
			
			<ul>
				<li data-options="attributes:{'url':'content-category'}">内容分类管理</li>
				<li data-options="attributes:{'url':'content'}">内容管理</li>
				<li data-options="attributes:{'url':'index-solr-manager'}">索引库维护</li>
			</ul>
				
		2.3、创建索引库维护的页面
			在manager-web工程的jsp目录下创建index-solr-manager.jsp
			
			<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
			<script type="text/javascript">
				function dataImport(){
					$.ajax({
						//导入数据库数据到索引库的请求
						type:"POST",
						url:"${pageContext.request.contextPath}/dataImport",
						success:function(data){
							if(data.status=200){
								$.messager.alert('提示','成功导入索引库');
							} else {
								$.messager.alert('提示','导入失败，请再次重试');
							}
						}
					});
				};
			</script>

			<div style="padding:5px">
				<a href="javascript:void(0)" class="easyui-linkbutton" onclick="dataImport()">导入数据库数据</a>
			</div>

		2.4、使用sql语句测试查询
			SELECT  
				a.id,
				a.title,
				a.sell_point,
				a.price,
				a.image,
				b.name catelog_name,
				c.item_desc
			FROM 
				tb_item a, 
				tb_item_cat b, 
				tb_item_desc c
			WHERE
				a.cid=b.id
				AND a.id=c.item_id
				AND a.status=1;
				
		2.5、编写dao接口和配置文件
			因为是搜索模块的功能所以要在search-interface工程中定义
			
			创建study.project.search.mapper包
			
			第一步：创建返回的pojo
				创建study.project.search.pojo包
				public class SearchItem implements Serializable{

					private Long id;
					private String title;
					private String sell_point;
					private Float price;
					private String image;
					private String catelog_name;
					private String item_desc;
				
				}
			
			第二步：创建接口的配置文件
				在此包下创建SearchItemMapper.xml配置文件
				
				<?xml version="1.0" encoding="UTF-8" ?>
				<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "
					http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
					
				<!--  
					mybatis接口开发规范：
						1、映射文件的namespace必须是接口的全类路径名称
						2、映射文件的sql查询id必须和接口的方法名称一致
						3、映射配置文件必须和接口在同一个包下，且名称相同
				-->
				<mapper namespace="study.project.search.mapper.SearchItemMapper" >
					<!-- resultType查询的列名称必须和javabean中的属性名称一一对应，且一样 -->
					<select id="dataImport" resultType="study.project.search.pojo.SearchItem" parameterType="java.lang.Long">
						SELECT  
							a.id,
							a.title,
							a.sell_point,
							a.price,
							a.image,
							b.name catelog_name,
							c.item_desc
						FROM 
							tb_item a, 
							tb_item_cat b, 
							tb_item_desc c
						WHERE
							a.cid=b.id
							AND a.id=c.item_id
							AND a.status=1;
					</select>
				</mapper>
			
			第三步：创建接口
				在此包下创建SearchItemMapper.java接口
				
				public interface SearchItemMapper {

					//导入数据库数据到索引库
					public List<SearchItem> dataImport();
				}

			第四步：扫描此接口
				在search-service工程下的applicationContext-dao.xml配置文件中添加扫描
				
				<!-- 接口代理开发，扫描接口 -->
				<!-- 
					1.接口名称和映射文件名称相同，且在同一个目录下
					2.映射文件namespace名称必须是接口的全类路径名
					3.映射文件sql语句的字段Id必须是接口方法名
				 -->
				 <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
					<property name="basePackage" value="study.project.mapper,study.project.search.mapper"></property>
					<property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"></property>
				 </bean>
				
			第五步：加载配置文件
				修改search-interface工程的pom.xml配置文件
				
				<!-- 加载接口的配置文件 -->
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
	3、代码实现
		3.1、导入solr依赖
			在search-service工程中的pom.xml中导入solr依赖
			
			<!-- solr客户端 -->
			<dependency>
				<groupId>org.apache.solr</groupId>
				<artifactId>solr-solrj</artifactId>
				<version>${solrj.version}</version>
			</dependency>
		
		3.2、创建solr的配置文件
			在search-service工程中创建applicationContext-solr.xml配置文件
			<?xml version="1.0" encoding="UTF-8"?>
			<beans xmlns="http://www.springframework.org/schema/beans"
				xmlns:context="http://www.springframework.org/schema/context" 
				xmlns:mvc="http://www.springframework.org/schema/mvc"
				xmlns:aop="http://www.springframework.org/schema/aop" 
				xmlns:tx="http://www.springframework.org/schema/tx"
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
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
				<bean class="org.apache.solr.client.solrj.impl.HttpSolrServer">
					<property name="baseURL" value="http://192.168.254.66:8081/solr"></property>
				</bean>
			</beans>

		3.3、interface
			在search-interface工程下创建study.project.search.service包
			public interface SearchItemService {

				//导入数据库数据到索引库的接口
				public List<SearchItem> dataImport();
			}
			
		3.4、service
			在search-service工程中创建study.project.search.service.impl包
			
			@Service
			public class SearchItemServiceImpl implements SearchItemService{

				@Resource
				private SearchItemMapper searchItemMapper;
				
				@Resource
				private SolrServer solrServer;
				
				/**
				 * 功能11：
				 * 		导入数据库数据到索引库中
				 * 业务分析：
				 * 		查询数据库，把数据映射到索引库的字段中
				 * 遍历数据库查询的集合设置到文档对象中
				 * 使用solrService.add(doc)
				 */
				public ProjectResultDTO dataImport() {
					//查询数据库数据
					List<SearchItem> searchItems = searchItemMapper.dataImport();

					//循环集合，把数据放入Document文档对象中
					for (SearchItem searchItem : searchItems) {
						//创建文档对象
						SolrInputDocument doc = new SolrInputDocument();
						//设置文档id
						doc.addField("id", searchItem.getId());
						//设置商品标题
						doc.addField("item_title", searchItem.getTitle());
						//设置商品卖点
						doc.addField("item_sell_point", searchItem.getSell_point());
						//设置商品图片地址
						doc.addField("item_image", searchItem.getImage());
						//设置商品类别
						doc.addField("item_category_name", searchItem.getCatelog_name());
						//设置商品价格
						doc.addField("item_price", searchItem.getPrice());
						//设置商品描述
						doc.addField("item_desc", searchItem.getItem_desc());
						
						try {
							solrServer.add(doc);
						} catch (Exception ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						}
					}
					
					try {
						solrServer.commit();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					return ProjectResultDTO.ok();
				}
			}
			
		3.5、发布服务
			在search-service工程下发布服务
			
			<!-- 创建需要发布对象(包含根据id查询商品信息、分页查询商品列表)-->
			<bean id="searchItemServiceImpl" class="study.project.search.service.impl.SearchItemServiceImpl"></bean>
			<!-- 发布服务 -->
			<dubbo:service ref="searchItemServiceImpl" interface="study.project.search.service.SearchItemService" version="1.0.0" timeout="10000"/>
			
		3.6、引用服务
			在manager-web工程的springmvc.xml配置文件中引入dubbo服务
			
			<!-- 引用服务(y引用solr索引库服务) -->
			<dubbo:reference id="searchItemService" interface="study.project.search.service.SearchItemService" version="1.0.0" timeout="5000"/>

		3.7、引用依赖
			在manager-web工程中引入search-service工程的依赖
			<dependency>
				<groupId>com.taotao</groupId>
				<artifactId>16taotao-search-service</artifactId>
				<version>0.0.1-SNAPSHOT</version>
			</dependency>
			
		3.8、controller
			索引库的维护硬挨属于后台管理人员的维护，所以要在manager-web工程中操作
			
			@Controller
			public class SearchSolrController {

				@Resource
				private SearchItemService searchItemService;
				
				/**
				 * 功能11：
				 * 		导入数据库中的数据到索引库
				 * 请求：/dataImport
				 * 参数：无
				 * 返回值：json格式的ProjectResultDTO
				 * @return
				 */
				@RequestMapping("/dataImport")
				@ResponseBody
				public ProjectResultDTO dataImport(){
					ProjectResultDTO result = searchItemService.dataImport();
					return result;
				}
			}
			
			
二十、前台系统--实现搜索功能(12)			
	1、创建pojo
		在search-interface工程的pojo包中创建搜索结果包装类SearchResult.java
		
		/**
		 * 查询索引库时的包装类对象
		 * @author canglang
		 */
		public class SearchResult implements Serializable {
			//总记录数
			private Long totalRecord;
			//查询分页列表数据
			private List<SearchItem> itemList;
			//当前页码
			private Integer curPage;
			//总页数
			private Integer pages;
		}
		
	2、导入依赖
		把search-service工程中的solr依赖剪切到search-interface工程中，因为search-service工程间接的依赖search-interface工程
		
		<!-- solr客户端 -->
		<dependency>
			<groupId>org.apache.solr</groupId>
			<artifactId>solr-solrj</artifactId>
			<version>${solrj.version}</version>
		</dependency>
	
	3、创建dao层接口		
		在search-interface工程的dao包中创建搜索solr索引库的dao接口
		
		public interface SearchItemDao {

			/**
			 * 功能12：
			 * 		查询索引库中的商品列表
			 * 搜索工程的搜索功能，查询solr中的数据
			 */
			public SearchResult findItemsBySolr(SolrQuery solrQuery);
		}

	4、创建dao接口的实现类
		在search-service工程的study.project.searchsolr.impl包下创建SearchItemDaoImpl implements SearchItemDao

		@Repository
		public class SearchItemDaoImpl implements SearchItemDao {

			@Resource
			private SolrServer solrServer;
			
			public SearchResult findItemsBySolr(SolrQuery solrQuery) {
				
				//创建SearchResult对象封装返回结果集
				SearchResult searchResult = new SearchResult();
				
				try {
					//查询索引库
					QueryResponse query = solrServer.query(solrQuery);
					//获取文档对象
					SolrDocumentList solrDocumentList = query.getResults();
					//设置总记录数
					long totalNum = solrDocumentList.getNumFound();
					searchResult.setTotalRecord(totalNum);

					//创建List<SearchItem>封装文档对象
					List<SearchItem> list = new ArrayList<SearchItem>();
					
					for (SolrDocument solrDoc : solrDocumentList) {
						
						SearchItem item = new SearchItem();
						
						//设置id
						String id = (String) solrDoc.get("id");
						item.setId(Long.parseLong(id));
						//设置商品标题
						String item_title = (String) solrDoc.get("item_title");
						//获取高亮
						Map<String, Map<String, List<String>>> highlighting = query.getHighlighting();
						Map<String, List<String>> map = highlighting.get(id);
						List<String> list2 = map.get("item_title");
						
						if (list2!=null && list2.size() > 0) {
							item_title = list2.get(0);
						}
						
						item.setTitle(item_title);
						//设置商品价格
						Long item_price = (Long) solrDoc.get("item_price");
						item.setPrice(item_price);
						//设置商品图片
						String item_image = (String) solrDoc.get("item_image");
						item.setImage(item_image);
						//设置商品描述
						String item_desc = (String) solrDoc.get("item_desc");
						item.setItem_desc(item_desc);
						//设置商品分类名称
						String item_category_name = (String) solrDoc.get("item_category_name");
						item.setCatelog_name(item_category_name);
						//设置商品卖点
						String item_sell_point = (String) solrDoc.get("item_sell_point");
						item.setSell_point(item_sell_point);
						
						list.add(item);
					}
					//封装到结果集对象中
					searchResult.setItemList(list);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				return searchResult;
			}
		}

	5、创建service层接口
		在search-interface工程下的study.project.search.service包下的SearchItemService.java中添加
		
		/**
		 * 功能12：
		 * 		搜索-查询索引库
		 * @param solrQuery
		 * @return
		 */
		public SearchResult findItemsBySolr(String keyWorld, Integer page, Integer rows);
		
	6、创建service层接口的实现类
		在search-service工程下的study.project.search.service.impl包下的SearchItemServiceImpl.java类中
		
		@Resource
		private SearchItemDao searchItemDao;
		
		/**
		 * 功能12：
		 * 		搜索-查询索引库
		 * @param solrQuery
		 * @return
		 */
		public SearchResult findItemsBySolr(String keyWorld, Integer page, Integer rows) {
			//创建SolrQuery封装查询结果
			SolrQuery solrQuery = new SolrQuery();
			
			//判断是否有关键字
			if (StringUtils.isNotBlank(keyWorld)) {
				solrQuery.setQuery(keyWorld);
			} else {
				solrQuery.setQuery("*.*");
			}
			//设置分页信息
			solrQuery.setStart((page-1)*rows);
			solrQuery.setRows(rows);
			
			//设置高亮信息
			solrQuery.setHighlight(true);//开启高亮
			solrQuery.addHighlightField("item_title");//设置那一个字段高亮显示
			solrQuery.setHighlightSimplePre("<font class=\"skcolor_1jg\">");//设置高亮前缀(ljg是Ljg不是1jg)
			solrQuery.setHighlightSimplePost("</font>");//设置高亮后缀
			
			//设置默认查询字段
			solrQuery.set("df", "item_keywords");
			
			//查询
			SearchResult result = searchItemDao.findItemsBySolr(solrQuery);
			
			//计算页码
			Integer totalRecord = result.getTotalRecord().intValue();
			int pages = totalRecord/rows;
			
			if (totalRecord%rows > 0) {
				pages++;
			}
			
			result.setCurPage(page);//当前页
			result.setPages(pages);//总页数
			
			return result;
		}
	7、配置文件
		在search-web工程下的springmvc.xml工程中引用服务
	
		<!-- 引用服务(y引用solr索引库服务) -->
		<dubbo:reference id="searchItemService" interface="study.project.search.service.SearchItemService" version="1.0.0" timeout="5000"/>
    
	8、controller
		在search-web工程下的study.project.controller包下的SearchItemController.java类中的searchItem方法中完善
		
		@Controller
		public class SearchItemController {

			@Resource
			private SearchItemService searchSolrItemService;
			
			@RequestMapping("/search")
			public String searchItem(@RequestParam(value = "q") String keyWorld, 
					@RequestParam(defaultValue="1") Integer page, 
					@RequestParam(defaultValue="30") Integer rows, Model model) throws Exception{
				
				//解决中文乱码
				keyWorld = new String(keyWorld.getBytes("ISO8859-1"), "UTF-8");
				
				SearchResult result = searchSolrItemService.findItemsBySolr(keyWorld, page, rows);
				
				//回显参数
				model.addAttribute("query", keyWorld);
				//回显列表
				model.addAttribute("itemList", result.getItemList());
				//回显当前页
				model.addAttribute("page", result.getCurPage());
				//回显总记录数
				model.addAttribute("totalPages", result.getPages());
				
				return "search";
			}
		}

	9、实现页码的点击
		早search-web工程的search.jsp的71行引用了/js/search_main.js
		
		在search_main.js中搜索8082改成8085即可
		
二十一、solrCloud简介及搭建

	Solr集群参考：参考资料/solr/solrCloud.doc

	1、solrCloud简介
        SolrCloud(solr 云)是Solr提供的分布式搜索方案，

        当你需要大规模，容错，分布式索引和检索能力时使用 SolrCloud。

        当一个系统的索引数据量少的时候是不需要使用SolrCloud的，

        当索引量很大，搜索请求并发很高，这时需要使用SolrCloud来满足这些需求。

        SolrCloud是基于Solr和Zookeeper的分布式搜索方案，它的主要思想是使用Zookeeper作为集群的配置信息中心。

        它有几个特色功能：
            1）集中式的配置信息zookeeper
            
            2）自动容错
            
            3）近实时搜索
            
            4）查询时自动负载均衡

	2、SolrCloud结构
	
        SolrCloud为了降低单机的处理压力，需要由多台服务器共同来完成索引和搜索任务。

        实现的思路是将索引数据进行Shard（分片）拆分，每个分片由多台的服务器共同完成，

        当一个索引或搜索请求过来时会分别从不同的Shard的服务器中操作索引。

        SolrCloud需要Solr基于Zookeeper部署，Zookeeper是一个集群管理软件，

        由于SolrCloud需要由多台服务器组成，由zookeeper来进行协调管理。

        见图1

        详解：
            2.1、物理结构
                三个Solr实例（ 每个实例包括两个Core），组成一个SolrCloud。
                
            2.2、逻辑结构
                索引集合包括两个Shard（shard1和shard2），
                
                shard1和shard2分别由三个Core组成，其中一个Leader两个Replication，
                
                Leader是由zookeeper选举产生，zookeeper控制每个shard上三个Core的索引数据一致，解决高可用问题。
                
                用户发起索引请求分别从shard1和shard2上获取，解决高并发问题。

                2.3.1、collection
                    Collection在SolrCloud集群中是一个逻辑意义上的完整的索引结构。
                    
                    它常常被划分为一个或多个Shard（分片），它们使用相同的配置信息。
                    
                    比如：
                        针对商品信息搜索可以创建一个collection。
                        
                        collection=shard1+shard2+....+shardX

                2.3.2、Core
                    每个Core是Solr中一个独立运行单位，提供 索引和搜索服务。
                    
                    一个shard需要由一个Core或多个Core组成。
                    
                    由于collection由多个shard组成所以collection一般由多个core组成。

                2.3.3、Master或Slave
                    Master是master-slave结构中的主结点（通常说主服务器），
                    
                    Slave是master-slave结构中的从结点（通常说从服务器或备服务器）。
                    
                    同一个Shard下master和slave存储的数据是一致的，这是为了达到高可用目的。

                2.3.4、Shard
                    Collection的逻辑分片。
                    
                    每个Shard被化成一个或者多个replication，通过选举确定哪个是Leader。


        注意：
            本次安装是单机版的安装，所以采用伪集群的方式进行安装，如果是真正的生成环境，将伪集群的ip改下就可以了，步骤是一样的。

            见图2

	3、Zookeeper集群搭建
		第一步：创建zookeeper集群目录
        在/home/canglang/solr/目录下创建solrcluster目录用于存放zookeeper集群和solr集群

		第二步：导入tar
			在刚才创建的solrcluster目录下导入zookeeper的tar包

			解压zookeeper：tar -zxvf zookeeper-3.4.6.tar.gz

			重命名zookeeper：mv zookeeper-3.4.6 zkCluster-2182

		第三步：重命名zookeeper配置文件
			注意：
				需要修改每一个zookeeper服务的配置文件，这里先修改一个服务的配置文件然后再复制三份服务

			进入：cd zkCluster-2182/conf/

			重命名：mv zoo_sample.cfg zoo.cfg

		第四步：复制三份zookeeper服务 
			进入zookeeper集群根目录：cd ../../

			复制：
				cp -r zkCluster-2182 zkCluster-2183
				cp -r zkCluster-2182 zkCluster-2184 

		第五步：修改zookeeper端口
			进入：cd zkCluster-2182/conf/

			编辑：vim zoo.cfg

			修改端口(14行)：clientPort=2182


			进入：cd zkCluster-2183/conf/

			编辑：vim zoo.cfg

			修改端口(14行)：clientPort=2183


			进入：cd zkCluster-2184/conf/

			编辑：vim zoo.cfg

			修改端口(14行)：clientPort=2184

		第六步：修改dataDir数据存储目录
			在每一台zookeeper服务的目录下创建data和log目录


			进入：cd zkCluster-2182
			
			创建目录：mkdir data log
			
			进入：cd conf
			
			编辑：vim zoo.cfg
				dataDir=/home/canglang/solr/solrcluster/zkCluster-2182/data
				dataLogDir=/home/canglang/solr/solrcluster/zkCluster-2182/log 


			进入：cd ../zkCluster-2183
			
			创建目录：mkdir data log
			
			进入：cd conf
			
			编辑：vim zoo.cfg
				dataDir=/home/canglang/solr/solrcluster/zkCluster-2183/data
				dataLogDir=/home/canglang/solr/solrcluster/zkCluster-2183/log 


			进入：cd ../zkCluster-2184
			
			创建目录：mkdir data log 
			
			进入：cd conf
			
			编辑：vim zoo.cfg
				dataDir=/home/canglang/solr/solrcluster/zkCluster-2184/data
				dataLogDir=/home/canglang/solr/solrcluster/zkCluster-2184/log 

		第七步：配置心跳检测
			每一台zookeeper服务的zoo.cfg配置文件(最后一行下面)中都需要配置

			内容：
				server.1=192.168.254.66:2881:3881
				server.2=192.168.254.66:2882:3882
				server.3=192.168.254.66:2883:3883
				
			见图1

			详解：
				server.A=B：C：D：
				A 是一个数字，表示这个是第几号服务器；
				B 是这个服务器的 ip 地址；
				C 表示的是这个服务器与集群中的 Leader（领袖服务器）服务器交换信息的端口；
				D 表示的是万一集群中的 Leader 服务器挂了，需要一个端口来重新进行选举，选出一个新的 Leader，而这个端口就是用来执行选举时服务器相互通信的端口。
				如果是伪集群的配置方式，由于 B 都是一样，所以不同的 Zookeeper 实例通信端口号不能一样，所以要给它们分配不同的端口号

		第八步：创建选举id
			注意：
			   * 在每一台Zookeeper的data目录创文件：myid(文件名必须叫myid)

				在myid填入内容：心跳检测选举Id。
				zkCluster-2182（myid=1、zkCluster-2183（myid = 2）、zkCluster-2184(myid=3)


			进入：cd solr/solrcluster/zkCluster-2182/data 
			
			创建文件：touch myid
			
			编辑文件：vim myid
			
			内容：1(服务器编号，跟刚才配置的心跳检测的A一致就行)


			进入：cd solr/solrcluster/zkCluster-2183/data 

			创建文件：touch myid

			编辑文件：vim myid

			内容：2(服务器编号，跟刚才配置的心跳检测的A一致就行)


			进入：cd solr/solrcluster/zkCluster-2184/data 

			创建文件：touch myid

			编辑文件：vim myid

			内容：3(服务器编号，跟刚才配置的心跳检测的A一致就行)

		第九步：测试集群
			1、启动集群
				sh zkCluster-2182/bin/zkServer.sh start 
				
				sh zkCluster-2183/bin/zkServer.sh start 
				
				sh zkCluster-2184/bin/zkServer.sh start 

			2、查看集群状态
				[root@localhost zk-cluster]# sh zkCluster-2182/bin/zkServer.sh status
				JMX enabled by default
				Using config: /home/canglang/solr/solrcluster/zkCluster-2182/bin/../conf/zoo.cfg
				Mode: follower//随从服务器

				[root@localhost zk-cluster]# sh zkCluster-2183/bin/zkServer.sh status
				JMX enabled by default
				Using config: /home/canglang/solr/solrcluster/zkCluster-2183/bin/../conf/zoo.cfg
				Mode: leader//领袖服务

				[root@localhost zk-cluster]# sh zkCluster-2184/bin/zkServer.sh status
				JMX enabled by default
				Using config: /home/canglang/solr/solrcluster/zkCluster-2184/bin/../conf/zoo.cfg
				Mode: follower

		第十：集群选主原理
			1）Zookeeper1启动首先给自己投票，选举自己为leader
			
			2）Zookeeper2启动，首先给自己投票，选举自己为leader
			
			3）Zookeeper1和Zookeeper2集群通信，一个集群只能有一个leader
			
			4）Zookeeper1和Zookeeper2比myid大小，谁大谁就是leader
			
			5）Zookeeper2的myid=2 大于 Zookeeper1的myid=1，Zookeeper1改变投票Zookeeper2
			
			6）此时Zookeeper2有2票。
			
			7）启动Zookeeper3，Zookeeper3检测集群，发送Zookeeper2已经超过半数投票，Zookeeper3遵循少数服从多数原理

			8）zookeeper集群必须是奇数台
			
			9）zookeeper集群选主的跟启动顺序有关系

	4、搭建SolrCloud集群
		第一步：创建4台solr集群服务
			拷贝之前部署好的单机版solr服务到solr/solrcluster/solrClusterHome目录

			命令：cp -r /home/canglang/solr/tomcat-solr/ solrcluster/

			进入：cd /home/canglang/solr/solrcluster

			重命名：mv tomcat-solr tomcat-solrCluster-8082

			复制4份：
				cp -r tomcat-solrCluster-8082 tomcat-solrCluster-8083
				cp -r tomcat-solrCluster-8082 tomcat-solrCluster-8084 
				cp -r tomcat-solrCluster-8082 tomcat-solrCluster-8085 

		第二步：创建solr集群所需要的solr仓库
			mkdir solrClusterHome

		第三步：拷贝solr服务所需要仓库
			复制单机版的solr仓库solrhome目录到solr/solrcluster/solrClusterHome目录下

			进入：cd /home/canglang/solr

			命令：cp -r solrhome ./solrcluster/solrClusterHome/

			进入：cd solrcluster/solrClusterHome

			重命名：mv solrhome solrClusterHome-2

			复制4份：
				cp -r solrClusterHome-2/ solrClusterHome-3
				cp -r solrClusterHome-2/ solrClusterHome-4
				cp -r solrClusterHome-2/ solrClusterHome-5

		第四步：指定仓库地址和zookeeper注册中心
			注意：
				配置每一台tomcat的bin目录下的catalina.sh配置文件

			tomcat-solrCluster-8082
				export JAVA_OPTS="-Dsolr.solr.home=/home/canglang/solr/solrcluster/solrClusterHome/solrClusterHome-2/solr -DzkHost=192.168.254.66:2182,192.168.254.66:2183,192.168.254.66:2184"

			tomcat-solrCluster-8083
				export JAVA_OPTS="-Dsolr.solr.home=/home/canglang/solr/solrcluster/solrClusterHome/solrClusterHome-3/solr -DzkHost=192.168.254.66:2182,192.168.254.66:2183,192.168.254.66:2184"
			
			tomcat-solrCluster-8084
				export JAVA_OPTS="-Dsolr.solr.home=/home/canglang/solr/solrcluster/solrClusterHome/solrClusterHome-4/solr -DzkHost=192.168.254.66:2182,192.168.254.66:2183,192.168.254.66:2184"
			
			tomcat-solrCluster-8085
				export JAVA_OPTS="-Dsolr.solr.home=/home/canglang/solr/solrcluster/solrClusterHome/solrClusterHome-5/solr -DzkHost=192.168.254.66:2182,192.168.254.66:2183,192.168.254.66:2184"

		第五步：修改tomcat端口
			tomcat-solrCluster-8082
				vim tomcat-solrCluster-8082/conf/server.xml
				8001
				8082
				8002

			tomcat-solrCluster-8083
				vim tomcat-solrCluster-8083/conf/server.xml
				8003
				8083
				8004

			tomcat-solrCluster-8084
				vim tomcat-solrCluster-8084/conf/server.xml
				8005
				8084
				8006

			tomcat-solrCluster-8085
				vim tomcat-solrCluster-8085/conf/server.xml
				8007
				8085
				8008
		
		第六步：修改集群监控端口
			修改仓库下面solr.xml
			
			Solr.xml在仓库：solrhome1/solr/solr.xml(33行)
			见图2

			Tomcat1运行端口：8081，监控端口也是：8081

			cd /home/canglang/solr/solrcluster/solrClusterHome/solrClusterHome-2/solr

			vim solr.xml

			改33行端口8082


			cd /home/canglang/solr/solrcluster/solrClusterHome/solrClusterHome-3/solr

			vim solr.xml

			改33行端口8083

			
			cd /home/canglang/solr/solrcluster/solrClusterHome/solrClusterHome-4/solr

			vim solr.xml

			改33行端口8084


			cd /home/canglang/solr/solrcluster/solrClusterHome/solrClusterHome-5/solr

			vim solr.xml

			改33行端口8085

		第七步：把solr集群配置文件交给Zookeeper注册中心管理
			
			把仓库核心配置文件放入Zookeeper注册中心交给zookeeper集群管理，当solr集群需要加载配置文件，只需要从Zookeeper中获取配置文件

			进入solr解压目录：cd /home/canglang/solr/solr-4.10.3/example/scripts/cloud-scripts
			
			执行上传命令：./zkcli.sh -zkhost 192.168.254.66:2182, 192.168.254.66:2183, 192.168.254.66:2184 -cmd upconfig -confdir /home/canglang/solr/solrcluster/solrClusterHome/solrClusterHome-2/solr/collection1/conf -confname myconf
				详解：
					./zkcli.sh :zookeeper解压目录下的执行命令
					-zkhost :zookeeper服务的地址
					-cmd upconfig :上传命令
					-confdir :要上传到zookeeper服务的文件目录
					-confname :新建目录用于存储要上传的配置文件

		第八步：登录Zookeeper
			进入：cd /home/canglang/solr/solrcluster/zkCluster-2182/bin/

			执行登录命令：./zkCli.sh  -server 192.168.254.66:2182

			成功后的标识：[zk: 192.168.254.66:2182(CONNECTED) 0]

			查看：ls / (出现：[configs, zookeeper] )

			查看：ls /configs/myconf (其中包含：solrconfig.xml和schema.xml，表示成功)

		第九步：启动4台tomcat服务
			sh tomcat-solrCluster-8082/bin/startup.sh 
			
			sh tomcat-solrCluster-8083/bin/startup.sh 
			
			sh tomcat-solrCluster-8084/bin/startup.sh 
			
			sh tomcat-solrCluster-8085/bin/startup.sh 

		第十步：查看进程
			ps -ef | grep solr

			此时会看到7个进程(3个zk、4个solr)

		第十一步：访问
			http://192.168.254.66:8082/solr/#/

			见图3

		第十二步：创建启动脚本
			在/home/canglang/solr/solrcluster目录下创建启动脚本

			创建文件：touch start-solrcluster-all.sh

			设置读写权限：chmod 755 start-solrcluster-all.sh

			编辑：vim start-solrcluster-all.sh

			内容：
				sh zkCluster-2182/bin/zkServer.sh start
				sh zkCluster-2183/bin/zkServer.sh start
				sh zkCluster-2184/bin/zkServer.sh start
				sh tomcat-solrCluster-8082/bin/startup.sh
				sh tomcat-solrCluster-8083/bin/startup.sh
				sh tomcat-solrCluster-8084/bin/startup.sh
				sh tomcat-solrCluster-8085/bin/startup.sh 

		第十三步：创建停止脚本
			在/home/canglang/solr/solrcluster目录下创建停止脚本

			创建文件：touch stop-solrcluster-all.sh

			设置读写权限：chmod 755 stop-solrcluster-all.sh

			编辑：vim stop-solrcluster-all.sh

			内容：
				sh tomcat-solrCluster-8082/bin/shutdown.sh
				sh tomcat-solrCluster-8083/bin/shutdown.sh
				sh tomcat-solrCluster-8084/bin/shutdown.sh
				sh tomcat-solrCluster-8085/bin/shutdown.sh
				sh zkCluster-2182/bin/zkServer.sh stop
				sh zkCluster-2183/bin/zkServer.sh stop
				sh zkCluster-2184/bin/zkServer.sh stop
				
		第十四步：对solr集群分片
			直接在浏览器上执行以下命令即可
			
			命令：
				http://192.168.254.66:8082/solr/admin/collections?action=CREATE&name=collection2&numShards=2&replicationFactor=3&maxShardsPerNode=8&property=schema.xml&property.config=solrconfig.xml
				
				见图4
				
			详解：
				http://192.168.254.66:8082：solr服务地址
				/solr/admin/collections?
				action=CREATE：创建
				&name=collection2：索引库名称
				&numShards=2：分成两片
				&replicationFactor=2：两个从机
				&maxShardsPerNode=8：最大的从机数量是8
				&property=schema.xml：使用的文件约束是scheam
				&property.config=solrconfig.xml：使用的约束文件是solrconfig
	
	5、java代码测试-测试类
		在search-service工程的test包中编写测试代码
		
		创建包study.project.solrcluster.test
		
		创建测试类MySolrClusterTest.java
		
		/**
		 * 测试solr集群
		 * @author canglang
		 */
		public class MySolrClusterTest {

			/**
			 * 向solr集群中添加数据
			 */
			@Ignore
			@Test
			public void addSolrclusterDoc(){
				//设置zk集群地址
				String zkhost = "192.168.254.66:2182,192.168.254.66:2183,192.168.254.66:2184";
				//创建solr集群对象
				CloudSolrServer cloudSolrServer = new CloudSolrServer(zkhost);
				//设置默认操作的索引库
				cloudSolrServer.setDefaultCollection("collection2");
				//创建docs文档对象
				SolrInputDocument docs = new SolrInputDocument();
				//设置数据
				docs.addField("id", "12345678");
				docs.addField("item_title", "牙膏");
				
				try {
					//设置doc文档对象
					cloudSolrServer.add(docs);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					//提交
					cloudSolrServer.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//测试成功见图5
			
			/**
			 * 查询数据
			 */
			@Ignore
			@Test
			public void querySolrclusterDoc(){
				//设置zk集群地址
				String zkhost = "192.168.254.66:2182,192.168.254.66:2183,192.168.254.66:2184";
				//创建solr集群对象
				CloudSolrServer cloudSolrServer = new CloudSolrServer(zkhost);
				//设置默认操作的索引库
				cloudSolrServer.setDefaultCollection("collection2");	
				
				//设置查询条件
				SolrQuery solrQuery = new SolrQuery();
				solrQuery.setQuery("id:12345678");
				
				try {
					//查询
					QueryResponse query = cloudSolrServer.query(solrQuery);
					SolrDocumentList results = query.getResults();
					
					//查询总记录数
					System.out.println("*************"+results.getNumFound());//1
					
				} catch (SolrServerException e) {
					e.printStackTrace();
				}
			}
		}

	6、java代码测试-交给spring管理
		注意：
			单机版和集群版之间的切换不需要修改代码，只需要修改配置文件即可
	
		在search-service工程的applicationContext-solr.xml配置文件中
		
		<!-- 配置单机版的solr -->			
		<!-- <bean class="org.apache.solr.client.solrj.impl.HttpSolrServer">
			<constructor-arg name="baseURL" value="http://192.168.254.66:8081/solr"></constructor-arg>
		</bean> -->
		
		<!-- 配置集群版的solr -->
		<bean class="org.apache.solr.client.solrj.impl.CloudSolrServer">
			<constructor-arg name="zkHost" value="192.168.254.66:2182,192.168.254.66:2183,192.168.254.66:2184"></constructor-arg>
			<property name="defaultCollection" value="collection2"></property>
		</bean>

	7、访问
        启动search-web、search、content、protal-web

        见图6

二十二、前台系统-ActiveMQ简介、安装

	详见：项目中用的新知识点/activeMQ

二十三、前台系统-同步索引库(13)
	后台管理系统manager工程中添加、修改、删除了一个商品，需要立即像jms服务器中发送消息
	
	前台搜索系统search-web工程监听到有商品的变动应该立即更新solr索引库，以保证solr中的数据和数据库中的数据一致

	1、导入activeMQ依赖
		在manager-service工程和search-service工程中 
		
		<!-- Activemq -->
		<dependency>
			<groupId>org.apache.activemq</groupId>
			<artifactId>activemq-all</artifactId>
			<version>5.11.2</version>
		</dependency>
		<!-- jms -->
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-jms</artifactId>
			<version>5.11.2</version>
		</dependency>
	2、applicationContext-mq.xml
		在manager-service工程中创建ActiveMQ的配置文件applicationContext-mq.xml
		
		需要引入JMS约束
			xmlns:jms="http://www.springframework.org/schema/jms"
			http://www.springframework.org/schema/jms
			http://www.springframework.org/schema/jms/spring-jms-4.0.xsd
		
		<?xml version="1.0" encoding="UTF-8"?>
		<beans xmlns="http://www.springframework.org/schema/beans"
			xmlns:context="http://www.springframework.org/schema/context" 
			xmlns:mvc="http://www.springframework.org/schema/mvc"
			xmlns:aop="http://www.springframework.org/schema/aop" 
			xmlns:tx="http://www.springframework.org/schema/tx"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
			xmlns:jms="http://www.springframework.org/schema/jms"
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
								http://code.alibabatech.com/schema/dubbo/dubbo.xsd
								http://www.springframework.org/schema/jms
								http://www.springframework.org/schema/jms/spring-jms-4.0.xsd">	
			
			<!--消息工厂交给spring管理-->
			<bean id="targetConnectionFactory" class="org.apache.activemq.ActiveMQConnectionFactory">
				<constructor-arg name="brokerURL" value="tcp://192.168.254.66:61616"></constructor-arg>
			</bean>

			<!--
				spring消息服务管理：管理消息工厂
				spring来管理jms消息服务器
			-->
			<bean id="connectionFactory" class="org.springframework.jms.connection.SingleConnectionFactory">
				<property name="targetConnectionFactory" ref="targetConnectionFactory"></property>
			</bean>
			
			<!-- spring框架提供消息发送模式：JmsTemplate发送模式 -->
			<bean class="org.springframework.jms.core.JmsTemplate">
				<property name="connectionFactory" ref="connectionFactory"></property>
			</bean>
			
			<!-- 整合到项目中 -->
			<bean id="addItem" class="org.apache.activemq.command.ActiveMQTopic">
				<constructor-arg index="0" value="add_item_topic"></constructor-arg>
			</bean>
		</beans>

	3、修改ItemServiceImpl.java
		修改manager-service工程中的study.project.service.impl包下的saveItem方法
		
		//消息发送模版
		@Resource
		private JmsTemplate jmsTemplate;
		
		//消息发送目的地
		@Resource
		private ActiveMQTopic activeMQTopic;
		
		/**
		 * 功能4：
         * 		添加商品后保存商品到数据库
         * 功能13：
         * 		同步索引库
		 * 需要保存两张表：
		 * 		TbItem：商品表
		 * 		TbItemDesc:商品描述表
		 * 
		 * 同步索引库:
		 * 		新增一个商品时发送消息到索引库，同步商品到索引库
		 */
		public ProjectResultDTO saveItem(TbItem item, TbItemDesc itemDesc) {
			/**
			 * 根据数据库设计表可以知道，id需要手动设置
			 * 1、使用redis的主键自增长
			 * 2、使用UUID
			 * 3、使用时间戳：毫秒+随机数
			 */
			final long itemId = IDUtils.genItemId();
			
			//补全参数
			//设置id
			item.setId(itemId);
			//商品状态，1-正常，2-下架，3-删除
			item.setStatus((byte)1);
			//补全时间
			Date date = new Date();
			//创建时间
			item.setCreated(date);
			//更新时间
			item.setUpdated(date);
			
			//执行保存
			itemMapper.insert(item);
			
			//商品表和商品描述表的关系是一对一，所以id应该一样
			itemDesc.setItemId(itemId);
			itemDesc.setCreated(date);
			itemDesc.setUpdated(date);
			
			int insertNum = itemDescMapper.insert(itemDesc);
			
			if (insertNum == 1) {
				//保存成功后发送消息到JMS
				jmsTemplate.send(activeMQTopic, new MessageCreator() {
					
					@Override
					public Message createMessage(Session session) throws JMSException {
						// TODO Auto-generated method stub
						return session.createTextMessage(itemId +"");
					}
				});
			}
			return ProjectResultDTO.ok();
		}

	4、applicationContext-mq-receive.xml
		在search-service工程中创建applicationContext-mq-receive.xml配置文件
		
		需要引入JMS约束
			xmlns:jms="http://www.springframework.org/schema/jms"
			http://www.springframework.org/schema/jms
			http://www.springframework.org/schema/jms/spring-jms-4.0.xsd
			
		<?xml version="1.0" encoding="UTF-8"?>
		<beans xmlns="http://www.springframework.org/schema/beans"
			xmlns:context="http://www.springframework.org/schema/context" 
			xmlns:mvc="http://www.springframework.org/schema/mvc"
			xmlns:aop="http://www.springframework.org/schema/aop" 
			xmlns:tx="http://www.springframework.org/schema/tx"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
			xmlns:jms="http://www.springframework.org/schema/jms"
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
								http://code.alibabatech.com/schema/dubbo/dubbo.xsd
								http://www.springframework.org/schema/jms
								http://www.springframework.org/schema/jms/spring-jms-4.0.xsd">	
			
			<!--消息工厂交给spring管理-->
			<bean id="targetConnectionFactory" class="org.apache.activemq.ActiveMQConnectionFactory">
				<constructor-arg name="brokerURL" value="tcp://192.168.254.66:61616"></constructor-arg>
			</bean>

			<!--
				spring消息服务管理：管理消息工厂
				spring来管理jms消息服务器
			-->
			<bean id="connectionFactory" class="org.springframework.jms.connection.SingleConnectionFactory">
				<property name="targetConnectionFactory" ref="targetConnectionFactory"></property>
			</bean>
			
			<!-- 整合到项目中 -->
			<bean id="addItem" class="org.apache.activemq.command.ActiveMQTopic">
				<constructor-arg index="0" value="add_item_topic"></constructor-arg>
			</bean>
			
			<!-- 指定自己的监听器 -->
			<!-- <bean id="messageListener" class="study.project.avtivemqListerne.MyMqListener"></bean> -->
			<bean id="messageListener" class="study.project.avtivemqListerne.AddItemMessageListerne"></bean>
			
			<!--  
				使用监听器接收消息
				spring JMS框架使用默认监听器来加载自定义监听器接收消息
			-->
			<bean class="org.springframework.jms.listener.DefaultMessageListenerContainer">
				<!-- 指定信息服务器地址 -->
				<property name="connectionFactory" ref="connectionFactory"></property>
				<!-- 指定消息服务器监听的目的地 -->
				<!-- <property name="destination" ref="myqueue"></property> -->
				 
					<!--使用订阅模式时将这一行放开，将上面的注释掉就行-->
				<property name="destination" ref="addItem"></property> 
				
				<!-- 指定自己的监听器 -->
				<property name="messageListener" ref="messageListener"></property>
			</bean>
		</beans>

	5、AddItemMessageListerne.java
		在search-service工程的study.project.avtivemqListerne包下创建AddItemMessageListerne.java监听类

		public class AddItemMessageListerne implements MessageListener{

			//查询数据库
			@Resource
			private SearchItemMapper searchItemMapper;
			
			@Resource
			private SolrServer solrServer;
			
			/**
			 * 功能13:
			 * 接收到消息后同步索引库
			 */
			public void onMessage(Message message) {
				if (message instanceof TextMessage) {
					TextMessage tm = (TextMessage)message;
					try {
						String itemId = tm.getText();
						
						//根据itemId查询数据库
						SearchItem searchItem = searchItemMapper.findItemInfoById(Long.parseLong(itemId));
						
						//创建文档对象
						SolrInputDocument doc = new SolrInputDocument();
						//设置文档id
						doc.addField("id", searchItem.getId());
						//设置商品标题
						doc.addField("item_title", searchItem.getTitle());
						//设置商品卖点
						doc.addField("item_sell_point", searchItem.getSell_point());
						//设置商品图片地址
						doc.addField("item_image", searchItem.getImage());
						//设置商品类别
						doc.addField("item_category_name", searchItem.getCatelog_name());
						//设置商品价格
						doc.addField("item_price", searchItem.getPrice());
						//设置商品描述
						doc.addField("item_desc", searchItem.getItem_desc());
						
						solrServer.add(doc);
						solrServer.commit();
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

	6、SearchItemMapper.java
	
		在search-interface工程中的study.project.search.mapper包下的SearchItemMapper接口中添加接口方法
		
		/**
		 * 功能13：
		 * 同步索引库时根据itemId查询数据库
		 */
		public SearchItem findItemInfoById(Long itemId);

	7、SearchItemMapper.xml
        在search-interface工程中的study.project.search.mapper包下的SearchItemMapper配置文件中添加sql语句

        <!-- 
        resultType查询的列名称必须和javabean中的属性名称一一对应，且一样 
        同步索引库时根据itemId查询商品信息
        -->
        <select id="findItemInfoById" parameterType="Long" resultType="study.project.search.pojo.SearchItem">

        SELECT  
            a.id,
            a.title,
            a.sell_point,
            a.price,
            a.image,
            b.name catelog_name,
            c.item_desc
        FROM 
            tb_item a, 
            tb_item_cat b, 
            tb_item_desc c
        WHERE
            a.cid=b.id
            AND a.id=c.item_id
            AND a.status=1
            AND a.id=#{itemId}
        </select>

	8、测试
		在后台添加一个商品，从搜索系统中搜索刚才添加的商品
		
二十四、前台系统-搭建商品详情服务	
	1、搭建
        item-web服务依赖于manager聚合工程就行，所以不需要再创建item工程的聚合工程、service和接口工程了

        17taotao-item-web
        在parent工程上右键创建maven-project工程
        见图7

	2、整合
		复制portal-web工程中的数据
		
		2.1、复制配置文件
			复制portal-web工程中的所有配置文件到item-web工程中
			
			log4j.properties
			resource.properties
			springmvc.xml

		2.2、修改resource.properties
			删除里面不必要的配置
			
		2.3、springmvc.xml
			<!-- 引用服务 -->
			修改：<dubbo:application name="taotao-item-web"/>
		
			注释掉不必要的dubbo服务
			
		2.4、页面
			复制portal-web工程webapp目录下的所有配置文件到item-web工程中
			
			只留item.jsp和success.jsp页面
			
		2.5、web.xml
			不需要改动
			
		2.6、pom.xml
			复制portal-web工程下的pom文件中的所有依赖到item-web工程的pom文件中
			
			删除：11taotao-content-interface依赖
			
			添加：08taotao-manager-interface依赖
			
			修改端口：8087

		2.7、添加跳转到商品详情的连接
			在search-web工程的search.jsp中修改
			
			修改46和51行的跳转连接：
				<div class="p-img">
					<a target="_blank" href="http://localhost:8087/${item.id }.html">
						<img width="160" height="160" data-img="1" data-lazyload="${item.image}" />
					</a>
				</div>
				<div class="p-name">
					<a target="_blank" href="http://localhost:8087/${item.id }.html">
						${item.title}
					</a>
				</div>
		
二十五、前台系统-展示商品详情(14)
	1、配置文件
        在item-web工程中的springmvc.xml中引用manager-service的dubbo服务

        <!-- 引用服务(包含根据id查询商品信息、分页查询商品列表) -->
        <dubbo:reference id="itemService" interface="study.project.ItemService" version="1.0.0" timeout="5000"/>
    
	2、controller
		在item-web工程中
		创建study.project.item.controller包
		创建ItemDetailController.java
		
		@Controller
		public class ItemDetailController {

			@Resource
			private ItemService itemService;
			
			/**
			 * 功能14：
			 * 		根据商品id查询商品的详情
			 * 请求：	
			 * 		http://localhost:8087/${item.id }.html
			 * 参数：
			 * 		itemId
			 * 页面所需要的数据：
			 * 		1、商品信息（商品表）
			 * 		2、商品描述信息（商品描述表）
			 * 		3、商品规格
			 * 
			 * @param itemId
			 * @return
			 */
			@RequestMapping("{itemId}")
			public String findItemDetailById(@PathVariable Long itemId, Model model){
				
				//查询商品信息
				TbItem item = itemService.findItemByID(itemId);
				
				//查询商品描述表
				TbItemDesc itemDesc = itemService.findItemDescById(itemId);
				
				//根据页面需求，需要将数据放入model中
				model.addAttribute("item", item);
				model.addAttribute("itemDesc", itemDesc);
				
				return "item";
			}
		}
	
	3、interface
		在manager-interface工程的study.project.ItemService接口类中添加根据商品id查询商品描述的的接口方法
		
		itemService.findItemByID(itemId);接口还用之前的
		
		/**
		 * 功能14：
		 * 		根据商品id查询商品的详情
		 * 请求：	
		 * 		http://localhost:8087/${item.id }.html
		 * 参数：
		 * 		itemId
		 * 页面所需要的数据：
		 * 		1、商品信息（商品表）
		 * 		2、商品描述信息（商品描述表）
		 * 		3、商品规格
		 * 
		 * @param itemId
		 * @return
		 */
		public TbItemDesc findItemDescById(Long itemId);
	
	4、impl
		在manager-service工程的study.project.service.impl.ItemServiceImpl类中实现接口方法
		
		/**
		 * 功能14：
		 * 		根据商品id查询商品的详情
		 * 请求：	
		 * 		http://localhost:8087/${item.id }.html
		 * 参数：
		 * 		itemId
		 * 页面所需要的数据：
		 * 		1、商品信息（商品表）
		 * 		2、商品描述信息（商品描述表）
		 * 		3、商品规格
		 * 
		 * @param itemId
		 * @return
		 */
		public TbItemDesc findItemDescById(Long itemId) {
			TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
			return itemDesc;
		}
	
	5、TbItem
		修改manager-pojo共的中的TbItem,java			
			
		根据页面回显的数据${item.images}来看需要在TbItem.java中添加一个字段images(数组)
		
		private String[] images;
		
		public String[] getImages() {
			String[] img = null;
			
			if (StringUtils.isNotBlank(image)) {
				img = image.split(",");
			}
			
			return img;
		}

		public void setImages(String[] images) {
			this.images = images;
		}
			
	6、测试
		见图8
			
二十六、前台系统-商品详情也添加缓存(15)			
	1、dao接口
        在manager-interface工程下创建redis.dao包，在此包下复制content.interface工程下的redis包下的jedisDao.java

        public interface JedisDao {

            //抽取Jedis的常用方法
            //数据结构string
            public String set(String key, String value);
            public String get(String key);
            //自增、自减
            public Long incr(String key);
            public Long decr(String key);
            
            //数据结构hash
            public Long hset(String key, String field, String value);
            public String hget(String key, String field);
            //删除
            public Long hdel(String key, String field);
            
            //过期设置
            public Long expire(String key, int seconds);
            //查看过期时间
            public Long ttl(String key);
        }		
	2、导入依赖
        在manager-service工程的pom.xml中导入jedis的依赖
        <!-- Redis客户端 -->
		<dependency>
			<groupId>redis.clients</groupId>
			<artifactId>jedis</artifactId>
			<version>${jedis.version}</version>
		</dependency>


    3、daoimpl
        在manager-servier工程下创建redis.dao.impl包，在此包下复制content-service工程下redis.impl包下的JedisDaoImpl.java

        @Repository
        public class JedisDaoImpl implements JedisDao {

            //**************集群版*********************
            @Resource
            private JedisCluster jedisCluster;
            
            @Override
            public String set(String key, String value) {
                String set = jedisCluster.set(key, value);
                return set;
            }

            @Override
            public String get(String key) {
                String get = jedisCluster.get(key);
                return get;
            }

            @Override
            public Long incr(String key) {
                Long incr = jedisCluster.incr(key);
                return incr;
            }

            @Override
            public Long decr(String key) {
                Long decr = jedisCluster.decr(key);
                return decr;
            }

            @Override
            public Long hset(String key, String field, String value) {
                Long hset = jedisCluster.hset(key, field, value);
                return hset;
            }

            @Override
            public String hget(String key, String field) {
                String hget = jedisCluster.hget(key, field);
                return hget;
            }

            @Override
            public Long hdel(String key, String field) {
                Long hdel = jedisCluster.hdel(key, field);
                return hdel;
            }

            @Override
            public Long expire(String key, int seconds) {
                Long expire = jedisCluster.expire(key, seconds);
                return expire;
            }

            @Override
            public Long ttl(String key) {
                Long ttl = jedisCluster.ttl(key);
                return ttl;
            }
        }
	4、配置文件
        在manager-service工程下的配置文件目录复制content-service工程的下的redis配置

        <?xml version="1.0" encoding="UTF-8"?>
        <beans xmlns="http://www.springframework.org/schema/beans"
            xmlns:context="http://www.springframework.org/schema/context" 
            xmlns:mvc="http://www.springframework.org/schema/mvc"
            xmlns:aop="http://www.springframework.org/schema/aop" 
            xmlns:tx="http://www.springframework.org/schema/tx"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
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
            
            <!--1、 测试单机版的Jedis连接Redis -->
            <!-- <bean class="redis.clients.jedis.Jedis">
                <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                <constructor-arg name="port" value="6379"></constructor-arg>
            </bean> -->
            
            <!--2、使用单机版的jedis连接池连接redis 把JedisPoolConfig对象交给spring创建 -->
            <bean id="poolConfig" class="redis.clients.jedis.JedisPoolConfig">
                <property name="maxIdle" value="20"/>
                <property name="maxTotal" value="1000"/>
            </bean>
            <!-- 将JedisPool对象交给spring创建 -->
            <!-- <bean class="redis.clients.jedis.JedisPool">
                <constructor-arg name="poolConfig" ref="poolConfig"></constructor-arg>
                <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                <constructor-arg name="port" value="6379"></constructor-arg>
            </bean> -->
            
            <!-- 3、使用spring整合集群版的jedis连接池连接redis集群 -->
            <bean class="redis.clients.jedis.JedisCluster">
                <constructor-arg name="nodes">
                    <set>
                        <bean class="redis.clients.jedis.HostAndPort">
                            <constructor-arg name="host" value="192.168.254.66"/>
                            <constructor-arg name="port" value="7001"/>
                        </bean>
                        <bean class="redis.clients.jedis.HostAndPort">
                            <constructor-arg name="host" value="192.168.254.66"/>
                            <constructor-arg name="port" value="7002"/>
                        </bean>
                        <bean class="redis.clients.jedis.HostAndPort">
                            <constructor-arg name="host" value="192.168.254.66"/>
                            <constructor-arg name="port" value="7003"/>
                        </bean>
                        <bean class="redis.clients.jedis.HostAndPort">
                            <constructor-arg name="host" value="192.168.254.66"/>
                            <constructor-arg name="port" value="7004"/>
                        </bean>
                        <bean class="redis.clients.jedis.HostAndPort">
                            <constructor-arg name="host" value="192.168.254.66"/>
                            <constructor-arg name="port" value="7005"/>
                        </bean>
                        <bean class="redis.clients.jedis.HostAndPort">
                            <constructor-arg name="host" value="192.168.254.66"/>
                            <constructor-arg name="port" value="7006"/>
                        </bean>
                        <bean class="redis.clients.jedis.HostAndPort">
                            <constructor-arg name="host" value="192.168.254.66"/>
                            <constructor-arg name="port" value="7007"/>
                        </bean>
                        <bean class="redis.clients.jedis.HostAndPort">
                            <constructor-arg name="host" value="192.168.254.66"/>
                            <constructor-arg name="port" value="7008"/>
                        </bean>
                    </set>
                </constructor-arg>
                <constructor-arg name="poolConfig" ref="poolConfig"/>
            </bean>
        </beans>

    5、提取常量配置    
        在manager-service工程下的创建resource.properties配置文件

        #商品详情页的商品信息存入redis中的key
        ITEM_DETAIL_CHACHE=ITEM_DETAIL
        #设置商品的过期时间
        EXPIRE_TIME=86400

    6、添加缓存
        在manager-service工程的study.project.service.impl包下的ItemServiceImpl.java类中修改之前已有的方法
        
        修改根据itemId查询商品的方法和功能14

        @Resource
        private JedisDao jedisDao;

        //#商品详情页的商品信息存入redis中的key
        @Value("${ITEM_DETAIL_CHACHE}")
        private String ITEM_DETAIL_CHACHE;

        //商品的过期时间
        @Value("${EXPIRE_TIME}")
        private Integer EXPIRE_TIME;


        修改根据itemId查询商品的方法
            /**
            * 根据itemId查询Item信息
            * 添加缓存：
            * 		目的：减轻数据库压力，提高查询效率
            * 		业务流程：
            * 			查询数据库前先查询redis缓存
            * 				有：
            * 					直接返回
            * 				无：
            * 					再查询数据库，并且将查到的数据同步到缓存中
            *
            * 过期时间：
            *		商品详情页在redis中的存储应该有过期时间：1天(864000秒)
            *		redis的数据结构中只有string类型可以设置过期时间
            *
            * 业务设计：
            *		redis存储结构：key：value
            *		商品信息：
            *			key=ITEM_DETAIL:BASE:itemId
            *			value=json格式的商品数据
            *
            *		商品描述信息：
            *			key=ITEM_DETAIL:DESC:itemId
            *			value=json格式的商品描述数据
            *
            */
            public TbItem findItemByID(Long itemId) {

                //查询数据库之前先查询redis缓存
                String itemJson = jedisDao.get(ITEM_DETAIL_CHACHE + ":BASE:" + itemId);

                if (StringUtils.isNotBlank(itemJson)){
                    TbItem tbItem = JsonUtils.jsonToPojo(itemJson, TbItem.class);

                    return tbItem;
                }


                //创建TbItemExample对象
                TbItemExample example = new TbItemExample();
                //获取Criteria对象
                Criteria criteria = example.createCriteria();
                //传参
                criteria.andIdEqualTo(itemId);
                //执行查询
                List<TbItem> itemList = itemMapper.selectByExample(example);

                TbItem tbItem = null;
                if (itemList != null && itemList.size() > 0) {
                    tbItem = itemList.get(0);
                }

                //将查询结果放入redis缓存中
                jedisDao.set(ITEM_DETAIL_CHACHE + ":BASE:" + itemId, JsonUtils.objectToJson(tbItem));
                //设置过期时间
                jedisDao.expire(ITEM_DETAIL_CHACHE + ":BASE:" + itemId, EXPIRE_TIME);

                return tbItem;
            }

        功能14
            /**
            * 功能14：
            * 		根据商品id查询商品的描述
            * 请求：	
            * 		http://localhost:8087/${item.id }.html
            * 参数：
            * 		itemId
            * 页面所需要的数据：
            * 		1、商品信息（商品表）
            * 		2、商品描述信息（商品描述表）
            * 		3、商品规格
            * 添加缓存：
            * 		目的：减轻数据库压力，提高查询效率
            * 		业务流程：
            * 			查询数据库前先查询redis缓存
            * 				有：
            * 					直接返回
            * 				无：
            * 					再查询数据库，并且将查到的数据同步到缓存中
            *
            * 过期时间：
            *		商品详情页在redis中的存储应该有过期时间：1天(864000秒)
            *		redis的数据结构中只有string类型可以设置过期时间
            *
            * 业务设计：
            *		redis存储结构：key：value
            *		商品信息：
            *			key=ITEM_DETAIL:BASE:itemId
            *			value=json格式的商品数据
            *
            *		商品描述信息：
            *			key=ITEM_DETAIL:DESC:itemId
            *			value=json格式的商品描述数据
            * 
            * @param itemId
            * @return
            */
            public TbItemDesc findItemDescById(Long itemId) {

                //查询数据库之前先查询redis缓存
                String itemJson = jedisDao.get(ITEM_DETAIL_CHACHE + ":DESC:" + itemId);

                if (StringUtils.isNotBlank(itemJson)){
                    TbItemDesc tbItemDesc = JsonUtils.jsonToPojo(itemJson, TbItemDesc.class);

                    return tbItemDesc;
                }

                TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);

                //将查询结果放入redis缓存中
                jedisDao.set(ITEM_DETAIL_CHACHE + ":DESC:" + itemId, JsonUtils.objectToJson(itemDesc));
                //设置过期时间
                jedisDao.expire(ITEM_DETAIL_CHACHE + ":DESC:" + itemId, EXPIRE_TIME);

                return itemDesc;
            }

二十七、前台系统-Freemarker简介及使用
    详见：项目中用的新知识点/Freemarker
    
二十八、前台系统-页面实现静态化(16)
    0、数据同步分析
		见图2

    1、导入依赖
        在item-service工程中导入ActiveMq和Freemarker的依赖

        <!--freemarker-->
		<dependency>
			<groupId>org.freemarker</groupId>
			<artifactId>freemarker</artifactId>
			<version>${freemarker.version}</version>
		</dependency>
        <!-- Activemq -->
        <dependency>
            <groupId>org.apache.activemq</groupId>
            <artifactId>activemq-all</artifactId>
            <version>${activemq.version}</version>
        </dependency>
			
	2、添加配置文件		
		在item-service工程中添加ActiveMq和Freemarker配置文件

        在resource.properties中添加html文件保存的路径

        复制search-service工程中mq的配置文件即可

        applicationContext-mq-receive.xml
            <?xml version="1.0" encoding="UTF-8"?>
            <beans xmlns="http://www.springframework.org/schema/beans"
                。。。。。。

                <!--将Freemarker对像的创建交给spring管理-->
                <bean class="org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer">
                    <property name="templateLoaderPath" value="/WEB-INF/jsp/freemarker/"/>
                    <property name="defaultEncoding" value="UTF-8"/>
                </bean>
            </beans>

        applicationContext-freemarker.xml	
			<?xml version="1.0" encoding="UTF-8"?>
            <beans xmlns="http://www.springframework.org/schema/beans"
                。。。。。。
                
                <!--消息工厂交给spring管理-->
                <bean id="targetConnectionFactory" class="org.apache.activemq.ActiveMQConnectionFactory">
                    <constructor-arg name="brokerURL" value="tcp://192.168.254.66:61616"/>
                </bean>

                <!--
                    spring消息服务管理：管理消息工厂
                    spring来管理jms消息服务器
                -->
                <bean id="connectionFactory" class="org.springframework.jms.connection.SingleConnectionFactory">
                    <property name="targetConnectionFactory" ref="targetConnectionFactory"/>
                </bean>
                
                <!-- 整合到项目中 -->
                <bean id="addItem" class="org.apache.activemq.command.ActiveMQTopic">
                    <constructor-arg index="0" value="add_item_topic"/>
                </bean>
                
                <!-- 指定自己的监听器 -->
                <!-- <bean id="messageListener" class="study.project.avtivemqListerne.MyMqListener"></bean> -->
                <bean id="messageListener" class="study.project.item.controller.freemarkerListerne.AddItemFreemarkerListerne"/>
                
                <!--  
                    使用监听器接收消息
                    spring JMS框架使用默认监听器来加载自定义监听器接收消息
                -->
                <bean class="org.springframework.jms.listener.DefaultMessageListenerContainer">
                    <!-- 指定信息服务器地址 -->
                    <property name="connectionFactory" ref="connectionFactory"/>
                    <!-- 指定消息服务器监听的目的地 -->
                    <!-- <property name="destination" ref="myqueue"></property> -->
                    
                        <!--使用订阅模式时将这一行放开，将上面的注释掉就行-->
                    <property name="destination" ref="addItem"/>
                    
                    <!-- 指定自己的监听器 -->
                    <property name="messageListener" ref="messageListener"/>
                </bean>
            </beans>

        resource.properties
            #Freemarker生成的html路径
            GEN_HTML_PATH=E:\\JAVA\\ReStudy\\Study\\Project\\WJ-project\\WJ-project\\17taotao-item-web\\src\\main\\webapp\\WEB-INF\\jsp\\freemarker\\out\\

    3、监听器
        在item-web工程中的study.project.item.controller.freemarkerListerne包下创建AddItemFreemarkerListerne.java监听器

        /**
        * freemarker的mq监听器
        * Created by canglang on 2017/9/3.
        */
        public class AddItemFreemarkerListerne implements MessageListener{

            @Resource
            private ItemService itemService;

            @Resource
            private FreeMarkerConfigurer freeMarkerConfigurer;

            @Value("${GEN_HTML_PATH}")
            private String GEN_HTML_PATH;

            public void onMessage(Message message) {

                if (message instanceof TextMessage){
                    TextMessage textMessage = (TextMessage)message;

                    try {
                        String itemId = textMessage.getText();

                        //根据消息商品id查询商品信息
                        TbItem item = itemService.findItemByID(Long.parseLong(itemId));

                        //根据消息商品id查询商品描述信息
                        TbItemDesc itemDesc = itemService.findItemDescById(Long.parseLong(itemId));

                        //创建Freemarker配置对象

                        Configuration configuration = freeMarkerConfigurer.getConfiguration();

                        Template template = configuration.getTemplate("item.ftl");

                        Map<String, Object> maps = new HashMap<>();
                        maps.put("item", item);
                        maps.put("itemDesc", itemDesc);

                        Writer out = new FileWriter(new File(GEN_HTML_PATH + itemId + ".html"));

                        template.process(maps, out);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }

    4、改造页面
        改造item-web工程中的item.jsp页面为freemarker模版

        需要将item-web工程下的WEB-INF/jsp/commons目录、error目录、item.jsp、success.jsp复制到WEB-INF/jsp/freemarker目录下

        将这些文件中的jsp引用，循环改成Freemarker的动态指令

        将这些文件的后缀改成Freemarker的后缀(.ftl)


二十九、前台系统-单点登录(sso)			
    1、SSO简介
        SSO英文全称Single Sign On，单点登录。
        
        SSO是在多个应用系统中，用户只需要登录一次就可以访问所有相互信任的应用系统。
        
        它包括可以将这次主要的登录映射到其他应用中用于同一个用户的登录的机制。
        
        它是目前比较流行的企业业务整合的解决方案之一。
        
    2、SSO作用   
        单点登录主要解决的问题就是session共享

    3、登录流程
        传统登录模式：适合单个项目，小型项目
        见图1

    4、session共享的演变
        集群Session共享
        见图1

        分布式Session共享			
        见图3
			
三十、前台系统-搭建登录系统
    1、搭建
        1.1、18taotao-user
            继承：01taotao-parent
            打包方式：pom
            见图1

        1.2、19taotao-user-interface
            继承18taotao-user
            打包方式：jar
            见图2

        1.3、20taotao-user-service
            继承18taotao-user
            打包方式：war
            见图3

        1.4、21taotao-user-sso-web
            继承01taotao-parent
            打包方式：war
            见图4
			
	2、整合
        2.1、18taotao-user
            pom.xml
                <!--  
                    需要的依赖：
                        1、抽取公共的jar
                        2、管理聚合项目(依赖、插件)
                    参考：
                        content工程
                -->
                <dependencies>
                    <dependency>
                        <groupId>com.taotao</groupId>
                        <artifactId>02taotao-common</artifactId>
                        <version>0.0.1-SNAPSHOT</version>
                    </dependency>
                </dependencies>

                <build>
                    <plugins>
                        <plugin>
                            <groupId>org.apache.tomcat.maven</groupId>
                            <artifactId>tomcat7-maven-plugin</artifactId>
                            <version>2.2</version>
                            <configuration>
                                <!-- 定义项目发布路径，相当于直接放到tomcat的ROOT目录，在访问时不需要项目名称，直接访问路径就行 -->
                                <path>/</path>
                                <!-- 配置tomcat端口 -->
                                <port>8086</port>
                            </configuration>
                        </plugin>
                    </plugins>
                </build>

        2.2、19taotao-user-interface		
			pom.xml
                <!--
                    所需坐标：
                        pojo
                    参考：
                        manager-interface
                -->
                <dependencies>
                    <dependency>
                        <groupId>com.taotao</groupId>
                        <artifactId>04taotao-manager-pojo</artifactId>
                        <version>0.0.1-SNAPSHOT</version>
                    </dependency>
                </dependencies>

        2.3、20taotao-user-service
            pom.xml：
                <!--
                    业务层：处理业务代码
                    事物：spring
                    依赖dao、service依赖dao从而间接依赖pojo
                -->
                <dependencies>
                    <!-- Spring -->
                    
                    <!-- dubbo相关 -->
                    
                    <!-- Activemq -->
                    
                    <!-- 分页 -->
                    
                    <!-- Redis客户端 -->

                    <!-- 依赖dao、间接依赖pojo -->
                    <dependency>
                        <groupId>com.taotao</groupId>
                        <artifactId>05taotao-manager-dao</artifactId>
                        <version>0.0.1-SNAPSHOT</version>
                    </dependency>

                    <!-- 依赖接口 -->
                    <dependency>
                        <groupId>com.taotao</groupId>
                        <artifactId>19taotao-user-interface</artifactId>
                        <version>0.0.1-SNAPSHOT</version>
                    </dependency>
                </dependencies>

            配置文件：
                复制content-service工程中的配资文件

                applicationContext-dao.xml
                applicationContext-service.xml
                jdbc.properties
                log4j.properties
                resource.properties
                sqlMapConfig.xml

                除了以下两个配置文件 其他不变

                applicationContext-service.xml  
                    <!-- 切面 -->
                    <aop:config>
                        <aop:advisor advice-ref="txAdvice" pointcut="execution(* study.project.user.service.*.*(..))"/>
                    </aop:config>

                    <dubbo:protocol name="dubbo" port="20883" /><!-- 同一个tomcat下端口不能重复 -->

                resource.properties
                    清空这个配置文件中的内容，需要时再添加

            web.xml：
                复制content-service工程下的web-INF目录到user工程下是webapp目录下即可

        2.4、21taotao-user-sso-web
			pom.xml 
                <!--
                    表现层：
                        springmvc(集成service就继承了spring的jar)
                        mvc依赖
                        jstl/servlet/jsp等
                -->
                <dependencies>
                    <!-- JSP相关 -->
                    
                    <!-- Spring -->
                    
                    <!-- dubbo相关 -->
                    
                    <!-- 图片上传需要两个依赖，fileUpload和fastdfs -->
                    
                    <!-- 单元测试 -->
                    <dependency>
                        <groupId>junit</groupId>
                        <artifactId>junit</artifactId>
                        <version>${junit.version}</version>
                        <scope>test</scope>
                    </dependency>

                    <!-- 依赖接口 -->
                    <dependency>
                        <groupId>com.taotao</groupId>
                        <artifactId>19taotao-user-interface</artifactId>
                        <version>0.0.1-SNAPSHOT</version>
                    </dependency>
                </dependencies>
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
                                <port>8089</port>
                            </configuration>
                        </plugin>
                    </plugins>
                </build>

            配置文件：   
                log4j.properties
                resource.properties
                springmvc.xml

                除了以下配置文件 其他不需要修改

                springmvc.xml
                    <dubbo:application name="taotao-user-sso-web"/>

                    去除没有的dubbo服务

                resource.properties
                    清空此配置文件，需要时再添加

            web.xml：
                复制protal-web工程下web-INF目录到user-sso-web工程的webapp目录下即可，有需要修改的修改

            单点登录页面：
                复制JAVA/ReStudy/DataWord/30WJ商城/资料/静态页面/单点登录静态页面/目录下的页面到user-sso-web工程中

                将css、images、js目录复制到webapp目录下

                将jsp目录复制到web-INF目录下

                修改jsp中js和css路径
	3、测试
        3.1、controller  
            在user-sso-web工程中创建study.project.user.sso.controller包

            在此包下创建PageController.java
            @Controller
            public class PageController {

                @RequestMapping("{page}")
                public String showIndex(@PathVariable String page){

                    return page;
                }
            }

        3.2、启动user和user-sso-web工程
			
        3.3、访问
            http://localhost:8089/register.html
            或者
            http://localhost:8089/login.html
			
三十一、前台系统-单点登录前检查数据是否可用(17)
    1、interface
        在user-interface 工程下创建study.project.user.service包，在此包下创建数据检查的接口IUserService.java

        /**
        * SSO接口
        * Created by canglang on 2017/9/5.
        */
        public interface IUserService {

            /**
            * 功能17：
            *      登录前检查数据的可用性
            * 请求：
            *      http://sso.taotao.com/user/check/{param}/{type}
            * 要检查的数据：
            *      username、phone、email
            * 返回值：
            *      json格式的ProjectResultDTO
            */
            public ProjectResultDTO dataCheck(String param, Integer type);
        }

    2、service
        在user-service工程下创建study.project.user.service.impl包，在此包下创建UserServiceImpl.java数据检查的接口实现类

        /**
        *  sso
        * Created by panhusun on 2017/9/4.
        */
        @Service
        public class UserServiceImpl implements IUserService {

            @Resource
            private TbUserMapper userMapper;

            /**
            * 功能17：
            *      登录前检查数据的可用性
            * 请求：
            *      http://sso.taotao.com/user/check/{param}/{type}
            * 要检查的数据：
            *      username、phone、email
            * 返回值：
            *      json格式的ProjectResultDTO
            */
            public ProjectResultDTO dataCheck(String param, Integer type) {

                TbUserExample example = new TbUserExample();

                TbUserExample.Criteria criteria = example.createCriteria();

                //根据参数类型设置值
                if (type == 1) {
                    criteria.andUsernameEqualTo(param);
                } else if (type == 2) {
                    criteria.andPhoneEqualTo(param);
                } else if (type == 3) {
                    criteria.andEmailEqualTo(param);
                }

                List<TbUser> tbUsers = userMapper.selectByExample(example);

                if (tbUsers == null || tbUsers .isEmpty() || tbUsers.size() == 0) {
                    return ProjectResultDTO.ok(true);
                }

                return ProjectResultDTO.ok(false);
            }
        }

        在applicationContext-service.xml配置文件中发布dubbo服务

            <!-- 创建需要发布对象(包含根据id查询商品信息、分页查询商品列表)-->
            <bean id="userServiceImpl" class="study.project.user.service.impl.UserServiceImpl"/>
            <!-- 发布服务 user工程 -->
            <dubbo:service ref="userServiceImpl" interface="study.project.user.service.IUserService" version="1.0.0" retries="0" timeout="100000"/>

    3、controller
        在user-sso-web工程中的study.project.user.sso.controller包下创建UserContrller.java

        /**
        * 校验登录数据是否可用
        * Created by canglang on 2017/9/5.
        */
        @Controller
        public class UaerController {

            @Resource
            private IUserService userService;

            /**
            * 功能17：
            *      登录前检查数据的可用性
            * 请求：
            *      http://sso.taotao.com/user/check/{param}/{type}
            * 要检查的数据：
            *      username、phone、email
            * 返回值：
            *      json格式的ProjectResultDTO
            */
            @RequestMapping("/user/check/{param}/{type}")
            @ResponseBody
            public ProjectResultDTO dataCheck(@PathVariable String param, @PathVariable Integer type){

                ProjectResultDTO dataCheck = userService.dataCheck(param, type);


                return dataCheck;
            }
        }

        在springmvc.xml配置文件中引用dubbo服务
        
            <!-- 引用服务(y引用solr索引库服务) -->
            <dubbo:reference id="userService" interface="study.project.user.service.IUserService" version="1.0.0" timeout="5000"/>

    4、web.xml
		根据接口开发文档得知请求采用restfull风格的结构，所以需要将web.xml中的拦截改成“/”	
			
	5、测试
        见图4	
			
三十二、前台系统-用户注册(18)
    1、controller
        访问登录和注册页面创建controller跳转方法，在PageController.java中操作

        /**
        *
        * Created by panhusun on 2017/9/4.
        */
        @Controller
        public class PageController {

            /**
            * 跳转到注册
            * /user/showLogin
            * @return
            */
            @RequestMapping("/user/showRegister")
            public String showRegister(){

                return "register";
            }
            /**
            * 跳转到登录
            * /user/showLogin
            * @return
            */
            @RequestMapping("/user/showLogin")
            public String showLogin(){

                return "login";
            }
        }

    2、interface
        在User-interface工程中的IuserService接口类中添加

        /**
        * 功能18：
        *      用户注册(注册前要先检查数据的可用性)
        * 请求：
        *      http://sso.taotao.com/user/register
        * 参数：
        *      TbUser
        */
        public ProjectResultDTO register(TbUser user);

    3、service
        在User-service工程中的UserService中实现接口的实现类

        /**
        * 功能18：
        *      用户注册(注册前要先检查数据的可用性)
        * 请求：
        *      http://sso.taotao.com/user/register
        * 参数：
        *      TbUser
        */
        public ProjectResultDTO register(TbUser user) {

            try {
                //补全参数
                user.setCreated(new Date());
                user.setUpdated(new Date());

                //给密码加密
                if (StringUtils.isNotBlank(user.getPassword())) {
                    //DigestUtils是spring提供的工具类
                    user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
                }

                //注册
                int insert = userMapper.insert(user);

                if (insert != 1) {
                    return ProjectResultDTO.build(400, "注册失败，请稍后重试！");
                }

            } catch (Exception e) {
                e.printStackTrace();
                return ProjectResultDTO.build(400, "注册失败，请稍后重试！");
            }

            return ProjectResultDTO.ok();
        }

    4、controller
        在user-sso-web工程的UserController中调用接口

        /**
        * 功能18：
        *      用户注册(注册前要先检查数据的可用性)
        * 请求：
        *      http://sso.taotao.com/user/register
        * 参数：
        *      TbUser
        */
        @ResponseBody
        @RequestMapping(value = "/user/register", method = RequestMethod.POST)
        public ProjectResultDTO register(TbUser user){

            ProjectResultDTO result = userService.register(user);

            return result;
        }

    5、配置文件
        在springmvc.xml配置文件中添加放行静态页面

        <!-- 放行静态资源  或者使用<mvc:default-servlet-handler/>-->
        <mvc:resources location="/css/" mapping="/css/**"/>
        <mvc:resources location="/js/" mapping="/js/**"/>
        <mvc:resources location="/images/" mapping="/images/**"/>

    6、注册流程
        跳转register页面

        立即注册

        触发js事件

        验证输入数据格式是否正确(不能为空)

        验证数据是否可以使用（用户名是否被占用）

        提交

    7、jsp
        跳转到注册页面(register.jsp)的请求：/user/showRegister

        当用户输入完注册信息点击立即注册，执行108行代码，出发一个点击事件onclick=‘REGISTER.reg()’;

        执行189行的点击事件，执行191行的调用this.beforeSubmit();

        执行150行方法，注册前的先校验用户名和手机号是否已经被注册，

        数据可用的情况下执行REGISTER.doSubmit();方法

        执行175行代码进行注册，成功后跳转到登录页面

        执行185行代码跳转到登录页面

    8、测试
        启动user和user-sso-web工程
			
三十三、前台系统-用户登录(19)
    1、业务流程：
        传递用户名，密码，根据用户名查询数据库

        校验，校验用户名，校验密码，密码需要使用md5加密再校验

        校验功能，登录成功。把用户基本信息写入redis中，设置redis过期时间，

        返回token信息。Token就是Redis的key。

        需要把token写入cookie当中，当用户在不同系统之前登录时，先从cookie中拿到token，

        根据token去redis服务其中查询用户信息，有值并且用户信息没有过期，说明用户登录成功 

        成功后再重新设置redis中的用户信息的过期时间

        注意：
            cookie可以在系统之间进行交互。

    2、导入jedis依赖
        <!-- Redis客户端 -->
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
            <version>${jedis.version}</version>
        </dependency>

    3、interface
        在user-interface工程下的IUserService接口类中编写用户登录的接口方法

        /**
        * 功能19：
        *      用户登录
        * 请求：
        *      /user/login
        * 参数：
        *      String username
        *      String password
        * 返回值：
        *       封装token数据TaoTaoResult。
        *       校验用户名不存在，返回400,msg:用户名或者密码错误
        *       校验密码：密码错误，返回400，msg：用户名或者密码错误。
        * 业务流程：
        *      1、根据用户名查询用户信息(校验用户是否存在)
        *          存在：
        *              获取查询到的数据
        *              判断加密后的密码是否正确，校验通过，则等陆成功
        *          不存在：
        *              直接返回给出错误 信息
        *              
        *      2、登录成功后把用户信息放入redis服务器
        *      3、返回token，token就是redis存储用户身份信息的可以
        *      4、把返回的token写入cookie
        *      
        * 需要将用户信息写入redis
        *      redis的数据结构：key ：value
        *      key: SESSION_KEY:token
        *      value: json格式i的user对象
        */
        public ProjectResultDTO login(String username, String password);


        在user-interface工程下创建study\project\user\redis\dao\JedisDao.java、复制manager工程下的JedisDao.java即可

        public interface JedisDao {

            //抽取Jedis的常用方法
            //数据结构string
            public String set(String key, String value);
            public String get(String key);
            //自增、自减
            public Long incr(String key);
            public Long decr(String key);
            
            //数据结构hash
            public Long hset(String key, String field, String value);
            public String hget(String key, String field);
            //删除
            public Long hdel(String key, String field);
            
            //过期设置
            public Long expire(String key, int seconds);
            //查看过期时间
            public Long ttl(String key);
        }

    4、配置文件
        在user-service工程下创建redis的配置文件applicationContext-redis.xml、复制manager工程中的redis配置文件即可

            <?xml version="1.0" encoding="UTF-8"?>
            <beans xmlns="http://www.springframework.org/schema/beans"
                xmlns:context="http://www.springframework.org/schema/context" 
                xmlns:mvc="http://www.springframework.org/schema/mvc"
                xmlns:aop="http://www.springframework.org/schema/aop" 
                xmlns:tx="http://www.springframework.org/schema/tx"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
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
                
                <!--1、 测试单机版的Jedis连接Redis -->
                <!-- <bean class="redis.clients.jedis.Jedis">
                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                    <constructor-arg name="port" value="6379"></constructor-arg>
                </bean> -->
                
                <!--2、使用单机版的jedis连接池连接redis 把JedisPoolConfig对象交给spring创建 -->
                <bean id="poolConfig" class="redis.clients.jedis.JedisPoolConfig">
                    <property name="maxIdle" value="20"/>
                    <property name="maxTotal" value="1000"/>
                </bean>
                <!-- 将JedisPool对象交给spring创建 -->
                <!-- <bean class="redis.clients.jedis.JedisPool">
                    <constructor-arg name="poolConfig" ref="poolConfig"></constructor-arg>
                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                    <constructor-arg name="port" value="6379"></constructor-arg>
                </bean> -->
                
                <!-- 3、使用spring整合集群版的jedis连接池连接redis集群 -->
                <bean class="redis.clients.jedis.JedisCluster">
                    <constructor-arg name="nodes">
                        <set>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7001"/>
                            </bean>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7002"/>
                            </bean>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7003"/>
                            </bean>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7004"/>
                            </bean>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7005"/>
                            </bean>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7006"/>
                            </bean>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7007"/>
                            </bean>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7008"/>
                            </bean>
                        </set>
                    </constructor-arg>
                    <constructor-arg name="poolConfig" ref="poolConfig"/>
                </bean>
            </beans>

        在user-service工程下的resource.properties配置文件中配置存入redis缓存中的用户登录信息的key和超时时间的key

            #存入redis缓存服务器中的用户登录信息的key
            SESSION_KEY=SESSION_KEY
            #用户登录后的session过期时间(7天)
            SESSION_TIMEOUT=604800

    5、service
        在user-service工程中操作

        Jedis接口的实现类：study.project.user.redis.impl包下的JedisDaoImpl implements JedisDao、复制manager-service工程中的jedis接口实现类即可

            @Repository
            public class JedisDaoImpl implements JedisDao {

                //**************集群版*********************
                @Resource
                private JedisCluster jedisCluster;
                
                @Override
                public String set(String key, String value) {
                    String set = jedisCluster.set(key, value);
                    return set;
                }

                @Override
                public String get(String key) {
                    String get = jedisCluster.get(key);
                    return get;
                }

                @Override
                public Long incr(String key) {
                    Long incr = jedisCluster.incr(key);
                    return incr;
                }

                @Override
                public Long decr(String key) {
                    Long decr = jedisCluster.decr(key);
                    return decr;
                }

                @Override
                public Long hset(String key, String field, String value) {
                    Long hset = jedisCluster.hset(key, field, value);
                    return hset;
                }

                @Override
                public String hget(String key, String field) {
                    String hget = jedisCluster.hget(key, field);
                    return hget;
                }

                @Override
                public Long hdel(String key, String field) {
                    Long hdel = jedisCluster.hdel(key, field);
                    return hdel;
                }

                @Override
                public Long expire(String key, int seconds) {
                    Long expire = jedisCluster.expire(key, seconds);
                    return expire;
                }

                @Override
                public Long ttl(String key) {
                    Long ttl = jedisCluster.ttl(key);
                    return ttl;
                }
            }

        用户登录的接口实现类：UserServiceImpl implements IUserService

            @Value("${SESSION_KEY}")
            private String SESSION_KEY;

            @Value("${SESSION_TIMEOUT}")
            private Integer SESSION_TIMEOUT;

            @Resource
            private JedisDao jedisDao;

            /**
            * 功能19：
            *      用户登录
            * 请求：
            *      /user/login
            * 参数：
            *      String username
            *      String password
            * 返回值：
            *       封装token数据TaoTaoResult。
            *       校验用户名不存在，返回400,msg:用户名或者密码错误
            *       校验密码：密码错误，返回400，msg：用户名或者密码错误。
            * 业务流程：
            *      1、根据用户名查询用户信息(校验用户是否存在)
            *          存在：
            *              获取查询到的数据
            *              判断加密后的密码是否正确，校验通过，则等陆成功
            *          不存在：
            *              直接返回给出错误 信息
            *              
            *      2、登录成功后把用户信息放入redis服务器
            *      3、返回token，token就是redis存储用户身份信息的可以
            *      4、把返回的token写入cookie
            *      
            * 需要将用户信息写入redis
            *      redis的数据结构：key ：value
            *      key: SESSION_KEY:token
            *      value: json格式i的user对象
            */
            public ProjectResultDTO login(String username, String password) {

                TbUserExample example = new TbUserExample();

                TbUserExample.Criteria criteria = example.createCriteria();

                criteria.andUsernameEqualTo(username);

                //根据用户名查询用户信息，正常情况下只能查出一条数据
                List<TbUser> users = userMapper.selectByExample(example);

                //查询结果为空表示用户不存在
                if (users == null || users.isEmpty() || users.size() < 1) {
                    return ProjectResultDTO.build(400, "用户名或密码错误，请核对后重试！");
                }

                //获取用户信息
                TbUser user = users.get(0);

                //对密码进行md5加密
                String md5 = DigestUtils.md5DigestAsHex(password.getBytes());

                ///判断密码是否相等
                if(!md5.equals(user.getPassword())){
                    return ProjectResultDTO.build(400, "用户名或密码错误，请核对后重试！");
                }

                //返回用户已经登录的标识token，token使用UUID
                String token = UUID.randomUUID().toString();

                //将用户登录信息存入redis缓存服务器中
                //把密码置成null
                user.setPassword(null);

                //将用户登录信息存入redis缓存
                jedisDao.set(SESSION_KEY+":"+token, JsonUtils.objectToJson(user));
                //设置用户登录信息的超时时间
                jedisDao.expire(SESSION_KEY+":"+token, SESSION_TIMEOUT);

                return ProjectResultDTO.ok(token);
            }

    6、工具类
        在common工程中导入servlet-api、jsp-api依赖
            <dependency>
                <groupId>javax.servlet</groupId>
                <artifactId>servlet-api</artifactId>
                <version>${servlet-api.version}</version>
                <scope>provided</scope>
            </dependency>
            <dependency>
                <groupId>javax.servlet</groupId>
                <artifactId>jsp-api</artifactId>
                <version>${jsp-api.version}</version>
                <scope>provided</scope>
            </dependency>

        在common工程中创建cookie的工具类
            /**
            * 
            * Cookie 工具类
            *
            */
            public final class CookieUtils {

                /**
                * 得到Cookie的值, 不编码
                * 
                * @param request
                * @param cookieName
                * @return
                */
                public static String getCookieValue(HttpServletRequest request, String cookieName) {
                    return getCookieValue(request, cookieName, false);
                }

                /**
                * 得到Cookie的值,
                * 
                * @param request
                * @
                * @return
                */
                private static String getCookieValue(HttpServletRequest request, String cookieName, Boolean isDecoder) {
                    Cookie[] cookieList = request.getCookies();
                    if (cookieList == null || cookieName == null) {
                        return null;
                    }
                    String retValue = null;
                    try {
                        for (int i = 0; i < cookieList.length; i++) {
                            if (cookieList[i].getName().equals(cookieName)) {
                                if (isDecoder) {
                                    retValue = URLDecoder.decode(cookieList[i].getValue(), "UTF-8");
                                } else {
                                    retValue = cookieList[i].getValue();
                                }
                                break;
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return retValue;
                }

                /**
                * 得到Cookie的值,
                * 
                * @param request
                * @param cookieName
                * @return
                */
                public static String getCookieValue(HttpServletRequest request, String cookieName, String encodeString) {
                    Cookie[] cookieList = request.getCookies();
                    if (cookieList == null || cookieName == null) {
                        return null;
                    }
                    String retValue = null;
                    try {
                        for (int i = 0; i < cookieList.length; i++) {
                            if (cookieList[i].getName().equals(cookieName)) {
                                retValue = URLDecoder.decode(cookieList[i].getValue(), encodeString);
                                break;
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return retValue;
                }

                /**
                * 设置Cookie的值 不设置生效时间默认浏览器关闭即失效,也不编码
                */
                public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                                            String cookieValue) {
                    setCookie(request, response, cookieName, cookieValue, -1);
                }

                /**
                * 设置Cookie的值 在指定时间内生效,但不编码
                */
                public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                        String cookieValue, int cookieMaxage) {
                    setCookie(request, response, cookieName, cookieValue, cookieMaxage, false);
                }

                /**
                * 设置Cookie的值 不设置生效时间,但编码
                */
                public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                        String cookieValue, boolean isEncode) {
                    setCookie(request, response, cookieName, cookieValue, -1, isEncode);
                }

                /**
                * 设置Cookie的值 在指定时间内生效, 编码参数
                */
                public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                        String cookieValue, int cookieMaxage, boolean isEncode) {
                    doSetCookie(request, response, cookieName, cookieValue, cookieMaxage, isEncode);
                }

                /**
                * 设置Cookie的值 在指定时间内生效, 编码参数(指定编码)
                */
                public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
                        String cookieValue, int cookieMaxage, String encodeString) {
                    doSetCookie(request, response, cookieName, cookieValue, cookieMaxage, encodeString);
                }

                /**
                * 删除Cookie带cookie域名
                */
                public static void deleteCookie(HttpServletRequest request, HttpServletResponse response,
                        String cookieName) {
                    doSetCookie(request, response, cookieName, "", -1, false);
                }

                /**
                * 设置Cookie的值，并使其在指定时间内生效
                * 
                * @param cookieMaxage cookie生效的最大秒数
                */
                private static final void doSetCookie(HttpServletRequest request, HttpServletResponse response,
                        String cookieName, String cookieValue, int cookieMaxage, boolean isEncode) {
                    try {
                        if (cookieValue == null) {
                            cookieValue = "";
                        } else if (isEncode) {
                            cookieValue = URLEncoder.encode(cookieValue, "utf-8");
                        }
                        Cookie cookie = new Cookie(cookieName, cookieValue);
                        if (cookieMaxage > 0)
                            cookie.setMaxAge(cookieMaxage);
                        if (null != request) {// 设置域名的cookie
                            String domainName = getDomainName(request);
                            System.out.println(domainName);
                            if (!"localhost".equals(domainName)) {
                                cookie.setDomain(domainName);
                            }
                        }
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                /**
                * 设置Cookie的值，并使其在指定时间内生效
                * 
                * @param cookieMaxage cookie生效的最大秒数
                */
                private static final void doSetCookie(HttpServletRequest request, HttpServletResponse response,
                        String cookieName, String cookieValue, int cookieMaxage, String encodeString) {
                    try {
                        if (cookieValue == null) {
                            cookieValue = "";
                        } else {
                            cookieValue = URLEncoder.encode(cookieValue, encodeString);
                        }
                        Cookie cookie = new Cookie(cookieName, cookieValue);
                        if (cookieMaxage > 0)
                            cookie.setMaxAge(cookieMaxage);
                        if (null != request) {// 设置域名的cookie
                            String domainName = getDomainName(request);
                            System.out.println(domainName);
                            if (!"localhost".equals(domainName)) {
                                cookie.setDomain(domainName);
                            }
                        }
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                /**
                * 得到cookie的域名
                */
                private static final String getDomainName(HttpServletRequest request) {
                    String domainName = null;

                    String serverName = request.getRequestURL().toString();
                    if (serverName == null || serverName.equals("")) {
                        domainName = "";
                    } else {
                        serverName = serverName.toLowerCase();
                        serverName = serverName.substring(7);
                        final int end = serverName.indexOf("/");
                        serverName = serverName.substring(0, end);
                        final String[] domains = serverName.split("\\.");
                        int len = domains.length;
                        if (len > 3) {
                            // www.xxx.com.cn
                            domainName = "." + domains[len - 3] + "." + domains[len - 2] + "." + domains[len - 1];
                        } else if (len <= 3 && len > 1) {
                            // xxx.com or xxx.cn
                            domainName = "." + domains[len - 2] + "." + domains[len - 1];
                        } else {
                            domainName = serverName;
                        }
                    }

                    if (domainName != null && domainName.indexOf(":") > 0) {
                        String[] ary = domainName.split("\\:");
                        domainName = ary[0];
                    }
                    return domainName;
                }
            }

    7、配置文件
        在user-sso-web工程中的resource.properties配置文件中配置存入cookie中的key

        #存到cookies中的token的key
        TOKEN_COOKIE_KEY=WJ_TOKEN_KEY
    
    8、controller
        需要导入servlet-api、jsp-api依赖
            <dependency>
                <groupId>javax.servlet</groupId>
                <artifactId>servlet-api</artifactId>
                <version>${servlet-api.version}</version>
                <scope>provided</scope>
            </dependency>
            <dependency>
                <groupId>javax.servlet</groupId>
                <artifactId>jsp-api</artifactId>
                <version>${jsp-api.version}</version>
                <scope>provided</scope>
            </dependency>

        在user-sso-web工程的UserController.java中对接页面和业务层

        @Value("${TOKEN_COOKIE_KEY}")
        private String TOKEN_COOKIE_KEY;
        /**
        * 功能19：
        *      用户登录
        * 请求：
        *      /user/login
        * 参数：
        *      String username
        *      String password
        * 返回值：
        *       封装token数据TaoTaoResult。
        *       校验用户名不存在，返回400,msg:用户名或者密码错误
        *       校验密码：密码错误，返回400，msg：用户名或者密码错误。
        * 业务流程：
        *      1、根据用户名查询用户信息(校验用户是否存在)
        *          存在：
        *              获取查询到的数据
        *              判断加密后的密码是否正确，校验通过，则等陆成功
        *          不存在：
        *              直接返回给出错误 信息
        *              
        *      2、登录成功后把用户信息放入redis服务器
        *      3、返回token，token就是redis存储用户身份信息的可以
        *      4、把返回的token写入cookie
        *      
        * 需要将用户信息写入redis
        *      redis的数据结构：key ：value
        *      key: SESSION_KEY:token
        *      value: json格式i的user对象
        */
        @ResponseBody
        @RequestMapping(value = "/user/login", method = RequestMethod.POST)
        public ProjectResultDTO login(HttpServletRequest request, HttpServletResponse response, String username, String password){

            ProjectResultDTO result = userService.login(username, password);

            if (result.getStatus() == 200 && result.getData() != null) {
                //把token放入cookies、true：存入cookie中的数据加密
                CookieUtils.setCookie(request, response, TOKEN_COOKIE_KEY, result.getData().toString(), true);
            return result;
        }

    8、jsp
        通过controller请求/user/showLogin跳转到登录页面login.jsp

        到用户输入完用户名和密码点击登录时执行57行登录

        根据57行 id="loginsubmit"执行106行的点击事件方法

        执行100行登录方法，同时根据101行找到登录前的用户名和密码的校验

        根据102行找到登录方法找到85行的登录请求/user/login

        成功后执行90行跳转到门户系统：location.href = "http://localhost:8083";

        门户系统默认访问index.jsp

        indes.jsp的396行包含了footer.jsp页面：<jsp:include page="commons/footer.jsp" />

        footer.jsp页面的78行引入了taotao.js：<script type="text/javascript" src="/js/taotao.js" charset="utf-8"></script>

        taotao.js会先执行22行的方法

        根据23行的调用会执行2行的查询请求

        taotao.js的意思是获取“7“中存入cookie中的key：WJ_TOKEN_KEY，并跨服务器查询redis中的用户信息

        查到后回显

三十四、前台系统-根据cookie中token跨服务器查询redis中的用户登录信息(20)

    注意： 
        SSO主要就是依靠cookie实现的，如果cookie被禁用  则不能实现登录和单点登录

    1、jsp
        通过controller请求/user/showLogin跳转到登录页面login.jsp

        到用户输入完用户名和密码点击登录时执行57行登录

        根据57行 id="loginsubmit"执行106行的点击事件方法

        执行100行登录方法，同时根据101行找到登录前的用户名和密码的校验

        根据102行找到登录方法找到85行的登录请求/user/login

        成功后执行90行跳转到门户系统：location.href = "http://localhost:8083";

        门户系统默认访问index.jsp

        indes.jsp的396行包含了footer.jsp页面：<jsp:include page="commons/footer.jsp" />

        footer.jsp页面的78行引入了taotao.js：<script type="text/javascript" src="/js/taotao.js" charset="utf-8"></script>

        taotao.js会先执行22行的方法

        根据23行的调用会执行2行的查询请求

        taotao.js的意思是获取“7“中存入cookie中的key：WJ_TOKEN_KEY，并跨服务器查询redis中的用户信息

        查到后回显

    2、根据token查询redis--interface
        在user-interface工程的IUserService.java接口类中编写接口方法

        /**
        * 功能20：
        *      页面加载时根据token查询redis服务器中是否有用户登录信息
        * 请求：
        *      /user/token/{token}
        * 参数：
        *      String token
        *      String callback
        */
        public ProjectResultDTO userCheck(String token, String callback);

    3、service
        在user-service工程下的UserServiceImpl.java类中实现接口的实现方法

        /**
        * 功能20：
        *      页面加载时根据token查询redis服务器中是否有用户登录信息
        * 请求：
        *      /user/token/{token}
        * 参数：
        *      String token
        *      String callback
        */
        public ProjectResultDTO userCheck(String token, String callback) {

            String userCookieInfo = jedisDao.get(SESSION_KEY + ":" + token);

            if (StringUtils.isNotBlank(userCookieInfo)) {

                TbUser user = JsonUtils.jsonToPojo(userCookieInfo, TbUser.class);

                //设置用户登录信息的超时时间
                jedisDao.expire(SESSION_KEY+":"+token, SESSION_TIMEOUT);

                return ProjectResultDTO.ok(user);
            }
            return ProjectResultDTO.build(201, "您的登录信息已经过期，请重新登录！");
        }

    4、controller
        在user-sso-web工程下的UserControlle.java类中对接接口和页面

        /**
        * 功能20：
        *      页面加载时根据token查询redis服务器中是否有用户登录信息
        * 请求：
        *      /user/token/{token}
        * 参数：
        *      String token
        *      String callback
        */
        @ResponseBody
        @RequestMapping("/user/token/{token}")
        public Object userCheck(@PathVariable String token, String callback){

            ProjectResultDTO result = userService.userCheck(token, callback);

            if (StringUtils.isBlank(callback)) {
                return result;
            } else {
                //否则是一个跨域请求
                //返回json格式就是必须是callback(json) callback(userCheck)
                MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
                mappingJacksonValue.setJsonpFunction(callback);
                return mappingJacksonValue;
            }
        }

    9、测试
        启动user、user-sso-web、content、protal-web
            
        访问登录页面：/user/login
			
三十五、前台系统-搭建订单和购物车系统
    1、添加商品到购物车分析
        见图5

        购物车原理分析
            A：未登录
                添加商品到购物车时存在了cookie中

                添加商品到购物车要先判断当前cookie购物车中是否已经有当前要添加到购物车的商品
                    有：
                        数量相加
                    没有：
                        直接添加商品到cookie购物车中

                登录时：
                    要将cookie购物车中的商品同步到redis服务器的购物车中
                    redis中已有当前商品：
                        数量相加

                    redis中没有当前商品：
                        直接添加到购物车

            B：已登录
                直接将当前要购买的商品添加到redis服务器的购物车中

                添加前要判断当前redis购物车中是否已有当前有添加的商品
                    有：
                        数量相加
                    没有：
                        直接添加商品到redis购物车中
        redis购物车结构设计：
            KEY：REDIS_CART_USERID
            FILED:userId
            Value:json格式的item数据信息

    2、搭建
        2.1、搭建订单系统-22taotao-order
            继承：01taotao-parent
            打包方式：pom
            见图1

        2.2、搭建订单系统-22taotao-order-interface
            继承：22taotao-order
            打包方式：jar
            见图2

        2.3、搭建订单系统-22taotao-order-service
            继承：22taotao-order
            打包方式：war
            见图3

        2.4、搭建订单系统-22taotao-order-web
            继承：01taotao-parent
            打包方式：war
            见图4
			
	3、整合
        3.1、22taotao-order	
            pom.xml

            <!--
                需要的依赖：
                    1、抽取公共的jar
                    2、管理聚合项目(依赖、插件)
                参考：
                    content工程
            -->
            <dependencies>
                <dependency>
                    <groupId>com.taotao</groupId>
                    <artifactId>02taotao-common</artifactId>
                    <version>0.0.1-SNAPSHOT</version>
                </dependency>
            </dependencies>

            <build>
                <plugins>
                    <plugin>
                        <groupId>org.apache.tomcat.maven</groupId>
                        <artifactId>tomcat7-maven-plugin</artifactId>
                        <version>2.2</version>
                        <configuration>
                            <!-- 定义项目发布路径，相当于直接放到tomcat的ROOT目录，在访问时不需要项目名称，直接访问路径就行 -->
                            <path>/</path>
                            <!-- 配置tomcat端口 -->
                            <port>8090</port>
                        </configuration>
                    </plugin>
                </plugins>
            </build>

        3.2、22taotao-order-interface
            pom.xml

            <!--
                所需坐标：
                    pojo
                参考：
                    manager-interface
            -->
            <dependencies>
                <dependency>
                    <groupId>com.taotao</groupId>
                    <artifactId>04taotao-manager-pojo</artifactId>
                    <version>0.0.1-SNAPSHOT</version>
                </dependency>
            </dependencies>

        3.3、22taotao-order-service
            3.3.1、配置文件
                复制content-service工程的配置文件，修改即可

                applicationContext-dao.xml
                applicationContext-service.xml
                jdbc.properties
                log4j.properties
                resource.properties(删除不必要的数据)
                sqlMapConfig.xml

                除了applicationContext-service.xml配置文件以外其他的配置文件不需要修改

                applicationContext-service.xml
                    <!-- 切面 -->
                    <aop:config>
                        <aop:advisor advice-ref="txAdvice" pointcut="execution(* study.project.order.service.*.*(..))"/>
                    </aop:config>

                    <!-- 用dubbo协议在20880端口暴露服务 ：此端口是提供给消费者的，消费者通过dubbo协议调用zk服务 -->
                    <dubbo:protocol name="dubbo" port="20884" /><!-- 同一个tomcat下端口不能重复 -->

            3.3.2、web.xml
                复制content-service工程的web.xml，修改即可

            3.3.3、pom.xml
                content-service工程的pom.xml配置文件，除了依赖包不一样以外 其他都一样

                <!-- 依赖dao、间接依赖pojo -->
                <dependency>
                    <groupId>com.taotao</groupId>
                    <artifactId>05taotao-manager-dao</artifactId>
                    <version>0.0.1-SNAPSHOT</version>
                </dependency>

                <!-- 依赖接口 -->
                <dependency>
                    <groupId>com.taotao</groupId>
                    <artifactId>23taotao-order-interface</artifactId>
                    <version>0.0.1-SNAPSHOT</version>
                </dependency>

        3.4、22taotao-order-web	
            3.4.1、配置文件
                复制portotal-web工程的配置文件修改即可

                log4j.properties
                resource.properties(删除不必要的数据)
                springmvc.xml

                修改springmvc.xml配置文件，其他不需要修改

                <!-- 引用服务 -->
                <dubbo:application name="taotao-user-sso-web"/>     

            3.4.2、web.xml
                复制portotal-web工程的web.xml文件修改即可 

            3.4.3、pom.xml
                修改依赖接口，和tomcat端口，其他不变

                <!-- 依赖接口 -->
                <dependency>
                    <groupId>com.taotao</groupId>
                    <artifactId>23taotao-order-interface</artifactId>
                    <version>0.0.1-SNAPSHOT</version>
                </dependency>

            3.4.4、复制页面
                复制必要的页面到webapp和WEB-INF下即可
                位置：
                    JAVA/ReStudy/DataWord/30WJ商城/资料/静态页面/购物车成功页面
			
三十六、前台系统-添加购物车(未登录)(21)

    在order-web工程下

    1、pom.xml
        <dependency>
            <groupId>com.taotao</groupId>
            <artifactId>08taotao-manager-interface</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>

    2、springmvc.xml
        <!-- 引用服务(引用manager工程中的根据ID查询商品信息服务) -->
        <dubbo:reference id="userService" interface="study.project.ItemService" version="1.0.0" timeout="100000"/>
    
    3、resource.properties
        #cookie中的购物车的唯一key
        CART_KEY=CART_KEY

    4、controller
        创建study.project.order.controller包，在此包下创建CartController.java

        /**
        * 购物车操作
        * Created by panhusun on 2017/9/9.
        */
        @Controller
        public class CartController {

            @Value("${CART_KEY}")
            private String CART_KEY;

            @Resource
            private ItemService itemService;

            /**
            * 功能21：
            *      添加购物车(未登录 添加到cookie)
            * URL:
            *      /cart/add/${item.id}/" + $("#buy-num").val() + ".html
            * param：
            *      URL模板映射
            *      itemId
            *      num
            * return：
            *      string 返回到购物车添加成功页面
            * 业务流程：
            *      先查询购物车(cookie)商品列表
            *      判断购物车中是否有当前有添加的商品
            *      有：数量相加
            *      没有：直接添加到cookie购物车
            */
            @RequestMapping("/cart/add/{itemId}/{num}")
            public String addCart(HttpServletRequest request, HttpServletResponse response, @PathVariable Long itemId, @PathVariable Long num){

                //查询购物车
                List<TbItem> itemList = this.getCookieValue(request);

                //判断购物车中是否有当前有添加的商品
                boolean flag = false;

                for (TbItem tbItem : itemList) {
                    if (tbItem.getId() == itemId.longValue()) {
                        //存在，数量相加
                        tbItem.setNum(tbItem.getNum() + num.intValue());

                        flag = true;

                        break;
                    }
                }

                //不存在
                if (!flag) {
                    //根据itemId查询数据库
                    TbItem item = itemService.findItemByID(itemId);

                    //设置购买数量
                    item.setNum(num.intValue());

                    //放回购物车列表
                    itemList.add(item);
                }

                //将购物车列表加密存入cookie
                CookieUtils.setCookie(request, response, CART_KEY, JsonUtils.objectToJson(itemList), true);

                //返回到购物车成功页面cartSuccess
                return "cartSuccess";
            }

            //查询cookie中的购物车列表
            private List<TbItem> getCookieValue(HttpServletRequest request) {

                //根据key加密查询cookie
                String cookieValue = CookieUtils.getCookieValue(request, CART_KEY, true);

                if (StringUtils.isBlank(cookieValue)) {
                    //如果为空返回一个空列表
                    return new ArrayList<>();
                }

                //将json转成list集合
                List<TbItem> tbItems = JsonUtils.jsonToList(cookieValue, TbItem.class);

                return tbItems;
            }
        }

    5、jsp
        访问item-web工程的item.jsp页面

        点击233行“加入购物车”，执行点击事件addCart()

        执行461行，跳转到order-web工程

        带参数itemId和num

    6、测试
        见图6
            
三十七、前台系统-查询购物车列表(未登录)(22)
	1、controller
        在order-web工程中的CartController.java中

        /**
        * 功能22：
        *      查询购物车列表
        * URL:
        *      /cart/cart.html
        * 参数：
        *      Model
        * 返回值：
        *      String  购物车列表页 order-cart.jsp
        *
        */
        @RequestMapping("/cart/cart")
        public String findCart(HttpServletRequest request, Model model){

            //从cookie中获取购物车列表
            List<TbItem> cartList = this.getCookieValue(request);

            //放入Model中
            model.addAttribute("cartList", cartList);

            return "cart";
        }

    2、jsp
        添加购物车成功以后跳转到cartSuccess.jsp页面

        点击47行“去购物车结算”发送请求/cart/cart.html

        从cookie中获取购物城列表，并将数据放入Model中供页面回显
			
	3、测试
        见图7

三十八、前台系统-购物车数量加减(未登录)(23)

    在order-web工程中操作

    1、controller
        /**
        * 功能23：
        *      购物车数量加减
        * URL:
        *      /cart/update/num/{itemId}/{num}.html"
        * 参数：
        *      URL模板映射
        *      itemId
        *      num
        * 返回值：
        *      ProjectResultDTO.ok();
        * 业务流程：
        *      1、从cookie中获取商品列表
        *      2、根据cookie中商品ID判断要修改哪一个商品
        *      3、将修改好的商品放回cookie
        */
        @ResponseBody
        @RequestMapping(value = "/cart/update/num/{itemId}/{num}", method = RequestMethod.POST)
        public ProjectResultDTO updateItemNum(HttpServletRequest request, HttpServletResponse response, @PathVariable Long itemId, @PathVariable Integer num){
            //1、从cookie中获取购物车商品列表集合
            List<TbItem> cartList = this.getCookieValue(request);

            //2、遍历购物车列表，判断要修改哪一个商品
            for (TbItem item : cartList) {
                if (item.getId() == itemId.longValue()) {
                    item.setNum(num);
                    break;
                }
            }

            //3、将修改好的商品加密放回cookie
            CookieUtils.setCookie(request, response, CART_KEY, JsonUtils.objectToJson(cartList), true);

            return ProjectResultDTO.ok();
        }

    2、jsp
        访问购物车列表页cart.jsp

        cart.jsp页面中的161行引用的cart.js

        当点击购物车中的加减按钮时会根据class选择器执行cart.js中的6行或者13行进行加减操作

        请求返回后会异步刷新需要改变的内容

        注意：
            请求后缀是.html的不支持json数据，所以需要改成别的(随意)

            同时修改web.xml的拦截

            添加一个拦截(可以是多个拦截)：
                <servlet-mapping>
                    <servlet-name>springmvc</servlet-name>
                    <!-- 购物车数量加减时使用 -->
                    <url-pattern>*.do</url-pattern>
                </servlet-mapping>

三十九、前台系统-购物车删除商品(未登录)(24)

    在order-web工程中操作

    1、controller
        /**
        * 功能24：
        *      删除购物车中的商品
        * URL:
        *      /cart/delete/${cart.id}.html
        * 参数：
        *      URL模板映射
        *      itemId
        * 返回值：
        *      String 重定向到购物车列表
        * 业务流程：
        *      1、从cookie中获取商品列表
        *      2、根据cookie中商品ID判断要修改哪一个商品
        *      3、将修改好的商品放回cookie
        */
        @RequestMapping(value = "/cart/delete/{itemId}")
        public String deleteCartItemById(HttpServletRequest request, HttpServletResponse response, @PathVariable Long itemId){
            //1、从cookie中获取购物车商品列表集合
            List<TbItem> cartList = this.getCookieValue(request);

            //2、遍历购物车列表，判断要修改哪一个商品
            for (TbItem item : cartList) {
                if (item.getId() == itemId.longValue()) {
                    cartList.remove(item);
                    break;
                }
            }

            //3、将修改好的商品加密放回cookie
            CookieUtils.setCookie(request, response, CART_KEY, JsonUtils.objectToJson(cartList), true);

            //重定向的上面获取购物车列表的请求
            return "redirect:/cart/cart.html";
        }

    2、jsp
        在cart.jsp页面中点击删除按钮，会执行96行当前标签中的 href="/cart/delete/${cart.id}.html"

        删除成功后需要重定向到获取购物车列表的请求，显示删除效果


四十、前台系统-购物车结算(未登录)(25)
    1、结算业务流程分析
        订单提交>订单详情页>支付>支付成功

        购物车列表点击提交 > 使用拦截器拦截订单请求判断用户是否登录
            登录：
                直接跳转到订单详情 > 确认订单信息 > 支付

                订单详情页应该有的信息：
                    收货地址
                    商品信息
                    购物车信息
                    支付信息

            未登录：
                根据TT_TOKEN获取cookie中token
                    没有：
                        需要跳转到登录页面登录
                    有：
                        标识登录过

                        如果redis中数据过期，需要跳转到登录页面重新登录。
                
                        如果redis中数据没有过期，放行拦截器，跳转到订单详情页

    2、redis
        在order-web工程中操作

        因为要查询redis中用户信息所以要用到redis

        2.1、JedisService.java
            在web工程下创建study.project.redis.service包，在此包下创建操作redis的常用方法接口JedisService.java

            public interface JedisService {

                //抽取Jedis的常用方法
                //数据结构string
                public String set(String key, String value);
                public String get(String key);
                //自增、自减
                public Long incr(String key);
                public Long decr(String key);
                
                //数据结构hash
                public Long hset(String key, String field, String value);
                public String hget(String key, String field);
                //删除
                public Long hdel(String key, String field);
                
                //过期设置
                public Long expire(String key, int seconds);
                //查看过期时间
                public Long ttl(String key);
            }

        2.2、JedisServiceImpl.java
            在 order-web工程下创建study.project.redis.impl包，在此包下创建JedisServiceImpl.java实现redis的接口

            @Repository
            public class JedisServiceImpl implements JedisService {

                //**************集群版*********************
                @Resource
                private JedisCluster jedisCluster;
                
                @Override
                public String set(String key, String value) {
                    String set = jedisCluster.set(key, value);
                    return set;
                }

                @Override
                public String get(String key) {
                    String get = jedisCluster.get(key);
                    return get;
                }

                @Override
                public Long incr(String key) {
                    Long incr = jedisCluster.incr(key);
                    return incr;
                }

                @Override
                public Long decr(String key) {
                    Long decr = jedisCluster.decr(key);
                    return decr;
                }

                @Override
                public Long hset(String key, String field, String value) {
                    Long hset = jedisCluster.hset(key, field, value);
                    return hset;
                }

                @Override
                public String hget(String key, String field) {
                    String hget = jedisCluster.hget(key, field);
                    return hget;
                }

                @Override
                public Long hdel(String key, String field) {
                    Long hdel = jedisCluster.hdel(key, field);
                    return hdel;
                }

                @Override
                public Long expire(String key, int seconds) {
                    Long expire = jedisCluster.expire(key, seconds);
                    return expire;
                }

                @Override
                public Long ttl(String key) {
                    Long ttl = jedisCluster.ttl(key);
                    return ttl;
                }
            }

        2.3、applicationContext-redis.xml
            在order-web工程下添加redis的配置文件

            <?xml version="1.0" encoding="UTF-8"?>
            <beans xmlns="http://www.springframework.org/schema/beans"
                xmlns:context="http://www.springframework.org/schema/context" 
                xmlns:mvc="http://www.springframework.org/schema/mvc"
                xmlns:aop="http://www.springframework.org/schema/aop" 
                xmlns:tx="http://www.springframework.org/schema/tx"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
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
                
                <!--1、 测试单机版的Jedis连接Redis -->
                <!-- <bean class="redis.clients.jedis.Jedis">
                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                    <constructor-arg name="port" value="6379"></constructor-arg>
                </bean> -->
                
                <!--2、使用单机版的jedis连接池连接redis 把JedisPoolConfig对象交给spring创建 -->
                <bean id="poolConfig" class="redis.clients.jedis.JedisPoolConfig">
                    <property name="maxIdle" value="20"/>
                    <property name="maxTotal" value="1000"/>
                </bean>
                <!-- 将JedisPool对象交给spring创建 -->
                <!-- <bean class="redis.clients.jedis.JedisPool">
                    <constructor-arg name="poolConfig" ref="poolConfig"></constructor-arg>
                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                    <constructor-arg name="port" value="6379"></constructor-arg>
                </bean> -->
                
                <!-- 3、使用spring整合集群版的jedis连接池连接redis集群 -->
                <bean class="redis.clients.jedis.JedisCluster">
                    <constructor-arg name="nodes">
                        <set>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7001"/>
                            </bean>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7002"/>
                            </bean>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7003"/>
                            </bean>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7004"/>
                            </bean>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7005"/>
                            </bean>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7006"/>
                            </bean>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7007"/>
                            </bean>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7008"/>
                            </bean>
                        </set>
                    </constructor-arg>
                    <constructor-arg name="poolConfig" ref="poolConfig"/>
                </bean>
            </beans>

        2.4、web.xml
            在order-web工程的web.xml中加载spring的配置文件，同时修改优先级

            <!-- 加载springmvc配置文件 -->
            <servlet>
                <servlet-name>springmvc</servlet-name>
                <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
                <init-param>
                    <param-name>contextConfigLocation</param-name>
                    <param-value>classpath:springmvc.xml,classpath:applicationContext-redis.xml</param-value>
                </init-param>
                <!--应为要随着web容器的启动加载spring的配置文件所有要配置优先级-->
                <load-on-startup>1</load-on-startup>
            </servlet>

    3、resource.properties
#cookie中的购物车的唯一key
CART_KEY=CART_KEY
#存到cookies中的token的key
TOKEN_COOKIE_KEY=WJ_TOKEN_KEY
#SSO重定向请求
SSO_URL=http://localhost:8089/user/showLogin
#存入redis缓存服务器中的用户登录信息的key
SESSION_KEY=SESSION_KEY

    4、LoginInterceptor.java
在order-web工程下创建study.project.interceptor包，在此包下创建LoginInterceptor.java拦截器

/**
* 登录拦截器
* Created by panhusun on 2017/9/10.
*/
public class LoginInterceptor implements HandlerInterceptor{

    //存入cookie中的用户唯一标识token
    @Value("${TOKEN_COOKIE_KEY}")
    private String TOKEN_COOKIE_KEY;

    //重定向到登录页面的URL
    @Value("${SSO_URL}")
    private String SSO_URL;

    @Resource
    private JedisService jedisService;

    //存入redis和缓存服务器中的用户信息的唯一key
    @Value("${SESSION_KEY}")
    private String SESSION_KEY;
    /**
    * 业务流程：
    *      1、判断cookie中是否有当前用户的token
    *          有：仅仅表示登陆过
    *          没有：重新登录。登录成功后必须跳转到历史操作页面
    *      2、判断redis中用户身份信息是否过期
    *          没有：放行，登录成功
    *          过期：重新登录。登录成功后必须跳转到历史操作页面
    * @param request
    * @param response
    * @param handler
    * @return
    * @throws Exception
    */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //获取cookie中的用户唯一标识token
        String userToken = CookieUtils.getCookieValue(request, TOKEN_COOKIE_KEY);

        //判断token是否为空
        if (StringUtils.isBlank(userToken)) {//为空,重新登录
            //获取当前操作请求地址
            String url = request.getRequestURL().toString();
            //跳转登录页面，携带历史操作地址
            response.sendRedirect(SSO_URL+"?redirectURL="+url);
            //拦截
            return false;
        }

        //不为空
        //根据token查询redis服务器
        String userRedis = jedisService.get(SESSION_KEY + ":" + userToken);

        //判断redis中用户信息是否过期
        if (StringUtils.isBlank(userRedis)) {//过期
            //获取当前操作请求地址
            String url = request.getRequestURL().toString();
            //跳转登录页面，携带历史操作地址
            response.sendRedirect(SSO_URL + "?redirectURL=" + url);
            //拦截
            return false;
        }

        //没有过期,登录成功，将用户信息存入request
        request.setAttribute("user", JsonUtils.jsonToPojo(userRedis, TbUser.class));

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}

    5、springmvc.xml
        <!--配置登录拦截器-->
        <mvc:interceptors>
            <mvc:interceptor>
                <mvc:mapping path="/order/**"/>
                <bean class="study.project.interceptor.LoginInterceptor"></bean>
            </mvc:interceptor>
        </mvc:interceptors>

    6、controller
        在orde-web工程下的study.project.order.controller.CartController.java下添加跳转到订单详情的方法

        /**
        * 功能25：
        *      去结算 - 跳转待订单详情页
        * URL:
        *      /order/order-cart.html
        * return：
        *      String  跳转到订单详情页
        */
        @RequestMapping("/order/order-cart")
        public String orderCart(HttpServletRequest request, HttpServletResponse response, Model model){

            //从cookie中获取购物车列表数据
            List<TbItem> cartList = this.getCookieValue(request);

            //从request中获取用户身份信息
            TbUser user = (TbUser) request.getAttribute("user");

            //放入model中
            model.addAttribute("user", user);
            model.addAttribute("cartList", cartList);

            return "order-cart";
        }

    7、jsp
        点击购物车“去结算”执行138行代码，发送请求/order/order-cart.html

        跳转前后先经过拦截器，判断是否登录

        未登录跳转到user-sso-web工程的login.jsp，需要带参数历史操作页面(登录完成后调回历史操作页面)

        把历史操作页面当做参数传递，参数名redirect

        在user-sso-web工程的login.jps中的点击登录时会根据id执行106行代码

        根据106行代码执行100行代码，

        根据106行代码执行85代码，

        成功后先判断从request(70行代码获取的值)中拿到的redirectUrl是否为空

        没值跳转到首页

        有值就跳转到redirectUrl对应的值的页面

        所以修改修改user-sso-web工程的study.project.user.sso.controller.PageController.java中的showLogin方法

        /**
        * 跳转到登录
        * /user/showLogin
        * @return
        */
        @RequestMapping("/user/showLogin")
        public String showLogin(Model model, String redirect){

            model.addAttribute("redirect", redirect);

            return "login";
        }

    8、测试
        见图8

四十一、前台系统-提交订单(未登录)(26)
    1、分析
        提交订单需要操作三张表，所以需要封装一个包装类对象分装三张表的数据

        见图9

    2、jsp
        访问order-web工程中的order-cart.jsp点击280行的“提交订单按钮”，

        执行一个点击事件onclick="$('#orderForm').submit()"

        找到36行发请求action="/order/create.html"

    3、pojo
        在order-interface工程中创建包装类对象OrderInfo.java implements Serializable(order-web工程依赖interface接口依赖，所以在web工程中页可以使用)

        //变量名称必须和页面保持一样才能封装成功
        //封装订单对象
        private TbOrder order;
        //封装订单明细对象
        private List<TbOrderItem> orderItems;
        //封装收货地址对象
        private TbOrderShipping orderShipping;

    4、jedis
        4.1、JedisDao.java
            在order-interface工程中创建 study.project.order.redis.dao.JedisDao.java

            public interface JedisDao {

                //抽取Jedis的常用方法
                //数据结构string
                public String set(String key, String value);
                public String get(String key);
                //自增、自减
                public Long incr(String key);
                public Long decr(String key);
                
                //数据结构hash
                public Long hset(String key, String field, String value);
                public String hget(String key, String field);
                //删除
                public Long hdel(String key, String field);
                
                //过期设置
                public Long expire(String key, int seconds);
                //查看过期时间
                public Long ttl(String key);
            }

        4.2、JedisDaoImpl.java
            在order-service工程中创建study.project.order.redis.service.impl.JedisDaoImpl.java

            @Repository
            public class JedisDaoImpl implements JedisDao {

                //**************集群版*********************
                @Resource
                private JedisCluster jedisCluster;
                
                @Override
                public String set(String key, String value) {
                    String set = jedisCluster.set(key, value);
                    return set;
                }

                @Override
                public String get(String key) {
                    String get = jedisCluster.get(key);
                    return get;
                }

                @Override
                public Long incr(String key) {
                    Long incr = jedisCluster.incr(key);
                    return incr;
                }

                @Override
                public Long decr(String key) {
                    Long decr = jedisCluster.decr(key);
                    return decr;
                }

                @Override
                public Long hset(String key, String field, String value) {
                    Long hset = jedisCluster.hset(key, field, value);
                    return hset;
                }

                @Override
                public String hget(String key, String field) {
                    String hget = jedisCluster.hget(key, field);
                    return hget;
                }

                @Override
                public Long hdel(String key, String field) {
                    Long hdel = jedisCluster.hdel(key, field);
                    return hdel;
                }

                @Override
                public Long expire(String key, int seconds) {
                    Long expire = jedisCluster.expire(key, seconds);
                    return expire;
                }

                @Override
                public Long ttl(String key) {
                    Long ttl = jedisCluster.ttl(key);
                    return ttl;
                }
            }

        4.3、applicationContext-redis.xml
            在order-service工程中创建redis所需的配置文件

            <?xml version="1.0" encoding="UTF-8"?>
            <beans xmlns="http://www.springframework.org/schema/beans"
                xmlns:context="http://www.springframework.org/schema/context" 
                xmlns:mvc="http://www.springframework.org/schema/mvc"
                xmlns:aop="http://www.springframework.org/schema/aop" 
                xmlns:tx="http://www.springframework.org/schema/tx"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
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
                
                <!--1、 测试单机版的Jedis连接Redis -->
                <!-- <bean class="redis.clients.jedis.Jedis">
                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                    <constructor-arg name="port" value="6379"></constructor-arg>
                </bean> -->
                
                <!--2、使用单机版的jedis连接池连接redis 把JedisPoolConfig对象交给spring创建 -->
                <bean id="poolConfig" class="redis.clients.jedis.JedisPoolConfig">
                    <property name="maxIdle" value="20"/>
                    <property name="maxTotal" value="1000"/>
                </bean>
                <!-- 将JedisPool对象交给spring创建 -->
                <!-- <bean class="redis.clients.jedis.JedisPool">
                    <constructor-arg name="poolConfig" ref="poolConfig"></constructor-arg>
                    <constructor-arg name="host" value="192.168.254.66"></constructor-arg>
                    <constructor-arg name="port" value="6379"></constructor-arg>
                </bean> -->
                
                <!-- 3、使用spring整合集群版的jedis连接池连接redis集群 -->
                <bean class="redis.clients.jedis.JedisCluster">
                    <constructor-arg name="nodes">
                        <set>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7001"/>
                            </bean>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7002"/>
                            </bean>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7003"/>
                            </bean>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7004"/>
                            </bean>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7005"/>
                            </bean>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7006"/>
                            </bean>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7007"/>
                            </bean>
                            <bean class="redis.clients.jedis.HostAndPort">
                                <constructor-arg name="host" value="192.168.254.66"/>
                                <constructor-arg name="port" value="7008"/>
                            </bean>
                        </set>
                    </constructor-arg>
                    <constructor-arg name="poolConfig" ref="poolConfig"/>
                </bean>
            </beans>

        4.4、jedis依赖
            在order-service工程的pom.xml中引入jedis的配置文件

            <!-- Redis客户端 -->
            <dependency>
                <groupId>redis.clients</groupId>
                <artifactId>jedis</artifactId>
                <version>${jedis.version}</version>
            </dependency>

    5、interface
        在order-interface工程中study.project.order.service.OrderService.java

        /**
        * 提交订单
        * Created by panhusun on 2017/9/10.
        */
        public interface OrderService {
            /**
            * 功能26：
            *      提交订单
            * URL:
            *      /order/create.html
            * param：
            *
            * return：
            *      页面回显数据:
            *          orderId
            *          时间
            *          金额(页面传递)
            *      success成功页面
            * 订单号设置：
            *      1、sql查询获取初始值，每次加1(缺点：加重的数据库的读写压力)
            *      2、redis自增方法，每一次生成一个订单，自动加1
            *          redis订单号设计：
            *              key:ORDER_NUM
            *              value:初始值(START_ORDER_NUM)
            * 业务流程;
            *      需要提交三张表的数据，所以需要封装一个包装类对象来封装三张表的数据
            */
            String orderCreate(OrderInfo orderInfo);
        }

    6、service
        在order-service工程中创建study.project.order.service.impl.OrderServiceImpl.java

        /**
        * 订到操作的实现类
        * Created by panhusun on 2017/9/10.
        */
        @Service
        public class OrderServiceImpl implements OrderService {

            //保存三张表数据
            @Resource
            private TbOrderMapper orderMapper;

            @Resource
            private TbOrderItemMapper orderItemMapper;

            @Resource
            private TbOrderShippingMapper orderShippingMapper;

            //存入redis服务器中的订单号的key
            @Value("${ORDER_NUM}")
            private String ORDER_NUM;

            //存入redis服务器中的订单号的可以对应的初始值
            @Value("${START_ORDER_NUM}")
            private String START_ORDER_NUM;

            //订单商品id，在redis中自增1时是1
            @Value("${ORDER_DETAIL_ID}")
            private String ORDER_DETAIL_ID;


            @Resource
            private JedisDao jedisDao;

            /**
            * 功能26：
            *      提交订单
            * URL:
            *      /order/create.html
            * param：
            *
            * return：
            *      页面回显数据:
            *          orderId
            *          时间
            *          金额(页面传递)
            *      success成功页面
            * 订单号设置：
            *      1、sql查询获取初始值，每次加1(缺点：加重的数据库的读写压力)
            *      2、redis自增方法，每一次生成一个订单，自动加1
            *          redis订单号设计：
            *              key:ORDER_NUM
            *              value:初始值(START_ORDER_NUM)
            * 业务流程;
            *      需要提交三张表的数据，所以需要封装一个包装类对象来封装三张表的数据
            */
            @Override
            public String orderCreate(OrderInfo orderInfo) {

                //****************************保存order对象***************start*************
                //获取order对象
                TbOrder order = orderInfo.getOrder();
                //设计orderId
                //从redis中获取orderId
                String orderId = jedisDao.get(ORDER_NUM);

                //判断redis中orderId是否存在
                if (StringUtils.isBlank(orderId)) {//为空
                    //设置一个初始值
                    jedisDao.set(ORDER_NUM, START_ORDER_NUM);
                }

                //新增订单时redis中的orderId自增1
                orderId = jedisDao.incr(ORDER_NUM).toString();

                //不全参数 - orderId
                order.setOrderId(orderId);
                //邮费
                order.setPostFee("0");
                //订单状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
                order.setStatus(1);
                //修改日期
                order.setUpdateTime(new Date());
                //创建日期
                order.setCreateTime(new Date());

                //保存
                orderMapper.insert(order);
                //****************************保存order对象***************end*************

                //****************************保存订单商品明细对象***************start*************
                //获取OrderItem对象
                List<TbOrderItem> orderItemList = orderInfo.getOrderItems();

                for (TbOrderItem orderItem : orderItemList) {

                    //意思是从redis中获取，没有就直接设置值为1，以后每次自增1
                    Long orderItemId = jedisDao.incr(ORDER_DETAIL_ID);

                    //设置订单商品明细ID
                    orderItem.setId(orderItemId.toString());
                    //设置订单id
                    orderItem.setOrderId(orderId);

                    //保存
                    orderItemMapper.insert(orderItem);
                }
                //****************************保存订单商品明细对象***************end*************

                //****************************保存收货地址对象***************start*************
                //获取收货地址对象
                TbOrderShipping orderShipping = orderInfo.getOrderShipping();

                orderShipping.setOrderId(orderId);
                orderShipping.setCreated(new Date());
                orderShipping.setUpdated(new Date());

                orderShippingMapper.insert(orderShipping);
                //****************************保存收货地址对象***************end*************

                return orderId;
            }
        }

    7、配置文件
        在order-service工程的spplicationContext-service.xml配置文件中发布服务

        <!-- 创建需要发布服务 提交订单-->
        <bean id="orderServiceImpl" class="study.project.order.service.impl.OrderServiceImpl"/>
        <!-- 发布服务 user工程 -->
        <dubbo:service ref="orderServiceImpl" interface="study.project.order.service.OrderService" version="1.0.0" retries="0" timeout="100000"/>

    8、controller
        在order-web工程中创建study.project.order.controller.OrderController.java

        /**
        * 订单操作
        * Created by panhusun on 2017/9/10.
        */
        @Controller
        public class OrderController {

            @Resource
            private OrderService orderService;

            /**
            * 功能26：
            *      提交订单
            * URL:
            *      /order/create.html
            * param：
            *
            * return：
            *      页面回显数据:
            *          orderId
            *          时间
            *          金额(页面传递)
            *      success成功页面
            * 订单号设置：
            *      1、sql查询获取初始值，每次加1(缺点：加重的数据库的读写压力)
            *      2、redis自增方法，每一次生成一个订单，自动加1
            *          redis订单号设计：
            *              key:ORDER_NUM
            *              value:初始值(START_ORDER_NUM)
            * 业务流程;
            *      需要提交三张表的数据，所以需要封装一个包装类对象来封装三张表的数据
            */
            @RequestMapping("/order/create")
            public String createOrder(OrderInfo orderInfo, Model model) {

                //订单号
                String orderId = orderService.orderCreate(orderInfo);

                //订单号
                model.addAttribute("orderId", orderId);
                //支付金额
                model.addAttribute("payment", orderInfo.getOrder().getPayment());
                //当前时间往后延长三天
                DateTime dateTime = new DateTime();
                dateTime.plusDays(3);

                model.addAttribute("date", dateTime);

                return "success";
            }

        }

    9、配置文件
        在order-web工程中的springmvc.xml配置文件中引用dubbo服务

        <!--提交订单服务-->
        <dubbo:reference id="orderService" interface="study.project.order.service.OrderService" version="1.0.0" timeout="100000"/>
    
    9、测试
        见图10
    





























