package com.jixiebackstage.springboot.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jixiebackstage.springboot.common.Result;
import com.jixiebackstage.springboot.config.AuthAccess;
import com.jixiebackstage.springboot.entity.Comment;
import com.jixiebackstage.springboot.service.ICommentService;
import com.jixiebackstage.springboot.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 邹永靖
 * @since 2022-08-06
 */
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Resource
    private ICommentService commentService;

    @PostMapping("/postBlog")
    public Result saveBlog(@RequestBody Comment comment) {
        return save(comment, 0);
    }

    @PostMapping("/postNews")
    public Result saveNews(@RequestBody Comment comment) {
        return save(comment, 1);
    }

    public Result save(Comment comment, Integer type) {
//        当id为空时为新增评论
        if (comment.getId() == null) {
            comment.setTime(DateUtil.now());
            comment.setAvatarUrl(TokenUtils.getCurrentUser().getAvatarUrl());
            comment.setNickname(TokenUtils.getCurrentUser().getNickname());
            comment.setUserId(TokenUtils.getCurrentUser().getId());
            comment.setType(type);

            Integer pid = comment.getPid();
            if (pid != null) {
                Comment pComment = commentService.getById(pid);
                comment.setReplyId(pComment.getUserId());
                if (pComment.getOriginId() != null) {       //  若当前回复的父级还有祖宗级，则设置相同的祖宗级
                    comment.setOriginId(pComment.getOriginId());
                } else {        //  若当前回复的父级没有祖宗级，则设置当前的回复为祖宗级
                    comment.setOriginId(comment.getPid());
                }
            }
        }
        return Result.success(commentService.saveOrUpdate(comment));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(commentService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(commentService.removeByIds(ids));
    }


    @GetMapping
    public Result findAll() {
        return Result.success(commentService.list());
    }

    @GetMapping("/tree/getBlog/{articleId}")
    public Result findCommentDetailBlog(@PathVariable Integer articleId) {
        return findCommentDetail(articleId, 0);
    }

    @GetMapping("/tree/getNews/{articleId}")
    public Result findCommentDetailNews(@PathVariable Integer articleId) {
        return findCommentDetail(articleId, 1);
    }

    public Result findCommentDetail(Integer articleId, Integer type) {
//        查出当前文章的所有评论
        List<Comment> articleComments = commentService.findCommentDetail(articleId, type);
//        获取祖宗级评论
        List<Comment> originList = articleComments.stream().filter(comment -> comment.getOriginId() == null).collect(Collectors.toList());
        articleComments.removeAll(originList);
//        获取父级评论
        List<Comment> pList =
                articleComments.stream().filter(comment -> comment.getPid().equals(comment.getOriginId())).collect(Collectors.toList());
//        获取子级评论
        articleComments.removeAll(pList);
//        将子级评论装配到父级评论
        matchComments(pList, articleComments);
//        将父级评论装配到祖宗级评论
        matchComments(originList, pList);
        return Result.success(originList);
    }

    private void matchComments(List<Comment> originList, List<Comment> pList) {
        for (Comment originComment : originList) {
            List<Comment> comments = new ArrayList<>();
            for (Comment pComment : pList) {
                if (pComment.getPid().equals(originComment.getId())) {
                    comments.add(pComment);
                }
            }
            originComment.setChildren(comments);
        }
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(commentService.getById(id));
    }

    @AuthAccess
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        if (!StrUtil.isBlank(name)) {
            queryWrapper.like("name", name);
        }
        queryWrapper.orderByAsc("id");

        return Result.success(commentService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }
}

