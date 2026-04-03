@echo off
echo Starting Ticket Service...
cd /d "%~dp0\ticket_service\ticket_service"
if not exist ".m2" mkdir .m2
set MAVEN_OPTS=-Xmx512m
call mvnw.cmd spring-boot:run
if errorlevel 1 (
    echo Failed to start Ticket Service
    pause
)
