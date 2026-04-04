@echo off
echo ========================================
echo   ALLIXIA MVP - Quick Start
echo ========================================
echo.

echo Starting backend server...
cd allixia-backend

start "ALLIXIA Backend" cmd /k "mvn spring-boot:run"

timeout /t 5

echo.
echo ========================================
echo   ALLIXIA is starting...
echo ========================================
echo.
echo Backend: http://localhost:8082/api/health
echo Frontend: Opening in browser...
echo.
echo Press Ctrl+C in the backend window to stop
echo ========================================

timeout /t 3

start "" "http://localhost:8082/api/health"
cd ..
start "" "index.html"

echo.
echo All services started!
pause
