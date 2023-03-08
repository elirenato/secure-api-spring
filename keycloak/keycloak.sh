#!/bin/bash
if [ ! -f "./docker/docker-compose.env" ]
then
  cp ./docker/docker-compose.env.example ./docker/docker-compose.env
fi
docker-compose -f ./docker/docker-compose.yaml --env-file=./docker/docker-compose.env --compatibility "$@"
