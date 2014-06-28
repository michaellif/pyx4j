/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             _admin_ schema changes for v. 1.1.4
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
        
        DROP INDEX business_information_business_address_country_name_idx;
        DROP INDEX business_information_business_address_province_code_idx;
        DROP INDEX business_information_business_address_province_name_idx;
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
        
        
        -- business_information
        
        ALTER TABLE business_information    ADD COLUMN business_address_country VARCHAR(50),
                                            ADD COLUMN business_address_province VARCHAR(500),
                                            ADD COLUMN business_address_street_name VARCHAR(500),
                                            ADD COLUMN business_address_street_number VARCHAR(500),
                                            ADD COLUMN business_address_suite_number VARCHAR(500);
        
        -- dev_equifax_simulator_config
        
        ALTER TABLE dev_equifax_simulator_config ADD COLUMN force_result_risk_code VARCHAR(500);
        
        
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
        
        
        --outgoing_mail_queue
        
        UPDATE  outgoing_mail_queue
        SET     priority = 0;
        
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
       
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
        'paymentsScheduledEcheck', 'paymentsTenantSure', 'tenantSureCancellation', 'tenantSureHQUpdate', 'tenantSureReports', 'tenantSureTransactionReports', 
        'test', 'updateArrears', 'updatePaymentsSummary', 'vistaBusinessReport', 'vistaCaleonReport', 'vistaHeathMonitor', 'yardiARDateVerification', 
        'yardiImportProcess'));

                
        /**
        ***     ============================================================================================================
        ***     
        ***             CREATE INDEXES
        ***
        ***     ============================================================================================================
        **/
        
       CREATE INDEX direct_debit_record_pmc_processing_status_idx ON direct_debit_record USING btree(pmc, processing_status);

COMMIT;

SET client_min_messages = 'notice';
