package study.project;

import java.util.List;

import study.project.domain.ADItem;
import study.project.domain.TbContent;

/**
 * 查询分类内容
 * @author canglang
 */
public interface ContentService {

	/**
	 * 功能7：
	 * 		根据categoryId分页查询分类内容表数据
	 * 参数：
	 * 		categoryId：子节点分类id
	 * 返回值：
	 * 		EasyUIResult:easyui分页查询
	 */
	public EasyUIResult findContentByCategoeyId(Long categoryId, Integer page, Integer rows);

	/**
	 * 功能8：
	 * 		根据分类id添加此分类下的内容数据
	 * 参数：
	 * 		TbContent
	 * 返回值：
	 * 		ProjectResultDTO
	 */
	public ProjectResultDTO saveContent(TbContent content);
	
	/**
	 * 功能9：
	 * 		前台门户系统-查询轮播图
	 * 参数：
	 * 		categoryId
	 * 返回值：
	 * 		List<ADItem>,因为有多张图片
	 */
	public List<ADItem> findContentListByCategoryId(Long categoryId);
}

















