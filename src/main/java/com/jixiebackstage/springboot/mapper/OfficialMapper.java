package com.jixiebackstage.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jixiebackstage.springboot.entity.Official;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author 邹永靖
 * @since 2022-08-09
 */
public interface OfficialMapper extends BaseMapper<Official> {

    @Select("select COUNT(id) from official")
    Integer getNum();

    @Select("select type from official where id=#{id}")
    Integer selectTypeById(@Param("id") Integer id);
}
