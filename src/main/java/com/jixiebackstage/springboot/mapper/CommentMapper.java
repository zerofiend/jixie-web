package com.jixiebackstage.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jixiebackstage.springboot.entity.Comment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author 邹永靖
 * @since 2022-08-06
 */
public interface CommentMapper extends BaseMapper<Comment> {

    @Select("select c.*,u.nickname,u.avatar_url from t_comment c left join sys_user u on c.user_id = u.id where c.article_id = #{articleId} and c.type = #{type}")
    List<Comment> findCommentDetail(@Param("articleId") Integer articleId, @Param("type") Integer type);
}
