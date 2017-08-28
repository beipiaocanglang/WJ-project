package study.project.avtivemqListerne;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;

import study.project.search.mapper.SearchItemMapper;
import study.project.search.pojo.SearchItem;

public class AddItemMessageListerne implements MessageListener{

	//查询数据库
	@Resource
	private SearchItemMapper searchItemMapper;
	
	@Resource
	private SolrServer solrServer;
	
	/**
	 * 功能13
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
