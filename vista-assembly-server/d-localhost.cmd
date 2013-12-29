@echo off
rem @version $Revision$ ($Author$)  $Date$
rem helper to run maven
rem

for /f "tokens=*" %%I in ('CD') do @set CurDir=%%~nI
title *%CurDir% - mvn -P deploy,deploy-target-local
call mvn install -Dmaven.test.skip=true -P deploy,deploy-target-local %*
if errorlevel 1 (
    echo   
    pause
)
title %CurDir%
