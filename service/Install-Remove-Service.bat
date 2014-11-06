@echo off
::---------------------------------------------
::- Author: Sarah Kreidler
::- Date: 11/4/2014
::- Modified from 
::- https://github.com/Ulrich-Palha/JavaWindowsServiceUsingCommonsDaemon.git
::---------------------------------------------
if "%OS%" == "Windows_NT" setlocal
set "CURRENT_DIR=%cd%"
set "APPLICATION_SERVICE_HOME=%cd%"
echo %APPLICATION_SERVICE_HOME%
cd "%CURRENT_DIR%"

:: Name of service 
set SERVICE_NAME=RFIDMeetingTrackerService
set EXECUTABLE_NAME=%SERVICE_NAME%.exe
set EXECUTABLE=%APPLICATION_SERVICE_HOME%\bin\%EXECUTABLE_NAME%

:: java class containing start/stop routines
set CG_START_CLASS=edu.ucdenver.rfidmeetingtracker.reader.RFIDMeetingTracker
set CG_STOP_CLASS=%CG_START_CLASS%

:: java classpath 
set CG_PATH_TO_JAR_CONTAINING_SERVICE=%APPLICATION_SERVICE_HOME%\lib\rfidmeetingtracker-1.0.0.jar;%APPLICATION_SERVICE_HOME%\lib\jna.jar
:: startup the service automatically (set to manual otherwise)
set CG_STARTUP_TYPE=auto

:: Attendee log file path
set RFID_TRACKER_LOG_DIR=%APPLICATION_SERVICE_HOME%\log
set RFID_TRACKER_LOG_PATH=%RFID_TRACKER_LOG_DIR%\attendee_log.csv
set RFID_TRACKER_LOG_STDOUT=%RFID_TRACKER_LOG_DIR%\stdout.log
set RFID_TRACKER_LOG_STDERR=%RFID_TRACKER_LOG_DIR%\stderr.log
:: Path to directory containing the pcProx
set RFID_TRACKER_PCPROX_DIR='C:\Program Files (x86)\RF IDeas\pcProxSDK\OS\Win64'

if "%1" == "" goto displayUsage
if /i %1 == install goto install
if /i %1 == remove goto remove
:displayUsage
echo Usage: service.bat install/remove
goto end

:remove
:: Remove the service
"%EXECUTABLE%" //DS//%SERVICE_NAME%
echo The service '%SERVICE_NAME%' has been removed
goto end

:install
:: Install the Service
echo Installing service '%SERVICE_NAME%' ...
echo.
set EXECUTE_STRING="%EXECUTABLE%" //IS//%SERVICE_NAME% --Startup=%CG_STARTUP_TYPE%
set EXECUTE_STRING=%EXECUTE_STRING% --Install="%EXECUTABLE%"
set EXECUTE_STRING=%EXECUTE_STRING% --StartClass=%CG_START_CLASS% --StopClass=%CG_STOP_CLASS%
set EXECUTE_STRING=%EXECUTE_STRING% --StartMethod="start" --StopMethod="stop"
set EXECUTE_STRING=%EXECUTE_STRING% --Classpath="%APPLICATION_SERVICE_HOME%\lib\rfidmeetingtracker-1.0.0.jar;%APPLICATION_SERVICE_HOME%\lib\jna.jar"
set EXECUTE_STRING=%EXECUTE_STRING% --StartMode=jvm --StopMode=jvm 
set EXECUTE_STRING=%EXECUTE_STRING% --LogPath="%RFID_TRACKER_LOG_DIR%" --StdOutput="%RFID_TRACKER_LOG_STDOUT%" --StdError="%RFID_TRACKER_LOG_STDERR%"
:: set EXECUTE_STRING=%EXECUTE_STRING% --JvmOptions="-Djna.library.path=%RFID_TRACKER_PCPROX_DIR%" 
set EXECUTE_STRING=%EXECUTE_STRING% ++JvmOptions="-Djna.library.path=%RFID_TRACKER_PCPROX_DIR%;-Drfidmeetingtracker.log.path=%RFID_TRACKER_LOG_PATH%" 
echo %EXECUTE_STRING%
%EXECUTE_STRING%
echo.
echo The service '%SERVICE_NAME%' has been installed.
:end