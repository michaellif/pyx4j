/**
***     =====================================================================================================================
***
***             _admin_ schema changes for v. 1.4.2
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
        
        ALTER TABLE audit_record DROP CONSTRAINT audit_record_event_e_ck;
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

        -- admin_pmc_payment_method
        
        ALTER TABLE admin_pmc_payment_method RENAME COLUMN creation_date TO created;
        
        
        -- dev_card_service_simulation_reconciliation_record
        
        ALTER TABLE dev_card_service_simulation_reconciliation_record ADD COLUMN created TIMESTAMP;
        
        -- development_user
        
        ALTER TABLE development_user ADD COLUMN walk_me_disabled BOOLEAN;
        
        -- tenant_sure_merchant_account
        
        ALTER TABLE tenant_sure_merchant_account ALTER COLUMN charge_description TYPE VARCHAR(30);
        
        -- vista_merchant_account
        
        ALTER TABLE vista_merchant_account ALTER COLUMN charge_description TYPE VARCHAR(30);
      
        

        /**
        ***     ============================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     ============================================================================================================
        **/


        -- cleanup for orpaned records in admin_pmc_vista_features
        
        DELETE FROM admin_pmc_vista_features
        WHERE   id NOT IN (SELECT features FROM admin_pmc);
        
        SET CONSTRAINTS ALL IMMEDIATE;


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
        
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_event_e_ck 
            CHECK ((event) IN ('Create', 'CredentialUpdate', 'Delete', 'EquifaxReadReport', 'EquifaxRequest', 'Info', 'Login', 
            'LoginFailed', 'Logout', 'OpenIdLogin', 'PermitionsUpdate', 'Read', 'SessionExpiration', 'System', 'Update'));
        ALTER TABLE scheduler_trigger ADD CONSTRAINT scheduler_trigger_trigger_type_e_ck 
            CHECK ((trigger_type) IN ('billing', 'cleanup', 'depositInterestAdjustment', 'depositRefund', 'equifaxRetention', 
            'ilsEmailFeed', 'ilsUpdate', 'initializeFutureBillingCycles', 'leaseActivation', 'leaseCompletion', 'leaseRenewal', 
            'n4AutoCancellation', 'paymentsBmoReceive', 'paymentsCardsPostRejected', 'paymentsCardsSend', 'paymentsDbpProcess', 
            'paymentsDbpProcessAcknowledgment', 'paymentsDbpProcessReconciliation', 'paymentsDbpSend', 'paymentsIssue', 
            'paymentsLastMonthSuspend', 'paymentsPadProcessAcknowledgment', 'paymentsPadProcessReconciliation', 'paymentsPadSend', 
            'paymentsProcessCardsReconciliation', 'paymentsReceiveAcknowledgment', 'paymentsReceiveCardsReconciliation', 
            'paymentsReceiveReconciliation', 'paymentsScheduledCards', 'paymentsScheduledEcheck', 'paymentsTenantSure', 
            'resetDemoPMC', 'tenantSureBusinessReport', 'tenantSureCancellation', 'tenantSureHQUpdate', 'tenantSureRenewal', 
            'tenantSureReports', 'tenantSureTransactionReports', 'test', 'updateArrears', 'updatePaymentsSummary', 
            'vistaBusinessReport', 'vistaCaleonReport', 'vistaHeathMonitor', 'yardiARDateVerification', 'yardiImportProcess'));
        
        -- not null
        
        ALTER TABLE admin_pmc_vista_features ALTER COLUMN country_of_operation SET NOT NULL;
        ALTER TABLE admin_pmc_vista_features ALTER COLUMN online_application SET NOT NULL;
        ALTER TABLE admin_pmc_vista_features ALTER COLUMN tenant_email_enabled SET NOT NULL;
        ALTER TABLE admin_pmc_vista_features ALTER COLUMN tenant_sure_integration SET NOT NULL;
        ALTER TABLE admin_pmc_vista_features ALTER COLUMN white_label_portal SET NOT NULL;
        ALTER TABLE admin_pmc_vista_features ALTER COLUMN yardi_integration SET NOT NULL;
        ALTER TABLE admin_pmc_vista_features ALTER COLUMN yardi_maintenance SET NOT NULL;
        


        /**
        ***     ============================================================================================================
        ***
        ***             CREATE INDEXES
        ***
        ***     ============================================================================================================
        **/

       
COMMIT;

SET client_min_messages = 'notice';
