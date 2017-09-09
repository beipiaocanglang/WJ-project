package study.project.user.sso.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import study.project.CookieUtils;
import study.project.JsonUtils;
import study.project.ProjectResultDTO;
import study.project.domain.TbUser;
import study.project.user.service.IUserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    /**
     * 功能18：
     *      用户注册(注册前要先检查数据的可用性)
     * 请求：
     *      http://sso.taotao.com/user/register
     * 参数：
     *      TbUser
     */
    @ResponseBody
    @RequestMapping(value = "/user/register", method = RequestMethod.POST)
    public ProjectResultDTO register(TbUser user){

        ProjectResultDTO result = userService.register(user);

        return result;
    }

    @Value("${TOKEN_COOKIE_KEY}")
    private String TOKEN_COOKIE_KEY;
    /**
     * 功能19：
     *      用户登录
     * 请求：
     *      /user/login
     * 参数：
     *      String username
     *      String password
     * 返回值：
     *       封装token数据TaoTaoResult。
     *       校验用户名不存在，返回400,msg:用户名或者密码错误
     *       校验密码：密码错误，返回400，msg：用户名或者密码错误。
     * 业务流程：
     *      1、根据用户名查询用户信息(校验用户是否存在)
     *          存在：
     *              获取查询到的数据
     *              判断加密后的密码是否正确，校验通过，则等陆成功
     *          不存在：
     *              直接返回给出错误 信息
     *
     *      2、登录成功后把用户信息放入redis服务器
     *      3、返回token，token就是redis存储用户身份信息的可以
     *      4、把返回的token写入cookie
     *
     * 需要将用户信息写入redis
     *      redis的数据结构：key ：value
     *      key: SESSION_KEY:token
     *      value: json格式i的user对象
     */
    @ResponseBody
    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    public ProjectResultDTO login(HttpServletRequest request, HttpServletResponse response, String username, String password){

        ProjectResultDTO result = userService.login(username, password);

        if (result.getStatus() == 200 && result.getData() != null) {
            //把token放入cookies、true：存入cookie中的数据加密
            CookieUtils.setCookie(request, response, TOKEN_COOKIE_KEY, result.getData().toString(), true);
        }
        return result;
    }

    /**
     * 功能20：
     *      页面加载时根据token查询redis服务器中是否有用户登录信息
     * 请求：
     *      /user/token/{token}
     * 参数：
     *      String token
     *      String callback
     */
    @ResponseBody
    @RequestMapping("/user/token/{token}")
    public Object userCheck(@PathVariable String token, String callback){

        ProjectResultDTO result = userService.userCheck(token, callback);

        if (StringUtils.isBlank(callback)) {//如果是空就是普通请求，直接返回
            return result;
        } else {
            //否则是一个跨域请求，
            /*
             * 返回json格式就是必须是callback(json) callback(userCheck)
             * 注意：
             *      系统跨域进行数据请求，，返回时不能直接识别json格式数据  但是识别js代码
             * 思考：
             *      能不能把json格式数据转成js代码呢？
             *      普通的json格式数据：{"username"："张三"，"password"："123"}
             *      跨域json格式：callback({"username"："张三"，"password"："123"});
             */
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
            mappingJacksonValue.setJsonpFunction(callback);

            String s = JsonUtils.objectToJson(mappingJacksonValue);

            return mappingJacksonValue;
        }
    }
}
