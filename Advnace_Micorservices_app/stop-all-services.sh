#!/bin/bash

# Ticket Management System - Stop All Services
# This script stops all running microservices and the UI

echo "🛑 Stopping Ticket Management System..."
echo "========================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

# Base directory
BASE_DIR="/Users/mac/Downloads/sem6/ticketmicro/microservices"

# Function to stop a service
stop_service() {
    local service_name=$1
    local pid_file="$BASE_DIR/$service_name/$service_name.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null 2>&1; then
            echo -e "${RED}Stopping $service_name (PID: $pid)...${NC}"
            kill $pid
            rm "$pid_file"
            echo -e "${GREEN}✓ $service_name stopped${NC}"
        else
            echo "⚠ $service_name is not running"
            rm "$pid_file"
        fi
    else
        echo "⚠ No PID file found for $service_name"
    fi
}

# Stop all services
echo ""
echo "Stopping Backend Services..."
echo "----------------------------"

stop_service "auth-user-service"
stop_service "ticket-service"
stop_service "customer-service"
stop_service "admin-service"
stop_service "executive-service"
stop_service "api-gateway"

echo ""
echo "Stopping Frontend..."
echo "----------------------------"

# Stop UI
pid_file="$BASE_DIR/ui/ui.pid"
if [ -f "$pid_file" ]; then
    pid=$(cat "$pid_file")
    if ps -p $pid > /dev/null 2>&1; then
        echo -e "${RED}Stopping UI Server (PID: $pid)...${NC}"
        kill $pid
        rm "$pid_file"
        echo -e "${GREEN}✓ UI Server stopped${NC}"
    else
        echo "⚠ UI Server is not running"
        rm "$pid_file"
    fi
else
    echo "⚠ No PID file found for UI Server"
fi

echo ""
echo -e "${GREEN}========================================"
echo "✅ All services stopped!"
echo "========================================${NC}"
echo ""
