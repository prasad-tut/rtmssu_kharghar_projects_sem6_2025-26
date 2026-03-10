#!/bin/bash

# Ticket Management System - Start All Services
# This script starts all microservices and the UI

echo "🚀 Starting Ticket Management System..."
echo "========================================"

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Base directory
BASE_DIR="/Users/mac/Downloads/sem6/ticketmicro/microservices"

# Function to start a Spring Boot service
start_service() {
    local service_name=$1
    local service_dir="$BASE_DIR/$service_name"
    
    echo -e "${BLUE}Starting $service_name...${NC}"
    cd "$service_dir"
    
    # Start the service in the background
    mvn spring-boot:run > "$service_name.log" 2>&1 &

    
    echo -e "${GREEN}✓ $service_name started (PID: $!)${NC}"
    echo "$!" > "$service_dir/$service_name.pid"
}

# Function to start the UI server
start_ui() {
    echo -e "${BLUE}Starting UI Server on port 5050...${NC}"
    cd "$BASE_DIR/ui"
    
    # Start a simple HTTP server using Python
    python3 -m http.server 5050 > ui.log 2>&1 &
    
    echo -e "${GREEN}✓ UI Server started (PID: $!)${NC}"
    echo "$!" > "$BASE_DIR/ui/ui.pid"
}

# Start all services
echo ""
echo "Starting Backend Services..."
echo "----------------------------"

start_service "auth-user-service"
sleep 5

start_service "ticket-service"
sleep 3

start_service "customer-service"
sleep 3

start_service "admin-service"
sleep 3

start_service "executive-service"
sleep 3

start_service "api-gateway"
sleep 3

echo ""
echo "Starting Frontend..."
echo "----------------------------"
start_ui

echo ""
echo -e "${GREEN}========================================"
echo "✅ All services started successfully!"
echo "========================================${NC}"
echo ""
echo "Service URLs:"
echo "  • Auth Service:      http://localhost:9101"
echo "  • Ticket Service:    http://localhost:9102"
echo "  • Customer Service:  http://localhost:9103"
echo "  • Admin Service:     http://localhost:9104"
echo "  • Executive Service: http://localhost:9105"
echo "  • UI:                http://localhost:5050"
echo ""
echo -e "${YELLOW}To stop all services, run: ./stop-all-services.sh${NC}"
echo ""
