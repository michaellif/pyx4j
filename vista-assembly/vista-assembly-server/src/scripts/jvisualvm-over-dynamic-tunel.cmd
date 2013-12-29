@echo off
rem @version $Revision$ ($Author$)  $Date$
start %JAVA_HOME%\bin\jvisualvm "-J-Dnetbeans.system_socks_proxy=localhost:9999" "-J-Djava.net.useSystemProxies=true"