package study.project.search.mapper;

import java.util.List;

import study.project.search.pojo.SearchItem;

public interface SearchItemMapper {

	//导入数据库数据到索引库
	public List<SearchItem> dataImport();
	
	/**
	 * 功能13：
	 * 同步索引库时根据itemId查询数据库
	 */
	public SearchItem findItemInfoById(Long itemId);
}
