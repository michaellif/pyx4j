@echo off
rem @version $Revision$ ($Author$)  $Date$
rem helper to run maven2
rem
set MAVEN_OPTS=-XX:MaxPermSize=356m -Xms1024m -Xmx1524m -XX:ReservedCodeCacheSize=128m
set JAVA_HOME=D:\jdk1.7.0
for /f "tokens=*" %%I in ('CD') do @set CurDir=%%~nI
title *%CurDir% - mvn with pyx and GWT Compile
call mvn install -P pyx,gwtc,draft %*
@if errorlevel 1 goto errormark
title %CurDir%

@goto endmark
:errormark
	echo   
    pause
:endmark
@ENDLOCAL
