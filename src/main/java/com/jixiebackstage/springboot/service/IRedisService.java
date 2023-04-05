package com.jixiebackstage.springboot.service;

public interface IRedisService {

    String getRedis(String key);

    void setRedis(String key, String value);

    void flushRedis(String key);
}
