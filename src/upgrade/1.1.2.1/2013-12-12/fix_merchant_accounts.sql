/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             Delete records with null merchant_terminal_id from _admin_.admin_pmc_merchant_account_index
***
***     ======================================================================================================================
**/

BEGIN TRANSACTION;
        
        
        -- Merchant_terminal_id is NULL for all corresponding pmc records 
        
        DELETE  FROM _admin_.admin_pmc_merchant_account_index 
        WHERE   merchant_terminal_id IS NULL; 
        
COMMIT;
        

