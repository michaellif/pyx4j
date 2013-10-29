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

 
        -- Renamed sequences
 
        ALTER SEQUENCE application_document_file_seq RENAME TO insurance_certificate_scan_seq;
        ALTER SEQUENCE application_document_blob_seq RENAME TO general_insurance_policy_blob_seq;
        ALTER SEQUENCE file_seq RENAME TO note_attachment_seq;
        ALTER SEQUENCE insurance_certificate_seq RENAME TO insurance_policy_seq;
        ALTER SEQUENCE insurance_tenant_sure_client_seq RENAME TO tenant_sure_insurance_policy_client_seq;
        ALTER SEQUENCE insurance_tenant_sure_report_seq RENAME TO tenant_sure_insurance_policy_report_seq;
        ALTER SEQUENCE insurance_tenant_sure_transaction_seq RENAME TO tenant_sure_transaction_seq;
        ALTER SEQUENCE media_seq RENAME TO media_file_seq;
        ALTER SEQUENCE portal_image_set_seq  RENAME TO site_image_set_seq ;
        ALTER SEQUENCE portal_image_set$image_set_seq RENAME TO site_image_set$image_set_seq;
        ALTER SEQUENCE portal_logo_image_resource_seq RENAME TO site_logo_image_resource_seq; 
        ALTER SEQUENCE preauthorized_payment_seq RENAME TO autopay_agreement_seq;
        ALTER SEQUENCE preauthorized_payment_covered_item_seq RENAME TO autopay_agreement_covered_item_seq;
        
 
        -- Sequences to drop
         -- DROP SEQUENCE file_seq;
        DROP SEQUENCE ilspolicy_item$buildings_seq;
        DROP SEQUENCE ilspolicy_item$cities_seq;
        DROP SEQUENCE ilspolicy_item$provinces_seq;
        DROP SEQUENCE ilspolicy_item_seq;
        DROP SEQUENCE ilspolicy_seq;
        DROP SEQUENCE name_seq;
        DROP SEQUENCE pricing_seq;
        DROP SEQUENCE resident_portal_settings$custom_html_seq;
        DROP SEQUENCE resident_portal_settings$proxy_whitelist_seq;
        DROP SEQUENCE resident_portal_settings_seq;

        
  
        -- New sequences
        CREATE SEQUENCE application_document_file_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE application_document_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE company_logo_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE employee_signature_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE employee_signature_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        -- CREATE SEQUENCE general_insurance_policy_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE ilsbatch$units_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE ilsbatch_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE ilsconfig_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE ilsopen_house_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE ilsprofile_building_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE ilsprofile_floorplan_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE ilsvendor_config_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE insurance_certificate_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        -- CREATE SEQUENCE insurance_certificate_scan_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE legal_letter_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE legal_letter_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE n4_policy_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE n4_policy$relevant_ar_codes_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE payment_record$_assert_autopay_covered_items_changes_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE portal_banner_image_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE site_descriptor$portal_banner_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;

        
  
        -- Change owner to vista
        
        ALTER SEQUENCE application_document_file_seq OWNER TO vista;
        ALTER SEQUENCE application_document_blob_seq OWNER TO vista;
        ALTER SEQUENCE autopay_agreement_seq OWNER TO vista;
        ALTER SEQUENCE autopay_agreement_covered_item_seq OWNER TO vista;
        ALTER SEQUENCE company_logo_seq OWNER TO vista ;
        ALTER SEQUENCE employee_signature_blob_seq OWNER TO vista ;
        ALTER SEQUENCE employee_signature_seq OWNER TO vista ;
        ALTER SEQUENCE general_insurance_policy_blob_seq OWNER TO vista ;
        ALTER SEQUENCE ilsbatch$units_seq OWNER TO vista ;
        ALTER SEQUENCE ilsbatch_seq OWNER TO vista ;
        ALTER SEQUENCE ilsconfig_seq OWNER TO vista ;
        ALTER SEQUENCE ilsopen_house_seq OWNER TO vista ;
        ALTER SEQUENCE ilsprofile_building_seq OWNER TO vista ;
        ALTER SEQUENCE ilsprofile_floorplan_seq OWNER TO vista ;
        ALTER SEQUENCE ilsvendor_config_seq OWNER TO vista ;
        ALTER SEQUENCE insurance_policy_seq OWNER TO vista ;
        ALTER SEQUENCE insurance_certificate_seq OWNER TO vista;
        ALTER SEQUENCE insurance_certificate_scan_seq OWNER TO vista;
        ALTER SEQUENCE legal_letter_blob_seq OWNER TO vista ;
        ALTER SEQUENCE legal_letter_seq OWNER TO vista ;
        ALTER SEQUENCE media_file_seq OWNER TO vista ;
        ALTER SEQUENCE note_attachment_seq OWNER TO vista ;
        ALTER SEQUENCE n4_policy_seq OWNER TO vista ;
        ALTER SEQUENCE n4_policy$relevant_ar_codes_seq OWNER TO vista ;
        ALTER SEQUENCE payment_record$_assert_autopay_covered_items_changes_seq OWNER TO vista ;
        ALTER SEQUENCE portal_banner_image_seq OWNER TO vista ;
        ALTER SEQUENCE site_descriptor$portal_banner_seq OWNER TO vista ;
        ALTER SEQUENCE site_image_set$image_set_seq OWNER TO vista ;
        ALTER SEQUENCE site_image_set_seq OWNER TO vista ;
        ALTER SEQUENCE site_logo_image_resource_seq OWNER TO vista ;
        ALTER SEQUENCE tenant_sure_insurance_policy_client_seq OWNER TO vista ;
        ALTER SEQUENCE tenant_sure_insurance_policy_report_seq OWNER TO vista ;
        ALTER SEQUENCE tenant_sure_transaction_seq OWNER TO vista ;

         
COMMIT;

SET client_min_messages = 'notice';


