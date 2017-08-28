package study.project;

import java.util.List;

/**
 * 商品类目接口
 */
public interface ItemCatService {
	/**
	 * 功能2：
	 * 		根据parentId查询商品类目
	 * 参数：
	 * 		Long parentId--父id
	 * 返回值：
	 * 		List<EasyUITreeNode>
	 * 业务描述：
	 * 		1、根据父节点查询此节点下面的子节点，如果有子节点，必然也是parentId
	 * 		2、状态：isParent
	 * 			1：表示当前节点是父节点，有子节点
	 * 			0：表示当前父节点就是子节点，没有子节点
	 */
	public List<EasyUITreeNode> findItemCatByParentId(Long parentId);
}
