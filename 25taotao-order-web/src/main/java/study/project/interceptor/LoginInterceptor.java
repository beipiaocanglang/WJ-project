package study.project.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import study.project.CookieUtils;
import study.project.JsonUtils;
import study.project.domain.TbUser;
import study.project.redis.service.JedisService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器
 * Created by panhusun on 2017/9/10.
 */
public class LoginInterceptor implements HandlerInterceptor{

    //存入cookie中的用户唯一标识token
    @Value("${TOKEN_COOKIE_KEY}")
    private String TOKEN_COOKIE_KEY;

    //重定向到登录页面的URL
    @Value("${SSO_URL}")
    private String SSO_URL;

    @Resource
    private JedisService jedisService;

    //存入redis和缓存服务器中的用户信息的唯一key
    @Value("${SESSION_KEY}")
    private String SESSION_KEY;
    /**
     * 业务流程：
     *      1、判断cookie中是否有当前用户的token
     *          有：仅仅表示登陆过
     *          没有：重新登录。登录成功后必须跳转到历史操作页面
     *      2、判断redis中用户身份信息是否过期
     *          没有：放行，登录成功
     *          过期：重新登录。登录成功后必须跳转到历史操作页面
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //获取cookie中的用户唯一标识token
        String userToken = CookieUtils.getCookieValue(request, TOKEN_COOKIE_KEY);

        //判断token是否为空
        if (StringUtils.isBlank(userToken)) {//为空,重新登录
            //获取当前操作请求地址
            String url = request.getRequestURL().toString();
            //跳转登录页面，携带历史操作地址
            response.sendRedirect(SSO_URL+"?redirectURL="+url);
            //拦截
            return false;
        }

        //不为空
        //根据token查询redis服务器
        String userRedis = jedisService.get(SESSION_KEY + ":" + userToken);

        //判断redis中用户信息是否过期
        if (StringUtils.isBlank(userRedis)) {//过期
            //获取当前操作请求地址
            String url = request.getRequestURL().toString();
            //跳转登录页面，携带历史操作地址
            response.sendRedirect(SSO_URL + "?redirectURL=" + url);
            //拦截
            return false;
        }

        //没有过期,登录成功，将用户信息存入request
        request.setAttribute("user", JsonUtils.jsonToPojo(userRedis, TbUser.class));

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
