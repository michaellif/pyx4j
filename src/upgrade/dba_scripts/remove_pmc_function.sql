/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             Function to remove PMC completely. Dangerous if used improperly
***
***     =====================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.remove_pmc(v_namespace TEXT, v_rm_onb_usr BOOLEAN DEFAULT TRUE) RETURNS VOID AS
$$
DECLARE
        v_pmc_id                BIGINT;
        
BEGIN
        
        SELECT  id 
        FROM    _admin_.admin_pmc 
        INTO    v_pmc_id 
        WHERE   namespace = v_namespace;
        
               
        IF (v_rm_onb_usr)
        THEN
                
                DELETE  FROM _admin_.onboarding_user 
                WHERE   pmc = v_pmc_id;
                
        ELSE
                UPDATE  _admin_.onboarding_user_credential 
                SET     pmc = NULL
                WHERE   pmc = v_pmc_id;
        END IF;
        
        
        
        
        DELETE FROM _admin_.admin_pmc_account_numbers WHERE pmc = v_pmc_id;
        DELETE FROM _admin_.admin_pmc_dns_name WHERE pmc = v_pmc_id;
        DELETE FROM _admin_.global_crm_user_index WHERE   pmc = v_pmc_id;
       --  DELETE FROM _admin_.onboarding_user_credential WHERE pmc = v_pmc_id ;
        DELETE FROM _admin_.scheduler_trigger_pmc WHERE pmc = v_pmc_id ;
        DELETE FROM _admin_.scheduler_run_data WHERE pmc = v_pmc_id;
        DELETE FROM _admin_.audit_record WHERE namespace = v_namespace;
        DELETE FROM _admin_.admin_pmc_equifax_info WHERE pmc = v_pmc_id;
        DELETE FROM _admin_.pad_reconciliation_debit_record
        WHERE reconciliation_summary IN (SELECT id FROM _admin_.pad_reconciliation_summary 
                                        WHERE merchant_account IN 
                                        (SELECT id FROM _admin_.admin_pmc_merchant_account_index WHERE pmc = v_pmc_id));
        DELETE FROM _admin_.pad_reconciliation_summary 
        WHERE merchant_account IN 
                (SELECT id FROM _admin_.admin_pmc_merchant_account_index WHERE pmc = v_pmc_id);
        DELETE FROM _admin_.admin_pmc_merchant_account_index WHERE pmc = v_pmc_id;
        DELETE FROM _admin_.admin_pmc_yardi_credential WHERE pmc = v_pmc_id;
        DELETE FROM _admin_.fee_pmc_equifax_fee WHERE pmc = v_pmc_id;
        
        DELETE FROM _admin_.admin_pmc_vista_features 
        WHERE id = (SELECT features FROM _admin_.admin_pmc WHERE id = v_pmc_id);
        
        DELETE FROM _admin_.admin_pmc WHERE id = v_pmc_id;
        
        -- EXECUTE 'DROP SCHEMA '||v_namespace||' CASCADE';
END;
$$
LANGUAGE plpgsql VOLATILE;

