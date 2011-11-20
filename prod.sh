#!/bin/sh
# @version $Revision$ ($Author$) $Date$
#
# helper to run maven3 on Mac and Linux
#
#   - SVN Updates Pyx and Vista
#   - Runs maven buid without tests
#
# requirements: maven3, javac, svn

mvn --file ../pyx4j/pom.xml clean --fail-never
if [ "$?" != "0" ]; then
    echo   
    echo Error in PYX Clean
    exit 1
fi

mvn scm:update --file ../pyx4j/pom.xml
if [ "$?" != "0" ]; then
    echo   
    echo Error in PYX SVN Update
    exit 1
fi

mvn --file ../pyx4j/pom.xml -DskipTests=true
if [ "$?" != "0" ]; then
    echo   
    echo Error in PYX build
    exit 1
fi

mvn clean --fail-never
if [ "$?" != "0" ]; then
    echo   
    echo Error in Vista SVN Clean
    exit 1
fi

mvn scm:update
if [ "$?" != "0" ]; then
    echo   
    echo Error in Vista SVN Update
    exit 1
fi

mvn -DskipTests=true -P gwtc,full,build-full -P \!draft -P \!developer-env -Dbamboo.buildNumber=ManualBuild
if [ "$?" != "0" ]; then
    echo   
    echo Error in vista build
    exit 1
fi
