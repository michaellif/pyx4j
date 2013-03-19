/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             1.0.8  PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_108(v_schema_name TEXT) RETURNS VOID AS
$$
BEGIN
        EXECUTE 'SET search_path = '||v_schema_name;
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP CONSTRAINTS SECTION
        ***
        ***     ======================================================================================================
        **/

        -- Foreign keys
        --ALTER TABLE insurance_tenant_sure_details DROP CONSTRAINT insurance_tenant_sure_details_insurance_fk;
        --ALTER TABLE insurance_tenant_sure_tax DROP CONSTRAINT insurance_tenant_sure_tax_tenant_sure_details_fk;

        -- Check constraints
        --ALTER TABLE insurance_tenant_sure_details DROP CONSTRAINT insurance_tenant_sure_details_insurance_discriminator_d_ck;
        --ALTER TABLE insurance_tenant_sure_tax DROP CONSTRAINT insurance_tenant_sure_tax_id_discriminator_ck;
        --ALTER TABLE insurance_tenant_sure_tax DROP CONSTRAINT insurance_tenant_sure_tax_tenant_sure_details_ck;
        ALTER TABLE aging_buckets DROP CONSTRAINT aging_buckets_debit_type_e_ck;
        ALTER TABLE billing_billing_type DROP CONSTRAINT billing_billing_type_payment_frequency_e_ck;
        ALTER TABLE billing_invoice_line_item DROP CONSTRAINT billing_invoice_line_item_debit_type_e_ck;
        ALTER TABLE billing_debit_credit_link DROP CONSTRAINT billing_debit_credit_link_credit_item_discriminator_d_ck;
        ALTER TABLE billing_invoice_line_item DROP CONSTRAINT billing_invoice_line_item_id_discriminator_ck;
        ALTER TABLE lease DROP CONSTRAINT lease_payment_frequency_e_ck;

        
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP TABLES 
        ***
        ***     ======================================================================================================
        **/
        
        DROP TABLE IF EXISTS insurance_tenant_sure_tax;
        DROP TABLE IF EXISTS insurance_tenant_sure_details;
        
        
        /**
        ***     ======================================================================================================
        ***
        ***             NEW AND ALTERED TABLES 
        ***
        ***     ======================================================================================================
        **/
        
        -- billing_account
        ALTER TABLE billing_account     ADD COLUMN billing_cycle_start_day INTEGER,
                                        ADD COLUMN payment_due_day_offset INTEGER,
                                        ADD COLUMN final_due_day_offset INTEGER,
                                        ADD COLUMN billing_period VARCHAR(50);
                                        
                                        
        -- billing_billing_cycle
        
        ALTER TABLE billing_billing_cycle       ADD COLUMN target_pad_generation_date DATE,
                                                ADD COLUMN target_bill_execution_date DATE,
                                                ADD COLUMN target_pad_execution_date DATE,
                                                ADD COLUMN actual_pad_generation_date DATE,
                                                ADD COLUMN actual_bill_execution_date DATE,
                                                ADD COLUMN actual_pad_calculation_date DATE,
                                                ADD COLUMN actual_pad_execution_date DATE;
                                                
        -- billing_billing_type
        
        ALTER TABLE billing_billing_type RENAME COLUMN payment_frequency TO billing_period;
                                                
                                                
        -- field_user
        
        CREATE TABLE field_user 
        (
                id                                      BIGINT                          NOT NULL,
                updated                                 TIMESTAMP,
                email                                   VARCHAR(64),
                created                                 TIMESTAMP,
                name                                    VARCHAR(500),
                        CONSTRAINT      field_user_pk PRIMARY KEY(id)
        );
        
        
        ALTER TABLE field_user OWNER TO vista;   
        
        
        -- field_user_credential
        
        CREATE TABLE field_user_credential
        (
                id                                      BIGINT                          NOT NULL,
                security_answer                         VARCHAR(500),
                access_key                              VARCHAR(500),
                credential_updated                      TIMESTAMP,
                usr                                     BIGINT,
                security_question                       VARCHAR(500),
                enabled                                 BOOLEAN,
                required_password_change_on_next_log_in BOOLEAN,
                recovery_email                          VARCHAR(500),
                password_updated                        TIMESTAMP,
                credential                              VARCHAR(500),
                access_key_expire                       TIMESTAMP,
                        CONSTRAINT      field_user_credential_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE field_user_credential OWNER TO vista;                           
                                                
        -- insurance_certificate
        ALTER TABLE insurance_certificate ADD COLUMN total_anniversary_first_month_payable NUMERIC(18,2);
        
        
        -- lease_billing_policy$available_billing_types
        
        CREATE TABLE lease_billing_policy$available_billing_types
        (
                id                                      BIGINT                          NOT NULL,
                owner                                   BIGINT,
                value                                   BIGINT,
                seq                                     INT,
                        CONSTRAINT      lease_billing_policy$available_billing_types_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE lease_billing_policy$available_billing_types OWNER TO vista;
        
        
        -- lease_billing_type_policy_item
        
        CREATE TABLE lease_billing_type_policy_item
        (
                id                                      BIGINT                          NOT NULL,
                billing_period                       VARCHAR(50),
                billing_cycle_start_day                 INT,
                bill_execution_day_offset               INT,
                payment_due_day_offset                  INT,
                final_due_day_offset                    INT,
                pad_calculation_day_offset              INT,
                pad_execution_day_offset                INT,
                        CONSTRAINT      lease_billing_type_policy_item_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE lease_billing_type_policy_item OWNER TO vista;
        
        
        -- padpolicy
        
        CREATE TABLE padpolicy
        (
                id                                      BIGINT                          NOT NULL,
                updated                                 TIMESTAMP,
                node_discriminator                      VARCHAR(50),
                node                                    BIGINT,
                charge_type                             VARCHAR(50),
                        CONSTRAINT      padpolicy_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE padpolicy OWNER TO vista;
        
        -- padpolicy_item
        
        CREATE TABLE padpolicy_item
        (       
                id                                      BIGINT                          NOT NULL,
                debit_type                              VARCHAR(50),
                owing_balance_type                      VARCHAR(50),
                        CONSTRAINT      padpolicy_item_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE padpolicy_item OWNER TO vista;
        
        -- padpolicy$debit_balance_types
        
        CREATE TABLE padpolicy$debit_balance_types
        (
                id                                      BIGINT                          NOT NULL,
                owner                                   BIGINT,
                value                                   BIGINT,
                seq                                     INT,
                        CONSTRAINT      padpolicy$debit_balance_types_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE padpolicy$debit_balance_types OWNER TO vista;
        
        
        -- payment_record
        
        ALTER TABLE payment_record ADD COLUMN pad_billing_cycle BIGINT;
        
        -- preauthorized_payment
        
        CREATE TABLE preauthorized_payment
        (
                id                                      BIGINT                          NOT NULL,
                amount_type                             VARCHAR(50),
                amount                                  NUMERIC(18,2),
                payment_method_discriminator            VARCHAR(50),
                payment_method                          BIGINT,
                comments                                VARCHAR(40),
                tenant_discriminator                    VARCHAR(50)                     NOT NULL,
                tenant                                  BIGINT                          NOT NULL,
                creation_date                           DATE,
                order_in_parent                         INT,
                        CONSTRAINT      preauthorized_payment_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE preauthorized_payment OWNER TO vista;
        
        
        -- product_item_type$yardi_charge_codes
        
        CREATE TABLE product_item_type$yardi_charge_codes
        (
                id                                      BIGINT                          NOT NULL,
                owner                                   BIGINT,
                value                                   BIGINT,
                seq                                     INT,
                        CONSTRAINT      product_item_type$yardi_charge_codes_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE product_item_type$yardi_charge_codes OWNER TO vista;
        
        -- tenant_info
        
        CREATE TABLE tenant_info
        (
                id                                      BIGINT                          NOT NULL,
                name                                    BIGINT,
                role                                    VARCHAR(50),
                        CONSTRAINT      tenant_info_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE tenant_info OWNER TO vista;
        
        -- yardi_charge_code
        
        CREATE TABLE yardi_charge_code
        (
                id                                      BIGINT                          NOT NULL,
                yardi_charge_code                       VARCHAR(500),
                        CONSTRAINT      yardi_charge_code_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE yardi_charge_code OWNER TO vista;
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        -- Move payment frequency from lease to billing_account
        
        EXECUTE 'UPDATE '||v_schema_name||'.billing_account  AS b '
                ||'SET  billing_period =  l.payment_frequency '
                ||'FROM '||v_schema_name||'.lease AS l '
                ||'WHERE b.id = l.billing_account ';
                
        -- Create lease_billing_type_policy_items 
        
        EXECUTE 'INSERT INTO lease_billing_type_policy_item (id,billing_period,billing_cycle_start_day,'
                ||'bill_execution_day_offset,payment_due_day_offset,final_due_day_offset,pad_calculation_day_offset,pad_execution_day_offset) '
                ||'(SELECT nextval(''public.lease_billing_type_policy_item_seq'') AS id, b.billing_period, '
                ||'l.default_billing_cycle_sart_day AS billing_cycle_start_day,-15 AS bill_execution_day_offset,'
                ||'0 AS payment_due_day_offset,15 AS final_due_day_offset, -3 AS pad_calculation_day_offset,'
                ||'0 AS pad_execution_day_offset '
                ||'FROM         '||v_schema_name||'.lease_billing_policy l, '
                ||'             (SELECT DISTINCT billing_period FROM '||v_schema_name||'.billing_account ) AS b )';   
        
         
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        -- Foreign keys
        ALTER TABLE field_user_credential ADD CONSTRAINT field_user_credential_usr_fk FOREIGN KEY(usr) REFERENCES field_user(id);
        ALTER TABLE lease_billing_policy$available_billing_types ADD CONSTRAINT lease_billing_policy$available_billing_types_owner_fk FOREIGN KEY(owner) 
                REFERENCES lease_billing_policy(id);
        ALTER TABLE lease_billing_policy$available_billing_types ADD CONSTRAINT lease_billing_policy$available_billing_types_value_fk FOREIGN KEY(value) 
                REFERENCES lease_billing_type_policy_item(id);
        ALTER TABLE padpolicy$debit_balance_types ADD CONSTRAINT padpolicy$debit_balance_types_owner_fk FOREIGN KEY(owner) REFERENCES padpolicy(id);
        ALTER TABLE padpolicy$debit_balance_types ADD CONSTRAINT padpolicy$debit_balance_types_value_fk FOREIGN KEY(value) REFERENCES padpolicy_item(id);
        ALTER TABLE payment_record ADD CONSTRAINT payment_record_pad_billing_cycle_fk FOREIGN KEY(pad_billing_cycle) REFERENCES billing_billing_cycle(id);
        ALTER TABLE preauthorized_payment ADD CONSTRAINT preauthorized_payment_tenant_fk FOREIGN KEY(tenant) REFERENCES lease_participant(id);
        ALTER TABLE preauthorized_payment ADD CONSTRAINT preauthorized_payment_payment_method_fk FOREIGN KEY(payment_method) REFERENCES payment_method(id);
        ALTER TABLE product_item_type$yardi_charge_codes ADD CONSTRAINT product_item_type$yardi_charge_codes_owner_fk FOREIGN KEY(owner) REFERENCES product_item_type(id);
        ALTER TABLE product_item_type$yardi_charge_codes ADD CONSTRAINT product_item_type$yardi_charge_codes_value_fk FOREIGN KEY(value) REFERENCES yardi_charge_code(id);
        ALTER TABLE tenant_info ADD CONSTRAINT tenant_info_name_fk FOREIGN KEY(name) REFERENCES name(id);

                
        -- Check constraints
        ALTER TABLE aging_buckets ADD CONSTRAINT aging_buckets_debit_type_e_ck 
                CHECK ((debit_type) IN ('accountCharge', 'addOn', 'booking', 'deposit', 'latePayment', 'lease', 'locker', 'nsf', 'other', 'parking', 'pet', 'target', 'total', 'unknown', 'utility'));
        ALTER TABLE billing_account ADD CONSTRAINT billing_account_billing_period_e_ck 
                CHECK ((billing_period) IN ('Annually', 'BiWeekly', 'Monthly', 'SemiAnnyally', 'SemiMonthly', 'Weekly'));
        ALTER TABLE billing_billing_type ADD CONSTRAINT billing_billing_type_billing_period_e_ck 
                CHECK ((billing_period) IN ('Annually', 'BiWeekly', 'Monthly', 'SemiAnnyally', 'SemiMonthly', 'Weekly'));
        ALTER TABLE billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_debit_type_e_ck 
                CHECK ((debit_type) IN ('accountCharge', 'addOn', 'booking', 'deposit', 'latePayment', 'lease', 'locker', 'nsf', 'other', 'parking', 'pet', 'target', 'total', 'unknown', 'utility'));
        ALTER TABLE billing_debit_credit_link ADD CONSTRAINT billing_debit_credit_link_credit_item_discriminator_d_ck 
                CHECK ((credit_item_discriminator) IN ('AccountCredit', 'CarryforwardCredit', 'DepositRefund', 'Payment', 'ProductCredit', 'YardiCredit', 
                'YardiPayment', 'YardiReceipt'));
        ALTER TABLE billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_id_discriminator_ck 
                CHECK ((id_discriminator) IN ('AccountCharge', 'AccountCredit', 'CarryforwardCharge', 'CarryforwardCredit', 'Deposit', 'DepositRefund', 
                'LatePaymentFee', 'NSF', 'Payment', 'PaymentBackOut', 'ProductCharge', 'ProductCredit', 'Withdrawal', 'YardiCharge', 'YardiCredit', 
                'YardiPayment', 'YardiReceipt', 'YardiReversal'));
        ALTER TABLE lease_billing_type_policy_item ADD CONSTRAINT lease_billing_type_policy_item_billing_period_e_ck 
                CHECK ((billing_period) IN ('Annually', 'BiWeekly', 'Monthly', 'SemiAnnyally', 'SemiMonthly', 'Weekly'));
        ALTER TABLE padpolicy ADD CONSTRAINT padpolicy_charge_type_e_ck CHECK ((charge_type) IN ('FixedAmount', 'OwingBalance'));
        ALTER TABLE padpolicy_item ADD CONSTRAINT padpolicy_item_debit_type_e_ck 
                CHECK ((debit_type) IN ('accountCharge', 'addOn', 'booking', 'deposit', 'latePayment', 'lease', 'locker', 'nsf', 'other', 'parking', 
                'pet', 'target', 'total', 'unknown', 'utility'));
        ALTER TABLE padpolicy_item ADD CONSTRAINT padpolicy_item_owing_balance_type_e_ck CHECK ((owing_balance_type) IN ('LastBill', 'ToDateTotal'));
        ALTER TABLE padpolicy ADD CONSTRAINT padpolicy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('Disc Complex', 'Disc_Building', 'Disc_Country', 'Disc_Floorplan', 'Disc_Province', 'OrganizationPoliciesNode', 'Unit_BuildingElement'));
        ALTER TABLE preauthorized_payment ADD CONSTRAINT preauthorized_payment_amount_type_e_ck CHECK ((amount_type) IN ('Percent', 'Value'));
        ALTER TABLE preauthorized_payment ADD CONSTRAINT preauthorized_payment_payment_method_discriminator_d_ck CHECK (payment_method_discriminator = 'LeasePaymentMethod');
        ALTER TABLE preauthorized_payment ADD CONSTRAINT preauthorized_payment_tenant_discriminator_d_ck CHECK (tenant_discriminator= 'Tenant');
        ALTER TABLE tenant_info ADD CONSTRAINT tenant_info_role_e_ck CHECK ((role) IN ('Applicant', 'CoApplicant', 'Dependent', 'Guarantor'));


        
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        -- Drop indexes
        DROP INDEX billing_type_payment_frequency_billing_cycle_start_day_idx;
        
        -- Create indexes
        CREATE UNIQUE INDEX billing_billing_type_billing_period_billing_cycle_start_day_idx ON billing_billing_type USING btree (billing_period, billing_cycle_start_day);
        CREATE INDEX field_user_name_idx ON field_user USING btree (name);
        CREATE UNIQUE INDEX field_user_email_idx ON field_user USING btree (LOWER(email));
        CREATE INDEX lease_billing_policy$available_billing_types_owner_idx ON lease_billing_policy$available_billing_types USING btree (owner);
        CREATE INDEX padpolicy$debit_balance_types_owner_idx ON padpolicy$debit_balance_types USING btree (owner);
        CREATE INDEX product_item_type$yardi_charge_codes_owner_idx ON product_item_type$yardi_charge_codes USING btree (owner);
               
        
END;
$$
LANGUAGE plpgsql VOLATILE;

        
