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
    "customer-svc:run")
        ./mvnw -pl customer-svc spring-boot:run
        ;;
    "customer-svc:install")
        ./mvnw clean install -Pcustomer-svc
        ;;
    "customer-svc:test-report")
        ./mvnw -pl customer-svc jacoco:report
        echo -e "\nClick in the link below to open the report on the default browser:\n"
        echo -e "file://$(pwd)/customer-svc/target/site/jacoco/index.html\n"
        ;;
    "services:db:ssh")
        docker exec -it secure-api-spring-postgresql bash
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
