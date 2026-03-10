#!/bin/bash

echo "Starting Conquest Microservices..."

echo ""
echo "Starting MySQL with Docker Compose..."
docker-compose up -d

echo ""
echo "Waiting for MySQL to be ready..."
sleep 10

echo ""
echo "Starting Eureka Server..."
cd eureka_server_app/eureka_server_app
mvn spring-boot:run &
EUREKA_PID=$!
cd ../..

echo ""
echo "Waiting for Eureka Server to start..."
sleep 30

echo ""
echo "Starting User Service..."
cd user_service/user_service
mvn spring-boot:run &
USER_PID=$!
cd ../..

echo ""
echo "Starting Ticket Service..."
cd ticket_service/ticket_service
mvn spring-boot:run &
TICKET_PID=$!
cd ../..

echo ""
echo "Waiting for services to register..."
sleep 20

echo ""
echo "Starting API Gateway..."
cd api_gateway/api_gateway
mvn spring-boot:run &
GATEWAY_PID=$!
cd ../..

echo ""
echo "All services are starting up!"
echo ""
echo "Access points:"
echo "- Eureka Dashboard: http://localhost:8761"
echo "- API Gateway: http://localhost:8080"
echo "- User Service: http://localhost:8081"
echo "- Ticket Service: http://localhost:8082"
echo ""
echo "Process IDs:"
echo "- Eureka: $EUREKA_PID"
echo "- User Service: $USER_PID"
echo "- Ticket Service: $TICKET_PID"
echo "- API Gateway: $GATEWAY_PID"
echo ""
echo "Press Ctrl+C to stop all services"

# Wait for user interrupt
trap 'kill $EUREKA_PID $USER_PID $TICKET_PID $GATEWAY_PID; docker-compose down; exit' INT
wait