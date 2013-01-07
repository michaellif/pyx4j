#!/bin/bash
# Script to load all pmc from production backup
# Requires entry in .pgpass file for user vista

SQLDIR='/home/akinareevski/tmp/import';
DBNAME='vista_trunk';


cd $SQLDIR;

for i in $(ls | grep 'vista_' | grep '.sql')
do
        psql -U vista -h localhost -d $DBNAME -f $i -w ;
done;


exit 0;
