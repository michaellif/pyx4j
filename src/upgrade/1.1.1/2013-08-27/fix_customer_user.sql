/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             fix for customer_user entries with NULL email
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.fix_customer_user(v_schema_name TEXT) RETURNS VOID AS
$$
BEGIN
        EXECUTE 'UPDATE '||v_schema_name||'.customer '
                ||'SET  user_id = NULL '
                ||'WHERE user_id IN (SELECT id FROM '||v_schema_name||'.customer_user WHERE email IS NULL) ';
                
        
        EXECUTE 'DELETE FROM  '||v_schema_name||'.customer_user_credential '
                ||'WHERE usr IN (SELECT id FROM '||v_schema_name||'.customer_user WHERE email IS NULL) ';
              
        EXECUTE 'DELETE FROM  '||v_schema_name||'.customer_user '
                ||'WHERE email IS NULL';
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

        SELECT  pmc,_dba_.fix_customer_user(pmc)
        FROM    _dba_.count_rows_all_pmc('customer_user',ARRAY['email IS NULL']) 
        WHERE   row_count > 0;
        
COMMIT;

DROP FUNCTION _dba_.fix_customer_user(text);



