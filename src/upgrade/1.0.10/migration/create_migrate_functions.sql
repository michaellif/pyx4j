/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             VISTA-2778 (future version 1.0.10) PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_1010(v_schema_name TEXT) RETURNS VOID AS
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
        
        -- foreign keys
        
        ALTER TABLE maintenance_request DROP CONSTRAINT maintenance_request_lease_participant_fk;
        
        -- check constraints
        
        ALTER TABLE id_assignment_item DROP CONSTRAINT id_assignment_item_target_e_ck;
        ALTER TABLE id_assignment_sequence DROP CONSTRAINT id_assignment_sequence_target_e_ck;
        ALTER TABLE maintenance_request DROP CONSTRAINT maintenance_request_lease_participant_discriminator_d_ck;
        
       
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
        
        -- maintenance_request
        
        ALTER TABLE maintenance_request ADD COLUMN building BIGINT,
                                        ADD COLUMN building_element BIGINT,
                                        ADD COLUMN building_element_discriminator VARCHAR(50),
                                        ADD COLUMN originator BIGINT,
                                        ADD COLUMN originator_discriminator VARCHAR(50),
                                        ADD COLUMN reporter_email VARCHAR(500),
                                        ADD COLUMN reporter_name VARCHAR(500),
                                        ADD COLUMN reporter_phone VARCHAR(500);
                                        
        ALTER TABLE maintenance_request RENAME COLUMN lease_participant TO reporter;
        ALTER TABLE maintenance_request RENAME COLUMN lease_participant_discriminator TO reporter_discriminator;
       
        
        ALTER TABLE maintenance_request ALTER COLUMN submitted TYPE TIMESTAMP;
        ALTER TABLE maintenance_request ALTER COLUMN updated TYPE TIMESTAMP;
        
       
                               
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        -- maintenance_request
        
        -- Kill all the maintenance requests for yardi-enabled pmcs
        
        IF EXISTS (SELECT 'x' FROM _admin_.admin_pmc a JOIN _admin_.admin_pmc_vista_features f 
                        ON (a.features = f.id AND f.yardi_integration AND a.namespace = v_schema_name ))
        THEN
                EXECUTE 'DELETE FROM '||v_schema_name||'.maintenance_request ';
        END IF;
        
        
        EXECUTE 'UPDATE '||v_schema_name||'.maintenance_request AS m '
                ||'SET  building = b.id '
                ||'FROM lease_participant lp '
                ||'JOIN lease l ON (lp.lease = l.id) '
                ||'JOIN apt_unit a ON (l.unit = a.id) '
                ||'JOIN building b ON (a.building = b.id) '
                ||'WHERE m.reporter = lp.id ';
               
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        -- maintenance_request
        
        
        
        
        
                 
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        -- foreign keys
        
        SET CONSTRAINTS maintenance_request_category_fk IMMEDIATE;
        SET CONSTRAINTS maintenance_request_priority_fk IMMEDIATE;
        SET CONSTRAINTS maintenance_request_status_fk IMMEDIATE;
        
        ALTER TABLE maintenance_request ADD CONSTRAINT maintenance_request_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE maintenance_request ADD CONSTRAINT maintenance_request_reporter_fk FOREIGN KEY(reporter) REFERENCES lease_participant(id)  DEFERRABLE INITIALLY DEFERRED;

        
        -- check constraints
        
        ALTER TABLE id_assignment_item ADD CONSTRAINT id_assignment_item_target_e_ck 
                CHECK ((target) IN ('accountNumber', 'application', 'customer', 'employee', 'guarantor', 'lead', 'lease', 'maintenance', 
                'propertyCode', 'tenant'));
        ALTER TABLE id_assignment_sequence ADD CONSTRAINT id_assignment_sequence_target_e_ck 
                CHECK ((target) IN ('accountNumber', 'application', 'customer', 'employee', 'guarantor', 'lead', 'lease', 'maintenance', 
                'propertyCode', 'tenant'));
        ALTER TABLE maintenance_request ADD CONSTRAINT maintenance_request_building_element_discriminator_d_ck 
                CHECK ((building_element_discriminator) IN ('LockerArea_BuildingElement', 'Parking_BuildingElement', 'Roof_BuildingElement', 'Unit_BuildingElement'));
        ALTER TABLE maintenance_request ADD CONSTRAINT maintenance_request_originator_discriminator_d_ck CHECK ((originator_discriminator) IN ('CrmUser', 'CustomerUser'));
        ALTER TABLE maintenance_request ADD CONSTRAINT maintenance_request_reporter_discriminator_d_ck CHECK (reporter_discriminator = 'Tenant');

        
        -- not null
        
        ALTER TABLE maintenance_request ALTER COLUMN reporter DROP NOT NULL;
        ALTER TABLE maintenance_request ALTER COLUMN reporter_discriminator DROP NOT NULL;
        ALTER TABLE maintenance_request ALTER COLUMN building SET NOT NULL;
        
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        
        
        -- Finishing touch
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.0.10',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;

        
