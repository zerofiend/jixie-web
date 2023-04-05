package com.jixiebackstage.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jixiebackstage.springboot.entity.RoleMenu;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 邹永靖
 * @since 2022-07-25
 */
public interface IRoleMenuService extends IService<RoleMenu> {

    List<RoleMenu> getByMenuId(List<Integer> ids);
}
