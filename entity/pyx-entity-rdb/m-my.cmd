@echo off
rem @version $Revision$ ($Author$)  $Date$
rem helper to run maven2
rem

for /f "tokens=*" %%I in ('CD') do @set CurDir=%%~nI
title *%CurDir% - mvn -P pyx4j-mysql-tests
call mvn  -P pyx4j-mysql-tests %*
if errorlevel 1 (
    echo   
    pause
)
title %CurDir%
