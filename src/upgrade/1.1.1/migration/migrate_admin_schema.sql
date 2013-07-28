/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             _admin_ schema changes for v. 1.1.1
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
        
        ALTER TABLE pad_sim_batch DROP CONSTRAINT pad_sim_batch_pad_file_fk;
        ALTER TABLE pad_sim_debit_record DROP CONSTRAINT pad_sim_debit_record_pad_batch_fk;
        ALTER TABLE pad_sim_file$state DROP CONSTRAINT pad_sim_file$state_owner_fk;
        ALTER TABLE pad_sim_file DROP CONSTRAINT pad_sim_file_original_file_fk;

        -- primary keys
        
        ALTER TABLE pad_sim_batch DROP CONSTRAINT pad_sim_batch_pk;
        ALTER TABLE pad_sim_debit_record DROP CONSTRAINT pad_sim_debit_record_pk;
        ALTER TABLE pad_sim_file$state DROP CONSTRAINT pad_sim_file$state_pk;
        ALTER TABLE pad_sim_file DROP CONSTRAINT pad_sim_file_pk;
        
        
        -- check constraints
        
        ALTER TABLE admin_pmc_dns_name DROP CONSTRAINT admin_pmc_dns_name_target_e_ck;
        ALTER TABLE audit_record DROP CONSTRAINT audit_record_app_e_ck;
        ALTER TABLE audit_record DROP CONSTRAINT audit_record_event_e_ck;
        ALTER TABLE pad_sim_batch DROP CONSTRAINT pad_sim_batch_reconciliation_status_e_ck;
        ALTER TABLE pad_sim_debit_record DROP CONSTRAINT pad_sim_debit_record_reconciliation_status_e_ck;
        ALTER TABLE operations_alert DROP CONSTRAINT operations_alert_app_e_ck;

        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP INDEXES 
        ***
        ***     ======================================================================================================
        **/
        
        DROP INDEX onboarding_user_email_idx;
        DROP INDEX pad_sim_batch_pad_file_idx;
        DROP INDEX pad_sim_file$state_owner_idx;
        
        /**
        ***     =======================================================================================================
        ***
        ***             RENAMED TABLES 
        ***
        ***     =======================================================================================================
        **/
        
        -- pad_sim_batch
        
        ALTER TABLE pad_sim_batch RENAME TO dev_pad_sim_batch;
        
        -- pad_sim_debit_record
        
        ALTER TABLE pad_sim_debit_record RENAME TO dev_pad_sim_debit_record;
        
        
        -- pad_sim_file$state 
        
        ALTER TABLE pad_sim_file$state RENAME TO dev_pad_sim_file$state;
        
       
        -- pad_sim_file
        
        ALTER TABLE pad_sim_file RENAME TO dev_pad_sim_file;
        
        
        /**
        ***     =======================================================================================================
        ***
        ***             NEW AND ALTERED TABLES 
        ***
        ***     =======================================================================================================
        **/
        
        -- admin_pmc_yardi_credential
        
        ALTER TABLE admin_pmc_yardi_credential ADD COLUMN password_encrypted VARCHAR(1024);
        ALTER TABLE admin_pmc_yardi_credential RENAME COLUMN credential TO password_password;
        
        
        -- audit_record
        
        ALTER TABLE audit_record        ADD COLUMN pmc BIGINT,
                                        ADD COLUMN user_type VARCHAR(50),
                                        ADD COLUMN session_id VARCHAR(500),
                                        ADD COLUMN world_time TIMESTAMP;
        
        -- dev_card_service_simulation_merchant_account
        
        ALTER TABLE dev_card_service_simulation_merchant_account ALTER COLUMN terminal_id TYPE VARCHAR(8);
        
        
        -- dev_card_service_simulation_token
        
        ALTER TABLE dev_card_service_simulation_token ALTER COLUMN token TYPE VARCHAR(30);
        
        
        -- dev_card_service_simulation_transaction
        
        ALTER TABLE dev_card_service_simulation_transaction ALTER COLUMN  reference TYPE VARCHAR(60);
        
        -- dev_direct_debit_sim_file
        
        CREATE TABLE dev_direct_debit_sim_file
        (
                id                              BIGINT                  NOT NULL,
                creatation_date                 TIMESTAMP,
                sent_date                       TIMESTAMP,
                status                          VARCHAR(50),
                        CONSTRAINT dev_direct_debit_sim_file_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE dev_direct_debit_sim_file OWNER TO vista;
        
        
        -- dev_direct_debit_sim_record
        
        CREATE TABLE dev_direct_debit_sim_record
        (
                id                              BIGINT                  NOT NULL,
                file                            BIGINT,
                account_number                  VARCHAR(14),
                amount                          NUMERIC(18,2),
                payment_reference_number        VARCHAR(30),
                customer_name                   VARCHAR(35),
                received_date                   TIMESTAMP,
                        CONSTRAINT dev_direct_debit_sim_record_pk PRIMARY KEY(id)        
        );
        
        ALTER TABLE dev_direct_debit_sim_record OWNER TO vista;
        
        
        -- dev_equifax_simulator_config
        
        ALTER TABLE dev_equifax_simulator_config        ADD COLUMN approve BIGINT,
                                                        ADD COLUMN decline BIGINT,
                                                        ADD COLUMN more_info BIGINT;
        
        -- dev_equifax_simulator_data
        
        CREATE TABLE dev_equifax_simulator_data
        (
                id                              BIGINT                  NOT NULL,
                xml                             VARCHAR(30000),
                        CONSTRAINT dev_equifax_simulator_data_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE dev_equifax_simulator_data OWNER TO vista;
        
        -- dev_pad_sim_file
        
        ALTER TABLE dev_pad_sim_file ADD COLUMN funds_transfer_type VARCHAR(50);
        
        -- development_user
        
        ALTER TABLE development_user ALTER COLUMN email TYPE VARCHAR(64);
        ALTER TABLE development_user ALTER COLUMN host1 TYPE VARCHAR(128);
        ALTER TABLE development_user ALTER COLUMN host2 TYPE VARCHAR(128);
        ALTER TABLE development_user ALTER COLUMN host3 TYPE VARCHAR(128);
        
        
        -- direct_debit_record
        
        CREATE TABLE direct_debit_record
        (
                id                              BIGINT                  NOT NULL,
                account_number                  VARCHAR(14),
                amount                          NUMERIC(18,2),
                payment_reference_number        VARCHAR(30),
                customer_name                   VARCHAR(35),
                received_date                   TIMESTAMP,
                processing_status               VARCHAR(50),
                trace_file_serial_number        VARCHAR(500),
                trace_file_serial_date          VARCHAR(500),
                trace_location_code             VARCHAR(500),
                trace_collection_date           VARCHAR(500),
                trace_source_code               VARCHAR(500),
                trace_trace_number              VARCHAR(500),
                        CONSTRAINT direct_debit_record_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE direct_debit_record OWNER TO vista;
                                    
        -- global_crm_user_index
        
        CREATE TABLE global_crm_user_index
        (
                id                      BIGINT                  NOT NULL,
                pmc                     BIGINT,
                crm_user                BIGINT,
                email                   VARCHAR(64),
                        CONSTRAINT global_crm_user_index_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE global_crm_user_index OWNER TO vista;
        
        
        -- onboarding_user
        
        ALTER TABLE onboarding_user     ADD COLUMN pmc BIGINT,
                                        ADD COLUMN password VARCHAR(500);                              
        
                
        --  pad_file
        
        ALTER TABLE  pad_file ADD COLUMN funds_transfer_type VARCHAR(50);
        
        
        -- pad_reconciliation_file
        
        ALTER TABLE pad_reconciliation_file ADD COLUMN funds_transfer_type VARCHAR(50);
        
        
        -- pmc_document_file
        
        ALTER TABLE pmc_document_file   ADD COLUMN caption VARCHAR(500),
                                        ADD COLUMN description VARCHAR(500);
       
       -- scheduler_execution_report_section
       
       ALTER TABLE scheduler_execution_report_section ALTER COLUMN name TYPE VARCHAR(120);
       
        /**
        ***     ============================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     ============================================================================================================
        **/
        
        -- onboarding_user
        
        UPDATE  onboarding_user AS u
        SET     pmc = c.pmc
        FROM    onboarding_user_credential c
        WHERE   c.usr = u.id;
        
        
        -- pad_file
        
        UPDATE  pad_file
        SET     funds_transfer_type = 'PreAuthorizedDebit';
        
        
        -- pad_reconciliation_file
        
        UPDATE  pad_reconciliation_file
        SET     funds_transfer_type = 'PreAuthorizedDebit';
               
        
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        -- admin_pmc
        
        ALTER TABLE admin_pmc DROP COLUMN onboarding_account_id;
        
        -- admin_pmc_vista_features
        
        ALTER TABLE admin_pmc_vista_features DROP COLUMN xml_site_export;
        
        -- onboarding_user
        
        ALTER TABLE onboarding_user     DROP COLUMN name,
                                        DROP COLUMN updated;
                                        
        -- onboarding_user_credential
        
        DROP TABLE onboarding_user_credential;
       
        
        /**
        ***     ========================================================================================================
        ***
        ***             CREATE CONSTRAINTS
        ***
        ***     ========================================================================================================
        **/
        
        -- primary keys
        
        ALTER TABLE dev_pad_sim_batch ADD CONSTRAINT dev_pad_sim_batch_pk PRIMARY KEY(id);
        ALTER TABLE dev_pad_sim_debit_record ADD CONSTRAINT dev_pad_sim_debit_record_pk PRIMARY KEY(id);
        ALTER TABLE dev_pad_sim_file$state ADD CONSTRAINT dev_pad_sim_file$state_pk PRIMARY KEY(id);
        ALTER TABLE dev_pad_sim_file ADD CONSTRAINT dev_pad_sim_file_pk PRIMARY KEY(id);

        
        -- foreign keys
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE dev_direct_debit_sim_record ADD CONSTRAINT dev_direct_debit_sim_record_file_fk FOREIGN KEY(file) 
                REFERENCES dev_direct_debit_sim_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE dev_equifax_simulator_config ADD CONSTRAINT dev_equifax_simulator_config_approve_fk FOREIGN KEY(approve) 
                REFERENCES dev_equifax_simulator_data(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE dev_equifax_simulator_config ADD CONSTRAINT dev_equifax_simulator_config_decline_fk FOREIGN KEY(decline) 
                REFERENCES dev_equifax_simulator_data(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE dev_equifax_simulator_config ADD CONSTRAINT dev_equifax_simulator_config_more_info_fk FOREIGN KEY(more_info) 
                REFERENCES dev_equifax_simulator_data(id)  DEFERRABLE INITIALLY DEFERRED;  
        ALTER TABLE global_crm_user_index ADD CONSTRAINT global_crm_user_index_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE onboarding_user ADD CONSTRAINT onboarding_user_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE dev_pad_sim_batch ADD CONSTRAINT dev_pad_sim_batch_pad_file_fk FOREIGN KEY(pad_file) 
                REFERENCES dev_pad_sim_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE dev_pad_sim_debit_record ADD CONSTRAINT dev_pad_sim_debit_record_pad_batch_fk FOREIGN KEY(pad_batch) 
                REFERENCES dev_pad_sim_batch(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE dev_pad_sim_file$state ADD CONSTRAINT dev_pad_sim_file$state_owner_fk FOREIGN KEY(owner) 
                REFERENCES dev_pad_sim_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE dev_pad_sim_file ADD CONSTRAINT dev_pad_sim_file_original_file_fk FOREIGN KEY(original_file) 
                REFERENCES dev_pad_sim_file(id)  DEFERRABLE INITIALLY DEFERRED;


        -- check constraints
        
        ALTER TABLE admin_pmc_dns_name ADD CONSTRAINT admin_pmc_dns_name_target_e_ck 
                CHECK ((target) IN ('field', 'prospectPortal', 'resident', 'residentPortal', 'vistaCrm'));
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_app_e_ck 
                CHECK ((app) IN ('crm', 'field', 'onboarding', 'operations', 'prospect', 'resident', 'residentPortal'));
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_event_e_ck 
                CHECK ((event) IN ('Create', 'CredentialUpdate', 'EquifaxReadReport', 'EquifaxRequest', 'Info', 'Login', 
                'LoginFailed', 'Logout', 'PermitionsUpdate', 'Read', 'SessionExpiration', 'System', 'Update'));
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_user_type_e_ck CHECK ((user_type) IN ('crm', 'customer', 'operations'));
        ALTER TABLE dev_direct_debit_sim_file ADD CONSTRAINT dev_direct_debit_sim_file_status_e_ck CHECK ((status) IN ('New', 'Sent'));
        ALTER TABLE dev_pad_sim_batch ADD CONSTRAINT dev_pad_sim_batch_reconciliation_status_e_ck CHECK ((reconciliation_status) IN ('HOLD', 'PAID'));
        ALTER TABLE dev_pad_sim_debit_record ADD CONSTRAINT dev_pad_sim_debit_record_reconciliation_status_e_ck 
                CHECK ((reconciliation_status) IN ('DUPLICATE', 'PROCESSED', 'REJECTED', 'RETURNED'));
        ALTER TABLE dev_pad_sim_file ADD CONSTRAINT dev_pad_sim_file_funds_transfer_type_e_ck 
                CHECK ((funds_transfer_type) IN ('DirectBankingPayment', 'InteracOnlinePayment', 'PreAuthorizedDebit'));
        ALTER TABLE direct_debit_record ADD CONSTRAINT direct_debit_record_processing_status_e_ck CHECK ((processing_status) IN ('Invalid', 'Procesed', 'Received'));
        ALTER TABLE operations_alert ADD CONSTRAINT operations_alert_app_e_ck 
                CHECK ((app) IN ('crm', 'field', 'onboarding', 'operations', 'prospect', 'resident', 'residentPortal'));
        ALTER TABLE pad_file ADD CONSTRAINT pad_file_funds_transfer_type_e_ck 
                CHECK ((funds_transfer_type) IN ('DirectBankingPayment', 'InteracOnlinePayment', 'PreAuthorizedDebit'));
        ALTER TABLE pad_reconciliation_file ADD CONSTRAINT pad_reconciliation_file_funds_transfer_type_e_ck 
                CHECK ((funds_transfer_type) IN ('DirectBankingPayment', 'InteracOnlinePayment', 'PreAuthorizedDebit'));
        

                
        /**
        ***     ============================================================================================================
        ***     
        ***             CREATE INDEXES
        ***
        ***     ============================================================================================================
        **/
        
        CREATE INDEX dev_direct_debit_sim_record_account_number_idx ON dev_direct_debit_sim_record USING btree (account_number);
        CREATE INDEX dev_direct_debit_sim_record_file_idx ON dev_direct_debit_sim_record USING btree (file);
        CREATE INDEX dev_pad_sim_batch_pad_file_idx ON dev_pad_sim_batch USING btree (pad_file);
        CREATE INDEX dev_pad_sim_debit_record_pad_batch_idx ON dev_pad_sim_debit_record USING btree (pad_batch);
        CREATE INDEX "dev_pad_sim_file$state_owner_idx" ON "dev_pad_sim_file$state" USING btree (owner);
        CREATE INDEX direct_debit_record_account_number_idx ON direct_debit_record USING btree (account_number);
        CREATE INDEX global_crm_user_index_email_idx ON global_crm_user_index USING btree (email);
        CREATE INDEX global_crm_user_index_pmc_crm_user_idx ON global_crm_user_index USING btree (pmc, crm_user);
        CREATE INDEX onboarding_user_email_idx ON onboarding_user USING btree (lower(email));

       


COMMIT;

SET client_min_messages = 'notice';
