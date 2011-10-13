@echo off
rem @version $Revision$ ($Author$)  $Date$
rem helper to run maven2
rem

for /f "tokens=*" %%I in ('CD') do @set CurDir=%%~nI
title *%CurDir% - mvn with pyx and GWT Compile
call mvn install -P pyx,gwtc,draft %* -Dmaven.test.skip=true
@if errorlevel 1 goto errormark
title %CurDir%

@goto endmark
:errormark
	echo   
    pause
:endmark
@ENDLOCAL
