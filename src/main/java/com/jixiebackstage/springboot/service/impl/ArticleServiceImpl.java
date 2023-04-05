package com.jixiebackstage.springboot.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jixiebackstage.springboot.common.Constants;
import com.jixiebackstage.springboot.entity.Article;
import com.jixiebackstage.springboot.mapper.ArticleMapper;
import com.jixiebackstage.springboot.service.IArticleService;
import com.jixiebackstage.springboot.service.IRedisService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 邹永靖
 * @since 2022-08-05
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {
    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private IRedisService redisService;

    /**
     * 获取博客数量
     *
     * @return
     */
    @Override
    public Integer getNum() {
        String jsonStr = redisService.getRedis(Constants.BLOG_NUM_KEY);
        int num;
        if (StrUtil.isBlank(jsonStr)) {
            num = articleMapper.getNum();
            redisService.setRedis(Constants.BLOG_NUM_KEY, StrUtil.toString(num));
        } else {
            num = Integer.parseInt(jsonStr);
        }
        return num;
    }

    @Override
    public List<Article> getList() {
        String jsonStr = redisService.getRedis(Constants.BLOG_TOTAL_KEY);
        List<Article> list;
        if (StrUtil.isBlank(jsonStr)) {
            list = list();
            redisService.setRedis(Constants.BLOG_TOTAL_KEY, JSONUtil.toJsonStr(list));
        } else {
            list = JSONUtil.toBean(jsonStr, new TypeReference<List<Article>>() {
            }, true);
        }
        return list;
    }

    @Override
    public Article getBlogById(Integer id) {
        String jsonStr = redisService.getRedis(Constants.BLOG_SINGLE_KEY + id);
        Article blog;
        if (StrUtil.isBlank(jsonStr)) {
            blog = getById(id);
            redisService.setRedis(Constants.BLOG_SINGLE_KEY + id, JSONUtil.toJsonStr(blog));
        } else {
            blog = JSONUtil.toBean(jsonStr, new TypeReference<Article>() {
            }, true);
        }
        return blog;
    }

    @Override
    public Page<Article> getPage(Page<Article> objectPage, QueryWrapper<Article> queryWrapper, String type) {
        String jsonStr = redisService.getRedis(Constants.BLOG_PAGE_KEY + type);
        Page<Article> list;
        if (StrUtil.isBlank(jsonStr)) {
            list = page(objectPage, queryWrapper);
            redisService.setRedis(Constants.BLOG_PAGE_KEY + type, JSONUtil.toJsonStr(list));
        } else {
            list = JSONUtil.toBean(jsonStr, new TypeReference<Page<Article>>() {
            }, true);
        }
        return list;
    }

    @Override
    public Integer getUserIdById(Integer id) {
        return articleMapper.getUserIdById(id);
    }
}
