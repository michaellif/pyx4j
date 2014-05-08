/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             _admin_ schema changes for v. 1.1.4
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
        
        DROP INDEX direct_debit_record_pmc_idx;

        /**
        ***     =======================================================================================================
        ***
        ***             RENAMED TABLES 
        ***
        ***     =======================================================================================================
        **/
        
        
        
        /**
        ***     =======================================================================================================
        ***
        ***             NEW AND ALTERED TABLES 
        ***
        ***     =======================================================================================================
        **/
        
        -- scheduler_trigger
        
        ALTER TABLE scheduler_trigger   ADD COLUMN run_timeout INT,
                                        ADD COLUMN threads INT;
       
       
        /**
        ***     ============================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     ============================================================================================================
        **/
        
        
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
       
       
        
        /**
        ***     ========================================================================================================
        ***
        ***             CREATE CONSTRAINTS
        ***
        ***     ========================================================================================================
        **/
        
       
        -- check constraints
        
        ALTER TABLE scheduler_trigger ADD CONSTRAINT scheduler_trigger_trigger_type_e_ck 
            CHECK ((trigger_type) IN (  'billing', 'cleanup', 'depositInterestAdjustment', 'depositRefund', 
                                        'equifaxRetention', 'ilsEmailFeed', 'ilsUpdate', 'initializeFutureBillingCycles', 
                                        'leaseActivation', 'leaseCompletion', 'leaseRenewal', 'paymentsBmoReceive', 
                                        'paymentsDbpProcess', 'paymentsDbpProcessAcknowledgment', 'paymentsDbpProcessReconciliation', 
                                        'paymentsDbpSend', 'paymentsIssue', 'paymentsLastMonthSuspend', 'paymentsPadProcessAcknowledgment', 
                                        'paymentsPadProcessReconciliation', 'paymentsPadSend', 'paymentsReceiveAcknowledgment', 
                                        'paymentsReceiveReconciliation', 'paymentsScheduledCreditCards', 'paymentsScheduledEcheck', 
                                        'paymentsTenantSure', 'tenantSureCancellation', 'tenantSureHQUpdate', 'tenantSureReports', 
                                        'tenantSureTransactionReports', 'test', 'updateArrears', 'updatePaymentsSummary', 
                                        'vistaBusinessReport', 'vistaCaleonReport', 'vistaHeathMonitor', 'yardiARDateVerification', 
                                        'yardiImportProcess'));

                
        /**
        ***     ============================================================================================================
        ***     
        ***             CREATE INDEXES
        ***
        ***     ============================================================================================================
        **/
        
       CREATE INDEX direct_debit_record_pmc_processing_status_idx ON direct_debit_record USING btree(pmc, processing_status);

COMMIT;

SET client_min_messages = 'notice';
