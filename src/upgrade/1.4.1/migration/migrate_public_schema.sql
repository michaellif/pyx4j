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
    DROP SEQUENCE aggregated_transfer$adjustments_seq;
    DROP SEQUENCE aggregated_transfer$chargebacks_seq;
  
    -- New sequences
    CREATE SEQUENCE aggregated_transfer_non_vista_transaction_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    --CREATE SEQUENCE customer_settings_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE id_assignment_payment_type_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE lease_participant_move_in_action_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE oapi_conversion_blob_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE oapi_conversion_file_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE oapi_conversion_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
    CREATE SEQUENCE portal_resident_marketing_tip_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
  
    -- Change owner to vista
    ALTER SEQUENCE aggregated_transfer_non_vista_transaction_seq OWNER TO vista ;
    --ALTER SEQUENCE customer_settings_seq OWNER TO vista ;
    ALTER SEQUENCE id_assignment_payment_type_seq OWNER TO vista ;
    ALTER SEQUENCE lease_participant_move_in_action_seq OWNER TO vista ;
    ALTER SEQUENCE oapi_conversion_blob_seq OWNER TO vista ;
    ALTER SEQUENCE oapi_conversion_file_seq OWNER TO vista ;
    ALTER SEQUENCE oapi_conversion_seq OWNER TO vista ;
    ALTER SEQUENCE portal_resident_marketing_tip_seq OWNER TO vista ;
    
       
COMMIT;

SET client_min_messages = 'notice';


