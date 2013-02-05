/**
*** =================================================================================
*** @version $Revision$ ($Author$) $Date$
***
***     Migration of PMC schema's to version 1.0.7
***
*** =================================================================================
**/


CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_107(v_schema_name TEXT) RETURNS VOID AS
$$
BEGIN
        EXECUTE 'SET search_path = '||v_schema_name;
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP CONSTRAINTS SECTION
        ***
        ***     ======================================================================================================
        **/
        
         -- Check constraints to drop
        ALTER TABLE insurance_tenant_sure_transaction DROP CONSTRAINT insurance_tenant_sure_transaction_status_e_ck;
        
        
        
        
        -- Check constraint to create
        ALTER TABLE insurance_tenant_sure_transaction ADD CONSTRAINT insurance_tenant_sure_transaction_status_e_ck 
                CHECK ((status) IN ('AuthorizationRejected', 'AuthorizationReversal', 'Authorized', 'AuthorizedPaymentRejectedRetry', 'Cleared', 'Draft', 'PaymentError', 'PaymentRejected'));
        

END;
$$
LANGUAGE plpgsql VOLATILE;

