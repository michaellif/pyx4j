/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             version 1.1.2.2 PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_1122(v_schema_name TEXT) RETURNS VOID AS
$$
DECLARE 
        v_row_cnt       INT ;
BEGIN
        
        EXECUTE 'SET search_path = '||v_schema_name;
        
        /**
        ***     ======================================================================================================
        ***
        ***             DROP CONSTRAINTS SECTION
        ***
        ***     ======================================================================================================
        **/
        
        -- check constraints
        
        ALTER TABLE merchant_account DROP CONSTRAINT merchant_account_status_e_ck;
        
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
        ***             This index doesn''t exist in new schemas, and might bloated for schemas
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
        
        -- merchant_account
        
        ALTER TABLE merchant_account    ADD COLUMN setup_accepted_echeck BOOLEAN,
                                        ADD COLUMN setup_accepted_direct_banking BOOLEAN,
                                        ADD COLUMN setup_accepted_credit_card BOOLEAN,
                                        ADD COLUMN setup_accepted_credit_card_convenience_fee BOOLEAN,
                                        ADD COLUMN setup_accepted_interac BOOLEAN ;
        
        -- payment_record
        
        ALTER TABLE payment_record ADD COLUMN convenience_fee_reference_number VARCHAR(30);
       
        
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
        -- arcode : VISTA-3900 
        
        v_row_cnt := 0;
        
        EXECUTE 'SELECT COUNT(id) FROM '||v_schema_name||'.arcode WHERE name = ''Unknown External Credit'' '
                INTO v_row_cnt;
        
        IF (v_row_cnt = 0)         
        THEN
                EXECUTE 'INSERT INTO '||v_schema_name||'.arcode (id,code_type,name,updated,reserved) '
                        ||'(SELECT      nextval(''public.arcode_seq'') AS id, ''ExternalCredit'' AS code_type,'
                        ||'             ''Unknown External Credit'' AS name, DATE_TRUNC(''second'',current_timestamp)::timestamp, '
                        ||'             TRUE)';
        ELSE
                EXECUTE 'UPDATE '||v_schema_name||'.arcode '
                        ||'SET  reserved = TRUE,'
                        ||'     updated = DATE_TRUNC(''second'',current_timestamp)::timestamp '
                        ||'WHERE name = ''Unknown External Credit'' '
                        ||'AND  NOT reserved ';
        END IF;
        
        
        EXECUTE 'SELECT COUNT(id) FROM '||v_schema_name||'.arcode WHERE name = ''Unknown External Charge'' '
                INTO v_row_cnt;
        
        IF (v_row_cnt = 0)         
        THEN
                EXECUTE 'INSERT INTO '||v_schema_name||'.arcode (id,code_type,name,updated,reserved) '
                        ||'(SELECT      nextval(''public.arcode_seq'') AS id, ''ExternalCharge'' AS code_type,'
                        ||'             ''Unknown External Charge'' AS name, DATE_TRUNC(''second'',current_timestamp)::timestamp, '
                        ||'             TRUE)';
        ELSE
                EXECUTE 'UPDATE '||v_schema_name||'.arcode '
                        ||'SET  reserved = TRUE,'
                        ||'     updated = DATE_TRUNC(''second'',current_timestamp)::timestamp '
                        ||'WHERE name = ''Unknown External Charge'' '
                        ||'AND  NOT reserved ';
        END IF;
        
        
        -- email_template
        
        EXECUTE 'UPDATE '||v_schema_name||'.email_template '
                ||'SET content = regexp_replace(content,''Current Status: \${MaintenanceRequest.status}'', '
                ||'             ''Request Completed: ${MaintenanceRequest.resolved}'' ) '
                ||'WHERE template_type = ''MaintenanceRequestCompleted'' ';
        
        
        -- merchant_account
        
        EXECUTE 'UPDATE '||v_schema_name||'.merchant_account '
                ||'SET  invalid = FALSE '
                ||'WHERE invalid IS NULL';
                
        EXECUTE 'UPDATE '||v_schema_name||'.merchant_account '
                ||'SET  setup_accepted_echeck = TRUE, '
                ||'     setup_accepted_direct_banking = TRUE, '
                ||'     setup_accepted_credit_card = TRUE, '
                ||'     setup_accepted_interac = TRUE ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.merchant_account AS m '
                ||'SET     setup_accepted_credit_card_convenience_fee = TRUE '
                ||'FROM '||v_schema_name||'.building b '
                ||'JOIN '||v_schema_name||'.building_merchant_account bm ON (b.id = bm.building) '
                ||'WHERE   UPPER(b.info_address_city) IN (''CAMBRIDGE'',''GUELPH'',''KITCHENER'',''LONDON'',''WATERLOO'') '
                ||'AND     m.id = bm.merchant_account ';
       
        
        /**
        ***     ==========================================================================================================
        ***
        ***             DROP TABLES AND COLUMNS
        ***
        ***     ==========================================================================================================
        **/
        
        
       
                 
        /**
        ***     ======================================================================================================
        ***
        ***             CREATE CONSTRAINTS 
        ***     
        ***     =======================================================================================================
        **/
        
        -- check constraints
        
        ALTER TABLE merchant_account ADD CONSTRAINT merchant_account_status_e_ck 
                CHECK ((status) IN ('Active', 'Cancelled', 'PendindAcknowledgement', 'PendindAppoval', 'Rejected', 'Suspended'));
        
        -- not null
        
        ALTER TABLE merchant_account ALTER COLUMN invalid SET NOT NULL;
        
       
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
        SET     schema_version = '1.1.2.2',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;

