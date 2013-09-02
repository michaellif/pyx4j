/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             version 1.1.1.3 PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_1113(v_schema_name TEXT) RETURNS VOID AS
$$
BEGIN
        EXECUTE 'SET search_path = '||v_schema_name;
        
        
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP CONSTRAINTS SECTION
        ***
        ***     ======================================================================================================
        **/
        
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP INDEX
        ***
        ***     ======================================================================================================
        **/
        
        
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP TABLES 
        ***
        ***     ======================================================================================================
        **/
        
        
        
        
        /**
        ***     ======================================================================================================
        ***
        ***             NEW AND ALTERED TABLES 
        ***
        ***     ======================================================================================================
        **/
        
        -- payments_summary
        
        ALTER TABLE payments_summary    ADD COLUMN master_card NUMERIC(18,2),
                                        ADD COLUMN visa NUMERIC(18,2),
                                        ADD COLUMN visa_debit NUMERIC(18,2);
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        
        
        
        -- preauthorized_payment
        
        
        EXECUTE 'DELETE FROM '||v_schema_name||'.preauthorized_payment_covered_item '
                ||'WHERE        pap IN (SELECT id FROM '||v_schema_name||'.preauthorized_payment '
                ||'                     WHERE        is_deleted '
                ||'                     AND          payment_method IS NULL)';
        
        
        EXECUTE 'DELETE FROM '||v_schema_name||'.preauthorized_payment '
                ||'WHERE        is_deleted '
                ||'AND          payment_method IS NULL';
                
        
        SET CONSTRAINTS         preauthorized_payment_payment_method_fk, preauthorized_payment_tenant_fk, 
                                payment_record_preauthorized_payment_fk,preauthorized_payment_covered_item_pap_fk IMMEDIATE;
       
        
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        
                 
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        -- not null
        
        ALTER TABLE preauthorized_payment ALTER COLUMN payment_method SET NOT NULL;
        ALTER TABLE preauthorized_payment ALTER COLUMN payment_method_discriminator SET NOT NULL;

        
       
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        
        
        -- Finishing touch
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.1.1.3',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;

        
