package com.jixiebackstage.springboot.service.impl;

import com.jixiebackstage.springboot.entity.Dict;
import com.jixiebackstage.springboot.mapper.DictMapper;
import com.jixiebackstage.springboot.service.IDictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 邹永靖
 * @since 2022-07-25
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements IDictService {

}
