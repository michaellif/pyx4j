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

BEGIN TRANSACTION;
        SELECT _dba_.rename_discriminator_cols();
COMMIT;

DROP FUNCTION _dba_.rename_discriminator_cols();

BEGIN TRANSACTION;

SET client_min_messages = 'WARNING';

/** _admin_ schema **/

SET search_path = '_admin_';

ALTER TABLE admin_pmc ADD COLUMN schema_version VARCHAR(500);
ALTER TABLE admin_pmc ADD COLUMN schema_data_upgrade_steps INT;

ALTER TABLE admin_pmc_equifax_info
    ADD COLUMN report_type VARCHAR(50),
    ADD COLUMN approved BOOLEAN;

ALTER TABLE audit_record ALTER COLUMN details TYPE VARCHAR(1024);

ALTER TABLE admin_pmc ADD CONSTRAINT admin_pmc_status_e_ck CHECK (status IN ('Active','Cancelled','Created','Suspended','Terminated'));
ALTER TABLE admin_pmc_account_numbers ADD CONSTRAINT admin_pmc_account_numbers_pmc_type_e_ck CHECK (pmc_type IN ('Large','Medium','Small'));
ALTER TABLE admin_pmc_dns_name ADD CONSTRAINT admin_pmc_dns_name_target_e_ck CHECK (target IN ('prospectPortal','residentPortal','vistaCrm'));
ALTER TABLE admin_pmc_equifax_info ADD CONSTRAINT admin_pmc_equifax_info_report_type_e_ck CHECK (report_type IN ('longReport','shortReport'));
ALTER TABLE audit_record ADD CONSTRAINT audit_record_app_e_ck CHECK (app IN ('admin','crm','prospect','resident'));
ALTER TABLE audit_record ADD CONSTRAINT audit_record_event_e_ck 
CHECK (event IN ('Create','CredentialUpdate','Info','Login','LoginFailed','PermitionsUpdate','Read','Update'));
ALTER TABLE legal_document ADD CONSTRAINT legal_document_locale_e_ck CHECK (locale IN ('en','en_CA','en_US','es','fr','fr_CA','ru','zh_CN','zh_TW'));
ALTER TABLE onboarding_user_credential ADD CONSTRAINT onboarding_user_credential_behavior_e_ck 
CHECK (behavior IN ('Caledon','Client','Equifax','OnboardingAdministrator','ProspectiveClient','VistaDemo'));
ALTER TABLE pad_file ADD CONSTRAINT pad_file_acknowledgment_status_e_ck 
CHECK (acknowledgment_status IN ('Accepted','BatchAndTransactionReject','BatchLevelReject','DetailRecordCountOutOfBalance','FileOutOfBalance', 'InvalidFileFormat', 'InvalidFileHeader','TransactionReject'));
ALTER TABLE pad_file ADD CONSTRAINT pad_file_status_e_ck 
CHECK (status IN ('AcknowledgeProcesed','Acknowledged','Canceled','Creating','Invalid','Procesed','SendError','Sending','Sent'));
ALTER TABLE pad_reconciliation_debit_record ADD CONSTRAINT pad_reconciliation_debit_record_reconciliation_status_e_ck 
CHECK (reconciliation_status IN ('DUPLICATE','PROCESSED','REJECTED','RETURNED'));
ALTER TABLE pad_reconciliation_summary ADD CONSTRAINT pad_reconciliation_summary_reconciliation_status_e_ck CHECK (reconciliation_status IN ('HOLD','PAID'));
ALTER TABLE pad_sim_batch ADD CONSTRAINT pad_sim_batch_reconciliation_status_e_ck CHECK (reconciliation_status IN ('HOLD','PAID'));
ALTER TABLE pad_sim_debit_record ADD CONSTRAINT pad_sim_debit_record_reconciliation_status_e_ck 
CHECK (reconciliation_status IN ('DUPLICATE','PROCESSED','REJECTED','RETURNED'));
ALTER TABLE pad_sim_file ADD CONSTRAINT pad_sim_file_status_e_ck CHECK (status IN ('Acknowledged','Loaded','ReconciliationSent'));
ALTER TABLE scheduler_run ADD CONSTRAINT scheduler_run_status_e_ck CHECK (status IN ('Completed','Failed','PartiallyCompleted','Running','Sleeping'));
ALTER TABLE scheduler_run_data ADD CONSTRAINT scheduler_run_data_status_e_ck CHECK (status IN ('Canceled','Erred','Failed','NeverRan','Processed','Running'));
ALTER TABLE scheduler_trigger ADD CONSTRAINT scheduler_trigger_population_type_e_ck CHECK (population_type IN ('allPmc','except','manual'));
ALTER TABLE scheduler_trigger ADD CONSTRAINT scheduler_trigger_trigger_type_e_ck 
CHECK (trigger_type IN ('billing','cleanup','initializeFutureBillingCycles','leaseActivation','leaseCompletion','leaseRenewal','paymentsBmoRecive','paymentsIssue', 'paymentsPadReciveAcknowledgment','paymentsPadReciveReconciliation','paymentsPadSend','paymentsScheduledCreditCards','paymentsScheduledEcheck','test','updateArrears', 'updatePaymentsSummary'));
ALTER TABLE scheduler_trigger_notification ADD CONSTRAINT scheduler_trigger_notification_event_e_ck CHECK (event IN ('All','Completed','Error'));
ALTER TABLE scheduler_trigger_schedule ADD CONSTRAINT scheduler_trigger_schedule_repeat_type_e_ck 
CHECK (repeat_type IN ('Daily','Hourly','Manual','Minute','Monthly', 'Once','Weekly'));


ALTER TABLE vista_terms_v ALTER COLUMN holder SET NOT NULL;
ALTER TABLE scheduler_trigger ADD CONSTRAINT scheduler_trigger_trigger_details_discriminator_d_ck
        CHECK (trigger_details_discriminator IN ('default','pad','test'));
ALTER TABLE scheduler_trigger_details ADD CONSTRAINT scheduler_trigger_details_id_discriminator_ck
        CHECK (id_discriminator IN ('default','pad','test'));


DROP INDEX admin_pmc_dns_name_idx;
CREATE UNIQUE INDEX admin_pmc_dns_name_idx ON admin_pmc USING btree (LOWER(dns_name));

CREATE UNIQUE INDEX admin_pmc_equifax_info_pmc_idx ON admin_pmc_equifax_info USING btree (pmc);
CREATE UNIQUE INDEX admin_pmc_payment_type_info_pmc_idx ON admin_pmc_payment_type_info USING btree (pmc);
CREATE INDEX pad_batch_pad_file_idx ON pad_batch USING btree (pad_file);
CREATE INDEX pad_debit_record_pad_batch_idx ON pad_debit_record USING btree (pad_batch);
CREATE INDEX pad_reconciliation_debit_record_reconciliation_summary_idx ON pad_reconciliation_debit_record USING btree (reconciliation_summary);
CREATE INDEX pad_reconciliation_summary_reconciliation_file_idx ON pad_reconciliation_summary USING btree (reconciliation_file);
CREATE INDEX pad_sim_batch_pad_file_idx ON pad_sim_batch USING btree (pad_file);



/**     public schema   **/

SET search_path = 'public';

-- Remove sequences for tables to be deleted - 63 in total

DROP SEQUENCE arrears_status_gadget_metadata$column_descriptors_seq;
DROP SEQUENCE arrears_status_gadget_metadata_seq;
DROP SEQUENCE arrears_summary_gadget_metadata$column_descriptors_seq;
DROP SEQUENCE arrears_summary_gadget_metadata_seq;
DROP SEQUENCE arrears_yoyanalysis_chart_metadata_seq;
DROP SEQUENCE bank_account_info_approval_seq;
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
DROP SEQUENCE get_satisfaction_fastpass_url_response_io_seq;
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
DROP SEQUENCE note_attachment_seq;
DROP SEQUENCE note$attachments_seq;
DROP SEQUENCE note_seq;
DROP SEQUENCE notes_seq;
DROP SEQUENCE notes_and_attachments$notes_seq;
DROP SEQUENCE payment_records_gadget_metadata$column_descriptors_seq;
DROP SEQUENCE payment_records_gadget_metadata$payment_method_filter_seq;
DROP SEQUENCE payment_records_gadget_metadata$payment_status_filter_seq;
DROP SEQUENCE payment_records_gadget_metadata_seq;
DROP SEQUENCE payments_summary_gadget_metadata$column_descriptors_seq;
DROP SEQUENCE payments_summary_gadget_metadata$payment_status_seq;
DROP SEQUENCE payments_summary_gadget_metadata_seq;
DROP SEQUENCE person_screening_personal_asset_seq;
DROP SEQUENCE person_screening_seq;
DROP SEQUENCE personal_income_seq;
DROP SEQUENCE onboarding_user_password_reset_question_response_io_seq;
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
DROP SEQUENCE yardi_connection_seq;

-- Create new sequences - 21 total
CREATE SEQUENCE background_check_policy_v_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE background_check_policy_v_seq OWNER TO vista;
CREATE SEQUENCE billing_billing_cycle_stats_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE billing_billing_cycle_stats_seq OWNER TO vista;
CREATE SEQUENCE gadget_metadata_holder_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE gadget_metadata_holder_seq OWNER TO vista;
CREATE SEQUENCE lease_participant_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE lease_participant_seq OWNER TO vista;
CREATE SEQUENCE lease_term_participant_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE lease_term_participant_seq OWNER TO vista;
CREATE SEQUENCE lease_term_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE lease_term_seq OWNER TO vista;
CREATE SEQUENCE lease_term_v_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE lease_term_v_seq OWNER TO vista;
CREATE SEQUENCE lease_term_vlease_products$concessions_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE lease_term_vlease_products$concessions_seq OWNER TO vista;
CREATE SEQUENCE lease_term_vlease_products$feature_items_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE lease_term_vlease_products$feature_items_seq OWNER TO vista;
--CREATE SEQUENCE notes_and_attachments$attachments_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
--ALTER SEQUENCE notes_and_attachments$attachments_seq OWNER TO vista;
CREATE SEQUENCE customer_credit_check_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE customer_credit_check_seq OWNER TO vista;
CREATE SEQUENCE customer_screening_income_info_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE customer_screening_income_info_seq OWNER TO vista;
CREATE SEQUENCE customer_screening_legal_questions_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE customer_screening_legal_questions_seq OWNER TO vista;
CREATE SEQUENCE customer_screening_income_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE customer_screening_income_seq OWNER TO vista;
CREATE SEQUENCE customer_screening_v_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
CREATE SEQUENCE customer_screening_personal_asset_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE customer_screening_personal_asset_seq OWNER TO vista;
CREATE SEQUENCE customer_screening_seq START WITH 1 INCREMENT BY 1 NO MAXVALUE NO MINVALUE CACHE 1;
ALTER SEQUENCE customer_screening_seq OWNER TO vista;
ALTER SEQUENCE customer_screening_v_seq OWNER TO vista;
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

COMMIT;

CREATE OR REPLACE FUNCTION _dba_.convert_id_to_string(TEXT) RETURNS TEXT AS
$$
    SELECT  CASE WHEN $1 ~ '^[0-9]+$' THEN LPAD($1,7,'0')
        ELSE regexp_replace($1,'([0-9]+)',LPAD('\1',7,'0'),'g') END;
$$
LANGUAGE SQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _dba_.migrate_to_105(v_schema_name VARCHAR(64)) RETURNS VOID AS
$$
DECLARE
    v_table_name                VARCHAR(64);
    v_void                      CHAR(1);
BEGIN

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

        -- apt_unit_occupancy_segment
        EXECUTE 'UPDATE '||v_schema_name||'.apt_unit_occupancy_segment '
        ||'     SET     status = ''occupied'' '
        ||'     WHERE   status = ''leased'' ';

        -- billing_arrears_snapshot
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_arrears_snapshot ADD CONSTRAINT billing_arrears_snapshot_billing_account_ck '||
        'CHECK (((id_discriminator = ''LeaseArrearsSnapshot'') AND (billing_account IS NOT NULL)) OR ((id_discriminator != ''LeaseArrearsSnapshot'') AND (billing_account IS NULL))),'||
        'ADD CONSTRAINT billing_arrears_snapshot_building_ck CHECK (((id_discriminator = ''BuildingArrearsSnapshot'') AND (building IS NOT NULL)) '||
        'OR ((id_discriminator != ''BuildingArrearsSnapshot'') AND (building IS NULL)))';


         -- billing_bill
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_bill DROP COLUMN lease_for,'
                                                        ||'ADD COLUMN previous_charge_refunds NUMERIC(18,2)';

        EXECUTE 'UPDATE '||v_schema_name||'.billing_bill '
	||'	SET	previous_charge_refunds = 0 ';


	-- billing_billing_cycle_stats
        EXECUTE 'CREATE TABLE '||v_schema_name||'.billing_billing_cycle_stats '
        ||'( '
        ||'     id              BIGINT          NOT NULL,'
        ||'     billing_cycle   BIGINT          NOT NULL,'
        ||'     failed          BIGINT,'
        ||'     rejected        BIGINT,'
        ||'     not_confirmed   BIGINT,'
        ||'     confirmed       BIGINT,'
        ||'     CONSTRAINT billing_billing_cycle_stats_pk PRIMARY KEY(id) '
        ||') ';


        EXECUTE 'INSERT INTO '||v_schema_name||'.billing_billing_cycle_stats (id,failed,rejected,not_confirmed,confirmed,billing_cycle) '
        ||'(SELECT nextval(''public.billing_billing_cycle_stats_seq'') AS id,failed,rejected,not_confirmed,confirmed,id AS billing_cycle '
        ||'FROM '||v_schema_name||'.billing_billing_cycle '
        ||'ORDER BY id )';

        EXECUTE 'CREATE UNIQUE INDEX billing_billing_cycle_stats_billing_cycle_idx ON '||v_schema_name||'.billing_billing_cycle_stats USING btree(billing_cycle)';


        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_billing_cycle_stats ADD CONSTRAINT billing_billing_cycle_stats_billing_cycle_fk FOREIGN KEY(billing_cycle) '||
        'REFERENCES '||v_schema_name||'.billing_billing_cycle(id)';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_billing_cycle DROP COLUMN failed,'
                                                                ||'     DROP COLUMN rejected,'
                                                                ||'     DROP COLUMN not_confirmed,'
                                                                ||'     DROP COLUMN confirmed';


        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_billing_cycle_stats OWNER TO vista';

        -- billing_billing_type
        EXECUTE 'CREATE UNIQUE INDEX billing_type_payment_frequency_billing_cycle_start_day_idx ON '||v_schema_name
        ||'.billing_billing_type USING btree (payment_frequency, billing_cycle_start_day) ';

        -- billing_debit_credit_link
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_debit_credit_link ADD COLUMN credit_item_discriminator VARCHAR(50),'||
                                        'ADD COLUMN debit_item_discriminator VARCHAR(50)';

        EXECUTE 'UPDATE '||v_schema_name||'.billing_debit_credit_link AS a '||
                'SET     credit_item_discriminator = b.id_discriminator '||
                'FROM    (SELECT id,id_discriminator FROM '||v_schema_name||'.billing_invoice_line_item ) AS b '||
                'WHERE  a.credit_item = b.id ';

        EXECUTE 'UPDATE '||v_schema_name||'.billing_debit_credit_link AS a '||
        '       SET     debit_item_discriminator = b.id_discriminator '||
        '       FROM    (SELECT id,id_discriminator FROM '||v_schema_name||'.billing_invoice_line_item ) AS b '||
        '       WHERE  a.debit_item = b.id ';


        -- billing_invoice_line_item
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_invoice_line_item ADD COLUMN product_charge_discriminator VARCHAR(50)';

        EXECUTE 'UPDATE '||v_schema_name||'.billing_invoice_line_item AS a '||
        '       SET     product_charge_discriminator = b.id_discriminator '||
        '       FROM    '||v_schema_name||'.billing_invoice_line_item b '||
        '       WHERE   a.product_charge = b.id ';

        -- boiler
        EXECUTE 'ALTER TABLE '||v_schema_name||'.boiler DROP COLUMN notes';


        -- building
        EXECUTE 'ALTER TABLE '||v_schema_name||'.building DROP COLUMN dashboard, DROP COLUMN notes_and_attachments';

        -- complex
        EXECUTE 'ALTER TABLE '||v_schema_name||'.complex DROP COLUMN dashboard';

        -- crm_role$behaviors
        EXECUTE 'INSERT INTO '||v_schema_name||'.crm_role$behaviors (id,owner,value) '||
        '(SELECT nextval(''public.crm_role$behaviors_seq'') AS id, id AS owner,''DashboardManager'' '||
        'FROM '||v_schema_name||'.crm_role '||
        'WHERE name = ''All'') ';

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


        -- elevator
         EXECUTE 'ALTER TABLE '||v_schema_name||'.elevator DROP COLUMN notes';

         -- email_template

         EXECUTE 'CREATE UNIQUE INDEX email_template_policy_template_type_idx ON '||v_schema_name||'.email_template (policy,template_type)';


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

        -- get_satisfaction_fastpass_url_response_io
         SELECT * INTO v_void FROM _dba_.drop_schema_table(v_schema_name,'get_satisfaction_fastpass_url_response_io');


        -- identification_document_type

        EXECUTE 'ALTER TABLE '||v_schema_name||'.identification_document_type DROP COLUMN document_id, '||
                                                'DROP COLUMN document_issuer,'||
                                                'DROP COLUMN drivers_license_state';


        -- invoice_adjustment_sub_line_item
        EXECUTE 'ALTER TABLE '||v_schema_name||'.invoice_adjustment_sub_line_item ADD COLUMN line_item_discriminator VARCHAR(50)';
        EXECUTE 'CREATE INDEX invoice_adjustment_sub_line_item_line_item_discriminator_idx ON '||v_schema_name||'.invoice_adjustment_sub_line_item '||
        'USING btree (line_item_discriminator) ';

        EXECUTE 'UPDATE '||v_schema_name||'.invoice_adjustment_sub_line_item AS a '||
                'SET    line_item_discriminator = b.id_discriminator '||
                'FROM   '||v_schema_name||'.billing_invoice_line_item AS b '||
                'WHERE  a.line_item = b.id ';

        -- invoice_charge_sub_line_item
        EXECUTE 'ALTER TABLE '||v_schema_name||'.invoice_charge_sub_line_item ADD COLUMN line_item_discriminator VARCHAR(50)';
        EXECUTE 'CREATE INDEX  invoice_charge_sub_line_item_line_item_discriminator_idx ON '||v_schema_name||'.invoice_charge_sub_line_item '||
        'USING btree (line_item_discriminator) ';

        EXECUTE 'UPDATE '||v_schema_name||'.invoice_charge_sub_line_item AS a '||
                'SET    line_item_discriminator = b.id_discriminator '||
                'FROM   '||v_schema_name||'.billing_invoice_line_item AS b '||
                'WHERE  a.line_item = b.id ';

        -- invoice_concession_sub_line_item
        EXECUTE 'ALTER TABLE '||v_schema_name||'.invoice_concession_sub_line_item ADD COLUMN line_item_discriminator VARCHAR(50)';
        EXECUTE 'CREATE INDEX  invoice_concession_sub_line_item_line_item_discriminator_idx ON '||v_schema_name||'.invoice_concession_sub_line_item '||
        'USING btree (line_item_discriminator) ';

         EXECUTE 'UPDATE '||v_schema_name||'.invoice_concession_sub_line_item AS a '||
                'SET    line_item_discriminator = b.id_discriminator '||
                'FROM   '||v_schema_name||'.billing_invoice_line_item AS b '||
                'WHERE  a.line_item = b.id ';

        -- lead
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lead ADD COLUMN lead_id_s VARCHAR(26)';
        EXECUTE 'UPDATE '||v_schema_name||'.lead SET lead_id_s = _dba_.convert_id_to_string(lead_id) ';

        -- lease_application
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_application DROP COLUMN lease_on_application,'||
                                    'DROP COLUMN lease_on_application_for,'||
                                    'DROP COLUMN equifax_approval,'||
                                    'DROP COLUMN notes';

        EXECUTE 'DELETE FROM '||v_schema_name||'.lease_application '
        ||'       WHERE id IN     (SELECT a.id '
        ||'                       FROM '||v_schema_name||'.lease_application a '
        ||'                       JOIN '||v_schema_name||'.lease c ON (a.lease = c.id) '
        ||'                       JOIN '||v_schema_name||'.lease_v b ON (b.holder = c.id) '
        ||'                       WHERE a.status = ''Created'' AND b.status != ''Application'') ';


        /**
        ***     ==========================================================================================
        ***
        ***             Status update on lease_v table performed here so lease_term can be populated
        ***             with updated data later on
        ***
        ***     ==========================================================================================
        **/

        EXECUTE 'UPDATE '||v_schema_name||'.lease_v '
        ||'     SET     status = ''ExistingLease'' '
        ||'     WHERE   status = ''Created'' ';

        EXECUTE 'UPDATE '||v_schema_name||'.lease_v '
        ||'     SET     status = ''Completed'' '
        ||'     WHERE   status = ''FinalBillIssued'' ';



        -- legal_questions

        EXECUTE 'ALTER TABLE '||v_schema_name||'.legal_questions DROP CONSTRAINT IF EXISTS legal_questions_pk CASCADE';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.legal_questions RENAME TO customer_screening_legal_questions';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_legal_questions ADD CONSTRAINT customer_screening_legal_questions_pk PRIMARY KEY(id) ';

        -- locker_area
        EXECUTE 'ALTER TABLE '||v_schema_name||'.locker_area DROP COLUMN is_private';


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

        -- notes
        SELECT * INTO v_void FROM _dba_.drop_schema_table(v_schema_name,'notes',FALSE);


        -- note$attachments  - drop
        SELECT * INTO v_void FROM _dba_.drop_schema_table(v_schema_name,'note$attachments',FALSE);

        -- note_attachment
        SELECT * INTO v_void FROM _dba_.drop_schema_table(v_schema_name,'note_attachment');

        --  notes_and_attachments$attachments
        /*
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
        */
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

        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_payment_details RENAME COLUMN incomming_interac_trasaction TO incoming_interac_transaction';

        EXECUTE 'UPDATE '||v_schema_name||'.payment_payment_details '||
                '   SET     account_no_obfuscated_number = LPAD(account_no_obfuscated_number,12,''X''),'||
                '       card_obfuscated_number = LPAD(card_obfuscated_number,16,''X'') ';


        -- onboarding_user_password_reset_question_response_io
         SELECT * INTO v_void FROM _dba_.drop_schema_table(v_schema_name,'onboarding_user_password_reset_question_response_io');


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

        -- roof
        EXECUTE 'ALTER TABLE '||v_schema_name||'.roof DROP COLUMN notes';

        -- tenant_charge
        EXECUTE 'ALTER TABLE '||v_schema_name||'.tenant_charge ADD COLUMN tenant_discriminator VARCHAR(50)';


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

        -- yardi_connection
        SELECT * INTO v_void FROM _dba_.drop_schema_table(v_schema_name,'yardi_connection');



        /**
        *** -----------------------------------------------------------------------------------------
        ***     Service,feature, product and such
        *** -----------------------------------------------------------------------------------------
        **/

        -- product

        EXECUTE 'CREATE TABLE '||v_schema_name||'.product ( '||
                '       id                      BIGINT      NOT NULL,'||
                '       old_id                  BIGINT,'||          -- To hold original value for data migration
                '       id_discriminator         VARCHAR(64) NOT NULL,'||
                '       catalog                 BIGINT,'||
                '       order_in_catalog        INT,'||
                '       feature_type            VARCHAR(50),'||
                '       service_type            VARCHAR(50),'||
                '       updated                 TIMESTAMP,'||
                '       CONSTRAINT product_pk PRIMARY KEY (id),'||
                '       CONSTRAINT product_catalog_fk FOREIGN KEY(catalog) '||
                '               REFERENCES '||v_schema_name||'.product_catalog(id))';

        EXECUTE 'INSERT INTO '||v_schema_name||'.product (id,old_id,id_discriminator,catalog,updated) '||
        '((SELECT nextval(''public.product_seq''),id AS old_id,''feature'',catalog,updated FROM '||v_schema_name||'.feature ORDER BY id) '||
        'UNION '||
        '(SELECT nextval(''public.product_seq''),id AS old_id,''service'',catalog,updated FROM '||v_schema_name||'.service ORDER BY id ))';

        EXECUTE 'UPDATE '||v_schema_name||'.product AS a '||
                'SET    feature_type = b.feature_type '||
                'FROM   (SELECT DISTINCT holder,feature_type FROM '||v_schema_name||'.feature_v ) AS b '||
                'WHERE  a.old_id = b.holder '||
                'AND    a.id_discriminator = ''feature'' ';

        EXECUTE 'UPDATE '||v_schema_name||'.product AS a '||
                'SET    service_type = b.service_type '||
                'FROM   (SELECT DISTINCT holder,service_type FROM '||v_schema_name||'.service_v ) AS b '||
                'WHERE  a.old_id = b.holder '
                'AND    a.id_discriminator = ''service'' ';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.product ADD CONSTRAINT product_feature_type_ck CHECK ((id_discriminator = ''feature'' '||
                                                ' AND feature_type IS NOT NULL) OR (id_discriminator != ''feature'' AND feature_type IS NULL)) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product ADD CONSTRAINT product_service_type_ck CHECK ((id_discriminator = ''service'' '||
                                                ' AND service_type IS NOT NULL) OR (id_discriminator != ''service'' AND service_type IS NULL)) ';


        EXECUTE 'ALTER TABLE '||v_schema_name||'.product OWNER TO vista';


        -- product_v

        EXECUTE 'CREATE TABLE '||v_schema_name||'.product_v ( '||
            '   id          BIGINT      NOT NULL,'||
            '   old_id          BIGINT,'||          -- To hold original value for data migration
            '   id_discriminator     VARCHAR(64) NOT NULL,'||
            '   version_number      INT,'||
            '   to_date         TIMESTAMP,'||
            '   from_date       TIMESTAMP,'||
            '   holder_discriminator VARCHAR(50),'||
            '   holder          BIGINT,'||
            '   old_holder      BIGINT,'||          -- To hold original value for data migration
            '   created_by_user_key BIGINT,'||
            '   name            VARCHAR(25),'||
            '   description     VARCHAR(250),'||
            '   recurring       BOOLEAN,'||
            '   mandatory       BOOLEAN,'||
            '   CONSTRAINT product_v_pk PRIMARY KEY (id))';

        EXECUTE 'INSERT INTO '||v_schema_name||'.product_v (id,old_id,id_discriminator,version_number,to_date,from_date,'||
        'description,old_holder,created_by_user_key,name,recurring,mandatory) '||
        '(SELECT nextval(''public.product_v_seq'') AS id, id AS old_id,''feature'',version_number,to_date,from_date,'||
        'description,holder,created_by_user_key,name,recurring,mandatory '||
        'FROM '||v_schema_name||'.feature_v ORDER BY id)';


        EXECUTE 'INSERT INTO '||v_schema_name||'.product_v (id,old_id,id_discriminator,version_number,to_date,from_date,'||
        'description,old_holder,created_by_user_key,name) '||
        '(SELECT nextval(''public.product_v_seq'') AS id, id AS old_id,''service'',version_number,to_date,from_date,'||
        'description,holder,created_by_user_key,name '||
        'FROM '||v_schema_name||'.service_v ORDER BY id)';


        EXECUTE 'UPDATE '||v_schema_name||'.product_v AS b '||
        '   SET     holder = a.holder '||
        '   FROM    '||
        '   (SELECT a.id,b.id AS holder '||
        '   FROM '||v_schema_name||'.product_v a '||
        '   JOIN '||v_schema_name||'.product b ON (a.old_holder = b.old_id AND a.id_discriminator = b.id_discriminator)) AS a '||
        '   WHERE b.id = a.id ';

        EXECUTE 'UPDATE '||v_schema_name||'.product_v AS a '
                'SET holder_discriminator = b.id_discriminator '
                'FROM (SELECT a.id,a.id_discriminator '||
                '      FROM '||v_schema_name||'.product a '||
                '      JOIN '||v_schema_name||'.product_v b ON (a.id = b.holder)) AS b '||
                'WHERE a.holder = b.id';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_v ADD CONSTRAINT product_v_holder_fk FOREIGN KEY(holder) REFERENCES '||v_schema_name||'.product(id)';

        EXECUTE 'CREATE INDEX product_v_holder_holder_discriminator_from_date_to_date_idx ON '||v_schema_name||'.product_v '||
        'USING btree (holder, holder_discriminator, from_date, to_date) ';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_v OWNER TO vista';

        --product_v$features
        EXECUTE 'CREATE TABLE '||v_schema_name||'.product_v$features ( '||
        '   id          BIGINT      NOT NULL,'||
        '   owner           BIGINT,'||
        '   value_discriminator  VARCHAR(50),'||
        '   value           BIGINT,'||
        '   seq         INT,'||
        '   CONSTRAINT product_v$features_pk PRIMARY KEY(id))';

        EXECUTE 'INSERT INTO '||v_schema_name||'.product_v$features (id,owner,value_discriminator,value,seq) '||
        '(SELECT nextval(''public.product_v$features_seq'') AS id,b.id AS owner,''feature'',c.id AS value,a.seq '||
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
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_item RENAME COLUMN product TO old_product';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_item ADD COLUMN product BIGINT';

        EXECUTE 'UPDATE '||v_schema_name||'.product_item AS a '||
        '   SET     product = b.id '||
        '   FROM    '||v_schema_name||'.product_v b '
        '   WHERE   (a.old_product = b.old_id AND a.product_discriminator = b.id_discriminator) ';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_item ADD CONSTRAINT product_item_product_fk FOREIGN KEY(product) '
        ||'REFERENCES '||v_schema_name||'.product_v(id),'
        ||'DROP COLUMN old_product ';

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
                                'ADD COLUMN previous_term BIGINT,'||
                                'ADD COLUMN termination_lease_to DATE';



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
        '       id                      BIGINT      NOT NULL,'||
        '       lease_term_type         VARCHAR(50),'||
        '       lease_term_status       VARCHAR(50),'||
        '       term_from               DATE,'||
        '       term_to                 DATE,'||
        '       creation_date           DATE,'||
        '       lease                   BIGINT,'||
        '       order_in_owner          INT,'||
        '       actual_term_to          DATE,'||
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

        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease DROP COLUMN actual_lease_to';

        -- lease_participant

        EXECUTE 'CREATE TABLE '||v_schema_name||'.lease_participant ( '||
        '   id                          BIGINT          NOT NULL,'||
        '   id_discriminator             VARCHAR(64)     NOT NULL,'||
        '   lease                       BIGINT,'||
        '   customer                    BIGINT,'||
        '   participant_id              VARCHAR(14),'||
        '   preauthorized_payment       BIGINT,'||
        '   participant_id_s            VARCHAR(26),'||
        '   CONSTRAINT lease_participant_pk PRIMARY KEY(id),'||
        '   CONSTRAINT lease_participant_customer_fk FOREIGN KEY(customer) '||
        '       REFERENCES '||v_schema_name||'.customer(id),'||
        '   CONSTRAINT lease_participant_lease_fk FOREIGN KEY(lease) '||
        '       REFERENCES '||v_schema_name||'.lease(id),'||
        '   CONSTRAINT lease_participant_preauthorized_payment_fk FOREIGN KEY(preauthorized_payment) '||
        '       REFERENCES '||v_schema_name||'.payment_method(id))';

        EXECUTE 'INSERT INTO '||v_schema_name||'.lease_participant '||
        '(id,id_discriminator,lease,customer,participant_id) '||
        '(SELECT nextval(''public.lease_participant_seq'') AS id,''Tenant'' AS id_discriminator,'||
        'a.* FROM '||
        '(SELECT a.id AS lease, b.customer, MAX(b.participant_id) AS participant_id  '||
        'FROM '||v_schema_name||'.lease a '||
        'JOIN '||v_schema_name||'.lease_v c ON (a.id = c.holder) '||
        'JOIN '||v_schema_name||'.tenant b ON (c.id = b.lease_v) '||
        'GROUP BY a.id,b.customer '||
        'ORDER BY a.id ) AS a )';


        EXECUTE 'INSERT INTO '||v_schema_name||'.lease_participant '||
        '(id,id_discriminator,lease,customer,participant_id) '||
        '(SELECT nextval(''public.lease_participant_seq'') AS id,''Guarantor'' AS id_discriminator,a.* '||
        'FROM '||
        '(SELECT a.id AS lease, b.customer, MAX(b.participant_id) AS participant_id '||
        'FROM '||v_schema_name||'.lease a '||
        'JOIN '||v_schema_name||'.lease_v c ON (a.id = c.holder) '||
        'JOIN '||v_schema_name||'.guarantor b ON (c.id = b.lease_v) '||
        'GROUP BY a.id,b.customer '||
        'ORDER BY a.id ) AS a )';

        EXECUTE 'UPDATE '||v_schema_name||'.lease_participant '||
        '       SET     participant_id_s = LPAD(participant_id,7,''0'')';

        EXECUTE 'UPDATE '||v_schema_name||'.lease_participant AS a '||
        '       SET preauthorized_payment = b.preauthorized_payment '||
        '       FROM '||
        '       (SELECT DISTINCT a.id AS lease, b.customer, b.preauthorized_payment '||
        '               FROM '||v_schema_name||'.lease a '||
        '               JOIN '||v_schema_name||'.lease_v c ON (a.id = c.holder) '||
        '               JOIN '||v_schema_name||'.tenant b ON (c.id = b.lease_v) '||
        '               JOIN    (SELECT MAX(a.id) AS id,a.customer,b.id AS lease  FROM '||v_schema_name||'.tenant a '||
        '                       JOIN '||v_schema_name||'.lease_v c ON (a.lease_v = c.id) '||
        '                       JOIN '||v_schema_name||'.lease b ON (b.id = c.holder) '||
        '                       GROUP BY a.customer,b.id ) AS d ON (b.id = d.id) '||
        '               WHERE b.preauthorized_payment IS NOT NULL ) AS b '||
        '       WHERE   a.lease = b.lease '||
        '       AND     a.customer = b.customer ';



        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_participant OWNER TO vista';
        EXECUTE 'CREATE UNIQUE INDEX lease_participant_lease_customer_idx ON '||v_schema_name||'.lease_participant USING btree(lease,customer,id_discriminator)';
        EXECUTE 'CREATE UNIQUE INDEX lease_participant_participant_id_idx ON '||v_schema_name||'.lease_participant USING btree(participant_id,id_discriminator)';


        -- lease_term_participant

        EXECUTE 'CREATE TABLE '||v_schema_name||'.lease_term_participant ( '||
        '       id                              BIGINT      NOT NULL,'||
        '       tenant_id                       BIGINT,'||
        '       id_discriminator                 VARCHAR(64)     NOT NULL,'||
        '       lease_participant_discriminator     VARCHAR(50),'||
        '       lease_participant                  BIGINT,'||
        '       participant_role                VARCHAR(50),'||
        '       lease_term_v                    BIGINT,'||
        '       order_in_lease                  INT,'||
        '       application                     BIGINT,'||
        '       credit_check                    BIGINT,'||
        '       screening                       BIGINT,'||
        '       screening_for                   TIMESTAMP WITHOUT TIME ZONE,'||
        '       relationship                    VARCHAR(50),'||
        '       tenant_discriminator             VARCHAR(50),'||
        '       tenant                          BIGINT,'||
        '       take_ownership                  BOOLEAN,'||
        '       percentage                      NUMERIC(18,2),'||
        '       CONSTRAINT lease_term_participant_pk PRIMARY KEY(id),'||
        '       CONSTRAINT lease_term_participant_application_fk FOREIGN KEY(application) '||
        '               REFERENCES '||v_schema_name||'.online_application(id),'||
        '       CONSTRAINT lease_term_participant_lease_participant_fk FOREIGN KEY(lease_participant) '||
        '               REFERENCES '||v_schema_name||'.lease_participant(id), '||
        '       CONSTRAINT lease_term_participant_lease_term_v_fk FOREIGN KEY(lease_term_v) '||
        '               REFERENCES '||v_schema_name||'.lease_term_v(id), '||
       -- '       CONSTRAINT lease_term_participant_credit_check_fk FOREIGN KEY (credit_check) '||
       -- '               REFERENCES '||v_schema_name||'.customer_credit_check(id),'||
       -- '       CONSTRAINT lease_term_participant_screening_fk FOREIGN KEY(screening) '||
       -- '               REFERENCES '||v_schema_name||'.customer_screening(id), '||
        '       CONSTRAINT lease_term_participant_tenant_fk FOREIGN KEY(tenant) '||
        '               REFERENCES '||v_schema_name||'.lease_participant(id))';


        EXECUTE 'INSERT INTO '||v_schema_name||'.lease_term_participant '||
        '(id,tenant_id,id_discriminator,lease_participant_discriminator,lease_participant,participant_role,lease_term_v,order_in_lease,'||
        'application,screening,relationship,take_ownership,percentage) '||
        '(SELECT nextval(''public.lease_term_participant_seq'') AS id,a.id AS tenant_id,''Tenant'' AS id_discriminator, ''Tenant'' AS lease_participant_discriminator,'||
        'c.id AS lease_participant,a.participant_role,b.id AS lease_term_v,a.order_in_lease,a.application,a.screening,a.relationship,'||
        'a.take_ownership,a.percentage '||
        'FROM '||v_schema_name||'.tenant a '||
        'JOIN '||v_schema_name||'.lease_term_v b ON (a.lease_v = b.old_id) '||
        'JOIN '||v_schema_name||'.lease_v d ON (a.lease_v = d.id) '||
        'JOIN '||v_schema_name||'.lease e ON (d.holder = e.id) '||
        'JOIN '||v_schema_name||'.lease_participant c ON (a.customer = c.customer AND e.id = c.lease ) '||
        'ORDER BY a.id )';

        EXECUTE 'INSERT INTO '||v_schema_name||'.lease_term_participant '||
        '(id,id_discriminator,lease_participant_discriminator,lease_participant,participant_role,lease_term_v,order_in_lease,'||
        'application,screening,relationship,tenant_discriminator,tenant) '||
        '(SELECT nextval(''public.lease_term_participant_seq'') AS id,''Guarantor'' AS id_discriminator, ''Guarantor'' AS lease_participant_discriminator,'||
        'c.id AS lease_participant,a.participant_role,b.id AS lease_term_v,a.order_in_lease,a.application,a.screening,a.relationship,'||
        '''Tenant'',f.lease_participant AS tenant '||
        'FROM '||v_schema_name||'.guarantor a '||
        'JOIN '||v_schema_name||'.lease_term_v b ON (a.lease_v = b.old_id) '||
        'JOIN '||v_schema_name||'.lease_v d ON (a.lease_v = d.id) '||
        'JOIN '||v_schema_name||'.lease e ON (d.holder = e.id) '||
        'JOIN '||v_schema_name||'.lease_participant c ON (a.customer = c.customer AND e.id = c.lease) '||
        'LEFT JOIN '||v_schema_name||'.lease_term_participant f ON (f.tenant_id = a.tenant) '||
        'ORDER BY a.id )';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term_participant OWNER TO vista';
        EXECUTE 'CREATE INDEX lease_term_participant_application_idx ON '||v_schema_name||'.lease_term_participant USING btree(application)';
        EXECUTE 'CREATE INDEX lease_term_participant_lease_term_v_idx ON '||v_schema_name||'.lease_term_participant USING btree(lease_term_v)';

        -- maintenance_request

        EXECUTE 'ALTER TABLE '||v_schema_name||'.maintenance_request ADD COLUMN lease_participant BIGINT,'||
                                        'ADD COLUMN lease_participant_discriminator VARCHAR(50),'||
                                        'ADD CONSTRAINT maintenance_request_lease_participant_fk FOREIGN KEY(lease_participant) '||
                                        '   REFERENCES '||v_schema_name||'.lease_participant(id)';

        EXECUTE 'UPDATE '||v_schema_name||'.maintenance_request AS a '
        ||'     SET lease_participant = b.lease_participant,'
        ||'     lease_participant_discriminator = b.lease_participant_discriminator '
        ||'     FROM    (SELECT DISTINCT a.id AS lease_participant,'
        ||'                     b.lease_participant_discriminator, '
        ||'                     b.tenant_id '
        ||'             FROM    '||v_schema_name||'.lease_participant a '
        ||'             JOIN    '||v_schema_name||'.lease_term_participant b ON (a.id = b.lease_participant)) AS b '
        ||'     WHERE a.tenant = b.tenant_id ';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.maintenance_request DROP COLUMN tenant';

        -- payment_record

        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_record RENAME COLUMN lease_participant TO tenant_id ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_record ADD COLUMN lease_term_participant BIGINT ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_record RENAME COLUMN lease_participant_discriminator TO lease_term_participant_discriminator ';

        EXECUTE 'UPDATE '||v_schema_name||'.payment_record AS a '||
            'SET    lease_term_participant = b.id '||
            'FROM   (SELECT id,tenant_id FROM '||v_schema_name||'.lease_term_participant) AS b '||
            'WHERE  a.tenant_id = b.tenant_id ';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_record ADD CONSTRAINT payment_record_lease_term_participant_fk FOREIGN KEY(lease_term_participant) '||
                                'REFERENCES '||v_schema_name||'.lease_term_participant(id),'||
                                'DROP COLUMN tenant_id ';

        -- tenant_charge

        EXECUTE 'ALTER TABLE '||v_schema_name||'.tenant_charge DROP CONSTRAINT IF EXISTS tenant_charge_tenant_fk';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.tenant_charge RENAME COLUMN tenant  TO old_tenant';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.tenant_charge ADD COLUMN tenant BIGINT';

        EXECUTE 'UPDATE '||v_schema_name||'.tenant_charge AS a '||
        '   SET         tenant = b.id, '||
        '               tenant_discriminator = b.tenant_discriminator '||
        '   FROM        (SELECT id,tenant_id,tenant_discriminator '||
        '               FROM '||v_schema_name||'.lease_term_participant ) AS b '||
        '   WHERE   a.old_tenant = b.tenant_id ';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.tenant_charge ADD CONSTRAINT tenant_charge_tenant_fk FOREIGN KEY(tenant) '||
                            'REFERENCES '||v_schema_name||'.lease_term_participant(id), '||
                            'DROP COLUMN old_tenant';


        /**
        ***     ===============================================================================================================
        ***
        ***             Screening part - just to be sure that everything is migrated
        ***
        ***     ===============================================================================================================
        **/

        -- equifax_approval
        SELECT * INTO v_void FROM _dba_.drop_schema_table(v_schema_name,'equifax_approval');

        -- equifax_result
        SELECT * INTO v_void FROM _dba_.drop_schema_table(v_schema_name,'equifax_result');


        -- customer_screening_income_info

        EXECUTE 'CREATE TABLE '||v_schema_name||'.customer_screening_income_info ('||
                '   id              BIGINT          NOT NULL,'||
                '   id_discriminator         VARCHAR(64)         NOT NULL,'||
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
            '   CONSTRAINT customer_screening_income_info_pk PRIMARY KEY (id),'||
            '   CONSTRAINT customer_screening_income_info_address_country_fk FOREIGN KEY (address_country) '||
            '       REFERENCES '||v_schema_name||'.country(id),'||
            '   CONSTRAINT customer_screening_income_info_address_province_fk FOREIGN KEY (address_province) '||
            '       REFERENCES '||v_schema_name||'.province(id))';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_income_info OWNER TO vista';

        -- personal_income - just in case rename to customer_screening_income

        EXECUTE 'ALTER TABLE '||v_schema_name||'.personal_income DROP CONSTRAINT IF EXISTS personal_income_pk,'||
                                                'DROP CONSTRAINT IF EXISTS personal_income_employer_fk,'||
                                                'DROP CONSTRAINT IF EXISTS personal_income_other_income_information_fk,'||
                                                'DROP CONSTRAINT IF EXISTS personal_income_owner_fk,'||
                                                'DROP CONSTRAINT IF EXISTS personal_income_seasonally_employed_fk,'||
                                                'DROP CONSTRAINT IF EXISTS personal_income_self_employed_fk,'||
                                                'DROP CONSTRAINT IF EXISTS personal_income_social_services_fk,'||
                                                'DROP CONSTRAINT IF EXISTS personal_income_student_income_fk';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.personal_income RENAME TO customer_screening_income';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_income DROP COLUMN other_income_information,'||
                                                'DROP COLUMN employer,'||
                                                'DROP COLUMN self_employed,'||
                                                'DROP COLUMN seasonally_employed,'||
                                                'DROP COLUMN social_services,'||
                                                'DROP COLUMN student_income';


        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_income ADD COLUMN details_discriminator VARCHAR(50),'||
                                                'ADD COLUMN details BIGINT';


        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_income ADD CONSTRAINT  customer_screening_income_pk PRIMARY KEY(id),'||
        'ADD CONSTRAINT customer_screening_income_details_fk FOREIGN KEY(details) REFERENCES '||v_schema_name||'.customer_screening_income_info(id)' ;


        -- person_screening -> customer_screening

        EXECUTE 'ALTER TABLE '||v_schema_name||'.person_screening '||
                                                'DROP CONSTRAINT IF EXISTS person_screening_pk CASCADE,'||
                                                'DROP CONSTRAINT IF EXISTS person_screening_current_address_country_fk,'||
                                                'DROP CONSTRAINT IF EXISTS person_screening_current_address_province_fk,'||
                                                'DROP CONSTRAINT IF EXISTS person_screening_equifax_approval_fk,'||
                                                'DROP CONSTRAINT IF EXISTS person_screening_legal_questions_fk,'||
                                                'DROP CONSTRAINT IF EXISTS person_screening_previous_address_country_fk,'||
                                                'DROP CONSTRAINT IF EXISTS person_screening_previous_address_province_fk,'||
                                                'DROP CONSTRAINT IF EXISTS person_screening_screene_fk';


        EXECUTE 'ALTER TABLE '||v_schema_name||'.person_screening RENAME TO customer_screening_v';

        EXECUTE 'CREATE TABLE '||v_schema_name||'.customer_screening ( '||
                '       id              BIGINT          NOT NULL,'||
                '       screene         BIGINT,'||
                '       CONSTRAINT customer_screening_pk PRIMARY KEY(id),'||
                '       CONSTRAINT customer_screening_screene_fk FOREIGN KEY(screene) REFERENCES '||v_schema_name||'.customer(id))';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_v OWNER TO vista';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_v ADD COLUMN created_by_user_key BIGINT,'||
                                                                'ADD COLUMN from_date TIMESTAMP,'||
                                                                'ADD COLUMN to_date TIMESTAMP,'||
                                                                'ADD COLUMN holder BIGINT,'||
                                                                'ADD COLUMN Version_number INT,'||
                                                                'DROP COLUMN screene,'||
                                                                'DROP COLUMN current_address_phone,'||
                                                                'DROP COLUMN equifax_approval,'||
                                                                'DROP COLUMN previous_address_phone,'||
                        'ADD CONSTRAINT customer_screening_v_pk PRIMARY KEY(id),'||
                        'ADD CONSTRAINT customer_screening_v_current_address_country_fk FOREIGN KEY(current_address_country) REFERENCES '||v_schema_name||'.country(id),'||
                        'ADD CONSTRAINT customer_screening_v_current_address_province_fk FOREIGN KEY(current_address_province) REFERENCES '||v_schema_name||'.province(id),'||
                        'ADD CONSTRAINT customer_screening_v_holder_fk FOREIGN KEY(holder) REFERENCES '||v_schema_name||'.customer_screening(id),'||
                        'ADD CONSTRAINT customer_screening_v_legal_questions_fk FOREIGN KEY(legal_questions) REFERENCES '||v_schema_name||'.customer_screening_legal_questions(id),'||
                        'ADD CONSTRAINT customer_screening_v_previous_address_country_fk FOREIGN KEY(previous_address_country) REFERENCES '||v_schema_name||'.country(id),'||
                        'ADD CONSTRAINT customer_screening_v_previous_address_province_fk FOREIGN KEY(previous_address_province) REFERENCES '||v_schema_name||'.province(id)';

        EXECUTE 'CREATE INDEX customer_screening_v_holder_from_date_to_date_idx ON '||v_schema_name||'.customer_screening_v USING btree(holder, from_date, to_date)';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.person_screening_personal_asset RENAME TO customer_screening_personal_asset';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_personal_asset DROP CONSTRAINT person_screening_personal_asset_pk';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_personal_asset ADD CONSTRAINT customer_screening_personal_asset_pk PRIMARY KEY (id)';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_income '||
        'ADD CONSTRAINT customer_screening_income_owner_fk FOREIGN KEY(owner) REFERENCES '||v_schema_name||'.customer_screening_v(id)';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening OWNER TO vista';

        -- bank_account_info_approval
        SELECT * INTO v_void FROM _dba_.drop_schema_table(v_schema_name,'bank_account_info_approval');

        -- background_check_policy
        EXECUTE 'ALTER TABLE '||v_schema_name||'.background_check_policy ADD COLUMN version BIGINT,'||
                                        'DROP COLUMN bankruptcy,'||
                                        'DROP COLUMN judgment,'||
                                        'DROP COLUMN collection,'||
                                        'DROP COLUMN charge_off';

        -- background_check_policy_v

        EXECUTE 'CREATE TABLE '||v_schema_name||'.background_check_policy_v ( '||
        '       id              BIGINT          NOT NULL,'||
        '       bankruptcy      VARCHAR(50),'||
        '       judgment        VARCHAR(50),'||
        '       collection      VARCHAR(50),'||
        '       charge_off      VARCHAR(50),'||
        '       CONSTRAINT background_check_policy_v_pk PRIMARY KEY(id))';


        EXECUTE 'ALTER TABLE '||v_schema_name||'.background_check_policy_v OWNER TO vista';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.background_check_policy ADD CONSTRAINT background_check_policy_version_fk '||
        'FOREIGN KEY(version) REFERENCES '||v_schema_name||'.background_check_policy_v(id)';

        -- customer_credit_check
        EXECUTE 'CREATE TABLE '||v_schema_name||'.customer_credit_check ( '||
        '       id                      BIGINT          NOT NULL,'||
        '       screening               BIGINT,'||
        '       screening_for           TIMESTAMP,'||
        '       credit_check_date       TIMESTAMP,'||
        '       created_by              BIGINT,'||
        '       background_check_policy BIGINT,'||
        '       amount_checked          NUMERIC(18,2),'||
        '       risk_code               VARCHAR(500),'||
        '       credit_check_result     VARCHAR(50),'||
        '       amount_approved         NUMERIC(18,2),'||
        '       credit_check_report     BIGINT,'||
        '       reason                  VARCHAR(500),'||
        '       CONSTRAINT customer_credit_check_pk PRIMARY KEY(id),'||
        '       CONSTRAINT customer_credit_check_background_check_policy_fk FOREIGN KEY(background_check_policy) '||
        '               REFERENCES '||v_schema_name||'.background_check_policy_v(id),'||
        '       CONSTRAINT customer_credit_check_created_by_fk FOREIGN KEY(created_by) '||
        '               REFERENCES '||v_schema_name||'.employee(id),'||
        '       CONSTRAINT customer_credit_check_screening_fk FOREIGN KEY(screening) '||
        '               REFERENCES '||v_schema_name||'.customer_screening(id))';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_credit_check OWNER TO vista';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term_participant ADD CONSTRAINT lease_term_participant_credit_check_fk '||
        'FOREIGN KEY(credit_check) REFERENCES '||v_schema_name||'.customer_credit_check(id),'||
        'ADD CONSTRAINT lease_term_participant_screening_fk FOREIGN KEY(screening) REFERENCES '||v_schema_name||'.customer_screening(id)';

        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_personal_asset ADD CONSTRAINT customer_screening_personal_asset_owner_fk '||
        'FOREIGN KEY(owner) REFERENCES '||v_schema_name||'.customer_screening_v(id)';


        /** Cleanup **/

        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term_v DROP COLUMN old_id';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term_participant DROP COLUMN tenant_id';


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
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_billing_type ALTER COLUMN billing_cycle_start_day SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_billing_type ALTER COLUMN payment_frequency SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_debit_credit_link ALTER COLUMN credit_item SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_debit_credit_link ALTER COLUMN credit_item_discriminator SET NOT NULL' ;
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
        EXECUTE 'ALTER TABLE '||v_schema_name||'.invoice_adjustment_sub_line_item ALTER COLUMN line_item_discriminator SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.invoice_charge_sub_line_item ALTER COLUMN line_item SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.invoice_charge_sub_line_item ALTER COLUMN line_item_discriminator SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.invoice_concession_sub_line_item ALTER COLUMN line_item SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.invoice_concession_sub_line_item ALTER COLUMN line_item_discriminator SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.issue_classification ALTER COLUMN subject_details SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.issue_repair_subject ALTER COLUMN issue_element SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.issue_subject_details ALTER COLUMN subject SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.late_fee_item ALTER COLUMN policy SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lead_guest ALTER COLUMN lead SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_adjustment_policy_item ALTER COLUMN policy SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_participant ALTER COLUMN customer SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_participant ALTER COLUMN lease SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term_participant ALTER COLUMN lease_participant SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term_participant ALTER COLUMN lease_participant_discriminator SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term_participant ALTER COLUMN lease_term_v SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term ALTER COLUMN lease SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term_v ALTER COLUMN holder SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.locker ALTER COLUMN locker_area SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.locker_area ALTER COLUMN building SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.maintenance_request ALTER COLUMN lease_participant SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.maintenance_request ALTER COLUMN lease_participant_discriminator SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.nsf_fee_item ALTER COLUMN policy SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.online_application ALTER COLUMN master_online_application SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.page_content ALTER COLUMN descriptor SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.page_descriptor ALTER COLUMN parent SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.page_descriptor ALTER COLUMN parent_discriminator SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.parking ALTER COLUMN building SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.parking_spot ALTER COLUMN parking SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_information ALTER COLUMN payment_method_customer SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_method ALTER COLUMN customer SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_record ALTER COLUMN billing_account SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_record_processing ALTER COLUMN payment_record SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening ALTER COLUMN screene SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_personal_asset ALTER COLUMN owner SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_income ALTER COLUMN details SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_income ALTER COLUMN details_discriminator SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_income ALTER COLUMN income_source SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_income ALTER COLUMN owner SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_v ALTER COLUMN holder SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product ALTER COLUMN catalog SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_catalog ALTER COLUMN building SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_item ALTER COLUMN product SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_item ALTER COLUMN product_discriminator SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_tax_policy_item ALTER COLUMN policy SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_v ALTER COLUMN holder SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_v ALTER COLUMN holder_discriminator SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.roof ALTER COLUMN building SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.roof_segment ALTER COLUMN roof SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.showing ALTER COLUMN appointment SET NOT NULL' ;
        EXECUTE 'ALTER TABLE '||v_schema_name||'.unit_turnover_stats ALTER COLUMN building SET NOT NULL' ;



        /**
        ***     ============================================================================================================
        ***
        ***             CHECK constraints
        ***
        ***     ============================================================================================================
        **/

        EXECUTE 'ALTER TABLE '||v_schema_name||'.application_document_file ADD CONSTRAINT application_document_file_owner_discriminator_d_ck '||
                'CHECK (owner_discriminator IN (''IdentificationDocument'',''InsuranceCertificate'',''ProofOfEmploymentDocument'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.application_documentation_policy ADD CONSTRAINT application_documentation_policy_node_discriminator_d_ck '||
                'CHECK (node_discriminator IN (''Disc Complex'',''Disc_Building'',''Disc_Country'',''Disc_Floorplan'',''Disc_Province'',''OrganizationPoliciesNode'',''Unit_BuildingElement'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.arpolicy ADD CONSTRAINT arpolicy_node_discriminator_d_ck '||
                'CHECK (node_discriminator IN (''Disc Complex'',''Disc_Building'',''Disc_Country'',''Disc_Floorplan'',''Disc_Province'',''OrganizationPoliciesNode'',''Unit_BuildingElement'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.background_check_policy ADD CONSTRAINT background_check_policy_node_discriminator_d_ck '||
               'CHECK (node_discriminator IN (''Disc Complex'',''Disc_Building'',''Disc_Country'',''Disc_Floorplan'',''Disc_Province'',''OrganizationPoliciesNode'',''Unit_BuildingElement'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_arrears_snapshot ADD CONSTRAINT billing_arrears_snapshot_id_discriminator_ck '||
                'CHECK (id_discriminator IN (''BuildingArrearsSnapshot'', ''LeaseArrearsSnapshot'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billable_item ADD CONSTRAINT billable_item_extra_data_discriminator_d_ck '||
                'CHECK (extra_data_discriminator IN (''Pet_ChargeItemExtraData'',''Vehicle_ChargeItemExtraData'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_debit_credit_link ADD CONSTRAINT billing_debit_credit_link_credit_item_discriminator_d_ck '||
                'CHECK (credit_item_discriminator IN (''AccountCredit'',''CarryforwardCredit'',''DepositRefund'',''Payment'',''ProductCredit'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_debit_credit_link ADD CONSTRAINT billing_debit_credit_link_debit_item_discriminator_d_ck '||
                'CHECK (debit_item_discriminator IN (''AccountCharge'',''CarryforwardCharge'',''Deposit'',''LatePaymentFee'',''NSF'',''PaymentBackOut'',''ProductCharge'',''Withdrawal'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_product_charge_discriminator_d_ck '||
                'CHECK (product_charge_discriminator = ''ProductCharge'') ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_id_discriminator_ck '||
                'CHECK (id_discriminator IN (''AccountCharge'',''AccountCredit'',''CarryforwardCharge'',''CarryforwardCredit'',''Deposit'',''DepositRefund'',''LatePaymentFee'','||
                '''NSF'', ''Payment'',''PaymentBackOut'',''ProductCharge'',''ProductCredit'',''Withdrawal'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.concession_v ADD CONSTRAINT concession_v_product_item_type_discriminator_d_ck '||
                'CHECK (product_item_type_discriminator IN (''feature'',''service'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.dates_policy ADD CONSTRAINT dates_policy_node_discriminator_d_ck '||
                'CHECK (node_discriminator IN (''Disc Complex'',''Disc_Building'',''Disc_Country'',''Disc_Floorplan'',''Disc_Province'',''OrganizationPoliciesNode'',''Unit_BuildingElement'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.deposit_policy ADD CONSTRAINT deposit_policy_node_discriminator_d_ck '||
                'CHECK (node_discriminator IN (''Disc Complex'',''Disc_Building'',''Disc_Country'',''Disc_Floorplan'',''Disc_Province'',''OrganizationPoliciesNode'',''Unit_BuildingElement'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.deposit_policy_item ADD CONSTRAINT deposit_policy_item_product_type_discriminator_d_ck '||
                'CHECK (product_type_discriminator IN (''feature'',''service'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.email_templates_policy ADD CONSTRAINT email_templates_policy_node_discriminator_d_ck '||
                'CHECK (node_discriminator IN (''Disc Complex'',''Disc_Building'',''Disc_Country'',''Disc_Floorplan'',''Disc_Province'',''OrganizationPoliciesNode'',''Unit_BuildingElement'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.gadget_content ADD CONSTRAINT gadget_content_id_discriminator_ck '||
                'CHECK (id_discriminator IN (''Custom'',''News'',''Promo'',''Testimonials'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.home_page_gadget ADD CONSTRAINT home_page_gadget_content_discriminator_d_ck '||
                'CHECK (content_discriminator IN (''Custom'',''News'',''Promo'',''Testimonials'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.id_assignment_policy ADD CONSTRAINT id_assignment_policy_node_discriminator_d_ck '||
                'CHECK (node_discriminator IN (''Disc Complex'',''Disc_Building'',''Disc_Country'',''Disc_Floorplan'',''Disc_Province'',''OrganizationPoliciesNode'',''Unit_BuildingElement'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.identification_document ADD CONSTRAINT identification_document_owner_discriminator_d_ck '
        ||'CHECK (owner_discriminator IN (''CustomerScreening'',''CustomerScreeningIncome'',''ExistingInsurance''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.insurance_certificate ADD CONSTRAINT insurance_certificate_owner_discriminator_d_ck '
        ||'CHECK (owner_discriminator IN (''CustomerScreening'',''CustomerScreeningIncome'',''ExistingInsurance''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.invoice_adjustment_sub_line_item ADD CONSTRAINT invoice_adjustment_sub_line_item_line_item_discriminator_d_ck '||
                'CHECK (line_item_discriminator = ''ProductCharge'') ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.invoice_charge_sub_line_item ADD CONSTRAINT invoice_charge_sub_line_item_line_item_discriminator_d_ck '||
                'CHECK (line_item_discriminator = ''ProductCharge'') ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.invoice_concession_sub_line_item ADD CONSTRAINT invoice_concession_sub_line_item_line_item_discriminator_d_ck '||
                'CHECK (line_item_discriminator = ''ProductCharge'') ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_adjustment_policy ADD CONSTRAINT lease_adjustment_policy_node_discriminator_d_ck '||
                'CHECK (node_discriminator IN (''Disc Complex'',''Disc_Building'',''Disc_Country'',''Disc_Floorplan'',''Disc_Province'',''OrganizationPoliciesNode'',''Unit_BuildingElement'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_billing_policy ADD CONSTRAINT lease_billing_policy_node_discriminator_d_ck '||
                'CHECK (node_discriminator IN (''Disc Complex'',''Disc_Building'',''Disc_Country'',''Disc_Floorplan'',''Disc_Province'',''OrganizationPoliciesNode'',''Unit_BuildingElement'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_participant ADD CONSTRAINT lease_participant_id_discriminator_ck '||
                'CHECK (id_discriminator IN (''Guarantor'',''Tenant'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term_participant ADD CONSTRAINT lease_term_participant_lease_participant_discriminator_d_ck '||
                'CHECK (lease_participant_discriminator IN (''Guarantor'',''Tenant'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term_participant ADD CONSTRAINT lease_term_participant_id_discriminator_ck '||
                'CHECK (id_discriminator IN (''Guarantor'',''Tenant'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term_participant ADD CONSTRAINT lease_term_participant_tenant_discriminator_d_ck CHECK (tenant_discriminator = ''Tenant'') ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.legal_documentation ADD CONSTRAINT legal_documentation_node_discriminator_d_ck '||
               'CHECK (node_discriminator IN (''Disc Complex'',''Disc_Building'',''Disc_Country'',''Disc_Floorplan'',''Disc_Province'',''OrganizationPoliciesNode'',''Unit_BuildingElement'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.maintenance_request ADD CONSTRAINT maintenance_request_lease_participant_discriminator_d_ck CHECK (lease_participant_discriminator = ''Tenant'') ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.page_descriptor ADD CONSTRAINT page_descriptor_parent_discriminator_d_ck '||
                'CHECK (parent_discriminator IN (''PageDescriptor'',''SiteDescriptor'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_information ADD CONSTRAINT payment_information_payment_method_details_discriminator_d_ck '||
                'CHECK (payment_method_details_discriminator IN (''CashInfo'',''CheckInfo'',''CreditCard'',''EcheckInfo'',''InteracInfo'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_method ADD CONSTRAINT payment_method_details_discriminator_d_ck '||
                'CHECK (details_discriminator IN (''CashInfo'',''CheckInfo'',''CreditCard'',''EcheckInfo'',''InteracInfo'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_payment_details ADD CONSTRAINT payment_payment_details_id_discriminator_ck '||
                'CHECK (id_discriminator IN (''CashInfo'',''CheckInfo'',''CreditCard'',''EcheckInfo'',''InteracInfo'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_record ADD CONSTRAINT payment_record_lease_term_participant_discriminator_d_ck '||
                'CHECK (lease_term_participant_discriminator IN (''Guarantor'',''Tenant'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_transactions_policy ADD CONSTRAINT payment_transactions_policy_node_discriminator_d_ck '||
                'CHECK (node_discriminator IN (''Disc Complex'',''Disc_Building'',''Disc_Country'',''Disc_Floorplan'',''Disc_Province'',''OrganizationPoliciesNode'',''Unit_BuildingElement'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_income_info ADD CONSTRAINT customer_screening_income_info_id_discriminator_ck '||
                'CHECK (id_discriminator IN (''employee'',''other'',''seasonalEmployee'',''selfEmployed'',''socialServices'',''student'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_income ADD CONSTRAINT customer_screening_income_details_discriminator_d_ck '||
                'CHECK (details_discriminator IN (''employee'',''other'',''seasonalEmployee'',''selfEmployed'',''socialServices'',''student'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.pet_constraints ADD CONSTRAINT pet_constraints_pet_discriminator_d_ck '||
                'CHECK (pet_discriminator IN (''feature'',''service'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.pet_policy ADD CONSTRAINT pet_policy_node_discriminator_d_ck '||
                'CHECK (node_discriminator IN (''Disc Complex'',''Disc_Building'',''Disc_Country'',''Disc_Floorplan'',''Disc_Province'',''OrganizationPoliciesNode'',''Unit_BuildingElement'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product ADD CONSTRAINT product_id_discriminator_ck '||
                'CHECK (id_discriminator IN (''feature'',''service'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_item ADD CONSTRAINT product_item_element_discriminator_d_ck '||
                'CHECK (element_discriminator IN (''LockerArea_BuildingElement'',''Parking_BuildingElement'',''Roof_BuildingElement'',''Unit_BuildingElement'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_item ADD CONSTRAINT product_item_item_type_discriminator_d_ck '||
                'CHECK (item_type_discriminator IN (''feature'',''service'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_item ADD CONSTRAINT product_item_product_discriminator_d_ck '||
                'CHECK (product_discriminator IN (''feature'',''service'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_item_type ADD CONSTRAINT product_item_type_id_discriminator_ck '||
                'CHECK (id_discriminator IN (''feature'',''service'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_tax_policy ADD CONSTRAINT product_tax_policy_node_discriminator_d_ck '||
                'CHECK (node_discriminator IN (''Disc Complex'',''Disc_Building'',''Disc_Country'',''Disc_Floorplan'',''Disc_Province'',''OrganizationPoliciesNode'',''Unit_BuildingElement'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_tax_policy_item ADD CONSTRAINT product_tax_policy_item_product_item_type_discriminator_d_ck '||
                'CHECK (product_item_type_discriminator IN (''feature'',''service'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_v ADD CONSTRAINT product_v_id_discriminator_ck '||
                'CHECK (id_discriminator IN (''feature'',''service'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_v ADD CONSTRAINT product_v_holder_discriminator_d_ck '||
                'CHECK (holder_discriminator IN (''feature'',''service'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.proof_of_employment_document ADD CONSTRAINT proof_of_employment_document_owner_discriminator_d_ck '
        ||'CHECK (owner_discriminator IN (''CustomerScreening'',''CustomerScreeningIncome'',''ExistingInsurance''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.restrictions_policy ADD CONSTRAINT restrictions_policy_node_discriminator_d_ck '||
                'CHECK (node_discriminator IN (''Disc Complex'',''Disc_Building'',''Disc_Country'',''Disc_Floorplan'',''Disc_Province'',''OrganizationPoliciesNode'',''Unit_BuildingElement'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.tax ADD CONSTRAINT tax_policy_node_discriminator_d_ck '||
                'CHECK (policy_node_discriminator IN (''Disc Complex'',''Disc_Building'',''Disc_Country'',''Disc_Floorplan'',''Disc_Province'',''OrganizationPoliciesNode'',''Unit_BuildingElement'')) ';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.tenant_charge ADD CONSTRAINT tenant_charge_tenant_discriminator_d_ck CHECK (tenant_discriminator = ''Tenant'') ';

        /**
        ***     =====================================================================================================================
        ***
        ***             Enum check constraints 
        ***  
        ***     =====================================================================================================================
        **/
        
        EXECUTE 'ALTER TABLE '||v_schema_name||'.aggregated_transfer ADD CONSTRAINT aggregated_transfer_status_e_ck '
        ||'CHECK (status IN (''Canceled'',''Hold'',''Paid'',''Rejected''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.aging_buckets ADD CONSTRAINT aging_buckets_debit_type_e_ck '
        ||'CHECK (debit_type IN (''accountCharge'',''addOn'',''booking'',''deposit'',''latePayment'',''lease'','
        ||'''locker'',''nsf'',''other'',''parking'',''pet'',''target'',''total'',''utility''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.application_wizard_step ADD CONSTRAINT application_wizard_step_status_e_ck '
        ||'CHECK (status IN (''complete'',''invalid'',''latest'',''notVisited''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.application_wizard_substep ADD CONSTRAINT application_wizard_substep_status_e_ck '
        ||'CHECK (status IN (''complete'',''invalid'',''latest'',''notVisited''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.appointment ADD CONSTRAINT appointment_status_e_ck CHECK (status IN (''closed'',''planned''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.apt_unit ADD CONSTRAINT apt_unit_info_area_units_e_ck CHECK (info_area_units IN (''sqFeet'',''sqMeters''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.apt_unit ADD CONSTRAINT apt_unit_info_economic_status_e_ck '
        ||'CHECK (info_economic_status IN (''commercial'',''offMarket'',''other'',''residential''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.apt_unit_item ADD CONSTRAINT apt_unit_item_cabinets_type_e_ck '
        ||'CHECK (cabinets_type IN (''laminate'',''melamine'',''other'',''wood'',''woodVeneer''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.apt_unit_item ADD CONSTRAINT apt_unit_item_counter_top_type_e_ck '
        ||'CHECK (counter_top_type IN (''granite'',''laminate'',''marble'',''metal'',''naturalStone'',''other'',''quartz'',''solidSurface'',''tile'',''wood''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.apt_unit_item ADD CONSTRAINT apt_unit_item_flooring_type_e_ck '
        ||'CHECK (flooring_type IN (''hardwood'',''laminate'',''other'',''parquet'',''tile''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.apt_unit_item ADD CONSTRAINT apt_unit_item_unit_detail_type_e_ck '
        ||'CHECK (unit_detail_type IN (''balcony'',''bathroom'',''bedroom'',''den'',''diningRoom'',''familyRoom'',''kitchen'','
        ||'''library'',''livingRoom'',''office'',''other'',''sunroom''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.apt_unit_occupancy_segment ADD CONSTRAINT apt_unit_occupancy_segment_off_market_e_ck '
        ||'CHECK (off_market IN (''construction'',''down'',''employee'',''model'',''office'',''other''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.apt_unit_occupancy_segment ADD CONSTRAINT apt_unit_occupancy_segment_status_e_ck '
        ||'CHECK (status IN (''available'',''migrated'',''occupied'',''offMarket'',''pending'',''renovation'',''reserved''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.arpolicy ADD CONSTRAINT arpolicy_credit_debit_rule_e_ck '
        ||'CHECK (credit_debit_rule IN (''byAgingBucketAndDebitType'',''byDebitType'',''byDueDate''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.available_locale ADD CONSTRAINT available_locale_lang_e_ck '
        ||'CHECK (lang IN (''en'',''en_CA'',''en_US'',''es'',''fr'',''fr_CA'',''ru'',''zh_CN'',''zh_TW''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.background_check_policy_v ADD CONSTRAINT background_check_policy_v_bankruptcy_e_ck '
        ||'CHECK (bankruptcy IN (''m12'',''m24'',''m36''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.background_check_policy_v ADD CONSTRAINT background_check_policy_v_charge_off_e_ck '
        ||'CHECK (charge_off IN (''m12'',''m24'',''m36''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.background_check_policy_v ADD CONSTRAINT background_check_policy_v_collection_e_ck '
        ||'CHECK (collection IN (''m12'',''m24'',''m36''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.background_check_policy_v ADD CONSTRAINT background_check_policy_v_judgment_e_ck '
        ||'CHECK (judgment IN (''m12'',''m24'',''m36''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billable_item_adjustment ADD CONSTRAINT billable_item_adjustment_adjustment_type_e_ck '
        ||'CHECK (adjustment_type IN (''monetary'',''percentage''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_account ADD CONSTRAINT billing_account_proration_method_e_ck '
        ||'CHECK (proration_method IN (''Actual'',''Annual'',''Standard''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_bill ADD CONSTRAINT billing_bill_bill_status_e_ck '
        ||'CHECK (bill_status IN (''Confirmed'',''Failed'',''Finished'',''Rejected'',''Running''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_bill ADD CONSTRAINT billing_bill_bill_type_e_ck '
        ||'CHECK (bill_type IN (''Final'',''First'',''Regular'',''ZeroCycle''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_billing_type ADD CONSTRAINT billing_billing_type_payment_frequency_e_ck '
        ||'CHECK (payment_frequency IN (''Annually'',''BiWeekly'',''Monthly'',''SemiAnnyally'',''SemiMonthly'',''Weekly''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_debit_type_e_ck '
        ||'CHECK (debit_type IN (''accountCharge'',''addOn'',''booking'',''deposit'',''latePayment'',''lease'','
        ||'''locker'',''nsf'',''other'',''parking'',''pet'',''target'',''total'',''utility''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_period_e_ck '
        ||'CHECK (period IN (''current'',''next'',''previous''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.billing_invoice_line_item ADD CONSTRAINT billing_invoice_line_item_product_type_e_ck '
        ||'CHECK (product_type IN (''oneTimeFeature'',''recurringFeature'',''service''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.boiler ADD CONSTRAINT boiler_warranty_warranty_type_e_ck '
        ||'CHECK (warranty_warranty_type IN (''conditional'',''full'',''labour'',''other'',''partial'',''parts'',''partsAndLabor''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.building ADD CONSTRAINT building_info_address_street_direction_e_ck '
        ||'CHECK (info_address_street_direction IN (''east'',''north'',''northEast'',''northWest'',''south'',''southEast'',''southWest'',''west''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.building ADD CONSTRAINT building_info_address_street_type_e_ck '
        ||'CHECK (info_address_street_type IN (''alley'',''approach'',''arcade'',''avenue'',''boulevard'',''brow'',''bypass'',''causeway'','
        ||'''circle'',''circuit'',''circus'',''close'',''copse'',''corner'',''court'',''cove'',''crescent'',''drive'',''end'','
        ||'''esplanande'',''flat'',''freeway'',''frontage'',''gardens'',''glade'',''glen'',''green'',''grove'',''heights'',''highway'','
        ||'''lane'',''line'',''link'',''loop'',''mall'',''mews'',''other'',''packet'',''parade'',''park'',''parkway'',''place'',''promenade'','
        ||'''reserve'',''ridge'',''rise'',''road'',''row'',''square'',''street'',''strip'',''tarn'',''terrace'',''thoroughfaree'',''track'',''trunkway'','
        ||'''view'',''vista'',''walk'',''walkway'',''way'',''yard''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.building ADD CONSTRAINT building_info_building_type_e_ck '
        ||'CHECK (info_building_type IN (''agricultural'',''commercial'',''industrial'',''military'',''mixed_residential'',''other'',''parking_storage'',''residential''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.building ADD CONSTRAINT building_info_construction_type_e_ck '
        ||'CHECK (info_construction_type IN (''block'',''brick'',''other'',''panel'',''wood''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.building ADD CONSTRAINT building_info_floor_type_e_ck '
        ||'CHECK (info_floor_type IN (''carpet'',''hardwood'',''laminate'',''mixed'',''other'',''tile''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.building ADD CONSTRAINT building_info_foundation_type_e_ck '
        ||'CHECK (info_foundation_type IN (''continuousFooting'',''foundationWalls'',''other'',''pile'',''spreadFooting''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.building ADD CONSTRAINT building_info_shape_e_ck '
        ||'CHECK (info_shape IN (''irregular'',''lShape'',''regular'',''tShape'',''uShape''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.building ADD CONSTRAINT building_info_structure_type_e_ck '
        ||'CHECK (info_structure_type IN (''condo'',''highRise'',''lowRise'',''midRise'',''other'',''townhouse'',''walkUp''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.building ADD CONSTRAINT building_info_water_supply_e_ck '
        ||'CHECK (info_water_supply IN (''municipal'',''other'',''privateCommunityWell'',''privateWell''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.building_amenity ADD CONSTRAINT building_amenity_building_amenity_type_e_ck '
        ||'CHECK (building_amenity_type IN (''availability24Hours'',''basketballCourt'',''businessCenter'',''childCare'',''clubDiscount'',''clubHouse'',''concierge'','
        ||'''coveredParking'',''doorAttendant'',''elevator'',''fitness'',''fitnessCentre'',''freeWeights'',''garage'',''gas'',''gate'','
        ||'''groupExercise'',''guestRoom'',''highSpeed'',''houseSitting'',''housekeeping'',''hydro'',''laundry'',''library'',''mealService'',''nightPatrol'','
        ||'''onSiteMaintenance'',''onSiteManagement'',''other'',''packageReceiving'',''parking'',''playGround'',''pool'',''racquetball'',''recreationalRoom'','
        ||'''sauna'',''shortTermLease'',''spa'',''storageSpace'',''sundeck'',''tennisCourt'',''transportation'',''tvLounge'',''vintage'',''volleyballCourt'',''water''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.campaign_history ADD CONSTRAINT campaign_history_trg_e_ck '
        ||'CHECK (trg IN (''ApplicationCompleted'',''Registration''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.charge_line ADD CONSTRAINT charge_line_tp_e_ck '
        ||'CHECK (tp IN (''deposit'',''firstMonthRent'',''monthlyRent'',''oneTimePayment'',''prorated''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.communication_media ADD CONSTRAINT communication_media_media_type_e_ck '
        ||'CHECK (media_type IN (''email'',''internal'',''phone'',''postal'',''sms''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.company ADD CONSTRAINT company_logo_media_type_e_ck '
        ||'CHECK (logo_media_type IN (''externalUrl'',''file'',''youTube''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.company ADD CONSTRAINT company_logo_visibility_e_ck '
        ||'CHECK (logo_visibility IN (''global'',''internal'',''tenant''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.concession_v ADD CONSTRAINT concession_v_concession_type_e_ck '
        ||'CHECK (concession_type IN (''free'',''monetaryOff'',''percentageOff'',''promotionalItem''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.concession_v ADD CONSTRAINT concession_v_cond_e_ck CHECK (cond IN (''compliance'',''none''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.concession_v ADD CONSTRAINT concession_v_term_e_ck CHECK (term IN (''firstMonth'',''lastMonth'',''term''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.contact ADD CONSTRAINT contact_media_type_e_ck CHECK (media_type IN (''email'',''internal'',''phone'',''postal'',''sms''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.contact_email ADD CONSTRAINT contact_email_media_type_e_ck '
        ||'CHECK (media_type IN (''email'',''internal'',''phone'',''postal'',''sms''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.contact_internal ADD CONSTRAINT contact_internal_media_type_e_ck '
        ||'CHECK (media_type IN (''email'',''internal'',''phone'',''postal'',''sms''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.contact_phone ADD CONSTRAINT contact_phone_media_type_e_ck '
        ||'CHECK (media_type IN (''email'',''internal'',''phone'',''postal'',''sms''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.contact_postal ADD CONSTRAINT contact_postal_address_street_direction_e_ck '
        ||'CHECK (address_street_direction IN (''east'',''north'',''northEast'',''northWest'',''south'',''southEast'',''southWest'',''west''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.contact_postal ADD CONSTRAINT contact_postal_address_street_type_e_ck '
        ||'CHECK (address_street_type IN (''alley'',''approach'',''arcade'',''avenue'',''boulevard'',''brow'',''bypass'',''causeway'',''circle'',''circuit'',''circus'','
        ||'''close'',''copse'',''corner'',''court'',''cove'',''crescent'',''drive'',''end'',''esplanande'',''flat'',''freeway'',''frontage'','
        ||'''gardens'',''glade'',''glen'',''green'',''grove'',''heights'',''highway'',''lane'',''line'',''link'',''loop'',''mall'',''mews'','
        ||'''other'',''packet'',''parade'',''park'',''parkway'',''place'',''promenade'',''reserve'',''ridge'',''rise'',''road'',''row'','
        ||'''square'',''street'',''strip'',''tarn'',''terrace'',''thoroughfaree'',''track'',''trunkway'',''view'',''vista'',''walk'',''walkway'',''way'',''yard''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.contact_postal ADD CONSTRAINT contact_postal_media_type_e_ck '
        ||'CHECK (media_type IN (''email'',''internal'',''phone'',''postal'',''sms''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer ADD CONSTRAINT customer_person_name_name_prefix_e_ck '
        ||'CHECK (person_name_name_prefix IN (''Dr'',''Miss'',''Mr'',''Mrs'',''Ms''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer ADD CONSTRAINT customer_person_sex_e_ck CHECK (person_sex IN (''Female'',''Male''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_credit_check ADD CONSTRAINT customer_credit_check_credit_check_result_e_ck '
        ||'CHECK (credit_check_result IN (''Accept'',''Decline'',''Error'',''Review'',''ReviewNoInformationAvalable''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_income ADD CONSTRAINT customer_screening_income_income_source_e_ck '
        ||'CHECK (income_source IN(''disabilitySupport'',''dividends'',''fulltime'',''other'',''parttime'',''pension'',''retired'',''seasonallyEmployed'',''selfemployed'','
        ||'''socialServices'',''student'',''unemployed''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_income_info ADD CONSTRAINT customer_screening_income_info_address_street_direction_e_ck '
        ||'CHECK (address_street_direction IN (''east'',''north'',''northEast'',''northWest'',''south'',''southEast'',''southWest'',''west''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_income_info ADD CONSTRAINT customer_screening_income_info_address_street_type_e_ck '
        ||'CHECK (address_street_type IN (''alley'',''approach'',''arcade'',''avenue'',''boulevard'',''brow'',''bypass'',''causeway'',''circle'',''circuit'',''circus'',''close'',''copse'','
        ||'''corner'',''court'',''cove'',''crescent'',''drive'',''end'',''esplanande'',''flat'',''freeway'',''frontage'','
        ||'''gardens'',''glade'',''glen'',''green'',''grove'',''heights'',''highway'',''lane'',''line'',''link'',''loop'',''mall'',''mews'','
        ||'''other'',''packet'',''parade'',''park'',''parkway'',''place'',''promenade'',''reserve'',''ridge'',''rise'',''road'',''row'','
        ||'''square'',''street'',''strip'',''tarn'',''terrace'',''thoroughfaree'',''track'',''trunkway'',''view'',''vista'',''walk'',''walkway'',''way'',''yard''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_income_info ADD CONSTRAINT customer_screening_income_info_funding_choices_e_ck '
        ||'CHECK (funding_choices IN (''bursary'',''grant'',''loan'',''scolarship''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_income_info ADD CONSTRAINT customer_screening_income_info_program_e_ck '
        ||'CHECK (program IN (''graduate'',''undergraduate''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_personal_asset ADD CONSTRAINT customer_screening_personal_asset_asset_type_e_ck '
        ||'CHECK (asset_type IN (''bankAccounts'',''businesses'',''cars'',''insurancePolicies'',''other'',''realEstateProperties'',''shares'',''unitTrusts''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_v ADD CONSTRAINT customer_screening_v_current_address_rented_e_ck '
        ||'CHECK (current_address_rented IN (''owned'',''rented''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_v ADD CONSTRAINT customer_screening_v_current_address_street_direction_e_ck '
        ||'CHECK (current_address_street_direction IN (''east'',''north'',''northEast'',''northWest'',''south'',''southEast'',''southWest'',''west''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_v ADD CONSTRAINT customer_screening_v_current_address_street_type_e_ck '
        ||'CHECK (current_address_street_type IN (''alley'',''approach'',''arcade'',''avenue'',''boulevard'',''brow'',''bypass'',''causeway'',''circle'',''circuit'',''circus'','
        ||'''close'',''copse'',''corner'',''court'',''cove'',''crescent'',''drive'',''end'',''esplanande'',''flat'',''freeway'',''frontage'','
        ||'''gardens'',''glade'',''glen'',''green'',''grove'',''heights'',''highway'',''lane'',''line'',''link'',''loop'',''mall'',''mews'','
        ||'''other'',''packet'',''parade'',''park'',''parkway'',''place'',''promenade'',''reserve'',''ridge'',''rise'',''road'',''row'','
        ||'''square'',''street'',''strip'',''tarn'',''terrace'',''thoroughfaree'',''track'',''trunkway'',''view'',''vista'',''walk'',''walkway'',''way'',''yard''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_v ADD CONSTRAINT customer_screening_v_previous_address_rented_e_ck '
        ||'CHECK (previous_address_rented IN (''owned'',''rented''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_v ADD CONSTRAINT customer_screening_v_previous_address_street_direction_e_ck '
        ||'CHECK (previous_address_street_direction IN (''east'',''north'',''northEast'',''northWest'',''south'',''southEast'',''southWest'',''west''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.customer_screening_v ADD CONSTRAINT customer_screening_v_previous_address_street_type_e_ck '
        ||'CHECK (previous_address_street_type IN (''alley'',''approach'',''arcade'',''avenue'',''boulevard'',''brow'',''bypass'',''causeway'',''circle'','
        ||'''circuit'',''circus'',''close'',''copse'',''corner'',''court'',''cove'',''crescent'',''drive'',''end'',''esplanande'','
        ||'''flat'',''freeway'',''frontage'',''gardens'',''glade'',''glen'',''green'',''grove'',''heights'',''highway'',''lane'',''line'',''link'',''loop'','
        ||'''mall'',''mews'',''other'',''packet'',''parade'',''park'',''parkway'',''place'',''promenade'',''reserve'',''ridge'',''rise'',''road'',''row'','
        ||'''square'',''street'',''strip'',''tarn'',''terrace'',''thoroughfaree'',''track'',''trunkway'',''view'',''vista'',''walk'',''walkway'',''way'',''yard''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.dashboard_metadata ADD CONSTRAINT dashboard_metadata_dashboard_type_e_ck '
        ||'CHECK (dashboard_type IN (''building'',''system''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.deposit ADD CONSTRAINT deposit_deposit_type_e_ck '
        ||'CHECK (deposit_type IN (''LastMonthDeposit'',''MoveInDeposit'',''SecurityDeposit''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.deposit_lifecycle ADD CONSTRAINT deposit_lifecycle_status_e_ck '
        ||'CHECK (status IN (''Created'',''Paid'',''Processed'',''Refunded''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.deposit_policy_item ADD CONSTRAINT deposit_policy_item_deposit_type_e_ck '
        ||'CHECK (deposit_type IN (''LastMonthDeposit'',''MoveInDeposit'',''SecurityDeposit''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.deposit_policy_item ADD CONSTRAINT deposit_policy_item_value_type_e_ck '
        ||'CHECK (value_type IN (''Monetary'',''Percentage''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.elevator ADD CONSTRAINT elevator_warranty_warranty_type_e_ck '
        ||'CHECK (warranty_warranty_type IN (''conditional'',''full'',''labour'',''other'',''partial'',''parts'',''partsAndLabor''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.email_template ADD CONSTRAINT email_template_template_type_e_ck '
        ||'CHECK (template_type IN (''ApplicationApproved'',''ApplicationCreatedApplicant'',''ApplicationCreatedCoApplicant'',''ApplicationCreatedGuarantor'','
        ||'''ApplicationDeclined'',''PasswordRetrievalCrm'',''PasswordRetrievalProspect'',''PasswordRetrievalTenant'',''TenantInvitation''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.emergency_contact ADD CONSTRAINT emergency_contact_address_street_direction_e_ck '
        ||'CHECK (address_street_direction IN (''east'',''north'',''northEast'',''northWest'',''south'',''southEast'',''southWest'',''west''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.emergency_contact ADD CONSTRAINT emergency_contact_address_street_type_e_ck '
        ||'CHECK (address_street_type IN (''alley'',''approach'',''arcade'',''avenue'',''boulevard'',''brow'',''bypass'',''causeway'',''circle'',''circuit'',''circus'','
        ||'''close'',''copse'',''corner'',''court'',''cove'',''crescent'',''drive'',''end'',''esplanande'',''flat'',''freeway'',''frontage'','
        ||'''gardens'',''glade'',''glen'',''green'',''grove'',''heights'',''highway'',''lane'',''line'',''link'',''loop'','
        ||'''mall'',''mews'',''other'',''packet'',''parade'',''park'',''parkway'',''place'',''promenade'',''reserve'',''ridge'',''rise'',''road'',''row'','
        ||'''square'',''street'',''strip'',''tarn'',''terrace'',''thoroughfaree'',''track'',''trunkway'',''view'',''vista'',''walk'',''walkway'',''way'',''yard''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.emergency_contact ADD CONSTRAINT emergency_contact_name_name_prefix_e_ck '
        ||'CHECK (name_name_prefix IN (''Dr'',''Miss'',''Mr'',''Mrs'',''Ms''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.emergency_contact ADD CONSTRAINT emergency_contact_sex_e_ck CHECK (sex IN (''Female'',''Male''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.employee ADD CONSTRAINT employee_name_name_prefix_e_ck '
        ||'CHECK (name_name_prefix IN (''Dr'',''Miss'',''Mr'',''Mrs'',''Ms''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.employee ADD CONSTRAINT employee_sex_e_ck CHECK (sex IN (''Female'',''Male''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.file_image_thumbnail_blob ADD CONSTRAINT file_image_thumbnail_blob_thumbnail_size_e_ck '
        ||'CHECK (thumbnail_size IN (''large'',''medium'',''small'',''xlarge'',''xsmall''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.floorplan ADD CONSTRAINT floorplan_area_units_e_ck CHECK (area_units IN (''sqFeet'',''sqMeters''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.floorplan_amenity ADD CONSTRAINT floorplan_amenity_floorplan_type_e_ck '
        ||'CHECK (floorplan_type IN (''additionalStorage'',''airConditioner'',''alarm'',''balcony'',''cable'',''carport'',''ceilingFan'',''controlledAccess'',''courtyard'','
        ||'''dishwasher'',''disposal'',''dryer'',''fireplace'',''furnished'',''garage'',''handrails'',''heat'',''individualClimateControl'','
        ||'''largeClosets'',''microwave'',''other'',''patio'',''privateBalcony'',''privatePatio'',''range'',''refrigerator'','
        ||'''satellite'',''skylight'',''view'',''washer'',''wdHookup'',''wheelChair'',''windowCoverings''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.home_page_gadget ADD CONSTRAINT home_page_gadget_area_e_ck CHECK (area IN (''narrow'',''wide''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.home_page_gadget ADD CONSTRAINT home_page_gadget_status_e_ck CHECK (status IN (''disabled'',''editing'',''published''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.iagree ADD CONSTRAINT iagree_person_name_name_prefix_e_ck '
        ||'CHECK (person_name_name_prefix IN (''Dr'',''Miss'',''Mr'',''Mrs'',''Ms''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.iagree ADD CONSTRAINT iagree_person_sex_e_ck CHECK (person_sex IN (''Female'',''Male''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.id_assignment_item ADD CONSTRAINT id_assignment_item_target_e_ck '
        ||'CHECK (target IN (''accountNumber'',''application'',''customer'',''employee'',''guarantor'',''lead'',''lease'',''propertyCode'',''tenant''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.id_assignment_item ADD CONSTRAINT id_assignment_item_tp_e_ck '
        ||'CHECK (tp IN (''generatedAlphaNumeric'',''generatedNumber'',''userAssigned'',''userEditable''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.id_assignment_sequence ADD CONSTRAINT id_assignment_sequence_target_e_ck '
        ||'CHECK (target IN (''accountNumber'',''application'',''customer'',''employee'',''guarantor'',''lead'',''lease'',''propertyCode'',''tenant''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.identification_document_type ADD CONSTRAINT identification_document_type_id_type_e_ck '
        ||'CHECK (id_type IN (''canadianSIN'',''citizenship'',''immigration'',''license'',''other'',''passport''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.issue_classification ADD CONSTRAINT issue_classification_priority_e_ck '
        ||'CHECK (priority IN (''EMERGENCY'',''STANDARD''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.issue_element ADD CONSTRAINT issue_element_tp_e_ck '
        ||'CHECK (tp IN (''Amenities'',''ApartmentUnit'',''Exterior''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.late_fee_item ADD CONSTRAINT late_fee_item_base_fee_type_e_ck '
        ||'CHECK (base_fee_type IN (''FlatAmount'',''PercentMonthlyRent'',''PercentOwedTotal''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.late_fee_item ADD CONSTRAINT late_fee_item_max_total_fee_type_e_ck '
        ||'CHECK (max_total_fee_type IN (''FlatAmount'',''PercentMonthlyRent'',''Unlimited''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lead ADD CONSTRAINT lead_appointment_time1_e_ck '
        ||'CHECK (appointment_time1 IN (''Afternoon'',''Evening'',''Morning''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lead ADD CONSTRAINT lead_appointment_time2_e_ck '
        ||'CHECK (appointment_time2 IN (''Afternoon'',''Evening'',''Morning''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lead ADD CONSTRAINT lead_lease_term_e_ck CHECK (lease_term IN (''months12'',''months18'',''months6'',''other''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lead ADD CONSTRAINT lead_lease_type_e_ck CHECK (lease_type IN (''commercialUnit'',''residentialUnit''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lead ADD CONSTRAINT lead_ref_source_e_ck '
        ||'CHECK (ref_source IN (''DirectMail'',''Import'',''Internet'',''LocatorServices'',''Newspaper'',''Other'',''Radio'',''Referral'',''TV''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lead ADD CONSTRAINT lead_status_e_ck CHECK (status IN (''active'',''closed'',''rented''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lead_guest ADD CONSTRAINT lead_guest_person_name_name_prefix_e_ck '
        ||'CHECK (person_name_name_prefix IN (''Dr'',''Miss'',''Mr'',''Mrs'',''Ms''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lead_guest ADD CONSTRAINT lead_guest_person_sex_e_ck CHECK (person_sex IN (''Female'',''Male''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease ADD CONSTRAINT lease_completion_e_ck CHECK (completion IN (''Eviction'',''Notice'',''Skip'',''Termination''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease ADD CONSTRAINT lease_lease_type_e_ck CHECK (lease_type IN (''commercialUnit'',''residentialUnit''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease ADD CONSTRAINT lease_payment_frequency_e_ck '
        ||'CHECK (payment_frequency IN (''Annually'',''BiWeekly'',''Monthly'',''SemiAnnyally'',''SemiMonthly'',''Weekly''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease ADD CONSTRAINT lease_status_e_ck '
        ||'CHECK (status IN (''Active'',''Application'',''Approved'',''Cancelled'',''Closed'',''Completed'',''ExistingLease''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_adjustment ADD CONSTRAINT lease_adjustment_execution_type_e_ck '
        ||'CHECK (execution_type IN (''immediate'',''pending''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_adjustment ADD CONSTRAINT lease_adjustment_status_e_ck CHECK (status IN (''draft'',''submited''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_adjustment ADD CONSTRAINT lease_adjustment_tax_type_e_ck CHECK (tax_type IN (''percent'',''value''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_adjustment_reason ADD CONSTRAINT lease_adjustment_reason_action_type_e_ck '
        ||'CHECK (action_type IN (''charge'',''credit''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_application ADD CONSTRAINT lease_application_status_e_ck '
        ||'CHECK (status IN (''Approved'',''Cancelled'',''Created'',''Declined'',''OnlineApplication'',''PendingDecision''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_billing_policy ADD CONSTRAINT lease_billing_policy_confirmation_method_e_ck '
        ||'CHECK (confirmation_method IN (''automatic'',''manual''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_billing_policy ADD CONSTRAINT lease_billing_policy_proration_method_e_ck '
        ||'CHECK (proration_method IN (''Actual'',''Annual'',''Standard''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term ADD CONSTRAINT lease_term_lease_term_status_e_ck '
        ||'CHECK (lease_term_status IN (''AcceptedOffer'',''Current'',''Historic'',''Offer''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term ADD CONSTRAINT lease_term_lease_term_type_e_ck '
        ||'CHECK (lease_term_type IN (''Fixed'',''FixedEx'',''Periodic''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term_participant ADD CONSTRAINT lease_term_participant_participant_role_e_ck '
        ||'CHECK (participant_role IN (''Applicant'',''CoApplicant'',''Dependent'',''Guarantor''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.lease_term_participant ADD CONSTRAINT lease_term_participant_relationship_e_ck '
        ||'CHECK (relationship IN (''Aunt'',''Daughter'',''Father'',''Friend'',''Grandfather'',''Grandmother'',''Mother'',''Other'',''Son'',''Spouse'',''Uncle''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.locker ADD CONSTRAINT locker_spot_type_e_ck CHECK (spot_type IN (''large'',''regular'',''small''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.maintenance_request ADD CONSTRAINT maintenance_request_status_e_ck '
        ||'CHECK (status IN (''Cancelled'',''Resolved'',''Scheduled'',''Submitted''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.marketing ADD CONSTRAINT marketing_visibility_e_ck CHECK (visibility IN (''global'',''internal'',''tenant''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.master_online_application ADD CONSTRAINT master_online_application_status_e_ck '
        ||'CHECK (status IN (''Cancelled'',''Incomplete'',''InformationRequested'',''Submitted''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.media ADD CONSTRAINT media_media_type_e_ck CHECK (media_type IN (''externalUrl'',''file'',''youTube''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.media ADD CONSTRAINT media_visibility_e_ck CHECK (visibility IN (''global'',''internal'',''tenant''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.message ADD CONSTRAINT message_message_type_e_ck '
        ||'CHECK (message_type IN (''communication'',''maintananceAlert'',''paymentMethodExpired'',''paymnetPastDue''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.name ADD CONSTRAINT name_name_prefix_e_ck CHECK (name_prefix IN (''Dr'',''Miss'',''Mr'',''Mrs'',''Ms''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.nsf_fee_item ADD CONSTRAINT nsf_fee_item_payment_type_e_ck '
        ||'CHECK (payment_type IN (''Cash'',''Check'',''CreditCard'',''EFT'',''Echeck'',''Interac''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.online_application ADD CONSTRAINT online_application_role_e_ck '
        ||'CHECK (role IN (''Applicant'',''CoApplicant'',''Guarantor''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.online_application ADD CONSTRAINT online_application_status_e_ck '
        ||'CHECK (status IN (''Incomplete'',''InformationRequested'',''Invited'',''Submitted''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.owner ADD CONSTRAINT owner_person_name_name_prefix_e_ck '
        ||'CHECK (person_name_name_prefix IN (''Dr'',''Miss'',''Mr'',''Mrs'',''Ms''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.owner ADD CONSTRAINT owner_person_sex_e_ck CHECK (person_sex IN (''Female'',''Male''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.page_descriptor ADD CONSTRAINT page_descriptor_page_type_e_ck '
        ||'CHECK (page_type IN (''findApartment'',''potentialTenants'',''residents'',''staticContent''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.parking ADD CONSTRAINT parking_parking_type_e_ck '
        ||'CHECK (parking_type IN (''coveredLot'',''garageLot'',''none'',''other'',''street'',''surfaceLot''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.parking_spot ADD CONSTRAINT parking_spot_spot_type_e_ck '
        ||'CHECK (spot_type IN (''disabled'',''narrow'',''regular'',''wide''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_information ADD CONSTRAINT payment_information_payment_method_billing_addr_str_dir_e_ck '
        ||'CHECK (payment_method_billing_address_street_direction IN (''east'',''north'',''northEast'',''northWest'',''south'',''southEast'',''southWest'',''west''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_information ADD CONSTRAINT payment_information_payment_method_billing_addr_str_type_e_ck '
        ||'CHECK (payment_method_billing_address_street_type IN(''alley'',''approach'',''arcade'',''avenue'',''boulevard'',''brow'',''bypass'',''causeway'',''circle'','
        ||'''circuit'',''circus'',''close'',''copse'',''corner'',''court'',''cove'',''crescent'',''drive'',''end'',''esplanande'','
        ||'''flat'',''freeway'',''frontage'',''gardens'',''glade'',''glen'',''green'',''grove'',''heights'',''highway'',''lane'',''line'',''link'',''loop'','
        ||'''mall'',''mews'',''other'',''packet'',''parade'',''park'',''parkway'',''place'',''promenade'',''reserve'',''ridge'',''rise'',''road'',''row'','
        ||'''square'',''street'',''strip'',''tarn'',''terrace'',''thoroughfaree'',''track'',''trunkway'',''view'',''vista'',''walk'',''walkway'',''way'',''yard''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_information ADD CONSTRAINT payment_information_payment_method_payment_type_e_ck '
        ||'CHECK (payment_method_payment_type IN (''Cash'',''Check'',''CreditCard'',''EFT'',''Echeck'',''Interac''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_method ADD CONSTRAINT payment_method_billing_address_street_direction_e_ck '
        ||'CHECK (billing_address_street_direction IN (''east'',''north'',''northEast'',''northWest'',''south'',''southEast'',''southWest'',''west''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_method ADD CONSTRAINT payment_method_billing_address_street_type_e_ck '
        ||'CHECK (billing_address_street_type IN (''alley'',''approach'',''arcade'',''avenue'',''boulevard'',''brow'',''bypass'',''causeway'',''circle'',''circuit'',''circus'','
        ||'''close'',''copse'',''corner'',''court'',''cove'',''crescent'',''drive'',''end'',''esplanande'',''flat'',''freeway'',''frontage'','
        ||'''gardens'',''glade'',''glen'',''green'',''grove'',''heights'',''highway'',''lane'',''line'',''link'',''loop'','
        ||'''mall'',''mews'',''other'',''packet'',''parade'',''park'',''parkway'',''place'',''promenade'',''reserve'',''ridge'',''rise'',''road'',''row'','
        ||'''square'',''street'',''strip'',''tarn'',''terrace'',''thoroughfaree'',''track'',''trunkway'',''view'',''vista'',''walk'',''walkway'',''way'',''yard''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_method ADD CONSTRAINT payment_method_payment_type_e_ck '
        ||'CHECK (payment_type IN (''Cash'',''Check'',''CreditCard'',''EFT'',''Echeck'',''Interac''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_payment_details ADD CONSTRAINT payment_payment_details_account_type_e_ck '
        ||'CHECK (account_type IN (''Chequing'',''Saving''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_payment_details ADD CONSTRAINT payment_payment_details_card_type_e_ck '
        ||'CHECK (card_type IN (''MasterCard'',''Visa''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payment_record ADD CONSTRAINT payment_record_payment_status_e_ck '
        ||'CHECK (payment_status IN (''Canceled'',''Cleared'',''Processing'',''Queued'',''Received'',''Rejected'',''Returned'',''Scheduled'',''Submitted''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.payments_summary ADD CONSTRAINT payments_summary_status_e_ck '
        ||'CHECK (status IN (''Canceled'',''Cleared'',''Processing'',''Queued'',''Received'',''Rejected'',''Returned'',''Scheduled'',''Submitted''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.pet ADD CONSTRAINT pet_weight_unit_e_ck CHECK (weight_unit IN (''kg'',''lb''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.phone_call_campaign ADD CONSTRAINT phone_call_campaign_trg_e_ck CHECK (trg IN (''ApplicationCompleted'',''Registration''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.pricing ADD CONSTRAINT pricing_payment_term_e_ck CHECK (payment_term IN (''monthly'',''oneTime''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product ADD CONSTRAINT product_feature_type_e_ck '
        ||'CHECK (feature_type IN (''addOn'',''booking'',''locker'',''parking'',''pet'',''utility''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product ADD CONSTRAINT product_service_type_e_ck CHECK (service_type IN (''commercialUnit'',''residentialUnit''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_item_type ADD CONSTRAINT product_item_type_feature_type_e_ck '
        ||'CHECK (feature_type IN (''addOn'',''booking'',''locker'',''parking'',''pet'',''utility''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.product_item_type ADD CONSTRAINT product_item_type_service_type_e_ck '
        ||'CHECK (service_type IN (''commercialUnit'',''residentialUnit''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.property_contact ADD CONSTRAINT property_contact_phone_type_e_ck '
        ||'CHECK (phone_type IN (''administrator'',''elevator'',''fireMonitoring'',''intercom'',''laundry'',''mainOffice'',''pointOfSale'',''pool'','
        ||'''poolEmergency'',''superintendent''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.property_contact ADD CONSTRAINT property_contact_visibility_e_ck '
        ||'CHECK (visibility IN (''global'',''internal'',''tenant''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.property_phone ADD CONSTRAINT property_phone_designation_e_ck '
        ||'CHECK (designation IN (''elevator'',''fireMonitoring'',''intercom'',''laundry'',''office'',''pointOfSale'',''pool''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.property_phone ADD CONSTRAINT property_phone_phone_type_e_ck '
        ||'CHECK (phone_type IN (''fax'',''landLine'',''mobile'',''other'',''pager''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.property_phone ADD CONSTRAINT property_phone_visibility_e_ck CHECK (visibility IN (''global'',''internal'',''tenant''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.recipient ADD CONSTRAINT recipient_recipient_type_e_ck CHECK (recipient_type IN (''business'',''person''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.roof ADD CONSTRAINT roof_warranty_warranty_type_e_ck '
        ||'CHECK (warranty_warranty_type IN (''conditional'',''full'',''labour'',''other'',''partial'',''parts'',''partsAndLabor''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.showing ADD CONSTRAINT showing_reason_e_ck CHECK (reason IN (''other'',''tooDark'',''tooExpensive'',''tooSmall''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.showing ADD CONSTRAINT showing_result_e_ck CHECK (result IN (''interested'',''notInterested''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.showing ADD CONSTRAINT showing_status_e_ck CHECK (status IN (''planned'',''seen''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.site_descriptor ADD CONSTRAINT site_descriptor_skin_e_ck CHECK (skin IN (''crm'',''skin1'',''skin2'',''skin3'',''skin4''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.social_link ADD CONSTRAINT social_link_social_site_e_ck CHECK (social_site IN (''Facebook'',''Flickr'',''Twitter'',''Youtube''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.unit_availability_status ADD CONSTRAINT unit_availability_status_rent_readiness_status_e_ck '
        ||'CHECK (rent_readiness_status IN (''NeedsRepairs'',''RenoInProgress'',''RentReady''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.unit_availability_status ADD CONSTRAINT unit_availability_status_rented_status_e_ck '
        ||'CHECK (rented_status IN (''OffMarket'',''Rented'',''Unrented''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.unit_availability_status ADD CONSTRAINT unit_availability_status_scoping_e_ck '
        ||'CHECK (scoping IN (''Scoped'',''Unscoped''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.unit_availability_status ADD CONSTRAINT unit_availability_status_vacancy_status_e_ck '
        ||'CHECK (vacancy_status IN (''Notice'',''Vacant''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.vendor ADD CONSTRAINT vendor_logo_media_type_e_ck CHECK (logo_media_type IN (''externalUrl'',''file'',''youTube''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.vendor ADD CONSTRAINT vendor_logo_visibility_e_ck CHECK (logo_visibility IN (''global'',''internal'',''tenant''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.vendor ADD CONSTRAINT vendor_vendor_type_e_ck '
        ||'CHECK (vendor_type IN (''electrical'',''hvac'',''other'',''plumbing'',''regularMaintenance''))';
        EXECUTE 'ALTER TABLE '||v_schema_name||'.warranty ADD CONSTRAINT warranty_warranty_type_e_ck '
        ||'CHECK (warranty_type IN (''conditional'',''full'',''labour'',''other'',''partial'',''parts'',''partsAndLabor''))';
         

        -- Final touch - update admin_pmc table

        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.0.5',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;

        EXCEPTION WHEN OTHERS THEN
                RAISE EXCEPTION 'Failed executing statement in schema %, code: %, error message: %',v_schema_name,SQLSTATE,SQLERRM ;

END;
$$
LANGUAGE plpgsql VOLATILE;

/**
***     Split schema update into several transactions
***     to avoid increasing max_locks_per_transaction
**/

BEGIN TRANSACTION;
        SELECT  namespace, _dba_.migrate_to_105(namespace) AS result
        FROM    _admin_.admin_pmc
        WHERE   status != 'Created'
        AND     schema_version IS NULL
        AND     namespace ~ '^[abc]';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace, _dba_.migrate_to_105(namespace) AS result
        FROM    _admin_.admin_pmc
        WHERE   status != 'Created'
        AND     schema_version IS NULL
        AND     namespace ~ '^[def]';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace, _dba_.migrate_to_105(namespace) AS result
        FROM    _admin_.admin_pmc
        WHERE   status != 'Created'
        AND     schema_version IS NULL
        AND     namespace ~ '^[ghi]';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace, _dba_.migrate_to_105(namespace) AS result
        FROM    _admin_.admin_pmc
        WHERE   status != 'Created'
        AND     schema_version IS NULL
        AND     namespace ~ '^[jkl]';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace, _dba_.migrate_to_105(namespace) AS result
        FROM    _admin_.admin_pmc
        WHERE   status != 'Created'
        AND     schema_version IS NULL
        AND     namespace ~ '^[mno]';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace, _dba_.migrate_to_105(namespace) AS result
        FROM    _admin_.admin_pmc
        WHERE   status != 'Created'
        AND     schema_version IS NULL
        AND     namespace ~ '^[pqr]';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace, _dba_.migrate_to_105(namespace) AS result
        FROM    _admin_.admin_pmc
        WHERE   status != 'Created'
        AND     schema_version IS NULL
        AND     namespace ~ '^[stu]';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace, _dba_.migrate_to_105(namespace) AS result
        FROM    _admin_.admin_pmc
        WHERE   status != 'Created'
        AND     schema_version IS NULL
        AND     namespace ~ '^[vwx]';
COMMIT;

BEGIN TRANSACTION;
        SELECT  namespace, _dba_.migrate_to_105(namespace) AS result
        FROM    _admin_.admin_pmc
        WHERE   status != 'Created'
        AND     schema_version IS NULL
        AND     namespace ~ '^[yz]';
COMMIT;

DROP FUNCTION _dba_.migrate_to_105(VARCHAR(64));

SET client_min_messages = 'NOTICE';



