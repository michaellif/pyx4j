@echo off
rem @version $Revision$ ($Author$)  $Date$
rem helper to run maven2
rem

for /f "tokens=*" %%I in ('CD') do @set CurDir=%%~nI
title *%CurDir% - mvn with pyx
call mvn install -P !developer-env %*
if errorlevel 1 (
    echo   
    pause
)
title %CurDir%
