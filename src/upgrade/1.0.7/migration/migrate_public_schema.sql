/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     Migration script for public schema - generated with by _dba_.generate_sql_sequences function
***
***     ===========================================================================================================
**/                                                     

SET client_min_messages = 'error';
SET search_path = 'public';

BEGIN TRANSACTION;


 -- Sequences to drop
 DROP SEQUENCE admin_pmc$credit_check_transaction_seq;
 DROP SEQUENCE insurance_tenant_sure_seq;
 DROP SEQUENCE insurance_tenant_sure_details$taxes_seq;
 
 -- Alter sequences
 ALTER SEQUENCE admin_onboarding_merchant_account_seq RENAME TO admin_pmc_merchant_account_index_seq;
 ALTER SEQUENCE scheduler_run_stats_seq RENAME TO scheduler_execution_report_seq;
 ALTER SEQUENCE admin_user_seq RENAME TO operations_user_seq;
 ALTER SEQUENCE admin_user_credential$behaviors_seq RENAME TO operations_user_credential$behaviors_seq;
 
  
 -- New sequences
 CREATE SEQUENCE default_equifax_limit_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE dev_card_service_simulation_card_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE dev_card_service_simulation_merchant_account_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE dev_card_service_simulation_token_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE dev_card_service_simulation_transaction_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE dev_card_service_simulator_config_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE dev_equifax_simulator_config_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE encrypted_storage_public_key_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE encrypted_storage_current_key_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE insurance_tenant_sure_report_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE operations_alert_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE payment_type_selection_policy_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE resident_portal_settings$custom_html_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE resident_portal_settings_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE resident_portal_settings$proxy_whitelist_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE scheduler_execution_report_message_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE scheduler_execution_report_section_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE tenant_sure_hqupdate_file_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE tenant_sure_hqupdate_record_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE tenant_sure_subscribers_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;


  
  -- Change owner to vista
 ALTER SEQUENCE admin_pmc_merchant_account_index_seq OWNER TO vista;
 ALTER SEQUENCE default_equifax_limit_seq OWNER TO vista ;
 ALTER SEQUENCE dev_card_service_simulation_card_seq OWNER TO vista ;
 ALTER SEQUENCE dev_card_service_simulation_merchant_account_seq OWNER TO vista ;
 ALTER SEQUENCE dev_card_service_simulation_token_seq OWNER TO vista ;
 ALTER SEQUENCE dev_card_service_simulation_transaction_seq OWNER TO vista ;
 ALTER SEQUENCE dev_card_service_simulator_config_seq OWNER TO vista ;
 ALTER SEQUENCE dev_equifax_simulator_config_seq OWNER TO vista ;
 ALTER SEQUENCE encrypted_storage_public_key_seq OWNER TO vista ;
 ALTER SEQUENCE encrypted_storage_current_key_seq OWNER TO vista ;
 ALTER SEQUENCE insurance_tenant_sure_report_seq OWNER TO vista ;
 ALTER SEQUENCE operations_alert_seq OWNER TO vista ;
 ALTER SEQUENCE operations_user_credential$behaviors_seq OWNER TO vista ;
 ALTER SEQUENCE operations_user_seq OWNER TO vista ;
 ALTER SEQUENCE payment_type_selection_policy_seq OWNER TO vista ;
 ALTER SEQUENCE resident_portal_settings$custom_html_seq OWNER TO vista ;
 ALTER SEQUENCE resident_portal_settings_seq OWNER TO vista ;
 ALTER SEQUENCE resident_portal_settings$proxy_whitelist_seq OWNER TO vista ;
 ALTER SEQUENCE scheduler_execution_report_message_seq OWNER TO vista ;
 ALTER SEQUENCE scheduler_execution_report_section_seq OWNER TO vista ;
 ALTER SEQUENCE tenant_sure_hqupdate_file_seq OWNER TO vista ;
 ALTER SEQUENCE tenant_sure_hqupdate_record_seq OWNER TO vista ;
 ALTER SEQUENCE tenant_sure_subscribers_seq OWNER TO vista ;

 
COMMIT;

SET client_min_messages = 'notice';


