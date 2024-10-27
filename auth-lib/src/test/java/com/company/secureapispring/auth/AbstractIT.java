package com.company.secureapispring.auth;

import com.company.secureapispring.auth.entities.Organization;
import com.company.secureapispring.auth.entities.User;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class AbstractIT {
    protected int fakerCounter = 0;
    protected static final Faker faker = new Faker();

    @Autowired
    protected CacheManager cacheManager;

    private static final PostgreSQLContainer<?> dbContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.6-alpine"))
            .withDatabaseName("auth_lib_test")
            .withUsername("auth_lib_test")
            .withPassword("password");

    static {
        dbContainer.start();
        System.setProperty("DATASOURCE_JDBC_URL_TEST", dbContainer.getJdbcUrl());
        System.setProperty("DATASOURCE_USERNAME_TEST", dbContainer.getUsername());
        System.setProperty("DATASOURCE_PASSWORD_TEST", dbContainer.getPassword());
    }

    @BeforeEach
    public void setup() {
        cacheManager.getCacheNames().forEach(s -> Objects.requireNonNull(cacheManager.getCache(s)).clear());
    }

    @AfterTestClass
    public static void stopContainer() {
        dbContainer.stop();
    }

    protected String generateUniqueEmail() {
        return faker.internet().emailAddress().replace("@", "+" + (fakerCounter++) + "@");
    }

    protected User makeUser() {
        User user = new User();
        user.setEmail(generateUniqueEmail());
        user.setUsername(faker.internet().username());
        user.setGivenName(faker.name().firstName());
        user.setFamilyName(faker.name().lastName());
        user.setEmailVerified(false);
        user.setCreatedDate(Instant.now().minusSeconds(86400).truncatedTo(ChronoUnit.MICROS));
        user.setLastModifiedDate(Instant.now().minusSeconds(43200).truncatedTo(ChronoUnit.MICROS));
        return user;
    }

    protected Organization makeOrganization() {
        String email = generateUniqueEmail();
        String username = email.substring(0, email.indexOf("@"));

        Organization organization = new Organization();
        organization.setAlias(faker.internet().uuid());
        organization.setCreatedDate(Instant.now().minusSeconds(86400).truncatedTo(ChronoUnit.MICROS));
        organization.setCreatedBy(username);
        organization.setLastModifiedDate(Instant.now().minusSeconds(43200).truncatedTo(ChronoUnit.MICROS));
        organization.setLastModifiedBy(username);
        return organization;
    }

}
