@echo off
rem @version $Revision$ ($Author$)  $Date$
rem helper to run maven3
rem

for /f "tokens=*" %%I in ('CD') do @set CurDir=%%~nI
title *%CurDir% - mvn with pyx and svn update

call mvn --file ..\pyx4j\pom.xml clean --fail-never
@if errorlevel 1 goto errormark

call mvn --file ..\pyx4j\pom.xml scm:update
@if errorlevel 1 goto errormark

call mvn --file ..\pyx4j\pom.xml -DskipTests=true
@if errorlevel 1 goto errormark

call mvn clean --fail-never
@if errorlevel 1 goto errormark

call mvn scm:update
@if errorlevel 1 goto errormark

call mvn -DskipTests=true
@if errorlevel 1 goto errormark

title %CurDir%

@goto endmark
:errormark
	echo   
    pause
:endmark
@ENDLOCAL
