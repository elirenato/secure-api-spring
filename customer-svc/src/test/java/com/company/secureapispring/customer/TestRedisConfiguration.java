package com.company.secureapispring.customer;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;

class TestRedisConfiguration {
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + System.getProperty("REDIS_HOST_PORT"));
        return Redisson.create(config);
    }
}
