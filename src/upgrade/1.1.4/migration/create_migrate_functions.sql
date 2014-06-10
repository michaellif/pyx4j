/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             version 1.1.4 PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_114(v_schema_name TEXT) RETURNS VOID AS
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
        ALTER TABLE city_intro_page DROP CONSTRAINT city_intro_page_province_fk;
        ALTER TABLE city DROP CONSTRAINT city_province_fk;
        ALTER TABLE communication_message$to DROP CONSTRAINT communication_message$to_owner_fk;
        ALTER TABLE communication_message_attachment DROP CONSTRAINT communication_message_attachment_message_fk;
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
        
        ALTER TABLE billable_item_adjustment DROP CONSTRAINT billable_item_adjustment_adjustment_type_e_ck;
        -- ALTER TABLE communication_message DROP CONSTRAINT communication_message_sender_discriminator_d_ck;
        -- ALTER TABLE communication_thread DROP CONSTRAINT communication_thread_responsible_discriminator_d_ck;
        ALTER TABLE lease_adjustment DROP CONSTRAINT lease_adjustment_tax_type_e_ck;
        -- ALTER TABLE legal_letter DROP CONSTRAINT legal_letter_status_discriminator_d_ck;
        -- ALTER TABLE legal_status DROP CONSTRAINT legal_status_id_discriminator_ck;
        -- ALTER TABLE marketing DROP CONSTRAINT marketing_marketing_address_street_direction_e_ck;
        -- ALTER TABLE marketing DROP CONSTRAINT marketing_marketing_address_street_type_e_ck;
        -- ALTER TABLE n4_policy DROP CONSTRAINT n4_policy_mailing_address_street_direction_e_ck;
        -- ALTER TABLE n4_policy DROP CONSTRAINT n4_policy_mailing_address_street_type_e_ck;
        -- ALTER TABLE notification DROP CONSTRAINT notification_tp_e_ck;
        -- ALTER TABLE system_endpoint DROP CONSTRAINT system_endpoint_type_e_ck;

        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP INDEXES
        ***
        ***     ======================================================================================================
        **/
        
        DROP INDEX province_code_idx;
        DROP INDEX province_name_idx;
      
        
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
        
        -- apt_unit
        
        ALTER TABLE apt_unit RENAME COLUMN info_legal_address_country TO info_legal_address_country_old;
        ALTER TABLE apt_unit RENAME COLUMN info_legal_address_province TO info_legal_address_province_old;
        
        ALTER TABLE apt_unit    ADD COLUMN info_legal_address_country VARCHAR(50),
                                ADD COLUMN info_legal_address_province VARCHAR(500);
        
        
        
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
        
        
        -- concession_v
        
        ALTER TABLE concession_v    ADD COLUMN val_amount NUMERIC(18,2),
                                    ADD COLUMN val_percent NUMERIC(18,2);
        
        -- country_policy_node
        
        CREATE TABLE country_policy_node
        (
            id                          BIGINT              NOT NULL,
            country                     VARCHAR(50),
                CONSTRAINT  country_policy_node_pk  PRIMARY KEY(id)
        );
        
        ALTER TABLE country_policy_node OWNER TO vista;
        
        
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
                                    ADD COLUMN billing_address_suite_number VARCHAR(500);
        
        
        -- online_application
        
        ALTER TABLE online_application ADD COLUMN create_date DATE;
        
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
        
        -- restrictions_policy
        
        ALTER TABLE restrictions_policy ADD COLUMN no_need_guarantors BOOLEAN;
        
        -- site_titles
        
        ALTER TABLE site_titles RENAME COLUMN resident_portal_promotions TO site_promo_title;
        
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        
         -- province_policy_node
        
        EXECUTE 'UPDATE '||v_schema_name||'.province_policy_node '
                ||'SET  name = ''Newfoundland'' '
                ||'WHERE name = ''Newfoundland and Labrador'' ';
        
        EXECUTE 'UPDATE '||v_schema_name||'.province_policy_node '
                ||'SET  province = replace(INITCAP(name),'' '','''') ';
        
        
        -- apt_unit
        
        EXECUTE 'UPDATE '||v_schema_name||'.apt_unit AS a '
                ||'SET    info_legal_address_country = replace(c.name,'' '','''') '
                ||'FROM   '||v_schema_name||'.country AS c '
                ||'WHERE  a.info_legal_address_country_old = c.id ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.apt_unit AS a '
                ||'SET    info_legal_address_province = p.name '
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
                ||'SET  info_address_province = p.name '
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
                
        
        -- city
        
        EXECUTE 'UPDATE '||v_schema_name||'.city AS c '
                ||'SET  province = p.name '
                ||'FROM '||v_schema_name||'.province_policy_node p '
                ||'WHERE    c.province_old = p.id ';
                
        
        -- city_intro_page
        
        EXECUTE 'UPDATE '||v_schema_name||'.city_intro_page AS c '
                ||'SET  province = p.name '
                ||'FROM '||v_schema_name||'.province_policy_node p '
                ||'WHERE    c.province_old = p.id ';
                
       
        -- customer_screening_income_info
        
        EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_income_info AS i '
                ||'SET  address_country = replace(c.name,'' '','''') '
                ||'FROM '||v_schema_name||'.country c '
                ||'WHERE    i.address_country_old = c.id ';
                
                
        EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_income_info AS i '
                ||'SET  address_province = p.name '
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
                ||'SET current_address_province = p.name '
                ||'FROM '||v_schema_name||'.province_policy_node AS p '
                ||'WHERE   current_address_province_old = p.id ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_v AS s '
                ||'SET previous_address_province = p.name '
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
                ||'SET  address_province = p.name '
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
                ||'SET  address_province = p.name '
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
                
        -- marketing
        
        EXECUTE 'UPDATE '||v_schema_name||'.marketing AS m '
                ||'SET  marketing_address_country = replace(c.name,'' '','''') '
                ||'FROM   '||v_schema_name||'.country AS c '
                ||'WHERE  m.marketing_address_country_old = c.id ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.marketing AS m '
                ||'SET  marketing_address_province = p.name '
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
                ||'SET  mailing_address_province = p.name '
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
                ||'SET  billing_address_province = p.name '
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
                ||'SET  province = p.name '
                ||'FROM '||v_schema_name||'.province_policy_node p '
                ||'WHERE    pt.province_old = p.id ';
                
        -- restrictions_policy
        
        EXECUTE 'UPDATE '||v_schema_name||'.restrictions_policy '
                ||'SET  no_need_guarantors = FALSE ';
       
       
        SET CONSTRAINTS ALL IMMEDIATE;
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
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
                                DROP COLUMN info_address_street_type;
                                
        -- city 
        
        ALTER TABLE city DROP COLUMN province_old;
        
        
        -- city_intro_page 
        
        ALTER TABLE city_intro_page DROP COLUMN province_old;
        
        
        -- concession_v
        
        ALTER TABLE concession_v DROP COLUMN val;
        
        -- country
        
        DROP TABLE country;
        
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
        
        -- province_policy_node
        
        ALTER TABLE province_policy_node    DROP COLUMN code,
                                            DROP COLUMN country,
                                            DROP COLUMN name;
                                            
        -- pt_vehicle
        
        ALTER TABLE pt_vehicle  DROP COLUMN country_old,
                                DROP COLUMN province_old;
                 
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        -- foreign keys
        
        ALTER TABLE master_online_application ADD CONSTRAINT master_online_application_ils_building_fk FOREIGN KEY(ils_building) 
            REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE master_online_application ADD CONSTRAINT master_online_application_ils_floorplan_fk FOREIGN KEY(ils_floorplan) 
            REFERENCES floorplan(id)  DEFERRABLE INITIALLY DEFERRED;
            
        -- check constraints
        
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
        ALTER TABLE billable_item_adjustment ADD CONSTRAINT billable_item_adjustment_adjustment_type_e_ck 
            CHECK ((adjustment_type) IN ('Monetary', 'Percentage'));
        ALTER TABLE lease_adjustment ADD CONSTRAINT lease_adjustment_tax_type_e_ck CHECK ((tax_type) IN ('Monetary', 'Percentage'));
        
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        
        
        -- billing_arrears_snapshot -GiST index!
        
        CREATE INDEX billing_arrears_snapshot_from_date_to_date_idx ON billing_arrears_snapshot 
                USING GiST (box(point(from_date,from_date),point(to_date,to_date)) box_ops);
        
        
        
        
        -- Finishing touch
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.1.4',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;      
