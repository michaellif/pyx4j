/**
***     ===========================================================================================================
***     
***     @version $Revision: $ ($Author: $) $Date: $
***
***     Migration script for public schema - generated with by _dba_.generate_sql_sequences function
***
***     ===========================================================================================================
**/                                                     

SET client_min_messages = 'error';
SET search_path = 'public';

BEGIN TRANSACTION;

-- Sequences to drop

-- New sequences
 CREATE SEQUENCE auto_pay_change_policy_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE notification$buildings_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE notification$portfolios_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE notification_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
  
-- Change owner to vista
 ALTER SEQUENCE auto_pay_change_policy_seq OWNER TO vista ;
 ALTER SEQUENCE notification$buildings_seq OWNER TO vista ;
 ALTER SEQUENCE notification$portfolios_seq OWNER TO vista ;
 ALTER SEQUENCE notification_seq OWNER TO vista ;

 
COMMIT;

SET client_min_messages = 'notice';


