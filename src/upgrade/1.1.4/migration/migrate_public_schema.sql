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

    -- Altered Sequences
    
    ALTER SEQUENCE province_seq RENAME TO province_policy_node_seq;
    
  
    -- Change owner to vista

    ALTER SEQUENCE province_policy_node_seq OWNER TO vista ;

       
COMMIT;

SET client_min_messages = 'notice';


