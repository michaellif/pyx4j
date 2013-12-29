@echo off
rem @version $Revision$ ($Author$)  $Date$
rem
@set CLASSPATH=%JAVA_HOME%\lib\jconsole.jar;%JAVA_HOME%\lib\tools.jar

@set MAVEN2_REPO=%HOMEDRIVE%\%HOMEPATH%\.m2\repository
@set CLASSPATH=%CLASSPATH%;%MAVEN2_REPO%\log4j\log4j\1.2.16\log4j-1.2.16.jar
rem @set CLASSPATH=%CLASSPATH%;%MAVEN2_REPO%\org\apache\tomcat\tomcat-catalina-jmx-remote\7.0.27\tomcat-catalina-jmx-remote-7.0.27.jar

@start %JAVA_HOME%\bin\jconsole "-J-Djava.class.path=%CLASSPATH%"  %*

