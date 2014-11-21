/**
***     ======================================================================================================================
***
***             @version $Revision$ ($Author$) $Date$
***
***             version 1.4.1.4 PMC migration function
***
***     ======================================================================================================================
**/

CREATE OR REPLACE FUNCTION _dba_.migrate_pmc_1414(v_schema_name TEXT) RETURNS VOID AS
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
        
        -- check constraints
        
        ALTER TABLE online_application DROP CONSTRAINT online_application_status_e_ck;
                
        
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
        
        -- tenant_sure_transaction
        
        ALTER TABLE tenant_sure_transaction ADD COLUMN transaction_error_message VARCHAR(500);
       
        /**
        ***     =====================================================================================================
        ***
        ***             DATA MIGRATION
        ***
        ***     =====================================================================================================
        **/
        
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
                ||'WHERE    t.template_type = ''DirectDebitAccountChanged'')';
                
        EXECUTE 'UPDATE '||v_schema_name||'.email_template AS e '
                ||'SET  content = t.content '
                ||'FROM     _dba_.tmp_emails t '
                ||'WHERE    e.template_type =  t.template_type '
                ||'AND      t.template_type = ''PaymentReturned'' ';
        
        -- maintenance email templates changes
                
        EXECUTE 'UPDATE '||v_schema_name||'.email_template  '
                ||'SET  content = regexp_replace(content, ''requestViewUrl'', ''residentViewUrl'', ''g'') '
                ||'WHERE    template_type ~ ''^MaintenanceRequest'' ';
                
        EXECUTE 'UPDATE '||v_schema_name||'.email_template  '
                ||'SET  content = regexp_replace(content, ''residentViewUrl'',''crmViewUrl'', ''g'') '
                ||'WHERE    template_type  = ''MaintenanceRequestCreatedPMC'' ';
        
        -- online_application
        
        EXECUTE 'UPDATE '||v_schema_name||'.online_application AS a '
                ||'SET  status = ''Cancelled'' '
                ||'FROM '||v_schema_name||'.master_online_application m '
                ||'WHERE    a.master_online_application = m.id '
                ||'AND      m.status = ''Cancelled'' ';
                
                
        /**
        *** =============================================================================================================
        ***
        ***     POPULATE _admin_.tenant_sure_subscribers 
        ***
        *** =============================================================================================================
        **/
        
        EXECUTE 'INSERT INTO _admin_.tenant_sure_subscribers(id,pmc,certificate_number) '
                ||'(SELECT  i.id, a.id AS pmc, c.insurance_certificate_number AS certificate_number '
                ||'FROM '||v_schema_name||'.insurance_policy i '
                ||'JOIN '||v_schema_name||'.insurance_certificate c ON (i.id = c.insurance_policy) '
                ||'JOIN _admin_.admin_pmc a ON (a.namespace = '''||v_schema_name||''' ) '
                ||'WHERE    i.id_discriminator = ''TenantSureInsurancePolicy'' ) ';
                
        
        
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
        
        ALTER TABLE online_application ADD CONSTRAINT online_application_status_e_ck 
            CHECK ((status) IN ('Cancelled', 'Incomplete', 'InformationRequested', 'Invited', 'Submitted'));
       
        
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
        SET     schema_version = '1.4.1.4',
                schema_data_upgrade_steps = NULL
        WHERE   namespace = v_schema_name;          
        
END;
$$
LANGUAGE plpgsql VOLATILE;      
