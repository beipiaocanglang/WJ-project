package study.project.search.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import study.project.ProjectResultDTO;
import study.project.search.dao.SearchItemDao;
import study.project.search.mapper.SearchItemMapper;
import study.project.search.pojo.SearchItem;
import study.project.search.pojo.SearchResult;
import study.project.search.service.SearchItemService;

@Service
public class SearchItemServiceImpl implements SearchItemService{

	@Resource
	private SearchItemMapper searchItemMapper;
	
	@Resource
	private SolrServer solrServer;
	
	@Resource
	private SearchItemDao searchItemDao;
	
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

	/**
	 * 功能12：
	 * 		根据关键字搜索商品列表(查询索引库)
	 * @param keyWorld
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
		solrQuery.setHighlightSimplePre("<font class=\"skcolor_ljg\">");//设置高亮前缀(ljg是Ljg不是1jg)
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
}
