/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Function to obfuscate prod data
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.obfuscate_prod_data() RETURNS VOID AS
$$
DECLARE
        v_schema_name   VARCHAR(64);
BEGIN
        
        UPDATE  _admin_.pad_batch
        SET     account_number = LPAD(id::text,12,'0');
        
        UPDATE  _admin_.pad_debit_record
        SET     account_number = LPAD(id::text,12,'0');
        
        FOR v_schema_name IN 
        SELECT  namespace 
        FROM    _admin_.admin_pmc
        WHERE   status != 'Created'
        LOOP
        
                EXECUTE 'UPDATE '||v_schema_name||'.payment_payment_details '
                        ||'SET account_no_number = regexp_replace(account_no_obfuscated_number,''X'',''0'',''g'') ';
                        
                EXECUTE 'UPDATE '||v_schema_name||'.merchant_account '
                        ||'SET account_number = LPAD(id::text,12,''0'') ';
                        
        END LOOP;
                
                        
END;
$$
LANGUAGE plpgsql VOLATILE;

