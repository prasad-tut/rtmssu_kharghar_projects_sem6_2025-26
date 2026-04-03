@echo off
echo Starting API Gateway...
cd /d "%~dp0\api_gateway\api_gateway"
if not exist ".m2" mkdir .m2
set MAVEN_OPTS=-Xmx512m
call mvnw.cmd spring-boot:run
if errorlevel 1 (
    echo Failed to start API Gateway
    pause
)
