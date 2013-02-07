/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             _admin_ schema changes
***
***     =====================================================================================================================
**/

SET client_min_messages = 'error';

BEGIN TRANSACTION;

SET search_path = '_admin_';

        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP CONSTRAINTS SECTION
        ***
        ***     ======================================================================================================
        **/
        
        -- Check constraints to drop
        
        ALTER TABLE scheduler_trigger DROP CONSTRAINT scheduler_trigger_trigger_type_e_ck;
        
        
        /**
        ***     =======================================================================================================
        ***
        ***             NEW AND ALTERED TABLES 
        ***
        ***     =======================================================================================================
        **/
        
        -- admin_pmc_vista_features
        
        ALTER TABLE admin_pmc_vista_features ADD COLUMN tenant_sure_integration BOOLEAN;
        
        /** simulation tables - not really needed for production **/
        
        -- dev_card_service_simulation
        
        CREATE TABLE dev_card_service_simulation
        (
                id                              BIGINT                  NOT NULL,
                card_type                       VARCHAR(50),
                number                          VARCHAR(500),
                expiry_date                     DATE,
                balance                         NUMERIC(18,2),
                response_code                   VARCHAR(500),
                        CONSTRAINT      dev_card_service_simulation_pk PRIMARY KEY(id)
        );
        
        
        -- dev_card_service_simulation_token
        
        CREATE TABLE dev_card_service_simulation_token
        (
                id                              BIGINT                  NOT NULL,
                card                            BIGINT                  NOT NULL,
                token                           VARCHAR(500),
                odr                             INT,
                        CONSTRAINT      dev_card_service_simulation_token_pk PRIMARY KEY(id)
        );
        
        
        -- dev_card_service_simulation_transaction
        
        CREATE TABLE dev_card_service_simulation_transaction
        (
                id                              BIGINT                  NOT NULL,
                card                            BIGINT                  NOT NULL,
                amount                          NUMERIC(18,2),
                response_code                   VARCHAR(500),
                authorization_number            VARCHAR(500),
                transaction_date                TIMESTAMP WITHOUT TIME ZONE,
                        CONSTRAINT      dev_card_service_simulation_transaction_pk PRIMARY KEY(id)
        );
        
        
        -- dev_equifax_simulator_config
        
        CREATE TABLE dev_equifax_simulator_config
        (
                id                              BIGINT                  NOT NULL,
                approve_xml                     VARCHAR(300000),
                decline_xml                     VARCHAR(300000),
                more_info_xml                   VARCHAR(300000),
                        CONSTRAINT      dev_equifax_simulator_config_pk PRIMARY KEY(id)
        );
        
        /** simulation done **/
                
        
        -- legal_document
        
        ALTER TABLE legal_document ALTER COLUMN content TYPE VARCHAR(300000);
        
        -- tenant_sure_hqupdate_file
        
        CREATE TABLE tenant_sure_hqupdate_file
        (
                id                              BIGINT                  NOT NULL,
                file_name                       VARCHAR(500),
                received                        TIMESTAMP WITHOUT TIME ZONE,
                        CONSTRAINT      tenant_sure_hqupdate_file_pk PRIMARY KEY(id)
        );
        
        
        -- tenant_sure_hqupdate_record
        
        CREATE TABLE tenant_sure_hqupdate_record
        (
                id                              BIGINT                  NOT NULL,
                file                            BIGINT                  NOT NULL,
                certificate_number              VARCHAR(500),
                status                          VARCHAR(50),
                consumed                        BOOLEAN,
                        CONSTRAINT      tenant_sure_hqupdate_record_pk PRIMARY KEY(id)
        );
        
        
        -- tenant_sure_subscribers
        
        CREATE TABLE tenant_sure_subscribers
        (
                id                              BIGINT                  NOT NULL,
                pmc                             BIGINT,
                certificate_number              VARCHAR(500),
                        CONSTRAINT      tenant_sure_subscribers_pk PRIMARY KEY(id)
        );
                
        
        
        /**
        ***     ========================================================================================================
        ***
        ***             CREATE CONSTRAINTS
        ***
        ***     ========================================================================================================
        **/
        
        -- Foreign keys to create
        ALTER TABLE dev_card_service_simulation_token ADD CONSTRAINT dev_card_service_simulation_token_card_fk FOREIGN KEY(card) 
                REFERENCES dev_card_service_simulation(id);
        ALTER TABLE dev_card_service_simulation_transaction ADD CONSTRAINT dev_card_service_simulation_transaction_card_fk FOREIGN KEY(card) 
                REFERENCES dev_card_service_simulation(id);
        ALTER TABLE tenant_sure_hqupdate_record ADD CONSTRAINT tenant_sure_hqupdate_record_file_fk FOREIGN KEY(file) REFERENCES tenant_sure_hqupdate_file(id);
        ALTER TABLE tenant_sure_subscribers ADD CONSTRAINT tenant_sure_subscribers_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id);

                
        -- Check constraint to create
        
        ALTER TABLE dev_card_service_simulation ADD CONSTRAINT dev_card_service_simulation_card_type_e_ck 
                CHECK ((card_type) IN ('MasterCard', 'Visa'));
        
        ALTER TABLE scheduler_trigger ADD CONSTRAINT scheduler_trigger_trigger_type_e_ck 
                CHECK ((trigger_type) IN ('billing', 'cleanup', 'equifaxRetention', 'initializeFutureBillingCycles', 'leaseActivation', 
                'leaseCompletion', 'leaseRenewal', 'paymentsBmoRecive', 'paymentsIssue', 'paymentsPadReciveAcknowledgment', 
                'paymentsPadReciveReconciliation', 'paymentsPadSend', 'paymentsScheduledCreditCards', 'paymentsScheduledEcheck', 
                'paymentsTenantSure', 'tenantSureCancellation', 'tenantSureHQUpdate', 'tenantSureReports', 'test', 'updateArrears', 
                'updatePaymentsSummary', 'yardiBatchProcess', 'yardiImportProcess'));
                
        ALTER TABLE tenant_sure_hqupdate_record ADD CONSTRAINT tenant_sure_hqupdate_record_status_e_ck CHECK (status = 'Cancel');

        /**
        ***     ============================================================================================================
        ***     
        ***             INDEXES
        ***
        ***     ============================================================================================================
        **/
        
        CREATE INDEX tenant_sure_hqupdate_record_file_idx ON tenant_sure_hqupdate_record USING btree (file);
        CREATE INDEX tenant_sure_subscribers_certificate_number_idx ON tenant_sure_subscribers USING btree (certificate_number);
        
        
        /**
        ***     ============================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     ============================================================================================================
        **/
        
        UPDATE  admin_pmc_vista_features
        SET     tenant_sure_integration = TRUE;

COMMIT;

SET client_min_messages = 'notice';
