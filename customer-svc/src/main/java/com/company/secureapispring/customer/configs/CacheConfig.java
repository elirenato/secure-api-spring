package com.company.secureapispring.customer.configs;

import com.company.secureapispring.customer.constants.CacheName;
import jakarta.annotation.Resource;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.ObjectInputFilter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean(destroyMethod="shutdown")
    RedissonClient redisson() throws Exception {
        String yamlConfig = Files.readString(
                Paths.get(Objects.requireNonNull(
                        CacheConfig.class.getClassLoader().getResource("redisson.yaml")).toURI()
                ));
        Config config = Config.fromYAML(yamlConfig);
        return Redisson.create(config);
    }

    @Bean
    CacheManager cacheManager(RedissonClient redissonClient) {
        Map<String, org.redisson.spring.cache.CacheConfig> config = new HashMap<>();
        int oneHour = 3600 * 1000;
        config.put(
                com.company.secureapispring.auth.constants.CacheName.USER_BY_USERNAME,
                new org.redisson.spring.cache.CacheConfig(oneHour, oneHour)
        );
        config.put(
                com.company.secureapispring.auth.constants.CacheName.ORG_BY_ALIAS,
                new org.redisson.spring.cache.CacheConfig(oneHour, oneHour)
        );
        config.put(CacheName.ALL_COUNTRIES, new org.redisson.spring.cache.CacheConfig(oneHour, oneHour));
        config.put(CacheName.ALL_STATE_PROVINCES, new org.redisson.spring.cache.CacheConfig(oneHour, oneHour));
        return new RedissonSpringCacheManager(redissonClient, config);
    }
}
