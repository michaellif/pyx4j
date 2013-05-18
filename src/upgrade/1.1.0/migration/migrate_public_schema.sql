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

-- New sequences
 CREATE SEQUENCE covered_item_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE ilspolicy_item$buildings_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE ilspolicy_item$cities_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE ilspolicy_item$provinces_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE ilspolicy_item_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE ilspolicy_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE preauthorized_payment$covered_items_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
  
 -- Change owner to vista
 ALTER SEQUENCE covered_item_seq OWNER TO vista ;
 ALTER SEQUENCE ilspolicy_item$buildings_seq OWNER TO vista ;
 ALTER SEQUENCE ilspolicy_item$cities_seq OWNER TO vista ;
 ALTER SEQUENCE ilspolicy_item$provinces_seq OWNER TO vista ;
 ALTER SEQUENCE ilspolicy_item_seq OWNER TO vista ;
 ALTER SEQUENCE ilspolicy_seq OWNER TO vista ;
 ALTER SEQUENCE preauthorized_payment$covered_items_seq OWNER TO vista ;

 
COMMIT;

SET client_min_messages = 'notice';


