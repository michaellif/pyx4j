/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             version 1.1.2 PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_112(v_schema_name TEXT) RETURNS VOID AS
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
        
        ALTER TABLE insurance_certificate DROP CONSTRAINT insurance_certificate_client_fk;
        ALTER TABLE insurance_certificate DROP CONSTRAINT insurance_certificate_tenant_fk;
        ALTER TABLE insurance_tenant_sure_client DROP CONSTRAINT insurance_tenant_sure_client_tenant_fk;
        ALTER TABLE insurance_tenant_sure_report DROP CONSTRAINT insurance_tenant_sure_report_insurance_fk;
        ALTER TABLE insurance_tenant_sure_transaction DROP CONSTRAINT insurance_tenant_sure_transaction_insurance_fk;
        ALTER TABLE insurance_tenant_sure_transaction DROP CONSTRAINT insurance_tenant_sure_transaction_payment_method_fk;
        
        -- primary keys
        
        ALTER TABLE insurance_certificate DROP CONSTRAINT insurance_certificate_pk;
        ALTER TABLE insurance_tenant_sure_client DROP CONSTRAINT insurance_tenant_sure_client_pk;
        ALTER TABLE insurance_tenant_sure_report DROP CONSTRAINT insurance_tenant_sure_report_pk;
        ALTER TABLE insurance_tenant_sure_transaction DROP CONSTRAINT insurance_tenant_sure_transaction_pk;

        
        

        
        
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
        
        -- insurance_certificate
        
        ALTER TABLE insurance_certificate RENAME TO insurance_policy;
        
        CREATE TABLE insurance_certificate
        (
                id                                      BIGINT                  NOT NULL,
                id_discriminator                        VARCHAR(64)             NOT NULL,
                insurance_policy_discriminator          VARCHAR(50),
                insurance_policy                        BIGINT,
                is_managed_by_tenant                    BOOLEAN,
                insurance_provider                      VARCHAR(500),
                insurance_certificate_number            VARCHAR(500),
                liability_coverage                      NUMERIC(18,2),
                inception_date                          DATE,
                expiry_date                             DATE,
                        CONSTRAINT insurance_certificate_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE insurance_certificate OWNER TO vista;
        
        
        -- insurance_tenant_sure_client
        
        ALTER TABLE insurance_tenant_sure_client RENAME TO tenant_sure_insurance_policy_client;
        
        
        -- insurance_tenant_sure_report
        
        ALTER TABLE insurance_tenant_sure_report RENAME TO tenant_sure_insurance_policy_report;
        
        -- insurance_tenant_sure_transaction
        
        ALTER TABLE insurance_tenant_sure_transaction RENAME TO tenant_sure_transaction;
        
        
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        -- insurance_certificate
        /*
        EXECUTE 'INSERT INTO '||v_schema_name||'.insurance_certificate (id,id_discriminator,insurance_policy_discriminator,'
                ||'insurance_policy,is_managed_by_tenant,insurance_provider,insurance_certificate_number,liability_coverage,'
                ||'inception_date,expiry_date) '
                ||'(SELECT nextval(''public.insurance_certificate_seq'') AS id,'
        
        */
        
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
        
        -- primary keys
        ALTER TABLE insurance_policy ADD CONSTRAINT insurance_policy_pk PRIMARY KEY(id);
        ALTER TABLE tenant_sure_insurance_policy_client ADD CONSTRAINT tenant_sure_insurance_policy_client_pk PRIMARY KEY(id);
        ALTER TABLE tenant_sure_insurance_policy_report ADD CONSTRAINT tenant_sure_insurance_policy_report_pk PRIMARY KEY(id);
        ALTER TABLE tenant_sure_transaction ADD CONSTRAINT tenant_sure_transaction_pk PRIMARY KEY(id);

        
        -- foreign keys
        
        ALTER TABLE tenant_sure_insurance_policy_client ADD CONSTRAINT tenant_sure_insurance_policy_client_tenant_fk FOREIGN KEY(tenant) 
                REFERENCES lease_participant(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE tenant_sure_insurance_policy_report ADD CONSTRAINT tenant_sure_insurance_policy_report_insurance_fk FOREIGN KEY(insurance) 
                REFERENCES insurance_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE tenant_sure_transaction ADD CONSTRAINT tenant_sure_transaction_insurance_fk FOREIGN KEY(insurance) 
                REFERENCES insurance_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE tenant_sure_transaction ADD CONSTRAINT tenant_sure_transaction_payment_method_fk FOREIGN KEY(payment_method) 
                REFERENCES payment_method(id)  DEFERRABLE INITIALLY DEFERRED;

        

       
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
        SET     schema_version = '1.1.2',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;

        
