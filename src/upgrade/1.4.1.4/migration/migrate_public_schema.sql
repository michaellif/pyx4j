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
    DROP SEQUENCE tenant_sure_subscribers_seq;
    
     -- New sequences
    CREATE SEQUENCE tenant_sure_communication_history_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
  
    -- Change owner to vista
    ALTER SEQUENCE tenant_sure_communication_history_seq OWNER TO vista ;

    
COMMIT;

SET client_min_messages = 'notice';
