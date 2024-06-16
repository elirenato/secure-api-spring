#!/bin/bash

set +x

# Create the database used by Keycloak
/create-db.sh "$KC_DB_URL_DATABASE" "$KC_DB_USERNAME" "$KC_DB_PASSWORD"

# Create the database used by Customer Service
/create-db.sh "$CUSTOMER_SERVICE_DATABASE" "$CUSTOMER_SERVICE_DB_USERNAME" "$CUSTOMER_SERVICE_DB_PASSWORD"
