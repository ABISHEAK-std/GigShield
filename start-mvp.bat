@echo off
REM ====================================================
REM ALLIXIA - Start MVP with Neon Database
REM ====================================================

echo.
echo ╔═══════════════════════════════════════════════════╗
echo ║         ALLIXIA MVP - Starting Application        ║
echo ║    AI-Powered Parametric Insurance Platform       ║
echo ╚═══════════════════════════════════════════════════╝
echo.

cd /d "%~dp0allixia-backend"

echo [1/3] Checking Java and Maven...
java -version
echo.
mvn -version
echo.

echo [2/3] Building application (skipping tests for faster startup)...
echo Running: mvn clean package -DskipTests
echo.
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo.
    echo ❌ BUILD FAILED - Check errors above
    pause
    exit /b 1
)

echo.
echo ✅ Build successful!
echo.

echo [3/3] Starting ALLIXIA application...
echo.
echo ╔═══════════════════════════════════════════════════╗
echo ║  🚀 Application starting with Neon Database       ║
echo ║                                                   ║
echo ║  Health Check: http://localhost:8080/api/health   ║
echo ║  Dashboard: http://localhost:8080/api/demo/dashboard
echo ║                                                   ║
echo ║  Press Ctrl+C to stop the server                  ║
echo ╚═══════════════════════════════════════════════════╝
echo.

java -jar target\allixia-backend-1.0.0.jar
