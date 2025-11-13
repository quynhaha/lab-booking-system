@echo off
echo ===================================
echo Rebuild and Reinstall App
echo ===================================
echo.

echo Checking device connection...
adb devices
echo.

echo Uninstalling old app...
adb uninstall com.unilab.lab_booking_mobile 2>nul

echo.
echo Building new APK...
cd android
call gradlew assembleDebug
cd ..

echo.
echo Installing new APK...
adb install android\app\build\outputs\apk\debug\app-debug.apk

echo.
echo Done! App has been reinstalled.
pause
