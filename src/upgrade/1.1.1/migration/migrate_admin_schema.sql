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
        
        ALTER TABLE audit_record ADD COLUMN world_time TIMESTAMP; 
        
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
        
        -- check constraints
        
        ALTER TABLE admin_pmc_dns_name ADD CONSTRAINT admin_pmc_dns_name_target_e_ck 
                CHECK ((target) IN ('field', 'prospectPortal', 'resident', 'residentPortal', 'vistaCrm'));
        ALTER TABLE audit_record ADD CONSTRAINT audit_record_app_e_ck 
                CHECK ((app) IN ('crm', 'field', 'onboarding', 'operations', 'prospect', 'resident', 'residentPortal'));
        ALTER TABLE operations_alert ADD CONSTRAINT operations_alert_app_e_ck 
                CHECK ((app) IN ('crm', 'field', 'onboarding', 'operations', 'prospect', 'resident', 'residentPortal'));

                
        /**
        ***     ============================================================================================================
        ***     
        ***             CREATE INDEXES
        ***
        ***     ============================================================================================================
        **/
        
        
       


COMMIT;

SET client_min_messages = 'notice';
