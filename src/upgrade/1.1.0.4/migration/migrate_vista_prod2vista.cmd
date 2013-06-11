@echo off
rem @version $Revision: 12740 $ ($Author: akinareevski $)  $Date: 2013-05-17 23:39:57 -0400 (Fri, 17 May 2013) $
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



