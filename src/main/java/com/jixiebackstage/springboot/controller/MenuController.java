package com.jixiebackstage.springboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jixiebackstage.springboot.common.Constants;
import com.jixiebackstage.springboot.common.Result;
import com.jixiebackstage.springboot.entity.Dict;
import com.jixiebackstage.springboot.entity.Menu;
import com.jixiebackstage.springboot.service.IDictService;
import com.jixiebackstage.springboot.service.IMenuService;
import com.jixiebackstage.springboot.service.IRoleMenuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 邹永靖
 * @since 2022-07-25
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Resource(name = "menuServiceImpl")
    private IMenuService menuService;

    @Resource(name = "dictServiceImpl")
    private IDictService dictService;

    @Resource
    private IRoleMenuService roleMenuService;

    @PostMapping
    public Result save(@RequestBody Menu menu) {
        return Result.success(menuService.saveOrUpdate(menu));
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(menuService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(menuService.removeByIds(ids));
    }

    @GetMapping("/containIds")
    public Result isContainIds() {
        return Result.success(roleMenuService.list());
    }

    @Transactional(rollbackFor = Exception.class)
    @GetMapping
    public Result findMenuTrees(
            @RequestParam(defaultValue = "") String name) {
        return Result.success(menuService.findMenus(name));
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(menuService.list());
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        queryWrapper.orderByDesc("id");
        return Result.success(menuService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    @GetMapping("/icons")
    public Result getIcons() {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", Constants.DICT_TYPE_ICON);
        return Result.success(dictService.list(queryWrapper));
    }

}

