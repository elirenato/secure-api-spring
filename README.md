# Secure API with Spring Boot 3

This project is a Java REST API application that was configured to use [Keycloak](https://www.keycloak.org) as access management.

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

## Directory Structure

This project follows the default Java structure `/src/main`.

- `/src/docs` - A quick reference of how to create a realm with Keycloak version 20 and a Postman collection with sample requests to the endpoints of this project.
- `/src/main/java` - The Java code.
- `/src/main/test` - The Java test code.
- `/src/main/docker/dev-env`: The Docker files to build and run the Postgres and Keycloak containers required for this project.
- `/src/main/jenkins`: - Jenkins file and resources that are used to set up the pipeline to build and deploy the application to a self-hosted env (EC2).

## Running tests

The tests use the Testcontainers to start a PostgreSQL container, so, you can just run:

`./mvnw test`

## Generate coverage test report 

`./mvnw jacoco:report`

and see `target/site/jacoco/index.html`

## Before run the application in dev mode

### Keycloak (20.0.1) and PostgreSQL (15.1)

This application depend on these services, so, you must have them running.

For dev purpose, you can use the bash script `./dependencies.sh up` to start Docker containers with these services.

The Keycloak will be available at `http://localhost:8080`.

To stop the containers you can use the script `./dependencies.sh down`.

### Keycloak Realm

To complete the setup of Keycloak, you also need to configure a Realm to test the application.

[Configure a new realm](./docs/create-new-realm-keycloak-20.pdf).

### Run the application in dev mode

Finally, it's possible to run the application:
```bash
./mvnw spring-boot:run
```

## Packaging and running the application

The application can be packaged using:
```bash
./mvnw package
```

## Deploying application to Kubernetes

The application is deployed to Kubernetes using Jenkins:

1. The first stage, run the tests with coverage report.
2. The second stage, package the application, build an image and push the image to a private Docker Registry repository.
3. The last stage, uses the Kubernetes resources generated to deploy the application.

See the [Jenkinsfile](./src/main/jenkins/Jenkinsfile) for more details:

PS: The Jenkins agent to build this project is Docker. The image created for Jenkins agent, uses a GraalVM JDK,
as I intend to do some tests building a native executable in the future.

## Creating a native executable

**PS: NOT TESTED YET**

You can create a native executable using:
```shell script
./mvnw package -Pnative
```

You can then execute your native executable with: `./target/secure-api-spring-1.0.0-SNAPSHOT-runner`
