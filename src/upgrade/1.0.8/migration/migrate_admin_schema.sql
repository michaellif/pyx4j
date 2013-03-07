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
        ***             DROP TABLE SECTION
        ***
        ***     ======================================================================================================
        **/
        
        

        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP CONSTRAINTS SECTION
        ***
        ***     ======================================================================================================
        **/
        
        -- Check constraints
        ALTER TABLE dev_card_service_simulation_transaction DROP CONSTRAINT dev_card_service_simulation_transaction_transaction_type_e_ck;
        ALTER TABLE scheduler_trigger DROP CONSTRAINT scheduler_trigger_trigger_type_e_ck;

       
        
        /**
        ***     =======================================================================================================
        ***
        ***             NEW AND ALTERED TABLES 
        ***
        ***     =======================================================================================================
        **/
        
        -- scheduler_execution_report
       
        ALTER TABLE scheduler_execution_report  DROP COLUMN amount_failed,
                                                DROP COLUMN amount_processed;
       
       
        
        /**
        ***     ========================================================================================================
        ***
        ***             CREATE CONSTRAINTS
        ***
        ***     ========================================================================================================
        **/
               
        -- Check constraints
        ALTER TABLE dev_card_service_simulation_transaction ADD CONSTRAINT dev_card_service_simulation_transaction_tp_e_ck 
                CHECK ((tp) IN ('completion', 'preAuthorization', 'preAuthorizationReversal', 'returnVoid', 'sale'));
 
        ALTER TABLE scheduler_trigger ADD CONSTRAINT scheduler_trigger_trigger_type_e_ck 
                CHECK ((trigger_type) IN ('billing', 'cleanup', 'equifaxRetention', 'initializeFutureBillingCycles', 'leaseActivation', 
                'leaseCompletion', 'leaseRenewal', 'paymentsBmoReceive', 'paymentsIssue', 'paymentsPadReceiveAcknowledgment', 
                'paymentsPadReceiveReconciliation', 'paymentsPadSend', 'paymentsScheduledCreditCards', 'paymentsScheduledEcheck', 
                'paymentsTenantSure', 'tenantSureCancellation', 'tenantSureHQUpdate', 'tenantSureReports', 'tenantSureTransactionReports', 
                'test', 'updateArrears', 'updatePaymentsSummary', 'vistaBusinessReport', 'yardiBatchProcess', 'yardiImportProcess'));

              

        /**
        ***     ============================================================================================================
        ***     
        ***             INDEXES
        ***
        ***     ============================================================================================================
        **/
        
       
        
        /**
        ***     ============================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     ============================================================================================================
        **/
        
       
     
        
        

COMMIT;

SET client_min_messages = 'notice';
