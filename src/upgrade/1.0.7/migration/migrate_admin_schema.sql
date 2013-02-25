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
        
        DROP TABLE admin_pmc$credit_check_transaction;

        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP CONSTRAINTS SECTION
        ***
        ***     ======================================================================================================
        **/
        
        -- Foreign keys to drop
        ALTER TABLE admin_onboarding_merchant_account DROP CONSTRAINT admin_onboarding_merchant_account_pmc_fk;
        ALTER TABLE admin_user_credential$behaviors DROP CONSTRAINT admin_user_credential$behaviors_owner_fk;
        ALTER TABLE admin_user_credential DROP CONSTRAINT admin_user_credential_usr_fk;
        ALTER TABLE pad_reconciliation_summary DROP CONSTRAINT pad_reconciliation_summary_merchant_account_fk;
        ALTER TABLE scheduler_run_data DROP CONSTRAINT scheduler_run_data_stats_fk;
        ALTER TABLE scheduler_run DROP CONSTRAINT scheduler_run_stats_fk;
        ALTER TABLE scheduler_trigger_notification DROP CONSTRAINT scheduler_trigger_notification_usr_fk;

        
        -- Primary keys to drop
        
        ALTER TABLE admin_user_credential$behaviors DROP CONSTRAINT admin_user_credential$behaviors_pk;
        ALTER TABLE admin_user_credential DROP CONSTRAINT admin_user_credential_pk;
        ALTER TABLE admin_user DROP CONSTRAINT admin_user_pk;
        ALTER TABLE admin_onboarding_merchant_account DROP CONSTRAINT admin_onboarding_merchant_account_pk;
        ALTER TABLE scheduler_run_stats DROP CONSTRAINT scheduler_run_stats_pk;
        
        -- Check constraints to drop
        
        ALTER TABLE audit_record DROP CONSTRAINT audit_record_app_e_ck;
        ALTER TABLE audit_record DROP CONSTRAINT audit_record_event_e_ck;
        ALTER TABLE scheduler_trigger DROP CONSTRAINT scheduler_trigger_trigger_type_e_ck;
        ALTER TABLE vista_terms DROP CONSTRAINT vista_terms_target_e_ck;
       
        
        /**
        ***     =======================================================================================================
        ***
        ***             NEW AND ALTERED TABLES 
        ***
        ***     =======================================================================================================
        **/
        
        /** admin_user tables renamed **/
        
        ALTER TABLE admin_user RENAME TO operations_user;
        ALTER TABLE admin_user_credential RENAME TO operations_user_credential;
        ALTER TABLE admin_user_credential$behaviors RENAME TO operations_user_credential$behaviors; 
        
        -- admin_onboarding_merchant_account
        
        ALTER TABLE admin_onboarding_merchant_account   DROP COLUMN account_number,
                                                        DROP COLUMN bank_id,
                                                        DROP COLUMN branch_transit_number,
                                                        DROP COLUMN charge_description,
                                                        DROP COLUMN onboarding_bank_account_id;
                                                        
        ALTER TABLE admin_onboarding_merchant_account RENAME TO admin_pmc_merchant_account_index;
        
        -- admin_pmc_equifax_info
        
        ALTER TABLE admin_pmc_equifax_info      ADD COLUMN limit_daily_reports INT,
                                                ADD COLUMN limit_daily_requests INT;
        
        -- admin_pmc_vista_features
        
        ALTER TABLE admin_pmc_vista_features ADD COLUMN tenant_sure_integration BOOLEAN;
        
        
        -- customer_credit_check_transaction
        
        ALTER TABLE customer_credit_check_transaction ADD COLUMN pmc BIGINT;
        ALTER TABLE customer_credit_check_transaction ALTER COLUMN pmc SET NOT NULL;
        
        -- default_equifax_limit
        
        CREATE TABLE default_equifax_limit
        (
                id                              BIGINT                  NOT NULL,
                daily_reports                   INT,
                daily_requests                  INT,
                        CONSTRAINT      default_equifax_limit_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE default_equifax_limit OWNER TO vista;
        
        /** simulation tables - not really needed for production **/
        
        -- dev_card_service_simulation_card
        
        CREATE TABLE dev_card_service_simulation_card
        (
                id                              BIGINT                  NOT NULL,
                merchant                        BIGINT,
                card_type                       VARCHAR(50),
                number                          VARCHAR(500),
                expiry_date                     DATE,
                balance                         NUMERIC(18,2),
                reserved                        NUMERIC(18,2),
                response_code                   VARCHAR(500),
                created                         TIMESTAMP WITHOUT TIME ZONE,
                updated                         TIMESTAMP WITHOUT TIME ZONE,
                        CONSTRAINT      dev_card_service_simulation_card_pk PRIMARY KEY(id)
        );
        
        -- dev_card_service_simulation_merchant_account
        
        CREATE TABLE dev_card_service_simulation_merchant_account
        (
                id                              BIGINT                  NOT NULL,
                terminal_id                     VARCHAR(500),
                balance                         NUMERIC(18,2),
                response_code                   VARCHAR(500),
                created                         TIMESTAMP WITHOUT TIME ZONE,
                        CONSTRAINT      dev_card_service_simulation_merchant_account_pk PRIMARY KEY(id)
        );
                
        
        -- dev_card_service_simulation_token
        
        CREATE TABLE dev_card_service_simulation_token
        (
                id                              BIGINT                  NOT NULL,
                card                            BIGINT                  NOT NULL,
                token                           VARCHAR(500),
                active                          BOOLEAN,
                odr                             INT,
                        CONSTRAINT      dev_card_service_simulation_token_pk PRIMARY KEY(id)
        );
        
        
        -- dev_card_service_simulation_transaction
        
        CREATE TABLE dev_card_service_simulation_transaction
        (
                id                              BIGINT                  NOT NULL,
                card                            BIGINT                  NOT NULL,
                transaction_type                VARCHAR(50),
                amount                          NUMERIC(18,2),
                reference                       VARCHAR(500),
                response_code                   VARCHAR(500),
                authorization_number            VARCHAR(500),
                transaction_date                TIMESTAMP WITHOUT TIME ZONE,
                scheduled_simulated_responce    BOOLEAN,
                        CONSTRAINT      dev_card_service_simulation_transaction_pk PRIMARY KEY(id)
        );
        
        -- dev_card_service_simulator_config
        
        CREATE TABLE dev_card_service_simulator_config
        (
                id                              BIGINT                  NOT NULL,
                response_type                   VARCHAR(50),
                response_code                   VARCHAR(500),
                response_http_code              INT,
                response_text                   VARCHAR(500),
                delay                           INT,
                        CONSTRAINT      dev_card_service_simulator_config_pk PRIMARY KEY(id)
        );
        
        
        -- dev_equifax_simulator_config
        
        CREATE TABLE dev_equifax_simulator_config
        (
                id                              BIGINT                  NOT NULL,
                approve_xml                     VARCHAR(300000),
                decline_xml                     VARCHAR(300000),
                more_info_xml                   VARCHAR(300000),
                        CONSTRAINT      dev_equifax_simulator_config_pk PRIMARY KEY(id)
        );
        
        /** simulation done **/
                
        
        -- encrypted_storage_public_key
        
        CREATE TABLE encrypted_storage_public_key
        (
                id                              BIGINT                          NOT NULL,
                created                         TIMESTAMP WITHOUT TIME ZONE     NOT NULL,
                expired                         TIMESTAMP WITHOUT TIME ZONE,
                name                            VARCHAR(500),
                algorithms_version              INT,
                key_data                        BYTEA,
                key_test_data                   BYTEA,
                encrypt_test_data               BYTEA,
                        CONSTRAINT      encrypted_storage_public_key_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE encrypted_storage_public_key OWNER TO vista;
        
        
        -- encrypted_storage_current_key
        
        CREATE TABLE encrypted_storage_current_key
        (
                id                              BIGINT                          NOT NULL,
                current                         BIGINT,
                        CONSTRAINT      encrypted_storage_current_key_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE encrypted_storage_current_key OWNER TO vista;
                
        
        -- legal_document
        
        -- ALTER TABLE legal_document ALTER COLUMN content TYPE VARCHAR(300000);
        
        -- scheduler_run_stats
        
        ALTER TABLE scheduler_run_stats RENAME TO scheduler_execution_report;
        
        ALTER TABLE scheduler_execution_report  ADD COLUMN erred BIGINT,
                                                ADD COLUMN amount_erred DOUBLE PRECISION;
        
        
        -- scheduler_execution_report_section
        
        CREATE TABLE scheduler_execution_report_section
        (
                id                              BIGINT                  NOT NULL,
                execution_report                BIGINT,
                name                            VARCHAR(500),
                tp                              VARCHAR(50),
                counter                         BIGINT,
                value                           NUMERIC(18,2),
                        CONSTRAINT      scheduler_execution_report_section_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE scheduler_execution_report_section OWNER TO vista;
                                        
        -- scheduler_execution_report_message
        
        CREATE TABLE scheduler_execution_report_message
        (
                id                              BIGINT                  NOT NULL,
                execution_report_section        BIGINT,
                event_time                      TIMESTAMP,
                message                         VARCHAR(4000),
                        CONSTRAINT      scheduler_execution_report_message_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE scheduler_execution_report_message OWNER TO vista;
        
        -- scheduler_run
        
        ALTER TABLE scheduler_run RENAME COLUMN stats TO execution_report;
        
        -- scheduler_run_data
        
        ALTER TABLE scheduler_run_data RENAME COLUMN stats TO execution_report;
        
        -- tenant_sure_hqupdate_file
        
        CREATE TABLE tenant_sure_hqupdate_file
        (
                id                              BIGINT                  NOT NULL,
                file_name                       VARCHAR(500),
                received                        TIMESTAMP WITHOUT TIME ZONE,
                        CONSTRAINT      tenant_sure_hqupdate_file_pk PRIMARY KEY(id)
        );
        
        
        -- tenant_sure_hqupdate_record
        
        CREATE TABLE tenant_sure_hqupdate_record
        (
                id                              BIGINT                  NOT NULL,
                file                            BIGINT                  NOT NULL,
                certificate_number              VARCHAR(500),
                status                          VARCHAR(50),
                consumed                        BOOLEAN,
                        CONSTRAINT      tenant_sure_hqupdate_record_pk PRIMARY KEY(id)
        );
        
        
        -- tenant_sure_subscribers
        
        CREATE TABLE tenant_sure_subscribers
        (
                id                              BIGINT                  NOT NULL,
                pmc                             BIGINT,
                certificate_number              VARCHAR(500),
                        CONSTRAINT      tenant_sure_subscribers_pk PRIMARY KEY(id)
        );
                
        
        
        /**
        ***     ========================================================================================================
        ***
        ***             CREATE CONSTRAINTS
        ***
        ***     ========================================================================================================
        **/
        
        -- Primary kesy to create
        ALTER TABLE operations_user_credential$behaviors ADD CONSTRAINT operations_user_credential$behaviors_pk PRIMARY KEY(id);
        ALTER TABLE operations_user_credential ADD CONSTRAINT operations_user_credential_pk PRIMARY KEY(id);
        ALTER TABLE operations_user ADD CONSTRAINT operations_user_pk PRIMARY KEY(id);
        ALTER TABLE admin_pmc_merchant_account_index ADD CONSTRAINT admin_pmc_merchant_account_index_pk PRIMARY KEY(id);
        ALTER TABLE scheduler_execution_report ADD CONSTRAINT scheduler_execution_report_pk PRIMARY KEY(id);

        
        -- Foreign keys to create
        ALTER TABLE customer_credit_check_transaction ADD CONSTRAINT customer_credit_check_transaction_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id);
        ALTER TABLE dev_card_service_simulation_card ADD CONSTRAINT dev_card_service_simulation_card_merchant_fk FOREIGN KEY(merchant) 
                REFERENCES dev_card_service_simulation_merchant_account(id);
        ALTER TABLE dev_card_service_simulation_token ADD CONSTRAINT dev_card_service_simulation_token_card_fk FOREIGN KEY(card) 
                REFERENCES dev_card_service_simulation_card(id);
        ALTER TABLE dev_card_service_simulation_transaction ADD CONSTRAINT dev_card_service_simulation_transaction_card_fk FOREIGN KEY(card) 
                REFERENCES dev_card_service_simulation_card(id);
        ALTER TABLE encrypted_storage_current_key ADD CONSTRAINT encrypted_storage_current_key_current_fk FOREIGN KEY(current) REFERENCES encrypted_storage_public_key(id);
        ALTER TABLE operations_user_credential$behaviors ADD CONSTRAINT operations_user_credential$behaviors_owner_fk FOREIGN KEY(owner) 
                REFERENCES operations_user_credential(id);
        ALTER TABLE operations_user_credential ADD CONSTRAINT operations_user_credential_usr_fk FOREIGN KEY(usr) REFERENCES operations_user(id);
        ALTER TABLE scheduler_execution_report_message ADD CONSTRAINT scheduler_execution_report_message_execution_report_section_fk FOREIGN KEY(execution_report_section) 
                REFERENCES scheduler_execution_report_section(id);
        ALTER TABLE scheduler_execution_report_section ADD CONSTRAINT scheduler_execution_report_section_execution_report_fk FOREIGN KEY(execution_report) 
                REFERENCES scheduler_execution_report(id);
        ALTER TABLE scheduler_run_data ADD CONSTRAINT scheduler_run_data_execution_report_fk FOREIGN KEY(execution_report) 
                REFERENCES scheduler_execution_report(id);
        ALTER TABLE scheduler_run ADD CONSTRAINT scheduler_run_execution_report_fk FOREIGN KEY(execution_report) REFERENCES scheduler_execution_report(id);
        ALTER TABLE scheduler_trigger_notification ADD CONSTRAINT scheduler_trigger_notification_usr_fk FOREIGN KEY(usr) REFERENCES operations_user(id);
        ALTER TABLE tenant_sure_hqupdate_record ADD CONSTRAINT tenant_sure_hqupdate_record_file_fk FOREIGN KEY(file) REFERENCES tenant_sure_hqupdate_file(id);
        ALTER TABLE tenant_sure_subscribers ADD CONSTRAINT tenant_sure_subscribers_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id);
        ALTER TABLE admin_pmc_merchant_account_index ADD CONSTRAINT admin_pmc_merchant_account_index_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id);
        ALTER TABLE pad_reconciliation_summary ADD CONSTRAINT pad_reconciliation_summary_merchant_account_fk FOREIGN KEY(merchant_account) 
                REFERENCES admin_pmc_merchant_account_index(id);


                
        -- Check constraint to create
        
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_app_e_ck CHECK ((app) IN ('crm', 'operations', 'prospect', 'resident'));
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_event_e_ck 
                CHECK ((event) IN ('Create', 'CredentialUpdate', 'EquifaxReadReport', 'EquifaxRequest', 'Info', 'Login', 
                'LoginFailed', 'PermitionsUpdate', 'Read', 'System', 'Update'));
        ALTER TABLE dev_card_service_simulation_card ADD CONSTRAINT dev_card_service_simulation_card_card_type_e_ck CHECK ((card_type) IN ('MasterCard', 'Visa'));
        ALTER TABLE dev_card_service_simulation_transaction ADD CONSTRAINT dev_card_service_simulation_transaction_transaction_type_e_ck 
                CHECK ((transaction_type) IN ('completion', 'preAuthorization', 'preAuthorizationReversal', 'sale'));
        ALTER TABLE dev_card_service_simulator_config ADD CONSTRAINT dev_card_service_simulator_config_response_type_e_ck 
                CHECK ((response_type) IN ('DropConnection', 'RespondWithCode', 'RespondWithHttpCode', 'RespondWithText', 'SimulateTransations'));
        ALTER TABLE scheduler_execution_report_section ADD CONSTRAINT scheduler_execution_report_section_tp_e_ck CHECK ((tp) IN ('erred', 'failed', 'processed'));
        ALTER TABLE scheduler_trigger ADD CONSTRAINT scheduler_trigger_trigger_type_e_ck 
                CHECK ((trigger_type) IN ('billing', 'cleanup', 'equifaxRetention', 'initializeFutureBillingCycles', 'leaseActivation', 'leaseCompletion', 
                'leaseRenewal', 'paymentsBmoRecive', 'paymentsIssue', 'paymentsPadReciveAcknowledgment', 'paymentsPadReciveReconciliation', 
                'paymentsPadSend', 'paymentsScheduledCreditCards', 'paymentsScheduledEcheck', 'paymentsTenantSure', 'tenantSureCancellation', 
                'tenantSureHQUpdate', 'tenantSureReports', 'tenantSureTransactionReports', 'test', 'updateArrears', 'updatePaymentsSummary', 'vistaBusinessReport', 
                'yardiBatchProcess', 'yardiImportProcess'));
        ALTER TABLE tenant_sure_hqupdate_record ADD CONSTRAINT tenant_sure_hqupdate_record_status_e_ck CHECK (status = 'Cancel');
        ALTER TABLE vista_terms ADD CONSTRAINT vista_terms_target_e_ck 
                CHECK ((target) IN ('PMC', 'PmcCaldedonSolePropetorshipSection', 'PmcCaledonTemplate', 'PmcPaymentPad', 
                'Tenant', 'TenantSurePreAuthorizedPaymentsAgreement'));


        /**
        ***     ============================================================================================================
        ***     
        ***             INDEXES
        ***
        ***     ============================================================================================================
        **/
        
        -- Drop indexes
        DROP INDEX admin_user_credential$behaviors_owner_idx;
        DROP INDEX admin_user_name_idx;
        DROP INDEX admin_user_email_idx;
        DROP INDEX admin_onboarding_merchant_account_merchant_terminal_id_idx;
        
        CREATE UNIQUE INDEX admin_pmc_merchant_account_index_merchant_terminal_id_idx ON admin_pmc_merchant_account_index USING btree (merchant_terminal_id);
        CREATE INDEX customer_credit_check_transaction_pmc_idx ON customer_credit_check_transaction USING btree (pmc);
        CREATE INDEX operations_user_credential$behaviors_owner_idx ON operations_user_credential$behaviors USING btree (owner);
        CREATE INDEX operations_user_name_idx ON operations_user USING btree (name);
        CREATE UNIQUE INDEX operations_user_email_idx ON operations_user USING btree (LOWER(email));
        CREATE UNIQUE INDEX dev_card_service_simulation_merchant_account_terminal_id_idx ON dev_card_service_simulation_merchant_account USING btree (terminal_id);
        CREATE UNIQUE INDEX dev_card_service_simulation_token_token_idx ON dev_card_service_simulation_token USING btree (token);
        CREATE INDEX scheduler_execution_report_message_execution_report_section_idx ON scheduler_execution_report_message USING btree (execution_report_section);
        CREATE UNIQUE INDEX scheduler_execution_report_section_execution_report_name_tp_idx ON scheduler_execution_report_section USING btree (execution_report, name, tp);
        CREATE INDEX tenant_sure_hqupdate_record_file_idx ON tenant_sure_hqupdate_record USING btree (file);
        CREATE INDEX tenant_sure_subscribers_certificate_number_idx ON tenant_sure_subscribers USING btree (certificate_number);
        
        
        /**
        ***     ============================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     ============================================================================================================
        **/
        
        UPDATE admin_pmc_vista_features 
        SET  tenant_sure_integration = FALSE ;
        

COMMIT;

SET client_min_messages = 'notice';
