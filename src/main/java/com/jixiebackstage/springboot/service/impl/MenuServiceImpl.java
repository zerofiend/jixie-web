package com.jixiebackstage.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jixiebackstage.springboot.entity.Menu;
import com.jixiebackstage.springboot.mapper.MenuMapper;
import com.jixiebackstage.springboot.service.IMenuService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 邹永靖
 * @since 2022-07-25
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    @Override
    public List<Menu> findMenus(String name) {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        queryWrapper.orderByAsc("id");
        //  查询所有数据
        List<Menu> list = list(queryWrapper);
        return getTrees(list);
    }

    /**
     * 获取当前角色menus所组成的二级菜单结构
     *
     * @param list
     * @return
     */
    @Override
    public List<Menu> getTrees(List<Menu> list) {
        //  找出pid为null的一级菜单
        List<Menu> parentNodes = list.stream().filter(menu -> menu.getPid() == null).collect(Collectors.toList());
        //  找出一级菜单的子菜单
        for (Menu menu : parentNodes) {
            List<Menu> children =
                    list.stream().filter(m -> menu.getId().equals(m.getPid())).collect(Collectors.toList());
            menu.setChildren(children);
        }
        return parentNodes;
    }

}
