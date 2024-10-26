package com.company.secureapispring.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.company.secureapispring.auth",
        "com.company.secureapispring.customer",
})
@EnableJpaRepositories(
        basePackages = {
                "com.company.secureapispring.auth",
                "com.company.secureapispring.customer",
        }
)
@EntityScan(
        basePackages = {
                "com.company.secureapispring.auth",
                "com.company.secureapispring.customer",
        }
)
public class CustomerSvcApp {

    public static void main(String[] args) {
        SpringApplication.run(CustomerSvcApp.class, args);
    }

}
