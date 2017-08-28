package study.project.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import study.project.ProjectResultDTO;
import study.project.search.service.SearchItemService;

@Controller
public class SearchSolrController {

	@Resource
	private SearchItemService searchItemService;
	
	/**
	 * 功能11：
	 * 		导入数据库中的数据到索引库
	 * 请求：/dataImport
	 * 参数：无
	 * 返回值：json格式的ProjectResultDTO
	 * @return
	 */
	@RequestMapping("/dataImport")
	@ResponseBody
	public ProjectResultDTO dataImport(){
		ProjectResultDTO result = searchItemService.dataImport();
		return result;
	}
}
