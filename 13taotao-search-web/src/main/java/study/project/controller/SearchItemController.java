package study.project.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import study.project.search.pojo.SearchItem;
import study.project.search.pojo.SearchResult;
import study.project.search.service.SearchItemService;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchItemController {

	@Resource
	private SearchItemService searchItemService;

	/**
	 * 功能12：
	 * 		根据关键字搜索商品列表(查询索引库)
	 * @param request
	 * @param keyWorld
	 * @param page
	 * @param rows
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/search")
	public String searchItem(HttpServletRequest request, @RequestParam(value = "q") String keyWorld,
                             @RequestParam(defaultValue="1") Integer page,
                             @RequestParam(defaultValue="30") Integer rows, Model model) throws Exception{
		
		//解决中文乱码
		keyWorld = new String(keyWorld.getBytes("ISO8859-1"), "UTF-8");
		
		SearchResult result = searchItemService.findItemsBySolr(keyWorld, page, rows);

        List<SearchItem> itemList = new ArrayList<>();

        List<SearchItem> items = result.getItemList();

        /*for (SearchItem searchItem : items) {

            if (StringUtils.isNotBlank(searchItem.getImage())){
                searchItem.setImages(searchItem.getImage().split(","));
                itemList.add(searchItem);
            }
        }*/

		//回显参数
		model.addAttribute("query", keyWorld);
		//回显列表
		model.addAttribute("itemList", items);
		//回显当前页
		model.addAttribute("page", result.getCurPage());
		//回显总记录数
		model.addAttribute("totalPages", result.getPages());
		
		return "search";
	}
}
