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
        ALTER TABLE audit_record DROP CONSTRAINT audit_record_app_e_ck;
        ALTER TABLE dev_card_service_simulation_transaction DROP CONSTRAINT dev_card_service_simulation_transaction_transaction_type_e_ck;
        ALTER TABLE operations_alert DROP CONSTRAINT operations_alert_app_e_ck;
        ALTER TABLE scheduler_trigger DROP CONSTRAINT scheduler_trigger_trigger_type_e_ck;

       
        
        /**
        ***     =======================================================================================================
        ***
        ***             NEW AND ALTERED TABLES 
        ***
        ***     =======================================================================================================
        **/
        
        -- dev_card_service_simulation_transaction
        
        ALTER TABLE dev_card_service_simulation_transaction RENAME COLUMN transaction_type TO tp;
        
        
        -- scheduler_execution_report
       
        ALTER TABLE scheduler_execution_report  DROP COLUMN amount_failed,
                                                DROP COLUMN amount_processed;
       
       
        /**
        ***     ============================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     ============================================================================================================
        **/
       
       
        UPDATE  scheduler_trigger
        SET     trigger_type = 'paymentsBmoReceive'
        WHERE   trigger_type = 'paymentsBmoRecive';
        
        UPDATE  scheduler_trigger
        SET     trigger_type = 'paymentsPadReceiveAcknowledgment'
        WHERE   trigger_type = 'paymentsPadReciveAcknowledgment';
        
        UPDATE  scheduler_trigger
        SET     trigger_type = 'paymentsPadReceiveReconciliation'
        WHERE   trigger_type = 'paymentsPadReciveReconciliation';
        
        /**
        ***     ========================================================================================================
        ***
        ***             CREATE CONSTRAINTS
        ***
        ***     ========================================================================================================
        **/
               
        -- Check constraints
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_app_e_ck CHECK ((app) IN ('crm', 'onboarding', 'operations', 'prospect', 'resident'));
        ALTER TABLE dev_card_service_simulation_transaction ADD CONSTRAINT dev_card_service_simulation_transaction_tp_e_ck 
                CHECK ((tp) IN ('completion', 'preAuthorization', 'preAuthorizationReversal', 'returnVoid', 'sale'));
        ALTER TABLE operations_alert ADD CONSTRAINT operations_alert_app_e_ck CHECK ((app) IN ('crm', 'onboarding', 'operations', 'prospect', 'resident'));
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
        
        CREATE UNIQUE INDEX dev_card_service_simulation_transaction_reference_tp_idx ON dev_card_service_simulation_transaction USING btree (reference, tp);
            
       


COMMIT;

SET client_min_messages = 'notice';
