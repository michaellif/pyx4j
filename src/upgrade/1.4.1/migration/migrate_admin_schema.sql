/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             _admin_ schema changes for v. 1.4.1
***
***     =====================================================================================================================
**/

SET client_min_messages = 'error';

BEGIN TRANSACTION;

SET search_path = '_admin_';

        /**
        ***     ======================================================================================================
        ***
        ***             DROP TABLE SECTION
        ***
        ***     ======================================================================================================
        **/




        /**
        ***     ======================================================================================================
        ***
        ***             DROP CONSTRAINTS SECTION
        ***
        ***     ======================================================================================================
        **/


        /**
        ***     ======================================================================================================
        ***
        ***             DROP INDEXES
        ***
        ***     ======================================================================================================
        **/

       
        /**
        ***     =======================================================================================================
        ***
        ***             RENAMED TABLES
        ***
        ***     =======================================================================================================
        **/



        /**
        ***     =======================================================================================================
        ***
        ***             NEW AND ALTERED TABLES
        ***
        ***     =======================================================================================================
        **/

        -- admin_pmc_yardi_credential
        
        ALTER TABLE admin_pmc_yardi_credential  ADD COLUMN created TIMESTAMP,
                                                ADD COLUMN updated TIMESTAMP,
                                                ADD COLUMN enabled BOOLEAN;
                                                
                                                
        -- cards_clearance_record
        
        ALTER TABLE cards_clearance_record ADD COLUMN card_type VARCHAR(50);
        
        -- oapi_conversion
        
        CREATE TABLE oapi_conversion
        (
            id                          BIGINT              NOT NULL,
            created                     DATE,
            name                        VARCHAR(500),
            tp                          VARCHAR(50),
            description                 VARCHAR(500),
                CONSTRAINT oapi_conversion_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE oapi_conversion OWNER TO vista;
        

        -- oapi_conversion_blob
        
        CREATE TABLE oapi_conversion_blob
        (
            id                          BIGINT              NOT NULL,
            name                        VARCHAR(500),
            content_type                VARCHAR(500),
            updated                     TIMESTAMP,
            created                     TIMESTAMP,
            data                        BYTEA,
                CONSTRAINT oapi_conversion_blob_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE oapi_conversion_blob OWNER TO vista;
        

        -- oapi_conversion_file
        
        CREATE TABLE oapi_conversion_file
        (
            id                          BIGINT              NOT NULL,
            oapi                        BIGINT,
            tp                          VARCHAR(50),
            file_file_name              VARCHAR(500),
            file_updated_timestamp      BIGINT,
            file_cache_version          INTEGER,
            file_file_size              INTEGER,
            file_content_mime_type      VARCHAR(500),
            file_blob_key               BIGINT,
                CONSTRAINT oapi_conversion_file_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE oapi_conversion_file OWNER TO vista;
        
        
        -- portal_resident_marketing_tip
        
        CREATE TABLE portal_resident_marketing_tip
        (
            id                          BIGINT              NOT NULL,
            created                     TIMESTAMP,
            updated                     TIMESTAMP,
            target                      VARCHAR(50),
            comments                    VARCHAR(500),
            content                     VARCHAR(300000),
                CONSTRAINT portal_resident_marketing_tip_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE portal_resident_marketing_tip OWNER TO vista;
        
        
        /**
        ***     ============================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     ============================================================================================================
        **/

        -- admin_pmc_yardi_credential
        
        UPDATE  admin_pmc_yardi_credential
        SET     enabled = TRUE;
       

        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/




        /**
        ***     ========================================================================================================
        ***
        ***             CREATE CONSTRAINTS
        ***
        ***     ========================================================================================================
        **/

        -- foreign keys
        
        ALTER TABLE oapi_conversion_file ADD CONSTRAINT oapi_conversion_file_oapi_fk FOREIGN KEY(oapi) 
            REFERENCES oapi_conversion(id)  DEFERRABLE INITIALLY DEFERRED;

       

        -- check constraints
        
        ALTER TABLE cards_clearance_record ADD CONSTRAINT cards_clearance_record_card_type_e_ck 
            CHECK ((card_type) IN ('CREDIT', 'MCRD', 'VISA'));
        ALTER TABLE oapi_conversion ADD CONSTRAINT oapi_conversion_tp_e_ck CHECK (tp = 'Base');
        ALTER TABLE oapi_conversion_file ADD CONSTRAINT oapi_conversion_file_tp_e_ck 
            CHECK ((tp) IN ('AnotherIO', 'BuildingIO'));
        ALTER TABLE portal_resident_marketing_tip ADD CONSTRAINT portal_resident_marketing_tip_target_e_ck 
            CHECK ((target) IN ('AutopayAgreementNotSetup', 'InsuranceMissing', 'Other'));


        /**
        ***     ============================================================================================================
        ***
        ***             CREATE INDEXES
        ***
        ***     ============================================================================================================
        **/



COMMIT;

SET client_min_messages = 'notice';
