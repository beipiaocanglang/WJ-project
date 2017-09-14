package freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.*;

/**
 *
 * Created by canglang on 2017/9/3.
 */
public class FirstFreemarkerTest {

    /**
     * freemarker的第一个测试案例
     * @throws Exception
     */
    @Test
    public void test01() throws Exception {

        //创建Freemarker的配置对象，指定Freemarker版本
        Configuration configuration = new Configuration(Configuration.getVersion());

        //指定Freemarker模版路径
        configuration.setDirectoryForTemplateLoading(new File("/Users/panhusun/canglang/java_project/study-project/WJ-project/project/17taotao-item-web/src/main/webapp/WEB-INF/jsp/freemarker"));

        //指定模版编码
        configuration.setDefaultEncoding("UTF-8");

        //根据摹本路径获取模版文件对象
        Template template = configuration.getTemplate("hello.ftl");

        //创建模版数据，注意：模版数据必须是map
        Map<String, Object> maps = new HashMap<>();
        maps.put("hello", "这是Freemarker的第一个测试案例！！！！！！！！！");

        //创建模版输出路径
        Writer out = new FileWriter(new File("/Users/panhusun/canglang/java_project/study-project/WJ-project/project/17taotao-item-web/src/main/webapp/WEB-INF/jsp/freemarker/out/hello.html"));

        //生成html文件,参数意义：参数1：放入模版中的数据、参数2：模版输出路径
        template.process(maps, out);

        //关闭资源
        out.close();
    }

    /**
     * freemarker测试数据类型--pojo
     * @throws Exception
     */
    @Test
    public void testPojo() throws Exception {

        //创建Freemarker的配置对象，指定Freemarker版本
        Configuration configuration = new Configuration(Configuration.getVersion());

        //指定Freemarker模版路径
        configuration.setDirectoryForTemplateLoading(new File("/Users/panhusun/canglang/java_project/study-project/WJ-project/project/17taotao-item-web/src/main/webapp/WEB-INF/jsp/freemarker"));

        //指定模版编码
        configuration.setDefaultEncoding("UTF-8");

        //根据摹本路径获取模版文件对象
        Template template = configuration.getTemplate("pojo.ftl");

        //创建模版数据，注意：模版数据必须是map
        Map<String, Object> maps = new HashMap<>();

        Person person = new Person();
        person.setUsername("张三丰");
        person.setAddress("武当山");
        person.setAge(26);

        maps.put("person", person);

        //创建模版输出路径
        Writer out = new FileWriter(new File("/Users/panhusun/canglang/java_project/study-project/WJ-project/project/17taotao-item-web/src/main/webapp/WEB-INF/jsp/freemarker/out/pojo.html"));

        //生成html文件,参数意义：参数1：放入模版中的数据、参数2：模版输出路径
        template.process(maps, out);

        //关闭资源
        out.close();
    }

    /**
     * freemarker测试数据类型--list
     * @throws Exception
     */
    @Test
    public void testList() throws Exception {

        //创建Freemarker的配置对象，指定Freemarker版本
        Configuration configuration = new Configuration(Configuration.getVersion());

        //指定Freemarker模版路径
        configuration.setDirectoryForTemplateLoading(new File("/Users/panhusun/canglang/java_project/study-project/WJ-project/project/17taotao-item-web/src/main/webapp/WEB-INF/jsp/freemarker"));

        //指定模版编码
        configuration.setDefaultEncoding("UTF-8");

        //根据摹本路径获取模版文件对象
        Template template = configuration.getTemplate("list.ftl");

        //创建模版数据，注意：模版数据必须是map
        Map<String, Object> maps = new HashMap<>();

        List<Person> pList = new ArrayList<>();

        Person person1 = new Person();
        person1.setUsername("张三丰");
        person1.setAddress("武当山");
        person1.setAge(26);

        Person person2 = new Person();
        person2.setUsername("赵敏");
        person2.setAddress("大都");
        person2.setAge(23);

        Person person3 = new Person();
        person3.setUsername("周芷若");
        person3.setAddress("全真教");
        person3.setAge(16);

        pList.add(person1);
        pList.add(person2);
        pList.add(person3);

        maps.put("pList", pList);

        //创建模版输出路径
        Writer out = new FileWriter(new File("/Users/panhusun/canglang/java_project/study-project/WJ-project/project/17taotao-item-web/src/main/webapp/WEB-INF/jsp/freemarker/out/list.html"));

        //生成html文件,参数意义：参数1：放入模版中的数据、参数2：模版输出路径
        template.process(maps, out);

        //关闭资源
        out.close();
    }

    /**
     * freemarker测试数据类型--日期
     * @throws Exception
     */
    @Test
    public void testDate() throws Exception {

        //创建Freemarker的配置对象，指定Freemarker版本
        Configuration configuration = new Configuration(Configuration.getVersion());

        //指定Freemarker模版路径
        configuration.setDirectoryForTemplateLoading(new File("/Users/panhusun/canglang/java_project/study-project/WJ-project/project/17taotao-item-web/src/main/webapp/WEB-INF/jsp/freemarker"));

        //指定模版编码
        configuration.setDefaultEncoding("UTF-8");

        //根据摹本路径获取模版文件对象
        Template template = configuration.getTemplate("date.ftl");

        //创建模版数据，注意：模版数据必须是map
        Map<String, Object> maps = new HashMap<>();

        maps.put("today", new Date());

        //创建模版输出路径
        Writer out = new FileWriter(new File("/Users/panhusun/canglang/java_project/study-project/WJ-project/project/17taotao-item-web/src/main/webapp/WEB-INF/jsp/freemarker/out/date.html"));

        //生成html文件,参数意义：参数1：放入模版中的数据、参数2：模版输出路径
        template.process(maps, out);

        //关闭资源
        out.close();
    }
}
