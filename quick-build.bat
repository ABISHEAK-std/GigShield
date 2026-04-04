@echo off
echo ========================================
echo   ALLIXIA - Quick Fix Build
echo ========================================
echo.

echo [1/3] Stopping any running Java processes...
taskkill /F /IM java.exe 2>nul
if errorlevel 1 (
    echo No Java process running
) else (
    echo Java process stopped
)
timeout /t 2

echo.
echo [2/3] Cleaning target directory manually...
cd allixia-backend
if exist target (
    rmdir /s /q target
    echo Target directory cleaned
) else (
    echo Target directory not found - OK
)

echo.
echo [3/3] Building without clean step...
call mvn install -DskipTests

if errorlevel 1 (
    echo.
    echo ========================================
    echo   Build failed! Check errors above
    echo ========================================
    pause
    exit /b 1
)

echo.
echo ========================================
echo   Build successful! Starting server...
echo   Server will run on port 8082
echo ========================================
echo.
timeout /t 2

call mvn spring-boot:run
