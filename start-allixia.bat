@echo off
echo ========================================
echo   ALLIXIA MVP - Complete Setup
echo ========================================
echo.

echo [1/4] Checking Java version...
java -version
if errorlevel 1 (
    echo ERROR: Java not found! Please install Java 17 or higher.
    pause
    exit /b 1
)
echo.

echo [2/4] Checking Maven...
cd allixia-backend
call mvn -version
if errorlevel 1 (
    echo ERROR: Maven not found! Please install Maven 3.8+
    pause
    exit /b 1
)
echo.

echo [3/4] Building backend (this may take a minute)...
call mvn clean install -DskipTests
if errorlevel 1 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)
echo.

echo [4/4] Starting ALLIXIA Backend Server...
echo.
echo ========================================
echo   Backend: http://localhost:8082
echo   Frontend: Open index.html in browser
echo ========================================
echo.
echo The server will start in 3 seconds...
timeout /t 3

call mvn spring-boot:run
