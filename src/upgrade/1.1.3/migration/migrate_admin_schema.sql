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
        
        -- foreign keys
        ALTER TABLE dev_card_service_simulation_card DROP CONSTRAINT dev_card_service_simulation_card_merchant_fk;
        ALTER TABLE pad_batch DROP CONSTRAINT pad_batch_pad_file_fk;
        ALTER TABLE pad_batch DROP CONSTRAINT pad_batch_pmc_fk;
        ALTER TABLE pad_debit_record DROP CONSTRAINT pad_debit_record_pad_batch_fk;
        ALTER TABLE pad_debit_record_transaction DROP CONSTRAINT pad_debit_record_transaction_pad_debit_record_fk;
        ALTER TABLE pad_reconciliation_debit_record DROP CONSTRAINT pad_reconciliation_debit_record_reconciliation_summary_fk;
        ALTER TABLE pad_reconciliation_summary DROP CONSTRAINT pad_reconciliation_summary_merchant_account_fk;
        ALTER TABLE pad_reconciliation_summary DROP CONSTRAINT pad_reconciliation_summary_reconciliation_file_fk;
        
        -- primary keys
        ALTER TABLE pad_batch DROP CONSTRAINT pad_batch_pk;
        ALTER TABLE pad_debit_record DROP CONSTRAINT pad_debit_record_pk;
        ALTER TABLE pad_debit_record_transaction DROP CONSTRAINT pad_debit_record_transaction_pk;
        ALTER TABLE pad_file_creation_number DROP CONSTRAINT pad_file_creation_number_pk;
        ALTER TABLE pad_file DROP CONSTRAINT pad_file_pk;
        ALTER TABLE pad_reconciliation_debit_record DROP CONSTRAINT pad_reconciliation_debit_record_pk;
        ALTER TABLE pad_reconciliation_file DROP CONSTRAINT pad_reconciliation_file_pk;
        ALTER TABLE pad_reconciliation_summary DROP CONSTRAINT pad_reconciliation_summary_pk;

        -- check constraints
        ALTER TABLE admin_pmc_dns_name DROP CONSTRAINT admin_pmc_dns_name_target_e_ck;
        ALTER TABLE audit_record DROP CONSTRAINT audit_record_app_e_ck;
        ALTER TABLE direct_debit_record DROP CONSTRAINT direct_debit_record_processing_status_e_ck;
        ALTER TABLE operations_alert DROP CONSTRAINT operations_alert_app_e_ck;
        ALTER TABLE pad_batch DROP CONSTRAINT pad_batch_processing_status_e_ck;
        ALTER TABLE pad_debit_record DROP CONSTRAINT pad_debit_record_processing_status_e_ck;
        ALTER TABLE pad_file DROP CONSTRAINT pad_file_acknowledgment_status_e_ck;
        ALTER TABLE pad_file_creation_number DROP CONSTRAINT pad_file_creation_number_funds_transfer_type_e_ck;
        ALTER TABLE pad_file DROP CONSTRAINT pad_file_funds_transfer_type_e_ck;
        ALTER TABLE pad_file DROP CONSTRAINT pad_file_status_e_ck;
        ALTER TABLE pad_reconciliation_debit_record DROP CONSTRAINT pad_reconciliation_debit_record_reconciliation_status_e_ck;
        ALTER TABLE pad_reconciliation_file DROP CONSTRAINT pad_reconciliation_file_funds_transfer_type_e_ck;
        ALTER TABLE pad_reconciliation_summary DROP CONSTRAINT pad_reconciliation_summary_reconciliation_status_e_ck;
        ALTER TABLE pmc_personal_information_document DROP CONSTRAINT pmc_personal_information_document_type_e_ck;
        ALTER TABLE scheduler_trigger DROP CONSTRAINT scheduler_trigger_trigger_type_e_ck;
        ALTER TABLE vista_terms DROP CONSTRAINT vista_terms_target_e_ck;

        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP INDEXES 
        ***
        ***     ======================================================================================================
        **/
        
        DROP INDEX pad_batch_pad_file_idx;
        DROP INDEX pad_batch_pmc_merchant_account_key_idx;
        DROP INDEX pad_debit_record_pad_batch_idx;
        DROP INDEX pad_debit_record_transaction_id_idx;
        DROP INDEX pad_debit_record_transaction_pad_debit_record_idx;
        DROP INDEX pad_file_creation_number_funds_transfer_type_company_id_idx;
        DROP INDEX pad_reconciliation_debit_record_reconciliation_summary_idx;
        DROP INDEX pad_reconciliation_summary_merchant_account_idx;
        DROP INDEX pad_reconciliation_summary_reconciliation_file_idx;

        /**
        ***     =======================================================================================================
        ***
        ***             RENAMED TABLES 
        ***
        ***     =======================================================================================================
        **/
        
        -- pad_batch
        
        ALTER TABLE pad_batch RENAME TO funds_transfer_batch;
        
        -- pad_debit_record
        
        ALTER TABLE pad_debit_record RENAME TO funds_transfer_record;
        
        
        -- pad_debit_record_transaction
        
        ALTER TABLE pad_debit_record_transaction RENAME TO funds_transfer_record_transaction;
        
        
        -- pad_file_creation_number
        
        ALTER TABLE pad_file_creation_number RENAME TO funds_transfer_file_creation_number;
        
        
        -- pad_file
        
        ALTER TABLE pad_file RENAME TO funds_transfer_file;
        
        
        -- pad_reconciliation_debit_record
        
        ALTER TABLE pad_reconciliation_debit_record RENAME TO funds_reconciliation_record_record;
        
        
        -- pad_reconciliation_file
        
        ALTER TABLE pad_reconciliation_file RENAME TO funds_reconciliation_file;
        
        
        -- pad_reconciliation_summary
        
        ALTER TABLE pad_reconciliation_summary RENAME TO funds_reconciliation_summary;
        
        
        /**
        ***     =======================================================================================================
        ***
        ***             NEW AND ALTERED TABLES 
        ***
        ***     =======================================================================================================
        **/
        
        
        
        -- admin_pmc_equifax_info
        
        ALTER TABLE admin_pmc_equifax_info RENAME COLUMN customer_number TO member_number_encrypted;
        ALTER TABLE admin_pmc_equifax_info ALTER COLUMN member_number_encrypted TYPE VARCHAR(1024);
        
        ALTER TABLE admin_pmc_equifax_info RENAME COLUMN security_code TO security_code_encrypted;
        ALTER TABLE admin_pmc_equifax_info ALTER COLUMN security_code_encrypted TYPE VARCHAR(1024);
        
        ALTER TABLE admin_pmc_equifax_info      ADD COLUMN member_number_password VARCHAR(500),
                                                ADD COLUMN security_code_password VARCHAR(500);
                                                
                                                    
      
        -- admin_pmc_yardi_credential
        
        ALTER TABLE admin_pmc_yardi_credential ADD COLUMN ils_guest_card20_service_url VARCHAR(500);
        
        
        -- dev_card_service_simulation_transaction
        
        ALTER TABLE dev_card_service_simulation_transaction ADD COLUMN merchant BIGINT;
        
        
        -- dev_card_service_simulator_config
        
        ALTER TABLE dev_card_service_simulator_config RENAME COLUMN delay TO response_delay;
        
        -- dev_pad_sim_batch
        
        ALTER TABLE dev_pad_sim_batch ADD COLUMN updated TIMESTAMP;
        
        
        -- dev_pad_sim_debit_record
        
        ALTER TABLE dev_pad_sim_debit_record ADD COLUMN updated TIMESTAMP;
        
        
        -- direct_debit_record
        
        ALTER TABLE direct_debit_record ADD COLUMN operations_notes VARCHAR(500);
        
        -- funds_reconciliation_file
        
        ALTER TABLE funds_reconciliation_file   ADD COLUMN file_name_date TIMESTAMP,
                                                ADD COLUMN remote_file_date TIMESTAMP;
                                                
        -- funds_transfer_file
        
        ALTER TABLE funds_transfer_file ADD COLUMN acknowledgment_file_name VARCHAR(500),
                                        ADD COLUMN acknowledgment_file_name_date TIMESTAMP,
                                        ADD COLUMN acknowledgment_remote_file_date TIMESTAMP;
                                        
        -- outgoing_mail_queue
        
        CREATE TABLE outgoing_mail_queue
        (
            id                          BIGINT                  NOT NULL,
            id_discriminator            VARCHAR(64)             NOT NULL,
            status                      VARCHAR(50),
            namespace                   VARCHAR(500),
            configuration_id            VARCHAR(500),
            status_callback_class       VARCHAR(500),
            created                     TIMESTAMP,
            updated                     TIMESTAMP,
            attempts                    INT,
            last_attempt_error_message  VARCHAR(4000),
            send_to                     VARCHAR(500),
            keywords                    VARCHAR(500),
            sent_date                   VARCHAR(500),
            message_id                  VARCHAR(500),
            data                        BYTEA,
                CONSTRAINT outgoing_mail_queue_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE outgoing_mail_queue OWNER TO vista;
                                        
        
        -- pmc_document_blob
        
        ALTER TABLE pmc_document_blob   ADD COLUMN name VARCHAR(500),
                                        ADD COLUMN updated TIMESTAMP;
                                        
       
        -- pmc_document_file
                
        ALTER TABLE pmc_document_file RENAME COLUMN blob_key TO file_blob_key;
        ALTER TABLE pmc_document_file RENAME COLUMN cache_version TO file_cache_version;
        ALTER TABLE pmc_document_file RENAME COLUMN content_mime_type TO file_content_mime_type;
        ALTER TABLE pmc_document_file RENAME COLUMN file_name TO file_file_name;
        ALTER TABLE pmc_document_file RENAME COLUMN file_size TO file_file_size;
        ALTER TABLE pmc_document_file RENAME COLUMN updated_timestamp TO file_updated_timestamp; 
        
        ALTER TABLE pmc_document_file   DROP COLUMN caption,
                                        DROP COLUMN description;
                                        
        
        -- pmc_personal_information_document
        
        ALTER TABLE pmc_personal_information_document RENAME COLUMN type TO personal_info_doc_type;
                                        
        -- vista_terms_v
        
        ALTER TABLE vista_terms_v ADD COLUMN caption VARCHAR(500);
       
       
        /**
        ***     ============================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     ============================================================================================================
        **/
        
        -- audit_record 
        
        UPDATE  audit_record
        SET     app = 'resident'
        WHERE   app = 'portal';
        
        
        -- vista_terms
        
        UPDATE  vista_terms
        SET     target = 'PmcPropertyVistaService'
        WHERE   target = 'PMC';
        
        UPDATE  vista_terms
        SET     target = 'ResidentPortalTermsAndConditions'
        WHERE   target = 'Tenant';
        
        UPDATE  vista_terms
        SET     target = 'TenantBillingTerms'
        WHERE   target = 'TenantBilling';
        
        UPDATE  vista_terms
        SET     target = 'TenantPreAuthorizedPaymentECheckTerms'
        WHERE   target = 'TenantPaymentPad';
        
        UPDATE  vista_terms
        SET     target = 'TenantPreAuthorizedPaymentCardTerms'
        WHERE   target = 'TenantPaymentCreditCard';
        
        UPDATE  vista_terms
        SET     target = 'TenantPaymentWebPaymentFeeTerms'
        WHERE   target = 'TenantPaymentConvenienceFee';
        

        
        
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
       
        -- admin_pmc_vista_features
        
        ALTER TABLE admin_pmc_vista_features    DROP COLUMN occupancy_model,
                                                DROP COLUMN default_product_catalog,
                                                DROP COLUMN leases,
                                                DROP COLUMN product_catalog;
        
        -- dev_card_service_simulation_card
        
        ALTER TABLE dev_card_service_simulation_card DROP COLUMN merchant;
        
        /**
        ***     ========================================================================================================
        ***
        ***             CREATE CONSTRAINTS
        ***
        ***     ========================================================================================================
        **/
        
        -- primary keys
        ALTER TABLE funds_reconciliation_file ADD CONSTRAINT funds_reconciliation_file_pk PRIMARY KEY(id);
        ALTER TABLE funds_reconciliation_record_record ADD CONSTRAINT funds_reconciliation_record_record_pk PRIMARY KEY(id);
        ALTER TABLE funds_reconciliation_summary ADD CONSTRAINT funds_reconciliation_summary_pk PRIMARY KEY(id);
        ALTER TABLE funds_transfer_batch ADD CONSTRAINT funds_transfer_batch_pk PRIMARY KEY(id);
        ALTER TABLE funds_transfer_file_creation_number ADD CONSTRAINT funds_transfer_file_creation_number_pk PRIMARY KEY(id);
        ALTER TABLE funds_transfer_file ADD CONSTRAINT funds_transfer_file_pk PRIMARY KEY(id);
        ALTER TABLE funds_transfer_record ADD CONSTRAINT funds_transfer_record_pk PRIMARY KEY(id);
        ALTER TABLE funds_transfer_record_transaction ADD CONSTRAINT funds_transfer_record_transaction_pk PRIMARY KEY(id);
       
        
        -- foreign keys
        ALTER TABLE dev_card_service_simulation_transaction ADD CONSTRAINT dev_card_service_simulation_transaction_merchant_fk FOREIGN KEY(merchant) 
                REFERENCES dev_card_service_simulation_merchant_account(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE funds_reconciliation_record_record ADD CONSTRAINT funds_reconciliation_record_record_reconciliation_summary_fk FOREIGN KEY(reconciliation_summary) 
                REFERENCES funds_reconciliation_summary(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE funds_reconciliation_summary ADD CONSTRAINT funds_reconciliation_summary_merchant_account_fk FOREIGN KEY(merchant_account) 
                REFERENCES admin_pmc_merchant_account_index(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE funds_reconciliation_summary ADD CONSTRAINT funds_reconciliation_summary_reconciliation_file_fk FOREIGN KEY(reconciliation_file) 
                REFERENCES funds_reconciliation_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE funds_transfer_batch ADD CONSTRAINT funds_transfer_batch_pad_file_fk FOREIGN KEY(pad_file) 
                REFERENCES funds_transfer_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE funds_transfer_batch ADD CONSTRAINT funds_transfer_batch_pmc_fk FOREIGN KEY(pmc) 
                REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE funds_transfer_record ADD CONSTRAINT funds_transfer_record_pad_batch_fk FOREIGN KEY(pad_batch) 
                REFERENCES funds_transfer_batch(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE funds_transfer_record_transaction ADD CONSTRAINT funds_transfer_record_transaction_pad_debit_record_fk FOREIGN KEY(pad_debit_record) 
                REFERENCES funds_transfer_record(id)  DEFERRABLE INITIALLY DEFERRED;


        -- check constraints
        
        ALTER TABLE admin_pmc_dns_name ADD CONSTRAINT admin_pmc_dns_name_target_e_ck CHECK ((target) IN ('crm', 'portal', 'site'));
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_app_e_ck CHECK ((app) IN ('crm', 'onboarding', 'operations', 'prospect', 'resident', 'site'));
        ALTER TABLE direct_debit_record ADD CONSTRAINT direct_debit_record_processing_status_e_ck CHECK ((processing_status) IN ('Invalid', 'Processed', 'Received', 'Refunded'));
        ALTER TABLE funds_reconciliation_file ADD CONSTRAINT funds_reconciliation_file_funds_transfer_type_e_ck 
                CHECK ((funds_transfer_type) IN ('DirectBankingPayment', 'InteracOnlinePayment', 'PreAuthorizedDebit'));
        ALTER TABLE funds_reconciliation_record_record ADD CONSTRAINT funds_reconciliation_record_record_reconciliation_status_e_ck 
                CHECK ((reconciliation_status) IN ('DUPLICATE', 'PROCESSED', 'REJECTED', 'RETURNED'));
        ALTER TABLE funds_reconciliation_summary ADD CONSTRAINT funds_reconciliation_summary_reconciliation_status_e_ck CHECK ((reconciliation_status) IN ('HOLD', 'PAID'));
        ALTER TABLE funds_transfer_batch ADD CONSTRAINT funds_transfer_batch_processing_status_e_ck 
                CHECK ((processing_status) IN ('AcknowledgeProcessed', 'AcknowledgeReject', 'AcknowledgedReceived'));
        ALTER TABLE funds_transfer_file ADD CONSTRAINT funds_transfer_file_acknowledgment_status_e_ck 
                CHECK ((acknowledgment_status) IN ('Accepted', 'BatchAndTransactionReject', 'BatchLevelReject', 'DetailRecordCountOutOfBalance', 'FileOutOfBalance', 
                'InvalidFileFormat', 'InvalidFileHeader', 'TransactionReject'));
        ALTER TABLE funds_transfer_file_creation_number ADD CONSTRAINT funds_transfer_file_creation_number_funds_transfer_type_e_ck 
                CHECK ((funds_transfer_type) IN ('DirectBankingPayment', 'InteracOnlinePayment', 'PreAuthorizedDebit'));
        ALTER TABLE funds_transfer_file ADD CONSTRAINT funds_transfer_file_funds_transfer_type_e_ck 
                CHECK ((funds_transfer_type) IN ('DirectBankingPayment', 'InteracOnlinePayment', 'PreAuthorizedDebit'));
        ALTER TABLE funds_transfer_file ADD CONSTRAINT funds_transfer_file_status_e_ck 
                CHECK ((status) IN ('Acknowledged', 'Canceled', 'Creating', 'Invalid', 'SendError', 'Sending', 'Sent'));
        ALTER TABLE funds_transfer_record ADD CONSTRAINT funds_transfer_record_processing_status_e_ck 
                CHECK ((processing_status) IN ('AcknowledgeProcessed', 'AcknowledgeReject', 'AcknowledgedReceived', 'ReconciliationProcessed', 'ReconciliationReceived'));
        ALTER TABLE operations_alert ADD CONSTRAINT operations_alert_app_e_ck CHECK ((app) IN ('crm', 'onboarding', 'operations', 'prospect', 'resident', 'site'));
        ALTER TABLE outgoing_mail_queue ADD CONSTRAINT outgoing_mail_queue_id_discriminator_ck 
            CHECK ((id_discriminator) IN ('Default', 'TenantSure'));
        ALTER TABLE outgoing_mail_queue ADD CONSTRAINT outgoing_mail_queue_status_e_ck 
            CHECK ((status) IN ('Cancelled', 'GiveUp', 'Queued', 'Success'));
        ALTER TABLE pmc_personal_information_document ADD CONSTRAINT pmc_personal_information_document_personal_info_doc_type_e_ck 
            CHECK ((personal_info_doc_type) IN ('DriversLicense', 'Passport'));
        ALTER TABLE scheduler_trigger ADD CONSTRAINT scheduler_trigger_trigger_type_e_ck 
                CHECK ((trigger_type) IN ('billing', 'cleanup', 'depositInterestAdjustment', 'depositRefund', 'equifaxRetention', 'ilsEmailFeed', 'ilsUpdate', 
                'initializeFutureBillingCycles', 'leaseActivation', 'leaseCompletion', 'leaseRenewal', 'paymentsBmoReceive', 'paymentsDbpProcess', 'paymentsDbpProcessAcknowledgment', 
                'paymentsDbpProcessReconciliation', 'paymentsDbpSend', 'paymentsIssue', 'paymentsLastMonthSuspend', 'paymentsPadProcessAcknowledgment', 
                'paymentsPadProcessReconciliation', 'paymentsPadSend', 'paymentsReceiveAcknowledgment', 'paymentsReceiveReconciliation', 'paymentsScheduledCreditCards', 
                'paymentsScheduledEcheck', 'paymentsTenantSure', 'tenantSureCancellation', 'tenantSureHQUpdate', 'tenantSureReports', 'tenantSureTransactionReports', 
                'test', 'updateArrears', 'updatePaymentsSummary', 'vistaBusinessReport', 'vistaCaleonReport', 'yardiARDateVerification', 'yardiImportProcess'));
        ALTER TABLE vista_terms ADD CONSTRAINT vista_terms_target_e_ck 
                CHECK ((target) IN ('PmcCaledonSoleProprietorshipSection', 'PmcCaledonTemplate', 'PmcPaymentPad', 'PmcPropertyVistaService', 'ProspectPortalPrivacyPolicy',
                 'ProspectPortalTermsAndConditions', 'ResidentPortalPrivacyPolicy', 'ResidentPortalTermsAndConditions', 'TenantBillingTerms', 'TenantPaymentWebPaymentFeeTerms',
                 'TenantPreAuthorizedPaymentCardTerms', 'TenantPreAuthorizedPaymentECheckTerms', 'TenantSurePreAuthorizedPaymentsAgreement'));


        

        -- Not null
        
        ALTER TABLE funds_reconciliation_file ALTER COLUMN created DROP NOT NULL;
                
        /**
        ***     ============================================================================================================
        ***     
        ***             CREATE INDEXES
        ***
        ***     ============================================================================================================
        **/
        
        CREATE INDEX funds_reconciliation_record_record_reconciliation_summary_idx ON funds_reconciliation_record_record USING btree (reconciliation_summary);
        CREATE INDEX funds_reconciliation_summary_merchant_account_idx ON funds_reconciliation_summary USING btree (merchant_account);
        CREATE INDEX funds_reconciliation_summary_reconciliation_file_idx ON funds_reconciliation_summary USING btree (reconciliation_file);
        CREATE INDEX funds_transfer_batch_pad_file_idx ON funds_transfer_batch USING btree (pad_file);
        CREATE INDEX funds_transfer_batch_pmc_merchant_account_key_idx ON funds_transfer_batch USING btree (pmc, merchant_account_key);
        CREATE UNIQUE INDEX funds_transfer_file_creation_number_company_id_idx ON funds_transfer_file USING btree (file_creation_number, funds_transfer_type, company_id);
        CREATE INDEX funds_transfer_record_pad_batch_idx ON funds_transfer_record USING btree (pad_batch);
        CREATE INDEX funds_transfer_record_transaction_id_idx ON funds_transfer_record USING btree (transaction_id);
        CREATE INDEX funds_transfer_record_transaction_pad_debit_record_idx ON funds_transfer_record_transaction USING btree (pad_debit_record);
        

COMMIT;

SET client_min_messages = 'notice';
