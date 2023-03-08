#!/bin/bash
set -e

# app database for dev
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -c "
    CREATE ROLE ${APP_DB_USERNAME}
    WITH NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT LOGIN NOREPLICATION NOBYPASSRLS
    PASSWORD '${APP_DB_PASSWORD}'"
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -c "
    CREATE DATABASE ${APP_DB_NAME}
    WITH OWNER = ${APP_DB_USERNAME} ENCODING = 'UTF8'"
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$APP_DB_USERNAME" -c "
    GRANT ALL ON SCHEMA public to ${APP_DB_NAME}"

# Keycloak database
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -c "
    CREATE ROLE ${KC_DB_USERNAME}
    WITH NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT LOGIN NOREPLICATION NOBYPASSRLS
    PASSWORD '${KC_DB_PASSWORD}'"
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -c "
    CREATE DATABASE ${KC_DB_URL_DATABASE}
    WITH OWNER = ${KC_DB_USERNAME} ENCODING = 'UTF8'"
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$KC_DB_URL_DATABASE" -c "
    GRANT ALL ON SCHEMA public to ${KC_DB_USERNAME}"


