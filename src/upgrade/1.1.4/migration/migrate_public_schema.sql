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
    
    -- New sequences
    
    CREATE SEQUENCE aggregated_transfer$adjustments_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE aggregated_transfer$chargebacks_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE aggregated_transfer_adjustment_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE aggregated_transfer_chargeback_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;

  
    -- Change owner to vista
    
    ALTER SEQUENCE aggregated_transfer$adjustments_seq OWNER TO vista ;
    ALTER SEQUENCE aggregated_transfer$chargebacks_seq OWNER TO vista ;
    ALTER SEQUENCE aggregated_transfer_adjustment_seq OWNER TO vista ;
    ALTER SEQUENCE aggregated_transfer_chargeback_seq OWNER TO vista ;
    ALTER SEQUENCE province_policy_node_seq OWNER TO vista ;

       
COMMIT;

SET client_min_messages = 'notice';


