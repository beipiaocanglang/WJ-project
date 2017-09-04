package study.project.user.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import study.project.ProjectResultDTO;
import study.project.user.service.IUserService;

import javax.annotation.Resource;

/**
 * 校验登录数据是否可用
 * Created by canglang on 2017/9/5.
 */
@Controller
public class UaerController {

    @Resource
    private IUserService userService;

    /**
     * 功能17：
     *      登录前检查数据的可用性
     * 请求：
     *      http://sso.taotao.com/user/check/{param}/{type}
     * 要检查的数据：
     *      username、phone、email
     * 返回值：
     *      json格式的ProjectResultDTO
     */
    @RequestMapping("/user/check/{param}/{type}")
    @ResponseBody
    public ProjectResultDTO dataCheck(@PathVariable String param, @PathVariable Integer type){

        ProjectResultDTO dataCheck = userService.dataCheck(param, type);


        return dataCheck;
    }
}
