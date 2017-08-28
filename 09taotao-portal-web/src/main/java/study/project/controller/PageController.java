package study.project.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import study.project.ContentService;
import study.project.JsonUtils;
import study.project.domain.ADItem;

@Controller
public class PageController {

	@Resource
	private ContentService contentService;
	
	@Value("${LUNBOTU_CATEGORYID}")
	private Long LUNBOTU_CATEGORYID;
	
	/**
	 * 跳转到首页
	 * 
	 * 因为在首页加载时就获取首页的数据，所以需要放在跳转到首页的请求中来操作
	 * 
	 * 功能9：
	 * 		前台门户系统-查询轮播图
	 * 参数：
	 * 		categoryId
	 * 返回值：
	 * 		将List<ADItem>集合转成json放入model中，页面通过el表达式获取
	 * @author canglang
	 */
	@RequestMapping("/index")
	public String showIndex(Model model){
		
		List<ADItem> itemList = contentService.findContentListByCategoryId(LUNBOTU_CATEGORYID);
		//将结果转成json格式的字符串
		String adJson = JsonUtils.objectToJson(itemList);
		model.addAttribute("adJson", adJson);
		return "index";
	}
}
