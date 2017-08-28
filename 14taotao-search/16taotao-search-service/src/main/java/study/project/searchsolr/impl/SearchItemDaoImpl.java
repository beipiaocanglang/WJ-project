package study.project.searchsolr.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Repository;

import study.project.search.dao.SearchItemDao;
import study.project.search.pojo.SearchItem;
import study.project.search.pojo.SearchResult;

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
