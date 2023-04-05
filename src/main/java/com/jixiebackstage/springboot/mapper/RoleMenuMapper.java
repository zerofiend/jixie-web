package com.jixiebackstage.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jixiebackstage.springboot.entity.RoleMenu;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author 邹永靖
 * @since 2022-07-25
 */
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    @Delete("delete from sys_role_menu where role_id = #{roleId}")
    void deleteByRoleId(@Param("roleId") Integer roleId);

    @Select("select menu_id from sys_role_menu where role_id = #{roleId}")
    List<Integer> selectByRoleId(@Param("roleId") Integer roleId);
}
