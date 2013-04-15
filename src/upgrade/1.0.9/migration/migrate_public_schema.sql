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
 DROP SEQUENCE lease_adjustment_reason_seq;
 DROP SEQUENCE product_item_type_seq;
 

-- New sequences
 CREATE SEQUENCE arcode_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE maintenance_request_category_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE maintenance_request_category$sub_categories_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE yardi_service_request_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;

  
 -- Change owner to vista
 ALTER SEQUENCE arcode_seq OWNER TO vista ;
 ALTER SEQUENCE maintenance_request_category_seq OWNER TO vista ;
 ALTER SEQUENCE maintenance_request_category$sub_categories_seq OWNER TO vista ;
 ALTER SEQUENCE yardi_service_request_seq OWNER TO vista ;

 
COMMIT;

SET client_min_messages = 'notice';


