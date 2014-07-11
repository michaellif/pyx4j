/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             _admin_ schema changes for v. 1.1.3.8
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
            CHECK ((trigger_type) IN ('billing', 'cleanup', 'depositInterestAdjustment', 'depositRefund', 'equifaxRetention', 
            'ilsEmailFeed', 'ilsUpdate', 'initializeFutureBillingCycles', 'leaseActivation', 'leaseCompletion', 'leaseRenewal', 
            'paymentsBmoReceive', 'paymentsDbpProcess', 'paymentsDbpProcessAcknowledgment', 'paymentsDbpProcessReconciliation', 
            'paymentsDbpSend', 'paymentsIssue', 'paymentsLastMonthSuspend', 'paymentsPadProcessAcknowledgment', 
            'paymentsPadProcessReconciliation', 'paymentsPadSend', 'paymentsReceiveAcknowledgment', 'paymentsReceiveReconciliation', 
            'paymentsScheduledCreditCards', 'paymentsScheduledEcheck', 'paymentsTenantSure', 'tenantSureCancellation', 'tenantSureHQUpdate', 
            'tenantSureRenewal', 'tenantSureReports', 'tenantSureTransactionReports', 'test', 'updateArrears', 'updatePaymentsSummary', 
            'vistaBusinessReport', 'vistaCaleonReport', 'yardiARDateVerification', 'yardiImportProcess'));

                
        /**
        ***     ============================================================================================================
        ***     
        ***             CREATE INDEXES
        ***
        ***     ============================================================================================================
        **/
        
        

COMMIT;

SET client_min_messages = 'notice';
