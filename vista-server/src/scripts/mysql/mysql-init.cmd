@echo off
rem @version $Revision$ ($Author$)  $Date$
title mysql
mysql --user=root --password=root  --force < mysql-init.sql
@if errorlevel 1 goto errormark

@goto endmark
:errormark
	echo   
    pause
:endmark
@ENDLOCAL

