/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     One time function to insert merchant_terminal_id_convenience_fee
***     into _admin_.admin_pmc_merchant_account_index 
***
***     ===========================================================================================================
**/                            

CREATE OR REPLACE FUNCTION _dba_.tmp_mid() RETURNS VOID AS 
$$
DECLARE 
    
    v_schema_name               VARCHAR(64);
    v_pmc_id                    BIGINT;
BEGIN

    FOR v_schema_name, v_pmc_id IN
    SELECT  a.namespace,a.id
    FROM    _admin_.admin_pmc a
    JOIN    pg_namespace n ON (a.namespace = n.nspname)
    LOOP
    
        EXECUTE 'INSERT INTO _admin_.admin_pmc_merchant_account_index (id,'
                ||'merchant_terminal_id,pmc,merchant_account_key) '
                ||'(SELECT  nextval(''public.admin_pmc_merchant_account_index_seq'') AS id,'
                ||'         merchant_terminal_id_convenience_fee,'||v_pmc_id||',id '
                ||'FROM '||v_schema_name||'.merchant_account '
                ||'WHERE    merchant_terminal_id_convenience_fee IS NOT NULL '
                ||'AND  merchant_terminal_id_convenience_fee NOT IN '
                ||'     (SELECT DISTINCT COALESCE(merchant_terminal_id,''x'' FROM _admin_.admin_pmc_merchant_account_index))';
    END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

    SELECT * FROM _dba_.tmp_mid() ;

COMMIT;

DROP FUNCTION _dba_.tmp_mid();
    
    
