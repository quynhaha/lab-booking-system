@echo off
echo ===================================
echo Build and Install Flutter App
echo ===================================
echo.

echo [1/4] Cleaning previous build...
cd android
call gradlew clean
echo.

echo [2/4] Building APK...
call gradlew assembleDebug
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)
echo.

echo [3/4] Checking device connection...
adb devices
echo.

echo [4/4] Installing APK...
adb uninstall com.unilab.lab_booking_mobile 2>nul
adb install app\build\outputs\apk\debug\app-debug.apk
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Installation failed!
    pause
    exit /b 1
)
echo.

echo ===================================
echo SUCCESS! App installed successfully
echo ===================================
echo.
echo You can now open the app on your device.
echo.

cd ..
pause
