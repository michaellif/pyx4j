/**
*** =================================================================================
*** @version $Revision$ ($Author$) $Date$
***
***     Migration of PMC schema's to version 1.0.6 for env77
***
***
*** =================================================================================
**/


CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_106(v_schema_name TEXT) RETURNS VOID AS
$$
BEGIN
        EXECUTE 'SET search_path = '||v_schema_name;
        
        -- application_document_file
        
        ALTER TABLE application_document_file DROP COLUMN access_key;
        
        -- boiler
        
        ALTER TABLE boiler DROP COLUMN maintenance_contract_document,
                                DROP COLUMN warranty_contract_document;
                                
        -- building
        
        ALTER TABLE building ADD COLUMN info_has_earthquakes BOOLEAN,
                                ADD COLUMN info_has_fire_alarm BOOLEAN,
                                ADD COLUMN info_has_sprinklers BOOLEAN,
                                ADD COLUMN property_code_s VARCHAR(22),
                                ADD COLUMN use_external_billing BOOLEAN;
        
        ALTER TABLE building ALTER COLUMN property_code SET NOT NULL;
        
        UPDATE  building 
        SET     property_code_s = _dba_.convert_id_to_string(property_code);
        
        ALTER TABLE building DROP CONSTRAINT building_info_building_type_e_ck;
        
        UPDATE  building 
        SET     info_building_type = 'mixedResidential'
        WHERE   info_building_type = 'mixed_residential';
        
        ALTER TABLE building ADD CONSTRAINT building_info_building_type_e_ck
        CHECK (info_building_type IN ('agricultural','association','commercial','condo','industrial','military','mixedResidential','other','parkingStorage','residential',
        'seniorHousing','socialHousing'));

        
        
        -- business_id_blob
        
        CREATE TABLE business_id_blob 
        (
                id                              BIGINT                  NOT NULL,
                content_type                    VARCHAR(500),
                data                            BYTEA,
                created                         TIMESTAMP WITHOUT TIME  ZONE,
                        CONSTRAINT      business_id_blob_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE business_id_blob OWNER TO vista;
        
        
        
        -- caledon_co_signer
        
        CREATE TABLE caledon_co_signer
        (
                id                              BIGINT                  NOT NULL,
                        CONSTRAINT      caledon_co_signer_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE caledon_co_signer OWNER TO vista;
        
        -- city_intro_page
        
        CREATE TABLE city_intro_page
        (
                id                              BIGINT                  NOT NULL,
                city_name                       VARCHAR(500),
                province                        BIGINT,
                        CONSTRAINT      city_intro_page_pk PRIMARY KEY(id),
                        CONSTRAINT      city_intro_page_province_fk FOREIGN KEY(province)
                                REFERENCES province(id)
        );
        
        ALTER TABLE city_intro_page OWNER TO vista;
        
        -- city_intro_page$content
        
        CREATE TABLE city_intro_page$content
        (
                id                              BIGINT                  NOT NULL,
                owner                           BIGINT,
                value                           BIGINT,
                seq                             INT,
                        CONSTRAINT      city_intro_page$content_pk PRIMARY KEY(id),
                        CONSTRAINT      city_intro_page$content_owner_fk FOREIGN KEY(owner)
                                REFERENCES city_intro_page(id),
                        CONSTRAINT      city_intro_page$content_value_fk FOREIGN KEY(value)
                                REFERENCES html_content(id)
        );
               
        CREATE INDEX city_intro_page$content_owner_idx ON city_intro_page$content USING btree(owner);
        
        ALTER TABLE city_intro_page$content OWNER TO vista;
        
        -- company
        
        ALTER TABLE company DROP COLUMN logo_media_file_access_key;
        
        -- contract
        
        ALTER TABLE contract DROP COLUMN document;
        
       
        -- customer_credit_check
        
        ALTER TABLE customer_credit_check ADD COLUMN transaction_id BIGINT;
        
        -- elevator
        
        ALTER TABLE elevator DROP COLUMN maintenance_contract_document,
                                DROP COLUMN warranty_contract_document;
                                
        -- existing_insurance
        
        DROP TABLE existing_insurance;
        
        -- file
        
        ALTER TABLE file DROP COLUMN access_key;
        
        -- insurance_certificate
       
       DROP TABLE insurance_certificate;
        
        CREATE TABLE insurance_certificate
        (
                id                              BIGINT                  NOT NULL,
                tenant_discriminator            VARCHAR(50),
                tenant                          BIGINT,
                is_property_vista_integrated_provider   BOOLEAN,
                insurance_provider              VARCHAR(500),
                insurance_certificate_number    VARCHAR(500),
                liability_coverage              NUMERIC(18,2),
                inception_date                  DATE,
                expiry_date                     DATE,
                        CONSTRAINT      insurance_certificate_pk PRIMARY KEY(id),
                        CONSTRAINT      insurance_certificate_tenant_fk FOREIGN KEY(tenant)
                                REFERENCES lease_participant(id),
                        CONSTRAINT      insurance_certificate_tenant_discriminator_d_ck
                                CHECK (tenant_discriminator = 'Tenant')
        );
        
        ALTER TABLE insurance_certificate OWNER TO vista;
        
        -- insurance_certificate_document
        
        CREATE TABLE insurance_certificate_document
        (
                id                              BIGINT                  NOT NULL,
                order_in_owner                  INT,
                owner_discriminator             VARCHAR(50),
                owner                           BIGINT,
                        CONSTRAINT      insurance_certificate_document_pk PRIMARY KEY(id),
                        CONSTRAINT      insurance_certificate_document_owner_discriminator_d_ck
                                CHECK (owner_discriminator IN ('CustomerScreening','CustomerScreeningIncome','InsuranceCertificate'))
        );
        
        ALTER TABLE insurance_certificate_document OWNER TO vista;
        
        -- insurance_tenant_sure
        
        CREATE TABLE insurance_tenant_sure
        (
                id                              BIGINT                  NOT NULL,
                client                          BIGINT,
                insurance_certificate           BIGINT,
                quote_id                        VARCHAR(500),
                status                          VARCHAR(50),
                cancellation                    VARCHAR(50),
                cancellation_description_reason_from_tenant_sure        VARCHAR(500),
                inception_date                  DATE,
                expiry_date                     DATE,
                cancellation_date               DATE,
                monthly_payable                 NUMERIC(18,2),
                        CONSTRAINT      insurance_tenant_sure_pk PRIMARY KEY(id),
                        CONSTRAINT      insurance_tenant_sure_insurance_certificate_fk FOREIGN KEY(insurance_certificate)
                                REFERENCES insurance_certificate(id),
                        CONSTRAINT      insurance_tenant_sure_cancellation_e_ck
                                CHECK (cancellation IN ('CancelledByTenant','CancelledByTenantSure','SkipPayment')),
                        CONSTRAINT      insurance_tenant_sure_status_e_ck
                                CHECK (status IN ('Active','Cancelled','Draft','Failed','Pending','PendingCancellation'))
        );
        
        ALTER TABLE insurance_tenant_sure OWNER TO vista;
        
        -- insurance_tenant_sure_client
        
        CREATE TABLE insurance_tenant_sure_client
        (
                id                              BIGINT                  NOT NULL,
                tenant_discriminator            VARCHAR(50),
                tenant                          BIGINT,
                client_reference_number         VARCHAR(500),
                        CONSTRAINT      insurance_tenant_sure_client_pk PRIMARY KEY(id),
                        CONSTRAINT      insurance_tenant_sure_client_tenant_fk FOREIGN KEY(tenant)
                                REFERENCES lease_participant(id),
                        CONSTRAINT      insurance_tenant_sure_client_tenant_discriminator_d_ck
                                CHECK (tenant_discriminator = 'Tenant')
        );
        
        ALTER TABLE insurance_tenant_sure ADD CONSTRAINT insurance_tenant_sure_client_fk FOREIGN KEY(client)
                REFERENCES insurance_tenant_sure_client(id);
                
        ALTER TABLE insurance_tenant_sure_client OWNER TO vista;
                
        -- insurance_tenant_sure_details
        
        CREATE TABLE insurance_tenant_sure_details
        (
                id                              BIGINT                  NOT NULL,
                insurance                       BIGINT                  NOT NULL,
                liability_coverage              NUMERIC(18,2),
                contents_coverage               NUMERIC(18,2),
                deductible                      NUMERIC(18,2),
                gross_premium                   NUMERIC(18,2),
                underwriter_fee                 NUMERIC(18,2),
                        CONSTRAINT      insurance_tenant_sure_details_pk PRIMARY KEY(id),
                        CONSTRAINT      insurance_tenant_sure_details_insurance_fk FOREIGN KEY(insurance)
                                REFERENCES insurance_tenant_sure(id)
        );
        
        ALTER TABLE insurance_tenant_sure_details OWNER TO vista;
        
        -- insurance_tenant_sure_tax
        
        CREATE TABLE insurance_tenant_sure_tax
        (
                id                              BIGINT                  NOT NULL,
                absolute_amount                 NUMERIC(18,2),
                description                     VARCHAR(500),
                buiness_line                    VARCHAR(500),
                        CONSTRAINT      insurance_tenant_sure_tax_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE insurance_tenant_sure_tax OWNER TO vista;
        
        -- insurance_tenant_sure_details$taxes
        
        CREATE TABLE insurance_tenant_sure_details$taxes
        (
                id                              BIGINT                  NOT NULL,
                owner                           BIGINT,
                value                           BIGINT,
                seq                             INT,
                        CONSTRAINT      insurance_tenant_sure_details$taxes_pk PRIMARY KEY(id),
                        CONSTRAINT      insurance_tenant_sure_details$taxes_owner_fk FOREIGN KEY(owner)
                                REFERENCES insurance_tenant_sure_details(id),
                        CONSTRAINT      insurance_tenant_sure_details$taxes_value_fk FOREIGN KEY(value)
                                REFERENCES insurance_tenant_sure_tax(id)
        );
        
        ALTER TABLE insurance_tenant_sure_details$taxes OWNER TO vista;
        
        
        -- insurance_tenant_sure_transaction
        
        CREATE TABLE insurance_tenant_sure_transaction
        (
                id                              BIGINT                  NOT NULL,
                insurance                       BIGINT                  NOT NULL,
                amount                          NUMERIC(18,2),
                payment_method_discriminator    VARCHAR(50),
                payment_method                  BIGINT,
                status                          VARCHAR(50),
                transaction_authorization_number        VARCHAR(500),
                transaction_date                TIMESTAMP WITHOUT TIME ZONE,
                        CONSTRAINT      insurance_tenant_sure_transaction_pk PRIMARY KEY(id),
                        CONSTRAINT      insurance_tenant_sure_transaction_insurance_fk FOREIGN KEY(insurance) 
                                REFERENCES insurance_tenant_sure(id),
                        CONSTRAINT      insurance_tenant_sure_transaction_payment_method_fk FOREIGN KEY(payment_method)
                                REFERENCES payment_method(id),
                        CONSTRAINT      insurance_tenant_sure_transaction_payment_method_discr_d_ck
                                CHECK (payment_method_discriminator = 'InsurancePaymentMethod'),
                        CONSTRAINT      insurance_tenant_sure_transaction_status_e_ck
                                CHECK (status IN ('Authorized','Cleared','Draft','Processing','Rejected'))
        );
        
         ALTER TABLE insurance_tenant_sure_transaction OWNER TO vista;
        
        -- lease_participant
        
        ALTER TABLE lease_participant ADD COLUMN preauthorized_payment_discriminator VARCHAR(50);
        
        -- maintenance
        
        ALTER TABLE maintenance DROP COLUMN contract_document;
        
        -- media
        
        ALTER TABLE media DROP COLUMN media_file_access_key;
        
        -- note_attachment_blob
        
        CREATE TABLE note_attachment_blob
        (
                id                              BIGINT                  NOT NULL,
                content_type                    VARCHAR(500),
                data                            BYTEA,
                created                         TIMESTAMP WITHOUT TIME ZONE,
                        CONSTRAINT      note_attachment_blob_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE note_attachment_blob OWNER TO vista;
        
        -- page_meta_tags
        
        CREATE TABLE page_meta_tags
        (
                id                              BIGINT                  NOT NULL,
                locale                          BIGINT,
                title                           VARCHAR(500),
                description                     VARCHAR(500),
                keywords                        VARCHAR(500),
                        CONSTRAINT      page_meta_tags_pk PRIMARY KEY(id),
                        CONSTRAINT      page_meta_tags_locale_fk FOREIGN KEY(locale)
                                REFERENCES available_locale(id)
        );
        
        ALTER TABLE page_meta_tags OWNER TO vista;
        
        INSERT INTO page_meta_tags (id,locale)
        (SELECT nextval('public.page_meta_tags_seq'),id AS locale
        FROM    available_locale);
        
        -- payment_method
        
        ALTER TABLE payment_method ADD COLUMN id_discriminator VARCHAR(64),
                                        ADD COLUMN tenant BIGINT,
                                        ADD COLUMN tenant_discriminator VARCHAR(50);
                                        -- ADD COLUMN payment_method_discriminator VARCHAR(50);
        
        -- ALTER TABLE payment_method ALTER COLUMN customer SET NOT NULL;
        ALTER TABLE payment_method ALTER COLUMN id_discriminator SET NOT NULL;
        ALTER TABLE payment_method ALTER COLUMN customer DROP NOT NULL;
        -- ALTER TABLE payment_method ALTER COLUMN payment_method_discriminator SET NOT NULL;
        
        -- ALTER TABLE payment_method ADD CONSTRAINT  
                     
        -- payment_record
        
        ALTER TABLE payment_record ADD COLUMN payment_method_discriminator VARCHAR(50);
        
        -- payment_record_external
        
        CREATE TABLE payment_record_external
        (
                id                              BIGINT                  NOT NULL,
                billing_account                 BIGINT                  NOT NULL,
                payment_record                  BIGINT,                 
                external_transaction_id         VARCHAR(500),
                        CONSTRAINT      payment_record_external_pk PRIMARY KEY(id),
                        CONSTRAINT      payment_record_external_billing_account_fk FOREIGN KEY(billing_account)
                                REFERENCES billing_account(id),
                        CONSTRAINT      payment_record_external_payment_record_fk FOREIGN KEY(payment_record)
                                REFERENCES payment_record(id)
        );
        
        ALTER TABLE payment_record_external OWNER TO vista;
        
        -- payments_summary
        
        ALTER TABLE payments_summary RENAME COLUMN cheque TO p_check;
        ALTER TABLE payments_summary RENAME COLUMN e_cheque TO e_check;
       
        
        -- personal_information_id_blob
        
        CREATE TABLE personal_information_id_blob
        (
                id                              BIGINT                          NOT NULL,
                content_type                    VARCHAR(500),
                data                            BYTEA,
                created                         TIMESTAMP WITHOUT TIME ZONE,
                        CONSTRAINT      personal_information_id_blob_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE personal_information_id_blob OWNER TO vista;
        
        -- pmc_signature
        
        CREATE TABLE pmc_signature
        (
                id                              BIGINT                          NOT NULL,
                signature_timestamp             TIMESTAMP WITHOUT TIME ZONE,
                ip_address                      VARCHAR(500),
                full_name                       VARCHAR(500),
                      CONSTRAINT        pmc_signature_pk PRIMARY KEY(id)
        );  
        
        ALTER TABLE pmc_signature OWNER TO vista;
        
        -- portal_image_set
        
        CREATE TABLE portal_image_set
        (
                id                              BIGINT                          NOT NULL,
                locale                          BIGINT,
                        CONSTRAINT      portal_image_set_pk PRIMARY KEY(id),
                        CONSTRAINT      portal_image_set_locale_fk FOREIGN KEY(locale)
                                REFERENCES available_locale(id)
        );
        
        ALTER TABLE portal_image_set OWNER TO vista;
        
        -- portal_image_set$image_set
        
        CREATE TABLE portal_image_set$image_set
        (
                id                              BIGINT                          NOT NULL,
                owner                           BIGINT,
                value                           BIGINT,
                seq                             INT,
                        CONSTRAINT      portal_image_set$image_set_pk PRIMARY KEY(id),
                        CONSTRAINT      portal_image_set$image_set_owner_fk FOREIGN KEY(owner)
                                REFERENCES portal_image_set(id),
                        CONSTRAINT      portal_image_set$image_set_value_fk FOREIGN KEY(value)
                                REFERENCES site_image_resource(id)
        );
        
        CREATE INDEX portal_image_set$image_set_owner_idx ON portal_image_set$image_set USING btree(owner);
        
        ALTER TABLE portal_image_set$image_set OWNER TO vista;
        
        
        -- product
        
        ALTER TABLE product ADD COLUMN is_default_catalog_item BOOLEAN;
        
        UPDATE product SET is_default_catalog_item = FALSE;
        
        -- product_v
        
        ALTER TABLE product_v ALTER COLUMN name TYPE VARCHAR(50);
        
        -- property_account_info
        
        CREATE TABLE property_account_info
        (
                id                              BIGINT                          NOT NULL,
                property                        BIGINT,
                average_monthly_rent            NUMERIC(18,2),
                number_of_rented_units          INT,
                bank_name                       VARCHAR(500),
                account_type                    VARCHAR(50),
                transit_number                  VARCHAR(500),
                institution_number              VARCHAR(500),
                account_number                  VARCHAR(500),
                        CONSTRAINT      property_account_info_pk PRIMARY KEY(id),
                        CONSTRAINT      property_account_info_property_fk FOREIGN KEY(property)
                                REFERENCES building(id),
                        CONSTRAINT      property_account_info_account_type_e_ck CHECK (account_type IN ('Chequing','Saving'))   
        );
        
        ALTER TABLE property_account_info OWNER TO vista;
        
        -- roof
        
        ALTER TABLE roof DROP COLUMN maintenance_contract_document,
                        DROP COLUMN warranty_contract_document;
                        
        -- site_descriptor
        
        ALTER TABLE site_descriptor ADD COLUMN crm_logo BIGINT;
        
        ALTER TABLE site_descriptor ADD CONSTRAINT site_descriptor_crm_logo_fk FOREIGN KEY(crm_logo)
                REFERENCES site_image_resource(id);
                
        -- site_descriptor$city_intro_pages
        
        CREATE TABLE site_descriptor$city_intro_pages
        (
                id                              BIGINT                          NOT NULL,
                owner                           BIGINT,
                value                           BIGINT,
                seq                             INT,
                        CONSTRAINT      site_descriptor$city_intro_pages_pk PRIMARY KEY(id),
                        CONSTRAINT      site_descriptor$city_intro_pages_owner_fk FOREIGN KEY(owner)
                                REFERENCES site_descriptor(id),
                        CONSTRAINT      site_descriptor$city_intro_pages_value_fk FOREIGN KEY(value)
                                REFERENCES city_intro_page(id)
        );
        
                
        CREATE INDEX site_descriptor$city_intro_pages_owner_idx ON site_descriptor$city_intro_pages USING btree(owner);
        
        ALTER TABLE site_descriptor$city_intro_pages OWNER TO vista;
        
        
        -- site_descriptor$meta_tags
        
        CREATE TABLE site_descriptor$meta_tags
        (
                id                              BIGINT                          NOT NULL,
                owner                           BIGINT,
                value                           BIGINT,
                seq                             INT,
                        CONSTRAINT      site_descriptor$meta_tags_pk PRIMARY KEY(id),
                        CONSTRAINT      site_descriptor$meta_tags_owner_fk FOREIGN KEY(owner)
                                REFERENCES site_descriptor(id),
                        CONSTRAINT      site_descriptor$meta_tags_value_fk FOREIGN KEY(value)
                                REFERENCES page_meta_tags(id)
        );
        
        CREATE INDEX site_descriptor$meta_tags_owner_idx ON site_descriptor$meta_tags USING btree (owner);
        
        ALTER TABLE site_descriptor$meta_tags OWNER TO vista;
        
        INSERT INTO site_descriptor$meta_tags (id,owner,value)
        (SELECT         nextval('public.site_descriptor$meta_tags_seq') AS id,
                        a.id AS owner, b.id AS value
        FROM            site_descriptor a,page_meta_tags b);
        
        -- site_image_resource
        
        ALTER TABLE site_image_resource DROP COLUMN access_key;
        
        -- tenant_insurance_policy
        
        CREATE TABLE tenant_insurance_policy
        (
                id                              BIGINT                          NOT NULL,
                updated                         TIMESTAMP WITHOUT TIME ZONE,
                node_discriminator              VARCHAR(50),
                node                            BIGINT,
                require_minimum_liability       BOOLEAN,
                minimum_required_liability      NUMERIC(18,2),
                tenant_insurance_invitation     VARCHAR(10240),
                no_insurance_status_message     VARCHAR(10240),
                        CONSTRAINT      tenant_insurance_policy_pk PRIMARY KEY(id),
                        CONSTRAINT      tenant_insurance_policy_node_discriminator_d_ck
                                CHECK (node_discriminator IN ('Disc Complex','Disc_Building','Disc_Country','Disc_Floorplan','Disc_Province','OrganizationPoliciesNode','Unit_BuildingElement'))
        );
        
        ALTER TABLE tenant_insurance_policy OWNER TO vista;
        
             
        -- vendor
       
        ALTER TABLE vendor DROP COLUMN logo_media_file_access_key;
       
        -- warranty
       
        ALTER TABLE warranty DROP COLUMN contract_document;

        -- document
        
        DROP TABLE document;
        
        
        -- Portal data migration part
        
        UPDATE  site_descriptor 
        SET     crm_logo = b.id
        FROM    (SELECT MAX(id) AS id FROM site_image_resource WHERE file_name = 'logo.png') AS b;
        
        INSERT INTO portal_image_set (id,locale)
        (SELECT nextval('public.portal_image_set_seq'), id
        FROM    available_locale);
        
        -- For site_descriptor$banner a full blown cartesian product is needed (purpose of which still escapes me)
               
        DELETE FROM site_descriptor$banner;
        
        ALTER TABLE site_descriptor$banner DROP CONSTRAINT  site_descriptor$banner_value_fk;
        ALTER TABLE site_descriptor$banner ADD CONSTRAINT  site_descriptor$banner_value_fk FOREIGN KEY(value)
                REFERENCES portal_image_set(id);
        
        
        
        INSERT INTO site_descriptor$banner (id,owner,value)
        (SELECT nextval('public.site_descriptor$banner_seq') AS id,a.id AS owner, b.id AS value 
        FROM    site_descriptor a,portal_image_set b );

END;      
$$
LANGUAGE plpgsql VOLATILE;

