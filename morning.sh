#!/bin/sh
# @version $Revision$ ($Author$) $Date$
#
# helper to run maven3 on Mac and Linux
#
#   - SVN Updates Pyx and Vista
#   - Runs maven buid without tests
#
# requirements: maven3, javac, svn

mvn scm:update --file ../pyx4j/pom.xml
if [[ ! "$?" = "0" ]]; then
    echo   
    echo Error in PYX SVN Update
    exit 1
fi

mvn scm:update
if [[ ! "$?" = "0" ]]; then
    echo   
    echo Error in Vista SVN Update
    exit 1
fi

mvn -P pyx -DskipTests=true
if [[ ! "$?" = "0" ]]; then
    echo   
    echo Error in build
    exit 1
fi
