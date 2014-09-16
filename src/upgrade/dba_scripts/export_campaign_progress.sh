#!/bin/bash
#	@version $Revision$ ($Author$) $Date$
# 	Export promotion campaign data every day at 7:08

# Script is intended to be executed as user postgres, since this user has an entry in ident file,
# thus not needing to login to database with a password

if [ $(whoami) != 'postgres' ]
then 
	echo "This script is intended to be executed under postgres' credentials. Exiting...";
	exit 2;
fi

DATE=$(date +%Y%m%d);
TARGET_DIR="/tmp/";

# Export all building data

FILENAME="$TARGET_DIR"campaign_"$DATE".csv;

psql -q -t -d vista -c "COPY (SELECT * FROM _dba_.campaign_progress) TO '"$FILENAME"' CSV HEADER ";

exit 0;
