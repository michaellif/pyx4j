/**
*** =================================================================================
*** @version $Revision$ ($Author$) $Date$
***
***     Migration to version 1.0.5
***
***     Since it is a huge transaction setting max_locks_per_transaction = 256
***     for running this script is higly recommended
***
*** =================================================================================
**/


/** For development environment only **/
/*
CREATE SCHEMA _dba_ ;
*/

BEGIN TRANSACTION;

SET client_min_messages = 'WARNING';

/** _admin_ schema **/

SET search_path = '_admin_';

ALTER TABLE admin_pmc_equifax_info
    ADD COLUMN report_type VARCHAR(50),
    ADD COLUMN approved BOOLEAN;



/**     public schema   **/

SET search_path = 'public';

-- Remove sequences for tables to be deleted - 55 in total

DROP SEQUENCE arrears_status_gadget_metadata$column_descriptors_seq;
DROP SEQUENCE arrears_status_gadget_metadata_seq;
DROP SEQUENCE arrears_summary_gadget_metadata$column_descriptors_seq;
DROP SEQUENCE arrears_summary_gadget_metadata_seq;
DROP SEQUENCE arrears_yoyanalysis_chart_metadata_seq;
DROP SEQUENCE building_lister$column_descriptors_seq;
DROP SEQUENCE building_lister_seq;
DROP SEQUENCE common_gadget_columns_seq;
DROP SEQUENCE common_gadget_columnsportfolio$buildings_seq;
DROP SEQUENCE dashboard_metadata$gadgets_seq;
DROP SEQUENCE demo_bar_chart2_d_seq;
DROP SEQUENCE demo_demo_seq;
DROP SEQUENCE demo_gauge_seq;
DROP SEQUENCE demo_line_chart_seq;
DROP SEQUENCE demo_pie_chart2_d_seq;
DROP SEQUENCE equifax_approval_seq;
DROP SEQUENCE equifax_result_seq;
DROP SEQUENCE feature_seq;
DROP SEQUENCE feature_v_seq;
DROP SEQUENCE gadget_docking_meta_seq;
DROP SEQUENCE guarantor_seq;
DROP SEQUENCE income_info_employer_seq;
DROP SEQUENCE income_info_other_seq;
DROP SEQUENCE income_info_seasonally_employed_seq;
DROP SEQUENCE income_info_self_employed_seq;
DROP SEQUENCE income_info_social_services_seq;
DROP SEQUENCE income_info_student_income_seq;
DROP SEQUENCE lease_v_seq;
DROP SEQUENCE lease_vlease_products$concessions_seq;
DROP SEQUENCE lease_vlease_products$feature_items_seq;
DROP SEQUENCE lease$documents_seq;
DROP SEQUENCE legal_questions_seq;
DROP SEQUENCE lister_gadget_base_metadata$column_descriptors_seq;
DROP SEQUENCE lister_gadget_base_metadata_seq;
DROP SEQUENCE note$attachments_seq;
DROP SEQUENCE note_seq;
DROP SEQUENCE notes_and_attachments$notes_seq;
DROP SEQUENCE payment_records_gadget_metadata$column_descriptors_seq;
DROP SEQUENCE payment_records_gadget_metadata$payment_method_filter_seq;
DROP SEQUENCE payment_records_gadget_metadata$payment_status_filter_seq;
DROP SEQUENCE payment_records_gadget_metadata_seq;
DROP SEQUENCE payments_summary_gadget_metadata$column_descriptors_seq;
DROP SEQUENCE payments_summary_gadget_metadata$payment_status_seq;
DROP SEQUENCE payments_summary_gadget_metadata_seq;
DROP SEQUENCE personal_income_seq;
DROP SEQUENCE service_seq;
DROP SEQUENCE service_v$concessions_seq;
DROP SEQUENCE service_v$features_seq;
DROP SEQUENCE service_v_seq;
DROP SEQUENCE tenant_seq;
DROP SEQUENCE turnover_analysis_metadata_seq;
DROP SEQUENCE unit_availability_gadget_meta$column_descriptors_seq;
DROP SEQUENCE unit_availability_gadget_meta_seq;
DROP SEQUENCE unit_availability_summary_gmeta$column_descriptors_seq;
DROP SEQUENCE unit_availability_summary_gmeta_seq;

-- Create new sequences - 19 total
CREATE SEQUENCE background_check_policy_v_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE background_check_policy_v_seq OWNER TO vista;
CREATE SEQUENCE gadget_metadata_holder_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE gadget_metadata_holder_seq OWNER TO vista;
CREATE SEQUENCE income_info_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE income_info_seq OWNER TO vista;
CREATE SEQUENCE lease_customer_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE lease_customer_seq OWNER TO vista;
CREATE SEQUENCE lease_participant_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE lease_participant_seq OWNER TO vista;
CREATE SEQUENCE lease_term_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE lease_term_seq OWNER TO vista;
CREATE SEQUENCE lease_term_v_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE lease_term_v_seq OWNER TO vista;
CREATE SEQUENCE lease_term_vlease_products$concessions_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE lease_term_vlease_products$concessions_seq OWNER TO vista;
CREATE SEQUENCE lease_term_vlease_products$feature_items_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE lease_term_vlease_products$feature_items_seq OWNER TO vista;
CREATE SEQUENCE notes_and_attachments$attachments_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE notes_and_attachments$attachments_seq OWNER TO vista;
CREATE SEQUENCE person_credit_check_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE person_credit_check_seq OWNER TO vista;
CREATE SEQUENCE person_screening_legal_questions_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE person_screening_legal_questions_seq OWNER TO vista;
CREATE SEQUENCE person_screening_personal_income_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE person_screening_personal_income_seq OWNER TO vista;
CREATE SEQUENCE person_screening_v_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE person_screening_v_seq OWNER TO vista;
CREATE SEQUENCE product_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE product_seq OWNER TO vista;
CREATE SEQUENCE product_v$concessions_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE product_v$concessions_seq OWNER TO vista;
CREATE SEQUENCE product_v$features_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE product_v$features_seq OWNER TO vista;
CREATE SEQUENCE product_v_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE product_v_seq OWNER TO vista;
CREATE SEQUENCE reports_settings_holder_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE reports_settings_holder_seq OWNER TO vista;

--  _c3p0_connection_test is not used anymore
DROP TABLE IF EXISTS _c3p0_connection_test;

/**
*** For the rest of migration plpsql function is required
**/

CREATE OR REPLACE FUNCTION _dba_.convert_id_to_string(TEXT) RETURNS TEXT AS
$$
    SELECT  CASE WHEN $1 ~ '^[0-9]+$' THEN LPAD($1,7,'0')
        ELSE regexp_replace($1,'([0-9]+)',LPAD('\1',7,'0'),'g') END;
$$
LANGUAGE SQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _dba_.migrate_to_105() RETURNS VOID AS
$$
DECLARE
    v_schema_name               VARCHAR(64);
    v_table_name                VARCHAR(64);
    v_void                      CHAR(1);
BEGIN
    FOR v_schema_name IN
    SELECT namespace FROM _admin_.admin_pmc WHERE status IN ('Active','Suspended')
    LOOP
        -- DROP all gadget tables
        /**
        *** We will use new and progressive way of dropping tables
        *** instead of old boring DROP TABLE routine
        **/

        FOREACH v_table_name IN ARRAY
        ARRAY[  'demo_gauge',
            'demo_pie_chart2_d',
            'building_lister$column_descriptors',
            'payments_summary_gadget_metadata$column_descriptors',
            'unit_availability_summary_gmeta$column_descriptors',
            'arrears_status_gadget_metadata$column_descriptors',
            'payment_records_gadget_metadata$column_descriptors',
            'common_gadget_columnsportfolio$buildings',
            'payment_records_gadget_metadata$payment_status_filter',
            'demo_line_chart',
            'arrears_summary_gadget_metadata$column_descriptors',
            'demo_demo',
            'payments_summary_gadget_metadata$payment_status',
            'demo_bar_chart2_d',
            'payment_records_gadget_metadata$payment_method_filter',
            'lister_gadget_base_metadata$column_descriptors',
            'unit_availability_gadget_meta$column_descriptors',
            'dashboard_metadata$gadgets',
            'arrears_status_gadget_metadata',
            'payment_records_gadget_metadata',
            'arrears_yoyanalysis_chart_metadata',
            'arrears_summary_gadget_metadata',
            'unit_availability_gadget_meta',
            'lister_gadget_base_metadata',
            'common_gadget_columns',
            'unit_availability_summary_gmeta',
            'payments_summary_gadget_metadata',
            'turnover_analysis_metadata',
            'building_lister',
            'gadget_docking_meta']
        LOOP
            -- DROPS NON-EMPTY TABLES!
            SELECT * INTO v_void FROM _dba_.drop_schema_table(v_schema_name,v_table_name,TRUE) ;

        END LOOP;

        -- income related tables
        FOREACH v_table_name IN ARRAY
        ARRAY[  'income_info_other',
                'income_info_self_employed',
                'income_info_social_services',
                'income_info_seasonally_employed',
                'income_info_student_income',
                'income_info_employer']
        LOOP
            SELECT * INTO v_void FROM _dba_.drop_schema_table(v_schema_name,v_table_name) ;

        END LOOP;

        -- aging_buckets table
        EXECUTE 'ALTER TABLE '||v_schema_name||'.aging_buckets ADD COLUMN bucket_this_month NUMERIC(18,2)';

        -- apt_unit table
        EXECUTE 'ALTER TABLE '||v_schema_name||'.apt_unit ADD COLUMN info_number_s VARCHAR(32), DROP COLUMN notes_and_attachments';
        EXECUTE 'UPDATE '||v_schema_name||'.apt_unit SET info_number_s = _dba_.convert_id_to_string(info_unit_number)';

        -- billing_arrears_snapshot
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_arrears_snapshot ADD CONSTRAINT billing_arrears_snapshot_billing_account_ck '||
        'CHECK (((iddiscriminator = ''LeaseArrearsSnapshot'') AND (billing_account IS NOT NULL)) OR ((iddiscriminator != ''LeaseArrearsSnapshot'') AND (billing_account IS NULL))),'||
        'ADD CONSTRAINT billing_arrears_snapshot_building_ck CHECK (((iddiscriminator = ''BuildingArrearsSnapshot'') AND (building IS NOT NULL)) '||
        'OR ((iddiscriminator != ''BuildingArrearsSnapshot'') AND (building IS NULL)))';


         -- billing_bill
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_bill DROP COLUMN lease_for';

        -- billing_debit_credit_link
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_debit_credit_link ADD COLUMN credit_itemdiscriminator VARCHAR(50),'||
                                        'ADD COLUMN debit_itemdiscriminator VARCHAR(50)';

        EXECUTE 'UPDATE '||v_schema_name||'.billing_debit_credit_link AS a '||
                'SET     credit_itemdiscriminator = b.iddiscriminator '||
                'FROM    (SELECT id,iddiscriminator FROM '||v_schema_name||'.billing_invoice_line_item ) AS b '||
                'WHERE  a.credit_item = b.id ';

        EXECUTE 'UPDATE '||v_schema_name||'.billing_debit_credit_link AS a '||
        '       SET     debit_itemdiscriminator = b.iddiscriminator '||
        '       FROM    (SELECT id,iddiscriminator FROM '||v_schema_name||'.billing_invoice_line_item ) AS b '||
        '       WHERE  a.debit_item = b.id ';


        -- billing_invoice_line_item
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_invoice_line_item ADD COLUMN product_chargediscriminator VARCHAR(50)';
        
        EXECUTE 'UPDATE '||v_schema_name||'.billing_invoice_line_item AS a '||
        '       SET     product_chargediscriminator = b.iddiscriminator '||
        '       FROM    '||v_schema_name||'.billing_invoice_line_item b '||
        '       WHERE   a.product_charge = b.id ';

        -- building
        EXECUTE 'ALTER TABLE '||v_schema_name||'.building DROP COLUMN dashboard, DROP COLUMN notes_and_attachments';

        -- complex
        EXECUTE 'ALTER TABLE '||v_schema_name||'.complex DROP COLUMN dashboard';

        -- customer
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer ADD COLUMN customer_id_s VARCHAR(26)';
        EXECUTE 'UPDATE '||v_schema_name||'.customer SET customer_id_s = _dba_.convert_id_to_string(customer_id) ';

        -- dashboard_metadata
        EXECUTE 'ALTER TABLE '||v_schema_name||'.dashboard_metadata DROP COLUMN is_favorite,'||
                                'DROP COLUMN layout_type,'||
                                'ADD COLUMN encoded_layout VARCHAR(500),'||
                                'DROP CONSTRAINT dashboard_metadata_user_id_fk ';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.dashboard_metadata RENAME COLUMN user_id TO owner_user_id';


        EXECUTE 'TRUNCATE TABLE '||v_schema_name||'.dashboard_metadata';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.dashboard_metadata '||
        '       ADD CONSTRAINT dashboard_metadata_owner_user_id_fk FOREIGN KEY(owner_user_id) REFERENCES '||v_schema_name||'.crm_user(id)';



        -- employee
        EXECUTE 'ALTER TABLE '||v_schema_name||'.employee ADD COLUMN employee_id_s VARCHAR(26)';
        EXECUTE 'UPDATE '||v_schema_name||'.employee SET employee_id_s = _dba_.convert_id_to_string(employee_id)';


        -- gadget_metadata_holder - there is a chance that it will be added by db integrity check

        EXECUTE 'CREATE TABLE '||v_schema_name||'.gadget_metadata_holder '||
                '(  id      BIGINT      NOT NULL, '||
                '   identifier_key  VARCHAR(255), '||
                '   class_name  VARCHAR(255), '||
                '   serialized_form VARCHAR(20845),'||
                '   base_class  VARCHAR(100), '||
                '   CONSTRAINT gadget_metadata_holder_pk PRIMARY KEY (id))';
        EXECUTE 'CREATE INDEX gadget_metadata_holder_class_name_identifier_key_idx ON '||v_schema_name||
        '.gadget_metadata_holder USING btree (class_name,identifier_key)';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.gadget_metadata_holder OWNER TO vista';

        -- identification_document_type

        EXECUTE 'ALTER TABLE '||v_schema_name||'.identification_document_type DROP COLUMN document_id, '||
                                                'DROP COLUMN document_issuer,'||
                                                'DROP COLUMN drivers_license_state';


        -- income_info

        EXECUTE 'CREATE TABLE '||v_schema_name||'.income_info ('||
                '   id              BIGINT          NOT NULL,'||
                '   iddiscriminator         VARCHAR(64)         NOT NULL,'||
                '   name                VARCHAR(500),'||
                '   monthly_amount          NUMERIC(18,2),'||
                '   starts              DATE,'||
                '   ends                DATE,'||
                '   position            VARCHAR(500),'||
                '   address_suite_number        VARCHAR(500),'||
                '   address_street_number       VARCHAR(500),'||
                '   address_street_number_suffix    VARCHAR(500),'||
                '   address_street_name         VARCHAR(500),'||
                '   address_street_type         VARCHAR(50),'||
                '   address_street_direction    VARCHAR(50),'||
                '   address_city            VARCHAR(500),'||
                '   address_county          VARCHAR(500),'||
                '   address_province        BIGINT,'||
                '   address_country         BIGINT,'||
                '   address_postal_code         VARCHAR(500),'||
                '   address_location_lat        DOUBLE PRECISION,'||
                '   address_location_lng        DOUBLE PRECISION,'||
                '   employed_for_years      DOUBLE PRECISION,'||
                '   supervisor_name         VARCHAR(500),'||
                '   supervisor_phone        VARCHAR(500),'||
                '   fully_owned             BOOLEAN,'||
                '   monthly_revenue         NUMERIC(18,2),'||
                '   number_of_employees         INT,'||
                '   program             VARCHAR(50),'||
                '   field_of_study          VARCHAR(500),'||
                '   funding_choices         VARCHAR(50),'||
            '   CONSTRAINT income_info_pk PRIMARY KEY (id),'||
            '   CONSTRAINT income_info_address_country_fk FOREIGN KEY (address_country) '||
            '       REFERENCES '||v_schema_name||'.country(id),'||
            '   CONSTRAINT income_info_address_province_fk FOREIGN KEY (address_province) '||
            '       REFERENCES '||v_schema_name||'.province(id))';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.income_info OWNER TO vista';

        -- invoice_adjustment_sub_line_item
        EXECUTE 'ALTER TABLE '||v_schema_name||'.invoice_adjustment_sub_line_item ADD COLUMN line_itemdiscriminator VARCHAR(50)';
        EXECUTE 'CREATE INDEX invoice_adjustment_sub_line_item_line_itemdiscriminator_idx ON '||v_schema_name||'.invoice_adjustment_sub_line_item '||
        'USING btree (line_itemdiscriminator) ';

        EXECUTE 'UPDATE '||v_schema_name||'.invoice_adjustment_sub_line_item AS a '||
                'SET    line_itemdiscriminator = b.iddiscriminator '||
                'FROM   '||v_schema_name||'.billing_invoice_line_item AS b '||
                'WHERE  a.line_item = b.id ';

        -- invoice_charge_sub_line_item
        EXECUTE 'ALTER TABLE '||v_schema_name||'.invoice_charge_sub_line_item ADD COLUMN line_itemdiscriminator VARCHAR(50)';
        EXECUTE 'CREATE INDEX  invoice_charge_sub_line_item_line_itemdiscriminator_idx ON '||v_schema_name||'.invoice_charge_sub_line_item '||
        'USING btree (line_itemdiscriminator) ';

        EXECUTE 'UPDATE '||v_schema_name||'.invoice_charge_sub_line_item AS a '||
                'SET    line_itemdiscriminator = b.iddiscriminator '||
                'FROM   '||v_schema_name||'.billing_invoice_line_item AS b '||
                'WHERE  a.line_item = b.id ';

        -- invoice_concession_sub_line_item
        EXECUTE 'ALTER TABLE '||v_schema_name||'.invoice_concession_sub_line_item ADD COLUMN line_itemdiscriminator VARCHAR(50)';
        EXECUTE 'CREATE INDEX  invoice_concession_sub_line_item_line_itemdiscriminator_idx ON '||v_schema_name||'.invoice_concession_sub_line_item '||
        'USING btree (line_itemdiscriminator) ';

         EXECUTE 'UPDATE '||v_schema_name||'.invoice_concession_sub_line_item AS a '||
                'SET    line_itemdiscriminator = b.iddiscriminator '||
                'FROM   '||v_schema_name||'.billing_invoice_line_item AS b '||
                'WHERE  a.line_item = b.id ';

        -- lead
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lead ADD COLUMN lead_id_s VARCHAR(26)';
        EXECUTE 'UPDATE '||v_schema_name||'.lead SET lead_id_s = _dba_.convert_id_to_string(lead_id) ';

        -- lease_application
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_application DROP COLUMN lease_on_application,'||
                                    'DROP COLUMN lease_on_application_for,'||
                                    'DROP COLUMN equifax_approval';
        -- legal_questions

        EXECUTE 'ALTER TABLE '||v_schema_name||'.legal_questions DROP CONSTRAINT IF EXISTS legal_questions_pk CASCADE';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.legal_questions DROP CONSTRAINT IF EXISTS legal_questions_pkey CASCADE';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.legal_questions RENAME TO person_screening_legal_questions';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.person_screening_legal_questions ADD CONSTRAINT person_screening_legal_questions_pk PRIMARY KEY(id) ';

        -- master_online_application
        EXECUTE 'ALTER TABLE '||v_schema_name||'.master_online_application ADD COLUMN online_application_id_s VARCHAR(26),'||
                                                                        'DROP COLUMN equifax_approval';
        EXECUTE 'UPDATE '||v_schema_name||'.master_online_application SET online_application_id_s = _dba_.convert_id_to_string(online_application_id) ';

        -- notes_and_attachments
        EXECUTE 'ALTER TABLE '||v_schema_name||'.notes_and_attachments DROP COLUMN x,'||
                            'ADD COLUMN owner_id BIGINT,'||
                            'ADD COLUMN owner_class VARCHAR(80),'||
                            'ADD COLUMN subject VARCHAR(128),'||
                            'ADD COLUMN note VARCHAR(20845),'||
                            'ADD COLUMN crmuser BIGINT,'||
                            'ADD COLUMN updated DATE,'||
                            'ADD COLUMN created DATE';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.notes_and_attachments '||
        'ADD CONSTRAINT notes_and_attachments_crmuser_fk FOREIGN KEY(crmuser) REFERENCES '||v_schema_name||'.crm_user(id)';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.notes_and_attachments OWNER TO vista';

        EXECUTE 'CREATE INDEX notes_and_attachments_owner_id_owner_class_idx ON '||v_schema_name||'.notes_and_attachments '||
        'USING btree (owner_id, owner_class) ';

        -- note table - drop
        SELECT * INTO v_void FROM _dba_.drop_schema_table(v_schema_name,'note',FALSE);

        -- note$attachments  - drop
        SELECT * INTO v_void FROM _dba_.drop_schema_table(v_schema_name,'note$attachments',FALSE);

        --  notes_and_attachments$attachments
        EXECUTE 'CREATE TABLE '||v_schema_name||'.notes_and_attachments$attachments ( '||
        '   id      BIGINT          NOT NULL,'||
        '   owner       BIGINT,'||
        '   value       BIGINT,'||
        '   seq     INT,'||
        'CONSTRAINT notes_and_attachments$attachments_pk PRIMARY KEY(id),'||
        'CONSTRAINT notes_and_attachments$attachments_owner_fk FOREIGN KEY(owner) REFERENCES '||v_schema_name||'.notes_and_attachments(id),'||
        'CONSTRAINT notes_and_attachments$attachments_value_fk FOREIGN KEY(value) REFERENCES '||v_schema_name||'.note_attachment(id))';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.notes_and_attachments$attachments OWNER TO vista';

        EXECUTE 'CREATE INDEX notes_and_attachments$attachments_owner_idx ON '||v_schema_name||'.notes_and_attachments$attachments '||
        'USING btree (owner) ';

        --  notes_and_attachments$notes -- drop
        SELECT * INTO v_void FROM _dba_.drop_schema_table(v_schema_name,'notes_and_attachments$notes',FALSE);

        -- payment_information
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_information DROP COLUMN payment_method_phone ';

        -- payment_method
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_method DROP COLUMN phone ';

        -- payment_payment_details


        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_payment_details RENAME COLUMN account_no_reference TO account_no_obfuscated_number';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_payment_details RENAME COLUMN card_reference TO card_obfuscated_number';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_payment_details ALTER COLUMN account_no_obfuscated_number TYPE VARCHAR(12),'||
                                        'ALTER COLUMN card_obfuscated_number TYPE VARCHAR(16)';

        EXECUTE 'UPDATE '||v_schema_name||'.payment_payment_details '||
        '   SET     account_no_obfuscated_number = LPAD(account_no_obfuscated_number,12,''X''),'||
        '       card_obfuscated_number = LPAD(card_obfuscated_number,16,''X'') ';

        -- personal_income - just in case rename to person_screening_personal_income

        EXECUTE 'ALTER TABLE '||v_schema_name||'.personal_income DROP CONSTRAINT IF EXISTS personal_income_pk,'||
                                                'DROP CONSTRAINT IF EXISTS personal_income_pkey,'||
                                                'DROP CONSTRAINT IF EXISTS personal_income_employer_fk,'||
                                                'DROP CONSTRAINT IF EXISTS personal_income_other_income_information_fk,'||
                                                'DROP CONSTRAINT IF EXISTS personal_income_owner_fk,'||
                                                'DROP CONSTRAINT IF EXISTS personal_income_seasonally_employed_fk,'||
                                                'DROP CONSTRAINT IF EXISTS personal_income_self_employed_fk,'||
                                                'DROP CONSTRAINT IF EXISTS personal_income_social_services_fk,'||
                                                'DROP CONSTRAINT IF EXISTS personal_income_student_income_fk';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.personal_income RENAME TO person_screening_personal_income';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.person_screening_personal_income DROP COLUMN other_income_information,'||
                                                'DROP COLUMN employer,'||
                                                'DROP COLUMN self_employed,'||
                                                'DROP COLUMN seasonally_employed,'||
                                                'DROP COLUMN social_services,'||
                                                'DROP COLUMN student_income';


        EXECUTE 'ALTER TABLE '||v_schema_name||'.person_screening_personal_income ADD COLUMN detailsdiscriminator VARCHAR(50),'||
                                                'ADD COLUMN details BIGINT';


        EXECUTE 'ALTER TABLE '||v_schema_name||'.person_screening_personal_income ADD CONSTRAINT  person_screening_personal_income_pk PRIMARY KEY(id),'||
        'ADD CONSTRAINT person_screening_personal_income_details_fk FOREIGN KEY(details) REFERENCES '||v_schema_name||'.income_info(id)' ;


        -- person_screening

        EXECUTE 'ALTER TABLE '||v_schema_name||'.person_screening '||
                                                'DROP CONSTRAINT IF EXISTS person_screening_pk CASCADE,'||
                                                'DROP CONSTRAINT IF EXISTS person_screening_pkey CASCADE,'||                    -- Very special case for pangroup
                                                'DROP CONSTRAINT IF EXISTS person_screening_current_address_country_fk,'||
                                                'DROP CONSTRAINT IF EXISTS person_screening_current_address_province_fk,'||
                                                'DROP CONSTRAINT IF EXISTS person_screening_equifax_approval_fk,'||
                                                'DROP CONSTRAINT IF EXISTS person_screening_legal_questions_fk,'||
                                                'DROP CONSTRAINT IF EXISTS person_screening_previous_address_country_fk,'||
                                                'DROP CONSTRAINT IF EXISTS person_screening_previous_address_province_fk,'||
                                                'DROP CONSTRAINT IF EXISTS person_screening_screene_fk';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.person_screening RENAME TO person_screening_v';

        EXECUTE 'CREATE TABLE '||v_schema_name||'.person_screening ( '||
                '       id              BIGINT          NOT NULL,'||
                '       screene         BIGINT,'||
                '       CONSTRAINT person_screening_pk PRIMARY KEY(id),'||
                '       CONSTRAINT person_screening_screene_fk FOREIGN KEY(screene) REFERENCES '||v_schema_name||'.customer(id))';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.person_screening OWNER TO vista';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.person_screening_v ADD COLUMN created_by_user_key BIGINT,'||
                                                                'ADD COLUMN from_date TIMESTAMP,'||
                                                                'ADD COLUMN to_date TIMESTAMP,'||
                                                                'ADD COLUMN holder BIGINT,'||
                                                                'ADD COLUMN Version_number INT,'||
                                                                'DROP COLUMN screene,'||
                        'ADD CONSTRAINT person_screening_v_pk PRIMARY KEY(id),'||
                        'ADD CONSTRAINT person_screening_v_current_address_country_fk FOREIGN KEY(current_address_country) REFERENCES '||v_schema_name||'.country(id),'||
                        'ADD CONSTRAINT person_screening_v_current_address_province_fk FOREIGN KEY(current_address_province) REFERENCES '||v_schema_name||'.province(id),'||
                        'ADD CONSTRAINT person_screening_v_equifax_approval_fk FOREIGN KEY(equifax_approval) REFERENCES '||v_schema_name||'.equifax_approval(id),'||
                        'ADD CONSTRAINT person_screening_v_holder_fk FOREIGN KEY(holder) REFERENCES '||v_schema_name||'.person_screening(id),'||
                        'ADD CONSTRAINT person_screening_v_legal_questions_fk FOREIGN KEY(legal_questions) REFERENCES '||v_schema_name||'.person_screening_legal_questions(id),'||
                        'ADD CONSTRAINT person_screening_v_previous_address_country_fk FOREIGN KEY(previous_address_country) REFERENCES '||v_schema_name||'.country(id),'||
                        'ADD CONSTRAINT person_screening_v_previous_address_province_fk FOREIGN KEY(previous_address_province) REFERENCES '||v_schema_name||'.province(id)';

        EXECUTE 'CREATE INDEX person_screening_v_holder_from_date_to_date_idx ON '||v_schema_name||'.person_screening_v USING btree(holder, from_date, to_date)';

        /*
        EXECUTE 'ALTER TABLE '||v_schema_name||'.person_screening_personal_income '||
        'ADD CONSTRAINT person_screening_personal_income_owner_fk FOREIGN KEY(owner) REFERENCES '||v_schema_name||'.person_screening_v(id)';
        */

        -- reports_settings_holder
        EXECUTE 'CREATE TABLE '||v_schema_name||'.reports_settings_holder ('||
            '   id      BIGINT      NOT NULL,'||
            '   identifier_key  VARCHAR(255),'||
            '   class_name  VARCHAR(255),'||
            '   serialized_form VARCHAR(20845),'||
            '   base_class  VARCHAR(100),'||
            '   CONSTRAINT reports_settings_holder_pk PRIMARY KEY(id))';
        EXECUTE 'CREATE INDEX reports_settings_holder_class_name_identifier_key_idx ON '||
        v_schema_name||'.reports_settings_holder USING btree (class_name, identifier_key)';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.reports_settings_holder OWNER TO vista';

        -- tenant_charge
        EXECUTE 'ALTER TABLE '||v_schema_name||'.tenant_charge ADD COLUMN tenantdiscriminator VARCHAR(50)';

        -- unit_availability_status

        EXECUTE 'ALTER TABLE '||v_schema_name||'.unit_availability_status RENAME COLUMN status_date TO status_from';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.unit_availability_status ADD COLUMN status_until DATE';

        EXECUTE 'WITH t AS (    SELECT  id, unit,status_from,status_until,'||
            '       row_number() OVER (PARTITION BY unit ORDER BY status_from) AS rownum '||
            '       FROM    '||v_schema_name||'.unit_availability_status ORDER BY unit) '||
            'UPDATE '||v_schema_name||'.unit_availability_status AS a '||
            'SET    status_until = b.date_til '||
            'FROM '||
            '(SELECT a.id, '||
            'CASE WHEN b.status_from IS NOT NULL THEN  b.status_from -1 '||
            'ELSE ''3000-01-01'' END AS date_til FROM t AS a '||
            'LEFT JOIN t AS b ON (a.unit = b.unit AND a.rownum +1 = b.rownum) ) AS b '
            'WHERE a.id = b.id ';


        /**
        *** -----------------------------------------------------------------------------------------
        ***     Service,feature, product and such
        *** -----------------------------------------------------------------------------------------
        **/

        -- product

        EXECUTE 'CREATE TABLE '||v_schema_name||'.product ( '||
            '   id          BIGINT      NOT NULL,'||
            '   old_id          BIGINT,'||          -- To hold original value for data migration
            '   iddiscriminator     VARCHAR(64) NOT NULL,'||
            '   catalog         BIGINT,'||
            '   order_in_catalog    INT,'||
            '   updated         TIMESTAMP,'||
            '   CONSTRAINT product_pk PRIMARY KEY (id),'||
            '   CONSTRAINT product_catalog_fk FOREIGN KEY(catalog) '||
            '       REFERENCES '||v_schema_name||'.product_catalog(id))';

        EXECUTE 'INSERT INTO '||v_schema_name||'.product (id,old_id,iddiscriminator,catalog,updated) '||
        '((SELECT nextval(''public.product_seq''),id AS old_id,''feature'',catalog,updated FROM '||v_schema_name||'.feature ORDER BY id) '||
        'UNION '||
        '(SELECT nextval(''public.product_seq''),id AS old_id,''service'',catalog,updated FROM '||v_schema_name||'.service ORDER BY id ))';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.product OWNER TO vista';


        -- product_v

        EXECUTE 'CREATE TABLE '||v_schema_name||'.product_v ( '||
            '   id          BIGINT      NOT NULL,'||
            '   old_id          BIGINT,'||          -- To hold original value for data migration
            '   iddiscriminator     VARCHAR(64) NOT NULL,'||
            '   version_number      INT,'||
            '   to_date         TIMESTAMP,'||
            '   from_date       TIMESTAMP,'||
            '   holderdiscriminator VARCHAR(50),'||
            '   holder          BIGINT,'||
            '   old_holder      BIGINT,'||          -- To hold original value for data migration
            '   created_by_user_key BIGINT,'||
            '   name            VARCHAR(25),'||
            '   description     VARCHAR(250),'||
            '   visibility      VARCHAR(50),'||
            '   feature_type        VARCHAR(50),'||
            '   recurring       BOOLEAN,'||
            '   mandatory       BOOLEAN,'||
            '   service_type        VARCHAR(50),'||
            '   CONSTRAINT product_v_pk PRIMARY KEY (id))';

        EXECUTE 'INSERT INTO '||v_schema_name||'.product_v (id,old_id,iddiscriminator,version_number,to_date,from_date,'||
        'description,old_holder,created_by_user_key,name,feature_type,recurring,mandatory) '||
        '(SELECT nextval(''public.product_v_seq'') AS id, id AS old_id,''feature'',version_number,to_date,from_date,'||
        'description,holder,created_by_user_key,name,feature_type,recurring,mandatory '||
        'FROM '||v_schema_name||'.feature_v ORDER BY id)';


        EXECUTE 'INSERT INTO '||v_schema_name||'.product_v (id,old_id,iddiscriminator,version_number,to_date,from_date,'||
        'description,old_holder,created_by_user_key,name,service_type) '||
        '(SELECT nextval(''public.product_v_seq'') AS id, id AS old_id,''service'',version_number,to_date,from_date,'||
        'description,holder,created_by_user_key,name,service_type '||
        'FROM '||v_schema_name||'.service_v ORDER BY id)';


        EXECUTE 'UPDATE '||v_schema_name||'.product_v AS b '||
        '   SET     holder = a.holder '||
        '   FROM    '||
        '   (SELECT a.id,b.id AS holder '||
        '   FROM '||v_schema_name||'.product_v a '||
        '   JOIN '||v_schema_name||'.product b ON (a.old_holder = b.old_id AND a.iddiscriminator = b.iddiscriminator)) AS a '||
        '   WHERE b.id = a.id ';

        EXECUTE 'UPDATE '||v_schema_name||'.product_v AS a '
                'SET holderdiscriminator = b.iddiscriminator '
                'FROM (SELECT a.id,a.iddiscriminator '||
                '      FROM '||v_schema_name||'.product a '||
                '      JOIN '||v_schema_name||'.product_v b ON (a.id = b.holder)) AS b '||
                'WHERE a.holder = b.id';

        EXECUTE 'UPDATE '||v_schema_name||'.product_v '||
                'SET visibility = ''global'' ';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_v ADD CONSTRAINT product_v_holder_fk FOREIGN KEY(holder) REFERENCES '||v_schema_name||'.product(id)';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_v ADD CONSTRAINT product_v_feature_type_ck CHECK (iddiscriminator = ''feature'' '||
        ' AND feature_type IS NOT NULL OR iddiscriminator != ''feature'' AND feature_type IS NULL) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_v ADD CONSTRAINT product_v_service_type_ck CHECK (iddiscriminator = ''service'' '||
        ' AND service_type IS NOT NULL OR iddiscriminator != ''service'' AND service_type IS NULL) ';

        EXECUTE 'CREATE INDEX product_v_holder_holderdiscriminator_from_date_to_date_idx ON '||v_schema_name||'.product_v '||
        'USING btree (holder, holderdiscriminator, from_date, to_date) ';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_v OWNER TO vista';

        --product_v$features
        EXECUTE 'CREATE TABLE '||v_schema_name||'.product_v$features ( '||
        '   id          BIGINT      NOT NULL,'||
        '   owner           BIGINT,'||
        '   valuediscriminator  VARCHAR(50),'||
        '   value           BIGINT,'||
        '   seq         INT,'||
        '   CONSTRAINT product_v$features_pk PRIMARY KEY(id))';

        EXECUTE 'INSERT INTO '||v_schema_name||'.product_v$features (id,owner,valuediscriminator,value,seq) '||
        '(SELECT nextval(''product_v$features_seq'') AS id,b.id AS owner,''feature'',c.id AS value,a.seq '||
        'FROM '||v_schema_name||'.service_v$features a '||
        'JOIN '||v_schema_name||'.product_v b ON (a.owner = b.old_id) '||
        'JOIN '||v_schema_name||'.product c ON (a.value = c.old_id))';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_v$features '||
        '   ADD CONSTRAINT product_v$features_owner_fk FOREIGN KEY(owner) REFERENCES '||v_schema_name||'.product_v(id), '||
        '   ADD CONSTRAINT product_v$features_value_fk FOREIGN KEY(value) REFERENCES '||v_schema_name||'.product(id) ';

        EXECUTE 'CREATE INDEX product_v$features_owner_idx ON '||v_schema_name||'.product_v$features USING btree(owner)';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_v$features OWNER TO vista';

        -- product_v$concessions

        EXECUTE 'CREATE TABLE '||v_schema_name||'.product_v$concessions ( '||
        '   id      BIGINT      NOT NULL,'||
        '   owner       BIGINT,'||
        '   value       BIGINT,'||
        '   seq     INT,'||
        '   CONSTRAINT product_v$concessions_pk PRIMARY KEY(id))';


        EXECUTE 'INSERT INTO '||v_schema_name||'.product_v$concessions (id,owner,value,seq) '||
        '(SELECT nextval(''public.product_v$concessions_seq'') AS id, b.id AS owner,a.value,a.seq '
        'FROM '||v_schema_name||'.service_v$concessions a '||
        'JOIN '||v_schema_name||'.product_v b ON (a.owner = b.old_id))';


        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_v$concessions '||
        '   ADD CONSTRAINT product_v$concessions_owner_fk FOREIGN KEY(owner) REFERENCES '||v_schema_name||'.product_v(id), '||
        '   ADD CONSTRAINT product_v$concessions_value_fk FOREIGN KEY(value) REFERENCES '||v_schema_name||'.concession(id) ';
        EXECUTE 'CREATE INDEX product_v$concessions_owner_idx ON '||v_schema_name||'.product_v$concessions USING btree (owner) ';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_v$concessions OWNER TO vista';

        -- product_item

        EXECUTE 'UPDATE '||v_schema_name||'.product_item AS b '||
        '   SET     product = a.product '||
        '   FROM    (SELECT a.old_id, a.id AS product, b.productdiscriminator '||
        '       FROM '||v_schema_name||'.product_v a '||
        '       JOIN '||v_schema_name||'.product_item b ON (a.old_id = b.product AND a.iddiscriminator = b.productdiscriminator )) AS a '||
        '   WHERE   (b.product = a.old_id AND a.productdiscriminator = b.productdiscriminator) ';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_item ADD CONSTRAINT product_item_product_fk FOREIGN KEY(product) '||
        'REFERENCES '||v_schema_name||'.product_v(id)';

        -- cleanup

        FOREACH v_table_name IN ARRAY
        ARRAY[  'service_v$concessions',
                'service_v$features',
                'service_v',
                'service',
                'feature_v',
                'feature']
        LOOP
            SELECT * INTO v_void FROM _dba_.drop_schema_table(v_schema_name,v_table_name,TRUE);

        END LOOP;

        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_v DROP COLUMN old_id, DROP COLUMN old_holder';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product DROP COLUMN old_id';

        /**
        *** -----------------------------------------------------------------------------------------
        ***
        ***     Services, features and products section is over. Finally!
        ***     Now for the next big task - lease migration.
        ***
        *** ------------------------------------------------------------------------------------------
        **/

        -- lease

        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease ADD COLUMN actual_lease_to DATE,'||
                                'ADD COLUMN actual_move_in DATE,'||
                                'ADD COLUMN actual_move_out DATE,'||
                                'ADD COLUMN completion VARCHAR(50),'||
                                'ADD COLUMN status VARCHAR(50),'||
                                'ADD COLUMN current_term BIGINT,'||
                                'ADD COLUMN expected_move_in DATE,'||
                                'ADD COLUMN expected_move_out DATE,'||
                                'ADD COLUMN lease_id_s VARCHAR(26),'||
                                'ADD COLUMN move_out_notice DATE,'||
                                'ADD COLUMN next_term BIGINT,'||
                                'ADD COLUMN previous_term BIGINT';


        /**
        *** Migratting lease_v data to lease - if lease is not in draft mode,
        *** the latest version should be taken, else - the draft one
        **/

        EXECUTE 'WITH t AS  (SELECT     holder,actual_lease_to,actual_move_in,actual_move_out,'||
                    '       completion,status,expected_move_in,expected_move_out,'||
                    '       move_out_notice,version_number '||
                    'FROM       '||v_schema_name||'.lease_v '||
                    'WHERE      to_date IS NULL '||
                    'AND        version_number IS NOT NULL) '||             -- t is just the completed versions of lease
        'UPDATE '||v_schema_name||'.lease AS a '||
        'SET    actual_lease_to = b.actual_lease_to,'||
        '   actual_move_in = b.actual_move_in,'||
        '   actual_move_out = b.actual_move_out,'||
        '   completion = b.completion,'||
        '   status = b.status,'||
        '   expected_move_in = b.expected_move_in,'||
        '   expected_move_out = b.expected_move_out,'||
        '   move_out_notice = b.move_out_notice '||
        ' FROM  '||
        '(SELECT holder,actual_lease_to,actual_move_in,actual_move_out,'||
        '   completion,status,expected_move_in,expected_move_out,'||
        '   move_out_notice '||
        'FROM   t '||
        'UNION '||
        '(SELECT holder,actual_lease_to,actual_move_in,actual_move_out,'||              -- draft versions that are not in t
        '   completion,status,expected_move_in,expected_move_out,'||
        '   move_out_notice '||
        'FROM   '||v_schema_name||'.lease_v '||
        'WHERE  to_date IS NULL '||
        'AND    version_number IS NULL '||
        'AND    holder NOT IN (SELECT holder FROM t))) AS b '||
        'WHERE  a.id = b.holder ';

        EXECUTE 'UPDATE '||v_schema_name||'.lease '||
                'SET    lease_id_s = LPAD(lease_id,7,''0'') ';

        -- lease_term

        EXECUTE 'CREATE TABLE '||v_schema_name||'.lease_term ( '||
        '   id          BIGINT      NOT NULL,'||
        '   lease_term_type     VARCHAR(50),'||
        '   lease_term_status   VARCHAR(50),'||
        '   term_from       DATE,'||
        '   term_to         DATE,'||
        '   creation_date       DATE,'||
        '   lease           BIGINT,'||
        '   order_in_owner      INT,'||
        '   CONSTRAINT lease_term_pk PRIMARY KEY(id),'||
        '   CONSTRAINT lease_term_lease_fk FOREIGN KEY(lease) '||
        '       REFERENCES '||v_schema_name||'.lease(id))';

        EXECUTE 'INSERT INTO '||v_schema_name||'.lease_term '||
        '(id,lease_term_type,lease_term_status,term_from,term_to,creation_date,lease) '||
        '(SELECT nextval(''public.lease_term_seq'') AS id,lease_term,''Current'','||
        'lease_from AS term_from,lease_to AS term_to,creation_date,id AS lease '||
        'FROM '||v_schema_name||'.lease ORDER BY id )';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term OWNER TO vista';

        -- lease_term_v
        EXECUTE 'CREATE TABLE '||v_schema_name||'.lease_term_v ( '||
        '   id              BIGINT      NOT NULL,'||
        '   old_id              BIGINT,'||
        '   version_number          INT,'||
        '   to_date             TIMESTAMP,'||
        '   from_date           TIMESTAMP,'||
        '   holder              BIGINT,'||
        '   created_by_user_key     BIGINT,'||
        '   lease_products_service_item BIGINT,'||
        '   CONSTRAINT lease_term_v_pk PRIMARY KEY(id),'||
        '   CONSTRAINT lease_term_v_holder_fk FOREIGN KEY(holder) '||
        '       REFERENCES '||v_schema_name||'.lease_term(id), '||
        '   CONSTRAINT lease_term_v_lease_products_service_item_fk FOREIGN KEY(lease_products_service_item) '||
        '       REFERENCES '||v_schema_name||'.billable_item(id))';

        EXECUTE 'INSERT INTO '||v_schema_name||'.lease_term_v '||
        '(id,old_id,version_number,to_date,from_date,holder,created_by_user_key,lease_products_service_item) '||
        '(SELECT nextval(''public.lease_term_v_seq'') AS id,a.id AS old_id,a.version_number,a.to_date,a.from_date,b.id AS holder,'||
        'a.created_by_user_key,a.lease_products_service_item '||
        'FROM '||v_schema_name||'.lease_v a '||
        'JOIN '||v_schema_name||'.lease_term b ON (a.holder = b.lease) '||
        'ORDER BY a.holder) ';

        EXECUTE 'CREATE INDEX lease_term_v_holder_from_date_to_date_idx ON '||v_schema_name||'.lease_term_v USING btree (holder, from_date, to_date)';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term_v OWNER TO vista';

        -- lease_term_vlease_products$concessions
        EXECUTE 'CREATE TABLE '||v_schema_name||'.lease_term_vlease_products$concessions ( '||
        '   id      BIGINT      NOT NULL,'||
        '   owner       BIGINT,'||
        '   value       BIGINT,'||
        '   value_for   TIMESTAMP,'||
        '   seq     INT,'||
        '   CONSTRAINT lease_term_vlease_products$concessions_pk PRIMARY KEY(id),'||
        '   CONSTRAINT lease_term_vlease_products$concessions_owner_fk FOREIGN KEY (owner) '||
        '       REFERENCES '||v_schema_name||'.lease_term_v(id),'||
        '   CONSTRAINT lease_term_vlease_products$concessions_value_fk FOREIGN KEY (value) '||
        '       REFERENCES '||v_schema_name||'.concession(id)) ';

        EXECUTE 'INSERT INTO '||v_schema_name||'.lease_term_vlease_products$concessions (id,owner,value,value_for,seq) '||
        '(SELECT DISTINCT nextval(''public.lease_term_vlease_products$concessions_seq'') AS id, b.id AS owner, a.value,a.value_for,a.seq '||
        'FROM '||v_schema_name||'.lease_vlease_products$concessions a '||
        'JOIN '||v_schema_name||'.lease_term_v b ON (b.old_id = a.owner) '||
        'ORDER BY b.id )';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term_vlease_products$concessions OWNER TO vista';
        EXECUTE 'CREATE INDEX lease_term_vlease_products$concessions_owner_idx ON '||v_schema_name||'.lease_term_vlease_products$concessions '||
        'USING btree (owner)';


        -- lease_term_vlease_products$feature_items
        EXECUTE 'CREATE TABLE '||v_schema_name||'.lease_term_vlease_products$feature_items ( '||
        '   id      BIGINT      NOT NULL,'||
        '   owner       BIGINT,'||
        '   value       BIGINT,'||
        '   seq     INT,'||
        '   CONSTRAINT lease_term_vlease_products$feature_items_pk PRIMARY KEY(id),'||
        '   CONSTRAINT lease_term_vlease_products$feature_items_owner_fk FOREIGN KEY (owner) '||
        '       REFERENCES '||v_schema_name||'.lease_term_v(id),'||
        '   CONSTRAINT lease_term_vlease_products$feature_items_value_fk FOREIGN KEY (value) '||
        '       REFERENCES '||v_schema_name||'.billable_item(id)) ';

        EXECUTE 'INSERT INTO '||v_schema_name||'.lease_term_vlease_products$feature_items (id,owner,value,seq) '||
        '(SELECT DISTINCT nextval(''public.lease_term_vlease_products$feature_items_seq'') AS id, b.id AS owner, a.value,a.seq '||
        'FROM '||v_schema_name||'.lease_vlease_products$feature_items a '||
        'JOIN '||v_schema_name||'.lease_term_v b ON (b.old_id = a.owner) '||
        'ORDER BY b.id )';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term_vlease_products$feature_items OWNER TO vista';
        EXECUTE 'CREATE INDEX lease_term_vlease_products$feature_items_owner_idx ON '||v_schema_name||'.lease_term_vlease_products$feature_items '||
        'USING btree (owner)';


        -- lease - revisited

        EXECUTE 'UPDATE '||v_schema_name||'.lease AS a '||
        'SET    current_term = b.id '||
        'FROM   (SELECT id, lease FROM '||v_schema_name||'.lease_term) AS b '||
        'WHERE  a.id = b.lease ';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease DROP COLUMN lease_term,'||
        'ADD CONSTRAINT lease_current_term_fk FOREIGN KEY(current_term) REFERENCES '||v_schema_name||'.lease_term(id),'||
        'ADD CONSTRAINT lease_next_term_fk FOREIGN KEY(next_term) REFERENCES '||v_schema_name||'.lease_term(id),'||
        'ADD CONSTRAINT lease_previous_term_fk FOREIGN KEY(previous_term) REFERENCES '||v_schema_name||'.lease_term(id)';

        -- lease_customer

        EXECUTE 'CREATE TABLE '||v_schema_name||'.lease_customer ( '||
        '   id                          BIGINT          NOT NULL,'||
        '   iddiscriminator             VARCHAR(64)     NOT NULL,'||
        '   lease                       BIGINT,'||
        '   customer                    BIGINT,'||
        '   participant_id              VARCHAR(14),'||
        '   preauthorized_payment       BIGINT,'||
        '   participant_id_s            VARCHAR(26),'||
        '   CONSTRAINT lease_customer_pk PRIMARY KEY(id),'||
        '   CONSTRAINT lease_customer_customer_fk FOREIGN KEY(customer) '||
        '       REFERENCES '||v_schema_name||'.customer(id),'||
        '   CONSTRAINT lease_customer_lease_fk FOREIGN KEY(lease) '||
        '       REFERENCES '||v_schema_name||'.lease(id),'||
        '   CONSTRAINT lease_customer_preauthorized_payment_fk FOREIGN KEY(preauthorized_payment) '||
        '       REFERENCES '||v_schema_name||'.payment_method(id))';

        EXECUTE 'INSERT INTO '||v_schema_name||'.lease_customer '||
        '(id,iddiscriminator,lease,customer,participant_id) '||
        '(SELECT nextval(''public.lease_customer_seq'') AS id,''Tenant'' AS iddiscriminator,'||
        'a.* FROM '||
        '(SELECT a.id AS lease, b.customer, MAX(b.participant_id) AS participant_id  '||
        'FROM '||v_schema_name||'.lease a '||
        'JOIN '||v_schema_name||'.lease_v c ON (a.id = c.holder) '||
        'JOIN '||v_schema_name||'.tenant b ON (c.id = b.lease_v) '||
        'GROUP BY a.id,b.customer '||
        'ORDER BY a.id ) AS a )';


        EXECUTE 'INSERT INTO '||v_schema_name||'.lease_customer '||
        '(id,iddiscriminator,lease,customer,participant_id) '||
        '(SELECT nextval(''public.lease_customer_seq'') AS id,''Guarantor'' AS iddiscriminator,a.* '||
        'FROM '||
        '(SELECT a.id AS lease, b.customer, MAX(b.participant_id) AS participant_id '||
        'FROM '||v_schema_name||'.lease a '||
        'JOIN '||v_schema_name||'.lease_v c ON (a.id = c.holder) '||
        'JOIN '||v_schema_name||'.guarantor b ON (c.id = b.lease_v) '||
        'GROUP BY a.id,b.customer '||
        'ORDER BY a.id ) AS a )';

        EXECUTE 'UPDATE '||v_schema_name||'.lease_customer '||
        '       SET     participant_id_s = LPAD(participant_id,7,''0'')';

        EXECUTE 'UPDATE '||v_schema_name||'.lease_customer AS a '||
        '       SET preauthorized_payment = b.preauthorized_payment '||
        '       FROM '||
        '       (SELECT a.id AS lease, b.customer, b.preauthorized_payment '||
        '               FROM '||v_schema_name||'.lease a '||
        '               JOIN '||v_schema_name||'.lease_v c ON (a.id = c.holder) '||
        '               JOIN '||v_schema_name||'.tenant b ON (a.id = b.lease_v) '||
        '               JOIN    (SELECT MAX(id) AS id,customer FROM '||v_schema_name||'.tenant '||
        '                       GROUP BY customer) d ON (b.id = d.id) '||
        '               WHERE b.preauthorized_payment IS NOT NULL ) AS b '||
        '       WHERE   a.lease = b.lease '||
        '       AND     a.customer = b.customer ';



        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_customer OWNER TO vista';
        EXECUTE 'CREATE UNIQUE INDEX lease_customer_lease_customer_idx ON '||v_schema_name||'.lease_customer USING btree(lease,customer,iddiscriminator)';
        EXECUTE 'CREATE UNIQUE INDEX lease_customer_participant_id_idx ON '||v_schema_name||'.lease_customer USING btree(participant_id,iddiscriminator)';


        -- lease_participant

        EXECUTE 'CREATE TABLE '||v_schema_name||'.lease_participant ( '||
        '   id                      BIGINT      NOT NULL,'||
        '   tenant_id               BIGINT,'||
        '   iddiscriminator         VARCHAR(64)     NOT NULL,'||
        '   lease_customerdiscriminator VARCHAR(50),'||
        '   lease_customer          BIGINT,'||
        '   participant_role        VARCHAR(50),'||
        '   lease_term_v            BIGINT,'||
        '   order_in_lease          INT,'||
        '   application             BIGINT,'||
        '   screening               BIGINT,'||
        '   screening_for           TIMESTAMP WITHOUT TIME ZONE,'||
        '   relationship            VARCHAR(50),'||
        '   tenantdiscriminator     VARCHAR(50),'||
        '   tenant                  BIGINT,'||
        '   take_ownership          BOOLEAN,'||
        '   percentage              NUMERIC(18,2),'||
        '   CONSTRAINT lease_participant_pk PRIMARY KEY(id),'||
        '   CONSTRAINT lease_participant_application_fk FOREIGN KEY(application) '||
        '       REFERENCES '||v_schema_name||'.online_application(id),'||
            '   CONSTRAINT lease_participant_lease_customer_fk FOREIGN KEY(lease_customer) '||
        '       REFERENCES '||v_schema_name||'.lease_customer(id), '||
            '   CONSTRAINT lease_participant_lease_term_v_fk FOREIGN KEY(lease_term_v) '||
        '       REFERENCES '||v_schema_name||'.lease_term_v(id), '||
            '   CONSTRAINT lease_participant_screening_fk FOREIGN KEY(screening) '||
        '       REFERENCES '||v_schema_name||'.person_screening(id), '||
            '   CONSTRAINT lease_participant_tenant_fk FOREIGN KEY(tenant) '||
        '       REFERENCES '||v_schema_name||'.lease_participant(id))';

        EXECUTE 'INSERT INTO '||v_schema_name||'.lease_participant '||
        '(id,iddiscriminator,lease_customerdiscriminator,lease_customer,participant_role,lease_term_v,order_in_lease,'||
        'application,screening,relationship,tenantdiscriminator,tenant) '||
        '(SELECT nextval(''public.lease_participant_seq'') AS id,''Guarantor'' AS iddiscriminator, ''Guarantor'' AS lease_customerdiscriminator,'||
        'c.id AS lease_customer,a.participant_role,b.id AS lease_term_v,a.order_in_lease,a.application,a.screening,a.relationship,'||
        '''Tenant'',f.id AS tenant '||
        'FROM '||v_schema_name||'.guarantor a '||
        'JOIN '||v_schema_name||'.lease_term_v b ON (a.lease_v = b.old_id) '||
        'JOIN '||v_schema_name||'.lease_v d ON (a.lease_v = d.id) '||
        'JOIN '||v_schema_name||'.lease e ON (d.holder = e.id) '||
        'JOIN '||v_schema_name||'.lease_customer c ON (a.customer = c.customer AND e.id = c.lease) '||
        'LEFT JOIN '||v_schema_name||'.lease_participant f ON (f.tenant_id = a.tenant) '||
        'ORDER BY a.id )';

        EXECUTE 'INSERT INTO '||v_schema_name||'.lease_participant '||
        '(id,tenant_id,iddiscriminator,lease_customerdiscriminator,lease_customer,participant_role,lease_term_v,order_in_lease,'||
        'application,screening,relationship,take_ownership,percentage) '||
        '(SELECT nextval(''public.lease_participant_seq'') AS id,a.id AS tenant_id,''Tenant'' AS iddiscriminator, ''Tenant'' AS lease_customerdiscriminator,'||
        'c.id AS lease_customer,a.participant_role,b.id AS lease_term_v,a.order_in_lease,a.application,a.screening,a.relationship,'||
        'a.take_ownership,a.percentage '||
        'FROM '||v_schema_name||'.tenant a '||
        'JOIN '||v_schema_name||'.lease_term_v b ON (a.lease_v = b.old_id) '||
        'JOIN '||v_schema_name||'.lease_v d ON (a.lease_v = d.id) '||
        'JOIN '||v_schema_name||'.lease e ON (d.holder = e.id) '||
        'JOIN '||v_schema_name||'.lease_customer c ON (a.customer = c.customer AND e.id = c.lease ) '||
        'ORDER BY a.id )';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_participant OWNER TO vista';
        EXECUTE 'CREATE INDEX lease_participant_application_idx ON '||v_schema_name||'.lease_participant USING btree(application)';
        EXECUTE 'CREATE INDEX lease_participant_lease_term_v_idx ON '||v_schema_name||'.lease_participant USING btree(lease_term_v)';

        -- maintenance_request - hope that there are no real maintenance requests!

        EXECUTE 'ALTER TABLE '||v_schema_name||'.maintenance_request ADD COLUMN lease_customer BIGINT,'||
                                        'ADD COLUMN lease_customerdiscriminator VARCHAR(50),'||
                                        'ADD CONSTRAINT maintenance_request_lease_customer_fk FOREIGN KEY(lease_customer) '||
                                        '   REFERENCES '||v_schema_name||'.lease_customer(id)';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.maintenance_request DROP COLUMN tenant';

        -- payment_record

        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_record RENAME COLUMN lease_participant TO tenant_id ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_record ADD COLUMN lease_participant BIGINT ';

        EXECUTE 'UPDATE '||v_schema_name||'.payment_record AS a '||
            'SET    lease_participant = b.id '||
            'FROM   (SELECT id,tenant_id FROM '||v_schema_name||'.lease_participant) AS b '||
            'WHERE  a.tenant_id = b.tenant_id ';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_record ADD CONSTRAINT payment_record_lease_participant_fk FOREIGN KEY(lease_participant) '||
                                'REFERENCES '||v_schema_name||'.lease_participant(id),'||
                                'DROP COLUMN tenant_id ';

        -- tenant_charge

        EXECUTE 'ALTER TABLE '||v_schema_name||'.tenant_charge DROP CONSTRAINT IF EXISTS tenant_charge_tenant_fk';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.tenant_charge RENAME COLUMN tenant  TO old_tenant';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.tenant_charge ADD COLUMN tenant BIGINT';

        EXECUTE 'UPDATE '||v_schema_name||'.tenant_charge AS a '||
        '   SET     tenant = b.id '
        '   FROM    (SELECT id,tenant_id '||
                'FROM '||v_schema_name||'.lease_participant ) AS b '||
        '   WHERE   a.old_tenant = b.tenant_id ';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.tenant_charge ADD CONSTRAINT tenant_charge_tenant_fk FOREIGN KEY(tenant) '||
                            'REFERENCES '||v_schema_name||'.lease_participant(id), '||
                            'DROP COLUMN old_tenant';


        /** Cleanup **/

        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term_v DROP COLUMN old_id';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_participant DROP COLUMN tenant_id';


        -- Delete all extra tables
        
        FOREACH v_table_name IN ARRAY
        ARRAY[  'guarantor',
                'lease_v',
                'lease_vlease_products$concessions',
                'lease_vlease_products$feature_items',
                'lease$documents',
                'tenant']
        LOOP
            SELECT * INTO v_void FROM _dba_.drop_schema_table(v_schema_name,v_table_name,TRUE);

        END LOOP;

        

        /**
        ***     ======================================================================================
        ***
        ***             Set NOT NULL constraints
        ***
        ***     ======================================================================================
        **/

        EXECUTE 'ALTER TABLE '||v_schema_name||'.application_wizard_substep ALTER COLUMN step SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.appointment ALTER COLUMN lead SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.apt_unit ALTER COLUMN building SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.apt_unit_item ALTER COLUMN apt_unit SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.apt_unit_occupancy_segment ALTER COLUMN unit SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billable_item_adjustment ALTER COLUMN billable_item SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_bill ALTER COLUMN billing_account SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_billing_cycle ALTER COLUMN building SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_debit_credit_link ALTER COLUMN credit_item SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_debit_credit_link ALTER COLUMN credit_itemdiscriminator SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_invoice_line_item ALTER COLUMN billing_account SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.boiler ALTER COLUMN building SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.building_amenity ALTER COLUMN building SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.building_merchant_account ALTER COLUMN building SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.concession ALTER COLUMN catalog SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.concession_v ALTER COLUMN holder SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_accepted_terms ALTER COLUMN customer SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.deposit_policy_item ALTER COLUMN policy SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.elevator ALTER COLUMN building SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.email_template ALTER COLUMN policy SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.emergency_contact ALTER COLUMN customer SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.floorplan ALTER COLUMN building SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.floorplan_amenity ALTER COLUMN floorplan SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.gl_code ALTER COLUMN gl_code_category SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.id_assignment_item ALTER COLUMN policy SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.identification_document_type ALTER COLUMN policy SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.invoice_adjustment_sub_line_item ALTER COLUMN line_item SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.invoice_adjustment_sub_line_item ALTER COLUMN line_itemdiscriminator SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.invoice_charge_sub_line_item ALTER COLUMN line_item SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.invoice_charge_sub_line_item ALTER COLUMN line_itemdiscriminator SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.invoice_concession_sub_line_item ALTER COLUMN line_item SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.invoice_concession_sub_line_item ALTER COLUMN line_itemdiscriminator SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.issue_classification ALTER COLUMN subject_details SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.issue_repair_subject ALTER COLUMN issue_element SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.issue_subject_details ALTER COLUMN subject SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.late_fee_item ALTER COLUMN policy SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lead_guest ALTER COLUMN lead SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_adjustment_policy_item ALTER COLUMN policy SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_customer ALTER COLUMN customer SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_customer ALTER COLUMN lease SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_participant ALTER COLUMN lease_customer SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_participant ALTER COLUMN lease_customerdiscriminator SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_participant ALTER COLUMN lease_term_v SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term ALTER COLUMN lease SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term_v ALTER COLUMN holder SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.locker ALTER COLUMN locker_area SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.locker_area ALTER COLUMN building SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.maintenance_request ALTER COLUMN lease_customer SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.maintenance_request ALTER COLUMN lease_customerdiscriminator SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.nsf_fee_item ALTER COLUMN policy SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.online_application ALTER COLUMN master_online_application SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.page_content ALTER COLUMN descriptor SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.page_descriptor ALTER COLUMN parent SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.page_descriptor ALTER COLUMN parentdiscriminator SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.parking ALTER COLUMN building SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.parking_spot ALTER COLUMN parking SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_information ALTER COLUMN payment_method_customer SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_method ALTER COLUMN customer SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_record ALTER COLUMN billing_account SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_record_processing ALTER COLUMN payment_record SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.person_screening ALTER COLUMN screene SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.person_screening_personal_asset ALTER COLUMN owner SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.person_screening_personal_income ALTER COLUMN owner SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.person_screening_v ALTER COLUMN holder SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product ALTER COLUMN catalog SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_catalog ALTER COLUMN building SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_item ALTER COLUMN product SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_item ALTER COLUMN productdiscriminator SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_tax_policy_item ALTER COLUMN policy SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_v ALTER COLUMN holder SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_v ALTER COLUMN holderdiscriminator SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.roof ALTER COLUMN building SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.roof_segment ALTER COLUMN roof SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.showing ALTER COLUMN appointment SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.unit_turnover_stats ALTER COLUMN building SET NOT NULL' ;


    END LOOP;

    EXCEPTION WHEN OTHERS THEN
                RAISE EXCEPTION 'Failed executing statement, code: %, error message: %',SQLSTATE,SQLERRM ;
END;
$$
LANGUAGE plpgsql VOLATILE;

SELECT * FROM _dba_.migrate_to_105();
DROP FUNCTION _dba_.migrate_to_105();

SET client_min_messages = 'NOTICE';

COMMIT;

