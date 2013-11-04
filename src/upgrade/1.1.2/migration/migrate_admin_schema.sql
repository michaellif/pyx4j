/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             _admin_ schema changes for v. 1.1.2
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
        
        ALTER TABLE admin_pmc_dns_name DROP CONSTRAINT admin_pmc_dns_name_target_e_ck;
        ALTER TABLE audit_record DROP CONSTRAINT audit_record_app_e_ck;
        ALTER TABLE audit_record DROP CONSTRAINT audit_record_event_e_ck;
        ALTER TABLE dev_card_service_simulator_config DROP CONSTRAINT dev_card_service_simulator_config_response_type_e_ck;
        ALTER TABLE operations_alert DROP CONSTRAINT operations_alert_app_e_ck;
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
        
        -- admin_pmc_yardi_credential
         
        ALTER TABLE admin_pmc_yardi_credential ADD COLUMN ils_guest_card_service_url VARCHAR(500);  
         
         
        -- dev_card_service_simulator_config
        
        ALTER TABLE dev_card_service_simulator_config   ADD COLUMN accept_card_expiry_to DATE,
                                                        ADD COLUMN accept_card_expiry_from DATE;            
        
        -- pad_file
        
        ALTER TABLE pad_file ADD COLUMN file_creation_number_s VARCHAR(512);
       
        /**
        ***     ============================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     ============================================================================================================
        **/
        
        UPDATE  admin_pmc_dns_name
        SET     target = 'portal'
        WHERE   target = 'residentPortal';
        
        UPDATE  audit_record
        SET     app = 'portal'
        WHERE   app = 'residentPortal';
        
        UPDATE  pad_file
        SET     file_creation_number_s = _dba_.convert_id_to_string('file_creation_number');
       
        DELETE 
        FROM    scheduler_run_data
        WHERE   execution IN    (SELECT id
                                FROM    scheduler_run
                                WHERE   trgr IN (SELECT id 
                                                FROM    scheduler_trigger
                                                WHERE   trigger_type = 'paymentsUpdate'));
       
        DELETE 
        FROM    scheduler_run 
        WHERE   trgr IN (SELECT id 
                        FROM    scheduler_trigger
                        WHERE   trigger_type = 'paymentsUpdate');
                        
        DELETE
        FROM    scheduler_trigger_schedule
        WHERE   trgr IN (SELECT id 
                        FROM    scheduler_trigger
                        WHERE   trigger_type = 'paymentsUpdate');
                        
        DELETE 
        FROM    scheduler_trigger
        WHERE   trigger_type = 'paymentsUpdate';
        
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
        
        ALTER TABLE admin_pmc_dns_name ADD CONSTRAINT admin_pmc_dns_name_target_e_ck CHECK ((target) IN ('crm', 'field', 'portal', 'prospect', 'site'));
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_app_e_ck CHECK ((app) IN ('crm', 'field', 'onboarding', 'operations', 'portal', 'prospect', 'site'));
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_event_e_ck 
                CHECK ((event) IN ('Create', 'CredentialUpdate', 'Delete', 'EquifaxReadReport', 'EquifaxRequest', 'Info', 'Login', 'LoginFailed', 'Logout', 'PermitionsUpdate', 
                'Read', 'SessionExpiration', 'System', 'Update'));
        ALTER TABLE dev_card_service_simulator_config ADD CONSTRAINT dev_card_service_simulator_config_response_type_e_ck 
                CHECK ((response_type) IN ('RespondEmpty', 'RespondWithCode', 'RespondWithHttpCode', 'RespondWithText', 'SimulateTransations'));
        ALTER TABLE operations_alert ADD CONSTRAINT operations_alert_app_e_ck CHECK ((app) IN ('crm', 'field', 'onboarding', 'operations', 'portal', 'prospect', 'site'));
        ALTER TABLE scheduler_trigger ADD CONSTRAINT scheduler_trigger_trigger_type_e_ck 
                CHECK ((trigger_type) IN ('billing', 'cleanup', 'depositInterestAdjustment', 'depositRefund', 'equifaxRetention', 'initializeFutureBillingCycles', 
                'leaseActivation', 'leaseCompletion', 'leaseRenewal', 'paymentsBmoReceive', 'paymentsDbpProcess', 'paymentsDbpProcessAcknowledgment', 'paymentsDbpProcessReconciliation',
                'paymentsDbpSend', 'paymentsIssue', 'paymentsLastMonthSuspend', 'paymentsPadProcessAcknowledgment', 'paymentsPadProcessReconciliation', 'paymentsPadSend',
                'paymentsReceiveAcknowledgment', 'paymentsReceiveReconciliation', 'paymentsScheduledCreditCards', 'paymentsScheduledEcheck', 'paymentsTenantSure', 
                'tenantSureCancellation', 'tenantSureHQUpdate', 'tenantSureReports', 'tenantSureTransactionReports', 'test', 'updateArrears', 'updatePaymentsSummary', 
                'vistaBusinessReport', 'vistaCaleonReport', 'yardiARDateVerification', 'yardiImportProcess'));
        ALTER TABLE vista_terms ADD CONSTRAINT vista_terms_target_e_ck 
                CHECK ((target) IN ('PMC', 'PmcCaledonSoleProprietorshipSection', 'PmcCaledonTemplate', 'PmcPaymentPad', 'Tenant', 'TenantBilling', 'TenantPaymentCreditCard', 
                'TenantPaymentPad', 'TenantPreAuthorizedPaymentsAgreement', 'TenantSurePreAuthorizedPaymentsAgreement'));



                
        /**
        ***     ============================================================================================================
        ***     
        ***             CREATE INDEXES
        ***
        ***     ============================================================================================================
        **/
        
        


       


COMMIT;

SET client_min_messages = 'notice';
