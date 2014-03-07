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
        ALTER TABLE billing_bill DROP CONSTRAINT billing_bill_lease_fk;
        ALTER TABLE charges DROP CONSTRAINT charges_application_charges_fk;
        ALTER TABLE charges DROP CONSTRAINT charges_application_fk;
        ALTER TABLE charges DROP CONSTRAINT charges_monthly_charges_fk;
        ALTER TABLE charges DROP CONSTRAINT charges_one_time_charges_fk;
        ALTER TABLE charges DROP CONSTRAINT charges_payment_split_charges_fk;
        ALTER TABLE charges DROP CONSTRAINT charges_prorated_charges_fk;
        ALTER TABLE deposit_policy_item DROP CONSTRAINT deposit_policy_item_policy_fk;
        ALTER TABLE deposit_policy_item DROP CONSTRAINT deposit_policy_item_product_code_fk;
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
        ALTER TABLE product_item DROP CONSTRAINT product_item_code_fk;
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
        ALTER TABLE apt_unit_occupancy_segment DROP CONSTRAINT apt_unit_occupancy_segment_status_e_ck;
        ALTER TABLE arpolicy DROP CONSTRAINT arpolicy_node_discriminator_d_ck;
        ALTER TABLE auto_pay_policy DROP CONSTRAINT auto_pay_policy_node_discriminator_d_ck;
        ALTER TABLE background_check_policy DROP CONSTRAINT background_check_policy_node_discriminator_d_ck;
        ALTER TABLE billable_item DROP CONSTRAINT billable_item_extra_data_discriminator_d_ck;
        ALTER TABLE building_utility DROP CONSTRAINT building_utility_building_utility_type_e_ck;
        ALTER TABLE customer_screening_income_info DROP CONSTRAINT customer_screening_income_info_address_street_direction_e_ck;
        ALTER TABLE customer_screening_income_info DROP CONSTRAINT customer_screening_income_info_address_street_type_e_ck;
        ALTER TABLE dates_policy DROP CONSTRAINT dates_policy_node_discriminator_d_ck;
        ALTER TABLE deposit_policy DROP CONSTRAINT deposit_policy_node_discriminator_d_ck;
        ALTER TABLE deposit_policy_item DROP CONSTRAINT deposit_policy_item_deposit_type_e_ck;
        ALTER TABLE deposit_policy_item DROP CONSTRAINT deposit_policy_item_value_type_e_ck;
        ALTER TABLE email_template DROP CONSTRAINT email_template_template_type_e_ck;
        ALTER TABLE email_templates_policy DROP CONSTRAINT email_templates_policy_node_discriminator_d_ck;
        ALTER TABLE id_assignment_policy DROP CONSTRAINT id_assignment_policy_node_discriminator_d_ck;
        ALTER TABLE identification_document DROP CONSTRAINT identification_document_owner_discriminator_d_ck;
        ALTER TABLE insurance_certificate_doc DROP CONSTRAINT insurance_certificate_doc_certificate_discriminator_d_ck;
        ALTER TABLE lead DROP CONSTRAINT lead_lease_type_e_ck;
        ALTER TABLE lease DROP CONSTRAINT lease_lease_type_e_ck;
        ALTER TABLE lease_adjustment_policy DROP CONSTRAINT lease_adjustment_policy_node_discriminator_d_ck;
        ALTER TABLE lease_billing_policy DROP CONSTRAINT lease_billing_policy_node_discriminator_d_ck;
        ALTER TABLE legal_documentation DROP CONSTRAINT legal_documentation_node_discriminator_d_ck;
        ALTER TABLE legal_letter DROP CONSTRAINT legal_letter_id_discriminator_ck;
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
        ALTER TABLE insurance_certificate_doc DROP CONSTRAINT insurance_certificate_doc_pk;
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
        ALTER TABLE payment_information DROP CONSTRAINT payment_information_pk;
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
        
        DROP INDEX apt_unit__available_for_rent_idx;
        DROP INDEX notes_and_attachments_owner_id_owner_class_idx;
        
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
                id                                      BIGINT                  NOT NULL,
                id_discriminator                        VARCHAR(64)             NOT NULL,
                lease_term_participant_discriminator    VARCHAR(50)             NOT NULL,
                lease_term_participant                  BIGINT                  NOT NULL,
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
        
        -- application_documentation_policy
        
        ALTER TABLE application_documentation_policy ADD COLUMN mandatory_proof_of_income BOOLEAN;
        
        -- apt_unit_effective_availability
        
        CREATE TABLE apt_unit_effective_availability
        (
                id                              BIGINT                  NOT NULL,
                unit                            BIGINT                  NOT NULL,
                available_for_rent              DATE,
                updated                         TIMESTAMP,
                        CONSTRAINT apt_unit_effective_availability_pk PRIMARY KEY(id)
        );
        
        
        ALTER TABLE apt_unit_effective_availability OWNER TO vista;
        
        
        -- apt_unit_reservation
        
        CREATE TABLE apt_unit_reservation
        (
                id                                      BIGINT                  NOT NULL,
                unit                                    BIGINT                  NOT NULL,
                date_from                               TIMESTAMP,
                date_to                                 TIMESTAMP,
                lease                                   BIGINT,
                        CONSTRAINT apt_unit_reservation_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE apt_unit_reservation OWNER TO vista;
        
		-- billable_item
		
		ALTER TABLE billable_item ADD COLUMN yardi_charge_code VARCHAR(500);
       
        
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
        
        
        -- crm_user_signature
        
        CREATE TABLE crm_user_signature
        (
                id                              BIGINT                  NOT NULL,
                sign_date                       TIMESTAMP,
                ip_address                      VARCHAR(39),
                signature_format                VARCHAR(50),
                full_name                       VARCHAR(500),
                initials                        VARCHAR(500),
                agree                           BOOLEAN,
                signing_user                    BIGINT,
                        CONSTRAINT crm_user_signature_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE crm_user_signature OWNER TO vista;
        
        
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
		
		-- deposit
		
		ALTER TABLE deposit ADD COLUMN charge_code BIGINT;
        
        
        -- deposit_policy
        
        ALTER TABLE deposit_policy  ADD COLUMN annual_interest_rate NUMERIC(18,2),
                                    ADD COLUMN security_deposit_refund_window INT;
		
		
		
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
                data                            BYTEA,
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
                description 					VARCHAR(500),
                        CONSTRAINT identification_document_file_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE identification_document_file OWNER TO vista;
        
        -- identification_document_folder
        
        CREATE TABLE identification_document_folder
        (
                id                              BIGINT                  NOT NULL,
                owner                           BIGINT                  NOT NULL,
                id_type                         BIGINT,
                id_number                       VARCHAR(500),
                notes                           VARCHAR(500),
                        CONSTRAINT identification_document_folder_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE identification_document_folder OWNER TO vista;
        
        -- identification_document_type
        
        ALTER TABLE identification_document_type ADD COLUMN importance VARCHAR(50);
        
        
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
                                        ADD COLUMN application_id_s VARCHAR(26),
                                        ADD COLUMN yardi_application_id VARCHAR(500);
                                        
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
                apply_to_role					VARCHAR(50),
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
        
        
        -- lease_participant
        
        ALTER TABLE lease_participant ADD COLUMN yardi_applicant_id VARCHAR(500);
        
        
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
                is_signed_by_ink                BOOLEAN,
                signed_employee_uploader        BIGINT,
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
        
        
        -- lease_term_agreement_document$signed_participants
        
        CREATE TABLE lease_term_agreement_document$signed_participants
        (
                id                              BIGINT                  NOT NULL,
                owner                           BIGINT,
                value_discriminator             VARCHAR(50),
                value                           BIGINT,
                seq                             INT,
                        CONSTRAINT lease_term_agreement_document$signed_participants_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE lease_term_agreement_document$signed_participants OWNER TO vista;
        
        
        -- lease_term_v
        
        ALTER TABLE lease_term_v ADD COLUMN employee_signature BIGINT;
        
        
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
                                        ADD COLUMN termination_date DATE,
                                        ADD COLUMN status BIGINT,
										ADD COLUMN status_discriminator VARCHAR(50);
        
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
        
        
        -- maintenance_request_status_record
        
        CREATE TABLE maintenance_request_status_record
        (
                id                              BIGINT                  NOT NULL,
                request                         BIGINT,
                old_status                      BIGINT,
                new_status                      BIGINT,
                updated_by_discriminator        VARCHAR(50),
                updated_by                      BIGINT,
                created                         TIMESTAMP,
                        CONSTRAINT maintenance_request_status_record_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE maintenance_request_status_record OWNER TO vista;
        
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
        
        ALTER TABLE notes_and_attachments RENAME COLUMN owner_id TO owner;
		ALTER TABLE notes_and_attachments RENAME COLUMN owner_class TO owner_discriminator;
		ALTER TABLE notes_and_attachments ALTER COLUMN owner_discriminator TYPE VARCHAR(50);
                                                
       
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
                                ADD COLUMN expired_from DATE,
                                ADD COLUMN yardi_code VARCHAR(500);
                                
        ALTER TABLE product RENAME COLUMN is_default_catalog_item TO default_catalog_item;
        
        
        
        -- product_item
        
        ALTER TABLE product_item    ADD COLUMN name VARCHAR(50),
                                    ADD COLUMN deposit_lmr NUMERIC(18,2),
                                    ADD COLUMN deposit_move_in NUMERIC(18,2),
                                    ADD COLUMN deposit_security NUMERIC(18,2);
        
        
        -- product_v
        
        ALTER TABLE product_v   ADD COLUMN available_online BOOLEAN,
                                ADD COLUMN price NUMERIC(18,2),
                                ADD COLUMN deposit_lmr_enabled BOOLEAN,
                                ADD COLUMN deposit_lmr_charge_code BIGINT,
                                ADD COLUMN deposit_lmr_deposit_value NUMERIC(18,2),
                                ADD COLUMN deposit_lmr_value_type VARCHAR(50),
                                ADD COLUMN deposit_lmr_description VARCHAR(40),
                                ADD COLUMN deposit_lmr_deposit_type VARCHAR(50),
                                ADD COLUMN deposit_move_in_enabled BOOLEAN,
                                ADD COLUMN deposit_move_in_charge_code BIGINT,
                                ADD COLUMN deposit_move_in_deposit_value NUMERIC(18,2),
                                ADD COLUMN deposit_move_in_value_type VARCHAR(50),
                                ADD COLUMN deposit_move_in_description VARCHAR(40),
                                ADD COLUMN deposit_move_in_deposit_type VARCHAR(50),
                                ADD COLUMN deposit_security_enabled BOOLEAN,
                                ADD COLUMN deposit_security_charge_code BIGINT,
                                ADD COLUMN deposit_security_deposit_value NUMERIC(18,2),
                                ADD COLUMN deposit_security_value_type VARCHAR(50),
                                ADD COLUMN deposit_security_description VARCHAR(40),
                                ADD COLUMN deposit_security_deposit_type VARCHAR(50);
        
        
        -- proof_of_asset_document_blob
        
        CREATE TABLE proof_of_asset_document_blob
        (
                id                      BIGINT                  NOT NULL,
                name                    VARCHAR(500),
                data                    BYTEA,
                content_type            VARCHAR(500),
                updated                 TIMESTAMP,
                created                 TIMESTAMP,
                        CONSTRAINT proof_of_asset_document_blob_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE proof_of_asset_document_blob OWNER TO vista;
        
        
        -- proof_of_asset_document_file
        
        CREATE TABLE proof_of_asset_document_file
        (
                id                      BIGINT                  NOT NULL,
                owner                   BIGINT,
                file_file_name          VARCHAR(500),
                file_updated_timestamp  BIGINT,
                file_cache_version      INT,
                file_file_size          INT,
                file_content_mime_type  VARCHAR(500),
                file_blob_key           BIGINT,
                description 			VARCHAR(500),
                order_in_owner          INT,
                        CONSTRAINT proof_of_asset_document_file_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE proof_of_asset_document_file OWNER TO vista;
        
        
        -- proof_of_asset_document_folder
        
        CREATE TABLE proof_of_asset_document_folder
        (
                id                      BIGINT                  NOT NULL,
                owner                   BIGINT                  NOT NULL,
                description             VARCHAR(500),
                        CONSTRAINT proof_of_asset_document_folder_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE proof_of_asset_document_folder OWNER TO vista;
        
        
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
                owner                           BIGINT,
                file_file_name                  VARCHAR(500),
                file_updated_timestamp          BIGINT,
                file_cache_version              INT,
                file_file_size                  INT,
                file_content_mime_type          VARCHAR(500),
                file_blob_key                   BIGINT,
                description 					VARCHAR(500),
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
        
        -- advertising_blurb to ilssummary_building
        
        EXECUTE 'INSERT INTO    '||v_schema_name||'.ilssummary_building (id,building,title,description) '
                ||'(SELECT      nextval(''public.ilssummary_building_seq'') AS id, '
                ||'b.id AS building,m.name AS title,a.content AS description '
                ||'FROM         '||v_schema_name||'.building b '
                ||'JOIN         '||v_schema_name||'.marketing m ON (b.marketing = m.id) '
                ||'JOIN         '||v_schema_name||'.marketing$ad_blurbs mb ON (m.id = mb.owner) '
                ||'JOIN         '||v_schema_name||'.advertising_blurb a ON (a.id = mb.value) '
                ||'ORDER BY b.id )';
                
        -- aging_buckets
        
        /*
        
        EXECUTE 'UPDATE '||v_schema_name||'.aging_buckets '
                ||'SET  ar_code = ''DepositSecurity'' '
                ||'WHERE    ar_code = ''Deposit'' ';
                
                
        */
                
        -- apt_unit_effective_availability
        
        EXECUTE 'INSERT INTO    '||v_schema_name||'.apt_unit_effective_availability(id,unit,available_for_rent,updated) '
                ||'(SELECT      nextval(''public.apt_unit_effective_availability_seq'') AS id, '
                ||'             a.id AS unit, a._available_for_rent AS available_for_rent, '
                ||'             DATE_TRUNC(''second'',current_timestamp)::timestamp AS updated '
                ||'FROM         '||v_schema_name||'.apt_unit AS a '
                ||'ORDER BY     a.id )';
                
        -- arcode
        
        /*
        
        EXECUTE 'UPDATE '||v_schema_name||'.arcode '
                ||'SET  code_type = ''DepositSecurity'', '
                ||'     name = ''Security Deposit'' '
                ||'WHERE    code_type = ''Deposit'' ';
        */
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.arcode (id,code_type,name,updated,reserved) VALUES '
                ||'(nextval(''public.arcode_seq''),''Deposit'',''LMR Deposit'',DATE_TRUNC(''second'',current_timestamp)::timestamp,TRUE),'
                ||'(nextval(''public.arcode_seq''),''Deposit'',''Move In Deposit'',DATE_TRUNC(''second'',current_timestamp)::timestamp,TRUE),'
                ||'(nextval(''public.arcode_seq''),''Deposit'',''Security Deposit'',DATE_TRUNC(''second'',current_timestamp)::timestamp,TRUE),'
                ||'(nextval(''public.arcode_seq''),''DepositRefund'',''DepositRefund'',DATE_TRUNC(''second'',current_timestamp)::timestamp,TRUE)';
                
        
		
        -- apt_unit_occupancy_segment
        
        EXECUTE 'UPDATE '||v_schema_name||'.apt_unit_occupancy_segment '
                ||'SET  status = ''available'','
                ||'     lease = NULL '
                ||'WHERE    status = ''reserved'' ';
                
                
        -- billable_item
		
		EXECUTE 'UPDATE '||v_schema_name||'.billable_item b '
				||'SET	yardi_charge_code = y.charge_code, '
                ||'     extra_data_discriminator = NULL, '
                ||'     extra_data = NULL '
				||'FROM '||v_schema_name||'.yardi_lease_charge_data y '
				||'WHERE	extra_data_discriminator = ''YardiLeaseCharge'' '
				||'AND	b.extra_data = y.id ';
        
        -- insurance_certificate_scan
        
        EXECUTE 'UPDATE '||v_schema_name||'.insurance_certificate_scan AS s '
                ||'SET  certificate = d.certificate, '
                ||'     certificate_discriminator = d.certificate_discriminator '
                ||'FROM '||v_schema_name||'.insurance_certificate_doc AS d '
                ||'WHERE s.certificate_doc = d.id ';
                
        
        -- lease_agreement_legal_policy
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.lease_agreement_legal_policy (id,node_discriminator,node,updated) '
                ||'(SELECT nextval(''public.lease_agreement_legal_policy_seq'') AS id, '
                ||' ''OrganizationPoliciesNode'' AS node_discriminator, id AS node, '
                ||'DATE_TRUNC(''second'',current_timestamp)::timestamp AS updated '
                ||'FROM     '||v_schema_name||'.organization_policies_node )';
                
		-- lease_agreement_legal_term
		
		EXECUTE 'INSERT INTO '||v_schema_name||'.lease_agreement_legal_term(id,policy,title,body,signature_format,order_id) '
				||'(SELECT 	nextval(''public.lease_agreement_legal_term_seq'') AS id, '
				||'			p.id AS policy,t.title,t.body,t.signature_format,t.order_id '
				||'FROM 	'||v_schema_name||'.lease_agreement_legal_policy p, '
				||'			_dba_.lease_agreement_legal_term t )';
        
        -- lease_application_legal_policy
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.lease_application_legal_policy (id,node_discriminator,node,updated) '
                ||'(SELECT nextval(''public.lease_agreement_legal_policy_seq'') AS id, '
                ||' ''OrganizationPoliciesNode'' AS node_discriminator, id AS node, '
                ||'DATE_TRUNC(''second'',current_timestamp)::timestamp AS updated '
                ||'FROM     '||v_schema_name||'.organization_policies_node )';
                
                
		-- lease_application_legal_term
		
		EXECUTE 'INSERT INTO '||v_schema_name||'.lease_application_legal_term(id,policy,title,body,apply_to_role,signature_format,order_id) '
				||'(SELECT 	nextval(''public.lease_agreement_legal_term_seq'') AS id, '
				||'			p.id AS policy,t.title,t.body,t.apply_to_role,t.signature_format,t.order_id '
				||'FROM 	'||v_schema_name||'.lease_application_legal_policy p, '
				||'			_dba_.lease_application_legal_term t )';
        
        
        -- legal_terms_policy_item
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.legal_terms_policy_item(id,caption,enabled,content) '
                ||'(SELECT nextval(''public.legal_terms_policy_item_seq'') AS id, caption, enabled, content '
                ||'FROM         _dba_.legal_terms_policy_item )';
                
                
        -- legal_terms_policy
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.legal_terms_policy(id,node_discriminator,node) '
                ||'(SELECT nextval(''public.legal_terms_policy_seq'') AS id, '
                ||'''OrganizationPoliciesNode'' AS node_discriminator, id AS node '
                ||'FROM         '||v_schema_name||'.organization_policies_node) ';
                
                
        EXECUTE 'UPDATE '||v_schema_name||'.legal_terms_policy  AS p '
                ||'SET  resident_portal_terms_and_conditions = a.id, '
                ||'     resident_portal_privacy_policy = b.id, '
                ||'     prospect_portal_terms_and_conditions = c.id,'
                ||'     prospect_portal_privacy_policy = d.id, '
                ||'     updated = DATE_TRUNC(''second'',current_timestamp)::timestamp '
                ||'FROM '||v_schema_name||'.legal_terms_policy_item AS a, '
                ||'     '||v_schema_name||'.legal_terms_policy_item AS b, '
                ||'     '||v_schema_name||'.legal_terms_policy_item AS c, '
                ||'     '||v_schema_name||'.legal_terms_policy_item AS d '
                ||'WHERE        a.caption = ''RESIDENT PORTAL TERMS AND CONDITIONS'' '
                ||'AND          b.caption = ''RESIDENT PORTAL PRIVACY POLICY'' '
                ||'AND          c.caption = ''ONLINE APPLICATION TERMS AND CONDITIONS'' '
                ||'AND          d.caption = ''ONLINE APPLICATION PRIVACY POLICY'' ';
        
                
        -- maintenance_request
        
        EXECUTE 'UPDATE '||v_schema_name||'.maintenance_request '
                ||'SET  reported_date = submitted::date ';
        
        
        -- marketing - delete extra rows 
        
        EXECUTE 'DELETE FROM '||v_schema_name||'.marketing '
                ||'WHERE id IN  (SELECT DISTINCT marketing '
                ||'             FROM '||v_schema_name||'.apt_unit) '; 
        
        
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
        
        
        -- notes_and_attachments
        
        EXECUTE 'UPDATE '||v_schema_name||'.notes_and_attachments '
				||'SET		owner_discriminator = ''service'' '
				||'WHERE 	owner_discriminator = ''Service'' ';
         
        -- policy tables
        
        PERFORM * FROM _dba_.update_policy_tables(v_schema_name);
        
        
        
        -- product_item 
        
        EXECUTE 'UPDATE '||v_schema_name||'.product_item '
                ||'SET  element_discriminator = ''AptUnit'' '
                ||'WHERE element_discriminator = ''Unit_BuildingElement'' ';
                
                
        EXECUTE 'UPDATE '||v_schema_name||'.product_item '
                ||'SET  element_discriminator = ''Parking'' '
                ||'WHERE element_discriminator = ''Parking_BuildingElement'' ';

        EXECUTE 'UPDATE '||v_schema_name||'.product_item '
                ||'SET  element_discriminator = ''Roof'' '
                ||'WHERE element_discriminator = ''Roof_BuildingElement'' ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.product_item '
                ||'SET  element_discriminator = ''LockerArea'' '
                ||'WHERE element_discriminator = ''LockerArea_BuildingElement'' ';                        
        
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
                
                
		-- update of old product_v records 
		
		EXECUTE	'UPDATE '||v_schema_name||'.product_v AS pv '
				||'SET	from_date = DATE_TRUNC(''second'',current_timestamp)::timestamp, '
				||'		version_number = t.version '
				||'FROM 	(SELECT id, '
				||'					row_number() OVER (PARTITION BY holder ORDER BY id) AS version '
				||'			FROM    '||v_schema_name||'.product_v '
				||'			WHERE   from_date IS NULL ) AS t '
				||'WHERE	pv.id = t.id ';
		
        -- Update existing features in product table 
        
        EXECUTE 'UPDATE '||v_schema_name||'.product AS p '
                ||'SET  code = t.min_code '
                ||'FROM         (SELECT pv.holder,MIN(pi.code) AS min_code '
                ||'             FROM    '||v_schema_name||'.product_v pv '
                ||'             JOIN    '||v_schema_name||'.product_item pi ON (pv.id = pi.product) '
                ||'             WHERE   pi.product_discriminator = ''feature'' '
                ||'             AND     pv.to_date IS NULL '
                ||'             AND     pv.from_date IS NOT NULL '
                ||'             GROUP BY pv.holder ) AS t '
                ||'WHERE p.id = t.holder ';
                
        
        -- insert new records into product_table 
        
        ALTER TABLE product ALTER COLUMN code_type DROP NOT NULL;
        
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.product (id,id_discriminator,catalog,updated,default_catalog_item,code) '
                ||'(SELECT      nextval(''public.product_seq'') AS id, pi.product_discriminator AS id_discriminator,'
                ||'             p.catalog,DATE_TRUNC(''second'',current_timestamp)::timestamp AS updated,'
                ||'             TRUE, pi.code '
                ||'FROM '||v_schema_name||'.product p '
                ||'JOIN '||v_schema_name||'.product_v pv ON (p.id = pv.holder) '
                ||'JOIN '||v_schema_name||'.product_item pi ON (pv.id = pi.product) '
                ||'WHERE        pi.product_discriminator = ''feature'' '
                ||'AND          pv.to_date IS NULL '
                ||'AND          pv.from_date IS NOT NULL '
                ||'AND          pi.code != p.code ) ';             
        
        -- new records for product_v table
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.product_v (id,id_discriminator,version_number,'
                ||'from_date,holder_discriminator,holder,name,mandatory) '
                ||'(SELECT  nextval(''public.product_v_seq'') AS id,p.id_discriminator AS id_discriminator, '
                ||'1,DATE_TRUNC(''second'',current_timestamp)::timestamp AS from_date, '
                ||'p.id_discriminator AS holder_discriminator,p.id AS holder,a.code_type AS name, FALSE '
                ||'FROM '||v_schema_name||'.product p '
                ||'JOIN '||v_schema_name||'.arcode a ON (p.code = a.id) '
                ||'WHERE    p.id NOT IN (SELECT DISTINCT holder FROM '||v_schema_name||'.product_v ) )';
                
                
        -- wondrously perverted update of product_item
        
        EXECUTE 'WITH   t0 AS   (SELECT     p.id AS p_id, p.catalog,p.code, '
                ||'                         pv.id AS pv_id,'
                ||'                         pi.id AS pi_id, pi.code AS pi_code '
                ||'             FROM        '||v_schema_name||'.product p '
                ||'             JOIN        '||v_schema_name||'.product_v pv  ON (p.id = pv.holder) '
                ||'             JOIN        '||v_schema_name||'.product_item pi ON (pv.id = pi.product) '
                ||'             WHERE       p.id_discriminator = ''feature'' '
                ||'             AND         p.code != pi.code), '
                ||'     t1 AS   (SELECT     p.id AS p_id,p.catalog,p.code,pv.id AS pv_id '
                ||'             FROM        '||v_schema_name||'.product p '
                ||'             JOIN        '||v_schema_name||'.product_v pv ON (p.id = pv.holder) '
                ||'             WHERE       p.id_discriminator = ''feature'' '
                ||'             AND         pv.id NOT IN (SELECT DISTINCT product FROM '||v_schema_name||'.product_item)) '
                ||'UPDATE   '||v_schema_name||'.product_item pi '
                ||'SET      product = t2.pv_id '
                ||'FROM    (SELECT      t0.pi_id, t1.pv_id  '
                ||'         FROM        t0 '
                ||'         JOIN        t1 ON (t0.catalog = t1.catalog AND t0.pi_code = t1.code)) AS t2 '
                ||'WHERE   pi.id = t2.pi_id ';
                
        -- insert on product_v$features
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.product_v$features (id,owner,value_discriminator,value) '
                ||'(SELECT  nextval(''public.product_v$features_seq'') AS id, s.id AS owner, '
                ||'         f.id_discriminator AS value_discriminator,f.id AS value '
                ||'FROM    (SELECT      id  '
                ||'         FROM        '||v_schema_name||'.product_v '
                ||'         WHERE       id_discriminator = ''service'' '
                ||'         AND         to_date IS NULL '
                ||'         AND         name IN (''Residential Unit'',''Commercial Unit'')) AS s, '
                ||'         (SELECT     id,id_discriminator '
                ||'         FROM        '||v_schema_name||'.product '
                ||'         WHERE       code IS NOT NULL '
                ||'         AND         id_discriminator = ''feature'' '
                ||'         AND         id NOT IN (SELECT DISTINCT value FROM '||v_schema_name||'.product_v$features)) AS f )';
                
		
		-- delete those rare rows that do not have a code still
		
		EXECUTE 'DELETE FROM '||v_schema_name||'.product_v '
				||'WHERE 	holder IN 	(SELECT 	id '
				||'						FROM 	'||v_schema_name||'.product '
				||'						WHERE	code IS NULL) ';
		
		EXECUTE 'DELETE FROM '||v_schema_name||'.product '
				||'WHERE	code IS NULL';
				
	
		-- update product_v with deposits
		
		EXECUTE 'UPDATE '||v_schema_name||'.product_v AS p '
				||'SET	deposit_lmr_enabled = FALSE, '
				||'		deposit_lmr_charge_code = t0.id, '
				||'		deposit_lmr_deposit_value = 1,'
				||'		deposit_lmr_value_type = ''Percentage'', '
				||'		deposit_lmr_description = ''LMR Deposit'','
				||'		deposit_lmr_deposit_type = ''LastMonthDeposit'', '
				||'		deposit_move_in_enabled = FALSE, '
				||'		deposit_move_in_charge_code = t1.id, '
				||'		deposit_move_in_deposit_value = 1,'
				||'		deposit_move_in_value_type = ''Percentage'', '
				||'		deposit_move_in_description = ''Move In Deposit'','
				||'		deposit_move_in_deposit_type = ''MoveInDeposit'','
				||'		deposit_security_enabled = FALSE,'
				||'		deposit_security_charge_code = t2.id, '
				||'		deposit_security_deposit_value = 1,'
				||'		deposit_security_value_type = ''Percentage'','
				||'		deposit_security_description = ''Security Deposit'','
				||'		deposit_security_deposit_type = ''SecurityDeposit'' '
				||'FROM 	(SELECT id FROM '||v_schema_name||'.arcode WHERE name = ''LMR Deposit'') AS t0,'
				||'			(SELECT id FROM '||v_schema_name||'.arcode WHERE name = ''Move In Deposit'') AS t1,'
				||'			(SELECT id FROM '||v_schema_name||'.arcode WHERE name = ''Security Deposit'') AS t2 ';
        
                      
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
        
        ALTER TABLE apt_unit    DROP COLUMN marketing,
                                DROP COLUMN _available_for_rent;
        
        -- billing_account
        
        ALTER TABLE billing_account     DROP COLUMN billing_cycle_start_day;
                                        
        -- billing_bill
        
        ALTER TABLE billing_bill DROP COLUMN lease;
        
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
        
        
        -- deposit_policy_item
        
        DROP TABLE deposit_policy_item;
        
        
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
        
        ALTER TABLE product     DROP COLUMN code_type;
                                                       
        -- product_item
        
        ALTER TABLE product_item        DROP COLUMN code,
                                        DROP COLUMN is_default;
        
        
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
        
        -- yardi_lease_charge_data
        
        DROP TABLE yardi_lease_charge_data;
                 
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
        ALTER TABLE agreement_signatures ADD CONSTRAINT agreement_signatures_lease_term_participant_fk FOREIGN KEY(lease_term_participant) 
                REFERENCES lease_term_participant(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE apt_unit_effective_availability ADD CONSTRAINT apt_unit_effective_availability_unit_fk FOREIGN KEY(unit) 
                REFERENCES apt_unit(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE apt_unit_reservation ADD CONSTRAINT apt_unit_reservation_lease_fk FOREIGN KEY(lease) REFERENCES lease(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE apt_unit_reservation ADD CONSTRAINT apt_unit_reservation_unit_fk FOREIGN KEY(unit) REFERENCES apt_unit(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE building ADD CONSTRAINT building_landlord_fk FOREIGN KEY(landlord) REFERENCES landlord(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE community_event ADD CONSTRAINT community_event_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE crm_user_signature ADD CONSTRAINT crm_user_signature_signing_user_fk FOREIGN KEY(signing_user) REFERENCES crm_user(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_signature ADD CONSTRAINT customer_signature_signing_user_fk FOREIGN KEY(signing_user) REFERENCES customer_user(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE deposit ADD CONSTRAINT deposit_charge_code_fk FOREIGN KEY(charge_code) REFERENCES arcode(id)  DEFERRABLE INITIALLY DEFERRED;
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
        ALTER TABLE lease_term_agreement_document ADD CONSTRAINT lease_term_agreement_document_signed_employee_uploader_fk FOREIGN KEY(signed_employee_uploader) 
            REFERENCES crm_user(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term_agreement_document$signed_participants ADD CONSTRAINT lease_term_agreement_document$signed_participants_owner_fk FOREIGN KEY(owner) 
                REFERENCES lease_term_agreement_document(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_term_v ADD CONSTRAINT lease_term_v_employee_signature_fk FOREIGN KEY(employee_signature) REFERENCES crm_user_signature(id)  DEFERRABLE INITIALLY DEFERRED;
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
        ALTER TABLE legal_letter ADD CONSTRAINT legal_letter_status_fk FOREIGN KEY(status) REFERENCES legal_status(id)  DEFERRABLE INITIALLY DEFERRED;
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
        ALTER TABLE maintenance_request_status_record ADD CONSTRAINT maintenance_request_status_record_new_status_fk FOREIGN KEY(new_status) 
                REFERENCES maintenance_request_status(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE maintenance_request_status_record ADD CONSTRAINT maintenance_request_status_record_old_status_fk FOREIGN KEY(old_status) 
                REFERENCES maintenance_request_status(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE maintenance_request_status_record ADD CONSTRAINT maintenance_request_status_record_request_fk FOREIGN KEY(request) 
                REFERENCES maintenance_request(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE pmc_company_info_contact ADD CONSTRAINT pmc_company_info_contact_company_info_fk FOREIGN KEY(company_info) 
                REFERENCES pmc_company_info(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE product ADD CONSTRAINT product_code_fk FOREIGN KEY(code) REFERENCES arcode(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE product_v ADD CONSTRAINT product_v_deposit_lmr_charge_code_fk FOREIGN KEY(deposit_lmr_charge_code) 
            REFERENCES arcode(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE product_v ADD CONSTRAINT product_v_deposit_move_in_charge_code_fk FOREIGN KEY(deposit_move_in_charge_code) 
            REFERENCES arcode(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE product_v ADD CONSTRAINT product_v_deposit_security_charge_code_fk FOREIGN KEY(deposit_security_charge_code) 
            REFERENCES arcode(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE proof_of_asset_document_file ADD CONSTRAINT proof_of_asset_document_file_owner_fk FOREIGN KEY(owner) 
                REFERENCES proof_of_asset_document_folder(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE proof_of_asset_document_folder ADD CONSTRAINT proof_of_asset_document_folder_owner_fk FOREIGN KEY(owner) 
                REFERENCES customer_screening_personal_asset(id)  DEFERRABLE INITIALLY DEFERRED;
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
        ALTER TABLE agreement_signatures ADD CONSTRAINT agreement_signatures_lease_term_participant_discriminator_d_ck 
                CHECK ((lease_term_participant_discriminator) IN ('Guarantor', 'Tenant'));
        ALTER TABLE application_documentation_policy ADD CONSTRAINT application_documentation_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE apt_unit_occupancy_segment ADD CONSTRAINT apt_unit_occupancy_segment_status_e_ck 
            CHECK ((status) IN ('available', 'migrated', 'occupied', 'offMarket', 'pending', 'renovation'));
        ALTER TABLE arpolicy ADD CONSTRAINT arpolicy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE auto_pay_policy ADD CONSTRAINT auto_pay_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE background_check_policy ADD CONSTRAINT background_check_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE billable_item ADD CONSTRAINT billable_item_extra_data_discriminator_d_ck 
            CHECK ((extra_data_discriminator) IN ('Pet_ChargeItemExtraData', 'Vehicle_ChargeItemExtraData'));
        ALTER TABLE building_utility ADD CONSTRAINT building_utility_building_utility_type_e_ck 
                CHECK ((building_utility_type) IN ('airConditioning', 'cable', 'electricity', 'garbage', 'gas', 'heating', 'hydro', 'internet', 
                'other', 'sewage', 'telephone', 'television', 'water'));
        ALTER TABLE crm_user_signature ADD CONSTRAINT crm_user_signature_signature_format_e_ck 
                CHECK ((signature_format) IN ('AgreeBox', 'AgreeBoxAndFullName', 'FullName', 'Initials', 'None'));
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
        ALTER TABLE identification_document_type ADD CONSTRAINT identification_document_type_importance_e_ck 
            CHECK ((importance) IN ('Optional', 'Preferred', 'Required'));
        ALTER TABLE ilsemail_config ADD CONSTRAINT ilsemail_config_frequency_e_ck CHECK ((frequency) IN ('daily', 'monthly', 'weekly'));
        ALTER TABLE insurance_certificate_scan ADD CONSTRAINT insurance_certificate_scan_certificate_discriminator_d_ck 
                CHECK ((certificate_discriminator) IN ('InsuranceGeneral', 'InsuranceTenantSure'));
        ALTER TABLE lead ADD CONSTRAINT lead_lease_type_e_ck 
            CHECK ((lease_type) IN ('AccountCharge', 'AccountCredit', 'AddOn', 'CarryForwardCharge', 'CarryForwardCredit', 'Commercial', 
            'Deposit', 'DepositRefund', 'ExternalCharge', 'ExternalCredit', 'LatePayment', 'Locker', 'NSF', 'OneTime', 'Parking', 'Payment', 
            'Pet', 'Residential', 'ResidentialShortTerm', 'Utility'));
        ALTER TABLE lease ADD CONSTRAINT lease_lease_type_e_ck 
            CHECK ((lease_type) IN ('AccountCharge', 'AccountCredit', 'AddOn', 'CarryForwardCharge', 'CarryForwardCredit', 'Commercial', 
            'Deposit', 'DepositRefund', 'ExternalCharge', 'ExternalCredit', 'LatePayment', 'Locker', 'NSF', 'OneTime', 'Parking', 'Payment', 
            'Pet', 'Residential', 'ResidentialShortTerm', 'Utility'));
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
		ALTER TABLE lease_application_confirmation_term ADD CONSTRAINT lease_application_confirmation_term_apply_to_role_e_ck 
			CHECK ((apply_to_role) IN ('All', 'Applicant', 'Guarantor'));
        ALTER TABLE lease_application_legal_policy ADD CONSTRAINT lease_application_legal_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE lease_billing_policy ADD CONSTRAINT lease_billing_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE legal_terms_policy ADD CONSTRAINT legal_terms_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE legal_letter ADD CONSTRAINT legal_letter_id_discriminator_ck 
			CHECK ((id_discriminator) IN ('GenericLegalLetter', 'N4LegalLetter'));
		ALTER TABLE legal_letter ADD CONSTRAINT legal_letter_status_discriminator_d_ck CHECK (status_discriminator = 'LegalStatus');
        ALTER TABLE legal_status ADD CONSTRAINT legal_status_id_discriminator_ck CHECK (id_discriminator= 'LegalStatus');
        ALTER TABLE legal_status ADD CONSTRAINT legal_status_status_e_ck 
                CHECK ((status) IN ('HearingDate', 'L1', 'N4', 'None', 'Order', 'RequestToReviewOrder', 'SetAside', 'Sheriff', 'StayOrder'));
        ALTER TABLE maintenance_request ADD CONSTRAINT maintenance_request_phone_type_e_ck CHECK ((phone_type) IN ('home', 'mobile', 'work'));
        ALTER TABLE maintenance_request_category ADD CONSTRAINT maintenance_request_category_type_e_ck CHECK ((type) IN ('Amenities', 'ApartmentUnit', 'Exterior'));
        ALTER TABLE maintenance_request_policy ADD CONSTRAINT maintenance_request_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE maintenance_request_status_record ADD CONSTRAINT maintenance_request_status_record_updated_by_discriminator_d_ck 
                CHECK ((updated_by_discriminator) IN ('CrmUser', 'CustomerUser'));
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
			'N4Policy', 'OnlineAppPolicy', 'Parking', 'PaymentPostingBatch', 'PaymentRecord', 'PaymentTransactionsPolicy', 
			'PaymentTypeSelectionPolicy', 'PetPolicy', 'ProductTaxPolicy', 'ProspectPortalPolicy', 'RestrictionsPolicy', 'Tenant', 
			'TenantInsurancePolicy', 'Vendor', 'YardiInterfacePolicy', 'feature', 'service'));
        ALTER TABLE online_application ADD CONSTRAINT online_application_role_e_ck CHECK ((role) IN ('Applicant', 'CoApplicant', 'Dependent', 'Guarantor'));
        ALTER TABLE online_application_wizard_step_status ADD CONSTRAINT online_application_wizard_step_status_step_e_ck 
			CHECK ((step) IN ('AboutYou', 'AdditionalInfo', 'Confirmation', 'EmergencyContacts', 'Financial', 'Lease', 
			'Legal', 'Options', 'Payment', 'People', 'Summary', 'Unit'));
        ALTER TABLE payment_posting_batch ADD CONSTRAINT payment_posting_batch_created_by_discriminator_d_ck CHECK ((created_by_discriminator) IN ('CrmUser', 'CustomerUser'));
        ALTER TABLE payment_posting_batch ADD CONSTRAINT payment_posting_batch_status_e_ck CHECK ((status) IN ('Canceled', 'Created', 'Posted'));
        ALTER TABLE payment_transactions_policy ADD CONSTRAINT payment_transactions_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE payment_type_selection_policy ADD CONSTRAINT payment_type_selection_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE pet_policy ADD CONSTRAINT pet_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE pmc_company_info_contact ADD CONSTRAINT pmc_company_info_contact_tp_e_ck CHECK ((tp) IN ('administrator', 'privacyIssues'));
        ALTER TABLE product_item ADD CONSTRAINT product_item_element_discriminator_d_ck CHECK ((element_discriminator) IN ('AptUnit', 'LockerArea', 'Parking', 'Roof'));
        ALTER TABLE product_tax_policy ADD CONSTRAINT product_tax_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));
        ALTER TABLE product_v ADD CONSTRAINT product_v_deposit_lmr_deposit_type_e_ck 
            CHECK ((deposit_lmr_deposit_type) IN ('LastMonthDeposit', 'MoveInDeposit', 'SecurityDeposit'));
        ALTER TABLE product_v ADD CONSTRAINT product_v_deposit_lmr_value_type_e_ck CHECK ((deposit_lmr_value_type) IN ('Monetary', 'Percentage'));
        ALTER TABLE product_v ADD CONSTRAINT product_v_deposit_move_in_deposit_type_e_ck 
            CHECK ((deposit_move_in_deposit_type) IN ('LastMonthDeposit', 'MoveInDeposit', 'SecurityDeposit'));
        ALTER TABLE product_v ADD CONSTRAINT product_v_deposit_move_in_value_type_e_ck CHECK ((deposit_move_in_value_type) IN ('Monetary', 'Percentage'));
        ALTER TABLE product_v ADD CONSTRAINT product_v_deposit_security_deposit_type_e_ck 
            CHECK ((deposit_security_deposit_type) IN ('LastMonthDeposit', 'MoveInDeposit', 'SecurityDeposit'));
        ALTER TABLE product_v ADD CONSTRAINT product_v_deposit_security_value_type_e_ck CHECK ((deposit_security_value_type) IN ('Monetary', 'Percentage'));
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
        
        ALTER TABLE crm_role ALTER COLUMN name SET NOT NULL;
        ALTER TABLE lease ALTER COLUMN integration_system_id DROP NOT NULL;
        ALTER TABLE lease ALTER COLUMN lease_id DROP NOT NULL;
        ALTER TABLE maintenance_request_status_record ALTER COLUMN request SET NOT NULL;
		ALTER TABLE product ALTER COLUMN code SET NOT NULL;
        
       
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        CREATE INDEX agreement_signatures_lease_term_participant_discriminator_idx ON agreement_signatures USING btree (lease_term_participant_discriminator);
        CREATE INDEX agreement_signatures_lease_term_participant_idx ON agreement_signatures USING btree (lease_term_participant);
        CREATE INDEX agreement_signatures$legal_terms_signatures_owner_idx ON agreement_signatures$legal_terms_signatures USING btree (owner);
        CREATE INDEX apt_unit_effective_availability_available_for_rent_idx ON apt_unit_effective_availability USING btree (available_for_rent);
        CREATE INDEX ilsprofile_email_building_idx ON ilsprofile_email USING btree (building);
        CREATE INDEX lease_term_v$agreement_confirmation_term_owner_idx ON lease_term_v$agreement_confirmation_term USING btree (owner);
        CREATE INDEX lease_term_v$agreement_legal_terms_owner_idx ON lease_term_v$agreement_legal_terms USING btree (owner);
        CREATE INDEX lease_term_v$utilities_owner_idx ON lease_term_v$utilities USING btree (owner);
        CREATE INDEX online_application$confirmation_terms_owner_idx ON online_application$confirmation_terms USING btree (owner);
        CREATE INDEX online_application$legal_terms_owner_idx ON online_application$legal_terms USING btree (owner);
        CREATE INDEX ilssummary_building_building_idx ON ilssummary_building USING btree (building);
        CREATE INDEX ilssummary_floorplan_floorplan_idx ON ilssummary_floorplan USING btree (floorplan);
        CREATE UNIQUE INDEX lease_application_application_id_idx ON lease_application USING btree (LOWER(application_id));
        CREATE INDEX lease_term_agreement_document_lease_term_v_idx ON lease_term_agreement_document USING btree (lease_term_v);
        CREATE INDEX lease_term_agreement_document$signed_participants_owner_idx ON lease_term_agreement_document$signed_participants USING btree (owner);
        CREATE INDEX maintenance_request_status_record_request_idx ON maintenance_request_status_record USING btree (request);
        CREATE INDEX notes_and_attachments_owner_discriminator_idx ON notes_and_attachments USING btree (owner_discriminator);
        CREATE INDEX notes_and_attachments_owner_idx ON notes_and_attachments USING btree (owner);
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
