package study.project.user.service.impl;

import org.springframework.stereotype.Service;
import study.project.ProjectResultDTO;
import study.project.domain.TbUser;
import study.project.domain.TbUserExample;
import study.project.mapper.TbUserMapper;
import study.project.user.service.IUserService;

import javax.annotation.Resource;
import java.util.List;

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
}