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
        ALTER TABLE scheduler_run DROP CONSTRAINT scheduler_run_status_e_ck;
        ALTER TABLE scheduler_trigger DROP CONSTRAINT scheduler_trigger_trigger_type_e_ck;
        ALTER TABLE vista_terms DROP CONSTRAINT vista_terms_target_e_ck;

       

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


        -- admin_pmc_dns_name
        
        UPDATE  admin_pmc_dns_name
        SET https_enabled = FALSE
        WHERE   https_enabled IS NULL;

        -- cleanup for orpaned records in admin_pmc_vista_features
        
        DELETE FROM admin_pmc_vista_features
        WHERE   id NOT IN (SELECT features FROM admin_pmc);
        
        SET CONSTRAINTS ALL IMMEDIATE;
        
        
        /**     DELETE unnneded records from _admin_.vista.terms, _admin_.vista.terms_v,
        ***     _admin_.vista_terms_v$document AND _admin_.legal_document 
        **/
        
        
        
        DELETE FROM vista_terms_v$document
        WHERE   owner IN (  SELECT id  FROM vista_terms_v 
                            WHERE holder IN (   SELECT  id
                                                FROM    vista_terms 
                                                WHERE   target IN ('TenantBillingTerms',
                                                'TenantPreAuthorizedPaymentECheckTerms',
                                                'TenantPreAuthorizedPaymentCardTerms')));
                                                
        DELETE FROM legal_document 
        WHERE   id NOT IN ( SELECT value  FROM vista_terms_v$document) ;
            

        
        DELETE FROM vista_terms_v WHERE holder IN ( SELECT  id
                                                    FROM    vista_terms 
                                                    WHERE   target IN ('TenantBillingTerms',
                                                    'TenantPreAuthorizedPaymentECheckTerms',
                                                    'TenantPreAuthorizedPaymentCardTerms'));
        
        DELETE FROM vista_terms 
        WHERE target IN (   'TenantBillingTerms','TenantPreAuthorizedPaymentECheckTerms',
                            'TenantPreAuthorizedPaymentCardTerms');


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
        ALTER TABLE scheduler_run ADD CONSTRAINT scheduler_run_status_e_ck 
            CHECK ((status) IN ('Completed', 'Failed', 'PartiallyCompleted', 'Running', 'Sleeping', 'Terminated', 'TryAgain'));
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
        ALTER TABLE vista_terms ADD CONSTRAINT vista_terms_target_e_ck 
            CHECK ((target) IN ('PmcCaledonSoleProprietorshipSection', 'PmcCaledonTemplate', 'PmcPaymentPad', 'PmcPropertyVistaService', 
            'ProspectPortalPrivacyPolicy', 'ProspectPortalTermsAndConditions', 'ResidentPortalPrivacyPolicy', 'ResidentPortalTermsAndConditions', 
            'TenantPaymentWebPaymentFeeTerms', 'TenantSurePreAuthorizedPaymentsAgreement'));

        
        -- not null
        
        ALTER TABLE admin_pmc_dns_name ALTER COLUMN enabled SET NOT NULL;
        ALTER TABLE admin_pmc_dns_name ALTER COLUMN https_enabled SET NOT NULL;
        ALTER TABLE admin_pmc_vista_features ALTER COLUMN country_of_operation SET NOT NULL;
        ALTER TABLE admin_pmc_vista_features ALTER COLUMN online_application SET NOT NULL;
        ALTER TABLE admin_pmc_vista_features ALTER COLUMN tenant_email_enabled SET NOT NULL;
        ALTER TABLE admin_pmc_vista_features ALTER COLUMN tenant_sure_integration SET NOT NULL;
        ALTER TABLE admin_pmc_vista_features ALTER COLUMN white_label_portal SET NOT NULL;
        ALTER TABLE admin_pmc_vista_features ALTER COLUMN yardi_integration SET NOT NULL;
        ALTER TABLE admin_pmc_vista_features ALTER COLUMN yardi_maintenance SET NOT NULL;
        ALTER TABLE dev_card_service_simulation_transaction ALTER COLUMN voided SET NOT NULL;
        


        /**
        ***     ============================================================================================================
        ***
        ***             CREATE INDEXES
        ***
        ***     ============================================================================================================
        **/

       
COMMIT;

SET client_min_messages = 'notice';
