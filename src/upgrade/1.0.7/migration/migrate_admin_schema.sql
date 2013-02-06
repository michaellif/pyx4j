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
        
        ALTER TABLE tenant_sure_hqupdate_record ADD CONSTRAINT tenant_sure_hqupdate_record_file_fk FOREIGN KEY(file) REFERENCES tenant_sure_hqupdate_file(id);
        ALTER TABLE tenant_sure_subscribers ADD CONSTRAINT tenant_sure_subscribers_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id);

                
        -- Check constraint to create
        
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
