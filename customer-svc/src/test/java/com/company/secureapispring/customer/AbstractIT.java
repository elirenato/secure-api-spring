package com.company.secureapispring.customer;

import com.company.secureapispring.auth.entities.Organization;
import com.company.secureapispring.auth.entities.User;
import com.company.secureapispring.customer.factory.EntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

public abstract class AbstractIT {

    private User dechatedUser;
    private Organization detachedOrganization;

    @Autowired
    protected CacheManager cacheManager;

    private static final PostgreSQLContainer<?> dbContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.6-alpine"))
            .withDatabaseName("customers_test")
            .withUsername("customers_test")
            .withPassword("password");

    private static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:alpine"))
            .withExposedPorts(6379);

    static {
        dbContainer.start();
        System.setProperty("DATASOURCE_JDBC_URL_TEST", dbContainer.getJdbcUrl());
        System.setProperty("DATASOURCE_USERNAME_TEST", dbContainer.getUsername());
        System.setProperty("DATASOURCE_PASSWORD_TEST", dbContainer.getPassword());

        redis.start();
        System.setProperty("REDIS_HOST", redis.getHost());
        System.setProperty("REDIS_PORT", redis.getMappedPort(6379).toString());
    }

    @BeforeEach
    public void setup() {
        this.dechatedUser = EntityFactory.user().make();
        this.detachedOrganization = EntityFactory.organization().make();
        cacheManager.getCacheNames().forEach(s -> Objects.requireNonNull(cacheManager.getCache(s)).clear());
    }

    @AfterTestClass
    public static void stopContainer() {
        dbContainer.stop();
    }

    protected User getDechatedUser() {
        return this.dechatedUser;
    }

    protected Organization getDetachedOrganization() {
        return this.detachedOrganization;
    }
}
