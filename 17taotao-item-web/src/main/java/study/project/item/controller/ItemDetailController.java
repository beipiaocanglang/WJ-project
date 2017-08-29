package study.project.item.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import study.project.ItemService;
import study.project.domain.TbItem;
import study.project.domain.TbItemDesc;

@Controller
public class ItemDetailController {


	@Resource
	private ItemService itemService;
	
	/**
	 * 功能14：
	 * 		根据商品id查询商品的详情
	 * 请求：	
	 * 		http://localhost:8087/${item.id }.html
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
	@RequestMapping("{itemId}")
	public String findItemDetailById(@PathVariable Long itemId, Model model){
		
		//查询商品信息
		TbItem item = itemService.findItemByID(itemId);
		
		//查询商品描述表
		TbItemDesc itemDesc = itemService.findItemDescById(itemId);
		
		model.addAttribute("item", item);
		model.addAttribute("itemDesc", itemDesc);
		
		return "item";
	}
}
