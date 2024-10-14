# Secure API Spring - Customer Service

## Running tests

The tests use the Testcontainers to start a PostgreSQL container, so, you can just run:

`./mvnw install`

### Generate coverage test report 

`./mvnw jacoco:report`

and see `target/site/jacoco/index.html`

### Run the application in dev mode

`./mvnw spring-boot:run`

## Deploy with Jenkins to Kubernetes cluster

See the [README.md](./src/main/jenkins/README.md) for more details.
