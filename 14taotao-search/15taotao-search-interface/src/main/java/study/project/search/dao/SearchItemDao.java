package study.project.search.dao;

import org.apache.solr.client.solrj.SolrQuery;

import study.project.search.pojo.SearchResult;

public interface SearchItemDao {

	/**
	 * 功能12：
	 * 		查询索引库中的商品列表
	 * 搜索工程的搜索功能，查询solr中的数据
	 */
	public SearchResult findItemsBySolr(SolrQuery solrQuery);
}
