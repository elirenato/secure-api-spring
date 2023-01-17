#!/bin/bash
if [ ! -f "./src/main/keycloak/docker-compose.env" ]
then
  cp ./src/main/keycloak/docker-compose.env.example ./src/main/keycloak/docker-compose.env
fi
docker-compose -f src/main/keycloak/docker-compose.yaml --env-file=./src/main/keycloak/docker-compose.env down
