@echo off
rem @version $Revision$ ($Author$)  $Date$
title psql
set PGPASSWORD=tst_entity
psql --username tst_entity
@if errorlevel 1 goto errormark

@goto endmark
:errormark
	echo   
    pause
:endmark
@ENDLOCAL



