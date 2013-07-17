/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             version 1.1.0.7 PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_1107(v_schema_name TEXT) RETURNS VOID AS
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
        
        -- custom_skin_resource_blob
        
        CREATE TABLE custom_skin_resource_blob
        (
                id                      BIGINT                  NOT NULL,
                url                     VARCHAR(500),
                content_type            VARCHAR(500),
                data                    BYTEA,
                created                 TIMESTAMP,
                        CONSTRAINT custom_skin_resource_blob_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE custom_skin_resource_blob OWNER TO vista;
        
        -- yardi_interface_policy
        
        CREATE TABLE yardi_interface_policy
        (
                id                      BIGINT                  NOT NULL,
                node_discriminator      VARCHAR(50),
                node                    BIGINT,
                updated                 TIMESTAMP,
                        CONSTRAINT yardi_interface_policy_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE yardi_interface_policy OWNER TO vista;
       
       
       -- yardi_interface_policy_charge_code_ignore
       
       CREATE TABLE yardi_interface_policy_charge_code_ignore
       (
                id                      BIGINT                  NOT NULL,
                policy                  BIGINT                  NOT NULL,
                yardi_charge_code       VARCHAR(500),
                        CONSTRAINT yardi_interface_policy_charge_code_ignore_pk PRIMARY KEY(id)
       );
       
       ALTER TABLE yardi_interface_policy_charge_code_ignore OWNER TO vista;
       
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
        
        -- foreign keys
        
        ALTER TABLE yardi_interface_policy_charge_code_ignore ADD CONSTRAINT yardi_interface_policy_charge_code_ignore_policy_fk FOREIGN KEY(policy) 
                REFERENCES yardi_interface_policy(id)  DEFERRABLE INITIALLY DEFERRED;
                
        -- check constraints
        
        ALTER TABLE yardi_interface_policy ADD CONSTRAINT yardi_interface_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('Disc Complex', 'Disc_Building', 'Disc_Country', 'Disc_Floorplan', 'Disc_Province', 
                'OrganizationPoliciesNode', 'Unit_BuildingElement'));

        
       
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        
        
        
        -- Finishing touch
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.1.0.7',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;

        
