/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             1.1.4 migration
***
***     ======================================================================================================================
**/

-- public schema migration
\i migrate_public_schema.sql;

-- _admin_ schema migration 
\i migrate_admin_schema.sql;


-- _expiring_ schema migration 
--\i migrate_expiring_schema.sql;

-- Import new roles and behaviours
\i insert_tmp_roles.sql

-- Split simple address functioon
\i split_simple_address.sql

-- Phone/fax numbers update
\i update_phone_numbers.sql

-- create migration function
\i create_migrate_functions.sql;



SET client_min_messages = 'error';


BEGIN TRANSACTION;
        SELECT  'a' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^a';
COMMIT;

BEGIN TRANSACTION;

        SELECT  'b' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^b';

COMMIT;


BEGIN TRANSACTION;

        SELECT  'c' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^c';

COMMIT;


BEGIN TRANSACTION;
        SELECT  'd' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^d';
COMMIT;


BEGIN TRANSACTION;
        SELECT  'e' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^e';
COMMIT;


BEGIN TRANSACTION;
        SELECT  'f' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^f';
COMMIT;

BEGIN TRANSACTION;
        SELECT  'g' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^g';
COMMIT;

BEGIN TRANSACTION;
        SELECT  'h' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^h';
COMMIT;

BEGIN TRANSACTION;
        SELECT  'i' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^i';
COMMIT;

BEGIN TRANSACTION;
        SELECT  'j' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^j';
COMMIT;

BEGIN TRANSACTION;
        SELECT  'k' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^k';
COMMIT;

BEGIN TRANSACTION;
        SELECT  'l' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^l';
COMMIT;

BEGIN TRANSACTION;
        SELECT  'm' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^m';
COMMIT;

BEGIN TRANSACTION;
        SELECT  'n' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^n';
COMMIT;

BEGIN TRANSACTION;
        SELECT  'o' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^o';
COMMIT;

BEGIN TRANSACTION;
        SELECT  'p' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^p';
COMMIT;

BEGIN TRANSACTION;
        SELECT  'q' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^q';
COMMIT;

BEGIN TRANSACTION;
        SELECT  'r' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^r';
COMMIT;

BEGIN TRANSACTION;
        SELECT  's' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^s';
COMMIT;

BEGIN TRANSACTION;
        SELECT  't' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^t';
COMMIT;

BEGIN TRANSACTION;
        SELECT  'u' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^u';
COMMIT;

BEGIN TRANSACTION;
        SELECT  'v' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^v';
COMMIT;

BEGIN TRANSACTION;
        SELECT  'w' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^w';
COMMIT;

BEGIN TRANSACTION;
        SELECT  'x' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^x';
COMMIT;

BEGIN TRANSACTION;
        SELECT  'y' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^y';
COMMIT;

BEGIN TRANSACTION;
        SELECT  'z' AS letter,
                COUNT(namespace) AS to_process,
                COUNT(_dba_.migrate_pmc_114(namespace)) AS processed
        FROM    _admin_.admin_pmc a
        JOIN    pg_namespace n ON (a.namespace = n.nspname)
        WHERE   status != 'Created'
        AND     schema_version != '1.1.4'
        AND     namespace ~ '^z';
COMMIT;


DROP FUNCTION _dba_.migrate_pmc_114(text);
DROP TABLE _dba_.tmp_roles;


