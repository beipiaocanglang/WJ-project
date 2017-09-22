package study.project.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import study.project.ContentService;
import study.project.EasyUIResult;
import study.project.JsonUtils;
import study.project.ProjectResultDTO;
import study.project.domain.ADItem;
import study.project.domain.TbContent;
import study.project.domain.TbContentExample;
import study.project.domain.TbContentExample.Criteria;
import study.project.mapper.TbContentMapper;
import study.project.redis.JedisDao;

/**
 * 查询分类内容
 * @author canglang
 */
@Service
public class ContentServiceImpl implements ContentService {

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
	
	/**
	 * 功能7：
	 * 		根据categoryId分页查询分类内容表数据
	 * 参数：
	 * 		categoryId：子节点分类id
	 * 返回值：
	 * 		EasyUIResult:easyui分页查询
	 */
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

    //消息发送模版
    @Resource
    private JmsTemplate jmsTemplate;

    //消息发送目的地
    @Resource
    private ActiveMQTopic activeMQTopic;

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

        final Long categoryId = content.getCategoryId();

        if (insert == 1) {
            //保存成功后发送消息到JMS
            jmsTemplate.send(activeMQTopic, new MessageCreator() {

                @Override
                public Message createMessage(Session session) throws JMSException {

                    return session.createTextMessage(String.valueOf(categoryId));
                }
            });
        }

        return ProjectResultDTO.ok();
    }


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
}
