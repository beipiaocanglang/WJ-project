package study.project.user.service;

import study.project.ProjectResultDTO;

/**
 * SSO接口
 * Created by canglang on 2017/9/5.
 */
public interface IUserService {

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
    public ProjectResultDTO dataCheck(String param, Integer type);
}