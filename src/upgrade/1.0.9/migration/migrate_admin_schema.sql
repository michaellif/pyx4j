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
        
        

        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP CONSTRAINTS SECTION
        ***
        ***     ======================================================================================================
        **/
        
        -- foreign keys
        
        ALTER TABLE admin_pmc_account_numbers DROP CONSTRAINT admin_pmc_account_numbers_pmc_fk;
        ALTER TABLE admin_pmc_dns_name DROP CONSTRAINT admin_pmc_dns_name_pmc_fk;
        ALTER TABLE admin_pmc_equifax_info DROP CONSTRAINT admin_pmc_equifax_info_business_information_fk;
        ALTER TABLE admin_pmc_equifax_info DROP CONSTRAINT admin_pmc_equifax_info_payment_method_fk;
        ALTER TABLE admin_pmc_equifax_info DROP CONSTRAINT admin_pmc_equifax_info_personal_information_fk;
        ALTER TABLE admin_pmc_equifax_info DROP CONSTRAINT admin_pmc_equifax_info_pmc_fk;
        ALTER TABLE admin_pmc DROP CONSTRAINT admin_pmc_features_fk;
        ALTER TABLE admin_pmc_merchant_account_index DROP CONSTRAINT admin_pmc_merchant_account_index_pmc_fk;
        ALTER TABLE admin_pmc_payment_method DROP CONSTRAINT admin_pmc_payment_method_details_fk;
        ALTER TABLE admin_pmc_payment_method DROP CONSTRAINT admin_pmc_payment_method_pmc_fk;
        ALTER TABLE admin_pmc_payment_type_info DROP CONSTRAINT admin_pmc_payment_type_info_pmc_fk;
        ALTER TABLE admin_pmc_yardi_credential DROP CONSTRAINT admin_pmc_yardi_credential_pmc_fk;
        ALTER TABLE customer_credit_check_transaction DROP CONSTRAINT customer_credit_check_transaction_payment_method_fk;
        ALTER TABLE customer_credit_check_transaction DROP CONSTRAINT customer_credit_check_transaction_pmc_fk;
        ALTER TABLE dev_card_service_simulation_card DROP CONSTRAINT dev_card_service_simulation_card_merchant_fk;
        ALTER TABLE dev_card_service_simulation_token DROP CONSTRAINT dev_card_service_simulation_token_card_fk;
        ALTER TABLE dev_card_service_simulation_transaction DROP CONSTRAINT dev_card_service_simulation_transaction_card_fk;
        ALTER TABLE encrypted_storage_current_key DROP CONSTRAINT encrypted_storage_current_key_current_fk;
        ALTER TABLE fee_pmc_equifax_fee DROP CONSTRAINT fee_pmc_equifax_fee_pmc_fk;
        ALTER TABLE onboarding_user_credential DROP CONSTRAINT onboarding_user_credential_pmc_fk;
        ALTER TABLE onboarding_user_credential DROP CONSTRAINT onboarding_user_credential_usr_fk;
        ALTER TABLE operations_user_credential$behaviors DROP CONSTRAINT operations_user_credential$behaviors_owner_fk;
        ALTER TABLE operations_user_credential DROP CONSTRAINT operations_user_credential_usr_fk;
        ALTER TABLE pad_batch DROP CONSTRAINT pad_batch_pad_file_fk;
        ALTER TABLE pad_debit_record DROP CONSTRAINT pad_debit_record_pad_batch_fk;
        ALTER TABLE pad_reconciliation_debit_record DROP CONSTRAINT pad_reconciliation_debit_record_reconciliation_summary_fk;
        ALTER TABLE pad_reconciliation_summary DROP CONSTRAINT pad_reconciliation_summary_merchant_account_fk;
        ALTER TABLE pad_reconciliation_summary DROP CONSTRAINT pad_reconciliation_summary_reconciliation_file_fk;
        ALTER TABLE pad_sim_batch DROP CONSTRAINT pad_sim_batch_pad_file_fk;
        ALTER TABLE pad_sim_debit_record DROP CONSTRAINT pad_sim_debit_record_pad_batch_fk;
        ALTER TABLE pad_sim_file$state DROP CONSTRAINT pad_sim_file$state_owner_fk;
        ALTER TABLE pmc_business_info_document$document_pages DROP CONSTRAINT pmc_business_info_document$document_pages_owner_fk;
        ALTER TABLE pmc_business_info_document$document_pages DROP CONSTRAINT pmc_business_info_document$document_pages_value_fk;
        ALTER TABLE pmc_business_info_document DROP CONSTRAINT pmc_business_info_document_owner_fk;
        ALTER TABLE pmc_personal_information_document$document_pages DROP CONSTRAINT pmc_personal_information_document$document_pages_owner_fk;
        ALTER TABLE pmc_personal_information_document$document_pages DROP CONSTRAINT pmc_personal_information_document$document_pages_value_fk;
        ALTER TABLE pmc_personal_information_document DROP CONSTRAINT pmc_personal_information_document_owner_fk;
        ALTER TABLE scheduler_execution_report_message DROP CONSTRAINT scheduler_execution_report_message_execution_report_section_fk;
        ALTER TABLE scheduler_execution_report_section DROP CONSTRAINT scheduler_execution_report_section_execution_report_fk;
        ALTER TABLE scheduler_run_data DROP CONSTRAINT scheduler_run_data_execution_fk;
        ALTER TABLE scheduler_run_data DROP CONSTRAINT scheduler_run_data_execution_report_fk;
        ALTER TABLE scheduler_run_data DROP CONSTRAINT scheduler_run_data_pmc_fk;
        ALTER TABLE scheduler_run DROP CONSTRAINT scheduler_run_execution_report_fk;
        ALTER TABLE scheduler_run DROP CONSTRAINT scheduler_run_trgr_fk;
        ALTER TABLE scheduler_trigger_notification DROP CONSTRAINT scheduler_trigger_notification_trgr_fk;
        ALTER TABLE scheduler_trigger_notification DROP CONSTRAINT scheduler_trigger_notification_usr_fk;
        ALTER TABLE scheduler_trigger_pmc DROP CONSTRAINT scheduler_trigger_pmc_pmc_fk;
        ALTER TABLE scheduler_trigger_pmc DROP CONSTRAINT scheduler_trigger_pmc_trgr_fk;
        ALTER TABLE scheduler_trigger_schedule DROP CONSTRAINT scheduler_trigger_schedule_trgr_fk;
        ALTER TABLE scheduler_trigger DROP CONSTRAINT scheduler_trigger_trigger_details_fk;
        ALTER TABLE tenant_sure_hqupdate_record DROP CONSTRAINT tenant_sure_hqupdate_record_file_fk;
        ALTER TABLE tenant_sure_subscribers DROP CONSTRAINT tenant_sure_subscribers_pmc_fk;
        ALTER TABLE vista_terms_v$document DROP CONSTRAINT vista_terms_v$document_owner_fk;
        ALTER TABLE vista_terms_v$document DROP CONSTRAINT vista_terms_v$document_value_fk;
        ALTER TABLE vista_terms_v DROP CONSTRAINT vista_terms_v_holder_fk;
        
        
        /**
        ***     =======================================================================================================
        ***
        ***             NEW AND ALTERED TABLES 
        ***
        ***     =======================================================================================================
        **/
        
        -- admin_pmc_yardi_credential
        
        ALTER TABLE admin_pmc_yardi_credential  ADD COLUMN maintenance_requests_service_url VARCHAR(500);
        
        -- pad_sim_file
        
        ALTER TABLE pad_sim_file        ADD COLUMN original_file BIGINT,
                                        ADD COLUMN return_sent TIMESTAMP,
                                        ADD COLUMN returns BOOLEAN;
                                        
        -- scheduler_run
        
        ALTER TABLE scheduler_run ADD COLUMN completed TIMESTAMP;
       
        
        
       
        /**
        ***     ============================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     ============================================================================================================
        **/
       
       
        
        
        
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
        
        -- Foreign Keys
        
        ALTER TABLE admin_pmc_account_numbers ADD CONSTRAINT admin_pmc_account_numbers_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE admin_pmc_dns_name ADD CONSTRAINT admin_pmc_dns_name_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE admin_pmc_equifax_info ADD CONSTRAINT admin_pmc_equifax_info_business_information_fk 
                FOREIGN KEY(business_information) REFERENCES business_information(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE admin_pmc_equifax_info ADD CONSTRAINT admin_pmc_equifax_info_payment_method_fk 
                FOREIGN KEY(payment_method) REFERENCES admin_pmc_payment_method(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE admin_pmc_equifax_info ADD CONSTRAINT admin_pmc_equifax_info_personal_information_fk 
                FOREIGN KEY(personal_information) REFERENCES personal_information(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE admin_pmc_equifax_info ADD CONSTRAINT admin_pmc_equifax_info_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE admin_pmc ADD CONSTRAINT admin_pmc_features_fk FOREIGN KEY(features) REFERENCES admin_pmc_vista_features(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE admin_pmc_merchant_account_index ADD CONSTRAINT admin_pmc_merchant_account_index_pmc_fk 
                FOREIGN KEY(pmc) REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE admin_pmc_payment_method ADD CONSTRAINT admin_pmc_payment_method_details_fk 
                FOREIGN KEY(details) REFERENCES payment_payment_details(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE admin_pmc_payment_method ADD CONSTRAINT admin_pmc_payment_method_pmc_fk 
                FOREIGN KEY(pmc) REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE admin_pmc_payment_type_info ADD CONSTRAINT admin_pmc_payment_type_info_pmc_fk 
                FOREIGN KEY(pmc) REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE admin_pmc_yardi_credential ADD CONSTRAINT admin_pmc_yardi_credential_pmc_fk 
                FOREIGN KEY(pmc) REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_credit_check_transaction ADD CONSTRAINT customer_credit_check_transaction_payment_method_fk 
                FOREIGN KEY(payment_method) REFERENCES admin_pmc_payment_method(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_credit_check_transaction ADD CONSTRAINT customer_credit_check_transaction_pmc_fk 
                FOREIGN KEY(pmc) REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE dev_card_service_simulation_card ADD CONSTRAINT dev_card_service_simulation_card_merchant_fk 
                FOREIGN KEY(merchant) REFERENCES dev_card_service_simulation_merchant_account(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE dev_card_service_simulation_token ADD CONSTRAINT dev_card_service_simulation_token_card_fk 
                FOREIGN KEY(card) REFERENCES dev_card_service_simulation_card(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE dev_card_service_simulation_transaction ADD CONSTRAINT dev_card_service_simulation_transaction_card_fk 
                FOREIGN KEY(card) REFERENCES dev_card_service_simulation_card(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE encrypted_storage_current_key ADD CONSTRAINT encrypted_storage_current_key_current_fk 
                FOREIGN KEY(current) REFERENCES encrypted_storage_public_key(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE fee_pmc_equifax_fee ADD CONSTRAINT fee_pmc_equifax_fee_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE onboarding_user_credential ADD CONSTRAINT onboarding_user_credential_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE onboarding_user_credential ADD CONSTRAINT onboarding_user_credential_usr_fk 
                FOREIGN KEY(usr) REFERENCES onboarding_user(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE operations_user_credential$behaviors ADD CONSTRAINT operations_user_credential$behaviors_owner_fk 
                FOREIGN KEY(owner) REFERENCES operations_user_credential(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE operations_user_credential ADD CONSTRAINT operations_user_credential_usr_fk 
                FOREIGN KEY(usr) REFERENCES operations_user(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pad_batch ADD CONSTRAINT pad_batch_pad_file_fk FOREIGN KEY(pad_file) REFERENCES pad_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pad_debit_record ADD CONSTRAINT pad_debit_record_pad_batch_fk FOREIGN KEY(pad_batch) REFERENCES pad_batch(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pad_reconciliation_debit_record ADD CONSTRAINT pad_reconciliation_debit_record_reconciliation_summary_fk 
                FOREIGN KEY(reconciliation_summary) REFERENCES pad_reconciliation_summary(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pad_reconciliation_summary ADD CONSTRAINT pad_reconciliation_summary_merchant_account_fk 
                FOREIGN KEY(merchant_account) REFERENCES admin_pmc_merchant_account_index(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pad_reconciliation_summary ADD CONSTRAINT pad_reconciliation_summary_reconciliation_file_fk 
                FOREIGN KEY(reconciliation_file) REFERENCES pad_reconciliation_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pad_sim_batch ADD CONSTRAINT pad_sim_batch_pad_file_fk FOREIGN KEY(pad_file) REFERENCES pad_sim_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pad_sim_debit_record ADD CONSTRAINT pad_sim_debit_record_pad_batch_fk 
                FOREIGN KEY(pad_batch) REFERENCES pad_sim_batch(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pad_sim_file$state ADD CONSTRAINT pad_sim_file$state_owner_fk FOREIGN KEY(owner) REFERENCES pad_sim_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pad_sim_file ADD CONSTRAINT pad_sim_file_original_file_fk FOREIGN KEY(original_file) REFERENCES pad_sim_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pmc_business_info_document$document_pages ADD CONSTRAINT pmc_business_info_document$document_pages_owner_fk 
                FOREIGN KEY(owner) REFERENCES pmc_business_info_document(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pmc_business_info_document$document_pages ADD CONSTRAINT pmc_business_info_document$document_pages_value_fk 
                FOREIGN KEY(value) REFERENCES pmc_document_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pmc_business_info_document ADD CONSTRAINT pmc_business_info_document_owner_fk 
                FOREIGN KEY(owner) REFERENCES business_information(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pmc_personal_information_document$document_pages ADD CONSTRAINT pmc_personal_information_document$document_pages_owner_fk 
                FOREIGN KEY(owner) REFERENCES pmc_personal_information_document(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pmc_personal_information_document$document_pages ADD CONSTRAINT pmc_personal_information_document$document_pages_value_fk 
                FOREIGN KEY(value) REFERENCES pmc_document_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pmc_personal_information_document ADD CONSTRAINT pmc_personal_information_document_owner_fk 
                FOREIGN KEY(owner) REFERENCES personal_information(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE scheduler_execution_report_message ADD CONSTRAINT scheduler_execution_report_message_execution_report_section_fk 
                FOREIGN KEY(execution_report_section) REFERENCES scheduler_execution_report_section(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE scheduler_execution_report_section ADD CONSTRAINT scheduler_execution_report_section_execution_report_fk 
                FOREIGN KEY(execution_report) REFERENCES scheduler_execution_report(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE scheduler_run_data ADD CONSTRAINT scheduler_run_data_execution_fk 
                FOREIGN KEY(execution) REFERENCES scheduler_run(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE scheduler_run_data ADD CONSTRAINT scheduler_run_data_execution_report_fk 
                FOREIGN KEY(execution_report) REFERENCES scheduler_execution_report(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE scheduler_run_data ADD CONSTRAINT scheduler_run_data_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE scheduler_run ADD CONSTRAINT scheduler_run_execution_report_fk 
                FOREIGN KEY(execution_report) REFERENCES scheduler_execution_report(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE scheduler_run ADD CONSTRAINT scheduler_run_trgr_fk FOREIGN KEY(trgr) REFERENCES scheduler_trigger(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE scheduler_trigger_notification ADD CONSTRAINT scheduler_trigger_notification_trgr_fk 
                FOREIGN KEY(trgr) REFERENCES scheduler_trigger(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE scheduler_trigger_notification ADD CONSTRAINT scheduler_trigger_notification_usr_fk 
                FOREIGN KEY(usr) REFERENCES operations_user(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE scheduler_trigger_pmc ADD CONSTRAINT scheduler_trigger_pmc_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE scheduler_trigger_pmc ADD CONSTRAINT scheduler_trigger_pmc_trgr_fk FOREIGN KEY(trgr) REFERENCES scheduler_trigger(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE scheduler_trigger_schedule ADD CONSTRAINT scheduler_trigger_schedule_trgr_fk 
                FOREIGN KEY(trgr) REFERENCES scheduler_trigger(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE scheduler_trigger ADD CONSTRAINT scheduler_trigger_trigger_details_fk 
                FOREIGN KEY(trigger_details) REFERENCES scheduler_trigger_details(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE tenant_sure_hqupdate_record ADD CONSTRAINT tenant_sure_hqupdate_record_file_fk 
                FOREIGN KEY(file) REFERENCES tenant_sure_hqupdate_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE tenant_sure_subscribers ADD CONSTRAINT tenant_sure_subscribers_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE vista_terms_v$document ADD CONSTRAINT vista_terms_v$document_owner_fk FOREIGN KEY(owner) REFERENCES vista_terms_v(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE vista_terms_v$document ADD CONSTRAINT vista_terms_v$document_value_fk FOREIGN KEY(value) REFERENCES legal_document(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE vista_terms_v ADD CONSTRAINT vista_terms_v_holder_fk FOREIGN KEY(holder) REFERENCES vista_terms(id)  DEFERRABLE INITIALLY DEFERRED;
        

        /**
        ***     ============================================================================================================
        ***     
        ***             INDEXES
        ***
        ***     ============================================================================================================
        **/
        
        
       


COMMIT;

SET client_min_messages = 'notice';
