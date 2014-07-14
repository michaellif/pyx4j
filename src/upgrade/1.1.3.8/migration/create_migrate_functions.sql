/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             version 1.1.3.8 PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_1138(v_schema_name TEXT) RETURNS VOID AS
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
        
        ALTER TABLE insurance_policy DROP CONSTRAINT insurance_policy_cancellation_e_ck;
        
        
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
        
        -- insurance_policy
        
        ALTER TABLE insurance_policy    ADD COLUMN coverage BIGINT,
                                        ADD COLUMN renewal_of BIGINT,
                                        ADD COLUMN renewal_of_discriminator VARCHAR(50);
                                        
        
        -- tenant_sure_coverage
        
        CREATE TABLE tenant_sure_coverage
        (
            id                              BIGINT          NOT NULL,
            previous_claims                 INT,
            smoker                          BOOLEAN,
            tmp_ins_id                      BIGINT,
                CONSTRAINT tenant_sure_coverage_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE tenant_sure_coverage OWNER TO vista;
        
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        -- tenant_sure_coverage
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.tenant_sure_coverage(id,previous_claims,smoker,tmp_ins_id) '
                ||'(SELECT  nextval(''public.tenant_sure_coverage_seq'') AS id, 0 AS previous_claims, '
                ||'CASE WHEN c.insurance_certificate_number = ''HIG-PNJ-C50-397'' THEN TRUE ELSE FALSE END AS smoker, '
                ||'i.id AS  tmp_ins_id '
                ||'FROM '||v_schema_name||'.insurance_policy i '
                ||'JOIN '||v_schema_name||'.insurance_certificate c ON (i.id = c.insurance_policy) '
                ||'WHERE    i.id_discriminator = ''TenantSureInsurancePolicy'') ';

        -- insurance_policy
        
        EXECUTE 'UPDATE '||v_schema_name||'.insurance_policy AS i '
                ||'SET  coverage = c.id '
                ||'FROM '||v_schema_name||'.tenant_sure_coverage c '
                ||'WHERE c.tmp_ins_id = i.id ';
                
        SET CONSTRAINTS ALL IMMEDIATE;
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        -- tenant_sure_coverage
        
        ALTER TABLE tenant_sure_coverage DROP COLUMN tmp_ins_id;
                 
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        -- foreign keys
        
        ALTER TABLE insurance_policy ADD CONSTRAINT insurance_policy_coverage_fk FOREIGN KEY(coverage) 
            REFERENCES tenant_sure_coverage(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE insurance_policy ADD CONSTRAINT insurance_policy_renewal_of_fk FOREIGN KEY(renewal_of) 
            REFERENCES insurance_policy(id)  DEFERRABLE INITIALLY DEFERRED;

        
        -- check constraints
        
        ALTER TABLE insurance_policy ADD CONSTRAINT insurance_policy_cancellation_e_ck 
            CHECK ((cancellation) IN ('CancelledByTenant', 'CancelledByTenantSure', 'Renewed', 'SkipPayment'));
        ALTER TABLE insurance_policy ADD CONSTRAINT insurance_policy_renewal_of_discriminator_d_ck 
            CHECK (renewal_of_discriminator = 'TenantSureInsurancePolicy');

       
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
        SET     schema_version = '1.1.3.8',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;      
