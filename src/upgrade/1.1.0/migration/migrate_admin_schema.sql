/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             _admin_ schema changes
***
***     =====================================================================================================================
**/

SET client_min_messages = 'error';

BEGIN TRANSACTION;

SET search_path = '_admin_';

        /**
        ***     ======================================================================================================
        ***
        ***             DROP TABLE SECTION
        ***
        ***     ======================================================================================================
        **/
        
        

        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP CONSTRAINTS SECTION
        ***
        ***     ======================================================================================================
        **/
        
        -- check constraints
        
        ALTER TABLE pad_batch DROP CONSTRAINT pad_batch_processing_status_e_ck;
        ALTER TABLE pad_debit_record DROP CONSTRAINT pad_debit_record_processing_status_e_ck;
        ALTER TABLE scheduler_run_data DROP CONSTRAINT scheduler_run_data_status_e_ck;
        
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP INDEXES 
        ***
        ***     ======================================================================================================
        **/
        
        
        
        
        /**
        ***     =======================================================================================================
        ***
        ***             NEW AND ALTERED TABLES 
        ***
        ***     =======================================================================================================
        **/
        
        -- admin_pmc_payment_method
        
        ALTER TABLE admin_pmc_payment_method ADD COLUMN updated TIMESTAMP;
        
        -- admin_pmc_yardi_credential
        
        ALTER TABLE admin_pmc_yardi_credential ALTER COLUMN property_code TYPE VARCHAR(2048);
       
       
        /**
        ***     ============================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     ============================================================================================================
        **/
        
        
        
        
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
       
        
        /**
        ***     ========================================================================================================
        ***
        ***             CREATE CONSTRAINTS
        ***
        ***     ========================================================================================================
        **/
        
        -- check constraints
        
        ALTER TABLE pad_batch ADD CONSTRAINT pad_batch_processing_status_e_ck 
                CHECK ((processing_status) IN ('AcknowledgeProcesed', 'AcknowledgeReject', 'AcknowledgedReceived'));
        ALTER TABLE pad_debit_record ADD CONSTRAINT pad_debit_record_processing_status_e_ck 
                CHECK ((processing_status) IN ('AcknowledgeProcesed', 'AcknowledgeReject', 'AcknowledgedReceived', 'ReconciliationProcesed', 'ReconciliationReceived'));
        ALTER TABLE  scheduler_run_data ADD CONSTRAINT scheduler_run_data_status_e_ck 
                CHECK (status IN ('Canceled','Erred','Failed','NeverRan','Processed','Running','Terminated'));


                
        /**
        ***     ============================================================================================================
        ***     
        ***             CREATE INDEXES
        ***
        ***     ============================================================================================================
        **/
        
        
       


COMMIT;

SET client_min_messages = 'notice';
