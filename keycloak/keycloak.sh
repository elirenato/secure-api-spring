#!/bin/bash
if [ ! -f "./docker/.env" ]
then
  cp ./docker/.env.example ./docker/.env
fi
docker-compose -f ./docker/docker-compose.yaml --env-file=./docker/.env --compatibility "$@"
