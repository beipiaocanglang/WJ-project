package study.project.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import study.project.ContentCategoryService;
import study.project.EasyUITreeNode;
import study.project.ProjectResultDTO;

@Controller
public class ContentCategoryController {
	
	@Resource
	private ContentCategoryService contentCategoryService;
	
	/**
	 * 功能5：
	 * 		后台内容分类管理查询-加载树形分类
	 * 请求：
	 * 		/content/category/list
	 * 参数：
	 * 		父id：parentId
	 * 返回值：
	 * 		List<EasyUITreeNode>:转成json
	 */
	@ResponseBody
	@RequestMapping("/content/category/list")
	public List<EasyUITreeNode> findContentCategoryList(@RequestParam(defaultValue="0",value="id") Long parentId){
		List<EasyUITreeNode> categoryList = contentCategoryService.findContentCategoryList(parentId);
		return categoryList;
	}
	
	/**
	 * 功能6：
	 * 		后台内容分类管理查询-创建节点
	 * 请求：
	 * 		/content/category/create
	 * 参数：
	 * 		parentId(上一级节点id)，name(当前节点名称)
	 * 返回值：
	 * 		json格式ProjectResultDTO.ok(TbContentCategory)
	 * 业务：
	 * 		如果创建的节点id是子节点，就需要将子节点修改成父节点，修改isParent=1
	 */
	@ResponseBody
	@RequestMapping("/content/category/create")
	public ProjectResultDTO creatNode(Long parentId, String name){
		ProjectResultDTO creatNode = contentCategoryService.creatNode(parentId, name);
		return creatNode;
	}
}
