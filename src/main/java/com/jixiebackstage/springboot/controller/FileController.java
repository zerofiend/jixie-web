package com.jixiebackstage.springboot.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jixiebackstage.springboot.common.Result;
import com.jixiebackstage.springboot.entity.Files;
import com.jixiebackstage.springboot.service.IFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 文件相关接口
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${files.upload.path}")
    private String fileUploadPath;
    @Value("${server.ip}")
    private String serverIp;

    @Resource
    private IFileService fileService;

    /**
     * 头像上传
     *
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload/avatar")
    public String uploadAvatar(@RequestParam MultipartFile file) throws IOException {
        return upload(file, "avatar/");
    }

    /**
     * 博客上传
     *
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload/blog")
    public String uploadBlog(@RequestParam MultipartFile file) throws IOException {
        return upload(file, "blog/");
    }

    /**
     * 通知，动态和资料上传
     *
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload/data/{type}")
    public String uploadNotice(@RequestParam MultipartFile file, @PathVariable Integer type) throws IOException {
        if (type == 1) {
            return upload(file, "official/notice/");
        } else if (type == 2) {
            return upload(file, "official/news/");
        } else {
            return upload(file, "official/data/");
        }
    }

    /**
     * 文件上传接口
     *
     * @param file
     * @return
     * @throws IOException
     */
    public String upload(MultipartFile file, String folder) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();
        /*  先存储到磁盘  */
        File uploadParentFile = new File(fileUploadPath + folder);
        //  判断配置的文件目录是否存在，不存在就创建新的文件目录
        if (!uploadParentFile.exists()) {
            uploadParentFile.mkdirs();
        }
        //  定义一个文件唯一的标识码
        String uuid = IdUtil.fastSimpleUUID();
        File uploadFile = new File(fileUploadPath + folder + uuid + StrUtil.DOT + type);
        //  获取文件的md5
        String md5 = SecureUtil.md5(file.getInputStream());
        //  查询文件的md5是否存在
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5", md5);
        Files files = fileService.getOne(queryWrapper);

        //  获取文件的url
        String url;
        Files saveFile = new Files();
        if (files != null) {
            url = files.getUrl();
        } else {
            //  获取到的文件存到u盘里
            file.transferTo(uploadFile);
            url = "http://" + serverIp + ":9090/file/" + folder + uuid + StrUtil.DOT + type;
            //  唯一文件属性
            saveFile.setMd5(md5);
        }
        //  通用属性
        saveFile.setUrl(url);
        saveFile.setSize(size / 1024);
        saveFile.setName(originalFilename);
        saveFile.setType(type);
        fileService.saveOrUpdate(saveFile);
        return url;
    }

    //    下载头像
    @GetMapping("/avatar/{fileUuid}")
    public void downloadAvatar(@PathVariable String fileUuid, HttpServletResponse response) throws IOException {
        download("avatar/" + fileUuid, response);
    }

    //    下载轮播图
    @GetMapping("/official/carousel/{fileUuid}")
    public void downloadCarousel(@PathVariable String fileUuid, HttpServletResponse response) throws IOException {
        download("official/carousel/" + fileUuid, response);
    }

    //    下载博客图片
    @GetMapping("/blog/{fileUuid}")
    public void downloadBlog(@PathVariable String fileUuid, HttpServletResponse response) throws IOException {
        download("blog/" + fileUuid, response);
    }

    //    下载通知图片
    @GetMapping("/official/notice/{fileUuid}")
    public void downloadNotice(@PathVariable String fileUuid, HttpServletResponse response) throws IOException {
        download("official/notice/" + fileUuid, response);
    }

    //    下载动态图片
    @GetMapping("/official/news/{fileUuid}")
    public void downloadNews(@PathVariable String fileUuid, HttpServletResponse response) throws IOException {
        download("official/news/" + fileUuid, response);
    }

    //    下载资料图片
    @GetMapping("/official/data/{fileUuid}")
    public void downloadData(@PathVariable String fileUuid, HttpServletResponse response) throws IOException {
        download("official/data/" + fileUuid, response);
    }

    /**
     * 文件下载接口
     *
     * @param fileUuid
     * @param response
     * @throws IOException
     */
    public void download(@PathVariable String fileUuid, HttpServletResponse response) throws IOException {
        //  根据文件的唯一标识获取文件的字节流
        File uploadFile = new File(fileUploadPath + fileUuid);
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUuid, "UTF-8"));

        //  读取文件字节流
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }

    /**
     * 分页查询接口
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
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        queryWrapper.orderByDesc("id");
        return Result.success(fileService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 新增或更新
     *
     * @param files
     * @return
     */
    @PostMapping("/update")
    public Result save(@RequestBody Files files) {
        return Result.success(fileService.saveOrUpdate(files));
    }

    /**
     * 伪删除接口
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        Files files = fileService.getById(id);
        files.setIsDelete(true);
        fileService.updateById(files);
        return Result.success();
    }

    /**
     * 批量删除接口
     *
     * @param ids
     * @return
     */
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        //  select * from sys_files where id in (id,id,id......)
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        //  查询未删除的记录
        queryWrapper.eq("is_delete", false);
        queryWrapper.in("id", ids);
        List<Files> list = fileService.list(queryWrapper);
        for (Files files : list) {
            files.setIsDelete(true);
            fileService.updateById(files);
        }
        return Result.success();
    }
}
