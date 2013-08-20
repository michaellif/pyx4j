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
        ALTER TABLE scheduler_trigger DROP CONSTRAINT scheduler_trigger_trigger_details_fk;

        -- primary keys
        
        ALTER TABLE pad_sim_batch DROP CONSTRAINT pad_sim_batch_pk;
        ALTER TABLE pad_sim_debit_record DROP CONSTRAINT pad_sim_debit_record_pk;
        ALTER TABLE pad_sim_file$state DROP CONSTRAINT pad_sim_file$state_pk;
        ALTER TABLE pad_sim_file DROP CONSTRAINT pad_sim_file_pk;
        
        
        -- check constraints
        
        ALTER TABLE admin_pmc_dns_name DROP CONSTRAINT admin_pmc_dns_name_target_e_ck;
        ALTER TABLE admin_pmc_payment_method DROP CONSTRAINT admin_pmc_payment_method_details_discriminator_d_ck;
        ALTER TABLE admin_pmc_payment_method DROP CONSTRAINT admin_pmc_payment_method_payment_type_e_ck;
        ALTER TABLE audit_record DROP CONSTRAINT audit_record_app_e_ck;
        ALTER TABLE audit_record DROP CONSTRAINT audit_record_event_e_ck;
        ALTER TABLE dev_card_service_simulation_card DROP CONSTRAINT dev_card_service_simulation_card_card_type_e_ck;
        ALTER TABLE pad_batch DROP CONSTRAINT pad_batch_processing_status_e_ck;
        ALTER TABLE pad_debit_record DROP CONSTRAINT pad_debit_record_processing_status_e_ck;
        ALTER TABLE pad_sim_batch DROP CONSTRAINT pad_sim_batch_reconciliation_status_e_ck;
        ALTER TABLE pad_sim_debit_record DROP CONSTRAINT pad_sim_debit_record_reconciliation_status_e_ck;
        ALTER TABLE payment_payment_details DROP CONSTRAINT payment_payment_details_card_type_e_ck;
        ALTER TABLE payment_payment_details DROP CONSTRAINT payment_payment_details_id_discriminator_ck;
        ALTER TABLE operations_alert DROP CONSTRAINT operations_alert_app_e_ck;
        ALTER TABLE scheduler_trigger_details DROP CONSTRAINT scheduler_trigger_details_id_discriminator_ck;
        ALTER TABLE scheduler_trigger DROP CONSTRAINT scheduler_trigger_trigger_details_discriminator_d_ck;
        ALTER TABLE scheduler_trigger DROP CONSTRAINT scheduler_trigger_trigger_type_e_ck;
        ALTER TABLE scheduler_trigger_notification DROP CONSTRAINT scheduler_trigger_notification_event_e_ck;

        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP INDEXES 
        ***
        ***     ======================================================================================================
        **/
        
        DROP INDEX admin_pmc_yardi_credential_pmc_idx;
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
        
        -- admin_pmc_payment_method
        
        ALTER TABLE admin_pmc_payment_method ADD COLUMN creation_date TIMESTAMP;
        
        -- admin_pmc_payment_type_info
        
        ALTER TABLE admin_pmc_payment_type_info ADD COLUMN direct_banking_fee NUMERIC(18,2),
                                                ADD COLUMN visa_debit_fee NUMERIC(18,4);
                                                
        ALTER TABLE admin_pmc_payment_type_info ALTER COLUMN cc_amex_fee TYPE NUMERIC(18,4);
        ALTER TABLE admin_pmc_payment_type_info ALTER COLUMN cc_discover_fee TYPE NUMERIC(18,4);
        ALTER TABLE admin_pmc_payment_type_info ALTER COLUMN cc_master_card_fee TYPE NUMERIC(18,4);
        ALTER TABLE admin_pmc_payment_type_info ALTER COLUMN cc_visa_fee TYPE NUMERIC(18,4);
        
        
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
                serial_number                   INT,
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
        
        -- dev_visa_debit_range
        
        CREATE TABLE dev_visa_debit_range
        (
                id                      BIGINT                          NOT NULL,
                range_start             BIGINT,
                range_end               BIGINT,
                        CONSTRAINT dev_visa_debit_range_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE dev_visa_debit_range OWNER TO vista;
        
        -- development_user
        
        ALTER TABLE development_user ALTER COLUMN email TYPE VARCHAR(64);
        ALTER TABLE development_user ALTER COLUMN host1 TYPE VARCHAR(128);
        ALTER TABLE development_user ALTER COLUMN host2 TYPE VARCHAR(128);
        ALTER TABLE development_user ALTER COLUMN host3 TYPE VARCHAR(128);
        
        
        -- direct_debit_file
        
        CREATE TABLE direct_debit_file
        (
                id                              BIGINT                  NOT NULL,
                file_name                       VARCHAR(500),
                created                         TIMESTAMP,
                file_serial_number              VARCHAR(500),
                file_serial_date                VARCHAR(500),
                        CONSTRAINT direct_debit_file_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE direct_debit_file OWNER TO vista;
        
        -- direct_debit_record
        
        CREATE TABLE direct_debit_record
        (
                id                              BIGINT                  NOT NULL,
                file                            BIGINT                  NOT NULL,
                account_number                  VARCHAR(14),
                pmc                             BIGINT,
                amount                          NUMERIC(18,2),
                payment_reference_number        VARCHAR(30),
                customer_name                   VARCHAR(35),
                received_date                   TIMESTAMP,
                processing_status               VARCHAR(50),
                trace_location_code             VARCHAR(500),
                trace_collection_date           VARCHAR(500),
                trace_source_code               VARCHAR(500),
                trace_trace_number              VARCHAR(500),
                        CONSTRAINT direct_debit_record_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE direct_debit_record OWNER TO vista;
        
        
        -- fee_default_payment_fees
        
        ALTER TABLE fee_default_payment_fees    ADD COLUMN direct_banking_fee NUMERIC(18,2),
                                                ADD COLUMN visa_debit_fee NUMERIC(18,4);
                                                
        ALTER TABLE fee_default_payment_fees ALTER COLUMN cc_amex_fee TYPE NUMERIC(18,4);
        ALTER TABLE fee_default_payment_fees ALTER COLUMN cc_discover_fee TYPE NUMERIC(18,4);
        ALTER TABLE fee_default_payment_fees ALTER COLUMN cc_master_card_fee TYPE NUMERIC(18,4);
        ALTER TABLE fee_default_payment_fees ALTER COLUMN cc_visa_fee TYPE NUMERIC(18,4);
                                    
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
        
        -- pad_debit_record_transaction
        
        CREATE TABLE pad_debit_record_transaction
        (
                id                      BIGINT                  NOT NULL,
                pad_debit_record        BIGINT                  NOT NULL,
                payment_record_key      BIGINT,
                fee_amount              NUMERIC(18,2),
                        CONSTRAINT pad_debit_record_transaction_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE pad_debit_record_transaction OWNER TO vista;
        
                
        --  pad_file
        
        ALTER TABLE  pad_file ADD COLUMN funds_transfer_type VARCHAR(50);
        
        
        -- pad_file_creation_number
        
        ALTER TABLE pad_file_creation_number ADD COLUMN funds_transfer_type VARCHAR(50);
        
        -- pad_reconciliation_file
        
        ALTER TABLE pad_reconciliation_file     ADD COLUMN funds_transfer_type VARCHAR(50),
                                                ADD COLUMN created TIMESTAMP;
        -- payment_payment_details
        
        ALTER TABLE payment_payment_details     ADD COLUMN location_code VARCHAR(500),
                                                ADD COLUMN trace_number VARCHAR(500);
        
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
        
        -- admin_pmc_payment_type_info
        
        DELETE FROM admin_pmc_payment_type_info;
        
        INSERT INTO admin_pmc_payment_type_info (id,pmc,direct_banking_fee)
        (SELECT nextval('public.admin_pmc_payment_type_info_seq') AS id,
                a.id AS pmc, 0.60
         FROM   admin_pmc a 
         JOIN   admin_pmc_vista_features f ON (a.features = f.id)
         WHERE  f.yardi_integration 
         AND    a.namespace NOT IN ('propertyvistatest','vic'));
        
        SET CONSTRAINTS _admin_.admin_pmc_payment_type_info_pmc_fk IMMEDIATE;
       
       
        -- audit_record
        
        UPDATE  audit_record AS r
        SET     pmc = a.id
        FROM    admin_pmc a
        WHERE   r.namespace = a.namespace;
        
        UPDATE  audit_record
        SET     user_type = 'operations'
        WHERE   namespace = '_admin_';
        
        
        -- dev_visa_debit_range
        
        INSERT INTO dev_visa_debit_range (id,range_start,range_end) VALUES
                (nextval('public.dev_visa_debit_range_seq'),400447000000,400447999999),
                (nextval('public.dev_visa_debit_range_seq'),402371000000,402371999999),
                (nextval('public.dev_visa_debit_range_seq'),402372000000,402372999999),
                (nextval('public.dev_visa_debit_range_seq'),402982000000,402982999999),
                (nextval('public.dev_visa_debit_range_seq'),417895000000,417895999999),
                (nextval('public.dev_visa_debit_range_seq'),420099002000,420099002999),
                (nextval('public.dev_visa_debit_range_seq'),421402000000,421402999999),
                (nextval('public.dev_visa_debit_range_seq'),421545000000,421545999999),
                (nextval('public.dev_visa_debit_range_seq'),421546000000,421546999999),
                (nextval('public.dev_visa_debit_range_seq'),421611000000,421611999999),
                (nextval('public.dev_visa_debit_range_seq'),421657000000,421657999999),
                (nextval('public.dev_visa_debit_range_seq'),421659000000,421659999999),
                (nextval('public.dev_visa_debit_range_seq'),422112000000,422112999999),
                (nextval('public.dev_visa_debit_range_seq'),423915000000,423915999999),
                (nextval('public.dev_visa_debit_range_seq'),424107000000,424107999999),
                (nextval('public.dev_visa_debit_range_seq'),428434000000,428434999999),
                (nextval('public.dev_visa_debit_range_seq'),428459000000,428459999999),
                (nextval('public.dev_visa_debit_range_seq'),428460000000,428460999999),
                (nextval('public.dev_visa_debit_range_seq'),432144000000,432144999999),
                (nextval('public.dev_visa_debit_range_seq'),437428000000,437428999999),
                (nextval('public.dev_visa_debit_range_seq'),437429000000,437429999999),
                (nextval('public.dev_visa_debit_range_seq'),440039000000,440039999999),
                (nextval('public.dev_visa_debit_range_seq'),450644000000,450644099999),
                (nextval('public.dev_visa_debit_range_seq'),450644100000,450644599999),
                (nextval('public.dev_visa_debit_range_seq'),450644600000,450644999999),
                (nextval('public.dev_visa_debit_range_seq'),451016000000,451016999999),
                (nextval('public.dev_visa_debit_range_seq'),451017000000,451017999999),
                (nextval('public.dev_visa_debit_range_seq'),451018000000,451018999999),
                (nextval('public.dev_visa_debit_range_seq'),451991000000,451991999999),
                (nextval('public.dev_visa_debit_range_seq'),451992000000,451992999999),
                (nextval('public.dev_visa_debit_range_seq'),451993000000,451993999999),
                (nextval('public.dev_visa_debit_range_seq'),452901000000,452901999999),
                (nextval('public.dev_visa_debit_range_seq'),453081000000,453081000999),
                (nextval('public.dev_visa_debit_range_seq'),453739000000,453739999999),
                (nextval('public.dev_visa_debit_range_seq'),453740000000,453740999999),
                (nextval('public.dev_visa_debit_range_seq'),453745000000,453745999999),
                (nextval('public.dev_visa_debit_range_seq'),453746000000,453746999999),
                (nextval('public.dev_visa_debit_range_seq'),457757000000,457757999999),
                (nextval('public.dev_visa_debit_range_seq'),457758000000,457758999999),
                (nextval('public.dev_visa_debit_range_seq'),457850000000,457850999999),
                (nextval('public.dev_visa_debit_range_seq'),457851000000,457851999999),
                (nextval('public.dev_visa_debit_range_seq'),458714000000,458714999999),
                (nextval('public.dev_visa_debit_range_seq'),460022000000,460022999999),
                (nextval('public.dev_visa_debit_range_seq'),461126000000,461126999999),
                (nextval('public.dev_visa_debit_range_seq'),461128000000,461128999999),
                (nextval('public.dev_visa_debit_range_seq'),461130000000,461130999999),
                (nextval('public.dev_visa_debit_range_seq'),461703000000,461703999999),
                (nextval('public.dev_visa_debit_range_seq'),461788000000,461788999999),
                (nextval('public.dev_visa_debit_range_seq'),467726000000,467726999999),
                (nextval('public.dev_visa_debit_range_seq'),469344000000,469344999999),
                (nextval('public.dev_visa_debit_range_seq'),469358000000,469358999999),
                (nextval('public.dev_visa_debit_range_seq'),470360000000,470360999999),
                (nextval('public.dev_visa_debit_range_seq'),470372000000,470372999999),
                (nextval('public.dev_visa_debit_range_seq'),471401000000,471401999999),
                (nextval('public.dev_visa_debit_range_seq'),471402000000,471402999999),
                (nextval('public.dev_visa_debit_range_seq'),471403000000,471403999999),
                (nextval('public.dev_visa_debit_range_seq'),471404000000,471404999999),
                (nextval('public.dev_visa_debit_range_seq'),472408000000,472408999999),
                (nextval('public.dev_visa_debit_range_seq'),472409000000,472409999999),
                (nextval('public.dev_visa_debit_range_seq'),472476000000,472476999999),
                (nextval('public.dev_visa_debit_range_seq'),472571000000,472571999999),
                (nextval('public.dev_visa_debit_range_seq'),472926000000,472926999999),
                (nextval('public.dev_visa_debit_range_seq'),483588000000,483588999999),
                (nextval('public.dev_visa_debit_range_seq'),483994000000,483994999999),
                (nextval('public.dev_visa_debit_range_seq'),483995000000,483995999999),
                (nextval('public.dev_visa_debit_range_seq'),483996000000,483996999999),
                (nextval('public.dev_visa_debit_range_seq'),483997000000,483997999999),
                (nextval('public.dev_visa_debit_range_seq'),484887000000,484887999999),
                (nextval('public.dev_visa_debit_range_seq'),484888000000,484888999999),
                (nextval('public.dev_visa_debit_range_seq'),486098000000,486098999999),
                (nextval('public.dev_visa_debit_range_seq'),489382000000,489382999999);
        
        -- fee_default_payment_fees
        
        UPDATE  fee_default_payment_fees
        SET     cc_visa_fee = 1.50,
                cc_master_card_fee = 2.22,
                visa_debit_fee = 1.77,
                e_cheque_fee = 1.50,
                direct_banking_fee = 1.50,
                interac_caledon_fee = 1.50,
                interac_payment_pad_fee = 19.99,
                interac_visa_fee = 1.50;
                
        
        -- onboarding_user
        
        UPDATE  onboarding_user AS u
        SET     pmc = c.pmc
        FROM    onboarding_user_credential c
        WHERE   c.usr = u.id;
        
        
        -- pad_batch
        
        UPDATE  pad_batch
        SET     processing_status = 'AcknowledgeProcessed'
        WHERE   processing_status = 'AcknowledgeProcesed';
        
        
        -- pad_debit_record
        
        UPDATE  pad_debit_record
        SET     processing_status = 'AcknowledgeProcessed'
        WHERE   processing_status = 'AcknowledgeProcesed';
        
        UPDATE  pad_debit_record
        SET     processing_status = 'ReconciliationProcessed'
        WHERE   processing_status = 'ReconciliationProcesed';
        
        -- pad_file
        
        UPDATE  pad_file
        SET     funds_transfer_type = 'PreAuthorizedDebit';
        
        
        -- pad_file_creation_number
        
        UPDATE  pad_file_creation_number
        SET     funds_transfer_type = 'PreAuthorizedDebit';
        
        -- pad_reconciliation_file
        
        UPDATE  pad_reconciliation_file
        SET     funds_transfer_type = 'PreAuthorizedDebit';
               
        
        -- scheduler_trigger
        
        UPDATE  scheduler_trigger
        SET     trigger_type = 'paymentsReceiveAcknowledgment'
        WHERE   trigger_type = 'paymentsPadReceiveAcknowledgment';
        
        
        UPDATE  scheduler_trigger
        SET     trigger_type = 'paymentsReceiveReconciliation'
        WHERE   trigger_type = 'paymentsPadReceiveReconciliation';
        
        UPDATE  scheduler_trigger
        SET     trigger_type = 'paymentsPadProcessReconciliation'
        WHERE   trigger_type = 'paymentsPadProcesReconciliation';
        
        
        UPDATE  scheduler_trigger
        SET     trigger_type = 'paymentsPadProcessAcknowledgment'
        WHERE   trigger_type = 'paymentsPadProcesAcknowledgment';
        
        INSERT INTO scheduler_trigger (id,trigger_type,name,population_type,created)
        ((SELECT nextval('public.scheduler_trigger_seq') AS id,'paymentsDbpProcess','P 0B - Process Direct Banking, create payment records and post to yardi (auto triggered by PaymentsBmoReceive)',
        'allPmc',current_timestamp)
        UNION
        (SELECT nextval('public.scheduler_trigger_seq') AS id,'paymentsDbpSend','P 5B - Send Direct Banking (BMO) Funds Transfer to Caledon',
        'allPmc',current_timestamp)
        UNION
        (SELECT nextval('public.scheduler_trigger_seq') AS id,'paymentsDbpProcessAcknowledgment','P 6C - Payments Direct Banking Process Acknowledgment (auto triggered by ReceiveAcknowledgment)',
        'allPmc',current_timestamp)
        UNION
        (SELECT nextval('public.scheduler_trigger_seq') AS id,'paymentsDbpProcessReconciliation','P 7C - Payments Direct Banking Process Reconciliation (auto triggered by ReceiveReconciliation)',
        'allPmc',current_timestamp));
        
        
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        -- admin_pmc
        
        ALTER TABLE admin_pmc DROP COLUMN onboarding_account_id;
        
        
        -- admin_pmc_payment_type_info
              
        ALTER TABLE admin_pmc_payment_type_info DROP COLUMN eft_fee;
        
        
        -- admin_pmc_vista_features
        
        ALTER TABLE admin_pmc_vista_features DROP COLUMN xml_site_export;
        
        
        -- dev_equifax_simulator_config
        
        ALTER TABLE dev_equifax_simulator_config        DROP COLUMN approve_xml,
                                                        DROP COLUMN decline_xml,
                                                        DROP COLUMN more_info_xml;
                                                        
                                                        
        -- fee_default_payment_fees
        
        ALTER TABLE fee_default_payment_fees DROP COLUMN eft_fee;
        
        -- onboarding_user
        
        ALTER TABLE onboarding_user     DROP COLUMN name,
                                        DROP COLUMN updated;
                                        
        -- onboarding_user_credential
        
        DROP TABLE onboarding_user_credential;
        
        
        -- scheduler_trigger
        
        ALTER TABLE scheduler_trigger   DROP COLUMN trigger_details,
                                        DROP COLUMN trigger_details_discriminator;
                                        
                                        
        -- scheduler_trigger_details
        
        DROP TABLE scheduler_trigger_details;
       
        
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
        ALTER TABLE direct_debit_record ADD CONSTRAINT direct_debit_record_file_fk FOREIGN KEY(file) REFERENCES direct_debit_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE direct_debit_record ADD CONSTRAINT direct_debit_record_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pad_debit_record_transaction ADD CONSTRAINT pad_debit_record_transaction_pad_debit_record_fk FOREIGN KEY(pad_debit_record) 
                REFERENCES pad_debit_record(id)  DEFERRABLE INITIALLY DEFERRED;



        -- check constraints
        
        ALTER TABLE admin_pmc_dns_name ADD CONSTRAINT admin_pmc_dns_name_target_e_ck 
                CHECK ((target) IN ('field', 'prospectPortal', 'resident', 'residentPortal', 'vistaCrm'));
        ALTER TABLE admin_pmc_payment_method ADD CONSTRAINT admin_pmc_payment_method_details_discriminator_d_ck 
                CHECK ((details_discriminator) IN ('CashInfo', 'CheckInfo', 'CreditCard', 'DirectDebit', 'EcheckInfo', 'InteracInfo'));
        ALTER TABLE admin_pmc_payment_method ADD CONSTRAINT admin_pmc_payment_method_payment_type_e_ck 
                CHECK ((payment_type) IN ('Cash', 'Check', 'CreditCard', 'DirectBanking', 'Echeck', 'Interac'));
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_app_e_ck 
                CHECK ((app) IN ('crm', 'field', 'onboarding', 'operations', 'prospect', 'resident', 'residentPortal'));
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_event_e_ck 
                CHECK ((event) IN ('Create', 'CredentialUpdate', 'EquifaxReadReport', 'EquifaxRequest', 'Info', 'Login', 
                'LoginFailed', 'Logout', 'PermitionsUpdate', 'Read', 'SessionExpiration', 'System', 'Update'));
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_user_type_e_ck CHECK ((user_type) IN ('crm', 'customer', 'operations'));
        ALTER TABLE dev_card_service_simulation_card ADD CONSTRAINT dev_card_service_simulation_card_card_type_e_ck CHECK ((card_type) IN ('MasterCard', 'Visa', 'VisaDebit'));
        ALTER TABLE dev_direct_debit_sim_file ADD CONSTRAINT dev_direct_debit_sim_file_status_e_ck CHECK ((status) IN ('New', 'Sent'));
        ALTER TABLE dev_pad_sim_batch ADD CONSTRAINT dev_pad_sim_batch_reconciliation_status_e_ck CHECK ((reconciliation_status) IN ('HOLD', 'PAID'));
        ALTER TABLE dev_pad_sim_debit_record ADD CONSTRAINT dev_pad_sim_debit_record_reconciliation_status_e_ck 
                CHECK ((reconciliation_status) IN ('DUPLICATE', 'PROCESSED', 'REJECTED', 'RETURNED'));
        ALTER TABLE dev_pad_sim_file ADD CONSTRAINT dev_pad_sim_file_funds_transfer_type_e_ck 
                CHECK ((funds_transfer_type) IN ('DirectBankingPayment', 'InteracOnlinePayment', 'PreAuthorizedDebit'));
        ALTER TABLE direct_debit_record ADD CONSTRAINT direct_debit_record_processing_status_e_ck CHECK ((processing_status) IN ('Invalid', 'Processed', 'Received'));
        ALTER TABLE operations_alert ADD CONSTRAINT operations_alert_app_e_ck 
                CHECK ((app) IN ('crm', 'field', 'onboarding', 'operations', 'prospect', 'resident', 'residentPortal'));
        ALTER TABLE pad_batch ADD CONSTRAINT pad_batch_processing_status_e_ck CHECK ((processing_status) IN ('AcknowledgeProcessed', 'AcknowledgeReject', 'AcknowledgedReceived'));
        ALTER TABLE pad_debit_record ADD CONSTRAINT pad_debit_record_processing_status_e_ck 
                CHECK ((processing_status) IN ('AcknowledgeProcessed', 'AcknowledgeReject', 'AcknowledgedReceived', 'ReconciliationProcessed', 'ReconciliationReceived'));
        ALTER TABLE pad_file ADD CONSTRAINT pad_file_funds_transfer_type_e_ck 
                CHECK ((funds_transfer_type) IN ('DirectBankingPayment', 'InteracOnlinePayment', 'PreAuthorizedDebit'));
        ALTER TABLE pad_file_creation_number ADD CONSTRAINT pad_file_creation_number_funds_transfer_type_e_ck 
                CHECK ((funds_transfer_type) IN ('DirectBankingPayment', 'InteracOnlinePayment', 'PreAuthorizedDebit'));
        ALTER TABLE pad_reconciliation_file ADD CONSTRAINT pad_reconciliation_file_funds_transfer_type_e_ck 
                CHECK ((funds_transfer_type) IN ('DirectBankingPayment', 'InteracOnlinePayment', 'PreAuthorizedDebit'));
        ALTER TABLE payment_payment_details ADD CONSTRAINT payment_payment_details_card_type_e_ck CHECK ((card_type) IN ('MasterCard', 'Visa', 'VisaDebit'));
        ALTER TABLE payment_payment_details ADD CONSTRAINT payment_payment_details_id_discriminator_ck 
                CHECK ((id_discriminator) IN ('CashInfo', 'CheckInfo', 'CreditCard', 'DirectDebit', 'EcheckInfo', 'InteracInfo'));
        ALTER TABLE scheduler_trigger ADD CONSTRAINT scheduler_trigger_trigger_type_e_ck 
                CHECK ((trigger_type) IN ('billing', 'cleanup', 'depositInterestAdjustment', 'depositRefund', 'equifaxRetention', 'initializeFutureBillingCycles', 
                'leaseActivation', 'leaseCompletion', 'leaseRenewal', 'paymentsBmoReceive', 'paymentsDbpProcess','paymentsDbpProcessAcknowledgment', 'paymentsDbpProcessReconciliation', 
                'paymentsDbpSend', 'paymentsIssue', 'paymentsLastMonthSuspend', 'paymentsPadProcessAcknowledgment', 'paymentsPadProcessReconciliation', 'paymentsPadSend', 
                'paymentsReceiveAcknowledgment', 'paymentsReceiveReconciliation', 'paymentsScheduledCreditCards', 'paymentsScheduledEcheck', 'paymentsTenantSure', 
                'paymentsUpdate', 'tenantSureCancellation', 'tenantSureHQUpdate', 'tenantSureReports', 'tenantSureTransactionReports', 'test', 'updateArrears', 
                'updatePaymentsSummary', 'vistaBusinessReport', 'vistaCaleonReport', 'yardiARDateVerification', 'yardiImportProcess'));
        ALTER TABLE scheduler_trigger_notification ADD CONSTRAINT scheduler_trigger_notification_event_e_ck 
                CHECK ((event) IN ('All', 'Completed', 'Error', 'Failed', 'NonEmpty'));

        

                
        /**
        ***     ============================================================================================================
        ***     
        ***             CREATE INDEXES
        ***
        ***     ============================================================================================================
        **/
        
        CREATE INDEX admin_pmc_yardi_credential_pmc_idx ON admin_pmc_yardi_credential USING btree (pmc);
        CREATE INDEX dev_direct_debit_sim_record_account_number_idx ON dev_direct_debit_sim_record USING btree (account_number);
        CREATE INDEX dev_direct_debit_sim_record_file_idx ON dev_direct_debit_sim_record USING btree (file);
        CREATE INDEX dev_pad_sim_batch_pad_file_idx ON dev_pad_sim_batch USING btree (pad_file);
        CREATE INDEX dev_pad_sim_debit_record_pad_batch_idx ON dev_pad_sim_debit_record USING btree (pad_batch);
        CREATE INDEX dev_pad_sim_file$state_owner_idx ON dev_pad_sim_file$state USING btree (owner);
        CREATE INDEX direct_debit_record_account_number_idx ON direct_debit_record USING btree (account_number);
        CREATE INDEX direct_debit_record_file_idx ON direct_debit_record USING btree (file);
        CREATE INDEX direct_debit_record_pmc_idx ON direct_debit_record USING btree (pmc);
        CREATE INDEX global_crm_user_index_email_idx ON global_crm_user_index USING btree (email);
        CREATE INDEX global_crm_user_index_pmc_crm_user_idx ON global_crm_user_index USING btree (pmc, crm_user);
        CREATE INDEX onboarding_user_email_idx ON onboarding_user USING btree (lower(email));
        CREATE INDEX pad_debit_record_transaction_pad_debit_record_idx ON pad_debit_record_transaction USING btree (pad_debit_record);
        CREATE UNIQUE INDEX pad_file_creation_number_funds_transfer_type_company_id_idx ON pad_file USING btree (file_creation_number, funds_transfer_type, company_id) ;

       


COMMIT;

SET client_min_messages = 'notice';
