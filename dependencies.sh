#!/bin/bash
if [ ! -f "./src/main/docker/docker-compose.env" ]
then
  cp ./src/main/docker/docker-compose.env.example ./src/main/docker/docker-compose.env
fi
docker-compose -f ./src/main/docker/docker-compose.yaml --env-file=./src/main/docker/docker-compose.env --compatibility "$@"