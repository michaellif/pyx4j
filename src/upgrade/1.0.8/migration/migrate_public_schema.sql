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
 DROP SEQUENCE IF EXISTS insurance_tenant_sure_details_seq;
 DROP SEQUENCE IF EXISTS insurance_tenant_sure_tax_seq;


 -- New sequences
 CREATE SEQUENCE lease_billing_type_policy_item_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE padpolicy_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE pad_sim_file$state_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE padpolicy_item_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE preauthorized_payment_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE product_item_type$yardi_charge_codes_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE yardi_charge_code_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;


  
 -- Change owner to vista
 ALTER SEQUENCE lease_billing_type_policy_item_seq OWNER TO vista ;
 ALTER SEQUENCE padpolicy_seq OWNER TO vista ;
 ALTER SEQUENCE pad_sim_file$state_seq OWNER TO vista ;
 ALTER SEQUENCE padpolicy_item_seq OWNER TO vista ;
 ALTER SEQUENCE preauthorized_payment_seq OWNER TO vista ;
 ALTER SEQUENCE product_item_type$yardi_charge_codes_seq OWNER TO vista ;
 ALTER SEQUENCE yardi_charge_code_seq OWNER TO vista ;

 
COMMIT;

SET client_min_messages = 'notice';


