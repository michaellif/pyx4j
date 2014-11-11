@echo off
echo.

REM ------------------------------------------------------
REM Define constants for requests, concurrency and timeout
SET REQUESTS=10
SET CONCURRENCY=2
SET TIMEOUT=60
REM ------------------------------------------------------

echo Starting NET TESTS...

echo.
IF "%1"=="" GOTO finish
echo File: %1

IF "%2"=="" (SET folder="") ELSE (SET folder=%2)

echo.
echo Destiny folder: %folder%
echo.
echo executing 'abs -n %REQUESTS% -c %CONCURRENCY% -s %TIMEOUT%'

echo launching test for: VIOLET - Old plumbing https
REM abs.exe -n %REQUESTS% -c %CONCURRENCY% -s %TIMEOUT% https://static-88.birchwoodsoftwaregroup.com/static/net-test/%1 > "%folder%\violet-oldPlumbing-https.log"

echo launching test for: VIOLET - Old plumbing http
REM abs.exe -n %REQUESTS% -c %CONCURRENCY% -s %TIMEOUT% http://static-88.birchwoodsoftwaregroup.com/static/net-test/%1 > "%folder%\violet-oldPlumbing-http.log"

echo launching test for: VIOLET - Old plumbing bypass tomcat
REM abs.exe -n %REQUESTS% -c %CONCURRENCY% -s %TIMEOUT% https://env-88.birchwoodsoftwaregroup.com/net-test/net-test/%1 > "%folder%\violet-oldPlumbing-bypassTomcat.log"

echo launching test for: RED - HTTPS Complete New Plumbing
REM abs.exe -n %REQUESTS% -c %CONCURRENCY% -s %TIMEOUT% https://static-11.devpv.com/static/net-test/%1 > "%folder%\red-newPlumbing-https.log"

echo launching test for: RED - HTTP Complete New Plumbing
REM abs.exe -n %REQUESTS% -c %CONCURRENCY% -s %TIMEOUT% http://static-11.devpv.com/static/net-test/%1 > "%folder%\red-newPlumbing-http.log"

echo launching test for: RED - HTTPS Complete New Plumbing WITH FILTER
REM abs.exe -n %REQUESTS% -c %CONCURRENCY% -s %TIMEOUT% https://vista-crm-11.devpv.com/net-test/%1 > "%folder%\red-newPlumbing-https_filter.log"

echo launching test for: BLUE - Apache server on dev (no tomcat)
abs.exe -n %REQUESTS% -c %CONCURRENCY% -s %TIMEOUT% http://dev22-net-test.pyx4j.com/files-net-test/%1 > "%folder%\blue-apache-noTomcat.log"

echo launching test for: GREEN - PfSense only HTTPS
abs.exe -n %REQUESTS% -c %CONCURRENCY% -s %TIMEOUT% https://net-test.devpv.com/net-test/%1 > "%folder%\green-PfSense-only-https.log"

echo launching test for: GREEN - PfSense only HTTP
abs.exe -n %REQUESTS% -c %CONCURRENCY% -s %TIMEOUT% http://net-test.devpv.com/net-test/%1 > "%folder%\green-PfSense-only-http.log"

echo launching test for: ORANGE - Just apache httpd and Tomcat (new deployment)
abs.exe -n %REQUESTS% -c %CONCURRENCY% -s %TIMEOUT% http://dev22-net-test.pyx4j.com/tomcat-net-test/%1 > "%folder%\orange-apache-and-tomcat-new-deployment.log"
exit /b

:finish
echo test file is required for testing. Execute: "%0 fileToDownload [destinyFolderFoResult]"
exit /b





