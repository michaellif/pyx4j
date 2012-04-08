@echo off
rem @version $Revision$ ($Author$)  $Date$
title psql
set PGPASSWORD=vista
psql --username vista
@if errorlevel 1 goto errormark

@goto endmark
:errormark
	echo   
    pause
:endmark
@ENDLOCAL



