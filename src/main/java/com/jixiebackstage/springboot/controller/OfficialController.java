package com.jixiebackstage.springboot.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jixiebackstage.springboot.common.Constants;
import com.jixiebackstage.springboot.common.Result;
import com.jixiebackstage.springboot.entity.Official;
import com.jixiebackstage.springboot.service.IOfficialService;
import com.jixiebackstage.springboot.service.IRedisService;
import com.jixiebackstage.springboot.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 邹永靖
 * @since 2022-08-09
 */
@CrossOrigin
@RestController
@RequestMapping("/official")
public class OfficialController {
    @Resource
    private IOfficialService officialService;

    @Resource
    private FileController fileController;

    @Resource
    private IRedisService redisService;

    /**
     * 获取官方动态数量
     *
     * @return
     */
    @GetMapping("/getNum")
    public Integer getUserNum() {
        return officialService.getNum();
    }

    /**
     * 轮播图片上传
     *
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload/carousel")
    public String uploadCarousel(@RequestParam MultipartFile file) throws IOException {
        String name = file.getOriginalFilename();
        Official official = new Official();
        String url = fileController.upload(file, "official/carousel/");    //  存入file数据库
        official.setTime(DateUtil.now());
        official.setName(name);
        official.setTop(false);
        official.setType(0);
        official.setContent(url);
        officialService.saveOrUpdate(official);     //  存入official数据库
//        删除redis缓存
        redisService.flushRedis(Constants.OFFICIAL_TYPE_KEY + 0);
        redisService.flushRedis(Constants.OFFICIAL_NUM_KEY);
        return url;
    }

    /**
     * 官方信息上传
     *
     * @param official
     * @return
     */
    @PostMapping("/{type}")
    public Result save(@RequestBody Official official, @PathVariable Integer type) {
        if (official.getId() == null) { //  新增
            official.setTime(DateUtil.now());
            official.setUser(TokenUtils.getCurrentUser().getNickname());
            official.setUserId(TokenUtils.getCurrentUser().getId());
            official.setType(type);
            official.setTop(false);
        }
        //        删除redis缓存
        redisService.flushRedis(Constants.OFFICIAL_TYPE_KEY + type);
        redisService.flushRedis(Constants.OFFICIAL_PAGE_KEY + type);
        redisService.flushRedis(Constants.OFFICIAL_SINGLE_KEY + official.getId());
        redisService.flushRedis(Constants.OFFICIAL_NUM_KEY);
        return Result.success(officialService.saveOrUpdate(official));
    }

    /**
     * 获取轮播图
     *
     * @return
     */
    @GetMapping("/carousel")
    public Result getCarousel() {
        return Result.success(officialService.getByType(0));
    }

    /**
     * 获取主页通知
     *
     * @return
     */
    @GetMapping("/notice/show")
    public Result getNoticeShow() {
        return Result.success(officialService.getByType(1));
    }

    /**
     * 获取通知公告
     *
     * @return
     */
    @GetMapping("/notice")
    public Result getNotice(@RequestParam Integer pageNum,
                            @RequestParam Integer pageSize,
                            @RequestParam(defaultValue = "") String name) {
        return getPage(pageNum, pageSize, name, 1);
    }

    /**
     * 获取主页动态
     *
     * @return
     */
    @GetMapping("/news/show")
    public Result getNewsShow() {
        return Result.success(officialService.getByType(2));
    }

    /**
     * 获取协会动态
     *
     * @return
     */
    @GetMapping("/news")
    public Result getNews(@RequestParam Integer pageNum,
                          @RequestParam Integer pageSize,
                          @RequestParam(defaultValue = "") String name) {
        return getPage(pageNum, pageSize, name, 2);
    }

    /**
     * 获取主页友情链接
     *
     * @return
     */
    @GetMapping("/link/show")
    public Result getLinkShow() {
        return Result.success(officialService.getByType(3));
    }

    /**
     * 获取友情链接
     *
     * @return
     */
    @GetMapping("/link")
    public Result getLink(@RequestParam Integer pageNum,
                          @RequestParam Integer pageSize,
                          @RequestParam(defaultValue = "") String name) {
        return getPage(pageNum, pageSize, name, 3);
    }

    /**
     * 获取协会资料
     *
     * @return
     */
    @GetMapping("/data")
    public Result getData(@RequestParam Integer pageNum,
                          @RequestParam Integer pageSize,
                          @RequestParam(defaultValue = "") String name) {
        return getPage(pageNum, pageSize, name, 4);
    }

    /**
     * 通用获取数据方法
     *
     * @param pageNum
     * @param pageSize
     * @param name
     * @param type
     * @return
     */

    private Result getPage(Integer pageNum, Integer pageSize, String name, Integer type) {
        QueryWrapper<Official> queryWrapper = new QueryWrapper<>();
        if (!StrUtil.isBlank(name)) {
            queryWrapper.like("name", name);
        }
        queryWrapper.eq("type", type);
        queryWrapper.orderByDesc("id");
        return Result.success(officialService.getPage(new Page<>(pageNum, pageSize), queryWrapper, type));
    }

    /**
     * 删除官方文件
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        //  获取当前文件的类型
        Integer type = officialService.selectTypeById(id);
        //  删除缓存
        redisService.flushRedis(Constants.OFFICIAL_TYPE_KEY + type);
        redisService.flushRedis(Constants.OFFICIAL_PAGE_KEY + type);
        redisService.flushRedis(Constants.OFFICIAL_SINGLE_KEY + id);
        redisService.flushRedis(Constants.OFFICIAL_NUM_KEY);
        return Result.success(officialService.removeById(id));
    }

    /**
     * 批量删除官方文件
     *
     * @param ids
     * @return
     */
    @PostMapping("/del/batch/{type}")
    public Result deleteBatch(@RequestBody List<Integer> ids, @PathVariable Integer type) {
        redisService.flushRedis(Constants.OFFICIAL_TYPE_KEY + type);
        redisService.flushRedis(Constants.OFFICIAL_PAGE_KEY + type);
        for (Integer id : ids) {
            redisService.flushRedis(Constants.OFFICIAL_SINGLE_KEY + id);
        }
        redisService.flushRedis(Constants.OFFICIAL_NUM_KEY);
        return Result.success(officialService.removeByIds(ids));
    }


    /**
     * 获取单个资料
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(officialService.getOfficialById(id));
    }

}

