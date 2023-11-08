@echo off 
set local
cls

set "port=4723" REM you need to change the port number respectively

netstat -ano | findstr ":%port%" | findstr /i listening >nul

if %errorlevel% == 0 (
echo Appium is running on port %port%
echo Appium is already running 
 
echo Killing the appium server
taskkill /F /IM node.exe
echo Restarting the appium server
appium
 
) else (
echo Appium is not running 
echo starting the appium server along with the adb devices 
appium
)
ping localhost -n 6 > nul
endlocal