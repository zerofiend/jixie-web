package com.jixiebackstage.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jixiebackstage.springboot.entity.Comment;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 邹永靖
 * @since 2022-08-06
 */
public interface ICommentService extends IService<Comment> {

    List<Comment> findCommentDetail(Integer articleId, Integer type);
}
