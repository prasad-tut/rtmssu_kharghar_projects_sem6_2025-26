#!/bin/bash

# Stop any running services
bash stop-all-services.sh

echo "Syncing UI files to API Gateway..."
mkdir -p microservices/api-gateway/src/main/resources/static
cp -r microservices/ui/* microservices/api-gateway/src/main/resources/static/

echo "Starting Ticket System Services..."
# Note: In a real environment, you'd run these in background or use docker-compose
# Here we just show the intended structure
# nohup mvn -f microservices/auth-user-service/pom.xml spring-boot:run > auth.log 2>&1 &
# ... and so on

echo "Services started (simulated). Access at http://localhost:9100"
