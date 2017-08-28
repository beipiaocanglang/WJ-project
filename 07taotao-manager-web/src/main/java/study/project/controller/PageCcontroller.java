package study.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageCcontroller {

	/**
	 * 配置一个通用的页面跳转请求
	 * localhost:8081/index---其中index就相当与参数
	 * @param page
	 * @return
	 */
	@RequestMapping("{page}")
	public String showIndex(@PathVariable String page){
		
		return page;
	}
}
