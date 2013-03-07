/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             1.0.8  PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_108(v_schema_name TEXT) RETURNS VOID AS
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

        -- Foreign keys
        --ALTER TABLE insurance_tenant_sure_details DROP CONSTRAINT insurance_tenant_sure_details_insurance_fk;
        --ALTER TABLE insurance_tenant_sure_tax DROP CONSTRAINT insurance_tenant_sure_tax_tenant_sure_details_fk;

        -- Check constraints
        --ALTER TABLE insurance_tenant_sure_details DROP CONSTRAINT insurance_tenant_sure_details_insurance_discriminator_d_ck;
        --ALTER TABLE insurance_tenant_sure_tax DROP CONSTRAINT insurance_tenant_sure_tax_id_discriminator_ck;
        --ALTER TABLE insurance_tenant_sure_tax DROP CONSTRAINT insurance_tenant_sure_tax_tenant_sure_details_ck;
        ALTER TABLE lease DROP CONSTRAINT lease_payment_frequency_e_ck;

        
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP TABLES 
        ***
        ***     ======================================================================================================
        **/
        
        DROP TABLE IF EXISTS insurance_tenant_sure_tax;
        DROP TABLE IF EXISTS insurance_tenant_sure_details;
        
        
        /**
        ***     ======================================================================================================
        ***
        ***             NEW AND ALTERED TABLES 
        ***
        ***     ======================================================================================================
        **/
        
        -- billing_account
        ALTER TABLE billing_account     ADD COLUMN billing_cycle_start_day INTEGER,
                                        ADD COLUMN offset_payment_due_day INTEGER,
                                        ADD COLUMN offset_preauthorized_payment_day INTEGER,
                                        ADD COLUMN payment_frequency VARCHAR(50);
                                        
                                                
        -- insurance_certificate
        ALTER TABLE insurance_certificate ADD COLUMN total_anniversary_first_month_payable NUMERIC(18,2);
        
        
        -- lease_billing_policy$available_billing_types
        
        CREATE TABLE lease_billing_policy$available_billing_types
        (
                id                                      BIGINT                          NOT NULL,
                owner                                   BIGINT,
                value                                   BIGINT,
                seq                                     INT,
                        CONSTRAINT      lease_billing_policy$available_billing_types_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE lease_billing_policy$available_billing_types OWNER TO vista;
        
        
        -- lease_billing_type_policy_item
        
        CREATE TABLE lease_billing_type_policy_item
        (
                id                                      BIGINT                          NOT NULL,
                payment_frequency                       VARCHAR(50),
                billing_cycle_start_day                 INT,
                offset_payment_due_day                  INT,
                offset_preauthorized_payment_day        INT,
                offset_execution_target_day             INT,
                        CONSTRAINT      lease_billing_type_policy_item_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE lease_billing_type_policy_item OWNER TO vista;
        
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        
         
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        -- Foreign keys
        ALTER TABLE lease_billing_policy$available_billing_types ADD CONSTRAINT lease_billing_policy$available_billing_types_owner_fk FOREIGN KEY(owner) 
                REFERENCES lease_billing_policy(id);
        ALTER TABLE lease_billing_policy$available_billing_types ADD CONSTRAINT lease_billing_policy$available_billing_types_value_fk FOREIGN KEY(value) 
                REFERENCES lease_billing_type_policy_item(id);
                
        -- Check constraints
        ALTER TABLE billing_account ADD CONSTRAINT billing_account_payment_frequency_e_ck 
                CHECK ((payment_frequency) IN ('Annually', 'BiWeekly', 'Monthly', 'SemiAnnyally', 'SemiMonthly', 'Weekly'));
        ALTER TABLE lease_billing_type_policy_item ADD CONSTRAINT lease_billing_type_policy_item_payment_frequency_e_ck 
                CHECK ((payment_frequency) IN ('Annually', 'BiWeekly', 'Monthly', 'SemiAnnyally', 'SemiMonthly', 'Weekly'));

        
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        CREATE INDEX lease_billing_policy$available_billing_types_owner_idx ON lease_billing_policy$available_billing_types USING btree (owner);
        
        
        
        
END;
$$
LANGUAGE plpgsql VOLATILE;

        
