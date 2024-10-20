package com.company.secureapispring.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.company.secureapispring.common",
        "com.company.secureapispring.customer",
        "com.company.secureapispring.auth",
})
@EnableJpaRepositories(
        basePackages = {
                "com.company.secureapispring.customer",
                "com.company.secureapispring.auth",
        }
)
@EntityScan(
        basePackages = {
                "com.company.secureapispring.customer",
                "com.company.secureapispring.auth",
        }
)
public class CustomerSvcApp {

    public static void main(String[] args) {
        SpringApplication.run(CustomerSvcApp.class, args);
    }

}
