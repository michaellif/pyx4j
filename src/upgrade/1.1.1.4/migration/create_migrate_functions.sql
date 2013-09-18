/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             version 1.1.1.4 PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_1114(v_schema_name TEXT) RETURNS VOID AS
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
        
        -- foreign key
        ALTER TABLE yardi_building_origination DROP CONSTRAINT yardi_building_origination_building_fk;
        
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP INDEX
        ***
        ***     ======================================================================================================
        **/
        
        DROP INDEX building_property_code_idx;
        DROP INDEX lease_lease_id_idx;
      
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
        
        ALTER TABLE building ADD COLUMN integration_system_id BIGINT;
        
        
        -- lease
        
        ALTER TABLE lease ADD COLUMN integration_system_id BIGINT; 
        
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DATA MIGRATION 
        ***
        ***     ==========================================================================================================
        **/
        
        
        IF EXISTS (SELECT 'x' FROM _admin_.admin_pmc a JOIN _admin_.admin_pmc_vista_features f 
                        ON (a.features = f.id AND f.yardi_integration AND a.namespace = v_schema_name ))
        THEN
                EXECUTE 'UPDATE '||v_schema_name||'.building AS b '
                        ||'SET  integration_system_id = y.yardi_interface_id '
                        ||'FROM (SELECT DISTINCT yardi_interface_id '
                        ||'     FROM '||v_schema_name||'.yardi_building_origination ) AS y ';
                        
                EXECUTE 'UPDATE '||v_schema_name||'.lease AS l '
                        ||'SET  integration_system_id = y.yardi_interface_id '
                        ||'FROM (SELECT DISTINCT yardi_interface_id '
                        ||'     FROM '||v_schema_name||'.yardi_building_origination ) AS y ';
                
        ELSE
                EXECUTE 'UPDATE '||v_schema_name||'.building  '
                        ||'SET  integration_system_id = -1 ';
                        
                EXECUTE 'UPDATE '||v_schema_name||'.lease  '
                        ||'SET  integration_system_id = -1 ';
                        
        END IF; 
        
        SET CONSTRAINTS ALL IMMEDIATE;
        
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        -- yardi_building_origination
        
        DROP TABLE yardi_building_origination;
                 
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
       -- not null
       
       ALTER TABLE building ALTER COLUMN integration_system_id SET NOT NULL;
       ALTER TABLE building ALTER COLUMN suspended SET NOT NULL;
       ALTER TABLE lease ALTER COLUMN integration_system_id SET NOT NULL;
       ALTER TABLE lease ALTER COLUMN lease_id SET NOT NULL;
       
       
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        CREATE UNIQUE INDEX building_property_code_integration_system_id_idx ON building USING btree (LOWER(property_code),integration_system_id);
        CREATE UNIQUE INDEX lease_lease_id_integration_system_id_idx ON lease USING btree (LOWER(lease_id),integration_system_id);
         
         
        -- Finishing touch
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.1.1.4',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;

