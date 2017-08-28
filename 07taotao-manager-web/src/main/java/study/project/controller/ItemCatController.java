package study.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import study.project.EasyUITreeNode;
import study.project.ItemCatService;

/**
 * 查询商品类目
 * @author canglang
 */
@Controller
public class ItemCatController {
	@Autowired
	private ItemCatService itemCatService;
	
	/**
	 * 功能：
	 * 		根据parentId查询商品类目
	 * 参数：
	 * 		Long parentId--父id
	 * 		注意：
	 * 			第一次加载时没有parentId，需要给默认值
	 * 			框架传递的参数是id，我们接收的参数名称是parendID,所以要使用value值
	 * 返回值：
	 * 		List<EasyUITreeNode>
	 * 业务描述：
	 * 		1、根据父节点查询此节点下面的子节点，如果有子节点，必然也是parentId
	 * 		2、状态：isParent
	 * 			1：表示当前节点是父节点，有子节点
	 * 			0：表示当前父节点就是子节点，没有子节点
	 */
	@ResponseBody
	@RequestMapping("/item/cat/list")
	public List<EasyUITreeNode> findItemCatList(@RequestParam(defaultValue="0", value="id") Long parentId){
		List<EasyUITreeNode> itemCatList = itemCatService.findItemCatByParentId(parentId);
		return itemCatList;
	}
}
