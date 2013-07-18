/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             _admin_ schema changes for v. 1.1.0.7
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
        ALTER TABLE scheduler_run DROP CONSTRAINT scheduler_run_status_e_ck;
        
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
        
        -- turn tenantsure on
        
        UPDATE  _admin_.admin_pmc_vista_features
        SET     tenant_sure_integration = TRUE
        WHERE   NOT tenant_sure_integration;
        
        
        
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
        
        ALTER TABLE scheduler_run ADD CONSTRAINT scheduler_run_status_e_ck
                CHECK (status IN ('Completed','Failed','PartiallyCompleted','Running','Sleeping','Terminated'));

        
        ALTER TABLE scheduler_trigger ADD CONSTRAINT scheduler_trigger_trigger_type_e_ck 
                CHECK ((trigger_type) IN ('billing', 'cleanup', 'depositInterestAdjustment', 'depositRefund', 'equifaxRetention', 
                'initializeFutureBillingCycles', 'leaseActivation', 'leaseCompletion', 'leaseRenewal', 'paymentsBmoReceive', 
                'paymentsIssue', 'paymentsLastMonthSuspend', 'paymentsPadProcesAcknowledgment', 'paymentsPadProcesReconciliation', 
                'paymentsPadReceiveAcknowledgment', 'paymentsPadReceiveReconciliation', 'paymentsPadSend', 'paymentsScheduledCreditCards', 
                'paymentsScheduledEcheck', 'paymentsTenantSure', 'paymentsUpdate', 'tenantSureCancellation', 'tenantSureHQUpdate', 
                'tenantSureReports', 'tenantSureTransactionReports', 'test', 'updateArrears', 'updatePaymentsSummary', 'vistaBusinessReport', 
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
