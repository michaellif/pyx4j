/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             _admin_ schema changes for v. 1.1.1
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
        
        -- check constraints
        
        ALTER TABLE admin_pmc_dns_name DROP CONSTRAINT admin_pmc_dns_name_target_e_ck;
        ALTER TABLE audit_record DROP CONSTRAINT audit_record_app_e_ck;
        ALTER TABLE audit_record DROP CONSTRAINT audit_record_event_e_ck;
        ALTER TABLE operations_alert DROP CONSTRAINT operations_alert_app_e_ck;

        
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
        ***             NEW AND ALTERED TABLES 
        ***
        ***     =======================================================================================================
        **/
        
        -- admin_pmc_yardi_credential
        
        ALTER TABLE admin_pmc_yardi_credential ADD COLUMN password_encrypted VARCHAR(1024);
        ALTER TABLE admin_pmc_yardi_credential RENAME COLUMN credential TO password_password;
        
        
        -- audit_record
        
        ALTER TABLE audit_record        ADD COLUMN pmc BIGINT,
                                        ADD COLUMN user_type VARCHAR(50),
                                        ADD COLUMN session_id VARCHAR(500),
                                        ADD COLUMN world_time TIMESTAMP;
                                        
        -- global_crm_user_index
        
        CREATE TABLE global_crm_user_index
        (
                id                      BIGINT                  NOT NULL,
                pmc                     BIGINT,
                crm_user                BIGINT,
                email                   VARCHAR(64),
                        CONSTRAINT global_crm_user_index_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE global_crm_user_index OWNER TO vista;
        
        
        -- onboarding_user
        
        ALTER TABLE onboarding_user     ADD COLUMN pmc BIGINT,
                                        ADD COLUMN password VARCHAR(500);                              
        
        -- pmc_document_file
        
        ALTER TABLE pmc_document_file   ADD COLUMN caption VARCHAR(500),
                                        ADD COLUMN description VARCHAR(500);
       
       
        /**
        ***     ============================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     ============================================================================================================
        **/
        
        -- onboarding_user
        
        UPDATE  onboarding_user AS u
        SET     pmc = c.pmc
        FROM    onboarding_user_credential c
        WHERE   c.usr = u.id;
               
        
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        -- admin_pmc
        
        ALTER TABLE admin_pmc DROP COLUMN onboarding_account_id;
        
        -- admin_pmc_vista_features
        
        ALTER TABLE admin_pmc_vista_features DROP COLUMN xml_site_export;
        
        -- onboarding_user
        
        ALTER TABLE onboarding_user     DROP COLUMN name,
                                        DROP COLUMN updated;
                                        
        -- onboarding_user_credential
        
        DROP TABLE onboarding_user_credential;
       
        
        /**
        ***     ========================================================================================================
        ***
        ***             CREATE CONSTRAINTS
        ***
        ***     ========================================================================================================
        **/
        
        -- foreign keys
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE global_crm_user_index ADD CONSTRAINT global_crm_user_index_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE onboarding_user ADD CONSTRAINT onboarding_user_pmc_fk FOREIGN KEY(pmc) REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;

        -- check constraints
        
        ALTER TABLE admin_pmc_dns_name ADD CONSTRAINT admin_pmc_dns_name_target_e_ck 
                CHECK ((target) IN ('field', 'prospectPortal', 'resident', 'residentPortal', 'vistaCrm'));
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_app_e_ck 
                CHECK ((app) IN ('crm', 'field', 'onboarding', 'operations', 'prospect', 'resident', 'residentPortal'));
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_event_e_ck 
                CHECK ((event) IN ('Create', 'CredentialUpdate', 'EquifaxReadReport', 'EquifaxRequest', 'Info', 'Login', 
                'LoginFailed', 'Logout', 'PermitionsUpdate', 'Read', 'SessionExpiration', 'System', 'Update'));
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_user_type_e_ck CHECK ((user_type) IN ('crm', 'customer', 'operations'));
        ALTER TABLE operations_alert ADD CONSTRAINT operations_alert_app_e_ck 
                CHECK ((app) IN ('crm', 'field', 'onboarding', 'operations', 'prospect', 'resident', 'residentPortal'));

                
        /**
        ***     ============================================================================================================
        ***     
        ***             CREATE INDEXES
        ***
        ***     ============================================================================================================
        **/
        
        CREATE INDEX global_crm_user_index_email_idx ON global_crm_user_index USING btree (email);
        CREATE INDEX global_crm_user_index_pmc_crm_user_idx ON global_crm_user_index USING btree (pmc, crm_user);
        CREATE INDEX onboarding_user_email_idx ON onboarding_user USING btree (lower(email));

       


COMMIT;

SET client_min_messages = 'notice';
