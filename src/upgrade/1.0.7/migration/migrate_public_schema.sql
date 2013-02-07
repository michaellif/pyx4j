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


 -- Sequences to drop
 DROP SEQUENCE insurance_tenant_sure_details$taxes_seq;
  
 -- New sequences
 CREATE SEQUENCE dev_card_service_simulation_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE dev_card_service_simulation_token_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE dev_card_service_simulation_transaction_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE dev_equifax_simulator_config_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE insurance_tenant_sure_report_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE resident_portal_settings$custom_html_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE resident_portal_settings_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE tenant_sure_hqupdate_file_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE tenant_sure_hqupdate_record_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE tenant_sure_subscribers_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;

  
  -- Change owner to vista
 ALTER SEQUENCE dev_card_service_simulation_seq OWNER TO vista ;
 ALTER SEQUENCE dev_card_service_simulation_token_seq OWNER TO vista ;
 ALTER SEQUENCE dev_card_service_simulation_transaction_seq OWNER TO vista ;
 ALTER SEQUENCE dev_equifax_simulator_config_seq OWNER TO vista ;
 ALTER SEQUENCE insurance_tenant_sure_report_seq OWNER TO vista ;
 ALTER SEQUENCE resident_portal_settings$custom_html_seq OWNER TO vista ;
 ALTER SEQUENCE resident_portal_settings_seq OWNER TO vista ;
 ALTER SEQUENCE tenant_sure_hqupdate_file_seq OWNER TO vista ;
 ALTER SEQUENCE tenant_sure_hqupdate_record_seq OWNER TO vista ;
 ALTER SEQUENCE tenant_sure_subscribers_seq OWNER TO vista ;

 
COMMIT;

SET client_min_messages = 'notice';


