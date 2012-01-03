#!/bin/sh
# @version $Revision$ ($Author$) $Date$
#
# helper to run maven3 on Mac and Linux
#
# requirements: maven3, javac, svn

mvn install -P pyx,gwtc,draft
if [ "$?" != "0" ]; then
    echo   
    echo Error in vista build
    exit 1
fi
