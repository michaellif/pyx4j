/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             _admin_ schema changes for v. 1.4.0.1
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
        
        -- cards_clearance_file
        
        CREATE TABLE cards_clearance_file
        (
            id                          BIGINT                  NOT NULL,
            file_name                   VARCHAR(500),
            remote_file_date            TIMESTAMP,
            received                    TIMESTAMP,
                CONSTRAINT cards_clearance_file_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE cards_clearance_file OWNER TO vista;
        
        
        -- cards_clearance_record
        
        CREATE TABLE cards_clearance_record
        (
            id                          BIGINT                  NOT NULL,
            file                        BIGINT                  NOT NULL,
            merchant_id                 VARCHAR(500),
            merchant_account            BIGINT,
            status                      VARCHAR(50),
            convenience_fee_account     BOOLEAN,
            reference_number            VARCHAR(500),
            clearance_date              TIMESTAMP,
            amount                      NUMERIC(18,2),
                CONSTRAINT cards_clearance_record_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE cards_clearance_record OWNER TO vista;
        
        /**
        ***     ============================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     ============================================================================================================
        **/
        
        
        
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
        
        ALTER TABLE cards_clearance_record ADD CONSTRAINT cards_clearance_record_file_fk FOREIGN KEY(file) 
            REFERENCES cards_clearance_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE cards_clearance_record ADD CONSTRAINT cards_clearance_record_merchant_account_fk FOREIGN KEY(merchant_account) 
            REFERENCES admin_pmc_merchant_account_index(id)  DEFERRABLE INITIALLY DEFERRED;

        
        -- check constraints
        
        ALTER TABLE cards_clearance_record ADD CONSTRAINT cards_clearance_record_status_e_ck CHECK ((status) IN ('Processed', 'Received'));
        
        
        /**
        ***     ============================================================================================================
        ***     
        ***             CREATE INDEXES
        ***
        ***     ============================================================================================================
        **/
        
        CREATE INDEX cards_clearance_record_merchant_account_idx ON cards_clearance_record USING btree (merchant_account);
        CREATE INDEX cards_clearance_record_file_idx ON cards_clearance_record USING btree (file);


       


COMMIT;

SET client_min_messages = 'notice';
