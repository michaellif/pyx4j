/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             _admin_ schema changes for v. 1.4.1
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
        
        -- foreign keys
        
        ALTER TABLE cards_clearance_record DROP CONSTRAINT cards_clearance_record_file_fk;
        ALTER TABLE dev_direct_debit_sim_record DROP CONSTRAINT dev_direct_debit_sim_record_file_fk;
        ALTER TABLE direct_debit_record DROP CONSTRAINT direct_debit_record_file_fk;
        ALTER TABLE encrypted_storage_current_key DROP CONSTRAINT encrypted_storage_current_key_current_fk;
        ALTER TABLE tenant_sure_hqupdate_record DROP CONSTRAINT tenant_sure_hqupdate_record_file_fk;


        -- check constraints
        ALTER TABLE audit_record DROP CONSTRAINT audit_record_app_e_ck;
        ALTER TABLE dev_card_service_simulation_transaction DROP CONSTRAINT dev_card_service_simulation_transaction_tp_e_ck;
        ALTER TABLE operations_alert DROP CONSTRAINT operations_alert_app_e_ck;
        ALTER TABLE scheduler_run_data DROP CONSTRAINT scheduler_run_data_status_e_ck;
        ALTER TABLE scheduler_trigger DROP CONSTRAINT scheduler_trigger_trigger_type_e_ck;
        

        /**
        ***     ======================================================================================================
        ***
        ***             DROP INDEXES
        ***
        ***     ======================================================================================================
        **/

        DROP INDEX cards_clearance_record_file_idx;
        DROP INDEX dev_direct_debit_sim_record_file_idx;
        DROP INDEX direct_debit_record_file_idx;
        DROP INDEX tenant_sure_hqupdate_record_file_idx;

       
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
        
        
        -- admin_pmc_vista_features
        
        ALTER TABLE admin_pmc_vista_features ADD COLUMN white_label_portal BOOLEAN;

        -- admin_pmc_yardi_credential
        
        ALTER TABLE admin_pmc_yardi_credential  ADD COLUMN created TIMESTAMP,
                                                ADD COLUMN updated TIMESTAMP;
                                               
                                                
                                                
        -- cards_clearance_record
        
        ALTER TABLE cards_clearance_record ADD COLUMN card_type VARCHAR(50);
        ALTER TABLE cards_clearance_record RENAME COLUMN file TO clearance_file;
        
        -- cards_reconciliation_record
        
        ALTER TABLE cards_reconciliation_record RENAME COLUMN date TO deposit_date;
        
        
        -- card_transaction_record
        
        ALTER TABLE card_transaction_record ADD COLUMN voided BOOLEAN;
        
        -- dev_card_service_simulation_card
        
        ALTER TABLE dev_card_service_simulation_card RENAME COLUMN number TO card_number;
        
        
        -- dev_card_service_simulation_merchant_account
        
        ALTER TABLE dev_card_service_simulation_merchant_account    ADD COLUMN master_card_fee NUMERIC(18,4),
                                                                    ADD COLUMN visa_credit_fee NUMERIC(18,4),
                                                                    ADD COLUMN visa_debit_fee NUMERIC(18,4);
        
        
        -- dev_card_service_simulation_reconciliation_record
        
        ALTER TABLE dev_card_service_simulation_reconciliation_record RENAME COLUMN date TO deposit_date;
        
        
        -- dev_direct_debit_sim_record
        
        ALTER TABLE dev_direct_debit_sim_record RENAME COLUMN file TO direct_debit_file;
        
        
        -- direct_debit_record
        
        ALTER TABLE direct_debit_record RENAME COLUMN file TO direct_debit_file;
        
        -- encrypted_storage_current_key
        
        ALTER TABLE encrypted_storage_current_key RENAME COLUMN current TO current_key;
        
        -- oapi_conversion
        
        CREATE TABLE oapi_conversion
        (
            id                          BIGINT              NOT NULL,
            created                     DATE,
            name                        VARCHAR(500),
            tp                          VARCHAR(50),
            description                 VARCHAR(500),
                CONSTRAINT oapi_conversion_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE oapi_conversion OWNER TO vista;
        

        -- oapi_conversion_blob
        
        CREATE TABLE oapi_conversion_blob
        (
            id                          BIGINT              NOT NULL,
            name                        VARCHAR(500),
            content_type                VARCHAR(500),
            updated                     TIMESTAMP,
            created                     TIMESTAMP,
            data                        BYTEA,
                CONSTRAINT oapi_conversion_blob_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE oapi_conversion_blob OWNER TO vista;
        

        -- oapi_conversion_file
        
        CREATE TABLE oapi_conversion_file
        (
            id                          BIGINT              NOT NULL,
            oapi                        BIGINT,
            tp                          VARCHAR(50),
            file_file_name              VARCHAR(500),
            file_updated_timestamp      BIGINT,
            file_cache_version          INTEGER,
            file_file_size              INTEGER,
            file_content_mime_type      VARCHAR(500),
            file_blob_key               BIGINT,
                CONSTRAINT oapi_conversion_file_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE oapi_conversion_file OWNER TO vista;
        
        
        -- onboarding_user
        
        ALTER TABLE onboarding_user RENAME COLUMN password TO credential;
        
        -- portal_resident_marketing_tip
        
        CREATE TABLE portal_resident_marketing_tip
        (
            id                          BIGINT              NOT NULL,
            created                     TIMESTAMP,
            updated                     TIMESTAMP,
            target                      VARCHAR(50),
            comments                    VARCHAR(500),
            content                     VARCHAR(300000),
                CONSTRAINT portal_resident_marketing_tip_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE portal_resident_marketing_tip OWNER TO vista;
        
        
        -- scheduler_execution_report
        
        ALTER TABLE scheduler_execution_report ADD COLUMN details_erred BIGINT;
        
        -- scheduler_execution_report_section
        
        ALTER TABLE scheduler_execution_report_section RENAME COLUMN value TO val;
        
        
        -- tenant_sure_hqupdate_record
        
        ALTER TABLE tenant_sure_hqupdate_record RENAME COLUMN file TO owner;
        
        
        /**
        ***     ============================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     ============================================================================================================
        **/

        -- admin_pmc_vista_features
        
        UPDATE  admin_pmc_vista_features
        SET     white_label_portal = FALSE;
        
        -- scheduler_trigger
        
        INSERT INTO scheduler_trigger (id,trigger_type,name,population_type,schedule_suspended,created) VALUES 
        (nextval('public.scheduler_trigger_seq'),'paymentsCardsSend','P 5C - Send Cards Payments to Caledon','allPmc',TRUE,
        DATE_TRUNC('second',current_timestamp)::timestamp),
        (nextval('public.scheduler_trigger_seq'),'paymentsCardsPostRejected','P 5D - Post rejected Cards Payments','allPmc',TRUE,
        DATE_TRUNC('second',current_timestamp)::timestamp);
        
        UPDATE  scheduler_trigger
        SET     trigger_type = 'paymentsScheduledCards'
        WHERE   trigger_type = 'paymentsScheduledCreditCards';
        

       
        /**
        ***     ==========================================================================================================
        ***
        ***         BUG VISTA-5353 FIX - VOIDED TRANSACTIONS
        ***     
        ***     ==========================================================================================================
        **/
        
        UPDATE  card_transaction_record
        SET     voided = TRUE 
        WHERE   payment_transaction_id IN ('R70588','R76478','R70586','R70587',
        'R76355','R70591','R57157','R57171','R43408','R43409','R43433','R43435',
        'R43443','R43424','R64080','R76614');
       

        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/

        -- operations_alert
        
        ALTER TABLE operations_alert DROP COLUMN admin;


        /**
        ***     ========================================================================================================
        ***
        ***             CREATE CONSTRAINTS
        ***
        ***     ========================================================================================================
        **/

        -- foreign keys
        ALTER TABLE cards_clearance_record ADD CONSTRAINT cards_clearance_record_clearance_file_fk FOREIGN KEY(clearance_file) 
            REFERENCES cards_clearance_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE dev_direct_debit_sim_record ADD CONSTRAINT dev_direct_debit_sim_record_direct_debit_file_fk FOREIGN KEY(direct_debit_file) 
            REFERENCES dev_direct_debit_sim_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE direct_debit_record ADD CONSTRAINT direct_debit_record_direct_debit_file_fk FOREIGN KEY(direct_debit_file) 
            REFERENCES direct_debit_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE encrypted_storage_current_key ADD CONSTRAINT encrypted_storage_current_key_current_key_fk FOREIGN KEY(current_key) 
            REFERENCES encrypted_storage_public_key(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE tenant_sure_hqupdate_record ADD CONSTRAINT tenant_sure_hqupdate_record_owner_fk FOREIGN KEY(owner) 
            REFERENCES tenant_sure_hqupdate_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE oapi_conversion_file ADD CONSTRAINT oapi_conversion_file_oapi_fk FOREIGN KEY(oapi) 
            REFERENCES oapi_conversion(id)  DEFERRABLE INITIALLY DEFERRED;

       

        -- check constraints
        
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_app_e_ck 
            CHECK ((app) IN ('crm', 'interfaces', 'noApp', 'onboarding', 'operations', 'prospect', 'resident', 'site'));
        ALTER TABLE cards_clearance_record ADD CONSTRAINT cards_clearance_record_card_type_e_ck 
            CHECK ((card_type) IN ('CREDIT', 'MCRD', 'VISA'));
        ALTER TABLE dev_card_service_simulation_transaction ADD CONSTRAINT dev_card_service_simulation_transaction_tp_e_ck 
            CHECK ((tp) IN ('Completion', 'PreAuthorization', 'PreAuthorizationReversal', 'Return', 'Sale', 'Void'));
        ALTER TABLE oapi_conversion ADD CONSTRAINT oapi_conversion_tp_e_ck CHECK (tp = 'Base');
        ALTER TABLE oapi_conversion_file ADD CONSTRAINT oapi_conversion_file_tp_e_ck 
            CHECK ((tp) IN ('AnotherIO', 'BuildingIO'));
        ALTER TABLE operations_alert ADD CONSTRAINT operations_alert_app_e_ck 
            CHECK ((app) IN ('crm', 'interfaces', 'noApp', 'onboarding', 'operations', 'prospect', 'resident', 'site'));
        ALTER TABLE portal_resident_marketing_tip ADD CONSTRAINT portal_resident_marketing_tip_target_e_ck 
            CHECK ((target) IN ('AutopayAgreementNotSetup', 'InsuranceMissing', 'Other'));
        ALTER TABLE scheduler_run_data ADD CONSTRAINT scheduler_run_data_status_e_ck 
            CHECK ((status) IN ('Canceled', 'Erred', 'Failed', 'NeverRan', 'PartiallyProcessed', 'Processed', 'Running', 'Terminated'));
        ALTER TABLE scheduler_trigger ADD CONSTRAINT scheduler_trigger_trigger_type_e_ck 
            CHECK ((trigger_type) IN ('billing', 'cleanup', 'depositInterestAdjustment', 'depositRefund', 'equifaxRetention', 'ilsEmailFeed', 
            'ilsUpdate', 'initializeFutureBillingCycles', 'leaseActivation', 'leaseCompletion', 'leaseRenewal', 'paymentsBmoReceive', 
            'paymentsCardsPostRejected', 'paymentsCardsSend', 'paymentsDbpProcess', 'paymentsDbpProcessAcknowledgment', 
            'paymentsDbpProcessReconciliation', 'paymentsDbpSend', 'paymentsIssue', 'paymentsLastMonthSuspend', 'paymentsPadProcessAcknowledgment', 
            'paymentsPadProcessReconciliation', 'paymentsPadSend', 'paymentsProcessCardsReconciliation', 'paymentsReceiveAcknowledgment', 
            'paymentsReceiveCardsReconciliation', 'paymentsReceiveReconciliation', 'paymentsScheduledCards', 'paymentsScheduledEcheck', 
            'paymentsTenantSure', 'tenantSureBusinessReport', 'tenantSureCancellation', 'tenantSureHQUpdate', 'tenantSureRenewal', 'tenantSureReports', 
            'tenantSureTransactionReports', 'test', 'updateArrears', 'updatePaymentsSummary', 'vistaBusinessReport', 'vistaCaleonReport', 
            'vistaHeathMonitor', 'yardiARDateVerification', 'yardiImportProcess'));



        /**
        ***     ============================================================================================================
        ***
        ***             CREATE INDEXES
        ***
        ***     ============================================================================================================
        **/
    
        CREATE INDEX cards_clearance_record_clearance_file_idx ON cards_clearance_record USING btree (clearance_file);
        CREATE INDEX dev_direct_debit_sim_record_direct_debit_file_idx ON dev_direct_debit_sim_record USING btree (direct_debit_file);
        CREATE INDEX direct_debit_record_direct_debit_file_idx ON direct_debit_record USING btree (direct_debit_file);
        CREATE INDEX tenant_sure_hqupdate_record_owner_idx ON tenant_sure_hqupdate_record USING btree (owner);




COMMIT;

SET client_min_messages = 'notice';
