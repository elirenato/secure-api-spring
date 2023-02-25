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
- Deploy with Jenkins to a self-hosted server with Kubernetes (EC2 or other).

## Directory Structure

This project follows the default Java structure `/src/main`.

- `/src/docs` - A quick reference of how to create a realm with Keycloak version 20 and a Postman collection with sample requests to the endpoints of this project.
- `/src/main/java` - The Java code.
- `/src/main/test` - The Java test code.
- `/src/main/docker`: The Docker files to build and run the Postgres and Keycloak containers required for test and development.
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

## Debug image generated for build by Jenkins

- Build the image:
```bash
export DOCKER_BUILDKIT=1 && docker build -t jenkins_java17 -f ./src/main/jenkins/Dockerfile . --no-cache
```
- Run the image as container:
```bash
docker run --name jenkins_java17 -v /root/.m2:/root/.m2 -v /var/run/docker.sock:/var/run/docker.sock -v $(pwd):/var/lib/secure-api-spring -w /var/lib/secure-api-spring -d jenkins_java17 sleep infinity
```
- Enter inside the container using the image ID returned by the previous command:
```bash
docker exec -it <Image ID> /bin/sh
```
- Execute a maven command:
```bash
./mvnw clean test
```

## Debug image generated for deployment

If you would like to debug the image generated for deployment, here are the steps:

- Start the dependencies. See `Before run the application in dev mode above` section above. 
- Build the image:
```bash
export DOCKER_BUILDKIT=1 && /mvnw spring-boot:build-image 
```
- Run the image as container:
```bash
docker run --name secure-api-spring -p 8081:8081 --add-host=host.docker.internal:host-gateway  --env DATASOURCE_JDBC_URL=jdbc:postgresql://host.docker.internal:5432/app_dev --env OIDC_AUTH_SERVER_URL=http://host.docker.internal:8080/realms/app --env LOG_LEVEL=DEBUG -d secure-api-spring:0.0.1-SNAPSHOT sleep infinity
```
- Enter inside the container using the image ID returned by the previous command:
```bash
docker exec -it <Image ID> /bin/sh
```
- Execute the jar file
```bash
java -jar /secure-api-spring-0.0.1-SNAPSHOT.jar
```

PS: Starting the project from a container like this, the OIDC_AUTH_SERVER_URL environment was changed to access Keycloak of the Host machine. The JWT token generated should also use the same URL to avoid the error `The iss claim is not valid`.

## Deploying application to Kubernetes using Jenkins

Create a `New Item` of the type `Pipeline` inside Jenkins.

- Use the URL of the Git repository as the Pipeline Repository URL.
- Set the `Branches to bulid` with the name of the branches. E.g. `*/main`.
- Set the `Script path` with `src/main/jenkins/Jenkinsfile`.

The `Jenkinsfile` is composed by the following stages:

- Test: Run the tests with coverage report.
- Build: Build the image and push to the registry repository.
- Deploy: Deploy the image to the Kubernetes.

PS: For subsequent builds, increase the version number inside all files with the current version (e.g. search by 0.0.1-SNAPSHOT and replace it by the next version in all files found).

See the [Jenkinsfile](./src/main/jenkins/Jenkinsfile) for more details.

### Jenkins required plugins

- Docker Pipeline.
- Docker Commons.
- JaCoCo.
- SSH Agent.

### Jenkins required environment variables (Manage Jenkins -> Configure System -> Global Properties)

- K8S_SERVER_HOST: Host of the server with the Kubernetes.
- K8S_SERVER_PORT: SSH of the server with the Kubernetes.
- K8S_SERVER_USERNAME: Username to access the server with the Kubernetes.
- REGISTRY_HOST: Private image registry. Leave empty to use the public Docker Hub registry.
- REGISTRY_USERNAME: Registry Username.
- REGISTRY_PASSWORD: Registry Password.
- REGISTRY_TAG: Registry Image Tag prefix. Use the Username for the public Docker Hub registry.
- SPRING_APP_PORT: Same server port used inside the application.properties.

PS: The server with the Kubernetes must have authentication with SSH key pairs. Add the private key inside (Manage Jenkins -> Credentials -> System -> Global credentials).

### Kubernetes Config Map example

```yaml
kind: ConfigMap
apiVersion: v1
metadata:
  name: secure-api-spring-cfg-map
  namespace: default
data:
  LOG_LEVEL: "ERROR"
  OIDC_AUTH_SERVER_URL: "https://<OIDC_HOST>/realms/app"
  DATASOURCE_JDBC_URL: "jdbc:postgresql://<DATABASE_HOST>:<DATABASE_PORT>/<DATABASE_NAME>"
  DATASOURCE_USERNAME: "<DATABASE_USERNAME>"
  DATASOURCE_PASSWORD: "<DATABASE_PASSWORD>"
  SPRING_APP_PORT: "<SPRING_APP_PORT>"
  SPRING_APP_CONTEXT_PATH: "/api"
  CORS_ORIGINS: "<CLIENT_APP_HOST_FOR_CORS_ORIGINS>"
```

### How this application was deployed for develop and test purpose.

- One AWS EC2 service was created from the scratch with the following specifications.
- Ubuntu 22.04.
- MicroK8S (Kubernetes). Some addons were added:
    - Cert-manager to manage SSL certificates with Lets encrypt.
    - NGINX ingress as a reverse proxy. Because of that there is no SSL configuration in the Spring Boot project, it runs behind a proxy that uses SSL.
    - Hostpath-storage to map the volume used by the private registry.

Inside the Kubernetes, the following services were deployed:
- PostgreSQL service as database.
- Keycloak version 20 as OIDC server.
- The Spring Boot app generated by this repository and deployed with Jenkins.
- Private image registry, although in the end it was not necessary because the image of the example was deployed to the public Docker Hub.

The Jenkins server was installed locally in the development machine.