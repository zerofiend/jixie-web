package com.jixiebackstage.springboot.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jixiebackstage.springboot.common.Constants;
import com.jixiebackstage.springboot.entity.Official;
import com.jixiebackstage.springboot.mapper.OfficialMapper;
import com.jixiebackstage.springboot.service.IOfficialService;
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
 * @since 2022-08-09
 */
@Service
public class OfficialServiceImpl extends ServiceImpl<OfficialMapper, Official> implements IOfficialService {
    @Resource
    private OfficialMapper officialMapper;

    @Resource
    private IRedisService redisService;

    /**
     * 根据type获取资料
     *
     * @param type
     * @return
     */
    @Override
    public List<Official> getByType(Integer type) {
        QueryWrapper<Official> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type);
        List<Official> list;
//        从缓存获取数据
        String jsonStr = redisService.getRedis(Constants.OFFICIAL_TYPE_KEY + type);
        if (StrUtil.isBlank(jsonStr)) {  //  取出来的json是空的
            list = officialMapper.selectList(queryWrapper);
            //  缓存进redis
            redisService.setRedis(Constants.OFFICIAL_TYPE_KEY + type, JSONUtil.toJsonStr(list));
        } else {    //  取出来的json非空，则转换为相应数据类型
            list = JSONUtil.toBean(jsonStr, new TypeReference<List<Official>>() {
            }, true);
        }
        return list;
    }

    @Override
    public Integer removeByUrl(String url) {
        QueryWrapper<Official> queryWrapper = new QueryWrapper<>();
        if (!"".equals(url)) {
            queryWrapper.eq("content", url);
        }
        return officialMapper.delete(queryWrapper);
    }

    /**
     * 获取总数
     *
     * @return
     */
    @Override
    public Integer getNum() {
        String jsonStr = redisService.getRedis(Constants.OFFICIAL_NUM_KEY);
        int num;
        if (StrUtil.isBlank(jsonStr)) {
            num = officialMapper.getNum();
            redisService.setRedis(Constants.OFFICIAL_NUM_KEY, StrUtil.toString(num));
        } else {
            num = Integer.parseInt(jsonStr);
        }
        return num;
    }

    /**
     * 根据id获取类型
     *
     * @param id
     * @return
     */
    @Override
    public Integer selectTypeById(Integer id) {
        return officialMapper.selectTypeById(id);
    }

    /**
     * 获取分页数据
     *
     * @param objectPage
     * @param queryWrapper
     * @return
     */
    @Override
    public Page<Official> getPage(Page<Official> objectPage, QueryWrapper<Official> queryWrapper, Integer type) {
        Page<Official> page;
        String jsonStr = redisService.getRedis(Constants.OFFICIAL_PAGE_KEY + type);
        if (StrUtil.isBlank(jsonStr)) {
            page = this.page(objectPage, queryWrapper);
            redisService.setRedis(Constants.OFFICIAL_PAGE_KEY + type, JSONUtil.toJsonStr(page));
        } else {
            page = JSONUtil.toBean(jsonStr, new TypeReference<Page<Official>>() {
            }, true);
        }
        return page;
    }

    @Override
    public Official getOfficialById(Integer id) {
        String jsonStr = redisService.getRedis(Constants.OFFICIAL_SINGLE_KEY + id);
        Official single;
        if (StrUtil.isBlank(jsonStr)) {
            single = getById(id);
            redisService.setRedis(Constants.OFFICIAL_SINGLE_KEY + id, JSONUtil.toJsonStr(single));
        } else {
            single = JSONUtil.toBean(jsonStr, new TypeReference<Official>() {
            }, true);
        }
        return single;
    }
}
