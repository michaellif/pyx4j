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
        

        /**
        ***     ======================================================================================================
        ***
        ***             DROP INDEXES
        ***
        ***     ======================================================================================================
        **/
        
      
        
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
        
        -- application_documentation_policy
        
        ALTER TABLE application_documentation_policy    ADD COLUMN mandatory_proof_of_asset BOOLEAN,
                                                        ADD COLUMN mandatory_proof_of_employment BOOLEAN;
                                                        
                                                        
        -- autopay_agreement
        
        ALTER TABLE autopay_agreement   RENAME COLUMN creation_date TO created;
       
        
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
        
        
        -- eviction_case$eviction_flow
        
        CREATE TABLE eviction_case$eviction_flow
        (
            id                          BIGINT                  NOT NULL,
            owner                       BIGINT,
            value                       BIGINT,
            seq                         INTEGER,
                CONSTRAINT eviction_case$eviction_flow_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE eviction_case$eviction_flow OWNER TO vista;
        
        
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
        
        
        -- eviction_status
        
        CREATE TABLE eviction_status
        (
            id                          BIGINT                  NOT NULL,
            eviction_case               BIGINT                  NOT NULL,
            eviction_step               BIGINT,
            added_on                    TIMESTAMP,
            added_by                    BIGINT,
            note                        VARCHAR(500),
                CONSTRAINT eviction_status_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE eviction_status OWNER TO vista;
        
        
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
        
        
        -- not null
        
        ALTER TABLE customer_screening_asset ALTER COLUMN asset_type SET NOT NULL;
        ALTER TABLE identification_document_file ALTER COLUMN owner SET NOT NULL;
       
        
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
       
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
