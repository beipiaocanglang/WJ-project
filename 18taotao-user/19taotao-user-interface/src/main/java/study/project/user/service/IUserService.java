package study.project.user.service;

import study.project.ProjectResultDTO;
import study.project.domain.TbUser;

/**
 * SSO接口
 * Created by canglang on 2017/9/5.
 */
public interface IUserService {

    /**
     * 功能17：
     *      登录前检查数据的可用性
     * 请求：
     *      /user/check/{param}/{type}
     * 要检查的数据：
     *      username、phone、email
     * 返回值：
     *      json格式的ProjectResultDTO
     */
    public ProjectResultDTO dataCheck(String param, Integer type);

    /**
     * 功能18：
     *      用户注册(注册前要先检查数据的可用性)
     * 请求：
     *      /user/register
     * 参数：
     *      TbUser
     */
    public ProjectResultDTO register(TbUser user);

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
     */
    public ProjectResultDTO login(String username, String password);

    /**
     * 功能20：
     *      页面加载时根据token查询redis服务器中是否有用户登录信息
     * 请求：
     *      /user/token/{token}
     * 参数：
     *      String token
     *      String callback
     */
    public ProjectResultDTO userCheck(String token, String callback);
}
