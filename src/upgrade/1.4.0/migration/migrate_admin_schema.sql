/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             _admin_ schema changes for v. 1.4.0
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
                
        ALTER TABLE scheduler_trigger DROP CONSTRAINT scheduler_trigger_trigger_type_e_ck;
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP INDEXES 
        ***
        ***     ======================================================================================================
        **/
        
        DROP INDEX admin_pmc_merchant_account_index_merchant_terminal_id_idx;
        DROP INDEX business_information_business_address_country_name_idx;
        DROP INDEX business_information_business_address_province_code_idx;
        DROP INDEX business_information_business_address_province_name_idx;
        DROP INDEX dev_card_service_simulation_merchant_account_terminal_id_idx;
        DROP INDEX direct_debit_record_pmc_idx;
        DROP INDEX personal_information_personal_address_country_name_idx;
        DROP INDEX personal_information_personal_address_province_code_idx;
        DROP INDEX personal_information_personal_address_province_name_idx;

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
        
        -- admin_pmc_merchant_account_index
        
        ALTER TABLE admin_pmc_merchant_account_index RENAME COLUMN merchant_terminal_id TO terminal_id;
        ALTER TABLE admin_pmc_merchant_account_index ADD COLUMN terminal_id_conv_fee VARCHAR(8);
        
        
        -- admin_pmc_payment_type_info
        
        ALTER TABLE admin_pmc_payment_type_info ADD COLUMN accepted_echeck BOOLEAN,
                                                ADD COLUMN accepted_direct_banking BOOLEAN,
                                                ADD COLUMN accepted_master_card BOOLEAN,
                                                ADD COLUMN accepted_master_card_convenience_fee BOOLEAN,
                                                ADD COLUMN accepted_visa BOOLEAN,
                                                ADD COLUMN accepted_visa_convenience_fee BOOLEAN,
                                                ADD COLUMN accepted_visa_debit BOOLEAN,
                                                ADD COLUMN accepted_visa_debit_convenience_fee BOOLEAN;
                                                
        
        
        -- business_information
        
        ALTER TABLE business_information    ADD COLUMN business_address_country VARCHAR(50),
                                            ADD COLUMN business_address_province VARCHAR(500),
                                            ADD COLUMN business_address_street_name VARCHAR(500),
                                            ADD COLUMN business_address_street_number VARCHAR(500),
                                            ADD COLUMN business_address_suite_number VARCHAR(500);
                                            
        -- card_transaction_record
        
        ALTER TABLE card_transaction_record ADD COLUMN pmc BIGINT,
                                            ADD COLUMN completion_date TIMESTAMP;
        
        -- cards_reconciliation_file
        
        CREATE TABLE cards_reconciliation_file 
        (
            id                      BIGINT              NOT NULL,
            file_name               VARCHAR(500),
            remote_file_date        TIMESTAMP,
            received                TIMESTAMP,
                CONSTRAINT cards_reconciliation_file_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE cards_reconciliation_file OWNER TO vista;
        
        -- cards_reconciliation_record
        
        CREATE TABLE cards_reconciliation_record
        (
            id                      BIGINT              NOT NULL,
            date                    DATE,
            merchant_id             VARCHAR(500),
            merchant_terminal_id    VARCHAR(500),
            merchant_account        BIGINT,
            convenience_fee_account BOOLEAN,
            status                  VARCHAR(50),
            total_deposit           NUMERIC(18,2),
            total_fee               NUMERIC(18,2),
            visa_deposit            NUMERIC(18,2),
            visa_fee                NUMERIC(18,2),
            mastercard_deposit      NUMERIC(18,2),
            mastercard_fee          NUMERIC(18,2),
            file_merchant_total     BIGINT,
            file_card_total         BIGINT,
                CONSTRAINT cards_reconciliation_record_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE cards_reconciliation_record OWNER TO vista;
        
        
        -- cards_reconciliation_record$adjustments
        
        CREATE TABLE cards_reconciliation_record$adjustments
        (
            id                      BIGINT              NOT NULL,
            owner                   BIGINT,
            value                   NUMERIC(18,2),
                CONSTRAINT cards_reconciliation_record$adjustments_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE cards_reconciliation_record$adjustments OWNER TO vista;
        
        
        -- cards_reconciliation_record$chargebacks
        
        CREATE TABLE cards_reconciliation_record$chargebacks
        (
            id                      BIGINT              NOT NULL,
            owner                   BIGINT,
            value                   NUMERIC(18,2),
                CONSTRAINT cards_reconciliation_record$chargebacks_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE cards_reconciliation_record$chargebacks OWNER TO vista;
        
        -- customer_credit_check_transaction
        
        ALTER TABLE customer_credit_check_transaction ADD COLUMN tax NUMERIC(18,2);
        
        
        -- dev_card_service_simulation_merchant_account
        
        ALTER TABLE dev_card_service_simulation_merchant_account ADD COLUMN company BIGINT;
        
        -- dev_card_service_simulation_company
        
        CREATE TABLE dev_card_service_simulation_company
        (
            id                      BIGINT              NOT NULL,
            company_id              VARCHAR(500),
                CONSTRAINT dev_card_service_simulation_company_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE dev_card_service_simulation_company OWNER TO vista;
        
        
        -- dev_card_service_simulation_reconciliation_record
        
        CREATE TABLE dev_card_service_simulation_reconciliation_record
        (
            id                      BIGINT              NOT NULL,
            file_id                 VARCHAR(500),
            date                    DATE,
            merchant                BIGINT,
            total_deposit           NUMERIC(18,2),
            total_fee               NUMERIC(18,2),
            visa_transactions       INT,
            visa_deposit            NUMERIC(18,2),
            visa_fee                NUMERIC(18,2),
            mastercard_transactions INT,
            mastercard_deposit      NUMERIC(18,2),
            mastercard_fee          NUMERIC(18,2),
                CONSTRAINT dev_card_service_simulation_reconciliation_record_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE dev_card_service_simulation_reconciliation_record OWNER TO vista;
        
        -- dev_equifax_simulator_config
        
        ALTER TABLE dev_equifax_simulator_config ADD COLUMN force_result_risk_code VARCHAR(500);
        
        
        -- direct_debit_record
        
        ALTER TABLE direct_debit_record ADD COLUMN trace_collection DATE;
        
        
        -- fee_default_equifax_fee
        
        ALTER TABLE fee_default_equifax_fee ADD COLUMN tax_rate NUMERIC(18,4);
        
        
        -- fee_default_payment_fees
        
        ALTER TABLE fee_default_payment_fees    ADD COLUMN accepted_echeck BOOLEAN,
                                                ADD COLUMN accepted_direct_banking BOOLEAN,
                                                ADD COLUMN accepted_master_card BOOLEAN,
                                                ADD COLUMN accepted_master_card_convenience_fee BOOLEAN,
                                                ADD COLUMN accepted_visa BOOLEAN,
                                                ADD COLUMN accepted_visa_convenience_fee BOOLEAN,
                                                ADD COLUMN accepted_visa_debit BOOLEAN,
                                                ADD COLUMN accepted_visa_debit_convenience_fee BOOLEAN;
         
        
        -- operations_alert
        
        ALTER TABLE operations_alert RENAME COLUMN handled TO resolved;
        ALTER TABLE operations_alert ADD COLUMN operations_notes VARCHAR(500);
        
        
        -- outgoing_mail_queue
        
        ALTER TABLE outgoing_mail_queue ADD COLUMN priority INT;
        
        
        -- personal_information
        
        ALTER TABLE personal_information    ADD COLUMN personal_address_country VARCHAR(50),
                                            ADD COLUMN personal_address_province VARCHAR(500),
                                            ADD COLUMN personal_address_street_name VARCHAR(500),
                                            ADD COLUMN personal_address_street_number VARCHAR(500),
                                            ADD COLUMN personal_address_suite_number VARCHAR(500);
        
        -- scheduler_trigger
        
        ALTER TABLE scheduler_trigger   ADD COLUMN run_timeout INT,
                                        ADD COLUMN threads INT;
       
       
        /**
        ***     ============================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     ============================================================================================================
        **/
        
        
        -- card_transaction_record
        
        UPDATE  card_transaction_record
        SET     completion_date = creation_date
        WHERE   sale_response_code = '0000';
        
        
        -- direct_debit_record
        
        UPDATE  direct_debit_record
        SET     trace_collection = TO_DATE(trace_collection_date,'DDMMYY');
        
        -- fee_default_equifax_fee
        
        UPDATE  fee_default_equifax_fee
        SET     tax_rate = 0.1300;
        
        -- fee_default_payment_fees
        
        UPDATE  fee_default_payment_fees
        SET accepted_echeck = TRUE,
            accepted_direct_banking = TRUE,
            accepted_master_card = TRUE,
            accepted_master_card_convenience_fee = TRUE,
            accepted_visa = TRUE,
            accepted_visa_convenience_fee = TRUE,
            accepted_visa_debit = TRUE,
            accepted_visa_debit_convenience_fee = TRUE;
        
        --outgoing_mail_queue
        
        UPDATE  outgoing_mail_queue
        SET     priority = 0;
        
        -- scheduler_trigger
        
        INSERT INTO scheduler_trigger(id,trigger_type,name,population_type,
        schedule_suspended,created) VALUES (nextval('public.scheduler_trigger_seq'),
        'vistaHeathMonitor','Vista Heath Monitor','allPmc',TRUE,
        DATE_TRUNC('second',current_timestamp)::timestamp);
        
        INSERT INTO scheduler_trigger(id,trigger_type,name,population_type,
        schedule_suspended,created) VALUES (nextval('public.scheduler_trigger_seq'),
        'tenantSureBusinessReport','Tenant Sure Business Report','allPmc',TRUE,
        DATE_TRUNC('second',current_timestamp)::timestamp);
        
        INSERT INTO scheduler_trigger(id,trigger_type,name,population_type,
        schedule_suspended,created) VALUES (nextval('public.scheduler_trigger_seq'),
        'paymentsReceiveCardsReconciliation','P 8A - Payments Receive Cards Reconciliation from Caledon',
        'none',TRUE,DATE_TRUNC('second',current_timestamp)::timestamp);
        
        INSERT INTO scheduler_trigger(id,trigger_type,name,population_type,
        schedule_suspended,created) VALUES (nextval('public.scheduler_trigger_seq'),
        'paymentsProcessCardsReconciliation',
        'P 8B - Payments Process Cards Reconciliation (auto triggered by paymentsReceiveCardsReconciliation)',
        'allPmc',TRUE,DATE_TRUNC('second',current_timestamp)::timestamp);
        
        
        
        
        UPDATE  scheduler_trigger
        SET     schedule_suspended = FALSE
        WHERE   schedule_suspended IS NULL;
        
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
       
        -- admin_pmc_payment_type_info
        
        ALTER TABLE admin_pmc_payment_type_info DROP COLUMN cc_amex_payment_available,
                                                DROP COLUMN cc_discover_payment_available,
                                                DROP COLUMN cc_master_card_payment_available,
                                                DROP COLUMN cc_visa_payment_available,
                                                DROP COLUMN e_check_payment_available,
                                                DROP COLUMN eft_payment_available,
                                                DROP COLUMN interac_caledon_payment_available,
                                                DROP COLUMN interac_payment_pad_payment_available,
                                                DROP COLUMN interac_visa_payment_available;
       
        -- business_information
        
        ALTER TABLE business_information    DROP COLUMN business_address_country_name,
                                            DROP COLUMN business_address_country_name_s,
                                            DROP COLUMN business_address_province_code,
                                            DROP COLUMN business_address_province_name,
                                            DROP COLUMN business_address_street1,
                                            DROP COLUMN business_address_street2;
                                            
                                        
        -- personal_information         
        
        ALTER TABLE personal_information    DROP COLUMN personal_address_country_name,
                                            DROP COLUMN personal_address_country_name_s,
                                            DROP COLUMN personal_address_province_code,
                                            DROP COLUMN personal_address_province_name,
                                            DROP COLUMN personal_address_street1,
                                            DROP COLUMN personal_address_street2;
       
        
        /**
        ***     ========================================================================================================
        ***
        ***             CREATE CONSTRAINTS
        ***
        ***     ========================================================================================================
        **/
        
       
        -- foreign keys
        
        ALTER TABLE card_transaction_record ADD CONSTRAINT card_transaction_record_pmc_fk FOREIGN KEY(pmc) 
            REFERENCES admin_pmc(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE cards_reconciliation_record ADD CONSTRAINT cards_reconciliation_record_file_card_total_fk 
            FOREIGN KEY(file_card_total) REFERENCES cards_reconciliation_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE cards_reconciliation_record ADD CONSTRAINT cards_reconciliation_record_file_merchant_total_fk FOREIGN KEY(file_merchant_total) 
            REFERENCES cards_reconciliation_file(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE cards_reconciliation_record ADD CONSTRAINT cards_reconciliation_record_merchant_account_fk FOREIGN KEY(merchant_account) 
            REFERENCES admin_pmc_merchant_account_index(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE cards_reconciliation_record$adjustments ADD CONSTRAINT cards_reconciliation_record$adjustments_owner_fk FOREIGN KEY(owner) 
            REFERENCES cards_reconciliation_record(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE cards_reconciliation_record$chargebacks ADD CONSTRAINT cards_reconciliation_record$chargebacks_owner_fk FOREIGN KEY(owner) 
            REFERENCES cards_reconciliation_record(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE dev_card_service_simulation_merchant_account ADD CONSTRAINT dev_card_service_simulation_merchant_account_company_fk FOREIGN KEY(company) 
            REFERENCES dev_card_service_simulation_company(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE dev_card_service_simulation_reconciliation_record ADD CONSTRAINT dev_card_service_simulation_reconciliation_record_merchant_fk FOREIGN KEY(merchant) 
            REFERENCES dev_card_service_simulation_merchant_account(id)  DEFERRABLE INITIALLY DEFERRED;

       
        -- check constraints
        
        ALTER TABLE business_information ADD CONSTRAINT business_information_business_address_country_e_ck 
            CHECK ((business_address_country) IN ('Afghanistan', 'AlandIslands', 'Albania', 'Algeria', 'AmericanSamoa', 'Andorra', 'Angola', 
                        'Anguilla', 'Antarctica', 'Antigua', 'Argentina', 'Armenia', 'Aruba', 'Australia', 'Austria', 'Azerbaijan', 'Bahamas', 
                        'Bahrain', 'Bangladesh', 'Barbados', 'Belarus', 'Belgium', 'Belize', 'Benin', 'Bermuda', 'Bhutan', 'Bolivia', 'Bonaire', 
                        'BosniaHerzegovina', 'Botswana', 'BouvetIsland', 'Brazil', 'BruneiDarussalam', 'Bulgaria', 'BurkinaFaso', 'Burundi', 
                        'CaboVerde', 'Cambodia', 'Cameroon', 'Canada', 'CaymanIslands', 'CentralAfricanRepublic', 'Chad', 'Chile', 'China', 
                        'ChristmasIsland', 'CocosIslands', 'Colombia', 'Comoros', 'Congo', 'CookIslands', 'CostaRica', 'Croatia', 'Cuba', 
                        'Curacao', 'Cyprus', 'CzechRepublic', 'Denmark', 'Djibouti', 'Dominica', 'DominicanRepublic', 'Ecuador', 'Egypt', 
                        'ElSalvador', 'EquatorialGuinea', 'Eritrea', 'Estonia', 'Ethiopia', 'FalklandIslands', 'FaroeIslands', 'Fiji', 'Finland', 
                        'France', 'FrenchGuiana', 'FrenchPolynesia', 'FrenchTerritories', 'Gabon', 'Gambia', 'Georgia', 'Germany', 'Ghana', 'Gibraltar', 
                        'Greece', 'Greenland', 'Grenada', 'Guadeloupe', 'Guam', 'Guatemala', 'Guernsey', 'Guinea', 'GuineaBissau', 'Guyana', 'Haiti', 
                        'HeardIslands', 'Honduras', 'HongKong', 'Hungary', 'Iceland', 'India', 'Indonesia', 'Iran', 'Iraq', 'Ireland', 'IsleOfMan', 
                        'Israel', 'Italy', 'Jamaica', 'Japan', 'Jersey', 'Jordan', 'Kazakhstan', 'Kenya', 'Kiribati', 'Kuwait', 'Kyrgyzstan', 
                        'LaoRepublic', 'Latvia', 'Lebanon', 'Lesotho', 'Liberia', 'Libya', 'Liechtenstein', 'Lithuania', 'Luxembourg', 'Macao', 
                        'Macedonia', 'Madagascar', 'Malawi', 'Malaysia', 'Maldives', 'Mali', 'Malta', 'MarshallIslands', 'Martinique', 'Mauritania', 
                        'Mauritius', 'Mayotte', 'Mexico', 'Micronesia', 'Moldova', 'Monaco', 'Mongolia', 'Montenegro', 'Montserrat', 'Morocco', 
                        'Mozambique', 'Myanmar', 'Namibia', 'Nauru', 'Nepal', 'Netherlands', 'NewCaledonia', 'NewZealand', 'Nicaragua', 'Niger', 
                        'Nigeria', 'Niue', 'NorfolkIsland', 'NorthKorea', 'NorthernMarianaIslands', 'Norway', 'Oman', 'Pakistan', 'Palau', 'Palestine', 
                        'Panama', 'PapuaNewGuinea', 'Paraguay', 'Peru', 'Philippines', 'Pitcairn', 'Poland', 'Portugal', 'PuertoRico', 'Qatar', 'Reunion', 
                        'Romania', 'RussianFederation', 'Rwanda', 'SaintBarthelemy', 'SaintHelena', 'SaintKitts', 'SaintLucia', 'SaintMartin', 'SaintPierre', 
                        'SaintVincent', 'Samoa', 'SanMarino', 'SaoTome', 'SaudiArabia', 'Senegal', 'Serbia', 'Seychelles', 'SierraLeone', 'Singapore', 
                        'SintMaartenDutch', 'Slovakia', 'Slovenia', 'SolomonIslands', 'Somalia', 'SouthAfrica', 'SouthKorea', 'SouthSudan', 'Spain', 
                        'SriLanka', 'Sudan', 'Suriname', 'Svalbard', 'Swaziland', 'Sweden', 'Switzerland', 'Syria', 'Taiwan', 'Tajikistan', 'Tanzania', 
                        'Thailand', 'TimorLeste', 'Togo', 'Tokelau', 'Tonga', 'Trinidad', 'Tunisia', 'Turkey', 'Turkmenistan', 'TurksCaicos', 'Tuvalu', 
                        'Uganda', 'Ukraine', 'UnitedArabEmirates', 'UnitedKingdom', 'UnitedStates', 'Uruguay', 'Uzbekistan', 'Vanuatu', 'Vatican', 
                        'Venezuela', 'VietNam', 'VirginIslands', 'VirginIslandsGB', 'WallisFutuna', 'WesternSahara', 'Yemen', 'Zambia', 'Zimbabwe'));

    ALTER TABLE cards_reconciliation_record ADD CONSTRAINT cards_reconciliation_record_status_e_ck 
        CHECK ((status) IN ('Processed', 'Received'));
        
    ALTER TABLE personal_information ADD CONSTRAINT personal_information_personal_address_country_e_ck 
        CHECK ((personal_address_country) IN ('Afghanistan', 'AlandIslands', 'Albania', 'Algeria', 'AmericanSamoa', 'Andorra', 'Angola', 'Anguilla', 
                        'Antarctica', 'Antigua', 'Argentina', 'Armenia', 'Aruba', 'Australia', 'Austria', 'Azerbaijan', 'Bahamas', 'Bahrain', 'Bangladesh', 
                        'Barbados', 'Belarus', 'Belgium', 'Belize', 'Benin', 'Bermuda', 'Bhutan', 'Bolivia', 'Bonaire', 'BosniaHerzegovina', 'Botswana', 
                        'BouvetIsland', 'Brazil', 'BruneiDarussalam', 'Bulgaria', 'BurkinaFaso', 'Burundi', 'CaboVerde', 'Cambodia', 'Cameroon', 'Canada', 
                        'CaymanIslands', 'CentralAfricanRepublic', 'Chad', 'Chile', 'China', 'ChristmasIsland', 'CocosIslands', 'Colombia', 'Comoros', 'Congo', 
                        'CookIslands', 'CostaRica', 'Croatia', 'Cuba', 'Curacao', 'Cyprus', 'CzechRepublic', 'Denmark', 'Djibouti', 'Dominica', 'DominicanRepublic', 
                        'Ecuador', 'Egypt', 'ElSalvador', 'EquatorialGuinea', 'Eritrea', 'Estonia', 'Ethiopia', 'FalklandIslands', 'FaroeIslands', 'Fiji', 'Finland', 
                        'France', 'FrenchGuiana', 'FrenchPolynesia', 'FrenchTerritories', 'Gabon', 'Gambia', 'Georgia', 'Germany', 'Ghana', 'Gibraltar', 'Greece', 
                        'Greenland', 'Grenada', 'Guadeloupe', 'Guam', 'Guatemala', 'Guernsey', 'Guinea', 'GuineaBissau', 'Guyana', 'Haiti', 'HeardIslands', 'Honduras', 
                        'HongKong', 'Hungary', 'Iceland', 'India', 'Indonesia', 'Iran', 'Iraq', 'Ireland', 'IsleOfMan', 'Israel', 'Italy', 'Jamaica', 'Japan', 'Jersey', 
                        'Jordan', 'Kazakhstan', 'Kenya', 'Kiribati', 'Kuwait', 'Kyrgyzstan', 'LaoRepublic', 'Latvia', 'Lebanon', 'Lesotho', 'Liberia', 'Libya', 
                        'Liechtenstein', 'Lithuania', 'Luxembourg', 'Macao', 'Macedonia', 'Madagascar', 'Malawi', 'Malaysia', 'Maldives', 'Mali', 'Malta', 
                        'MarshallIslands', 'Martinique', 'Mauritania', 'Mauritius', 'Mayotte', 'Mexico', 'Micronesia', 'Moldova', 'Monaco', 'Mongolia', 
                        'Montenegro', 'Montserrat', 'Morocco', 'Mozambique', 'Myanmar', 'Namibia', 'Nauru', 'Nepal', 'Netherlands', 'NewCaledonia', 'NewZealand', 
                        'Nicaragua', 'Niger', 'Nigeria', 'Niue', 'NorfolkIsland', 'NorthKorea', 'NorthernMarianaIslands', 'Norway', 'Oman', 'Pakistan', 'Palau', 
                        'Palestine', 'Panama', 'PapuaNewGuinea', 'Paraguay', 'Peru', 'Philippines', 'Pitcairn', 'Poland', 'Portugal', 'PuertoRico', 'Qatar', 'Reunion', 
                        'Romania', 'RussianFederation', 'Rwanda', 'SaintBarthelemy', 'SaintHelena', 'SaintKitts', 'SaintLucia', 'SaintMartin', 'SaintPierre', 
                        'SaintVincent', 'Samoa', 'SanMarino', 'SaoTome', 'SaudiArabia', 'Senegal', 'Serbia', 'Seychelles', 'SierraLeone', 'Singapore', 
                        'SintMaartenDutch', 'Slovakia', 'Slovenia', 'SolomonIslands', 'Somalia', 'SouthAfrica', 'SouthKorea', 'SouthSudan', 'Spain', 
                        'SriLanka', 'Sudan', 'Suriname', 'Svalbard', 'Swaziland', 'Sweden', 'Switzerland', 'Syria', 'Taiwan', 'Tajikistan', 'Tanzania', 
                        'Thailand', 'TimorLeste', 'Togo', 'Tokelau', 'Tonga', 'Trinidad', 'Tunisia', 'Turkey', 'Turkmenistan', 'TurksCaicos', 'Tuvalu', 
                        'Uganda', 'Ukraine', 'UnitedArabEmirates', 'UnitedKingdom', 'UnitedStates', 'Uruguay', 'Uzbekistan', 'Vanuatu', 'Vatican', 
                        'Venezuela', 'VietNam', 'VirginIslands', 'VirginIslandsGB', 'WallisFutuna', 'WesternSahara', 'Yemen', 'Zambia', 'Zimbabwe'));
                        
        ALTER TABLE scheduler_trigger ADD CONSTRAINT scheduler_trigger_trigger_type_e_ck 
            CHECK ((trigger_type) IN ('billing', 'cleanup', 'depositInterestAdjustment', 'depositRefund', 'equifaxRetention', 'ilsEmailFeed', 'ilsUpdate', 
            'initializeFutureBillingCycles', 'leaseActivation', 'leaseCompletion', 'leaseRenewal', 'paymentsBmoReceive', 'paymentsDbpProcess', 
            'paymentsDbpProcessAcknowledgment', 'paymentsDbpProcessReconciliation', 'paymentsDbpSend', 'paymentsIssue', 'paymentsLastMonthSuspend', 
            'paymentsPadProcessAcknowledgment', 'paymentsPadProcessReconciliation', 'paymentsPadSend', 'paymentsProcessCardsReconciliation', 
            'paymentsReceiveAcknowledgment', 'paymentsReceiveCardsReconciliation', 'paymentsReceiveReconciliation', 'paymentsScheduledCreditCards', 
            'paymentsScheduledEcheck', 'paymentsTenantSure', 'tenantSureBusinessReport', 'tenantSureCancellation', 'tenantSureHQUpdate', 'tenantSureRenewal', 
            'tenantSureReports', 'tenantSureTransactionReports', 'test', 'updateArrears', 'updatePaymentsSummary', 'vistaBusinessReport', 'vistaCaleonReport', 
            'vistaHeathMonitor', 'yardiARDateVerification', 'yardiImportProcess'));


        -- not null
               
        ALTER TABLE fee_default_payment_fees    ALTER COLUMN accepted_echeck SET NOT NULL,
                                                ALTER COLUMN accepted_direct_banking SET NOT NULL,
                                                ALTER COLUMN accepted_master_card SET NOT NULL,
                                                ALTER COLUMN accepted_master_card_convenience_fee SET NOT NULL,
                                                ALTER COLUMN accepted_visa SET NOT NULL,
                                                ALTER COLUMN accepted_visa_convenience_fee SET NOT NULL,
                                                ALTER COLUMN accepted_visa_debit SET NOT NULL,
                                                ALTER COLUMN accepted_visa_debit_convenience_fee SET NOT NULL;
        
        ALTER TABLE scheduler_trigger ALTER COLUMN schedule_suspended SET NOT NULL;
                
        /**
        ***     ============================================================================================================
        ***     
        ***             CREATE INDEXES
        ***
        ***     ============================================================================================================
        **/
        
        CREATE UNIQUE INDEX admin_pmc_merchant_account_index_terminal_id_conv_fee_idx ON admin_pmc_merchant_account_index USING btree (terminal_id_conv_fee);
        CREATE UNIQUE INDEX admin_pmc_merchant_account_index_terminal_id_idx ON admin_pmc_merchant_account_index USING btree (terminal_id);
        CREATE INDEX cards_reconciliation_record$adjustments_owner_idx ON cards_reconciliation_record$adjustments USING btree (owner);
        CREATE INDEX cards_reconciliation_record$chargebacks_owner_idx ON cards_reconciliation_record$chargebacks USING btree (owner);
        CREATE INDEX cards_reconciliation_record_merchant_account_idx ON cards_reconciliation_record USING btree (merchant_account);
        CREATE INDEX card_transaction_record_payment_transaction_id_idx ON card_transaction_record USING btree (payment_transaction_id);
        CREATE UNIQUE INDEX dev_card_service_simulation_merchant_account_term_comp_idx ON dev_card_service_simulation_merchant_account USING btree (terminal_id, company);
        CREATE UNIQUE INDEX dev_card_service_simulation_company_company_id_idx ON dev_card_service_simulation_company USING btree (company_id);
        CREATE INDEX direct_debit_record_pmc_processing_status_idx ON direct_debit_record USING btree(pmc, processing_status);

COMMIT;

SET client_min_messages = 'notice';
