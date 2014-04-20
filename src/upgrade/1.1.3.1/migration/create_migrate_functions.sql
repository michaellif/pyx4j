/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             version 1.1.3.1 PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_1131(v_schema_name TEXT) RETURNS VOID AS
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
        
        -- check constraints
        
        ALTER TABLE insurance_policy DROP CONSTRAINT insurance_policy_status_e_ck;
        
        
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
        ***             This index doesn''t exist in new schemas, and may be bloated for schemas
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
        
        
        
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        
        
        IF EXISTS ( SELECT  'x' FROM _admin_.admin_pmc a 
                    JOIN    _admin_.admin_pmc_vista_features f ON (a.features = f.id AND f.yardi_integration AND a.namespace = v_schema_name ))
        THEN
                EXECUTE 'UPDATE '||v_schema_name||'.apt_unit '
                        ||'SET  info_unit_number = UPPER(info_unit_number) '; 
                
        END IF;
        
        
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
        
        -- check constraints
        
        ALTER TABLE insurance_policy ADD CONSTRAINT insurance_policy_status_e_ck 
            CHECK ((status) IN ('Active', 'Cancelled', 'Draft', 'Failed', 'Moved', 'Pending', 'PendingCancellation'));
       
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        

        
        -- billing_arrears_snapshot -GiST index!
        
        CREATE INDEX billing_arrears_snapshot_from_date_to_date_idx ON billing_arrears_snapshot 
                USING GiST (box(point(from_date,from_date),point(to_date,to_date)) box_ops);
        
        
        
        
        -- Finishing touch
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.1.3.1',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;      
