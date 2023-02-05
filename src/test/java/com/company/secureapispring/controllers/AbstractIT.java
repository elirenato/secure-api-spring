package com.company.secureapispring.controllers;

import com.company.secureapispring.SecureApiSpringIT;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SecureApiSpringIT
@AutoConfigureMockMvc
@Transactional
public abstract class AbstractIT {

  @Autowired
  protected EntityManager emTest;

  static final PostgreSQLContainer<?> dbContainer;

  static {
    dbContainer =
     new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.1"))
      .withDatabaseName("app_test")
      .withUsername("app_test")
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