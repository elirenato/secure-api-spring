package com.company.secureapispring.controllers;

import com.company.secureapispring.SecureApiSpringIT;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SecureApiSpringIT
@AutoConfigureMockMvc
public abstract class AbstractIT {
 
  static final PostgreSQLContainer<?> dbContainer;
 
  static {
    dbContainer =
     new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.1"))
      .withDatabaseName("test")
      .withUsername("test")
      .withPassword("change_me")
      .withReuse(true)
    ;
    dbContainer.start();
  }
 
  @DynamicPropertySource
  static void datasourceConfig(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", dbContainer::getJdbcUrl);
    registry.add("spring.datasource.password", dbContainer::getPassword);
    registry.add("spring.datasource.username", dbContainer::getUsername);
  }
}