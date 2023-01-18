#!/bin/bash
if [ ! -f "./src/main/docker/dev-env/docker-compose.env" ]
then
  cp ./src/main/docker/dev-env/docker-compose.env.example ./src/main/docker/dev-env/docker-compose.env
fi
docker-compose -f ./src/main/docker/dev-env/docker-compose.yaml --env-file=./src/main/docker/dev-env/docker-compose.env build
docker-compose -f ./src/main/docker/dev-env/docker-compose.yaml --env-file=./src/main/docker/dev-env/docker-compose.env up -d
docker-compose -f ./src/main/docker/dev-env/docker-compose.yaml --env-file=./src/main/docker/dev-env/docker-compose.env logs -f
