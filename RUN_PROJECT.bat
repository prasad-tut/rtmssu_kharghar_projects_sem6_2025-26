@echo off
set "ROOT=%~dp0"
set "ROOT=%ROOT:~0,-1%"
echo ==========================================
echo    Ticket System - Diagnostic Startup
echo ==========================================
echo.

:: Check Java
echo [CHECK] Checking Java version...
java -version
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java not found! Please install JDK 17.
    pause
    exit /b
)

:: Memory Limit
set MAVEN_OPTS=-Xmx512m

:: 1. Eureka Discovery
echo [1/4] Starting Discovery Server (8761)...
cd /d "%ROOT%\eureka_server_app\eureka_server_app"
start "Ticket System Discovery" cmd /k "echo Starting... && mvnw.cmd spring-boot:run"
echo Waiting for Discovery to warm up...
timeout /t 20

:: 2. API Gateway
echo [2/4] Starting API Gateway (8080)...
cd /d "%ROOT%\api_gateway\api_gateway"
start "Ticket System Gateway" cmd /k "echo Starting... && mvnw.cmd spring-boot:run"
timeout /t 10

:: 3. Ticket Service
echo [3/4] Starting Ticket Service (8081)...
cd /d "%ROOT%\ticket_service\ticket_service"
start "Ticket System Ticket Service" cmd /k "echo Starting... && mvnw.cmd spring-boot:run"
timeout /t 10

:: 4. User Service
echo [4/4] Starting User Service (8082)...
cd /d "%ROOT%\user_service\user_service"
start "Ticket System User Service" cmd /k "echo Starting... && mvnw.cmd spring-boot:run"

echo.
echo ==========================================
echo SUCCESS: All windows should be open now.
echo IF WINDOWS CLOSE IMMEDIATELY, check for error:
echo 1. Right-click the .bat and 'Run as Administrator'
echo 2. Ensure MySQL is running on port 3306
echo ==========================================
pause
