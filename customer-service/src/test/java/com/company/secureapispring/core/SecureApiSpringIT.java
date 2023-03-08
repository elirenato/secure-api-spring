package com.company.secureapispring.customer;

import com.company.secureapispring.common.config.TestSecurityConfig;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = {SecureApiSpringApp.class, TestSecurityConfig.class})
public @interface SecureApiSpringIT {
}
