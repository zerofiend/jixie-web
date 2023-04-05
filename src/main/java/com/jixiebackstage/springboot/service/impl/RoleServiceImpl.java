package com.jixiebackstage.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jixiebackstage.springboot.entity.Menu;
import com.jixiebackstage.springboot.entity.Role;
import com.jixiebackstage.springboot.entity.RoleMenu;
import com.jixiebackstage.springboot.mapper.MenuMapper;
import com.jixiebackstage.springboot.mapper.RoleMapper;
import com.jixiebackstage.springboot.mapper.RoleMenuMapper;
import com.jixiebackstage.springboot.service.IRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {
    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private MenuMapper menuMapper;

    @Transactional
    @Override
    public void setRoleMenu(Integer roleId, List<Integer> menuIds) {
        //  先删除当前用户所有的绑定关系
        roleMenuMapper.deleteByRoleId(roleId);

        //  再把前台传过来的菜单id数组绑定到当前这个角色id上去
        for (Integer menuId : menuIds) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
    }

    @Override
    public List<Menu> getRoleMenu(Integer roleId) {
        List<Integer> menuIdList = roleMenuMapper.selectByRoleId(roleId);
        return menuMapper.selectBatchIds(menuIdList);
    }
}
