package com.company.secureapispring.auth;

import com.company.secureapispring.auth.cache.CacheName;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

@EnableCaching
@SpringBootApplication
public class AuthLibSpringBootApp {
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(CacheName.USER_BY_USERNAME, CacheName.ORG_BY_ALIAS);
    }
}
