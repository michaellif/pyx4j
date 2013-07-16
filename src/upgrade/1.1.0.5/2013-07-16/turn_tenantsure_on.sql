/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     Set tenant_sure_integration for all pmc that have it off
***
***     ===========================================================================================================
**/                                                     

BEGIN TRANSACTION;

        UPDATE  _admin_.admin_pmc_vista_features
        SET     tenant_sure_integration = TRUE
        WHERE   NOT tenant_sure_integration;
        
COMMIT;
