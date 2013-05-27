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
        
        -- foreign keys
        ALTER TABLE billing_arrears_snapshot$aging_buckets DROP CONSTRAINT billing_arrears_snapshot$aging_buckets_owner_fk;
        ALTER TABLE billing_arrears_snapshot$aging_buckets DROP CONSTRAINT billing_arrears_snapshot$aging_buckets_value_fk;
        ALTER TABLE billing_arrears_snapshot DROP CONSTRAINT billing_arrears_snapshot_total_aging_buckets_fk;
        ALTER TABLE padcredit_policy_item DROP CONSTRAINT padcredit_policy_item_ar_code_fk;
        ALTER TABLE padcredit_policy_item DROP CONSTRAINT padcredit_policy_item_padpolicy_fk;
        ALTER TABLE paddebit_policy_item DROP CONSTRAINT paddebit_policy_item_ar_code_fk;
        ALTER TABLE paddebit_policy_item DROP CONSTRAINT paddebit_policy_item_padpolicy_fk;
        
        -- check constraints
  
        ALTER TABLE billable_item DROP CONSTRAINT billable_item_extra_data_discriminator_d_ck;
        ALTER TABLE billing_account DROP CONSTRAINT billing_account_id_discriminator_ck;
        ALTER TABLE billing_arrears_snapshot DROP CONSTRAINT billing_arrears_snapshot_billing_account_discriminator_ck;
        ALTER TABLE billing_arrears_snapshot DROP CONSTRAINT billing_arrears_snapshot_billing_account_discriminator_d_ck;
        ALTER TABLE billing_bill DROP CONSTRAINT billing_bill_billing_account_discriminator_d_ck;
        ALTER TABLE billing_invoice_line_item DROP CONSTRAINT billing_invoice_line_item_billing_account_discriminator_d_ck;
        ALTER TABLE deposit_lifecycle DROP CONSTRAINT deposit_lifecycle_billing_account_discriminator_d_ck;
        ALTER TABLE lease_adjustment DROP CONSTRAINT lease_adjustment_billing_account_discriminator_d_ck;
        ALTER TABLE lease DROP CONSTRAINT lease_billing_account_discriminator_d_ck;
        ALTER TABLE paddebit_policy_item DROP CONSTRAINT paddebit_policy_item_owing_balance_type_e_ck;
        ALTER TABLE padpolicy DROP CONSTRAINT padpolicy_charge_type_e_ck;
        ALTER TABLE padpolicy DROP CONSTRAINT padpolicy_node_discriminator_d_ck;
        ALTER TABLE payment_record DROP CONSTRAINT payment_record_billing_account_discriminator_d_ck;
        ALTER TABLE payment_record_external DROP CONSTRAINT payment_record_external_billing_account_discriminator_d_ck;
        ALTER TABLE preauthorized_payment DROP CONSTRAINT preauthorized_payment_amount_type_e_ck;
       
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
        
        -- aging_buckets
        
        ALTER TABLE aging_buckets       ADD COLUMN arrears_snapshot BIGINT,
                                        ADD COLUMN arrears_snapshot_discriminator VARCHAR(50),
                                        ADD COLUMN id_discriminator VARCHAR(64);
                                        
                                        
        
        -- billing_billing_cycle
        
        ALTER TABLE billing_billing_cycle RENAME COLUMN pad_execution_date TO target_pad_execution_date;
        
        -- billable_item
        
        ALTER TABLE billable_item ADD COLUMN description VARCHAR(500);
        
        
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
        
        ALTER TABLE ilspolicy_item OWNER TO vista;
        
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
        
        -- payment_information
        
        ALTER TABLE payment_information ADD COLUMN payment_method_updated TIMESTAMP;
        
        -- payment_method
        
        ALTER TABLE payment_method ADD COLUMN updated TIMESTAMP;
        
        -- preauthorized_payment
        
        ALTER TABLE preauthorized_payment       ADD COLUMN effective_from DATE,
                                                ADD COLUMN expiring DATE,
                                                ADD COLUMN updated TIMESTAMP;
                                                
        -- preauthorized_payment_covered_item
        
        CREATE TABLE preauthorized_payment_covered_item
        (
                id                              BIGINT                  NOT NULL,
                billable_item                   BIGINT,
                amount                          NUMERIC(18,2),
                pap                             BIGINT                  NOT NULL,
                order_id                        INT,
                        CONSTRAINT      preauthorized_payment_covered_item_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE preauthorized_payment_covered_item OWNER TO vista;
        
        -- yardi_lease_charge_data
        
        CREATE TABLE yardi_lease_charge_data
        (
                id                              BIGINT                  NOT NULL,
                charge_code                     VARCHAR(500),
                        CONSTRAINT      yardi_lease_charge_data_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE yardi_lease_charge_data OWNER TO vista;
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        -- aging_buckets
        /*
        EXECUTE 'UPDATE '||v_schema_name||'.aging_buckets AS a '
                ||'SET  arrears_snapshot = ab.owner '
                ||'FROM '||v_schema_name||'.billing_arrears_snapshot$aging_buckets ab '
                ||'WHERE a.id = ab.value ';
        
                
        EXECUTE 'UPDATE '||v_schema_name||'.aging_buckets AS a '
                ||'SET  arrears_snapshot = bas.id '
                ||'FROM '||v_schema_name||'.billing_arrears_snapshot bas '
                ||'WHERE a.id = bas.total_aging_buckets '
                ||'AND a.arrears_snapshot IS NULL ';
        
        */
        
        EXECUTE 'UPDATE '||v_schema_name||'.aging_buckets a '
                ||'SET id_discriminator = CASE WHEN bas.id_discriminator = ''LeaseArrearsSnapshot'' THEN ''LeaseAgingBuckets'' '
                ||'                             WHEN bas.id_discriminator = ''BuildingArrearsSnapshot'' THEN ''BuildingAgingBuckets'' END, '
                ||'arrears_snapshot_discriminator = CASE WHEN bas.id_discriminator = ''LeaseArrearsSnapshot'' THEN ''LeaseArrearsSnapshot'' '
                ||'                             WHEN bas.id_discriminator = ''BuildingArrearsSnapshot'' THEN ''BuildingArrearsSnapshot'' END, ' 
                ||'arrears_snapshot = bas.id '
                ||'FROM (SELECT id,total_aging_buckets,id_discriminator FROM '||v_schema_name||'.billing_arrears_snapshot '
                ||'     UNION '
                ||'     SELECT  ab.owner,ab.value,bas.id_discriminator FROM '||v_schema_name||'.billing_arrears_snapshot bas '
                ||'     JOIN    '||v_schema_name||'.billing_arrears_snapshot$aging_buckets ab ON (ab.owner = bas.id) ) bas '
                ||'WHERE a.id =  bas.total_aging_buckets ';
                
      
        /**
        ***     ---------------------------------------------------------------------------------------------------------
        ***     Very special case for starlight - they managed to get some aging_buckets not referenced 
        ***     from neither billing_arrears_snapshot nor billing_arrears_snapshot$aging_buckets 
        ***     ---------------------------------------------------------------------------------------------------------
        **/
        
        IF v_schema_name = 'starlight' 
        THEN
                
                DELETE FROM starlight.aging_buckets WHERE id_discriminator IS NULL;
                
        END IF;
      
        /**
        ***     ----------------------------------------------------------------------------------------------------------
        ***     Set Is_deleted to true for all yardi-enabled pmc
        ***     ----------------------------------------------------------------------------------------------------------
        **/
        
        IF EXISTS (SELECT 'x' FROM _admin_.admin_pmc a JOIN _admin_.admin_pmc_vista_features f 
                        ON (a.features = f.id AND f.yardi_integration AND a.namespace = v_schema_name ))
        THEN
                EXECUTE 'UPDATE '||v_schema_name||'.preauthorized_payment SET is_deleted = TRUE ';
        END IF;
      
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        -- billing_arrears_snapshot$aging_buckets
        
        DROP TABLE billing_arrears_snapshot$aging_buckets;
        
        
        -- billing_account
        
        ALTER TABLE billing_account DROP COLUMN id_discriminator;
        
        -- billing_arrears_snapshot
        
        ALTER TABLE billing_arrears_snapshot    DROP COLUMN billing_account_discriminator,
                                                DROP COLUMN total_aging_buckets;
        
        -- billing_bill
        
        ALTER TABLE billing_bill DROP COLUMN billing_account_discriminator;
        
        -- billing_invoice_line_item
        
        ALTER TABLE billing_invoice_line_item DROP COLUMN billing_account_discriminator;
        
        -- deposit_lifecycle
        
        ALTER TABLE deposit_lifecycle DROP COLUMN billing_account_discriminator;
        
        -- lease
        
        ALTER TABLE lease DROP COLUMN billing_account_discriminator;
        
        -- lease_adjustment
        
        ALTER TABLE lease_adjustment DROP COLUMN billing_account_discriminator;
        
        -- padcredit_policy_item
        
        DROP TABLE padcredit_policy_item;
        
        -- paddebit_policy_item
        
        DROP TABLE paddebit_policy_item;
        
        -- padpolicy
        
        DROP TABLE padpolicy;
        
        -- payment_record
        
        ALTER TABLE payment_record DROP COLUMN billing_account_discriminator;
        
        -- payment_record_external 
        
        ALTER TABLE payment_record_external DROP COLUMN billing_account_discriminator;
        
        -- preauthorized_payment
        
        ALTER TABLE preauthorized_payment       DROP COLUMN amount_type,
                                                DROP COLUMN percent,
                                                DROP COLUMN value;
        
             
        
                 
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        -- foreign keys
        
        ALTER TABLE aging_buckets ADD CONSTRAINT aging_buckets_arrears_snapshot_fk FOREIGN KEY(arrears_snapshot) REFERENCES billing_arrears_snapshot(id)  DEFERRABLE INITIALLY DEFERRED;
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
        ALTER TABLE preauthorized_payment_covered_item ADD CONSTRAINT preauthorized_payment_covered_item_billable_item_fk FOREIGN KEY(billable_item) 
                REFERENCES billable_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE preauthorized_payment_covered_item ADD CONSTRAINT preauthorized_payment_covered_item_pap_fk FOREIGN KEY(pap) 
                REFERENCES preauthorized_payment(id)  DEFERRABLE INITIALLY DEFERRED;

        

        -- check constraints
        
        ALTER TABLE aging_buckets ADD CONSTRAINT aging_buckets_arrears_snapshot_discriminator_d_ck CHECK ((arrears_snapshot_discriminator) IN ('BuildingArrearsSnapshot', 'LeaseArrearsSnapshot'));
        ALTER TABLE aging_buckets ADD CONSTRAINT aging_buckets_id_discriminator_ck CHECK ((id_discriminator) IN ('BuildingAgingBuckets', 'LeaseAgingBuckets'));
        ALTER TABLE billable_item ADD CONSTRAINT billable_item_extra_data_discriminator_d_ck 
                CHECK ((extra_data_discriminator) IN ('Pet_ChargeItemExtraData', 'Vehicle_ChargeItemExtraData', 'YardiLeaseCharge'));
        ALTER TABLE ilspolicy_item ADD CONSTRAINT ilspolicy_item_provider_e_ck CHECK (provider= 'kijiji');
        ALTER TABLE ilspolicy ADD CONSTRAINT ilspolicy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('Disc Complex', 'Disc_Building', 'Disc_Country', 'Disc_Floorplan', 'Disc_Province', 
                'OrganizationPoliciesNode', 'Unit_BuildingElement'));

       
       -- not null7431 |           0.00 |     0.00 |     0.00 |     0.00 |          0.00 |           0.00 |          0.00 |          0.00 |                   |                | BuildingArrearsSnapshot

       
       ALTER TABLE aging_buckets ALTER COLUMN id_discriminator SET NOT NULL;
       
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
        
        
        -- Finishing touch
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.1.0',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;

        
