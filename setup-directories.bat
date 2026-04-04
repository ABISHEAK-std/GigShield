@echo off
echo ================================================
echo ALLIXIA Project Directory Setup
echo ================================================
echo.

cd /d d:\ALLIXA

if not exist allixia-backend (
    echo Creating allixia-backend directory...
    mkdir allixia-backend
) else (
    echo allixia-backend directory already exists
)

cd allixia-backend

echo Creating Java package structure...
mkdir src\main\java\com\allixia\config 2>nul
mkdir src\main\java\com\allixia\controller 2>nul
mkdir src\main\java\com\allixia\dto 2>nul
mkdir src\main\java\com\allixia\entity 2>nul
mkdir src\main\java\com\allixia\exception 2>nul
mkdir src\main\java\com\allixia\repository 2>nul
mkdir src\main\java\com\allixia\security 2>nul
mkdir src\main\java\com\allixia\service 2>nul

echo Creating resources directories...
mkdir src\main\resources\db\migration 2>nul
mkdir src\main\resources\static 2>nul
mkdir src\main\resources\templates 2>nul

echo Creating test directories...
mkdir src\test\java\com\allixia 2>nul
mkdir src\test\resources 2>nul

echo.
echo ================================================
echo Directory structure created successfully!
echo ================================================
echo.
echo Next steps:
echo 1. Run the project to generate all source files
echo 2. Configure environment variables
echo 3. Build with: mvn clean install
echo 4. Run with: mvn spring-boot:run
echo.

dir /s /b /ad src
echo.
echo Press any key to exit...
pause >nul
