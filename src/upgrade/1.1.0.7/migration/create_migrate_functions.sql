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
        ***             DROP INDEX
        ***
        ***     ======================================================================================================
        **/
        
        
        DROP INDEX billing_cycle_start_date_building_type_idx;
        
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
        
        -- billing_arrears_snapshot
        
        ALTER TABLE billing_arrears_snapshot RENAME COLUMN from_date TO from_date_old;
        ALTER TABLE billing_arrears_snapshot RENAME COLUMN to_date TO to_date_old;
        
        ALTER TABLE billing_arrears_snapshot    ADD COLUMN from_date INT,
                                                ADD COLUMN to_date INT;
        
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
        
        -- billing_arrears_snapshot
        
        EXECUTE 'UPDATE '||v_schema_name||'.billing_arrears_snapshot '
                ||'SET  from_date = 1 + (from_date_old - ''01-JAN-1970''::date),'
                ||'     to_date = 1 + (to_date_old - ''01-JAN-1970''::date) ';
                
        SET CONSTRAINTS billing_arrears_snapshot_billing_account_fk, billing_arrears_snapshot_building_fk IMMEDIATE;
  
        -- customer
        
        EXECUTE 'UPDATE '||v_schema_name||'.customer '
                ||'SET  person_email = LOWER(person_email) ';
                
                
        -- employee
        
        EXECUTE 'UPDATE '||v_schema_name||'.employee '
                ||'SET  email = LOWER(email) ';
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        -- billing_arrears_snapshot
        
        ALTER TABLE billing_arrears_snapshot    DROP COLUMN from_date_old,
                                                DROP COLUMN to_date_old;
                 
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
        
        -- billing_arrears_snapshot -GiST index!
        
        CREATE INDEX billing_arrears_snapshot_from_date_to_date_idx ON billing_arrears_snapshot 
                USING GiST (box(point(from_date,from_date),point(to_date,to_date)) box_ops);
        
        CREATE INDEX aging_buckets_arrears_snapshot_discriminator_idx ON aging_buckets USING btree (arrears_snapshot_discriminator);
        CREATE INDEX aging_buckets_arrears_snapshot_idx ON aging_buckets USING btree (arrears_snapshot);
        CREATE INDEX deposit_billable_item_idx ON deposit USING btree (billable_item);
        CREATE INDEX emergency_contact_customer_idx ON emergency_contact USING btree (customer);
        CREATE INDEX lease_term_lease_idx ON lease_term USING btree (lease);
        CREATE INDEX lease_unit_idx ON lease USING btree (unit);
        CREATE INDEX product_item_product_discriminator_idx ON product_item USING btree (product_discriminator);
        CREATE INDEX product_item_product_idx ON product_item USING btree (product);
        CREATE UNIQUE INDEX billing_billing_cycle_building_billing_cycle_start_date_idx ON billing_billing_cycle USING btree (building, billing_cycle_start_date);
        
        
        -- Finishing touch
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.1.0.7',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;

        
