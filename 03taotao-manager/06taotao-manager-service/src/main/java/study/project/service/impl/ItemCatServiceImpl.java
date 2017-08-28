package study.project.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import study.project.EasyUITreeNode;
import study.project.ItemCatService;
import study.project.domain.TbItemCat;
import study.project.domain.TbItemCatExample;
import study.project.domain.TbItemCatExample.Criteria;
import study.project.mapper.TbItemCatMapper;

/**
 * 查询商品类目的实现类
 * @author canglang
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	/**
	 * 功能：
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
	public List<EasyUITreeNode> findItemCatByParentId(Long parentId) {
		//创建TbItemCatExample对象
		TbItemCatExample example = new TbItemCatExample();
		//创建Criteria对象
		Criteria criteria = example.createCriteria();
		//设置参数parentId
		criteria.andParentIdEqualTo(parentId);
		//执行查询
		List<TbItemCat> itemsCatList = itemCatMapper.selectByExample(example);
		//创建List<EasyUITreeNode>集合对象，封装节点信息
		List<EasyUITreeNode> treeNodeList = new ArrayList<EasyUITreeNode>();
		
		for (TbItemCat tbItemCat : itemsCatList) {
			EasyUITreeNode treeNode = new EasyUITreeNode();
			treeNode.setId(tbItemCat.getId().intValue());
			treeNode.setText(tbItemCat.getName());
			//节点状态是否为1：closed、0：open
			treeNode.setState(tbItemCat.getIsParent()?"closed":"open");
			//封装到list集合
			treeNodeList.add(treeNode);
		}
		
		return treeNodeList;
	}
}
