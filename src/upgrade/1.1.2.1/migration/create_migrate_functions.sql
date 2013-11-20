/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             version 1.1.2.1 PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_1121(v_schema_name TEXT) RETURNS VOID AS
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
        ***             DROP INDEXES
        ***
        ***     ======================================================================================================
        **/
        
        
        
        /**
        ***    ======================================================================================================
        ***
        ***             Very special case for billing_arrears_snapshot_from_date_to_date_idx
        ***             This index doesn''t exist in new schemas, and might bloated for schemas
        ***             where it does exists due to removal of extra rows from billing_arrears_snapshot table 
        ***             So I''ll just drop and recreate it
        ***
        ***     ===================================================================================================== 
        **/
        
        DROP INDEX IF EXISTS billing_arrears_snapshot_from_date_to_date_idx;
        
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
        
       
        -- payment_record
        
        ALTER TABLE payment_record ADD COLUMN convenience_fee NUMERIC(18,2);
       
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        
       
        
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
        
        
       
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        CREATE INDEX lease_billing_account_idx ON lease USING btree (billing_account);
       
        
        -- billing_arrears_snapshot -GiST index!
        
        CREATE INDEX billing_arrears_snapshot_from_date_to_date_idx ON billing_arrears_snapshot 
                USING GiST (box(point(from_date,from_date),point(to_date,to_date)) box_ops);
        
        
        
        
        -- Finishing touch
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.1.2.1',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;

        
