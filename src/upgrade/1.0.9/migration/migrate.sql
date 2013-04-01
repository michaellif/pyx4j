/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             1.0.9 migration
***
***     ======================================================================================================================
**/

-- public schema migration
\i migrate_public_schema.sql;

-- _admin_ schema migration 
\i migrate_admin_schema.sql;


-- create migration function
\i create_migrate_functions.sql;


SET client_min_messages = 'error';

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   a.status != 'Created';
COMMIT;


SET client_min_messages = 'notice';

DROP FUNCTION _dba_.migrate_pmc_109(text);

