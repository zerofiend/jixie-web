package com.jixiebackstage.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jixiebackstage.springboot.entity.Menu;
import com.jixiebackstage.springboot.entity.Role;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 邹永靖
 * @since 2022-07-25
 */
public interface IRoleService extends IService<Role> {

    void setRoleMenu(Integer roleId, List<Integer> menuIds);

    List<Menu> getRoleMenu(Integer roleId);
}
