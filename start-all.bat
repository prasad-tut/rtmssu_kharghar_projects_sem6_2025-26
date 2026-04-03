@echo off
echo Starting Ticket System Microservices...
echo.

:: Start Eureka Server (Port 8761)
echo Starting Discovery Server on port 8761...
start /B cmd /c "cd eureka_server_app\eureka_server_app && mvnw.cmd spring-boot:run > eureka.log 2>&1"
timeout /t 15 /nobreak > nul

:: Start API Gateway (Port 8080)
echo Starting API Gateway on port 8080...
start /B cmd /c "cd api_gateway\api_gateway && mvnw.cmd spring-boot:run > gateway.log 2>&1"
timeout /t 10 /nobreak > nul

:: Start Ticket Service (Port 8081)
echo Starting Ticket Service on port 8081...
start /B cmd /c "cd ticket_service\ticket_service && mvnw.cmd spring-boot:run > ticket.log 2>&1"
timeout /t 10 /nobreak > nul

:: Start User Service
echo Starting User Service...
start /B cmd /c "cd user_service\user_service && mvnw.cmd spring-boot:run > user.log 2>&1"

echo.
echo All services are starting in separate windows...
echo.
echo Service URLs:
echo - Discovery Dashboard: http://localhost:8761
echo - API Gateway: http://localhost:8080
echo - Ticket Service: http://localhost:8081
echo.
echo Press any key to exit this window (services will keep running)...
pause > nul
