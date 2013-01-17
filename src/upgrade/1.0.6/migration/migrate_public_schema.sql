/**
***     ===========================================================================================================
***     
***     @version $Revision$ ($Author$) $Date$
***
***     Migration script for public schema - generated with by _dba_.generate_sql_sequences function
***
***     ===========================================================================================================
**/                                                     


 SET search_path = 'public';
  
 -- Sequences to drop
 DROP SEQUENCE document_seq;
 DROP SEQUENCE existing_insurance_seq;
  
 -- New sequences
 CREATE SEQUENCE admin_pmc$credit_check_transaction_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE admin_pmc_payment_method_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE admin_pmc_yardi_credential_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE business_id_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE business_information_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE caledon_co_signer_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE city_intro_page$content_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE city_intro_page_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE customer_credit_check_report_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE customer_credit_check_transaction_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE fee_default_equifax_fee_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE fee_default_payment_fees_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE fee_pmc_equifax_fee_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE insurance_certificate_document_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE insurance_tenant_sure_client_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE insurance_tenant_sure_details$taxes_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE insurance_tenant_sure_details_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE insurance_tenant_sure_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE insurance_tenant_sure_tax_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE insurance_tenant_sure_transaction_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE note_attachment_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE page_meta_tags_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE payment_record_external_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE personal_information_id_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE personal_information_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE pmc_signature_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE portal_image_set$image_set_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE portal_image_set_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE property_account_info_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE site_descriptor$city_intro_pages_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE site_descriptor$meta_tags_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE tenant_insurance_policy_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE tenant_sure_merchant_account_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE vista_merchant_account_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
  
 -- Change owner to vista
 ALTER SEQUENCE admin_pmc$credit_check_transaction_seq OWNER TO vista ;
 ALTER SEQUENCE admin_pmc_payment_method_seq OWNER TO vista ;
 ALTER SEQUENCE admin_pmc_yardi_credential_seq OWNER TO vista ;
 ALTER SEQUENCE business_id_blob_seq OWNER TO vista ;
 ALTER SEQUENCE business_information_seq OWNER TO vista ;
 ALTER SEQUENCE caledon_co_signer_seq OWNER TO vista ;
 ALTER SEQUENCE city_intro_page$content_seq OWNER TO vista ;
 ALTER SEQUENCE city_intro_page_seq OWNER TO vista ;
 ALTER SEQUENCE customer_credit_check_report_seq OWNER TO vista ;
 ALTER SEQUENCE customer_credit_check_transaction_seq OWNER TO vista ;
 ALTER SEQUENCE fee_default_equifax_fee_seq OWNER TO vista ;
 ALTER SEQUENCE fee_default_payment_fees_seq OWNER TO vista ;
 ALTER SEQUENCE fee_pmc_equifax_fee_seq OWNER TO vista ;
 ALTER SEQUENCE insurance_certificate_document_seq OWNER TO vista ;
 ALTER SEQUENCE insurance_tenant_sure_client_seq OWNER TO vista ;
 ALTER SEQUENCE insurance_tenant_sure_details$taxes_seq OWNER TO vista ;
 ALTER SEQUENCE insurance_tenant_sure_details_seq OWNER TO vista ;
 ALTER SEQUENCE insurance_tenant_sure_seq OWNER TO vista ;
 ALTER SEQUENCE insurance_tenant_sure_tax_seq OWNER TO vista ;
 ALTER SEQUENCE insurance_tenant_sure_transaction_seq OWNER TO vista ;
 ALTER SEQUENCE note_attachment_blob_seq OWNER TO vista ;
 ALTER SEQUENCE page_meta_tags_seq OWNER TO vista ;
 ALTER SEQUENCE payment_record_external_seq OWNER TO vista ;
 ALTER SEQUENCE personal_information_id_blob_seq OWNER TO vista ;
 ALTER SEQUENCE personal_information_seq OWNER TO vista ;
 ALTER SEQUENCE pmc_signature_seq OWNER TO vista ;
 ALTER SEQUENCE portal_image_set$image_set_seq OWNER TO vista ;
 ALTER SEQUENCE portal_image_set_seq OWNER TO vista ;
 ALTER SEQUENCE property_account_info_seq OWNER TO vista ;
 ALTER SEQUENCE site_descriptor$city_intro_pages_seq OWNER TO vista ;
 ALTER SEQUENCE site_descriptor$meta_tags_seq OWNER TO vista ;
 ALTER SEQUENCE tenant_insurance_policy_seq OWNER TO vista ;
 ALTER SEQUENCE tenant_sure_merchant_account_seq OWNER TO vista ;
 ALTER SEQUENCE vista_merchant_account_seq OWNER TO vista ;


