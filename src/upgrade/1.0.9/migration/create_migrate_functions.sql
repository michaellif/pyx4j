/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             VISTA-2778 (future version 1.0.9) PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_109(v_schema_name TEXT) RETURNS VOID AS
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
        
        -- Foreign Keys
        
        ALTER TABLE concession_v DROP CONSTRAINT concession_v_product_item_type_fk;
        ALTER TABLE deposit_policy_item DROP CONSTRAINT deposit_policy_item_product_type_fk;
        ALTER TABLE lease_adjustment DROP CONSTRAINT lease_adjustment_item_type_fk;
        ALTER TABLE lease_adjustment_policy_item DROP CONSTRAINT lease_adjustment_policy_item_lease_adjustment_reason_fk;
        ALTER TABLE lease_adjustment_reason DROP CONSTRAINT lease_adjustment_reason_gl_code_fk;
        ALTER TABLE pet_constraints DROP CONSTRAINT pet_constraints_pet_fk;
        ALTER TABLE product_item DROP CONSTRAINT product_item_item_type_fk;
        ALTER TABLE product_item_type DROP CONSTRAINT product_item_type_gl_code_fk;
        ALTER TABLE product_tax_policy_item DROP CONSTRAINT product_tax_policy_item_product_item_type_fk;
        ALTER TABLE yardi_charge_code DROP CONSTRAINT yardi_charge_code_product_item_type_fk;


        -- Check Constraints
        
        ALTER TABLE aging_buckets DROP CONSTRAINT aging_buckets_debit_type_e_ck;
        ALTER TABLE arpolicy DROP CONSTRAINT arpolicy_credit_debit_rule_e_ck;
        ALTER TABLE concession_v DROP CONSTRAINT concession_v_product_item_type_discriminator_d_ck;
        ALTER TABLE deposit_policy_item DROP CONSTRAINT deposit_policy_item_product_type_discriminator_d_ck;
        ALTER TABLE lead DROP CONSTRAINT lead_lease_type_e_ck;
        ALTER TABLE lease_adjustment_reason DROP CONSTRAINT lease_adjustment_reason_action_type_e_ck;
        ALTER TABLE lease DROP CONSTRAINT lease_lease_type_e_ck;
        ALTER TABLE padpolicy DROP CONSTRAINT padpolicy_charge_type_e_ck;
        ALTER TABLE padpolicy_item DROP CONSTRAINT padpolicy_item_debit_type_e_ck;
        ALTER TABLE pet_constraints DROP CONSTRAINT pet_constraints_pet_discriminator_d_ck;
        ALTER TABLE product DROP CONSTRAINT product_feature_type_ck;
        ALTER TABLE product DROP CONSTRAINT product_feature_type_e_ck;
        ALTER TABLE product_item DROP CONSTRAINT product_item_item_type_discriminator_d_ck;
        ALTER TABLE product_item_type DROP CONSTRAINT product_item_type_feature_type_e_ck;
        ALTER TABLE product_item_type DROP CONSTRAINT product_item_type_id_discriminator_ck;
        ALTER TABLE product_item_type DROP CONSTRAINT product_item_type_service_type_e_ck;
        ALTER TABLE product DROP CONSTRAINT product_service_type_ck;
        ALTER TABLE product DROP CONSTRAINT product_service_type_e_ck;
        ALTER TABLE product_tax_policy_item DROP CONSTRAINT product_tax_policy_item_product_item_type_discriminator_d_ck;
        ALTER TABLE yardi_charge_code DROP CONSTRAINT yardi_charge_code_product_item_type_discriminator_d_ck;

        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP TABLES 
        ***
        ***     ======================================================================================================
        **/
        
        
        
        /**
        ***     ======================================================================================================
        ***
        ***             NEW AND ALTERED TABLES 
        ***
        ***     ======================================================================================================
        **/
        
        -- aging_buckets
        
        ALTER TABLE aging_buckets ADD COLUMN ar_code VARCHAR(50);
        
        -- arcode
        
        CREATE TABLE arcode
        (
                id                              BIGINT                          NOT NULL,
                code_type                       VARCHAR(50),
                name                            VARCHAR(50),
                gl_code                         BIGINT,
                updated                         TIMESTAMP,
                default_code                    BOOLEAN,
                lad_id                          BIGINT,                        -- lease_adjustment_reason.id - to be dropped at the end
                pit_id                          BIGINT,                        -- product_item_type.id - also not gonna last
                        CONSTRAINT      arcode_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE arcode OWNER TO vista;
        
        
        -- billing_invoice_line_item
        
        ALTER TABLE billing_invoice_line_item ADD COLUMN ar_code BIGINT;
        
        
        -- concession_v
        
        ALTER TABLE concession_v ADD COLUMN product_code BIGINT;
        
        -- deposit_policy_item
        
        ALTER TABLE deposit_policy_item ADD COLUMN product_code BIGINT;
        
        -- lease_adjustment
        
        ALTER TABLE lease_adjustment ADD COLUMN code BIGINT;
        
        -- lease_adjustment_policy_item
        
        ALTER TABLE lease_adjustment_policy_item ADD COLUMN code BIGINT;
        
        -- maintenance_request
        
        ALTER TABLE maintenance_request ADD COLUMN category BIGINT;
        
        -- maintenance_request_category
        
        CREATE TABLE maintenance_request_category
        (
                id                      BIGINT                  NOT NULL,
                name                    VARCHAR(500),
                        CONSTRAINT      maintenance_request_category_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE maintenance_request_category OWNER TO vista;
        
        -- maintenance_request_category$sub_categories
        
        CREATE TABLE maintenance_request_category$sub_categories
        (
                id                      BIGINT                  NOT NULL,
                owner                   BIGINT,
                value                   BIGINT,
                seq                     INTEGER,
                        CONSTRAINT      maintenance_request_category$sub_categories_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE maintenance_request_category$sub_categories OWNER TO vista;
        
        
        -- padpolicy_item
        
        ALTER TABLE padpolicy_item RENAME COLUMN debit_type TO debit_type_old;
        
        ALTER TABLE padpolicy_item ADD COLUMN debit_type BIGINT;
        
        
        -- payment_record
        
        ALTER TABLE payment_record ADD COLUMN notice VARCHAR(500);
        
        -- product
        
        ALTER TABLE product ADD COLUMN code_type VARCHAR(50);
        
        -- product_item
        
        ALTER TABLE product_item ADD COLUMN code BIGINT;
        
        -- product_tax_policy_item
        
        ALTER TABLE product_tax_policy_item ADD COLUMN product_code BIGINT;
        
        -- yardi_charge_code
        
        ALTER TABLE yardi_charge_code ADD COLUMN ar_code BIGINT;
        
        -- yardi_service_request
        
        CREATE TABLE yardi_service_request
        (
                id                              BIGINT          NOT NULL,
                request_id                      INT,
                property_code                   VARCHAR(500),
                unit_code                       VARCHAR(500),
                tenant_code                     VARCHAR(500),
                vendor_code                     VARCHAR(500),
                request_description_brief       VARCHAR(500),
                request_description_full        VARCHAR(250),
                priority                        VARCHAR(500),
                permission_to_enter             BOOLEAN,
                access_notes                    VARCHAR(500),
                problem_description             VARCHAR(500),
                technical_notes                 VARCHAR(500),
                tenant_caused                   BOOLEAN,
                requestor_name                  VARCHAR(500),
                requestor_phone                 VARCHAR(500),
                requestor_email                 VARCHAR(500),
                authorized_by                   VARCHAR(500),
                current_status                  VARCHAR(500),
                resolution                      VARCHAR(500),
                        CONSTRAINT      yardi_service_request_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE yardi_service_request OWNER TO vista;
        
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        -- aging_buckets
        
        EXECUTE 'UPDATE '||v_schema_name||'.aging_buckets '
                ||'SET  ar_code = '
                ||'     CASE WHEN debit_type = ''accountCharge'' THEN ''AccountCharge'' '
                ||'     WHEN debit_type = ''addOn'' THEN ''AddOn'' '
                ||'     WHEN debit_type = ''booking'' THEN ''OneTime'' '
                ||'     WHEN debit_type = ''deposit'' THEN ''Deposit'' '
                ||'     WHEN debit_type = ''latePayment'' THEN ''LatePayment'' '
                ||'     WHEN debit_type = ''lease'' THEN ''Residential'' '
                ||'     WHEN debit_type = ''locker'' THEN ''Locker'' '
                ||'     WHEN debit_type = ''nsf'' THEN ''NSF'' '
                ||'     WHEN debit_type = ''other'' THEN ''ExternalCharge'' '
                ||'     WHEN debit_type = ''parking'' THEN ''Parking'' '
                ||'     WHEN debit_type = ''pet'' THEN ''Pet'' '
                ||'     WHEN debit_type = ''total'' THEN NULL '
                ||'     WHEN debit_type = ''utility'' THEN ''Utility'' END ';
        
        
        -- arcode data import from lease_adjustment_type
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.arcode (id,code_type,name,gl_code,updated,lad_id) '
                ||'(SELECT      nextval(''public.arcode_seq'') AS id, '
                ||'             CASE WHEN action_type = ''charge'' THEN ''AccountCharge'' '
                ||'             WHEN action_type = ''credit'' THEN ''AccountCredit'' END AS code_type, '
                ||'             name,gl_code,updated,id AS lad_id '
                ||' FROM        '||v_schema_name||'.lease_adjustment_reason '
                ||' ORDER BY    id )';
                
        
        -- arcode data import from product_item_type
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.arcode (id,code_type,name,gl_code,updated,pit_id) '
                ||'(SELECT      nextval(''public.arcode_seq'') AS id, '
                ||'             CASE WHEN service_type = ''commercialUnit'' THEN ''Commercial'' '
                ||'             WHEN service_type = ''residentialShortTermUnit'' THEN ''ResidentialShortTerm'' '
                ||'             WHEN service_type = ''residentialUnit'' THEN ''Residential'' '
                ||'             WHEN feature_type = ''addOn'' THEN ''AddOn'' '
                ||'             WHEN feature_type = ''booking'' THEN ''Residential'' '
                ||'             WHEN feature_type = ''locker'' THEN ''Locker'' '
                ||'             WHEN feature_type = ''oneTimeCharge'' THEN ''OneTime'' '
                ||'             WHEN feature_type = ''parking'' THEN ''Parking'' '
                ||'             WHEN feature_type = ''pet'' THEN ''Pet'' '
                ||'             WHEN feature_type = ''utility'' THEN ''Utility'' END AS code_type,'
                ||'             name,gl_code,updated,id AS pit_id '
                ||' FROM        '||v_schema_name||'.product_item_type '
                ||' ORDER BY    id ) ';
                
              
        
        
        -- arpolicy
        
        EXECUTE 'UPDATE '||v_schema_name||'.arpolicy '
                ||'SET credit_debit_rule = '
                ||'CASE WHEN credit_debit_rule IN (''byDueDate'',''byAgingBucketAndDebitType'') THEN ''oldestDebtFirst'' '
                ||'WHEN credit_debit_rule = ''byDebitType'' THEN ''rentDebtLast'' END ';
        
        
        -- billing_invoice_line_item
        
        EXECUTE 'WITH t AS (SELECT      id,CASE WHEN debit_type = ''accountCharge'' THEN ''AccountCharge'' '
                ||'             WHEN debit_type = ''addOn'' THEN ''AddOn'' '
                ||'             WHEN debit_type = ''booking'' THEN ''OneTime'' '
                ||'             WHEN debit_type = ''deposit'' THEN ''Deposit'' '
                ||'             WHEN debit_type = ''latePayment'' THEN ''LatePayment'' '
                ||'             WHEN debit_type = ''lease'' THEN ''Residential'' '
                ||'             WHEN debit_type = ''locker'' THEN ''Locker'' '
                ||'             WHEN debit_type = ''nsf'' THEN ''NSF'' '
                ||'             WHEN debit_type = ''other'' THEN ''ExternalCharge'' '
                ||'             WHEN debit_type = ''parking'' THEN ''Parking'' '
                ||'             WHEN debit_type = ''pet'' THEN ''Pet'' '
                ||'             WHEN debit_type = ''total'' THEN NULL '
                ||'             WHEN debit_type = ''utility'' THEN ''Utility'' END  AS debit_type_new '
                ||'     FROM    '||v_schema_name||'.billing_invoice_line_item ) '
                ||'UPDATE '||v_schema_name||'.billing_invoice_line_item AS a '
                ||'SET  ar_code = b.arcode '
                ||'FROM (SELECT t.id,a.id AS arcode '
                ||'     FROM t '
                ||'     JOIN '||v_schema_name||'.arcode a ON (t.debit_type_new = a.name)) AS b '
                ||'WHERE a.id = b.id ';   
        
        
        -- concession_v
        
        EXECUTE 'UPDATE '||v_schema_name||'.concession_v AS c '
                ||'SET  product_code = a.id '
                ||'FROM '||v_schema_name||'.arcode  AS a '
                ||'WHERE c.product_item_type = a.pit_id ';
                
        
        -- deposit_policy_item
        
        EXECUTE 'UPDATE '||v_schema_name||'.deposit_policy_item AS d '
                ||'SET  product_code = a.id '
                ||'FROM '||v_schema_name||'.arcode  AS a '
                ||'WHERE d.product_type = a.pit_id ';
        
        
        -- html_content
        
        ALTER TABLE html_content ADD COLUMN updated TIMESTAMP;
                
        
        -- lead
        
        EXECUTE 'UPDATE '||v_schema_name||'.lead '
                ||'SET lease_type = ''Residential'' '
                ||'WHERE lease_type = ''residentialUnit'' ';
                
                
        -- lease
        
        EXECUTE 'UPDATE '||v_schema_name||'.lease '
                ||'SET lease_type = ''Residential'' '
                ||'WHERE lease_type = ''residentialUnit'' ';
                
                
        -- lease_adjustment
        
        EXECUTE 'UPDATE '||v_schema_name||'.lease_adjustment  AS l '
                ||'SET  code = a.id '
                ||'FROM '||v_schema_name||'.arcode AS a '
                ||'WHERE l.item_type = a.lad_id ';
                
        -- lease_adjustment_policy_item
        
        EXECUTE 'UPDATE '||v_schema_name||'.lease_adjustment_policy_item AS l '
                ||'SET  code = a.id '
                ||'FROM '||v_schema_name||'.arcode AS a '
                ||'WHERE        l.lease_adjustment_reason = a.lad_id ';
                
        -- padpolicy_item
       
        EXECUTE 'WITH t AS (SELECT debit_type_old, '
                ||'             CASE WHEN debit_type_old = ''accountCharge'' THEN ''AccountCharge'' '
                ||'             WHEN debit_type_old = ''addOn'' THEN ''AddOn'' '
                ||'             WHEN debit_type_old = ''booking'' THEN ''OneTime'' '
                ||'             WHEN debit_type_old = ''deposit'' THEN ''Deposit'' '
                ||'             WHEN debit_type_old = ''latePayment'' THEN ''LatePayment'' '
                ||'             WHEN debit_type_old = ''lease'' THEN ''Residential'' '
                ||'             WHEN debit_type_old = ''locker'' THEN ''Locker'' '
                ||'             WHEN debit_type_old = ''nsf'' THEN ''NSF'' '
                ||'             WHEN debit_type_old = ''other'' THEN ''ExternalCharge'' '
                ||'             WHEN debit_type_old = ''parking'' THEN ''Parking'' '
                ||'             WHEN debit_type_old = ''pet'' THEN ''Pet'' '
                ||'             WHEN debit_type_old = ''total'' THEN NULL '
                ||'             WHEN debit_type_old = ''utility'' THEN ''Utility'' END  AS debit_type '
                ||'     FROM    '||v_schema_name||'.padpolicy_item ) '
                ||'UPDATE '||v_schema_name||'.padpolicy_item AS a '
                ||'SET  debit_type = b.id '
                ||'FROM (SELECT t.debit_type_old,a.id '
                ||'     FROM t '
                ||'     JOIN '||v_schema_name||'.arcode a ON (t.debit_type = a.name)) AS b '
                ||'WHERE a.debit_type_old = b.debit_type_old ';   
        
        
        -- pet_constraints
        
        EXECUTE 'UPDATE '||v_schema_name||'.pet_constraints AS p '
                ||'SET  pet = a.id '
                ||'FROM '||v_schema_name||'.arcode AS a '
                ||'WHERE p.pet = a.pit_id ';    
                
         
        -- product 
        
        EXECUTE 'UPDATE '||v_schema_name||'.product AS p '
                ||'SET  code_type = '
                ||'CASE WHEN service_type = ''commercialUnit'' THEN ''Commercial'' '
                ||'WHEN service_type = ''residentialShortTermUnit'' THEN ''ResidentialShortTerm'' '
                ||'WHEN service_type = ''residentialUnit'' THEN ''Residential'' '
                ||'WHEN feature_type = ''addOn'' THEN ''AddOn'' '
                ||'WHEN feature_type = ''booking'' THEN ''Residential'' '
                ||'WHEN feature_type = ''locker'' THEN ''Locker'' '
                ||'WHEN feature_type = ''oneTimeCharge'' THEN ''OneTime'' '
                ||'WHEN feature_type = ''parking'' THEN ''Parking'' '
                ||'WHEN feature_type = ''pet'' THEN ''Pet'' '
                ||'WHEN feature_type = ''utility'' THEN ''Utility'' END ';
            
        
        -- product_item
        
        EXECUTE 'UPDATE '||v_schema_name||'.product_item  AS p '
                ||'SET  code = a.id '
                ||'FROM '||v_schema_name||'.arcode a '
                ||'WHERE p.item_type = a.pit_id ';
        
        
        -- product_tax_policy_item
        
        EXECUTE 'UPDATE '||v_schema_name||'.product_tax_policy_item AS p '
                ||'SET  product_code = a.id '
                ||'FROM '||v_schema_name||'.arcode a '
                ||'WHERE p.product_item_type = a.pit_id ';
        
        
        -- reports_settings_holder
        
        EXECUTE 'DELETE FROM '||v_schema_name||'.reports_settings_holder '
                ||'WHERE        class_name = ''PapReportMetadata'' ';
        
                
        -- yardi_charge_code
        
        EXECUTE 'UPDATE '||v_schema_name||'.yardi_charge_code AS y '
                ||'SET  ar_code = a.id '
                ||'FROM '||v_schema_name||'.arcode a '
                ||'WHERE y.product_item_type = a.pit_id ';
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        -- aging_buckets
        
        ALTER TABLE aging_buckets DROP COLUMN debit_type;
        
        -- arcode 
        
        ALTER TABLE arcode      DROP COLUMN lad_id,
                                DROP COLUMN pit_id;
                                
        --  billing_bill
        
        ALTER TABLE  billing_bill DROP COLUMN previous_cycle_bill;
        
        -- billing_invoice_line_item
        
        ALTER TABLE billing_invoice_line_item   DROP COLUMN target_date,
                                                DROP COLUMN debit_type ;
        
        -- concession_v
        
        ALTER TABLE concession_v        DROP COLUMN product_item_type,
                                        DROP COLUMN product_item_type_discriminator;
                                        
        -- deposit_policy_item
        
        ALTER TABLE deposit_policy_item DROP COLUMN product_type,
                                        DROP COLUMN product_type_discriminator;
                                        
        -- lease_adjustment
        
        ALTER TABLE lease_adjustment    DROP COLUMN item_type;
        
        -- lease_adjustment_policy_item
        
        ALTER TABLE lease_adjustment_policy_item        DROP COLUMN lease_adjustment_reason;
        
        
        -- lease_adjustment_reason
        
        DROP TABLE lease_adjustment_reason;
        
        
        -- padpolicy_item
        
        ALTER TABLE padpolicy_item DROP COLUMN debit_type_old;
        
        -- pet_constraints
        
        ALTER TABLE pet_constraints     DROP COLUMN pet_discriminator;
        
        
        -- product
        
        ALTER TABLE product     DROP COLUMN feature_type,
                                DROP COLUMN service_type;
                                
                                
        -- product_item
        
        ALTER TABLE product_item        DROP COLUMN item_type,
                                        DROP COLUMN item_type_discriminator;
                                        
                                        
        -- product_item_type
        
        DROP TABLE product_item_type;
        
        
        -- product_tax_policy_item
        
        ALTER TABLE product_tax_policy_item     DROP COLUMN product_item_type,
                                                DROP COLUMN product_item_type_discriminator;
                                                
        -- yardi_charge_code
        
        ALTER TABLE yardi_charge_code   DROP COLUMN product_item_type,
                                        DROP COLUMN product_item_type_discriminator;
         
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        -- Not Null
        
        ALTER TABLE product ALTER COLUMN code_type SET NOT NULL;
        ALTER TABLE yardi_charge_code ALTER COLUMN ar_code SET NOT NULL;
        
        -- Foreign Keys
        
        ALTER TABLE arcode ADD CONSTRAINT arcode_gl_code_fk FOREIGN KEY(gl_code) REFERENCES gl_code(id);
        ALTER TABLE billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_ar_code_fk FOREIGN KEY(ar_code) REFERENCES arcode(id);
        ALTER TABLE concession_v ADD CONSTRAINT concession_v_product_code_fk FOREIGN KEY(product_code) REFERENCES arcode(id);
        ALTER TABLE deposit_policy_item ADD CONSTRAINT deposit_policy_item_product_code_fk FOREIGN KEY(product_code) REFERENCES arcode(id);
        ALTER TABLE lease_adjustment ADD CONSTRAINT lease_adjustment_code_fk FOREIGN KEY(code) REFERENCES arcode(id);
        ALTER TABLE lease_adjustment_policy_item ADD CONSTRAINT lease_adjustment_policy_item_code_fk FOREIGN KEY(code) REFERENCES arcode(id);
        ALTER TABLE padpolicy_item ADD CONSTRAINT padpolicy_item_debit_type_fk FOREIGN KEY(debit_type) REFERENCES arcode(id);
        ALTER TABLE pet_constraints ADD CONSTRAINT pet_constraints_pet_fk FOREIGN KEY(pet) REFERENCES arcode(id);
        ALTER TABLE product_item ADD CONSTRAINT product_item_code_fk FOREIGN KEY(code) REFERENCES arcode(id);
        ALTER TABLE product_tax_policy_item ADD CONSTRAINT product_tax_policy_item_product_code_fk FOREIGN KEY(product_code) REFERENCES arcode(id);
        ALTER TABLE yardi_charge_code ADD CONSTRAINT yardi_charge_code_ar_code_fk FOREIGN KEY(ar_code) REFERENCES arcode(id);

        -- Check Constraints
        
         ALTER TABLE aging_buckets ADD CONSTRAINT aging_buckets_ar_code_e_ck 
                CHECK ((ar_code) IN ('AccountCharge', 'AccountCredit', 'AddOn', 'CarryForwardCharge', 'Commercial', 'Deposit', 'ExternalCharge', 'ExternalCredit',
                 'LatePayment', 'Locker', 'NSF', 'OneTime', 'Parking', 'Pet', 'Residential', 'ResidentialShortTerm', 'Utility'));
        ALTER TABLE arcode ADD CONSTRAINT arcode_code_type_e_ck 
                CHECK ((code_type) IN ('AccountCharge', 'AccountCredit', 'AddOn', 'CarryForwardCharge', 'Commercial', 'Deposit', 'ExternalCharge', 'ExternalCredit',
                 'LatePayment', 'Locker', 'NSF', 'OneTime', 'Parking', 'Pet', 'Residential', 'ResidentialShortTerm', 'Utility'));
        ALTER TABLE arpolicy ADD CONSTRAINT arpolicy_credit_debit_rule_e_ck CHECK ((credit_debit_rule) IN ('oldestDebtFirst', 'rentDebtLast'));
        ALTER TABLE lead ADD CONSTRAINT lead_lease_type_e_ck 
                CHECK ((lease_type) IN ('AccountCharge', 'AccountCredit', 'AddOn', 'CarryForwardCharge', 'Commercial', 'Deposit', 'ExternalCharge', 'ExternalCredit',
                 'LatePayment', 'Locker', 'NSF', 'OneTime', 'Parking', 'Pet', 'Residential', 'ResidentialShortTerm', 'Utility'));
        ALTER TABLE lease ADD CONSTRAINT lease_lease_type_e_ck 
                CHECK ((lease_type) IN ('AccountCharge', 'AccountCredit', 'AddOn', 'CarryForwardCharge', 'Commercial', 'Deposit', 'ExternalCharge', 'ExternalCredit',
                 'LatePayment', 'Locker', 'NSF', 'OneTime', 'Parking', 'Pet', 'Residential', 'ResidentialShortTerm', 'Utility'));
        ALTER TABLE padpolicy ADD CONSTRAINT padpolicy_charge_type_e_ck CHECK ((charge_type) IN ('Any', 'FixedAmount', 'OwingBalance'));
        ALTER TABLE product ADD CONSTRAINT product_code_type_e_ck 
                CHECK ((code_type) IN ('AccountCharge', 'AccountCredit', 'AddOn', 'CarryForwardCharge', 'Commercial', 'Deposit', 'ExternalCharge', 'ExternalCredit',
                 'LatePayment', 'Locker', 'NSF', 'OneTime', 'Parking', 'Pet', 'Residential', 'ResidentialShortTerm', 'Utility'));

        
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        CREATE INDEX billing_invoice_line_item_billing_account_idx ON billing_invoice_line_item USING btree (billing_account);
        CREATE INDEX billing_invoice_line_item_billing_account_discriminator_idx ON billing_invoice_line_item USING btree (billing_account_discriminator);
        CREATE INDEX maintenance_request_category$sub_categories_owner_idx ON maintenance_request_category$sub_categories USING btree (owner);
        
        
        -- Finishing touch
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.0.9',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;

        
