@echo off
rem @version $Revision$ ($Author$)  $Date$
title psql
set PGPASSWORD=root
psql --username postgres --file postgre-reset.sql
@if errorlevel 1 goto errormark

@goto endmark
:errormark
	echo   
    pause
:endmark
@ENDLOCAL



