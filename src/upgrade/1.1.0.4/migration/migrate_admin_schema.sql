/**
***     =====================================================================================================================
***
***             @version $Revision: 12919 $ ($Author: akinareevski $) $Date: 2013-05-28 16:26:54 -0400 (Tue, 28 May 2013) $
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
                'initializeFutureBillingCycles', 'leaseActivation', 'leaseCompletion', 'leaseRenewal', 'paymentsBmoReceive', 
                'paymentsIssue', 'paymentsPadProcesAcknowledgment', 'paymentsPadProcesReconciliation', 'paymentsPadReceiveAcknowledgment', 
                'paymentsPadReceiveReconciliation', 'paymentsPadSend', 'paymentsScheduledCreditCards', 'paymentsScheduledEcheck', 
                'paymentsTenantSure', 'paymentsUpdate', 'tenantSureCancellation', 'tenantSureHQUpdate', 'tenantSureReports', 
                'tenantSureTransactionReports', 'test', 'updateArrears', 'updatePaymentsSummary', 'vistaBusinessReport', 
                'vistaCaleonReport', 'yardiARDateVerification', 'yardiImportProcess'));



                
        /**
        ***     ============================================================================================================
        ***     
        ***             CREATE INDEXES
        ***
        ***     ============================================================================================================
        **/
        
        
       


COMMIT;

SET client_min_messages = 'notice';
