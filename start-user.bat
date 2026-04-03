@echo off
echo Starting User Service...
cd /d "%~dp0\user_service\user_service"
if not exist ".m2" mkdir .m2
set MAVEN_OPTS=-Xmx512m
call mvnw.cmd spring-boot:run
if errorlevel 1 (
    echo Failed to start User Service
    pause
)
