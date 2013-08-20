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

 -- renamed sequences 
        
 ALTER SEQUENCE auto_pay_change_policy_seq RENAME TO auto_pay_policy_seq;
 ALTER SEQUENCE portal_image_resource_seq RENAME TO portal_logo_image_resource_seq;
 ALTER SEQUENCE pad_sim_batch_seq RENAME TO dev_pad_sim_batch_seq;
 ALTER SEQUENCE pad_sim_debit_record_seq RENAME TO dev_pad_sim_debit_record_seq;
 ALTER SEQUENCE pad_sim_file$state_seq RENAME TO dev_pad_sim_file$state_seq;
 ALTER SEQUENCE pad_sim_file_seq RENAME TO dev_pad_sim_file_seq;
 
 -- Sequences to drop
 
 DROP SEQUENCE communication_favorited_messages_seq;
 DROP SEQUENCE portal_preferences_seq;
 DROP SEQUENCE scheduler_trigger_details_seq;
 


 -- New sequences
 CREATE SEQUENCE customer_picture_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE direct_debit_file_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE dev_direct_debit_sim_file_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE dev_direct_debit_sim_record_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE dev_equifax_simulator_data_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE dev_visa_debit_range_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE direct_debit_record_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE global_crm_user_index_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE maintenance_request_metadata_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE maintenance_request_schedule_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE notice_of_entry_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE pad_debit_record_transaction_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE site_descriptor$pmc_info_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE yardi_building_origination_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 CREATE SEQUENCE yardi_maintenance_meta_origination_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
 

  
 -- Change owner to vista
 ALTER SEQUENCE auto_pay_policy_seq OWNER TO vista;
 ALTER SEQUENCE customer_picture_seq OWNER TO vista ;
 ALTER SEQUENCE direct_debit_file_seq OWNER TO vista ;
 ALTER SEQUENCE dev_direct_debit_sim_file_seq OWNER TO vista ;
 ALTER SEQUENCE dev_direct_debit_sim_record_seq OWNER TO vista ;
 ALTER SEQUENCE dev_equifax_simulator_data_seq OWNER TO vista ;
 ALTER SEQUENCE dev_pad_sim_batch_seq OWNER TO vista ;
 ALTER SEQUENCE dev_pad_sim_debit_record_seq OWNER TO vista ;
 ALTER SEQUENCE dev_pad_sim_file$state_seq OWNER TO vista ;
 ALTER SEQUENCE dev_pad_sim_file_seq OWNER TO vista ;
 ALTER SEQUENCE dev_visa_debit_range_seq OWNER TO vista ;
 ALTER SEQUENCE direct_debit_record_seq OWNER TO vista ;
 ALTER SEQUENCE global_crm_user_index_seq OWNER TO vista ;
 ALTER SEQUENCE maintenance_request_metadata_seq OWNER TO vista ;
 ALTER SEQUENCE maintenance_request_schedule_seq OWNER TO vista ;
 ALTER SEQUENCE notice_of_entry_seq OWNER TO vista ;
 ALTER SEQUENCE portal_logo_image_resource_seq OWNER TO vista ;
 ALTER SEQUENCE pad_debit_record_transaction_seq OWNER TO vista ;
 ALTER SEQUENCE site_descriptor$pmc_info_seq OWNER TO vista ;
 ALTER SEQUENCE yardi_building_origination_seq OWNER TO vista ;
 ALTER SEQUENCE yardi_maintenance_meta_origination_seq OWNER TO vista ;
 
        /**
        ***     =======================================================================================================
        ***
        ***             NEW AND ALTERED TABLES 
        ***
        ***     =======================================================================================================
        **/
 
 
        -- qrtz_fired_triggers
        
        ALTER TABLE qrtz_fired_triggers ADD COLUMN sched_time BIGINT;
        
        ALTER TABLE qrtz_fired_triggers ALTER COLUMN sched_time SET NOT NULL;
COMMIT;

SET client_min_messages = 'notice';


