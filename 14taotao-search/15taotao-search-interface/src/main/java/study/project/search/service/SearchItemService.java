package study.project.search.service;

import study.project.ProjectResultDTO;
import study.project.search.pojo.SearchResult;

public interface SearchItemService {

	//导入数据库数据到索引库的接口
	public ProjectResultDTO dataImport();
	
	/**
	 * 功能12：
	 * 		根据关键字搜索商品列表(查询索引库)
	 * @param keyWorld
	 * @return
	 */
	public SearchResult findItemsBySolr(String keyWorld, Integer page, Integer rows);	
}
