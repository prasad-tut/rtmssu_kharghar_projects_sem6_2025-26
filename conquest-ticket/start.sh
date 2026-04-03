#!/bin/bash

# Absolute Maven Path for this environment
MVN_PATH="/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven/bin/mvn"

# Kill any existing processes on these ports
echo "Cleaning up existing services..."
fuser -k 9000/tcp 9001/tcp 9002/tcp 9090/tcp > /dev/null 2>&1
sleep 2

echo "Starting Microservices..."

# Start Eureka Server
echo "Starting Eureka Server (Port 9000)..."
cd eureka_server_app && $MVN_PATH spring-boot:run > ../eureka.log 2>&1 &
sleep 25

# Start User Service
echo "Starting User Service (Port 9001)..."
cd user_service && $MVN_PATH spring-boot:run > ../user.log 2>&1 &
sleep 15

# Start Ticket Service
echo "Starting Ticket Service (Port 9002)..."
cd ticket_service && $MVN_PATH spring-boot:run > ../ticket.log 2>&1 &
sleep 15

# Start API Gateway
echo "Starting API Gateway (Port 9090)..."
cd api_gateway && $MVN_PATH spring-boot:run > ../gateway.log 2>&1 &

echo "All backend services are starting. Check logs (*.log) for details."
echo "Eureka: http://localhost:9000"
echo "Gateway: http://localhost:9090"
