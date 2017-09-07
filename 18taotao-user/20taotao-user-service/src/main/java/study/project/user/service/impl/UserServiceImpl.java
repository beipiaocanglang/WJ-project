package study.project.user.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import study.project.JsonUtils;
import study.project.ProjectResultDTO;
import study.project.domain.TbUser;
import study.project.domain.TbUserExample;
import study.project.mapper.TbUserMapper;
import study.project.user.redis.dao.JedisDao;
import study.project.user.service.IUserService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *  sso
 * Created by panhusun on 2017/9/4.
 */
@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private TbUserMapper userMapper;

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
    public ProjectResultDTO dataCheck(String param, Integer type) {

        TbUserExample example = new TbUserExample();

        TbUserExample.Criteria criteria = example.createCriteria();

        //根据参数类型设置值
        if (type == 1) {
            criteria.andUsernameEqualTo(param);
        } else if (type == 2) {
            criteria.andPhoneEqualTo(param);
        } else if (type == 3) {
            criteria.andEmailEqualTo(param);
        }

        List<TbUser> tbUsers = userMapper.selectByExample(example);

        if (tbUsers == null || tbUsers .isEmpty() || tbUsers.size() == 0) {
            return ProjectResultDTO.ok(true);
        }

        return ProjectResultDTO.ok(false);
    }

    /**
     * 功能18：
     *      用户注册
     * 请求：
     *      /user/register
     * 参数：
     *      TbUser
     */
    public ProjectResultDTO register(TbUser user) {

        try {
            //补全参数
            user.setCreated(new Date());
            user.setUpdated(new Date());

            //给密码加密
            if (StringUtils.isNotBlank(user.getPassword())) {
                //DigestUtils是spring提供的工具类
                user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
            }

            //注册
            int insert = userMapper.insert(user);

            if (insert != 1) {
                return ProjectResultDTO.build(400, "注册失败，请稍后重试！");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ProjectResultDTO.build(400, "注册失败，请稍后重试！");
        }

        return ProjectResultDTO.ok();
    }

    @Value("${SESSION_KEY}")
    private String SESSION_KEY;

    @Value("${SESSION_TIMEOUT}")
    private Integer SESSION_TIMEOUT;

    @Resource
    private JedisDao jedisDao;

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
    public ProjectResultDTO login(String username, String password) {

        TbUserExample example = new TbUserExample();

        TbUserExample.Criteria criteria = example.createCriteria();

        criteria.andUsernameEqualTo(username);

        //根据用户名查询用户信息，正常情况下只能查出一条数据
        List<TbUser> users = userMapper.selectByExample(example);

        //查询结果为空表示用户不存在
        if (users == null || users.isEmpty() || users.size() < 1) {
            return ProjectResultDTO.build(400, "用户名或密码错误，请核对后重试！");
        }

        //获取用户信息
        TbUser user = users.get(0);

        //对密码进行md5加密
        String md5 = DigestUtils.md5DigestAsHex(password.getBytes());

        ///判断密码是否相等
        if(!md5.equals(user.getPassword())){
            return ProjectResultDTO.build(400, "用户名或密码错误，请核对后重试！");
        }

        //返回用户已经登录的标识token，token使用UUID
        String token = UUID.randomUUID().toString();

        //将用户登录信息存入redis缓存服务器中
        //把密码置成null
        user.setPassword(null);

        //将用户登录信息存入redis缓存
        jedisDao.set(SESSION_KEY+":"+token, JsonUtils.objectToJson(user));
        //设置用户登录信息的超时时间
        jedisDao.expire(SESSION_KEY+":"+token, SESSION_TIMEOUT);

        return ProjectResultDTO.ok(token);
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
    public ProjectResultDTO userCheck(String token, String callback) {

        String userCookieInfo = jedisDao.get(SESSION_KEY + ":" + token);

        if (StringUtils.isNotBlank(userCookieInfo)) {

            TbUser user = JsonUtils.jsonToPojo(userCookieInfo, TbUser.class);

            //将用户登录信息存入redis缓存
            jedisDao.set(SESSION_KEY+":"+token, JsonUtils.objectToJson(user));
            //设置用户登录信息的超时时间
            jedisDao.expire(SESSION_KEY+":"+token, SESSION_TIMEOUT);

            return ProjectResultDTO.ok(user);
        }
        return ProjectResultDTO.build(201, "您的登录信息已经过期，请重新登录！");
    }
}
