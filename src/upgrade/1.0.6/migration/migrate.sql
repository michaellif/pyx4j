/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Mster script for 1.0.6 migration
***
***     ======================================================================================================================
**/

-- public schema migration
\i migrate_public_schema.sql;

-- _admin_ schema migration 
\i migrate_admin_schema.sql;

-- _expiring_ schema



       
