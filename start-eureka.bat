@echo off
echo Starting Eureka Server...
cd /d "%~dp0\eureka_server_app\eureka_server_app"
if not exist ".m2" mkdir .m2
set MAVEN_OPTS=-Xmx512m
call mvnw.cmd spring-boot:run
if errorlevel 1 (
    echo Failed to start Eureka Server
    pause
)
