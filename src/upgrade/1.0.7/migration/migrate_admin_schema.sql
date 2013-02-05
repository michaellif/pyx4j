/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             _admin_ schema changes
***
***     =====================================================================================================================
**/

SET client_min_messages = 'error';

BEGIN TRANSACTION;

SET search_path = '_admin_';

        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP CONSTRAINTS SECTION
        ***
        ***     ======================================================================================================
        **/
        
        -- Check constraints to drop
        
        ALTER TABLE scheduler_trigger DROP CONSTRAINT scheduler_trigger_trigger_type_e_ck;
        
        -- Check constraint to create
        
        ALTER TABLE scheduler_trigger ADD CONSTRAINT scheduler_trigger_trigger_type_e_ck 
                CHECK ((trigger_type) IN ('billing', 'cleanup', 'equifaxRetention', 'initializeFutureBillingCycles', 'leaseActivation', 
                'leaseCompletion', 'leaseRenewal', 'paymentsBmoRecive', 'paymentsIssue', 'paymentsPadReciveAcknowledgment', 
                'paymentsPadReciveReconciliation', 'paymentsPadSend', 'paymentsScheduledCreditCards', 'paymentsScheduledEcheck', 
                'paymentsTenantSure', 'tenantSureCancellation', 'tenantSureHQUpdate', 'tenantSureReports', 'test', 'updateArrears', 
                'updatePaymentsSummary', 'yardiBatchProcess', 'yardiImportProcess'));
                
        ALTER TABLE tenant_sure_hqupdate_record ADD CONSTRAINT tenant_sure_hqupdate_record_status_e_ck CHECK (status = 'Cancel');



COMMIT;

SET client_min_messages = 'notice';
