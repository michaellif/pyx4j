/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     Removal of pangroup merchant accounts
***
***     ===========================================================================================================
**/     

BEGIN TRANSACTION;

        DELETE FROM _admin_.admin_pmc_merchant_account_index WHERE pmc = 2;    
        
        UPDATE pangroup_old.aggregated_transfer SET merchant_account = NULL WHERE merchant_account IS NOT NULL;
        
        DELETE FROM pangroup_old.building_merchant_account;
        
        DELETE FROM pangroup_old.merchant_account;   
        
COMMIT;              
