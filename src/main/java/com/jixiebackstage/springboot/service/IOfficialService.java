package com.jixiebackstage.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jixiebackstage.springboot.entity.Official;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 邹永靖
 * @since 2022-08-09
 */
public interface IOfficialService extends IService<Official> {

    List<Official> getByType(Integer type);

    Integer removeByUrl(String url);

    Integer getNum();

    Integer selectTypeById(Integer id);

    Page<Official> getPage(Page<Official> objectPage, QueryWrapper<Official> queryWrapper, Integer type);

    Official getOfficialById(Integer id);
}
