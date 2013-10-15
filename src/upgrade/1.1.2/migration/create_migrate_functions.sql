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
        ALTER TABLE payment_record DROP CONSTRAINT payment_record_preauthorized_payment_fk;
        ALTER TABLE preauthorized_payment_covered_item DROP CONSTRAINT preauthorized_payment_covered_item_pap_fk;
        ALTER TABLE preauthorized_payment DROP CONSTRAINT preauthorized_payment_payment_method_fk;
        ALTER TABLE preauthorized_payment DROP CONSTRAINT preauthorized_payment_tenant_fk;

        
        -- primary keys
        
        ALTER TABLE insurance_certificate DROP CONSTRAINT insurance_certificate_pk;
        ALTER TABLE insurance_tenant_sure_client DROP CONSTRAINT insurance_tenant_sure_client_pk;
        ALTER TABLE insurance_tenant_sure_report DROP CONSTRAINT insurance_tenant_sure_report_pk;
        ALTER TABLE insurance_tenant_sure_transaction DROP CONSTRAINT insurance_tenant_sure_transaction_pk;
        ALTER TABLE preauthorized_payment DROP CONSTRAINT preauthorized_payment_pk;

        
        

        
        
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
        
        -- apt-unit
        
        ALTER TABLE apt_unit    ADD COLUMN info_legal_address_override BOOLEAN,
                                ADD COLUMN info_legal_address_suite_number VARCHAR(500),
                                ADD COLUMN info_legal_address_street_number VARCHAR(500),
                                ADD COLUMN info_legal_address_street_number_suffix VARCHAR(500),
                                ADD COLUMN info_legal_address_street_name VARCHAR(500),
                                ADD COLUMN info_legal_address_street_type VARCHAR(50),
                                ADD COLUMN info_legal_address_street_direction VARCHAR(50),
                                ADD COLUMN info_legal_address_city VARCHAR(500),
                                ADD COLUMN info_legal_address_county VARCHAR(500),
                                ADD COLUMN info_legal_address_province BIGINT,
                                ADD COLUMN info_legal_address_country BIGINT,
                                ADD COLUMN info_legal_address_postal_code VARCHAR(500);
       
        -- billing_billing_cycle
        
        ALTER TABLE billing_billing_cycle RENAME COLUMN target_pad_execution_date TO target_autopay_execution_date;
        ALTER TABLE billing_billing_cycle RENAME COLUMN actual_pad_generation_date TO actual_autopay_execution_date;
        
        
        
        
        -- ilsbatch
        
        CREATE TABLE ilsbatch
        (
                id                                      BIGINT                  NOT NULL,
                run_date                                DATE,
                vendor                                  VARCHAR(50),
                building                                BIGINT,
                listing_xml                             VARCHAR(500),
                        CONSTRAINT ilsbatch_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE ilsbatch OWNER TO vista;
        
        
        -- ilsbatch$units
        
        CREATE TABLE ilsbatch$units
        (
                id                                      BIGINT                  NOT NULL,
                owner                                   BIGINT,
                value                                   BIGINT,
                seq                                     INT,
                        CONSTRAINT ilsbatch$units_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE ilsbatch$units OWNER TO vista;
        
        
        -- ilsconfig
        
        CREATE TABLE ilsconfig
        (
                id                                      BIGINT                  NOT NULL,
                x                                       VARCHAR(500),
                        CONSTRAINT ilsconfig_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE ilsconfig OWNER TO vista;
        
        -- ilsopen_house
        
        CREATE TABLE ilsopen_house
        (
                id                                      BIGINT                  NOT NULL,
                marketing                               BIGINT                  NOT NULL,
                event_date                              DATE,
                start_time                              TIME,
                end_time                                TIME,
                details                                 VARCHAR(1000),
                appointment_required                    BOOLEAN,
                        CONSTRAINT ilsopen_house_pk PRIMARY KEY(id)                            
        );
        
        ALTER TABLE ilsopen_house OWNER TO vista;
        
        
        -- ilsprofile_building
        
        CREATE TABLE ilsprofile_building
        (
                id                                      BIGINT                  NOT NULL,
                building                                BIGINT                  NOT NULL,
                vendor                                  VARCHAR(50),
                preferred_contacts_url_description      VARCHAR(500),
                preferred_contacts_url_value            VARCHAR(500),
                preferred_contacts_email_description    VARCHAR(500),
                preferred_contacts_email_value          VARCHAR(500),
                preferred_contacts_phone_description    VARCHAR(500),
                preferred_contacts_phone_value          VARCHAR(500),
                disabled                                BOOLEAN,
                        CONSTRAINT ilsprofile_building_pk PRIMARY KEY(id)
        );
        
               
        ALTER TABLE ilsprofile_building OWNER TO vista;
        
        
        -- ilsprofile_floorplan
        
        CREATE TABLE ilsprofile_floorplan
        (
                id                                      BIGINT                  NOT NULL,
                floorplan                               BIGINT                  NOT NULL,
                vendor                                  VARCHAR(50),
                listing_title                           VARCHAR(500),
                description                             VARCHAR(500),
                priority                                VARCHAR(50),
                        CONSTRAINT ilsprofile_floorplan_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE ilsprofile_floorplan OWNER TO vista;
        
        
        -- ilsvendor_config
        
        CREATE TABLE ilsvendor_config
        (
                id                                      BIGINT                  NOT NULL,
                config                                  BIGINT                  NOT NULL,
                vendor                                  VARCHAR(50),
                max_daily_ads                           INT,
                        CONSTRAINT ilsvendor_config_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE ilsvendor_config OWNER TO vista;
        
        -- initialization_data
        
        CREATE TABLE initialization_data
        (
                id                                      BIGINT                  NOT NULL,
                        CONSTRAINT initialization_data_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE initialization_data OWNER TO vista;
        
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
        
        
        -- insurance_certificate_scan
        
        CREATE TABLE insurance_certificate_scan
        (
                id                                      BIGINT                  NOT NULL,
                file_name                               VARCHAR(500),
                updated_timestamp                       BIGINT,
                cache_version                           INT,
                file_size                               INT,
                content_mime_type                       VARCHAR(500),
                caption                                 VARCHAR(500),
                description                             VARCHAR(500),
                blob_key                                BIGINT,
                certificate_discriminator               VARCHAR(50),
                certificate                             BIGINT,
                        CONSTRAINT insurance_certificate_scan_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE insurance_certificate_scan OWNER TO vista;
        
        -- insurance_tenant_sure_client
        
        ALTER TABLE insurance_tenant_sure_client RENAME TO tenant_sure_insurance_policy_client;
        
        
        -- insurance_tenant_sure_report
        
        ALTER TABLE insurance_tenant_sure_report RENAME TO tenant_sure_insurance_policy_report;
        
        -- insurance_tenant_sure_transaction
        
        ALTER TABLE insurance_tenant_sure_transaction RENAME TO tenant_sure_transaction;
        
        
        -- lease_billing_type_policy_item
        
        ALTER TABLE lease_billing_type_policy_item RENAME COLUMN pad_execution_day_offset TO autopay_execution_day_offset;
        
        
        -- legal_letter
        
        CREATE TABLE legal_letter
        (
                id                                      BIGINT                  NOT NULL,
                id_discriminator                        VARCHAR(64)             NOT NULL,
                file_name                               VARCHAR(500),
                updated_timestamp                       BIGINT,
                cache_version                           INT,
                file_size                               INT,
                content_mime_type                       VARCHAR(500),
                caption                                 VARCHAR(500),
                description                             VARCHAR(500),
                blob_key                                BIGINT,
                lease                                   BIGINT                  NOT NULL,
                notes                                   VARCHAR(500),
                generated_on                            TIMESTAMP               NOT NULL,
                amount_owed                             NUMERIC(18,2),
                        CONSTRAINT legal_letter_pk PRIMARY KEY(id)
                
        );
        
        ALTER TABLE legal_letter OWNER TO vista;
        
        --legal_letter_blob
        
        CREATE TABLE legal_letter_blob
        (
                id                                      BIGINT                  NOT NULL,
                name                                    VARCHAR(500),
                content                                 BYTEA,
                content_type                            VARCHAR(500),
                updated                                 TIMESTAMP,
                        CONSTRAINT legal_letter_blob_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE legal_letter_blob OWNER TO vista;
        
        -- marketing
        
        ALTER TABLE marketing   ADD COLUMN marketing_address_suite_number VARCHAR(500),
                                ADD COLUMN marketing_address_street_number VARCHAR(500),
                                ADD COLUMN marketing_address_street_number_suffix VARCHAR(500),
                                ADD COLUMN marketing_address_street_name VARCHAR(500),
                                ADD COLUMN marketing_address_street_type VARCHAR(50),
                                ADD COLUMN marketing_address_street_direction VARCHAR(50),
                                ADD COLUMN marketing_address_city VARCHAR(500),
                                ADD COLUMN marketing_address_county VARCHAR(500),
                                ADD COLUMN marketing_address_province BIGINT,
                                ADD COLUMN marketing_address_country BIGINT,
                                ADD COLUMN marketing_address_postal_code VARCHAR(500),
                                ADD COLUMN marketing_contacts_url_description VARCHAR(500),
                                ADD COLUMN marketing_contacts_url_value VARCHAR(500),
                                ADD COLUMN marketing_contacts_email_description VARCHAR(500),
                                ADD COLUMN marketing_contacts_email_value VARCHAR(500),
                                ADD COLUMN marketing_contacts_phone_description VARCHAR(500),
                                ADD COLUMN marketing_contacts_phone_value VARCHAR(500);
                                
                                
        -- n4_policy
        
        CREATE TABLE n4_policy
        (
                id                                      BIGINT                  NOT NULL,
                node_discriminator                      VARCHAR(50),
                node                                    BIGINT,
                updated                                 TIMESTAMP,
                include_signature                       BOOLEAN,
                signature_address_street1               VARCHAR(500),
                signature_address_street2               VARCHAR(500),
                signature_address_city                  VARCHAR,
                signature_address_province              BIGINT,
                signature_address_country               BIGINT,
                signature_address_postal_code           BOOLEAN,
                        CONSTRAINT n4_policy_pk PRIMARY KEY(id)     
        );
        
        ALTER TABLE n4_policy OWNER TO vista;
        
        -- preauthorized_payment
        
        ALTER TABLE preauthorized_payment RENAME TO autopay_agreement;
        
        ALTER TABLE autopay_agreement   ADD COLUMN review_of_pap BIGINT,
                                        ADD COLUMN updated_by_tenant DATE,
                                        ADD COLUMN updated_by_system DATE;
        
        
        
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
        
         -- billing_billing_cycle
        
        ALTER TABLE billing_billing_cycle DROP COLUMN target_pad_generation_date;
        
        -- customer_picture
        
        ALTER TABLE customer_picture DROP COLUMN order_id;
        
        
        -- ilspolicy_item$provinces
        
        DROP TABLE ilspolicy_item$provinces;
        
         -- ilspolicy_item$cities
        
        DROP TABLE ilspolicy_item$cities;
        
         -- ilspolicy_item$buildings
        
        DROP TABLE ilspolicy_item$buildings;
        
        -- ilspolicy_item
        
        DROP TABLE ilspolicy_item;
        
        -- ilspolicy
        
        DROP TABLE ilspolicy;
        
        
        -- lease_billing_type_policy_item
        
        ALTER TABLE lease_billing_type_policy_item DROP COLUMN pad_calculation_day_offset;
       
        
       
        
        
        
          
                 
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        -- primary keys
        ALTER TABLE autopay_agreement ADD CONSTRAINT autopay_agreement_pk PRIMARY KEY(id);
        ALTER TABLE insurance_policy ADD CONSTRAINT insurance_policy_pk PRIMARY KEY(id);
        ALTER TABLE tenant_sure_insurance_policy_client ADD CONSTRAINT tenant_sure_insurance_policy_client_pk PRIMARY KEY(id);
        ALTER TABLE tenant_sure_insurance_policy_report ADD CONSTRAINT tenant_sure_insurance_policy_report_pk PRIMARY KEY(id);
        ALTER TABLE tenant_sure_transaction ADD CONSTRAINT tenant_sure_transaction_pk PRIMARY KEY(id);

        
        -- foreign keys
        ALTER TABLE apt_unit ADD CONSTRAINT apt_unit_info_legal_address_country_fk FOREIGN KEY(info_legal_address_country) 
                REFERENCES country(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE apt_unit ADD CONSTRAINT apt_unit_info_legal_address_province_fk FOREIGN KEY(info_legal_address_province) 
                REFERENCES province(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE autopay_agreement ADD CONSTRAINT autopay_agreement_payment_method_fk FOREIGN KEY(payment_method) 
                REFERENCES payment_method(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE autopay_agreement ADD CONSTRAINT autopay_agreement_review_of_pap_fk FOREIGN KEY(review_of_pap) 
                REFERENCES autopay_agreement(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE autopay_agreement ADD CONSTRAINT autopay_agreement_tenant_fk FOREIGN KEY(tenant) REFERENCES lease_participant(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE tenant_sure_insurance_policy_client ADD CONSTRAINT tenant_sure_insurance_policy_client_tenant_fk FOREIGN KEY(tenant) 
                REFERENCES lease_participant(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE tenant_sure_insurance_policy_report ADD CONSTRAINT tenant_sure_insurance_policy_report_insurance_fk FOREIGN KEY(insurance) 
                REFERENCES insurance_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE tenant_sure_transaction ADD CONSTRAINT tenant_sure_transaction_insurance_fk FOREIGN KEY(insurance) 
                REFERENCES insurance_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE tenant_sure_transaction ADD CONSTRAINT tenant_sure_transaction_payment_method_fk FOREIGN KEY(payment_method) 
                REFERENCES payment_method(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_record ADD CONSTRAINT payment_record_preauthorized_payment_fk FOREIGN KEY(preauthorized_payment) 
                REFERENCES autopay_agreement(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE preauthorized_payment_covered_item ADD CONSTRAINT preauthorized_payment_covered_item_pap_fk FOREIGN KEY(pap) 
                REFERENCES autopay_agreement(id)  DEFERRABLE INITIALLY DEFERRED;


        

       
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

        
