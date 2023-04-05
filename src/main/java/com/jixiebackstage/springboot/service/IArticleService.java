package com.jixiebackstage.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jixiebackstage.springboot.entity.Article;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 邹永靖
 * @since 2022-08-05
 */
public interface IArticleService extends IService<Article> {

    Integer getNum();

    List<Article> getList();

    Article getBlogById(Integer id);

    Page<Article> getPage(Page<Article> objectPage, QueryWrapper<Article> queryWrapper, String type);

    Integer getUserIdById(Integer id);
}
