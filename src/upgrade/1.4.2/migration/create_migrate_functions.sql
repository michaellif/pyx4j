/**
***     ======================================================================================================================
***
***             version 1.4.2 PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_142(v_schema_name TEXT) RETURNS VOID AS
$$
DECLARE
        v_rowcount      INT     := 0;
BEGIN
        EXECUTE 'SET search_path = '||v_schema_name;
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP CONSTRAINTS SECTION
        ***
        ***     ======================================================================================================
        **/
        
        -- foreign keys
        
        ALTER TABLE charge_line_list$charges DROP CONSTRAINT charge_line_list$charges_owner_fk;
        ALTER TABLE charge_line_list$charges DROP CONSTRAINT charge_line_list$charges_value_fk;
        ALTER TABLE customer_screening_personal_asset DROP CONSTRAINT customer_screening_personal_asset_owner_fk;
        ALTER TABLE customer_screening_v DROP CONSTRAINT customer_screening_v_legal_questions_fk;
        ALTER TABLE identification_document_folder DROP CONSTRAINT identification_document_folder_id_type_fk;
        ALTER TABLE identification_document_folder DROP CONSTRAINT identification_document_folder_owner_fk;
        ALTER TABLE identification_document_file DROP CONSTRAINT identification_document_file_owner_fk;
        ALTER TABLE lease_billing_type_policy_item DROP CONSTRAINT lease_billing_type_policy_item_lease_billing_policy_fk;
        ALTER TABLE maintenance_request_schedule DROP CONSTRAINT maintenance_request_schedule_request_fk;
        ALTER TABLE legal_letter DROP CONSTRAINT legal_letter_lease_fk;
        ALTER TABLE legal_letter DROP CONSTRAINT legal_letter_status_fk;
        ALTER TABLE legal_status DROP CONSTRAINT legal_status_lease_fk;
        ALTER TABLE legal_status DROP CONSTRAINT legal_status_set_by_fk;
        ALTER TABLE permission_to_enter_note DROP CONSTRAINT permission_to_enter_note_locale_fk;
        ALTER TABLE proof_of_asset_document_file DROP CONSTRAINT proof_of_asset_document_file_owner_fk;
        ALTER TABLE proof_of_asset_document_folder DROP CONSTRAINT proof_of_asset_document_folder_owner_fk;
        ALTER TABLE proof_of_income_document_file DROP CONSTRAINT proof_of_income_document_file_owner_fk;
        ALTER TABLE proof_of_income_document_folder DROP CONSTRAINT proof_of_income_document_folder_owner_fk;
        
        -- primary keys
        
        ALTER TABLE charge_line DROP CONSTRAINT charge_line_pk;
        ALTER TABLE charge_line_list DROP CONSTRAINT charge_line_list_pk;
        ALTER TABLE charge_line_list$charges DROP CONSTRAINT charge_line_list$charges_pk;
        ALTER TABLE charge_old DROP CONSTRAINT charge_old_pk;
        ALTER TABLE customer_screening_personal_asset DROP CONSTRAINT customer_screening_personal_asset_pk;
        ALTER TABLE customer_screening_legal_questions DROP CONSTRAINT customer_screening_legal_questions_pk;
        ALTER TABLE identification_document_folder DROP CONSTRAINT identification_document_folder_pk;
        ALTER TABLE legal_letter_blob DROP CONSTRAINT legal_letter_blob_pk;
        ALTER TABLE legal_letter DROP CONSTRAINT legal_letter_pk;
        ALTER TABLE legal_status DROP CONSTRAINT legal_status_pk;
        ALTER TABLE maintenance_request_schedule DROP CONSTRAINT maintenance_request_schedule_pk;
        ALTER TABLE proof_of_asset_document_folder DROP CONSTRAINT proof_of_asset_document_folder_pk;
        ALTER TABLE proof_of_income_document_folder DROP CONSTRAINT proof_of_income_document_folder_pk;
        
        
        -- check constraints
        
        ALTER TABLE available_crm_report DROP CONSTRAINT available_crm_report_report_type_e_ck;
        ALTER TABLE billing_arrears_snapshot DROP CONSTRAINT billing_arrears_snapshot_legal_status_e_ck;
        ALTER TABLE building_amenity DROP CONSTRAINT building_amenity_building_amenity_type_e_ck;
        ALTER TABLE communication_message_category DROP CONSTRAINT communication_message_category_category_type_e_ck;
        ALTER TABLE communication_message_category DROP CONSTRAINT communication_message_category_ticket_type_e_ck;
        ALTER TABLE customer_screening_personal_asset DROP CONSTRAINT customer_screening_personal_asset_asset_type_e_ck;
        ALTER TABLE email_template DROP CONSTRAINT email_template_template_type_e_ck;
        ALTER TABLE floorplan_amenity DROP CONSTRAINT floorplan_amenity_floorplan_type_e_ck;
        ALTER TABLE lead DROP CONSTRAINT lead_ref_source_e_ck;
        ALTER TABLE legal_status DROP CONSTRAINT legal_status_id_discriminator_ck;
        ALTER TABLE legal_status DROP CONSTRAINT legal_status_status_e_ck;
        ALTER TABLE maintenance_request DROP CONSTRAINT maintenance_request_preferred_time1_e_ck;
        ALTER TABLE maintenance_request DROP CONSTRAINT maintenance_request_preferred_time2_e_ck;
        ALTER TABLE notes_and_attachments DROP CONSTRAINT notes_and_attachments_owner_discriminator_d_ck;



        /**
        ***     ======================================================================================================
        ***
        ***             DROP INDEXES
        ***
        ***     ======================================================================================================
        **/
        
        
        DROP INDEX charge_line_list$charges_owner_idx;
        DROP INDEX lease_billing_type_policy_item_lease_billing_policy_idx;
        DROP INDEX maintenance_request_schedule_request_idx;

        
        /**
        ***    ======================================================================================================
        ***
        ***             Very special case for billing_arrears_snapshot_from_date_to_date_idx
        ***             This index doesn''t exist in new schemas, and may be bloated for schemas
        ***             where it does exists due to removal of extra rows from billing_arrears_snapshot table 
        ***             So I''ll just drop and recreate it
        ***
        ***     ===================================================================================================== 
        **/
        
        DROP INDEX IF EXISTS billing_arrears_snapshot_from_date_to_date_idx;
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP TABLES 
        ***
        ***     ======================================================================================================
        **/
        
        
        /**
        ***     ======================================================================================================
        ***
        ***             NEW AND ALTERED TABLES 
        ***
        ***     ======================================================================================================
        **/
        
        -- application_approval_checklist_policy
        
        CREATE TABLE application_approval_checklist_policy
        (
            id                              BIGINT              NOT NULL,
            node                            BIGINT,
            node_discriminator              VARCHAR(50),
            updated                         TIMESTAMP,
                CONSTRAINT application_approval_checklist_policy_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE application_approval_checklist_policy OWNER TO vista;
        
        
        -- application_approval_checklist_policy_item
        
        CREATE TABLE application_approval_checklist_policy_item
        (
            id                              BIGINT              NOT NULL,
            policy                          BIGINT              NOT NULL,
            order_in_policy                 INTEGER,
            item_to_check                   VARCHAR(500),
                CONSTRAINT application_approval_checklist_policy_item_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE application_approval_checklist_policy_item OWNER TO vista;
        
        -- application_documentation_policy
        
        ALTER TABLE application_documentation_policy    ADD COLUMN mandatory_proof_of_asset BOOLEAN,
                                                        ADD COLUMN mandatory_proof_of_employment BOOLEAN;
                                                        
        -- approval_checklist_item
        
        CREATE TABLE approval_checklist_item
        (
            id                              BIGINT              NOT NULL,
            lease_application               BIGINT              NOT NULL,
            order_in_lease_application      INTEGER,
            decided_by                      BIGINT,
            decision_date                   DATE,
            notes                           VARCHAR(500),
            task                            VARCHAR(500),
            status                          VARCHAR(500),
                CONSTRAINT approval_checklist_item_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE approval_checklist_item OWNER TO vista;
        
                                                        
                                                        
        -- autopay_agreement
        
        ALTER TABLE autopay_agreement   RENAME COLUMN creation_date TO created;
        
        -- billing_arrears_snapshot
        
        ALTER TABLE billing_arrears_snapshot ALTER COLUMN legal_status TYPE VARCHAR(500);
        
        
        -- broadcast_event
        
        CREATE TABLE broadcast_event
        (
            id                              BIGINT              NOT NULL,
            template                        BIGINT,
            message_date                    TIMESTAMP,
                CONSTRAINT broadcast_event_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE broadcast_event OWNER TO vista;
        
        
        -- broadcast_event$threads
        
        CREATE TABLE broadcast_event$threads
        (
            id                              BIGINT              NOT NULL,
            owner                           BIGINT,
            value                           BIGINT,
            seq                             INTEGER,
                CONSTRAINT broadcast_event$threads_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE broadcast_event$threads OWNER TO vista;
        
        
        -- broadcast_template
        
        CREATE TABLE broadcast_template
        (
            id                              BIGINT              NOT NULL,
            content                         VARCHAR(48000),
            template_type                   VARCHAR(50),
            name                            VARCHAR(78),
            subject                         VARCHAR(500),
            audience_type                   VARCHAR(50),
            message_type                    VARCHAR(50),
            allowed_reply                   BOOLEAN,
            category                        BIGINT              NOT NULL,
            high_importance                 BOOLEAN,
                CONSTRAINT broadcast_template_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE broadcast_template OWNER TO vista;
       
        
        --  broadcast_template_schedules
        
        CREATE TABLE  broadcast_template_schedules
        (
            id                              BIGINT              NOT NULL,
                CONSTRAINT broadcast_template_schedules_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE  broadcast_template_schedules OWNER TO vista;
        
        
        -- broadcast_template_schedules$schedules
        
        CREATE TABLE broadcast_template_schedules$schedules
        (
            id                              BIGINT              NOT NULL,
            owner                           BIGINT,
            value                           BIGINT,
            seq                             INTEGER,
                CONSTRAINT broadcast_template_schedules$schedules_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE broadcast_template_schedules$schedules OWNER TO vista;
        
        
        -- communication_broadcast_attachment
        
        CREATE TABLE communication_broadcast_attachment
        (
            id                              BIGINT              NOT NULL,
            file_file_name                  VARCHAR(500),
            file_updated_timestamp          BIGINT,
            file_cache_version              INTEGER,
            file_file_size                  INTEGER,
            file_content_mime_type          VARCHAR(500),
            file_blob_key                   BIGINT,
            template                        BIGINT,
            description                     VARCHAR(500),
                CONSTRAINT communication_broadcast_attachment_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE communication_broadcast_attachment OWNER TO vista;
        
        -- communication_delivery_handle
        
        ALTER TABLE communication_delivery_handle ADD COLUMN message_type VARCHAR(50);
        
                
        
        -- communication_message
        
        ALTER TABLE communication_message   ADD COLUMN is_system BOOLEAN,
                                            ADD COLUMN on_behalf BIGINT,
                                            ADD COLUMN on_behalf_discriminator VARCHAR(50),
                                            ADD COLUMN on_behalf_visible BOOLEAN;
                                            
        ALTER TABLE communication_message RENAME COLUMN text TO content;
                                            
                                            
        -- communication_thread
        
        ALTER TABLE communication_thread    ADD COLUMN associated BIGINT,
                                            ADD COLUMN associated_discriminator VARCHAR(50),
                                            ADD COLUMN delivery_method VARCHAR(50),
                                            ADD COLUMN special_delivery BIGINT,
                                            ADD COLUMN special_delivery_discriminator VARCHAR(50);
                                            
        -- crm_user_delivery_preferences
        
        CREATE TABLE crm_user_delivery_preferences
        (
            id                          BIGINT                  NOT NULL,
            user_preferences            BIGINT,
            promotional_delivery        VARCHAR(50),
            informational_delivery      VARCHAR(50),
                CONSTRAINT crm_user_delivery_preferences_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE crm_user_delivery_preferences OWNER TO vista;
        
        
        -- crm_user_preferences
        
        CREATE TABLE crm_user_preferences
        (
            id                          BIGINT                  NOT NULL,
            logical_date_format         VARCHAR(500),
            date_time_format            VARCHAR(500),
            crm_user                    BIGINT,
                CONSTRAINT crm_user_preferences_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE crm_user_preferences OWNER TO vista;
        
        -- customer_credit_check
        
        ALTER TABLE customer_credit_check   ADD COLUMN building BIGINT,
                                            ADD COLUMN screene BIGINT,
                                            ADD COLUMN screene_discriminator VARCHAR(50);
        
        -- customer_delivery_preferences
        
        CREATE TABLE customer_delivery_preferences
        (
            id                          BIGINT                  NOT NULL,
            user_preferences            BIGINT,
            promotional_delivery        VARCHAR(50),
            informational_delivery      VARCHAR(50),
                CONSTRAINT customer_delivery_preferences_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE customer_delivery_preferences OWNER TO vista;
        
        
        -- customer_screening_personal_asset/customer_screening_asset
        
        ALTER TABLE customer_screening_personal_asset RENAME TO customer_screening_asset;
        
        
        -- customer_screening_income_info
        
        ALTER TABLE customer_screening_income_info RENAME COLUMN monthly_amount TO income_amount;
        ALTER TABLE customer_screening_income_info RENAME COLUMN monthly_revenue TO revenue_amount;
        
        ALTER TABLE customer_screening_income_info  ADD COLUMN amount_period VARCHAR(50),
                                                    ADD COLUMN revenue_amount_period VARCHAR(50);
                                                    
                                                    
        -- customer_screening_legal_question
        
        CREATE TABLE customer_screening_legal_question
        (
            id                          BIGINT                  NOT NULL,
            owner                       BIGINT                  NOT NULL,
            order_in_owner              INTEGER,
            question                    VARCHAR(500),
            answer                      BOOLEAN,
            notes                       VARCHAR(500),
                CONSTRAINT customer_screening_legal_question_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE customer_screening_legal_question OWNER TO vista;
        
        
        -- customer_screening_v
        
        ALTER TABLE customer_screening_v RENAME COLUMN create_date TO created;
        ALTER TABLE customer_screening_v ALTER COLUMN created TYPE TIMESTAMP;
        ALTER TABLE customer_screening_v RENAME COLUMN update_date TO updated;
        ALTER TABLE customer_screening_v ALTER COLUMN updated TYPE TIMESTAMP;
        
        
        -- dashboard_metadata
        
        ALTER TABLE dashboard_metadata ALTER COLUMN encoded_layout TYPE VARCHAR(2048);
        
        -- email_template
        
        ALTER TABLE email_template ALTER COLUMN content TYPE VARCHAR(48000);
        
        -- entry_instructions_note
        
        CREATE TABLE entry_instructions_note
        (
            id                          BIGINT                  NOT NULL,
            locale                      VARCHAR(50),
            policy                      BIGINT                  NOT NULL,  
            caption                     VARCHAR(500),
            text                        VARCHAR(500),
            order_by                    INTEGER,
                    CONSTRAINT entry_instructions_note_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE entry_instructions_note OWNER TO vista;
        
        
        -- entry_not_granted_alert
        
        CREATE TABLE entry_not_granted_alert
        (
            id                          BIGINT                  NOT NULL,
            locale                      VARCHAR(50),
            policy                      BIGINT                  NOT NULL,  
            title                       VARCHAR(500),
            text                        VARCHAR(500),
            order_by                    INTEGER,
                CONSTRAINT entry_not_granted_alert_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE entry_not_granted_alert OWNER TO vista;
        
        
        -- eviction_case
        
        CREATE TABLE eviction_case
        (
            id                          BIGINT                  NOT NULL,
            lease                       BIGINT,
            created_on                  TIMESTAMP,
            updated_on                  TIMESTAMP,
            closed_on                   TIMESTAMP,
            created_by                  BIGINT,
            note                        VARCHAR(500),
            eviction_flow_policy        BIGINT,
                CONSTRAINT eviction_case_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE eviction_case OWNER TO vista;
                
        
        -- eviction_case_init_data
        
        CREATE TABLE eviction_case_init_data
        (
            id                          BIGINT                  NOT NULL,
            lease                       BIGINT,
                CONSTRAINT eviction_case_init_data_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE eviction_case_init_data OWNER TO vista;
        
        
        -- eviction_case_status
        
        CREATE TABLE eviction_case_status
        (
            id                          BIGINT                  NOT NULL,
            id_discriminator            VARCHAR(64)             NOT NULL,
            eviction_case               BIGINT                  NOT NULL,
            eviction_step               BIGINT,
            added_on                    TIMESTAMP,
            added_by                    BIGINT,
            note                        VARCHAR(500),
            lease_arrears               BIGINT,
            n4_data                     BIGINT,
            originating_batch           BIGINT,
            termination_date            DATE,
            expiry_date                 DATE,
            cancellation_balance        NUMERIC(18,2),
                CONSTRAINT eviction_case_status_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE eviction_case_status OWNER TO vista;
        
        
        -- eviction_case_status$generated_forms
        
        CREATE TABLE eviction_case_status$generated_forms
        (
            id                              BIGINT              NOT NULL,
            owner                           BIGINT,
            value                           BIGINT,
            seq                             INT,
                CONSTRAINT eviction_case_status$generated_forms_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE eviction_case_status$generated_forms OWNER TO vista;
        
        
        -- eviction_document
        
        CREATE TABLE eviction_document
        (
            id                          BIGINT                  NOT NULL,
            file_file_name              VARCHAR(500),
            file_updated_timestamp      BIGINT,
            file_cache_version          INTEGER,
            file_file_size              INTEGER,
            file_content_mime_type      VARCHAR(500),
            file_blob_key               BIGINT,
            lease                       BIGINT,
            record                      BIGINT,
            added_on                    TIMESTAMP,
            title                       VARCHAR(500),
            note                        VARCHAR(500),
            print_order                 INTEGER,
                CONSTRAINT eviction_document_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE eviction_document OWNER TO vista;
        
        -- eviction_document_blob
        
        CREATE TABLE eviction_document_blob
        (
            id                          BIGINT                  NOT NULL,
            name                        VARCHAR(500),
            data                        BYTEA,
            content_type                VARCHAR(500),
            updated                     TIMESTAMP,
            created                     TIMESTAMP,
                CONSTRAINT eviction_document_blob_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE eviction_document_blob OWNER TO vista;
        
        
        -- eviction_flow_policy
        
        CREATE TABLE eviction_flow_policy
        (
            id                          BIGINT                  NOT NULL,
            node                        BIGINT,
            node_discriminator          VARCHAR(50),
            updated                     TIMESTAMP,
            x                           VARCHAR(500),
                CONSTRAINT eviction_flow_policy_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE eviction_flow_policy OWNER TO vista;
        
        
        -- eviction_flow_step
        
        CREATE TABLE eviction_flow_step
        (
            id                          BIGINT                  NOT NULL,
            policy                      BIGINT                  NOT NULL,
            step_type                   VARCHAR(50),
            name                        VARCHAR(500),
            description                 VARCHAR(500),
                CONSTRAINT eviction_flow_step_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE eviction_flow_step OWNER TO vista;
        
        
        
        -- eviction_status_record
        
        CREATE TABLE eviction_status_record
        (
            id                              BIGINT              NOT NULL,
            eviction_status                 BIGINT              NOT NULL,
            eviction_status_discriminator   VARCHAR(50)         NOT NULL,
            note                            VARCHAR(500),
            added_on                        TIMESTAMP,
            added_by                        BIGINT,
                CONSTRAINT eviction_status_record_pk PRIMARY KEY(id) 
        );
        
        ALTER TABLE eviction_status_record OWNER TO vista;
        
        
        -- financial_terms_policy
        
        CREATE TABLE financial_terms_policy
        (
            id                              BIGINT              NOT NULL,
            node                            BIGINT,
            node_discriminator              VARCHAR(50),
            updated                         TIMESTAMP,
            billing_terms                   BIGINT,
            preauthorized_payment_echeck_terms  BIGINT,
            preauthorized_payment_card_terms    BIGINT,
                CONSTRAINT financial_terms_policy_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE financial_terms_policy OWNER TO vista;
        
        
        -- financial_terms_policy_item
        
        CREATE TABLE financial_terms_policy_item
        (
            id                              BIGINT              NOT NULL,
            caption                         VARCHAR(500),
            content                         VARCHAR(300000),
            enabled                         BOOLEAN,
                CONSTRAINT financial_terms_policy_item_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE financial_terms_policy_item OWNER TO vista;
        
        -- identification_document_folder/identification_document
        
        ALTER TABLE identification_document_folder RENAME TO identification_document;
        
        -- identification_document_file
        
        ALTER TABLE identification_document_file    ADD COLUMN notes VARCHAR(500),  
                                                    ADD COLUMN verified BOOLEAN,
                                                    ADD COLUMN verified_by BIGINT,
                                                    ADD COLUMN verified_on TIMESTAMP;
                                                    
        -- identification_document_type
        
        ALTER TABLE identification_document_type ADD COLUMN notes VARCHAR(500);
        
        
        -- lead
        
        ALTER TABLE lead RENAME COLUMN ref_source TO reference_source;
        
        -- lease_application
        
        ALTER TABLE lease_application ADD COLUMN reference_source VARCHAR(50);
        
        -- lease_application$approval_checklist
        
        CREATE TABLE lease_application$approval_checklist
        (
            id                              BIGINT              NOT NULL,
            owner                           BIGINT,
            value                           BIGINT,
            seq                             INTEGER,
                CONSTRAINT lease_application$approval_checklist_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE lease_application$approval_checklist OWNER TO vista;
        
        -- lease_billing_type_policy_item
        
        ALTER TABLE lease_billing_type_policy_item RENAME COLUMN lease_billing_policy TO policy;
        
        -- legal_questions_policy
        
        CREATE TABLE legal_questions_policy
        (
            id                          BIGINT                  NOT NULL,
            node                        BIGINT,
            node_discriminator          VARCHAR(50),
            updated                     TIMESTAMP,
            enabled                     BOOLEAN,
                CONSTRAINT legal_questions_policy_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE legal_questions_policy OWNER TO vista;
        
        -- legal_questions_policy_item
        
        CREATE TABLE legal_questions_policy_item
        (
            id                          BIGINT                  NOT NULL,
            locale                      VARCHAR(50),
            policy                      BIGINT                  NOT NULL,
            order_in_policy             INTEGER,
            question                    VARCHAR(500),
                CONSTRAINT legal_questions_policy_item_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE legal_questions_policy_item OWNER TO vista;
        
        
        -- maintenance_request
        
        ALTER TABLE maintenance_request ADD COLUMN preferred_time1_time_from TIME,
                                        ADD COLUMN preferred_time1_time_to TIME,
                                        ADD COLUMN preferred_time2_time_from TIME,
                                        ADD COLUMN preferred_time2_time_to TIME;
                                        
        -- maintenance_request_policy
        
        ALTER TABLE maintenance_request_policy  ADD COLUMN allow24_hour_schedule BOOLEAN,
                                                ADD COLUMN max_allowed_window_hours INTEGER,
                                                ADD COLUMN min_advance_notice_hours INTEGER,
                                                ADD COLUMN permission_granted_by_default BOOLEAN,
                                                ADD COLUMN scheduling_window_time_from TIME,
                                                ADD COLUMN scheduling_window_time_to TIME;
                                                
        -- maintenance_request_policy$tenant_preferred_windows
        
        CREATE TABLE maintenance_request_policy$tenant_preferred_windows
        (
            id                              BIGINT              NOT NULL,
            owner                           BIGINT,
            value                           BIGINT,
            seq                             INTEGER,
                CONSTRAINT maintenance_request_policy$tenant_preferred_windows_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE maintenance_request_policy$tenant_preferred_windows OWNER TO vista;
        
        
        -- maintenance_request_window
        
        CREATE TABLE maintenance_request_window
        (
            id                              BIGINT              NOT NULL,
            time_from                       TIME,
            time_to                         TIME,
                CONSTRAINT maintenance_request_window_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE maintenance_request_window OWNER TO vista;
        
        
        -- maintenance_request_schedule
        
        ALTER TABLE maintenance_request_schedule RENAME TO maintenance_request_work_order;
        
        -- maintenance_request_work_order
        
        ALTER TABLE maintenance_request_work_order RENAME COLUMN scheduled_time_from TO scheduled_time_time_from;
        ALTER TABLE maintenance_request_work_order RENAME COLUMN scheduled_time_to TO scheduled_time_time_to;
        
        ALTER TABLE maintenance_request_work_order  ADD COLUMN created TIMESTAMP,
                                                    ADD COLUMN updated TIMESTAMP,
                                                    ADD COLUMN is_emergency_work BOOLEAN;
        
        
        -- n4_batch
        
        CREATE TABLE n4_batch
        (
            id                              BIGINT              NOT NULL,
            building                        BIGINT,
            delivery_method                 VARCHAR(50),
            delivery_date                   DATE,
            is_ready_for_service            BOOLEAN,
            service_date                    DATE,
            company_legal_name              VARCHAR(500),
            company_address_street_number   VARCHAR(500),
            company_address_street_name     VARCHAR(500),
            company_address_suite_number    VARCHAR(500),
            company_address_city            VARCHAR(500),
            company_address_province        VARCHAR(500),
            company_address_country         VARCHAR(50),
            company_address_postal_code     VARCHAR(500),
            use_agent_contact_info_n4       BOOLEAN,
            phone_number                    VARCHAR(500),
            phone_number_cs                 VARCHAR(500),
            fax_number                      VARCHAR(500),
            email_address                   VARCHAR(500),
            use_agent_contact_info_cs       BOOLEAN,
            is_landlord                     BOOLEAN,
            signature_date                  DATE,
            servicing_agent                 BIGINT,
            signing_agent                   BIGINT,
            name                            VARCHAR(500),
            created                         TIMESTAMP,
            termination_date_option         VARCHAR(50),
            n4policy                        BIGINT,
                CONSTRAINT n4_batch_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE n4_batch OWNER TO vista;
        
        
        -- n4_batch_item
        
        CREATE TABLE n4_batch_item
        (
            id                              BIGINT              NOT NULL,
            batch                           BIGINT,
            lease                           BIGINT,
            lease_arrears                   BIGINT,
            service_date                    TIMESTAMP,
                CONSTRAINT n4_batch_item_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE n4_batch_item OWNER TO vista;
        
        
        -- n4_lease_arrears
        
        CREATE TABLE n4_lease_arrears
        (
            id                              BIGINT              NOT NULL,
            lease                           BIGINT,
            created                         TIMESTAMP,
            total_rent_owning               NUMERIC(18,2),
                CONSTRAINT n4_lease_arrears_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE n4_lease_arrears OWNER TO vista;
        
        
        -- n4_lease_data
        
        CREATE TABLE n4_lease_data
        (
            id                              BIGINT              NOT NULL,
            created                         TIMESTAMP,
            termination_date_option         VARCHAR(50),
            service_date                    DATE,
            delivery_method                 VARCHAR(50),
            delivery_date                   DATE,
            company_legal_name              VARCHAR(500),
            company_address_street_number   VARCHAR(500),
            company_address_street_name     VARCHAR(500),
            company_address_suite_number    VARCHAR(500),
            company_address_city            VARCHAR(500),
            company_address_province        VARCHAR(500),
            company_address_country         VARCHAR(50),
            company_address_postal_code     VARCHAR(500),
            use_agent_contact_info_n4       BOOLEAN,
            phone_number                    VARCHAR(500),
            fax_number                      VARCHAR(500),
            email_address                   VARCHAR(500),
            use_agent_contact_info_cs       BOOLEAN,
            phone_number_cs                 VARCHAR(500),
            is_landlord                     BOOLEAN,
            signature_date                  DATE,
            signing_agent                   BIGINT,
            servicing_agent                 BIGINT,
            lease                           BIGINT,
                CONSTRAINT n4_lease_data_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE n4_lease_data OWNER TO vista;
        
        -- n4_policy
        
        ALTER TABLE n4_policy   ADD COLUMN agent_selection_method_cs VARCHAR(50),
                                ADD COLUMN agent_selection_method_n4 VARCHAR(50),
                                ADD COLUMN use_agent_contact_info_cs BOOLEAN,
                                ADD COLUMN use_agent_contact_info_n4 BOOLEAN,
                                ADD COLUMN use_agent_signature_cs BOOLEAN,
                                ADD COLUMN use_agent_signature_n4 BOOLEAN,
                                ADD COLUMN phone_number_cs VARCHAR(500);
                                
                                
        -- n4_unpaid_charge
        
        CREATE TABLE n4_unpaid_charge
        (
            id                          BIGINT                  NOT NULL,
            parent                      BIGINT,
            from_date                   DATE,
            to_date                     DATE,
            rent_charged                NUMERIC(18,2),
            rent_paid                   NUMERIC(18,2),
            rent_owing                  NUMERIC(18,2),
            ar_code                     BIGINT,
                CONSTRAINT n4_unpaid_charge_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE n4_unpaid_charge OWNER TO vista;
        
        
        -- notes_and_attachments
        
        ALTER TABLE notes_and_attachments ALTER COLUMN created TYPE TIMESTAMP;
        ALTER TABLE notes_and_attachments ALTER COLUMN updated TYPE TIMESTAMP;
        
        
        -- payment_method
        
        ALTER TABLE payment_method RENAME COLUMN creation_date TO created;
        ALTER TABLE payment_method ALTER COLUMN created TYPE TIMESTAMP;
        
        
        -- payment_posting_batch
        
        ALTER TABLE payment_posting_batch RENAME COLUMN creation_date TO created;
        ALTER TABLE payment_posting_batch ALTER COLUMN created TYPE TIMESTAMP;
        
        -- payment_record
        
        ALTER TABLE payment_record RENAME COLUMN created_date TO created;
        
        -- permission_to_enter_note
        
        ALTER TABLE permission_to_enter_note RENAME COLUMN locale TO locale_old;
        ALTER TABLE permission_to_enter_note    ADD COLUMN caption VARCHAR(500),
                                                ADD COLUMN locale VARCHAR(50);
                                                
        -- proof_of_asset_document_file
        
        ALTER TABLE proof_of_asset_document_file    ADD COLUMN verified BOOLEAN,
                                                    ADD COLUMN verified_by BIGINT,
                                                    ADD COLUMN verified_on TIMESTAMP,
                                                    ADD COLUMN notes VARCHAR(500);
                                                    
                                                    
        -- proof_of_asset_document_type
        
        CREATE TABLE proof_of_asset_document_type
        (
            id                              BIGINT              NOT NULL,
            policy                          BIGINT              NOT NULL,
            order_in_policy                 INTEGER,
            name                            VARCHAR(500),
            importance                      VARCHAR(50),
            asset_type                      VARCHAR(50)         NOT NULL,
            notes                           VARCHAR(500),
                CONSTRAINT proof_of_asset_document_type_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE proof_of_asset_document_type OWNER TO vista;
        
        
        -- proof_of_employment_document_type
        
        CREATE TABLE proof_of_employment_document_type
        (
            id                              BIGINT              NOT NULL,
            policy                          BIGINT              NOT NULL,
            order_in_policy                 INTEGER,
            name                            VARCHAR(500),
            importance                      VARCHAR(50),
            income_source                   VARCHAR(50)         NOT NULL,
            notes                           VARCHAR(500),
                CONSTRAINT proof_of_employment_document_type_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE proof_of_employment_document_type OWNER TO vista;
        
        
        -- proof_of_income_document_file
        
        ALTER TABLE proof_of_income_document_file   ADD COLUMN verified BOOLEAN,
                                                    ADD COLUMN verified_by BIGINT,
                                                    ADD COLUMN verified_on TIMESTAMP,
                                                    ADD COLUMN notes VARCHAR(500);
                                                    
                                                    
        -- proof_of_income_document_type
        
        CREATE TABLE proof_of_income_document_type
        (
            id                              BIGINT              NOT NULL,
            policy                          BIGINT              NOT NULL,
            order_in_policy                 INTEGER,
            name                            VARCHAR(500),
            importance                      VARCHAR(50),
            income_source                   VARCHAR(50)         NOT NULL,
            notes                           VARCHAR(500),
                CONSTRAINT proof_of_income_document_type_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE proof_of_income_document_type OWNER TO vista;
                                                    
        
        -- restrictions_policy
        
        ALTER TABLE restrictions_policy ADD COLUMN emergency_contacts_is_mandatory BOOLEAN,
                                        ADD COLUMN emergency_contacts_number INTEGER,
                                        ADD COLUMN max_number_of_employments INTEGER,
                                        ADD COLUMN min_employment_duration INTEGER,
                                        ADD COLUMN reference_source_is_mandatory BOOLEAN;
                                        
                                        
        -- schedule
        
        CREATE TABLE schedule
        (
            id                          BIGINT                  NOT NULL,
            frequency                   VARCHAR(50),
            start_date                  DATE,
            end_date                    DATE,
            on_date                     DATE,
            template                    BIGINT                  NOT NULL,
                CONSTRAINT schedule_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE schedule OWNER TO vista;
                                        
                                        
        -- special_delivery
        
        CREATE TABLE special_delivery
        (
            id                              BIGINT              NOT NULL,
            id_discriminator                VARCHAR(64)         NOT NULL,
            delivered_text                  VARCHAR(48000),
            date_from                       DATE,
            notification_type               VARCHAR(50),
            date_to                         DATE,
                CONSTRAINT special_delivery_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE special_delivery OWNER TO vista;
        
        -- status_selection_item
        
        CREATE TABLE status_selection_item
        (
            id                              BIGINT              NOT NULL,
            checklist_item                  BIGINT              NOT NULL,
            order_in_checklist_item         INTEGER,
            status_selection                VARCHAR(500),
                CONSTRAINT status_selection_item_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE status_selection_item OWNER TO vista;
        
        -- status_selection_policy_item
        
        CREATE TABLE status_selection_policy_item
        (
            id                              BIGINT              NOT NULL,
            checklist_item                  BIGINT              NOT NULL,
            order_in_checklist_item         INTEGER,
            status_selection                VARCHAR(500),
                CONSTRAINT status_selection_policy_item_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE status_selection_policy_item OWNER TO vista;
        
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        -- application_documentation_policy
        
        EXECUTE 'UPDATE '||v_schema_name||'.application_documentation_policy '
                ||'SET  mandatory_proof_of_asset = FALSE, '
                ||'     mandatory_proof_of_employment = mandatory_proof_of_income ';
                
        -- available_crm_report
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.available_crm_report (id,report_type) '
                ||'VALUES (nextval(''public.available_crm_report_seq''),''AutoPayReconciliation'')';
                
        -- available_crm_report$rls
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.available_crm_report$rls (id,owner,value) '
                ||'(SELECT  nextval(''public.available_crm_report$rls_seq'') AS id,'
                ||'         a.id AS owner, r.id AS value '
                ||'FROM     '||v_schema_name||'.available_crm_report a, '
                ||'         '||v_schema_name||'.crm_role r '
                ||'WHERE    a.report_type = ''AutoPayReconciliation'' '
                ||'AND      r.name = ''Super Administrator'' )';
                
        -- building_amenity
        
        EXECUTE 'UPDATE '||v_schema_name||'.building_amenity '
                ||'SET  building_amenity_type = ''racquetballCourt'' '
                ||'WHERE    building_amenity_type = ''racquetball'' ';
                
        -- communication_message_category
        
        EXECUTE 'UPDATE '||v_schema_name||'.communication_message_category '
                ||'SET  ticket_type = NULL '
                ||'WHERE    ticket_type = ''NotTicket'' ';
                
        EXECUTE 'INSERT INTO '||v_schema_name||'.communication_message_category '
                ||'             (id,category,category_type,ticket_type,deleted) '
                ||'(SELECT  nextval(''public.communication_message_category_seq'') AS id, '
                ||'         ''Maintenance'',''Ticket'',''Maintenance'',FALSE )';
        
        
        -- customer_screening_income_info
        
        EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_income_info '
                ||'SET  amount_period = ''Monthly'', '
                ||'     revenue_amount_period = ''Monthly'' ';
                
                
        PERFORM * FROM _dba_.migrate_legal_questions(v_schema_name);
        
        
        -- deposit policy 
        
        EXECUTE 'UPDATE '||v_schema_name||'.deposit_policy '
                ||'SET  annual_interest_rate = 0.00 '
                ||'WHERE annual_interest_rate IS NULL';
                
        EXECUTE 'UPDATE '||v_schema_name||'.deposit_policy '
                ||'SET  security_deposit_refund_window = 0 '
                ||'WHERE security_deposit_refund_window IS NULL';
        
        
        -- financial_terms_policy_item
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.financial_terms_policy_item (id,caption,content,enabled) '
                ||'(SELECT  nextval(''public.financial_terms_policy_item_seq'') AS id, '
                ||'         t.caption, t.content, TRUE '
                ||' FROM    _dba_.tmp_terms t ) ';
                
        
        -- financial_terms_policy
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.financial_terms_policy(id,node,'
                ||'node_discriminator,updated,billing_terms) '
                ||'(SELECT  nextval(''public.financial_terms_policy_seq'') AS id, '
                ||'         n.id AS node, ''OrganizationPoliciesNode'' AS node_discriminator, '
                ||'         DATE_TRUNC(''second'',current_timestamp)::timestamp, '
                ||'         i.id AS billing_terms '
                ||' FROM    '||v_schema_name||'.organization_policies_node n, '
                ||'         '||v_schema_name||'.financial_terms_policy_item i '
                ||' WHERE   i.caption = ''Billing and Refund Policy'') ';
                
                
        EXECUTE 'UPDATE '||v_schema_name||'.financial_terms_policy AS p '
                ||'SET  preauthorized_payment_echeck_terms = i.id '
                ||'FROM '||v_schema_name||'.financial_terms_policy_item i ,'
                ||'     _dba_.tmp_terms t '
                ||'WHERE    t.target = ''TenantPreAuthorizedPaymentECheckTerms'' '
                ||'AND      t.content = i.content ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.financial_terms_policy AS p '
                ||'SET  preauthorized_payment_card_terms = i.id '
                ||'FROM '||v_schema_name||'.financial_terms_policy_item i ,'
                ||'     _dba_.tmp_terms t '
                ||'WHERE    t.target = ''TenantPreAuthorizedPaymentCardTerms'' '
                ||'AND      t.content = i.content ';
        
        
        -- identification_document_type
        
        EXECUTE 'UPDATE '||v_schema_name||'.identification_document_type '
                ||'SET importance = ''Optional'' '
                ||'WHERE importance IS NULL';
                
        -- try to preload Canadian SIN where doesn't exist
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.identification_document_type '
                ||'(id,policy,name,importance,id_type) '
                ||'(SELECT nextval(''public.identification_document_type_seq'') AS id, '
                ||'         id AS policy, ''SIN'' AS name, ''Required'' AS importance, '
                ||'         ''canadianSIN'' AS id_type '
                ||' FROM   '||v_schema_name||'.application_documentation_policy '
                ||'         WHERE NOT EXISTS (  SELECT ''x'' '
                ||'                             FROM '||v_schema_name||'.identification_document_type '
                ||'                             WHERE   id_type = ''canadianSIN''))';
        
        -- permission_to_enter_note
        
        EXECUTE 'UPDATE '||v_schema_name||'.permission_to_enter_note AS p '
                ||'SET      locale = l.lang '
                ||'FROM     '||v_schema_name||'.available_locale l '
                ||'WHERE    p.locale_old = l.id ';
                
                
        -- restrictions_policy
        
        EXECUTE 'UPDATE '||v_schema_name||'.restrictions_policy '
                ||'SET  emergency_contacts_is_mandatory = TRUE, '
                ||'     emergency_contacts_number = 1, '
                ||'     max_number_of_employments = 2, '
                ||'     min_employment_duration = 24, '
                ||'     reference_source_is_mandatory = TRUE';
                
        EXECUTE 'UPDATE '||v_schema_name||'.restrictions_policy '
                ||'SET  enforce_age_of_majority = TRUE '
                ||'WHERE enforce_age_of_majority IS NULL';
                
        EXECUTE 'UPDATE '||v_schema_name||'.restrictions_policy '
                ||'SET  age_of_majority = 18 '
                ||'WHERE age_of_majority IS NULL';
                
        -- system_endpoint
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.system_endpoint (id,name) '
                ||'VALUES (nextval(''public.system_endpoint_seq''),''Archive'')';
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        -- charge_line
        
        DROP TABLE charge_line;
        
        -- charge_line_list
        
        DROP TABLE charge_line_list;
        
        -- charge_line_list$charges
        
        DROP TABLE charge_line_list$charges;
        
        -- charge_old
        
        DROP TABLE charge_old;
        
        -- customer_screening_legal_questions
        
        DROP TABLE customer_screening_legal_questions;
        
        -- customer_screening_v
        
        ALTER TABLE customer_screening_v DROP COLUMN legal_questions;
        
        -- legal_letter
        
        DROP TABLE legal_letter;
        
        -- legal_letter_blob
        
        DROP TABLE legal_letter_blob;
        
        -- legal_status
        
        DROP TABLE legal_status;
        
        -- maintenance_request
        
        
        ALTER TABLE maintenance_request DROP COLUMN preferred_time1,
                                        DROP COLUMN preferred_time2;
        
        
        -- n4_policy
        
        ALTER TABLE n4_policy DROP COLUMN include_signature;
        
        -- permission_to_enter_note
        
        ALTER TABLE permission_to_enter_note DROP COLUMN locale_old;
        
        -- proof_of_asset_document_folder
        
        DROP TABLE proof_of_asset_document_folder;
        
        -- proof_of_income_document_folder
        
        DROP TABLE proof_of_income_document_folder;
        
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        -- primary keys
        
        ALTER TABLE customer_screening_asset ADD CONSTRAINT customer_screening_asset_pk PRIMARY KEY(id);
        ALTER TABLE identification_document ADD CONSTRAINT identification_document_pk PRIMARY KEY(id);
        ALTER TABLE maintenance_request_work_order ADD CONSTRAINT maintenance_request_work_order_pk PRIMARY KEY(id);

        -- foreign keys
        
        ALTER TABLE application_approval_checklist_policy_item ADD CONSTRAINT application_approval_checklist_policy_item_policy_fk FOREIGN KEY(policy) 
            REFERENCES application_approval_checklist_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE approval_checklist_item ADD CONSTRAINT approval_checklist_item_decided_by_fk FOREIGN KEY(decided_by) 
            REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE approval_checklist_item ADD CONSTRAINT approval_checklist_item_lease_application_fk FOREIGN KEY(lease_application) 
            REFERENCES lease_application(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE broadcast_event ADD CONSTRAINT broadcast_event_template_fk FOREIGN KEY(template) 
            REFERENCES broadcast_template(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE broadcast_event$threads ADD CONSTRAINT broadcast_event$threads_owner_fk FOREIGN KEY(owner) 
            REFERENCES broadcast_event(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE broadcast_event$threads ADD CONSTRAINT broadcast_event$threads_value_fk FOREIGN KEY(value) 
            REFERENCES communication_thread(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE broadcast_template ADD CONSTRAINT broadcast_template_category_fk FOREIGN KEY(category) 
            REFERENCES communication_message_category(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE broadcast_template_schedules$schedules ADD CONSTRAINT broadcast_template_schedules$schedules_owner_fk FOREIGN KEY(owner) 
            REFERENCES broadcast_template_schedules(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE broadcast_template_schedules$schedules ADD CONSTRAINT broadcast_template_schedules$schedules_value_fk FOREIGN KEY(value) 
            REFERENCES schedule(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE communication_broadcast_attachment ADD CONSTRAINT communication_broadcast_attachment_template_fk FOREIGN KEY(template) 
            REFERENCES broadcast_template(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE communication_thread ADD CONSTRAINT communication_thread_special_delivery_fk FOREIGN KEY(special_delivery) 
            REFERENCES special_delivery(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE crm_user_delivery_preferences ADD CONSTRAINT crm_user_delivery_preferences_user_preferences_fk FOREIGN KEY(user_preferences) 
            REFERENCES crm_user_preferences(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE crm_user_preferences ADD CONSTRAINT crm_user_preferences_crm_user_fk FOREIGN KEY(crm_user) 
            REFERENCES crm_user(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_credit_check ADD CONSTRAINT customer_credit_check_building_fk FOREIGN KEY(building) 
            REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_credit_check ADD CONSTRAINT customer_credit_check_screene_fk FOREIGN KEY(screene) 
            REFERENCES lease_participant(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_delivery_preferences ADD CONSTRAINT customer_delivery_preferences_user_preferences_fk FOREIGN KEY(user_preferences) 
            REFERENCES customer_preferences(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_screening_asset ADD CONSTRAINT customer_screening_asset_owner_fk FOREIGN KEY(owner) 
            REFERENCES customer_screening_v(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_screening_legal_question ADD CONSTRAINT customer_screening_legal_question_owner_fk FOREIGN KEY(owner) 
            REFERENCES customer_screening_v(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE entry_instructions_note ADD CONSTRAINT entry_instructions_note_policy_fk FOREIGN KEY(policy) 
            REFERENCES maintenance_request_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE entry_not_granted_alert ADD CONSTRAINT entry_not_granted_alert_policy_fk FOREIGN KEY(policy) 
            REFERENCES maintenance_request_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE eviction_case ADD CONSTRAINT eviction_case_created_by_fk FOREIGN KEY(created_by) 
            REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE eviction_case ADD CONSTRAINT eviction_case_eviction_flow_policy_fk FOREIGN KEY(eviction_flow_policy) 
            REFERENCES eviction_flow_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE eviction_case ADD CONSTRAINT eviction_case_lease_fk FOREIGN KEY(lease) 
            REFERENCES lease(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE eviction_case_init_data ADD CONSTRAINT eviction_case_init_data_lease_fk FOREIGN KEY(lease) 
            REFERENCES lease(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE eviction_case_status ADD CONSTRAINT eviction_case_status_added_by_fk FOREIGN KEY(added_by) 
            REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE eviction_case_status ADD CONSTRAINT eviction_case_status_eviction_case_fk FOREIGN KEY(eviction_case) 
            REFERENCES eviction_case(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE eviction_case_status ADD CONSTRAINT eviction_case_status_eviction_step_fk FOREIGN KEY(eviction_step) 
            REFERENCES eviction_flow_step(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE eviction_case_status ADD CONSTRAINT eviction_case_status_lease_arrears_fk FOREIGN KEY(lease_arrears) 
            REFERENCES n4_lease_arrears(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE eviction_case_status ADD CONSTRAINT eviction_case_status_n4_data_fk FOREIGN KEY(n4_data) 
            REFERENCES n4_lease_data(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE eviction_case_status ADD CONSTRAINT eviction_case_status_originating_batch_fk FOREIGN KEY(originating_batch) 
            REFERENCES n4_batch(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE eviction_case_status$generated_forms ADD CONSTRAINT eviction_case_status$generated_forms_owner_fk FOREIGN KEY(owner) 
            REFERENCES eviction_case_status(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE eviction_case_status$generated_forms ADD CONSTRAINT eviction_case_status$generated_forms_value_fk FOREIGN KEY(value) 
            REFERENCES eviction_document(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE eviction_document ADD CONSTRAINT eviction_document_lease_fk FOREIGN KEY(lease) 
            REFERENCES lease(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE eviction_document ADD CONSTRAINT eviction_document_record_fk FOREIGN KEY(record) 
            REFERENCES eviction_status_record(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE eviction_flow_step ADD CONSTRAINT eviction_flow_step_policy_fk FOREIGN KEY(policy) 
            REFERENCES eviction_flow_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE eviction_status_record ADD CONSTRAINT eviction_status_record_added_by_fk FOREIGN KEY(added_by) 
            REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE eviction_status_record ADD CONSTRAINT eviction_status_record_eviction_status_fk FOREIGN KEY(eviction_status) 
            REFERENCES eviction_case_status(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE identification_document ADD CONSTRAINT identification_document_id_type_fk FOREIGN KEY(id_type) 
            REFERENCES identification_document_type(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE identification_document ADD CONSTRAINT identification_document_owner_fk FOREIGN KEY(owner) 
            REFERENCES customer_screening_v(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE financial_terms_policy ADD CONSTRAINT financial_terms_policy_billing_terms_fk FOREIGN KEY(billing_terms) 
            REFERENCES financial_terms_policy_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE financial_terms_policy ADD CONSTRAINT financial_terms_policy_preauthorized_payment_card_terms_fk FOREIGN KEY(preauthorized_payment_card_terms) 
            REFERENCES financial_terms_policy_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE financial_terms_policy ADD CONSTRAINT financial_terms_policy_preauthorized_payment_echeck_terms_fk FOREIGN KEY(preauthorized_payment_echeck_terms) 
            REFERENCES financial_terms_policy_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE identification_document_file ADD CONSTRAINT identification_document_file_verified_by_fk FOREIGN KEY(verified_by) 
            REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE identification_document_file ADD CONSTRAINT identification_document_file_owner_fk FOREIGN KEY(owner) 
            REFERENCES identification_document(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_application$approval_checklist ADD CONSTRAINT lease_application$approval_checklist_owner_fk FOREIGN KEY(owner) 
            REFERENCES lease_application(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_application$approval_checklist ADD CONSTRAINT lease_application$approval_checklist_value_fk FOREIGN KEY(value) 
            REFERENCES approval_checklist_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_billing_type_policy_item ADD CONSTRAINT lease_billing_type_policy_item_policy_fk FOREIGN KEY(policy) 
            REFERENCES lease_billing_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE legal_questions_policy_item ADD CONSTRAINT legal_questions_policy_item_policy_fk FOREIGN KEY(policy) 
            REFERENCES legal_questions_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE maintenance_request_policy$tenant_preferred_windows ADD CONSTRAINT maintenance_request_policy$tenant_preferred_windows_owner_fk FOREIGN KEY(owner) 
            REFERENCES maintenance_request_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE maintenance_request_policy$tenant_preferred_windows ADD CONSTRAINT maintenance_request_policy$tenant_preferred_windows_value_fk FOREIGN KEY(value) 
            REFERENCES maintenance_request_window(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE maintenance_request_work_order ADD CONSTRAINT maintenance_request_work_order_request_fk FOREIGN KEY(request) 
            REFERENCES maintenance_request(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE n4_batch ADD CONSTRAINT n4_batch_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE n4_batch ADD CONSTRAINT n4_batch_n4policy_fk FOREIGN KEY(n4policy) REFERENCES n4_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE n4_batch ADD CONSTRAINT n4_batch_servicing_agent_fk FOREIGN KEY(servicing_agent) REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE n4_batch ADD CONSTRAINT n4_batch_signing_agent_fk FOREIGN KEY(signing_agent) REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE n4_batch_item ADD CONSTRAINT n4_batch_item_batch_fk FOREIGN KEY(batch) 
            REFERENCES n4_batch(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE n4_batch_item ADD CONSTRAINT n4_batch_item_lease_fk FOREIGN KEY(lease) 
            REFERENCES lease(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE n4_batch_item ADD CONSTRAINT n4_batch_item_lease_arrears_fk FOREIGN KEY(lease_arrears) 
            REFERENCES n4_lease_arrears(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE n4_lease_arrears ADD CONSTRAINT n4_lease_arrears_lease_fk FOREIGN KEY(lease) REFERENCES lease(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE n4_lease_data ADD CONSTRAINT n4_lease_data_lease_fk FOREIGN KEY(lease) REFERENCES lease(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE n4_lease_data ADD CONSTRAINT n4_lease_data_servicing_agent_fk FOREIGN KEY(servicing_agent) 
            REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE n4_lease_data ADD CONSTRAINT n4_lease_data_signing_agent_fk FOREIGN KEY(signing_agent) 
            REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE n4_unpaid_charge ADD CONSTRAINT n4_unpaid_charge_ar_code_fk FOREIGN KEY(ar_code) 
            REFERENCES arcode(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE n4_unpaid_charge ADD CONSTRAINT n4_unpaid_charge_parent_fk FOREIGN KEY(parent) 
            REFERENCES n4_lease_arrears(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE proof_of_asset_document_file ADD CONSTRAINT proof_of_asset_document_file_owner_fk FOREIGN KEY(owner) 
            REFERENCES customer_screening_asset(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE proof_of_asset_document_file ADD CONSTRAINT proof_of_asset_document_file_verified_by_fk FOREIGN KEY(verified_by) 
            REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE proof_of_asset_document_type ADD CONSTRAINT proof_of_asset_document_type_policy_fk FOREIGN KEY(policy) 
            REFERENCES application_documentation_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE proof_of_employment_document_type ADD CONSTRAINT proof_of_employment_document_type_policy_fk FOREIGN KEY(policy) 
            REFERENCES application_documentation_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE proof_of_income_document_file ADD CONSTRAINT proof_of_income_document_file_owner_fk FOREIGN KEY(owner) 
            REFERENCES customer_screening_income(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE proof_of_income_document_file ADD CONSTRAINT proof_of_income_document_file_verified_by_fk FOREIGN KEY(verified_by) 
            REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE proof_of_income_document_type ADD CONSTRAINT proof_of_income_document_type_policy_fk FOREIGN KEY(policy) 
            REFERENCES application_documentation_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE schedule ADD CONSTRAINT schedule_template_fk FOREIGN KEY(template) 
            REFERENCES broadcast_template(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE status_selection_item ADD CONSTRAINT status_selection_item_checklist_item_fk FOREIGN KEY(checklist_item) 
            REFERENCES approval_checklist_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE status_selection_policy_item ADD CONSTRAINT status_selection_policy_item_checklist_item_fk FOREIGN KEY(checklist_item) 
            REFERENCES application_approval_checklist_policy_item(id)  DEFERRABLE INITIALLY DEFERRED;

        
        -- check constraints
        
        ALTER TABLE available_crm_report ADD CONSTRAINT available_crm_report_report_type_e_ck 
            CHECK ((report_type) IN ('AutoPayChanges', 'AutoPayReconciliation', 'Availability', 'CustomerCreditCheck', 'EFT', 
            'EftVariance', 'ResidentInsurance'));
        ALTER TABLE application_approval_checklist_policy ADD CONSTRAINT application_approval_checklist_policy_node_discriminator_d_ck 
            CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE broadcast_template ADD CONSTRAINT broadcast_template_audience_type_e_ck 
            CHECK ((audience_type) IN ('Customer', 'Employee', 'Guarantor', 'Prospect', 'Tenant'));
        ALTER TABLE broadcast_template ADD CONSTRAINT broadcast_template_message_type_e_ck 
            CHECK ((message_type) IN ('CommercialActivity', 'Informational', 'Legal', 'Organizational', 'Promotional'));
        ALTER TABLE broadcast_template ADD CONSTRAINT broadcast_template_template_type_e_ck 
            CHECK ((template_type) IN ('ApplicationApproved', 'ApplicationCreatedApplicant', 'ApplicationCreatedCoApplicant', 
            'ApplicationCreatedGuarantor', 'ApplicationDeclined', 'AutoPayCancellation', 'AutoPayChanges', 'AutoPaySetupConfirmation', 
            'DirectDebitAccountChanged', 'MaintenanceRequestCancelled', 'MaintenanceRequestCompleted', 'MaintenanceRequestCreatedPMC', 
            'MaintenanceRequestCreatedTenant', 'MaintenanceRequestEntryNotice', 'MaintenanceRequestUpdated', 'MessageBroadcastCustomer', 
            'MessageBroadcastEmployee', 'MessageBroadcastGuarantor', 'MessageBroadcastProspect', 'MessageBroadcastTenant', 
            'OneTimePaymentSubmitted', 'PasswordRetrievalCrm', 'PasswordRetrievalProspect', 'PasswordRetrievalTenant', 
            'PaymentReceipt', 'PaymentReceiptWithWebPaymentFee', 'PaymentReturned', 'ProspectWelcome', 'TenantInvitation'));
        ALTER TABLE building_amenity ADD CONSTRAINT building_amenity_building_amenity_type_e_ck 
            CHECK ((building_amenity_type) IN ('airConditioning', 'availability24Hours', 'balcony', 'basketballCourt', 'businessCenter', 
            'childCare', 'clubDiscount', 'clubHouse', 'concierge', 'controlledAccess', 'coveredParking', 'doorAttendant', 'elevator', 'fitness', 
            'fitnessCentre', 'freeWeights', 'furnishedUnits', 'garage', 'gas', 'gate', 'groupExercise', 'guestRoom', 'handicapAccess', 'highSpeed', 
            'houseSitting', 'housekeeping', 'hydro', 'intrusionAlarm', 'laundry', 'library', 'mealService', 'nightPatrol', 'onSiteMaintenance', 
            'onSiteManagement', 'other', 'packageReceiving', 'parking', 'playGround', 'pool', 'racquetballCourt', 'recreationalRoom', 'sauna', 
            'shortTermLease', 'spa', 'storageSpace', 'sundeck', 'tennisCourt', 'transportation', 'tvLounge', 'vintage', 'volleyballCourt', 'water'));
        ALTER TABLE communication_delivery_handle ADD CONSTRAINT communication_delivery_handle_message_type_e_ck 
            CHECK ((message_type) IN ('CommercialActivity', 'Informational', 'Legal', 'Organizational', 'Promotional'));
        ALTER TABLE communication_message ADD CONSTRAINT communication_message_on_behalf_discriminator_d_ck 
            CHECK ((on_behalf_discriminator) IN ('Employee', 'Guarantor', 'SystemEndpoint', 'Tenant'));
        ALTER TABLE communication_message_category ADD CONSTRAINT communication_message_category_category_type_e_ck 
            CHECK ((category_type) IN ('Message', 'Ticket'));
        ALTER TABLE communication_message_category ADD CONSTRAINT communication_message_category_ticket_type_e_ck 
            CHECK ((ticket_type) IN ('Landlord', 'Maintenance', 'Tenant', 'Vendor'));
        ALTER TABLE communication_thread ADD CONSTRAINT communication_thread_associated_discriminator_d_ck 
            CHECK (associated_discriminator = 'MaintenanceRequest');
        ALTER TABLE communication_thread ADD CONSTRAINT communication_thread_delivery_method_e_ck 
            CHECK ((delivery_method) IN ('IVR', 'Notification', 'SMS'));
        ALTER TABLE communication_thread ADD CONSTRAINT communication_thread_special_delivery_discriminator_d_ck 
            CHECK ((special_delivery_discriminator) IN ('IVRDelivery', 'NotificationDelivery', 'SMSDelivery'));
        ALTER TABLE crm_user_delivery_preferences ADD CONSTRAINT crm_user_delivery_preferences_informational_delivery_e_ck 
            CHECK ((informational_delivery) IN ('Daily', 'DoNotSend', 'Individual', 'Weekly'));
        ALTER TABLE crm_user_delivery_preferences ADD CONSTRAINT crm_user_delivery_preferences_promotional_delivery_e_ck 
            CHECK ((promotional_delivery) IN ('Daily', 'DoNotSend', 'Individual', 'Weekly'));
        ALTER TABLE customer_credit_check ADD CONSTRAINT customer_credit_check_screene_discriminator_d_ck 
            CHECK ((screene_discriminator) IN ('Guarantor', 'Tenant'));
        ALTER TABLE customer_delivery_preferences ADD CONSTRAINT customer_delivery_preferences_informational_delivery_e_ck 
            CHECK ((informational_delivery) IN ('Daily', 'DoNotSend', 'Individual', 'Weekly'));
        ALTER TABLE customer_delivery_preferences ADD CONSTRAINT customer_delivery_preferences_promotional_delivery_e_ck 
            CHECK ((promotional_delivery) IN ('Daily', 'DoNotSend', 'Individual', 'Weekly'));
        ALTER TABLE customer_screening_asset ADD CONSTRAINT customer_screening_asset_asset_type_e_ck 
            CHECK ((asset_type) IN ('bankAccounts', 'businesses', 'cars', 'insurancePolicies', 'other', 'realEstateProperties', 'shares', 'unitTrusts'));
        ALTER TABLE customer_screening_income_info ADD CONSTRAINT customer_screening_income_info_amount_period_e_ck 
            CHECK ((amount_period) IN ('Annually', 'BiWeekly', 'Daily', 'Hourly', 'Monthly', 'Quaterly', 'SemiAnnually', 'SemiMonthly', 'Weekly'));
        ALTER TABLE customer_screening_income_info ADD CONSTRAINT customer_screening_income_info_revenue_amount_period_e_ck 
            CHECK ((revenue_amount_period) IN ('Annually', 'BiWeekly', 'Daily', 'Hourly', 'Monthly', 'Quaterly', 'SemiAnnually', 'SemiMonthly', 'Weekly'));
        ALTER TABLE email_template ADD CONSTRAINT email_template_template_type_e_ck 
            CHECK ((template_type) IN ('ApplicationApproved', 'ApplicationCreatedApplicant', 'ApplicationCreatedCoApplicant', 'ApplicationCreatedGuarantor', 
            'ApplicationDeclined', 'AutoPayCancellation', 'AutoPayChanges', 'AutoPaySetupConfirmation', 'DirectDebitAccountChanged', 
            'MaintenanceRequestCancelled', 'MaintenanceRequestCompleted', 'MaintenanceRequestCreatedPMC', 'MaintenanceRequestCreatedTenant', 
            'MaintenanceRequestEntryNotice', 'MaintenanceRequestUpdated', 'MessageBroadcastCustomer', 'MessageBroadcastEmployee', 
            'MessageBroadcastGuarantor', 'MessageBroadcastProspect', 'MessageBroadcastTenant', 'OneTimePaymentSubmitted', 'PasswordRetrievalCrm', 
            'PasswordRetrievalProspect', 'PasswordRetrievalTenant', 'PaymentReceipt', 'PaymentReceiptWithWebPaymentFee', 'PaymentReturned', 
            'ProspectWelcome', 'TenantInvitation'));
        ALTER TABLE entry_instructions_note ADD CONSTRAINT entry_instructions_note_locale_e_ck 
            CHECK ((locale) IN ('en', 'en_CA', 'en_GB', 'en_US', 'es', 'fr', 'fr_CA', 'ru', 'zh_CN', 'zh_TW'));
        ALTER TABLE entry_not_granted_alert ADD CONSTRAINT entry_not_granted_alert_locale_e_ck 
            CHECK ((locale) IN ('en', 'en_CA', 'en_GB', 'en_US', 'es', 'fr', 'fr_CA', 'ru', 'zh_CN', 'zh_TW'));
        ALTER TABLE eviction_case_status ADD CONSTRAINT eviction_case_status_id_discriminator_ck 
            CHECK ((id_discriminator) IN ('General', 'N4'));
        ALTER TABLE eviction_flow_policy ADD CONSTRAINT eviction_flow_policy_node_discriminator_d_ck 
            CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE eviction_flow_step ADD CONSTRAINT eviction_flow_step_step_type_e_ck 
            CHECK ((step_type) IN ('Custom', 'HearingDate', 'L1', 'N4', 'Order', 'RequestToReviewOrder', 'SetAside', 'Sheriff', 'StayOrder'));
        ALTER TABLE eviction_status_record ADD CONSTRAINT eviction_status_record_eviction_status_discriminator_d_ck 
            CHECK ((eviction_status_discriminator) IN ('General', 'N4'));
        ALTER TABLE financial_terms_policy ADD CONSTRAINT financial_terms_policy_node_discriminator_d_ck 
            CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE floorplan_amenity ADD CONSTRAINT floorplan_amenity_floorplan_type_e_ck 
            CHECK ((floorplan_type) IN ('additionalStorage', 'airConditioner', 'alarm', 'balcony', 'cable', 'carpeting', 'carport', 'ceilingFan', 
            'controlledAccess', 'courtyard', 'dishwasher', 'disposal', 'dryer', 'fireplace', 'furnished', 'garage', 'handrails', 'hardwoodFloors', 
            'heat', 'highSpeedInternetAvailable', 'individualClimateControl', 'largeClosets', 'microwave', 'other', 'patio', 'privateBalcony', 
            'privatePatio', 'range', 'refrigerator', 'satellite', 'skylight', 'vaultedCeiling', 'view', 'walkinClosets', 'washer', 'wdHookup', 
            'wheelChair', 'windowCoverings'));
        ALTER TABLE lead ADD CONSTRAINT lead_reference_source_e_ck 
            CHECK ((reference_source) IN ('DirectMail', 'Internet', 'Newspaper', 'Other', 'Radio', 'Referral', 'TV'));
        ALTER TABLE lease_application ADD CONSTRAINT lease_application_reference_source_e_ck 
            CHECK ((reference_source) IN ('DirectMail', 'Internet', 'Newspaper', 'Other', 'Radio', 'Referral', 'TV'));
        ALTER TABLE legal_questions_policy ADD CONSTRAINT legal_questions_policy_node_discriminator_d_ck 
            CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE legal_questions_policy_item ADD CONSTRAINT legal_questions_policy_item_locale_e_ck 
            CHECK ((locale) IN ('en', 'en_CA', 'en_GB', 'en_US', 'es', 'fr', 'fr_CA', 'ru', 'zh_CN', 'zh_TW'));
        ALTER TABLE n4_batch ADD CONSTRAINT n4_batch_company_address_country_e_ck 
            CHECK ((company_address_country) IN ('Afghanistan', 'AlandIslands', 'Albania', 'Algeria', 'AmericanSamoa', 'Andorra', 'Angola', 'Anguilla', 
            'Antarctica', 'Antigua', 'Argentina', 'Armenia', 'Aruba', 'Australia', 'Austria', 'Azerbaijan', 'Bahamas', 'Bahrain', 'Bangladesh', 'Barbados', 
            'Belarus', 'Belgium', 'Belize', 'Benin', 'Bermuda', 'Bhutan', 'Bolivia', 'Bonaire', 'BosniaHerzegovina', 'Botswana', 'BouvetIsland', 'Brazil', 
            'BruneiDarussalam', 'Bulgaria', 'BurkinaFaso', 'Burundi', 'CaboVerde', 'Cambodia', 'Cameroon', 'Canada', 'CaymanIslands', 'CentralAfricanRepublic', 
            'Chad', 'Chile', 'China', 'ChristmasIsland', 'CocosIslands', 'Colombia', 'Comoros', 'Congo', 'CookIslands', 'CostaRica', 'Croatia', 'Cuba', 'Curacao', 
            'Cyprus', 'CzechRepublic', 'Denmark', 'Djibouti', 'Dominica', 'DominicanRepublic', 'Ecuador', 'Egypt', 'ElSalvador', 'EquatorialGuinea', 'Eritrea', 
            'Estonia', 'Ethiopia', 'FalklandIslands', 'FaroeIslands', 'Fiji', 'Finland', 'France', 'FrenchGuiana', 'FrenchPolynesia', 'FrenchTerritories', 'Gabon', 
            'Gambia', 'Georgia', 'Germany', 'Ghana', 'Gibraltar', 'Greece', 'Greenland', 'Grenada', 'Guadeloupe', 'Guam', 'Guatemala', 'Guernsey', 'Guinea', 
            'GuineaBissau', 'Guyana', 'Haiti', 'HeardIslands', 'Honduras', 'HongKong', 'Hungary', 'Iceland', 'India', 'Indonesia', 'Iran', 'Iraq', 'Ireland', 
            'IsleOfMan', 'Israel', 'Italy', 'Jamaica', 'Japan', 'Jersey', 'Jordan', 'Kazakhstan', 'Kenya', 'Kiribati', 'Kuwait', 'Kyrgyzstan', 'LaoRepublic', 
            'Latvia', 'Lebanon', 'Lesotho', 'Liberia', 'Libya', 'Liechtenstein', 'Lithuania', 'Luxembourg', 'Macao', 'Macedonia', 'Madagascar', 'Malawi', 'Malaysia', 
            'Maldives', 'Mali', 'Malta', 'MarshallIslands', 'Martinique', 'Mauritania', 'Mauritius', 'Mayotte', 'Mexico', 'Micronesia', 'Moldova', 'Monaco', 'Mongolia', 
            'Montenegro', 'Montserrat', 'Morocco', 'Mozambique', 'Myanmar', 'Namibia', 'Nauru', 'Nepal', 'Netherlands', 'NewCaledonia', 'NewZealand', 'Nicaragua', 
            'Niger', 'Nigeria', 'Niue', 'NorfolkIsland', 'NorthKorea', 'NorthernMarianaIslands', 'Norway', 'Oman', 'Pakistan', 'Palau', 'Palestine', 'Panama', 
            'PapuaNewGuinea', 'Paraguay', 'Peru', 'Philippines', 'Pitcairn', 'Poland', 'Portugal', 'PuertoRico', 'Qatar', 'Reunion', 'Romania', 'RussianFederation', 
            'Rwanda', 'SaintBarthelemy', 'SaintHelena', 'SaintKitts', 'SaintLucia', 'SaintMartin', 'SaintPierre', 'SaintVincent', 'Samoa', 'SanMarino', 'SaoTome', 
            'SaudiArabia', 'Senegal', 'Serbia', 'Seychelles', 'SierraLeone', 'Singapore', 'SintMaartenDutch', 'Slovakia', 'Slovenia', 'SolomonIslands', 'Somalia', 
            'SouthAfrica', 'SouthKorea', 'SouthSudan', 'Spain', 'SriLanka', 'Sudan', 'Suriname', 'Svalbard', 'Swaziland', 'Sweden', 'Switzerland', 'Syria', 'Taiwan', 
            'Tajikistan', 'Tanzania', 'Thailand', 'TimorLeste', 'Togo', 'Tokelau', 'Tonga', 'Trinidad', 'Tunisia', 'Turkey', 'Turkmenistan', 'TurksCaicos', 'Tuvalu', 
            'Uganda', 'Ukraine', 'UnitedArabEmirates', 'UnitedKingdom', 'UnitedStates', 'Uruguay', 'Uzbekistan', 'Vanuatu', 'Vatican', 'Venezuela', 'VietNam', 
            'VirginIslands', 'VirginIslandsGB', 'WallisFutuna', 'WesternSahara', 'Yemen', 'Zambia', 'Zimbabwe'));
        ALTER TABLE n4_batch ADD CONSTRAINT n4_batch_delivery_method_e_ck CHECK ((delivery_method) IN ('Courier', 'Hand', 'Mail'));
        ALTER TABLE n4_batch ADD CONSTRAINT n4_batch_termination_date_option_e_ck CHECK ((termination_date_option) IN ('Calculate', 'LeaveBlank'));
        ALTER TABLE n4_lease_data ADD CONSTRAINT n4_lease_data_company_address_country_e_ck 
            CHECK ((company_address_country) IN ('Afghanistan', 'AlandIslands', 'Albania', 'Algeria', 'AmericanSamoa', 'Andorra', 'Angola', 'Anguilla', 
            'Antarctica', 'Antigua', 'Argentina', 'Armenia', 'Aruba', 'Australia', 'Austria', 'Azerbaijan', 'Bahamas', 'Bahrain', 'Bangladesh', 'Barbados', 
            'Belarus', 'Belgium', 'Belize', 'Benin', 'Bermuda', 'Bhutan', 'Bolivia', 'Bonaire', 'BosniaHerzegovina', 'Botswana', 'BouvetIsland', 'Brazil', 
            'BruneiDarussalam', 'Bulgaria', 'BurkinaFaso', 'Burundi', 'CaboVerde', 'Cambodia', 'Cameroon', 'Canada', 'CaymanIslands', 'CentralAfricanRepublic', 
            'Chad', 'Chile', 'China', 'ChristmasIsland', 'CocosIslands', 'Colombia', 'Comoros', 'Congo', 'CookIslands', 'CostaRica', 'Croatia', 'Cuba', 'Curacao', 
            'Cyprus', 'CzechRepublic', 'Denmark', 'Djibouti', 'Dominica', 'DominicanRepublic', 'Ecuador', 'Egypt', 'ElSalvador', 'EquatorialGuinea', 'Eritrea', 
            'Estonia', 'Ethiopia', 'FalklandIslands', 'FaroeIslands', 'Fiji', 'Finland', 'France', 'FrenchGuiana', 'FrenchPolynesia', 'FrenchTerritories', 'Gabon', 
            'Gambia', 'Georgia', 'Germany', 'Ghana', 'Gibraltar', 'Greece', 'Greenland', 'Grenada', 'Guadeloupe', 'Guam', 'Guatemala', 'Guernsey', 'Guinea', 
            'GuineaBissau', 'Guyana', 'Haiti', 'HeardIslands', 'Honduras', 'HongKong', 'Hungary', 'Iceland', 'India', 'Indonesia', 'Iran', 'Iraq', 'Ireland', 
            'IsleOfMan', 'Israel', 'Italy', 'Jamaica', 'Japan', 'Jersey', 'Jordan', 'Kazakhstan', 'Kenya', 'Kiribati', 'Kuwait', 'Kyrgyzstan', 'LaoRepublic', 
            'Latvia', 'Lebanon', 'Lesotho', 'Liberia', 'Libya', 'Liechtenstein', 'Lithuania', 'Luxembourg', 'Macao', 'Macedonia', 'Madagascar', 'Malawi', 'Malaysia', 
            'Maldives', 'Mali', 'Malta', 'MarshallIslands', 'Martinique', 'Mauritania', 'Mauritius', 'Mayotte', 'Mexico', 'Micronesia', 'Moldova', 'Monaco', 'Mongolia', 
            'Montenegro', 'Montserrat', 'Morocco', 'Mozambique', 'Myanmar', 'Namibia', 'Nauru', 'Nepal', 'Netherlands', 'NewCaledonia', 'NewZealand', 'Nicaragua', 
            'Niger', 'Nigeria', 'Niue', 'NorfolkIsland', 'NorthKorea', 'NorthernMarianaIslands', 'Norway', 'Oman', 'Pakistan', 'Palau', 'Palestine', 'Panama', 
            'PapuaNewGuinea', 'Paraguay', 'Peru', 'Philippines', 'Pitcairn', 'Poland', 'Portugal', 'PuertoRico', 'Qatar', 'Reunion', 'Romania', 'RussianFederation', 
            'Rwanda', 'SaintBarthelemy', 'SaintHelena', 'SaintKitts', 'SaintLucia', 'SaintMartin', 'SaintPierre', 'SaintVincent', 'Samoa', 'SanMarino', 'SaoTome', 
            'SaudiArabia', 'Senegal', 'Serbia', 'Seychelles', 'SierraLeone', 'Singapore', 'SintMaartenDutch', 'Slovakia', 'Slovenia', 'SolomonIslands', 'Somalia', 
            'SouthAfrica', 'SouthKorea', 'SouthSudan', 'Spain', 'SriLanka', 'Sudan', 'Suriname', 'Svalbard', 'Swaziland', 'Sweden', 'Switzerland', 'Syria', 'Taiwan', 
            'Tajikistan', 'Tanzania', 'Thailand', 'TimorLeste', 'Togo', 'Tokelau', 'Tonga', 'Trinidad', 'Tunisia', 'Turkey', 'Turkmenistan', 'TurksCaicos', 'Tuvalu', 
            'Uganda', 'Ukraine', 'UnitedArabEmirates', 'UnitedKingdom', 'UnitedStates', 'Uruguay', 'Uzbekistan', 'Vanuatu', 'Vatican', 'Venezuela', 'VietNam', 
            'VirginIslands', 'VirginIslandsGB', 'WallisFutuna', 'WesternSahara', 'Yemen', 'Zambia', 'Zimbabwe'));
        ALTER TABLE n4_lease_data ADD CONSTRAINT n4_lease_data_delivery_method_e_ck CHECK ((delivery_method) IN ('Courier', 'Hand', 'Mail'));
        ALTER TABLE n4_lease_data ADD CONSTRAINT n4_lease_data_termination_date_option_e_ck CHECK ((termination_date_option) IN ('Calculate', 'LeaveBlank'));
        ALTER TABLE n4_policy ADD CONSTRAINT n4_policy_agent_selection_method_cs_e_ck CHECK ((agent_selection_method_cs) IN ('ByLoggedInUser', 'FromEmployeeList'));
        ALTER TABLE n4_policy ADD CONSTRAINT n4_policy_agent_selection_method_n4_e_ck CHECK ((agent_selection_method_n4) IN ('ByLoggedInUser', 'FromEmployeeList'));
        ALTER TABLE notes_and_attachments ADD CONSTRAINT notes_and_attachments_owner_discriminator_d_ck 
            CHECK ((owner_discriminator) IN ('ARPolicy', 'AgreementLegalPolicy', 'ApplicationApprovalChecklistPolicy', 
            'ApplicationDocumentationPolicy', 'AptUnit', 'AutoPayPolicy', 'AutopayAgreement', 'BackgroundCheckPolicy', 'Building', 
            'CardsAggregatedTransfer', 'Complex', 'DatesPolicy', 'DepositPolicy', 'EftAggregatedTransfer', 'EmailTemplatesPolicy', 
            'Employee', 'EvictionFlowPolicy', 'FiancialTermsPolicy', 'Floorplan', 'Guarantor', 'IdAssignmentPolicy', 'Landlord', 'Lease', 
            'LeaseAdjustmentPolicy', 'LeaseBillingPolicy', 'LegalQuestionsPolicy', 'LegalTermsPolicy', 'Locker', 'MaintenanceRequest', 
            'MaintenanceRequestPolicy', 'MerchantAccount', 'N4Policy', 'OnlineAppPolicy', 'Parking', 'PaymentPostingBatch', 'PaymentRecord', 
            'PaymentTransactionsPolicy', 'PaymentTypeSelectionPolicy', 'PetPolicy', 'ProductTaxPolicy', 'ProspectPortalPolicy', 
            'ResidentPortalPolicy', 'RestrictionsPolicy', 'Tenant', 'TenantInsurancePolicy', 'Vendor', 'YardiInterfacePolicy', 'feature', 
            'service'));
        ALTER TABLE permission_to_enter_note ADD CONSTRAINT permission_to_enter_note_locale_e_ck 
            CHECK ((locale) IN ('en', 'en_CA', 'en_GB', 'en_US', 'es', 'fr', 'fr_CA', 'ru', 'zh_CN', 'zh_TW'));
        ALTER TABLE proof_of_asset_document_type ADD CONSTRAINT proof_of_asset_document_type_asset_type_e_ck 
            CHECK ((asset_type) IN ('bankAccounts', 'businesses', 'cars', 'insurancePolicies', 'other', 'realEstateProperties', 'shares', 'unitTrusts'));
        ALTER TABLE proof_of_asset_document_type ADD CONSTRAINT proof_of_asset_document_type_importance_e_ck 
            CHECK ((importance) IN ('Optional', 'Preferred', 'Required'));
        ALTER TABLE proof_of_employment_document_type ADD CONSTRAINT proof_of_employment_document_type_importance_e_ck 
            CHECK ((importance) IN ('Optional', 'Preferred', 'Required'));
        ALTER TABLE proof_of_employment_document_type ADD CONSTRAINT proof_of_employment_document_type_income_source_e_ck 
            CHECK ((income_source) IN ('disabilitySupport', 'dividends', 'fulltime', 'other', 'parttime', 'pension', 'retired', 'seasonallyEmployed', 
            'selfemployed', 'socialServices', 'student', 'unemployed'));
        ALTER TABLE proof_of_income_document_type ADD CONSTRAINT proof_of_income_document_type_importance_e_ck 
            CHECK ((importance) IN ('Optional', 'Preferred', 'Required'));
        ALTER TABLE proof_of_income_document_type ADD CONSTRAINT proof_of_income_document_type_income_source_e_ck 
            CHECK ((income_source) IN ('disabilitySupport', 'dividends', 'fulltime', 'other', 'parttime', 'pension', 'retired', 'seasonallyEmployed', 
            'selfemployed', 'socialServices', 'student', 'unemployed'));
        ALTER TABLE schedule ADD CONSTRAINT schedule_frequency_e_ck CHECK ((frequency) IN ('Daily', 'Monthly', 'Weekly'));
        ALTER TABLE special_delivery ADD CONSTRAINT special_delivery_id_discriminator_ck 
            CHECK ((id_discriminator) IN ('IVRDelivery', 'NotificationDelivery', 'SMSDelivery'));
        ALTER TABLE special_delivery ADD CONSTRAINT special_delivery_notification_type_e_ck CHECK ((notification_type) IN ('Information', 'Note', 'Warning'));



        
        
        -- not null
        
        ALTER TABLE autopay_agreement ALTER COLUMN is_deleted SET NOT NULL;
        ALTER TABLE customer ALTER COLUMN registered_in_portal SET NOT NULL;
        ALTER TABLE customer_screening_asset ALTER COLUMN asset_type SET NOT NULL;
        ALTER TABLE identification_document_file ALTER COLUMN owner SET NOT NULL;
        ALTER TABLE product ALTER COLUMN default_catalog_item SET NOT NULL;
        ALTER TABLE proof_of_asset_document_file ALTER COLUMN owner SET NOT NULL;
        ALTER TABLE proof_of_income_document_file ALTER COLUMN owner SET NOT NULL;
        ALTER TABLE restrictions_policy ALTER COLUMN age_of_majority SET NOT NULL;
        ALTER TABLE restrictions_policy ALTER COLUMN emergency_contacts_number SET NOT NULL;
        ALTER TABLE restrictions_policy ALTER COLUMN emergency_contacts_is_mandatory SET NOT NULL;
        ALTER TABLE restrictions_policy ALTER COLUMN enforce_age_of_majority SET NOT NULL;
        ALTER TABLE restrictions_policy ALTER COLUMN matured_occupants_are_applicants SET NOT NULL;
        ALTER TABLE restrictions_policy ALTER COLUMN min_employment_duration SET NOT NULL;
        ALTER TABLE restrictions_policy ALTER COLUMN no_need_guarantors SET NOT NULL;
        ALTER TABLE restrictions_policy ALTER COLUMN reference_source_is_mandatory SET NOT NULL;
        ALTER TABLE restrictions_policy ALTER COLUMN years_to_forcing_previous_address SET NOT NULL;

       
        
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        
        
        CREATE INDEX application_approval_checklist_policy_item_policy_idx ON application_approval_checklist_policy_item USING btree (policy);
        CREATE INDEX approval_checklist_item_lease_application_idx ON approval_checklist_item USING btree (lease_application);
        CREATE INDEX broadcast_event$threads_owner_idx ON broadcast_event$threads USING btree (owner);
        CREATE INDEX broadcast_template_schedules$schedules_owner_idx ON broadcast_template_schedules$schedules USING btree (owner);
        CREATE INDEX entry_instructions_note_policy_idx ON entry_instructions_note USING btree (policy);
        CREATE INDEX entry_not_granted_alert_policy_idx ON entry_not_granted_alert USING btree (policy);
        CREATE INDEX eviction_case_lease_idx ON eviction_case USING btree (lease);
        CREATE INDEX eviction_case_eviction_flow_policy_idx ON eviction_case USING btree (eviction_flow_policy);
        CREATE INDEX eviction_case_status_eviction_case_eviction_step_idx ON eviction_case_status USING btree (eviction_case, eviction_step);
        CREATE INDEX eviction_case_status_originating_batch_idx ON eviction_case_status USING btree (originating_batch);
        CREATE INDEX eviction_case_status$generated_forms_owner_idx ON eviction_case_status$generated_forms USING btree (owner);
        CREATE INDEX eviction_flow_step_policy_name_idx ON eviction_flow_step USING btree (policy, name);
        CREATE INDEX lease_application$approval_checklist_owner_idx ON lease_application$approval_checklist USING btree (owner);
        CREATE INDEX legal_questions_policy_item_policy_idx ON legal_questions_policy_item USING btree (policy);
        CREATE UNIQUE INDEX lease_billing_type_policy_item_policy_billing_period_idx ON lease_billing_type_policy_item USING btree (policy, billing_period);
        CREATE INDEX maintenance_request_work_order_request_idx ON maintenance_request_work_order USING btree (request);
        CREATE INDEX maintenance_request_policy$tenant_preferred_windows_owner_idx ON maintenance_request_policy$tenant_preferred_windows USING btree (owner);
        CREATE INDEX n4_batch_building_idx ON n4_batch USING btree (building);
        CREATE INDEX n4_batch_n4policy_idx ON n4_batch USING btree (n4policy);
        CREATE INDEX n4_batch_item_batch_idx ON n4_batch_item USING btree (batch);
        CREATE INDEX n4_batch_item_lease_idx ON n4_batch_item USING btree (lease);
        CREATE INDEX n4_lease_arrears_lease_idx ON n4_lease_arrears USING btree (lease);
        CREATE INDEX n4_lease_data_lease_idx ON n4_lease_data USING btree (lease);
        CREATE INDEX n4_unpaid_charge_parent_idx ON n4_unpaid_charge USING btree (parent);
        CREATE INDEX status_selection_item_checklist_item_idx ON status_selection_item USING btree (checklist_item);
        CREATE INDEX status_selection_policy_item_checklist_item_idx ON status_selection_policy_item USING btree (checklist_item);


       
        -- billing_arrears_snapshot -GiST index!
        
        CREATE INDEX billing_arrears_snapshot_from_date_to_date_idx ON billing_arrears_snapshot 
                USING GiST (box(point(from_date,from_date),point(to_date,to_date)) box_ops);
        
        
        
        
        -- Finishing touch
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.4.2',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;      
