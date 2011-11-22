@echo off
rem @version $Revision$ ($Author$)  $Date$
rem helper to run maven2
rem

for /f "tokens=*" %%I in ('CD') do @set CurDir=%%~nI
title *%CurDir% - mvn with pyx and GWT Compile in production mode
call mvn install -P full,pyx,gwtc -P !draft -P !developer-env %*
@if errorlevel 1 goto errormark
title %CurDir%

@goto endmark
:errormark
	echo   
    pause
:endmark
@ENDLOCAL
