/**
***     ===========================================================================================================
***
***     Change merchant_account.charge_description to VARCHAR(30)
***
***     ===========================================================================================================
**/                                                     


CREATE OR REPLACE FUNCTION _dba_.change_charge_description() RETURNS VOID AS
$$
DECLARE 
    
    v_schema_name   VARCHAR(64);
    
BEGIN

    FOR  v_schema_name IN 
    SELECT  namespace 
    FROM    _admin_.admin_pmc
    LOOP
        
        EXECUTE 'ALTER TABLE '||v_schema_name||'.merchant_account '
                ||'ALTER COLUMN charge_description TYPE VARCHAR(30) ';
    
    END LOOP;


END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;

    UPDATE  nepm.merchant_account 
    SET charge_description = '25 Mabelle Ave/177 Redpath Av'
    WHERE   id = 198;

COMMIT;

BEGIN TRANSACTION;

    SELECT * FROM _dba_.change_charge_description();
    
COMMIT;

DROP FUNCTION _dba_.change_charge_description();
