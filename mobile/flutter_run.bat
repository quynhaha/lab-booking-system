@echo off
REM Fix for path with spaces in username - set Gradle cache to Public folder
set GRADLE_USER_HOME=C:\Users\Public\.gradle

REM Run Flutter with all arguments passed to this script
flutter %*


