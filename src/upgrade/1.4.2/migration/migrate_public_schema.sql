/**
***     ===========================================================================================================
***
***     Migration script for public schema - generated with by _dba_.generate_sql_sequences function
***
***     ===========================================================================================================
**/                                                     

SET client_min_messages = 'error';
SET search_path = 'public';

BEGIN TRANSACTION;

    -- Renamed sequences 
    
    ALTER SEQUENCE customer_screening_personal_asset_seq RENAME TO customer_screening_asset_seq;
    ALTER SEQUENCE identification_document_folder_seq RENAME TO  identification_document_seq;

    -- Sequences to drop
    DROP SEQUENCE charge_line_list$charges_seq;
    DROP SEQUENCE charge_line_list_seq;
    DROP SEQUENCE charge_line_seq;
    DROP SEQUENCE charge_old_seq;
    DROP SEQUENCE customer_screening_legal_questions_seq;
    DROP SEQUENCE maintenance_request_schedule_seq;
    DROP SEQUENCE proof_of_asset_document_folder_seq;
    DROP SEQUENCE proof_of_income_document_folder_seq;
    
    -- New sequences
    CREATE SEQUENCE crm_user_delivery_preferences_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE crm_user_preferences_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE customer_delivery_preferences_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE customer_screening_legal_question_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE entry_instructions_note_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE entry_not_granted_alert_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE eviction_case$eviction_flow_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE eviction_case_init_data_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE eviction_case_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE eviction_document_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE eviction_document_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE eviction_flow_policy_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE eviction_flow_step_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE eviction_status_record_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE eviction_status_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE legal_questions_policy_item_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE legal_questions_policy_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE maintenance_request_policy$tenant_preferred_windows_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE maintenance_request_window_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE maintenance_request_work_order_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE n4_batch_init_data$lease_candidates_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE n4_batch_init_data_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE n4_batch_item$lease_tenants_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE n4_batch_item_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE n4_batch_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE n4_csdocument_type_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE n4_csservice_method_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE n4_cssignature_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE n4_csto_person_info_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE n4_rent_owing_for_period_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE proof_of_asset_document_type_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE proof_of_employment_document_type_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE proof_of_income_document_type_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE special_delivery_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    
    
     -- Change owner to vista
    ALTER SEQUENCE crm_user_delivery_preferences_seq OWNER TO vista ;
    ALTER SEQUENCE crm_user_preferences_seq OWNER TO vista ;
    ALTER SEQUENCE customer_delivery_preferences_seq OWNER TO vista ;
    ALTER SEQUENCE customer_screening_asset_seq OWNER TO vista ;
    ALTER SEQUENCE customer_screening_legal_question_seq OWNER TO vista ;
    ALTER SEQUENCE entry_instructions_note_seq OWNER TO vista ;
    ALTER SEQUENCE entry_not_granted_alert_seq OWNER TO vista ;
    ALTER SEQUENCE eviction_case$eviction_flow_seq OWNER TO vista ;
    ALTER SEQUENCE eviction_case_init_data_seq OWNER TO vista ;
    ALTER SEQUENCE eviction_case_seq OWNER TO vista ;
    ALTER SEQUENCE eviction_document_blob_seq OWNER TO vista ;
    ALTER SEQUENCE eviction_document_seq OWNER TO vista ;
    ALTER SEQUENCE eviction_flow_policy_seq OWNER TO vista ;
    ALTER SEQUENCE eviction_flow_step_seq OWNER TO vista ;
    ALTER SEQUENCE eviction_status_record_seq OWNER TO vista ;
    ALTER SEQUENCE eviction_status_seq OWNER TO vista ;
    ALTER SEQUENCE identification_document_seq OWNER TO vista ;
    ALTER SEQUENCE legal_questions_policy_item_seq OWNER TO vista ;
    ALTER SEQUENCE legal_questions_policy_seq OWNER TO vista ;
    ALTER SEQUENCE maintenance_request_policy$tenant_preferred_windows_seq OWNER TO vista ;
    ALTER SEQUENCE maintenance_request_window_seq OWNER TO vista ;
    ALTER SEQUENCE maintenance_request_work_order_seq OWNER TO vista ;
    ALTER SEQUENCE n4_batch_init_data$lease_candidates_seq OWNER TO vista ;
    ALTER SEQUENCE n4_batch_init_data_seq OWNER TO vista ;
    ALTER SEQUENCE n4_batch_item$lease_tenants_seq OWNER TO vista ;
    ALTER SEQUENCE n4_batch_item_seq OWNER TO vista ;
    ALTER SEQUENCE n4_batch_seq OWNER TO vista ;
    ALTER SEQUENCE n4_csdocument_type_seq OWNER TO vista ;
    ALTER SEQUENCE n4_csservice_method_seq OWNER TO vista ;
    ALTER SEQUENCE n4_cssignature_seq OWNER TO vista ;
    ALTER SEQUENCE n4_csto_person_info_seq OWNER TO vista ;
    ALTER SEQUENCE n4_rent_owing_for_period_seq OWNER TO vista ;
    ALTER SEQUENCE proof_of_asset_document_type_seq OWNER TO vista ;
    ALTER SEQUENCE proof_of_employment_document_type_seq OWNER TO vista ;
    ALTER SEQUENCE proof_of_income_document_type_seq OWNER TO vista ;
    ALTER SEQUENCE special_delivery_seq OWNER TO vista ;

        
COMMIT;

SET client_min_messages = 'notice';


