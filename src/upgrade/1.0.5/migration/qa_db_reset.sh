#!/bin/bash
# @version $Revision$ ($Author$) $Date$
# Shell wrapper for qa_db_update.sql migration script

su -c 'psql < /home/akinareevsky/import/qa_db_update.sql' postgres;

