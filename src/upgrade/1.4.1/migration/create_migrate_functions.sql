/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             version 1.4.1 PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_141(v_schema_name TEXT) RETURNS VOID AS
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
        
        ALTER TABLE aggregated_transfer$adjustments DROP CONSTRAINT aggregated_transfer$adjustments_owner_fk;
        ALTER TABLE aggregated_transfer$adjustments DROP CONSTRAINT aggregated_transfer$adjustments_value_fk;
        ALTER TABLE aggregated_transfer$chargebacks DROP CONSTRAINT aggregated_transfer$chargebacks_owner_fk;
        ALTER TABLE aggregated_transfer$chargebacks DROP CONSTRAINT aggregated_transfer$chargebacks_value_fk;
        ALTER TABLE communication_message DROP CONSTRAINT communication_message_thread_fk;
        ALTER TABLE communication_thread_policy_handle DROP CONSTRAINT communication_thread_policy_handle_thread_fk;
        ALTER TABLE notification DROP CONSTRAINT notification_tp_e_ck;


        -- check constraints
        
        ALTER TABLE maintenance_request_category DROP CONSTRAINT maintenance_request_category_type_e_ck;
        ALTER TABLE maintenance_request_priority DROP CONSTRAINT maintenance_request_priority_level_e_ck;
        ALTER TABLE online_application DROP CONSTRAINT online_application_role_e_ck;
        ALTER TABLE payment_record DROP CONSTRAINT payment_record_payment_status_e_ck;
        ALTER TABLE payments_summary DROP CONSTRAINT payments_summary_status_e_ck;
        ALTER TABLE pt_vehicle DROP CONSTRAINT pt_vehicle_province_e_ck;
        ALTER TABLE site_descriptor DROP CONSTRAINT site_descriptor_skin_e_ck;

        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP INDEXES
        ***
        ***     ======================================================================================================
        **/
       
        DROP INDEX communication_message_thread_idx;
        DROP INDEX communication_thread_policy_handle_thread_idx;

        
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
        
        -- aggregated_transfer_adjustment
        
        ALTER TABLE aggregated_transfer_adjustment  ADD COLUMN aggregated_transfer BIGINT,
                                                    ADD COLUMN aggregated_transfer_discriminator VARCHAR(50);
                                                    
        -- aggregated_transfer_chargeback
        
        ALTER TABLE aggregated_transfer_chargeback  ADD COLUMN aggregated_transfer BIGINT,
                                                    ADD COLUMN aggregated_transfer_discriminator VARCHAR(50);
                                                    
                                                    
        -- aggregated_transfer_non_vista_transaction
        
        CREATE TABLE aggregated_transfer_non_vista_transaction
        (
            id                              BIGINT              NOT NULL,
            agg_tf                          BIGINT,
            agg_tf_discriminator            VARCHAR(50),
            merchant_account                BIGINT,
            cards_clearance_record_key      BIGINT,
            amount                          NUMERIC(18,2),
            card_type                       VARCHAR(50),
            transaction_date                TIMESTAMP,
            reconciliation_date             DATE,
            details                         VARCHAR(500),
                CONSTRAINT aggregated_transfer_non_vista_transaction_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE aggregated_transfer_non_vista_transaction OWNER TO vista;
        
        -- apt_unit
        
        ALTER TABLE apt_unit    ADD COLUMN info_legal_address_street_direction VARCHAR(500),
                                ADD COLUMN info_legal_address_street_type VARCHAR(500);
        
        -- billable_item
        
        ALTER TABLE billable_item RENAME COLUMN uid TO uuid;
        
        -- billable_item_adjustment
        
        ALTER TABLE billable_item_adjustment RENAME COLUMN uid TO uuid;
        
        -- billing_invoice_line_item
        
        ALTER TABLE billing_invoice_line_item RENAME COLUMN comment  TO cmt;
        
        -- communication_message
        
        ALTER TABLE communication_message ALTER COLUMN text TYPE VARCHAR(48000);
        ALTER TABLE communication_message RENAME COLUMN thread TO thrd;
        
        
        -- communication_thread_policy_handle
        
        ALTER TABLE communication_thread_policy_handle RENAME COLUMN thread TO thrd;
        
        
        -- community_event
        
        ALTER TABLE community_event RENAME COLUMN date TO event_date;
        ALTER TABLE community_event RENAME COLUMN time TO event_time;
        
        -- customer_preferences
        
        CREATE TABLE customer_preferences
        (
            id                              BIGINT              NOT NULL,
            logical_date_format             VARCHAR(500),
            date_time_format                VARCHAR(500),
            customer_user                   BIGINT,
                CONSTRAINT customer_preferences_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE customer_preferences OWNER TO vista;
        
        
        -- customer_preferences_portal_hidable
        
        CREATE TABLE customer_preferences_portal_hidable
        (
            id                              BIGINT              NOT NULL,
            customer_preferences            BIGINT,
            tp                              VARCHAR(50),
                CONSTRAINT customer_preferences_portal_hidable_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE customer_preferences_portal_hidable OWNER TO vista;
        
        -- customer_screening_personal_asset
        
        ALTER TABLE customer_screening_personal_asset ADD COLUMN ownership NUMERIC(18,2);
        
        
        -- deposit_interest_adjustment
        
        ALTER TABLE deposit_interest_adjustment RENAME COLUMN date TO collection_date;
        
       
        -- email_templates_policy
        
        ALTER TABLE email_templates_policy RENAME COLUMN header TO hdr;
        
        -- id_assignment_payment_type
        
        CREATE TABLE id_assignment_payment_type
        (
            id                              BIGINT              NOT NULL,
            policy                          BIGINT              NOT NULL,
            cash_prefix                     VARCHAR(9),
            check_prefix                    VARCHAR(9),
            echeck_prefix                   VARCHAR(9),
            direct_banking_prefix           VARCHAR(9),
            credit_card_visa_prefix         VARCHAR(9),
            credit_card_master_card_prefix  VARCHAR(9),
            visa_debit_prefix               VARCHAR(9),
            autopay_prefix                  VARCHAR(3),
            one_time_prefix                 VARCHAR(3),
                CONSTRAINT id_assignment_payment_type_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE id_assignment_payment_type OWNER TO vista;
        
        -- lease_adjustment
        
        ALTER TABLE lease_adjustment RENAME COLUMN uid TO uuid;
        
        -- lease_agreement_confirmation_term
        
        ALTER TABLE lease_agreement_confirmation_term RENAME COLUMN body TO content;
        
        -- lease_agreement_legal_term
        
        ALTER TABLE lease_agreement_legal_term RENAME COLUMN body TO content;
        
        -- lease_application_confirmation_term
        
        ALTER TABLE lease_application_confirmation_term RENAME COLUMN body TO content;
        
        -- lease_application_legal_term
        
        ALTER TABLE lease_application_legal_term RENAME COLUMN body TO content;
        
        -- lease_participant_move_in_action
        
        CREATE TABLE lease_participant_move_in_action
        (
            id                              BIGINT              NOT NULL,
            lease_participant               BIGINT              NOT NULL,
            lease_participant_discriminator VARCHAR(50)         NOT NULL,
            tp                              VARCHAR(50)         NOT NULL,
            status                          VARCHAR(50),
                CONSTRAINT lease_participant_move_in_action_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE lease_participant_move_in_action OWNER TO vista;
        
        
        -- maintenance_request_category
        
        ALTER TABLE maintenance_request_category RENAME COLUMN type TO element_type;
        
        -- maintenance_request_priority
        
        ALTER TABLE maintenance_request_priority RENAME COLUMN level TO lvl;
        
        -- master_online_application
        
        ALTER TABLE master_online_application   ADD COLUMN fee_payment VARCHAR(50),
                                                ADD COLUMN fee_amount  NUMERIC(18,2);
                                                
                                                
        -- online_application
        
        ALTER TABLE online_application RENAME COLUMN role TO participant_role;
        
        
        -- online_application_wizard_step_status
        
        ALTER TABLE online_application_wizard_step_status RENAME COLUMN complete TO completed;
        
        -- payment_record
        
        ALTER TABLE payment_record RENAME COLUMN finalize_date TO finalized_date;
        
        
        -- pt_vehicle
        
        ALTER TABLE pt_vehicle ALTER COLUMN province TYPE VARCHAR(500);
        
        -- restrictions_policy
        
        ALTER TABLE restrictions_policy ADD COLUMN years_to_forcing_previous_address INTEGER;
        
        
        -- site_logo_image_resource
        
        ALTER TABLE site_logo_image_resource    ADD COLUMN logo_label BIGINT;
        
        -- site_palette
        
        ALTER TABLE site_palette RENAME COLUMN background TO form_background;
        
        ALTER TABLE site_palette    ADD COLUMN contrast3 INTEGER,
                                    ADD COLUMN contrast4 INTEGER,
                                    ADD COLUMN contrast5 INTEGER,
                                    ADD COLUMN contrast6 INTEGER,
                                    ADD COLUMN site_background INTEGER;
                                    
                                    
        -- yardi_payment_posting_batch
        
        CREATE TABLE yardi_payment_posting_batch
        (
            id                          BIGINT                      NOT NULL,
            building                    BIGINT                      NOT NULL,
            external_batch_number       VARCHAR(500),
            status                      VARCHAR(50),
            creation_date               TIMESTAMP,
            finalize_date               TIMESTAMP,
            post_failed                 BOOLEAN,
            post_failed_error_message   VARCHAR(500),
            cancel_failed               BOOLEAN,
            cancel_failed_error_message VARCHAR(500),
                CONSTRAINT yardi_payment_posting_batch_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE yardi_payment_posting_batch OWNER TO vista;
        
        
        -- yardi_payment_posting_batch_record
        
        CREATE TABLE yardi_payment_posting_batch_record
        (
            id                          BIGINT                  NOT NULL,
            batch                       BIGINT                  NOT NULL,
            updated                     TIMESTAMP,
            created                     TIMESTAMP,
            added                       BOOLEAN,
            reversal                    BOOLEAN,
            payment_record              BIGINT                  NOT NULL,
                CONSTRAINT yardi_payment_posting_batch_record_pk PRIMARY KEY(id)
        );
        
        ALTER TABLE yardi_payment_posting_batch_record OWNER TO vista;
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        -- aggregated_transfer_adjustment
        
        EXECUTE 'UPDATE '||v_schema_name||'.aggregated_transfer_adjustment AS a '
                ||'SET  aggregated_transfer = t.owner '
                ||'FROM     '||v_schema_name||'.aggregated_transfer$adjustments AS t '
                ||'WHERE    a.id = t.value ';
        
         EXECUTE 'UPDATE '||v_schema_name||'.aggregated_transfer_adjustment AS a '
                ||'SET  aggregated_transfer_discriminator = t.id_discriminator '
                ||'FROM     '||v_schema_name||'.aggregated_transfer AS t '
                ||'WHERE    t.id = a.aggregated_transfer ';
        
        -- aggregated_transfer_chargeback
        
        EXECUTE 'UPDATE '||v_schema_name||'.aggregated_transfer_chargeback AS c '
                ||'SET  aggregated_transfer = t.owner '
                ||'FROM     '||v_schema_name||'.aggregated_transfer$chargebacks AS t '
                ||'WHERE    c.id = t.value ';
        
        EXECUTE 'UPDATE '||v_schema_name||'.aggregated_transfer_chargeback AS c '
                ||'SET  aggregated_transfer_discriminator = t.id_discriminator '
                ||'FROM     '||v_schema_name||'.aggregated_transfer AS t '
                ||'WHERE    t.id = c.aggregated_transfer ';
                
        
        /*
        -- apt_unit 
        
        EXECUTE 'UPDATE '||v_schema_name||'.apt_unit AS a '
                ||'SET  info_legal_address_suite_number = NULL, '
                ||'     info_legal_address_street_number = NULL, '
                ||'     info_legal_address_street_name = NULL, '
                ||'     info_legal_address_city = NULL, '
                ||'     info_legal_address_postal_code = NULL, '
                ||'     info_legal_address_country = NULL, '
                ||'     info_legal_address_province = NULL '
                ||'FROM     '||v_schema_name||'.building b '
                ||'WHERE    NOT info_legal_address_override '
                ||'AND      a.building = b.id '
                ||'AND      UPPER(COALESCE(a.info_legal_address_street_number,'''')||'
                ||'         COALESCE(a.info_legal_address_street_name,'''')||'
                ||'         COALESCE(a.info_legal_address_city,'''')||'
                ||'         COALESCE(a.info_legal_address_province,'''')) = '
                ||'         UPPER(COALESCE(b.info_address_street_number,'''')||'
                ||'         COALESCE(b.info_address_street_name,'''')||'
                ||'         COALESCE(b.info_address_city,'''')||'
                ||'         COALESCE(b.info_address_province,'''')) ';
                
        */
        
        -- customer_screening_personal_asset
        
        EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_personal_asset '
                ||'SET  ownership = prcnt::numeric(18,2) ';
        
         -- email_template
        
        -- Delete first - in case DirectDebitAccountChanged tepmlate exists
        
        EXECUTE 'DELETE FROM '||v_schema_name||'.email_template '
                ||'WHERE    template_type = ''DirectDebitAccountChanged'' ';
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.email_template (id,policy,'
                ||'order_in_policy,subject,use_header,use_footer,content,template_type) '
                ||'(SELECT  nextval(''public.email_template_seq'') AS id, '
                ||'p.id AS policy, t.order_in_policy,t.subject,t.use_header,'
                ||'t.use_footer,t.content,t. template_type '
                ||'FROM     '||v_schema_name||'.email_templates_policy p, '
                ||'         _dba_.tmp_emails t '
                ||'WHERE    t.template_type = '''')';
                
        EXECUTE 'UPDATE '||v_schema_name||'.email_template AS e '
                ||'SET  content = t.content '
                ||'FROM     _dba_.tmp_emails t '
                ||'WHERE    e.template_type = ''PaymentReturned '' '
                ||'AND      t.template_type = ''PaymentReturned'' ';
        
        
        -- employee_signature 
        
        EXECUTE 'DELETE FROM '||v_schema_name||'.employee_signature '
                ||'WHERE    file_blob_key IS NULL ';
        
        -- restrictions_policy
        
        EXECUTE 'UPDATE '||v_schema_name||'.restrictions_policy '
                ||'SET  years_to_forcing_previous_address = 3';
        
        
        -- site_palette
        
        EXECUTE 'UPDATE '||v_schema_name||'.site_palette '
                ||'SET  contrast3 = 1, '
                ||'     contrast4 = 1, '
                ||'     contrast5 = 1, '
                ||'     contrast6 = 1, '
                ||'     site_background = 1 ';
        
        /**
        *** -------------------------------------------------------------------------------------
        ***
        ***     prospect_portal_policy cleanup
        ***
        *** --------------------------------------------------------------------------------------
        **/
        
        EXECUTE 'DELETE FROM '||v_schema_name||'.prospect_portal_policy '
                ||'WHERE id NOT IN  (SELECT MIN(id) FROM '||v_schema_name||'.prospect_portal_policy) ';
        
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        --  aggregated_transfer$adjustments
        
        DROP TABLE aggregated_transfer$adjustments;
        
        
        -- aggregated_transfer$chargebacks
        
        DROP TABLE aggregated_transfer$chargebacks;
        
        
        -- customer_screening_personal_asset 
        
        ALTER TABLE customer_screening_personal_asset DROP COLUMN prcnt;
        
        -- id_assignment_policy
        
        ALTER TABLE id_assignment_policy DROP COLUMN x;
        
       
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        -- foreign keys
        
         ALTER TABLE aggregated_transfer_adjustment ADD CONSTRAINT aggregated_transfer_adjustment_aggregated_transfer_fk FOREIGN KEY(aggregated_transfer) 
            REFERENCES aggregated_transfer(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE aggregated_transfer_chargeback ADD CONSTRAINT aggregated_transfer_chargeback_aggregated_transfer_fk FOREIGN KEY(aggregated_transfer) 
            REFERENCES aggregated_transfer(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE aggregated_transfer_non_vista_transaction ADD CONSTRAINT aggregated_transfer_non_vista_transaction_agg_tf_fk FOREIGN KEY(agg_tf) 
            REFERENCES aggregated_transfer(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE aggregated_transfer_non_vista_transaction ADD CONSTRAINT aggregated_transfer_non_vista_transaction_merchant_account_fk FOREIGN KEY(merchant_account) 
            REFERENCES merchant_account(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE communication_message ADD CONSTRAINT communication_message_thrd_fk FOREIGN KEY(thrd) 
            REFERENCES communication_thread(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE communication_thread_policy_handle ADD CONSTRAINT communication_thread_policy_handle_thrd_fk FOREIGN KEY(thrd) 
            REFERENCES communication_thread(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_preferences ADD CONSTRAINT customer_preferences_customer_user_fk FOREIGN KEY(customer_user) 
            REFERENCES customer_user(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE customer_preferences_portal_hidable ADD CONSTRAINT customer_preferences_portal_hidable_customer_preferences_fk FOREIGN KEY(customer_preferences) 
            REFERENCES customer_preferences(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE id_assignment_payment_type ADD CONSTRAINT id_assignment_payment_type_policy_fk FOREIGN KEY(policy) 
            REFERENCES id_assignment_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_participant_move_in_action ADD CONSTRAINT lease_participant_move_in_action_lease_participant_fk FOREIGN KEY(lease_participant) 
            REFERENCES lease_participant(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE site_logo_image_resource ADD CONSTRAINT site_logo_image_resource_logo_label_fk FOREIGN KEY(logo_label) 
            REFERENCES site_image_resource(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE yardi_payment_posting_batch ADD CONSTRAINT yardi_payment_posting_batch_building_fk FOREIGN KEY(building) 
            REFERENCES building(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE yardi_payment_posting_batch_record ADD CONSTRAINT yardi_payment_posting_batch_record_batch_fk FOREIGN KEY(batch) 
            REFERENCES yardi_payment_posting_batch(id)  DEFERRABLE INITIALLY DEFERRED;


        
        -- check constraints
        
        ALTER TABLE aggregated_transfer_adjustment ADD CONSTRAINT aggregated_transfer_adjustment_aggregated_transfer_discr_d_ck 
            CHECK ((aggregated_transfer_discriminator) IN ('CardsAggregatedTransfer', 'EftAggregatedTransfer'));
        ALTER TABLE aggregated_transfer_chargeback ADD CONSTRAINT aggregated_transfer_chargeback_aggregated_transfer_discr_d_ck 
            CHECK ((aggregated_transfer_discriminator) IN ('CardsAggregatedTransfer', 'EftAggregatedTransfer'));
        ALTER TABLE aggregated_transfer_non_vista_transaction ADD CONSTRAINT aggregated_transfer_non_vista_transaction_agg_tf_discr_d_ck 
            CHECK ((agg_tf_discriminator) IN ('CardsAggregatedTransfer', 'EftAggregatedTransfer'));
        ALTER TABLE aggregated_transfer_non_vista_transaction ADD CONSTRAINT aggregated_transfer_non_vista_transaction_card_type_e_ck 
            CHECK ((card_type) IN ('MasterCard', 'Visa', 'VisaDebit'));
        ALTER TABLE customer_preferences_portal_hidable ADD CONSTRAINT customer_preferences_portal_hidable_tp_e_ck CHECK (tp = 'GettingStartedGadget');
        ALTER TABLE lease_participant_move_in_action ADD CONSTRAINT lease_participant_move_in_action_lease_participant_discr_d_ck 
            CHECK ((lease_participant_discriminator) IN ('Guarantor', 'Tenant'));
        ALTER TABLE lease_participant_move_in_action ADD CONSTRAINT lease_participant_move_in_action_status_e_ck 
            CHECK ((status) IN ('completed', 'doItLater'));
        ALTER TABLE lease_participant_move_in_action ADD CONSTRAINT lease_participant_move_in_action_tp_e_ck 
            CHECK ((tp) IN ('autoPay', 'insurance'));
        ALTER TABLE maintenance_request_category ADD CONSTRAINT maintenance_request_category_element_type_e_ck 
            CHECK ((element_type) IN ('Amenities', 'ApartmentUnit', 'Exterior'));
        ALTER TABLE maintenance_request_priority ADD CONSTRAINT maintenance_request_priority_lvl_e_ck 
            CHECK ((lvl) IN ('EMERGENCY', 'STANDARD'));
        ALTER TABLE master_online_application ADD CONSTRAINT master_online_application_fee_payment_e_ck 
            CHECK ((fee_payment) IN ('none', 'perApplicant', 'perLease'));
        ALTER TABLE notification ADD CONSTRAINT notification_tp_e_ck 
            CHECK ((tp) IN ('AutoPayCanceledByResident', 'AutoPayCreatedByResident', 'AutoPayReviewRequired', 'BillingAlert', 'ElectronicPaymentRejectedNsf', 
                            'MaintenanceRequest', 'YardiSynchronization'));
        ALTER TABLE online_application ADD CONSTRAINT online_application_participant_role_e_ck 
            CHECK ((participant_role) IN ('Applicant', 'CoApplicant', 'Dependent', 'Guarantor'));
         ALTER TABLE payment_record ADD CONSTRAINT payment_record_payment_status_e_ck 
            CHECK ((payment_status) IN ('Canceled', 'Cleared', 'PendingAction', 'Processing', 'ProcessingReject', 'ProcessingReturn', 'Queued', 'Received', 
            'Rejected', 'Returned', 'Scheduled', 'Submitted', 'Void'));
        ALTER TABLE payments_summary ADD CONSTRAINT payments_summary_status_e_ck 
            CHECK ((status) IN ('Canceled', 'Cleared', 'PendingAction', 'Processing', 'ProcessingReject', 'ProcessingReturn', 'Queued', 'Received', 
            'Rejected', 'Returned', 'Scheduled', 'Submitted', 'Void'));
        ALTER TABLE site_descriptor ADD CONSTRAINT site_descriptor_skin_e_ck 
            CHECK ((skin) IN ('skin1', 'skin2', 'skin3', 'skin4', 'skin5', 'skin6'));
        ALTER TABLE yardi_payment_posting_batch ADD CONSTRAINT yardi_payment_posting_batch_status_e_ck 
            CHECK ((status) IN ('Canceled', 'Open', 'Posted'));

        
 
        
        -- not null
        
        ALTER TABLE aggregated_transfer_adjustment ALTER COLUMN aggregated_transfer SET NOT NULL;
        ALTER TABLE aggregated_transfer_adjustment ALTER COLUMN aggregated_transfer_discriminator SET NOT NULL;
        ALTER TABLE aggregated_transfer_chargeback ALTER COLUMN aggregated_transfer SET NOT NULL;
        ALTER TABLE aggregated_transfer_chargeback ALTER COLUMN aggregated_transfer_discriminator SET NOT NULL;
        
        
        /**
        ***     ====================================================================================================
        ***     
        ***             INDEXES 
        ***
        ***     ====================================================================================================
        **/
        
        CREATE INDEX aggregated_transfer_adjustment_aggregated_transfer_discr_idx ON aggregated_transfer_adjustment USING btree (aggregated_transfer_discriminator);
        CREATE INDEX aggregated_transfer_adjustment_aggregated_transfer_idx ON aggregated_transfer_adjustment USING btree (aggregated_transfer);
        CREATE INDEX aggregated_transfer_chargeback_aggregated_transfer_discr_idx ON aggregated_transfer_chargeback USING btree (aggregated_transfer_discriminator);
        CREATE INDEX aggregated_transfer_chargeback_aggregated_transfer_idx ON aggregated_transfer_chargeback USING btree (aggregated_transfer);
        CREATE INDEX aggregated_transfer_non_vista_transaction_agg_tf_discr_idx ON aggregated_transfer_non_vista_transaction USING btree (agg_tf_discriminator);
        CREATE INDEX aggregated_transfer_non_vista_transaction_agg_tf_idx ON aggregated_transfer_non_vista_transaction USING btree (agg_tf);
        CREATE INDEX aggregated_transfer_non_vista_transaction_merchant_account_idx ON aggregated_transfer_non_vista_transaction USING btree (merchant_account);
        CREATE INDEX communication_message_thrd_idx ON communication_message USING btree (thrd);
        CREATE INDEX communication_thread_policy_handle_thrd_idx ON communication_thread_policy_handle USING btree (thrd);
        CREATE INDEX lease_participant_move_in_action_lease_participant_discr_idx ON lease_participant_move_in_action USING btree (lease_participant_discriminator);
        CREATE INDEX lease_participant_move_in_action_lease_participant_idx ON lease_participant_move_in_action USING btree (lease_participant);
        CREATE INDEX yardi_payment_posting_batch_building_idx ON yardi_payment_posting_batch USING btree (building);
        CREATE INDEX yardi_payment_posting_batch_record_batch_idx ON yardi_payment_posting_batch_record USING btree (batch);

        
        -- billing_arrears_snapshot -GiST index!
        
        CREATE INDEX billing_arrears_snapshot_from_date_to_date_idx ON billing_arrears_snapshot 
                USING GiST (box(point(from_date,from_date),point(to_date,to_date)) box_ops);
        
        
        
        
        -- Finishing touch
        
        UPDATE  _admin_.admin_pmc
        SET     schema_version = '1.4.1',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;      
