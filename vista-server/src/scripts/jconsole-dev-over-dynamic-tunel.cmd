@echo off
rem @version $Revision$ ($Author$)  $Date$
call %~dp0jconsole.cmd "-J-DsocksProxyHost=localhost" "-J-DsocksProxyPort=9999" localhost:9002

