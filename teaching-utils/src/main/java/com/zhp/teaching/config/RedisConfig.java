package com.zhp.teaching.config;

import com.zhp.teaching.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Class_Name RedisConfig
 * @Author zhongping
 * @Date 2020/7/7 14:17
 **/
@Configuration
public class RedisConfig {
    @Value("${spring.redis.host:disabled}")
    private String host;
    @Value("${spring.redis.port:0}")
    private int port;
    @Value("${spring.redis.database:1}")
    private int database;
    @Bean
    public RedisUtil getRedisUtil(){
        if (host.equals("disabled")){
            return null;
        }
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.initPool(host,port,database);
        return redisUtil;
    }
}
