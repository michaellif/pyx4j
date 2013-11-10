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
        ALTER TABLE building$media DROP CONSTRAINT building$media_value_fk;
        ALTER TABLE floorplan$media DROP CONSTRAINT floorplan$media_value_fk;
        ALTER TABLE insurance_certificate DROP CONSTRAINT insurance_certificate_client_fk;
        ALTER TABLE insurance_certificate DROP CONSTRAINT insurance_certificate_tenant_fk;
        ALTER TABLE insurance_tenant_sure_client DROP CONSTRAINT insurance_tenant_sure_client_tenant_fk;
        ALTER TABLE insurance_tenant_sure_report DROP CONSTRAINT insurance_tenant_sure_report_insurance_fk;
        ALTER TABLE insurance_tenant_sure_transaction DROP CONSTRAINT insurance_tenant_sure_transaction_insurance_fk;
        ALTER TABLE insurance_tenant_sure_transaction DROP CONSTRAINT insurance_tenant_sure_transaction_payment_method_fk;
        ALTER TABLE payment_record DROP CONSTRAINT payment_record_preauthorized_payment_fk;
        ALTER TABLE portal_image_set$image_set DROP CONSTRAINT portal_image_set$image_set_owner_fk;
        ALTER TABLE portal_image_set$image_set DROP CONSTRAINT portal_image_set$image_set_value_fk;
        ALTER TABLE portal_image_set DROP CONSTRAINT portal_image_set_locale_fk;
        ALTER TABLE portal_logo_image_resource DROP CONSTRAINT portal_logo_image_resource_large_fk;
        ALTER TABLE portal_logo_image_resource DROP CONSTRAINT portal_logo_image_resource_locale_fk;
        ALTER TABLE portal_logo_image_resource DROP CONSTRAINT portal_logo_image_resource_small_fk;
        ALTER TABLE preauthorized_payment_covered_item DROP CONSTRAINT preauthorized_payment_covered_item_pap_fk;
        ALTER TABLE preauthorized_payment DROP CONSTRAINT preauthorized_payment_payment_method_fk;
        ALTER TABLE preauthorized_payment DROP CONSTRAINT preauthorized_payment_tenant_fk;
        ALTER TABLE preauthorized_payment_covered_item DROP CONSTRAINT preauthorized_payment_covered_item_billable_item_fk;
        ALTER TABLE site_descriptor DROP CONSTRAINT site_descriptor_resident_portal_settings_fk;
        ALTER TABLE site_descriptor$banner DROP CONSTRAINT site_descriptor$banner_value_fk;
        ALTER TABLE site_descriptor$logo DROP CONSTRAINT site_descriptor$logo_value_fk;
        


        
        -- primary keys
        
        ALTER TABLE application_document_file DROP CONSTRAINT application_document_file_pk;
        ALTER TABLE application_document_blob DROP CONSTRAINT application_document_blob_pk;
        ALTER TABLE insurance_certificate DROP CONSTRAINT insurance_certificate_pk;
        ALTER TABLE insurance_certificate_document DROP CONSTRAINT insurance_certificate_document_pk;
        ALTER TABLE insurance_tenant_sure_client DROP CONSTRAINT insurance_tenant_sure_client_pk;
        ALTER TABLE insurance_tenant_sure_report DROP CONSTRAINT insurance_tenant_sure_report_pk;
        ALTER TABLE insurance_tenant_sure_transaction DROP CONSTRAINT insurance_tenant_sure_transaction_pk;
        ALTER TABLE file DROP CONSTRAINT file_pk;
        ALTER TABLE media DROP CONSTRAINT media_pk;
        ALTER TABLE portal_image_set$image_set DROP CONSTRAINT portal_image_set$image_set_pk;
        ALTER TABLE portal_image_set DROP CONSTRAINT portal_image_set_pk;
        ALTER TABLE portal_logo_image_resource DROP CONSTRAINT portal_logo_image_resource_pk;
        ALTER TABLE preauthorized_payment DROP CONSTRAINT preauthorized_payment_pk;
        ALTER TABLE preauthorized_payment_covered_item DROP CONSTRAINT preauthorized_payment_covered_item_pk;
        
        
        
        -- check constraints
        
        ALTER TABLE application_document_file DROP CONSTRAINT application_document_file_owner_discriminator_d_ck;
        ALTER TABLE company DROP CONSTRAINT company_logo_media_type_e_ck;
        ALTER TABLE company DROP CONSTRAINT company_logo_visibility_e_ck;
        ALTER TABLE identification_document DROP CONSTRAINT identification_document_owner_discriminator_d_ck;
        ALTER TABLE ilspolicy_item DROP CONSTRAINT ilspolicy_item_provider_e_ck;
        ALTER TABLE ilspolicy DROP CONSTRAINT ilspolicy_node_discriminator_d_ck;
        ALTER TABLE insurance_certificate DROP CONSTRAINT insurance_certificate_cancellation_e_ck;
        ALTER TABLE insurance_certificate DROP CONSTRAINT insurance_certificate_client_ck;
        ALTER TABLE insurance_certificate_document DROP CONSTRAINT insurance_certificate_document_owner_discriminator_d_ck;
        ALTER TABLE insurance_certificate DROP CONSTRAINT insurance_certificate_id_discriminator_ck;
        ALTER TABLE insurance_certificate DROP CONSTRAINT insurance_certificate_payment_schedule_e_ck;
        ALTER TABLE insurance_certificate DROP CONSTRAINT insurance_certificate_status_e_ck;
        ALTER TABLE insurance_certificate DROP CONSTRAINT insurance_certificate_tenant_discriminator_d_ck;
        ALTER TABLE insurance_tenant_sure_client DROP CONSTRAINT insurance_tenant_sure_client_tenant_discriminator_d_ck;
        ALTER TABLE insurance_tenant_sure_report DROP CONSTRAINT insurance_tenant_sure_report_insurance_discriminator_d_ck;
        ALTER TABLE insurance_tenant_sure_report DROP CONSTRAINT insurance_tenant_sure_report_reported_status_e_ck;
        ALTER TABLE insurance_tenant_sure_transaction DROP CONSTRAINT insurance_tenant_sure_transaction_insurance_discriminator_d_ck;
        ALTER TABLE insurance_tenant_sure_transaction DROP CONSTRAINT insurance_tenant_sure_transaction_payment_method_discr_d_ck;
        ALTER TABLE insurance_tenant_sure_transaction DROP CONSTRAINT insurance_tenant_sure_transaction_status_e_ck;
        ALTER TABLE media DROP CONSTRAINT media_media_type_e_ck;
        ALTER TABLE media DROP CONSTRAINT media_visibility_e_ck;
        ALTER TABLE notification DROP CONSTRAINT notification_tp_e_ck;
        ALTER TABLE payment_record DROP CONSTRAINT payment_record_payment_status_e_ck;
        ALTER TABLE payments_summary DROP CONSTRAINT payments_summary_status_e_ck;
        ALTER TABLE preauthorized_payment DROP CONSTRAINT preauthorized_payment_created_by_discriminator_d_ck;
        ALTER TABLE preauthorized_payment DROP CONSTRAINT preauthorized_payment_payment_method_discriminator_d_ck;
        ALTER TABLE preauthorized_payment DROP CONSTRAINT preauthorized_payment_tenant_discriminator_d_ck;
        ALTER TABLE proof_of_employment_document DROP CONSTRAINT proof_of_employment_document_owner_discriminator_d_ck;
        ALTER TABLE vendor DROP CONSTRAINT vendor_logo_media_type_e_ck;
        ALTER TABLE vendor DROP CONSTRAINT vendor_logo_visibility_e_ck;


        
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP INDEXES
        ***
        ***     ======================================================================================================
        **/
        
        DROP INDEX portal_image_set$image_set_owner_idx;
        
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
        
        -- application_document_file - renamed and re-created
        
        ALTER TABLE application_document_file RENAME TO insurance_certificate_scan;
        
        CREATE TABLE application_document_file (LIKE insurance_certificate_scan);
        
        ALTER TABLE application_document_file OWNER TO vista;
        
        ALTER TABLE insurance_certificate_scan RENAME COLUMN owner TO certificate_doc;
        
        
         -- application_document_blob - renamed and re-created
       
        ALTER TABLE application_document_blob RENAME TO general_insurance_policy_blob;
        
        CREATE TABLE application_document_blob (LIKE general_insurance_policy_blob);
        
        ALTER TABLE application_document_blob OWNER TO vista;
        
        ALTER TABLE general_insurance_policy_blob       ADD COLUMN name VARCHAR(500),
                                                        ADD COLUMN updated TIMESTAMP;
        
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
                                
        -- auto_pay_policy
        
        ALTER TABLE auto_pay_policy ADD COLUMN allow_cancelation_by_resident BOOLEAN;
       
        -- billing_billing_cycle
        
        ALTER TABLE billing_billing_cycle RENAME COLUMN target_pad_execution_date TO target_autopay_execution_date;
        ALTER TABLE billing_billing_cycle RENAME COLUMN actual_pad_generation_date TO actual_autopay_execution_date;
        
        
        -- building
        
        ALTER TABLE building RENAME COLUMN info_address_location_lat TO info_location_lat;
        ALTER TABLE building RENAME COLUMN info_address_location_lng TO info_location_lng;
        ALTER TABLE building ADD COLUMN default_product_catalog BOOLEAN;
        
        -- company
        
        ALTER TABLE company ADD COLUMN logo BIGINT;
        
        -- company_logo
        
        CREATE TABLE company_logo
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
                visibility                              VARCHAR(50),
                        CONSTRAINT company_logo_pk PRIMARY KEY(id)
                        
        );
        
        ALTER TABLE company_logo OWNER TO vista;
        
        -- customer_picture_blob
        
        ALTER TABLE customer_picture_blob       ADD COLUMN name VARCHAR(500),
                                                ADD COLUMN updated TIMESTAMP;
        
        -- employee_signature
        
        CREATE TABLE employee_signature
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
                employee                                BIGINT,
                        CONSTRAINT employee_signature_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE employee_signature OWNER TO vista;
        
        
        -- employee_signature_blob
        
        CREATE TABLE employee_signature_blob
        (
                id                                      BIGINT                  NOT NULL,
                content_type                            VARCHAR(500),
                data                                    BYTEA,
                created                                 TIMESTAMP,
                        CONSTRAINT employee_signature_blob_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE employee_signature_blob OWNER TO vista;
       
        
        
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
        
        
        -- insurance_certificate
        
        ALTER TABLE insurance_certificate RENAME TO insurance_policy;
        ALTER TABLE insurance_policy ADD COLUMN user_id BIGINT;
        
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
        
        
        -- insurance_certificate_document
        
        ALTER TABLE insurance_certificate_document RENAME TO insurance_certificate_doc;
        
        ALTER TABLE insurance_certificate_doc   ADD COLUMN certificate BIGINT,
                                                ADD COLUMN certificate_discriminator VARCHAR(50),
                                                ADD COLUMN description VARCHAR(500),
                                                ADD COLUMN scan_id BIGINT;
        
        
        
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
        
        ALTER TABLE marketing   ADD COLUMN use_property_address_as_marketing BOOLEAN,
                                ADD COLUMN marketing_address_suite_number VARCHAR(500),
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
        
        ALTER TABLE marketing RENAME COLUMN use_property_address_as_marketing TO use_custom_address;
                                
     
        -- media
        
        ALTER TABLE media RENAME TO media_file;
        
        ALTER TABLE media_file RENAME COLUMN media_file_blob_key TO blob_key;
        ALTER TABLE media_file RENAME COLUMN media_file_cache_version TO cache_version;
        -- ALTER TABLE media_file RENAME COLUMN media_file_caption TO caption;
        ALTER TABLE media_file RENAME COLUMN media_file_content_mime_type TO content_mime_type;
        ALTER TABLE media_file RENAME COLUMN media_file_description TO description;
        ALTER TABLE media_file RENAME COLUMN media_file_file_name TO file_name;
        ALTER TABLE media_file RENAME COLUMN media_file_file_size TO file_size;
        ALTER TABLE media_file RENAME COLUMN media_file_updated_timestamp TO updated_timestamp;
        
        
                               
        -- n4_policy
        
        CREATE TABLE n4_policy
        (
                id                                      BIGINT                  NOT NULL,
                node_discriminator                      VARCHAR(50),
                node                                    BIGINT,
                updated                                 TIMESTAMP,
                include_signature                       BOOLEAN,
                company_name                            VARCHAR(500),
                mailing_address_street1                 VARCHAR(500),
                mailing_address_street2                 VARCHAR(500),
                mailing_address_city                    VARCHAR(500),
                mailing_address_province                BIGINT,
                mailing_address_country                 BIGINT,
                mailing_address_postal_code             VARCHAR(500),
                phone_number                            VARCHAR(500),
                fax_number                              VARCHAR(500),
                email_address                           VARCHAR(500),
                hand_delivery_advance_days              INT,
                mail_delivery_advance_days              INT,
                courier_delivery_advance_days           INT,
                        CONSTRAINT n4_policy_pk PRIMARY KEY(id)     
        );
        
        ALTER TABLE n4_policy OWNER TO vista;
        
        
        -- n4_policy$relevant_arcodes
        
        CREATE TABLE n4_policy$relevant_arcodes
        (
                id                                      BIGINT                  NOT NULL,
                owner                                   BIGINT,
                value                                   BIGINT,
                seq                                     INT,
                        CONSTRAINT n4_policy$relevant_arcodes_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE n4_policy$relevant_arcodes OWNER TO vista;
        
        -- payment_record$_assert_autopay_covered_items_changes
        
        CREATE TABLE payment_record$_assert_autopay_covered_items_changes
        (
                id                                      BIGINT                  NOT NULL,
                owner                                   BIGINT,
                value                                   BIGINT,
                        CONSTRAINT payment_record$_assert_autopay_covered_items_changes_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE payment_record$_assert_autopay_covered_items_changes OWNER TO vista;
        
        
        -- portal_banner_image
        
        CREATE TABLE portal_banner_image
        (
                id                                      BIGINT                  NOT NULL,
                locale                                  BIGINT,
                image                                   BIGINT,
                        CONSTRAINT portal_banner_image_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE portal_banner_image OWNER TO vista;
        
        -- portal_image_set
        
        ALTER TABLE portal_image_set RENAME TO site_image_set;
        
        
        -- portal_image_set$image_set
        
        ALTER TABLE portal_image_set$image_set RENAME TO site_image_set$image_set; 
        
        
        -- portal_logo_image_resource
        
        ALTER TABLE portal_logo_image_resource RENAME TO site_logo_image_resource;
        
        -- preauthorized_payment
        
        ALTER TABLE preauthorized_payment RENAME TO autopay_agreement;
        
        ALTER TABLE autopay_agreement   ADD COLUMN review_of_pap BIGINT,
                                        ADD COLUMN updated_by_tenant DATE,
                                        ADD COLUMN updated_by_system DATE;
                                       
        ALTER TABLE autopay_agreement RENAME COLUMN expiring TO expired_from;
        
        
        -- preauthorized_payment_covered_item
        
        ALTER TABLE preauthorized_payment_covered_item RENAME TO autopay_agreement_covered_item;
        
        
        -- site_descriptor
        
        ALTER TABLE site_descriptor ADD COLUMN resident_portal_enabled BOOLEAN;
        
        -- site_descriptor$portal_banner
        
        CREATE TABLE site_descriptor$portal_banner
        (
                id                                      BIGINT                  NOT NULL,
                owner                                   BIGINT,
                value                                   BIGINT,
                seq                                     INT,
                        CONSTRAINT site_descriptor$portal_banner_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE site_descriptor$portal_banner OWNER TO vista;
        
        
        -- vendor
        
        ALTER TABLE vendor ADD COLUMN logo BIGINT;
        
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        
        -- autopay_agreement
        
        EXECUTE 'UPDATE '||v_schema_name||'.autopay_agreement '
                ||'SET is_deleted = TRUE '
                ||'WHERE expired_from IS NOT NULL';
        
        
        -- auto_pay_policy
        
        EXECUTE 'UPDATE '||v_schema_name||'.auto_pay_policy '
                ||'SET allow_cancelation_by_resident = TRUE ';
                
        -- building
        
        EXECUTE 'UPDATE '||v_schema_name||'.building '
                ||'SET default_product_catalog = t.default_product_catalog '
                ||'FROM         (SELECT  f.default_product_catalog '
                ||'             FROM    _admin_.admin_pmc a '
                ||'             JOIN    _admin_.admin_pmc_vista_features f ON (a.features = f.id) '
                ||'             WHERE   a.namespace = '''||v_schema_name||''' ) AS t ';
        
        -- email_template
        
        EXECUTE 'UPDATE '||v_schema_name||'.email_template '
                ||'SET content = regexp_replace(content,''\${PortalLinks.PortalHomeUrl}'',''${PortalLinks.SiteHomeUrl}'',''g'') '
                ||'WHERE content ~ ''\${PortalLinks.PortalHomeUrl}'' ';
        
        
        -- email_templates_policy
        
        EXECUTE 'UPDATE '||v_schema_name||'.email_templates_policy '
                ||'SET header = regexp_replace(header,''\${PortalLinks.PortalHomeUrl}'',''${PortalLinks.SiteHomeUrl}'',''g'') '
                ||'WHERE header ~ ''\${PortalLinks.PortalHomeUrl}'' ';
        
        
        EXECUTE 'UPDATE '||v_schema_name||'.email_templates_policy '
                ||'SET footer = regexp_replace(footer,''\${PortalLinks.PortalHomeUrl}'',''${PortalLinks.SiteHomeUrl}'',''g'') '
                ||'WHERE footer ~ ''\${PortalLinks.PortalHomeUrl}'' ';
        
        
        -- insurance_certificate
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.insurance_certificate (id,id_discriminator,insurance_policy_discriminator,'
                ||'insurance_policy,is_managed_by_tenant,insurance_provider,insurance_certificate_number,liability_coverage,'
                ||'inception_date,expiry_date) '
                ||'(SELECT nextval(''public.insurance_certificate_seq'') AS id,'
                ||'CASE WHEN id_discriminator = ''InsuranceGeneric'' THEN ''InsuranceGeneral'' '
                ||'ELSE id_discriminator END AS id_discriminator, '
                ||'CASE WHEN id_discriminator = ''InsuranceGeneric'' THEN ''GeneralInsurancePolicy'' '
                ||'ELSE ''TenantSureInsurancePolicy'' END AS insurance_policy_discriminator, '
                ||'id AS insurance_policy, is_managed_by_tenant, insurance_provider,'
                ||'insurance_certificate_number,liability_coverage,inception_date,expiry_date '
                ||'FROM '||v_schema_name||'.insurance_policy) ';
        
        
        EXECUTE 'UPDATE '||v_schema_name||'.insurance_policy AS i '
                ||'SET user_id = t.user_id '
                ||'FROM (SELECT lp.id,c.user_id '
                ||'     FROM '||v_schema_name||'.lease_participant lp '
                ||'     JOIN '||v_schema_name||'.customer c ON (c.id = lp.customer)) AS t '
                ||'WHERE i.tenant = t.id ';
                
       
       
        -- Update insurance_policy.is_deleted
        
        EXECUTE 'UPDATE '||v_schema_name||'.insurance_policy '
                ||'SET is_deleted = FALSE '
                ||'WHERE is_deleted IS NULL';
       
        -- insurance_certificate_scan
        
        -- first thing - delete everything that is not Insurance certificate
        
        EXECUTE 'DELETE FROM '||v_schema_name||'.insurance_certificate_scan '
                ||'WHERE owner_discriminator != ''InsuranceCertificateDocument'' ';
                
        EXECUTE 'DELETE FROM '||v_schema_name||'.general_insurance_policy_blob '
                ||'WHERE id NOT IN (SELECT blob_key FROM '||v_schema_name||'.insurance_certificate_scan ) ';
       
        -- data update
        
        EXECUTE 'UPDATE '||v_schema_name||'.insurance_certificate_doc AS d '
                ||'SET  certificate_discriminator = t.id_discriminator, '
                ||'     certificate = t.cert_id '
                ||'FROM         (SELECT         c.id AS cert_id,c.id_discriminator, d.id '
                ||'             FROM    '||v_schema_name||'.insurance_certificate c '
                ||'             JOIN    '||v_schema_name||'.insurance_certificate_doc d ON (c.insurance_policy = d.owner)) AS t '
                ||'WHERE d.id = t.id ';     
                
        
        -- insert into insurance_certificate_doc for multiple scans
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.insurance_certificate_doc (id,certificate,certificate_discriminator,description,scan_id) '
                ||'(SELECT nextval(''public.insurance_certificate_doc_seq'') AS id, '
                ||'     d.certificate, d.certificate_discriminator,d.description, s.id AS scan_id '
                ||'FROM '||v_schema_name||'.insurance_certificate_doc d '
                ||'JOIN '||v_schema_name||'.insurance_certificate_scan s ON (d.id = s.certificate_doc) '
                ||'WHERE s.order_in_owner > 0 ) ';
                
        -- update insurance_certificate_scan 
        
        EXECUTE 'UPDATE '||v_schema_name||'.insurance_certificate_scan AS s '
                ||'SET certificate_doc = d.id '
                ||'FROM '||v_schema_name||'.insurance_certificate_doc d '
                ||'WHERE s.id = d.scan_id ';
                
        -- insurance_policy
        
        EXECUTE 'UPDATE '||v_schema_name||'.insurance_policy '
                ||'SET id_discriminator = ''TenantSureInsurancePolicy'' '
                ||'WHERE id_discriminator = ''InsuranceTenantSure'' ';
                
         EXECUTE 'UPDATE '||v_schema_name||'.insurance_policy '
                ||'SET id_discriminator = ''GeneralInsurancePolicy'' '
                ||'WHERE id_discriminator = ''InsuranceGeneric'' ';
        
      
        -- notification
        
        EXECUTE 'UPDATE '||v_schema_name||'.notification '
                ||'SET  tp = ''AutoPayReviewRequired'' '
                ||'WHERE tp = ''PreauthorizedPaymentSuspension'' ';
                
                
        -- site_descriptor
        
        EXECUTE 'UPDATE '||v_schema_name||'.site_descriptor AS s '
                ||'SET  resident_portal_enabled = t.enabled '
                ||'FROM (SELECT a.id, b.enabled '
                ||'     FROM    '||v_schema_name||'.site_descriptor a '
                ||'     JOIN    '||v_schema_name||'.resident_portal_settings b ON (b.id = a.resident_portal_settings)) AS t '
                ||'WHERE  s.id = t.id '; 
                
                
        -- tenant_sure_insurance_policy_report
        
        EXECUTE 'UPDATE '||v_schema_name||'.tenant_sure_insurance_policy_report '
                ||'SET insurance_discriminator = ''TenantSureInsurancePolicy'' '
                ||'WHERE insurance_discriminator = ''InsuranceTenantSure'' ';
                
                
        -- tenant_sure_transaction
        
         EXECUTE 'UPDATE '||v_schema_name||'.tenant_sure_transaction '
                ||'SET insurance_discriminator = ''TenantSureInsurancePolicy'' '
                ||'WHERE insurance_discriminator = ''InsuranceTenantSure'' ';
        
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        
        SET CONSTRAINTS ALL IMMEDIATE ;
        
        -- autopay_agreement
        
        
        
        
         -- billing_billing_cycle
        
        ALTER TABLE billing_billing_cycle DROP COLUMN target_pad_generation_date;
        
        
        -- company
        
        ALTER TABLE company     DROP COLUMN logo_caption,
                                DROP COLUMN logo_media_file_blob_key,
                                DROP COLUMN logo_media_file_cache_version,
                                DROP COLUMN logo_media_file_caption,
                                DROP COLUMN logo_media_file_content_mime_type,
                                DROP COLUMN logo_media_file_description,
                                DROP COLUMN logo_media_file_file_name,
                                DROP COLUMN logo_media_file_file_size,
                                DROP COLUMN logo_media_file_updated_timestamp,
                                DROP COLUMN logo_media_type,
                                DROP COLUMN logo_url,
                                DROP COLUMN logo_visibility,
                                DROP COLUMN logo_you_tube_video_id;
        
        -- customer_picture
        
        ALTER TABLE customer_picture DROP COLUMN order_id;
        
        -- customer_screening_income_info
        
        ALTER TABLE customer_screening_income_info      DROP COLUMN address_location_lat,
                                                        DROP COLUMN address_location_lng;
                                                        
        -- customer_screening_v
        
        ALTER TABLE customer_screening_v        DROP COLUMN current_address_location_lat,
                                                DROP COLUMN current_address_location_lng,
                                                DROP COLUMN previous_address_location_lat,
                                                DROP COLUMN previous_address_location_lng;
                                                
        -- file
        
        DROP TABLE file;
        
                                                
        
        -- insurance_certificate_doc
        
        ALTER TABLE insurance_certificate_doc   DROP COLUMN order_in_owner,
                                                DROP COLUMN owner,
                                                DROP COLUMN owner_discriminator,
                                                DROP COLUMN scan_id;
                                              
   
        -- insurance_certificate_scan
                                                  
        ALTER TABLE insurance_certificate_scan  DROP COLUMN order_in_owner,
                                                DROP COLUMN owner_discriminator;
        
                
        
        
        -- insurance_policy
        
        ALTER TABLE insurance_policy    DROP COLUMN expiry_date,
                                        DROP COLUMN inception_date,
                                        DROP COLUMN insurance_certificate_number,
                                        DROP COLUMN insurance_provider,
                                        DROP COLUMN is_managed_by_tenant,
                                        DROP COLUMN is_property_vista_integrated_provider,
                                        DROP COLUMN liability_coverage;
        
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
       
        
        -- media_file
        
        ALTER TABLE media_file  DROP COLUMN media_file_caption,
                                DROP COLUMN media_type,
                                DROP COLUMN url,
                                DROP COLUMN you_tube_video_id;
        
        
        -- name
        
        DROP TABLE name;
        
        
        -- pricing
        
        DROP TABLE pricing;
        
        
        -- resident_portal_settings$proxy_whitelist
        
        DROP TABLE resident_portal_settings$proxy_whitelist;
        
        
        -- resident_portal_settings$custom_html
        
        DROP TABLE resident_portal_settings$custom_html;
            
        
        
        -- resident_portal_settings
        
        DROP TABLE resident_portal_settings;
        
        
        -- site_descriptor
        
                
        ALTER TABLE site_descriptor DROP COLUMN resident_portal_settings;
        
        
        -- tenant_insurance_policy
        
        ALTER TABLE tenant_insurance_policy     DROP COLUMN no_insurance_status_message,
                                                DROP COLUMN tenant_insurance_invitation;
                                                
        -- vendor
               
        ALTER TABLE vendor      DROP COLUMN logo_caption,
                                DROP COLUMN logo_media_file_blob_key,
                                DROP COLUMN logo_media_file_cache_version,
                                DROP COLUMN logo_media_file_caption,
                                DROP COLUMN logo_media_file_content_mime_type,
                                DROP COLUMN logo_media_file_description,
                                DROP COLUMN logo_media_file_file_name,
                                DROP COLUMN logo_media_file_file_size,
                                DROP COLUMN logo_media_file_updated_timestamp,
                                DROP COLUMN logo_media_type,
                                DROP COLUMN logo_url,
                                DROP COLUMN logo_visibility,
                                DROP COLUMN logo_you_tube_video_id;
        
          
                 
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        -- primary keys
        ALTER TABLE application_document_blob ADD CONSTRAINT application_document_blob_pk PRIMARY KEY(id);
        ALTER TABLE application_document_file ADD CONSTRAINT application_document_file_pk PRIMARY KEY(id);
        ALTER TABLE autopay_agreement ADD CONSTRAINT autopay_agreement_pk PRIMARY KEY(id);
        ALTER TABLE autopay_agreement_covered_item ADD CONSTRAINT autopay_agreement_covered_item_pk PRIMARY KEY(id);
        ALTER TABLE general_insurance_policy_blob ADD CONSTRAINT general_insurance_policy_blob_pk PRIMARY KEY(id);
        ALTER TABLE insurance_certificate_doc ADD CONSTRAINT insurance_certificate_doc_pk PRIMARY KEY(id);
        ALTER TABLE insurance_certificate_scan ADD CONSTRAINT insurance_certificate_scan_pk PRIMARY KEY(id);
        ALTER TABLE insurance_policy ADD CONSTRAINT insurance_policy_pk PRIMARY KEY(id);
        ALTER TABLE media_file ADD CONSTRAINT media_file_pk PRIMARY KEY(id);
        ALTER TABLE tenant_sure_insurance_policy_client ADD CONSTRAINT tenant_sure_insurance_policy_client_pk PRIMARY KEY(id);
        ALTER TABLE tenant_sure_insurance_policy_report ADD CONSTRAINT tenant_sure_insurance_policy_report_pk PRIMARY KEY(id);
        ALTER TABLE tenant_sure_transaction ADD CONSTRAINT tenant_sure_transaction_pk PRIMARY KEY(id);
        ALTER TABLE site_image_set$image_set ADD CONSTRAINT site_image_set$image_set_pk PRIMARY KEY(id);
        ALTER TABLE site_image_set ADD CONSTRAINT site_image_set_pk PRIMARY KEY(id);
        ALTER TABLE site_logo_image_resource ADD CONSTRAINT site_logo_image_resource_pk PRIMARY KEY(id);

        

        
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
        ALTER TABLE autopay_agreement_covered_item ADD CONSTRAINT autopay_agreement_covered_item_billable_item_fk FOREIGN KEY(billable_item) 
                REFERENCES billable_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE autopay_agreement_covered_item ADD CONSTRAINT autopay_agreement_covered_item_pap_fk FOREIGN KEY(pap) 
                REFERENCES autopay_agreement(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE building$media ADD CONSTRAINT building$media_value_fk FOREIGN KEY(value) REFERENCES media_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE company ADD CONSTRAINT company_logo_fk FOREIGN KEY(logo) REFERENCES company_logo(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE employee_signature ADD CONSTRAINT employee_signature_employee_fk FOREIGN KEY(employee) REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE floorplan$media ADD CONSTRAINT floorplan$media_value_fk FOREIGN KEY(value) REFERENCES media_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE ilsbatch$units ADD CONSTRAINT ilsbatch$units_owner_fk FOREIGN KEY(owner) REFERENCES ilsbatch(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE ilsbatch$units ADD CONSTRAINT ilsbatch$units_value_fk FOREIGN KEY(value) REFERENCES apt_unit(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE ilsbatch ADD CONSTRAINT ilsbatch_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE ilsopen_house ADD CONSTRAINT ilsopen_house_marketing_fk FOREIGN KEY(marketing) REFERENCES marketing(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE ilsprofile_building ADD CONSTRAINT ilsprofile_building_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE ilsprofile_floorplan ADD CONSTRAINT ilsprofile_floorplan_floorplan_fk FOREIGN KEY(floorplan) REFERENCES floorplan(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE ilsvendor_config ADD CONSTRAINT ilsvendor_config_config_fk FOREIGN KEY(config) REFERENCES ilsconfig(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE insurance_certificate ADD CONSTRAINT insurance_certificate_insurance_policy_fk FOREIGN KEY(insurance_policy) 
                REFERENCES insurance_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE insurance_certificate_doc ADD CONSTRAINT insurance_certificate_doc_certificate_fk FOREIGN KEY(certificate) 
                REFERENCES insurance_certificate(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE insurance_certificate_scan ADD CONSTRAINT insurance_certificate_scan_certificate_doc_fk FOREIGN KEY(certificate_doc) 
                REFERENCES insurance_certificate_doc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE insurance_policy ADD CONSTRAINT insurance_policy_client_fk FOREIGN KEY(client) 
                REFERENCES tenant_sure_insurance_policy_client(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE insurance_policy ADD CONSTRAINT insurance_policy_tenant_fk FOREIGN KEY(tenant) REFERENCES lease_participant(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE insurance_policy ADD CONSTRAINT insurance_policy_user_id_fk FOREIGN KEY(user_id) REFERENCES customer_user(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE legal_letter ADD CONSTRAINT legal_letter_lease_fk FOREIGN KEY(lease) REFERENCES lease(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE marketing ADD CONSTRAINT marketing_marketing_address_country_fk FOREIGN KEY(marketing_address_country) REFERENCES country(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE marketing ADD CONSTRAINT marketing_marketing_address_province_fk FOREIGN KEY(marketing_address_province) REFERENCES province(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE n4_policy ADD CONSTRAINT n4_policy_mailing_address_country_fk FOREIGN KEY(mailing_address_country) REFERENCES country(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE n4_policy ADD CONSTRAINT n4_policy_mailing_address_province_fk FOREIGN KEY(mailing_address_province) REFERENCES province(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE n4_policy$relevant_arcodes ADD CONSTRAINT n4_policy$relevant_arcodes_owner_fk FOREIGN KEY(owner) REFERENCES n4_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE n4_policy$relevant_arcodes ADD CONSTRAINT n4_policy$relevant_arcodes_value_fk FOREIGN KEY(value) REFERENCES arcode(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE portal_banner_image ADD CONSTRAINT portal_banner_image_image_fk FOREIGN KEY(image) REFERENCES site_image_resource(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE portal_banner_image ADD CONSTRAINT portal_banner_image_locale_fk FOREIGN KEY(locale) REFERENCES available_locale(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_record ADD CONSTRAINT payment_record_preauthorized_payment_fk FOREIGN KEY(preauthorized_payment) 
                REFERENCES autopay_agreement(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$banner ADD CONSTRAINT site_descriptor$banner_value_fk FOREIGN KEY(value) REFERENCES site_image_set(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$logo ADD CONSTRAINT site_descriptor$logo_value_fk FOREIGN KEY(value) REFERENCES site_logo_image_resource(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$portal_banner ADD CONSTRAINT site_descriptor$portal_banner_owner_fk FOREIGN KEY(owner) REFERENCES site_descriptor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$portal_banner ADD CONSTRAINT site_descriptor$portal_banner_value_fk FOREIGN KEY(value) REFERENCES portal_banner_image(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_image_set$image_set ADD CONSTRAINT site_image_set$image_set_owner_fk FOREIGN KEY(owner) REFERENCES site_image_set(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_image_set$image_set ADD CONSTRAINT site_image_set$image_set_value_fk FOREIGN KEY(value) REFERENCES site_image_resource(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_image_set ADD CONSTRAINT site_image_set_locale_fk FOREIGN KEY(locale) REFERENCES available_locale(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_logo_image_resource ADD CONSTRAINT site_logo_image_resource_large_fk FOREIGN KEY(large) REFERENCES site_image_resource(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_logo_image_resource ADD CONSTRAINT site_logo_image_resource_locale_fk FOREIGN KEY(locale) REFERENCES available_locale(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_logo_image_resource ADD CONSTRAINT site_logo_image_resource_small_fk FOREIGN KEY(small) REFERENCES site_image_resource(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_record$_assert_autopay_covered_items_changes ADD CONSTRAINT payment_record$_assert_autopay_covered_items_changes_owner_fk FOREIGN KEY(owner) 
                REFERENCES payment_record(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_record$_assert_autopay_covered_items_changes ADD CONSTRAINT payment_record$_assert_autopay_covered_items_changes_value_fk FOREIGN KEY(value) 
                REFERENCES autopay_agreement_covered_item(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE tenant_sure_insurance_policy_client ADD CONSTRAINT tenant_sure_insurance_policy_client_tenant_fk FOREIGN KEY(tenant) 
                REFERENCES lease_participant(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE tenant_sure_insurance_policy_report ADD CONSTRAINT tenant_sure_insurance_policy_report_insurance_fk FOREIGN KEY(insurance) 
                REFERENCES insurance_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE tenant_sure_transaction ADD CONSTRAINT tenant_sure_transaction_insurance_fk FOREIGN KEY(insurance) 
                REFERENCES insurance_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE tenant_sure_transaction ADD CONSTRAINT tenant_sure_transaction_payment_method_fk FOREIGN KEY(payment_method) 
                REFERENCES payment_method(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE vendor ADD CONSTRAINT vendor_logo_fk FOREIGN KEY(logo) REFERENCES company_logo(id)  DEFERRABLE INITIALLY DEFERRED;

        

        

        -- check constraints
        
        ALTER TABLE application_document_file ADD CONSTRAINT application_document_file_owner_discriminator_d_ck 
                CHECK ((owner_discriminator) IN ('IdentificationDocument', 'ProofOfEmploymentDocument'));
        ALTER TABLE apt_unit ADD CONSTRAINT apt_unit_info_legal_address_street_direction_e_ck 
                CHECK ((info_legal_address_street_direction) IN ('east', 'north', 'northEast', 'northWest', 'south', 'southEast', 'southWest', 'west'));
        ALTER TABLE apt_unit ADD CONSTRAINT apt_unit_info_legal_address_street_type_e_ck 
                CHECK ((info_legal_address_street_type) IN ('alley', 'approach', 'arcade', 'avenue', 'boulevard', 'brow', 'bypass', 'causeway', 'circle', 
                'circuit', 'circus', 'close', 'copse', 'corner', 'court', 'cove', 'crescent', 'drive', 'end', 'esplanande', 'flat', 'freeway', 'frontage', 
                'gardens', 'glade', 'glen', 'green', 'grove', 'heights', 'highway', 'lane', 'line', 'link', 'loop', 'mall', 'mews', 'other', 'packet', 
                'parade', 'park', 'parkway', 'place', 'promenade', 'reserve', 'ridge', 'rise', 'road', 'row', 'square', 'street', 'strip', 'tarn', 'terrace', 
                'thoroughfaree', 'track', 'trunkway', 'view', 'vista', 'walk', 'walkway', 'way', 'yard'));
        ALTER TABLE autopay_agreement ADD CONSTRAINT autopay_agreement_created_by_discriminator_d_ck CHECK ((created_by_discriminator) IN ('CrmUser', 'CustomerUser'));
        ALTER TABLE autopay_agreement ADD CONSTRAINT autopay_agreement_payment_method_discriminator_d_ck CHECK (payment_method_discriminator = 'LeasePaymentMethod');
        ALTER TABLE autopay_agreement ADD CONSTRAINT autopay_agreement_tenant_discriminator_d_ck CHECK (tenant_discriminator = 'Tenant');
        ALTER TABLE company_logo ADD CONSTRAINT company_logo_visibility_e_ck CHECK ((visibility) IN ('global', 'internal', 'tenant'));
        ALTER TABLE identification_document ADD CONSTRAINT identification_document_owner_discriminator_d_ck 
                CHECK ((owner_discriminator) IN ('CustomerScreening', 'CustomerScreeningIncome'));
        ALTER TABLE ilsbatch ADD CONSTRAINT ilsbatch_vendor_e_ck CHECK ((vendor) IN ('emg', 'gottarent', 'kijiji'));
        ALTER TABLE ilsprofile_building ADD CONSTRAINT ilsprofile_building_vendor_e_ck CHECK ((vendor) IN ('emg', 'gottarent', 'kijiji'));
        ALTER TABLE ilsprofile_floorplan ADD CONSTRAINT ilsprofile_floorplan_priority_e_ck CHECK ((priority) IN ('Disabled', 'High', 'Low', 'Normal'));
        ALTER TABLE ilsprofile_floorplan ADD CONSTRAINT ilsprofile_floorplan_vendor_e_ck CHECK ((vendor) IN ('emg', 'gottarent', 'kijiji'));
        ALTER TABLE ilsvendor_config ADD CONSTRAINT ilsvendor_config_vendor_e_ck CHECK ((vendor) IN ('emg', 'gottarent', 'kijiji'));
        --ALTER TABLE insurance_certificate_document ADD CONSTRAINT insurance_certificate_document_owner_discriminator_d_ck 
                -- CHECK ((owner_discriminator) IN ('CustomerScreening', 'CustomerScreeningIncome'));
        ALTER TABLE insurance_certificate ADD CONSTRAINT insurance_certificate_id_discriminator_ck 
                CHECK ((id_discriminator) IN ('InsuranceGeneral', 'InsuranceTenantSure'));
        ALTER TABLE insurance_certificate ADD CONSTRAINT insurance_certificate_insurance_policy_discriminator_d_ck 
                CHECK ((insurance_policy_discriminator) IN ('GeneralInsurancePolicy', 'TenantSureInsurancePolicy'));
        ALTER TABLE insurance_certificate_doc ADD CONSTRAINT insurance_certificate_doc_certificate_discriminator_d_ck 
                CHECK ((certificate_discriminator) IN ('InsuranceGeneral', 'InsuranceTenantSure'));
        ALTER TABLE insurance_policy ADD CONSTRAINT insurance_policy_cancellation_e_ck 
                CHECK ((cancellation) IN ('CancelledByTenant', 'CancelledByTenantSure', 'SkipPayment'));
        ALTER TABLE insurance_policy ADD CONSTRAINT insurance_policy_client_ck 
                CHECK ((id_discriminator = 'TenantSureInsurancePolicy' AND client IS NOT NULL) OR (id_discriminator != 'TenantSureInsurancePolicy' AND client IS NULL));
        ALTER TABLE insurance_policy ADD CONSTRAINT insurance_policy_id_discriminator_ck CHECK ((id_discriminator) IN ('GeneralInsurancePolicy', 'TenantSureInsurancePolicy'));
        ALTER TABLE insurance_policy ADD CONSTRAINT insurance_policy_payment_schedule_e_ck CHECK ((payment_schedule) IN ('Annual', 'Monthly'));
        ALTER TABLE insurance_policy ADD CONSTRAINT insurance_policy_status_e_ck 
                CHECK ((status) IN ('Active', 'Cancelled', 'Draft', 'Failed', 'Pending', 'PendingCancellation'));
        ALTER TABLE insurance_policy ADD CONSTRAINT insurance_policy_tenant_discriminator_d_ck CHECK (tenant_discriminator = 'Tenant');
        ALTER TABLE legal_letter ADD CONSTRAINT legal_letter_id_discriminator_ck CHECK ( id_discriminator = 'N4LegalLetter');
        ALTER TABLE marketing ADD CONSTRAINT marketing_marketing_address_street_direction_e_ck 
                CHECK ((marketing_address_street_direction) IN ('east', 'north', 'northEast', 'northWest', 'south', 'southEast', 'southWest', 'west'));
        ALTER TABLE marketing ADD CONSTRAINT marketing_marketing_address_street_type_e_ck 
                CHECK ((marketing_address_street_type) IN ('alley', 'approach', 'arcade', 'avenue', 'boulevard', 'brow', 'bypass', 'causeway', 'circle', 
                'circuit', 'circus', 'close', 'copse', 'corner', 'court', 'cove', 'crescent', 'drive', 'end', 'esplanande', 'flat', 'freeway', 'frontage', 
                'gardens', 'glade', 'glen', 'green', 'grove', 'heights', 'highway', 'lane', 'line', 'link', 'loop', 'mall', 'mews', 'other', 'packet', 
                'parade', 'park', 'parkway', 'place', 'promenade', 'reserve', 'ridge', 'rise', 'road', 'row', 'square', 'street', 'strip', 'tarn', 'terrace', 
                'thoroughfaree', 'track', 'trunkway', 'view', 'vista', 'walk', 'walkway', 'way', 'yard'));
        ALTER TABLE media_file ADD CONSTRAINT media_file_visibility_e_ck CHECK ((visibility) IN ('global', 'internal', 'tenant'));
        ALTER TABLE n4_policy ADD CONSTRAINT n4_policy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('Disc Complex', 'Disc_Building', 'Disc_Country', 'Disc_Floorplan', 'Disc_Province', 'OrganizationPoliciesNode', 'Unit_BuildingElement'));
        ALTER TABLE notification ADD CONSTRAINT notification_tp_e_ck 
                CHECK ((tp) IN ('AutoPayCanceledByResident', 'AutoPayReviewRequired', 'ElectronicPaymentRejectedNsf', 'MaintenanceRequest'));
        ALTER TABLE payment_record ADD CONSTRAINT payment_record_payment_status_e_ck 
                CHECK ((payment_status) IN ('Canceled', 'Cleared', 'PendingAction', 'Processing', 'Queued', 'Received', 'Rejected', 'Returned', 'Scheduled', 'Submitted', 'Void'));
        ALTER TABLE payments_summary ADD CONSTRAINT payments_summary_status_e_ck 
                CHECK ((status) IN ('Canceled', 'Cleared', 'PendingAction', 'Processing', 'Queued', 'Received', 'Rejected', 'Returned', 'Scheduled', 'Submitted', 'Void'));
        ALTER TABLE proof_of_employment_document ADD CONSTRAINT proof_of_employment_document_owner_discriminator_d_ck 
                CHECK ((owner_discriminator) IN ('CustomerScreening', 'CustomerScreeningIncome'));
        ALTER TABLE tenant_sure_insurance_policy_client ADD CONSTRAINT tenant_sure_insurance_policy_client_tenant_discriminator_d_ck CHECK (tenant_discriminator = 'Tenant');
        ALTER TABLE tenant_sure_insurance_policy_report ADD CONSTRAINT tenant_sure_insurance_policy_report_insurance_discr_d_ck 
                CHECK (insurance_discriminator = 'TenantSureInsurancePolicy');
        ALTER TABLE tenant_sure_insurance_policy_report ADD CONSTRAINT tenant_sure_insurance_policy_report_reported_status_e_ck 
                CHECK ((reported_status) IN ('Active', 'Cancelled', 'New'));
        ALTER TABLE tenant_sure_transaction ADD CONSTRAINT tenant_sure_transaction_insurance_discriminator_d_ck CHECK (insurance_discriminator = 'TenantSureInsurancePolicy');
        ALTER TABLE tenant_sure_transaction ADD CONSTRAINT tenant_sure_transaction_payment_method_discriminator_d_ck CHECK (payment_method_discriminator = 'InsurancePaymentMethod');
        ALTER TABLE tenant_sure_transaction ADD CONSTRAINT tenant_sure_transaction_status_e_ck 
                CHECK ((status) IN ('AuthorizationRejected', 'AuthorizationReversal', 'Authorized', 'AuthorizedPaymentRejectedRetry', 'Cleared', 'Draft', 'PaymentError', 'PaymentRejected'));
       


        -- not null
        
        ALTER TABLE insurance_policy ALTER COLUMN is_deleted SET NOT NULL;
       
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        CREATE INDEX ilsbatch$units_owner_idx ON ilsbatch$units USING btree(owner);
        CREATE INDEX ilsbatch_building_idx ON ilsbatch USING btree(building);
        CREATE INDEX ilsopen_house_marketing_idx ON ilsopen_house USING btree(marketing);
        CREATE INDEX ilsprofile_building_building_idx ON ilsprofile_building USING btree(building);
        CREATE INDEX ilsprofile_floorplan_floorplan_idx ON ilsprofile_floorplan USING btree(floorplan);
        CREATE INDEX n4_policy$relevant_arcodes_owner_idx ON n4_policy$relevant_arcodes USING btree(owner);
        CREATE INDEX payment_record$_assert_autopay_covered_items_changes_owner_idx ON payment_record$_assert_autopay_covered_items_changes USING btree(owner);
        CREATE INDEX site_descriptor$portal_banner_owner_idx ON site_descriptor$portal_banner USING btree(owner);
        CREATE INDEX site_image_set$image_set_owner_idx ON site_image_set$image_set USING btree(owner);

        
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

        
