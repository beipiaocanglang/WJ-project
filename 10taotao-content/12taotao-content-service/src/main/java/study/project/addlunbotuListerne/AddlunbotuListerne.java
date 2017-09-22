package study.project.addlunbotuListerne;

import org.apache.activemq.memory.list.MessageList;
import org.springframework.beans.factory.annotation.Value;
import study.project.JsonUtils;
import study.project.domain.ADItem;
import study.project.domain.TbContent;
import study.project.domain.TbContentExample;
import study.project.mapper.TbContentMapper;
import study.project.redis.JedisDao;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * 添加轮播图是的MQ监听
 * Created by panhusun on 2017/9/22.
 */
public class AddlunbotuListerne implements MessageListener{

    @Resource
    private TbContentMapper contentMapper;

    //注入图片的宽和高
    @Value("${WIDTH}")
    private Integer WIDTH;

    @Value("${WIDTHB}")
    private Integer WIDTHB;

    @Value("${HEIGTH}")
    private Integer HEIGTH;

    @Value("${HEIGTHB}")
    private Integer HEIGTHB;

    //注入首页缓存的key
    @Value("${AD_CHACHE}")
    private String AD_CHACHE;

    //注入jedisDao
    @Resource
    private JedisDao jedisDao;

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            TextMessage tm = (TextMessage) message;

            try {
                String categoryId = tm.getText();

                TbContentExample example = new TbContentExample();

                TbContentExample.Criteria createCriteria = example.createCriteria();

                createCriteria.andCategoryIdEqualTo(Long.parseLong(categoryId));

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

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
