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
        ALTER TABLE nsf_fee_item DROP CONSTRAINT nsf_fee_item_payment_type_e_ck;
        ALTER TABLE payment_information DROP CONSTRAINT payment_information_payment_method_details_discriminator_d_ck;
        ALTER TABLE payment_information DROP CONSTRAINT payment_information_payment_method_payment_type_e_ck;
        ALTER TABLE payment_information DROP CONSTRAINT payment_information_payment_method_billing_addr_str_dir_e_ck;
        ALTER TABLE payment_information DROP CONSTRAINT payment_information_payment_method_billing_addr_str_type_e_ck;
        ALTER TABLE payment_method DROP CONSTRAINT payment_method_billing_address_street_direction_e_ck;
        ALTER TABLE payment_method DROP CONSTRAINT payment_method_billing_address_street_type_e_ck;
        ALTER TABLE payment_method DROP CONSTRAINT payment_method_details_discriminator_d_ck;
        ALTER TABLE payment_method DROP CONSTRAINT payment_method_payment_type_e_ck;
        ALTER TABLE payment_payment_details DROP CONSTRAINT payment_payment_details_id_discriminator_ck;
        ALTER TABLE payment_payment_details DROP CONSTRAINT payment_payment_details_card_type_e_ck;
        ALTER TABLE recipient DROP CONSTRAINT recipient_recipient_type_e_ck;
        
        
        -- primary keys
        
        ALTER TABLE portal_image_resource DROP CONSTRAINT portal_image_resource_pk;
        
        
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
        
        
        -- insurance_certificate
        
        ALTER TABLE insurance_certificate ADD COLUMN payment_schedule VARCHAR(50);
        
        
        --  maintenance_request
        
        ALTER TABLE  maintenance_request ADD COLUMN cancellation_note VARCHAR(2048);
        
        
        -- maintenance_request_category
       
        ALTER TABLE maintenance_request_category ADD COLUMN root BIGINT;
        
        
        -- maintenance_request_metadata
        
        CREATE TABLE maintenance_request_metadata
        (
                id                              BIGINT                  NOT NULL,
                root_category                   BIGINT,
                        CONSTRAINT maintenance_request_metadata_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE maintenance_request_metadata OWNER TO vista;
        
        
        -- maintenance_request_priority
        
        ALTER TABLE maintenance_request_priority ADD COLUMN meta BIGINT;
        
        
        -- maintenance_request_status
        
        
        ALTER TABLE maintenance_request_status ADD COLUMN meta BIGINT;
        
        
        -- maintenance_request_schedule
        
        CREATE TABLE maintenance_request_schedule
        (
                id                              BIGINT                  NOT NULL,
                request                         BIGINT                  NOT NULL,
                scheduled_date                  DATE,
                scheduled_time_from             TIME,
                scheduled_time_to               TIME,
                work_description                VARCHAR(500),
                progress_note                   VARCHAR(500),
                notice_of_entry_message_date    VARCHAR(500),
                notice_of_entry_text            VARCHAR(10000),
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
                message_date                    VARCHAR(500),
                text                            VARCHAR(10000),
                message_id                      VARCHAR(500),
                        CONSTRAINT notice_of_entry_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE notice_of_entry OWNER TO vista;
        
        
        -- payment_information
        
        ALTER TABLE payment_information ADD COLUMN payment_method_creator BIGINT,
                                        ADD COLUMN payment_method_creation_date TIMESTAMP,
                                        ADD COLUMN payment_method_creator_discriminator VARCHAR(50),
                                        ADD COLUMN payment_method_billing_address_street1 VARCHAR(500),
                                        ADD COLUMN payment_method_billing_address_street2 VARCHAR(500);
                                        
                                        
        -- payment_method
        
        ALTER TABLE payment_method      ADD COLUMN creator BIGINT,
                                        ADD COLUMN creation_date TIMESTAMP,
                                        ADD COLUMN creator_discriminator VARCHAR(50),
                                        ADD COLUMN billing_address_street1 VARCHAR(500),
                                        ADD COLUMN billing_address_street2 VARCHAR(500);
                                        
        -- payment_payment_details
        
        ALTER TABLE payment_payment_details     ADD COLUMN location_code VARCHAR(500),
                                                ADD COLUMN trace_number VARCHAR(500);
                                                
       
        -- payment_record
       
        ALTER TABLE payment_record       ADD COLUMN creator BIGINT,
                                        ADD COLUMN creator_discriminator VARCHAR(50);
                                        
        ALTER TABLE payment_record ALTER COLUMN created_date TYPE TIMESTAMP;
       
                                        
        -- payment_type_selection_policy
        
        ALTER TABLE payment_type_selection_policy       ADD COLUMN accepted_credit_card_master_card BOOLEAN,
                                                        ADD COLUMN accepted_credit_card_visa BOOLEAN,
                                                        ADD COLUMN accepted_visa_debit BOOLEAN,
                                                        ADD COLUMN cash_equivalent_credit_card_master_card BOOLEAN,
                                                        ADD COLUMN cash_equivalent_credit_card_visa BOOLEAN,
                                                        ADD COLUMN cash_equivalent_visa_debit BOOLEAN,
                                                        ADD COLUMN resident_portal_credit_card_master_card BOOLEAN,
                                                        ADD COLUMN resident_portal_credit_card_visa BOOLEAN,
                                                        ADD COLUMN resident_portal_visa_debit BOOLEAN;
                                        
        -- preauthorized_payment
        
        ALTER TABLE preauthorized_payment       ADD COLUMN creator BIGINT,
                                                ADD COLUMN creator_discriminator VARCHAR(50);      
                                             
                                             
        ALTER TABLE preauthorized_payment ALTER COLUMN creation_date TYPE TIMESTAMP;                          
        
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
        
        
        -- tax
        
        ALTER TABLE tax ALTER COLUMN rate TYPE NUMERIC(18,4);                               
                                        
        -- vendor
        
        ALTER TABLE vendor   ADD COLUMN logo_media_file_caption VARCHAR(500),
                             ADD COLUMN logo_media_file_description  VARCHAR(500);   
                             
        
        -- yardi_building_origination
        
        CREATE TABLE yardi_building_origination
        (
                id                      BIGINT                  NOT NULL,
                building                BIGINT,
                yardi_interface_id      BIGINT,
                        CONSTRAINT yardi_building_origination_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE yardi_building_origination OWNER TO vista;
        
        -- yardi_maintenance_meta_origination
        
        CREATE TABLE yardi_maintenance_meta_origination
        (
                id                      BIGINT                  NOT NULL,
                metadata                BIGINT,
                yardi_interface_id      BIGINT,
                        CONSTRAINT yardi_maintenance_meta_origination_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE yardi_maintenance_meta_origination OWNER TO vista;
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        -- _admin_.audit_record
        
        UPDATE  _admin_.audit_record 
        SET     namespace = LOWER(namespace);
        
        DELETE FROM _admin_.audit_record 
        WHERE   (namespace NOT IN (SELECT namespace FROM _admin_.admin_pmc)
        AND     namespace != '_admin_');
        
        
        EXECUTE 'UPDATE _admin_.audit_record AS a '
                ||'SET  user_type = ''crm'' '
                ||'FROM '||v_schema_name||'.crm_user u '
                ||'WHERE a.namespace = '||quote_literal(v_schema_name)||' '
                ||'AND  a.usr = u.id ';
              
                               
        EXECUTE 'UPDATE _admin_.audit_record AS a '
                ||'SET  user_type = ''customer'' '
                ||'FROM '||v_schema_name||'.customer_user u '
                ||'WHERE a.namespace = '||quote_literal(v_schema_name)||' '
                ||'AND  a.usr = u.id ';
                
        
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
        
        
       
        
        -- emergency_contact
        EXECUTE 'UPDATE '||v_schema_name||'.emergency_contact '
                ||'SET  address_street1 = address_street_number|| '
                ||'CASE WHEN address_street_number_suffix IS NOT NULL THEN '' ''||address_street_number_suffix ELSE '''' END || '
                ||''' ''||address_street_name || '
                ||'CASE WHEN address_street_type IS NOT NULL AND address_street_type != ''other'' THEN '' ''||address_street_type ELSE '''' END ||'
                ||'CASE WHEN address_street_direction IS NOT NULL THEN '' ''||address_street_direction ELSE '''' END || '
                ||'CASE WHEN  address_suite_number IS NOT NULL THEN '' ''||''Unit ''||address_suite_number END ';
        
               
        -- insurance_certificate
        
        EXECUTE 'UPDATE '||v_schema_name||'.insurance_certificate '
                ||'SET  payment_schedule = ''Monthly'' '
                ||'WHERE        id_discriminator = ''InsuranceTenantSure'' ';
                
        
        -- nsf_fee_item
        
        EXECUTE 'DELETE FROM '||v_schema_name||'.nsf_fee_item';
        
        -- payment_information
        
        EXECUTE 'UPDATE '||v_schema_name||'.payment_information '
                ||'SET  payment_method_billing_address_street1 = payment_method_billing_address_street_number|| '
                ||'CASE WHEN payment_method_billing_address_street_number_suffix IS NOT NULL THEN '' ''||payment_method_billing_address_street_number_suffix ELSE '''' END || '
                ||''' ''||payment_method_billing_address_street_name || '
                ||'CASE WHEN payment_method_billing_address_street_type IS NOT NULL AND payment_method_billing_address_street_type != ''other'' '
                ||'THEN '' ''||payment_method_billing_address_street_type ELSE '''' END ||'
                ||'CASE WHEN payment_method_billing_address_street_direction IS NOT NULL THEN '' ''||payment_method_billing_address_street_direction ELSE '''' END || '
                ||'CASE WHEN payment_method_billing_address_suite_number IS NOT NULL THEN '' ''||''Unit ''||payment_method_billing_address_suite_number END ';
                
        
        -- payment_method
        
        EXECUTE 'UPDATE '||v_schema_name||'.payment_method '
                ||'SET  billing_address_street1 = billing_address_street_number|| '
                ||'CASE WHEN billing_address_street_number_suffix IS NOT NULL THEN '' ''||billing_address_street_number_suffix ELSE '''' END || '
                ||''' ''||billing_address_street_name || '
                ||'CASE WHEN billing_address_street_type IS NOT NULL AND billing_address_street_type != ''other'' THEN '' ''||billing_address_street_type ELSE '''' END ||'
                ||'CASE WHEN billing_address_street_direction IS NOT NULL THEN '' ''||billing_address_street_direction ELSE '''' END || '
                ||'CASE WHEN billing_address_suite_number IS NOT NULL THEN '' ''||''Unit ''||billing_address_suite_number END ';
                
        
        SET CONSTRAINTS payment_method_billing_address_country_fk,payment_method_billing_address_province_fk,
                        payment_method_customer_fk,payment_method_details_fk,payment_method_tenant_fk IMMEDIATE;
        
        
        
        -- yardi_building_origination
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.yardi_building_origination (id,building,yardi_interface_id) '
                ||'(SELECT nextval(''public.yardi_building_origination_seq'') AS id, b.id AS building,y.id AS yardi_interface_id '
                ||'FROM '||v_schema_name||'.building b, _admin_.admin_pmc a, _admin_.admin_pmc_yardi_credential y '
                ||'WHERE a.id = y.pmc '
                ||'AND a.namespace = '''||v_schema_name||''')';
        
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
        
        
        -- payment_information
       
        ALTER TABLE payment_information  DROP COLUMN payment_method_billing_address_county,
                                         DROP COLUMN payment_method_billing_address_location_lat,
                                         DROP COLUMN payment_method_billing_address_location_lng,
                                         DROP COLUMN payment_method_billing_address_street_direction,
                                         DROP COLUMN payment_method_billing_address_street_name,
                                         DROP COLUMN payment_method_billing_address_street_number,
                                         DROP COLUMN payment_method_billing_address_street_number_suffix,
                                         DROP COLUMN payment_method_billing_address_street_type,
                                         DROP COLUMN payment_method_billing_address_suite_number;
        
        -- payment_method
       
        ALTER TABLE payment_method      DROP COLUMN billing_address_county,
                                        DROP COLUMN billing_address_location_lat,
                                        DROP COLUMN billing_address_location_lng,
                                        DROP COLUMN billing_address_street_direction,
                                        DROP COLUMN billing_address_street_name,
                                        DROP COLUMN billing_address_street_number,
                                        DROP COLUMN billing_address_street_number_suffix,
                                        DROP COLUMN billing_address_street_type,
                                        DROP COLUMN billing_address_suite_number;
        
          
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
        ALTER TABLE maintenance_request_category ADD CONSTRAINT maintenance_request_category_root_fk FOREIGN KEY(root) REFERENCES maintenance_request_category(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE maintenance_request_metadata ADD CONSTRAINT maintenance_request_metadata_root_category_fk FOREIGN KEY(root_category) 
                REFERENCES maintenance_request_category(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE maintenance_request_priority ADD CONSTRAINT maintenance_request_priority_meta_fk FOREIGN KEY(meta) REFERENCES maintenance_request_metadata(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE maintenance_request_status ADD CONSTRAINT maintenance_request_status_meta_fk FOREIGN KEY(meta) REFERENCES maintenance_request_metadata(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE maintenance_request_schedule ADD CONSTRAINT maintenance_request_schedule_request_fk FOREIGN KEY(request) REFERENCES maintenance_request(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE portal_logo_image_resource ADD CONSTRAINT portal_logo_image_resource_large_fk FOREIGN KEY(large) REFERENCES site_image_resource(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE portal_logo_image_resource ADD CONSTRAINT portal_logo_image_resource_locale_fk FOREIGN KEY(locale) REFERENCES available_locale(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE portal_logo_image_resource ADD CONSTRAINT portal_logo_image_resource_small_fk FOREIGN KEY(small) REFERENCES site_image_resource(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$logo ADD CONSTRAINT site_descriptor$logo_value_fk FOREIGN KEY(value) REFERENCES portal_logo_image_resource(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$pmc_info ADD CONSTRAINT site_descriptor$pmc_info_owner_fk FOREIGN KEY(owner) REFERENCES site_descriptor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$pmc_info ADD CONSTRAINT site_descriptor$pmc_info_value_fk FOREIGN KEY(value) REFERENCES html_content(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE yardi_building_origination ADD CONSTRAINT yardi_building_origination_building_fk FOREIGN KEY(building) REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE yardi_maintenance_meta_origination ADD CONSTRAINT yardi_maintenance_meta_origination_metadata_fk FOREIGN KEY(metadata) 
                REFERENCES maintenance_request_metadata(id)  DEFERRABLE INITIALLY DEFERRED;


        
        -- check constraints
        
        ALTER TABLE aggregated_transfer ADD CONSTRAINT aggregated_transfer_funds_transfer_type_e_ck 
                CHECK ((funds_transfer_type) IN ('DirectBankingPayment', 'InteracOnlinePayment', 'PreAuthorizedDebit'));
        ALTER TABLE email_template ADD CONSTRAINT email_template_template_type_e_ck 
                CHECK ((template_type) IN ('ApplicationApproved', 'ApplicationCreatedApplicant', 'ApplicationCreatedCoApplicant', 
                'ApplicationCreatedGuarantor', 'ApplicationDeclined', 'MaintenanceRequestCancelled', 'MaintenanceRequestCompleted', 
                'MaintenanceRequestCreatedPMC', 'MaintenanceRequestCreatedTenant', 'MaintenanceRequestEntryNotice', 'MaintenanceRequestUpdated', 
                'PasswordRetrievalCrm', 'PasswordRetrievalProspect', 'PasswordRetrievalTenant', 'TenantInvitation'));
        ALTER TABLE insurance_certificate ADD CONSTRAINT insurance_certificate_payment_schedule_e_ck CHECK ((payment_schedule) IN ('Annual', 'Monthly'));
        ALTER TABLE nsf_fee_item ADD CONSTRAINT nsf_fee_item_payment_type_e_ck CHECK ((payment_type) IN ('Cash', 'Check', 'CreditCard', 'DirectBanking', 'Echeck', 'Interac'));
        ALTER TABLE payment_information ADD CONSTRAINT payment_information_payment_method_details_discriminator_d_ck 
                CHECK ((payment_method_details_discriminator) IN ('CashInfo', 'CheckInfo', 'CreditCard', 'DirectDebit', 'EcheckInfo', 'InteracInfo'));
        ALTER TABLE payment_information ADD CONSTRAINT payment_information_payment_method_payment_type_e_ck 
                CHECK ((payment_method_payment_type) IN ('Cash', 'Check', 'CreditCard', 'DirectBanking', 'Echeck', 'Interac'));
        ALTER TABLE payment_information ADD CONSTRAINT payment_information_payment_method_creator_discriminator_d_ck 
                CHECK ((payment_method_creator_discriminator) IN ('CrmUser', 'CustomerUser'));
         ALTER TABLE payment_method ADD CONSTRAINT payment_method_details_discriminator_d_ck 
                CHECK ((details_discriminator) IN ('CashInfo', 'CheckInfo', 'CreditCard', 'DirectDebit', 'EcheckInfo', 'InteracInfo'));
        ALTER TABLE payment_method ADD CONSTRAINT payment_method_payment_type_e_ck CHECK ((payment_type) IN ('Cash', 'Check', 'CreditCard', 'DirectBanking', 'Echeck', 'Interac'));
        ALTER TABLE payment_method ADD CONSTRAINT payment_method_creator_discriminator_d_ck CHECK ((creator_discriminator) IN ('CrmUser', 'CustomerUser'));
        ALTER TABLE payment_payment_details ADD CONSTRAINT payment_payment_details_card_type_e_ck CHECK ((card_type) IN ('MasterCard', 'Visa', 'VisaDebit'));  
        ALTER TABLE payment_payment_details ADD CONSTRAINT payment_payment_details_id_discriminator_ck 
                CHECK ((id_discriminator) IN ('CashInfo', 'CheckInfo', 'CreditCard', 'DirectDebit', 'EcheckInfo', 'InteracInfo')); 
        ALTER TABLE payment_record ADD CONSTRAINT payment_record_creator_discriminator_d_ck CHECK ((creator_discriminator) IN ('CrmUser', 'CustomerUser'));
        ALTER TABLE preauthorized_payment ADD CONSTRAINT preauthorized_payment_creator_discriminator_d_ck CHECK ((creator_discriminator) IN ('CrmUser', 'CustomerUser'));
        ALTER TABLE recipient ADD CONSTRAINT recipient_recipient_type_e_ck CHECK ((recipient_type) IN ('company', 'group', 'person'));
  
        -- not null
        
        ALTER TABLE aggregated_transfer ALTER COLUMN funds_transfer_type SET NOT NULL;
        -- ALTER TABLE maintenance_request_priority ALTER COLUMN meta SET NOT NULL;
        -- ALTER TABLE maintenance_request_status ALTER COLUMN meta SET NOT NULL;
       
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
        
        
        CREATE UNIQUE INDEX id_assignment_item_policy_target_idx ON id_assignment_item USING btree (policy, target);
        CREATE INDEX maintenance_request_category_parent_idx ON maintenance_request_category USING btree (parent);
        CREATE INDEX maintenance_request_category_root_idx ON maintenance_request_category USING btree (root);
        CREATE INDEX maintenance_request_priority_meta_idx ON maintenance_request_priority USING btree (meta);
        CREATE INDEX maintenance_request_status_meta_idx ON maintenance_request_status USING btree (meta);
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

        
