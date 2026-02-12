#!/bin/bash

echo "Starting Frontend Server..."
echo "Frontend will be available at: http://localhost:8000"
echo ""
echo "Make sure your backend services are running on:"
echo "  - API Gateway: http://localhost:9090"
echo ""
echo "Press Ctrl+C to stop the server"
echo ""

cd "$(dirname "$0")"
python3 -m http.server 8000
