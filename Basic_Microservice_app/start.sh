#!/bin/bash

# Get the root directory
ROOT_DIR=$(pwd)

echo "Starting Microservices from $ROOT_DIR..."

# Function to start a service
start_service() {
    local service_dir=$1
    local log_file=$2
    local port=$3
    local name=$4

    echo "Starting $name..."
    cd "$ROOT_DIR/$service_dir" || { echo "Failed to enter $service_dir"; return; }
    ./mvnw spring-boot:run > "$ROOT_DIR/$log_file" 2>&1 &
    
    if [ -n "$port" ]; then
        echo "Waiting for $name to start on port $port..."
        while ! nc -z localhost "$port"; do   
            sleep 2
            # Check if process is still running
            if ! ps -p $! > /dev/null; then
                echo "$name failed to start. Check $log_file"
                return 1
            fi
        done
        echo "$name is up!"
    fi
}

# Start Eureka (No port check yet, we check after)
start_service "eureka_server_app/eureka_server_app" "eureka.log" "9000" "Eureka Server"

# Start User Service
start_service "user_service/user_service" "user_service.log" "9001" "User Service"

# Start Ticket Service
start_service "ticket_service/ticket_service" "ticket_service.log" "9002" "Ticket Service"

# Start API Gateway
start_service "api_gateway/api_gateway" "api_gateway.log" "9090" "API Gateway"

echo "All services are starting up..."
echo "Service Logs: eureka.log, user_service.log, ticket_service.log, api_gateway.log"
