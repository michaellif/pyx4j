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
        
        -- check constraints
        
        ALTER TABLE scheduler_trigger DROP CONSTRAINT scheduler_trigger_trigger_type_e_ck;
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP INDEXES 
        ***
        ***     ======================================================================================================
        **/
        
        DROP INDEX pad_batch_pmc_namespace_merchant_account_key_idx;
        
        
        /**
        ***     =======================================================================================================
        ***
        ***             NEW AND ALTERED TABLES 
        ***
        ***     =======================================================================================================
        **/
        
        -- admin_pmc_vista_features
        
        ALTER TABLE admin_pmc_vista_features ADD COLUMN yardi_maintenance BOOLEAN;
        
        -- pad_batch
        
        ALTER TABLE pad_batch ADD COLUMN pmc BIGINT;
        
        
        -- scheduler_run
        
        ALTER TABLE scheduler_run ADD COLUMN started_by BIGINT;
       
        /**
        ***     ============================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     ============================================================================================================
        **/
        
        -- pad_batch
        
        
        UPDATE  _admin_.pad_batch AS b
        SET     pmc = a.id
        FROM    _admin_.admin_pmc a
        WHERE   a.namespace = b.pmc_namespace;
        
        
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        -- pad_reconciliation_debit_record
        
        ALTER TABLE pad_reconciliation_debit_record DROP COLUMN odr;
        
        
        -- pad_reconciliation_summary
        
        ALTER TABLE pad_reconciliation_summary DROP COLUMN odr;
        
        
        -- pad_batch
        
        ALTER TABLE pad_batch DROP COLUMN pmc_namespace;
        
        
        
        /**
        ***     ========================================================================================================
        ***
        ***             CREATE CONSTRAINTS
        ***
        ***     ========================================================================================================
        **/
        
        -- foreign keys
        
        ALTER TABLE pad_batch ADD CONSTRAINT pad_batch_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE scheduler_run ADD CONSTRAINT scheduler_run_started_by_fk FOREIGN KEY(started_by) REFERENCES operations_user(id)  DEFERRABLE INITIALLY DEFERRED;
        
        -- check constraints
        
        ALTER TABLE scheduler_trigger ADD CONSTRAINT scheduler_trigger_trigger_type_e_ck 
                CHECK ((trigger_type) IN ('billing', 'cleanup', 'depositInterestAdjustment', 'depositRefund', 'equifaxRetention', 'initializeFutureBillingCycles', 
                'leaseActivation', 'leaseCompletion', 'leaseRenewal', 'paymentsBmoReceive', 'paymentsIssue', 'paymentsPadProcesAcknowledgment', 'paymentsPadProcesReconciliation',
                'paymentsPadReceiveAcknowledgment', 'paymentsPadReceiveReconciliation', 'paymentsPadSend', 'paymentsScheduledCreditCards', 'paymentsScheduledEcheck', 
                'paymentsTenantSure', 'paymentsUpdate', 'tenantSureCancellation', 'tenantSureHQUpdate', 'tenantSureReports', 'tenantSureTransactionReports', 'test', 
                'updateArrears', 'updatePaymentsSummary', 'vistaBusinessReport', 'yardiImportProcess'));

        

        -- not null
        
        ALTER TABLE pad_batch ALTER COLUMN pmc SET NOT NULL;
                
        /**
        ***     ============================================================================================================
        ***     
        ***             CREATE INDEXES
        ***
        ***     ============================================================================================================
        **/
        
        CREATE INDEX pad_batch_pmc_merchant_account_key_idx ON pad_batch USING btree (pmc, merchant_account_key); 
       


COMMIT;

SET client_min_messages = 'notice';
