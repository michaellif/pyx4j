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

--BEGIN TRANSACTION;

SET search_path = '_admin_';

        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP CONSTRAINTS SECTION
        ***
        ***     ======================================================================================================
        **/
        
        -- Foreign keys to drop
        ALTER TABLE admin_user_credential$behaviors DROP CONSTRAINT admin_user_credential$behaviors_owner_fk;
        ALTER TABLE admin_user_credential DROP CONSTRAINT admin_user_credential_usr_fk;
        ALTER TABLE scheduler_trigger_notification DROP CONSTRAINT scheduler_trigger_notification_usr_fk;
        
        -- Primary keys to drop
        
        ALTER TABLE admin_user_credential$behaviors DROP CONSTRAINT admin_user_credential$behaviors_pk;
        ALTER TABLE admin_user_credential DROP CONSTRAINT admin_user_credential_pk;
        ALTER TABLE admin_user DROP CONSTRAINT admin_user_pk;
        
        -- Check constraints to drop
        
        ALTER TABLE audit_record DROP CONSTRAINT audit_record_app_e_ck;
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
        
        -- admin_pmc_vista_features
        
        ALTER TABLE admin_pmc_vista_features ADD COLUMN tenant_sure_integration BOOLEAN;
        
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
                
        
        -- legal_document
        
        -- ALTER TABLE legal_document ALTER COLUMN content TYPE VARCHAR(300000);
        
        -- scheduler_run_stats
        
        ALTER TABLE scheduler_run_stats ADD COLUMN erred BIGINT,
                                        ADD COLUMN amount_erred DOUBLE PRECISION;
        
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

        
        -- Foreign keys to create
        ALTER TABLE dev_card_service_simulation_card ADD CONSTRAINT dev_card_service_simulation_card_merchant_fk FOREIGN KEY(merchant) 
                REFERENCES dev_card_service_simulation_merchant_account(id);
        ALTER TABLE dev_card_service_simulation_token ADD CONSTRAINT dev_card_service_simulation_token_card_fk FOREIGN KEY(card) 
                REFERENCES dev_card_service_simulation_card(id);
        ALTER TABLE dev_card_service_simulation_transaction ADD CONSTRAINT dev_card_service_simulation_transaction_card_fk FOREIGN KEY(card) 
                REFERENCES dev_card_service_simulation_card(id);
        ALTER TABLE operations_user_credential$behaviors ADD CONSTRAINT operations_user_credential$behaviors_owner_fk FOREIGN KEY(owner) 
                REFERENCES operations_user_credential(id);
        ALTER TABLE operations_user_credential ADD CONSTRAINT operations_user_credential_usr_fk FOREIGN KEY(usr) REFERENCES operations_user(id);
        ALTER TABLE scheduler_trigger_notification ADD CONSTRAINT scheduler_trigger_notification_usr_fk FOREIGN KEY(usr) REFERENCES operations_user(id);
        ALTER TABLE tenant_sure_hqupdate_record ADD CONSTRAINT tenant_sure_hqupdate_record_file_fk FOREIGN KEY(file) REFERENCES tenant_sure_hqupdate_file(id);
        ALTER TABLE tenant_sure_subscribers ADD CONSTRAINT tenant_sure_subscribers_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id);

                
        -- Check constraint to create
        
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_app_e_ck CHECK ((app) IN ('crm', 'operations', 'prospect', 'resident'));
        ALTER TABLE dev_card_service_simulation_card ADD CONSTRAINT dev_card_service_simulation_card_card_type_e_ck CHECK ((card_type) IN ('MasterCard', 'Visa'));
        ALTER TABLE dev_card_service_simulation_transaction ADD CONSTRAINT dev_card_service_simulation_transaction_transaction_type_e_ck 
                CHECK ((transaction_type) IN ('completion', 'preAuthorization', 'preAuthorizationReversal', 'sale'));
        ALTER TABLE dev_card_service_simulator_config ADD CONSTRAINT dev_card_service_simulator_config_response_type_e_ck 
                CHECK ((response_type) IN ('DropConnection', 'RespondWithCode', 'RespondWithHttpCode', 'RespondWithText', 'SimulateTransations'));
        ALTER TABLE scheduler_trigger ADD CONSTRAINT scheduler_trigger_trigger_type_e_ck 
                CHECK ((trigger_type) IN ('billing', 'cleanup', 'equifaxRetention', 'initializeFutureBillingCycles', 'leaseActivation', 'leaseCompletion', 
                'leaseRenewal', 'paymentsBmoRecive', 'paymentsIssue', 'paymentsPadReciveAcknowledgment', 'paymentsPadReciveReconciliation', 
                'paymentsPadSend', 'paymentsScheduledCreditCards', 'paymentsScheduledEcheck', 'paymentsTenantSure', 'tenantSureCancellation', 
                'tenantSureHQUpdate', 'tenantSureReports', 'test', 'updateArrears', 'updatePaymentsSummary', 'vistaBusinessReport', 
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
        
        CREATE INDEX operations_user_credential$behaviors_owner_idx ON operations_user_credential$behaviors USING btree (owner);
        CREATE INDEX operations_user_name_idx ON operations_user USING btree (name);
        CREATE UNIQUE INDEX operations_user_email_idx ON operations_user USING btree (LOWER(email));
        CREATE UNIQUE INDEX dev_card_service_simulation_merchant_account_terminal_id_idx ON dev_card_service_simulation_merchant_account USING btree (terminal_id);
        CREATE UNIQUE INDEX dev_card_service_simulation_token_token_idx ON dev_card_service_simulation_token USING btree (token);
        CREATE INDEX tenant_sure_hqupdate_record_file_idx ON tenant_sure_hqupdate_record USING btree (file);
        CREATE INDEX tenant_sure_subscribers_certificate_number_idx ON tenant_sure_subscribers USING btree (certificate_number);
        
        
        /**
        ***     ============================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     ============================================================================================================
        **/
        
        UPDATE  admin_pmc_vista_features
        SET     tenant_sure_integration = TRUE;

--COMMIT;

SET client_min_messages = 'notice';
