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
 DROP SEQUENCE insurance_tenant_sure_details_seq;
 DROP SEQUENCE insurance_tenant_sure_tax_seq;


 -- New sequences
 CREATE SEQUENCE lease_billing_policy$available_billing_types_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE lease_billing_type_policy_item_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
  
 -- Change owner to vista
 ALTER SEQUENCE lease_billing_policy$available_billing_types_seq OWNER TO vista ;
 ALTER SEQUENCE lease_billing_type_policy_item_seq OWNER TO vista ;

 
COMMIT;

SET client_min_messages = 'notice';


