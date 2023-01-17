package com.company.secureapispring3;

import com.company.secureapispring3.config.TestSecurityConfig;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = {SecureApiSpring3Application.class, TestSecurityConfig.class})
public @interface SecureApiSpring3ApplicationIT {
}
