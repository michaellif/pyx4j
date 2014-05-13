/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             version 1.1.4 PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_114(v_schema_name TEXT) RETURNS VOID AS
$$
DECLARE
        v_rowcount      INT     := 0;
BEGIN
        EXECUTE 'SET search_path = '||v_schema_name;
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP CONSTRAINTS SECTION
        ***
        ***     ======================================================================================================
        **/
        
        -- foreign keys
        
        ALTER TABLE master_online_application DROP CONSTRAINT master_online_application_building_fk;
        ALTER TABLE master_online_application DROP CONSTRAINT master_online_application_floorplan_fk;
        
        
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
        
        -- building 
        
        ALTER TABLE building ADD COLUMN contacts_support_phone VARCHAR(500);
        
        -- legal_status
        
        ALTER TABLE legal_status    ADD COLUMN cancellation_threshold NUMERIC(18,2),
                                    ADD COLUMN expiry TIMESTAMP,
                                    ADD COLUMN termination_date DATE;
        
        -- master_online_application
        
        ALTER TABLE master_online_application RENAME COLUMN building TO ils_building;
        ALTER TABLE master_online_application RENAME COLUMN floorplan TO ils_floorplan;
        
        
        -- n4_policy
        
        ALTER TABLE n4_policy   ADD COLUMN cancellation_threshold NUMERIC(18,2),
                                ADD COLUMN expiry_days INT;
        
        
        -- online_application
        
        ALTER TABLE online_application ADD COLUMN create_date DATE;
        
        -- restrictions_policy
        
        ALTER TABLE restrictions_policy ADD COLUMN no_need_guarantors BOOLEAN;
        
        -- site_titles
        
        ALTER TABLE site_titles RENAME COLUMN resident_portal_promotions TO site_promo_title;
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        -- Phone numbers update
        
        PERFORM * FROM _dba_.update_phone_numbers(v_schema_name);
       
        
        
        -- online_application
        
        EXECUTE 'UPDATE '||v_schema_name||'.online_application AS a '
                ||'SET  create_date = m.create_date '
                ||'FROM '||v_schema_name||'.master_online_application AS m '
                ||'WHERE    m.id = a.master_online_application ';
                
        -- restrictions_policy
        
        EXECUTE 'UPDATE '||v_schema_name||'.restrictions_policy '
                ||'SET  no_need_guarantors = FALSE ';
       
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
        
        -- foreign keys
        
        ALTER TABLE master_online_application ADD CONSTRAINT master_online_application_ils_building_fk FOREIGN KEY(ils_building) 
            REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE master_online_application ADD CONSTRAINT master_online_application_ils_floorplan_fk FOREIGN KEY(ils_floorplan) 
            REFERENCES floorplan(id)  DEFERRABLE INITIALLY DEFERRED;
        
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
        SET     schema_version = '1.1.4',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;      
