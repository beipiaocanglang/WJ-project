package study.project.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import study.project.EasyUIResult;
import study.project.ItemService;
import study.project.ProjectResultDTO;
import study.project.domain.TbItem;
import study.project.domain.TbItemDesc;

@Controller
public class ItemController {

	@Resource
	private ItemService itemService;
	
	/**
	 * 根据商品id查询商品
	 * 接收参数方法一：
	 * 		@RequestMapping("/findItemByItemId/{itemId}")
	 *		public TbItem findItemByItemId(@PathVariable Long itemId){}
	 *		url:
	 *			localhost:8081/findItemByItemId/536563
	 *接收参数方法二：
	 *		@RequestMapping("/findItemByItemId")
	 *		public TbItem findItemByItemId(@RequestParam Long itemId){}
	 *		url:
	 *			localhost:8081/findItemByItemId?itemId=536563
	 * @param itemId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/findItemByItemId/{itemId}")
	public TbItem findItemByItemId(@PathVariable Long itemId){
		//根据商品id查询商品数据
		TbItem tbItem = itemService.findItemByID(itemId);
		return tbItem;
	}
	
	/**
	 * 功能1：使用分页插件查询后台商品列表
	 * 请求：
	 * 		/item/list ---easyUI框架需要的
	 * 参数：--参数不可变，因为是前端框架EasyUI框架需要的参数
	 * 		当前页：Integer page--当入参为空时使用@RequestParam注解给默认值
	 * 		每页长度：Integer rows--当入参为空时使用@RequestParam注解给默认值
	 * 返回值：--json
	 * 		{total:3224,rows:[{},{}]}
	 * 使用包装对象EasyUIResult封装参数：
	 * 		Long total
	 * 		List<?> rows
	 * 使用@ResponseBody自动转换json格式
	 */
	@ResponseBody
	@RequestMapping("/item/list")
	public EasyUIResult fingItemListByPage(@RequestParam(defaultValue="1") Integer page, @RequestParam(defaultValue="20")Integer rows){

		//分页查询商品集合
		EasyUIResult easyUIResult = itemService.findItemListByPage(page, rows);
		return easyUIResult;
	}
	
	/**
	 * 功能4：
	 * 		添加商品后保存商品到数据库
	 * 功能13：
	 * 		同步索引库
	 *
	 * 请求：
	 * 		$.post("/item/save",$("#itemAddForm").serialize(), function(data){
	 * 参数：
	 * 		保存两张表的数据，商品表和商品描述表
	 * 返回值：
	 * 		根据ajax的毁掉函数来判断需要的返回值
	 * 需要保存两张表：
	 * 		TbItem：商品表
	 * 		TbItemDesc:商品描述表
	 */
	@ResponseBody
	@RequestMapping("/item/save")
	public ProjectResultDTO saveItem(TbItem item, TbItemDesc itemDesc){
		ProjectResultDTO resultDTO = itemService.saveItem(item, itemDesc);
		
		return resultDTO;
	}
}
