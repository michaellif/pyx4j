/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             version 1.4.0 PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_140(v_schema_name TEXT) RETURNS VOID AS
$$
DECLARE
        v_rowcount      INT     := 0;
BEGIN
        EXECUTE 'SET search_path = '||v_schema_name;
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP CONSTRAINTS SECTION
        ***
        ***     ======================================================================================================
        **/
        
        
        -- foreign keys
        
        ALTER TABLE apt_unit DROP CONSTRAINT apt_unit_info_legal_address_country_fk;
        ALTER TABLE apt_unit DROP CONSTRAINT apt_unit_info_legal_address_province_fk;
        ALTER TABLE building DROP CONSTRAINT building_info_address_country_fk;
        ALTER TABLE building DROP CONSTRAINT building_info_address_province_fk;
        ALTER TABLE building DROP CONSTRAINT building_property_manager_fk;
        ALTER TABLE city_intro_page DROP CONSTRAINT city_intro_page_province_fk;
        ALTER TABLE city DROP CONSTRAINT city_province_fk;
        ALTER TABLE communication_message$to DROP CONSTRAINT communication_message$to_owner_fk;
        ALTER TABLE communication_message_attachment DROP CONSTRAINT communication_message_attachment_message_fk;
        ALTER TABLE crm_role$rls DROP CONSTRAINT crm_role$rls_owner_fk;
        ALTER TABLE crm_role$rls DROP CONSTRAINT crm_role$rls_value_fk;
        ALTER TABLE customer_screening_income_info DROP CONSTRAINT customer_screening_income_info_address_country_fk;
        ALTER TABLE customer_screening_income_info DROP CONSTRAINT customer_screening_income_info_address_province_fk;
        ALTER TABLE customer_screening_v DROP CONSTRAINT customer_screening_v_current_address_country_fk;
        ALTER TABLE customer_screening_v DROP CONSTRAINT customer_screening_v_current_address_province_fk;
        ALTER TABLE customer_screening_v DROP CONSTRAINT customer_screening_v_previous_address_country_fk;
        ALTER TABLE customer_screening_v DROP CONSTRAINT customer_screening_v_previous_address_province_fk;
        ALTER TABLE emergency_contact DROP CONSTRAINT emergency_contact_address_country_fk;
        ALTER TABLE emergency_contact DROP CONSTRAINT emergency_contact_address_province_fk;
        ALTER TABLE landlord DROP CONSTRAINT landlord_address_country_fk;
        ALTER TABLE landlord DROP CONSTRAINT landlord_address_province_fk;
        ALTER TABLE lease_application DROP CONSTRAINT lease_application_decided_by_fk;
        ALTER TABLE marketing DROP CONSTRAINT marketing_marketing_address_country_fk;
        ALTER TABLE marketing DROP CONSTRAINT marketing_marketing_address_province_fk;
        ALTER TABLE master_online_application DROP CONSTRAINT master_online_application_building_fk;
        ALTER TABLE master_online_application DROP CONSTRAINT master_online_application_floorplan_fk;
        ALTER TABLE n4_policy DROP CONSTRAINT n4_policy_mailing_address_country_fk;
        ALTER TABLE n4_policy DROP CONSTRAINT n4_policy_mailing_address_province_fk;
        ALTER TABLE payment_method DROP CONSTRAINT payment_method_billing_address_country_fk;
        ALTER TABLE payment_method DROP CONSTRAINT payment_method_billing_address_province_fk;
        ALTER TABLE province DROP CONSTRAINT province_country_fk;
        ALTER TABLE pt_vehicle DROP CONSTRAINT pt_vehicle_country_fk;
        ALTER TABLE pt_vehicle DROP CONSTRAINT pt_vehicle_province_fk;
        
        
        -- primary keys 
        
        ALTER TABLE province DROP CONSTRAINT province_pk;
        
        -- check constraints
        
        ALTER TABLE aggregated_transfer DROP CONSTRAINT aggregated_transfer_funds_transfer_type_e_ck;
        ALTER TABLE billable_item_adjustment DROP CONSTRAINT billable_item_adjustment_adjustment_type_e_ck;
        ALTER TABLE communication_message DROP CONSTRAINT communication_message_sender_discriminator_d_ck;
        ALTER TABLE communication_thread DROP CONSTRAINT communication_thread_responsible_discriminator_d_ck;
        ALTER TABLE email_template DROP CONSTRAINT email_template_template_type_e_ck;
        ALTER TABLE lease_adjustment DROP CONSTRAINT lease_adjustment_tax_type_e_ck;
        ALTER TABLE lease_application DROP CONSTRAINT lease_application_status_e_ck;
        ALTER TABLE legal_letter DROP CONSTRAINT legal_letter_status_discriminator_d_ck;
        ALTER TABLE legal_status DROP CONSTRAINT legal_status_id_discriminator_ck;
        ALTER TABLE marketing DROP CONSTRAINT marketing_marketing_address_street_direction_e_ck;
        ALTER TABLE marketing DROP CONSTRAINT marketing_marketing_address_street_type_e_ck;
        ALTER TABLE n4_policy DROP CONSTRAINT n4_policy_mailing_address_street_direction_e_ck;
        ALTER TABLE n4_policy DROP CONSTRAINT n4_policy_mailing_address_street_type_e_ck;
        ALTER TABLE notes_and_attachments DROP CONSTRAINT notes_and_attachments_owner_discriminator_d_ck;
        ALTER TABLE notification DROP CONSTRAINT notification_tp_e_ck;
        ALTER TABLE system_endpoint DROP CONSTRAINT system_endpoint_type_e_ck;

        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP INDEXES
        ***
        ***     ======================================================================================================
        **/
        
        DROP INDEX province_code_idx;
        DROP INDEX province_name_idx;
        DROP INDEX system_endpoint_name_idx;
      
        
        /**
        ***    ======================================================================================================
        ***
        ***             Very special case for billing_arrears_snapshot_from_date_to_date_idx
        ***             This index doesn''t exist in new schemas, and may be bloated for schemas
        ***             where it does exists due to removal of extra rows from billing_arrears_snapshot table 
        ***             So I''ll just drop and recreate it
        ***
        ***     ===================================================================================================== 
        **/
        
        DROP INDEX IF EXISTS billing_arrears_snapshot_from_date_to_date_idx;
        
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
        
        -- aggregated_transfer
        
        ALTER TABLE aggregated_transfer ADD COLUMN id_discriminator VARCHAR(64),
                                        ADD COLUMN cards_reconciliation_record_key BIGINT,
                                        ADD COLUMN mastercard_deposit NUMERIC(18,2),
                                        ADD COLUMN mastercard_fee NUMERIC(18,2),
                                        ADD COLUMN visa_deposit NUMERIC(18,2),
                                        ADD COLUMN visa_fee NUMERIC(18,2);
                                        
        -- aggregated_transfer_adjustment
        
        CREATE TABLE aggregated_transfer_adjustment
        (
            id                          BIGINT                  NOT NULL,
            adjustment                  NUMERIC(18,2),
            ag_id                       BIGINT,                 -- very temporary column 
                CONSTRAINT aggregated_transfer_adjustment_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE aggregated_transfer_adjustment OWNER TO vista;
        
        -- aggregated_transfer_chargeback
        
        CREATE TABLE aggregated_transfer_chargeback
        (
            id                          BIGINT                  NOT NULL,
            chargeback                  NUMERIC(18,2),
                CONSTRAINT aggregated_transfer_chargeback_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE aggregated_transfer_chargeback OWNER TO vista;
        
        -- aggregated_transfer$adjustments
        
        CREATE TABLE aggregated_transfer$adjustments
        (
            id                          BIGINT                  NOT NULL,
            owner                       BIGINT,
            value                       BIGINT,
            seq                         INT,
                CONSTRAINT aggregated_transfer$adjustments_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE aggregated_transfer$adjustments OWNER TO vista;
        
        -- aggregated_transfer$chargebacks
        
        CREATE TABLE aggregated_transfer$chargebacks
        (
            id                          BIGINT                  NOT NULL,
            owner                       BIGINT,
            value                       BIGINT,
            seq                         INT,
                CONSTRAINT aggregated_transfer$chargebacks_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE aggregated_transfer$chargebacks OWNER TO vista;
        
        -- apt_unit
        
        ALTER TABLE apt_unit RENAME COLUMN info_legal_address_country TO info_legal_address_country_old;
        ALTER TABLE apt_unit RENAME COLUMN info_legal_address_province TO info_legal_address_province_old;
        
        ALTER TABLE apt_unit    ADD COLUMN info_legal_address_country VARCHAR(50),
                                ADD COLUMN info_legal_address_province VARCHAR(500);
        
        
        -- available_crm_report
        
        CREATE TABLE available_crm_report
        (
            id                          BIGINT                  NOT NULL,
            report_type                 VARCHAR(50),
                CONSTRAINT available_crm_report_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE available_crm_report OWNER TO vista;
        
        
        -- available_crm_report$rls
        
        CREATE TABLE available_crm_report$rls
        (
            id                          BIGINT                  NOT NULL,
            owner                       BIGINT,
            value                       BIGINT,
            seq                         INT,
                CONSTRAINT available_crm_report$rls_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE available_crm_report$rls OWNER TO vista;
        
        -- autopay_agreement
        
        ALTER TABLE autopay_agreement ALTER COLUMN comments TYPE VARCHAR(10000);
        
        -- billable_item_adjustment
        
        ALTER TABLE billable_item_adjustment    ADD COLUMN adjustment_value_amount NUMERIC(18,2),
                                                ADD COLUMN adjustment_value_percent NUMERIC(18,2);
        
        
        -- billing_arrears_snapshot
        
        ALTER TABLE billing_arrears_snapshot    ADD COLUMN legal_status VARCHAR(50),
                                                ADD COLUMN legal_status_date TIMESTAMP;
        
        -- building 
        
        ALTER TABLE building RENAME COLUMN info_address_country TO info_address_country_old;
        ALTER TABLE building RENAME COLUMN info_address_province TO info_address_province_old;
        
        ALTER TABLE building    ADD COLUMN contacts_support_phone VARCHAR(500),
                                ADD COLUMN info_address_country VARCHAR(50),
                                ADD COLUMN info_address_province VARCHAR(500);
                                
        -- city
        
        ALTER TABLE city RENAME COLUMN province TO province_old;
        ALTER TABLE city ADD COLUMN province VARCHAR(50);
        
        -- city_intro_page
        
        ALTER TABLE city_intro_page RENAME COLUMN province TO province_old;
        ALTER TABLE city_intro_page ADD COLUMN province VARCHAR(50);
        
        
        -- communication_delivery_handle
        
        CREATE TABLE communication_delivery_handle
        (
            id                              BIGINT                  NOT NULL,
            recipient                       BIGINT                  NOT NULL,
            recipient_discriminator         VARCHAR(50)             NOT NULL,
            star                            BOOLEAN,
            is_read                         BOOLEAN,
            message                         BIGINT                  NOT NULL,
            generated_from_group            BOOLEAN,
            communication_group_building    BIGINT,
            communication_group_portfolio   BIGINT,
                    CONSTRAINT communication_delivery_handle_pk PRIMARY KEY(id)
        );
        
        -- communication_message
        
        ALTER TABLE communication_message RENAME COLUMN is_high_importance TO high_importance;
        
        ALTER TABLE communication_delivery_handle OWNER TO vista;
        
        -- concession_v
        
        ALTER TABLE concession_v    ADD COLUMN val_amount NUMERIC(18,2),
                                    ADD COLUMN val_percent NUMERIC(18,2);
        
        -- country renamed country_policy_node
        
        -- ALTER TABLE country RENAME TO country_policy_node;
        
        -- country_policy_node --  as new table 
        
        CREATE TABLE country_policy_node
        (
            id                          BIGINT              NOT NULL,
            country                     VARCHAR(50),
                CONSTRAINT  country_policy_node_pk  PRIMARY KEY(id)
        );
        
        ALTER TABLE country_policy_node OWNER TO vista;
        
        
        -- communication_message_category
        
        CREATE TABLE communication_message_category
        (
            id                          BIGINT              NOT NULL,
            category                    VARCHAR(500),
            category_type               VARCHAR(50),
            ticket_type                 VARCHAR(50),
            deleted                     BOOLEAN,
                CONSTRAINT  communication_message_category_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE communication_message_category OWNER TO vista;
        
        -- communication_message_category$dispatchers
        
        CREATE TABLE communication_message_category$dispatchers
        (
            id                          BIGINT              NOT NULL,
            owner                       BIGINT,
            value                       BIGINT,
            seq                         INT,
                CONSTRAINT communication_message_category$dispatchers_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE communication_message_category$dispatchers OWNER TO vista; 
        
        -- communication_message_category$rls
        
        CREATE TABLE communication_message_category$rls
        (
            id                          BIGINT              NOT NULL,
            owner                       BIGINT,
            value                       BIGINT,
            seq                         INT,
                CONSTRAINT communication_message_category$rls_pk PRIMARY KEY(id)
        );
        
        -- communication_thread
        
        ALTER TABLE communication_thread    ADD COLUMN owner BIGINT,
                                            ADD COLUMN owner_discriminator VARCHAR(50),
                                            ADD COLUMN status VARCHAR(50),
                                            ADD COLUMN category BIGINT,
                                            ADD COLUMN allowed_reply BOOLEAN;
                                            
        ALTER TABLE communication_thread ALTER COLUMN subject TYPE VARCHAR(78);
        
        ALTER TABLE communication_message_category$rls OWNER TO vista;
        
        -- communication_thread_policy_handle
        
        CREATE TABLE communication_thread_policy_handle
        (
            id                              BIGINT          NOT NULL,
            policy_consumer                 BIGINT          NOT NULL,
            policy_consumer_discriminator   VARCHAR(50)     NOT NULL,
            hidden                          BOOLEAN,
            thread                          BIGINT          NOT NULL,
                CONSTRAINT communication_thread_policy_handle_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE communication_thread_policy_handle OWNER TO vista;
        
        -- crm_role
        
        ALTER TABLE crm_role ALTER COLUMN name TYPE VARCHAR(55);
        ALTER TABLE crm_role ADD COLUMN system_predefined BOOLEAN;
        
        -- customer_screening_income_info
        
        ALTER TABLE customer_screening_income_info RENAME COLUMN address_country TO address_country_old;
        ALTER TABLE customer_screening_income_info RENAME COLUMN address_province TO address_province_old;
        
        ALTER TABLE customer_screening_income_info  ADD COLUMN address_country VARCHAR(50),
                                                    ADD COLUMN address_province VARCHAR(500),
                                                    ADD COLUMN address_street_name VARCHAR(500),
                                                    ADD COLUMN address_street_number VARCHAR(500),
                                                    ADD COLUMN address_suite_number VARCHAR(500);
                                                    
        -- customer_screening_v
        
        ALTER TABLE customer_screening_v RENAME COLUMN current_address_country TO current_address_country_old;
        ALTER TABLE customer_screening_v RENAME COLUMN current_address_province TO current_address_province_old;
        ALTER TABLE customer_screening_v RENAME COLUMN previous_address_country TO previous_address_country_old;
        ALTER TABLE customer_screening_v RENAME COLUMN previous_address_province TO previous_address_province_old;
        
        ALTER TABLE customer_screening_v    ADD COLUMN current_address_country VARCHAR(50),
                                            ADD COLUMN current_address_province VARCHAR(500),
                                            ADD COLUMN previous_address_country VARCHAR(50),
                                            ADD COLUMN previous_address_province VARCHAR(500);
                                            
                                                
                                                
        -- decision_info
        
        CREATE TABLE decision_info
        (
            id                              BIGINT              NOT NULL,
            decided_by                      BIGINT,
            decision_date                   DATE,
            decision_reason                 VARCHAR(500),
                CONSTRAINT decision_info_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE decision_info OWNER TO vista;
        
        -- emergency_contact
        
        ALTER TABLE emergency_contact RENAME COLUMN address_country TO  address_country_old;
        ALTER TABLE emergency_contact RENAME COLUMN address_province TO address_province_old;
        
        ALTER TABLE emergency_contact   ADD COLUMN address_country VARCHAR(50),
                                        ADD COLUMN address_province VARCHAR(500),
                                        ADD COLUMN address_street_name VARCHAR(500),
                                        ADD COLUMN address_street_number VARCHAR(500),
                                        ADD COLUMN address_suite_number VARCHAR(500);
                                        
        -- landlord
        
        ALTER TABLE landlord RENAME COLUMN  address_country TO  address_country_old;
        ALTER TABLE landlord RENAME COLUMN  address_province TO  address_province_old;
        
        ALTER TABLE landlord    ADD COLUMN address_country VARCHAR(50),
                                ADD COLUMN address_province VARCHAR(500);
        
        
        -- late_fee_item
        
        ALTER TABLE late_fee_item   ADD COLUMN base_fee_amount NUMERIC(18,2),
                                    ADD COLUMN base_fee_percent NUMERIC(18,2),
                                    ADD COLUMN max_total_fee_amount NUMERIC(18,2),
                                    ADD COLUMN max_total_fee_percent NUMERIC(18,2);
                                    
        -- lease_adjustment
        
        ALTER TABLE lease_adjustment    ADD COLUMN tax_amount NUMERIC(18,2),
                                        ADD COLUMN tax_percent NUMERIC(18,2);
                                        
        -- lease_application
        
        ALTER TABLE lease_application   ADD COLUMN submission_decided_by BIGINT,
                                        ADD COLUMN submission_decision_date DATE,
                                        ADD COLUMN submission_decision_reason VARCHAR(500),
                                        ADD COLUMN validation_decided_by BIGINT,
                                        ADD COLUMN validation_decision_date DATE,
                                        ADD COLUMN validation_decision_reason VARCHAR(500),
                                        ADD COLUMN approval_decided_by BIGINT,
                                        ADD COLUMN approval_decision_date DATE,
                                        ADD COLUMN approval_decision_reason VARCHAR(500);
        
        -- legal_status
        
        ALTER TABLE legal_status    ADD COLUMN cancellation_threshold NUMERIC(18,2),
                                    ADD COLUMN expiry TIMESTAMP,
                                    ADD COLUMN termination_date DATE;
        
        -- marketing
        
        ALTER TABLE marketing RENAME COLUMN marketing_address_country TO marketing_address_country_old;
        ALTER TABLE marketing RENAME COLUMN marketing_address_province TO marketing_address_province_old;
        
        ALTER TABLE marketing   ADD COLUMN marketing_address_country VARCHAR(50),
                                ADD COLUMN marketing_address_province VARCHAR(500);
    
        
        -- master_online_application
        
        ALTER TABLE master_online_application RENAME COLUMN building TO ils_building;
        ALTER TABLE master_online_application RENAME COLUMN floorplan TO ils_floorplan;
        
        
        -- message_attachment_blob
        
        CREATE TABLE message_attachment_blob
        (
            id                      BIGINT                  NOT NULL,
            name                    VARCHAR(500),
            content_type            VARCHAR(500),
            updated                 TIMESTAMP,
            created                 TIMESTAMP,
            data                    BYTEA,
                CONSTRAINT message_attachment_blob_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE message_attachment_blob OWNER TO vista;
        
        -- n4_policy
        
        ALTER TABLE n4_policy RENAME COLUMN mailing_address_country TO mailing_address_country_old;
        ALTER TABLE n4_policy RENAME COLUMN mailing_address_province TO mailing_address_province_old;
        
        ALTER TABLE n4_policy   ADD COLUMN cancellation_threshold NUMERIC(18,2),
                                ADD COLUMN expiry_days INT,
                                ADD COLUMN mailing_address_country VARCHAR(50),
                                ADD COLUMN mailing_address_province VARCHAR(500);
                                
        -- payment_method
        
        ALTER TABLE payment_method RENAME COLUMN billing_address_country TO billing_address_country_old;
        ALTER TABLE payment_method RENAME COLUMN billing_address_province TO billing_address_province_old;
        
        ALTER TABLE payment_method  ADD COLUMN billing_address_country VARCHAR(50),
                                    ADD COLUMN billing_address_province VARCHAR(500),
                                    ADD COLUMN billing_address_street_name VARCHAR(500),
                                    ADD COLUMN billing_address_street_number VARCHAR(500),
                                    ADD COLUMN billing_address_suite_number VARCHAR(500),
                                    ADD COLUMN expiration_note_sent BOOLEAN;
        
        -- payment_record
        
        ALTER TABLE payment_record  ADD COLUMN aggregated_transfer_discriminator VARCHAR(50),
                                    ADD COLUMN aggregated_transfer_return_discriminator VARCHAR(50),
                                    ADD COLUMN convenience_fee_signed_term BIGINT;
                                    
        ALTER TABLE payment_record ALTER COLUMN yardi_document_number TYPE VARCHAR(24);
                                    
        
        -- payment_record_processing
        
        ALTER TABLE payment_record_processing ADD COLUMN aggregated_transfer_discriminator VARCHAR(50);
        
        
        -- online_application
        
        ALTER TABLE online_application ADD COLUMN create_date DATE;
        
        -- product_item
        
        ALTER TABLE product_item ADD COLUMN yardi_deposit_lmr NUMERIC(18,2);
        
        -- product_v
        
        ALTER TABLE product_v   ADD COLUMN deposit_lmr_deposit_value_amount NUMERIC(18,2),
                                ADD COLUMN deposit_lmr_deposit_value_percent NUMERIC(18,2),
                                ADD COLUMN deposit_move_in_deposit_value_amount NUMERIC(18,2),
                                ADD COLUMN deposit_move_in_deposit_value_percent NUMERIC(18,2),
                                ADD COLUMN deposit_security_deposit_value_amount NUMERIC(18,2),
                                ADD COLUMN deposit_security_deposit_value_percent NUMERIC(18,2);
        
        -- province 
        
        ALTER TABLE province RENAME TO province_policy_node;
        
        ALTER TABLE province_policy_node ADD COLUMN province VARCHAR(50);
        
        -- pt_vehicle
        
        ALTER TABLE pt_vehicle RENAME COLUMN country TO country_old;
        ALTER TABLE pt_vehicle RENAME COLUMN province TO province_old;
        
        ALTER TABLE pt_vehicle  ADD COLUMN country VARCHAR(50),
                                ADD COLUMN province VARCHAR(50);
                                
        -- resident_portal_policy
        
        CREATE TABLE resident_portal_policy
        (
            id                      BIGINT                  NOT NULL,
            node                    BIGINT,
            node_discriminator      VARCHAR(50),
            updated                 TIMESTAMP,
            communication_enabled   BOOLEAN,
                CONSTRAINT resident_portal_policy_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE resident_portal_policy OWNER TO vista;
        
        
        -- restrictions_policy
        
        ALTER TABLE restrictions_policy ADD COLUMN no_need_guarantors BOOLEAN;
        
        -- signed_web_payment_term
        
        CREATE TABLE signed_web_payment_term
        (
            id                          BIGINT              NOT NULL,
            term                        BIGINT,
            term_for                    TIMESTAMP,
            signature                   BIGINT,
                CONSTRAINT signed_web_payment_term_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE signed_web_payment_term OWNER TO vista;
        
        -- site_titles
        
        ALTER TABLE site_titles RENAME COLUMN resident_portal_promotions TO site_promo_title;
        
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        -- _admin_.admin_pmc_merchant_account_index 
        
        EXECUTE 'UPDATE _admin_.admin_pmc_merchant_account_index as a '
                ||'SET terminal_id_conv_fee = m.merchant_terminal_id_convenience_fee '
                ||'FROM '||v_schema_name||'.merchant_account m '
                ||'WHERE    a.merchant_account_key = m.id '; 
        
        
        -- province_policy_node
        
        EXECUTE 'UPDATE '||v_schema_name||'.province_policy_node '
                ||'SET  name = ''Newfoundland'' '
                ||'WHERE name = ''Newfoundland and Labrador'' ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.province_policy_node '
                ||'SET  name = ''Yukon Territory'' '
                ||'WHERE name = ''Yukon'' ';
        
        EXECUTE 'UPDATE '||v_schema_name||'.province_policy_node '
                ||'SET  province = replace(INITCAP(name),'' '','''') ';
        
        
        -- aggregated_transfer
        
        EXECUTE 'UPDATE '||v_schema_name||'.aggregated_transfer '
                ||'SET  id_discriminator = ''EftAggregatedTransfer'' ';
                
        -- aggregated_transfer_adjustment
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.aggregated_transfer_adjustment (id,adjustment,ag_id) '
                ||'(SELECT  nextval(''public.aggregated_transfer_adjustment_seq'') AS id, adjustments, id as ag_id '
                ||'FROM '||v_schema_name||'.aggregated_transfer '
                ||'WHERE    adjustments IS NOT NULL)';
                
        -- aggregated_transfer$adjustments
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.aggregated_transfer$adjustments (id,owner,value) '
                ||'(SELECT   nextval(''public.aggregated_transfer$adjustments_seq'') AS id, '
                ||'         ag_id AS owner, id AS value '
                ||'FROM '||v_schema_name||'.aggregated_transfer_adjustment)';
        
        -- apt_unit
        
        EXECUTE 'UPDATE '||v_schema_name||'.apt_unit AS a '
                ||'SET    info_legal_address_country = replace(c.name,'' '','''') '
                ||'FROM   '||v_schema_name||'.country AS c '
                ||'WHERE  a.info_legal_address_country_old = c.id ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.apt_unit AS a '
                ||'SET    info_legal_address_province = p.province '
                ||'FROM   '||v_schema_name||'.province_policy_node AS p '
                ||'WHERE  a.info_legal_address_province_old = p.id ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.apt_unit '
                ||'SET  info_legal_address_street_number = '
                ||'     info_legal_address_street_number||info_legal_address_street_number_suffix '
                ||'WHERE    info_legal_address_street_number_suffix IS NOT NULL';
        
        EXECUTE 'UPDATE '||v_schema_name||'.apt_unit '
                ||'SET  info_legal_address_street_name = '
                ||'     TRIM(info_legal_address_street_name)||'' ''||INITCAP(TRIM(info_legal_address_street_type)) '
                ||'WHERE    info_legal_address_street_type IS NOT NULL ';
        
        EXECUTE 'UPDATE '||v_schema_name||'.apt_unit '
                ||'SET  info_legal_address_street_name = '
                ||'     TRIM(info_legal_address_street_name)||'' ''||INITCAP(TRIM(info_legal_address_street_direction)) '
                ||'WHERE    info_legal_address_street_direction IS NOT NULL ';
                
        -- available_crm_report
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.available_crm_report(id,report_type) '
                ||'(SELECT nextval(''public.available_crm_report_seq'') AS id, '
                ||' ''AutoPayChanges'' AS report_type )';
        EXECUTE 'INSERT INTO '||v_schema_name||'.available_crm_report(id,report_type) '
                ||'(SELECT nextval(''public.available_crm_report_seq'') AS id, '
                ||' ''Availability'' AS report_type )';
        EXECUTE 'INSERT INTO '||v_schema_name||'.available_crm_report(id,report_type) '
                ||'(SELECT nextval(''public.available_crm_report_seq'') AS id, '
                ||' ''CustomerCreditCheck'' AS report_type )';
        EXECUTE 'INSERT INTO '||v_schema_name||'.available_crm_report(id,report_type) '
                ||'(SELECT nextval(''public.available_crm_report_seq'') AS id, '
                ||' ''EFT'' AS report_type )';
        EXECUTE 'INSERT INTO '||v_schema_name||'.available_crm_report(id,report_type) '
                ||'(SELECT nextval(''public.available_crm_report_seq'') AS id, '
                ||' ''EftVariance'' AS report_type )';
        EXECUTE 'INSERT INTO '||v_schema_name||'.available_crm_report(id,report_type) '
                ||'(SELECT nextval(''public.available_crm_report_seq'') AS id, '
                ||' ''ResidentInsurance'' AS report_type )';
        
        -- auto_pay_policy
        
        EXECUTE 'UPDATE '||v_schema_name||'.auto_pay_policy '
                ||'SET  exclude_first_billing_period_charge = FALSE '
                ||'WHERE exclude_first_billing_period_charge IS NULL';
                
        EXECUTE 'UPDATE '||v_schema_name||'.auto_pay_policy '
                ||'SET  exclude_last_billing_period_charge = FALSE '
                ||'WHERE exclude_last_billing_period_charge IS NULL';
        
        -- billable_item_adjustment
        
        EXECUTE 'UPDATE '||v_schema_name||'.billable_item_adjustment '
                ||'SET adjustment_value_amount = adjustment_value  '
                ||'WHERE    adjustment_type = ''monetary'' ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.billable_item_adjustment '
                ||'SET adjustment_value_percent = adjustment_value  '
                ||'WHERE    adjustment_type = ''percentage'' ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.billable_item_adjustment '
                ||'SET adjustment_type = INITCAP(adjustment_type)';
        
        -- building
        
        EXECUTE 'UPDATE '||v_schema_name||'.building AS b '
                ||'SET  info_address_country = replace(c.name,'' '','''') '
                ||'FROM '||v_schema_name||'.country AS c '
                ||'WHERE b.info_address_country_old = c.id ';
        
        EXECUTE 'UPDATE '||v_schema_name||'.building AS b '
                ||'SET  info_address_province = p.province '
                ||'FROM '||v_schema_name||'.province_policy_node p '
                ||'WHERE b.info_address_province_old = p.id ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.building '
                ||'SET  info_address_street_number = '
                ||' info_address_street_number||info_address_street_number_suffix '
                ||'WHERE    info_address_street_number_suffix IS NOT NULL';
                
        EXECUTE 'UPDATE '||v_schema_name||'.building '
                ||'SET  info_address_street_name = '
                ||' TRIM(info_address_street_name)||'' ''||INITCAP(TRIM(info_address_street_type)) '
                ||'WHERE    info_address_street_type IS NOT NULL';
                
        EXECUTE 'UPDATE '||v_schema_name||'.building '
                ||'SET  info_address_street_name = '
                ||' TRIM(info_address_street_name)||'' ''||INITCAP(TRIM(info_address_street_direction)) '
                ||'WHERE    info_address_street_direction IS NOT NULL';
                
        PERFORM * FROM _dba_.move_property_manager(v_schema_name);
        
        
        -- city
        
        EXECUTE 'UPDATE '||v_schema_name||'.city AS c '
                ||'SET  province = p.province '
                ||'FROM '||v_schema_name||'.province_policy_node p '
                ||'WHERE    c.province_old = p.id ';
                
        
        -- city_intro_page
        
        EXECUTE 'UPDATE '||v_schema_name||'.city_intro_page AS c '
                ||'SET  province = p.province '
                ||'FROM '||v_schema_name||'.province_policy_node p '
                ||'WHERE    c.province_old = p.id ';
        
        -- communication_message_category
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.communication_message_category '
                ||'(id,category,category_type,ticket_type,deleted) '
                ||'(SELECT  nextval(''public.communication_message_category_seq'') AS id, '
                ||'     category,category_type,ticket_type,deleted '
                ||'FROM _dba_.tmp_categories) ';
                
                
        -- communication_message_category$rls
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.communication_message_category$rls'
                ||'(id,owner,value,seq) '
                ||'(SELECT  nextval(''public.communication_message_category$rls_seq'') AS id,'
                ||'         c.id AS owner, r.id AS value, 0 AS seq '
                ||' FROM    '||v_schema_name||'.communication_message_category c, '
                ||'         '||v_schema_name||'.crm_role r '
                ||'WHERE r.name = ''All'') ';
       
        -- customer_screening_income_info
        
        EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_income_info AS i '
                ||'SET  address_country = replace(c.name,'' '','''') '
                ||'FROM '||v_schema_name||'.country c '
                ||'WHERE    i.address_country_old = c.id ';
                
                
        EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_income_info AS i '
                ||'SET  address_province = p.province '
                ||'FROM '||v_schema_name||'.province_policy_node AS p '
                ||'WHERE i.address_province_old = p.id ';
        
        EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_income_info AS c '
                ||'SET  address_street_number = t.street_num,'
                ||'     address_suite_number = t.suite_num,'
                ||'     address_street_name = t.street_name '
                ||'FROM     _dba_.split_simple_address('||quote_literal(v_schema_name)||','
                ||'         ''customer_screening_income_info'',''address_street1'',''address_street2'') AS t '
                ||'WHERE    c.id = t.id ';
        
        
        -- customer_screening_v
        
        EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_v AS s '
                ||'SET current_address_country = replace(c.name,'' '','''') '
                ||'FROM '||v_schema_name||'.country AS c '
                ||'WHERE   current_address_country_old = c.id ';
                
                
        EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_v AS s '
                ||'SET previous_address_country = replace(c.name,'' '','''') '
                ||'FROM '||v_schema_name||'.country AS c '
                ||'WHERE   previous_address_country_old = c.id ';
        
        
        EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_v AS s '
                ||'SET current_address_province = p.province '
                ||'FROM '||v_schema_name||'.province_policy_node AS p '
                ||'WHERE   current_address_province_old = p.id ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_v AS s '
                ||'SET previous_address_province = p.province '
                ||'FROM '||v_schema_name||'.province_policy_node AS p '
                ||'WHERE   previous_address_province_old = p.id ';
        
        
        EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_v '
                ||'SET  current_address_street_number = '
                ||' current_address_street_number||current_address_street_number_suffix '
                ||'WHERE    current_address_street_number_suffix IS NOT NULL';
                
        EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_v '
                ||'SET  previous_address_street_number = '
                ||' previous_address_street_number||previous_address_street_number_suffix '
                ||'WHERE    previous_address_street_number_suffix IS NOT NULL';
                
        EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_v '
                ||'SET  current_address_street_name = '
                ||' TRIM(current_address_street_name)||'' ''||INITCAP(TRIM(current_address_street_type)) '
                ||'WHERE    current_address_street_type IS NOT NULL';
        
        EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_v '
                ||'SET  previous_address_street_name = '
                ||' TRIM(previous_address_street_name)||'' ''||INITCAP(TRIM(previous_address_street_type)) '
                ||'WHERE    previous_address_street_type IS NOT NULL';
        
        EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_v '
                ||'SET  current_address_street_name = '
                ||' TRIM(current_address_street_name)||'' ''||INITCAP(TRIM(current_address_street_direction)) '
                ||'WHERE    current_address_street_direction IS NOT NULL';
        
        
        EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_v '
                ||'SET  previous_address_street_name = '
                ||' TRIM(previous_address_street_name)||'' ''||INITCAP(TRIM(previous_address_street_direction)) '
                ||'WHERE    previous_address_street_direction IS NOT NULL';
        
        
        -- emergency_contact
        
        EXECUTE 'UPDATE '||v_schema_name||'.emergency_contact AS e '
                ||'SET    address_country = replace(c.name,'' '','''') '
                ||'FROM   '||v_schema_name||'.country AS c '
                ||'WHERE  e.address_country_old = c.id ';
        
        EXECUTE 'UPDATE '||v_schema_name||'.emergency_contact AS e '
                ||'SET  address_province = p.province '
                ||'FROM '||v_schema_name||'.province_policy_node p '
                ||'WHERE    e.address_province_old = p.id ';
        
        
        EXECUTE 'UPDATE '||v_schema_name||'.emergency_contact AS e '
                ||'SET  address_suite_number = t.suite_num,'
                ||'     address_street_number = t.street_num,'
                ||'     address_street_name = t.street_name '
                ||'FROM     _dba_.split_simple_address('||quote_literal(v_schema_name)||','
                ||'         ''emergency_contact'',''address_street1'',''address_street2'') AS t '
                ||'WHERE    e.id = t.id ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.emergency_contact '
                ||'SET  address_street_number = ''INVALID'' '
                ||'WHERE address_street_number IS NULL ';
                
        -- landlord
        
        EXECUTE 'UPDATE '||v_schema_name||'.landlord AS l '
                ||'SET    address_country = replace(c.name,'' '','''') '
                ||'FROM   '||v_schema_name||'.country AS c '
                ||'WHERE  l.address_country_old = c.id ';
        
        EXECUTE 'UPDATE '||v_schema_name||'.landlord AS l '
                ||'SET  address_province = p.province '
                ||'FROM '||v_schema_name||'.province_policy_node p '
                ||'WHERE    l.address_province_old = p.id ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.landlord '
                ||'SET  address_street_number = '
                ||' address_street_number||address_street_number_suffix '
                ||'WHERE    address_street_number_suffix IS NOT NULL';
                
        EXECUTE 'UPDATE '||v_schema_name||'.landlord '
                ||'SET  address_street_name = '
                ||' TRIM(address_street_name)||'' ''||INITCAP(TRIM(address_street_type)) '
                ||'WHERE    address_street_type IS NOT NULL';
                
        EXECUTE 'UPDATE '||v_schema_name||'.landlord '
                ||'SET  address_street_name = '
                ||' TRIM(address_street_name)||'' ''||INITCAP(TRIM(address_street_direction)) '
                ||'WHERE    address_street_direction IS NOT NULL';
                
        -- late_fee_item
        
        EXECUTE 'UPDATE '||v_schema_name||'.late_fee_item '
                ||'SET  base_fee_amount = base_fee '
                ||'WHERE    base_fee_type = ''FlatAmount'' ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.late_fee_item '
                ||'SET  base_fee_percent = base_fee '
                ||'WHERE    base_fee_type != ''FlatAmount'' ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.late_fee_item '
                ||'SET  max_total_fee_amount = max_total_fee  '
                ||'WHERE    max_total_fee_type = ''FlatAmount'' ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.late_fee_item '
                ||'SET  max_total_fee_percent = max_total_fee  '
                ||'WHERE    max_total_fee_type != ''FlatAmount'' ';
                
        -- lease_adjustment
        
        EXECUTE 'UPDATE '||v_schema_name||'.lease_adjustment '
                ||'SET tax_amount = tax '
                ||'WHERE    tax_type = ''value'' ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.lease_adjustment '
                ||'SET tax_percent = tax '
                ||'WHERE    tax_type = ''percent'' ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.lease_adjustment '
                ||'SET tax_type = ''Monetary'' '
                ||'WHERE    tax_type = ''value'' ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.lease_adjustment '
                ||'SET tax_type = ''Percentage'' '
                ||'WHERE    tax_type = ''percent'' ';
                
        -- lease_application
        
        EXECUTE 'UPDATE '||v_schema_name||'.lease_application '
                ||'SET  status = ''InProgress'' '
                ||'WHERE    status = ''Created'' ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.lease_application '
                ||'SET  status = ''InProgress'' '
                ||'WHERE    status = ''OnlineApplication'' ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.lease_application '
                ||'SET  approval_decided_by = decided_by, '
                ||'     approval_decision_date = decision_date, '
                ||'     approval_decision_reason = decision_reason';
        
        /*        
        -- legal_terms_policy_item
        
        EXECUTE 'UPDATE '||v_schema_name||'.legal_terms_policy_item '
                ||'SET content = regexp_replace(content, ''Convenience Fee'', ''Web Payment Fee'',''g'') '
                ||'WHERE    caption = ''RESIDENT PORTAL TERMS AND CONDITIONS'' ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.legal_terms_policy_item '
                ||'SET content = regexp_replace(content, ''Convenience Fees'', ''Web Payment Fees'',''g'') '
                ||'WHERE    caption = ''RESIDENT PORTAL TERMS AND CONDITIONS'' ';
        
        */
        
        
        -- marketing
        
        EXECUTE 'UPDATE '||v_schema_name||'.marketing AS m '
                ||'SET  marketing_address_country = replace(c.name,'' '','''') '
                ||'FROM   '||v_schema_name||'.country AS c '
                ||'WHERE  m.marketing_address_country_old = c.id ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.marketing AS m '
                ||'SET  marketing_address_province = p.province '
                ||'FROM '||v_schema_name||'.province_policy_node p '
                ||'WHERE    m.marketing_address_province_old = p.id ';
        
        EXECUTE 'UPDATE '||v_schema_name||'.marketing '
                ||'SET  marketing_address_street_number = '
                ||' marketing_address_street_number||marketing_address_street_number_suffix '
                ||'WHERE    marketing_address_street_number_suffix IS NOT NULL';
                
        EXECUTE 'UPDATE '||v_schema_name||'.marketing '
                ||'SET  marketing_address_street_name = '
                ||' TRIM(marketing_address_street_name)||'' ''||INITCAP(TRIM(marketing_address_street_type)) '
                ||'WHERE    marketing_address_street_type IS NOT NULL';
                
        EXECUTE 'UPDATE '||v_schema_name||'.marketing '
                ||'SET  marketing_address_street_name = '
                ||' TRIM(marketing_address_street_name)||'' ''||INITCAP(TRIM(marketing_address_street_direction)) '
                ||'WHERE    marketing_address_street_direction IS NOT NULL';
        
        -- n4_policy
        
        EXECUTE 'UPDATE '||v_schema_name||'.n4_policy AS n '
                ||'SET  mailing_address_country = replace(c.name,'' '','''') '
                ||'FROM   '||v_schema_name||'.country AS c '
                ||'WHERE  n.mailing_address_country_old = c.id ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.n4_policy AS n '
                ||'SET  mailing_address_province = p.province '
                ||'FROM '||v_schema_name||'.province_policy_node p '
                ||'WHERE    n.mailing_address_province_old = p.id ';
        
        EXECUTE 'UPDATE '||v_schema_name||'.n4_policy '
                ||'SET  mailing_address_street_number = '
                ||' mailing_address_street_number||mailing_address_street_number_suffix '
                ||'WHERE    mailing_address_street_number_suffix IS NOT NULL';
                
        EXECUTE 'UPDATE '||v_schema_name||'.n4_policy '
                ||'SET  mailing_address_street_name = '
                ||' TRIM(mailing_address_street_name)||'' ''||INITCAP(TRIM(mailing_address_street_type)) '
                ||'WHERE    mailing_address_street_type IS NOT NULL';
                
        EXECUTE 'UPDATE '||v_schema_name||'.n4_policy '
                ||'SET  mailing_address_street_name = '
                ||' TRIM(mailing_address_street_name)||'' ''||INITCAP(TRIM(mailing_address_street_direction)) '
                ||'WHERE    mailing_address_street_direction IS NOT NULL';
                
        -- notes_and_attachments
        
        EXECUTE 'UPDATE '||v_schema_name||'.notes_and_attachments '
                ||'SET owner_discriminator = ''EftAggregatedTransfer'' '
                ||'WHERE    owner_discriminator = ''AggregatedTransfer'' ';
        
         -- online_application
        
        EXECUTE 'UPDATE '||v_schema_name||'.online_application AS a '
                ||'SET  create_date = m.create_date '
                ||'FROM '||v_schema_name||'.master_online_application AS m '
                ||'WHERE    m.id = a.master_online_application ';
        
        -- payment_method
        
        EXECUTE 'UPDATE '||v_schema_name||'.payment_method AS p '
                ||'SET    billing_address_country = replace(c.name,'' '','''') '
                ||'FROM   '||v_schema_name||'.country AS c '
                ||'WHERE  p.billing_address_country_old = c.id ';
        
        EXECUTE 'UPDATE '||v_schema_name||'.payment_method AS pm '
                ||'SET  billing_address_province = p.province '
                ||'FROM '||v_schema_name||'.province_policy_node p '
                ||'WHERE    pm.billing_address_province_old = p.id ';
        
        
        EXECUTE 'UPDATE '||v_schema_name||'.payment_method AS p '
                ||'SET  billing_address_suite_number = t.suite_num,'
                ||'     billing_address_street_number = t.street_num,'
                ||'     billing_address_street_name = t.street_name '
                ||'FROM     _dba_.split_simple_address('||quote_literal(v_schema_name)||','
                ||'         ''payment_method'',''billing_address_street1'',''billing_address_street2'') AS t '
                ||'WHERE    p.id = t.id ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.payment_method '
                ||'SET  billing_address_street_number = ''INVALID'' '
                ||'WHERE billing_address_street_number IS NULL ';
                
        -- payment_record
        
        EXECUTE 'UPDATE '||v_schema_name||'.payment_record '
                ||'SET  aggregated_transfer_discriminator = ''EftAggregatedTransfer'' '
                ||'WHERE    aggregated_transfer IS NOT NULL';
                
        EXECUTE 'UPDATE '||v_schema_name||'.payment_record '
                ||'SET  aggregated_transfer_return_discriminator = ''EftAggregatedTransfer'' '
                ||'WHERE    aggregated_transfer_return IS NOT NULL';
                
        -- payment_record_processing
        
        EXECUTE 'UPDATE '||v_schema_name||'.payment_record_processing '
                ||'SET  aggregated_transfer_discriminator = ''EftAggregatedTransfer'' ';
        
        -- product_v
        
        EXECUTE 'UPDATE '||v_schema_name||'.product_v '
                ||'SET  deposit_lmr_deposit_value_amount = deposit_lmr_deposit_value '
                ||'WHERE deposit_lmr_value_type = ''Monetary'' '; 
                
        EXECUTE 'UPDATE '||v_schema_name||'.product_v '
                ||'SET  deposit_lmr_deposit_value_percent = deposit_lmr_deposit_value '
                ||'WHERE deposit_lmr_value_type = ''Percentage'' '; 
                
        EXECUTE 'UPDATE '||v_schema_name||'.product_v '
                ||'SET  deposit_move_in_deposit_value_amount = deposit_move_in_deposit_value '
                ||'WHERE deposit_move_in_value_type = ''Monetary'' '; 
                
        EXECUTE 'UPDATE '||v_schema_name||'.product_v '
                ||'SET  deposit_move_in_deposit_value_percent = deposit_move_in_deposit_value '
                ||'WHERE deposit_move_in_value_type = ''Percentage'' '; 
                
        EXECUTE 'UPDATE '||v_schema_name||'.product_v '
                ||'SET  deposit_security_deposit_value_amount = deposit_security_deposit_value '
                ||'WHERE deposit_security_value_type = ''Monetary'' '; 
                
        EXECUTE 'UPDATE '||v_schema_name||'.product_v '
                ||'SET  deposit_security_deposit_value_percent = deposit_security_deposit_value '
                ||'WHERE deposit_security_value_type = ''Percentage'' '; 
        
        -- Phone numbers update
        
        PERFORM * FROM _dba_.update_phone_numbers(v_schema_name);
       
        
                
        -- pt_vehicle
        
        EXECUTE 'UPDATE '||v_schema_name||'.pt_vehicle AS pt '
                ||'SET  country = replace(c.name,'' '','''') '
                ||'FROM   '||v_schema_name||'.country AS c '
                ||'WHERE  pt.country_old = c.id ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.pt_vehicle AS pt '
                ||'SET  province = p.province '
                ||'FROM '||v_schema_name||'.province_policy_node p '
                ||'WHERE    pt.province_old = p.id ';
                
        
        -- resident_portal_policy
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.resident_portal_policy'
                ||'(id,node,node_discriminator,updated,communication_enabled) '
                ||'(SELECT  nextval(''public.resident_portal_policy_seq'') AS id, '
                ||'         id AS node, ''OrganizationPoliciesNode'' AS node_discriminator,'
                ||'         DATE_TRUNC(''second'',current_timestamp)::timestamp AS updated, '
                ||'         FALSE AS communication_enabled '
                ||' FROM '||v_schema_name||'.organization_policies_node )';
                
        -- restrictions_policy
        
        EXECUTE 'UPDATE '||v_schema_name||'.restrictions_policy '
                ||'SET  no_need_guarantors = FALSE ';
                
        
        -- system_endpoint
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.system_endpoint(id,name) VALUES '
                ||'(nextval(''public.system_endpoint_seq''), ''Ticket Dispatcher''),'
                ||'(nextval(''public.system_endpoint_seq''), ''Automatic''),'
                ||'(nextval(''public.system_endpoint_seq''), ''Group'')';
       
       
        SET CONSTRAINTS ALL IMMEDIATE;
        
        /**
        ***     ==========================================================================================================
        ***
        ***             ROLES AND BEHAVIOURS
        ***
        ***     ==========================================================================================================
        **/
        
        -- delete all behaviours for old roles
        
        EXECUTE 'DELETE FROM '||v_schema_name||'.crm_role$behaviors ';
        
        -- delete all from crm_user_credential$rls for roles other than 'All'
        EXECUTE 'DELETE FROM '||v_schema_name||'.crm_user_credential$rls '
                ||'WHERE value IN ( SELECT id FROM '||v_schema_name||'.crm_role '
                ||'                 WHERE name != ''All'')';
                
        -- delete old roles
        
         EXECUTE 'DELETE FROM '||v_schema_name||'.crm_role '
                ||'WHERE name  != ''All'' ';
        
        
        -- import new roles form tmp_table 
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.crm_role (id,name,'
                ||'require_security_question_for_password_reset,updated) '
                ||'(SELECT  DISTINCT nextval(''public.crm_role_seq'') AS id, '
                ||'         t.name,t.require_security_question_for_password_reset, '
                ||'         date_trunc(''second'',current_timestamp)::timestamp '
                ||'FROM     _dba_.tmp_roles t '
                ||'WHERE    t.name != ''All'' )';
        
        -- update role 'All'
        
        EXECUTE 'UPDATE '||v_schema_name||'.crm_role '
                ||'SET  system_predefined = TRUE '
                ||'WHERE    name = ''All'' ';
        
        -- import new role behaviours 
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.crm_role$behaviors (id,owner,value) '
                ||'(SELECT  nextval(''public.crm_role$behaviors_seq'') AS id, '
                ||'         r.id AS owner, t.value '
                ||'FROM     '||v_schema_name||'.crm_role r '
                ||'JOIN     _dba_.tmp_roles AS t ON (t.name = r.name)) ';
                
    
        -- available_crm_report$rls 
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.available_crm_report$rls (id, owner, value) '
                ||'(SELECT  nextval(''public.available_crm_report$rls_seq'') AS id, '
                ||'         a.id AS owner, r.id AS value '
                ||'FROM     '||v_schema_name||'.available_crm_report a, '
                ||'         '||v_schema_name||'.crm_role r '
                ||'WHERE    r.name = ''All'' )';
        
        
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        -- aggregated_transfer
        
        ALTER TABLE aggregated_transfer DROP COLUMN adjustments;
        
        -- aggregated_transfer_adjustment
        
        ALTER TABLE aggregated_transfer_adjustment DROP COLUMN ag_id;
        
        -- apt_unit
        
        ALTER TABLE apt_unit    DROP COLUMN info_legal_address_country_old,
                                DROP COLUMN info_legal_address_province_old,
                                DROP COLUMN info_legal_address_county,
                                DROP COLUMN info_legal_address_street_direction,
                                DROP COLUMN info_legal_address_street_number_suffix,
                                DROP COLUMN info_legal_address_street_type;
        
        -- billable_item_adjustment
        
        ALTER TABLE billable_item_adjustment DROP COLUMN adjustment_value;
                                
        -- building
        
        ALTER TABLE building    DROP COLUMN info_address_country_old,
                                DROP COLUMN info_address_province_old,
                                DROP COLUMN info_address_county,
                                DROP COLUMN info_address_street_direction,
                                DROP COLUMN info_address_street_number_suffix,
                                DROP COLUMN info_address_street_type,
                                DROP COLUMN property_manager;
                                
        -- city 
        
        ALTER TABLE city DROP COLUMN province_old;
        
        
        -- city_intro_page 
        
        ALTER TABLE city_intro_page DROP COLUMN province_old;
        
        -- communication_message 
        
        ALTER TABLE communication_message DROP COLUMN is_read;
        
        -- communication_message$to
        
        DROP TABLE communication_message$to;
        
        -- communication_message_attachment_blob
        
        DROP TABLE communication_message_attachment_blob;
        
        -- communication_thread
        
        ALTER TABLE communication_thread    DROP COLUMN created,
                                            DROP COLUMN responsible,
                                            DROP COLUMN responsible_discriminator; 
        
        -- concession_v
        
        ALTER TABLE concession_v DROP COLUMN val;
        
        -- country
        
        DROP TABLE country;
        
        -- crm_role$rls
        
        DROP TABLE crm_role$rls;
        
        -- customer_screening_income_info
        
        ALTER TABLE customer_screening_income_info  DROP COLUMN address_country_old,
                                                    DROP COLUMN address_province_old,
                                                    DROP COLUMN address_street1,
                                                    DROP COLUMN address_street2;
                                                    
        -- customer_screening_v

        ALTER TABLE customer_screening_v    DROP COLUMN current_address_country_old,
                                            DROP COLUMN current_address_county,
                                            DROP COLUMN current_address_province_old,
                                            DROP COLUMN current_address_street_direction,
                                            DROP COLUMN current_address_street_number_suffix,
                                            DROP COLUMN current_address_street_type,
                                            DROP COLUMN previous_address_country_old,
                                            DROP COLUMN previous_address_county,
                                            DROP COLUMN previous_address_province_old,
                                            DROP COLUMN previous_address_street_direction,
                                            DROP COLUMN previous_address_street_number_suffix,
                                            DROP COLUMN previous_address_street_type;
       
       -- emergency_contact
       
       ALTER TABLE emergency_contact    DROP COLUMN address_country_old,
                                        DROP COLUMN address_province_old,
                                        DROP COLUMN address_street1,
                                        DROP COLUMN address_street2;
                                        
        -- landlord
        
        ALTER TABLE landlord    DROP COLUMN address_country_old,
                                DROP COLUMN address_county,
                                DROP COLUMN address_province_old,
                                DROP COLUMN address_street_direction,
                                DROP COLUMN address_street_number_suffix,
                                DROP COLUMN address_street_type;
                                
        -- late_fee_item
        
        ALTER TABLE late_fee_item   DROP COLUMN base_fee,
                                    DROP COLUMN max_total_fee;
                                    
        -- lease_adjustment
        
        ALTER TABLE lease_adjustment DROP COLUMN tax;
        
        -- lease_application
        
        ALTER TABLE lease_application   DROP COLUMN decided_by,
                                        DROP COLUMN decision_date,
                                        DROP COLUMN decision_reason;
                                
        -- legal_letter
        
        ALTER TABLE legal_letter    DROP COLUMN cancellation_threshold,
                                    DROP COLUMN is_active;
                                    
        -- marketing
        
        ALTER TABLE marketing       DROP COLUMN marketing_address_country_old,
                                    DROP COLUMN marketing_address_county,
                                    DROP COLUMN marketing_address_province_old,
                                    DROP COLUMN marketing_address_street_direction,
                                    DROP COLUMN marketing_address_street_number_suffix,
                                    DROP COLUMN marketing_address_street_type;
        
        -- n4_policy
        
        ALTER TABLE n4_policy       DROP COLUMN mailing_address_country_old,
                                    DROP COLUMN mailing_address_county,
                                    DROP COLUMN mailing_address_province_old,
                                    DROP COLUMN mailing_address_street_direction,
                                    DROP COLUMN mailing_address_street_number_suffix,
                                    DROP COLUMN mailing_address_street_type;
                                    
        -- payment_method
       
       ALTER TABLE payment_method       DROP COLUMN billing_address_country_old,
                                        DROP COLUMN billing_address_province_old,
                                        DROP COLUMN billing_address_street1,
                                        DROP COLUMN billing_address_street2;
        -- product_v
        
        ALTER TABLE product_v   DROP COLUMN deposit_lmr_deposit_value,
                                DROP COLUMN deposit_move_in_deposit_value,
                                DROP COLUMN deposit_security_deposit_value;
                                
        -- property_manager
        
        DROP TABLE property_manager;
        
        -- province_policy_node
        
        ALTER TABLE province_policy_node    DROP COLUMN code,
                                            DROP COLUMN country,
                                            DROP COLUMN name;
                                            
        -- pt_vehicle
        
        ALTER TABLE pt_vehicle  DROP COLUMN country_old,
                                DROP COLUMN province_old;
                                

        -- system_endpoint
        
        ALTER TABLE system_endpoint DROP COLUMN email,
                                    DROP COLUMN type;
                 
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        -- primary keys 
        
        ALTER TABLE province_policy_node ADD CONSTRAINT province_policy_node_pk PRIMARY KEY(id);
        
        -- foreign keys
        
        ALTER TABLE aggregated_transfer$adjustments ADD CONSTRAINT aggregated_transfer$adjustments_owner_fk FOREIGN KEY(owner) 
            REFERENCES aggregated_transfer(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE aggregated_transfer$adjustments ADD CONSTRAINT aggregated_transfer$adjustments_value_fk FOREIGN KEY(value) 
            REFERENCES aggregated_transfer_adjustment(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE aggregated_transfer$chargebacks ADD CONSTRAINT aggregated_transfer$chargebacks_owner_fk 
            FOREIGN KEY(owner) REFERENCES aggregated_transfer(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE aggregated_transfer$chargebacks ADD CONSTRAINT aggregated_transfer$chargebacks_value_fk 
            FOREIGN KEY(value) REFERENCES aggregated_transfer_chargeback(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE available_crm_report$rls ADD CONSTRAINT available_crm_report$rls_owner_fk FOREIGN KEY(owner) 
            REFERENCES available_crm_report(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE available_crm_report$rls ADD CONSTRAINT available_crm_report$rls_value_fk FOREIGN KEY(value) 
            REFERENCES crm_role(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE communication_delivery_handle ADD CONSTRAINT communication_delivery_handle_communication_group_building_fk FOREIGN KEY(communication_group_building) 
            REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE communication_delivery_handle ADD CONSTRAINT communication_delivery_handle_communication_group_portfolio_fk FOREIGN KEY(communication_group_portfolio) 
            REFERENCES portfolio(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE communication_delivery_handle ADD CONSTRAINT communication_delivery_handle_message_fk FOREIGN KEY(message) 
            REFERENCES communication_message(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE communication_message_attachment ADD CONSTRAINT communication_message_attachment_message_fk FOREIGN KEY(message) 
            REFERENCES communication_message(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE communication_message_category$dispatchers ADD CONSTRAINT communication_message_category$dispatchers_owner_fk FOREIGN KEY(owner) 
            REFERENCES communication_message_category(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE communication_message_category$dispatchers ADD CONSTRAINT communication_message_category$dispatchers_value_fk FOREIGN KEY(value) 
            REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE communication_message_category$rls ADD CONSTRAINT communication_message_category$rls_owner_fk FOREIGN KEY(owner) 
            REFERENCES communication_message_category(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE communication_message_category$rls ADD CONSTRAINT communication_message_category$rls_value_fk FOREIGN KEY(value) 
            REFERENCES crm_role(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE communication_thread ADD CONSTRAINT communication_thread_category_fk FOREIGN KEY(category) 
            REFERENCES communication_message_category(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE communication_thread_policy_handle ADD CONSTRAINT communication_thread_policy_handle_thread_fk FOREIGN KEY(thread) 
            REFERENCES communication_thread(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE decision_info ADD CONSTRAINT decision_info_decided_by_fk FOREIGN KEY(decided_by) 
            REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_application ADD CONSTRAINT lease_application_approval_decided_by_fk FOREIGN KEY(approval_decided_by) 
            REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_application ADD CONSTRAINT lease_application_submission_decided_by_fk FOREIGN KEY(submission_decided_by) 
            REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_application ADD CONSTRAINT lease_application_validation_decided_by_fk FOREIGN KEY(validation_decided_by) 
            REFERENCES employee(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE master_online_application ADD CONSTRAINT master_online_application_ils_building_fk FOREIGN KEY(ils_building) 
            REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE master_online_application ADD CONSTRAINT master_online_application_ils_floorplan_fk FOREIGN KEY(ils_floorplan) 
            REFERENCES floorplan(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE payment_record ADD CONSTRAINT payment_record_convenience_fee_signed_term_fk FOREIGN KEY(convenience_fee_signed_term) 
            REFERENCES signed_web_payment_term(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE signed_web_payment_term ADD CONSTRAINT signed_web_payment_term_signature_fk FOREIGN KEY(signature) 
            REFERENCES customer_signature(id)  DEFERRABLE INITIALLY DEFERRED;
            
        -- check constraints
        
        ALTER TABLE aggregated_transfer ADD CONSTRAINT aggregated_transfer_funds_transfer_type_e_ck 
            CHECK ((funds_transfer_type) IN ('Cards', 'DirectBankingPayment', 'InteracOnlinePayment', 'PreAuthorizedDebit'));
        ALTER TABLE aggregated_transfer ADD CONSTRAINT aggregated_transfer_id_discriminator_ck 
            CHECK ((id_discriminator) IN ('CardsAggregatedTransfer', 'EftAggregatedTransfer'));
        ALTER TABLE apt_unit ADD CONSTRAINT apt_unit_info_legal_address_country_e_ck 
            CHECK ((info_legal_address_country) IN ('Afghanistan', 'AlandIslands', 'Albania', 'Algeria', 'AmericanSamoa', 'Andorra', 
                'Angola', 'Anguilla', 'Antarctica', 'Antigua', 'Argentina', 'Armenia', 'Aruba', 'Australia', 'Austria', 'Azerbaijan', 
                'Bahamas', 'Bahrain', 'Bangladesh', 'Barbados', 'Belarus', 'Belgium', 'Belize', 'Benin', 'Bermuda', 'Bhutan', 'Bolivia', 
                'Bonaire', 'BosniaHerzegovina', 'Botswana', 'BouvetIsland', 'Brazil', 'BruneiDarussalam', 'Bulgaria', 'BurkinaFaso', 
                'Burundi', 'CaboVerde', 'Cambodia', 'Cameroon', 'Canada', 'CaymanIslands', 'CentralAfricanRepublic', 'Chad', 'Chile', 
                'China', 'ChristmasIsland', 'CocosIslands', 'Colombia', 'Comoros', 'Congo', 'CookIslands', 'CostaRica', 'Croatia', 'Cuba', 
                'Curacao', 'Cyprus', 'CzechRepublic', 'Denmark', 'Djibouti', 'Dominica', 'DominicanRepublic', 'Ecuador', 'Egypt', 'ElSalvador', 
                'EquatorialGuinea', 'Eritrea', 'Estonia', 'Ethiopia', 'FalklandIslands', 'FaroeIslands', 'Fiji', 'Finland', 'France', 'FrenchGuiana', 
                'FrenchPolynesia', 'FrenchTerritories', 'Gabon', 'Gambia', 'Georgia', 'Germany', 'Ghana', 'Gibraltar', 'Greece', 'Greenland', 'Grenada', 
                'Guadeloupe', 'Guam', 'Guatemala', 'Guernsey', 'Guinea', 'GuineaBissau', 'Guyana', 'Haiti', 'HeardIslands', 'Honduras', 'HongKong', 
                'Hungary', 'Iceland', 'India', 'Indonesia', 'Iran', 'Iraq', 'Ireland', 'IsleOfMan', 'Israel', 'Italy', 'Jamaica', 'Japan', 'Jersey', 
                'Jordan', 'Kazakhstan', 'Kenya', 'Kiribati', 'Kuwait', 'Kyrgyzstan', 'LaoRepublic', 'Latvia', 'Lebanon', 'Lesotho', 'Liberia', 'Libya', 
                'Liechtenstein', 'Lithuania', 'Luxembourg', 'Macao', 'Macedonia', 'Madagascar', 'Malawi', 'Malaysia', 'Maldives', 'Mali', 'Malta', 
                'MarshallIslands', 'Martinique', 'Mauritania', 'Mauritius', 'Mayotte', 'Mexico', 'Micronesia', 'Moldova', 'Monaco', 'Mongolia', 
                'Montenegro', 'Montserrat', 'Morocco', 'Mozambique', 'Myanmar', 'Namibia', 'Nauru', 'Nepal', 'Netherlands', 'NewCaledonia', 'NewZealand', 
                'Nicaragua', 'Niger', 'Nigeria', 'Niue', 'NorfolkIsland', 'NorthKorea', 'NorthernMarianaIslands', 'Norway', 'Oman', 'Pakistan', 'Palau', 
                'Palestine', 'Panama', 'PapuaNewGuinea', 'Paraguay', 'Peru', 'Philippines', 'Pitcairn', 'Poland', 'Portugal', 'PuertoRico', 'Qatar', 
                'Reunion', 'Romania', 'RussianFederation', 'Rwanda', 'SaintBarthelemy', 'SaintHelena', 'SaintKitts', 'SaintLucia', 'SaintMartin', 
                'SaintPierre', 'SaintVincent', 'Samoa', 'SanMarino', 'SaoTome', 'SaudiArabia', 'Senegal', 'Serbia', 'Seychelles', 'SierraLeone', 
                'Singapore', 'SintMaartenDutch', 'Slovakia', 'Slovenia', 'SolomonIslands', 'Somalia', 'SouthAfrica', 'SouthKorea', 'SouthSudan', 
                'Spain', 'SriLanka', 'Sudan', 'Suriname', 'Svalbard', 'Swaziland', 'Sweden', 'Switzerland', 'Syria', 'Taiwan', 'Tajikistan', 
                'Tanzania', 'Thailand', 'TimorLeste', 'Togo', 'Tokelau', 'Tonga', 'Trinidad', 'Tunisia', 'Turkey', 'Turkmenistan', 'TurksCaicos', 
                'Tuvalu', 'Uganda', 'Ukraine', 'UnitedArabEmirates', 'UnitedKingdom', 'UnitedStates', 'Uruguay', 'Uzbekistan', 'Vanuatu', 'Vatican', 
                'Venezuela', 'VietNam', 'VirginIslands', 'VirginIslandsGB', 'WallisFutuna', 'WesternSahara', 'Yemen', 'Zambia', 'Zimbabwe'));
        ALTER TABLE available_crm_report ADD CONSTRAINT available_crm_report_report_type_e_ck 
            CHECK ((report_type) IN ('AutoPayChanges', 'Availability', 'CustomerCreditCheck', 'EFT', 'EftVariance', 'ResidentInsurance'));
        ALTER TABLE billable_item_adjustment ADD CONSTRAINT billable_item_adjustment_adjustment_type_e_ck 
            CHECK ((adjustment_type) IN ('Monetary', 'Percentage'));
        ALTER TABLE billing_arrears_snapshot ADD CONSTRAINT billing_arrears_snapshot_legal_status_e_ck
            CHECK ((legal_status) IN ('HearingDate', 'L1', 'N4', 'None', 'Order', 'RequestToReviewOrder', 
                'SetAside', 'Sheriff', 'StayOrder'));
        ALTER TABLE building ADD CONSTRAINT building_info_address_country_e_ck 
            CHECK ((info_address_country) IN ('Afghanistan', 'AlandIslands', 'Albania', 'Algeria', 'AmericanSamoa', 'Andorra', 'Angola', 'Anguilla',
                'Antarctica', 'Antigua', 'Argentina', 'Armenia', 'Aruba', 'Australia', 'Austria', 'Azerbaijan', 'Bahamas', 'Bahrain', 'Bangladesh', 
                'Barbados', 'Belarus', 'Belgium', 'Belize', 'Benin', 'Bermuda', 'Bhutan', 'Bolivia', 'Bonaire', 'BosniaHerzegovina', 'Botswana', 
                'BouvetIsland', 'Brazil', 'BruneiDarussalam', 'Bulgaria', 'BurkinaFaso', 'Burundi', 'CaboVerde', 'Cambodia', 'Cameroon', 'Canada', 
                'CaymanIslands', 'CentralAfricanRepublic', 'Chad', 'Chile', 'China', 'ChristmasIsland', 'CocosIslands', 'Colombia', 'Comoros', 'Congo', 
                'CookIslands', 'CostaRica', 'Croatia', 'Cuba', 'Curacao', 'Cyprus', 'CzechRepublic', 'Denmark', 'Djibouti', 'Dominica', 
                'DominicanRepublic', 'Ecuador', 'Egypt', 'ElSalvador', 'EquatorialGuinea', 'Eritrea', 'Estonia', 'Ethiopia', 'FalklandIslands', 
                'FaroeIslands', 'Fiji', 'Finland', 'France', 'FrenchGuiana', 'FrenchPolynesia', 'FrenchTerritories', 'Gabon', 'Gambia', 
                'Georgia', 'Germany', 'Ghana', 'Gibraltar', 'Greece', 'Greenland', 'Grenada', 'Guadeloupe', 'Guam', 'Guatemala', 'Guernsey', 
                'Guinea', 'GuineaBissau', 'Guyana', 'Haiti', 'HeardIslands', 'Honduras', 'HongKong', 'Hungary', 'Iceland', 'India', 'Indonesia', 
                'Iran', 'Iraq', 'Ireland', 'IsleOfMan', 'Israel', 'Italy', 'Jamaica', 'Japan', 'Jersey', 'Jordan', 'Kazakhstan', 'Kenya', 'Kiribati', 
                'Kuwait', 'Kyrgyzstan', 'LaoRepublic', 'Latvia', 'Lebanon', 'Lesotho', 'Liberia', 'Libya', 'Liechtenstein', 'Lithuania', 'Luxembourg', 
                'Macao', 'Macedonia', 'Madagascar', 'Malawi', 'Malaysia', 'Maldives', 'Mali', 'Malta', 'MarshallIslands', 'Martinique', 'Mauritania', 
                'Mauritius', 'Mayotte', 'Mexico', 'Micronesia', 'Moldova', 'Monaco', 'Mongolia', 'Montenegro', 'Montserrat', 'Morocco', 'Mozambique', 
                'Myanmar', 'Namibia', 'Nauru', 'Nepal', 'Netherlands', 'NewCaledonia', 'NewZealand', 'Nicaragua', 'Niger', 'Nigeria', 'Niue', 'NorfolkIsland', 
                'NorthKorea', 'NorthernMarianaIslands', 'Norway', 'Oman', 'Pakistan', 'Palau', 'Palestine', 'Panama', 'PapuaNewGuinea', 'Paraguay', 
                'Peru', 'Philippines', 'Pitcairn', 'Poland', 'Portugal', 'PuertoRico', 'Qatar', 'Reunion', 'Romania', 'RussianFederation', 'Rwanda', 
                'SaintBarthelemy', 'SaintHelena', 'SaintKitts', 'SaintLucia', 'SaintMartin', 'SaintPierre', 'SaintVincent', 'Samoa', 'SanMarino', 
                'SaoTome', 'SaudiArabia', 'Senegal', 'Serbia', 'Seychelles', 'SierraLeone', 'Singapore', 'SintMaartenDutch', 'Slovakia', 'Slovenia', 
                'SolomonIslands', 'Somalia', 'SouthAfrica', 'SouthKorea', 'SouthSudan', 'Spain', 'SriLanka', 'Sudan', 'Suriname', 'Svalbard', 'Swaziland', 
                'Sweden', 'Switzerland', 'Syria', 'Taiwan', 'Tajikistan', 'Tanzania', 'Thailand', 'TimorLeste', 'Togo', 'Tokelau', 'Tonga', 'Trinidad', 
                'Tunisia', 'Turkey', 'Turkmenistan', 'TurksCaicos', 'Tuvalu', 'Uganda', 'Ukraine', 'UnitedArabEmirates', 'UnitedKingdom', 'UnitedStates', 
                'Uruguay', 'Uzbekistan', 'Vanuatu', 'Vatican', 'Venezuela', 'VietNam', 'VirginIslands', 'VirginIslandsGB', 'WallisFutuna', 'WesternSahara', 
                'Yemen', 'Zambia', 'Zimbabwe'));
       ALTER TABLE city ADD CONSTRAINT city_province_e_ck 
            CHECK ((province) IN ('Alabama', 'Alaska', 'Alberta', 'AmericanSamoa', 'Arizona', 'Arkansas', 'BritishColumbia', 'California', 'Colorado', 
                'Connecticut', 'Delaware', 'DistrictOfColumbia', 'Florida', 'Georgia', 'Guam', 'Hawaii', 'Idaho', 'Illinois', 'Indiana', 'Iowa', 'Kansas', 
                'Kentucky', 'Louisiana', 'Maine', 'Manitoba', 'Maryland', 'Massachusetts', 'Michigan', 'Minnesota', 'MinorOutlyingIslands', 'Mississippi', 
                'Missouri', 'Montana', 'Nebraska', 'Nevada', 'NewBrunswick', 'NewHampshire', 'NewJersey', 'NewMexico', 'NewYork', 'Newfoundland', 
                'NorthCarolina', 'NorthDakota', 'NorthernMarianaIslands', 'NorthwestTerritories', 'NovaScotia', 'Nunavut', 'Ohio', 'Oklahoma', 'Ontario', 
                'Oregon', 'Pennsylvania', 'PrinceEdwardIsland', 'PuertoRico', 'Quebec', 'RhodeIsland', 'Saskatchewan', 'SouthCarolina', 'SouthDakota', 
                'Tennessee', 'Texas', 'Utah', 'Vermont', 'VirginIslands', 'Virginia', 'Washington', 'WestVirginia', 'Wisconsin', 'Wyoming', 'YukonTerritory'));
        ALTER TABLE city_intro_page ADD CONSTRAINT city_intro_page_province_e_ck 
            CHECK ((province) IN ('Alabama', 'Alaska', 'Alberta', 'AmericanSamoa', 'Arizona', 'Arkansas', 'BritishColumbia', 'California', 'Colorado', 
            'Connecticut', 'Delaware', 'DistrictOfColumbia', 'Florida', 'Georgia', 'Guam', 'Hawaii', 'Idaho', 'Illinois', 'Indiana', 'Iowa', 'Kansas', 
            'Kentucky', 'Louisiana', 'Maine', 'Manitoba', 'Maryland', 'Massachusetts', 'Michigan', 'Minnesota', 'MinorOutlyingIslands', 'Mississippi', 
            'Missouri', 'Montana', 'Nebraska', 'Nevada', 'NewBrunswick', 'NewHampshire', 'NewJersey', 'NewMexico', 'NewYork', 'Newfoundland',
            'NorthCarolina', 'NorthDakota', 'NorthernMarianaIslands', 'NorthwestTerritories', 'NovaScotia', 'Nunavut', 'Ohio', 'Oklahoma', 'Ontario', 
            'Oregon', 'Pennsylvania', 'PrinceEdwardIsland', 'PuertoRico', 'Quebec', 'RhodeIsland', 'Saskatchewan', 'SouthCarolina', 'SouthDakota', 
            'Tennessee', 'Texas', 'Utah', 'Vermont', 'VirginIslands', 'Virginia', 'Washington', 'WestVirginia', 'Wisconsin', 'Wyoming', 'YukonTerritory'));
        ALTER TABLE communication_delivery_handle ADD CONSTRAINT communication_delivery_handle_recipient_discriminator_d_ck 
            CHECK ((recipient_discriminator) IN ('Employee', 'Guarantor', 'SystemEndpoint', 'Tenant'));
        ALTER TABLE communication_message_category ADD CONSTRAINT communication_message_category_category_type_e_ck 
            CHECK ((category_type) IN ('IVR', 'Message', 'Notification', 'SMS', 'Ticket'));
        ALTER TABLE communication_message ADD CONSTRAINT communication_message_sender_discriminator_d_ck 
            CHECK ((sender_discriminator) IN ('Employee', 'Guarantor', 'SystemEndpoint', 'Tenant'));
        ALTER TABLE communication_message_category ADD CONSTRAINT communication_message_category_ticket_type_e_ck 
            CHECK ((ticket_type) IN ('Landlord', 'NotTicket', 'Tenant', 'Vendor'));
        ALTER TABLE communication_thread ADD CONSTRAINT communication_thread_owner_discriminator_d_ck 
            CHECK ((owner_discriminator) IN ('Employee', 'Guarantor', 'SystemEndpoint', 'Tenant'));
        ALTER TABLE communication_thread ADD CONSTRAINT communication_thread_status_e_ck CHECK ((status) IN ('Open', 'Resolved'));
        ALTER TABLE communication_thread_policy_handle ADD CONSTRAINT communication_thread_policy_handle_policy_consumer_discr_d_ck 
            CHECK ((policy_consumer_discriminator) IN ('Employee', 'Guarantor', 'SystemEndpoint', 'Tenant'));
        ALTER TABLE country_policy_node ADD CONSTRAINT country_policy_node_country_e_ck 
            CHECK ((country) IN ('Afghanistan', 'AlandIslands', 'Albania', 'Algeria', 'AmericanSamoa', 'Andorra', 'Angola', 'Anguilla', 'Antarctica', 
            'Antigua', 'Argentina', 'Armenia', 'Aruba', 'Australia', 'Austria', 'Azerbaijan', 'Bahamas', 'Bahrain', 'Bangladesh', 'Barbados', 'Belarus', 
            'Belgium', 'Belize', 'Benin', 'Bermuda', 'Bhutan', 'Bolivia', 'Bonaire', 'BosniaHerzegovina', 'Botswana', 'BouvetIsland', 'Brazil', 
            'BruneiDarussalam', 'Bulgaria', 'BurkinaFaso', 'Burundi', 'CaboVerde', 'Cambodia', 'Cameroon', 'Canada', 'CaymanIslands', 
            'CentralAfricanRepublic', 'Chad', 'Chile', 'China', 'ChristmasIsland', 'CocosIslands', 'Colombia', 'Comoros', 'Congo', 'CookIslands', 
            'CostaRica', 'Croatia', 'Cuba', 'Curacao', 'Cyprus', 'CzechRepublic', 'Denmark', 'Djibouti', 'Dominica', 'DominicanRepublic', 'Ecuador', 
            'Egypt', 'ElSalvador', 'EquatorialGuinea', 'Eritrea', 'Estonia', 'Ethiopia', 'FalklandIslands', 'FaroeIslands', 'Fiji', 'Finland', 'France', 
            'FrenchGuiana', 'FrenchPolynesia', 'FrenchTerritories', 'Gabon', 'Gambia', 'Georgia', 'Germany', 'Ghana', 'Gibraltar', 'Greece', 'Greenland', 
            'Grenada', 'Guadeloupe', 'Guam', 'Guatemala', 'Guernsey', 'Guinea', 'GuineaBissau', 'Guyana', 'Haiti', 'HeardIslands', 'Honduras', 'HongKong', 
            'Hungary', 'Iceland', 'India', 'Indonesia', 'Iran', 'Iraq', 'Ireland', 'IsleOfMan', 'Israel', 'Italy', 'Jamaica', 'Japan', 'Jersey', 'Jordan', 
            'Kazakhstan', 'Kenya', 'Kiribati', 'Kuwait', 'Kyrgyzstan', 'LaoRepublic', 'Latvia', 'Lebanon', 'Lesotho', 'Liberia', 'Libya', 'Liechtenstein', 
            'Lithuania', 'Luxembourg', 'Macao', 'Macedonia', 'Madagascar', 'Malawi', 'Malaysia', 'Maldives', 'Mali', 'Malta', 'MarshallIslands', 
            'Martinique', 'Mauritania', 'Mauritius', 'Mayotte', 'Mexico', 'Micronesia', 'Moldova', 'Monaco', 'Mongolia', 'Montenegro', 'Montserrat', 
            'Morocco', 'Mozambique', 'Myanmar', 'Namibia', 'Nauru', 'Nepal', 'Netherlands', 'NewCaledonia', 'NewZealand', 'Nicaragua', 'Niger', 'Nigeria', 
            'Niue', 'NorfolkIsland', 'NorthKorea', 'NorthernMarianaIslands', 'Norway', 'Oman', 'Pakistan', 'Palau', 'Palestine', 'Panama', 'PapuaNewGuinea', 
            'Paraguay', 'Peru', 'Philippines', 'Pitcairn', 'Poland', 'Portugal', 'PuertoRico', 'Qatar', 'Reunion', 'Romania', 'RussianFederation', 'Rwanda', 
            'SaintBarthelemy', 'SaintHelena', 'SaintKitts', 'SaintLucia', 'SaintMartin', 'SaintPierre', 'SaintVincent', 'Samoa', 'SanMarino', 'SaoTome', 
            'SaudiArabia', 'Senegal', 'Serbia', 'Seychelles', 'SierraLeone', 'Singapore', 'SintMaartenDutch', 'Slovakia', 'Slovenia', 'SolomonIslands', 
            'Somalia', 'SouthAfrica', 'SouthKorea', 'SouthSudan', 'Spain', 'SriLanka', 'Sudan', 'Suriname', 'Svalbard', 'Swaziland', 'Sweden', 'Switzerland', 
            'Syria', 'Taiwan', 'Tajikistan', 'Tanzania', 'Thailand', 'TimorLeste', 'Togo', 'Tokelau', 'Tonga', 'Trinidad', 'Tunisia', 'Turkey', 'Turkmenistan', 
            'TurksCaicos', 'Tuvalu', 'Uganda', 'Ukraine', 'UnitedArabEmirates', 'UnitedKingdom', 'UnitedStates', 'Uruguay', 'Uzbekistan', 'Vanuatu', 'Vatican', 
            'Venezuela', 'VietNam', 'VirginIslands', 'VirginIslandsGB', 'WallisFutuna', 'WesternSahara', 'Yemen', 'Zambia', 'Zimbabwe'));
        ALTER TABLE customer_screening_income_info ADD CONSTRAINT customer_screening_income_info_address_country_e_ck 
            CHECK ((address_country) IN ('Afghanistan', 'AlandIslands', 'Albania', 'Algeria', 'AmericanSamoa', 'Andorra', 'Angola', 'Anguilla', 'Antarctica', 
            'Antigua', 'Argentina', 'Armenia', 'Aruba', 'Australia', 'Austria', 'Azerbaijan', 'Bahamas', 'Bahrain', 'Bangladesh', 'Barbados', 'Belarus', 
            'Belgium', 'Belize', 'Benin', 'Bermuda', 'Bhutan', 'Bolivia', 'Bonaire', 'BosniaHerzegovina', 'Botswana', 'BouvetIsland', 'Brazil', 'BruneiDarussalam', 
            'Bulgaria', 'BurkinaFaso', 'Burundi', 'CaboVerde', 'Cambodia', 'Cameroon', 'Canada', 'CaymanIslands', 'CentralAfricanRepublic', 'Chad', 'Chile', 
            'China', 'ChristmasIsland', 'CocosIslands', 'Colombia', 'Comoros', 'Congo', 'CookIslands', 'CostaRica', 'Croatia', 'Cuba', 'Curacao', 'Cyprus', 
            'CzechRepublic', 'Denmark', 'Djibouti', 'Dominica', 'DominicanRepublic', 'Ecuador', 'Egypt', 'ElSalvador', 'EquatorialGuinea', 'Eritrea', 'Estonia', 
            'Ethiopia', 'FalklandIslands', 'FaroeIslands', 'Fiji', 'Finland', 'France', 'FrenchGuiana', 'FrenchPolynesia', 'FrenchTerritories', 'Gabon', 'Gambia', 
            'Georgia', 'Germany', 'Ghana', 'Gibraltar', 'Greece', 'Greenland', 'Grenada', 'Guadeloupe', 'Guam', 'Guatemala', 'Guernsey', 'Guinea', 'GuineaBissau', 
            'Guyana', 'Haiti', 'HeardIslands', 'Honduras', 'HongKong', 'Hungary', 'Iceland', 'India', 'Indonesia', 'Iran', 'Iraq', 'Ireland', 'IsleOfMan', 'Israel', 
            'Italy', 'Jamaica', 'Japan', 'Jersey', 'Jordan', 'Kazakhstan', 'Kenya', 'Kiribati', 'Kuwait', 'Kyrgyzstan', 'LaoRepublic', 'Latvia', 'Lebanon', 'Lesotho', 
            'Liberia', 'Libya', 'Liechtenstein', 'Lithuania', 'Luxembourg', 'Macao', 'Macedonia', 'Madagascar', 'Malawi', 'Malaysia', 'Maldives', 'Mali', 'Malta', 
            'MarshallIslands', 'Martinique', 'Mauritania', 'Mauritius', 'Mayotte', 'Mexico', 'Micronesia', 'Moldova', 'Monaco', 'Mongolia', 'Montenegro', 'Montserrat', 
            'Morocco', 'Mozambique', 'Myanmar', 'Namibia', 'Nauru', 'Nepal', 'Netherlands', 'NewCaledonia', 'NewZealand', 'Nicaragua', 'Niger', 'Nigeria', 'Niue', 
            'NorfolkIsland', 'NorthKorea', 'NorthernMarianaIslands', 'Norway', 'Oman', 'Pakistan', 'Palau', 'Palestine', 'Panama', 'PapuaNewGuinea', 'Paraguay', 
            'Peru', 'Philippines', 'Pitcairn', 'Poland', 'Portugal', 'PuertoRico', 'Qatar', 'Reunion', 'Romania', 'RussianFederation', 'Rwanda', 'SaintBarthelemy', 
            'SaintHelena', 'SaintKitts', 'SaintLucia', 'SaintMartin', 'SaintPierre', 'SaintVincent', 'Samoa', 'SanMarino', 'SaoTome', 'SaudiArabia', 'Senegal', 'Serbia', 
            'Seychelles', 'SierraLeone', 'Singapore', 'SintMaartenDutch', 'Slovakia', 'Slovenia', 'SolomonIslands', 'Somalia', 'SouthAfrica', 'SouthKorea', 'SouthSudan', 
            'Spain', 'SriLanka', 'Sudan', 'Suriname', 'Svalbard', 'Swaziland', 'Sweden', 'Switzerland', 'Syria', 'Taiwan', 'Tajikistan', 'Tanzania', 'Thailand', 
            'TimorLeste', 'Togo', 'Tokelau', 'Tonga', 'Trinidad', 'Tunisia', 'Turkey', 'Turkmenistan', 'TurksCaicos', 'Tuvalu', 'Uganda', 'Ukraine', 'UnitedArabEmirates', 
            'UnitedKingdom', 'UnitedStates', 'Uruguay', 'Uzbekistan', 'Vanuatu', 'Vatican', 'Venezuela', 'VietNam', 'VirginIslands', 'VirginIslandsGB', 'WallisFutuna', 
            'WesternSahara', 'Yemen', 'Zambia', 'Zimbabwe'));
        ALTER TABLE customer_screening_v ADD CONSTRAINT customer_screening_v_current_address_country_e_ck 
            CHECK ((current_address_country) IN ('Afghanistan', 'AlandIslands', 'Albania', 'Algeria', 'AmericanSamoa', 'Andorra', 'Angola', 'Anguilla', 'Antarctica', 
            'Antigua', 'Argentina', 'Armenia', 'Aruba', 'Australia', 'Austria', 'Azerbaijan', 'Bahamas', 'Bahrain', 'Bangladesh', 'Barbados', 'Belarus', 'Belgium', 
            'Belize', 'Benin', 'Bermuda', 'Bhutan', 'Bolivia', 'Bonaire', 'BosniaHerzegovina', 'Botswana', 'BouvetIsland', 'Brazil', 'BruneiDarussalam', 'Bulgaria', 
            'BurkinaFaso', 'Burundi', 'CaboVerde', 'Cambodia', 'Cameroon', 'Canada', 'CaymanIslands', 'CentralAfricanRepublic', 'Chad', 'Chile', 'China', 'ChristmasIsland', 
            'CocosIslands', 'Colombia', 'Comoros', 'Congo', 'CookIslands', 'CostaRica', 'Croatia', 'Cuba', 'Curacao', 'Cyprus', 'CzechRepublic', 'Denmark', 'Djibouti', 
            'Dominica', 'DominicanRepublic', 'Ecuador', 'Egypt', 'ElSalvador', 'EquatorialGuinea', 'Eritrea', 'Estonia', 'Ethiopia', 'FalklandIslands', 'FaroeIslands', 
            'Fiji', 'Finland', 'France', 'FrenchGuiana', 'FrenchPolynesia', 'FrenchTerritories', 'Gabon', 'Gambia', 'Georgia', 'Germany', 'Ghana', 'Gibraltar', 'Greece', 
            'Greenland', 'Grenada', 'Guadeloupe', 'Guam', 'Guatemala', 'Guernsey', 'Guinea', 'GuineaBissau', 'Guyana', 'Haiti', 'HeardIslands', 'Honduras', 'HongKong', 
            'Hungary', 'Iceland', 'India', 'Indonesia', 'Iran', 'Iraq', 'Ireland', 'IsleOfMan', 'Israel', 'Italy', 'Jamaica', 'Japan', 'Jersey', 'Jordan', 'Kazakhstan', 
            'Kenya', 'Kiribati', 'Kuwait', 'Kyrgyzstan', 'LaoRepublic', 'Latvia', 'Lebanon', 'Lesotho', 'Liberia', 'Libya', 'Liechtenstein', 'Lithuania', 'Luxembourg', 
            'Macao', 'Macedonia', 'Madagascar', 'Malawi', 'Malaysia', 'Maldives', 'Mali', 'Malta', 'MarshallIslands', 'Martinique', 'Mauritania', 'Mauritius', 'Mayotte', 
            'Mexico', 'Micronesia', 'Moldova', 'Monaco', 'Mongolia', 'Montenegro', 'Montserrat', 'Morocco', 'Mozambique', 'Myanmar', 'Namibia', 'Nauru', 'Nepal', 
            'Netherlands', 'NewCaledonia', 'NewZealand', 'Nicaragua', 'Niger', 'Nigeria', 'Niue', 'NorfolkIsland', 'NorthKorea', 'NorthernMarianaIslands', 'Norway', 'Oman', 
            'Pakistan', 'Palau', 'Palestine', 'Panama', 'PapuaNewGuinea', 'Paraguay', 'Peru', 'Philippines', 'Pitcairn', 'Poland', 'Portugal', 'PuertoRico', 'Qatar', 
            'Reunion', 'Romania', 'RussianFederation', 'Rwanda', 'SaintBarthelemy', 'SaintHelena', 'SaintKitts', 'SaintLucia', 'SaintMartin', 'SaintPierre', 'SaintVincent', 
            'Samoa', 'SanMarino', 'SaoTome', 'SaudiArabia', 'Senegal', 'Serbia', 'Seychelles', 'SierraLeone', 'Singapore', 'SintMaartenDutch', 'Slovakia', 'Slovenia', 
            'SolomonIslands', 'Somalia', 'SouthAfrica', 'SouthKorea', 'SouthSudan', 'Spain', 'SriLanka', 'Sudan', 'Suriname', 'Svalbard', 'Swaziland', 'Sweden', 
            'Switzerland', 'Syria', 'Taiwan', 'Tajikistan', 'Tanzania', 'Thailand', 'TimorLeste', 'Togo', 'Tokelau', 'Tonga', 'Trinidad', 'Tunisia', 'Turkey', 
            'Turkmenistan', 'TurksCaicos', 'Tuvalu', 'Uganda', 'Ukraine', 'UnitedArabEmirates', 'UnitedKingdom', 'UnitedStates', 'Uruguay', 'Uzbekistan', 'Vanuatu', 
            'Vatican', 'Venezuela', 'VietNam', 'VirginIslands', 'VirginIslandsGB', 'WallisFutuna', 'WesternSahara', 'Yemen', 'Zambia', 'Zimbabwe'));
        ALTER TABLE customer_screening_v ADD CONSTRAINT customer_screening_v_previous_address_country_e_ck 
            CHECK ((previous_address_country) IN ('Afghanistan', 'AlandIslands', 'Albania', 'Algeria', 'AmericanSamoa', 'Andorra', 'Angola', 'Anguilla', 'Antarctica', 
            'Antigua', 'Argentina', 'Armenia', 'Aruba', 'Australia', 'Austria', 'Azerbaijan', 'Bahamas', 'Bahrain', 'Bangladesh', 'Barbados', 'Belarus', 'Belgium', 
            'Belize', 'Benin', 'Bermuda', 'Bhutan', 'Bolivia', 'Bonaire', 'BosniaHerzegovina', 'Botswana', 'BouvetIsland', 'Brazil', 'BruneiDarussalam', 'Bulgaria', 
            'BurkinaFaso', 'Burundi', 'CaboVerde', 'Cambodia', 'Cameroon', 'Canada', 'CaymanIslands', 'CentralAfricanRepublic', 'Chad', 'Chile', 'China', 
            'ChristmasIsland', 'CocosIslands', 'Colombia', 'Comoros', 'Congo', 'CookIslands', 'CostaRica', 'Croatia', 'Cuba', 'Curacao', 'Cyprus', 'CzechRepublic', 
            'Denmark', 'Djibouti', 'Dominica', 'DominicanRepublic', 'Ecuador', 'Egypt', 'ElSalvador', 'EquatorialGuinea', 'Eritrea', 'Estonia', 'Ethiopia', 'FalklandIslands', 
            'FaroeIslands', 'Fiji', 'Finland', 'France', 'FrenchGuiana', 'FrenchPolynesia', 'FrenchTerritories', 'Gabon', 'Gambia', 'Georgia', 'Germany', 'Ghana', 
            'Gibraltar', 'Greece', 'Greenland', 'Grenada', 'Guadeloupe', 'Guam', 'Guatemala', 'Guernsey', 'Guinea', 'GuineaBissau', 'Guyana', 'Haiti', 'HeardIslands', 
            'Honduras', 'HongKong', 'Hungary', 'Iceland', 'India', 'Indonesia', 'Iran', 'Iraq', 'Ireland', 'IsleOfMan', 'Israel', 'Italy', 'Jamaica', 'Japan', 'Jersey', 
            'Jordan', 'Kazakhstan', 'Kenya', 'Kiribati', 'Kuwait', 'Kyrgyzstan', 'LaoRepublic', 'Latvia', 'Lebanon', 'Lesotho', 'Liberia', 'Libya', 'Liechtenstein', 
            'Lithuania', 'Luxembourg', 'Macao', 'Macedonia', 'Madagascar', 'Malawi', 'Malaysia', 'Maldives', 'Mali', 'Malta', 'MarshallIslands', 'Martinique', 
            'Mauritania', 'Mauritius', 'Mayotte', 'Mexico', 'Micronesia', 'Moldova', 'Monaco', 'Mongolia', 'Montenegro', 'Montserrat', 'Morocco', 'Mozambique', 
            'Myanmar', 'Namibia', 'Nauru', 'Nepal', 'Netherlands', 'NewCaledonia', 'NewZealand', 'Nicaragua', 'Niger', 'Nigeria', 'Niue', 'NorfolkIsland', 'NorthKorea', 
            'NorthernMarianaIslands', 'Norway', 'Oman', 'Pakistan', 'Palau', 'Palestine', 'Panama', 'PapuaNewGuinea', 'Paraguay', 'Peru', 'Philippines', 'Pitcairn', 
            'Poland', 'Portugal', 'PuertoRico', 'Qatar', 'Reunion', 'Romania', 'RussianFederation', 'Rwanda', 'SaintBarthelemy', 'SaintHelena', 'SaintKitts', 
            'SaintLucia', 'SaintMartin', 'SaintPierre', 'SaintVincent', 'Samoa', 'SanMarino', 'SaoTome', 'SaudiArabia', 'Senegal', 'Serbia', 'Seychelles', 'SierraLeone', 
            'Singapore', 'SintMaartenDutch', 'Slovakia', 'Slovenia', 'SolomonIslands', 'Somalia', 'SouthAfrica', 'SouthKorea', 'SouthSudan', 'Spain', 'SriLanka', 
            'Sudan', 'Suriname', 'Svalbard', 'Swaziland', 'Sweden', 'Switzerland', 'Syria', 'Taiwan', 'Tajikistan', 'Tanzania', 'Thailand', 'TimorLeste', 'Togo', 
            'Tokelau', 'Tonga', 'Trinidad', 'Tunisia', 'Turkey', 'Turkmenistan', 'TurksCaicos', 'Tuvalu', 'Uganda', 'Ukraine', 'UnitedArabEmirates', 'UnitedKingdom', 
            'UnitedStates', 'Uruguay', 'Uzbekistan', 'Vanuatu', 'Vatican', 'Venezuela', 'VietNam', 'VirginIslands', 'VirginIslandsGB', 'WallisFutuna', 'WesternSahara', 
            'Yemen', 'Zambia', 'Zimbabwe'));
        ALTER TABLE email_template ADD CONSTRAINT email_template_template_type_e_ck 
            CHECK ((template_type) IN ('ApplicationApproved', 'ApplicationCreatedApplicant', 'ApplicationCreatedCoApplicant', 'ApplicationCreatedGuarantor', 
            'ApplicationDeclined', 'AutoPayCancellation', 'AutoPayChanges', 'AutoPaySetupConfirmation', 'DirectDebitAccountChanged', 'MaintenanceRequestCancelled', 
            'MaintenanceRequestCompleted', 'MaintenanceRequestCreatedPMC', 'MaintenanceRequestCreatedTenant', 'MaintenanceRequestEntryNotice', 'MaintenanceRequestUpdated', 
            'OneTimePaymentSubmitted', 'PasswordRetrievalCrm', 'PasswordRetrievalProspect', 'PasswordRetrievalTenant', 'PaymentReceipt', 'PaymentReceiptWithWebPaymentFee', 
            'PaymentReturned', 'ProspectWelcome', 'TenantInvitation'));
        ALTER TABLE emergency_contact ADD CONSTRAINT emergency_contact_address_country_e_ck 
            CHECK ((address_country) IN ('Afghanistan', 'AlandIslands', 'Albania', 'Algeria', 'AmericanSamoa', 'Andorra', 'Angola', 'Anguilla', 'Antarctica', 
            'Antigua', 'Argentina', 'Armenia', 'Aruba', 'Australia', 'Austria', 'Azerbaijan', 'Bahamas', 'Bahrain', 'Bangladesh', 'Barbados', 'Belarus', 'Belgium', 
            'Belize', 'Benin', 'Bermuda', 'Bhutan', 'Bolivia', 'Bonaire', 'BosniaHerzegovina', 'Botswana', 'BouvetIsland', 'Brazil', 'BruneiDarussalam', 'Bulgaria', 
            'BurkinaFaso', 'Burundi', 'CaboVerde', 'Cambodia', 'Cameroon', 'Canada', 'CaymanIslands', 'CentralAfricanRepublic', 'Chad', 'Chile', 'China', 
            'ChristmasIsland', 'CocosIslands', 'Colombia', 'Comoros', 'Congo', 'CookIslands', 'CostaRica', 'Croatia', 'Cuba', 'Curacao', 'Cyprus', 'CzechRepublic', 
            'Denmark', 'Djibouti', 'Dominica', 'DominicanRepublic', 'Ecuador', 'Egypt', 'ElSalvador', 'EquatorialGuinea', 'Eritrea', 'Estonia', 'Ethiopia', 'FalklandIslands', 
            'FaroeIslands', 'Fiji', 'Finland', 'France', 'FrenchGuiana', 'FrenchPolynesia', 'FrenchTerritories', 'Gabon', 'Gambia', 'Georgia', 'Germany', 'Ghana', 
            'Gibraltar', 'Greece', 'Greenland', 'Grenada', 'Guadeloupe', 'Guam', 'Guatemala', 'Guernsey', 'Guinea', 'GuineaBissau', 'Guyana', 'Haiti', 'HeardIslands', 
            'Honduras', 'HongKong', 'Hungary', 'Iceland', 'India', 'Indonesia', 'Iran', 'Iraq', 'Ireland', 'IsleOfMan', 'Israel', 'Italy', 'Jamaica', 'Japan', 'Jersey', 
            'Jordan', 'Kazakhstan', 'Kenya', 'Kiribati', 'Kuwait', 'Kyrgyzstan', 'LaoRepublic', 'Latvia', 'Lebanon', 'Lesotho', 'Liberia', 'Libya', 'Liechtenstein', 
            'Lithuania', 'Luxembourg', 'Macao', 'Macedonia', 'Madagascar', 'Malawi', 'Malaysia', 'Maldives', 'Mali', 'Malta', 'MarshallIslands', 'Martinique', 'Mauritania', 
            'Mauritius', 'Mayotte', 'Mexico', 'Micronesia', 'Moldova', 'Monaco', 'Mongolia', 'Montenegro', 'Montserrat', 'Morocco', 'Mozambique', 'Myanmar', 'Namibia', 
            'Nauru', 'Nepal', 'Netherlands', 'NewCaledonia', 'NewZealand', 'Nicaragua', 'Niger', 'Nigeria', 'Niue', 'NorfolkIsland', 'NorthKorea', 'NorthernMarianaIslands', 
            'Norway', 'Oman', 'Pakistan', 'Palau', 'Palestine', 'Panama', 'PapuaNewGuinea', 'Paraguay', 'Peru', 'Philippines', 'Pitcairn', 'Poland', 'Portugal', 
            'PuertoRico', 'Qatar', 'Reunion', 'Romania', 'RussianFederation', 'Rwanda', 'SaintBarthelemy', 'SaintHelena', 'SaintKitts', 'SaintLucia', 'SaintMartin', 
            'SaintPierre', 'SaintVincent', 'Samoa', 'SanMarino', 'SaoTome', 'SaudiArabia', 'Senegal', 'Serbia', 'Seychelles', 'SierraLeone', 'Singapore', 'SintMaartenDutch', 
            'Slovakia', 'Slovenia', 'SolomonIslands', 'Somalia', 'SouthAfrica', 'SouthKorea', 'SouthSudan', 'Spain', 'SriLanka', 'Sudan', 'Suriname', 'Svalbard', 
            'Swaziland', 'Sweden', 'Switzerland', 'Syria', 'Taiwan', 'Tajikistan', 'Tanzania', 'Thailand', 'TimorLeste', 'Togo', 'Tokelau', 'Tonga', 'Trinidad', 'Tunisia', 
            'Turkey', 'Turkmenistan', 'TurksCaicos', 'Tuvalu', 'Uganda', 'Ukraine', 'UnitedArabEmirates', 'UnitedKingdom', 'UnitedStates', 'Uruguay', 'Uzbekistan', 'Vanuatu', 
            'Vatican', 'Venezuela', 'VietNam', 'VirginIslands', 'VirginIslandsGB', 'WallisFutuna', 'WesternSahara', 'Yemen', 'Zambia', 'Zimbabwe'));
        ALTER TABLE landlord ADD CONSTRAINT landlord_address_country_e_ck 
            CHECK ((address_country) IN ('Afghanistan', 'AlandIslands', 'Albania', 'Algeria', 'AmericanSamoa', 'Andorra', 'Angola', 'Anguilla', 'Antarctica', 
            'Antigua', 'Argentina', 'Armenia', 'Aruba', 'Australia', 'Austria', 'Azerbaijan', 'Bahamas', 'Bahrain', 'Bangladesh', 'Barbados', 'Belarus', 
            'Belgium', 'Belize', 'Benin', 'Bermuda', 'Bhutan', 'Bolivia', 'Bonaire', 'BosniaHerzegovina', 'Botswana', 'BouvetIsland', 'Brazil', 'BruneiDarussalam', 
            'Bulgaria', 'BurkinaFaso', 'Burundi', 'CaboVerde', 'Cambodia', 'Cameroon', 'Canada', 'CaymanIslands', 'CentralAfricanRepublic', 'Chad', 'Chile', 'China', 
            'ChristmasIsland', 'CocosIslands', 'Colombia', 'Comoros', 'Congo', 'CookIslands', 'CostaRica', 'Croatia', 'Cuba', 'Curacao', 'Cyprus', 'CzechRepublic', 
            'Denmark', 'Djibouti', 'Dominica', 'DominicanRepublic', 'Ecuador', 'Egypt', 'ElSalvador', 'EquatorialGuinea', 'Eritrea', 'Estonia', 'Ethiopia', 'FalklandIslands', 
            'FaroeIslands', 'Fiji', 'Finland', 'France', 'FrenchGuiana', 'FrenchPolynesia', 'FrenchTerritories', 'Gabon', 'Gambia', 'Georgia', 'Germany', 'Ghana', 'Gibraltar', 
            'Greece', 'Greenland', 'Grenada', 'Guadeloupe', 'Guam', 'Guatemala', 'Guernsey', 'Guinea', 'GuineaBissau', 'Guyana', 'Haiti', 'HeardIslands', 'Honduras', 
            'HongKong', 'Hungary', 'Iceland', 'India', 'Indonesia', 'Iran', 'Iraq', 'Ireland', 'IsleOfMan', 'Israel', 'Italy', 'Jamaica', 'Japan', 'Jersey', 'Jordan', 
            'Kazakhstan', 'Kenya', 'Kiribati', 'Kuwait', 'Kyrgyzstan', 'LaoRepublic', 'Latvia', 'Lebanon', 'Lesotho', 'Liberia', 'Libya', 'Liechtenstein', 'Lithuania', 
            'Luxembourg', 'Macao', 'Macedonia', 'Madagascar', 'Malawi', 'Malaysia', 'Maldives', 'Mali', 'Malta', 'MarshallIslands', 'Martinique', 'Mauritania', 'Mauritius', 
            'Mayotte', 'Mexico', 'Micronesia', 'Moldova', 'Monaco', 'Mongolia', 'Montenegro', 'Montserrat', 'Morocco', 'Mozambique', 'Myanmar', 'Namibia', 'Nauru', 
            'Nepal', 'Netherlands', 'NewCaledonia', 'NewZealand', 'Nicaragua', 'Niger', 'Nigeria', 'Niue', 'NorfolkIsland', 'NorthKorea', 'NorthernMarianaIslands', 
            'Norway', 'Oman', 'Pakistan', 'Palau', 'Palestine', 'Panama', 'PapuaNewGuinea', 'Paraguay', 'Peru', 'Philippines', 'Pitcairn', 'Poland', 'Portugal', 
            'PuertoRico', 'Qatar', 'Reunion', 'Romania', 'RussianFederation', 'Rwanda', 'SaintBarthelemy', 'SaintHelena', 'SaintKitts', 'SaintLucia', 'SaintMartin', 
            'SaintPierre', 'SaintVincent', 'Samoa', 'SanMarino', 'SaoTome', 'SaudiArabia', 'Senegal', 'Serbia', 'Seychelles', 'SierraLeone', 'Singapore', 
            'SintMaartenDutch', 'Slovakia', 'Slovenia', 'SolomonIslands', 'Somalia', 'SouthAfrica', 'SouthKorea', 'SouthSudan', 'Spain', 'SriLanka', 'Sudan', 
            'Suriname', 'Svalbard', 'Swaziland', 'Sweden', 'Switzerland', 'Syria', 'Taiwan', 'Tajikistan', 'Tanzania', 'Thailand', 'TimorLeste', 'Togo', 'Tokelau', 
            'Tonga', 'Trinidad', 'Tunisia', 'Turkey', 'Turkmenistan', 'TurksCaicos', 'Tuvalu', 'Uganda', 'Ukraine', 'UnitedArabEmirates', 'UnitedKingdom', 
            'UnitedStates', 'Uruguay', 'Uzbekistan', 'Vanuatu', 'Vatican', 'Venezuela', 'VietNam', 'VirginIslands', 'VirginIslandsGB', 'WallisFutuna', 
            'WesternSahara', 'Yemen', 'Zambia', 'Zimbabwe'));
        ALTER TABLE lease_adjustment ADD CONSTRAINT lease_adjustment_tax_type_e_ck CHECK ((tax_type) IN ('Monetary', 'Percentage'));
        ALTER TABLE lease_application ADD CONSTRAINT lease_application_status_e_ck 
            CHECK ((status) IN ('Approved', 'Cancelled', 'Declined', 'InProgress', 'PendingDecision', 'PendingFurtherInformation', 'Submitted'));
        ALTER TABLE legal_letter ADD CONSTRAINT legal_letter_status_discriminator_d_ck CHECK ((status_discriminator) IN ('LegalStatus', 'LegalStatusN4'));
        ALTER TABLE legal_status ADD CONSTRAINT legal_status_id_discriminator_ck CHECK ((id_discriminator) IN ('LegalStatus', 'LegalStatusN4'));
        ALTER TABLE marketing ADD CONSTRAINT marketing_marketing_address_country_e_ck 
            CHECK ((marketing_address_country) IN ('Afghanistan', 'AlandIslands', 'Albania', 'Algeria', 'AmericanSamoa', 'Andorra', 'Angola', 'Anguilla', 'Antarctica', 
            'Antigua', 'Argentina', 'Armenia', 'Aruba', 'Australia', 'Austria', 'Azerbaijan', 'Bahamas', 'Bahrain', 'Bangladesh', 'Barbados', 'Belarus', 
            'Belgium', 'Belize', 'Benin', 'Bermuda', 'Bhutan', 'Bolivia', 'Bonaire', 'BosniaHerzegovina', 'Botswana', 'BouvetIsland', 'Brazil', 
            'BruneiDarussalam', 'Bulgaria', 'BurkinaFaso', 'Burundi', 'CaboVerde', 'Cambodia', 'Cameroon', 'Canada', 'CaymanIslands', 'CentralAfricanRepublic', 
            'Chad', 'Chile', 'China', 'ChristmasIsland', 'CocosIslands', 'Colombia', 'Comoros', 'Congo', 'CookIslands', 'CostaRica', 'Croatia', 'Cuba', 'Curacao', 
            'Cyprus', 'CzechRepublic', 'Denmark', 'Djibouti', 'Dominica', 'DominicanRepublic', 'Ecuador', 'Egypt', 'ElSalvador', 'EquatorialGuinea', 'Eritrea', 
            'Estonia', 'Ethiopia', 'FalklandIslands', 'FaroeIslands', 'Fiji', 'Finland', 'France', 'FrenchGuiana', 'FrenchPolynesia', 'FrenchTerritories', 
            'Gabon', 'Gambia', 'Georgia', 'Germany', 'Ghana', 'Gibraltar', 'Greece', 'Greenland', 'Grenada', 'Guadeloupe', 'Guam', 'Guatemala', 'Guernsey', 
            'Guinea', 'GuineaBissau', 'Guyana', 'Haiti', 'HeardIslands', 'Honduras', 'HongKong', 'Hungary', 'Iceland', 'India', 'Indonesia', 'Iran', 'Iraq', 
            'Ireland', 'IsleOfMan', 'Israel', 'Italy', 'Jamaica', 'Japan', 'Jersey', 'Jordan', 'Kazakhstan', 'Kenya', 'Kiribati', 'Kuwait', 'Kyrgyzstan', 'LaoRepublic', 
            'Latvia', 'Lebanon', 'Lesotho', 'Liberia', 'Libya', 'Liechtenstein', 'Lithuania', 'Luxembourg', 'Macao', 'Macedonia', 'Madagascar', 'Malawi', 'Malaysia', 
            'Maldives', 'Mali', 'Malta', 'MarshallIslands', 'Martinique', 'Mauritania', 'Mauritius', 'Mayotte', 'Mexico', 'Micronesia', 'Moldova', 'Monaco', 
            'Mongolia', 'Montenegro', 'Montserrat', 'Morocco', 'Mozambique', 'Myanmar', 'Namibia', 'Nauru', 'Nepal', 'Netherlands', 'NewCaledonia', 'NewZealand', 
            'Nicaragua', 'Niger', 'Nigeria', 'Niue', 'NorfolkIsland', 'NorthKorea', 'NorthernMarianaIslands', 'Norway', 'Oman', 'Pakistan', 'Palau', 'Palestine', 
            'Panama', 'PapuaNewGuinea', 'Paraguay', 'Peru', 'Philippines', 'Pitcairn', 'Poland', 'Portugal', 'PuertoRico', 'Qatar', 'Reunion', 'Romania', 
            'RussianFederation', 'Rwanda', 'SaintBarthelemy', 'SaintHelena', 'SaintKitts', 'SaintLucia', 'SaintMartin', 'SaintPierre', 'SaintVincent', 'Samoa', 
            'SanMarino', 'SaoTome', 'SaudiArabia', 'Senegal', 'Serbia', 'Seychelles', 'SierraLeone', 'Singapore', 'SintMaartenDutch', 'Slovakia', 'Slovenia', 
            'SolomonIslands', 'Somalia', 'SouthAfrica', 'SouthKorea', 'SouthSudan', 'Spain', 'SriLanka', 'Sudan', 'Suriname', 'Svalbard', 'Swaziland', 'Sweden', 
            'Switzerland', 'Syria', 'Taiwan', 'Tajikistan', 'Tanzania', 'Thailand', 'TimorLeste', 'Togo', 'Tokelau', 'Tonga', 'Trinidad', 'Tunisia', 'Turkey', 
            'Turkmenistan', 'TurksCaicos', 'Tuvalu', 'Uganda', 'Ukraine', 'UnitedArabEmirates', 'UnitedKingdom', 'UnitedStates', 'Uruguay', 'Uzbekistan', 'Vanuatu', 
            'Vatican', 'Venezuela', 'VietNam', 'VirginIslands', 'VirginIslandsGB', 'WallisFutuna', 'WesternSahara', 'Yemen', 'Zambia', 'Zimbabwe'));
        ALTER TABLE n4_policy ADD CONSTRAINT n4_policy_mailing_address_country_e_ck 
            CHECK ((mailing_address_country) IN ('Afghanistan', 'AlandIslands', 'Albania', 'Algeria', 'AmericanSamoa', 'Andorra', 'Angola', 'Anguilla', 'Antarctica', 
            'Antigua', 'Argentina', 'Armenia', 'Aruba', 'Australia', 'Austria', 'Azerbaijan', 'Bahamas', 'Bahrain', 'Bangladesh', 'Barbados', 'Belarus', 'Belgium', 
            'Belize', 'Benin', 'Bermuda', 'Bhutan', 'Bolivia', 'Bonaire', 'BosniaHerzegovina', 'Botswana', 'BouvetIsland', 'Brazil', 'BruneiDarussalam', 'Bulgaria', 
            'BurkinaFaso', 'Burundi', 'CaboVerde', 'Cambodia', 'Cameroon', 'Canada', 'CaymanIslands', 'CentralAfricanRepublic', 'Chad', 'Chile', 'China', 
            'ChristmasIsland', 'CocosIslands', 'Colombia', 'Comoros', 'Congo', 'CookIslands', 'CostaRica', 'Croatia', 'Cuba', 'Curacao', 'Cyprus', 'CzechRepublic', 
            'Denmark', 'Djibouti', 'Dominica', 'DominicanRepublic', 'Ecuador', 'Egypt', 'ElSalvador', 'EquatorialGuinea', 'Eritrea', 'Estonia', 'Ethiopia', 
            'FalklandIslands', 'FaroeIslands', 'Fiji', 'Finland', 'France', 'FrenchGuiana', 'FrenchPolynesia', 'FrenchTerritories', 'Gabon', 'Gambia', 'Georgia', 
            'Germany', 'Ghana', 'Gibraltar', 'Greece', 'Greenland', 'Grenada', 'Guadeloupe', 'Guam', 'Guatemala', 'Guernsey', 'Guinea', 'GuineaBissau', 'Guyana', 
            'Haiti', 'HeardIslands', 'Honduras', 'HongKong', 'Hungary', 'Iceland', 'India', 'Indonesia', 'Iran', 'Iraq', 'Ireland', 'IsleOfMan', 'Israel', 'Italy', 
            'Jamaica', 'Japan', 'Jersey', 'Jordan', 'Kazakhstan', 'Kenya', 'Kiribati', 'Kuwait', 'Kyrgyzstan', 'LaoRepublic', 'Latvia', 'Lebanon', 'Lesotho', 
            'Liberia', 'Libya', 'Liechtenstein', 'Lithuania', 'Luxembourg', 'Macao', 'Macedonia', 'Madagascar', 'Malawi', 'Malaysia', 'Maldives', 'Mali', 'Malta', 
            'MarshallIslands', 'Martinique', 'Mauritania', 'Mauritius', 'Mayotte', 'Mexico', 'Micronesia', 'Moldova', 'Monaco', 'Mongolia', 'Montenegro', 'Montserrat', 
            'Morocco', 'Mozambique', 'Myanmar', 'Namibia', 'Nauru', 'Nepal', 'Netherlands', 'NewCaledonia', 'NewZealand', 'Nicaragua', 'Niger', 'Nigeria', 'Niue', 
            'NorfolkIsland', 'NorthKorea', 'NorthernMarianaIslands', 'Norway', 'Oman', 'Pakistan', 'Palau', 'Palestine', 'Panama', 'PapuaNewGuinea', 'Paraguay', 'Peru', 
            'Philippines', 'Pitcairn', 'Poland', 'Portugal', 'PuertoRico', 'Qatar', 'Reunion', 'Romania', 'RussianFederation', 'Rwanda', 'SaintBarthelemy', 'SaintHelena', 
            'SaintKitts', 'SaintLucia', 'SaintMartin', 'SaintPierre', 'SaintVincent', 'Samoa', 'SanMarino', 'SaoTome', 'SaudiArabia', 'Senegal', 'Serbia', 'Seychelles', 
            'SierraLeone', 'Singapore', 'SintMaartenDutch', 'Slovakia', 'Slovenia', 'SolomonIslands', 'Somalia', 'SouthAfrica', 'SouthKorea', 'SouthSudan', 'Spain', 
            'SriLanka', 'Sudan', 'Suriname', 'Svalbard', 'Swaziland', 'Sweden', 'Switzerland', 'Syria', 'Taiwan', 'Tajikistan', 'Tanzania', 'Thailand', 'TimorLeste', 
            'Togo', 'Tokelau', 'Tonga', 'Trinidad', 'Tunisia', 'Turkey', 'Turkmenistan', 'TurksCaicos', 'Tuvalu', 'Uganda', 'Ukraine', 'UnitedArabEmirates', 
            'UnitedKingdom', 'UnitedStates', 'Uruguay', 'Uzbekistan', 'Vanuatu', 'Vatican', 'Venezuela', 'VietNam', 'VirginIslands', 'VirginIslandsGB', 
            'WallisFutuna', 'WesternSahara', 'Yemen', 'Zambia', 'Zimbabwe'));
        ALTER TABLE notes_and_attachments ADD CONSTRAINT notes_and_attachments_owner_discriminator_d_ck 
            CHECK ((owner_discriminator) IN ('ARPolicy', 'AgreementLegalPolicy', 'ApplicationDocumentationPolicy', 'AptUnit', 'AutoPayPolicy', 
            'AutopayAgreement', 'BackgroundCheckPolicy', 'Building', 'CardsAggregatedTransfer', 'Complex', 'DatesPolicy', 'DepositPolicy', 
            'EftAggregatedTransfer', 'EmailTemplatesPolicy', 'Employee', 'Floorplan', 'Guarantor', 'IdAssignmentPolicy', 'Landlord', 'Lease', 
            'LeaseAdjustmentPolicy', 'LeaseBillingPolicy', 'LegalTermsPolicy', 'Locker', 'MaintenanceRequest', 'MaintenanceRequestPolicy', 
            'MerchantAccount', 'N4Policy', 'OnlineAppPolicy', 'Parking', 'PaymentPostingBatch', 'PaymentRecord', 'PaymentTransactionsPolicy', 
            'PaymentTypeSelectionPolicy', 'PetPolicy', 'ProductTaxPolicy', 'ProspectPortalPolicy', 'ResidentPortalPolicy', 'RestrictionsPolicy', 
            'Tenant', 'TenantInsurancePolicy', 'Vendor', 'YardiInterfacePolicy', 'feature', 'service'));
        ALTER TABLE notification ADD CONSTRAINT notification_tp_e_ck 
            CHECK ((tp) IN ('AutoPayCanceledByResident', 'AutoPayReviewRequired', 'ElectronicPaymentRejectedNsf', 'MaintenanceRequest', 'YardiSynchronization'));
        ALTER TABLE payment_method ADD CONSTRAINT payment_method_billing_address_country_e_ck 
            CHECK ((billing_address_country) IN ('Afghanistan', 'AlandIslands', 'Albania', 'Algeria', 'AmericanSamoa', 'Andorra', 'Angola', 'Anguilla', 
            'Antarctica', 'Antigua', 'Argentina', 'Armenia', 'Aruba', 'Australia', 'Austria', 'Azerbaijan', 'Bahamas', 'Bahrain', 'Bangladesh', 'Barbados', 
            'Belarus', 'Belgium', 'Belize', 'Benin', 'Bermuda', 'Bhutan', 'Bolivia', 'Bonaire', 'BosniaHerzegovina', 'Botswana', 'BouvetIsland', 'Brazil', 
            'BruneiDarussalam', 'Bulgaria', 'BurkinaFaso', 'Burundi', 'CaboVerde', 'Cambodia', 'Cameroon', 'Canada', 'CaymanIslands', 'CentralAfricanRepublic', 
            'Chad', 'Chile', 'China', 'ChristmasIsland', 'CocosIslands', 'Colombia', 'Comoros', 'Congo', 'CookIslands', 'CostaRica', 'Croatia', 'Cuba', 'Curacao', 
            'Cyprus', 'CzechRepublic', 'Denmark', 'Djibouti', 'Dominica', 'DominicanRepublic', 'Ecuador', 'Egypt', 'ElSalvador', 'EquatorialGuinea', 'Eritrea', 
            'Estonia', 'Ethiopia', 'FalklandIslands', 'FaroeIslands', 'Fiji', 'Finland', 'France', 'FrenchGuiana', 'FrenchPolynesia', 'FrenchTerritories', 'Gabon', 
            'Gambia', 'Georgia', 'Germany', 'Ghana', 'Gibraltar', 'Greece', 'Greenland', 'Grenada', 'Guadeloupe', 'Guam', 'Guatemala', 'Guernsey', 'Guinea', 
            'GuineaBissau', 'Guyana', 'Haiti', 'HeardIslands', 'Honduras', 'HongKong', 'Hungary', 'Iceland', 'India', 'Indonesia', 'Iran', 'Iraq', 'Ireland', 
            'IsleOfMan', 'Israel', 'Italy', 'Jamaica', 'Japan', 'Jersey', 'Jordan', 'Kazakhstan', 'Kenya', 'Kiribati', 'Kuwait', 'Kyrgyzstan', 'LaoRepublic', 
            'Latvia', 'Lebanon', 'Lesotho', 'Liberia', 'Libya', 'Liechtenstein', 'Lithuania', 'Luxembourg', 'Macao', 'Macedonia', 'Madagascar', 'Malawi', 'Malaysia', 
            'Maldives', 'Mali', 'Malta', 'MarshallIslands', 'Martinique', 'Mauritania', 'Mauritius', 'Mayotte', 'Mexico', 'Micronesia', 'Moldova', 'Monaco', 
            'Mongolia', 'Montenegro', 'Montserrat', 'Morocco', 'Mozambique', 'Myanmar', 'Namibia', 'Nauru', 'Nepal', 'Netherlands', 'NewCaledonia', 'NewZealand', 
            'Nicaragua', 'Niger', 'Nigeria', 'Niue', 'NorfolkIsland', 'NorthKorea', 'NorthernMarianaIslands', 'Norway', 'Oman', 'Pakistan', 'Palau', 'Palestine', 
            'Panama', 'PapuaNewGuinea', 'Paraguay', 'Peru', 'Philippines', 'Pitcairn', 'Poland', 'Portugal', 'PuertoRico', 'Qatar', 'Reunion', 'Romania', 
            'RussianFederation', 'Rwanda', 'SaintBarthelemy', 'SaintHelena', 'SaintKitts', 'SaintLucia', 'SaintMartin', 'SaintPierre', 'SaintVincent', 'Samoa', 
            'SanMarino', 'SaoTome', 'SaudiArabia', 'Senegal', 'Serbia', 'Seychelles', 'SierraLeone', 'Singapore', 'SintMaartenDutch', 'Slovakia', 'Slovenia', 
            'SolomonIslands', 'Somalia', 'SouthAfrica', 'SouthKorea', 'SouthSudan', 'Spain', 'SriLanka', 'Sudan', 'Suriname', 'Svalbard', 'Swaziland', 'Sweden', 
            'Switzerland', 'Syria', 'Taiwan', 'Tajikistan', 'Tanzania', 'Thailand', 'TimorLeste', 'Togo', 'Tokelau', 'Tonga', 'Trinidad', 'Tunisia', 'Turkey', 
            'Turkmenistan', 'TurksCaicos', 'Tuvalu', 'Uganda', 'Ukraine', 'UnitedArabEmirates', 'UnitedKingdom', 'UnitedStates', 'Uruguay', 'Uzbekistan', 'Vanuatu', 
            'Vatican', 'Venezuela', 'VietNam', 'VirginIslands', 'VirginIslandsGB', 'WallisFutuna', 'WesternSahara', 'Yemen', 'Zambia', 'Zimbabwe'));
        ALTER TABLE payment_record ADD CONSTRAINT payment_record_aggregated_transfer_discriminator_d_ck 
            CHECK ((aggregated_transfer_discriminator) IN ('CardsAggregatedTransfer', 'EftAggregatedTransfer'));
        ALTER TABLE payment_record ADD CONSTRAINT payment_record_aggregated_transfer_return_discriminator_d_ck 
            CHECK ((aggregated_transfer_return_discriminator) IN ('CardsAggregatedTransfer', 'EftAggregatedTransfer'));
        ALTER TABLE payment_record_processing ADD CONSTRAINT payment_record_processing_aggregated_transfer_discr_d_ck 
            CHECK ((aggregated_transfer_discriminator) IN ('CardsAggregatedTransfer', 'EftAggregatedTransfer'));
        ALTER TABLE province_policy_node ADD CONSTRAINT province_policy_node_province_e_ck 
            CHECK ((province) IN ('Alabama', 'Alaska', 'Alberta', 'AmericanSamoa', 'Arizona', 'Arkansas', 'BritishColumbia', 'California', 'Colorado', 
            'Connecticut', 'Delaware', 'DistrictOfColumbia', 'Florida', 'Georgia', 'Guam', 'Hawaii', 'Idaho', 'Illinois', 'Indiana', 'Iowa', 'Kansas', 
            'Kentucky', 'Louisiana', 'Maine', 'Manitoba', 'Maryland', 'Massachusetts', 'Michigan', 'Minnesota', 'MinorOutlyingIslands', 'Mississippi', 
            'Missouri', 'Montana', 'Nebraska', 'Nevada', 'NewBrunswick', 'NewHampshire', 'NewJersey', 'NewMexico', 'NewYork', 'Newfoundland', 'NorthCarolina', 
            'NorthDakota', 'NorthernMarianaIslands', 'NorthwestTerritories', 'NovaScotia', 'Nunavut', 'Ohio', 'Oklahoma', 'Ontario', 'Oregon', 'Pennsylvania', 
            'PrinceEdwardIsland', 'PuertoRico', 'Quebec', 'RhodeIsland', 'Saskatchewan', 'SouthCarolina', 'SouthDakota', 'Tennessee', 'Texas', 'Utah', 'Vermont', 
            'VirginIslands', 'Virginia', 'Washington', 'WestVirginia', 'Wisconsin', 'Wyoming', 'YukonTerritory'));
        ALTER TABLE pt_vehicle ADD CONSTRAINT pt_vehicle_country_e_ck 
            CHECK ((country) IN ('Afghanistan', 'AlandIslands', 'Albania', 'Algeria', 'AmericanSamoa', 'Andorra', 'Angola', 'Anguilla', 'Antarctica', 'Antigua', 
            'Argentina', 'Armenia', 'Aruba', 'Australia', 'Austria', 'Azerbaijan', 'Bahamas', 'Bahrain', 'Bangladesh', 'Barbados', 'Belarus', 'Belgium', 'Belize', 
            'Benin', 'Bermuda', 'Bhutan', 'Bolivia', 'Bonaire', 'BosniaHerzegovina', 'Botswana', 'BouvetIsland', 'Brazil', 'BruneiDarussalam', 'Bulgaria', 
            'BurkinaFaso', 'Burundi', 'CaboVerde', 'Cambodia', 'Cameroon', 'Canada', 'CaymanIslands', 'CentralAfricanRepublic', 'Chad', 'Chile', 'China', 
            'ChristmasIsland', 'CocosIslands', 'Colombia', 'Comoros', 'Congo', 'CookIslands', 'CostaRica', 'Croatia', 'Cuba', 'Curacao', 'Cyprus', 'CzechRepublic', 
            'Denmark', 'Djibouti', 'Dominica', 'DominicanRepublic', 'Ecuador', 'Egypt', 'ElSalvador', 'EquatorialGuinea', 'Eritrea', 'Estonia', 'Ethiopia', 
            'FalklandIslands', 'FaroeIslands', 'Fiji', 'Finland', 'France', 'FrenchGuiana', 'FrenchPolynesia', 'FrenchTerritories', 'Gabon', 'Gambia', 'Georgia', 
            'Germany', 'Ghana', 'Gibraltar', 'Greece', 'Greenland', 'Grenada', 'Guadeloupe', 'Guam', 'Guatemala', 'Guernsey', 'Guinea', 'GuineaBissau', 'Guyana', 
            'Haiti', 'HeardIslands', 'Honduras', 'HongKong', 'Hungary', 'Iceland', 'India', 'Indonesia', 'Iran', 'Iraq', 'Ireland', 'IsleOfMan', 'Israel', 'Italy', 
            'Jamaica', 'Japan', 'Jersey', 'Jordan', 'Kazakhstan', 'Kenya', 'Kiribati', 'Kuwait', 'Kyrgyzstan', 'LaoRepublic', 'Latvia', 'Lebanon', 'Lesotho', 
            'Liberia', 'Libya', 'Liechtenstein', 'Lithuania', 'Luxembourg', 'Macao', 'Macedonia', 'Madagascar', 'Malawi', 'Malaysia', 'Maldives', 'Mali', 'Malta', 
            'MarshallIslands', 'Martinique', 'Mauritania', 'Mauritius', 'Mayotte', 'Mexico', 'Micronesia', 'Moldova', 'Monaco', 'Mongolia', 'Montenegro', 'Montserrat', 
            'Morocco', 'Mozambique', 'Myanmar', 'Namibia', 'Nauru', 'Nepal', 'Netherlands', 'NewCaledonia', 'NewZealand', 'Nicaragua', 'Niger', 'Nigeria', 'Niue', 
            'NorfolkIsland', 'NorthKorea', 'NorthernMarianaIslands', 'Norway', 'Oman', 'Pakistan', 'Palau', 'Palestine', 'Panama', 'PapuaNewGuinea', 'Paraguay', 
            'Peru', 'Philippines', 'Pitcairn', 'Poland', 'Portugal', 'PuertoRico', 'Qatar', 'Reunion', 'Romania', 'RussianFederation', 'Rwanda', 'SaintBarthelemy', 
            'SaintHelena', 'SaintKitts', 'SaintLucia', 'SaintMartin', 'SaintPierre', 'SaintVincent', 'Samoa', 'SanMarino', 'SaoTome', 'SaudiArabia', 'Senegal', 
            'Serbia', 'Seychelles', 'SierraLeone', 'Singapore', 'SintMaartenDutch', 'Slovakia', 'Slovenia', 'SolomonIslands', 'Somalia', 'SouthAfrica', 'SouthKorea', 
            'SouthSudan', 'Spain', 'SriLanka', 'Sudan', 'Suriname', 'Svalbard', 'Swaziland', 'Sweden', 'Switzerland', 'Syria', 'Taiwan', 'Tajikistan', 'Tanzania', 
            'Thailand', 'TimorLeste', 'Togo', 'Tokelau', 'Tonga', 'Trinidad', 'Tunisia', 'Turkey', 'Turkmenistan', 'TurksCaicos', 'Tuvalu', 'Uganda', 'Ukraine', 
            'UnitedArabEmirates', 'UnitedKingdom', 'UnitedStates', 'Uruguay', 'Uzbekistan', 'Vanuatu', 'Vatican', 'Venezuela', 'VietNam', 'VirginIslands', 
            'VirginIslandsGB', 'WallisFutuna', 'WesternSahara', 'Yemen', 'Zambia', 'Zimbabwe'));
        ALTER TABLE pt_vehicle ADD CONSTRAINT pt_vehicle_province_e_ck 
            CHECK ((province) IN ('Alabama', 'Alaska', 'Alberta', 'AmericanSamoa', 'Arizona', 'Arkansas', 'BritishColumbia', 
            'California', 'Colorado', 'Connecticut', 'Delaware', 'DistrictOfColumbia', 'Florida', 'Georgia', 'Guam', 'Hawaii', 'Idaho', 
            'Illinois', 'Indiana', 'Iowa', 'Kansas', 'Kentucky', 'Louisiana', 'Maine', 'Manitoba', 'Maryland', 'Massachusetts', 'Michigan', 
            'Minnesota', 'MinorOutlyingIslands', 'Mississippi', 'Missouri', 'Montana', 'Nebraska', 'Nevada', 'NewBrunswick', 'NewHampshire', 
            'NewJersey', 'NewMexico', 'NewYork', 'Newfoundland', 'NorthCarolina', 'NorthDakota', 'NorthernMarianaIslands', 'NorthwestTerritories', 
            'NovaScotia', 'Nunavut', 'Ohio', 'Oklahoma', 'Ontario', 'Oregon', 'Pennsylvania', 'PrinceEdwardIsland', 'PuertoRico', 'Quebec', 
            'RhodeIsland', 'Saskatchewan', 'SouthCarolina', 'SouthDakota', 'Tennessee', 'Texas', 'Utah', 'Vermont', 'VirginIslands', 'Virginia', 
            'Washington', 'WestVirginia', 'Wisconsin', 'Wyoming', 'YukonTerritory'));
        ALTER TABLE resident_portal_policy ADD CONSTRAINT resident_portal_policy_node_discriminator_d_ck 
            CHECK ((node_discriminator) IN ('AptUnit', 'Building', 'Complex', 'Country', 'Floorplan', 'OrganizationPoliciesNode', 'Province'));


        
        -- not null
        
        ALTER TABLE aggregated_transfer ALTER COLUMN id_discriminator SET NOT NULL;
        ALTER TABLE auto_pay_policy ALTER COLUMN allow_cancelation_by_resident SET NOT NULL,
                                    ALTER COLUMN exclude_first_billing_period_charge SET NOT NULL,
                                    ALTER COLUMN exclude_last_billing_period_charge SET NOT NULL;
        ALTER TABLE communication_message ALTER COLUMN sender SET NOT NULL;
        ALTER TABLE communication_message ALTER COLUMN sender_discriminator SET NOT NULL;
        ALTER TABLE communication_thread ALTER COLUMN owner SET NOT NULL;
        ALTER TABLE communication_thread ALTER COLUMN category SET NOT NULL;
        ALTER TABLE communication_thread ALTER COLUMN owner_discriminator SET NOT NULL;
        
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        CREATE INDEX aggregated_transfer$adjustments_owner_idx ON aggregated_transfer$adjustments USING btree(owner);
        CREATE INDEX aggregated_transfer$chargebacks_owner_idx ON aggregated_transfer$chargebacks USING btree(owner);
        CREATE INDEX available_crm_report$rls_owner_idx ON available_crm_report$rls USING btree (owner);
        CREATE UNIQUE INDEX available_crm_report_report_type_idx ON available_crm_report USING btree (report_type);
        CREATE INDEX communication_delivery_handle_message_idx ON communication_delivery_handle USING btree(message);
        CREATE INDEX communication_message_category$dispatchers_owner_idx ON communication_message_category$dispatchers USING btree(owner);
        CREATE INDEX communication_message_category$rls_owner_idx ON communication_message_category$rls USING btree(owner);
        CREATE INDEX communication_message_thread_idx ON communication_message USING btree(thread);
        CREATE INDEX communication_thread_policy_handle_thread_idx ON communication_thread_policy_handle USING btree (thread);


        
        -- billing_arrears_snapshot -GiST index!
        
        CREATE INDEX billing_arrears_snapshot_from_date_to_date_idx ON billing_arrears_snapshot 
                USING GiST (box(point(from_date,from_date),point(to_date,to_date)) box_ops);
        
        
        
        
        -- Finishing touch
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.4.0',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;      
