package com.company.secureapispring.customer;

import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

class TestConfiguration {
    @Bean
    public JwtDecoder jwtDecoder() {
        return TestJWTUtils::decode;
    }
}
