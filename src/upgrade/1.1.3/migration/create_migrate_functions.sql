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
        
        -- employee_signature
        
        ALTER TABLE employee_signature RENAME COLUMN blob_key TO file_blob_key;
        ALTER TABLE employee_signature RENAME COLUMN cache_version TO file_cache_version;
        ALTER TABLE employee_signature RENAME COLUMN content_mime_type TO file_content_mime_type;
        ALTER TABLE employee_signature RENAME COLUMN file_name TO file_file_name;
        ALTER TABLE employee_signature RENAME COLUMN file_size TO file_file_size;
        ALTER TABLE employee_signature RENAME COLUMN updated_timestamp TO file_updated_timestamp;
        
        
        -- file_blob
        
        ALTER TABLE file_blob RENAME TO media_file_blob;
        ALTER TABLE media_file_blob RENAME COLUMN content TO data;
        
        
        -- floorplan
        
        ALTER TABLE floorplan ADD COLUMN code VARCHAR(500);
        ALTER TABLE floorplan ALTER COLUMN description TYPE VARCHAR(4000);
        
        
        -- general_insurance_policy_blob
        
        ALTER TABLE general_insurance_policy_blob RENAME TO insurance_certificate_scan_blob;
        
        -- legal_letter
        
        ALTER TABLE legal_letter RENAME COLUMN blob_key TO file_blob_key;
        ALTER TABLE legal_letter RENAME COLUMN cache_version TO file_cache_version;
        ALTER TABLE legal_letter RENAME COLUMN content_mime_type TO file_content_mime_type;
        ALTER TABLE legal_letter RENAME COLUMN file_name TO file_file_name;
        ALTER TABLE legal_letter RENAME COLUMN file_size TO file_file_size;
        ALTER TABLE legal_letter RENAME COLUMN updated_timestamp TO file_updated_timestamp;
        
        -- legal_letter_blob
        
        ALTER TABLE legal_letter_blob RENAME COLUMN content TO data;
        ALTER TABLE legal_letter_blob ADD COLUMN created TIMESTAMP;
        
        
        -- insurance_certificate_scan
        
        ALTER TABLE insurance_certificate_scan RENAME COLUMN blob_key TO file_blob_key;
        ALTER TABLE insurance_certificate_scan RENAME COLUMN cache_version TO file_cache_version;
        ALTER TABLE insurance_certificate_scan RENAME COLUMN content_mime_type TO file_content_mime_type;
        ALTER TABLE insurance_certificate_scan RENAME COLUMN file_name TO file_file_name;
        ALTER TABLE insurance_certificate_scan RENAME COLUMN file_size TO file_file_size;
        ALTER TABLE insurance_certificate_scan RENAME COLUMN updated_timestamp TO file_updated_timestamp;
        
        ALTER TABLE insurance_certificate_scan  ADD COLUMN certificate BIGINT,
                                                ADD COLUMN certificate_descriminator VARCHAR(50);
        
        
        -- marketing
        
        ALTER TABLE marketing ALTER COLUMN description TYPE VARCHAR(4000);
        
        
        -- media_file
        
        ALTER TABLE media_file RENAME COLUMN blob_key TO file_blob_key;
        ALTER TABLE media_file RENAME COLUMN cache_version TO file_cache_version;
        ALTER TABLE media_file RENAME COLUMN content_mime_type TO file_content_mime_type;
        ALTER TABLE media_file RENAME COLUMN file_name TO file_file_name;
        ALTER TABLE media_file RENAME COLUMN file_size TO file_file_size;
        ALTER TABLE media_file RENAME COLUMN updated_timestamp TO file_updated_timestamp;
        
        
        
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
                ||'WHERE a.certificate_doc = d.id ';
        
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        -- application_document_blob
        
        DROP TABLE application_document_blob;
        
        -- application_document_file
        
        DROP TABLE application_document_file;
        
        -- application_wizard_step
        
        DROP TABLE application_wizard_step ;
        
        -- application_wizard_substep
        
        DROP TABLE application_wizard_substep;
        
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
        
        
        -- insurance_certificate_doc
        
        DROP TABLE insurance_certificate_doc;
        
        
        -- insurance_certificate_scan
        
        ALTER TABLE insurance_certificate_scan DROP COLUMN certificate_doc;
        
        
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
        
        
        -- online_application$signatures
        
        DROP TABLE online_application$signatures;
        
        
        -- online_application$steps
        
        DROP TABLE online_application$steps;
        
        
        -- payment_information
        
        DROP TABLE payment_information;
        
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
        SET     schema_version = '1.1.3',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;

        
