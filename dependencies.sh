#!/bin/bash
if [ ! -f "./src/main/docker/develop/docker-compose.env" ]
then
  cp ./src/main/docker/develop/docker-compose.env.example ./src/main/docker/developv/docker-compose.env
fi
docker-compose -f ./src/main/docker/develop/docker-compose.yaml --env-file=./src/main/docker/develop/docker-compose.env --compatibility "$@"
