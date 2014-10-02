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

        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP INDEXES
        ***
        ***     ======================================================================================================
        **/
        
       
        
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
        
        -- billing_invoice_line_item
        
        ALTER TABLE billing_invoice_line_item RENAME COLUMN comment  TO cmt;
        
        -- communication_message
        
        ALTER TABLE communication_message ALTER COLUMN text TYPE VARCHAR(48000);
        ALTER TABLE communication_message RENAME COLUMN thread TO thrd;
        
        
        -- communication_thread_policy_handle
        
        ALTER TABLE communication_thread_policy_handle RENAME COLUMN thread TO thrd;
        
        
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
        
        -- customer_screening_personal_asset
        
        EXECUTE 'UPDATE '||v_schema_name||'.customer_screening_personal_asset '
                ||'SET  ownership = prcnt::numeric(18,2) ';
        
        
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
        -- ALTER TABLE customer_settings ADD CONSTRAINT customer_settings_customer_user_fk FOREIGN KEY(customer_user) REFERENCES customer_user(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE id_assignment_payment_type ADD CONSTRAINT id_assignment_payment_type_policy_fk FOREIGN KEY(policy) 
            REFERENCES id_assignment_policy(id)  DEFERRABLE INITIALLY DEFERRED;
        ALTER TABLE lease_participant_move_in_action ADD CONSTRAINT lease_participant_move_in_action_lease_participant_fk FOREIGN KEY(lease_participant) 
            REFERENCES lease_participant(id)  DEFERRABLE INITIALLY DEFERRED;

        
        -- check constraints
        
        ALTER TABLE aggregated_transfer_adjustment ADD CONSTRAINT aggregated_transfer_adjustment_aggregated_transfer_discr_d_ck 
            CHECK ((aggregated_transfer_discriminator) IN ('CardsAggregatedTransfer', 'EftAggregatedTransfer'));
        ALTER TABLE aggregated_transfer_chargeback ADD CONSTRAINT aggregated_transfer_chargeback_aggregated_transfer_discr_d_ck 
            CHECK ((aggregated_transfer_discriminator) IN ('CardsAggregatedTransfer', 'EftAggregatedTransfer'));
        ALTER TABLE aggregated_transfer_non_vista_transaction ADD CONSTRAINT aggregated_transfer_non_vista_transaction_agg_tf_discr_d_ck 
            CHECK ((agg_tf_discriminator) IN ('CardsAggregatedTransfer', 'EftAggregatedTransfer'));
        ALTER TABLE aggregated_transfer_non_vista_transaction ADD CONSTRAINT aggregated_transfer_non_vista_transaction_card_type_e_ck 
            CHECK ((card_type) IN ('MasterCard', 'Visa', 'VisaDebit'));
        ALTER TABLE lease_participant_move_in_action ADD CONSTRAINT lease_participant_move_in_action_lease_participant_discr_d_ck 
            CHECK ((lease_participant_discriminator) IN ('Guarantor', 'Tenant'));
        ALTER TABLE lease_participant_move_in_action ADD CONSTRAINT lease_participant_move_in_action_status_e_ck 
            CHECK ((status) IN ('completed', 'doItLater'));
        ALTER TABLE lease_participant_move_in_action ADD CONSTRAINT lease_participant_move_in_action_tp_e_ck 
            CHECK ((tp) IN ('autoPay', 'insurance'));
        ALTER TABLE master_online_application ADD CONSTRAINT master_online_application_fee_payment_e_ck 
            CHECK ((fee_payment) IN ('none', 'perApplicant', 'perLease'));

        
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
        CREATE INDEX lease_participant_move_in_action_lease_participant_discr_idx ON lease_participant_move_in_action USING btree (lease_participant_discriminator);
        CREATE INDEX lease_participant_move_in_action_lease_participant_idx ON lease_participant_move_in_action USING btree (lease_participant);


        
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
