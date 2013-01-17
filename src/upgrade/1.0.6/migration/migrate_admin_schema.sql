/**
***     =====================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***     
***             _admin_ schema changes
***
***     =====================================================================================================================
**/

BEGIN TRANSACTION;

SET search_path = '_admin_';


-- admin_pmc_equifax_info

ALTER TABLE admin_pmc_equifax_info DROP COLUMN approved,
                                ADD COLUMN business_information BIGINT,
                                ADD COLUMN equifax_per_applicant_credit_check_fee NUMERIC(18,2),
                                ADD COLUMN equifax_sign_up_fee NUMERIC(18,2),
                                ADD COLUMN payment_method BIGINT,
                                ADD COLUMN personal_information BIGINT,
                                ADD COLUMN status VARCHAR(50);
                                
-- admin_pmc_payment_method

CREATE TABLE admin_pmc_payment_method
(
        id                                      BIGINT                  NOT NULL,
        payment_type                            VARCHAR(50),
        details_discriminator                   VARCHAR(50),
        details                                 BIGINT,
        is_deleted                              BOOLEAN,
        pmc                                     BIGINT                  NOT NULL,
                CONSTRAINT      admin_pmc_payment_method_pk PRIMARY KEY(id),
                CONSTRAINT      admin_pmc_payment_method_pmc_fk FOREIGN KEY(pmc)
                        REFERENCES admin_pmc(id),
                CONSTRAINT      admin_pmc_payment_method_details_discriminator_d_ck
                        CHECK ((details_discriminator) IN ('CashInfo', 'CheckInfo', 'CreditCard', 'EcheckInfo', 'InteracInfo')),
                CONSTRAINT      admin_pmc_payment_method_payment_type_e_ck
                        CHECK ((payment_type) IN ('Cash', 'Check', 'CreditCard', 'EFT', 'Echeck', 'Interac'))
);

CREATE INDEX admin_pmc_payment_method_pmc_idx ON admin_pmc_payment_method USING btree (pmc);

ALTER TABLE admin_pmc_payment_method OWNER TO vista;


-- admin_pmc_vista_features

ALTER TABLE admin_pmc_vista_features ADD COLUMN country_of_operation VARCHAR(50),
                                ADD COLUMN default_product_catalog BOOLEAN,
                                ADD COLUMN yardi_integration BOOLEAN;
                                
-- admin_pmc_yardi_credential

CREATE TABLE admin_pmc_yardi_credential
(
        id                                      BIGINT                  NOT NULL,
        pmc                                     BIGINT                  NOT NULL,
        service_url                             VARCHAR(500),
        username                                VARCHAR(500),
        credential                              VARCHAR(500),
        server_name                             VARCHAR(500),
        db                                      VARCHAR(500),
        platform                                VARCHAR(50),
                CONSTRAINT      admin_pmc_yardi_credential_pk PRIMARY KEY(id),
                CONSTRAINT      admin_pmc_yardi_credential_pmc_fk FOREIGN KEY(pmc)
                        REFERENCES admin_pmc(id),
                CONSTRAINT      admin_pmc_yardi_credential_platform_e_ck
                        CHECK ((platform) IN ('Oracle', 'SQL'))
);

CREATE UNIQUE INDEX admin_pmc_yardi_credential_pmc_idx ON admin_pmc_yardi_credential USING btree (pmc);

ALTER TABLE admin_pmc_yardi_credential OWNER TO vista;

-- business_information

CREATE TABLE business_information
(
        id                                      BIGINT                  NOT NULL,
        company_name                            VARCHAR(500),
        company_type                            VARCHAR(50),
        business_address_street1                VARCHAR(500),
        business_address_street2                VARCHAR(500),
        business_address_city                   VARCHAR(500),
        business_address_province_name          VARCHAR(500),
        business_address_province_code          VARCHAR(500),
        business_address_country_name           VARCHAR(500),
        business_address_postal_code            VARCHAR(500),
        business_number                         VARCHAR(500),
        business_established_date               DATE,
        business_address_country_name_s         VARCHAR(500),   
                CONSTRAINT      business_information_pk PRIMARY KEY(id),
                CONSTRAINT      business_information_company_type_e_ck 
                        CHECK (company_type IN ('Cooperative','Corporation','Partnership','SoleProprietorship'))
);

CREATE INDEX business_information_business_address_country_name_idx ON business_information USING btree (business_address_country_name);
CREATE INDEX business_information_business_address_province_code_idx ON business_information USING btree (business_address_province_code);
CREATE INDEX business_information_business_address_province_name_idx ON business_information USING btree (business_address_province_name);

ALTER TABLE business_information OWNER TO vista;

-- customer_credit_check_transaction

CREATE TABLE customer_credit_check_transaction
(
        id                                      BIGINT                  NOT NULL,
        amount                                  NUMERIC(18,2),
        payment_method                          BIGINT,
        status                                  VARCHAR(50),
        transaction_authorization_number        VARCHAR(500),
        transaction_date                        TIMESTAMP WITHOUT TIME ZONE,
                CONSTRAINT      customer_credit_check_transaction_pk PRIMARY KEY(id),
                CONSTRAINT      customer_credit_check_transaction_payment_method_fk FOREIGN KEY(payment_method)
                        REFERENCES admin_pmc_payment_method(id),
                CONSTRAINT      customer_credit_check_transaction_status_e_ck
                        CHECK ((status) IN ('Authorized', 'Cleared', 'Draft', 'PaymentRejected', 'Rejected', 'Reversal'))
);

ALTER TABLE customer_credit_check_transaction OWNER TO vista;

-- admin_pmc$credit_check_transaction

CREATE TABLE admin_pmc$credit_check_transaction
(
        id                                      BIGINT                  NOT NULL,
        owner                                   BIGINT,
        value                                   BIGINT,
                CONSTRAINT      admin_pmc$credit_check_transaction_pk PRIMARY KEY(id),
                CONSTRAINT      admin_pmc$credit_check_transaction_owner_fk FOREIGN KEY(owner)
                        REFERENCES admin_pmc(id),
                CONSTRAINT      admin_pmc$credit_check_transaction_value_fk FOREIGN KEY(value)
                        REFERENCES customer_credit_check_transaction(id)
);

CREATE INDEX admin_pmc$credit_check_transaction_owner_idx ON admin_pmc$credit_check_transaction USING btree (owner);

ALTER TABLE admin_pmc$credit_check_transaction OWNER TO vista;



-- fee_default_equifax_fee

CREATE TABLE fee_default_equifax_fee
(
        id                                      BIGINT                  NOT NULL,
        recommendation_report_per_applicant_fee NUMERIC(18,2),
        full_credit_report_per_applicant_fee    NUMERIC(18,2),
        recommendation_report_set_up_fee        NUMERIC(18,2),
        full_credit_report_set_up_fee           NUMERIC(18,2),
                CONSTRAINT      fee_default_equifax_fee_pk PRIMARY KEY(id)
);

ALTER TABLE fee_default_equifax_fee OWNER TO vista;

-- fee_default_payment_fees

CREATE TABLE fee_default_payment_fees
(
        id                                      BIGINT                  NOT NULL,
        interac_caledon_fee                     NUMERIC(18,2),
        cc_visa_fee                             NUMERIC(18,2),
        interac_payment_pad_fee                 NUMERIC(18,2),
        cc_amex_fee                             NUMERIC(18,2),
        eft_fee                                 NUMERIC(18,2),
        cc_master_card_fee                      NUMERIC(18,2),
        cc_discover_fee                         NUMERIC(18,2),
        e_cheque_fee                            NUMERIC(18,2),
        interac_visa_fee                        NUMERIC(18,2),
                CONSTRAINT      fee_default_payment_fees_pk PRIMARY KEY(id)
);

ALTER TABLE fee_default_payment_fees OWNER TO vista;

-- fee_pmc_equifax_fee

CREATE TABLE fee_pmc_equifax_fee
(
        id                                      BIGINT                  NOT NULL,
        recommendation_report_per_applicant_fee NUMERIC(18,2),
        full_credit_report_per_applicant_fee    NUMERIC(18,2),
        recommendation_report_set_up_fee        NUMERIC(18,2),
        full_credit_report_set_up_fee           NUMERIC(18,2),
        pmc                                     BIGINT                  NOT NULL,
                CONSTRAINT      fee_pmc_equifax_fee_pk PRIMARY KEY(id),
                CONSTRAINT      fee_pmc_equifax_fee_pmc_fk FOREIGN KEY(pmc)
                        REFERENCES admin_pmc(id)
);

CREATE UNIQUE INDEX fee_pmc_equifax_fee_pmc_idx ON fee_pmc_equifax_fee USING btree (pmc);

ALTER TABLE fee_pmc_equifax_fee OWNER TO vista;

-- legal_document

ALTER TABLE legal_document ALTER COLUMN content TYPE VARCHAR(30000);

-- payment_payment_details

CREATE TABLE payment_payment_details
(
        id                                      BIGINT                  NOT NULL,
        id_discriminator                        VARCHAR(64)             NOT NULL,
        name_on                                 VARCHAR(500),
        bank_id                                 VARCHAR(3),
        branch_transit_number                   VARCHAR(5),
        account_no_number                       VARCHAR(12),
        account_no_obfuscated_number            VARCHAR(12),
        incoming_interac_transaction            BIGINT,
        bank_no                                 VARCHAR(500),
        transit_no                              VARCHAR(500),
        account_no                              VARCHAR(500),
        card_type                               VARCHAR(50),
        card_obfuscated_number                  VARCHAR(16),
        token                                   VARCHAR(500),
        expiry_date                             DATE,
        bank_phone                              VARCHAR(500),
        received_amount                         NUMERIC(18,2),
        change_amount                           NUMERIC(18,2),
        notes                                   VARCHAR(500),
        bank_name                               VARCHAR(500),
        account_type                            VARCHAR(50),
        check_no                                VARCHAR(500),
        institution_no                          VARCHAR(500),
                CONSTRAINT      payment_payment_details_pk PRIMARY KEY(id),
                CONSTRAINT      payment_payment_details_account_type_e_ck
                        CHECK ((account_type) IN ('Chequing', 'Saving')),
                CONSTRAINT      payment_payment_details_card_type_e_ck
                        CHECK ((card_type) IN ('MasterCard', 'Visa')),
                CONSTRAINT      payment_payment_details_id_discriminator_ck
                        CHECK ((id_discriminator) IN ('CashInfo', 'CheckInfo', 'CreditCard', 'EcheckInfo', 'InteracInfo'))
);

ALTER TABLE payment_payment_details OWNER TO vista;

ALTER TABLE admin_pmc_payment_method ADD CONSTRAINT admin_pmc_payment_method_details_fk FOREIGN KEY(details) 
                        REFERENCES payment_payment_details(id);

-- personal_information

CREATE TABLE personal_information
(
        id                              BIGINT                  NOT NULL,
        name_name_prefix                VARCHAR(50),
        name_first_name                 VARCHAR(500),
        name_middle_name                VARCHAR(500),
        name_last_name                  VARCHAR(500),
        name_maiden_name                VARCHAR(500),
        name_name_suffix                VARCHAR(500),
        personal_address_street1        VARCHAR(500),
        personal_address_street2        VARCHAR(500),
        personal_address_city           VARCHAR(500),
        personal_address_province_name  VARCHAR(500),
        personal_address_province_code  VARCHAR(500),
        personal_address_country_name   VARCHAR(500),
        personal_address_country_name_s  VARCHAR(500),
        personal_address_postal_code    VARCHAR(500),
        email                           VARCHAR(500),
        date_of_birth                   DATE,
        sin                             VARCHAR(500),
                        CONSTRAINT      personal_information_pk PRIMARY KEY(id),
                        CONSTRAINT      personal_information_name_name_prefix_e_ck
                                CHECK (name_name_prefix IN('Dr','Miss','Mr','Mrs','Ms'))
);

CREATE INDEX personal_information_personal_address_country_name_idx ON personal_information USING btree (personal_address_country_name);
CREATE INDEX personal_information_personal_address_province_code_idx ON personal_information USING btree (personal_address_province_code);
CREATE INDEX personal_information_personal_address_province_name_idx ON personal_information USING btree (personal_address_province_name);


        
ALTER TABLE personal_information OWNER TO vista;

-- tenant_sure_merchant_account

CREATE TABLE tenant_sure_merchant_account
(
        id                              BIGINT                  NOT NULL,
        account_number                  VARCHAR(12),
        charge_description              VARCHAR(60),
        bank_id                         VARCHAR(3),
        branch_transit_number           VARCHAR(5),
        merchant_terminal_id            VARCHAR(8),
                CONSTRAINT      tenant_sure_merchant_account_pk PRIMARY KEY(id)
);

CREATE UNIQUE INDEX tenant_sure_merchant_account_merchant_terminal_id_idx ON tenant_sure_merchant_account USING btree (merchant_terminal_id);

ALTER TABLE tenant_sure_merchant_account OWNER TO vista;

-- vista_merchant_account

CREATE TABLE vista_merchant_account
(
        id                              BIGINT                  NOT NULL,
        account_number                  VARCHAR(12),
        charge_description              VARCHAR(60),
        bank_id                         VARCHAR(3),
        branch_transit_number           VARCHAR(5),
        merchant_terminal_id            VARCHAR(8),
                CONSTRAINT      vista_merchant_account_pk PRIMARY KEY(id)
);

CREATE UNIQUE INDEX vista_merchant_account_merchant_terminal_id_idx ON vista_merchant_account USING btree (merchant_terminal_id);

ALTER TABLE vista_merchant_account OWNER TO vista;

-- vista_terms

ALTER TABLE vista_terms DROP COLUMN x;
ALTER TABLE vista_terms ADD COLUMN target VARCHAR(50);
ALTER TABLE vista_terms ADD CONSTRAINT vista_terms_target_e_ck CHECK ((target) IN ('PMC', 'PmcCaldedonSolePropetorshipSection', 'PmcCaledonTemplate', 'PmcPaymentPad', 'Tenant'));


/**
***     ============================================================================================
***             Data mangling
***     ============================================================================================
**/

ALTER TABLE admin_pmc_equifax_info DROP CONSTRAINT admin_pmc_equifax_info_report_type_e_ck;

UPDATE  admin_pmc_equifax_info
SET     report_type = 'RecomendationReport'
WHERE   report_type = 'shortReport';

/**     ==========================================================================================
***             Constraints changes not taken care of yet
***     ==========================================================================================
**/


ALTER TABLE admin_pmc DROP CONSTRAINT admin_pmc_status_e_ck;
ALTER TABLE legal_document DROP CONSTRAINT legal_document_locale_e_ck;
ALTER TABLE scheduler_trigger DROP CONSTRAINT scheduler_trigger_population_type_e_ck;
ALTER TABLE scheduler_trigger DROP CONSTRAINT scheduler_trigger_trigger_type_e_ck;

ALTER TABLE admin_pmc_equifax_info ADD CONSTRAINT admin_pmc_equifax_info_report_type_e_ck CHECK ((report_type) IN ('FullCreditReport', 'RecomendationReport'));
ALTER TABLE admin_pmc_equifax_info ADD CONSTRAINT admin_pmc_equifax_info_status_e_ck 
        CHECK ((status) IN ('Active', 'NotRequested', 'PendingEquifaxApproval', 'PendingVistaApproval', 'Rejected', 'Suspended'));
ALTER TABLE admin_pmc ADD CONSTRAINT admin_pmc_status_e_ck CHECK ((status) IN ('Activating', 'Active', 'Cancelled', 'Created', 'Suspended', 'Terminated'));
ALTER TABLE admin_pmc_vista_features ADD CONSTRAINT admin_pmc_vista_features_country_of_operation_e_ck CHECK ((country_of_operation) IN ('Canada', 'UK', 'US'));
ALTER TABLE legal_document ADD CONSTRAINT legal_document_locale_e_ck CHECK ((locale) IN ('en', 'en_CA', 'en_GB', 'en_US', 'es', 'fr', 'fr_CA', 'ru', 'zh_CN', 'zh_TW'));
ALTER TABLE scheduler_trigger ADD CONSTRAINT scheduler_trigger_population_type_e_ck CHECK ((population_type) IN ('allPmc', 'except', 'manual', 'none'));
ALTER TABLE scheduler_trigger ADD CONSTRAINT scheduler_trigger_trigger_type_e_ck 
CHECK ((trigger_type) IN ('billing', 'cleanup', 'equifaxRetention', 'initializeFutureBillingCycles', 'leaseActivation', 'leaseCompletion', 'leaseRenewal', 'paymentsBmoRecive', 'paymentsIssue', 'paymentsPadReciveAcknowledgment', 'paymentsPadReciveReconciliation', 'paymentsPadSend', 'paymentsScheduledCreditCards', 'paymentsScheduledEcheck', 'paymentsTenantSure', 'test', 'updateArrears', 'updatePaymentsSummary', 'yardiImportProcess'));

ALTER TABLE admin_pmc_equifax_info ADD CONSTRAINT admin_pmc_equifax_info_business_information_fk FOREIGN KEY(business_information) REFERENCES business_information(id);
ALTER TABLE admin_pmc_equifax_info ADD CONSTRAINT admin_pmc_equifax_info_payment_method_fk FOREIGN KEY(payment_method) REFERENCES admin_pmc_payment_method(id);
ALTER TABLE admin_pmc_equifax_info ADD CONSTRAINT admin_pmc_equifax_info_personal_information_fk FOREIGN KEY(personal_information) REFERENCES personal_information(id);


COMMIT;
