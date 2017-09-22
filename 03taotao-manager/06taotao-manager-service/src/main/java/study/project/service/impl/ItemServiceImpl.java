package study.project.service.impl;

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

import study.project.*;
import study.project.domain.TbItem;
import study.project.domain.TbItemDesc;
import study.project.domain.TbItemExample;
import study.project.domain.TbItemExample.Criteria;
import study.project.mapper.TbItemDescMapper;
import study.project.mapper.TbItemMapper;
import study.project.redis.dao.JedisDao;

@Service
public class ItemServiceImpl implements ItemService {

	@Resource
	private TbItemMapper itemMapper;
	
	@Resource
	private TbItemDescMapper itemDescMapper;

	@Resource
	private JedisDao jedisDao;

	//#商品详情页的商品信息存入redis中的key
	@Value("${ITEM_DETAIL_CHACHE}")
	private String ITEM_DETAIL_CHACHE;

	//商品的过期时间
	@Value("${EXPIRE_TIME}")
	private Integer EXPIRE_TIME;

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

	/**
	 * 功能14：
	 * 		根据商品id查询商品的描述
	 * 请求：	
	 * 		http://wj.item.client.com/${item.id }.html
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
}
