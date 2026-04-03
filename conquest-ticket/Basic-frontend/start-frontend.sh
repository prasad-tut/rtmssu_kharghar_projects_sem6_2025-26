#!/bin/bash

# Kill any existing process on port 8081
echo "Cleaning up port 8081..."
fuser -k 8081/tcp > /dev/null 2>&1
sleep 1

echo "Starting Frontend Server on http://localhost:8081..."
# Using python simple server for vanilla frontend
python3 -m http.server 8081
