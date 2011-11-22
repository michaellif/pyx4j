#!/bin/sh
# @version $Revision$ ($Author$) $Date$
#
# helper to run maven3 on Mac and Linux  demo server
#
SCRIPTS_DIR=`dirname "${0}"`

${SCRIPTS_DIR}/dd11.sh
if [ "$?" != "0" ]; then
    echo   
    echo Error in Demo11 build
    exit 1
fi

${SCRIPTS_DIR}/dd22.sh
if [ "$?" != "0" ]; then
    echo   
    echo Error in Demo22 build
    exit 1
fi

${SCRIPTS_DIR}/dd33.sh
if [ "$?" != "0" ]; then
    echo   
    echo Error in Demo33 build
    exit 1
fi

${SCRIPTS_DIR}/dd44.sh
if [ "$?" != "0" ]; then
    echo   
    echo Error in Demo44 build
    exit 1
fi