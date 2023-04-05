package com.jixiebackstage.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jixiebackstage.springboot.controller.dto.UserDto;
import com.jixiebackstage.springboot.entity.User;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 邹永靖
 * @since 2022-07-15
 */
public interface IUserService extends IService<User> {

    User getUserById(Integer id);

    UserDto login(UserDto user);

    User register(UserDto user);

    Integer getNum();

    Page<User> getPage(Page<User> objectPage, QueryWrapper<User> queryWrapper);

    List<User> getList();
}
