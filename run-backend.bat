@echo off
cd allixia-backend
call mvn clean install -DskipTests
if errorlevel 1 exit /b 1
call mvn spring-boot:run
