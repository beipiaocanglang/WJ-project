package study.project.item.controller.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试spring整合freemarker
 * Created by canglang on 2017/9/3.
 */
@Controller
public class SpringFreemarker {

    @Resource
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @RequestMapping("genHtml")
    @ResponseBody
    public String genHtml() throws Exception {

        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        Template template = configuration.getTemplate("hello.ftl");

        Map<String, Object> maps = new HashMap<>();
        maps.put("hello", "spring整合Freemarker！！！！");

        //Writer out = new FileWriter(new File("E:\\html\\out\\helloSpring.html"));
        Writer out = new FileWriter(new File("/Users/panhusun/canglang/java_project/study-project/WJ-project/project/17taotao-item-web/src/main/webapp/WEB-INF/jsp/freemarker/out/helloSpring.html"));

        template.process(maps, out);

        out.close();

        return "OK";
    }
}
