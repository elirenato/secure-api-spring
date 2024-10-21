package com.company.secureapispring.auth.config;

import com.company.secureapispring.auth.utils.TestJWTUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

public class TestSecurityConfig {
    @Bean
    public JwtDecoder jwtDecoder() {
        return TestJWTUtils::decode;
    }
}
