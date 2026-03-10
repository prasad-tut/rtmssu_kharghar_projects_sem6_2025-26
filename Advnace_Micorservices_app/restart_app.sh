#!/bin/bash
echo "=========================================="
echo "      TicketHub Restart Script           "
echo "=========================================="

# Set memory limits to prevent OOM crashes
export MAVEN_OPTS="-Xmx512m -Xms256m -XX:MaxMetaspaceSize=256m"

echo "[1/5] Stopping all running services..."
# Find PIDs using ports 9100-9105 and kill them
lsof -ti:9100,9101,9102,9103,9104,9105 | xargs kill -9 2>/dev/null
# Kill any ngrok and java instances
pkill -9 ngrok 2>/dev/null
pkill -9 java 2>/dev/null
sleep 2
echo "Services stopped."

echo "[2/5] Syncing UI files..."
# Copy all UI files to API Gateway static resources
cp -R microservices/ui/* microservices/api-gateway/src/main/resources/static/
echo "UI files synced."

echo "[3/5] Starting Microservices..."
# Start all services in the background and disown to keep them alive
nohup mvn -f microservices/auth-user-service/pom.xml spring-boot:run -DskipTests > auth.log 2>&1 & disown
echo " - Auth Service started"

nohup mvn -f microservices/ticket-service/pom.xml spring-boot:run -DskipTests > ticket.log 2>&1 & disown
echo " - Ticket Service started"

nohup mvn -f microservices/customer-service/pom.xml spring-boot:run -DskipTests > customer.log 2>&1 & disown
echo " - Customer Service started"

nohup mvn -f microservices/admin-service/pom.xml spring-boot:run -DskipTests > admin.log 2>&1 & disown
echo " - Admin Service started"

nohup mvn -f microservices/executive-service/pom.xml spring-boot:run -DskipTests > executive.log 2>&1 & disown
echo " - Executive Service started"

nohup mvn -f microservices/api-gateway/pom.xml spring-boot:run -DskipTests > gateway.log 2>&1 & disown
echo " - API Gateway started"

echo "[4/5] Waiting for services to initialize (15s)..."
sleep 15

echo "[5/5] Starting Ngrok..."
# Start ngrok on port 9100 (Gateway)
ngrok http 9100 > ngrok.log 2>&1 &

echo "=========================================="
echo "   Restart Complete! 🚀"
echo "=========================================="
echo "View logs in *.log files."
echo "Check ngrok URL: curl http://localhost:4040/api/tunnels"
