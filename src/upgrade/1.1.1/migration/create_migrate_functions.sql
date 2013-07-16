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
        
        -- application_document_file
        
        ALTER TABLE application_document_file   ADD COLUMN caption VARCHAR(500),
                                                ADD COLUMN description VARCHAR(500);
                                                
                                                
        -- company
        
        ALTER TABLE company     ADD COLUMN logo_media_file_caption VARCHAR(500),
                                ADD COLUMN logo_media_file_description  VARCHAR(500);
                                
        -- emergency_contact
        
        ALTER TABLE emergency_contact   ADD COLUMN address_street1 VARCHAR(500),
                                        ADD COLUMN address_street2 VARCHAR(500);
                                        
                                        
        -- file
        
        ALTER TABLE file        ADD COLUMN caption VARCHAR(500),
                                ADD COLUMN description VARCHAR(500);   
        
        
        -- media 
        
        ALTER TABLE media       ADD COLUMN media_file_caption VARCHAR(500),
                                ADD COLUMN media_file_description VARCHAR(500);
                                
        
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
        
        ALTER TABLE portal_logo_image_resource ADD CONSTRAINT portal_logo_image_resource_large_fk FOREIGN KEY(large) REFERENCES site_image_resource(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE portal_logo_image_resource ADD CONSTRAINT portal_logo_image_resource_locale_fk FOREIGN KEY(locale) REFERENCES available_locale(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE portal_logo_image_resource ADD CONSTRAINT portal_logo_image_resource_small_fk FOREIGN KEY(small) REFERENCES site_image_resource(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$logo ADD CONSTRAINT site_descriptor$logo_value_fk FOREIGN KEY(value) REFERENCES portal_logo_image_resource(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$pmc_info ADD CONSTRAINT site_descriptor$pmc_info_owner_fk FOREIGN KEY(owner) REFERENCES site_descriptor(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_descriptor$pmc_info ADD CONSTRAINT site_descriptor$pmc_info_value_fk FOREIGN KEY(value) REFERENCES html_content(id)  DEFERRABLE INITIALLY DEFERRED;


        
        -- check constraints
        ALTER TABLE recipient ADD CONSTRAINT recipient_recipient_type_e_ck CHECK ((recipient_type) IN ('company', 'group', 'person'));
  
        
       
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        CREATE UNIQUE INDEX id_assignment_item_policy_target_idx ON id_assignment_item USING btree (policy, target);
        CREATE INDEX site_descriptor$pmc_info_owner_idx ON site_descriptor$pmc_info USING btree (owner);
        
        
        -- Finishing touch
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.1.1',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;

        
