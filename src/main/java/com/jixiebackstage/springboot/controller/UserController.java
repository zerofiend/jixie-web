package com.jixiebackstage.springboot.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jixiebackstage.springboot.common.Constants;
import com.jixiebackstage.springboot.common.Result;
import com.jixiebackstage.springboot.config.AuthAccess;
import com.jixiebackstage.springboot.controller.dto.UserDto;
import com.jixiebackstage.springboot.entity.User;
import com.jixiebackstage.springboot.service.IRedisService;
import com.jixiebackstage.springboot.service.IUserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 邹永靖
 * @since 2022-07-15
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource(name = "userServiceImpl")
    private IUserService userService;

    @Resource
    private IRedisService redisService;

    /**
     * 新增或保存用户数据
     *
     * @param user
     * @return
     */
    @PostMapping
    public Result save(@RequestBody User user) {
        redisService.flushRedis(Constants.USER_PAGE_KEY);
        redisService.flushRedis(Constants.USER_SINGLE_KEY + user.getId());
        redisService.flushRedis(Constants.USER_NUM_KEY);
        redisService.flushRedis(Constants.USER_TOTAL_KEY);
        return Result.success(userService.saveOrUpdate(user));
    }

    /**
     * 删除单个用户数据
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        redisService.flushRedis(Constants.USER_PAGE_KEY);
        redisService.flushRedis(Constants.USER_SINGLE_KEY + id);
        redisService.flushRedis(Constants.USER_NUM_KEY);
        redisService.flushRedis(Constants.USER_TOTAL_KEY);
        return Result.success(userService.removeById(id));
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        redisService.flushRedis(Constants.USER_PAGE_KEY);
        for (Integer id : ids) {
            redisService.flushRedis(Constants.USER_SINGLE_KEY + id);
        }
        redisService.flushRedis(Constants.USER_NUM_KEY);
        redisService.flushRedis(Constants.USER_TOTAL_KEY);
        return Result.success(userService.removeByIds(ids));
    }

    /**
     * 获取用户数量
     *
     * @return
     */
    @GetMapping("/getNum")
    public Integer getUserNum() {
        return userService.getNum();
    }

    /**
     * 获取单个用户
     *
     * @param id
     * @return
     */
    @AuthAccess
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(userService.getUserById(id));
    }

    /**
     * 获取用户分页数据
     *
     * @param pageNum
     * @param pageSize
     * @param username
     * @param nickname
     * @param speciality
     * @param address
     * @return
     */
    @AuthAccess
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String username,
                           @RequestParam(defaultValue = "") String nickname,
                           @RequestParam(defaultValue = "") String speciality,
                           @RequestParam(defaultValue = "") String address) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (!"".equals(username)) {
            queryWrapper.like("username", username);
        }
        if (!"".equals(nickname)) {
            queryWrapper.like("nickname", nickname);
        }
        if (!"".equals(speciality)) {
            queryWrapper.like("speciality", speciality);
        }
        if (!"".equals(address)) {
            queryWrapper.like("address", address);
        }
        /*
        User currentUser = TokenUtils.getCurrentUser();
        System.out.println("当前用户的用户名：" + currentUser.getUsername());*/
        queryWrapper.orderByDesc("id");
        return Result.success(userService.getPage(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 获取用户OJ数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @AuthAccess
    @GetMapping("/OJ")
    public Result findOJ(@RequestParam Integer pageNum,
                         @RequestParam Integer pageSize) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("pass_num");
        return Result.success(userService.getPage(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 导出
     *
     * @param response
     * @throws IOException
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
//        从数据库查出所有数据
        List<User> list = userService.list();
//        通过工具类创建writer，写到磁盘路径
//        ExcelWriter writer = ExcelUtil.getWriter(fileUploadPath+"/用户信息.xlsx");
//        在内存操作写出到浏览器
        ExcelWriter writer = ExcelUtil.getWriter(true);
//        一次性写出list内的对象到excel，使用默认样式。强制输出标题
        writer.write(list, true);

//        设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();
    }

    /**
     * 导入
     *
     * @param file
     * @throws IOException
     */
    @PostMapping("/import")
    public Result imp(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        List<User> list = reader.readAll(User.class);
        return list != null ? Result.success(userService.saveBatch(list)) : Result.error(Constants.CODE_400,
                "当前导入表格的格式错误 " +
                        "o(╥﹏╥)o");
    }

    /**
     * 登录
     *
     * @param user
     * @return
     */
    @Transactional
    @PostMapping("/login")
    public Result login(@RequestBody UserDto user) {
        String username = user.getUsername();
        String password = user.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        UserDto dto = userService.login(user);
        redisService.flushRedis(Constants.USER_PAGE_KEY);
        redisService.flushRedis(Constants.USER_NUM_KEY);
        redisService.flushRedis(Constants.USER_TOTAL_KEY);
        return Result.success(dto);
    }

    /**
     * 注册
     *
     * @param user
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestBody UserDto user) {
        String username = user.getUsername();
        String password = user.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        return Result.success(userService.register(user));
    }
}

