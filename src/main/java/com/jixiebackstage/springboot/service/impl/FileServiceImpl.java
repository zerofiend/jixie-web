package com.jixiebackstage.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jixiebackstage.springboot.entity.Files;
import com.jixiebackstage.springboot.mapper.FileMapper;
import com.jixiebackstage.springboot.service.IFileService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 邹永靖
 * @since 2022-07-21
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, Files> implements IFileService {

}
