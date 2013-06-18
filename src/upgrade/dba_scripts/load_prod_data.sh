#!/bin/bash
# Script to load all pmc from production backup
# Requires entry in .pgpass file for user vista

SQLDIR='/home/akinareevski/tmp/import';
DBNAME='vista_branch';

psql -U vista -h localhost -d $DBNAME -c 'ALTER SCHEMA _admin_ RENAME TO new_admin';
psql -U vista -h localhost -d $DBNAME -c 'ALTER SCHEMA _expiring_ RENAME TO new_expiring';
psql -U vista -h localhost -d $DBNAME -c 'ALTER SCHEMA public RENAME TO new_public';

cd $SQLDIR;

for i in $(ls | grep 'vista_' | grep '.sql')
do
        psql -U vista -h localhost -d $DBNAME -f $i -w ;
done;


exit 0;
