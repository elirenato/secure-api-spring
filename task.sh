#!/bin/bash

case $1 in
    "services:up")
        cp -n docker/.env.example docker/.env && docker compose --env-file=docker/.env -f docker/services.yml up --build
        ;;
    "services:up:bg")
        cp -n docker/.env.example docker/.env
        docker compose --env-file=docker/.env -f docker/services.yml up --build -d
        ;;
    "services:down")
        docker compose --env-file=docker/.env -f docker/services.yml down -v
        ;;
    "customer-service:run")
        ./mvnw -pl customer-service spring-boot:run
        ;;
    "customer-service:install")
        ./mvnw clean install -Pcustomer-service
        ;;
    "customer-service:test-report")
        ./mvnw -pl customer-service jacoco:report
        echo -e "\nClick in the link below to open the report on the default browser:\n"
        echo -e "file://$(pwd)/customer-service/target/site/jacoco/index.html\n"
        ;;
    "docker:jenkins:up")
        docker compose -f docker/jenkins/jenkins-agent.yml up --build
        ;;
    "docker:jenkins:bash")
        docker exec -it jenkins_agent bash
        ;;
    *)
        echo "Invalid task name: $1. Please provide a valid task name."
        ;;
esac
