@echo off
::---------------------------------------------
::- Author: Sarah Kreidler
::- Date: 11/4/2014
::- Modified from 
::- https://github.com/Ulrich-Palha/JavaWindowsServiceUsingCommonsDaemon.git
::---------------------------------------------
if "%OS%" == "Windows_NT" setlocal
set "CURRENT_DIR=%cd%"
cd ..
set "APPLICATION_SERVICE_HOME=%cd%"
echo %APPLICATION_SERVICE_HOME%
cd "%CURRENT_DIR%"

:: Name of service 
set SERVICE_NAME=RFIDMeetingTrackerService
set EXECUTABLE_NAME=%SERVICE_NAME%.exe
set EXECUTABLE=%APPLICATION_SERVICE_HOME%\bin\%EXECUTABLE_NAME%

:: java class containing start/stop routines
set CG_START_CLASS=edu.ucdenver.rfidmeetingtracker.reader
set CG_STOP_CLASS=%CG_START_CLASS%

:: java classpath 
set CG_PATH_TO_JAR_CONTAINING_SERVICE=%APPLICATION_SERVICE_HOME%\lib\rfidmeetingtracker-1.0.0.jar;%APPLICATION_SERVICE_HOME%\lib\jna.jar
:: startup the service automatically (set to manual otherwise)
set CG_STARTUP_TYPE=auto

:: Attendee log file path
set RFID_TRACKER_LOG_PATH=%APPLICATION_SERVICE_HOME%\log\attendee_log.csv
:: Path to directory containing the pcProx
set RFID_TRACKER_PCPROX_DIR="C:\Program Files (x86)\RF IDeas\pcProxSDK\API"

if "%1" == "" goto displayUsage
if /i %1 == install goto install
if /i %1 == remove goto remove
:displayUsage
echo Usage: service.bat install/remove [service_name]
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
set EXECUTE_STRING= %EXECUTABLE% //IS//%SERVICE_NAME% --Startup %CG_STARTUP_TYPE% --StartClass %CG_START_CLASS% --StopClass %CG_STOP_CLASS%
call:executeAndPrint %EXECUTE_STRING%
set EXECUTE_STRING= "%EXECUTABLE%" //US//%SERVICE_NAME% --StartMode jvm --StopMode jvm --Jvm %CG_PATH_TO_JVM%
call:executeAndPrint %EXECUTE_STRING%
set EXECUTE_STRING= "%EXECUTABLE%" //US//%SERVICE_NAME% --StartMethod %CG_START_METHOD% --StopMethod %CG_STOP_METHOD%
call:executeAndPrint %EXECUTE_STRING%
set EXECUTE_STRING= "%EXECUTABLE%" //US//%SERVICE_NAME% --StartParams %CG_START_PARAMS% --StopParams %CG_STOP_PARAMS%
call:executeAndPrint %EXECUTE_STRING%
set EXECUTE_STRING= "%EXECUTABLE%" //US//%SERVICE_NAME% ++JvmOptions "-Djna.library.path=%RFID_TRACKER_PCPROX_DIR%" --JvmMs 128 --JvmMx 256
call:executeAndPrint %EXECUTE_STRING%
echo.
echo The service '%SERVICE_NAME%' has been installed.
goto end
::--------
::- Functions
::-------
:executeAndPrint
%*
echo %*
goto:eof
:end