package com.jixiebackstage.springboot.service.impl;

import com.jixiebackstage.springboot.service.IRedisService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RedisServiceImpl implements IRedisService {


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取缓存
     *
     * @param key
     * @return
     */
    @Override
    public String getRedis(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 设置缓存
     *
     * @param key
     * @param value
     */
    @Override
    public void setRedis(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 删除redis缓存
     *
     * @param key
     */
    @Override
    public void flushRedis(String key) {
        stringRedisTemplate.delete(key);
    }
}
