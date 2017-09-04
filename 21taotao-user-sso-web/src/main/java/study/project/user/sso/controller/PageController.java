package study.project.user.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * Created by panhusun on 2017/9/4.
 */
@Controller
public class PageController {

    @RequestMapping("{page}")
    public String showIndex(@PathVariable String page){

        return page;
    }

}
