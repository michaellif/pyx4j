#!/bin/sh
# @version $Revision$ ($Author$) $Date$
#
# helper to run maven3 on Mac and Linux
#

mvn -P pyx4j-mysql-tests
if [ "$?" != "0" ]; then
    echo   
    echo Error in build
    exit 1
fi
