package com.company.secureapispring.customer.controllers;

import com.company.secureapispring.auth.entities.Organization;
import com.company.secureapispring.auth.entities.User;
import com.company.secureapispring.customer.CustomerSvcAppIT;
import com.company.secureapispring.customer.factory.EntityFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@CustomerSvcAppIT
@AutoConfigureMockMvc
@Transactional
public abstract class AbstractIT {

  private User dechatedUser;
  private Organization detachedOrganization;

  static final PostgreSQLContainer<?> dbContainer;

  static {
    dbContainer =
     new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.6-alpine"))
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

  @BeforeEach
  public void setup() {
    this.dechatedUser = EntityFactory.user().make();
    this.detachedOrganization = EntityFactory.organization().make();
  }

  protected User getDechatedUser() {
    return this.dechatedUser;
  }

  protected Organization getDetachedOrganization() {
    return this.detachedOrganization;
  }
}