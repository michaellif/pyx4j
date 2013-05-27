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
 DROP SEQUENCE billing_arrears_snapshot$aging_buckets_seq;      
 DROP SEQUENCE padcredit_policy_item_seq;
 DROP SEQUENCE paddebit_policy_item_seq;
 DROP SEQUENCE padpolicy_seq;


-- New sequences
 
 CREATE SEQUENCE ilspolicy_item$buildings_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE ilspolicy_item$cities_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE ilspolicy_item$provinces_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE ilspolicy_item_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE ilspolicy_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE preauthorized_payment_covered_item_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE yardi_lease_charge_data_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;

  
 -- Change owner to vista
 
 ALTER SEQUENCE ilspolicy_item$buildings_seq OWNER TO vista ;
 ALTER SEQUENCE ilspolicy_item$cities_seq OWNER TO vista ;
 ALTER SEQUENCE ilspolicy_item$provinces_seq OWNER TO vista ;
 ALTER SEQUENCE ilspolicy_item_seq OWNER TO vista ;
 ALTER SEQUENCE ilspolicy_seq OWNER TO vista ;
 ALTER SEQUENCE preauthorized_payment_covered_item_seq OWNER TO vista ;
 ALTER SEQUENCE yardi_lease_charge_data_seq OWNER TO vista ;

 
COMMIT;

SET client_min_messages = 'notice';


