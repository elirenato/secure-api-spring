# Secure API with Spring Boot 3

This git mono repository is an example Java REST API application that was configured to use [Keycloak](https://www.keycloak.org) as access management.

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

## Initial steps

### 1st step

Right after clone the repository, just run the install command inside the root directory:

`./mvnw install`

Maven will download all the dependencies and run the tests with success.

## 2nd step

The Customer Service app is the main Spring Application of this example, it depends on Keycloak and the PostgreSQL.

To start these dependencies run the following command:

`cd keycloak && ./keycloak.sh up -d && cd ..`

After that:

1. Keycloak will be available at `http://localhost:8080/admin/master/console/#/`. For test purpose the username and password is admin/admin. 
2. PostgreSQL will be available at `localhost:5432`. For test purpose the username and password will be postgres/postgres.

## 3rd step

Run the Customer Service App.

`cd customer-service && ./mvnw spring-boot:run`

You should be seen the Spring boot start without ANY error.

The default port is `8081`. If you try the url `http://localhost:8081/api/app/build` in the browser you should get a HTTP ERROR 401 (Unauthorized error).

To access this endpoint, you must be authenticated, see the next step to configure the Keycloak and create valid credentials to hit this endpoint.

## 4th step

1. Navigate to the url `http://localhost:8080/admin/master/console/#/` and login using the admin/admin credential.
2. Follow these [instructions](./keycloak/docs/create-new-realm-keycloak-20.pdf) to create a new realm inside Keycloak. Each realm has its own set of users, roles, and permissions, and a user in one realm is not automatically granted access to resources in another realm.

## 5th step

Let's use Postman to make request to the Customer Service API.

1. Create a new Workspace with a meaningful like Spring Boot Application.
2. Inside `Collections`, import this [Postman collection](./customer-service/docs/App.postman_collection.json).
3. Inside `Environments`, import this [Postman environment](./customer-service/docs/Local.postman_environment.json). Variables of the Postman environment that you must change or double check:
   - client_secret: You must change the value of this variable with the value that Keycloak generated when you created the Client inside the Realm. Copy it from this place: App (Realm) -> Clients (secure-api) -> Credentials (Tab of the secure-api client) -> Client Secret (click the icon to see the value).
   - keycloakClientID, realmUsername and realmPassword: If you followed the values suggested on the PDF of the 4th step, you won't need to change, otherwise, please set them with the values you have used.
4. Go to `Collections->Keycloak->Generate Access Token` POST request and click Send button. You should be able to make this request with success.
   - The `Generate Access Token` request will automatically set the environment variable called `authToken` that will be required for the next requests.
5. Go to `Collections->Customer Service->App Build Version` GET request and click Send button. Now you should be able to make this request with success.

In the same way as App Build version should have worked, the following requests should also work if called in the same order:
1. `Customer Service->Create customer`.
2. `Customer Service->Get customer`.
3. `Customer Service->List all customers`.
4. `Customer Service->Update customer`.
5. `Customer Service->Delete customer`.


## Open the repository using IntelliJ IDEA (Community Edition)

When opening this repository with IntelliJ, open the root directory `secure-api-spring` directly, it will automatically import the `common-library` and `customer-service` as modules.

When the IDE show the popup `Lombok requires enable annotation processing`, click in the button `Enable annotation processing`.

You should be able to run tests and debug directly from the IDE normally.

## Directory Organization

The directories are structured like a [Multi Module Project for Spring Boot](https://spring.io/guides/gs/multi-module/):
- The [common-library](./common-library/README.md) directory is a library module, it was created with the purpose to share code between different services.
- The [customer-service](./customer-service/README.md) directory is the main application module of this repository, and it was bootstrapped with the dependencies mentioned above.
- The [keycloak](./keycloak/README.md) directory is not a maven module, however it was created to keep docker images that are used to run Keycloak and PostgreSQL.

PS: For more information about deploy using Jenkins, see the README file of `customer-service` above.