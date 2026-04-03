@echo off
echo Starting Microservices...

echo Starting Eureka Server (Port 9000)...
start /B "Eureka" cmd /c "cd eureka_server_app && mvn spring-boot:run"
timeout /t 20

echo Starting User Service (Port 9001)...
start /B "User" cmd /c "cd user_service && mvn spring-boot:run"
timeout /t 15

echo Starting Ticket Service (Port 9002)...
start /B "Ticket" cmd /c "cd ticket_service && mvn spring-boot:run"
timeout /t 15

echo Starting API Gateway (Port 9090)...
start /B "Gateway" cmd /c "cd api_gateway && mvn spring-boot:run"

echo All backend services are starting in background.
echo Eureka: http://localhost:9000
echo Gateway: http://localhost:9090
pause
