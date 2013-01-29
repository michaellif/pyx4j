/**
*** =================================================================================
*** @version $Revision$ ($Author$) $Date$
***
***     Migration of PMC schema's to version 1.0.6 
***
*** =================================================================================
**/


CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_106(v_schema_name TEXT) RETURNS VOID AS
$$
BEGIN
        EXECUTE 'SET search_path = '||v_schema_name;
        
        -- application_document_file
        
        ALTER TABLE application_document_file DROP COLUMN access_key;
        
        -- billing_account
        
        ALTER TABLE billing_account ADD COLUMN id_discriminator VARCHAR(64);
        
        EXECUTE 'UPDATE  '||v_schema_name||'.billing_account '
                ||'SET     id_discriminator = ''Internal''';
        
                
        -- billing_arrears_snapshot
        
        ALTER TABLE billing_arrears_snapshot ADD COLUMN billing_account_discriminator VARCHAR(50);
        
        EXECUTE 'UPDATE  '||v_schema_name||'.billing_arrears_snapshot '
                ||'SET     billing_account_discriminator = ''Internal'' '
                ||'WHERE   id_discriminator = ''LeaseArrearsSnapshot''';
        
        -- billing_bill
        
        ALTER TABLE billing_bill ADD COLUMN billing_account_discriminator VARCHAR(50);
        
        EXECUTE 'UPDATE  '||v_schema_name||'.billing_bill '
                ||'SET     billing_account_discriminator = ''Internal''';
        
                
        -- billing_invoice_line_item
        
        ALTER TABLE billing_invoice_line_item   ADD COLUMN amount_paid NUMERIC(18,2),
                                                ADD COLUMN apply_nsf BOOLEAN,
                                                ADD COLUMN balance_due NUMERIC(18,2),
                                                ADD COLUMN billing_account_discriminator VARCHAR(50),
                                                ADD COLUMN charge_code VARCHAR(500),
                                                ADD COLUMN comment VARCHAR(500),
                                                ADD COLUMN service_type VARCHAR(50),
                                                ADD COLUMN transaction_id VARCHAR(500); 
        
        EXECUTE 'UPDATE  '||v_schema_name||'.billing_invoice_line_item '
                ||'SET     billing_account_discriminator = ''Internal''';
        
                
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
        
        EXECUTE 'UPDATE  '||v_schema_name||'.building ' 
                ||'SET     property_code_s = _dba_.convert_id_to_string(property_code)';
        
        ALTER TABLE building DROP CONSTRAINT building_info_building_type_e_ck;
        
        EXECUTE 'UPDATE  '||v_schema_name||'.building '
                ||'SET     info_building_type = ''mixedResidential'' '
                ||'WHERE   info_building_type = ''mixed_residential''';
        
                
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
        
        /**
        ***     ============================================================================================
        ***
        ***             communication_* tables - may be removed from migration altogether
        ***
        ***     ============================================================================================
        **/
        
        -- communication_person
        
        CREATE TABLE communication_person
        (
                id                              BIGINT                  NOT NULL,
                type                            VARCHAR(50),
                user_id                         BIGINT,
                        CONSTRAINT      communication_person_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE communication_person OWNER TO vista;
        
        -- communication_message
        
        CREATE TABLE communication_message
        (
                id                              BIGINT                  NOT NULL,
                parent                          BIGINT,
                sender                          BIGINT,
                destination                     BIGINT,
                topic                           VARCHAR(500),
                content                         VARCHAR(500),
                is_high_importance              BOOLEAN,
                is_read                         BOOLEAN,
                created                         TIMESTAMP WITHOUT TIME ZONE,
                updated                         TIMESTAMP WITHOUT TIME ZONE,
                        CONSTRAINT      communication_message_pk PRIMARY KEY(id),
                        CONSTRAINT      communication_message_destination_fk FOREIGN KEY(destination)
                                REFERENCES communication_person(id),
                        CONSTRAINT      communication_message_parent_fk FOREIGN KEY(parent)
                                REFERENCES communication_message(id),
                        CONSTRAINT      communication_message_sender_fk FOREIGN KEY(sender)
                                REFERENCES communication_person(id)
        );   
        
        ALTER TABLE communication_message OWNER TO vista;
        
        -- communication_favorited_messages
        
        CREATE TABLE communication_favorited_messages
        (
                id                              BIGINT                  NOT NULL,
                person                          BIGINT,
                message                         BIGINT,
                        CONSTRAINT      communication_favorited_messages_pk PRIMARY KEY(id),
                        CONSTRAINT      communication_favorited_messages_message_fk FOREIGN KEY(message)
                                REFERENCES communication_message(id),
                        CONSTRAINT      communication_favorited_messages_person_fk FOREIGN KEY(person)
                                REFERENCES communication_person(id)
        );
        
        ALTER TABLE communication_favorited_messages OWNER TO vista;
        
        /** END COMMUNICATION **/
        
        -- company
        
        ALTER TABLE company DROP COLUMN logo_media_file_access_key;
        
        -- contact_email
        
        DROP TABLE contact_email;
        
        -- contact_internal
        
        DROP TABLE contact_internal;
        
        -- contact_phone
        
        DROP TABLE contact_phone;
        
        -- contact_postal
        
        DROP TABLE contact_postal;
        
        
        -- contract
        
        ALTER TABLE contract DROP COLUMN document;
        
        -- customer
        
        ALTER TABLE customer ADD COLUMN portal_registration_token VARCHAR(500);
        
       
        -- customer_credit_check
        
        ALTER TABLE customer_credit_check ADD COLUMN transaction_id BIGINT;
        
        -- deposit_lifecycle
        
        ALTER TABLE deposit_lifecycle ADD COLUMN billing_account_discriminator VARCHAR(50);
        
        UPDATE  deposit_lifecycle
        SET     billing_account_discriminator = 'Internal';
        
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
        
        -- lease
        
        ALTER TABLE lease ADD COLUMN billing_account_discriminator VARCHAR(50);
        
        EXECUTE 'UPDATE  '||v_schema_name||'.lease '
                ||'SET     billing_account_discriminator = ''Internal''';
        
       
        -- lease_adjustment
        
        ALTER TABLE lease_adjustment ADD COLUMN billing_account_discriminator VARCHAR(50);
        
        EXECUTE 'UPDATE  '||v_schema_name||'.lease_adjustment '
                ||'SET     billing_account_discriminator = ''Internal''';
        
        -- lease_participant
        
        ALTER TABLE lease_participant ADD COLUMN preauthorized_payment_discriminator VARCHAR(50);
        
        EXECUTE 'UPDATE '||v_schema_name||'.lease_participant '
                ||'SET  preauthorized_payment_discriminator = ''LeasePaymentMethod''';
        
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
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.page_meta_tags (id,locale) '
        ||'(SELECT nextval(''public.page_meta_tags_seq''),id AS locale '
        ||'FROM    '||v_schema_name||'.available_locale)';
        
        -- payment_method
        
        ALTER TABLE payment_method ADD COLUMN id_discriminator VARCHAR(64),
                                        ADD COLUMN tenant BIGINT,
                                        ADD COLUMN tenant_discriminator VARCHAR(50);
                                                
        
        ALTER TABLE payment_method ADD CONSTRAINT payment_method_tenant_fk FOREIGN KEY(tenant) REFERENCES lease_participant(id);
        
        EXECUTE 'UPDATE '||v_schema_name||'.payment_method '
                ||'SET  id_discriminator = ''LeasePaymentMethod''';
                     
        -- payment_record
        
        ALTER TABLE payment_record      ADD COLUMN payment_method_discriminator VARCHAR(50),
                                        ADD COLUMN billing_account_discriminator VARCHAR(50);
                                        
        EXECUTE 'UPDATE  '||v_schema_name||'.payment_record '
                ||'SET     payment_method_discriminator = ''LeasePaymentMethod'','
                ||'billing_account_discriminator = ''Internal''';
                
                                                
        
        
        -- payment_record_external
        
        CREATE TABLE payment_record_external
        (
                id                              BIGINT                  NOT NULL,
                billing_account                 BIGINT                  NOT NULL,
                billing_account_discriminator   VARCHAR(50)             NOT NULL,
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
        
        EXECUTE 'UPDATE '||v_schema_name||'.product SET is_default_catalog_item = FALSE';
        
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
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.site_descriptor$meta_tags (id,owner,value) '
                ||'(SELECT      nextval(''public.site_descriptor$meta_tags_seq'') AS id,'
                ||'             a.id AS owner, b.id AS value '
                ||'FROM         '||v_schema_name||'.site_descriptor a,'||v_schema_name||'.page_meta_tags b)';
        
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
        
        EXECUTE 'UPDATE  '||v_schema_name||'.site_descriptor '
                ||'SET     crm_logo = b.id '
                ||'FROM    (SELECT MAX(id) AS id FROM '||v_schema_name||'.site_image_resource WHERE file_name = ''logo.png'') AS b';
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.portal_image_set (id,locale) '
                ||'(SELECT nextval(''public.portal_image_set_seq''), id '
                ||'FROM    '||v_schema_name||'.available_locale)';
        
        -- For site_descriptor$banner a full blown cartesian product is needed (purpose of which still escapes me)
               
        EXECUTE 'DELETE FROM '||v_schema_name||'.site_descriptor$banner';
        
        ALTER TABLE site_descriptor$banner DROP CONSTRAINT  site_descriptor$banner_value_fk;
        ALTER TABLE site_descriptor$banner ADD CONSTRAINT  site_descriptor$banner_value_fk FOREIGN KEY(value)
                REFERENCES portal_image_set(id);
        
        
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.site_descriptor$banner (id,owner,value) '
                ||'(SELECT nextval(''public.site_descriptor$banner_seq'') AS id,a.id AS owner, b.id AS value '
                ||'FROM    '||v_schema_name||'.site_descriptor a,portal_image_set b )';
        
        -- crm_role$behaviors
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.crm_role$behaviors (id,owner,value) '
                ||'(SELECT nextval(''public.crm_role$behaviors_seq'') AS id, id, ''OrganizationFinancial'' '
                ||'FROM    '||v_schema_name||'.crm_role '
                ||'WHERE   name = ''All'')';
                
        EXECUTE 'INSERT INTO '||v_schema_name||'.crm_role$behaviors (id,owner,value) '
                ||'(SELECT nextval(''public.crm_role$behaviors_seq'') AS id, id, ''OrganizationPolicy'' '
                ||'FROM    '||v_schema_name||'.crm_role '
                ||'WHERE   name = ''All'')';
        
        
        /**
        ***     ================================================================================================
        ***     
        ***             Not null constraints 
        ***
        ***     ================================================================================================
        **/
        
        ALTER TABLE billing_account ALTER COLUMN id_discriminator SET NOT NULL;
        ALTER TABLE billing_bill ALTER COLUMN billing_account_discriminator SET NOT NULL;
        ALTER TABLE billing_invoice_line_item ALTER COLUMN billing_account_discriminator SET NOT NULL;
        ALTER TABLE lease ALTER COLUMN billing_account SET NOT NULL;
        ALTER TABLE lease ALTER COLUMN billing_account_discriminator SET NOT NULL;
        ALTER TABLE payment_method ALTER COLUMN id_discriminator SET NOT NULL;
        ALTER TABLE payment_method ALTER COLUMN customer DROP NOT NULL; 
        ALTER TABLE payment_record ALTER COLUMN billing_account_discriminator SET NOT NULL;
                
        
        /**
        ***     ================================================================================================
        ***     
        ***             Check constraints that were not taken care of yet
        ***
        ***     ================================================================================================
        **/
        
        -- Constraints to drop
        ALTER TABLE application_document_file DROP CONSTRAINT application_document_file_owner_discriminator_d_ck;
        ALTER TABLE available_locale DROP CONSTRAINT available_locale_lang_e_ck;
        ALTER TABLE billing_bill DROP CONSTRAINT billing_bill_bill_type_e_ck;
        ALTER TABLE billing_debit_credit_link DROP CONSTRAINT billing_debit_credit_link_credit_item_discriminator_d_ck;
        ALTER TABLE billing_debit_credit_link DROP CONSTRAINT billing_debit_credit_link_debit_item_discriminator_d_ck;
        ALTER TABLE billing_invoice_line_item DROP CONSTRAINT billing_invoice_line_item_id_discriminator_ck;
        ALTER TABLE gadget_content DROP CONSTRAINT gadget_content_id_discriminator_ck;
        ALTER TABLE home_page_gadget DROP CONSTRAINT home_page_gadget_content_discriminator_d_ck;
        ALTER TABLE identification_document DROP CONSTRAINT identification_document_owner_discriminator_d_ck;
        ALTER TABLE insurance_tenant_sure_transaction DROP CONSTRAINT insurance_tenant_sure_transaction_status_e_ck;
        ALTER TABLE lead DROP CONSTRAINT lead_lease_type_e_ck;
        ALTER TABLE lease DROP CONSTRAINT lease_lease_type_e_ck;
        ALTER TABLE lease DROP CONSTRAINT lease_status_e_ck;
        ALTER TABLE product DROP CONSTRAINT product_feature_type_e_ck;
        ALTER TABLE product_item_type DROP CONSTRAINT product_item_type_feature_type_e_ck;
        ALTER TABLE product_item_type DROP CONSTRAINT product_item_type_service_type_e_ck;
        ALTER TABLE product DROP CONSTRAINT product_service_type_e_ck;
        ALTER TABLE proof_of_employment_document DROP CONSTRAINT proof_of_employment_document_owner_discriminator_d_ck;
        ALTER TABLE site_descriptor DROP CONSTRAINT site_descriptor_skin_e_ck;
        
        -- Constraint to create
        ALTER TABLE application_document_file ADD CONSTRAINT application_document_file_owner_discriminator_d_ck 
                CHECK ((owner_discriminator) IN ('IdentificationDocument', 'InsuranceCertificateDocument', 'ProofOfEmploymentDocument'));
        ALTER TABLE available_locale ADD CONSTRAINT available_locale_lang_e_ck 
                CHECK ((lang) IN ('en', 'en_CA', 'en_GB', 'en_US', 'es', 'fr', 'fr_CA', 'ru', 'zh_CN', 'zh_TW'));
        ALTER TABLE billing_account ADD CONSTRAINT billing_account_id_discriminator_ck CHECK ((id_discriminator) IN ('Internal', 'YardiAccount'));
        ALTER TABLE billing_arrears_snapshot ADD CONSTRAINT billing_arrears_snapshot_billing_account_discriminator_ck 
                CHECK ((id_discriminator = 'LeaseArrearsSnapshot' AND billing_account_discriminator IS NOT NULL) 
                OR (id_discriminator != 'LeaseArrearsSnapshot' AND billing_account_discriminator IS NULL));
        ALTER TABLE billing_arrears_snapshot ADD CONSTRAINT billing_arrears_snapshot_billing_account_discriminator_d_ck 
                CHECK ((billing_account_discriminator)= 'Internal');
        ALTER TABLE billing_bill ADD CONSTRAINT billing_bill_bill_type_e_ck CHECK ((bill_type) IN ('External', 'Final', 'First', 'Regular', 'ZeroCycle'));
        ALTER TABLE billing_bill ADD CONSTRAINT billing_bill_billing_account_discriminator_d_ck CHECK ((billing_account_discriminator)= 'Internal');
        ALTER TABLE billing_debit_credit_link ADD CONSTRAINT billing_debit_credit_link_credit_item_discriminator_d_ck 
                CHECK ((credit_item_discriminator) IN ('AccountCredit', 'CarryforwardCredit', 'DepositRefund', 'Payment', 'ProductCredit', 'YardiPayment', 'YardiReceipt'));
        ALTER TABLE billing_debit_credit_link ADD CONSTRAINT billing_debit_credit_link_debit_item_discriminator_d_ck 
                CHECK ((debit_item_discriminator) IN ('AccountCharge','CarryforwardCharge','Deposit','LatePaymentFee','NSF','PaymentBackOut',
                'ProductCharge','Withdrawal','YardiCharge','YardiReversal'));
        ALTER TABLE billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_billing_account_discriminator_d_ck 
                CHECK ((billing_account_discriminator) IN ('Internal', 'YardiAccount'));
        ALTER TABLE billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_id_discriminator_ck 
                CHECK ((id_discriminator) IN ('AccountCharge', 'AccountCredit', 'CarryforwardCharge', 'CarryforwardCredit', 'Deposit', 'DepositRefund', 'LatePaymentFee',
                'NSF', 'Payment', 'PaymentBackOut', 'ProductCharge', 'ProductCredit', 'Withdrawal', 'YardiCharge', 'YardiPayment', 'YardiReceipt', 'YardiReversal'));
        ALTER TABLE billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_service_type_e_ck 
                CHECK ((service_type) IN ('AirCon', 'BroadbandInternet', 'Cable', 'Electric', 'Fees', 'Fitness', 'Gas', 'Heat', 'HotWater', 'Other', 'Parking', 'Rent',
                'Sewer', 'Telephone', 'Trash', 'Water'));
        ALTER TABLE building ADD CONSTRAINT building_info_building_type_e_ck
                CHECK (info_building_type IN ('agricultural','association','commercial','condo','industrial','military','mixedResidential','other','parkingStorage',
                'residential','seniorHousing','socialHousing'));
        ALTER TABLE communication_person ADD CONSTRAINT communication_person_type_e_ck CHECK ((type) IN ('CrmUser', 'CustomerUser'));
        ALTER TABLE deposit_lifecycle ADD CONSTRAINT deposit_lifecycle_billing_account_discriminator_d_ck CHECK ((billing_account_discriminator)= 'Internal');
        ALTER TABLE gadget_content ADD CONSTRAINT gadget_content_id_discriminator_ck 
                CHECK ((id_discriminator) IN ('Custom', 'News', 'Promo', 'QuickSearch', 'Testimonials'));
        ALTER TABLE home_page_gadget ADD CONSTRAINT home_page_gadget_content_discriminator_d_ck 
                CHECK ((content_discriminator) IN ('Custom', 'News', 'Promo', 'QuickSearch', 'Testimonials'));
        ALTER TABLE identification_document ADD CONSTRAINT identification_document_owner_discriminator_d_ck 
                CHECK ((owner_discriminator) IN ('CustomerScreening', 'CustomerScreeningIncome', 'InsuranceCertificate'));
        ALTER TABLE insurance_tenant_sure_transaction ADD CONSTRAINT insurance_tenant_sure_transaction_status_e_ck 
                CHECK ((status) IN ('Authorized', 'Cleared', 'Draft', 'PaymentRejected', 'Rejected', 'Reversal'));
        ALTER TABLE lead ADD CONSTRAINT lead_lease_type_e_ck CHECK ((lease_type) IN ('commercialUnit', 'residentialShortTermUnit', 'residentialUnit'));
        ALTER TABLE lease_adjustment ADD CONSTRAINT lease_adjustment_billing_account_discriminator_d_ck CHECK ((billing_account_discriminator)= 'Internal');
        ALTER TABLE lease ADD CONSTRAINT lease_billing_account_discriminator_d_ck CHECK ((billing_account_discriminator) IN ('Internal', 'YardiAccount'));
        ALTER TABLE lease ADD CONSTRAINT lease_lease_type_e_ck CHECK ((lease_type) IN ('commercialUnit', 'residentialShortTermUnit', 'residentialUnit'));
        ALTER TABLE lease_participant ADD CONSTRAINT lease_participant_preauthorized_payment_discriminator_d_ck 
                CHECK ((preauthorized_payment_discriminator)= 'LeasePaymentMethod');
        ALTER TABLE lease ADD CONSTRAINT lease_status_e_ck 
                CHECK (status IN ('Active', 'Application', 'Approved', 'Cancelled', 'Closed', 'Completed', 'ExistingLease', 'NewLease'));
        ALTER TABLE payment_method ADD CONSTRAINT payment_method_customer_ck 
                CHECK ((id_discriminator = 'LeasePaymentMethod' AND customer IS NOT NULL) 
                OR (id_discriminator != 'LeasePaymentMethod' AND customer IS NULL));
        ALTER TABLE payment_method ADD CONSTRAINT payment_method_id_discriminator_ck CHECK ((id_discriminator) IN ('InsurancePaymentMethod', 'LeasePaymentMethod'));
        ALTER TABLE payment_method ADD CONSTRAINT payment_method_tenant_ck 
                CHECK ((id_discriminator = 'InsurancePaymentMethod' AND tenant IS NOT NULL) 
                OR (id_discriminator != 'InsurancePaymentMethod' AND tenant IS NULL));
        ALTER TABLE payment_method ADD CONSTRAINT payment_method_tenant_discriminator_ck 
                CHECK ((id_discriminator = 'InsurancePaymentMethod' AND tenant_discriminator IS NOT NULL) 
                OR (id_discriminator != 'InsurancePaymentMethod' AND tenant_discriminator IS NULL));
        ALTER TABLE payment_method ADD CONSTRAINT payment_method_tenant_discriminator_d_ck CHECK ((tenant_discriminator)= 'Tenant');
        ALTER TABLE payment_record ADD CONSTRAINT payment_record_billing_account_discriminator_d_ck CHECK ((billing_account_discriminator) IN ('Internal', 'YardiAccount'));
        ALTER TABLE payment_record_external ADD CONSTRAINT payment_record_external_billing_account_discriminator_d_ck CHECK ((billing_account_discriminator)= 'Internal');
        ALTER TABLE payment_record ADD CONSTRAINT payment_record_payment_method_discriminator_d_ck CHECK ((payment_method_discriminator)= 'LeasePaymentMethod');
        ALTER TABLE product ADD CONSTRAINT product_feature_type_e_ck CHECK ((feature_type) IN ('addOn', 'booking', 'locker', 'oneTimeCharge', 'parking', 'pet', 'utility'));
        ALTER TABLE product_item_type ADD CONSTRAINT product_item_type_feature_type_e_ck 
                CHECK ((feature_type) IN ('addOn', 'booking', 'locker', 'oneTimeCharge', 'parking', 'pet', 'utility'));
        ALTER TABLE product_item_type ADD CONSTRAINT product_item_type_service_type_e_ck 
                CHECK ((service_type) IN ('commercialUnit', 'residentialShortTermUnit', 'residentialUnit'));
        ALTER TABLE product ADD CONSTRAINT product_service_type_e_ck CHECK ((service_type) IN ('commercialUnit', 'residentialShortTermUnit', 'residentialUnit'));
        ALTER TABLE proof_of_employment_document ADD CONSTRAINT proof_of_employment_document_owner_discriminator_d_ck 
                CHECK ((owner_discriminator) IN ('CustomerScreening', 'CustomerScreeningIncome', 'InsuranceCertificate'));
        ALTER TABLE site_descriptor ADD CONSTRAINT site_descriptor_skin_e_ck CHECK ((skin) IN ('crm', 'skin1', 'skin2', 'skin3', 'skin4', 'skin5'));

        /**
        ***     ========================================================================================================================================
        ***
        ***             Indexes overlooked
        ***
        ***     ========================================================================================================================================
        **/
        
        CREATE INDEX insurance_tenant_sure_details$taxes_owner_idx ON insurance_tenant_sure_details$taxes USING btree (owner);
        CREATE INDEX billing_arrears_snapshot_billing_account_discriminator_idx ON billing_arrears_snapshot USING btree (billing_account_discriminator);
        CREATE INDEX payment_method_tenant_discriminator_idx ON payment_method USING btree (tenant_discriminator);
        CREATE INDEX payment_method_tenant_idx ON payment_method USING btree (tenant);

     
        /** Finishing touch - update _admin_.admin_pmc **/

        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.0.6'
        WHERE   namespace = v_schema_name;
END;      
$$
LANGUAGE plpgsql VOLATILE;

