package com.lee.distributed.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author lee
 * @date 2020/3/29 15:41
 */
@Configuration
public class RedisConfig {

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(8);
        jedisPoolConfig.setMaxIdle(3);
        jedisPoolConfig.setMaxWaitMillis(10000);
        jedisPoolConfig.setMinIdle(0);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, "127.0.0.1");
        return jedisPool;
    }

}
