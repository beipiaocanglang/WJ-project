package study.project;

import java.util.List;

public interface ContentCategoryService {

	/**
	 * 功能5：
	 * 		后台内容分类管理查询-加载树形分类
	 * 参数：
	 * 		父id：parentId
	 * 返回值：
	 * 		List<EasyUITreeNode>:转成json
	 */
	public List<EasyUITreeNode> findContentCategoryList(Long parentId);

	/**
	 * 功能6：
	 * 		后台内容分类管理查询-创建节点
	 * 参数：
	 * 		parentId(上一级节点id)，name(当前节点名称)
	 * 返回值：
	 * 		ProjectResultDTO.ok(TbContentCategory)
	 * 业务：
	 * 		如果创建的节点id是子节点，就需要将子节点修改成父节点，修改isParent=1
	 */
	public ProjectResultDTO creatNode(Long parentId, String name);
}
