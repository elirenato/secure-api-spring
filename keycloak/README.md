# Secure API Spring (Keycloak and PostgreSQL dependencies)

## Before run the application in dev mode

### Keycloak (20.0.1) and PostgreSQL (15.1)

This application depend on these services, so, you must have them running.

For dev purpose, you can use the bash script `./keycloak.sh up` to start Docker containers with these services.

The Keycloak will be available at `http://localhost:8080`.

To stop the containers you can use the script `./keycloak.sh down`.

### Keycloak Realm

To complete the setup of Keycloak, you also need to configure a Realm to test the application.

[Configure a new realm](./docs/create-new-realm-keycloak-20.pdf).
