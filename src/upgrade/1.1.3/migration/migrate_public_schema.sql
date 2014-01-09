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
        DROP SEQUENCE application_document_blob_seq;
        DROP SEQUENCE application_document_file_seq;
        DROP SEQUENCE application_wizard_step_seq;
        DROP SEQUENCE application_wizard_substep_seq;
        DROP SEQUENCE charges_seq;
        DROP SEQUENCE custom_skin_resource_blob_seq;
        DROP SEQUENCE digital_signature_seq;
        DROP SEQUENCE general_insurance_policy_blob_seq;
        DROP SEQUENCE identification_document_seq;
        DROP SEQUENCE insurance_certificate_doc_seq;
        DROP SEQUENCE online_application$signatures_seq;
        DROP SEQUENCE online_application$steps_seq;   
        DROP SEQUENCE payment_information_seq;
        DROP SEQUENCE proof_of_employment_document_seq;
        DROP SEQUENCE property_phone_seq;
        DROP SEQUENCE summary_seq;
        DROP SEQUENCE tenant_charge_list$charges_seq;
        DROP SEQUENCE tenant_charge_list_seq;
        DROP SEQUENCE tenant_charge_seq;

        -- New sequences
        CREATE SEQUENCE agreement_legal_policy_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE agreement_legal_term_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE agreement_legal_term_signature_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE agreement_signatures$legal_terms_signatures_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE agreement_signatures_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE building$posting_batches_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE customer_signature_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE floorplan$ils_summary_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE identification_document_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE identification_document_file_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE identification_document_folder_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE ilsemail_config_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE ilssummary_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE insurance_certificate_scan_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE lease_term_agreement_document_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE lease_term_agreement_document_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE lease_term_v$agreement_legal_terms_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE lease_term_v$utilities_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE maintenance_request_picture_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE maintenance_request_picture_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE note_attachment_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE online_application$legal_terms_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE online_application_legal_policy_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE online_application_legal_term_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE payment_posting_batch$payments_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE payment_posting_batch_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE pmc_company_info_contact_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE pmc_company_info_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE proof_of_employment_document_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE proof_of_employment_document_file_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE proof_of_employment_document_folder_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE signed_online_application_legal_term_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;

        -- Change owner to vista
        ALTER SEQUENCE agreement_legal_policy_seq OWNER TO vista ;
        ALTER SEQUENCE agreement_legal_term_seq OWNER TO vista ;
        ALTER SEQUENCE agreement_legal_term_signature_seq OWNER TO vista ;
        ALTER SEQUENCE agreement_signatures$legal_terms_signatures_seq OWNER TO vista ;
        ALTER SEQUENCE agreement_signatures_seq OWNER TO vista ;
        ALTER SEQUENCE building$posting_batches_seq OWNER TO vista ;
        ALTER SEQUENCE customer_signature_seq OWNER TO vista ;
        ALTER SEQUENCE floorplan$ils_summary_seq OWNER TO vista ;
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
        ALTER SEQUENCE ilssummary_seq OWNER TO vista ;
        ALTER SEQUENCE insurance_certificate_scan_blob_seq OWNER TO vista ;
        ALTER SEQUENCE lease_term_agreement_document_blob_seq OWNER TO vista ;
        ALTER SEQUENCE lease_term_agreement_document_seq OWNER TO vista ;
        ALTER SEQUENCE lease_term_v$agreement_legal_terms_seq OWNER TO vista ;
        ALTER SEQUENCE lease_term_v$utilities_seq OWNER TO vista ;
        ALTER SEQUENCE maintenance_request_picture_blob_seq OWNER TO vista ;
        ALTER SEQUENCE maintenance_request_picture_seq OWNER TO vista ;
        ALTER SEQUENCE media_file_blob_seq OWNER TO vista ;
        ALTER SEQUENCE note_attachment_seq OWNER TO vista ;
        ALTER SEQUENCE online_application$legal_terms_seq OWNER TO vista ;
        ALTER SEQUENCE online_application_legal_policy_seq OWNER TO vista ;
        ALTER SEQUENCE online_application_legal_term_seq OWNER TO vista ;
        ALTER SEQUENCE payment_posting_batch$payments_seq OWNER TO vista ;
        ALTER SEQUENCE payment_posting_batch_seq OWNER TO vista ;
        ALTER SEQUENCE pmc_company_info_contact_seq OWNER TO vista ;
        ALTER SEQUENCE pmc_company_info_seq OWNER TO vista ;
        ALTER SEQUENCE proof_of_employment_document_blob_seq OWNER TO vista ;
        ALTER SEQUENCE proof_of_employment_document_file_seq OWNER TO vista ;
        ALTER SEQUENCE proof_of_employment_document_folder_seq OWNER TO vista ;
        ALTER SEQUENCE signed_online_application_legal_term_seq OWNER TO vista ;

       
COMMIT;

SET client_min_messages = 'notice';


