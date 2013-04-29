/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             1.0.9 migration
***
***     ======================================================================================================================
**/

-- pre-migration delete
\i pre_migration_delete.sql

-- pre-migration update
\i pre_migration_update.sql

-- public schema migration
\i migrate_public_schema.sql;

-- _admin_ schema migration 
\i migrate_admin_schema.sql;

-- _expiring_ schema migration 
--\i migrate_expiring_schema.sql;

-- create migration function
\i create_migrate_functions.sql;


SET client_min_messages = 'error';

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^a';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^b';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^c';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^d';
COMMIT;


BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^e';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^f';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^g';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^h';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^i';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^j';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^k';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^l';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^m';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^n';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^o';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^p';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^q';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^r';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^s';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^t';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^u';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^v';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^w';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^x';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^y';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_109(namespace)
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.0.9'
        AND     namespace ~ '^z';
COMMIT;

SET client_min_messages = 'notice';

DROP FUNCTION _dba_.migrate_pmc_109(text);

