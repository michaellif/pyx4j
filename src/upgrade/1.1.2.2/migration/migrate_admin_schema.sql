/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             _admin_ schema changes for v. 1.1.2.2
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
        
        ALTER TABLE vista_terms DROP CONSTRAINT vista_terms_target_e_ck;
        
        
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
        
        -- dev_card_service_simulation_merchant_account
        
        ALTER TABLE dev_card_service_simulation_merchant_account        ADD COLUMN visa_credit_convenience_fee NUMERIC(18,4),
                                                                        ADD COLUMN master_card_convenience_fee NUMERIC(18,4),
                                                                        ADD COLUMN visa_debit_convenience_fee NUMERIC(18,4);
                                                                        
                                                                        
        -- dev_card_service_simulation_transaction
        
        ALTER TABLE dev_card_service_simulation_transaction ADD COLUMN convenience_fee NUMERIC(18,2);
        
        
        -- card_transaction_record
        
        CREATE TABLE card_transaction_record
        (
                id                              BIGINT                          NOT NULL,
                merchant_terminal_id            VARCHAR(8),
                payment_transaction_id          VARCHAR(60),
                card_type                       VARCHAR(50),
                amount                          NUMERIC(18,2),
                fee_amount                      NUMERIC(18,2),
                sale_response_code              VARCHAR(500),
                sale_response_text              VARCHAR(500),
                fee_response_code               VARCHAR(500),
                creation_date                   TIMESTAMP,
                        CONSTRAINT card_transaction_record_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE card_transaction_record OWNER TO vista;
        
        
        -- dev_card_service_simulation_card
        
        ALTER TABLE dev_card_service_simulation_card ADD COLUMN credit_limit NUMERIC(18,2);
        
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
        
        ALTER TABLE card_transaction_record ADD CONSTRAINT card_transaction_record_card_type_e_ck 
                CHECK ((card_type) IN ('MasterCard', 'Visa', 'VisaDebit'));
        ALTER TABLE vista_terms ADD CONSTRAINT vista_terms_target_e_ck 
                CHECK ((target) IN ('PMC', 'PmcCaledonSoleProprietorshipSection', 'PmcCaledonTemplate', 'PmcPaymentPad', 'Tenant', 
                'TenantBilling', 'TenantPaymentConvenienceFee', 'TenantPaymentCreditCard', 'TenantPaymentPad', 
                'TenantPreAuthorizedPaymentsAgreement', 'TenantSurePreAuthorizedPaymentsAgreement'));

                
        /**
        ***     ============================================================================================================
        ***     
        ***             CREATE INDEXES
        ***
        ***     ============================================================================================================
        **/
        
        


       


COMMIT;

SET client_min_messages = 'notice';
