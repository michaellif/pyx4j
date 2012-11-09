/**
***     =================================================================================
***             
***             @version $Revision$ ($Author$) $Date$
***             Migration test for production data
***             To be run by qa_db_reset.sh on env 55
***
***     =================================================================================
**/

\c vista_prod

ALTER USER vista NOLOGIN;

SELECT  pg_terminate_backend(procpid) 
FROM    pg_stat_activity 
WHERE   datname = 'vista_qa';


DROP DATABASE IF EXISTS vista_qa;

SELECT  pg_terminate_backend(procpid) 
FROM    pg_stat_activity 
WHERE   datname = 'vista_prod'
AND     procpid != pg_backend_pid();

CREATE DATABASE vista_qa TEMPLATE vista_prod OWNER vista TABLESPACE vista33;

\c vista_qa

UPDATE  _admin_.pad_file
SET     status = 'SendError'
WHERE   status = 'Creating';

SET check_function_bodies = 'off';
 
 CREATE OR REPLACE FUNCTION _dba_.forge_user_credentials() RETURNS VOID AS
 $$
        
        -- SET search_path = $1;        
                
        UPDATE crm_user 
        SET     email = 'm001@pyx4j.com' 
        WHERE   email = 'support@propertyvista.com';
                
        UPDATE crm_user_credential 
        SET     credential = 'xbPRRQQmljSsXpqbARTClsXJ/F2MF0wO' 
        WHERE   usr = (SELECT id FROM crm_user WHERE email = 'm001@pyx4j.com');
        
        UPDATE  merchant_account
        SET     account_number = regexp_replace(account_number,'[0-9]','X','g')||id::text,
                merchant_terminal_id = regexp_replace(substring(merchant_terminal_id,1,6),'[A-Z0-9]','X','g')||id::text ;
                
        
                        
 $$
 LANGUAGE SQL VOLATILE;
 
 /*
 SELECT _dba_.forge_user_credentials(namespace) FROM _admin_.admin_pmc
 WHERE status IN ('Active','Suspended');
 
 */

 CREATE OR REPLACE FUNCTION _dba_.loop_pmc_update() RETURNS VOID AS
 $$
 DECLARE 
        v_schema_name           VARCHAR(64);
        v_void                  CHAR(1);
 BEGIN
        FOR v_schema_name IN 
        SELECT namespace FROM _admin_.admin_pmc
        WHERE status IN ('Active','Suspended')
        LOOP
                EXECUTE 'SET search_path  TO '||v_schema_name;
                
                SELECT * INTO v_void FROM _dba_.forge_user_credentials();
        END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE; 

SET check_function_bodies = 'on';

SELECT _dba_.loop_pmc_update();

 \i /home/akinareevsky/import/dba_functions.sql
-- \i /home/akinareevsky/import/dev_clean.sql
 \i /home/akinareevsky/import/rename_discriminator_cols.sql
 \i /home/akinareevsky/import/migrate.sql

ALTER USER vista LOGIN;

