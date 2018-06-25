package study.project.user.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * Created by panhusun on 2017/9/4.
 */
@Controller
public class PageController {

    /**
     * 跳转到注册
     * /user/showLogin
     * @return
     */
    @RequestMapping("/user/showRegister")
    public String showRegister(){

        return "register";
    }
    /**
     * 跳转到登录
     * /user/showLogin
     * @return
     */
    @RequestMapping("/user/showLogin")
    public String showLogin(Model model, String redirect){

        model.addAttribute("redirect", redirect);

        return "login";
    }
}
