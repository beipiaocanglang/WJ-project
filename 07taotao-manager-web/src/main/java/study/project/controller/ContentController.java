package study.project.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import study.project.ContentService;
import study.project.EasyUIResult;
import study.project.ProjectResultDTO;
import study.project.domain.TbContent;

/**
 * 查询分类内容
 * @author canglang
 */
@Controller
public class ContentController {

	@Resource
	private ContentService contentService;
	
	/**
	 * 功能7：
	 * 		根据categoryId分页查询分类内容表数据
	 * 请求：
	 * 		/content/query/list
	 * 参数：
	 * 		Long categoryId：子节点分类id
	 * 		Integer page:当前页
	 * 		Integer rows:内容集合
	 * 返回值：
	 * 		json格式EasyUIResult
	 */
	@ResponseBody
	@RequestMapping("/content/query/list")
	public EasyUIResult findContentByCategoryId(Long categoryId, Integer page, Integer rows){
		EasyUIResult contentList = contentService.findContentByCategoeyId(categoryId, page, rows);
		return contentList;
	}
	
	/**
	 * 功能8：
	 * 		根据分类id添加此分类下的内容数据
	 * 请求：
	 * 		/content/save
	 * 参数：
	 * 		TbContent
	 * 返回值：
	 * 		json格式的ProjectResultDTO
	 */
	@ResponseBody
	@RequestMapping("/content/save")
	public ProjectResultDTO saveContent(TbContent content){
		ProjectResultDTO saveContent = contentService.saveContent(content);
		return saveContent;
	}
}
