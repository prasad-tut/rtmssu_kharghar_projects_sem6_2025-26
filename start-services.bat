@echo off
echo Starting Conquest Microservices...

echo.
echo Starting MySQL with Docker Compose...
docker-compose up -d

echo.
echo Waiting for MySQL to be ready...
timeout /t 10

echo.
echo Starting Eureka Server...
start "Eureka Server" cmd /k "cd eureka_server_app\eureka_server_app && mvn spring-boot:run"

echo.
echo Waiting for Eureka Server to start...
timeout /t 30

echo.
echo Starting User Service...
start "User Service" cmd /k "cd user_service\user_service && mvn spring-boot:run"

echo.
echo Starting Ticket Service...
start "Ticket Service" cmd /k "cd ticket_service\ticket_service && mvn spring-boot:run"

echo.
echo Waiting for services to register...
timeout /t 20

echo.
echo Starting API Gateway...
start "API Gateway" cmd /k "cd api_gateway\api_gateway && mvn spring-boot:run"

echo.
echo All services are starting up!
echo.
echo Access points:
echo - Eureka Dashboard: http://localhost:8761
echo - API Gateway: http://localhost:8080
echo - User Service: http://localhost:8081
echo - Ticket Service: http://localhost:8082
echo.
pause