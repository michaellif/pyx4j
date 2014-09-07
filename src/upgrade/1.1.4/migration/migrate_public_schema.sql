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
    DROP SEQUENCE communication_message$to_seq;
    DROP SEQUENCE communication_message_attachment_blob_seq;
    DROP SEQUENCE country_seq;
    DROP SEQUENCE crm_role$rls_seq;
    DROP SEQUENCE property_manager_seq;


    -- Altered Sequences
    
    ALTER SEQUENCE province_seq RENAME TO province_policy_node_seq;
    
    -- New sequences
    
    CREATE SEQUENCE aggregated_transfer$adjustments_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE aggregated_transfer$chargebacks_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE aggregated_transfer_adjustment_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE aggregated_transfer_chargeback_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE available_crm_report$rls_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE available_crm_report_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE cards_reconciliation_file_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE cards_reconciliation_record$adjustments_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE cards_reconciliation_record$chargebacks_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE cards_reconciliation_record_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE communication_delivery_handle_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE communication_message_category$dispatchers_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE communication_message_category$rls_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE communication_message_category_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1; 
    CREATE SEQUENCE communication_thread_policy_handle_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1; 
    CREATE SEQUENCE country_policy_node_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE decision_info_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE dev_card_service_simulation_reconciliation_record_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE dev_card_service_simulation_company_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE message_attachment_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE resident_portal_policy_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE signed_web_payment_term_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;


  
    -- Change owner to vista
    
    ALTER SEQUENCE aggregated_transfer$adjustments_seq OWNER TO vista ;
    ALTER SEQUENCE aggregated_transfer$chargebacks_seq OWNER TO vista ;
    ALTER SEQUENCE aggregated_transfer_adjustment_seq OWNER TO vista ;
    ALTER SEQUENCE aggregated_transfer_chargeback_seq OWNER TO vista ;
    ALTER SEQUENCE available_crm_report$rls_seq OWNER TO vista ;
    ALTER SEQUENCE available_crm_report_seq OWNER TO vista ;
    ALTER SEQUENCE cards_reconciliation_file_seq OWNER TO vista ;
    ALTER SEQUENCE cards_reconciliation_record$adjustments_seq OWNER TO vista ;
    ALTER SEQUENCE cards_reconciliation_record$chargebacks_seq OWNER TO vista ;
    ALTER SEQUENCE cards_reconciliation_record_seq OWNER TO vista ;
    ALTER SEQUENCE communication_delivery_handle_seq OWNER TO vista ;
    ALTER SEQUENCE communication_message_category$dispatchers_seq OWNER TO vista ;
    ALTER SEQUENCE communication_message_category$rls_seq OWNER TO vista ;
    ALTER SEQUENCE communication_message_category_seq OWNER TO vista ;
    ALTER SEQUENCE communication_thread_policy_handle_seq OWNER TO vista ;
    ALTER SEQUENCE country_policy_node_seq OWNER TO vista ;
    ALTER SEQUENCE decision_info_seq OWNER TO vista ;
    ALTER SEQUENCE dev_card_service_simulation_reconciliation_record_seq OWNER TO vista ;
    ALTER SEQUENCE dev_card_service_simulation_company_seq OWNER TO vista ;
    ALTER SEQUENCE message_attachment_blob_seq OWNER TO vista ;
    ALTER SEQUENCE resident_portal_policy_seq OWNER TO vista ;
    ALTER SEQUENCE province_policy_node_seq OWNER TO vista ;
    ALTER SEQUENCE signed_web_payment_term_seq OWNER TO vista;

       
COMMIT;

SET client_min_messages = 'notice';


