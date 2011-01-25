@echo off
rem @version $Revision$ ($Author$)  $Date$
rem helper to run maven2
rem

for /f "tokens=*" %%I in ('CD') do @set CurDir=%%~nI
title *%CurDir% - mvn build
call mvn install -P pyx %*
title %CurDir%


