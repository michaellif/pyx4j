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
       
        
        -- check constraints
        ALTER TABLE recipient DROP CONSTRAINT recipient_recipient_type_e_ck;
       
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
                                
                                
        -- site_image_resource
        
        ALTER TABLE site_image_resource ADD COLUMN caption VARCHAR(500),
                                        ADD COLUMN description VARCHAR(500);
                                        
                                        
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
        
        
        
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        -- communication_favorited_messages
        
        DROP TABLE communication_favorited_messages;
        
        
        -- message
        
        ALTER TABLE message DROP COLUMN message_type;
        
        
        -- portal_preferences
        
        DROP TABLE portal_preferences;      
                 
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        -- foreign key
        
        

        
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
        
        
        -- Finishing touch
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.1.1',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;

        
