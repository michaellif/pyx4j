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
        
        ALTER SEQUENCE file_blob_seq RENAME TO media_file_blob_seq;
        ALTER SEQUENCE pad_batch_seq RENAME TO funds_transfer_batch_seq;
        ALTER SEQUENCE pad_debit_record_seq RENAME TO funds_transfer_record_seq;
        ALTER SEQUENCE pad_debit_record_transaction_seq RENAME TO funds_transfer_record_transaction_seq;
        ALTER SEQUENCE pad_file_creation_number_seq RENAME TO funds_transfer_file_creation_number_seq;
        ALTER SEQUENCE pad_file_seq RENAME TO funds_transfer_file_seq;
        ALTER SEQUENCE pad_reconciliation_debit_record_seq RENAME TO funds_reconciliation_record_record_seq;
        ALTER SEQUENCE pad_reconciliation_file_seq RENAME TO funds_reconciliation_file_seq;
        ALTER SEQUENCE pad_reconciliation_summary_seq RENAME TO funds_reconciliation_summary_seq;
        
 
        -- Sequences to drop
        DROP SEQUENCE advertising_blurb_seq;
        DROP SEQUENCE application_document_blob_seq;
        DROP SEQUENCE application_document_file_seq;
        DROP SEQUENCE application_wizard_step_seq;
        DROP SEQUENCE application_wizard_substep_seq;
        DROP SEQUENCE charges_seq;
        DROP SEQUENCE custom_skin_resource_blob_seq;
        DROP SEQUENCE deposit_policy_item_seq;
        DROP SEQUENCE digital_signature_seq;
        DROP SEQUENCE general_insurance_policy_blob_seq;
        DROP SEQUENCE identification_document_seq;
        DROP SEQUENCE insurance_certificate_doc_seq;
        DROP SEQUENCE legal_documentation$co_application_seq;
        DROP SEQUENCE legal_documentation$guarantor_application_seq;
        DROP SEQUENCE legal_documentation$lease_seq;
        DROP SEQUENCE legal_documentation$main_application_seq;
        DROP SEQUENCE legal_documentation$payment_authorization_seq;
        DROP SEQUENCE legal_documentation_seq;
        DROP SEQUENCE legal_terms_content_seq;
        DROP SEQUENCE legal_terms_descriptor$content_seq;
        DROP SEQUENCE legal_terms_descriptor_seq;
        DROP SEQUENCE marketing$ad_blurbs_seq;
        DROP SEQUENCE online_application$signatures_seq;
        DROP SEQUENCE online_application$steps_seq;   
        DROP SEQUENCE payment_information_seq;
        DROP SEQUENCE proof_of_employment_document_seq;
        DROP SEQUENCE property_phone_seq;
        DROP SEQUENCE summary_seq;
        DROP SEQUENCE tenant_charge_list$charges_seq;
        DROP SEQUENCE tenant_charge_list_seq;
        DROP SEQUENCE tenant_charge_seq;
        DROP SEQUENCE yardi_lease_charge_data_seq;

        -- New sequences
        
        CREATE SEQUENCE agreement_signatures$legal_terms_signatures_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE agreement_signatures_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE apt_unit_effective_availability_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE apt_unit_reservation_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE community_event_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE crm_user_signature_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE customer_signature_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE identification_document_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE identification_document_file_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE identification_document_folder_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE ilsemail_config_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE ilsprofile_email_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE ilssummary_building_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE ilssummary_floorplan_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE insurance_certificate_scan_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE landlord_media_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE landlord_media_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE landlord_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE legal_status_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE lease_agreement_confirmation_term_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE lease_agreement_legal_policy_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE lease_agreement_legal_term_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE lease_application_confirmation_term_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE lease_application_legal_policy_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE lease_application_legal_term_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE lease_term_v$agreement_confirmation_term_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE lease_term_agreement_document_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE lease_term_agreement_document_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE lease_term_agreement_document$signed_participants_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE lease_term_v$agreement_legal_terms_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE lease_term_v$utilities_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE legal_terms_policy_item_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE legal_terms_policy_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE maintenance_request_picture_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE maintenance_request_picture_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE maintenance_request_policy_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE maintenance_request_status_record_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE note_attachment_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE online_application$legal_terms_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE online_application$confirmation_terms_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE online_application_wizard_step_status_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE outgoing_mail_queue_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE payment_posting_batch_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE permission_to_enter_note_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE pmc_company_info_contact_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE pmc_company_info_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE product_deposit_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE proof_of_asset_document_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE proof_of_asset_document_file_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE proof_of_asset_document_folder_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE proof_of_employment_document_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE proof_of_employment_document_file_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE proof_of_employment_document_folder_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE prospect_portal_policy_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE signed_agreement_confirmation_term_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE signed_agreement_legal_term_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE signed_online_application_confirmation_term_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE signed_online_application_legal_term_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;

        -- Change owner to vista
        
        ALTER SEQUENCE agreement_signatures$legal_terms_signatures_seq OWNER TO vista ;
        ALTER SEQUENCE agreement_signatures_seq OWNER TO vista ;
        ALTER SEQUENCE apt_unit_effective_availability_seq OWNER TO vista ;
        ALTER SEQUENCE apt_unit_reservation_seq OWNER TO vista ;
        ALTER SEQUENCE community_event_seq OWNER TO vista ;
        ALTER SEQUENCE crm_user_signature_seq OWNER TO vista ;
        ALTER SEQUENCE customer_signature_seq OWNER TO vista ;
        ALTER SEQUENCE funds_reconciliation_file_seq OWNER TO vista ;
        ALTER SEQUENCE funds_reconciliation_record_record_seq OWNER TO vista ;
        ALTER SEQUENCE funds_reconciliation_summary_seq OWNER TO vista ;
        ALTER SEQUENCE funds_transfer_batch_seq OWNER TO vista ;
        ALTER SEQUENCE funds_transfer_file_creation_number_seq OWNER TO vista ;
        ALTER SEQUENCE funds_transfer_file_seq OWNER TO vista ;
        ALTER SEQUENCE funds_transfer_record_seq OWNER TO vista ;
        ALTER SEQUENCE funds_transfer_record_transaction_seq OWNER TO vista ;
        ALTER SEQUENCE identification_document_blob_seq OWNER TO vista ;
        ALTER SEQUENCE identification_document_file_seq OWNER TO vista ;
        ALTER SEQUENCE identification_document_folder_seq OWNER TO vista ;
        ALTER SEQUENCE ilsemail_config_seq OWNER TO vista ;
        ALTER SEQUENCE ilsprofile_email_seq OWNER TO vista ;
        ALTER SEQUENCE ilssummary_building_seq OWNER TO vista ;
        ALTER SEQUENCE ilssummary_floorplan_seq OWNER TO vista ;
        ALTER SEQUENCE insurance_certificate_scan_blob_seq OWNER TO vista ;
        ALTER SEQUENCE landlord_media_blob_seq OWNER TO vista ;
        ALTER SEQUENCE landlord_media_seq OWNER TO vista ;
        ALTER SEQUENCE landlord_seq OWNER TO vista ;
        ALTER SEQUENCE legal_status_seq OWNER TO vista ;
        ALTER SEQUENCE lease_agreement_confirmation_term_seq OWNER TO vista ;
        ALTER SEQUENCE lease_agreement_legal_policy_seq OWNER TO vista ;
        ALTER SEQUENCE lease_agreement_legal_term_seq OWNER TO vista ;
        ALTER SEQUENCE lease_application_confirmation_term_seq OWNER TO vista ;
        ALTER SEQUENCE lease_application_legal_policy_seq OWNER TO vista ;
        ALTER SEQUENCE lease_application_legal_term_seq OWNER TO vista ;
        ALTER SEQUENCE lease_term_v$agreement_confirmation_term_seq OWNER TO vista ;
        ALTER SEQUENCE lease_term_agreement_document_blob_seq OWNER TO vista ;
        ALTER SEQUENCE lease_term_agreement_document_seq OWNER TO vista ;
        ALTER SEQUENCE lease_term_agreement_document$signed_participants_seq OWNER TO vista ;
        ALTER SEQUENCE lease_term_v$agreement_legal_terms_seq OWNER TO vista ;
        ALTER SEQUENCE lease_term_v$utilities_seq OWNER TO vista ;
        ALTER SEQUENCE legal_terms_policy_item_seq OWNER TO vista ;
        ALTER SEQUENCE legal_terms_policy_seq OWNER TO vista ;
        ALTER SEQUENCE maintenance_request_picture_blob_seq OWNER TO vista ;
        ALTER SEQUENCE maintenance_request_picture_seq OWNER TO vista ;
        ALTER SEQUENCE maintenance_request_policy_seq OWNER TO vista ;
        ALTER SEQUENCE maintenance_request_status_record_seq OWNER TO vista ;
        ALTER SEQUENCE media_file_blob_seq OWNER TO vista ;
        ALTER SEQUENCE note_attachment_seq OWNER TO vista ;
        ALTER SEQUENCE online_application$confirmation_terms_seq OWNER TO vista ;
        ALTER SEQUENCE online_application$legal_terms_seq OWNER TO vista ;
        ALTER SEQUENCE online_application_wizard_step_status_seq OWNER TO vista ;
        ALTER SEQUENCE outgoing_mail_queue_seq OWNER TO vista ;
        ALTER SEQUENCE payment_posting_batch_seq OWNER TO vista ;
        ALTER SEQUENCE permission_to_enter_note_seq OWNER TO vista ;
        ALTER SEQUENCE pmc_company_info_contact_seq OWNER TO vista ;
        ALTER SEQUENCE pmc_company_info_seq OWNER TO vista ;
        ALTER SEQUENCE product_deposit_seq OWNER TO vista ;
        ALTER SEQUENCE proof_of_asset_document_blob_seq OWNER TO vista ;
        ALTER SEQUENCE proof_of_asset_document_file_seq OWNER TO vista ;
        ALTER SEQUENCE proof_of_asset_document_folder_seq OWNER TO vista ;
        ALTER SEQUENCE proof_of_employment_document_blob_seq OWNER TO vista ;
        ALTER SEQUENCE proof_of_employment_document_file_seq OWNER TO vista ;
        ALTER SEQUENCE proof_of_employment_document_folder_seq OWNER TO vista ;
        ALTER SEQUENCE prospect_portal_policy_seq OWNER TO vista ;
        ALTER SEQUENCE signed_agreement_confirmation_term_seq OWNER TO vista ;
        ALTER SEQUENCE signed_agreement_legal_term_seq OWNER TO vista ;
        ALTER SEQUENCE signed_online_application_confirmation_term_seq OWNER TO vista ;
        ALTER SEQUENCE signed_online_application_legal_term_seq OWNER TO vista ;

       
COMMIT;

SET client_min_messages = 'notice';


