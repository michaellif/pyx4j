/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             version 1.4.0.1 PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_1401(v_schema_name TEXT) RETURNS VOID AS
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


        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/

        -- payment_record

        EXECUTE 'UPDATE '||v_schema_name||'.payment_record AS p '
                ||'SET  payment_status = ''Received'' '
                ||'FROM '||v_schema_name||'.payment_method AS pm '
                ||'WHERE    pm.id = p.payment_method '
                ||'AND  pm.payment_type = ''CreditCard'' '
                ||'AND  p.payment_status = ''Cleared'' '
                ||'AND  p.finalize_date >= ''08-JUN-2014'' ';
        
        
        /**
        *** ==============================================================================================================
        ***
        ***         ROLES UPDATE 
        ***
        *** ==============================================================================================================
        **/
        
        -- Accounting
        
        EXECUTE 'DELETE FROM '||v_schema_name||'.crm_role$behaviors '
                ||'WHERE OWNER IN ( SELECT id FROM '||v_schema_name||'.crm_role '
                ||'                 WHERE   name = ''Accounting'' ) '
                ||'AND  value = ''FinancialPayments'' ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.crm_role$behaviors AS b '
                ||'SET  value = ''TenantAdvanced'' '
                ||'FROM     '||v_schema_name||'.crm_role r '
                ||'WHERE    b.owner = r.id '
                ||'AND      b.value = ''TenantBasic'' '
                ||'AND      r.name = ''Accounting'' ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.crm_role$behaviors AS b '
                ||'SET  value = ''GuarantorAdvanced'' '
                ||'FROM     '||v_schema_name||'.crm_role r '
                ||'WHERE    b.owner = r.id '
                ||'AND      b.value = ''GuarantorBasic'' '
                ||'AND      r.name = ''Accounting'' ';
        
        
        -- Leasing
        
        EXECUTE 'UPDATE '||v_schema_name||'.crm_role$behaviors AS b '
                ||'SET  value = ''TenantAdvanced'' '
                ||'FROM     '||v_schema_name||'.crm_role r '
                ||'WHERE    b.owner = r.id '
                ||'AND      b.value = ''TenantBasic'' '
                ||'AND      r.name = ''Leasing'' ';
                
        EXECUTE 'INSERT INTO '||v_schema_name||'.crm_role$behaviors(id, owner,value) '
                ||'(SELECT  nextval(''public.crm_role$behaviors_seq'') AS id, '
                ||'         r.id AS owner, ''GuarantorAdvanced'' AS value '
                ||'FROM '||v_schema_name||'.crm_role AS r '
                ||'WHERE    r.name = ''Leasing'') ';
                
        -- Marketing
        
        EXECUTE 'UPDATE '||v_schema_name||'.crm_role$behaviors AS b '
                ||'SET  value = ''FinancialBasic'' '
                ||'FROM     '||v_schema_name||'.crm_role r '
                ||'WHERE    b.owner = r.id '
                ||'AND      b.value = ''FinancialPayments'' '
                ||'AND      r.name = ''Marketing'' ';
        
        -- Property Administrator
        
        EXECUTE 'UPDATE '||v_schema_name||'.crm_role$behaviors AS b '
                ||'SET  value = ''FinancialAdvanced'' '
                ||'FROM     '||v_schema_name||'.crm_role r '
                ||'WHERE    b.owner = r.id '
                ||'AND      b.value = ''FinancialPayments'' '
                ||'AND      r.name = ''Property Administrator'' ';
                
        -- Property Manager
        
        EXECUTE 'UPDATE '||v_schema_name||'.crm_role$behaviors AS b '
                ||'SET  value = ''FinancialAdvanced'' '
                ||'FROM     '||v_schema_name||'.crm_role r '
                ||'WHERE    b.owner = r.id '
                ||'AND      b.value = ''FinancialPayments'' '
                ||'AND      r.name = ''Property Manager'' ';
        
        EXECUTE 'UPDATE '||v_schema_name||'.crm_role$behaviors AS b '
                ||'SET  value = ''GuarantorAdvanced'' '
                ||'FROM     '||v_schema_name||'.crm_role r '
                ||'WHERE    b.owner = r.id '
                ||'AND      b.value = ''GuarantorBasic'' '
                ||'AND      r.name = ''Property Manager'' ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.crm_role$behaviors AS b '
                ||'SET  value = ''TenantAdvanced'' '
                ||'FROM     '||v_schema_name||'.crm_role r '
                ||'WHERE    b.owner = r.id '
                ||'AND      b.value = ''TenantBasic'' '
                ||'AND      r.name = ''Property Manager'' ';
        
        
        -- Regional Administrator
        
        EXECUTE 'UPDATE '||v_schema_name||'.crm_role$behaviors AS b '
                ||'SET  value = ''FinancialAdvanced'' '
                ||'FROM     '||v_schema_name||'.crm_role r '
                ||'WHERE    b.owner = r.id '
                ||'AND      b.value = ''FinancialPayments'' '
                ||'AND      r.name = ''Regional Administrator'' ';
        
        EXECUTE 'UPDATE '||v_schema_name||'.crm_role$behaviors AS b '
                ||'SET  value = ''GuarantorAdvanced'' '
                ||'FROM     '||v_schema_name||'.crm_role r '
                ||'WHERE    b.owner = r.id '
                ||'AND      b.value = ''GuarantorBasic'' '
                ||'AND      r.name = ''Regional Administrator'' ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.crm_role$behaviors AS b '
                ||'SET  value = ''TenantAdvanced'' '
                ||'FROM     '||v_schema_name||'.crm_role r '
                ||'WHERE    b.owner = r.id '
                ||'AND      b.value = ''TenantBasic'' '
                ||'AND      r.name = ''Regional Administrator'' ';
                
        -- Regional Manager
        
        EXECUTE 'UPDATE '||v_schema_name||'.crm_role$behaviors AS b '
                ||'SET  value = ''GuarantorAdvanced'' '
                ||'FROM     '||v_schema_name||'.crm_role r '
                ||'WHERE    b.owner = r.id '
                ||'AND      b.value = ''GuarantorBasic'' '
                ||'AND      r.name = ''Regional Manager'' ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.crm_role$behaviors AS b '
                ||'SET  value = ''TenantAdvanced'' '
                ||'FROM     '||v_schema_name||'.crm_role r '
                ||'WHERE    b.owner = r.id '
                ||'AND      b.value = ''TenantBasic'' '
                ||'AND      r.name = ''Regional Manager'' ';
                
        EXECUTE 'INSERT INTO '||v_schema_name||'.crm_role$behaviors(id, owner,value) '
                ||'(SELECT  nextval(''public.crm_role$behaviors_seq'') AS id, '
                ||'         r.id AS owner, ''FinancialAdvanced'' AS value '
                ||'FROM '||v_schema_name||'.crm_role AS r '
                ||'WHERE    r.name = ''Regional Manager'') ';
    
        -- Revenue Admin
        
         EXECUTE 'INSERT INTO '||v_schema_name||'.crm_role$behaviors(id, owner,value) '
                ||'(SELECT  nextval(''public.crm_role$behaviors_seq'') AS id, '
                ||'         r.id AS owner, ''GuarantorFull'' AS value '
                ||'FROM '||v_schema_name||'.crm_role AS r '
                ||'WHERE    r.name = ''Revenue Admin'') ';
        
        -- Super Administrator
        
        EXECUTE 'INSERT INTO '||v_schema_name||'.crm_role$behaviors(id, owner,value) '
                ||'(SELECT  nextval(''public.crm_role$behaviors_seq'') AS id, '
                ||'         r.id AS owner, ''FinancialAdvanced'' AS value '
                ||'FROM '||v_schema_name||'.crm_role AS r '
                ||'WHERE    r.name = ''Super Administrator'') ';
                
        EXECUTE 'INSERT INTO '||v_schema_name||'.crm_role$behaviors(id, owner,value) '
                ||'(SELECT  nextval(''public.crm_role$behaviors_seq'') AS id, '
                ||'         r.id AS owner, ''FinancialBasic'' AS value '
                ||'FROM '||v_schema_name||'.crm_role AS r '
                ||'WHERE    r.name = ''Super Administrator'') ';
        
        
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
        SET     schema_version = '1.4.0.1',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;

END;
$$
LANGUAGE plpgsql VOLATILE;


