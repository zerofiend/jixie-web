package com.jixiebackstage.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jixiebackstage.springboot.entity.Menu;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 邹永靖
 * @since 2022-07-25
 */
public interface IMenuService extends IService<Menu> {

    List<Menu> findMenus(String name);

    List<Menu> getTrees(List<Menu> list);

}
