package com.jixiebackstage.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jixiebackstage.springboot.entity.RoleMenu;
import com.jixiebackstage.springboot.mapper.RoleMenuMapper;
import com.jixiebackstage.springboot.service.IRoleMenuService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 邹永靖
 * @since 2022-07-25
 */
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements IRoleMenuService {
    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Override
    public List<RoleMenu> getByMenuId(List<Integer> ids) {
        QueryWrapper<RoleMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("menu_id", ids);
        return roleMenuMapper.selectList(queryWrapper);
    }
}
