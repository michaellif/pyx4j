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
CREATE SEQUENCE cards_clearance_file_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
CREATE SEQUENCE cards_clearance_record_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
  
-- Change owner to vista
ALTER SEQUENCE cards_clearance_file_seq OWNER TO vista ;
ALTER SEQUENCE cards_clearance_record_seq OWNER TO vista ;


COMMIT;

SET client_min_messages = 'notice';


