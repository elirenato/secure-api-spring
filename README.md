# Secure API with Spring Boot 3

This repository is an example Java REST API application that was configured to use [Keycloak](https://www.keycloak.org) as access management.

It was bootstrapped using [Spring Initializer](https://start.spring.io/) with the following dependencies:

- RESTful classic (spring-boot-starter-web).
- JUnit Jupiter, Hamcrest and Mockito (spring-boot-starter-test).
- OIDC to enable the integration with Keycloak (spring-boot-starter-oauth2-resource-server).
- Jacoco to generate the coverage test report.
- Spring Data and Hibernate Validator.
- Liquibase (With PostgreSQL as the database).
- [TestContainers](https://www.testcontainers.org/) to run tests in an isolated PostgresSQL database.
- [Java Faker](http://github.com/DiUS/java-faker) to generate test data.
- [Lombok](https://projectlombok.org/) to be less verbose in this example.
- Deploy with Jenkins to a self-hosted server with Kubernetes (EC2 or other).

## Directory structure

Some directories are structured like a [Multi Module Project for Spring Boot](https://spring.io/guides/gs/multi-module/):
- [core](./core/README.md) is the main application module of this repository, and it was bootstrapped with the dependencies mentioned above.
- [common](./common/README.md) is a library module, it was created with the purpose to share code between different services.

Other non-module directories:
- [keycloak](./keycloak/README.md) was created to store container images to run Keycloak and PostgreSQL.

## Running tests for all modules

`./mvnw test`
