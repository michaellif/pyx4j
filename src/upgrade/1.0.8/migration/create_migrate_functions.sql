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
DECLARE 
        v_billing_type_id               BIGINT := NULL;
        v_billing_accounts              INT;
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
        ALTER TABLE lease_participant DROP CONSTRAINT lease_participant_preauthorized_payment_fk;

        -- Check constraints
        ALTER TABLE aging_buckets DROP CONSTRAINT aging_buckets_debit_type_e_ck;
        ALTER TABLE billing_billing_type DROP CONSTRAINT billing_billing_type_payment_frequency_e_ck;
        ALTER TABLE billing_invoice_line_item DROP CONSTRAINT billing_invoice_line_item_debit_type_e_ck;
        ALTER TABLE billing_debit_credit_link DROP CONSTRAINT billing_debit_credit_link_credit_item_discriminator_d_ck;
        ALTER TABLE billing_invoice_line_item DROP CONSTRAINT billing_invoice_line_item_id_discriminator_ck;
        ALTER TABLE lease DROP CONSTRAINT lease_payment_frequency_e_ck;
        ALTER TABLE lease_participant DROP CONSTRAINT lease_participant_preauthorized_payment_discriminator_d_ck;

        
        
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
                                                ADD COLUMN actual_pad_generation_date DATE,
                                                ADD COLUMN actual_bill_execution_date DATE,
                                                ADD COLUMN pad_execution_date DATE;
                                                
        -- billing_billing_type
        
        ALTER TABLE billing_billing_type RENAME COLUMN payment_frequency TO billing_period;
                                                
       
       -- billing_invoice_line_item
       
       ALTER TABLE billing_invoice_line_item ADD COLUMN billing_cycle BIGINT;
       
                                             
        -- insurance_certificate
        ALTER TABLE insurance_certificate ADD COLUMN total_anniversary_first_month_payable NUMERIC(18,2);
        
        
        
        -- lease_billing_type_policy_item
        
        CREATE TABLE lease_billing_type_policy_item
        (
                id                                      BIGINT                          NOT NULL,
                lease_billing_policy                    BIGINT                          NOT NULL,
                order_in_parent                         INT,
                billing_period                          VARCHAR(50),
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
                padpolicy                               BIGINT                          NOT NULL,
                order_in_parent                         INT,
                debit_type                              VARCHAR(50),
                owing_balance_type                      VARCHAR(50),
                        CONSTRAINT      padpolicy_item_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE padpolicy_item OWNER TO vista;
        
        -- payment_information
        
        ALTER TABLE payment_information RENAME COLUMN payment_method_is_one_time_payment TO payment_method_is_profiled_method;
        
        -- payment_method
        
        ALTER TABLE payment_method RENAME COLUMN is_one_time_payment TO is_profiled_method;
        
        -- payment_record
        
        ALTER TABLE payment_record      ADD COLUMN pad_billing_cycle BIGINT,
                                        ADD COLUMN preauthorized_payment BIGINT;
        
        -- preauthorized_payment
        
        CREATE TABLE preauthorized_payment
        (
                id                                      BIGINT                          NOT NULL,
                amount_type                             VARCHAR(50),
                percent                                 NUMERIC(18,2),
                value                                   NUMERIC(18,2),
                is_deleted                              BOOLEAN,
                payment_method_discriminator            VARCHAR(50),
                payment_method                          BIGINT,
                comments                                VARCHAR(40),
                tenant_discriminator                    VARCHAR(50)                     NOT NULL,
                tenant                                  BIGINT                          NOT NULL,
                creation_date                           DATE,
               -- order_in_parent                         INT,
                        CONSTRAINT      preauthorized_payment_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE preauthorized_payment OWNER TO vista;
        
        
        -- yardi_charge_code
        
        CREATE TABLE yardi_charge_code
        (
                id                                      BIGINT                          NOT NULL,
                product_item_type_discriminator         VARCHAR(50)                     NOT NULL,
                product_item_type                       BIGINT                          NOT NULL,
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
                
        -- Create lease_billing_type_policy_item 
        
        EXECUTE 'INSERT INTO lease_billing_type_policy_item (id,lease_billing_policy,order_in_parent,billing_period,billing_cycle_start_day,'
                ||'bill_execution_day_offset,payment_due_day_offset,final_due_day_offset,pad_calculation_day_offset,pad_execution_day_offset) '
                ||'(SELECT nextval(''public.lease_billing_type_policy_item_seq'') AS id, l.id AS lease_billing_policy,0 AS order_in_parent,b.billing_period, '
                ||'l.default_billing_cycle_sart_day AS billing_cycle_start_day,-15 AS bill_execution_day_offset,'
                ||'0 AS payment_due_day_offset,15 AS final_due_day_offset, -3 AS pad_calculation_day_offset,'
                ||'0 AS pad_execution_day_offset '
                ||'FROM         '||v_schema_name||'.lease_billing_policy l, '
                ||'             (SELECT DISTINCT billing_period FROM '||v_schema_name||'.billing_account ) AS b )';  
                
       
        -- billing_invoice_line_item
        
        EXECUTE 'UPDATE '||v_schema_name||'.billing_invoice_line_item AS a '
                ||'SET billing_cycle = b.billing_cycle '
                ||'FROM         (SELECT  li.id,b.billing_cycle '
                ||'             FROM    '||v_schema_name||'.billing_invoice_line_item li '
                ||'             JOIN    '||v_schema_name||'.billing_bill$line_items bb ON (bb.value = li.id) '
                ||'             JOIN    '||v_schema_name||'.billing_bill b ON (bb.owner = b.id) '
                ||'             WHERE   b.bill_status = ''Confirmed'' ) AS b '
                ||'WHERE  a.id = b.id ';
                
                
        EXECUTE 'UPDATE '||v_schema_name||'.billing_invoice_line_item AS a '
                ||'SET billing_cycle = b.billing_cycle '
                ||'FROM         (SELECT  li.id, MIN(bc.id) AS billing_cycle '
                ||'             FROM     '||v_schema_name||'.billing_invoice_line_item li ' 
                ||'             JOIN     '||v_schema_name||'.billing_billing_cycle bc ON (li.post_date <= bc.billing_cycle_start_date) '
                ||'             WHERE   li.billing_cycle IS NULL '
                ||'             AND     li.post_date IS NOT NULL '
                ||'             GROUP BY li.id ) AS b '
                ||'WHERE  a.id = b.id ';
         
        
        EXECUTE 'UPDATE '||v_schema_name||'.billing_invoice_line_item '
                ||'SET  due_date = post_date '
                ||'WHERE  id_discriminator = ''YardiCharge'' ';
                
       
        EXECUTE 'UPDATE '||v_schema_name||'.billing_invoice_line_item '
                ||'SET  post_date = NULL '
                ||'WHERE  id_discriminator = ''YardiCharge'' '; 
        
        
        -- Add padpolicy  
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.padpolicy (id,updated,node_discriminator,node,charge_type) '
                ||'(SELECT nextval(''public.padpolicy_seq'') AS id, DATE_TRUNC(''sec'',current_timestamp) AS updated, '
                ||'''OrganizationPoliciesNode'' AS node_discriminator, id AS node, ''FixedAmount'' AS charge_type '
                ||'FROM         '||v_schema_name||'.organization_policies_node )'; 
                
        
        -- Update payment_information
        EXECUTE 'UPDATE '||v_schema_name||'.payment_information '
                ||'SET payment_method_is_profiled_method = '
                ||'CASE WHEN payment_method_is_profiled_method = TRUE THEN FALSE '
                ||'WHEN payment_method_is_profiled_method = FALSE THEN TRUE END ';
                
        -- Update payment_method
        
        EXECUTE 'UPDATE '||v_schema_name||'.payment_method '
                ||'SET is_profiled_method = '
                ||'CASE WHEN is_profiled_method = TRUE THEN FALSE '
                ||'WHEN is_profiled_method = FALSE THEN TRUE END'; 
        
        
        -- billing_billing_cycle
        
        EXECUTE 'UPDATE '||v_schema_name||'.billing_billing_cycle '
                ||'SET  target_pad_generation_date = billing_cycle_start_date -3 ';
        
        EXECUTE 'UPDATE '||v_schema_name||'.billing_billing_cycle '
                ||'SET  pad_execution_date = billing_cycle_start_date ';
                      
                       
        -- check if billing_account exists
        EXECUTE 'SELECT COALESCE(COUNT(id),0) AS cnt FROM '||v_schema_name||'.billing_account '
                INTO v_billing_accounts;
        
        
        IF (v_billing_accounts > 0)
        THEN
                EXECUTE 'SELECT id FROM '||v_schema_name||'.billing_billing_type '              
                        ||'WHERE        billing_period = ''Monthly'' '
                        ||'AND          billing_cycle_start_day = 1 '
                        INTO v_billing_type_id ;
                
                IF (v_billing_type_id IS NULL) 
                THEN
                        SELECT nextval('public.billing_billing_type_seq') INTO v_billing_type_id;
                
                        EXECUTE 'INSERT INTO '||v_schema_name||'.billing_billing_type '
                                ||'(id,billing_period,billing_cycle_start_day) VALUES ('
                                ||v_billing_type_id||',''Monthly'',1)';
                END IF;
                
                 EXECUTE 'UPDATE '||v_schema_name||'.billing_account '
                        ||'SET  billing_type = '||v_billing_type_id||' '
                        ||'WHERE billing_type IS NULL';
                
                EXECUTE 'UPDATE '||v_schema_name||'.billing_account '
                        ||'SET  final_due_day_offset = 15 '
                        ||'WHERE        final_due_day_offset IS NULL';
                        
                EXECUTE 'UPDATE '||v_schema_name||'.billing_account '
                        ||'SET  payment_due_day_offset = 0 '
                        ||'WHERE        payment_due_day_offset IS NULL ';
                
        END IF;
        
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        -- billing_bill
        
        ALTER TABLE billing_bill DROP COLUMN latest_bill_in_cycle;
        
        -- billing_billing_cycle
        
        ALTER TABLE billing_billing_cycle DROP COLUMN execution_target_date;
        
        -- billing_billing_type
        
        ALTER TABLE billing_billing_type DROP COLUMN billing_cycle_target_day;
        
        -- billing_invoice_line_item
        
        ALTER TABLE billing_invoice_line_item DROP COLUMN claimed;
        
        -- lease
        
        ALTER TABLE lease DROP COLUMN payment_frequency;
        
        -- lease_billing_policy
        
        ALTER TABLE lease_billing_policy        DROP COLUMN default_billing_cycle_sart_day,
                                                DROP COLUMN use_default_billing_cycle_sart_day;
                                                
        
        -- lease_participant
        
        ALTER TABLE lease_participant           DROP COLUMN preauthorized_payment,
                                                DROP COLUMN preauthorized_payment_discriminator;
         
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        -- Foreign keys
        ALTER TABLE billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_billing_cycle_fk FOREIGN KEY(billing_cycle) REFERENCES billing_billing_cycle(id);
        ALTER TABLE lease_billing_type_policy_item ADD CONSTRAINT lease_billing_type_policy_item_lease_billing_policy_fk FOREIGN KEY(lease_billing_policy) REFERENCES lease_billing_policy(id);
        ALTER TABLE padpolicy_item ADD CONSTRAINT padpolicy_item_padpolicy_fk FOREIGN KEY(padpolicy) REFERENCES padpolicy(id);       
        ALTER TABLE payment_record ADD CONSTRAINT payment_record_pad_billing_cycle_fk FOREIGN KEY(pad_billing_cycle) REFERENCES billing_billing_cycle(id);
        ALTER TABLE payment_record ADD CONSTRAINT payment_record_preauthorized_payment_fk FOREIGN KEY(preauthorized_payment) REFERENCES preauthorized_payment(id);
        ALTER TABLE preauthorized_payment ADD CONSTRAINT preauthorized_payment_tenant_fk FOREIGN KEY(tenant) REFERENCES lease_participant(id);
        ALTER TABLE preauthorized_payment ADD CONSTRAINT preauthorized_payment_payment_method_fk FOREIGN KEY(payment_method) REFERENCES payment_method(id);
        ALTER TABLE yardi_charge_code ADD CONSTRAINT yardi_charge_code_product_item_type_fk FOREIGN KEY(product_item_type) REFERENCES product_item_type(id);
        

                
        -- Check constraints
        ALTER TABLE aging_buckets ADD CONSTRAINT aging_buckets_debit_type_e_ck 
                CHECK ((debit_type) IN ('accountCharge', 'addOn', 'booking', 'deposit', 'latePayment', 'lease', 'locker', 'nsf', 'other', 'parking', 'pet', 'target' ,'total', 'utility'));
        ALTER TABLE billing_account ADD CONSTRAINT billing_account_billing_period_e_ck 
                CHECK ((billing_period) IN ('Annually', 'BiWeekly', 'Monthly', 'SemiAnnyally', 'SemiMonthly', 'Weekly'));
        ALTER TABLE billing_billing_type ADD CONSTRAINT billing_billing_type_billing_period_e_ck 
                CHECK ((billing_period) IN ('Annually', 'BiWeekly', 'Monthly', 'SemiAnnyally', 'SemiMonthly', 'Weekly'));
        ALTER TABLE billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_debit_type_e_ck 
                CHECK ((debit_type) IN ('accountCharge', 'addOn', 'booking', 'deposit', 'latePayment', 'lease', 'locker', 'nsf', 'other', 'parking', 'pet', 'total', 'utility'));
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
                'pet', 'total', 'utility'));
        ALTER TABLE padpolicy_item ADD CONSTRAINT padpolicy_item_owing_balance_type_e_ck CHECK ((owing_balance_type) IN ('LastBill', 'ToDateTotal'));
        ALTER TABLE padpolicy ADD CONSTRAINT padpolicy_node_discriminator_d_ck 
                CHECK ((node_discriminator) IN ('Disc Complex', 'Disc_Building', 'Disc_Country', 'Disc_Floorplan', 'Disc_Province', 'OrganizationPoliciesNode', 'Unit_BuildingElement'));
        ALTER TABLE preauthorized_payment ADD CONSTRAINT preauthorized_payment_amount_type_e_ck CHECK ((amount_type) IN ('Percent', 'Value'));
        ALTER TABLE preauthorized_payment ADD CONSTRAINT preauthorized_payment_payment_method_discriminator_d_ck CHECK (payment_method_discriminator = 'LeasePaymentMethod');
        ALTER TABLE preauthorized_payment ADD CONSTRAINT preauthorized_payment_tenant_discriminator_d_ck CHECK (tenant_discriminator= 'Tenant');
        ALTER TABLE yardi_charge_code ADD CONSTRAINT yardi_charge_code_product_item_type_discriminator_d_ck CHECK ((product_item_type_discriminator) IN ('feature', 'service'));
        


        
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
        CREATE INDEX lease_billing_type_policy_item_lease_billing_policy_idx ON lease_billing_type_policy_item USING btree (lease_billing_policy) ;
        
        UPDATE  _admin_.admin_pmc 
        SET     schema_version = '1.0.8'
        WHERE   namespace = v_schema_name;
                      
        
END;
$$
LANGUAGE plpgsql VOLATILE;

        
