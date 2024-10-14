# Secure API with Spring Boot 3

This Git monorepository contains a sample Java REST API application configured to use [Keycloak](https://www.keycloak.org) for access management.

The project was bootstrapped using [Spring Initializer](https://start.spring.io/) with the following dependencies:

- Spring Boot Web (spring-boot-starter-web) for building RESTful APIs.
- JUnit Jupiter, Hamcrest, and Mockito (spring-boot-starter-test) for unit testing.
- Spring Security OAuth2 Resource Server (spring-boot-starter-oauth2-resource-server) to enable OIDC integration with Keycloak.
- Jacoco for generating test coverage reports.
- Spring Data and Hibernate Validator for data access and validation.
- Spring Doc Open API for API documentation.
- Liquibase with PostgreSQL as the database for managing database migrations.
- [TestContainers](https://www.testcontainers.org/) to run tests in an isolated PostgreSQL database environment.
- [Java Faker](http://github.com/DiUS/java-faker) for generating test data.
- [Lombok](https://projectlombok.org/) to reduce verbosity in the code.
- Jenkins for deploying to a self-hosted Kubernetes server.

## Requirements

1. Java JDK version 21 or higher.
2. Docker version 27 or higher.

## Initial steps

### 1st step

After cloning the repository, navigate to the root directory of the project and run the following command:

```bash
./mvnw clean install
```

## 2nd step

The customer service is the primary Spring application in this example. It relies on Keycloak for authentication and PostgreSQL as the database. To start these dependencies, run the following commands:

```bash
./task.sh services:up
```

## 3rd step

To run the customer service, use the following command in another terminal:

```bash
./mvnw -pl customer-svc spring-boot:run
```

## Try it out

You can test the API using the Swagger UI at the following URL:

http://localhost:8081/swagger-ui/index.html

1. Click the **Authorize** button and enter `secure-api` in the **client_id** field.
2. Click the **Authorize** button of the popup, which will redirect you to the Keycloak authentication page.

When the Keycloak service runs for the first time, it imports a realm called `App`, created exclusively for testing purposes.

This realm contains two users for authentication:
- The user with the administrator role (ROLE_ADMIN) has the username `admin` and password `password`. 
- The user with the analyst role (ROLE_ANALYST) has the username `analyst` and password `password`.

Note: The ROLE_ADMIN user is permitted to modify customers, while the ROLE_ANALYST user can only list them.

Once you have authenticated, you can begin testing the API endpoints using the **Try it out** button for each endpoint.


## Admin password for Keycloak and PostgreSQL

The Keycloak admin console is accessible at http://localhost:9080/admin/master/console/#/. For testing purposes, the super admin user credentials are as follows:

- Username: admin
- Password: admin

The PostgreSQL database is available at localhost:5432. For testing purposes, the super user credentials are:

- Username: postgresql
- Password: postgresql


## Directory Organization

The directories in this repository follow the structure of a [Multi Module Project for Spring Boot](https://spring.io/guides/gs/multi-module/):

- The [common-lib](./common-lib/README.md) directory is a library module designed to share code across different services.
- The [customer-svc](./customer-svc/README.md) directory is the main service of this repository, bootstrapped with the dependencies mentioned earlier.
- The [docker](./docker) directory is not a Maven module. It contains the Dockerfile used to build images for the Jenkins pipeline and Docker Compose templates to run the required dependencies.

Note: For more information about deployment using Jenkins, refer to the README file in the customer-svc directory.


