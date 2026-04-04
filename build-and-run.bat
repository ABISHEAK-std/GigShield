@echo off
REM ====================================================
REM ALLIXIA - Build and Run Script
REM ====================================================

echo.
echo ╔═══════════════════════════════════════════════════╗
echo ║         ALLIXIA Build and Run Script              ║
echo ║    AI-Powered Parametric Insurance Platform       ║
echo ╚═══════════════════════════════════════════════════╝
echo.

REM Check Java
echo [1/5] Checking Java installation...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ ERROR: Java is not installed or not in PATH
    echo Please install Java 17+ from https://adoptium.net/
    pause
    exit /b 1
)
echo ✅ Java is installed
java -version
echo.

REM Check Maven
echo [2/5] Checking Maven installation...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ ERROR: Maven is not installed or not in PATH
    echo Please install Maven from https://maven.apache.org/download.cgi
    pause
    exit /b 1
)
echo ✅ Maven is installed
mvn -version
echo.

REM Check PostgreSQL
echo [3/5] Checking PostgreSQL...
psql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ⚠️  WARNING: PostgreSQL command-line tools not found
    echo Make sure PostgreSQL server is running
    echo.
) else (
    echo ✅ PostgreSQL is installed
    psql --version
    echo.
)

REM Navigate to backend
cd /d "%~dp0allixia-backend"
if %errorlevel% neq 0 (
    echo ❌ ERROR: Could not find allixia-backend directory
    pause
    exit /b 1
)

REM Build the application
echo [4/5] Building ALLIXIA application...
echo Running: mvn clean package -DskipTests
echo.
mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo.
    echo ❌ ERROR: Build failed
    echo Please check the error messages above
    pause
    exit /b 1
)
echo.
echo ✅ Build successful!
echo.

REM Run the application
echo [5/5] Starting ALLIXIA application...
echo.
echo ╔═══════════════════════════════════════════════════╗
echo ║  Application is starting...                       ║
echo ║  Health Check: http://localhost:8080/api/health   ║
echo ║  Press Ctrl+C to stop the server                  ║
echo ╚═══════════════════════════════════════════════════╝
echo.

java -jar target\allixia-backend-1.0.0.jar

pause
