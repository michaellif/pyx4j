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
        ALTER TABLE admin_pmc_dns_name DROP CONSTRAINT admin_pmc_dns_name_target_e_ck;
        ALTER TABLE audit_record DROP CONSTRAINT audit_record_app_e_ck;
        ALTER TABLE dev_card_service_simulation_transaction DROP CONSTRAINT dev_card_service_simulation_transaction_transaction_type_e_ck;
        ALTER TABLE operations_alert DROP CONSTRAINT operations_alert_app_e_ck;
        ALTER TABLE pad_sim_file DROP CONSTRAINT pad_sim_file_status_e_ck;
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
        
        
        -- pad_sim_file$state
        
        CREATE TABLE pad_sim_file$state
        (
                id                              BIGINT                  NOT NULL,
                owner                           BIGINT,
                value                           VARCHAR(50),
                        CONSTRAINT      pad_sim_file$state_pk PRIMARY KEY(id)
        );
        
        
        ALTER TABLE pad_sim_file$state OWNER TO vista;
        
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
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        -- pad_sim_file
        
        ALTER TABLE pad_sim_file DROP COLUMN status;
        
        
        /**
        ***     ========================================================================================================
        ***
        ***             CREATE CONSTRAINTS
        ***
        ***     ========================================================================================================
        **/
        
        -- Foreigh keys 
        ALTER TABLE pad_sim_file$state ADD CONSTRAINT pad_sim_file$state_owner_fk FOREIGN KEY(owner) REFERENCES pad_sim_file(id);
             
        -- Check constraints
        ALTER TABLE admin_pmc_dns_name ADD CONSTRAINT admin_pmc_dns_name_target_e_ck CHECK ((target) IN ('field', 'prospectPortal', 'residentPortal', 'vistaCrm'));
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_app_e_ck CHECK ((app) IN ('crm', 'field', 'onboarding', 'operations', 'prospect', 'resident'));
        ALTER TABLE dev_card_service_simulation_transaction ADD CONSTRAINT dev_card_service_simulation_transaction_tp_e_ck 
                CHECK ((tp) IN ('completion', 'preAuthorization', 'preAuthorizationReversal', 'returnVoid', 'sale'));
        ALTER TABLE operations_alert ADD CONSTRAINT operations_alert_app_e_ck CHECK ((app) IN ('crm', 'field', 'onboarding', 'operations', 'prospect', 'resident'));
        ALTER TABLE scheduler_trigger ADD CONSTRAINT scheduler_trigger_trigger_type_e_ck 
                CHECK ((trigger_type) IN ('billing', 'cleanup', 'equifaxRetention', 'initializeFutureBillingCycles', 'leaseActivation', 
                'leaseCompletion', 'leaseRenewal', 'paymentsBmoReceive', 'paymentsIssue', 'paymentsPadReceiveAcknowledgment', 
                'paymentsPadReceiveReconciliation', 'paymentsPadSend', 'paymentsScheduledCreditCards', 'paymentsScheduledEcheck', 
                'paymentsTenantSure', 'paymentsUpdate', 'tenantSureCancellation', 'tenantSureHQUpdate', 'tenantSureReports', 
                'tenantSureTransactionReports', 'test', 'updateArrears', 'updatePaymentsSummary', 'vistaBusinessReport', 
                'yardiBatchProcess', 'yardiImportProcess'));

              

        /**
        ***     ============================================================================================================
        ***     
        ***             INDEXES
        ***
        ***     ============================================================================================================
        **/
        
        CREATE UNIQUE INDEX dev_card_service_simulation_transaction_reference_tp_idx ON dev_card_service_simulation_transaction USING btree (reference, tp);
        CREATE INDEX pad_sim_file$state_owner_idx ON pad_sim_file$state USING btree (owner);
            
       


COMMIT;

SET client_min_messages = 'notice';
