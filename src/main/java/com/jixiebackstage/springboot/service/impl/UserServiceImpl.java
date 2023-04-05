package com.jixiebackstage.springboot.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jixiebackstage.springboot.Exception.ServiceException;
import com.jixiebackstage.springboot.common.Constants;
import com.jixiebackstage.springboot.controller.dto.UserDto;
import com.jixiebackstage.springboot.entity.Menu;
import com.jixiebackstage.springboot.entity.User;
import com.jixiebackstage.springboot.mapper.RoleMapper;
import com.jixiebackstage.springboot.mapper.RoleMenuMapper;
import com.jixiebackstage.springboot.mapper.UserMapper;
import com.jixiebackstage.springboot.service.IMenuService;
import com.jixiebackstage.springboot.service.IRedisService;
import com.jixiebackstage.springboot.service.IUserService;
import com.jixiebackstage.springboot.utils.TokenUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 邹永靖
 * @since 2022-07-15
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private IMenuService menuService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private IRedisService redisService;

    /**
     * 根据id获取用户信息
     *
     * @param id
     * @return
     */
    @Override
    public User getUserById(Integer id) {
        String jsonStr = redisService.getRedis(Constants.USER_SINGLE_KEY + id);
        User user;
        if (StrUtil.isBlank(jsonStr)) {
            user = getById(id);
            redisService.setRedis(Constants.USER_SINGLE_KEY + id, JSONUtil.toJsonStr(user));
        } else {
            user = JSONUtil.toBean(jsonStr, new TypeReference<User>() {
            }, true);
        }
        return user;
    }

    /**
     * 登录
     *
     * @param user
     * @return
     */
    @Override
    public UserDto login(UserDto user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        User one = null;
        try {
            one = getOne(queryWrapper);
        } catch (Exception e) {
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        if (one != null) {
            BeanUtils.copyProperties(one, user);
            //  设置token
            String token = TokenUtils.genToken(one.getId().toString(), one.getPassword().toString());
            user.setToken(token);
            String flag = one.getRole();

            //  设置用户的菜单列表
            List<Menu> roleMenus = getRoleMenus(flag);
            user.setMenus(roleMenus);
            return user;
        } else {
            throw new ServiceException(Constants.CODE_600, "用户名或密码错误");
        }
    }

    /**
     * 注册
     *
     * @param user
     * @return
     */
    @Override
    public User register(UserDto user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        User one = null;
        try {
            one = getOne(queryWrapper);
        } catch (Exception e) {
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        if (one == null) {
            one = new User();
            BeanUtils.copyProperties(user, one);
            save(one);
        } else {
            throw new ServiceException(Constants.CODE_600, "用户名已经存在");
        }
        return one;
    }

    /**
     * 获取用户个数
     *
     * @return
     */
    @Override
    public Integer getNum() {
        String jsonStr = redisService.getRedis(Constants.USER_NUM_KEY);
        int num;
        if (StrUtil.isBlank(jsonStr)) {
            num = userMapper.getNum();
            redisService.setRedis(Constants.USER_NUM_KEY, StrUtil.toString(num));
        } else {
            num = Integer.parseInt(jsonStr);
        }
        return num;
    }

    /**
     * 获取用户分页数据
     *
     * @param objectPage
     * @param queryWrapper
     * @return
     */
    @Override
    public Page<User> getPage(Page<User> objectPage, QueryWrapper<User> queryWrapper) {
        String jsonStr = redisService.getRedis(Constants.USER_PAGE_KEY);
        Page<User> list;
        if (StrUtil.isBlank(jsonStr)) {
            list = page(objectPage, queryWrapper);
            redisService.setRedis(Constants.USER_PAGE_KEY, JSONUtil.toJsonStr(list));
        } else {
            list = JSONUtil.toBean(jsonStr, new TypeReference<Page<User>>() {
            }, true);
        }
        return list;
    }

    @Override
    public List<User> getList() {
        String jsonStr = redisService.getRedis(Constants.USER_TOTAL_KEY);
        List<User> list;
        if (StrUtil.isBlank(jsonStr)) {
            list = list();
            redisService.setRedis(Constants.USER_TOTAL_KEY, JSONUtil.toJsonStr(list));
        } else {
            list = JSONUtil.toBean(jsonStr, new TypeReference<List<User>>() {
            }, true);
        }
        return list;
    }

    /**
     * 获取当前角色的菜单列表
     *
     * @param roleFlag
     * @return
     */
    private List<Menu> getRoleMenus(String roleFlag) {
        //  获取当前角色flag的id
        Integer roleId = roleMapper.selectByFlag(roleFlag);
        //  当前角色的所有菜单id集合
        List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);

        //  获取当前角色的菜单集合
        List<Menu> list = menuService.listByIds(menuIds);
        //  返回当前菜单集合的二级菜单
        return menuService.getTrees(list);
    }
}
