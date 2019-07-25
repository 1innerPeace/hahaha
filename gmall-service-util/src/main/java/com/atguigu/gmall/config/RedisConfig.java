package com.atguigu.gmall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    // 从配置文件中获取host,port,database. 建议对其进行软编码，放在application.properties
    @Value("${spring.redis.host:disabled}")
    private String host;

    @Value("${spring.redis.port:0}")
    private int port;

    @Value("${spring.redis.database:0}")
    private int database;

    // 相当于在xxx.xml 中添加一个<bean></bean>
    @Bean
    public RedisUtil getRedisUtil(){
        if ("disabled".equals(host)){
            return  null;
        }
        RedisUtil redisUtil = new RedisUtil();
        // 初始化连接池
        redisUtil.init(host,port,database);

        return redisUtil;
    }

}
