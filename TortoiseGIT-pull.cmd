@echo off
rem @version $Revision$ ($Author$)  $Date$
rem helper to run GIT update in UI
rem

start TortoiseGitProc.exe /command:pull /closeonend:2 /path:"..\pyx4j"
start TortoiseGitProc.exe /command:pull /closeonend:2 /path:"%CD%"
