package study.project.item.controller.freemarkerListerne;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import study.project.ItemService;
import study.project.domain.TbItem;
import study.project.domain.TbItemDesc;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

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
