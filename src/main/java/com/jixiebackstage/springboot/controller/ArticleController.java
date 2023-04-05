package com.jixiebackstage.springboot.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jixiebackstage.springboot.common.Constants;
import com.jixiebackstage.springboot.common.Result;
import com.jixiebackstage.springboot.config.AuthAccess;
import com.jixiebackstage.springboot.entity.Article;
import com.jixiebackstage.springboot.service.IArticleService;
import com.jixiebackstage.springboot.service.IRedisService;
import com.jixiebackstage.springboot.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 邹永靖
 * @since 2022-08-05
 */
@RestController
@RequestMapping("/article")
public class ArticleController {
    @Resource
    private IArticleService articleService;

    @Resource
    private IRedisService redisService;

    /**
     * 获取博客数量
     *
     * @return
     */
    @GetMapping("/getNum")
    public Integer getUserNum() {
        return articleService.getNum();
    }

    /**
     * 新建或保存博客
     *
     * @param article
     * @return
     */
    @PostMapping
    public Result save(@RequestBody Article article) {
        if (article.getId() == null) { //  新增
            article.setTime(DateUtil.now());
            article.setUser(TokenUtils.getCurrentUser().getNickname());
            article.setUserId(TokenUtils.getCurrentUser().getId());
            article.setTop(false);
        }
        redisService.flushRedis(Constants.BLOG_PAGE_KEY + "TOTAL");
        redisService.flushRedis(Constants.BLOG_PAGE_KEY + article.getUserId());
        redisService.flushRedis(Constants.BLOG_TOTAL_KEY);
        redisService.flushRedis(Constants.BLOG_SINGLE_KEY + article.getId());
        redisService.flushRedis(Constants.BLOG_NUM_KEY);
        return Result.success(articleService.saveOrUpdate(article));
    }

    /**
     * 删除单个博客
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        redisService.flushRedis(Constants.BLOG_PAGE_KEY + "TOTAL");
        redisService.flushRedis(Constants.BLOG_PAGE_KEY + articleService.getUserIdById(id));
        redisService.flushRedis(Constants.BLOG_TOTAL_KEY);
        redisService.flushRedis(Constants.BLOG_SINGLE_KEY + id);
        redisService.flushRedis(Constants.BLOG_NUM_KEY);
        return Result.success(articleService.removeById(id));
    }

    /**
     * 批量删除博客
     *
     * @param ids
     * @return
     */
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        redisService.flushRedis(Constants.BLOG_PAGE_KEY + "TOTAL");
        redisService.flushRedis(Constants.BLOG_TOTAL_KEY);
        for (Integer id : ids) {
            redisService.flushRedis(Constants.BLOG_PAGE_KEY + articleService.getUserIdById(id));
            redisService.flushRedis(Constants.BLOG_SINGLE_KEY + id);
        }
        redisService.flushRedis(Constants.BLOG_NUM_KEY);
        return Result.success(articleService.removeByIds(ids));
    }


    /**
     * 查询所有博客
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        return Result.success(articleService.getList());
    }

    /**
     * 查询单个博客
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(articleService.getBlogById(id));
    }

    /**
     * 分页查询博客
     *
     * @param pageNum
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        if (!StrUtil.isBlank(name)) {
            queryWrapper.like("name", name);
        }
        queryWrapper.orderByDesc("id");

        return Result.success(articleService.getPage(new Page<>(pageNum, pageSize), queryWrapper, "TOTAL"));
    }

    /**
     * 查询个人博客总数
     *
     * @param pageNum
     * @param pageSize
     * @param userId
     * @param name
     * @return
     */
    @AuthAccess
    @GetMapping("/personPage")
    public Result findPersonPage(@RequestParam Integer pageNum,
                                 @RequestParam Integer pageSize,
                                 @RequestParam Integer userId,
                                 @RequestParam(defaultValue = "") String name) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        if (userId != 0) {
            queryWrapper.eq("user_id", userId);
        }
        if (!StrUtil.isBlank(name)) {
            queryWrapper.like("name", name);
        }
        queryWrapper.orderByDesc("id");

        return Result.success(articleService.getPage(new Page<>(pageNum, pageSize), queryWrapper,
                StrUtil.toString(userId)));
    }
}

