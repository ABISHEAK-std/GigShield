@echo off
REM ====================================================
REM ALLIXIA - Test Script
REM ====================================================

echo.
echo ╔═══════════════════════════════════════════════════╗
echo ║            ALLIXIA Testing Script                 ║
echo ║    Dynamic Configuration Verification             ║
echo ╚═══════════════════════════════════════════════════╝
echo.

REM Check if curl is available
curl --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ ERROR: curl is not installed
    echo Please install curl or use Postman for testing
    pause
    exit /b 1
)

set BASE_URL=http://localhost:8080

echo [Test 1] Health Check
echo Testing: %BASE_URL%/api/health
curl -s %BASE_URL%/api/health
echo.
echo.

echo [Test 2] Dashboard - Active Triggers
echo Testing: %BASE_URL%/api/demo/dashboard
curl -s %BASE_URL%/api/demo/dashboard
echo.
echo.

echo [Test 3] Worker in Mumbai (Default Location)
echo Testing: Quick setup with phone 9876543210
curl -s -X POST "%BASE_URL%/api/demo/quick-setup/9876543210"
echo.
echo.

echo [Test 4] Worker in Delhi (Custom Location)
echo Testing: Quick setup with custom lat/lon and name
curl -s -X POST "%BASE_URL%/api/demo/quick-setup/9876543211?name=Ravi&latitude=28.6139&longitude=77.2090&coverageAmount=3000"
echo.
echo.

echo [Test 5] Worker in Bangalore (Another Location)
echo Testing: Quick setup with Bangalore coordinates
curl -s -X POST "%BASE_URL%/api/demo/quick-setup/9876543212?latitude=12.9716&longitude=77.5946"
echo.
echo.

echo [Test 6] Premium Quote - Mumbai
echo Testing: Dynamic premium calculation for Mumbai
curl -s -X POST "%BASE_URL%/api/demo/premium-quote" -H "Content-Type: application/json" -d "{\"latitude\": 19.0760, \"longitude\": 72.8777}"
echo.
echo.

echo [Test 7] Premium Quote - Delhi
echo Testing: Dynamic premium calculation for Delhi
curl -s -X POST "%BASE_URL%/api/demo/premium-quote" -H "Content-Type: application/json" -d "{\"latitude\": 28.6139, \"longitude\": 77.2090}"
echo.
echo.

echo [Test 8] Trigger - Heavy Rain in Mumbai
echo Testing: Simulate heavy rain trigger
curl -s -X POST "%BASE_URL%/api/demo/trigger/HEAVY_RAIN" -H "Content-Type: application/json" -d "{\"latitude\": 19.0760, \"longitude\": 72.8777}"
echo.
echo.

echo [Test 9] End-to-End Workflow
echo Testing: Complete automated workflow
curl -s "%BASE_URL%/api/demo/workflow/1234567890"
echo.
echo.

echo.
echo ╔═══════════════════════════════════════════════════╗
echo ║             All Tests Completed!                  ║
echo ║   Review the responses above to verify            ║
echo ║   - Different premiums for different locations    ║
echo ║   - Custom names, coverage amounts                ║
echo ║   - Automated claim and payout processing         ║
echo ╚═══════════════════════════════════════════════════╝
echo.

pause
