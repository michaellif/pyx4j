@echo off
rem @version $Revision$ ($Author$)  $Date$
title psql
set PGPASSWORD=root

psql --username postgres --file ../../dba_scripts/swap_prod_dev.sql
@if errorlevel 1 goto errormark

psql --username postgres -d "vista" --file ../../dba_scripts/dba_functions.sql
@if errorlevel 1 goto errormark

psql --username postgres -d "vista" --file migrate.sql
@if errorlevel 1 goto errormark



@goto endmark
:errormark
	echo   
    pause
:endmark
@ENDLOCAL



