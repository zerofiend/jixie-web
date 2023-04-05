package com.jixiebackstage.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jixiebackstage.springboot.entity.Comment;
import com.jixiebackstage.springboot.mapper.CommentMapper;
import com.jixiebackstage.springboot.service.ICommentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 邹永靖
 * @since 2022-08-06
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    @Resource
    private CommentMapper commentMapper;

    @Override
    public List<Comment> findCommentDetail(Integer articleId, Integer type) {

        return commentMapper.findCommentDetail(articleId, type);
    }
}
