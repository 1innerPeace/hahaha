package com.atguigu.gmall.config;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


public class RedisUtil {

    // 创建一个连接池
    private JedisPool jedisPool = null;
    // 初始化方法
    public void init(String host,int prot,int database){
        // 创建连接池的参数配置类
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 总数
        jedisPoolConfig.setMaxTotal(200);
        // 获取连接时等待的最大毫秒
        jedisPoolConfig.setMaxWaitMillis(10*1000);
        // 最少剩余数
        jedisPoolConfig.setMinIdle(10);
        // 如果到最大数，设置等待
        jedisPoolConfig.setBlockWhenExhausted(true);
        // 在获取连接时，检查是否有效
        jedisPoolConfig.setTestOnBorrow(true);
        // 创建连接池
        jedisPool = new JedisPool(jedisPoolConfig,host,prot,20*1000);
    }

    // 获取连接池中的Jedis
    public Jedis getJedis() {

        return jedisPool.getResource();

       /*Jedis jedis = null;
       try {
           jedis = jedisPool.getResource();
       }catch (RuntimeException e){
           e.printStackTrace();
       }finally {
           // 正确释放资源
           if(jedis != null ) {
               jedis.close();
           }
       }

        return jedis;
    */
    }
}
