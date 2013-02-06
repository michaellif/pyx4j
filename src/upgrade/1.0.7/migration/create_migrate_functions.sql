/**
*** =================================================================================
*** @version $Revision$ ($Author$) $Date$
***
***     Migration of PMC schema's to version 1.0.7
***
*** =================================================================================
**/


CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_107(v_schema_name TEXT) RETURNS VOID AS
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
        
         -- Check constraints to drop
        ALTER TABLE billing_arrears_snapshot DROP CONSTRAINT billing_arrears_snapshot_billing_account_discriminator_d_ck;
        ALTER TABLE insurance_tenant_sure_transaction DROP CONSTRAINT insurance_tenant_sure_transaction_status_e_ck;

        
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP TABLES 
        ***
        ***     ======================================================================================================
        **/
        
        DROP TABLE insurance_tenant_sure_details$taxes;
        
        
        /**
        ***     ======================================================================================================
        ***
        ***             NEW AND ALTERED TABLES 
        ***
        ***     ======================================================================================================
        **/
        
        -- html_content
        
        ALTER TABLE html_content ALTER COLUMN html TYPE VARCHAR(48000);
        
        -- insurance_tenant_sure
        
        ALTER TABLE insurance_tenant_sure ADD COLUMN payment_date INT;
        
        -- insurance_tenant_sure_report
        
        CREATE TABLE insurance_tenant_sure_report
        (
                id                              BIGINT                          NOT NULL,
                insurance                       BIGINT                          NOT NULL,
                reported_status                 VARCHAR(50),
                status_from                     DATE,
                        CONSTRAINT      insurance_tenant_sure_report_pk PRIMARY KEY(id)
        );
        
        
        -- insurance_tenant_sure_tax
        
        ALTER TABLE insurance_tenant_sure_tax   ADD COLUMN order_in_owner INT,
                                                ADD COLUMN tenant_sure_details BIGINT;
                                                
        ALTER TABLE insurance_tenant_sure_tax ALTER COLUMN tenant_sure_details SET NOT NULL;
        
        
        -- insurance_tenant_sure_transaction
        
        ALTER TABLE insurance_tenant_sure_transaction ADD COLUMN payment_due DATE;
        
        -- resident_portal_settings
        
        CREATE TABLE resident_portal_settings
        (
                id                              BIGINT                          NOT NULL,
                enabled                         BOOLEAN,
                use_custom_html                 BOOLEAN,
                        CONSTRAINT      resident_portal_settings_pk PRIMARY KEY(id)
        );
        
        
        -- resident_portal_settings$custom_html
        
        CREATE TABLE resident_portal_settings$custom_html
        (
                id                              BIGINT                          NOT NULL,
                owner                           BIGINT,
                value                           BIGINT,
                seq                             INT,
                        CONSTRAINT      resident_portal_settings$custom_html_pk PRIMARY KEY(id)
        );
        
        -- site_descriptor
        
        ALTER TABLE site_descriptor ADD COLUMN resident_portal_settings BIGINT;
        
        
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        -- Foreig keys to create
        ALTER TABLE insurance_tenant_sure_report ADD CONSTRAINT insurance_tenant_sure_report_insurance_fk FOREIGN KEY(insurance) REFERENCES insurance_tenant_sure(id);
        ALTER TABLE insurance_tenant_sure_tax ADD CONSTRAINT insurance_tenant_sure_tax_tenant_sure_details_fk FOREIGN KEY(tenant_sure_details) REFERENCES insurance_tenant_sure_details(id);
        ALTER TABLE resident_portal_settings$custom_html ADD CONSTRAINT resident_portal_settings$custom_html_owner_fk FOREIGN KEY(owner) REFERENCES resident_portal_settings(id);
        ALTER TABLE resident_portal_settings$custom_html ADD CONSTRAINT resident_portal_settings$custom_html_value_fk FOREIGN KEY(value) REFERENCES html_content(id);
        ALTER TABLE site_descriptor ADD CONSTRAINT site_descriptor_resident_portal_settings_fk FOREIGN KEY(resident_portal_settings) REFERENCES resident_portal_settings(id);

        
        -- Check constraint to create
        ALTER TABLE billing_arrears_snapshot ADD CONSTRAINT billing_arrears_snapshot_billing_account_discriminator_d_ck 
                CHECK ((billing_account_discriminator) IN ('Internal', 'YardiAccount'));
        ALTER TABLE insurance_tenant_sure_report ADD CONSTRAINT insurance_tenant_sure_report_reported_status_e_ck 
                CHECK ((reported_status) IN ('Active', 'Cancelled', 'New'));
        ALTER TABLE insurance_tenant_sure_transaction ADD CONSTRAINT insurance_tenant_sure_transaction_status_e_ck 
                CHECK ((status) IN ('AuthorizationRejected', 'AuthorizationReversal', 'Authorized', 'AuthorizedPaymentRejectedRetry', 'Cleared', 'Draft', 'PaymentError', 'PaymentRejected'));

        
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        CREATE INDEX resident_portal_settings$custom_html_owner_idx ON resident_portal_settings$custom_html USING btree (owner);

END;
$$
LANGUAGE plpgsql VOLATILE;

