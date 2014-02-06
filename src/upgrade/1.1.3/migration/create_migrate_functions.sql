/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             version 1.1.3 PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_113(v_schema_name TEXT) RETURNS VOID AS
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
        ALTER TABLE application_wizard_substep DROP CONSTRAINT application_wizard_substep_step_fk;
        ALTER TABLE apt_unit DROP CONSTRAINT apt_unit_marketing_fk;
        ALTER TABLE charges DROP CONSTRAINT charges_application_charges_fk;
        ALTER TABLE charges DROP CONSTRAINT charges_application_fk;
        ALTER TABLE charges DROP CONSTRAINT charges_monthly_charges_fk;
        ALTER TABLE charges DROP CONSTRAINT charges_one_time_charges_fk;
        ALTER TABLE charges DROP CONSTRAINT charges_payment_split_charges_fk;
        ALTER TABLE charges DROP CONSTRAINT charges_prorated_charges_fk;
        ALTER TABLE digital_signature DROP CONSTRAINT digital_signature_person_fk;
        ALTER TABLE identification_document DROP CONSTRAINT identification_document_id_type_fk;
        ALTER TABLE insurance_certificate_doc DROP CONSTRAINT insurance_certificate_doc_certificate_fk;
        ALTER TABLE insurance_certificate_scan DROP CONSTRAINT insurance_certificate_scan_certificate_doc_fk;
        ALTER TABLE legal_documentation$co_application DROP CONSTRAINT legal_documentation$co_application_owner_fk;
        ALTER TABLE legal_documentation$co_application DROP CONSTRAINT legal_documentation$co_application_value_fk;
        ALTER TABLE legal_documentation$guarantor_application DROP CONSTRAINT legal_documentation$guarantor_application_owner_fk;
        ALTER TABLE legal_documentation$guarantor_application DROP CONSTRAINT legal_documentation$guarantor_application_value_fk;
        ALTER TABLE legal_documentation$lease DROP CONSTRAINT legal_documentation$lease_owner_fk;
        ALTER TABLE legal_documentation$lease DROP CONSTRAINT legal_documentation$lease_value_fk;
        ALTER TABLE legal_documentation$main_application DROP CONSTRAINT legal_documentation$main_application_owner_fk;
        ALTER TABLE legal_documentation$main_application DROP CONSTRAINT legal_documentation$main_application_value_fk;
        ALTER TABLE legal_documentation$payment_authorization DROP CONSTRAINT legal_documentation$payment_authorization_owner_fk;
        ALTER TABLE legal_documentation$payment_authorization DROP CONSTRAINT legal_documentation$payment_authorization_value_fk;
        ALTER TABLE legal_terms_content DROP CONSTRAINT legal_terms_content_locale_fk;
        ALTER TABLE legal_terms_descriptor$content DROP CONSTRAINT legal_terms_descriptor$content_owner_fk;
        ALTER TABLE legal_terms_descriptor$content DROP CONSTRAINT legal_terms_descriptor$content_value_fk;
        ALTER TABLE marketing$ad_blurbs DROP CONSTRAINT marketing$ad_blurbs_owner_fk;
        ALTER TABLE marketing$ad_blurbs DROP CONSTRAINT marketing$ad_blurbs_value_fk;
        ALTER TABLE online_application$signatures DROP CONSTRAINT online_application$signatures_owner_fk;
        ALTER TABLE online_application$signatures DROP CONSTRAINT online_application$signatures_value_fk;
        ALTER TABLE online_application$steps DROP CONSTRAINT online_application$steps_owner_fk;
        ALTER TABLE online_application$steps DROP CONSTRAINT online_application$steps_value_fk;
        ALTER TABLE payment_information DROP CONSTRAINT payment_information_application_fk;
        ALTER TABLE payment_information DROP CONSTRAINT payment_information_payment_method_billing_address_country_fk;
        ALTER TABLE payment_information DROP CONSTRAINT payment_information_payment_method_billing_address_province_fk;
        ALTER TABLE payment_information DROP CONSTRAINT payment_information_payment_method_customer_fk;
        ALTER TABLE payment_information DROP CONSTRAINT payment_information_payment_method_details_fk;
        -- ALTER TABLE product_item DROP CONSTRAINT product_item_code_fk;
        ALTER TABLE property_phone DROP CONSTRAINT property_phone_provider_fk;
        ALTER TABLE summary DROP CONSTRAINT summary_application_fk;
        ALTER TABLE tenant_charge_list$charges DROP CONSTRAINT tenant_charge_list$charges_owner_fk;
        ALTER TABLE tenant_charge_list$charges DROP CONSTRAINT tenant_charge_list$charges_value_fk;
        ALTER TABLE tenant_charge DROP CONSTRAINT tenant_charge_tenant_fk;
        
        
        
        -- check constraints
        ALTER TABLE application_document_file DROP CONSTRAINT application_document_file_owner_discriminator_d_ck;
        ALTER TABLE application_documentation_policy DROP CONSTRAINT application_documentation_policy_node_discriminator_d_ck;
        ALTER TABLE application_wizard_step DROP CONSTRAINT application_wizard_step_status_e_ck;
        ALTER TABLE application_wizard_substep DROP CONSTRAINT application_wizard_substep_status_e_ck;
        ALTER TABLE arpolicy DROP CONSTRAINT arpolicy_node_discriminator_d_ck;
        ALTER TABLE auto_pay_policy DROP CONSTRAINT auto_pay_policy_node_discriminator_d_ck;
        ALTER TABLE background_check_policy DROP CONSTRAINT background_check_policy_node_discriminator_d_ck;
        ALTER TABLE building_utility DROP CONSTRAINT building_utility_building_utility_type_e_ck;
        ALTER TABLE customer_screening_income_info DROP CONSTRAINT customer_screening_income_info_address_street_direction_e_ck;
        ALTER TABLE customer_screening_income_info DROP CONSTRAINT customer_screening_income_info_address_street_type_e_ck;
        ALTER TABLE dates_policy DROP CONSTRAINT dates_policy_node_discriminator_d_ck;
        ALTER TABLE deposit_policy DROP CONSTRAINT deposit_policy_node_discriminator_d_ck;
        ALTER TABLE email_template DROP CONSTRAINT email_template_template_type_e_ck;
        ALTER TABLE email_templates_policy DROP CONSTRAINT email_templates_policy_node_discriminator_d_ck;
        ALTER TABLE id_assignment_policy DROP CONSTRAINT id_assignment_policy_node_discriminator_d_ck;
        ALTER TABLE identification_document DROP CONSTRAINT identification_document_owner_discriminator_d_ck;
        ALTER TABLE insurance_certificate_doc DROP CONSTRAINT insurance_certificate_doc_certificate_discriminator_d_ck;
        ALTER TABLE lease_adjustment_policy DROP CONSTRAINT lease_adjustment_policy_node_discriminator_d_ck;
        ALTER TABLE lease_billing_policy DROP CONSTRAINT lease_billing_policy_node_discriminator_d_ck;
        ALTER TABLE legal_documentation DROP CONSTRAINT legal_documentation_node_discriminator_d_ck;
        ALTER TABLE master_online_application DROP CONSTRAINT master_online_application_status_e_ck;
        ALTER TABLE n4_policy DROP CONSTRAINT n4_policy_node_discriminator_d_ck;
        ALTER TABLE online_application DROP CONSTRAINT online_application_role_e_ck;
        ALTER TABLE payment_information DROP CONSTRAINT payment_information_payment_method_created_by_discr_d_ck;
        ALTER TABLE payment_information DROP CONSTRAINT payment_information_payment_method_details_discriminator_d_ck;
        ALTER TABLE payment_information DROP CONSTRAINT payment_information_payment_method_payment_type_e_ck;
        ALTER TABLE payment_transactions_policy DROP CONSTRAINT payment_transactions_policy_node_discriminator_d_ck;
        ALTER TABLE payment_type_selection_policy DROP CONSTRAINT payment_type_selection_policy_node_discriminator_d_ck;
        ALTER TABLE pet_policy DROP CONSTRAINT pet_policy_node_discriminator_d_ck;
        ALTER TABLE product DROP CONSTRAINT product_code_type_e_ck;
        ALTER TABLE product_item DROP CONSTRAINT product_item_element_discriminator_d_ck;
        ALTER TABLE product_tax_policy DROP CONSTRAINT product_tax_policy_node_discriminator_d_ck;
        ALTER TABLE proof_of_employment_document DROP CONSTRAINT proof_of_employment_document_owner_discriminator_d_ck;
        ALTER TABLE property_phone DROP CONSTRAINT property_phone_designation_e_ck;
        ALTER TABLE property_phone DROP CONSTRAINT property_phone_phone_type_e_ck;
        ALTER TABLE property_phone DROP CONSTRAINT property_phone_visibility_e_ck;
        ALTER TABLE restrictions_policy DROP CONSTRAINT restrictions_policy_node_discriminator_d_ck;
        ALTER TABLE tax DROP CONSTRAINT tax_policy_node_discriminator_d_ck;
        ALTER TABLE tenant_charge DROP CONSTRAINT tenant_charge_tenant_discriminator_d_ck;
        ALTER TABLE tenant_insurance_policy DROP CONSTRAINT tenant_insurance_policy_node_discriminator_d_ck;
        ALTER TABLE yardi_interface_policy DROP CONSTRAINT yardi_interface_policy_node_discriminator_d_ck;
        
        -- primary keys
        ALTER TABLE advertising_blurb DROP CONSTRAINT advertising_blurb_pk;
        ALTER TABLE application_document_blob DROP CONSTRAINT application_document_blob_pk;
        ALTER TABLE application_document_file DROP CONSTRAINT application_document_file_pk;
        ALTER TABLE application_wizard_substep DROP CONSTRAINT application_wizard_substep_pk;
        ALTER TABLE application_wizard_step DROP CONSTRAINT application_wizard_step_pk;
        ALTER TABLE charges DROP CONSTRAINT charges_pk;
        ALTER TABLE custom_skin_resource_blob DROP CONSTRAINT custom_skin_resource_blob_pk;
        ALTER TABLE digital_signature DROP CONSTRAINT digital_signature_pk;
        ALTER TABLE file_blob DROP CONSTRAINT file_blob_pk;
        ALTER TABLE general_insurance_policy_blob DROP CONSTRAINT general_insurance_policy_blob_pk;
        ALTER TABLE identification_document DROP CONSTRAINT identification_document_pk;
        --ALTER TABLE insurance_certificate_doc DROP CONSTRAINT insurance_certificate_doc_pk;
        ALTER TABLE legal_documentation$co_application DROP CONSTRAINT legal_documentation$co_application_pk;
        ALTER TABLE legal_documentation$guarantor_application DROP CONSTRAINT legal_documentation$guarantor_application_pk;
        ALTER TABLE legal_documentation$lease DROP CONSTRAINT legal_documentation$lease_pk;
        ALTER TABLE legal_documentation$main_application DROP CONSTRAINT legal_documentation$main_application_pk;
        ALTER TABLE legal_documentation$payment_authorization DROP CONSTRAINT legal_documentation$payment_authorization_pk;
        ALTER TABLE legal_documentation DROP CONSTRAINT legal_documentation_pk;
        ALTER TABLE legal_terms_content DROP CONSTRAINT legal_terms_content_pk;
        ALTER TABLE legal_terms_descriptor$content DROP CONSTRAINT legal_terms_descriptor$content_pk;
        ALTER TABLE legal_terms_descriptor DROP CONSTRAINT legal_terms_descriptor_pk;
        ALTER TABLE marketing$ad_blurbs DROP CONSTRAINT marketing$ad_blurbs_pk;
        ALTER TABLE online_application$signatures DROP CONSTRAINT online_application$signatures_pk;
        ALTER TABLE online_application$steps DROP CONSTRAINT online_application$steps_pk;
        --ALTER TABLE payment_information DROP CONSTRAINT payment_information_pk;
        ALTER TABLE proof_of_employment_document DROP CONSTRAINT proof_of_employment_document_pk;
        ALTER TABLE property_phone DROP CONSTRAINT property_phone_pk;
        ALTER TABLE summary DROP CONSTRAINT summary_pk;
        ALTER TABLE tenant_charge_list$charges DROP CONSTRAINT tenant_charge_list$charges_pk;
        ALTER TABLE tenant_charge_list DROP CONSTRAINT tenant_charge_list_pk;
        ALTER TABLE tenant_charge DROP CONSTRAINT tenant_charge_pk;

        
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
        
        
        
        
        
        -- agreement_signatures
        
        CREATE TABLE agreement_signatures
        (
                id                              BIGINT                  NOT NULL,
                id_discriminator                VARCHAR(64)             NOT NULL,
                lease_term_tenant_discriminator VARCHAR(50)             NOT NULL,
                lease_term_tenant               BIGINT                  NOT NULL,
                        CONSTRAINT agreement_signatures_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE agreement_signatures OWNER TO vista;
        
        
        -- agreement_signatures$legal_terms_signatures
        
        CREATE TABLE agreement_signatures$legal_terms_signatures
        (
                id                              BIGINT                  NOT NULL,
                owner                           BIGINT,
                value                           BIGINT,
                seq                             INT,
                        CONSTRAINT agreement_signatures$legal_terms_signatures_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE agreement_signatures$legal_terms_signatures OWNER TO vista;
        
        
        -- building
        
        ALTER TABLE building ADD COLUMN landlord BIGINT;    
                
        
        -- building_utility
        
        ALTER TABLE building_utility ADD COLUMN is_deleted BOOLEAN;
        
        -- community_event
        
        CREATE TABLE community_event
        (
                id                              BIGINT                  NOT NULL,
                caption                         VARCHAR(500),
                location                        VARCHAR(500),
                date                            DATE,
                time                            TIME,
                description                     VARCHAR(2048),
                building                        BIGINT                  NOT NULL,
                        CONSTRAINT community_event_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE community_event OWNER TO vista;
        
        -- company_logo
        
        ALTER TABLE company_logo RENAME COLUMN blob_key TO file_blob_key;
        ALTER TABLE company_logo RENAME COLUMN cache_version TO file_cache_version;
        ALTER TABLE company_logo RENAME COLUMN content_mime_type TO file_content_mime_type;
        ALTER TABLE company_logo RENAME COLUMN file_name TO file_file_name;
        ALTER TABLE company_logo RENAME COLUMN file_size TO file_file_size;
        ALTER TABLE company_logo RENAME COLUMN updated_timestamp TO file_updated_timestamp;
        
        -- customer_picture
        
        ALTER TABLE customer_picture RENAME COLUMN blob_key TO file_blob_key;
        ALTER TABLE customer_picture RENAME COLUMN cache_version TO file_cache_version;
        ALTER TABLE customer_picture RENAME COLUMN content_mime_type TO file_content_mime_type;
        ALTER TABLE customer_picture RENAME COLUMN file_name TO file_file_name;
        ALTER TABLE customer_picture RENAME COLUMN file_size TO file_file_size;
        ALTER TABLE customer_picture RENAME COLUMN updated_timestamp TO file_updated_timestamp;
        
        -- customer_screening_income_info
        
        ALTER TABLE customer_screening_income_info      ADD COLUMN address_street1 VARCHAR(500),
                                                        ADD COLUMN address_street2 VARCHAR(500);
                                                        
        -- customer_signature
        
        CREATE TABLE customer_signature
        (
                id                              BIGINT                  NOT NULL,
                sign_date                       TIMESTAMP,
                ip_address                      VARCHAR(39),
                signature_format                VARCHAR(50),
                full_name                       VARCHAR(500),
                initials                        VARCHAR(500),
                agree                           BOOLEAN,
                signing_user                    BIGINT,
                        CONSTRAINT customer_signature_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE customer_signature OWNER TO vista;
      
        -- emergency_contact
        
        ALTER TABLE emergency_contact ADD COLUMN relationship VARCHAR(50);
        
        
        -- employee_signature
        
        ALTER TABLE employee_signature RENAME COLUMN blob_key TO file_blob_key;
        ALTER TABLE employee_signature RENAME COLUMN cache_version TO file_cache_version;
        ALTER TABLE employee_signature RENAME COLUMN content_mime_type TO file_content_mime_type;
        ALTER TABLE employee_signature RENAME COLUMN file_name TO file_file_name;
        ALTER TABLE employee_signature RENAME COLUMN file_size TO file_file_size;
        ALTER TABLE employee_signature RENAME COLUMN updated_timestamp TO file_updated_timestamp;
        
        
        -- employee_signature_blob
        
        ALTER TABLE employee_signature_blob     ADD COLUMN name VARCHAR(500),
                                                ADD COLUMN updated TIMESTAMP;
        
        -- file_blob
        
        ALTER TABLE file_blob RENAME TO media_file_blob;
        ALTER TABLE media_file_blob RENAME COLUMN content TO data;
        ALTER TABLE media_file_blob ADD COLUMN created TIMESTAMP;
        
        ALTER TABLE media_file_blob ADD CONSTRAINT media_file_blob_pk PRIMARY KEY(id);
        
        
        -- floorplan
        
        ALTER TABLE floorplan ADD COLUMN code VARCHAR(500);
        ALTER TABLE floorplan ALTER COLUMN description TYPE VARCHAR(4000);
        
        -- general_insurance_policy_blob
        
        ALTER TABLE general_insurance_policy_blob RENAME TO insurance_certificate_scan_blob;
        
        ALTER TABLE insurance_certificate_scan_blob ADD CONSTRAINT insurance_certificate_scan_blob_pk PRIMARY KEY(id);
        
        -- identification_document_blob
        
        CREATE TABLE identification_document_blob
        (
                id                              BIGINT                  NOT NULL,
                name                            VARCHAR(500),
                data                            bytea,
                content_type                    VARCHAR(500),
                updated                         TIMESTAMP,
                created                         TIMESTAMP,
                        CONSTRAINT identification_document_blob_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE identification_document_blob OWNER TO vista;
        
        -- identification_document_file
        
        CREATE TABLE identification_document_file
        (
                id                              BIGINT                  NOT NULL,
                file_file_name                  VARCHAR(500),
                file_updated_timestamp          BIGINT,
                file_cache_version              INT,
                file_file_size                  INT,
                file_content_mime_type          VARCHAR(500),
                file_blob_key                   BIGINT,
                owner                           BIGINT,
                order_in_owner                  INT,
                        CONSTRAINT identification_document_file_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE identification_document_file OWNER TO vista;
        
        -- identification_document_folder
        
        CREATE TABLE identification_document_folder
        (
                id                              BIGINT                  NOT NULL,
                owner                           BIGINT                  NOT NULL,
                id_type                         BIGINT,
                donot_have                      BOOLEAN,
                id_number                       VARCHAR(500),
                notes                           VARCHAR(500),
                        CONSTRAINT identification_document_folder_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE identification_document_folder OWNER TO vista;
        
        -- identification_document_type
        
        ALTER TABLE identification_document_type ADD COLUMN required BOOLEAN;
        
        
        -- ilsemail_config
        
        CREATE TABLE ilsemail_config
        (
                id                              BIGINT                  NOT NULL,
                config                          BIGINT                  NOT NULL,
                frequency                       VARCHAR(50),
                email                           VARCHAR(500),
                max_daily_ads                   INT,
                        CONSTRAINT ilsemail_config_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE ilsemail_config OWNER TO vista;
        
        -- ilsprofile_building
        
        ALTER TABLE ilsprofile_building ADD COLUMN max_ads INT;
        
        
        -- ilsprofile_email
        
        CREATE TABLE ilsprofile_email
        (
                id                              BIGINT                  NOT NULL,
                building                        BIGINT                  NOT NULL,
                max_ads                         INT,
                disabled                        BOOLEAN,
                        CONSTRAINT ilsprofile_email_pk PRIMARY KEY(id)
        );
        
        
        ALTER TABLE ilsprofile_email OWNER TO vista;
                                                
       
        -- ilssummary_building
        
        CREATE TABLE ilssummary_building
        (
                id                              BIGINT                  NOT NULL,
                building                        BIGINT                  NOT NULL,
                title                           VARCHAR(500),
                description                     VARCHAR(4000),
                front_image                     BIGINT,
                        CONSTRAINT ilssummary_building_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE ilssummary_building OWNER TO vista;   
        
        
        -- ilssummary_floorplan
        
        CREATE TABLE ilssummary_floorplan
        (
                id                              BIGINT                  NOT NULL,
                floorplan                       BIGINT                  NOT NULL,
                title                           VARCHAR(500),
                description                     VARCHAR(4000),
                front_image                     BIGINT,
                        CONSTRAINT ilssummary_floorplan_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE ilssummary_floorplan OWNER TO vista;
        
        -- insurance_certificate_scan
        
        ALTER TABLE insurance_certificate_scan RENAME COLUMN blob_key TO file_blob_key;
        ALTER TABLE insurance_certificate_scan RENAME COLUMN cache_version TO file_cache_version;
        ALTER TABLE insurance_certificate_scan RENAME COLUMN content_mime_type TO file_content_mime_type;
        ALTER TABLE insurance_certificate_scan RENAME COLUMN file_name TO file_file_name;
        ALTER TABLE insurance_certificate_scan RENAME COLUMN file_size TO file_file_size;
        ALTER TABLE insurance_certificate_scan RENAME COLUMN updated_timestamp TO file_updated_timestamp;
        
        ALTER TABLE insurance_certificate_scan  ADD COLUMN certificate BIGINT,
                                                ADD COLUMN certificate_discriminator VARCHAR(50);                                   
       
        -- insurance_policy
        
        ALTER TABLE insurance_policy ADD COLUMN signature BIGINT;
                
        
        -- landlord
        
        CREATE TABLE landlord
        (
                id                              BIGINT                  NOT NULL,
                name                            VARCHAR(500),
                address_suite_number            VARCHAR(500),
                address_street_number           VARCHAR(500),
                address_street_number_suffix    VARCHAR(500),
                address_street_name             VARCHAR(500),
                address_street_type             VARCHAR(50),
                address_street_direction        VARCHAR(50),
                address_city                    VARCHAR(500),
                address_county                  VARCHAR(500),
                address_province                BIGINT,
                address_country                 BIGINT,
                address_postal_code             VARCHAR(500),
                website                         VARCHAR(500),
                logo                            BIGINT,
                signature                       BIGINT,
                updated                         TIMESTAMP,
                        CONSTRAINT landlord_pk PRIMARY KEY(id)
        );
        
        
        ALTER TABLE landlord OWNER TO vista;
        
        
        -- landlord_media
        
        CREATE TABLE landlord_media
        (
                id                              BIGINT                  NOT NULL,
                file_file_name                  VARCHAR(500),
                file_updated_timestamp          BIGINT,
                file_cache_version              INT,
                file_file_size                  INT,
                file_content_mime_type          VARCHAR(500),
                file_blob_key                   BIGINT,
                        CONSTRAINT landlord_media_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE landlord_media OWNER TO vista;
        
        -- landlord_media_blob
        
        CREATE TABLE landlord_media_blob
        (
                id                              BIGINT                  NOT NULL,
                name                            VARCHAR(500),
                data                            bytea,
                content_type                    VARCHAR(500),
                updated                         TIMESTAMP,
                created                         TIMESTAMP,
                        CONSTRAINT landlord_media_blob_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE landlord_media_blob OWNER TO vista;
        
        -- lease
        
        ALTER TABLE lease ALTER COLUMN approval_date TYPE TIMESTAMP;
        
        -- lease_application
        
        ALTER TABLE lease_application   ADD COLUMN created_by BIGINT,
                                        ADD COLUMN application_id VARCHAR(14),
                                        ADD COLUMN application_id_s VARCHAR(26);
                                        
        -- lease_agreement_confirmation_term
        
        CREATE TABLE lease_agreement_confirmation_term
        (
                id                              BIGINT                  NOT NULL,
                policy                          BIGINT                  NOT NULL,
                title                           VARCHAR(500),
                body                            VARCHAR(48000),
                signature_format                VARCHAR(50),
                order_id                        INT,
                        CONSTRAINT lease_agreement_confirmation_term_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE lease_agreement_confirmation_term OWNER TO vista;
        
        
        -- lease_agreement_legal_policy
        
        CREATE TABLE lease_agreement_legal_policy
        (
                id                              BIGINT                  NOT NULL,
                node_discriminator              VARCHAR(50),
                node                            BIGINT,
                updated                         TIMESTAMP,
                        CONSTRAINT lease_agreement_legal_policy_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE lease_agreement_legal_policy OWNER TO vista;
        
        
        -- lease_agreement_legal_term
        
        CREATE TABLE lease_agreement_legal_term
        (
                id                              BIGINT                  NOT NULL,
                policy                          BIGINT                  NOT NULL,
                title                           VARCHAR(500),
                body                            VARCHAR(48000),
                signature_format                VARCHAR(50),
                order_id                        INT,
                        CONSTRAINT lease_agreement_legal_term_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE lease_agreement_legal_term OWNER TO vista;
        
        
        -- lease_application_confirmation_term
        
        CREATE TABLE lease_application_confirmation_term
        (
                id                              BIGINT                  NOT NULL,
                policy                          BIGINT                  NOT NULL,
                title                           VARCHAR(500),
                body                            VARCHAR(48000),
                signature_format                VARCHAR(50),
                order_id                        INT,
                        CONSTRAINT lease_application_confirmation_term_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE lease_application_confirmation_term OWNER TO vista;
        
         -- lease_application_legal_policy
        
        CREATE TABLE lease_application_legal_policy
        (
                id                              BIGINT                  NOT NULL,
                node_discriminator              VARCHAR(50),
                node                            BIGINT,
                updated                         TIMESTAMP,
                        CONSTRAINT lease_application_legal_policy_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE lease_application_legal_policy OWNER TO vista;
        
        
        -- lease_application_legal_term
        
        CREATE TABLE lease_application_legal_term
        (
                id                              BIGINT                  NOT NULL,
                policy                          BIGINT                  NOT NULL,
                title                           VARCHAR(500),
                body                            VARCHAR(48000),
                signature_format                VARCHAR(50),
                apply_to_role                   VARCHAR(50),
                order_id                        INT,
                        CONSTRAINT lease_application_legal_term_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE lease_application_legal_term OWNER TO vista;
        
        
        -- lease_term_agreement_document
        
        CREATE TABLE lease_term_agreement_document
        (
                id                              BIGINT                  NOT NULL,
                file_file_name                  VARCHAR(500),
                file_updated_timestamp          BIGINT,
                file_cache_version              INT,
                file_file_size                  INT,
                file_content_mime_type          VARCHAR(500),
                file_blob_key                   BIGINT,
                lease_term_v                    BIGINT                  NOT NULL,
                        CONSTRAINT lease_term_agreement_document_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE lease_term_agreement_document OWNER TO vista;
        
        
        -- lease_term_agreement_document_blob
        
        CREATE TABLE lease_term_agreement_document_blob
        (
                id                              BIGINT                  NOT NULL,
                name                            VARCHAR(500),
                data                            bytea,
                content_type                    VARCHAR(500),
                updated                         TIMESTAMP,
                created                         TIMESTAMP,
                        CONSTRAINT lease_term_agreement_document_blob_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE lease_term_agreement_document_blob OWNER TO vista;
        
        
        -- lease_term_v$agreement_confirmation_term
        
        CREATE TABLE lease_term_v$agreement_confirmation_term
        (
                id                              BIGINT                  NOT NULL,
                owner                           BIGINT,
                value                           BIGINT,
                seq                             INT,
                        CONSTRAINT lease_term_v$agreement_confirmation_term_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE lease_term_v$agreement_confirmation_term OWNER TO vista;
        
        
        -- lease_term_v$agreement_legal_terms
        
        CREATE TABLE lease_term_v$agreement_legal_terms
        (
                id                              BIGINT                  NOT NULL,
                owner                           BIGINT,
                value                           BIGINT,
                seq                             INT,
                        CONSTRAINT lease_term_v$agreement_legal_terms_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE lease_term_v$agreement_legal_terms OWNER TO vista;
        
        -- lease_term_v$utilities
        
        CREATE TABLE lease_term_v$utilities
        (
                id                              BIGINT                  NOT NULL,
                owner                           BIGINT,
                value                           BIGINT,
                seq                             INT,
                        CONSTRAINT lease_term_v$utilities_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE lease_term_v$utilities OWNER TO vista;
        
         -- legal_letter
        
        ALTER TABLE legal_letter RENAME COLUMN blob_key TO file_blob_key;
        ALTER TABLE legal_letter RENAME COLUMN cache_version TO file_cache_version;
        ALTER TABLE legal_letter RENAME COLUMN content_mime_type TO file_content_mime_type;
        ALTER TABLE legal_letter RENAME COLUMN file_name TO file_file_name;
        ALTER TABLE legal_letter RENAME COLUMN file_size TO file_file_size;
        ALTER TABLE legal_letter RENAME COLUMN updated_timestamp TO file_updated_timestamp;
        
        ALTER TABLE legal_letter        ADD COLUMN cancellation_threshold NUMERIC(18,2),
                                        ADD COLUMN is_active BOOLEAN,
                                        ADD COLUMN termination_date DATE;
        
        -- legal_letter_blob
        
        ALTER TABLE legal_letter_blob RENAME COLUMN content TO data;
        ALTER TABLE legal_letter_blob ADD COLUMN created TIMESTAMP;
        
        
        -- legal_status
        
        CREATE TABLE legal_status
        (
                id                              BIGINT                  NOT NULL,
                id_discriminator                VARCHAR(64)             NOT NULL,
                lease                           BIGINT                  NOT NULL,
                status                          VARCHAR(50),
                details                         VARCHAR(500),
                notes                           VARCHAR(500),
                set_on                          TIMESTAMP,
                set_by                          BIGINT,
                        CONSTRAINT legal_status_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE legal_status OWNER TO vista;
        
        
        -- legal_terms_policy
        
        CREATE TABLE legal_terms_policy
        (
                id                                      BIGINT                  NOT NULL,
                node_discriminator                      VARCHAR(50),
                node                                    BIGINT,
                updated                                 TIMESTAMP,
                resident_portal_terms_and_conditions    BIGINT,
                resident_portal_privacy_policy          BIGINT,
                prospect_portal_terms_and_conditions    BIGINT,
                prospect_portal_privacy_policy          BIGINT,
                        CONSTRAINT legal_terms_policy_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE legal_terms_policy OWNER TO vista;
        
        -- legal_terms_policy_item
        
        CREATE TABLE legal_terms_policy_item
        (
                id                              BIGINT                  NOT NULL,
                caption                         VARCHAR(500),
                content                         VARCHAR(300000),
                enabled                         BOOLEAN,
                        CONSTRAINT legal_terms_policy_item_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE legal_terms_policy_item OWNER TO vista;
        
        -- maintenance_request
        
        ALTER TABLE maintenance_request         ADD COLUMN phone_type VARCHAR(50),
                                                ADD COLUMN reported_date DATE;
        
        
        -- maintenance_request_category
        
        ALTER TABLE maintenance_request_category ADD COLUMN type VARCHAR(50);
        
        -- maintenance_request_picture
        
        CREATE TABLE maintenance_request_picture
        (
                id                              BIGINT                  NOT NULL,
                file_file_name                  VARCHAR(500),
                file_updated_timestamp          BIGINT,
                file_cache_version              INT,
                file_file_size                  INT,
                file_content_mime_type          VARCHAR(500),
                file_blob_key                   BIGINT,
                maintenance_request             BIGINT,
                description                     VARCHAR(500),
                        CONSTRAINT maintenance_request_picture_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE maintenance_request_picture OWNER TO vista; 
        
        
        -- maintenance_request_picture_blob
        
        CREATE TABLE maintenance_request_picture_blob
        (
                id                              BIGINT                  NOT NULL,
                name                            VARCHAR(500),
                data                            bytea,
                content_type                    VARCHAR(500),
                updated                         TIMESTAMP,
                created                         TIMESTAMP,
                        CONSTRAINT maintenance_request_picture_blob_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE maintenance_request_picture_blob OWNER TO vista;
        
        -- maintenance_request_policy
        
        CREATE TABLE maintenance_request_policy
        (
                id                              BIGINT                  NOT NULL,
                node_discriminator              VARCHAR(50),
                node                            BIGINT,
                updated                         TIMESTAMP,
                        CONSTRAINT maintenance_request_policy_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE maintenance_request_policy OWNER TO vista;
        
        -- marketing
        
        ALTER TABLE marketing ALTER COLUMN description TYPE VARCHAR(4000);
        
        -- master_online_application
        
        ALTER TABLE master_online_application   ADD COLUMN building BIGINT,
                                                ADD COLUMN floorplan BIGINT;
        
        
        -- media_file
        
        ALTER TABLE media_file RENAME COLUMN blob_key TO file_blob_key;
        ALTER TABLE media_file RENAME COLUMN cache_version TO file_cache_version;
        ALTER TABLE media_file RENAME COLUMN content_mime_type TO file_content_mime_type;
        ALTER TABLE media_file RENAME COLUMN file_name TO file_file_name;
        ALTER TABLE media_file RENAME COLUMN file_size TO file_file_size;
        ALTER TABLE media_file RENAME COLUMN updated_timestamp TO file_updated_timestamp;
        
        
        --  merchant_account
        
        ALTER TABLE  merchant_account   ADD COLUMN operations_notes VARCHAR(500),
                                        ADD COLUMN account_name VARCHAR(500);
                                        
                                        
        -- n4_policy
        
        ALTER TABLE n4_policy   ADD COLUMN mailing_address_suite_number VARCHAR(500),
                                ADD COLUMN mailing_address_street_number VARCHAR(500),
                                ADD COLUMN mailing_address_street_number_suffix VARCHAR(500),
                                ADD COLUMN mailing_address_street_name VARCHAR(500),
                                ADD COLUMN mailing_address_street_type VARCHAR(50),
                                ADD COLUMN mailing_address_street_direction VARCHAR(50),
                                ADD COLUMN mailing_address_county VARCHAR(500),
                                ADD COLUMN termination_date_advance_days_long_rent_period INT,
                                ADD COLUMN termination_date_advance_days_short_rent_period INT;
                                        
                                        
        -- note_attachment
        
        CREATE TABLE note_attachment
        (
                id                              BIGINT                  NOT NULL,
                file_file_name                  VARCHAR(500),
                file_updated_timestamp          BIGINT,
                file_cache_version              INT,
                file_file_size                  INT,
                file_content_mime_type          VARCHAR(500),
                file_blob_key                   BIGINT,
                owner                           BIGINT,
                        CONSTRAINT note_attachment_pk PRIMARY KEY(id)
        );
        
        
        ALTER TABLE note_attachment OWNER TO vista;
        
        
        -- note_attachment_blob
        
        ALTER TABLE note_attachment_blob        ADD COLUMN name VARCHAR(500),
                                                ADD COLUMN updated TIMESTAMP;
                                                
                                                
        -- notes_and_attachments
        
        ALTER TABLE notes_and_attachments       ADD COLUMN owner BIGINT,
                                                ADD COLUMN owner_discriminator VARCHAR(50);
                                                
       
        -- online_application$confirmation_terms
        
        CREATE TABLE online_application$confirmation_terms
        (
                id                              BIGINT                  NOT NULL,
                owner                           BIGINT,
                value                           BIGINT,
                seq                             INT,
                        CONSTRAINT online_application$confirmation_terms_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE online_application$confirmation_terms OWNER TO vista;
                                                
        -- online_application$legal_terms
        
        CREATE TABLE online_application$legal_terms
        (
                id                              BIGINT                  NOT NULL,
                owner                           BIGINT,
                value                           BIGINT,
                seq                             INT,
                        CONSTRAINT online_application$legal_terms_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE online_application$legal_terms OWNER TO vista;
        
        
        
        -- online_application_wizard_step_status
        
        CREATE TABLE online_application_wizard_step_status
        (
                id                              BIGINT                  NOT NULL,
                online_application              BIGINT,
                step                            VARCHAR(50),
                complete                        BOOLEAN,
                visited                         BOOLEAN,
                        CONSTRAINT online_application_wizard_step_status_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE online_application_wizard_step_status OWNER TO vista;
        
        
        -- payment_method
        
        ALTER TABLE payment_method ADD COLUMN signature BIGINT;
        
        -- payment_posting_batch
        
        CREATE TABLE payment_posting_batch
        (
                id                                      BIGINT                  NOT NULL,
                building                                BIGINT                  NOT NULL,
                status                                  VARCHAR(50),
                creation_date                           DATE,
                updated                                 TIMESTAMP,
                created_by_discriminator                VARCHAR(50),
                created_by                              BIGINT,
                deposit_details_deposit_date            DATE,
                deposit_details_merchant_account        BIGINT,
                        CONSTRAINT payment_posting_batch_pk PRIMARY KEY(id)              
        );
        
        ALTER TABLE payment_posting_batch OWNER TO vista;
        
        -- payment_record
        
        ALTER TABLE payment_record ADD COLUMN batch BIGINT;
        
        -- payment_type_selection_policy
        
        ALTER TABLE payment_type_selection_policy       ADD COLUMN prospect_credit_card_master_card BOOLEAN,
                                                        ADD COLUMN prospect_credit_card_visa BOOLEAN,
                                                        ADD COLUMN prospect_direct_banking BOOLEAN,
                                                        ADD COLUMN prospect_echeck BOOLEAN,
                                                        ADD COLUMN prospect_interac BOOLEAN,
                                                        ADD COLUMN prospect_visa_debit BOOLEAN;
                                                        
        -- permission_to_enter_note
        
        CREATE TABLE permission_to_enter_note
        (
                id                                      BIGINT                  NOT NULL,
                locale                                  BIGINT,
                policy                                  BIGINT                  NOT NULL,
                text                                    VARCHAR(500),
                order_by                                INT,
                        CONSTRAINT permission_to_enter_note_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE permission_to_enter_note OWNER TO vista;
        
        -- pmc_company_info
        
        CREATE TABLE pmc_company_info
        (
                id                                      BIGINT                  NOT NULL,
                company_name                            VARCHAR(500),
                        CONSTRAINT pmc_company_info_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE pmc_company_info OWNER TO vista;
        
        
        -- pmc_company_info_contact
        
        CREATE TABLE pmc_company_info_contact
        (
                id                                      BIGINT                  NOT NULL,
                company_info                            BIGINT                  NOT NULL,
                tp                                      VARCHAR(50),
                name                                    VARCHAR(500),
                phone_number                            VARCHAR(500),
                email                                   VARCHAR(500),
                        CONSTRAINT pmc_company_info_contact_pk PRIMARY KEY(id)
        );
        
        
        ALTER TABLE pmc_company_info_contact OWNER TO vista;
        
        -- product
        
        ALTER TABLE product     ADD COLUMN code BIGINT,
                                ADD COLUMN default_catalog_item BOOLEAN,
                                ADD COLUMN expired_from DATE;
        
        
        -- product_item
        
        ALTER TABLE product_item ADD COLUMN name VARCHAR(50);
        
        
        -- product_v
        
        ALTER TABLE product_v   ADD COLUMN available_online BOOLEAN,
                                ADD COLUMN price NUMERIC(18,2);
        
        
        -- proof_of_employment_document_blob
        
        CREATE TABLE proof_of_employment_document_blob
        (       
                id                              BIGINT                  NOT NULL,
                name                            VARCHAR(500),
                data                            bytea,
                content_type                    VARCHAR(500),
                updated                         TIMESTAMP,
                created                         TIMESTAMP,
                        CONSTRAINT proof_of_employment_document_blob_pk PRIMARY KEY(id)
        );
        
        
        ALTER TABLE proof_of_employment_document_blob OWNER TO vista;
        
        
        -- proof_of_employment_document_file
        
        CREATE TABLE proof_of_employment_document_file
        (
                id                              BIGINT                  NOT NULL,
                file_file_name                  VARCHAR(500),
                file_updated_timestamp          BIGINT,
                file_cache_version              INT,
                file_file_size                  INT,
                file_content_mime_type          VARCHAR(500),
                file_blob_key                   BIGINT,
                owner                           BIGINT,
                order_in_owner                  INT,
                        CONSTRAINT proof_of_employment_document_file_pk PRIMARY KEY(id)
        );
        
        
        ALTER TABLE proof_of_employment_document_file OWNER TO vista;
        
        
        -- proof_of_employment_document_folder
        
        CREATE TABLE proof_of_employment_document_folder
        (
                id                              BIGINT                  NOT NULL,
                owner                           BIGINT                  NOT NULL,
                description                     VARCHAR(500),
                        CONSTRAINT proof_of_employment_document_folder_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE proof_of_employment_document_folder OWNER TO vista;
        
        -- prospect_portal_policy
        
        CREATE TABLE prospect_portal_policy
        (
                id                              BIGINT                  NOT NULL,
                node_discriminator              VARCHAR(50),
                node                            BIGINT,
                updated                         TIMESTAMP,
                unit_availability_span          INT,
                max_exact_match_units           INT,
                max_partial_match_units         INT,
                fee_payment                     VARCHAR(50),
                fee_amount                      NUMERIC(18,2),
                        CONSTRAINT prospect_portal_policy_pk PRIMARY KEY(id)
        );
        
        
        -- restrictions_policy
        
        ALTER TABLE restrictions_policy RENAME COLUMN occupants_over18are_applicants TO matured_occupants_are_applicants;
        
        
        ALTER TABLE prospect_portal_policy OWNER TO vista;
        
        
        -- signed_agreement_confirmation_term
        
        CREATE TABLE signed_agreement_confirmation_term
        (
                id                              BIGINT                  NOT NULL,
                term                            BIGINT,
                signature                       BIGINT,
                        CONSTRAINT signed_agreement_confirmation_term_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE signed_agreement_confirmation_term OWNER TO vista;
        
        -- signed_agreement_legal_term
        
        CREATE TABLE signed_agreement_legal_term
        (
                id                              BIGINT                  NOT NULL,
                term                            BIGINT,
                signature                       BIGINT,
                        CONSTRAINT signed_agreement_legal_term_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE signed_agreement_legal_term OWNER TO vista;
        
        
        -- signed_online_application_confirmation_term
        
        CREATE TABLE signed_online_application_confirmation_term
        (
                id                              BIGINT                  NOT NULL,
                term                            BIGINT,
                signature                       BIGINT,
                        CONSTRAINT signed_online_application_confirmation_term_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE signed_online_application_confirmation_term OWNER TO vista;
        
        
        -- signed_online_application_legal_term
        
        CREATE TABLE signed_online_application_legal_term
        (
                id                              BIGINT                  NOT NULL,
                term                            BIGINT,
                signature                       BIGINT,
                        CONSTRAINT signed_online_application_legal_term_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE signed_online_application_legal_term OWNER TO vista;
           
        -- site_image_resource
        
        ALTER TABLE site_image_resource RENAME COLUMN blob_key TO file_blob_key;
        ALTER TABLE site_image_resource RENAME COLUMN cache_version TO file_cache_version;
        ALTER TABLE site_image_resource RENAME COLUMN content_mime_type TO file_content_mime_type;
        ALTER TABLE site_image_resource RENAME COLUMN file_name TO file_file_name;
        ALTER TABLE site_image_resource RENAME COLUMN file_size TO file_file_size;
        ALTER TABLE site_image_resource RENAME COLUMN updated_timestamp TO file_updated_timestamp;
        
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        -- insurance_certificate_scan
        
        EXECUTE 'UPDATE '||v_schema_name||'.insurance_certificate_scan AS s '
                ||'SET  certificate = d.certificate, '
                ||'     certificate_discriminator = d.certificate_discriminator '
                ||'FROM '||v_schema_name||'.insurance_certificate_doc AS d '
                ||'WHERE s.certificate_doc = d.id ';
                
                
        -- maintenance_request
        
        EXECUTE 'UPDATE '||v_schema_name||'.maintenance_request '
                ||'SET  reported_date = submitted::date ';
        
        
        -- n4_policy
        
        EXECUTE 'SELECT COUNT(id) '
                ||'FROM '||v_schema_name||'.n4_policy '
                INTO v_rowcount;
                
        IF (v_rowcount = 0) 
        THEN
                EXECUTE 'INSERT INTO '||v_schema_name||'.n4_policy (id,node_discriminator,node,hand_delivery_advance_days,'
                        ||'mail_delivery_advance_days,courier_delivery_advance_days) '
                        ||'(SELECT nextval(''public.n4_policy_seq'') AS id, ''OrganizationPoliciesNode'' AS node_discriminator,'
                        ||'id  AS node, 0 AS hand_delivery_advance_days, 5 AS mail_delivery_advance_days, 1 AS courier_delivery_advance_days '
                        ||'FROM '||v_schema_name||'.organization_policies_node) ';
                        
        END IF;                        
        
        
        EXECUTE 'UPDATE '||v_schema_name||'.n4_policy '
                ||'SET  termination_date_advance_days_long_rent_period = 14,'
                ||'     termination_date_advance_days_short_rent_period = 7 ';
        
        
        -- policy tables
        
        PERFORM * FROM _dba_.update_policy_tables(v_schema_name);
        
        
        
        -- product_item 
        
        EXECUTE 'UPDATE '||v_schema_name||'.product_item '
                ||'SET  element_discriminator = ''AptUnit'' '
                ||'WHERE element_discriminator = ''Unit_BuildingElement'' ';
                
                
        EXECUTE 'UPDATE '||v_schema_name||'.product_item '
                ||'SET  element_discriminator = ''Parking'' '
                ||'WHERE element_discriminator = ''Parking_BuildingElement'' ';
        
        
        -- restrictions_policy
        
        EXECUTE 'UPDATE '||v_schema_name||'.restrictions_policy '
                ||'SET  age_of_majority = 18 '
                ||'WHERE age_of_majority IS NULL ';
        
        -- tax
        
        EXECUTE 'UPDATE '||v_schema_name||'.tax '
                        ||'SET  policy_node_discriminator = ''Province'' '
                        ||'WHERE policy_node_discriminator = ''Disc_Province'' ';
                        
                        
        /**     
        ***     ============================================================================================================
        ***
        ***             PRODUCT CATALOG MIGRATION 
        ***
        ***     ============================================================================================================
        **/
        
        -- Temporary code !!! 
        /*
        EXECUTE 'DELETE FROM '||v_schema_name||'.product_item '
                ||'WHERE id NOT IN      (SELECT DISTINCT item '
                ||'                     FROM '||v_schema_name||'.billable_item)';
                
        EXECUTE 'DELETE FROM '||v_schema_name||'.product_v '
                ||'WHERE id NOT IN      (SELECT DISTINCT product '
                ||'                     FROM '||v_schema_name||'.product_item)';
                
        
        EXECUTE 'DELETE FROM '||v_schema_name||'.product '
                ||'WHERE id NOT IN      (SELECT DISTINCT holder '
                ||'                     FROM '||v_schema_name||'.product_v)';
                
        */        
        
        -- Insert into arcode - if necessary
        
        
        
        EXECUTE 'SELECT COUNT(id) '
                ||'FROM '||v_schema_name||'.arcode '
                ||'WHERE name = ''Residential'' '
                INTO v_rowcount;
                
        IF (v_rowcount = 0) 
        THEN
                EXECUTE 'INSERT INTO '||v_schema_name||'.arcode (id,code_type,name,gl_code,updated,reserved) '
                        ||'(SELECT nextval(''public.arcode_seq'') AS id, ''Residential'' AS code_type,'
                        ||'''Residential'' AS name, id AS gl_code, '
                        ||'DATE_TRUNC(''second'',current_timestamp)::timestamp AS updated, ''FALSE'' AS reserved '
                        ||'FROM '||v_schema_name||'.gl_code '
                        ||'WHERE code_id = 5110 ) ';
        END IF;                        
                
              
        
        EXECUTE 'UPDATE '||v_schema_name||'.product AS p '
                ||'SET  default_catalog_item = ''FALSE'', '
                ||'     code = a.id '
                ||'FROM '||v_schema_name||'.arcode AS a '
                ||'WHERE a.name = ''Residential'' '
                ||'AND  p.id_discriminator = ''service'' ';
       
        
        EXECUTE 'UPDATE '||v_schema_name||'.product_v '
                ||'SET  price = 0.00 '
                ||'WHERE holder IN      (SELECT DISTINCT id FROM '||v_schema_name||'.product '
                ||'                     WHERE   id_discriminator  = ''service'' )';   
                        
        
                        
        SET CONSTRAINTS ALL IMMEDIATE;
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        -- advertising_blurb
        
        DROP TABLE advertising_blurb;
        
        -- application_document_blob
        
        DROP TABLE application_document_blob;
        
        -- application_document_file
        
        DROP TABLE application_document_file;
        
        -- application_wizard_step
        
        DROP TABLE application_wizard_step ;
        
        -- application_wizard_substep
        
        DROP TABLE application_wizard_substep;
        
        -- apt_unit
        
        ALTER TABLE apt_unit DROP COLUMN marketing;
        
        -- billing_account
        
        ALTER TABLE billing_account DROP COLUMN billing_cycle_start_day;
        
        -- charges
        
        DROP TABLE charges;
        
        -- custom_skin_resource_blob
        
        DROP TABLE custom_skin_resource_blob;
        
        
        -- customer_picture
        
        ALTER TABLE customer_picture    DROP COLUMN caption,
                                        DROP COLUMN description;
                                        
        -- customer_screening_income_info
        
        ALTER TABLE customer_screening_income_info      DROP COLUMN address_county,
                                                        DROP COLUMN address_street_direction,
                                                        DROP COLUMN address_street_name,
                                                        DROP COLUMN address_street_number,
                                                        DROP COLUMN address_street_number_suffix,
                                                        DROP COLUMN address_street_type,
                                                        DROP COLUMN address_suite_number;
                                                        
                                                        
        -- digital_signature
        
        DROP TABLE digital_signature;
        
        
        -- employee_signature
        
        ALTER TABLE employee_signature  DROP COLUMN caption,
                                        DROP COLUMN description; 
        
        -- identification_document
        
        DROP TABLE identification_document;
        
        
        -- ilsconfig
        
        -- ALTER TABLE ilsconfig DROP COLUMN x;
        
        
        -- ilsprofile_floorplan
        
        ALTER TABLE ilsprofile_floorplan        DROP COLUMN description,
                                                DROP COLUMN listing_title;
        
        -- insurance_certificate_doc
        
        DROP TABLE insurance_certificate_doc;
        
        
        -- insurance_certificate_scan
        
        ALTER TABLE insurance_certificate_scan  DROP COLUMN certificate_doc,
                                                DROP COLUMN caption;
        
        
        -- lease_term_participant
        
        ALTER TABLE lease_term_participant DROP COLUMN take_ownership;       
               
        -- legal_documentation
        
        DROP TABLE legal_documentation;
        
        
        -- legal_documentation$co_application
        
        DROP TABLE legal_documentation$co_application;
        
        
        -- legal_documentation$guarantor_application
        
        DROP TABLE legal_documentation$guarantor_application;
        
        
        -- legal_documentation$lease
        
        DROP TABLE legal_documentation$lease;
        
        
        -- legal_documentation$main_application
        
        DROP TABLE legal_documentation$main_application;
        
        
        -- legal_documentation$payment_authorization
        
        DROP TABLE legal_documentation$payment_authorization;
        
        
        -- legal_letter
        
        ALTER TABLE legal_letter        DROP COLUMN caption,
                                        DROP COLUMN description;
                                        
                                        
        -- legal_terms_content
        
        DROP TABLE legal_terms_content;
        
        
        -- legal_terms_descriptor
        
        DROP TABLE legal_terms_descriptor;
        
        
        -- legal_terms_descriptor$content
        
        DROP TABLE legal_terms_descriptor$content;
        
        
        -- marketing$ad_blurbs
        
        DROP TABLE marketing$ad_blurbs;
        
        
        -- master_online_application
        
        ALTER TABLE master_online_application   DROP COLUMN online_application_id,
                                                DROP COLUMN online_application_id_s;
        
        
        -- n4_policy
        
        ALTER TABLE n4_policy   DROP COLUMN mailing_address_street1,
                                DROP COLUMN mailing_address_street2;
        
        -- online_application$signatures
        
        DROP TABLE online_application$signatures;
        
        
        -- online_application$steps
        
        DROP TABLE online_application$steps;
        
        
        -- payment_information
        
        DROP TABLE payment_information;
        
        
        -- payment_type_selection_policy
        
        ALTER TABLE payment_type_selection_policy       DROP COLUMN prospect_direct_banking,
                                                        DROP COLUMN prospect_interac;
        
        -- product
        /*
        ALTER TABLE product     DROP COLUMN code_type,
                                DROP COLUMN is_default_catalog_item;
                                
        */                       
        -- product_item
        /*
        ALTER TABLE product_item        DROP COLUMN code,
                                        DROP COLUMN is_default;
        
        */
        -- proof_of_employment_document
        
        DROP TABLE proof_of_employment_document;
        
        -- property_phone
        
        DROP TABLE property_phone;
        
        -- summary
        
        DROP TABLE summary;
        
        -- tenant_charge_list$charges
        
        DROP TABLE tenant_charge_list$charges;
        
        -- tenant_charge
        
        DROP TABLE tenant_charge;
        
        -- tenant_charge_list
        
        DROP TABLE tenant_charge_list;
                 
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        
        -- foreign keys

        ALTER TABLE agreement_signatures$legal_terms_signatures ADD CONSTRAINT agreement_signatures$legal_terms_signatures_owner_fk FOREIGN KEY(owner) 
                REFERENCES agreement_signatures(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE agreement_signatures$legal_terms_signatures ADD CONSTRAINT agreement_signatures$legal_terms_signatures_value_fk FOREIGN KEY(value) 
                REFERENCES signed_agreement_legal_term(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE agreement_signatures ADD CONSTRAINT agreement_signatures_lease_term_tenant_fk FOREIGN KEY(lease_term_tenant) 
                REFERENCES lease_term_participant(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE building ADD CONSTRAINT building_landlord_fk FOREIGN KEY(landlord) REFERENCES landlord(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE community_event ADD CONSTRAINT community_event_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_signature ADD CONSTRAINT customer_signature_signing_user_fk FOREIGN KEY(signing_user) REFERENCES customer_user(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE identification_document_file ADD CONSTRAINT identification_document_file_owner_fk FOREIGN KEY(owner) 
                REFERENCES identification_document_folder(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE identification_document_folder ADD CONSTRAINT identification_document_folder_id_type_fk FOREIGN KEY(id_type) 
                REFERENCES identification_document_type(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE identification_document_folder ADD CONSTRAINT identification_document_folder_owner_fk FOREIGN KEY(owner) 
                REFERENCES customer_screening_v(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE ilsemail_config ADD CONSTRAINT ilsemail_config_config_fk FOREIGN KEY(config) REFERENCES ilsconfig(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE ilsprofile_email ADD CONSTRAINT ilsprofile_email_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE ilssummary_building ADD CONSTRAINT ilssummary_building_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE ilssummary_building ADD CONSTRAINT ilssummary_building_front_image_fk FOREIGN KEY(front_image) REFERENCES media_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE ilssummary_floorplan ADD CONSTRAINT ilssummary_floorplan_floorplan_fk FOREIGN KEY(floorplan) REFERENCES floorplan(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE ilssummary_floorplan ADD CONSTRAINT ilssummary_floorplan_front_image_fk FOREIGN KEY(front_image) REFERENCES media_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE insurance_certificate_scan ADD CONSTRAINT insurance_certificate_scan_certificate_fk FOREIGN KEY(certificate) 
                REFERENCES insurance_certificate(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE insurance_policy ADD CONSTRAINT insurance_policy_signature_fk FOREIGN KEY(signature) REFERENCES customer_signature(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE landlord ADD CONSTRAINT landlord_address_country_fk FOREIGN KEY(address_country) REFERENCES country(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE landlord ADD CONSTRAINT landlord_address_province_fk FOREIGN KEY(address_province) REFERENCES province(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE landlord ADD CONSTRAINT landlord_logo_fk FOREIGN KEY(logo) REFERENCES landlord_media(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE landlord ADD CONSTRAINT landlord_signature_fk FOREIGN KEY(signature) REFERENCES landlord_media(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_agreement_confirmation_term ADD CONSTRAINT lease_agreement_confirmation_term_policy_fk FOREIGN KEY(policy) 
                REFERENCES lease_agreement_legal_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_agreement_legal_term ADD CONSTRAINT lease_agreement_legal_term_policy_fk FOREIGN KEY(policy) REFERENCES lease_agreement_legal_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_application ADD CONSTRAINT lease_application_created_by_fk FOREIGN KEY(created_by) REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_application_confirmation_term ADD CONSTRAINT lease_application_confirmation_term_policy_fk FOREIGN KEY(policy) 
                REFERENCES lease_application_legal_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_application_legal_term ADD CONSTRAINT lease_application_legal_term_policy_fk FOREIGN KEY(policy) 
                REFERENCES lease_application_legal_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term_agreement_document ADD CONSTRAINT lease_term_agreement_document_lease_term_v_fk FOREIGN KEY(lease_term_v) 
                REFERENCES lease_term_v(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term_v$agreement_legal_terms ADD CONSTRAINT lease_term_v$agreement_legal_terms_owner_fk FOREIGN KEY(owner) 
                REFERENCES lease_term_v(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term_v$agreement_confirmation_term ADD CONSTRAINT lease_term_v$agreement_confirmation_term_owner_fk FOREIGN KEY(owner) 
                REFERENCES lease_term_v(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term_v$agreement_confirmation_term ADD CONSTRAINT lease_term_v$agreement_confirmation_term_value_fk FOREIGN KEY(value) 
                REFERENCES lease_agreement_confirmation_term(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term_v$agreement_legal_terms ADD CONSTRAINT lease_term_v$agreement_legal_terms_value_fk FOREIGN KEY(value) 
                REFERENCES lease_agreement_legal_term(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term_v$utilities ADD CONSTRAINT lease_term_v$utilities_owner_fk FOREIGN KEY(owner) REFERENCES lease_term_v(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term_v$utilities ADD CONSTRAINT lease_term_v$utilities_value_fk FOREIGN KEY(value) REFERENCES building_utility(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE legal_status ADD CONSTRAINT legal_status_lease_fk FOREIGN KEY(lease) REFERENCES lease(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE legal_status ADD CONSTRAINT legal_status_set_by_fk FOREIGN KEY(set_by) REFERENCES crm_user(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE maintenance_request_picture ADD CONSTRAINT maintenance_request_picture_maintenance_request_fk FOREIGN KEY(maintenance_request) 
                REFERENCES maintenance_request(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE legal_terms_policy ADD CONSTRAINT legal_terms_policy_prospect_portal_privacy_policy_fk FOREIGN KEY(prospect_portal_privacy_policy) 
                REFERENCES legal_terms_policy_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE legal_terms_policy ADD CONSTRAINT legal_terms_policy_prospect_portal_terms_and_conditions_fk FOREIGN KEY(prospect_portal_terms_and_conditions) 
                REFERENCES legal_terms_policy_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE legal_terms_policy ADD CONSTRAINT legal_terms_policy_resident_portal_privacy_policy_fk FOREIGN KEY(resident_portal_privacy_policy) 
                REFERENCES legal_terms_policy_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE legal_terms_policy ADD CONSTRAINT legal_terms_policy_resident_portal_terms_and_conditions_fk FOREIGN KEY(resident_portal_terms_and_conditions) 
                REFERENCES legal_terms_policy_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE master_online_application ADD CONSTRAINT master_online_application_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE master_online_application ADD CONSTRAINT master_online_application_floorplan_fk FOREIGN KEY(floorplan) REFERENCES floorplan(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE note_attachment ADD CONSTRAINT note_attachment_owner_fk FOREIGN KEY(owner) REFERENCES notes_and_attachments(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE online_application$confirmation_terms ADD CONSTRAINT online_application$confirmation_terms_owner_fk FOREIGN KEY(owner) 
                REFERENCES online_application(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE online_application$confirmation_terms ADD CONSTRAINT online_application$confirmation_terms_value_fk FOREIGN KEY(value) 
                REFERENCES signed_online_application_confirmation_term(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE online_application$legal_terms ADD CONSTRAINT online_application$legal_terms_owner_fk FOREIGN KEY(owner) 
                REFERENCES online_application(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE online_application$legal_terms ADD CONSTRAINT online_application$legal_terms_value_fk FOREIGN KEY(value) 
                REFERENCES signed_online_application_legal_term(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE online_application_wizard_step_status ADD CONSTRAINT online_application_wizard_step_status_online_application_fk FOREIGN KEY(online_application) 
                REFERENCES online_application(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_method ADD CONSTRAINT payment_method_signature_fk FOREIGN KEY(signature) REFERENCES customer_signature(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_posting_batch ADD CONSTRAINT payment_posting_batch_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_posting_batch ADD CONSTRAINT payment_posting_batch_deposit_details_merchant_account_fk FOREIGN KEY(deposit_details_merchant_account) 
                REFERENCES merchant_account(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_record ADD CONSTRAINT payment_record_batch_fk FOREIGN KEY(batch) REFERENCES payment_posting_batch(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE permission_to_enter_note ADD CONSTRAINT permission_to_enter_note_locale_fk FOREIGN KEY(locale) 
                REFERENCES available_locale(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE permission_to_enter_note ADD CONSTRAINT permission_to_enter_note_policy_fk FOREIGN KEY(policy) 
                REFERENCES maintenance_request_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pmc_company_info_contact ADD CONSTRAINT pmc_company_info_contact_company_info_fk FOREIGN KEY(company_info) 
                REFERENCES pmc_company_info(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE product ADD CONSTRAINT product_code_fk FOREIGN KEY(code) REFERENCES arcode(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE proof_of_employment_document_file ADD CONSTRAINT proof_of_employment_document_file_owner_fk FOREIGN KEY(owner) 
                REFERENCES proof_of_employment_document_folder(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE proof_of_employment_document_folder ADD CONSTRAINT proof_of_employment_document_folder_owner_fk FOREIGN KEY(owner) 
                REFERENCES customer_screening_income(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE signed_agreement_confirmation_term ADD CONSTRAINT signed_agreement_confirmation_term_signature_fk FOREIGN KEY(signature) 
                REFERENCES customer_signature(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE signed_agreement_confirmation_term ADD CONSTRAINT signed_agreement_confirmation_term_term_fk FOREIGN KEY(term) 
                REFERENCES lease_agreement_confirmation_term(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE signed_agreement_legal_term ADD CONSTRAINT signed_agreement_legal_term_signature_fk FOREIGN KEY(signature) 
                REFERENCES customer_signature(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE signed_agreement_legal_term ADD CONSTRAINT signed_agreement_legal_term_term_fk FOREIGN KEY(term) 
                REFERENCES lease_agreement_legal_term(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE signed_online_application_confirmation_term ADD CONSTRAINT signed_online_application_confirmation_term_signature_fk FOREIGN KEY(signature) 
                REFERENCES customer_signature(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE signed_online_application_confirmation_term ADD CONSTRAINT signed_online_application_confirmation_term_term_fk FOREIGN KEY(term) 
                REFERENCES lease_application_confirmation_term(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE signed_online_application_legal_term ADD CONSTRAINT signed_online_application_legal_term_signature_fk FOREIGN KEY(signature) 
                REFERENCES customer_signature(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE signed_online_application_legal_term ADD CONSTRAINT signed_online_application_legal_term_term_fk FOREIGN KEY(term) 
                REFERENCES lease_application_legal_term(id)  DEFERRABLE INITIALLY DEFERRED;

                
                
        -- check constraints
        
        ALTER TABLE agreement_signatures ADD CONSTRAINT agreement_signatures_id_discriminator_ck CHECK ((id_discriminator) IN ('Digital', 'Ink'));
        ALTER TABLE agreement_signatures ADD CONSTRAINT agreement_signatures_lease_term_tenant_discriminator_d_ck CHECK (lease_term_tenant_discriminator = 'Tenant');
        ALTER TABLE application_documentation_policy ADD CONSTRAINT application_documentation_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE arpolicy ADD CONSTRAINT arpolicy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE auto_pay_policy ADD CONSTRAINT auto_pay_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE background_check_policy ADD CONSTRAINT background_check_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE building_utility ADD CONSTRAINT building_utility_building_utility_type_e_ck 
                CHECK ((building_utility_type) IN ('airConditioning', 'cable', 'electricity', 'garbage', 'gas', 'heating', 'hydro', 'internet', 
                'other', 'sewage', 'telephone', 'television', 'water'));
        ALTER TABLE customer_signature ADD CONSTRAINT customer_signature_signature_format_e_ck 
                CHECK ((signature_format) IN ('AgreeBox', 'AgreeBoxAndFullName', 'FullName', 'Initials', 'None'));
        ALTER TABLE dates_policy ADD CONSTRAINT dates_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE deposit_policy ADD CONSTRAINT deposit_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE email_template ADD CONSTRAINT email_template_template_type_e_ck 
                CHECK ((template_type) IN ('ApplicationApproved', 'ApplicationCreatedApplicant', 'ApplicationCreatedCoApplicant', 'ApplicationCreatedGuarantor', 
                'ApplicationDeclined', 'AutoPaySetupConfirmation', 'MaintenanceRequestCancelled', 'MaintenanceRequestCompleted', 'MaintenanceRequestCreatedPMC', 
                'MaintenanceRequestCreatedTenant', 'MaintenanceRequestEntryNotice', 'MaintenanceRequestUpdated', 'OneTimePaymentSubmitted', 'PasswordRetrievalCrm', 
                'PasswordRetrievalProspect', 'PasswordRetrievalTenant', 'PaymentReceipt', 'PaymentReceiptWithWebPaymentFee', 'PaymentReturned', 'ProspectWelcome', 
                'TenantInvitation'));
        ALTER TABLE email_templates_policy ADD CONSTRAINT email_templates_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE emergency_contact ADD CONSTRAINT emergency_contact_relationship_e_ck 
                CHECK ((relationship) IN ('Aunt', 'Daughter', 'Father', 'Friend', 'Grandfather', 'Grandmother', 'Mother', 'Other', 'Son', 'Spouse', 'Uncle'));
        ALTER TABLE id_assignment_policy ADD CONSTRAINT id_assignment_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE ilsemail_config ADD CONSTRAINT ilsemail_config_frequency_e_ck CHECK ((frequency) IN ('daily', 'monthly', 'weekly'));
        ALTER TABLE insurance_certificate_scan ADD CONSTRAINT insurance_certificate_scan_certificate_discriminator_d_ck 
                CHECK ((certificate_discriminator) IN ('InsuranceGeneral', 'InsuranceTenantSure'));
        ALTER TABLE lease_application_legal_term ADD CONSTRAINT lease_application_legal_term_apply_to_role_e_ck CHECK ((apply_to_role) IN ('All', 'Applicant', 'Guarantor'));
        ALTER TABLE lease_application_legal_term ADD CONSTRAINT lease_application_legal_term_signature_format_e_ck 
                CHECK ((signature_format) IN ('AgreeBox', 'AgreeBoxAndFullName', 'FullName', 'Initials', 'None'));
        ALTER TABLE landlord ADD CONSTRAINT landlord_address_street_direction_e_ck 
                CHECK ((address_street_direction) IN ('east', 'north', 'northEast', 'northWest', 'south', 'southEast', 'southWest', 'west'));
        ALTER TABLE landlord ADD CONSTRAINT landlord_address_street_type_e_ck 
                CHECK ((address_street_type) IN ('alley', 'approach', 'arcade', 'avenue', 'boulevard', 'brow', 'bypass', 'causeway', 'circle', 'circuit', 
                'circus', 'close', 'copse', 'corner', 'court', 'cove', 'crescent', 'drive', 'end', 'esplanande', 'flat', 'freeway', 'frontage', 'gardens', 
                'glade', 'glen', 'green', 'grove', 'heights', 'highway', 'lane', 'line', 'link', 'loop', 'mall', 'mews', 'other', 'packet', 'parade', 'park', 
                'parkway', 'place', 'promenade', 'reserve', 'ridge', 'rise', 'road', 'row', 'square', 'street', 'strip', 'tarn', 'terrace', 'thoroughfaree', 
                'track', 'trunkway', 'view', 'vista', 'walk', 'walkway', 'way', 'yard'));
        ALTER TABLE lease_adjustment_policy ADD CONSTRAINT lease_adjustment_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE lease_agreement_confirmation_term ADD CONSTRAINT lease_agreement_confirmation_term_signature_format_e_ck 
                CHECK ((signature_format) IN ('AgreeBox', 'AgreeBoxAndFullName', 'FullName', 'Initials', 'None'));
        ALTER TABLE lease_agreement_legal_policy ADD CONSTRAINT lease_agreement_legal_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE lease_agreement_legal_term ADD CONSTRAINT lease_agreement_legal_term_signature_format_e_ck 
                CHECK ((signature_format) IN ('AgreeBox', 'AgreeBoxAndFullName', 'FullName', 'Initials', 'None'));
        ALTER TABLE lease_application_confirmation_term ADD CONSTRAINT lease_application_confirmation_term_signature_format_e_ck 
                CHECK ((signature_format) IN ('AgreeBox', 'AgreeBoxAndFullName', 'FullName', 'Initials', 'None'));
        ALTER TABLE lease_application_legal_policy ADD CONSTRAINT lease_application_legal_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE lease_billing_policy ADD CONSTRAINT lease_billing_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE legal_terms_policy ADD CONSTRAINT legal_terms_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE legal_status ADD CONSTRAINT legal_status_id_discriminator_ck CHECK (id_discriminator= 'LegalStatus');
        ALTER TABLE legal_status ADD CONSTRAINT legal_status_status_e_ck 
                CHECK ((status) IN ('HearingDate', 'L1', 'N4', 'None', 'Order', 'RequestToReviewOrder', 'SetAside', 'Sheriff', 'StayOrder'));
        ALTER TABLE maintenance_request ADD CONSTRAINT maintenance_request_phone_type_e_ck CHECK ((phone_type) IN ('home', 'mobile', 'work'));
        ALTER TABLE maintenance_request_category ADD CONSTRAINT maintenance_request_category_type_e_ck CHECK ((type) IN ('Amenities', 'ApartmentUnit', 'Exterior'));
        ALTER TABLE maintenance_request_policy ADD CONSTRAINT maintenance_request_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE master_online_application ADD CONSTRAINT master_online_application_status_e_ck 
                CHECK ((status) IN ('Approved', 'Cancelled', 'Incomplete', 'InformationRequested', 'Submitted'));
        ALTER TABLE n4_policy ADD CONSTRAINT n4_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE n4_policy ADD CONSTRAINT n4_policy_mailing_address_street_direction_e_ck 
                CHECK ((mailing_address_street_direction) IN ('east', 'north', 'northEast', 'northWest', 'south', 'southEast', 'southWest', 'west'));
        ALTER TABLE n4_policy ADD CONSTRAINT n4_policy_mailing_address_street_type_e_ck 
                CHECK ((mailing_address_street_type) IN ('alley', 'approach', 'arcade', 'avenue', 'boulevard', 'brow', 'bypass', 'causeway', 'circle', 
                'circuit', 'circus', 'close', 'copse', 'corner', 'court', 'cove', 'crescent', 'drive', 'end', 'esplanande', 'flat', 'freeway', 'frontage', 
                'gardens', 'glade', 'glen', 'green', 'grove', 'heights', 'highway', 'lane', 'line', 'link', 'loop', 'mall', 'mews', 'other', 'packet', 
                'parade', 'park', 'parkway', 'place', 'promenade', 'reserve', 'ridge', 'rise', 'road', 'row', 'square', 'street', 'strip', 'tarn', 'terrace', 
                'thoroughfaree', 'track', 'trunkway', 'view', 'vista', 'walk', 'walkway', 'way', 'yard'));
        ALTER TABLE notes_and_attachments ADD CONSTRAINT notes_and_attachments_owner_discriminator_d_ck 
                CHECK ((owner_discriminator) IN ('ARPolicy', 'AggregatedTransfer', 'AgreementLegalPolicy', 'ApplicationDocumentationPolicy', 
                'AptUnit', 'AutoPayPolicy', 'AutopayAgreement', 'BackgroundCheckPolicy', 'Building', 'Complex', 'DatesPolicy', 'DepositPolicy', 
                'EmailTemplatesPolicy', 'Employee', 'Floorplan', 'Guarantor', 'IdAssignmentPolicy', 'Landlord', 'Lease', 'LeaseAdjustmentPolicy', 
                'LeaseBillingPolicy', 'LegalTermsPolicy', 'Locker', 'MaintenanceRequest', 'MaintenanceRequestPolicy', 'MerchantAccount', 
                'N4Policy', 'OnlineAppPolicy', 'PaymentPostingBatch', 'PaymentRecord', 'PaymentTransactionsPolicy', 'PaymentTypeSelectionPolicy', 
                'PetPolicy', 'ProductTaxPolicy', 'ProspectPortalPolicy', 'RestrictionsPolicy', 'Tenant', 'TenantInsurancePolicy', 
                'YardiInterfacePolicy', 'feature', 'service'));
        ALTER TABLE online_application ADD CONSTRAINT online_application_role_e_ck CHECK ((role) IN ('Applicant', 'CoApplicant', 'Dependent', 'Guarantor'));
        ALTER TABLE online_application_wizard_step_status ADD CONSTRAINT online_application_wizard_step_status_step_e_ck 
                CHECK ((step) IN ('AboutYou', 'AdditionalInfo', 'Confirmation', 'Contacts', 'Financial', 'Lease', 'Legal', 'Options', 
                'Payment', 'People', 'Summary', 'Unit'));
        ALTER TABLE payment_posting_batch ADD CONSTRAINT payment_posting_batch_created_by_discriminator_d_ck CHECK ((created_by_discriminator) IN ('CrmUser', 'CustomerUser'));
        ALTER TABLE payment_posting_batch ADD CONSTRAINT payment_posting_batch_status_e_ck CHECK ((status) IN ('Canceled', 'Created', 'Posted'));
        ALTER TABLE payment_transactions_policy ADD CONSTRAINT payment_transactions_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE payment_type_selection_policy ADD CONSTRAINT payment_type_selection_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE pet_policy ADD CONSTRAINT pet_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE pmc_company_info_contact ADD CONSTRAINT pmc_company_info_contact_tp_e_ck CHECK (tp = 'administrator');
        ALTER TABLE product_item ADD CONSTRAINT product_item_element_discriminator_d_ck CHECK ((element_discriminator) IN ('AptUnit', 'LockerArea', 'Parking', 'Roof'));
        ALTER TABLE product_tax_policy ADD CONSTRAINT product_tax_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE prospect_portal_policy ADD CONSTRAINT prospect_portal_policy_fee_payment_e_ck CHECK ((fee_payment) IN ('none', 'perApplicant', 'perLease'));
        ALTER TABLE prospect_portal_policy ADD CONSTRAINT prospect_portal_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE restrictions_policy ADD CONSTRAINT restrictions_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE tax ADD CONSTRAINT tax_policy_node_discriminator_d_ck 
                CHECK ((policy_node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE tenant_insurance_policy ADD CONSTRAINT tenant_insurance_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE yardi_interface_policy ADD CONSTRAINT yardi_interface_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));


        
        -- not null
        
        ALTER TABLE lease ALTER COLUMN integration_system_id DROP NOT NULL;
        ALTER TABLE lease ALTER COLUMN lease_id DROP NOT NULL;
        --ALTER TABLE product ALTER COLUMN code SET NOT NULL;
        
       
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        CREATE INDEX agreement_signatures$legal_terms_signatures_owner_idx ON agreement_signatures$legal_terms_signatures USING btree (owner);
        CREATE INDEX ilsprofile_email_building_idx ON ilsprofile_email USING btree (building);
        CREATE INDEX lease_term_v$agreement_confirmation_term_owner_idx ON lease_term_v$agreement_confirmation_term USING btree (owner);
        CREATE INDEX lease_term_v$agreement_legal_terms_owner_idx ON lease_term_v$agreement_legal_terms USING btree (owner);
        CREATE INDEX lease_term_v$utilities_owner_idx ON lease_term_v$utilities USING btree (owner);
        CREATE INDEX online_application$confirmation_terms_owner_idx ON online_application$confirmation_terms USING btree (owner);
        CREATE INDEX online_application$legal_terms_owner_idx ON online_application$legal_terms USING btree (owner);
        CREATE INDEX agreement_signatures_lease_term_tenant_discriminator_idx ON agreement_signatures USING btree (lease_term_tenant_discriminator);
        CREATE INDEX agreement_signatures_lease_term_tenant_idx ON agreement_signatures USING btree (lease_term_tenant);
        CREATE INDEX ilssummary_building_building_idx ON ilssummary_building USING btree (building);
        CREATE INDEX ilssummary_floorplan_floorplan_idx ON ilssummary_floorplan USING btree (floorplan);
        CREATE UNIQUE INDEX lease_application_application_id_idx ON lease_application USING btree (LOWER(application_id));
        CREATE INDEX lease_term_agreement_document_lease_term_v_idx ON lease_term_agreement_document USING btree (lease_term_v);
        CREATE INDEX payment_posting_batch_building_idx ON payment_posting_batch USING btree (building);
        CREATE INDEX permission_to_enter_note_policy_idx ON permission_to_enter_note USING btree (policy);


        
        -- billing_arrears_snapshot -GiST index!
        
        CREATE INDEX billing_arrears_snapshot_from_date_to_date_idx ON billing_arrears_snapshot 
                USING GiST (box(point(from_date,from_date),point(to_date,to_date)) box_ops);
        
        
        
        
        -- Finishing touch
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.1.3',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;

        
