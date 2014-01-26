/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Update merchant accounts
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.update_merchant_accounts() RETURNS VOID AS
$$
DECLARE 
        v_schema_name VARCHAR(64);
BEGIN
        FOR v_schema_name IN
        SELECT  DISTINCT p.namespace
        FROM    _admin_.admin_pmc p
        JOIN    _admin_.admin_pmc_merchant_account_index m ON (p.id = m.pmc)
        LOOP
                EXECUTE 'UPDATE '||v_schema_name||'.merchant_account '
                        ||'SET  merchant_terminal_id_convenience_fee = regexp_replace(merchant_terminal_id,''PRV'',''PRC''),'
                        ||'     setup_accepted_credit_card_convenience_fee = TRUE '
                        ||'WHERE   NOT invalid '
                        ||'AND     status = ''Active'' '
                        ||'AND     merchant_terminal_id ~ ''^PRV'' ';          
        END LOOP;
END;
$$
LANGUAGE plpgsql VOLATILE;

BEGIN TRANSACTION;
       
       SELECT * FROM _dba_.update_merchant_accounts();

COMMIT;


DROP FUNCTION _dba_.update_merchant_accounts();
