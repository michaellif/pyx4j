@echo off
rem $Id$
mkdir "%USERPROFILE%\.ssh"
copy "%~dp0known_hosts" "%USERPROFILE%\.ssh\known_hosts"
if errorlevel 1 (
    echo Error calling copy
    pause
    exit /b 1
)

echo "Known_hosts installed Ok"
pause
