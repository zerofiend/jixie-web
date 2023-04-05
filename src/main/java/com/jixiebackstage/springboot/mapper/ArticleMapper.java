package com.jixiebackstage.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jixiebackstage.springboot.entity.Article;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author 邹永靖
 * @since 2022-08-05
 */
public interface ArticleMapper extends BaseMapper<Article> {

    @Select("select COUNT(id) from article")
    Integer getNum();

    @Select("select user_id from article where id=#{id}")
    Integer getUserIdById(@Param("id") Integer id);
}
