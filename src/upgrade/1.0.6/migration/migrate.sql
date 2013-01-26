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

-- create migration function
\i create_migrate_functions.sql;

/**     ======================================================================
***
***             To keep transactions nice and short migration is performed
***             in 26 easy steps...
***
***     ======================================================================
**/

SET client_min_messages = 'error';

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'a';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'b';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'c';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'd';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'e';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'f';
COMMIT;
        
BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'g';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'h';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'i';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'j';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'k';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'l';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'm';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'n';
COMMIT;
       
BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'o';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'p';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'q';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'r';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 's';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 't';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'u';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'v';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'w';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'x';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'y';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace,_dba_.migrate_pmc_106(namespace)
        FROM    _admin_.admin_pmc
        WHERE   schema_version != '1.0.6'
        AND     status != 'Created'
        AND     substring(namespace,1,1) = 'z';
COMMIT;

SET client_min_messages = 'notice';
