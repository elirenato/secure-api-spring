package com.company.secureapispring.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.company.secureapispring")
public class SecureApiSpringApp {

	public static void main(String[] args) {
		SpringApplication.run(SecureApiSpringApp.class, args);
	}

}
