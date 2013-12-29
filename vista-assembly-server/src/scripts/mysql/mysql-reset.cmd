@echo off
rem @version $Revision$ ($Author$)  $Date$
title mysql
mysql --user=vista --password=vista < mysql-reset.sql
@if errorlevel 1 goto errormark

@goto endmark
:errormark
	echo   
    pause
:endmark
@ENDLOCAL

