/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             VISTA-2778 (future version 1.1.0) PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_110(v_schema_name TEXT) RETURNS VOID AS
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
        
        -- billing_billing_cycle
        
        ALTER TABLE billing_billing_cycle RENAME COLUMN pad_execution_date TO target_pad_execution_date;
        
        -- covered_item
        
        CREATE TABLE covered_item
        (
                id                              BIGINT                  NOT NULL,
                billable_item                   BIGINT,
                percent                         NUMERIC(18,2),
                        CONSTRAINT      covered_item_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE covered_item OWNER TO vista;
        
        
        -- ilspolicy
        
        CREATE TABLE ilspolicy
        (
                id                              BIGINT                  NOT NULL,
                node_discriminator              VARCHAR(50),
                node                            BIGINT,
                updated                         TIMESTAMP,
                        CONSTRAINT      ilspolicy_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE ilspolicy OWNER TO vista;                      
        
        
        -- ilspolicy_item
        
        CREATE TABLE ilspolicy_item
        (
                id                              BIGINT                  NOT NULL,
                provider                        VARCHAR(50),
                max_units                       INT,
                max_units_per_building          INT,
                min_beds                        INT,
                max_beds                        INT,
                min_baths                       INT,
                max_baths                       INT,
                min_price                       NUMERIC(18,2),
                max_price                       NUMERIC(18,2),
                policy                          BIGINT                  NOT NULL,
                order_in_parent                 INT,
                        CONSTRAINT      ilspolicy_item_pk PRIMARY KEY(id)
        );
        
        CREATE TABLE ilspolicy_item$buildings
        (
                id                              BIGINT                  NOT NULL,
                owner                           BIGINT,
                value                           BIGINT,
                        CONSTRAINT      ilspolicy_item$buildings_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE ilspolicy_item$buildings OWNER TO vista;
        
        
        -- ilspolicy_item$cities
        
        CREATE TABLE ilspolicy_item$cities
        (
                id                              BIGINT                  NOT NULL,
                owner                           BIGINT,
                value                           BIGINT,
                        CONSTRAINT      ilspolicy_item$cities_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE ilspolicy_item$cities OWNER TO vista;
        
        -- ilspolicy_item$provinces 
        
        CREATE TABLE ilspolicy_item$provinces
        (
                id                              BIGINT                  NOT NULL,
                owner                           BIGINT,
                value                           BIGINT,
                        CONSTRAINT      ilspolicy_item$provinces_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE ilspolicy_item$provinces OWNER TO vista;
        
        --  preauthorized_payment$covered_items
        
        CREATE TABLE  preauthorized_payment$covered_items
        (
                id                              BIGINT                  NOT NULL,
                owner                           BIGINT,
                value                           BIGINT,
                seq                             INT,
                        CONSTRAINT      preauthorized_payment$covered_items_pk PRIMARY KEY(id)              
        );
        
        ALTER TABLE  preauthorized_payment$covered_items OWNER TO vista;
        
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
        
        ALTER TABLE covered_item ADD CONSTRAINT covered_item_billable_item_fk FOREIGN KEY(billable_item) REFERENCES billable_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE ilspolicy_item$buildings ADD CONSTRAINT ilspolicy_item$buildings_owner_fk FOREIGN KEY(owner) 
                REFERENCES ilspolicy_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE ilspolicy_item$buildings ADD CONSTRAINT ilspolicy_item$buildings_value_fk FOREIGN KEY(value) 
                REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE ilspolicy_item$cities ADD CONSTRAINT ilspolicy_item$cities_owner_fk FOREIGN KEY(owner) 
                REFERENCES ilspolicy_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE ilspolicy_item$cities ADD CONSTRAINT ilspolicy_item$cities_value_fk FOREIGN KEY(value) 
                REFERENCES city(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE ilspolicy_item$provinces ADD CONSTRAINT ilspolicy_item$provinces_owner_fk FOREIGN KEY(owner) 
                REFERENCES ilspolicy_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE ilspolicy_item$provinces ADD CONSTRAINT ilspolicy_item$provinces_value_fk FOREIGN KEY(value) 
                REFERENCES province(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE ilspolicy_item ADD CONSTRAINT ilspolicy_item_policy_fk FOREIGN KEY(policy) REFERENCES ilspolicy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE preauthorized_payment$covered_items ADD CONSTRAINT preauthorized_payment$covered_items_owner_fk FOREIGN KEY(owner) 
                REFERENCES preauthorized_payment(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE preauthorized_payment$covered_items ADD CONSTRAINT preauthorized_payment$covered_items_value_fk FOREIGN KEY(value) 
                REFERENCES covered_item(id)  DEFERRABLE INITIALLY DEFERRED;

        -- check constraints
        
        ALTER TABLE ilspolicy_item ADD CONSTRAINT ilspolicy_item_provider_e_ck CHECK (provider= 'kijiji');
        ALTER TABLE ilspolicy ADD CONSTRAINT ilspolicy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('Disc Complex', 'Disc_Building', 'Disc_Country', 'Disc_Floorplan', 'Disc_Province', 
                'OrganizationPoliciesNode', 'Unit_BuildingElement'));

       
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        CREATE INDEX ilspolicy_item$buildings_owner_idx ON ilspolicy_item$buildings USING btree (owner);
        CREATE INDEX ilspolicy_item$cities_owner_idx ON ilspolicy_item$cities USING btree (owner);
        CREATE INDEX ilspolicy_item$provinces_owner_idx ON ilspolicy_item$provinces USING btree (owner);
        CREATE INDEX preauthorized_payment$covered_items_owner_idx ON preauthorized_payment$covered_items USING btree (owner);
        
        
        -- Finishing touch
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.1.0',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;

        
