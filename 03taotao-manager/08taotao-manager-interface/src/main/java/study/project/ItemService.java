package study.project;

import java.util.List;

import study.project.domain.TbItem;
import study.project.domain.TbItemDesc;

/**
 * service层接口
 * @author yeying
 */
public interface ItemService {
	
	/**
	 * 根据itemId查询Item信息
	 * @param itemId
	 * @return
	 */
	public TbItem findItemByID(Long itemId);
	
	/**
	 * 功能1：使用分页插件查询后台商品列表
	 * 参数：--参数不可变，因为是前端框架EasyUI框架需要的参数
	 * 		当前页：Integer page
	 * 		每页长度：Integer rows
	 * 返回值：--json
	 * 		{total:3224,rows:[{},{}]}
	 * 使用包装对象EasyUIResult封装参数：
	 * 		Long total
	 * 		List<?> rows
	 * 使用@ResponseBody自动转换json格式
	 */
	public EasyUIResult findItemListByPage(Integer page, Integer rows);

	/**
	 * 功能4：
	 * 		添加商品后保存商品到数据库
	 * 功能13：
	 * 		同步索引库
	 * 需要保存两张表：
	 * 		TbItem：商品表
	 * 		TbItemDesc:商品描述表
	 *
	 * 同步索引库:
	 */
	 public ProjectResultDTO saveItem(TbItem item, TbItemDesc itemDesc);

	/**
	 * 功能14：
	 * 		根据商品id查询商品的详情
	 * 请求：	
	 * 		http://wj.item.client.com/${item.id }.html
	 * 参数：
	 * 		itemId
	 * 页面所需要的数据：
	 * 		1、商品信息（商品表）
	 * 		2、商品描述信息（商品描述表）
	 * 		3、商品规格
	 * 
	 * @param itemId
	 * @return
	 */
	public TbItemDesc findItemDescById(Long itemId);

}
