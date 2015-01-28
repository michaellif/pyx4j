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
        ALTER TABLE maintenance_request_schedule DROP CONSTRAINT maintenance_request_schedule_pk;
        ALTER TABLE proof_of_asset_document_folder DROP CONSTRAINT proof_of_asset_document_folder_pk;
        ALTER TABLE proof_of_income_document_folder DROP CONSTRAINT proof_of_income_document_folder_pk;
        
        



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
            id                              BIGINT              NOT NULL
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
            record                      BIGINT                  NOT NULL,
            added_on                    TIMESTAMP,
            title                       VARCHAR(500),
            note                        VARCHAR(500),
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
            name                        VARCHAR(500),
            description                 VARCHAR(500),
                CONSTRAINT eviction_flow_step_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE eviction_flow_step OWNER TO vista;
        
        
        
        -- eviction_status_record
        
        CREATE TABLE eviction_status_record
        (
            id                          BIGINT                  NOT NULL,
            eviction_status             BIGINT                  NOT NULL,
            note                        VARCHAR(500),
            added_on                    TIMESTAMP,
            added_by                    BIGINT,
                CONSTRAINT eviction_status_record_pk PRIMARY KEY(id) 
        );
        
        ALTER TABLE eviction_status_record OWNER TO vista;
        
        
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
        
        
        -- maintenance_request_work_order
        
        CREATE TABLE maintenance_request_work_order
        (
            id                              BIGINT              NOT NULL,
            request                         BIGINT              NOT NULL,
            created                         TIMESTAMP,
            updated                         TIMESTAMP,
            scheduled_date                  DATE,
            scheduled_time_time_from        TIME,
            scheduled_time_time_to          TIME,
            work_description                VARCHAR(500),
            progress_note                   VARCHAR(500),
            is_emergency_work               BOOLEAN,
            notice_of_entry_text            VARCHAR(10000),
            notice_of_entry_message_date    VARCHAR(500),
            notice_of_entry_message_id      VARCHAR(500),
                CONSTRAINT maintenance_request_work_order_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE maintenance_request_work_order OWNER TO vista;
        
        
        -- n4_batch
        
        CREATE TABLE n4_batch
        (
            id                              BIGINT              NOT NULL,
            building                        BIGINT,
            notice_issue_date               DATE,
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
            company_phone_number            VARCHAR(500),
            company_fax_number              VARCHAR(500),
            company_email_address           VARCHAR(500),
            is_landlord                     BOOLEAN,
            signature_date                  DATE,
            signing_employee                BIGINT,
            name                            VARCHAR(500),
            created                         TIMESTAMP,
                CONSTRAINT n4_batch_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE n4_batch OWNER TO vista;
        
        
        -- n4_batch_item
        
        CREATE TABLE n4_batch_item
        (
            id                              BIGINT              NOT NULL,
            batch                           BIGINT,
            lease                           BIGINT,
            total_rent_owning               NUMERIC(18,2),
            serviced                        TIMESTAMP,
                CONSTRAINT n4_batch_item_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE n4_batch_item OWNER TO vista;
        
        
        
        -- n4_policy
        
        ALTER TABLE n4_policy   ADD COLUMN agent_selection_method VARCHAR(50),
                                ADD COLUMN use_agent_contact_info BOOLEAN,
                                ADD COLUMN eviction_flow_step VARCHAR(500);
                                
                                
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
                                        ADD COLUMN min_employment_duration INTEGER;
                                        
                                        
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
        
        
        -- customer_screening_income_info
        
        EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_income_info '
                ||'SET  amount_period = ''Monthly'', '
                ||'     revenue_amount_period = ''Monthly'' ';
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        
                 
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

        -- foreign keys
        
        ALTER TABLE application_approval_checklist_policy_item ADD CONSTRAINT application_approval_checklist_policy_item_policy_fk FOREIGN KEY(policy) 
            REFERENCES application_approval_checklist_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE communication_thread ADD CONSTRAINT communication_thread_special_delivery_fk FOREIGN KEY(special_delivery) 
            REFERENCES special_delivery(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE crm_user_delivery_preferences ADD CONSTRAINT crm_user_delivery_preferences_user_preferences_fk FOREIGN KEY(user_preferences) 
            REFERENCES crm_user_preferences(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE crm_user_preferences ADD CONSTRAINT crm_user_preferences_crm_user_fk FOREIGN KEY(crm_user) 
            REFERENCES crm_user(id)  DEFERRABLE INITIALLY DEFERRED;
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
        ALTER TABLE eviction_case ADD CONSTRAINT eviction_case_lease_fk FOREIGN KEY(lease) 
            REFERENCES lease(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE eviction_case_init_data ADD CONSTRAINT eviction_case_init_data_lease_fk FOREIGN KEY(lease) 
            REFERENCES lease(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE eviction_document ADD CONSTRAINT eviction_document_record_fk FOREIGN KEY(record) 
            REFERENCES eviction_status_record(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE eviction_flow_step ADD CONSTRAINT eviction_flow_step_policy_fk FOREIGN KEY(policy) 
            REFERENCES eviction_flow_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE eviction_status_record ADD CONSTRAINT eviction_status_record_added_by_fk FOREIGN KEY(added_by) 
            REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE identification_document ADD CONSTRAINT identification_document_id_type_fk FOREIGN KEY(id_type) 
            REFERENCES identification_document_type(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE identification_document ADD CONSTRAINT identification_document_owner_fk FOREIGN KEY(owner) 
            REFERENCES customer_screening_v(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE identification_document_file ADD CONSTRAINT identification_document_file_verified_by_fk FOREIGN KEY(verified_by) 
            REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE identification_document_file ADD CONSTRAINT identification_document_file_owner_fk FOREIGN KEY(owner) 
            REFERENCES identification_document(id)  DEFERRABLE INITIALLY DEFERRED;
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
        ALTER TABLE n4_batch ADD CONSTRAINT n4_batch_signing_employee_fk FOREIGN KEY(signing_employee) 
            REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE n4_batch_item ADD CONSTRAINT n4_batch_item_batch_fk FOREIGN KEY(batch) 
            REFERENCES n4_batch(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE n4_batch_item ADD CONSTRAINT n4_batch_item_lease_fk FOREIGN KEY(lease) 
            REFERENCES lease(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE n4_unpaid_charge ADD CONSTRAINT n4_unpaid_charge_ar_code_fk FOREIGN KEY(ar_code) 
            REFERENCES arcode(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE n4_unpaid_charge ADD CONSTRAINT n4_unpaid_charge_parent_fk FOREIGN KEY(parent) 
            REFERENCES n4_batch_item(id)  DEFERRABLE INITIALLY DEFERRED;
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
        ALTER TABLE status_selection_item ADD CONSTRAINT status_selection_item_checklist_item_fk FOREIGN KEY(checklist_item) 
            REFERENCES application_approval_checklist_policy_item(id)  DEFERRABLE INITIALLY DEFERRED;


        
        -- not null
        
        ALTER TABLE customer_screening_asset ALTER COLUMN asset_type SET NOT NULL;
        ALTER TABLE identification_document_file ALTER COLUMN owner SET NOT NULL;
        ALTER TABLE product ALTER COLUMN default_catalog_item SET NOT NULL;
        ALTER TABLE proof_of_asset_document_file ALTER COLUMN owner SET NOT NULL;
        ALTER TABLE proof_of_income_document_file ALTER COLUMN owner SET NOT NULL;
       
        
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        
        CREATE INDEX maintenance_request_policy$tenant_preferred_windows_owner_idx ON maintenance_request_policy$tenant_preferred_windows USING btree (owner);
        CREATE INDEX application_approval_checklist_policy_item_policy_idx ON application_approval_checklist_policy_item USING btree (policy);
        CREATE INDEX entry_instructions_note_policy_idx ON entry_instructions_note USING btree (policy);
        CREATE INDEX entry_not_granted_alert_policy_idx ON entry_not_granted_alert USING btree (policy);
        CREATE INDEX eviction_case_lease_idx ON eviction_case USING btree (lease);
        CREATE INDEX eviction_flow_step_policy_name_idx ON eviction_flow_step USING btree (policy, name);
        CREATE INDEX legal_questions_policy_item_policy_idx ON legal_questions_policy_item USING btree (policy);
        CREATE UNIQUE INDEX lease_billing_type_policy_item_policy_billing_period_idx ON lease_billing_type_policy_item USING btree (policy, billing_period);
        CREATE INDEX maintenance_request_work_order_request_idx ON maintenance_request_work_order USING btree (request);
        CREATE INDEX n4_batch_building_idx ON n4_batch USING btree (building);
        CREATE INDEX n4_batch_item_batch_idx ON n4_batch_item USING btree (batch);
        CREATE INDEX n4_batch_item_lease_idx ON n4_batch_item USING btree (lease);
        CREATE INDEX n4_unpaid_charge_parent_idx ON n4_unpaid_charge USING btree (parent);
        CREATE INDEX status_selection_item_checklist_item_idx ON status_selection_item USING btree (checklist_item);
        

       
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
