#!/bin/sh
# @version $Revision$ ($Author$) $Date$
#
# helper to run maven3 on Mac and Linux  demo server
#

mvn -DskipTests=true -P deploy,deploy-target-d44
if [ "$?" != "0" ]; then
    echo   
    echo Error in build
    exit 1
fi
