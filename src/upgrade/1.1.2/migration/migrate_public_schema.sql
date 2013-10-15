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
 
        ALTER SEQUENCE insurance_certificate_seq RENAME TO insurance_policy_seq;
 
        ALTER SEQUENCE insurance_tenant_sure_client_seq RENAME TO tenant_sure_insurance_policy_client_seq;
        ALTER SEQUENCE insurance_tenant_sure_report_seq RENAME TO tenant_sure_insurance_policy_report_seq;
        ALTER SEQUENCE insurance_tenant_sure_transaction_seq RENAME TO tenant_sure_transaction_seq;
        ALTER SEQUENCE preauthorized_payment_seq RENAME TO autopay_agreement_seq;
        
 
        -- Sequences to drop
        DROP SEQUENCE ilspolicy_item$buildings_seq;
        DROP SEQUENCE ilspolicy_item$cities_seq;
        DROP SEQUENCE ilspolicy_item$provinces_seq;
        DROP SEQUENCE ilspolicy_item_seq;
        DROP SEQUENCE ilspolicy_seq;
        
  
        -- New sequences
        CREATE SEQUENCE ilsbatch$units_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE ilsbatch_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE ilsconfig_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE ilsopen_house_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE ilsprofile_building_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE ilsprofile_floorplan_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE ilsvendor_config_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE initialization_data_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE insurance_certificate_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE insurance_certificate_scan_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE legal_letter_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE legal_letter_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        CREATE SEQUENCE n4_policy_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
        
  
        -- Change owner to vista
        ALTER SEQUENCE autopay_agreement_seq OWNER TO vista;
        ALTER SEQUENCE ilsbatch$units_seq OWNER TO vista ;
        ALTER SEQUENCE ilsbatch_seq OWNER TO vista ;
        ALTER SEQUENCE ilsconfig_seq OWNER TO vista ;
        ALTER SEQUENCE ilsopen_house_seq OWNER TO vista ;
        ALTER SEQUENCE ilsprofile_building_seq OWNER TO vista ;
        ALTER SEQUENCE ilsprofile_floorplan_seq OWNER TO vista ;
        ALTER SEQUENCE ilsvendor_config_seq OWNER TO vista ;
        ALTER SEQUENCE initialization_data_seq OWNER TO vista ;
        ALTER SEQUENCE insurance_policy_seq OWNER TO vista ;
        ALTER SEQUENCE insurance_certificate_seq OWNER TO vista;
        ALTER SEQUENCE insurance_certificate_scan_seq OWNER TO vista;
        ALTER SEQUENCE legal_letter_blob_seq OWNER TO vista ;
        ALTER SEQUENCE legal_letter_seq OWNER TO vista ;
        ALTER SEQUENCE n4_policy_seq OWNER TO vista ;
        ALTER SEQUENCE tenant_sure_insurance_policy_client_seq OWNER TO vista ;
        ALTER SEQUENCE tenant_sure_insurance_policy_report_seq OWNER TO vista ;
        ALTER SEQUENCE tenant_sure_transaction_seq OWNER TO vista ;

         
COMMIT;

SET client_min_messages = 'notice';


