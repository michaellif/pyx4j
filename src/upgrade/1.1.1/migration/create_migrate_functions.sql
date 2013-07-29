/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             version 1.1.1 PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_111(v_schema_name TEXT) RETURNS VOID AS
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
        ALTER TABLE page_content DROP CONSTRAINT page_content_image_fk;
        ALTER TABLE portal_image_resource DROP CONSTRAINT portal_image_resource_image_resource_fk;
        ALTER TABLE portal_image_resource DROP CONSTRAINT portal_image_resource_locale_fk;
        ALTER TABLE site_descriptor$logo DROP CONSTRAINT site_descriptor$logo_value_fk;

        
        -- check constraints
        
        ALTER TABLE email_template DROP CONSTRAINT email_template_template_type_e_ck;
        ALTER TABLE recipient DROP CONSTRAINT recipient_recipient_type_e_ck;
        
        
        -- primary keys
        
        ALTER TABLE portal_image_resource DROP CONSTRAINT portal_image_resource_pk;
        
        
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
        
        -- aggregated_transfer
        
        ALTER TABLE aggregated_transfer ADD COLUMN funds_transfer_type VARCHAR(50);
        
        -- application_document_file
        
        ALTER TABLE application_document_file   ADD COLUMN caption VARCHAR(500),
                                                ADD COLUMN description VARCHAR(500);
                                                
                                                
        -- company
        
        ALTER TABLE company     ADD COLUMN logo_media_file_caption VARCHAR(500),
                                ADD COLUMN logo_media_file_description  VARCHAR(500);
                                
                                
        -- customer_picture
        
        CREATE TABLE customer_picture
        (
                id                              BIGINT                  NOT NULL,
                file_name                       VARCHAR(500),
                updated_timestamp               BIGINT,
                cache_version                   INT,
                file_size                       INT,
                content_mime_type               VARCHAR(500),
                caption                         VARCHAR(500),
                description                     VARCHAR(500),
                blob_key                        BIGINT,
                customer                        BIGINT,
                order_id                        INT,
                        CONSTRAINT customer_picture_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE customer_picture OWNER TO vista;
                                
        -- emergency_contact
        
        ALTER TABLE emergency_contact   ADD COLUMN address_street1 VARCHAR(500),
                                        ADD COLUMN address_street2 VARCHAR(500);
                                        
                                        
        -- file
        
        ALTER TABLE file        ADD COLUMN caption VARCHAR(500),
                                ADD COLUMN description VARCHAR(500);   
        
        
        -- maintenance_request_schedule
        
        CREATE TABLE maintenance_request_schedule
        (
                id                              BIGINT                  NOT NULL,
                request                         BIGINT                  NOT NULL,
                scheduled_date                  DATE,
                scheduled_time_from             TIME,
                scheduled_time_to               TIME,
                progress_note                   VARCHAR(500),
                notice_of_entry_created         TIMESTAMP,
                notice_of_entry_text            VARCHAR(500),
                notice_of_entry_message_id      VARCHAR(500),
                        CONSTRAINT maintenance_request_schedule_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE maintenance_request_schedule OWNER TO vista;
        
        
        -- media 
        
        ALTER TABLE media       ADD COLUMN media_file_caption VARCHAR(500),
                                ADD COLUMN media_file_description VARCHAR(500);
                                
     
              
        -- notice_of_entry
        
        CREATE TABLE notice_of_entry
        (
                id                              BIGINT                  NOT NULL,
                created                         TIMESTAMP,
                text                            VARCHAR(500),
                message_id                      VARCHAR(500),
                        CONSTRAINT notice_of_entry_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE notice_of_entry OWNER TO vista;
        
        
        -- payment_information
        
        ALTER TABLE payment_information ADD COLUMN payment_method_creator BIGINT,
                                        ADD COLUMN payment_method_creator_discriminator VARCHAR(50);
                                        
                                        
        -- payment_method
        
        ALTER TABLE payment_method      ADD COLUMN creator BIGINT,
                                        ADD COLUMN creator_discriminator VARCHAR(50);
                                        
        -- preauthorized_payment
        
        ALTER TABLE preauthorized_payment       ADD COLUMN creator BIGINT,
                                                ADD COLUMN creator_discriminator VARCHAR(50);                                 
        
        -- portal_image_resource
        
        ALTER TABLE portal_image_resource RENAME TO portal_logo_image_resource;
        ALTER TABLE portal_logo_image_resource RENAME COLUMN image_resource TO large;                    
        ALTER TABLE portal_logo_image_resource  ADD COLUMN small BIGINT;
                                               
        
                               
        -- site_image_resource
        
        ALTER TABLE site_image_resource ADD COLUMN caption VARCHAR(500),
                                        ADD COLUMN description VARCHAR(500);
                                        
        -- site_descriptor$pmc_info
        
        CREATE TABLE site_descriptor$pmc_info
        (
                id                      BIGINT                  NOT NULL,
                owner                   BIGINT,
                value                   BIGINT,
                seq                     INT,
                        CONSTRAINT site_descriptor$pmc_info_pk PRIMARY KEY(id)
                
        );
        
        ALTER TABLE site_descriptor$pmc_info OWNER TO vista;
                                        
                                        
        -- vendor
        
        ALTER TABLE vendor   ADD COLUMN logo_media_file_caption VARCHAR(500),
                             ADD COLUMN logo_media_file_description  VARCHAR(500);   
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        -- _admin_.global_crm_user_index
        
        EXECUTE 'INSERT INTO _admin_.global_crm_user_index(id,pmc,crm_user,email) '
                ||'(SELECT      nextval(''public.global_crm_user_index_seq'') AS id,'
                ||'             a.id AS pmc, u.id AS crm_user, u.email '
                ||'FROM         _admin_.admin_pmc a '
                ||'JOIN         '||v_schema_name||'.crm_user u ON (a.namespace = '||quote_literal(v_schema_name)||') '
                ||'WHERE        u.email != ''support@propertyvista.com'' )'; 
        
        
        -- aggregated_transfer
        
        EXECUTE 'UPDATE '||v_schema_name||'.aggregated_transfer '
                ||'SET  funds_transfer_type = ''PreAuthorizedDebit'' ';
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        -- communication_favorited_messages
        
        DROP TABLE communication_favorited_messages;
        
        -- emergency_contact
        
        ALTER TABLE emergency_contact   DROP COLUMN address_county,
                                        DROP COLUMN address_location_lat,
                                        DROP COLUMN address_location_lng,
                                        DROP COLUMN address_street_direction,
                                        DROP COLUMN address_street_name,
                                        DROP COLUMN address_street_number,
                                        DROP COLUMN address_street_number_suffix,
                                        DROP COLUMN address_street_type,
                                        DROP COLUMN address_suite_number;
                                        
        -- lease_term_participant
        
        ALTER TABLE lease_term_participant DROP COLUMN percentage;
        
        
        -- maintenance_request
        
        ALTER TABLE maintenance_request DROP COLUMN scheduled_date,
                                        DROP COLUMN scheduled_time;

        -- message
        
        ALTER TABLE message DROP COLUMN message_type;
        
        -- page_content
        
        ALTER TABLE page_content DROP COLUMN image;
        
        -- portal_preferences
        
        DROP TABLE portal_preferences;      
                 
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        
        -- primary key
        
        ALTER TABLE portal_logo_image_resource ADD CONSTRAINT portal_logo_image_resource_pk PRIMARY KEY(id);
        
        -- foreign key
        
        ALTER TABLE customer_picture ADD CONSTRAINT customer_picture_customer_fk FOREIGN KEY(customer) REFERENCES customer(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE maintenance_request_schedule ADD CONSTRAINT maintenance_request_schedule_request_fk FOREIGN KEY(request) REFERENCES maintenance_request(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE portal_logo_image_resource ADD CONSTRAINT portal_logo_image_resource_large_fk FOREIGN KEY(large) REFERENCES site_image_resource(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE portal_logo_image_resource ADD CONSTRAINT portal_logo_image_resource_locale_fk FOREIGN KEY(locale) REFERENCES available_locale(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE portal_logo_image_resource ADD CONSTRAINT portal_logo_image_resource_small_fk FOREIGN KEY(small) REFERENCES site_image_resource(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$logo ADD CONSTRAINT site_descriptor$logo_value_fk FOREIGN KEY(value) REFERENCES portal_logo_image_resource(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$pmc_info ADD CONSTRAINT site_descriptor$pmc_info_owner_fk FOREIGN KEY(owner) REFERENCES site_descriptor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$pmc_info ADD CONSTRAINT site_descriptor$pmc_info_value_fk FOREIGN KEY(value) REFERENCES html_content(id)  DEFERRABLE INITIALLY DEFERRED;


        
        -- check constraints
        
        ALTER TABLE aggregated_transfer ADD CONSTRAINT aggregated_transfer_funds_transfer_type_e_ck 
                CHECK ((funds_transfer_type) IN ('DirectBankingPayment', 'InteracOnlinePayment', 'PreAuthorizedDebit'));
        ALTER TABLE email_template ADD CONSTRAINT email_template_template_type_e_ck 
                CHECK ((template_type) IN ('ApplicationApproved', 'ApplicationCreatedApplicant', 'ApplicationCreatedCoApplicant', 'ApplicationCreatedGuarantor', 
                'ApplicationDeclined', 'MaintenanceRequestCompleted', 'MaintenanceRequestCreatedPMC', 'MaintenanceRequestCreatedTenant', 'MaintenanceRequestEntryNotice', 
                'MaintenanceRequestUpdated', 'PasswordRetrievalCrm', 'PasswordRetrievalProspect', 'PasswordRetrievalTenant', 'TenantInvitation'));
        ALTER TABLE payment_information ADD CONSTRAINT payment_information_payment_method_creator_discriminator_d_ck 
                CHECK ((payment_method_creator_discriminator) IN ('CrmUser', 'CustomerUser'));
        ALTER TABLE payment_method ADD CONSTRAINT payment_method_creator_discriminator_d_ck CHECK ((creator_discriminator) IN ('CrmUser', 'CustomerUser'));
        ALTER TABLE preauthorized_payment ADD CONSTRAINT preauthorized_payment_creator_discriminator_d_ck CHECK ((creator_discriminator) IN ('CrmUser', 'CustomerUser'));
        ALTER TABLE recipient ADD CONSTRAINT recipient_recipient_type_e_ck CHECK ((recipient_type) IN ('company', 'group', 'person'));
  
        
       
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        CREATE UNIQUE INDEX id_assignment_item_policy_target_idx ON id_assignment_item USING btree (policy, target);
        CREATE INDEX maintenance_request_schedule_request_idx ON maintenance_request_schedule USING btree (request);
        CREATE INDEX site_descriptor$pmc_info_owner_idx ON site_descriptor$pmc_info USING btree (owner);
        
        
        -- Finishing touch
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.1.1',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;

        
