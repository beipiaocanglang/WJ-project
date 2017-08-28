package study.project.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import study.project.ContentCategoryService;
import study.project.EasyUITreeNode;
import study.project.ProjectResultDTO;
import study.project.domain.TbContentCategory;
import study.project.domain.TbContentCategoryExample;
import study.project.domain.TbContentCategoryExample.Criteria;
import study.project.mapper.TbContentCategoryMapper;

/**
 * 商城门户系统首页数据
 * @author canglang
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

	//注入到接口
	@Resource
	private TbContentCategoryMapper contentCategoryMapper;

	/**
	 * 功能5：
	 * 		后台内容分类管理查询-加载树形分类
	 * 参数：
	 * 		父id：parentId
	 * 返回值：
	 * 		List<EasyUITreeNode>:转成json
	 */
	public List<EasyUITreeNode> findContentCategoryList(Long parentId) {
		//创建TbContentCategoryExample对象
		TbContentCategoryExample example = new TbContentCategoryExample();
		//创建Criteria对象
		Criteria createCriteria = example.createCriteria();
		//设置参数
		createCriteria.andParentIdEqualTo(parentId);
		//执行查询
		List<TbContentCategory> cList = contentCategoryMapper.selectByExample(example);
		//创建List<EasyUITreeNode>集合封装节点信息
		List<EasyUITreeNode> treeNodeList = new ArrayList<EasyUITreeNode>();
		
		for (TbContentCategory contentCategory : cList) {
			//创建EasyUITreeNode对象封装树形节点信息
			EasyUITreeNode treeNode = new EasyUITreeNode();
			treeNode.setId(contentCategory.getId().intValue());
			treeNode.setText(contentCategory.getName());
			treeNode.setState(contentCategory.getIsParent()?"closed":"open");
			treeNodeList.add(treeNode);
		}
		
		return treeNodeList;
	}

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
	public ProjectResultDTO creatNode(Long parentId, String name) {
		//创建节点就是插入一条数据
		//创建TbContentCategory对象，补齐其他属性
		TbContentCategory contentCategory = new TbContentCategory();
		//id自增长不需要手动设置
		//设置parentId
		contentCategory.setParentId(parentId);
		contentCategory.setName(name);
		//设置排序类型
		contentCategory.setSortOrder(1);
		//设置当前创建的节点的状态1：正常、2：删除
		contentCategory.setStatus(1);
		//设置是否是父节点(新创建的一定是子节点)
		contentCategory.setIsParent(false);
		
		Date date = new Date();
		contentCategory.setCreated(date);
		contentCategory.setUpdated(date);
		
		//执行插入
		int insert = contentCategoryMapper.insert(contentCategory);
		
		//根据上一级节点的id也就是新创建节点的父id查询上一级节点是否是父节点
		TbContentCategory cCategory = contentCategoryMapper.selectByPrimaryKey(parentId);
		
		//判断是否为父节点
		if (!cCategory.getIsParent()) {
			//上级节点是子节点，更新节点状态
			cCategory.setIsParent(true);
			//保存
			contentCategoryMapper.updateByPrimaryKey(cCategory);
		}
		
		return ProjectResultDTO.ok(contentCategory);
	}

}
