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
