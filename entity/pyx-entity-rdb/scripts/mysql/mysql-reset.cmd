@echo off
rem @version $Revision$ ($Author$)  $Date$
title mysql
mysql --user=tst_entity --password=tst_entity < mysql-reset.sql
@if errorlevel 1 goto errormark

@goto endmark
:errormark
	echo   
    pause
:endmark
@ENDLOCAL


