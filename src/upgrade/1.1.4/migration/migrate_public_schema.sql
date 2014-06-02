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
    
    -- Sequences to drop
    DROP SEQUENCE communication_message$to_seq;
    DROP SEQUENCE country_seq;
    
  
    -- New sequences
    CREATE SEQUENCE communication_message_data_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE country_policy_node_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE message_group$dispatchers_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE message_group$rls_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE message_group_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
  
    -- Change owner to vista
    ALTER SEQUENCE communication_message_data_seq OWNER TO vista ;
    ALTER SEQUENCE country_policy_node_seq OWNER TO vista ;
    ALTER SEQUENCE message_group$dispatchers_seq OWNER TO vista ;
    ALTER SEQUENCE message_group$rls_seq OWNER TO vista ;
    ALTER SEQUENCE message_group_seq OWNER TO vista ;
    ALTER SEQUENCE province_policy_node_seq OWNER TO vista ;

       
COMMIT;

SET client_min_messages = 'notice';


