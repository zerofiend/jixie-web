package com.jixiebackstage.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jixiebackstage.springboot.entity.User;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author 邹永靖
 * @since 2022-07-15
 */
public interface UserMapper extends BaseMapper<User> {

    @Select("select COUNT(id) from sys_user")
    Integer getNum();
}
